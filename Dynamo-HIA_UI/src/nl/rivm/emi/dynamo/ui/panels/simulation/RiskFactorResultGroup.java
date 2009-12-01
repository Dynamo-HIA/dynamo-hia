package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Shows the result drop downs of the risk factor
 * 
 * @author schutb
 * 
 */
public class RiskFactorResultGroup {

	public static final String RISK_FACTOR_PREVALENCE = "Risk Factor Prevalence";
	public static final String TRANSITION = "Transition";

	protected Composite group;
	// private Composite plotComposite;
	private GenericComboModifyListener riskDropDownModifyListener;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private HelpGroup helpGroup;

	public RiskFactorResultGroup(Set<String> selections,
			Composite plotComposite, BaseNode selectedNode,
			HelpGroup helpGroup, Composite topNeighbour,
			GenericComboModifyListener riskDropDownModifyListener,
			DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		this.selections = selections;
		// this.plotComposite = plotComposite;
		this.riskDropDownModifyListener = riskDropDownModifyListener;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;
		group = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);

		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {

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

		GenericDropDownPanel transitionDropDownPanel = createDropDown(
				TRANSITION, dynamoTabDataManager.getDropDownSet(TRANSITION,
						chosenRiskFactorName));
		this.riskDropDownModifyListener
				.registerDropDown(transitionDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(transitionDropDownPanel
				.getDropDown(), TRANSITION);
		GenericDropDownPanel riskFactorPrevalenceDropDownPanel = createDropDown(
				RISK_FACTOR_PREVALENCE, dynamoTabDataManager.getDropDownSet(
						RISK_FACTOR_PREVALENCE, chosenRiskFactorName));

		// Register with the drop down from the selector
		this.riskDropDownModifyListener
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

}
