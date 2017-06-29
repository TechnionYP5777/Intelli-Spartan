package il.org.spartan.Leonidas.plugin.tippers.leonidas;

import il.org.spartan.Leonidas.auxilary_layer.ExampleMapFactory;

import java.util.Map;

import static il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericPsiElementStub.booleanExpression;
import static il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericPsiElementStub.throwable;

/**
 * Collapse if(b)throw $; throw % => throw b ? $ : %
 *
 * @author Oren Afek
 * @since 30-5-2017
 */
public class CollapseTrinaryIfThrowNoElseThrow implements LeonidasTipperDefinition {

    @Override
    public void matcher() {
        new Template(() -> {
            /* start */
            if (booleanExpression(0))
                throw throwable(1);
            throw throwable(2);
            /* end */
        });
    }

    @Override
    public void replacer() {
        new Template(() -> {
            /* start */
            throw booleanExpression(0) ? throwable(1) : throwable(2);
            /* end */
        });
    }

    @Override
    public Map<String, String> getExamples() {
        return new ExampleMapFactory()
                .put("if (goophy != bucks)\n\tthrow new LooneyToonesException();\nthrow SameLooneyToones();",
                        "throw goophy != bucks ? new LooneyToonesException() : SameLooneyToones();")
                .put("if (goophy != bucks)\n\tthrow new LooneyToonesException();\nelse\nthrow SameLooneyToones();", null)
                .put("if (goophy != bucks){\n\tthrow new LooneyToonesException();\n}\nthrow SameLooneyToones();", null)
                .put("if(true){\nif (goophy != bucks)\n\tthrow new LooneyToonesException();\nthrow SameLooneyToones();\n}",
                        "if(true){\nthrow goophy != bucks ? new LooneyToonesException() : SameLooneyToones();\n}")
                .put("if(true)\nif (goophy != bucks)\n\tthrow new LooneyToonesException();\nthrow SameLooneyToones();", null)
                .map();
    }
}
