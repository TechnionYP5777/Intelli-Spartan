package il.org.spartan.Leonidas.plugin.tippers;


import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import il.org.spartan.Leonidas.PsiTypeHelper;
import il.org.spartan.Leonidas.auxilary_layer.Utils;
import il.org.spartan.Leonidas.plugin.Spartanizer;
import il.org.spartan.Leonidas.plugin.Toolbox;
import il.org.spartan.Leonidas.plugin.tippers.leonidas.LeonidasTipperDefinition;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;

import java.util.Map;

/**
 * @author @roey maor
 * @since 27-05-2017.
 */
/*
This class should be used by all tipper tests in order to test the tippers by the examples they provide.
It is correct to demand the creator of the tipper to provide the examples for testing, and it's he's
responsibility to make sure he has included examples that provide adequate coverage for testing.
examples can be in one of two forms:

1) String -> String : The tipper should be applied on the former, and output a PSI Element tree
                      that conforms to the latter
2) String -> null : The tipper should not affect the former.

 */

public class TipperTest{

    private static final String before = "package test;\n public class Foo\n{\n public void Bar(){\n";
    private static final String after = "\n }\n}";
    Tipper tipper = null;
    LeonidasTipperDefinition leonidasTipper = null;
    Boolean leonidasMode; //whether we are testing a leonidas or non-leonidas tipper
    PsiTypeHelper junitTest;
    private Boolean setup = false;

    TipperTest(Tipper t, PsiTypeHelper test){
        this.tipper = t;
        this.leonidasMode = false;
        this.junitTest = test;

    }

    TipperTest(LeonidasTipperDefinition l,PsiTypeHelper test){
        this.leonidasTipper = l;
        this.leonidasMode = true;
        this.junitTest = test;
    }



    private Map<String,String> getExamples(){
        if(leonidasMode){
            return leonidasTipper.getExamples();
        }
        return tipper.getExamples();
    }

    private void setUp(){
        if(setup) return;
        setup = true;
    }

    public void check(){
        if(!setup) {
            try {
                this.setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String,String> examples = getExamples();
        for (Map.Entry<String,String> entry : examples.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String beforeFileString = before+key+after;
            System.out.println(beforeFileString);
            PsiElement beforeElement = junitTest.createTestFileFromString(beforeFileString);
            //System.out.println(beforeElement.getText());

        }
        //Spartanizer.spartanizeElementWithTipper(null,"lala");
    }
    private boolean byExample(String input, String output){
        if(!setup) {
            try {
                this.setUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(leonidasMode){
            return leonidasTipperByExample(input,output);
        }
        else{
            return regularTipperByExample(input,output);
        }
    }

    private boolean leonidasTipperByExample(String input,String output){
        return false;
    }

    private boolean regularTipperByExample(String input, String output){
        return false;
    }


}
