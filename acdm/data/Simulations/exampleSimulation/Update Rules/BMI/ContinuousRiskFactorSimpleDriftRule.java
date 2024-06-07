package nl.rivm.emi.cdm.rules.update;

import java.util.ArrayList;
import java.util.HashMap;

public class ContinuousRiskfactorSimpleDrift extends GeneralRule{

	private HashMap<String, ArrayList<ParameterInfo>> parameters;

	ContinuousRiskfactorSimpleDrift() {
		super();
	}

	int userupdaterule(double[] oldValues) {

		/* fill the other Mortality with the baselineOtherMortality */
		double drift = this.parameters.get("drift")
				.get(0).getValue(oldValues);
		/* apply the relative risks for the continuous characteristics */
		double newValue;
		return newValue = drift+oldValues[this.getCharacteristicIndex()];
	}

}
