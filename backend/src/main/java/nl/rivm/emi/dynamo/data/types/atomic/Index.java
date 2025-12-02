package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.interfaces.ContainerType;

/**
 * Nonnegative Integer without fixed upper limit. This to enable adjustment to
 * the range of categories the transitions can cover.
 */
@SuppressWarnings("rawtypes")
public class Index extends AbstractClassIndex implements ContainerType {
	static final protected String XMLElementName = "index";

	public Index(){
		super(XMLElementName, hardLowerLimit, hardUpperLimit);
	}
}
