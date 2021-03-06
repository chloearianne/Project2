import java.util.*;

/*
 calls to mp, mt, repeat, ect. must take in strings and use try catches to see if they're valid
 must also extend the proof inside mp/mt, because if an exception isn't thrown then the reason is true
 */

public class Proof {
	private TheoremSet theorems;
	private SubProof originalProof;
	private SubProof currentProof;
	private Hashtable<String, LineNumber> isTrueGlobal;
	private LineNumber currentLineNum;
	private LineNumber prevLineNum;

	// Takes in a theoremset of theorems
	// Constructs a proof by initializing all the variables to a null value
	public Proof(TheoremSet theorems) {
		originalProof = null;
		currentProof = originalProof;
		isTrueGlobal = new Hashtable<String, LineNumber>();
		this.theorems = theorems;
		prevLineNum = null;
		currentLineNum = null;
	}


	public LineNumber getCurrentLineNumber() {
		return currentLineNum;
	}
	
	public LineNumber getPreviousLineNumber() {
		return prevLineNum;
	}
	
	public void setPreviousLineNumber(LineNumber line) {
		prevLineNum = line;
	}
	
	public void setCurrentLineNumber(LineNumber line) {
		currentLineNum = line;
	}
	
	public SubProof getCurrentProof() {
		return currentProof;
	}
	
	public void setCurrentProof(SubProof pf) {
		currentProof = pf;
	}
	
	public Hashtable<String, LineNumber> getIsTrueGlobal() {
		return isTrueGlobal;
	}
	
	public SubProof getOriginalProof() {
		return originalProof;
	}
	
	public void setOriginalProof(SubProof pf) {
		originalProof = pf;
	}
	
	public TheoremSet getTheoremSet() {
		return theorems;
	}
	
	public void setTheoremSet(TheoremSet thms) {
		theorems = thms;
	}
	
	
	// set up the next line number for nextLineNumber to return at the beginning
	// of the
	// next step's call.
	public LineNumber nextLineNumber() {
		// if it's print, do nothing
		if (prevLineNum != null && prevLineNum.getReason().equals("print")) {
			return currentLineNum;
		}
		// initializes the proof checker
		if (originalProof == null) {
			currentLineNum = new LineNumber();
			return currentLineNum;
		}
		
		String curLine = currentLineNum.getNum(); // string representation of
													// the line
		String newLine; // the new lineNumber string
		if (currentProof.equals(originalProof)) {
			newLine = "" + (Integer.parseInt(curLine) + 1);
		} else if (currentLineNum.getReason().equals("show")) {
			newLine = currentLineNum + ".1";
		} else if (currentProof.myLineNumber.IsProven()) {
			int index = curLine.lastIndexOf('.');
			newLine = curLine.substring(0, index);
			int index2 = newLine.lastIndexOf('.');
			if (index2 > 0) {
				int step = Integer.parseInt(newLine.substring(index2 + 1, newLine.length()));
				step++;
				newLine = newLine.substring(0, index2 + 1) + step;
			} else {
				int step = Integer.parseInt(newLine);
				newLine = (step + 1) + "";
			}
			
		} else {
			int index = curLine.lastIndexOf('.');
			int newInt = Integer.parseInt(curLine.substring(index + 1)) + 1;
			newLine = curLine.substring(0, index) + "." + newInt;
		}

		prevLineNum = currentLineNum; // set prevLineNum before currentLineNum
										// is changed
		currentLineNum = new LineNumber(newLine);
		return currentLineNum;
	}

