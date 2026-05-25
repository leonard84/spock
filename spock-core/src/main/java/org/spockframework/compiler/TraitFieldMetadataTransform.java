/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.compiler;

import org.spockframework.runtime.model.FieldMetadata;
import org.spockframework.util.InternalIdentifiers;
import org.spockframework.util.VersionChecker;
import spock.lang.Shared;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.transform.*;
import org.codehaus.groovy.transform.trait.Traits;
import org.objectweb.asm.Opcodes;

import static org.spockframework.compiler.AstUtil.primitiveConstExpression;

/**
 * Adapts trait-declared fields on a {@code Specification} subclass to Spock's
 * runtime model:
 *
 * <ol>
 *   <li>Adds {@link FieldMetadata} annotations to the synthetic fields that
 *       Groovy's trait composer generated on the spec, so
 *       {@code SpecInfoBuilder} discovers them and annotation-driven
 *       extensions (e.g. {@code @AutoCleanup}, {@code @TempDir}) work.</li>
 *   <li>Moves each trait's {@code $Trait$Helper.$init$(this)} object
 *       initializer statement out of the spec's constructor and into
 *       {@code $spock_initializeFields}, so trait field initialization runs
 *       per feature iteration (same timing as spec-declared field
 *       initializers) instead of once per JVM construction.</li>
 * </ol>
 *
 * <p>Runs at {@link CompilePhase#INSTRUCTION_SELECTION}, the same phase at
 * which Groovy's {@code TraitASTTransformation} schedules
 * {@code TraitComposer.doExtendTraits(...)}. At that point the composed
 * trait-backed fields exist on the spec's {@link ClassNode} as fields named
 * {@code <traitFQCN_with_dots_to_underscores>__<originalName>}, with the
 * trait's annotations already propagated by Groovy, and the
 * {@code $init$(this)} call has been appended to the spec's
 * {@code objectInitializerStatements} (these are inlined into every
 * constructor at {@code CLASS_GENERATION}, so we can still mutate the list
 * here).
 *
 * @author Leonard Brünings
 */
@SuppressWarnings("UnusedDeclaration")
@GroovyASTTransformation(phase = CompilePhase.INSTRUCTION_SELECTION)
public class TraitFieldMetadataTransform implements ASTTransformation {
  public TraitFieldMetadataTransform() {
    new VersionChecker().checkGroovyVersion("trait support");
  }

