package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * Shows the result drop downs of the scenario tab
 * 
 * @author schutb
 * 
 */
public class ScenarioResultGroup {

	public static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	public static final String TRANSITION = "Transition";
	private static final String CHANGE_WITH_RESPECT_BASELINE_SIMULATION = "Change with respect to baseline simulation";

	protected Composite group;
	// 20090715 No need to store? private Composite plotComposite;
	private GenericComboModifyListener scenarioDropDownModifyListener;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private GenericDropDownPanel transitionDropDownPanel;
	private GenericDropDownPanel riskFactorPrevalenceDropDownPanel;
	private HelpGroup helpGroup;

	public ScenarioResultGroup(Set<String> selections, Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup, Composite topNeighbour,
			GenericComboModifyListener scenarioDropDownModifyListener,
			DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		this.selections = selections;
		// this.plotComposite = plotComposite;
		this.scenarioDropDownModifyListener = scenarioDropDownModifyListener;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;

		this.group = new Composite(plotComposite, SWT.NONE);
		handleLayout();
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour)
			throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException
			 {

		handleLayoutData(topNeighbour);

		Label label = new Label(this.group, SWT.LEFT);
		label.setText(CHANGE_WITH_RESPECT_BASELINE_SIMULATION);
		Label emptyLabel = new Label(this.group, SWT.LEFT);
		emptyLabel.setText("");
		Label emptyLabel2 = new Label(this.group, SWT.LEFT);
		emptyLabel2.setText("");
         
		String chosenRiskFactorName = null;
		if (this.selections != null) {
			for (String chosenName : selections) {
				chosenRiskFactorName = chosenName;
			}
		}
		try {
			this.riskFactorPrevalenceDropDownPanel = createDropDown(
					RISK_FACTOR_PREVALENCE, dynamoTabDataManager.getDropDownSet(
							RISK_FACTOR_PREVALENCE, chosenRiskFactorName));
			// added by Hendriek 11/2011 uit analogie
			HelpTextListenerUtil.addHelpTextListeners(riskFactorPrevalenceDropDownPanel
					.getDropDown(), RISK_FACTOR_PREVALENCE);
		// added 2010-4-18 by Hendriek and removed again as causes "rondzingen "
		//this.riskFactorPrevalenceDropDownPanel.genericComboModifyListener.registerDropDown(transitionDropDownPanel);
		// added 2010-4-18 by Hendriek : one is OK
		//this.riskFactorPrevalenceDropDownPanel.genericComboModifyListener.registerDropDown(riskFactorPrevalenceDropDownPanel);
		
		this.transitionDropDownPanel = createDropDown(TRANSITION,
				dynamoTabDataManager.getDropDownSet(TRANSITION,
						chosenRiskFactorName));
		this.scenarioDropDownModifyListener
				.registerDropDown(transitionDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(transitionDropDownPanel
				.getDropDown(), TRANSITION);
		
		
		
		} catch (DynamoNoValidDataException e) {
			for (Control childwidget:this.group.getChildren())
				childwidget.dispose();
			throw new DynamoNoValidDataException(e.getMessage());
		}
		// Register with the drop down from the selector
		this.scenarioDropDownModifyListener
				.registerDropDown(riskFactorPrevalenceDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(
				riskFactorPrevalenceDropDownPanel.getDropDown(),
				RISK_FACTOR_PREVALENCE);
	}

	private GenericDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet)
			throws ConfigurationException, NoMoreDataException {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, this.dynamoTabDataManager, helpGroup);
	}

	public void refreshGroupDropDown() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		this.transitionDropDownPanel.refresh();
		this.riskFactorPrevalenceDropDownPanel.refresh();
	}

	private void handleLayout() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3; // Changed from -15.
		group.setLayout(gridLayout);
	}

	private void handleLayoutData(Composite topNeighbour) {
		FormData formData = new FormData();
//		formData.top = new FormAttachment(topNeighbour, 0);
		formData.top = new FormAttachment(topNeighbour, 6);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
//		formData.bottom = new FormAttachment(97, 5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
	public void remove() {
		
		this.group.dispose();
		
	}
}
