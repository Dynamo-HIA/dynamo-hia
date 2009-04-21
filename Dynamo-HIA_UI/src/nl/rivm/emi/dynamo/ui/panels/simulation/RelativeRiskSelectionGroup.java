package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
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

public class RelativeRiskSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public static final String FROM = "From";
	public static final String TO = "To";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener dropDownModifyListener;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private GenericDropDownPanel fromDropDownPanel;
	private GenericDropDownPanel toDropDownPanel;	
	private HelpGroup helpGroup;

	private GenericComboModifyListener fromDropDownModifyListener;

	private GenericComboModifyListener toDropDownModifyListener;

	public RelativeRiskSelectionGroup(String tabName, 
			Set<String> set, Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup, 
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		this.selections = selections;
		this.plotComposite = plotComposite;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;
		
		log.debug("relativeRiskFactorSelectionGroup::this.plotComposite: " + plotComposite);
		group = new Group(plotComposite, SWT.FILL);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -3;
		group.setLayout(gridLayout);	
		//group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???		
		log.debug("relativeRiskFactorSelectionGroup" + group);
		
		createDropDownArea();
	}

	private void createDropDownArea() throws ConfigurationException {
				
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, -5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(44, 0);
		group.setLayoutData(formData);					
		
		String chosenIndexSelection = null;
		if (this.selections != null) {
			for (String chosenIndex : selections) {
				chosenIndexSelection = chosenIndex;		
			}
		}
		
		this.fromDropDownPanel = 
			createDropDown(FROM, dynamoTabDataManager.
					getDropDownSet(FROM, chosenIndexSelection), 
					dynamoTabDataManager
					);
		this.fromDropDownModifyListener =
			fromDropDownPanel.getGenericComboModifyListener();
		
		
		this.toDropDownPanel = 
			createDropDown(TO, dynamoTabDataManager.
					getDropDownSet(TO, chosenIndexSelection), 
					dynamoTabDataManager
					);
		this.toDropDownModifyListener =
			toDropDownPanel.getGenericComboModifyListener();
		
	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		RelativeRiskFactorDataAction updateRelativeRiskFactorDataAction = 
			new RelativeRiskFactorDataAction();
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, 
				null,
				dynamoTabDataManager);		
	}	

	public void refreshSelectionDropDown() throws ConfigurationException {
		this.fromDropDownPanel.refresh();		
		this.toDropDownPanel.refresh();
	}

	public GenericComboModifyListener getFromDropDownModifyListener() {
		return this.fromDropDownModifyListener;
	}

	public GenericComboModifyListener getToDropDownModifyListener() {
		return this.toDropDownModifyListener;
	}
}
