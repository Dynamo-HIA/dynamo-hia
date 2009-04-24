package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class Cutoffs extends XMLTagEntity implements WrapperType {
	static final protected String XMLElementName = "cutoffs";

	public Cutoffs() {
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return (WrapperType) XMLTagEntityEnum.CUTOFF.getTheType();
	}
}
