import java.util.*;

public class Proof { 
	
	SubProof originalProof = null;
	SubProof currentProof;
	
	LineNumber currentLine;
	LineNumber previousLine;
	
	String previousReason;
	
	ArrayList<SubProof> isProvenGlobal;
	
	TheoremSet myTheorems;

	// Construct a proof by setting up the original (root) proof.
	public Proof (TheoremSet theorems) {
		originalProof = new SubProof(null, null, new LineNumber(0));
		currentProof = originalProof;
		previousReason = "";
		previousLine = new LineNumber(0);
		currentLine = new LineNumber(0);
		currentLine.step = 0;
		isProvenGlobal = new ArrayList<SubProof>();
		myTheorems = theorems;
	}

	public LineNumber nextLineNumber ( ) {
		return currentLine;
	}
	
	public void extendProof (String x) throws IllegalLineException, IllegalInferenceException {
		//at very beginning, set x to be the expression associated with the current line number
		currentLine.myExpression = new Expression(x);
		
		// elena's code
		
		//at end, after everything else, check if we just finished a proof, and if we did, add it to the 
		// globally proven arraylist
		if (currentProof.isProven()) {
			isProvenGlobal.add(currentProof);
		}
		
		// at very end, set up the next line number for nextLineNumber to return at the beginning of the 
		// next step's call.
		LineNumber nextLine;
		
		// if we're starting a new subproof, create a copy of the current line, add a ".0" for a placeholder
		// and call a new line number using that
		if (reason.equals("show")) {
			LineNumber tempLine = currentLine;
			tempLine.myString += ".0";
			nextLine = new LineNumber(tempLine);
		// if we're about to exit a subproof (the current proof just finished), then create a copy of the 
		// current line, subtract the last number and its point, and call a new line number using that
		} else if (isProvenGlobal.contains(currentProof)){
			LineNumber tempLine = currentLine;
			int tempLength = tempLine.myString.length();
			if (tempLine.step >= 10) {
				tempLine.myString = tempLine.myString.substring(0, tempLength - 3);
			} else if (tempLine.step >= 100) {
				tempLine.myString = tempLine.myString.substring(0, tempLength - 4);
			} else {
				tempLine.myString = tempLine.myString.substring(0, tempLength - 2);
			}
			nextLine = new LineNumber(tempLine);
		// if we're simply continuing a proof, then call a new line number on the current line.
		} else {
			nextLine = new LineNumber(currentLine);
		}
		// set the current line to be proven locally, set previousLine to this one, and set currentLine
		// to the nextLine we just created.
		currentLine.isProven = true;
		previousLine = currentLine;
		currentLine = nextLine;
	}

	public String toString ( ) {
		if (!(originalProof == null)) {
			return toStringHelper(originalProof);
		}
		return "";
	}	
	
	public String toStringHelper(SubProof pf) {
		String toReturn = "";
		toReturn = toReturn + "/n" + pf.myLineNumber.myString + " " + pf.reason + " " + pf.myExpression.toString();
		Iterator<SubProof> iter = pf.childProofs.iterator();
		while (iter.hasNext()) {
			toStringHelper(iter.next());
		}
		return toReturn;
	}
	
	
	public boolean isComplete ( ) {
		int sizeOfChildren = originalProof.childProofs.size();
		// If the last step of the proof has children, then that subproof is clearly not done and it's impossible
		// for the entire proof to be done.
		if (!(originalProof.childProofs.get(sizeOfChildren - 1).childProofs == null)) {
			return false;
		}
		// Convert originalProof expression (saved in firstExpr) to a string, and convert the most recent proof step 
		// to a string and save it in lastExpr. Compare these two to see if they're the same. If they are, the proof is done.
		SubProof toProve = originalProof.childProofs.get(0);
		Expression firstExpr = toProve.myExpression;
		String firstString = firstExpr.toString();
		
		SubProof lastProof = originalProof.childProofs.get(sizeOfChildren - 1);
		Expression lastExpr = lastProof.myExpression;
		String lastString = lastExpr.toString();
		return lastString.equals(firstString) && lastProof.myLineNumber.isProven;
		
	}
	
	// SubProof is a child class much like Amoeba was a child class of AmoebaFamily. Each proof object has a original proof
	// that is analogous to myRoot (stored in myExpression), and an ArrayList of childProofs that is analogous to myChildren.
	// Each proof also contains its own line number, its original reason, a reference to its parent proof, and a hashtable
	// containing all of the assumptions for the proof (lines/expressions that, for this proof, are essentially proven/true).
	public static class SubProof {
		
		Expression myExpression;
		SubProof parentProof;
		ArrayList<SubProof> childProofs;
		LineNumber myLineNumber;
		String reason;
		Hashtable<LineNumber, Expression> assumptions;
		
		public SubProof(Expression expr, SubProof parent, LineNumber line) {
			parentProof = parent;
			childProofs = new ArrayList<SubProof>();
			myExpression = expr;
			myLineNumber = line;
			assumptions = new Hashtable<LineNumber, Expression>();
		}
		
		public boolean hasParent () {
			return !(this.parentProof == null);
		}
		
		public void addChildProof(SubProof child) {
			childProofs.add(child);
		}
		
		public LineNumber lineNumber () {
			return myLineNumber;
		}
		
		public boolean isProven() {
			return myExpression.equals(childProofs.get(childProofs.size()).myExpression);
		}
		
		
	}
}
	