import static org.junit.Assert.*;

import org.junit.Assert.*;

import org.junit.Test;

public class ProofTest {
	@Test
	public void proofConstructorTest() {
		TheoremSet thms = new TheoremSet();
		Proof pftest = new Proof(thms);
		assertTrue(pftest.getOriginalProof() == null);
		assertTrue(pftest.getCurrentProof() == pftest.getOriginalProof());
		assertTrue(pftest.getCurrentLineNumber() == null);
		assertTrue(pftest.getIsTrueGlobal().size() == 0);
		assertTrue(pftest.getTheoremSet() == thms);
	}
	
	@Test
	public void subProofConstructorTest() throws IllegalLineException {
		Proof pftest = new Proof(null);
		Proof.SubProof sub1 = new Proof.SubProof(null, null, null);
		assertTrue(sub1.getMyExpression() == null);
		assertTrue(sub1.getLineNumber() == null);
		assertTrue(sub1.getEverythingSoFar().size() == 0);
		assertFalse(sub1.hasParent());
		assertTrue(sub1.getChildProofs().size() == 0);
		Proof.SubProof sub2 = new Proof.SubProof(new Expression("p"), sub1, new LineNumber());
		assertTrue(sub2.getMyExpression() != null);
		assertTrue(sub2.hasParent());
		assertTrue(sub2.getParent() == sub1);
		assertTrue(sub2.getLineNumber()!= null);
		pftest.setOriginalProof(sub2);
		assertTrue(pftest.getOriginalProof() == sub2);
		pftest.setCurrentProof(sub2);
		assertTrue(pftest.getCurrentProof() == sub2);
		pftest.getCurrentProof().getLineNumber().setIsProven(true);
		assertTrue(pftest.isComplete());
	}
	
	@Test
	public void isCompleteTest() throws IllegalLineException {
		Proof pftest = new Proof(null);
		assertFalse(pftest.isComplete());
		Proof.SubProof pf1 = new Proof.SubProof(new Expression("(p=>q)"), pftest.getOriginalProof(), null);
		Proof.SubProof pf2 = new Proof.SubProof(new Expression("(p=>q)"), pftest.getOriginalProof(), null);
		pftest.setOriginalProof(pf1);
		pftest.setCurrentProof(pf2);
		pf2.setLineNumber(new LineNumber());
		assertFalse(pftest.isComplete());
		pf2.getLineNumber().setIsProven(true);
		assertTrue(pftest.isComplete());
		
		Proof.SubProof pf3 = new Proof.SubProof(new Expression("p"), pftest.getOriginalProof(), null);
		pf3.setLineNumber(new LineNumber());
		pftest.setCurrentProof(pf3);
		assertFalse(pftest.isComplete());
		pf3.getLineNumber().setIsProven(true);
		assertFalse(pftest.isComplete());
		
		Proof.SubProof pf4 = new Proof.SubProof(new Expression("(q=>p)"), pftest.getOriginalProof(), null);
		pf4.setLineNumber(new LineNumber());
		pftest.setCurrentProof(pf4);
		assertFalse(pftest.isComplete());
		pf4.getLineNumber().setIsProven(true);
		assertFalse(pftest.isComplete());
	}

