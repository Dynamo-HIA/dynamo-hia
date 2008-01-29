package nl.rivm.emi.cdm_v0.exceptions;

public class WrongDataTypeException extends Exception {

public WrongDataTypeException(String dataTypeName){
	super("Container does not contain datatype " + dataTypeName);
}
}
