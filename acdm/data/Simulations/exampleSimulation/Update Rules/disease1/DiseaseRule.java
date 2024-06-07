package nl.rivm.emi.cdm.rules.update;

import java.util.ArrayList;
import java.util.HashMap;

public class DiseaseRule {

	private HashMap<String, ArrayList<ParameterInfo>> parameters;

	DiseaseRule() {
		super();
	}

	int userupdaterule(double[] oldValues) {

		/* those who are diseased stay dead */
        if (oldValues[this.getCharacteristicIndex()]==1) return 1;
        else{
		
		
		
		/* fill the incidence with the baselineIncidence */
		
		double incidence = this.parameters.get("baselineIncidence")
				.get(0).getValue(oldValues);
		/* apply the relative risks for the continuous characteristics */
		ArrayList<ParameterInfo> relativeRisks = this.parameters
				.get("relativeRisksContinuous");
		for (ParameterInfo singleRelativeRisk : relativeRisks) {
			/*
			 * get the index number of the characteristic to which the relative
			 * risk belongs
			 */
			int applyTo = singleRelativeRisk.getApplyToIndex();
			double referenceValue = singleRelativeRisk.getConstant();
			double RR = singleRelativeRisk.getValue(oldValues);

			incidence = incidence
					* (Math.pow(RR, oldValues[applyTo] - referenceValue));
		}
		/* apply the relative risks for the categorical characteristics */
		relativeRisks = this.parameters
				.get("relativeRisksCategorical");
		for (ParameterInfo singleRelativeRisk : relativeRisks) {

			incidence = incidence
					* singleRelativeRisk.getValue(oldValues);
		}
		/* draw new value */
		int newValue;
		return newValue = drawRandom(1-Math.exp(-incidence));
	}}

}
