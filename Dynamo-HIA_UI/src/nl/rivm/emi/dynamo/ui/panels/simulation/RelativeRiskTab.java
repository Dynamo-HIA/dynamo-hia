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
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

public class RelativeRiskTab extends NestedTab {
	
	private Log log = LogFactory.getLog("RelativeRiskTab");
	private RelativeRiskSelectionGroup relativeRiskSelectionGroup;
	private DynamoTabDataManager dynamoTabDataManager;
	
	
	public DynamoTabDataManager getDynamoTabDataManager() {
		return dynamoTabDataManager;
	}

	/**
	 * @param selectedRelativeRisk : contains only a single key for the particular relative risk data in the configuration 
	 * (relativeriskconfigurion) in the dynamosimulationobject. This will be put in "selections"
	 * @param tabfolder
	 * @param tabName
	 * @param dynamoSimulationObject
	 * @param dataBindingContext
	 * @param selectedNode
	 * @param helpGroup
	 * @throws ConfigurationException
	 */
	public RelativeRiskTab(Set<String> selectedRelativeRisk, TabFolder tabfolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup
			) throws ConfigurationException {
		super(selectedRelativeRisk, tabfolder, tabName,
				dynamoSimulationObject,
				dataBindingContext, 
				selectedNode,
				helpGroup);		
		
	}
	
	/**
	 * Create the active contents of this tab
	 * @throws ConfigurationException 
	 * @throws NoMoreDataException 
	 * @throws DynamoNoValidDataException 
	 */	
	@Override
	public void makeIt() throws ConfigurationException, NoMoreDataException{		
		
		this.dynamoTabDataManager =
			new RelativeRiskTabDataManager(selectedNode, 
					getDynamoSimulationObject(),
					this.selections, this);
		
		
			try {
				this.relativeRiskSelectionGroup =
					new RelativeRiskSelectionGroup(tabName,
							this.selections, 
							this.plotComposite,
							selectedNode, helpGroup,
							dynamoTabDataManager);
			
		
		
		RelativeRiskResultGroup RelativeRiskResultGroup =
			new RelativeRiskResultGroup(this.selections, 
					this.plotComposite,
					selectedNode, helpGroup,
					relativeRiskSelectionGroup,
					dynamoTabDataManager);
			} catch (DynamoNoValidDataException e) {
				this.dynamoTabDataManager.removeFromDynamoSimulationObject();
				throw new NoMoreDataException(e.getMessage());
				// TODO Auto-generated catch block
				
			}
		
	}
	
	
	public void refreshSelectionGroup() throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException {
		// Don't do this if first time object construction is going on
		if (this.relativeRiskSelectionGroup != null) {
			this.relativeRiskSelectionGroup.refreshSelectionDropDown();	
		}		
	}

	public void removeTabDataObject() throws ConfigurationException {
		this.dynamoTabDataManager.removeFromDynamoSimulationObject();
	}
}