package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

public class TestFocusListener extends AbstractLoggingClass implements
		FocusListener {

	public TestFocusListener() {
		super();
	}

	public void focusGained(FocusEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got focusGained event.");
	}

	public void focusLost(FocusEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got focusLost event.");
	}

}
