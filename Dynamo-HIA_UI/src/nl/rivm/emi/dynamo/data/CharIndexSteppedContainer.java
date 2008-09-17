package nl.rivm.emi.dynamo.data;

public class CharIndexSteppedContainer<E> {
	static public final String charIndexSteppedContainerTagName = "agesteppedcontainer";
	static public String transitionEndpointWrapperTagName = null;
	static public final String transitionEndpointValueAttributeName = "value";

	Object[] objects;

	public CharIndexSteppedContainer(int numberOfSteps, String wrapperTagName) {
		transitionEndpointWrapperTagName = wrapperTagName;
		objects = new Object[numberOfSteps];
	}

	public E get(int step) throws ArrayIndexOutOfBoundsException {
		return (E) objects[step];
	}

	/**
	 * 
	 * @param step
	 *            The index at which the Object should be stored.
	 * @param object
	 *            The Object to be stored
	 * @return The old Object when the place was already occupied, the new
	 *         Object when it was empty before the call, null when the step was
	 *         out of range.
	 */
	public E put(int step, E object) throws ArrayIndexOutOfBoundsException {
		E result;
		if ((result = (E) objects[step]) == null) {
			result = object;
		}
		objects[step] = object;
		return result;
	}

	public int size() {
		return objects.length;
	}
}