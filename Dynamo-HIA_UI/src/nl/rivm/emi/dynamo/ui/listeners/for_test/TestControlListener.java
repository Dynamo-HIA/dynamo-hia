package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Control;

public class TestControlListener extends AbstractLoggingClass implements
		ControlListener {

	public TestControlListener() {
		super();
	}

	public void controlMoved(ControlEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got Control moved event.");

	}

	public void controlResized(ControlEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got Control resized event.");
	}

}
