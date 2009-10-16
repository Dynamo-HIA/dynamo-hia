package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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

		group = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -15;
		group.setLayout(gridLayout);
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {

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

		this.transitionDropDownPanel = createDropDown(TRANSITION,
				dynamoTabDataManager.getDropDownSet(TRANSITION,
						chosenRiskFactorName));
		this.scenarioDropDownModifyListener
				.registerDropDown(transitionDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(transitionDropDownPanel
				.getDropDown(), TRANSITION);
		this.riskFactorPrevalenceDropDownPanel = createDropDown(
				RISK_FACTOR_PREVALENCE, dynamoTabDataManager.getDropDownSet(
						RISK_FACTOR_PREVALENCE, chosenRiskFactorName));
		// Register with the drop down from the selector
		this.scenarioDropDownModifyListener
				.registerDropDown(riskFactorPrevalenceDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(
				riskFactorPrevalenceDropDownPanel.getDropDown(),
				RISK_FACTOR_PREVALENCE);
	}

	private GenericDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet)
			throws ConfigurationException {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, this.dynamoTabDataManager, helpGroup);
	}

	public void refreshGroupDropDown() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		this.transitionDropDownPanel.refresh();
		this.riskFactorPrevalenceDropDownPanel.refresh();
	}
}
