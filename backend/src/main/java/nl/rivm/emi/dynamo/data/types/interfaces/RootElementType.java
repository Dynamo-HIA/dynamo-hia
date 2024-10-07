package nl.rivm.emi.dynamo.data.types.interfaces;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;

/**
 * This class defines the function in the configuration Object.<br/>
 * The RootElementType indicates the elementname belongs to a rootelement. It
 * has no value for putting into the modelobject.<br/>
 * 
 * A type implements either a PayloadType, a ContainerType, a WrapperType or
 * implements a RootElementType.
 * 
 * @author mondeelr
 * 
 */

public class RootElementType extends XMLTagEntity {

	/**
	 * 
	 * @param tagName The elementname belonging to this instance.
	 */
	protected RootElementType(String tagName) {
		super(tagName);
	}

}
