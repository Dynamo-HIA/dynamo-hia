package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class RiskFactorCompound extends RootElementType {
	static final protected String XMLElementName = "riskfactor_compound";
	public RiskFactorCompound() {
		super(XMLElementName);
	}
}