	/*
	 * precondition: x is the user's input, currentLineNum is the line that the
	 * user typed x on creates a new scanner that parses through and separates
	 * the words based on whitespace - checks to see how many words there are, a
	 * legal number of words is from 1 to 4 - checks to see which reason the
	 * user is calling, and runs the corresponding helper method to determine if
	 * the reason is being used correctly or not and if the subsequent arguments
	 * are valid. postcondition: The program throws an error while trying to
	 * parse the user's input, indicating invalid input or invalid inference,
	 * and this error is passed on - The program runs without throwing an error,
	 * and extendProof checks if the subProof has been proven. If it has,
	 * isTrueGlobal is updated with the new proven subProof
	 */
	public void extendProof(String x) throws IllegalLineException,
			IllegalInferenceException {
		if (x == null) {
			throw new IllegalLineException("Cannot input a null value");
		}
		x = x.toLowerCase();
		Scanner expScanner = new Scanner(x);
		if (!expScanner.hasNext()) {
			throw new IllegalLineException("Cannot input an empty string");
		}

		ArrayList<String> myWords = new ArrayList<String>();
		while (expScanner.hasNext()) { // separate the arguments of the string
			myWords.add(expScanner.next());
		}

		// general try catch statement to handle exceptions when calling outside
		// helper methods
		// ie. if calls to assume, repeat, mt, mc, ect. throw an exception
		try {
			String reason = myWords.get(0);
			if (myWords.size() == 1) { // only print statements can stand alone
				if (reason.equals("print")) {
					System.out.println(this);
					return;
				} else {
					throw new IllegalLineException(
							"A reason should be followed by at least an expression");
				}
			} else if (originalProof == null && !reason.equals("show")) {
				throw new IllegalLineException(
						"first line must begin with a show statement");
			} else if (myWords.size() == 2) { // must be a length 2 to be a
												// valid use of show, assume, or
												// theorem
				if (reason.equals("show")) { // ie. only one expression follows
												// the reason
					currentLineNum.setProof(currentProof);
					show(myWords.get(1));
				} else if (reason.equals("assume")) { // handles assume
					if (!prevLineNum.getReason().equals("show")) {
						throw new IllegalLineException(
								"An assume can only follow a show");
					} else {
						assume(myWords.get(1));
					}
				} else if (theorems.hasTheorem(reason)) { // handles theorem use
					theorem(reason, myWords.get(1));
				} else {
					throw new IllegalLineException(
							reason
									+ " is not a valid theorem or has an incorrect number of arguments");
				}
			} else if (myWords.size() == 3) {
				if (reason.equals("repeat")) { // handles repeat, if repeat
												// errors, pass it on
					repeat(myWords.get(1), myWords.get(2));
				} else if (reason.equals("ic")) { // handles ic
					ic(myWords.get(1), myWords.get(2));
				} else {
					throw new IllegalLineException(
							"Only repeat and ic take in 2 arguments");
				}
			} else if (myWords.size() == 4) { // handles mp, mt, co
				if (reason.equals("mp")) {
					mp(myWords.get(1), myWords.get(2), myWords.get(3));
				} else if (reason.equals("mt")) {
					mt(myWords.get(1), myWords.get(2), myWords.get(3));
				} else if (reason.equals("co")) {
					co(myWords.get(1), myWords.get(2), myWords.get(3));
				} else {
					throw new IllegalLineException(reason
							+ " does not take in 3 arguments");
				}
			} else {
				throw new IllegalLineException(
						"There can never be more than 3 arguments for a reason");
			}
			// if it hasn't thrown an exception, then the reason has been
			// validly used
			System.out.println("updating current line with " + reason);
			currentProof.everythingSoFar.add(currentLineNum);
			currentLineNum.setProof(currentProof);
			currentLineNum.setReason(reason);
			currentLineNum.setString(x);
			currentLineNum.setIsProven(true);

			if (currentProof.isProven()) {
				currentLineNum.setExp(currentProof.myExpression);
				isTrueGlobal.put(currentLineNum.getNum(), currentLineNum);
			}
		} catch (IllegalLineException e) {
			throw e;
		} catch (IllegalInferenceException e) {
			throw e;
		}
	}

	/*
	 * Precondition: Takes in a string exp that should represent the expression
	 * the user is trying to show - checks to see if exp is a valid expression -
	 * sets the original proof if it hasn't been initialized yet - creates child
	 * proofs Postcondition: the currentProof is now the new show statement
	 */
	private void show(String exp) throws IllegalInferenceException,
			IllegalLineException {
		Expression expression = null;
		try {
			expression = new Expression(exp);
			currentLineNum.setExp(expression);
		} catch (IllegalLineException e) {
			throw e;
		}

		if (originalProof == null) {
			originalProof = new SubProof(expression, null, currentLineNum);
			currentProof = originalProof;
		} else {
			SubProof child = new SubProof(expression, currentProof,
					currentLineNum);
			currentProof.addChildProof(child);
			currentProof = child;
		}
	}

