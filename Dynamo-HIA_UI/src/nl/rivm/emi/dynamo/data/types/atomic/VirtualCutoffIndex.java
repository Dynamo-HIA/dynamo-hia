package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.configuration.ConfigurationException;

/**
 * NB(mondeelr) Not yet in use.
 */
public class VirtualCutoffIndex extends AbstractRangedInteger implements ContainerType
		/* implements IXMLHandlingLayer<Integer> */ {
	static final protected String XMLElementName = "virtual_cutoff_index";

	/**
	 * Constructor for use at this level.
	 * 
	 * @param lowerLimit
	 * @param upperLimit
	 * @throws ConfigurationException
	 */
	public VirtualCutoffIndex(){
		super(XMLElementName, 0, 7);
	}
}
