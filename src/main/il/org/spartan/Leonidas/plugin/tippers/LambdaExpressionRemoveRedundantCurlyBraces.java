package il.org.spartan.Leonidas.plugin.tippers;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLambdaExpressionImpl;
import il.org.spartan.Leonidas.auxilary_layer.PsiRewrite;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.plugin.tipping.Tip;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;

import static il.org.spartan.Leonidas.auxilary_layer.step.*;

/**
 * @author Oren Afek
 * @since 01-12-2016
 */

public class LambdaExpressionRemoveRedundantCurlyBraces implements Tipper<PsiLambdaExpression> {

    @Override
    public boolean canTip(PsiElement ¢) {
        return ¢ instanceof PsiLambdaExpression && canTip((PsiLambdaExpression) ¢);
    }

    private boolean canTip(PsiLambdaExpression element) {
        return iz.block(element.getBody()) && statements(blockBody(element)).size() == 1 //
                && (isCandidateReturnStatement(element) || isCandidateStatementExpression(element));
    }

    private boolean isCandidateReturnStatement(PsiLambdaExpression element) {
        return iz.returnStatement(firstStatement(blockBody(element)));
    }

    private boolean isCandidateStatementExpression(PsiLambdaExpression element) {
        return iz.expressionStatement(firstStatement(blockBody(element)));
    }

    @Override
    public String description(PsiLambdaExpression x) {
        return "Remove redundant curly braces";
    }

    @Override
    public String name() {
        return "LambdaExpressionRemoveRedundantCurlyBraces";
    }

    @Override
    public Tip tip(final PsiLambdaExpression element) {
        assert statements(blockBody(element)).size() == 1;
        final PsiStatement s = firstStatement(blockBody(element));

        return new Tip(description(element), element, this.getClass()) {
            @Override
            public void go(final PsiRewrite r) {
                if (isCandidateReturnStatement(element))
					r.replace(element.getBody(), expression((PsiReturnStatement) s));

                if (isCandidateStatementExpression(element))
					r.replace(element.getBody(), expression((PsiExpressionStatement) s));

            }
        };

    }

    @Override
    public Class<? extends PsiLambdaExpression> getPsiClass() {
        return PsiLambdaExpressionImpl.class;
    }

}
