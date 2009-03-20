package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;
public class Scenarios extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "scenarios";

	public Scenarios() {
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return (WrapperType)XMLTagEntityEnum.SCENARIO.getTheType();
	}
}
