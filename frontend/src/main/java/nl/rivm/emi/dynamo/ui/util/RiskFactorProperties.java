package nl.rivm.emi.dynamo.ui.util;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.global.BaseNode;

/**
 * RiskFactorProperties Class as such cannot be instantiated, one of the three
 * subClasses should be used.
 * 
 * @author mondeelr
 * netto
 * 
 */
public abstract class RiskFactorProperties extends RiskSourceProperties {

	public static String[] possibleRiskFactorRootElementNames = {
			RootElementNamesEnum.RISKFACTOR_CATEGORICAL.getNodeLabel(),
			RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
			RootElementNamesEnum.RISKFACTOR_CONTINUOUS.getNodeLabel() };

	private String rootElementName;

	public RiskFactorProperties() {
		super();
	}

	public RiskFactorProperties(String fileNameMainPart, BaseNode baseNode,
			String rootElementName) {
		super(fileNameMainPart, baseNode);
		this.rootElementName = rootElementName;
	}

	public RiskFactorProperties(String rootElementName2) {
		this.rootElementName = rootElementName2;
	}

	public String getRootElementName() {
		return rootElementName;
	}

	public void setRootElementName(String rootElementName) {
		this.rootElementName = rootElementName;
	}
}
