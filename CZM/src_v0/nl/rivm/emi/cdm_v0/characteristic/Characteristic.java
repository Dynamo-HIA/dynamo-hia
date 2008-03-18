package nl.rivm.emi.cdm_v0.characteristic;

import java.util.HashMap;

public class Characteristic extends HashMap<Integer,SymptomRangesForAGender>{

	// name
	// type
	
	static final public Integer MALE_INDEX = new Integer(0);
	
	static final public Integer FEMALE_INDEX = new Integer(1);
	
	public Characteristic(){
		super();
	}
	public void addBothSymptomRangesForAGender(SymptomRangesForAGender femaleCPG,
			SymptomRangesForAGender maleCPG){
		put( Characteristic.FEMALE_INDEX, femaleCPG);
		put( Characteristic.MALE_INDEX, maleCPG);
	}
	public void addSymptomRangeForAGender(Integer gender_index, SymptomRangesForAGender genderSymptomRanges) {
		put(gender_index, genderSymptomRanges);
	}
}
