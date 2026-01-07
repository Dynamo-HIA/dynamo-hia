package nl.rivm.emi.dynamo.ui.util;

import nl.rivm.emi.dynamo.global.BaseNode;

public class ContinuousRiskFactorProperties extends RiskFactorProperties{

	private Float[] cutoffValues;
	private Float referenceValue;

	@SuppressWarnings("unused")
	private ContinuousRiskFactorProperties() {
		super();
	}

	public ContinuousRiskFactorProperties(String rootElementName) {
		super(rootElementName);
	}

	public ContinuousRiskFactorProperties(String fileNameMainPart, BaseNode baseNode,
			String rootElementName, Float[] cutoffValues,Float referenceValue) {
		super(fileNameMainPart, baseNode, rootElementName);
		this.cutoffValues = cutoffValues;
		this.referenceValue = referenceValue;
	}

	public Float[] getCutoffValues() {
		return cutoffValues;
	}

	public void setCutoffValues(Float[] cutoffValues) {
		this.cutoffValues = cutoffValues;
	}

	public Float getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(Float referenceValue) {
		this.referenceValue = referenceValue;
	}
}
