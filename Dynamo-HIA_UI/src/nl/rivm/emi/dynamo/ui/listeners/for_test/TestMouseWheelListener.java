package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Control;

public class TestMouseWheelListener extends AbstractLoggingClass implements
		MouseWheelListener {

	public TestMouseWheelListener() {
		super();
	}

	public void mouseScrolled(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseScrolled callback.");
	}

}
