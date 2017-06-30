package il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks;

import il.org.spartan.Leonidas.PsiTypeHelper;

import java.util.HashMap;

/**
 * @author Sharon LK
 */
public class IdentifiableTest extends PsiTypeHelper {
    Identifiable id;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        id = new Identifier();
    }

    public void testConforms() {
        assertTrue(id.conforms(createTestIdentifierFromString("identifier1")));
        assertTrue(id.conforms(createTestIdentifierFromString("identifier2")));
        assertTrue(id.conforms(createTestIdentifierFromString("identifier3")));
        assertFalse(id.conforms(createTestIdentifierFromString("x")));
        assertFalse(id.conforms(createTestIdentifierFromString("some_id")));
        assertFalse(id.conforms(createTestStatementFromString("identifier1++;")));
    }

    public void testGeneralizes() {
        assertTrue(id.generalizes(Encapsulator.buildTreeFromPsi(createTestIdentifierFromString("x")), new HashMap<>()).matches());
        assertTrue(id.generalizes(Encapsulator.buildTreeFromPsi(createTestIdentifierFromString("y")), new HashMap<>()).matches());
        assertFalse(id.generalizes(Encapsulator.buildTreeFromPsi(createTestStatementFromString("x++;")), new HashMap<>()).matches());
        assertFalse(id.generalizes(Encapsulator.buildTreeFromPsi(createTestExpressionFromString("true && false")), new HashMap<>()).matches());
        assertFalse(id.generalizes(Encapsulator.buildTreeFromPsi(createTestExpressionFromString("x + 2")), new HashMap<>()).matches());
    }
}