package nl.rivm.emi.dynamo.listeners;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;

public class TestMouseTrackListener extends AbstractLoggingClass implements
		MouseTrackListener {

	public TestMouseTrackListener() {
		super();
	}

	public void mouseEnter(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseEnter callback.");
	}

	public void mouseExit(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseExit callback.");
	}

	public void mouseHover(MouseEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got mouseHover callback.");
	}
}