	/*
	 * Precondition:
	 */
	private void theorem(String thm, String exp)
			throws IllegalInferenceException, IllegalLineException {
		Expression expression = null;
		try {
			expression = new Expression(exp);
			currentLineNum.setExp(expression);
		} catch (IllegalLineException e) {
			throw e;
		}
		if (theorems.validTheorem(thm, expression)) {
			currentLineNum.setExp(expression);
			currentProof.assumptions.put(currentLineNum.getNum(),
					currentLineNum);
		} else {
			throw new IllegalInferenceException(
					"Improper use of a theorem, variables used incorrectly");
		}
	}

	private void repeat(String line, String exp)
			throws IllegalInferenceException, IllegalLineException {
		Expression expression = null;
		try {
			expression = new Expression(exp);
			currentLineNum.setExp(expression);
		} catch (IllegalLineException e) {
			throw e;
		}
		if (isTrueGlobal.contains(line)) {
			if (isTrueGlobal.get(line).getExp().equals(expression)) {
				currentLineNum.setExp(expression);
				currentProof.assumptions.put(currentLineNum.getNum(),
						currentLineNum);
			} else {
				throw new IllegalLineException(
						"Cannot use repeat on the given expression");
			}
		}

		LineNumber lineNum = currentProof.assumptions.get(line);
		if (line == null) {
			throw new IllegalLineException(lineNum
					+ " is not a valid line number");
		}

		if (lineNum.getExp().equals(expression)) {
			currentLineNum.setExp(expression);
			currentProof.assumptions.put(currentLineNum.getNum(),
					currentLineNum);
		} else {
			throw new IllegalLineException(
					"Cannot use repeat on the given expression");
		}
		throw new IllegalInferenceException("cannot access " + line
				+ " in call to repeat");
	}

	public Expression.ExprNode switchSign(Expression.ExprNode exp) {
		Expression expression = null;
		if (exp.getMyItem().equals("~")) {
			try {
				expression = new Expression(exp.getMyLeft().toString());
			} catch (IllegalLineException e) {
				System.out.println("should not have errored");
			}
		} else {
			try {
				expression = new Expression("~" + exp.toString());
			} catch (IllegalLineException e) {
				System.out.println("should not have errored");
			}
		}

		System.out.println(expression);
		return expression.myRoot;
	}

	private void assume(String exp) throws IllegalInferenceException,
			IllegalLineException {
		Expression rtn = null;
		try {
			rtn = new Expression(exp); // tries to create a new expession out of
										// the given string
			currentLineNum.setExp(rtn);
		} catch (IllegalLineException e) {
			throw e; // tests if the string is correct
		}

		if (prevLineNum.getExp().toString().length() == 1) {

		}

		if (switchSign(rtn.myRoot).equals(prevLineNum.getExp().myRoot)) {
			currentLineNum.setExp(rtn);
			currentProof.assumptions.put(currentLineNum.getNum(),
					currentLineNum);
		} else if (prevLineNum.getExp().myRoot.getMyLeft() != null) {
			if (rtn.toString().equals(
					prevLineNum.getExp().myRoot.getMyLeft().toString())) {
				currentLineNum.setExp(rtn);
				currentProof.assumptions.put(currentLineNum.getNum(),
						currentLineNum);
			}
		} else {
			throw new IllegalInferenceException("bad inference");
		}
	}

