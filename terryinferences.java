//EQUALS IS A PLACEHOLDER METHOD! REPLACE WITH SOMETHING TO TRAVERSE THE TREES TO PATTERN MATCH  
		//Use a tree to do the pattern checking
		//ex: line2.subtree matches with line1.root


public class terryinferences {

	
	public static String switchSign(String s) {
		if (s.contains("~")) {
			return s.substring(1, s.length() - 1);
		} else {
			return "~" + s;
		}
	}
	
	public void mp(LineNumber line1, LineNumber line2, LineNumber rtn) throws IllegalInferenceException {
		if (line2.valueLeft.equals(line1.value)) {    //p == p
			if (line2.valueRight.equals(rtn.value)) {  //q == q
				//add rtn to arraylist of proven LineNumbers
				System.out.println("It is true!");
			}
		}
		
		else {
			throw new IllegalInferenceException("bad inference");
		}
	}
	
	public void mt(LineNumber line1, LineNumber line2, LineNumber rtn) throws IllegalInferenceException {
		if (line2.valueRight.equals(switchSign(line1.value))) {	//p == p
			if (line2.valueLeft.equals(switchSign(rtn.value))) {  //q == q
				//add rtn to arraylist of proven LineNumbers
			}
		}
		else {
			throw new IllegalInferenceException("bad inference");
		}
	}
	
	public void co(LineNumber line1, LineNumber line2, LineNumber rtn) throws IllegalInferenceException {
		if (line1.isProven && line2.isProven && line1.value.equals(switchSign(line2.value))) {
			if (rtn.equals(line1) || rtn.equals(line2.valueLeft)) {
				//add rtn to arraylist of proven LineNumbers
			}
		}
		else {
			throw new IllegalInferenceException("bad inference");
		}
	}
	
	public void ic(LineNumber line1, LineNumber rtn) throws IllegalInferenceException {
		if (line1.isProven) {
			if (rtn.valueLeft.equals(line1)) {
				//add rtn to arraylist of proven LineNumbers
			}
		}
		else {
			throw new IllegalInferenceException("bad inference");
		}
	}
	
	public static void main(String[] args) throws IllegalInferenceException {
		terrytester terry = new terrytester();
		LineNumber a1 = new LineNumber();
		LineNumber a2 = new LineNumber();
		a1.value = "p";
		a2.value = "p=>q";
		a2.valueLeft = "p";
		a2.valueRight = "q";
		LineNumber a3 = new LineNumber("2");
		
		terry.mp(a1, a2, a3);
	}
}
t
