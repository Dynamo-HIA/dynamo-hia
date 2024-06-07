package nl.rivm.emi.cdm.rules.update;

import java.util.ArrayList;
import java.util.HashMap;

public class SimpleMortalityRule extents GeneralRule {

	private HashMap<String, ArrayList<ParameterInfo>> parameters;

	SimpleMortalityRule() {
		super();
	}

	int userupdaterule(double[] oldValues) {
        int newValue; 
        /* 1 = alive, 0=dead */
        /* those who are dead stay dead */
        if (oldValues[this.getCharacteristicIndex()]==0) return 0;
        else{
		/* fill the other Mortality with the baselineOtherMortality */
		double otherMortality = this.parameters.get("baselineOtherMortality")
				.get(0).getValue(oldValues);
		
		/* apply the relative risks for the continuous characteristics */
		ArrayList<ParameterInfo> relativeRisks = this.parameters
				.get("relativeRisksOtherMortContinuous");
		for (ParameterInfo singleRelativeRisk : relativeRisks) {
			/*
			 * get the index number of the characteristic to which the relative
			 * risk belongs
			 */
			int applyTo = singleRelativeRisk.getApplyToIndex();
			double referenceValue = singleRelativeRisk.getConstant();
			double RR = singleRelativeRisk.getValue(oldValues);

			otherMortality = otherMortality
					* (Math.pow(RR, oldValues[applyTo] - referenceValue));
		}
		/* apply the relative risks for the categorical characteristics */
		relativeRisks = this.parameters
				.get("relativeRisksotherMortCategorical");
		for (ParameterInfo singleRelativeRisk : relativeRisks) {

			otherMortality = otherMortality
					* singleRelativeRisk.getValue(oldValues);
		}
		
		
		/*
		 * now add attributable mortality for diseases with constant attributable
		 * mortality
		 */
		double sumAttributableMort = 0;
		ArrayList<ParameterInfo> attributableMortalities = this.parameters
				.get("attributableMort");
		for (ParameterInfo attribMort : attributableMortalities) {
			sumAttributableMort = sumAttributableMort
					+ attribMort.getValue(oldValues);
		}
		
		double mortality = otherMortality + sumAttributableMort;
		
		newValue = 1-drawRandom(1-Math.exp(-mortality));
	}
	}
}
