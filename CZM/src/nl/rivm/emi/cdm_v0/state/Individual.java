package nl.rivm.emi.cdm_v0.state;

import java.util.ArrayList;
import java.util.Random;

import nl.rivm.emi.cdm_v0.exceptions.UnequalSizeException;

public class Individual {
	ArrayList<Float> myTimeLine;
	ArrayList<Integer> myChances;
	
	/**
	 * Individual-s are born alive.
	 * @throws UnequalSizeException 
	 *
	 */
	public Individual(ArrayList<Float> timeLine, ArrayList<Integer> chances) throws UnequalSizeException{
		myTimeLine = timeLine;
		myChances = chances;
		if(timeLine.size() != myChances.size()){
			throw new UnequalSizeException("timeLine", timeLine.size(),
					"chances", chances.size());
		}
	}
	
	public Float simulateAgeOfDeath(){
		int currentAgeIndex = 0;
		Random myGenerator = new Random();
		for(; currentAgeIndex < myTimeLine.size(); currentAgeIndex++){
			int draw = myGenerator.nextInt(100000);
			if(draw< myChances.get(currentAgeIndex).intValue()){
				return myTimeLine.get(currentAgeIndex);
			}
		}
		return myTimeLine.get(myTimeLine.size()-1);
	}
}
