package nl.rivm.emi.dynamo.ui.listeners.for_test;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

public class TestKeyListener extends AbstractLoggingClass implements
		KeyListener {

	public TestKeyListener() {
		super();
	}

	public void keyPressed(KeyEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got keyPressed callback.");
	}

	public void keyReleased(KeyEvent arg0) {
		log.info("Control " + ((Control) arg0.getSource()).getClass().getName()
				+ " got keyReleased callback.");
	}

}
