package nl.rivm.emi.dynamo.data.types.interfaces;
/**
 * This "" refers to the place in the model Object. 
 * A type is either a PayloadType or a ContainerType. 
 * The default value is used for creating a modelobject from scratch.
 *
 * @author mondeelr
 *
 * @param <T>
 */
public interface PayloadType<T> {
	public T getDefaultValue();
}
