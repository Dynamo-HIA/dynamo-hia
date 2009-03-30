package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractRangedInteger;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

import org.apache.commons.configuration.ConfigurationException;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
public class RelativeRiskIndex extends AbstractRangedInteger implements ContainerType {
	static final protected String XMLElementName = "RRindex";

	public RelativeRiskIndex(){
		super(XMLElementName, 0, Integer.MAX_VALUE);
	}

	/**
	 * No default instances for now.
	 */
	public Integer getMaxNumberOfDefaultValues() throws ConfigurationException{
		return getMIN_VALUE();
	}
}
