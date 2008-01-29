package nl.rivm.emi.cdm_v0.parameter;

import java.util.ArrayList;

/**
 * 
 * @author mondeelr
 * 
 */
public class ParameterInTimeArray extends ArrayList<Float> {

	private static final long serialVersionUID = -1999283956159006764L;

	public ParameterInTimeArray() {
		super();
	}

	public ParameterInTimeArray(ArrayList<Float> parameters) {
		this();
		for (int count = 0; count < parameters.size(); count++) {
			add(new Float(parameters.get(count).floatValue()));
		}
	}

	/**
	 * Temporary constructor to create ParameterArray-s with for instance all
	 * zeroes.
	 * 
	 * @param size
	 * @param setValue
	 */
	public ParameterInTimeArray(int size, float setValue) {
		for (int count = 0; count < size; count++) {
			add(new Float(setValue));
		}
	}
/**
 * 
 * @param incomingRates
 * @return
 */
	public static ParameterInTimeArray generateComplement(ParameterInTimeArray incomingRates) {
		ParameterInTimeArray outgoingRates = new ParameterInTimeArray();
		int incomingSize = incomingRates.size();
		for (int count = 0; count < incomingSize; count++) {
			Float incomingRate = incomingRates.get(count);
			Float outgoingRate = 1 - incomingRate; 
			outgoingRates.add(outgoingRate);
		}
		return outgoingRates;
	}
}
