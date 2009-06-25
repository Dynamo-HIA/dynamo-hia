package nl.rivm.emi.dynamo.ui.listeners.shell;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;

public class MyShellListener implements ShellListener {
	Log log = LogFactory.getLog(this.getClass().getName());

	@Override
	public void shellActivated(ShellEvent arg0) {
		log.debug(">>>Shell activated.");
	}

	@Override
	public void shellClosed(ShellEvent arg0) {
		log.debug(">>>Shell closed.");
	}

	@Override
	public void shellDeactivated(ShellEvent arg0) {
		log.debug(">>>Shell deactivated.");
	}

	@Override
	public void shellDeiconified(ShellEvent arg0) {
		log.debug(">>>Shell deiconified.");
	}

	@Override
	public void shellIconified(ShellEvent arg0) {
		log.debug(">>>Shell iconified.");
	}
}
