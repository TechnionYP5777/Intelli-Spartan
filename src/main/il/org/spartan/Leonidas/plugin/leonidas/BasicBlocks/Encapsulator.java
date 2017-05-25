package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static il.org.spartan.Leonidas.plugin.leonidas.KeyDescriptionParameters.ID;

/**
 * Encapsulating Psi elements so that non illegal transformation is done on the psi trees of Intellij.
 *
 * @author michalcohen
 * @since 22-02-2017
 */
public class Encapsulator implements Cloneable, VisitableNode, Iterable<Encapsulator> {
    protected PsiElement inner;
    protected Encapsulator parent;
    protected List<Encapsulator> children = new LinkedList<>();

    /**
     * For reflection use DO NOT REMOVE!
     */
    @SuppressWarnings("unused")
    protected Encapsulator() {
    }

    public Encapsulator(PsiElement e) {
        inner = e;
        Arrays.stream(e.getChildren()).forEach(child -> children.add(new Encapsulator(child, this)));
    }

    private Encapsulator(PsiElement e, Encapsulator parent) {
        this(e);
        this.parent = parent;
    }

    public Encapsulator(Encapsulator n) {
        this(n, n.parent);
    }

    public Encapsulator(Encapsulator n, Encapsulator parent) {
        this.parent = parent;
        inner = n.inner;
        children = n.getChildren().stream().map(c -> new Encapsulator(c, this)).collect(Collectors.toList());
    }

    /**
     * @param e PsiElement
     * @return an encapsulating node that hides e.
     */
    public static Encapsulator buildTreeFromPsi(PsiElement e) {
        return new Encapsulator(e);
    }

    public List<Encapsulator> getChildren() {
        return children;
    }

    public Encapsulator getParent() {
        return parent;
    }

    @Override
    public void accept(EncapsulatorVisitor v) {
        v.visit(this);
        children.forEach(child -> child.accept(v));
    }

    @Override
    public <T> T accept(EncapsulatorValueVisitor<T> v, BinaryOperator<T> accumulator) {
        return accumulator.apply(v.visit(this), children.stream().filter(Objects::nonNull).map(child -> child.accept(v, accumulator))
                .reduce(accumulator).orElse(null));
    }

    public PsiElement getInner() {
        return inner;
    }

    /**
     * @return the amount of children that are not white space Psi elements.
     */
    public int getAmountOfActualChildren() {
        return getActualChildren().size();
    }

    private List<Encapsulator> getActualChildren() {
        return children.stream().filter(child -> !iz.whiteSpace(child.getInner())).collect(Collectors.toList());
    }

    public String toString() {
        return inner != null ? inner.toString() : "stub";
    }

    @NotNull
    public String getText() {
        return inner.getText() != null ? inner.getText() : "";
    }

    @Override
    public Encapsulator.Iterator iterator() {
        return new Encapsulator.Iterator();
    }

    /**
     * Replaces a concrete element with a generalized one.
     *
     * @param replacer the new Generalized element
     * @return the replacer
     */
    public Encapsulator generalizeWith(Encapsulator replacer) {
        parent.children.replaceAll(e -> e == Encapsulator.this ? replacer : e);
        replacer.parent = this.parent;
        return replacer;
    }

    public boolean isGeneric() {
        return false;
    }

    /**
     * @return the ID of the generic element
     */
    public Integer getId() {
        return inner.getUserData(ID);
    }

    /**
     * @param i ID
     * @return this, for fluent API
     */
    public Encapsulator putId(Integer i) {
        inner.putUserData(ID, i);
        return this;
    }

    /**
     * <b>Linear Eager Iterator</b> for iterating over the tree without considering white spaces.
     * This iterator can hold its advance for n {@link #next() next} calls by calling
     * {@link #setNumberOfOccurrences(int) setNoOfOccurrences(int) }.
     *
     * @author Oren Afek
     * @since 14/05/17
     */
    public class Iterator implements java.util.Iterator<Encapsulator>, Cloneable {

        int cursor = 0;
        private int skipCounter;
        private int skipOverall;
        private boolean shouldSkip;
        private List<Encapsulator> elements = new LinkedList<>();

        private Iterator() {
            initializeElements(Encapsulator.this, elements);
        }

        private void initializeElements(Encapsulator e, List<Encapsulator> l) {
            l.add(e);
            e.getActualChildren().forEach(c -> initializeElements(c, l));
        }

        @Override
        public boolean hasNext() {
            return shouldSkip || cursor < elements.size();
        }

        @Override
        public Encapsulator next() {
            if (shouldSkip && skipCounter < skipOverall) {
                skipCounter++;
                return elements.get(cursor);
            }
            shouldSkip = false;
            return elements.get(cursor++);
        }

        public Encapsulator value() {
            return elements.get(cursor);
        }

        @Override
        public Encapsulator.Iterator clone() {
            try {
                Iterator cloned = (Iterator) super.clone();
                cloned.elements = new LinkedList<>(elements);
                cloned.cursor = cursor;
                cloned.shouldSkip = shouldSkip;
                cloned.skipCounter = skipCounter;
                cloned.skipOverall = skipOverall;
                return cloned;
            } catch (CloneNotSupportedException e) {
                return this;
            }
        }

        public int getNumberOfOccurrences() {
            return skipOverall;
        }

        public Iterator setNumberOfOccurrences(int i) {
            shouldSkip = true;
            skipOverall = i;
            return this;
        }

        /**
         * Delete from the list of elements all the elements that were already matched by an higher level composite
         * component, matched to a generic encapsulator.
         */
        public void matchedWithGeneric() {
            Encapsulator current = elements.get(cursor);
            current.accept(n -> {
                if (n != current)
                    elements.remove(n);
            });
        }
    }
}
