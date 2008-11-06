package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;

public class TestMouseListener extends AbstractLoggingClass implements
		MouseListener {

	public TestMouseListener() {
		super();
	}

	public void mouseDoubleClick(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseDoubleClick callback.");
	}

	public void mouseDown(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseDown callback.");
	}

	public void mouseUp(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseUp callback.");
	}

}
