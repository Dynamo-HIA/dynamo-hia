package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class RelativeRisksCategorical extends RootElementType{
	static final protected String XMLElementName = "relativerisks_categorical";

	public RelativeRisksCategorical(){
		super(XMLElementName);
	}
}
