public class LineNumber {
	
	Proof.SubProof myProof;
	Integer step;
	boolean isProven;
	Expression myExpression;
	String myString;
	
	public LineNumber() {
		step = 0;
	}
	
	public LineNumber(LineNumber previous, boolean startNewSubProof) {
		myProof = new Proof.SubProof(null, null, null);
		if (startNewSubProof) {
			step = 1;
		} else {
			myProof = previous.myProof;
			step = previous.step + 1;
		}
		isProven = false;
		myNumber(previous);
	}
	
	public void myNumber(LineNumber prev) {
		String line;
		if (!prev.myProof.hasParent()) {
			line = step + "";
		} else {
			int length = prev.myString.length();
			if (step - 1 < 10){
				line = prev.myString.substring(0, length - 1);
			} else if (step - 1 < 100) {
				line = prev.myString.substring(0, length - 2);
			} else {
				line = prev.myString.substring(0, length - 3);
			}
			line += step;
		}
		myString = line;
	}
	
	public String toString() {
		return this.myString;
	}
}