  @Override
  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
    new Impl().visit(sourceUnit);
  }

  // use of nested class defers linking until after groovy version check
  private static class Impl {
    static final AstNodeCache nodeCache = new AstNodeCache();
    static final String TRAIT_HELPER_SUFFIX = "$Trait$Helper";
    // Mirrors Groovy's package-private TRAIT_INIT_METHOD constant.
    static final String TRAIT_INIT_METHOD = "$init$";

    void visit(SourceUnit sourceUnit) {
      ErrorReporter errorReporter = new ErrorReporter(sourceUnit);
      for (ClassNode clazz : sourceUnit.getAST().getClasses()) {
        if (!clazz.isDerivedFrom(nodeCache.Specification)) continue;
        processSpec(clazz, errorReporter);
      }
    }

    private void processSpec(ClassNode spec, ErrorReporter errorReporter) {
      List<ClassNode> traits = Traits.findTraits(spec);
      if (traits.isEmpty()) return;

      annotateTraitFields(spec, traits, errorReporter);
      moveTraitInitializersIntoSpockInitializer(spec, traits);
    }

    // ----------------------------------------------------------------
    // 1) @FieldMetadata on composed trait fields
    // ----------------------------------------------------------------

    private void annotateTraitFields(ClassNode spec, List<ClassNode> traits, ErrorReporter errorReporter) {
      // By INSTRUCTION_SELECTION, Groovy's TraitASTTransformation has moved each
      // trait's original FieldNodes off the trait interface, so trait.getFields()
      // is empty. The authoritative source is the composed FieldNode on the spec
      // ClassNode, named "<TraitFQCN_with_dots_to_underscores>__<originalName>"
      // (TraitComposer.applyTrait). The trait's annotations are propagated to it.
      int nextOrdinal = computeNextOrdinal(spec);

      for (ClassNode trait : traits) {
        String prefix = trait.getName().replace('.', '_') + "__";
        for (FieldNode composed : spec.getFields()) {
          // SpecParser explicitly skips static fields (no per-instance
          // semantics, no @FieldMetadata, not in SpecInfo.fields). Match
          // that behavior for trait-composed fields. Their $static$init$
          // already runs at class-load time via the JVM <clinit>.
          if (composed.isStatic()) continue;
          String composedName = composed.getName();
          if (!composedName.startsWith(prefix)) continue;
          String originalName = composedName.substring(prefix.length());
          if (originalName.isEmpty() || originalName.indexOf('$') >= 0) continue;

          if (AstUtil.hasAnnotation(composed, Shared.class)) {
            errorReporter.error(composed,
                "@Shared is not supported on trait-declared fields ('%s' in trait '%s'). " +
                "Move the field to the spec or remove @Shared.",
                originalName, trait.getName());
            continue;
          }

          if (AstUtil.hasAnnotation(composed, FieldMetadata.class)) continue;

          boolean hasInitializer = traitFieldHasInitializer(trait, prefix, originalName, composed.isFinal());

          AnnotationNode ann = new AnnotationNode(nodeCache.FieldMetadata);
          ann.setMember(FieldMetadata.NAME, new ConstantExpression(originalName));
          ann.setMember(FieldMetadata.ORDINAL, primitiveConstExpression(nextOrdinal++));
          ann.setMember(FieldMetadata.LINE, primitiveConstExpression(composed.getLineNumber()));
          ann.setMember(FieldMetadata.INITIALIZER, primitiveConstExpression(hasInitializer));
          composed.addAnnotation(ann);
        }
      }
    }

    private int computeNextOrdinal(ClassNode spec) {
      int max = -1;
      for (FieldNode f : spec.getFields()) {
        AnnotationNode existing = AstUtil.getAnnotation(f, FieldMetadata.class);
        if (existing == null) continue;
        Expression e = existing.getMember(FieldMetadata.ORDINAL);
        if (e instanceof ConstantExpression) {
          Object v = ((ConstantExpression) e).getValue();
          if (v instanceof Integer) max = Math.max(max, (Integer) v);
        }
      }
      return max + 1;
    }

    /**
     * Detect whether a trait field has an explicit initializer in trait source.
     *
     * <p>Groovy stores initialization for non-final fields as a call to the
     * helper-setter (named {@code <remappedName>$set}) inside the trait
     * helper's {@code $init$(SpecInstance)} method body. For final fields,
     * Groovy emits a dedicated method {@code $init$<remappedName>} on the
     * helper. Either signal tells us the original field had an initializer.
     *
     * <p>For binary (precompiled) traits the helper class is reflection-loaded
     * and method bodies are {@code null}; in that case we conservatively
     * return {@code false} for non-final fields. Final-field detection still
     * works because it only relies on method-name presence.
     */
    private boolean traitFieldHasInitializer(ClassNode trait, String prefix, String originalName, boolean isFinal) {
      ClassNode helper = Traits.findHelper(trait);
      if (helper == null) return false;
      String remapped = prefix + originalName;

      if (isFinal) {
        return !helper.getMethods(TRAIT_INIT_METHOD + remapped).isEmpty();
      }

      List<MethodNode> initMethods = helper.getMethods(TRAIT_INIT_METHOD);
      if (initMethods.isEmpty()) return false;
      String setterName = remapped + "$set";
      for (MethodNode init : initMethods) {
        Statement code = init.getCode();
        if (!(code instanceof BlockStatement)) continue;
        for (Statement st : ((BlockStatement) code).getStatements()) {
          if (callsSetter(st, setterName)) return true;
        }
      }
      return false;
    }

    private boolean callsSetter(Statement st, String setterName) {
      if (!(st instanceof ExpressionStatement)) return false;
      Expression e = ((ExpressionStatement) st).getExpression();
      if (e instanceof MethodCallExpression) {
        return setterName.equals(((MethodCallExpression) e).getMethodAsString());
      }
      return false;
    }

    // ----------------------------------------------------------------
    // 2) Move trait $init$(this) calls into $spock_initializeFields
    // ----------------------------------------------------------------

    private void moveTraitInitializersIntoSpockInitializer(ClassNode spec, List<ClassNode> traits) {
      List<Statement> moved = removeTraitInitCalls(spec);
      if (moved.isEmpty()) return;
      BlockStatement body = ensureSpockInitializerMethodBody(spec);
      body.getStatements().addAll(moved);
    }

    /**
     * Remove ExpressionStatement → MethodCallExpression where the method is
     * Groovy's per-trait {@code $init$} on a class whose name ends with
     * {@code $Trait$Helper}. Preserves their order so the appended block
     * keeps Groovy's trait composition order.
     */
    private List<Statement> removeTraitInitCalls(ClassNode spec) {
      List<Statement> objectInits = spec.getObjectInitializerStatements();
      List<Statement> moved = new ArrayList<>();
      for (Iterator<Statement> it = objectInits.iterator(); it.hasNext(); ) {
        Statement st = it.next();
        if (isTraitInitCall(st)) {
          moved.add(st);
          it.remove();
        }
      }
      return moved;
    }

    private boolean isTraitInitCall(Statement statement) {
      if (!(statement instanceof ExpressionStatement)) return false;
      Expression expr = ((ExpressionStatement) statement).getExpression();
      if (!(expr instanceof MethodCallExpression)) return false;
      MethodCallExpression mce = (MethodCallExpression) expr;
      if (!TRAIT_INIT_METHOD.equals(mce.getMethodAsString())) return false;
      Expression target = mce.getObjectExpression();
      if (!(target instanceof ClassExpression)) return false;
      String targetName = target.getType().getName();
      return targetName != null && targetName.endsWith(TRAIT_HELPER_SUFFIX);
    }

    /**
     * Return the BlockStatement body of {@code $spock_initializeFields},
     * creating the method if SpecRewriter didn't already create it (which it
     * only does when at least one spec-declared field had an initializer).
     */
    private BlockStatement ensureSpockInitializerMethodBody(ClassNode spec) {
      MethodNode existing = spec.getDeclaredMethod(InternalIdentifiers.INITIALIZER_METHOD, Parameter.EMPTY_ARRAY);
      if (existing != null) {
        Statement code = existing.getCode();
        if (code instanceof BlockStatement) return (BlockStatement) code;
        BlockStatement block = new BlockStatement();
        if (code != null) block.addStatement(code);
        existing.setCode(block);
        return block;
      }
      BlockStatement body = new BlockStatement();
      MethodNode created = new MethodNode(
          InternalIdentifiers.INITIALIZER_METHOD,
          Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC,
          ClassHelper.OBJECT_TYPE.getPlainNodeReference(),
          Parameter.EMPTY_ARRAY,
          ClassNode.EMPTY_ARRAY,
          body);
      spec.addMethod(created);
      return body;
    }
  }
}
