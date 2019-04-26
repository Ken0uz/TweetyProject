package net.sf.tweety.logics.ml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.parser.MlParser;
import net.sf.tweety.logics.ml.reasoner.MleanCoPReasoner;
import net.sf.tweety.logics.ml.syntax.MlBeliefSet;

/**
 * JUnit Test class for MleanCoP.
 * 
 * @author Anna Gessler
 */
public class MleanCoPTest {

	MlParser parser;
	MleanCoPReasoner prover;
	public static final int DEFAULT_TIMEOUT = 10000;
	
	@Before 
	public void init() throws ParserException, IOException {
		parser = new MlParser();
		MlBeliefSet b = parser.parseBeliefBase("Test={test} \n type(p) \n type(q(Test)) \n p \n q(test)");
		parser.setSignature((FolSignature) b.getMinimalSignature());
		prover = new MleanCoPReasoner("/home/anna/sw/mlProver/mleancop/mleancop.sh");
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void SimpleQueryTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula) parser.parseFormula("p||!p");
		FolFormula f2 = (FolFormula) parser.parseFormula("p&&!p");
		assertTrue(prover.query(new MlBeliefSet(),f1));
		assertFalse(prover.query(new MlBeliefSet(),f2));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void ComplexQueryTest() throws ParserException, IOException {
		FolFormula f1 = (FolFormula) parser.parseFormula("[](forall X:(q(X)))=>forall X:( [](q(X)))");
		assertTrue(prover.query(new MlBeliefSet(),f1));
	}
}
