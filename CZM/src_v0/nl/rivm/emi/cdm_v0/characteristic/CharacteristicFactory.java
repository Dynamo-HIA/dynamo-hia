package nl.rivm.emi.cdm_v0.characteristic;

abstract public class CharacteristicFactory {

	abstract public Characteristic create();

	// Create NUMBER_OF_SYMPTOMRANGES SymptomRange-s with unique .
	// Create NUMBER_OF_SYMPTOMRANGES ParameterInTimeArrays for
	// the transitionrates plus one for the prevalencerate
	// for each SymptomRange.
	//
	// Do this for both genders.
	// 

}
