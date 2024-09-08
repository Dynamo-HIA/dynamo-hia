package nl.rivm.emi.dynamo.ui.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.global.BaseNode;

public class CategoricalRiskFactorProperties extends RiskFactorProperties {
	Log log = LogFactory.getLog(this.getClass().getName());
	private Integer numberOfCategories;
	private Integer referenceClassIndex;

	@SuppressWarnings("unused")
	private CategoricalRiskFactorProperties() {
		super();
	}

	public CategoricalRiskFactorProperties(String rootElementName) {
		super(rootElementName);
	}

	public CategoricalRiskFactorProperties(String fileNameMainPart,
			BaseNode baseNode, String rootElementName,
			Integer numberOfCategories, Integer referenceClassIndex) {
		super(fileNameMainPart, baseNode, rootElementName);
		log.debug("<init> fileNameMainPart: " + fileNameMainPart);
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
