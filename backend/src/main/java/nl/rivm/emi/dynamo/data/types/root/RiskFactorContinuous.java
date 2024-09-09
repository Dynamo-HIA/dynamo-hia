package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class RiskFactorContinuous extends RootElementType {
	static final protected String XMLElementName = "riskfactor_continuous";
	public RiskFactorContinuous() {
		super(XMLElementName);
	}
}
