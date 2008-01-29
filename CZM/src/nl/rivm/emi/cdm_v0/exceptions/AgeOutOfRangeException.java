package nl.rivm.emi.cdm_v0.exceptions;

public class AgeOutOfRangeException extends Exception {

	public AgeOutOfRangeException(int submittedAge, int startAge, int endAge){
	super("Age " + submittedAge + " does not fit the container range ( "
			+ startAge + " to " + endAge);
	}

}
