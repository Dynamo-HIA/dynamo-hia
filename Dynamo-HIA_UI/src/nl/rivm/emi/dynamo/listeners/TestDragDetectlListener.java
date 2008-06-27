package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.widgets.Control;

public class TestDragDetectlListener extends AbstractLoggingClass implements
		DragDetectListener {

	public TestDragDetectlListener() {
		super();
	}

	public void dragDetected(DragDetectEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got dragDetected event.");
	}

}
