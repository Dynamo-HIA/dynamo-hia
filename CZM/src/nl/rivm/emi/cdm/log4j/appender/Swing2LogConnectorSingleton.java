package nl.rivm.emi.cdm.log4j.appender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.log4j.appender.SwingAppender.SwingLogOutputStream;

public class Swing2LogConnectorSingleton {
	/**
	 * Statics first.
	 */
	static private Swing2LogConnectorSingleton instance = null;

	static public synchronized Swing2LogConnectorSingleton getInstance() {
		if (instance == null) {
			instance = new Swing2LogConnectorSingleton();
		}
		return instance;
	}

	/**
	 * Instance fields next.
	 */
	private Log log = LogFactory.getLog(this.getClass().getSimpleName());

	private SwingLogOutputStream theStream = null;

	private Swing2LogConnectorSingleton() {
		super();
	}

	public SwingLogOutputStream getTheStream() {
		log.info("Getting stream: " + theStream);
		return theStream;
	}

	public void setTheStream(SwingLogOutputStream theStream) {
		log.info("Setting stream: " + theStream);
		this.theStream = theStream;
	}
}
