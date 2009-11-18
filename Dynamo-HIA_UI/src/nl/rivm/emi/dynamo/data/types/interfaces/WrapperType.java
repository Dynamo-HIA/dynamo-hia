package nl.rivm.emi.dynamo.data.types.interfaces;

/**
 * This interface defines the function in the model Object.<br/>
 * The WrapperType interface is implemented for elements that will have no value
 * in the modelobject. The wrapping they do is only in the XML-file.<br/>
 * 
 * A type implements either a PayloadType, a ContainerType, a WrapperType or
 * extends a RootElementType.
 * 
 * @author mondeelr
 * 
 */

public interface WrapperType {
	public WrapperType getNextWrapper();
}
