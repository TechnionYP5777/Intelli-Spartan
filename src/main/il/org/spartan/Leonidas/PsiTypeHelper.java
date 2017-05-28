package il.org.spartan.Leonidas;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.testFramework.PsiTestCase;
import il.org.spartan.Leonidas.auxilary_layer.Utils;
import il.org.spartan.Leonidas.auxilary_layer.Wrapper;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Tests are extending this class to use a convenient API for handling Psi types and elements.
 * @author Oren Afek, Roey Maor, michalcohen
 * @since 26-12-2016
 */
public class PsiTypeHelper extends PsiTestCase {

    private static final String dummyTestFileName = "test.java";
    private static final String emptyText = "";

    /**
     * @return The dummy file in which the tested elements are created.
     */
    private PsiFile getTestFile() {
        return createDummyFile(dummyTestFileName, emptyText);
    }

    /**
     * @return The element factory with which this helper class creates new Psi elements.
     */
    private PsiElementFactory getTestFactory() {
        return JavaPsiFacade.getElementFactory(getTestFile().getProject());
    }

    /**
     * @param s - A string representing a statement.
     * @return - PsiStatement element that represents the given string
     */
    protected PsiStatement createTestStatementFromString(String s) {
        return getTestFactory().createStatementFromText(s, getTestFile());
    }

    /**
     * @param s - A string consists of a Java expression
     * @return - PsiExpression element that represents the given expression
     */
    protected PsiExpression createTestExpressionFromString(String s) {
        return getTestFactory().createExpressionFromText(s, getTestFile());
    }

    /**
     * @param s - A string consists of a Java class
     * @return - PsiClass element that represents the given class
     */
    public PsiClass createTestClassFromString(String s) {
        PsiClass dummyClass = getTestFactory().createClassFromText(s, getTestFile());
        //noinspection ConstantConditions
        assert dummyClass != null;
        return Utils.getChildrenOfType(dummyClass, PsiClass.class).get(0);
    }

