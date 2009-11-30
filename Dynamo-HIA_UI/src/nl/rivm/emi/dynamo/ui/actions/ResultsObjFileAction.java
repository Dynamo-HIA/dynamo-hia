package nl.rivm.emi.dynamo.ui.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.ui.panels.output.Output_UI;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;
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
		ScenarioParameters scenParms=deserializeScenarioParameters(savedFileDirectoryPath);
		DynamoOutputFactory output=deserializeOutputObject(savedFileDirectoryPath);
		new Output_UI(shell, output, scenParms, savedFileDirectoryPath);
	}

	private ScenarioParameters deserializeScenarioParameters(String savedFileDirectoryPath) {
		ObjectInputStream in;
		String scenarioInfoFilePath = savedFileDirectoryPath
				+ File.separator
				+ StandardTreeNodeLabelsEnum.SCENARIOPARMSOBJECTFILE
						.getNodeLabel() + ".obj";
		File scenarioInfoFile = new File(scenarioInfoFilePath);
		ScenarioParameters scenarioInfoObject = null;
		try {
	//		in = new ObjectInputStream(new BufferedInputStream(
	//				new FileInputStream(scenarioInfoFile)));
			in = new ObjectInputStream(
									new FileInputStream(scenarioInfoFile));
			scenarioInfoObject = (ScenarioParameters) in.readObject();
			//inputStream = new ObjectInputStream(new FileInputStream(filename));
		//	obj = inputStream.readObject()
			log.fatal("Deserialized ScenarioInfo");
		} catch (Exception e) {
			if (scenarioInfoObject == null) {
				log.fatal("Deserialized scenarioInfoObject is still null.");
			} else {
				log.fatal("Deserialized ScenarioInfo");
			}
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \"" + scenarioInfoFile.getName()
					+ "\"\nresulted in an " + e.getClass().getName()
					+ "\nwith message " + e.getMessage());
			messageBox.open();
		} return scenarioInfoObject; 
	}

	private DynamoOutputFactory deserializeOutputObject(String savedFileDirectoryPath) {
		ObjectInputStream in;
		String outputObjectFilePath = savedFileDirectoryPath
				+ File.separator
				+ StandardTreeNodeLabelsEnum.RESULTSOBJECTFILE
						.getNodeLabel() + ".obj";
		File outputObjectFile = new File(outputObjectFilePath);
		DynamoOutputFactory resultObject = null;
		try {
			//in = new ObjectInputStream(new BufferedInputStream(
			//		new FileInputStream(outputObjectFile)));
			in = new ObjectInputStream(
					new FileInputStream(outputObjectFile));
			resultObject = (DynamoOutputFactory) in.readObject();
			log.fatal("Deserialized ScenarioInfo, populationSize: "
					);
		} catch (Exception e) {
			if (resultObject == null) {
				log.fatal("Deserialized scenarioInfoObject is still null.");
			} else {
				log.fatal("Deserialized ScenarioInfo, populationSize: "
					);
			}
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_ADDED);
			messageBox.setMessage("Creation of \""
					+ outputObjectFile.getName() + "\"\nresulted in an "
					+ e.getClass().getName() + "\nwith message "
					+ e.getMessage());
			messageBox.open();
		}
		return resultObject;
	}

}
