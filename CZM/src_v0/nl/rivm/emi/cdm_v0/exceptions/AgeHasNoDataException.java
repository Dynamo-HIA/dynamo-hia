package nl.rivm.emi.cdm_v0.exceptions;

public class AgeHasNoDataException extends Exception {
	public AgeHasNoDataException(int age) {
		super("The data for age " + age + " is not filled.");
	}
}