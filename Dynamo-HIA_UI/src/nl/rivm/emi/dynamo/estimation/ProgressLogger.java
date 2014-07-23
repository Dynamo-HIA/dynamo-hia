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
		if (25*Math.floor(position/25)==position) 	log.info(message + ": Position set to " + percent);
	}
	
	
	@Override
	public void update() {
		position++;		
		if (25*Math.floor(position/25)==position) log.info(message + ": Position set to " + position);
	}

	@Override
	public int getPosition() {
		log.info(message + ": Position requested, returned: " + position);
		return position;
	}

	@Override
	public int getSelection() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDisposed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelection(int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIndeterminate(String text) {
		// TODO Auto-generated method stub
		
	}

}
