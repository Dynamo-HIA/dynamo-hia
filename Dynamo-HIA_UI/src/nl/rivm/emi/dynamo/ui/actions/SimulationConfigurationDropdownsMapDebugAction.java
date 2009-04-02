package nl.rivm.emi.dynamo.ui.actions;

import java.util.HashMap;

import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.util.SimulationConfigurationDropdownsMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SimulationConfigurationDropdownsMapDebugAction extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	private Shell shell;
	private DirectoryNode node;

	public SimulationConfigurationDropdownsMapDebugAction(Shell shell,
			DirectoryNode selectedNode) {
		super();
		this.shell = shell;
		this.node = selectedNode;
	}

	@Override
	public void run() {
		try {
			log.fatal("Starting test");
			SimulationConfigurationDropdownsMapFactory a = new SimulationConfigurationDropdownsMapFactory();
			HashMap<String, HashMap<?, ?>> product = SimulationConfigurationDropdownsMapFactory
					.make(node);
			MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
			box.setText("Debug");
			box.setMessage("Dropdowns Factory ran without blowing up.");
			box.open();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setText("Debug");
			box.setMessage("Dropdowns Factory blew up!");
			box.open();
		}
	}
}
