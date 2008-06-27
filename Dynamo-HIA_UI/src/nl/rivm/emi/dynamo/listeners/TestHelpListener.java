package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Control;

public class TestHelpListener extends AbstractLoggingClass implements
		HelpListener {

	public TestHelpListener() {
		super();
	}

	public void helpRequested(HelpEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got helpRequested event.");
	}

}