	private void mp(String line1, String line2, String rtnString)
			throws IllegalInferenceException, IllegalLineException {
		Expression rtn = null;
		try {
			rtn = new Expression(rtnString);
			currentLineNum.setExp(rtn);
		} catch (IllegalLineException e) {
			throw e;
		}
		LineNumber lineNum1 = currentProof.assumptions.get(line1);
		LineNumber lineNum2 = isTrueGlobal.get(line2);
		if (lineNum1 == null) {
			throw new IllegalLineException(lineNum1
					+ " is not a valid line number");
		} else if (lineNum2 == null) {
			throw new IllegalLineException(lineNum2
					+ " is not a valid line number");
		}

		Expression line1Exp = lineNum1.getExp();
		Expression line2Exp = lineNum2.getExp();
		// checks if the two LineNumbers are in the right order, if not,
		// switches them.
		if (line1Exp.toString().length() > line2Exp.toString().length()) {
			Expression temp = line1Exp;
			line1Exp = line2Exp;
			line2Exp = temp;
		}
		if (line2Exp.myRoot.getMyLeft().equals(lineNum1.getExp())) {
			if (line2Exp.myRoot.getMyRight().equals(rtn)) {
				currentLineNum.setExp(rtn);
				currentProof.assumptions.put(currentLineNum.getNum(),
						currentLineNum);
			} else {
				throw new IllegalInferenceException("bad inference");
			}
		}

		else {
			throw new IllegalInferenceException("bad inference");
		}
	}

	private void mt(String line1, String line2, String rtnString)
			throws IllegalInferenceException, IllegalLineException {
		Expression rtn = null;
		// checks for valid expression
		try {
			rtn = new Expression(rtnString);
			currentLineNum.setExp(rtn);
		} catch (IllegalLineException e) {
			throw e;
		}
		// checks for valid lineNumbers
		LineNumber lineNum1 = currentProof.assumptions.get(line1);
		LineNumber lineNum2 = isTrueGlobal.get(line2);
		if (lineNum1 == null) {
			throw new IllegalInferenceException(lineNum1
					+ " is not a valid line number");
		} else if (lineNum2 == null) {
			throw new IllegalInferenceException(lineNum2
					+ " is not a valid line number");
		}

		Expression line1Exp = lineNum1.getExp();
		Expression line2Exp = lineNum2.getExp();

		// checks if the two LineNumbers are in the right order, if not,
		// switches them.
		if (line1Exp.toString().length() > line2Exp.toString().length()) {
			Expression temp = line1Exp;
			line1Exp = line2Exp;
			line2Exp = temp;
		}
		if (line2Exp.myRoot.getMyRight().equals(switchSign(line1Exp.myRoot))) {
			if (line2Exp.myRoot.getMyLeft().equals(switchSign(rtn.myRoot))) {
				currentLineNum.setExp(rtn);
				currentProof.assumptions.put(currentLineNum.getNum(),
						currentLineNum);
			} else {
				throw new IllegalInferenceException("bad inference");
			}
		} else {
			throw new IllegalInferenceException("bad inference");
		}
	}

	private void co(String line1, String line2, String exp)
			throws IllegalInferenceException, IllegalLineException {
		Expression rtn = null;
		// checks for valid expression
		try {
			rtn = new Expression(exp);
			currentLineNum.setExp(rtn);
		} catch (IllegalLineException e) {
			throw e;
		}
		// checks for valid lineNumbers
		System.out.println(line1);
		System.out.println(currentProof.assumptions);
		LineNumber lineNum1 = currentProof.assumptions.get(line1);
		LineNumber lineNum2 = isTrueGlobal.get(line2);
		if (lineNum1 == null) {
			throw new IllegalInferenceException(lineNum1
					+ " is not a valid line number");
		} else if (lineNum2 == null) {
			throw new IllegalInferenceException(lineNum2
					+ " is not a valid line number");
		}

		Expression line1Exp = lineNum1.getExp();
		Expression line2Exp = lineNum2.getExp();

		// first this needs to check if line1 and line2 are proven, can only do
		// that with LINE NUMBER!
		if (switchSign(line1Exp.myRoot).equals(line2Exp)) { // ~p, p=>q, q
			currentLineNum.setExp(rtn);
			currentProof.assumptions.put(currentLineNum.getNum(),
					currentLineNum);
		} else {
			throw new IllegalInferenceException("bad inference");
		}
	}

