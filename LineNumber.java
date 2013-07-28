public class LineNumber {
	
	Proof.SubProof myProof;
	Integer step;
	boolean isProven;
	Expression myExpression;
	String myString;
	
	public LineNumber(int i) {
		myString = "";
	}
	
	public LineNumber(LineNumber previous) {
		myProof = previous.myProof;
		step = previous.step + 1;
		isProven = false;
		myNumber(step, previous);
	}
	
	public void myNumber(int stepNumber, LineNumber prev) {
		String line;		
		if (!(myProof.hasParent())) {
			line = step.toString();
		} else {
			int length = prev.myString.length();
			if (stepNumber - 1 < 10){
				line = prev.myString.substring(0, length - 2);
			} else if (stepNumber - 1 < 100) {
				line = prev.myString.substring(0, length - 3);
			} else {
				line = prev.myString.substring(0, length - 4);
			}
			line += stepNumber;
		}
		myString = line;
	}
	
	public String toString() {
		return this.myString;
	}
}





