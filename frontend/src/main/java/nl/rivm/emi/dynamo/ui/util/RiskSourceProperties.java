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
public class RiskSourceProperties {

	private String fileNameMainPart;
	private BaseNode riskSourceNode;
	private String riskSourceLabel;
	private String riskSourceName;

	public RiskSourceProperties() {
		super();
	}

	public RiskSourceProperties(String fileNameMainPart, BaseNode baseNode) {
		super();
		this.fileNameMainPart = fileNameMainPart;
		this.riskSourceNode = baseNode;
	}

	public String getFileNameMainPart() {
		return fileNameMainPart;
	}

	public void setFileNameMainPart(String fileNameMainPart) {
		this.fileNameMainPart = fileNameMainPart;
	}

	public BaseNode getRiskSourceNode() {
		return riskSourceNode;
	}

	public void setRiskSourceNode(BaseNode riskSourceNode) {
		this.riskSourceNode = riskSourceNode;
	}

	public String getRiskSourceLabel() {
		return riskSourceLabel;
	}

	public void setRiskSourceLabel(String riskSourceLabel) {
		this.riskSourceLabel = riskSourceLabel;
	}

	public String getRiskSourceName() {
		return riskSourceName;
	}

	public void setRiskSourceName(String riskSourceName) {
		this.riskSourceName = riskSourceName;
	}
}
