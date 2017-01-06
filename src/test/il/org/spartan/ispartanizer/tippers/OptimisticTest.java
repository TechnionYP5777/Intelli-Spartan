package il.org.spartan.ispartanizer.tippers;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.impl.ModuleImpl;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import il.org.spartan.ispartanizer.auxilary_layer.CompilationCenter;
import il.org.spartan.ispartanizer.auxilary_layer.haz;

/**
 * Created by maorroey on 1/4/2017.
 */
public class OptimisticTest extends TipperTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CompilationCenter.initialize();
    }

    public void testDetectsSyntaxErrors() throws Exception {
        assertTrue(haz.syntaxErrors(createTestMethodFromString("boolean dummy(){return (5=!=5);}")));
        //changing a final variable error:
        String source1 = "package test; "+
                        "public class Test { "+
                            "public Test() { "+
                                "final int x=3; x=5; System.out.println(\"lalala\"); "+
                            "} "+
                        "}";

        assertTrue(haz.compilationErrors(createTestFileFromString(source1)));
    }

    public void testNoSyntaxErrors(){
        assertFalse(haz.syntaxErrors(createTestExpressionFromString("5 == 5")));
        assertFalse(haz.syntaxErrors(createTestMethodFromString("boolean dummy(){return false;}")));
        assertFalse(haz.syntaxErrors(createTestMethodFromString("boolean dummy(){return (5==5);}")));
        String source2 = "package test; "+
                            "public class Test { "+
                                "public Test() { "+
                                    "final int x=3; System.out.println(\"lalala\"); "+
                                "} "+
                          "}";

        assertFalse(haz.compilationErrors(createTestFileFromString(source2)));
    }

}