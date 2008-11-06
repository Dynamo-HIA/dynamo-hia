package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Control;

public class TestTraverseListener extends AbstractLoggingClass implements
		TraverseListener {

	public TestTraverseListener() {
		super();
	}

	public void keyTraversed(TraverseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got keyTraversed callback.");
	}

}
