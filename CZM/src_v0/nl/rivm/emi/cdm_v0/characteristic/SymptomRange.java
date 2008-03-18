package nl.rivm.emi.cdm_v0.characteristic;

import java.util.HashMap;

import nl.rivm.emi.cdm_v0.parameter.ParameterInTimeArray;
import nl.rivm.emi.cdm_v0.time.TimeBaseBase;

/**
 * Prevalence- and transition-values for a SymptomRange of a Characteristic in
 * the Chronic Disease Model. TODO Also contains a TimeBase, but I doubt if it
 * is permanent. Mortality data should maybe be added.
 * 
 * @author mondeelr
 * 
 */
public class SymptomRange {
	/**
	 * Index of this range within the Characteristic.
	 */
	int index;

	/**
	 * PR label of this SymptomRange.
	 */
	String label;

	TimeBaseBase singletonTimeBase;

	ParameterInTimeArray myPrevalenceRates;

	/**
	 * One ParameterInTimeArray for each destination Range.
	 */
	HashMap<Integer, ParameterInTimeArray> transitionRates;

	public SymptomRange(int classIndex, TimeBaseBase singletonTimeBase,
			ParameterInTimeArray prevalenceRates,
			HashMap<Integer, ParameterInTimeArray> transitionRates) {
		this.index = classIndex;
		this.singletonTimeBase = singletonTimeBase;
		myPrevalenceRates = prevalenceRates;
		this.transitionRates = transitionRates;
	}

	public SymptomRange(int classIndex, TimeBaseBase singletonTimeBase) {
		this.index = classIndex;
		this.singletonTimeBase = singletonTimeBase;
	}

	public void setTransitionRate(int toRangeIndex,
			ParameterInTimeArray transitionRateArray) {
		transitionRates.put(new Integer(toRangeIndex), transitionRateArray);
	}

	public String getLabel() {
		return label;
	}

	public int getRangeIndex() {
		return index;
	}

	public void setPrevalenceRate(ParameterInTimeArray prevalenceRate) {
		myPrevalenceRates = prevalenceRate;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
