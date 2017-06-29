package il.org.spartan.Leonidas.plugin.tippers.leonidas;

import il.org.spartan.Leonidas.auxilary_layer.ExampleMapFactory;

import java.util.Map;

import static il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericPsiElementStub.anyNumberOf;
import static il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericPsiElementStub.statement;

/**
 * for(?;true;?) => for(?;;?)
 *
 * @author Oren Afek
 * @since 31/05/2017
 */
public class ForTrueConditionRemove implements LeonidasTipperDefinition {

    int identifier0;

    @Override
    public void constraints() {
    }

    @Override
    public void matcher() {
        new Template(() -> {
            /* start */
            for (statement(0); true; statement(1)) {
                anyNumberOf(statement(2));
            }
            /* end */
        });
    }

    @Override
    public void replacer() {
        new Template(() -> {
            /* start */
            for (statement(0); ; statement(1)) {
                anyNumberOf(statement(2));
            }
            /* end */
        });
    }

    @Override
    public Map<String, String> getExamples() {
        return new ExampleMapFactory()
                .put("for(int i = 0; true; ++i){\n\tSystem.out.println(i);\n}", "for(int i = 0;; ++i){\n\tSystem.out.println(i);\n}")
                .put("for(int i = 0; x == x; ++i){\n\tSystem.out.println(i);\n}", null)
                .put("for(;true;){\n\t;\n}", null)
                .put("for(int i = 0; true; ++i)\n\tSystem.out.println(i);\n", null)
                .map();
    }
}