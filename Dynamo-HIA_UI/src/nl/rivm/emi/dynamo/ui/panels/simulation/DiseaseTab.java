/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * Defines the nested disease tab
 * 
 * @author schutb
 * 
 */
public class DiseaseTab extends NestedTab {

	private Log log = LogFactory.getLog("DiseaseTab");
	private DiseaseSelectionGroup diseaseSelectionGroup;
	private DynamoTabDataManager dynamoTabDataManager;

	/**
	 * @param tabfolder
	 * @param diseasesTabPlatform
	 *            TODO
	 * @param defaultDisease
	 * @param output
	 * @throws ConfigurationException
	 * @throws NoMoreDataException 
	 */
	public DiseaseTab(Set<String> selectedDisease, TabFolder tabfolder,
			String tabName, DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, HelpGroup helpGroup,
			DiseasesTabPlatform diseasesTabPlatform)
			throws ConfigurationException, NoMoreDataException {
		super(selectedDisease, tabfolder, tabName, dynamoSimulationObject,
				selectedNode, helpGroup, null, diseasesTabPlatform);
	}

	/**
	 * Create the active contents of this tab
	 * (BRRR. Called from the constructor of the superclass.)
	 * 
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	@Override
	public void makeIt() throws ConfigurationException, NoMoreDataException {
		this.dynamoTabDataManager = new DiseaseTabDataManager(selectedNode,
				getDynamoSimulationObject(), this.selections,
				(DiseasesTabPlatform) myTabPlatform, tabName);
		try {
			this.diseaseSelectionGroup = new DiseaseSelectionGroup(tabName,
					this.selections, this.plotComposite, selectedNode,
					helpGroup, dynamoTabDataManager);

			DiseaseResultGroup diseaseResultGroup = new DiseaseResultGroup(
					this.selections, this.plotComposite, selectedNode,
					helpGroup, diseaseSelectionGroup.group,
					diseaseSelectionGroup.getDropDownModifyListener(),
					dynamoTabDataManager);
			diseaseSelectionGroup.goListen();
//			diseaseSelectionGroup.refreshSelectionDropDown();
		} catch (DynamoNoValidDataException e) {
			this.dynamoTabDataManager.removeFromDynamoSimulationObject();
			// When no more data is available.
			if (this.diseaseSelectionGroup != null) {
				this.diseaseSelectionGroup.remove();
			}
			throw new NoMoreDataException(e.getMessage());

		}
	}

	public DynamoTabDataManager getDynamoTabDataManager() {
		return dynamoTabDataManager;
	}

	public String getCurrentSelectionText() {
		return diseaseSelectionGroup.getCurrentSelectionText();
	}

	public void refreshSelectionGroup() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		this.diseaseSelectionGroup.refreshSelectionDropDown();
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}
}