    /**
     * @param javadoc        - A string containing the javadoc description of the class.
     * @param className      - The name of the class
     * @param classBody      - The content of the class
     * @param classModifiers - The modifiers of the class (such as "public", "abstract"...)
     * @return - PsiClass element with the given content.
     */
    protected PsiClass createTestClassFromString(String javadoc, String className, String classBody, String... classModifiers) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(classModifiers).forEach(sb::append);
        sb.append("class")
                .append(className)
                .append("{")
                .append(classBody)
                .append("}");
        return getTestFactory().createClassFromText(sb.toString(), getTestFile());
    }

    /**
     * @param interfaceName JD
     * @param interfaceBody JD
     * @param interfaceModifiers JD
     * @return PsiInterface element representing the given content
     */
    protected PsiClass createTestInterfaceFromString(String interfaceName, String interfaceBody, String... interfaceModifiers) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(interfaceModifiers).forEach(sb::append);
        sb.append("interface")
                .append(interfaceName)
                .append("{")
                .append(interfaceBody)
                .append("}");
        return getTestFactory().createClassFromText(sb.toString(), getTestFile());
    }

    protected PsiClass createEnumClassFromString(String s) {
        return getTestFactory().createEnum(s);
    }

    protected PsiClass createInterfaceClassFromString(String s) {
        return getTestFactory().createInterface(s);
    }

    protected PsiBlockStatement createTestBlockStatementFromString(String s) {
        return (PsiBlockStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    /**
     * doesn't work if there are several classes inside each other.
     *
     * @param f JD
     * @return the highest class defined in the file
     */
    protected PsiClass getClassInFile(PsiFile f) {
        Wrapper<PsiClass> classWrapper = new Wrapper<>(null);
        f.acceptChildren(new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                classWrapper.set(aClass);
            }
        });
        return classWrapper.get();
    }

    protected PsiMethod createTestMethodFromString(String s) {
        return getTestFactory().createMethodFromText(s, getTestFile());
    }

    protected PsiEnumConstant createTestEnumFromString(String s) {
        return getTestFactory().createEnumConstantFromText(s, getTestFile());
    }

    public PsiDeclarationStatement createTestEnumDecFromString(String s) {
        return (PsiDeclarationStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    protected PsiAnnotation createTestAnnotationFromString(String s) {
        return getTestFactory().createAnnotationFromText(s, getTestFile());
    }

    protected PsiType createTestTypeFromString(String s) {
        return getTestFactory().createTypeElementFromText(s, getTestFile()).getType();
    }

    protected PsiField createTestFieldDeclarationFromString(String s) {
        return getTestFactory().createFieldFromText(s, getTestFile());
    }

    protected PsiTypeElement createTestTypeElementFromString(String s) {
        return getTestFactory().createTypeElementFromText(s, getTestFile());
    }

    protected PsiIdentifier createTestIdentifierFromString(String s) {
        return getTestFactory().createIdentifier(s);
    }

    protected PsiDocComment createTestDocCommentFromString(String s) {
        return getTestFactory().createDocCommentFromText(s);
    }

    protected PsiComment createTestCommentFromString(String s) {
        return getTestFactory().createCommentFromText(s, getTestFile());
    }

    protected PsiWhileStatement createTestWhileStatementFromString(String s) {
        return (PsiWhileStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    protected PsiDoWhileStatement createTestDoWhileStatementFromString(String s) {
        return (PsiDoWhileStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    protected PsiSwitchStatement createTestSwitchStatementFromString(String s) {
        return (PsiSwitchStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    protected PsiForStatement createTestForStatementFromString(String s) {
        return (PsiForStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    protected PsiForeachStatement createTestForeachStatementFromString(String s) {
        return (PsiForeachStatement) getTestFactory().createStatementFromText(s, getTestFile());
    }

    protected PsiImportList createTestImportListFromString(String s) {
        return (PsiImportList) createTestFileFromString(s + "public class A{}").getNavigationElement().getFirstChild();
    }

    protected PsiCodeBlock createTestCodeBlockFromString(String s) {
        return getTestFactory().createCodeBlockFromText(s, getTestFile());
    }

    public PsiFile createTestFileFromString(String s) {
        return createDummyFile(dummyTestFileName, s);
    }

    protected PsiLiteralExpression createTestNullExpression() {
        return (PsiLiteralExpression) getTestFactory().createExpressionFromText("null", getTestFile());
    }

    protected PsiType createTestType(String s) {
        return getTestFactory().createType(getTestFactory().createClass(s));
    }

    protected PsiDeclarationStatement createTestDeclarationStatement(String name, String type, String initializer) {
        return getTestFactory().createVariableDeclarationStatement(name, createTestType(type),
				createTestExpressionFromString(initializer));
    }

    protected PsiIfStatement createTestIfStatement(String cond, String then) {
        return (PsiIfStatement) getTestFactory()
                .createStatementFromText("if (" + cond + ") {" + then + "} ", getTestFile());
    }

    /**
     * @param cond JD
     * @param then JD
     * @return if then has only one statement returns PsiIfStatement, otherwise null
     */
    public PsiIfStatement createTestIfStatementNoBraces(String cond, String then) {
		return StringUtils.countMatches(then, ";") != 1 ? null
				: (PsiIfStatement) getTestFactory().createStatementFromText("if (" + cond + ") " + then + " ",
						getTestFile());
	}

    protected PsiConditionalExpression createTestConditionalExpression(String cond, String then, String else$) {
        return (PsiConditionalExpression) getTestFactory()
                .createExpressionFromText(cond + " ? " + then + " : " + else$, getTestFile());
    }

    protected PsiMethodCallExpression createTestMethodCallExpression(String methodName, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append("(");
        Arrays.stream(args).forEach(sb::append);
        sb.append(")");
        return (PsiMethodCallExpression) getTestFactory()
                .createExpressionFromText(sb.toString(), getTestFile());
    }

    protected PsiExpression createTestExpression(String expression) {
        return getTestFactory()
                .createExpressionFromText(expression, getTestFile());
    }

    protected PsiTryStatement createTestTryStatement(String try$, String catch$, String do$) {
        return (PsiTryStatement) getTestFactory().createStatementFromText("try {" + try$ + "} catch(" + catch$ + "){" + do$ + "}", getTestFile());
    }

    protected PsiTryStatement createTestTryStatement(String try$, String catch$, String do$, String finally$) {
        return (PsiTryStatement) getTestFactory().createStatementFromText("try {" + try$ + "} catch(" + catch$ + "){" + do$ + "} finally {" + finally$ + "}", getTestFile());
    }

    protected void assertEqualsByText(PsiElement e1, PsiElement e2) {
        if (e1 == null && e2 == null)
			return;

        assert e1 != null;
        assert e2 != null;
        assertEquals(removeWhiteSpaces(e1.getText()), removeWhiteSpaces(e2.getText()));
    }

    private String removeWhiteSpaces(String s) {
        return s.replaceAll("\\s+", "");
    }

    protected PsiLambdaExpression createTestLambdaExpression(String s) {
        return (PsiLambdaExpression) getTestFactory().createExpressionFromText(s, getTestFile());
    }

    protected PsiNewExpression createTestNewExpression(String typeName, String... parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("new ")
                .append(typeName)
                .append("(");
        Arrays.stream(parameters).forEach(sb::append);
        sb.append(")");
        return (PsiNewExpression) getTestFactory().createExpressionFromText(sb.toString(), getTestFile());
    }

    protected PsiMethodReferenceExpression createTestMethodReferenceExpression(String typeName, String methodName) {
        return (PsiMethodReferenceExpression) getTestFactory().createExpressionFromText(typeName + "::" + methodName,
				getTestFile());
    }

    public PsiRequiresStatement createTestRequiresStatement(String module) {
        return (PsiRequiresStatement) getTestFactory().createStatementFromText("requires " + module + ";", getTestFile());
    }

    protected PsiReturnStatement createTestReturnStatement(String expression) {
        return (PsiReturnStatement) getTestFactory().createStatementFromText("return " + expression + ";", getTestFile());
    }

    protected PsiArrayAccessExpression createTestArrayAccess(String arrayName, String accessContent) {
        return (PsiArrayAccessExpression) getTestFactory().createExpressionFromText(arrayName + "[" + accessContent + "]", getTestFile());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
}