	private void ic(String line1, String exp) throws IllegalInferenceException,
			IllegalLineException {
		Expression rtn = null;
		// checks for valid expression
		try {
			rtn = new Expression(exp);
			currentLineNum.setExp(rtn);
		} catch (IllegalLineException e) {
			throw e;
		}
		// checks for valid lineNumbers
		LineNumber lineNum1 = currentProof.assumptions.get(line1);
		if (lineNum1 == null) {
			throw new IllegalInferenceException(lineNum1
					+ " is not a valid line number");
		}
		Expression line1Exp = lineNum1.getExp();

		// first this needs to check if line1 is proven, can only do that with
		// LINE NUMBER!
		if (line1Exp.myRoot.equals(rtn.myRoot.getMyRight())) {
			currentLineNum.setExp(rtn);
			currentProof.assumptions.put(currentLineNum.getNum(),
					currentLineNum);
			System.out.println("Result: " + rtn.toString() + " is true!");
		} else {
			throw new IllegalInferenceException("bad inference");
		}
		// }
		// else {
		// throw new IllegalInferenceException("bad inference");
		// }
	}

	public String toString() {
		if (!(originalProof == null)) {
			return toStringHelper(originalProof);
		}
		return "";
	}

	private String toStringHelper(SubProof pf) {
		String toReturn = "";
		toReturn = toReturn + "/n" + pf.myLineNumber + " " + pf.reason + " "
				+ pf.myExpression.toString();
		Iterator<SubProof> iter = pf.childProofs.iterator();
		while (iter.hasNext()) {
			toStringHelper(iter.next());
		}
		return toReturn;
	}

	public boolean isComplete ( ) {
		if (getOriginalProof() == null) {
			return false;
		}
		String firstString = getOriginalProof().getMyExpression().toString();
		String lastString = getCurrentProof().getMyExpression().toString();
		return lastString.equals(firstString) && getCurrentProof().getLineNumber().IsProven();
	}


	// SubProof is a child class much like Amoeba was a child class of
	// AmoebaFamily. Each proof object has a original proof
	// that is analogous to myRoot (stored in myExpression), and an ArrayList of
	// childProofs that is analogous to myChildren.
	// Each proof also contains its own line number, its original reason, a
	// reference to its parent proof, and a hashtable
	// containing all of the assumptions for the proof (lines/expressions that,
	// for this proof, are essentially proven/true).
	public static class SubProof {

		private Expression myExpression;
		private SubProof parentProof;
		private ArrayList<SubProof> childProofs;
		private LineNumber myLineNumber;
		private String reason;
		private ArrayList<LineNumber> everythingSoFar;

		// proof.assumptions should hold all values true for that proof, ie.
		// includes parent's assumptions

		private Hashtable<String, LineNumber> assumptions;

		public SubProof(Expression expr, SubProof parent, LineNumber line) {
			parentProof = parent;
			childProofs = new ArrayList<SubProof>();
			myExpression = expr;
			myLineNumber = line;
			assumptions = new Hashtable<String, LineNumber>();
			everythingSoFar = new ArrayList<LineNumber>();
		}

		public boolean hasParent() {
			return !(this.parentProof == null);
		}
		
		public Expression getMyExpression() {
			return myExpression;
		}
		
		public void setMyExpression(Expression e) {
			myExpression = e;
		}
		
		public SubProof getParent() {
			return parentProof;
		}
		
		public void setParent(SubProof pf) {
			parentProof = pf;
		}
		
		public ArrayList<SubProof> getChildProof() {
			return getChildProofs();
		}
		
		public Hashtable<String, LineNumber> getAssumptions() {
			return assumptions;
		}
		
		public ArrayList<LineNumber> getEverythingSoFar () {
			return everythingSoFar;
		}

		public void addChildProof(SubProof child) {
			getChildProofs().add(child);
		}

		public LineNumber getLineNumber() {
			return myLineNumber;
		}
		
		public void setLineNumber(LineNumber line) {
			myLineNumber = line;
		}

		public boolean isProven() {
			if (getChildProofs().size() > 0) {
				return myExpression
						.equals(getChildProofs().get(getChildProofs().size() - 1).myExpression);
			} else {
				return false;
			}
		}

		public ArrayList<SubProof> getChildProofs() {
			return childProofs;
		}

		public void setChildProofs(ArrayList<SubProof> childProofs) {
			this.childProofs = childProofs;
		}
	}
}
