package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;
public class RiskFactors extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "riskfactors";

	public RiskFactors() {
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return (WrapperType)XMLTagEntityEnum.RISKFACTOR.getTheType();
	}
}
