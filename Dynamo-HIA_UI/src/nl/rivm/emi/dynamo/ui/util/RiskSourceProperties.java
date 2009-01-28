package nl.rivm.emi.dynamo.ui.util;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

public class RiskSourceProperties {

	private String fileNameMainPart;
	private BaseNode riskSourceNode;
	private String rootElementName;
	private Integer numberOfCategories;
	private String riskSourceLabel;
	private String riskSourceName;

	public RiskSourceProperties() {
		super();
	}

	public RiskSourceProperties(String fileNameMainPart, BaseNode baseNode,
			String rootElementName, Integer numberOfCategories) {
		super();
		this.fileNameMainPart = fileNameMainPart;
		this.riskSourceNode = baseNode;
		this.rootElementName = rootElementName;
		this.numberOfCategories = numberOfCategories;
	}

	public String getFileNameMainPart() {
		return fileNameMainPart;
	}

	public void setFileNameMainPart(String fileNameMainPart) {
		this.fileNameMainPart = fileNameMainPart;
	}

	public String getRootElementName() {
		return rootElementName;
	}

	public void setRootElementName(String rootElementName) {
		this.rootElementName = rootElementName;
	}

	public Integer getNumberOfCategories() {
		return numberOfCategories;
	}

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
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
