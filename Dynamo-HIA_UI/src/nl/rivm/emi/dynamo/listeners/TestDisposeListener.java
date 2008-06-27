package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;

public class TestDisposeListener extends AbstractLoggingClass implements
		DisposeListener {

	public TestDisposeListener() {
		super();
	}

	public void widgetDisposed(DisposeEvent arg0) {
		log.info("Widget " + ((Control) arg0.getSource()).getClass().getName()
				+ " got widgetDisposed event.");
	}

}
