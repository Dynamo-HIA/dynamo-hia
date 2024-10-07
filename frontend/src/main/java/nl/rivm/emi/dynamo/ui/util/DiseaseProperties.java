package nl.rivm.emi.dynamo.ui.util;

import nl.rivm.emi.dynamo.global.BaseNode;

/**
 * A RiskSource is an entity that influences the risk for an individual to get a
 * Disease. It can be another Disease or a RiskFactor. The properties in this
 * Class are all that is collected for a Disease. For the various types of
 * RiskFactors derived Classes are provided that can contain the extra
 * information.
 * 
 * Death and Disability do not use this abstraction because their
 * risks are determined by the riskfactor only.
 * 
 * @author mondeelr
 * 
 */
public class DiseaseProperties extends RiskSourceProperties{

	public DiseaseProperties() {
		super();
	}

	public DiseaseProperties(String fileNameMainPart, BaseNode baseNode) {
		super(fileNameMainPart, baseNode);
	}

}
