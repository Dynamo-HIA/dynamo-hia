package nl.rivm.emi.dynamo.data;

import java.util.TreeMap;

public class AgeSteppedContainer<E> {
	static public final String ageSteppedContainerTagName = "agesteppedcontainer";
	static public final String ageStepAttributeName = "agestep";
	static public final String numberOfStepsAttributeName = "numsteps";
	static public final String ageWrapperTagName = "age";
	static public final String ageValueAttributeName = "value";

	float ageStepSize;
	Object[] objects;

	public AgeSteppedContainer(float stepSize, int numberOfSteps) {
		ageStepSize = stepSize;
		objects = new Object[numberOfSteps];
	}

	public float getAgeStepSize(){
		return ageStepSize;
	}
	
	public E get(int step) throws ArrayIndexOutOfBoundsException{
			return (E)objects[step];
	}

	/**
	 * 
	 * @param step The index at which the Object should be stored.
	 * @param object The Object to be stored
	 * @return The old Object when the place was already occupied, the new
	 *         Object when it was empty before the call, null when the step was
	 *         out of range.
	 */
	public E put(int step, E object) throws ArrayIndexOutOfBoundsException{
		E result;
			if ((result = (E)objects[step]) == null) {
				result = object;
			}
			objects[step] = object;
		return result;
	}
}