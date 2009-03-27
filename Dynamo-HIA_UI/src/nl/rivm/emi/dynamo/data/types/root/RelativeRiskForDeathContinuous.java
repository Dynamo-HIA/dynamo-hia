package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class RelativeRiskForDeathContinuous extends RootElementType {
	static final protected String XMLElementName = "relrisksfordeath_continuous";
	public RelativeRiskForDeathContinuous() {
		super(XMLElementName);
	}
}
