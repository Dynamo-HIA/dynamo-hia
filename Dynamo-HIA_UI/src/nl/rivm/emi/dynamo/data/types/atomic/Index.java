package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.configuration.ConfigurationException;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
public class Index extends AbstractClassIndex implements ContainerType
		/* implements IXMLHandlingLayer<Integer> */ {
	static final protected String XMLElementName = "index";

	static final protected Integer hardUpperLimit = new Integer(9);

	/**
	 * Constructor for use at this level.
	 * 
	 * @param lowerLimit
	 * @param upperLimit
	 * @throws ConfigurationException
	 */
	public Index(){
		super(XMLElementName, new Integer(0), hardUpperLimit);
	}
}
