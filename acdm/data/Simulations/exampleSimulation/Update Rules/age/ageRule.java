package nl.rivm.emi.cdm.rules.update;

import java.util.ArrayList;
import java.util.HashMap;

public class AgeRule extends GeneralRule{

	private HashMap<String, ArrayList<ParameterInfo>> parameters;

	AgeRule() {
		super();
	}

	int userupdaterule(double[] oldValues) {

		
		double newValue;
		return newValue = 1+oldValues[this.getCharacteristicIndex()];
	}

}
