package nl.rivm.emi.cdm_v0.exceptions;

public class MantissaTooLongException extends Exception {
public MantissaTooLongException(int currentLength, int maxLength){
	super("The decimal part of a figure is " + currentLength + " figures long," 
			+ " the maximum allowed is " + maxLength);
}
}
