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
 * Shows the selection drop downs of the risk factor tab
 * 
 * @author schutb
 *
 */
public class RiskFactorSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());

	public static final String RISK_FACTOR = "Risk Factor";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener dropDownModifyListener;
	private BaseNode selectedNode;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;


	public RiskFactorSelectionGroup(
			Set<String> selections,
			Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			DynamoTabDataManager dynamoTabDataManager) 
			throws ConfigurationException {
		this.selections = selections;
		this.plotComposite = plotComposite;
		this.selectedNode = selectedNode;
		this.dynamoTabDataManager = dynamoTabDataManager;
		
		log.debug("RiskFactorSelectionGroup::this.plotComposite: "
				+ plotComposite);
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);
		// group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???

		layoutDropDownArea();
		createDropDownArea();
	}

	private void createDropDownArea() throws ConfigurationException {

		
		TreeAsDropdownLists treeLists = TreeAsDropdownLists.getInstance(selectedNode); 
		DropDownPropertiesSet validRiskFactorSet = new DropDownPropertiesSet();
		validRiskFactorSet.addAll(treeLists.getRiskFactors());
		
		String chosenRiskFactorName = null;
		if (this.selections != null) {
			for (String chosenName : this.selections) {
				chosenRiskFactorName = chosenName;		
			}			
		}
		
		GenericDropDownPanel riskFactorDropDownPanel = 
			createDropDown(RISK_FACTOR, 
					dynamoTabDataManager.
					getDropDownSet(RISK_FACTOR, chosenRiskFactorName), 
					dynamoTabDataManager
					);
		
		this.dropDownModifyListener = riskFactorDropDownPanel
				.getGenericComboModifyListener();
	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		RiskFactorDataAction updateRiskFactorDataAction = 
			new RiskFactorDataAction();
		return new GenericDropDownPanel(group, label, 2, 
				selectablePropertiesSet, 
				null, dynamoTabDataManager);		
	}
	
	private void layoutDropDownArea() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(27, -5);
		group.setLayoutData(formData);
	}

	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}
}
