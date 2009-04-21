package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * 
 * Shows the selection drop downs of the disease tab
 * 
 * @author schutb
 *
 */
public class DiseaseSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public static final String DISEASE = "Disease";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener dropDownModifyListener;
	private BaseNode selectedNode;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private GenericDropDownPanel diseaseDropDownPanel;
	private HelpGroup helpGroup;

	public DiseaseSelectionGroup(String tabName, Set<String> selections, Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup, 
			DynamoTabDataManager dynamoTabDataManager) 
			throws ConfigurationException {
		this.selections = selections;
		this.plotComposite = plotComposite;		
		this.selectedNode = selectedNode;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;
		
		group = new Group(plotComposite, SWT.FILL);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -3;
		group.setLayout(gridLayout);	
		//group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???		
		log.debug("diseaseFactorSelectionGroup" + group);
		
		createDropDownArea();
	}

	private void createDropDownArea() throws ConfigurationException {
				
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(22, 0);
		group.setLayoutData(formData);			
		
		TreeAsDropdownLists treeLists = TreeAsDropdownLists.getInstance(selectedNode); 
		DropDownPropertiesSet validDiseasesSet = new DropDownPropertiesSet();
		validDiseasesSet.addAll(treeLists.getValidDiseases());
		
		String chosenDiseaseName = null;
		if (this.selections != null) {
			for (String chosenName : this.selections) {
				chosenDiseaseName = chosenName;		
			}			
		}
		
		diseaseDropDownPanel = 
			createDropDown(DISEASE, 
					dynamoTabDataManager.
					getDropDownSet(DISEASE, chosenDiseaseName), 
					dynamoTabDataManager
					);
		this.dropDownModifyListener =
			diseaseDropDownPanel.getGenericComboModifyListener();		
	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		DiseaseFactorDataAction updateDiseaseFactorDataAction = 
			new DiseaseFactorDataAction();
		return new GenericDropDownPanel(group, label, 2, 
				selectablePropertiesSet, 
				null, dynamoTabDataManager);		
	}
	
	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}

	public void refreshSelectionDropDown() throws ConfigurationException {
		this.diseaseDropDownPanel.refresh();		
	}
}
