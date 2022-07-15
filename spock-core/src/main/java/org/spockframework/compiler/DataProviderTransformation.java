package org.spockframework.compiler;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.spockframework.compiler.model.*;

import java.util.List;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class DataProviderTransformation implements ASTTransformation {

  private static final AstNodeCache nodeCache = new SpockTransform().getAstNodeCache();

  @Override
  public void visit(ASTNode[] nodes, SourceUnit source) {
    MethodNode method = (MethodNode) nodes[1];
    Spec fakeSpec = new Spec(method.getDeclaringClass());
    Method methodModel = new HelperMethod(fakeSpec, method);
    WhereBlock whereBlock = new WhereBlock(methodModel);

    List<Statement> stats = AstUtil.getStatements(methodModel.getAst());
    // transfer statements to where block
    whereBlock.getAst().addAll(stats);
    // remove statements from method
    stats.clear();

    IRewriteResources rewriteResources = new IRewriteResources() {

      @Override
      public Spec getCurrentSpec() {
        throw new UnsupportedOperationException("getCurrentSpec");
      }

      @Override
      public Method getCurrentMethod() {
        throw new UnsupportedOperationException("getCurrentMethod");
      }

      @Override
      public Block getCurrentBlock() {
        throw new UnsupportedOperationException("getCurrentBlock");
      }

      @Override
      public void defineValueRecorder(List<Statement> stats, String variableNameSuffix) {
        throw new UnsupportedOperationException("defineValueRecorder");
      }

      @Override
      public void defineErrorRethrower(List<Statement> stats) {
        throw new UnsupportedOperationException("defineErrorRethrower");
      }

      @Override
      public void defineErrorCollector(List<Statement> stats, String variableNameSuffix) {
        throw new UnsupportedOperationException("defineErrorCollector");
      }

      @Override
      public VariableExpression captureOldValue(Expression oldValue) {
        throw new UnsupportedOperationException("captureOldValue");
      }

      @Override
      public MethodCallExpression getMockInvocationMatcher() {
        throw new UnsupportedOperationException("getMockInvocationMatcher");
      }

      @Override
      public AstNodeCache getAstNodeCache() {
        return nodeCache;
      }

      @Override
      public String getSourceText(ASTNode node) {
        throw new UnsupportedOperationException("getSourceText");
      }

      @Override
      public ErrorReporter getErrorReporter() {
        return new ErrorReporter(source);
      }
    };
    WhereBlockRewriter.rewrite(whereBlock, rewriteResources, false);
    method.setParameters(new Parameter[0]);

  }
}
