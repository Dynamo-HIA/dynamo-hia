package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ScenarioResultGroup {


	public static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	public static final String TRANSITION = "Transition";
	private static final String CHANGE_WITH_RESPECT_BASELINE_SIMULATION = 
		"Change with respect to baseline simulation";
	
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener scenarioDropDownModifyListener;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private GenericDropDownPanel transitionDropDownPanel;
	private GenericDropDownPanel riskFactorPrevalenceDropDownPanel;
	
	public ScenarioResultGroup(Set<String> selections,
			Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener scenarioDropDownModifyListener, 
			DynamoTabDataManager dynamoTabDataManager
			) throws ConfigurationException {
		this.selections = selections;
		this.plotComposite = plotComposite;
		this.scenarioDropDownModifyListener = scenarioDropDownModifyListener;
		this.dynamoTabDataManager = dynamoTabDataManager;
		
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -15;
		group.setLayout(gridLayout);			
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour) throws ConfigurationException {
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(97, 5);
		group.setLayoutData(formData);
		
		Label label = new Label(group, SWT.LEFT);		
		label.setText(CHANGE_WITH_RESPECT_BASELINE_SIMULATION);
		Label emptyLabel = new Label(group, SWT.LEFT);
		emptyLabel.setText("");
		Label emptyLabel2 = new Label(group, SWT.LEFT);
		emptyLabel2.setText("");
		
		String chosenRiskFactorName = null;
		if (this.selections != null) {
			for (String chosenName : selections) {
				chosenRiskFactorName = chosenName;		
			}
		}
		
		this.transitionDropDownPanel = 
			createDropDown(TRANSITION, 
					dynamoTabDataManager.getDropDownSet(
							TRANSITION, chosenRiskFactorName));
		this.scenarioDropDownModifyListener.
			registerDropDown(transitionDropDownPanel);
		
		this.riskFactorPrevalenceDropDownPanel = 
			createDropDown(RISK_FACTOR_PREVALENCE, 
					dynamoTabDataManager.getDropDownSet(
					RISK_FACTOR_PREVALENCE, chosenRiskFactorName));
		// Register with the drop down from the selector
		this.scenarioDropDownModifyListener.
			registerDropDown(riskFactorPrevalenceDropDownPanel);


		
	}

	private GenericDropDownPanel createDropDown(String label, DropDownPropertiesSet selectablePropertiesSet) 
	throws ConfigurationException {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, 
				null, this.dynamoTabDataManager);		
	}

	public void refreshGroupDropDown() throws ConfigurationException {
		this.transitionDropDownPanel.refresh();
		this.riskFactorPrevalenceDropDownPanel.refresh();		
	}
}
