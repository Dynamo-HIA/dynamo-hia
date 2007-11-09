package nl.rivm.emi.cdm.exceptions;

public class WrongDataTypeException extends Exception {

public WrongDataTypeException(String dataTypeName){
	super("Container does not contain datatype " + dataTypeName);
}
}
