package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorResultGroup {

	public static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	public static final String TRANSITION = "Transition";
	
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener riskDropDownModifyListener;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	
	public RiskFactorResultGroup(Set<String> selections, 
			Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener riskDropDownModifyListener,
			DynamoTabDataManager dynamoTabDataManager
			) throws ConfigurationException {
		this.selections = selections;
		this.plotComposite = plotComposite;
		this.riskDropDownModifyListener = riskDropDownModifyListener;
		this.dynamoTabDataManager = dynamoTabDataManager;
		
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);			
		
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour) throws ConfigurationException {
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(65, -5);
		group.setLayoutData(formData);
						
		String chosenRiskFactorName = null;
		if (this.selections != null) {
			for (String chosenName : selections) {
				chosenRiskFactorName = chosenName;		
			}
		}
		

		GenericDropDownPanel transitionDropDownPanel = 
			createDropDown(TRANSITION, 
					dynamoTabDataManager.getDropDownSet(
							TRANSITION, chosenRiskFactorName));
		this.riskDropDownModifyListener.
			registerDropDown(transitionDropDownPanel);
		
		GenericDropDownPanel riskFactorPrevalenceDropDownPanel = 
			createDropDown(RISK_FACTOR_PREVALENCE, 
					dynamoTabDataManager.getDropDownSet(
					RISK_FACTOR_PREVALENCE, chosenRiskFactorName));
		
		// Register with the drop down from the selector
		this.riskDropDownModifyListener.
			registerDropDown(riskFactorPrevalenceDropDownPanel);

	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet 
			) throws ConfigurationException {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, 
				null, this.dynamoTabDataManager);		
	}
	
}
