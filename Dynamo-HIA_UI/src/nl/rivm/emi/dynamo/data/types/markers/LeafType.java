package nl.rivm.emi.dynamo.data.types.markers;
/**
 * This "leafness" refers to the place in the model Object. 
 * A type is either a LeafType or a ContainerType. 
 * The default value is used for creating a modelobject from scratch.
 *
 * @author mondeelr
 *
 * @param <T>
 */
public interface LeafType<T> {
	public T getDefaultValue();
}
