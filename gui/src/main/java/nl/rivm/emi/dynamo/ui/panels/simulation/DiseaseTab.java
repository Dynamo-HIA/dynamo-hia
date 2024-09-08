/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

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

	public DynamoTabDataManager getDynamoTabDataManager() {
		return dynamoTabDataManager;
	}

	/** 
	 * @param tabfolder
	 * @param defaultDisease 
	 * @param output
	 * @throws ConfigurationException 
	 */
	public DiseaseTab(Set<String> selectedDisease,  
			TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, 
			HelpGroup helpGroup) throws ConfigurationException {
		super(selectedDisease, tabfolder, tabName,
				dynamoSimulationObject,
				selectedNode, 
				helpGroup, null, null);
	}

	/**
	 * Create the active contents of this tab
	 * @throws ConfigurationException 
	 * @throws NoMoreDataException 
	 */	
	@Override
	public void makeIt() throws ConfigurationException, NoMoreDataException{
		this.dynamoTabDataManager =
			new DiseaseTabDataManager(selectedNode, 
					getDynamoSimulationObject(),
					this.selections);
		try {
		this.diseaseSelectionGroup =
			new DiseaseSelectionGroup(tabName, this.selections, this.plotComposite,
					selectedNode, helpGroup,
					dynamoTabDataManager
					);
		
		
			DiseaseResultGroup diseaseResultGroup =
				new DiseaseResultGroup(this.selections, this.plotComposite,					
						selectedNode, helpGroup,
						diseaseSelectionGroup.group,
						diseaseSelectionGroup.getDropDownModifyListener(),
						dynamoTabDataManager
						);
		} catch (DynamoNoValidDataException e) {
			this.dynamoTabDataManager.removeFromDynamoSimulationObject();
			if (this.diseaseSelectionGroup!= null) this.diseaseSelectionGroup.remove();
			throw new NoMoreDataException(e.getMessage());

		}
	}
	
	public void refreshSelectionGroup() throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		this.diseaseSelectionGroup.refreshSelectionDropDown();
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}
}