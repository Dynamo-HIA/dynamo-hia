package nl.rivm.emi.dynamo.ui.util;

import nl.rivm.emi.dynamo.global.BaseNode;

public class CompoundRiskFactorProperties extends CategoricalRiskFactorProperties{

	private Integer durationClassIndex;

	@SuppressWarnings("unused")
	private CompoundRiskFactorProperties() {
		super("bogus");
	}
	public CompoundRiskFactorProperties(String rootElementName) {
		super(rootElementName);
	}

	public CompoundRiskFactorProperties(String fileNameMainPart, BaseNode baseNode,
			String rootElementName, Integer numberOfCategories, Integer referenceClassIndex, Integer durationClassIndex) {
		super(fileNameMainPart, baseNode, rootElementName, numberOfCategories, referenceClassIndex);
		this.durationClassIndex = durationClassIndex;
	}

	public Integer getDurationClassIndex() {
		return durationClassIndex;
	}

	public void setDurationClassIndex(Integer durationClassIndex) {
		this.durationClassIndex = durationClassIndex;
	}
}
