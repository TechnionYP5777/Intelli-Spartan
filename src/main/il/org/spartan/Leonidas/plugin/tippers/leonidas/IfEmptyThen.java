package il.org.spartan.Leonidas.plugin.tippers.leonidas;

import java.util.HashMap;
import java.util.Map;

import static il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericPsiElementStub.booleanExpression;
import static il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericPsiElementStub.statement;

/**
 * Replace if(b); else{s;} with if(!b){s;}
 *
 * @author melanyc
 * @since 30-04-2017
 */
@SuppressWarnings({"ConstantConditions", "StatementWithEmptyBody"})
public class IfEmptyThen implements LeonidasTipperDefinition {

    /**
     * Write here additional constraints on the matcher tree.
     * The constraint are of the form:
     * the(<generic element>(<id>)).{is/isNot}(() - > <template>)[.ofType(Psi class)];
     */
    @Override
    public void constraints() {
    }

    @Override
    public void matcher() {
        new Template(() -> {
            if (booleanExpression(0))
                ;
            else
                statement(1);
        });
    }

    @Override
    public void replacer() {
        new Template(() -> {
            if (!(booleanExpression(0)))
                statement(1);
        });
    }

    @Override
    public Map<String,String> getExamples(){
        Map<String,String> examples = new HashMap<>();
        examples.put("int x=5; Object a,b; if(a!=b){;}else{x = 8;}","int x=5; Object a,b; if(!(a!=b)){x = 8;}");
        return examples;
    }
}