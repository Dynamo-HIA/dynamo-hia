package nl.rivm.emi.dynamo.ui.util;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

public class CategoricalRiskFactorProperties extends RiskFactorProperties{

	private Integer numberOfCategories;
	private Integer referenceClassIndex;

	@SuppressWarnings("unused")
	private CategoricalRiskFactorProperties() {
		super();
	}

	public CategoricalRiskFactorProperties(String rootElementName) {
		super(rootElementName);
	}

	public CategoricalRiskFactorProperties(String fileNameMainPart, BaseNode baseNode,
			String rootElementName, Integer numberOfCategories, Integer referenceClassIndex) {
		super(fileNameMainPart, baseNode, rootElementName);
		this.numberOfCategories = numberOfCategories;
		this.referenceClassIndex = referenceClassIndex;
	}

	public Integer getReferenceClassIndex() {
		return referenceClassIndex;
	}

	public void setReferenceClassIndex(Integer referenceClassIndex) {
		this.referenceClassIndex = referenceClassIndex;
	}

	public Integer getNumberOfCategories() {
		return numberOfCategories;
	}

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

}
