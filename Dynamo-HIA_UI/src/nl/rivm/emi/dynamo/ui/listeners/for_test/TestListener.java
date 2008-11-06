package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class TestListener extends AbstractLoggingClass implements
		Listener {

	public TestListener() {
		super();
	}

	public void handleEvent(Event arg0) {
		log.info("Control " + arg0.item.getClass().getName()
				+ " got handleEvent callback.");
	}

}
