package il.org.spartan.Leonidas.plugin.leonidas;

import com.intellij.psi.*;
import il.org.spartan.Leonidas.auxilary_layer.Wrapper;
import il.org.spartan.Leonidas.auxilary_layer.iz;
import il.org.spartan.Leonidas.plugin.EncapsulatingNode;
import il.org.spartan.Leonidas.plugin.leonidas.GenericPsiTypes.GenericPsi;
import il.org.spartan.Leonidas.plugin.leonidas.GenericPsiTypes.GenericPsiBlock;
import il.org.spartan.Leonidas.plugin.leonidas.GenericPsiTypes.GenericPsiExpression;
import il.org.spartan.Leonidas.plugin.leonidas.GenericPsiTypes.GenericPsiStatement;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This class defines methods that will represent generic structures of code
 * such as: statements, conditions, blocks, variable declaration and more.
 *
 * @author Oren Afek
 * @since 06-01-2017
 */
public class GenericPsiElementStub {

    /**
     * method stub representing a boolean expression for leonidas tippers
     *
     * @param id the serial no to distinct between several boolean expressions in the same tipper
     * @return true
     */

    public static boolean booleanExpression(int id) {
        return booleanExpression();
    }

    public static boolean booleanExpression() {
        return true;
    }

    /**
     * method stub representing a statement for leonidas tippers
     *
     * @param id the serial no to distinct between several statements in the same tipper
     */
    public static Object statement(int id) {return new Object();}

    public static Object statement() {return new Object();}

    /**
     * method stub representing an identifier for leonidas tippers
     *
     * @param id the serial no to distinct between several identifiers in the same tipper
     * @return stub object
     */
    public static Object identifier(int id) {
        return identifier();
    }

    public static Object identifier() {
        return new Object();
    }

    /**
     * method stub representing any code block
     * @param id the serial no to distinct between several identifiers in the same tipper
     */
    public static void anyBlock(int id) {
    }

    public static void anyBlock() {/**/}

    /**
     * method stub representing an array identifier for leonidas tippers
     *
     * @param id the serial no to distinct between several array identifiers in the same tipper
     * @return stub array
     */
    public static Object[] arrayIdentifier(int id) {
        return arrayIdentifier();
    }

    public static Object[] arrayIdentifier() {
        return new Object[0];
    }

    /**
     * method stub representing an array identifier for leonidas tippers
     *
     * @param id the serial no to distinct between several array identifiers in the same tipper
     * @return stub array
     */
    public static <T> Stream<T> streamMethodInvocations(int id) {
        return streamMethodInvocations();
    }

    public static <T> Stream<T> streamMethodInvocations() {
        return Stream.of();
    }

    public enum StubName {
        BOOLEAN_EXPRESSION("booleanExpression"),
        STATEMENT("statement"),
        IDENTIFIER("identifier"),
        ARRAY_IDENTIFIER("arrayIdentifier"),
        ANY_BLOCK("anyBlock");

        private String stubName;

        StubName(String stubName) { this.stubName = stubName; }

        public static StubName valueOfMethodCall(PsiMethodCallExpression x) {
            return Arrays.stream(values())
                    .filter(stub -> x.getMethodExpression().getText().equals(stub.stubName()))
                    .findFirst().orElseGet(null);

        }

        public static StubName valueOfStringExpression(String s) {
            return Arrays.stream(values())
                    .filter(stub -> s.equals(stub.stubMethodCallExpression()))
                    .findFirst().orElseGet(null);
        }

        public static StubName getGeneralTye(PsiElement e) {
            Wrapper<StubName> name = new Wrapper<>(null);
            e.accept(new JavaElementVisitor() {
                @Override
                public void visitCodeBlock(PsiCodeBlock b) {
                    super.visitCodeBlock(b);
                    name.set(ANY_BLOCK);
                }

                @Override
                public void visitExpression(PsiExpression x) {
                    super.visitExpression(x);
                    name.set(BOOLEAN_EXPRESSION);
                }

                @Override
                public void visitStatement(PsiStatement s) {
                    super.visitStatement(s);
                    name.set(STATEMENT);
                }

                @Override
                public void visitIdentifier(PsiIdentifier i) {
                    super.visitIdentifier(i);
                    name.set(IDENTIFIER);
                }

                @Override
                public void visitArrayAccessExpression(PsiArrayAccessExpression x) {
                    super.visitArrayAccessExpression(x);
                    name.set(ARRAY_IDENTIFIER);
                }
            });
            return name.get();
        }

        public String stubName() {
            return stubName;
        }

        public String stubMethodCallExpression() {
            return String.format("%s()", stubName);
        }

        public String stubMethodCallExpressionStatement() {
            return String.format("%s();", stubName);
        }

        public boolean matchesStubName(PsiMethodCallExpression x) {
            return x.getMethodExpression().getText().equals(this.stubName);
        }

        public GenericPsi getGenericPsiType(PsiElement inner, Integer id) {
            GenericPsi x;
            switch (this) {
                case BOOLEAN_EXPRESSION:
                    x = new GenericPsiExpression(PsiType.BOOLEAN, inner);
                    break;
                case STATEMENT:
                    x = new GenericPsiStatement(inner);
                    break;
                case ANY_BLOCK:
                    x = new GenericPsiBlock(inner);
                    break;
                default:
                    return null;
            }
            x.putUserData(KeyDescriptionParameters.ID, id);
            return x;
        }

        public boolean goUpwards(EncapsulatingNode prv, EncapsulatingNode next) {
            switch (this) {
			default:
				return prv.getText().equals(next.getText());
			case STATEMENT:
				return prv.getText().equals(next.getText()) || next.getText().equals(prv.getText() + ";");
			case ANY_BLOCK:
				return !iz.block(prv.getInner());
			}
        }
    }
}