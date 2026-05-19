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
import org.spockframework.util.VersionChecker;
import spock.lang.Shared;

import java.util.List;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.transform.*;
import org.codehaus.groovy.transform.trait.Traits;

import static org.spockframework.compiler.AstUtil.primitiveConstExpression;

/**
 * Adds {@link FieldMetadata} annotations to the synthetic fields that Groovy's
 * trait composer generated on a {@code Specification} subclass from fields
 * declared in a trait the spec implements. Without these annotations,
 * {@code SpecInfoBuilder} would skip trait-declared fields and Spock's
 * annotation-driven extensions (e.g. {@code @AutoCleanup}, {@code @TempDir})
 * would never see them.
 *
 * <p>Runs at {@link CompilePhase#INSTRUCTION_SELECTION}, the same phase at
 * which Groovy's {@code TraitASTTransformation} schedules
 * {@code TraitComposer.doExtendTraits(...)}. At that point, the composed
 * trait-backed fields exist on the spec's {@link ClassNode} as fields named
 * {@code <traitFQCN_with_dots_to_underscores>__<originalName>}, with the
 * trait's annotations already propagated by Groovy.
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

      // By INSTRUCTION_SELECTION, Groovy's TraitASTTransformation has moved each
      // trait's original FieldNodes off the trait interface, so trait.getFields()
      // is empty. The authoritative source is the composed FieldNode on the spec
      // ClassNode, named "<TraitFQCN_with_dots_to_underscores>__<originalName>"
      // (TraitComposer.applyTrait). The trait's annotations are propagated to it.
      int nextOrdinal = computeNextOrdinal(spec);

      for (ClassNode trait : traits) {
        String prefix = trait.getName().replace('.', '_') + "__";
        for (FieldNode composed : spec.getFields()) {
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

          AnnotationNode ann = new AnnotationNode(nodeCache.FieldMetadata);
          ann.setMember(FieldMetadata.NAME, new ConstantExpression(originalName));
          ann.setMember(FieldMetadata.ORDINAL, primitiveConstExpression(nextOrdinal++));
          ann.setMember(FieldMetadata.LINE, primitiveConstExpression(composed.getLineNumber()));
          ann.setMember(FieldMetadata.INITIALIZER, primitiveConstExpression(composed.hasInitialExpression()));
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
  }
}
