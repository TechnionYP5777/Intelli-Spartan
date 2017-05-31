package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.plugin.leonidas.Matcher;
import il.org.spartan.Leonidas.plugin.leonidas.MatchingResult;

import java.util.List;
import java.util.Map;

/**
 * @author Oren Afek
 * @since 30/5/2017.
 */
public class Throwable extends GenericMethodCallBasedBlock {
    private static final String TEMPLATE = "throwable";

    @SuppressWarnings("unused")
    public Throwable(PsiElement e) {
        super(e, TEMPLATE);
    }

    public Throwable(Encapsulator n) {
        super(n, TEMPLATE);
    }

    /**
     * For reflection use DO NOT REMOVE!
     */
    @SuppressWarnings("unused")
    protected Throwable() {
        super(TEMPLATE);
    }

    @Override
    public MatchingResult generalizes(Encapsulator e) {
        return new MatchingResult(super.generalizes(e).matches() && iz.expression(e.getInner()));
    }

    @Override
    protected boolean goUpwards(Encapsulator prev, Encapsulator next) {
        return next != null && prev.getText().equals(next.getText());
    }

    @Override
    public GenericEncapsulator create(Encapsulator e, Map<Integer, List<Matcher.Constraint>> map) {
        return new Throwable(e);
    }
}
