package il.org.spartan.Leonidas.plugin.leonidas;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIfStatement;
import il.org.spartan.Leonidas.PsiTypeHelper;
import il.org.spartan.Leonidas.auxilary_layer.Utils;
import il.org.spartan.Leonidas.auxilary_layer.Wrapper;
import il.org.spartan.Leonidas.plugin.Toolbox;
import il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.Encapsulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author michalcohen
 * @since 26-04-2017.
 */
public class MatcherTest extends PsiTypeHelper {

    /**
     * constraints(){
     *     element(1).isNot( () -> {
     *         if(booleanExpression(3){
     *             statement(4);
     *         }
     *     });
     *     element(2).is(() -> {
     *          return null;
     *     });
     *     element(3).isNot(() -> !booleanExpression(5));
     *     element(1).isNot(() -> {
     *          return null;
     *     });
     * }
     *
     *
     *
     * matcher()
     * if(booleanExpression(0){
     *     statement(1);
     *     statement(2);
     * }
     * @throws Exception
     */
    public void testMatch() throws Exception {
        Map<Integer, List<Matcher.Constraint>> constrains = new HashMap<>();
        Encapsulator n = buildTemplate(createTestIfStatement("booleanExpression(0)", "statement(1);\n statement(2);"));
        constrains.putIfAbsent(1, new LinkedList<>());
        constrains.putIfAbsent(2, new LinkedList<>());
        constrains.putIfAbsent(3, new LinkedList<>());
        Encapsulator firstConstraint = buildTemplate(createTestIfStatement("booleanExpression(3)", "statement(4);")),
				secondConstraint = buildTemplate(createTestReturnStatement("null")),
				thirdConstraint = buildTemplate(createTestExpression("!booleanExpression(5)")),
				forthConstraint = buildTemplate(createTestReturnStatement("null"));
        constrains.get(1).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.ISNOT, Utils.wrapWithList(firstConstraint)));
        constrains.get(2).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.IS, Utils.wrapWithList(secondConstraint)));
        constrains.get(3).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.ISNOT, Utils.wrapWithList(thirdConstraint)));
        constrains.get(1).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.ISNOT, Utils.wrapWithList(forthConstraint)));

        Matcher m = new Matcher(Utils.wrapWithList(n), constrains);
        assert m.match(createTestIfStatement("x > 2", "\nx++; \nreturn null;"));
        assert m.match(createTestIfStatement("x > 2", "\nif(!(x > 4)){x--;} \nreturn null;"));
        assert !m.match(createTestIfStatement("x > 2", "\nx++; \nx--;"));
        assert !m.match(createTestIfStatement("x > 2", "\nreturn null; \nreturn null;"));
        assert !m.match(createTestIfStatement("x > 2", "\nif(x < 3){x--;} \nreturn null;"));
        assert !m.match(createTestIfStatement("x > 2", "\nif(x > 4){x--;} \nreturn null;"));
    }

    public void testExtractInfo() throws Exception {
        Map<Integer, List<Matcher.Constraint>> constrains = new HashMap<>();
        Encapsulator n = buildTemplate(createTestIfStatement("booleanExpression(0)", "statement(1);\n statement(2);"));
        constrains.putIfAbsent(1, new LinkedList<>());
        constrains.putIfAbsent(2, new LinkedList<>());
        constrains.putIfAbsent(3, new LinkedList<>());
        Encapsulator firstConstraint = buildTemplate(createTestIfStatement("booleanExpression(3)", "statement(4);")),
				secondConstraint = buildTemplate(createTestReturnStatement("null")),
				thirdConstraint = buildTemplate(createTestExpression("!booleanExpression(5)")),
				forthConstraint = buildTemplate(createTestReturnStatement("null"));
        constrains.get(1).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.ISNOT, Utils.wrapWithList(firstConstraint)));
        constrains.get(2).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.IS, Utils.wrapWithList(secondConstraint)));
        constrains.get(3).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.ISNOT, Utils.wrapWithList(thirdConstraint)));
        constrains.get(1).add(new Matcher.StructuralConstraint(Matcher.StructuralConstraint.ConstraintType.ISNOT, Utils.wrapWithList(forthConstraint)));

        Matcher m = new Matcher(Utils.wrapWithList(n), constrains);
        PsiIfStatement tm1 = createTestIfStatement("x > 2", "\nx++; \nreturn null;");
        Wrapper<Integer> i = new Wrapper<>(0);
        Map<Integer, List<PsiElement>> map = m.extractInfo(tm1, i);
        assertEquals(map.get(0).get(0).getText(), "x > 2");
        assertEquals(map.get(1).get(0).getText(), "x++;");
        assertEquals(map.get(2).get(0).getText(), "return null;");
        map = m.extractInfo(createTestIfStatement("x > 2", "\nif(!(x > 4)){x--;} \nreturn null;"), i);
        assertEquals(map.get(1).get(0).getText(), "if(!(x > 4)){x--;}");
        assertEquals(map.get(2).get(0).getText(), "return null;");
        map = m.extractInfo(createTestIfStatement("x > 2", "\nx++; \nx--;"), i);
        assertEquals(map.get(1).get(0).getText(), "x++;");
        assertEquals(map.get(2).get(0).getText(), "x--;");
        map = m.extractInfo(createTestIfStatement("x > 2", "\nreturn null; \nreturn null;"), i);
        assertEquals(map.get(1).get(0).getText(), "return null;");
        assertEquals(map.get(2).get(0).getText(), "return null;");
        map = m.extractInfo(createTestIfStatement("x > 2", "\nif(x < 3){x--;} \nreturn null;"), i);
        assertEquals(map.get(1).get(0).getText(), "if(x < 3){x--;}");
        assertEquals(map.get(2).get(0).getText(), "return null;");
        map = m.extractInfo(createTestIfStatement("x > 2", "\nif(x > 4){x--;} \nreturn null;"), i);
        assertEquals(map.get(1).get(0).getText(), "if(x > 4){x--;}");
        assertEquals(map.get(2).get(0).getText(), "return null;");
    }

    /**
     * Inserts the ID numbers into the user data of the generic method call expressions.
     * For example 5 will be inserted to booleanExpression(5).
     *
     * @param tree the root of the tree for which we insert IDs.
     */
    private void giveIdToStubMethodCalls(PsiElement tree) {
        tree.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement e) {
                super.visitElement(e);
                Toolbox.getInstance().getGenericsBasicBlocks().stream().filter(g -> g.conforms(e)).findFirst().ifPresent(g -> e.putUserData(KeyDescriptionParameters.ID, g.extractId(e)));
            }
        });
    }

    private Encapsulator buildTemplate(PsiElement e) {
        giveIdToStubMethodCalls(e);
        return Pruning.prune(Encapsulator.buildTreeFromPsi(e), new HashMap<>());
    }
}