package nl.rivm.emi.dynamo.estimation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProgressLogger implements ProgressIndicatorInterface {
	Log log = LogFactory.getLog(this.getClass().getSimpleName());
	private String message = null;
	private int maximum;
	private int position = 0;

	public ProgressLogger(String message) {
		super();
		this.message = message;
		log.info(message + ": Instantiated.");
	}

	@Override
	public void dispose() {
		log.info(message + ": Disposed.");
	}

	@Override
	public void setMaximum(int percent) {
		maximum = percent;
		log.info(message + ": Maximum set to " + percent);
	}

	@Override
	public void update(int percent) {
		position = percent;
		log.info(message + ": Position set to " + percent);
	}

	@Override
	public int getPosition() {
		log.info(message + ": Position requested, returned: " + position);
		return position;
	}

}
