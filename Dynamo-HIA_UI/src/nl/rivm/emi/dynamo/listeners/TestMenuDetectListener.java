package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Control;

public class TestMenuDetectListener extends AbstractLoggingClass implements
		MenuDetectListener {

	public TestMenuDetectListener() {
		super();
	}

	public void menuDetected(MenuDetectEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got menuDetected callback.");
	}
}
