package nl.rivm.emi.dynamo.data;

import java.util.TreeMap;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class BiGenderSteppedContainer<E> {
	static public final String genderTagName = "gender";

	Object[] objects;

	public BiGenderSteppedContainer() {
		objects = new Object[BiGender.NUMBER_OF_CLASSES];
	}
	
	public E get(int index) throws ArrayIndexOutOfBoundsException{
			return (E)objects[index];
	}

	public IObservable getWritableValue(int index) throws ArrayIndexOutOfBoundsException{
		return new WritableValue(objects[index], objects[index].getClass());
}
	/**
	 * 
	 * @param index The index at which the Object should be stored.
	 * @param object The Object to be stored
	 * @return The old Object when the place was already occupied, the new
	 *         Object when it was empty before the call, null when the step was
	 *         out of range.
	 */
	public E put(int index, E object) throws ArrayIndexOutOfBoundsException{
		E result;
			if ((result = (E)objects[index]) == null) {
				result = object;
			}
			objects[index] = object;
		return result;
	}
}