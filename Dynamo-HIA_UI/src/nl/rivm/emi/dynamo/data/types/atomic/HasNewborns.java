package nl.rivm.emi.dynamo.data.types.atomic;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractBoolean;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HasNewborns extends AbstractBoolean implements PayloadType<Boolean>{
Log log = LogFactory.getLog(getClass().getName());
	static final protected String XMLElementName = "hasnewborns";

	public HasNewborns() {
		super(XMLElementName);
		log.debug("Initializing.");
	}
}
