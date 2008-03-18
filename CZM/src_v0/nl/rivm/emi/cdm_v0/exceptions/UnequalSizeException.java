package nl.rivm.emi.cdm_v0.exceptions;

public class UnequalSizeException extends Exception {
public UnequalSizeException(String firstContainerName, int fcSize, String scName, int scSize){
	super("The compared containers have different sizes. The first container \"" 
			+ firstContainerName + "\" has size "	+ fcSize 
			+ ". The second container, \"" + scName + "\" has size " + scSize);
}
}
