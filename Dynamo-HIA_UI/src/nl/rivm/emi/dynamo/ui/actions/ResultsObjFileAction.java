package nl.rivm.emi.dynamo.ui.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Action for use in TreeViewer contextmenus.<br/>
 * Deserializes the resultsObject and redisplays the results screen from a prior
 * simulation-run.
 */
public class ResultsObjFileAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Constructor initializing the context.
	 * 
	 * @param shell
	 *            Parent Shell.
	 * @param v
	 *            Containing TreeViewer.
	 * @param node
	 *            Selected Node on which the Action will work.
	 */
	public ResultsObjFileAction(Shell shell, TreeViewer v, BaseNode node) {
		super(shell, v, node, "bogus");
	}

	/**
	 * Start the Action.
	 */
	@Override
	public void run() {
		String filePath = node.getPhysicalStorage().getAbsolutePath();
		File savedFile = new File(filePath);
		deSerializeAndDisplay(savedFile);
	}

	/**
	 * Read the serialized Object and recreate the display.<br/>
	 * <br/>
	 * TODO Under construction. Deserializing the DynamoOutputFactory doesn't
	 * work the easy way. An attempt at a workaround through the ScenarioInfo
	 * and Population[] Objects and then constructing the DynamoOutputFactory
	 * failed for the first.
	 * 
	 * @param savedFile
	 *            The file containing the serialized DynamoOutputFactoryObject.
	 */
	public void deSerializeAndDisplay(File savedFile) {
		String savedFileAbsolutePath = savedFile.getAbsolutePath();
		String savedFileDirectoryPath = savedFileAbsolutePath.substring(0,
				savedFileAbsolutePath.lastIndexOf(File.separator));
		deserializeScenarioInfo(savedFileDirectoryPath);
		deserializePopulationArray(savedFileDirectoryPath);
	}

	private void deserializeScenarioInfo(String savedFileDirectoryPath) {
		ObjectInputStream in;
		String scenarioInfoFilePath = savedFileDirectoryPath
				+ File.separator
				+ StandardTreeNodeLabelsEnum.SCENARIOINFOOBJECTFILE
						.getNodeLabel() + ".obj";
		File scenarioInfoFile = new File(scenarioInfoFilePath);
		ScenarioInfo scenarioInfoObject = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(scenarioInfoFile)));
			scenarioInfoObject = (ScenarioInfo) in.readObject();
			log.fatal("Deserialized ScenarioInfo, populationSize: "
					+ scenarioInfoObject.getPopulationSize());
		} catch (Exception e) {
			if (scenarioInfoObject == null) {
				log.fatal("Deserialized scenarioInfoObject is still null.");
			} else {
				log.fatal("Deserialized ScenarioInfo, populationSize: "
						+ scenarioInfoObject.getPopulationSize());
			}
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + scenarioInfoFile.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		}
	}

	private void deserializePopulationArray(String savedFileDirectoryPath) {
		ObjectInputStream in;
		String populationArrayFilePath = savedFileDirectoryPath
				+ File.separator
				+ StandardTreeNodeLabelsEnum.POPULATIONARRAYOBJECTFILE
						.getNodeLabel() + ".obj";
		File populationArrayFile = new File(populationArrayFilePath);
		Population[] populationArrayObject = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(populationArrayFile)));
			populationArrayObject = (Population[]) in.readObject();
			log.fatal("Deserialized ScenarioInfo, populationSize: "
					+ populationArrayObject[0].size());
		} catch (Exception e) {
			if (populationArrayObject == null) {
				log.fatal("Deserialized scenarioInfoObject is still null.");
			} else {
				log.fatal("Deserialized ScenarioInfo, populationSize: "
						+ populationArrayObject[0].size());
			}
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \""
					+ populationArrayFile.getName() + "\"\nresulted in an "
					+ e.getClass().getName() + "\nwith message "
					+ e.getMessage());
			messageBox.open();
		}
	}

}
