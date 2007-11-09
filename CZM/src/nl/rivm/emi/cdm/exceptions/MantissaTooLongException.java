package nl.rivm.emi.cdm.exceptions;

public class MantissaTooLongException extends Exception {
public MantissaTooLongException(int currentLength, int maxLength){
	super("The decimal part of a figure is " + currentLength + " figures long," 
			+ " the maximum allowed is " + maxLength);
}
}
