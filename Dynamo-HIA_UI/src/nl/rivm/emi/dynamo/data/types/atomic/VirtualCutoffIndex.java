package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.configuration.ConfigurationException;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
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