	@Test
	// This tests the entire line number class as well as the nextLineNumber method in Proof.java.
	// There is a LOT of code, because I wrote this class to be independent of the functionality of
	// most other classes/methods in order to be able to test it without everything else working first.
	// This is why after every call, I have to manually set up the environment again to simulate a step
	// being proved/the reason being updated.
	public void nextLineNumberTest() {
		Proof pftest = new Proof(null);
		pftest.setOriginalProof(new Proof.SubProof(null, null, pftest.getCurrentLineNumber()));
		pftest.setCurrentLineNumber(new LineNumber()); 
		pftest.getCurrentLineNumber().setProof(pftest.getOriginalProof());
		pftest.setCurrentProof(pftest.getOriginalProof());
		pftest.getCurrentLineNumber().setReason("assume");
		
		assertTrue(pftest.getCurrentLineNumber().toString().equals("1"));
		pftest.getCurrentLineNumber().setProof(pftest.getOriginalProof());
		pftest.getCurrentLineNumber().setIsProven(true);
		assertTrue(pftest.nextLineNumber().toString().equals("2"));
		pftest.getCurrentLineNumber().setProof(pftest.getOriginalProof());
		
		// attempt to start new subproof
		Proof.SubProof pf2 = new Proof.SubProof(null, pftest.getOriginalProof(), pftest.getCurrentLineNumber());
		pftest.setCurrentProof(pf2);
		pftest.getCurrentLineNumber().setReason("show");
		pftest.getCurrentLineNumber().setProof(pf2);
		pftest.getCurrentLineNumber().setIsProven(true);
		assertTrue(pftest.nextLineNumber().toString().equals("2.1"));
		pftest.getCurrentLineNumber().setProof(pf2);
		pftest.getCurrentLineNumber().setReason("mt");
		pftest.getCurrentProof().getLineNumber().setIsProven(false);
		pftest.setPreviousLineNumber(pftest.getCurrentLineNumber());
		pftest.getCurrentLineNumber().setReason("mt");
		assertTrue(pftest.nextLineNumber().toString().equals("2.2"));
		pftest.getCurrentLineNumber().setReason("mt");
		assertTrue(pftest.nextLineNumber().toString().equals("2.3"));
		
		
		//attempt to exit subproof
		pftest.getCurrentProof().getLineNumber().setIsProven(true);
		pftest.getCurrentLineNumber().setReason("mt");
		assertTrue(pftest.nextLineNumber().toString().equals("3"));
		pftest.setCurrentProof(pftest.getOriginalProof());
		pftest.getCurrentLineNumber().setProof(pftest.getOriginalProof());
		assertTrue(pftest.nextLineNumber().toString().equals("4"));
		pftest.getPreviousLineNumber().setReason("mt");
		assertTrue(pftest.nextLineNumber().toString().equals("5"));
		
		// attempt to start another subproof
		Proof.SubProof pf3 = new Proof.SubProof(null, pftest.getOriginalProof(), pftest.getCurrentLineNumber());
		pftest.getCurrentLineNumber().setReason("show");
		pftest.setCurrentProof(pf3);
		pftest.getCurrentLineNumber().setProof(pf3);
		pftest.getPreviousLineNumber().setReason("mt");
		assertTrue(pftest.nextLineNumber().toString().equals("5.1"));
		pftest.getCurrentLineNumber().setReason("mt");
		pftest.getCurrentLineNumber().setProof(pf3);
		assertTrue(pftest.nextLineNumber().toString().equals("5.2"));
		pftest.getCurrentLineNumber().setProof(pf3);
		pftest.getCurrentLineNumber().setReason("mt");
		assertTrue(pftest.nextLineNumber().toString().equals("5.3"));
		
		// attempt to start sub-subproof
		Proof.SubProof pf3a = new Proof.SubProof(null, pf3, pftest.getCurrentLineNumber());
		pftest.getCurrentLineNumber().setReason("show");
		pftest.setCurrentProof(pf3a);
		pftest.getCurrentLineNumber().setProof(pf3a);
		assertTrue(pftest.nextLineNumber().toString().equals("5.3.1"));
		pftest.getCurrentLineNumber().setReason("ic");
		pftest.getCurrentLineNumber().setProof(pf3a);
		assertTrue(pftest.nextLineNumber().toString().equals("5.3.2"));
		pftest.getCurrentLineNumber().setProof(pf3a);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.3.3"));
		
		// attempt to exit sub-subproof
		pftest.getCurrentProof().getLineNumber().setIsProven(true);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.4"));
		pftest.getCurrentLineNumber().setReason("ic");
		pftest.setCurrentProof(pf3);
		assertTrue(pftest.nextLineNumber().toString().equals("5.5"));
		
		// attempt to start sub-subproof
		Proof.SubProof pf5a = new Proof.SubProof(null, pf3, pftest.getCurrentLineNumber());
		pftest.getCurrentLineNumber().setReason("show");
		pftest.setCurrentProof(pf5a);
		pftest.getCurrentLineNumber().setProof(pf5a);
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.1"));
		pftest.getCurrentLineNumber().setReason("ic");
		pftest.getCurrentLineNumber().setProof(pf5a);
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.2"));
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.3"));
		
		// attempt to start sub-sub-subproof
		Proof.SubProof pf5b = new Proof.SubProof(null, pf5a, pftest.getCurrentLineNumber());
		pftest.getCurrentLineNumber().setReason("show");
		pftest.setCurrentProof(pf5b);
		pftest.getCurrentLineNumber().setProof(pf5b);
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.3.1"));
		pftest.getCurrentLineNumber().setReason("ic");
		pftest.getCurrentLineNumber().setProof(pf5b);
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.3.2"));
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.3.3"));
		
		// attempt to exit sub-sub-subproof
		pftest.getCurrentProof().getLineNumber().setIsProven(true);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.4"));
		pftest.setCurrentProof(pf5a);
		pftest.getCurrentLineNumber().setProof(pf5a);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.5.5"));
				
		// attempt to exit sub-subproof
		pftest.getCurrentProof().getLineNumber().setIsProven(true);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.6"));
		pftest.setCurrentProof(pf3);
		pftest.getCurrentLineNumber().setProof(pf3);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("5.7"));
		
		// attempt to exit subproof
		pftest.getCurrentProof().getLineNumber().setIsProven(true);
		pftest.getCurrentLineNumber().setReason("ic");
		assertTrue(pftest.nextLineNumber().toString().equals("6"));
		pftest.setCurrentProof(pftest.getOriginalProof());
		pftest.getCurrentLineNumber().setProof(pftest.getOriginalProof());
		assertTrue(pftest.nextLineNumber().toString().equals("7"));
		pftest.getCurrentLineNumber().setReason("ic");
		pftest.setPreviousLineNumber(pftest.getCurrentLineNumber());
		assertTrue(pftest.nextLineNumber().toString().equals("8"));
	}

}











