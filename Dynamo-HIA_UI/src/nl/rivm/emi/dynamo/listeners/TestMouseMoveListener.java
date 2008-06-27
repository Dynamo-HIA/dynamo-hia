package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Control;

public class TestMouseMoveListener extends AbstractLoggingClass implements
		MouseMoveListener {

	public TestMouseMoveListener() {
		super();
	}

	public void mouseMove(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseMove callback.");
	}

}
