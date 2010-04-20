package nl.rivm.emi.dynamo.ui.actions;

import java.util.HashMap;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.support.RelativeRisksCollection;
import nl.rivm.emi.dynamo.ui.support.SimulationConfigurationDropdownsMapFactory;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;

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
			log.info("Starting test");
			SimulationConfigurationDropdownsMapFactory a = new SimulationConfigurationDropdownsMapFactory();
			HashMap<String, Object> product = SimulationConfigurationDropdownsMapFactory
					.make(node);
			TreeAsDropdownLists treeList = TreeAsDropdownLists.getInstance(node);
			RelativeRisksCollection collection = new RelativeRisksCollection(
					node, treeList);
			collection.findAllRelativeRisks(node, treeList);
			collection.dump4Debug();
			TreeAsDropdownLists lists = TreeAsDropdownLists.getInstance(node);
			log.debug("First call of getValidDiseases() through singleton.");
			Set<String> validDiseaseNames = lists.getValidDiseaseNames();
			for (String diseaseName : validDiseaseNames) {
				log.debug("Valid diseasename: " + diseaseName);
			}
			log.debug("Second call of getValidDiseases() through singleton.");
			validDiseaseNames = lists.getValidDiseaseNames();
			for (String diseaseName : validDiseaseNames) {
				log.debug("Valid diseasename: " + diseaseName);
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
			box.setText("Debug");
			box.setMessage("Dropdowns Factory blew up!");
			box.open();
		}
	}
}
