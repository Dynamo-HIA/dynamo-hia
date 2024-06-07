package nl.rivm.emi.dynamo.data.types.interfaces;
/**
 * This interface defines the function in the model Object. 
 * A type implements either a PayloadType, a ContainerType or a WrapperType. 
 *
 * @author mondeelr
 *
 * @param <T> The Type of the value in the modelobject.
 */
public interface PayloadType<T> {
	/**
	  * Get the default value for creating a modelobject from scratch.

	 * @return The default value.
	 */
	public T getDefaultValue();
}
