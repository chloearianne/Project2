import static org.junit.Assert.*;

import org.junit.Assert.*;

import org.junit.Test;


public class ProofTest {
	@Test
	public void nextLineNumberTest() {
		Proof pftest = new Proof(null);
		pftest.currentLine = new LineNumber(); // step = 0
		pftest.originalProof.myLineNumber = pftest.currentLine;
		pftest.currentLine.myProof = pftest.originalProof;
		pftest.currentProof = pftest.originalProof;
		pftest.currentReason = "assume";
		
		assertTrue(pftest.nextLineNumber().myString.equals("1"));
		pftest.currentLine.myProof = pftest.originalProof;
		assertTrue(pftest.nextLineNumber().myString.equals("2"));
		pftest.currentLine.myProof = pftest.originalProof;
		
		Proof.SubProof pf2 = new Proof.SubProof(null, pftest.originalProof, pftest.currentLine);
		pftest.currentProof = pf2;
		pftest.currentReason = "show";
		pftest.currentLine.myProof = pf2;
		assertTrue(pftest.nextLineNumber().myString.equals("2.1"));
		pftest.currentLine.myProof = pf2;
		pftest.currentReason = "mt";
		assertTrue(pftest.nextLineNumber().myString.equals("2.2"));
		assertTrue(pftest.nextLineNumber().myString.equals("2.3"));
		
		
		pftest.isProvenGlobal.add(pf2);
		assertTrue(pftest.nextLineNumber().myString.equals("3"));
		pftest.currentProof = pftest.originalProof;
		pftest.currentLine.myProof = pftest.originalProof;
		
		assertTrue(pftest.nextLineNumber().myString.equals("4"));
		assertTrue(pftest.nextLineNumber().myString.equals("5"));
		
		Proof.SubProof pf3 = new Proof.SubProof(null, pftest.originalProof, pftest.currentLine);
		pftest.currentReason = "show";
		pftest.currentProof = pf3;
		pftest.currentLine.myProof = pf3;
		assertTrue(pftest.nextLineNumber().myString.equals("5.1"));
		pftest.currentReason = "mt";
		pftest.currentLine.myProof = pf3;
		assertTrue(pftest.nextLineNumber().myString.equals("5.2"));
		assertTrue(pftest.nextLineNumber().myString.equals("5.3"));
		
		Proof.SubProof pf3a = new Proof.SubProof(null, pf3, pftest.currentLine);
		pftest.currentReason = "show";
		pftest.currentProof = pf3a;
		pftest.currentLine.myProof = pf3a;
		assertTrue(pftest.nextLineNumber().myString.equals("5.3.1"));
		pftest.currentReason = "ic";
		pftest.currentLine.myProof = pf3a;
		assertTrue(pftest.nextLineNumber().myString.equals("5.3.2"));
		assertTrue(pftest.nextLineNumber().myString.equals("5.3.3"));
		
		pftest.isProvenGlobal.add(pf3a);
		assertTrue(pftest.nextLineNumber().myString.equals("5.4"));
		pftest.currentProof = pf3;
		pftest.currentLine.myProof = pf3;
		assertTrue(pftest.nextLineNumber().myString.equals("5.5"));
		
		
		pftest.isProvenGlobal.add(pf3);
		assertTrue(pftest.nextLineNumber().myString.equals("6"));
		pftest.currentProof = pftest.originalProof;
		pftest.currentLine.myProof = pftest.originalProof;
		assertTrue(pftest.nextLineNumber().myString.equals("7"));
		assertTrue(pftest.nextLineNumber().myString.equals("8"));
	
	
	}

}











