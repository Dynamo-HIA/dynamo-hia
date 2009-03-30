package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;;

public class UnitType extends AbstractString implements PayloadType<String>{

	static final protected String XMLElementName = "unittype";

	public UnitType(){
		super(XMLElementName);
	}
}
