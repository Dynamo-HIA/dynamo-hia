package nl.rivm.emi.cdm.rules.update;

import java.util.ArrayList;
import java.util.HashMap;

public class SexRule extends GeneralRule{

	private HashMap<String, ArrayList<ParameterInfo>> parameters;

	SexRule() {
		super();
	}

	int userupdaterule(double[] oldValues) {

		
		double newValue;
		return newValue = oldValues[this.getCharacteristicIndex()];
	}

}
