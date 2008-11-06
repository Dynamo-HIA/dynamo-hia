package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Control;

public class TestPaintListener extends AbstractLoggingClass implements
		PaintListener {

	public TestPaintListener() {
		super();
	}

	public void paintControl(PaintEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got paintControl callback.");
	}

}
