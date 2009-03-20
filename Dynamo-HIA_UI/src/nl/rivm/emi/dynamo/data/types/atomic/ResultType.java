package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

public class ResultType extends AbstractString implements PayloadType<String>{
	static final protected String XMLElementName = "resultType";

	public ResultType(){
		super(XMLElementName);
	}
}
