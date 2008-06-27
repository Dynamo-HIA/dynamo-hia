package nl.rivm.emi.dynamo.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AbstractLoggingClass {
	protected Log log;

	protected AbstractLoggingClass() {
		super();
		this.log = LogFactory.getLog(this.getClass().getName());
	}
	
}
