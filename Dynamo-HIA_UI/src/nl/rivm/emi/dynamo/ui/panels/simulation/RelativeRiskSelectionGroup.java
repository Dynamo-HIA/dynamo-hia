package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.RelativeRiskComboModifyListener;
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

public class RelativeRiskSelectionGroup implements RelativeRiskDropDownGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());

	protected Composite group;
	// private Composite plotComposite;
	// private GenericComboModifyListener dropDownModifyListener;
	private Set<String> selections;
	private RelativeRiskTabDataManager dynamoTabDataManager;
	private RelativeRiskDropDownPanel fromDropDownPanel;
	private RelativeRiskDropDownPanel toDropDownPanel;
	private HelpGroup helpGroup;

	private RelativeRiskComboModifyListener fromDropDownModifyListener;

	private RelativeRiskComboModifyListener toDropDownModifyListener;

	public RelativeRiskSelectionGroup(String tabName, Set<String> set,
			Composite plotComposite, BaseNode selectedNode,
			HelpGroup helpGroup, RelativeRiskTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		// this.selections = selections;
		// this.plotComposite = plotComposite;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;

		log.debug("relativeRiskFactorSelectionGroup::this.plotComposite: "
				+ plotComposite);
		group = new Composite(plotComposite, SWT.FILL);

		handleLayout();
		// group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???
		log.debug("relativeRiskFactorSelectionGroup" + group);

		createDropDownArea();
	}

	private void createDropDownArea() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {

		// updateAvaillableRRsForThisTab();

		handleLayoutData();

		String chosenIndexSelection = null;
		if (this.selections != null) {
			for (String chosenIndex : selections) {
				chosenIndexSelection = chosenIndex;
			}
		}
//
//		DropDownPropertiesSet fromSet = dynamoTabDataManager.getFromSet();
//		this.fromDropDownPanel = createDropDown(RelativeRiskDropDownPanel.FROM,
//				fromSet, dynamoTabDataManager);
		this.fromDropDownPanel = createDropDown(RelativeRiskDropDownPanel.FROM,
		null, dynamoTabDataManager);
		HelpTextListenerUtil.addHelpTextListeners(fromDropDownPanel
				.getDropDown(), RelativeRiskDropDownPanel.FROM);
//		String configuredFrom = dynamoTabDataManager.getConfiguredFrom();
//		fromDropDownPanel.selectConfiguredValue(configuredFrom);
//		DropDownPropertiesSet toSet = dynamoTabDataManager
//				.getToSet(dynamoTabDataManager.getConfiguredFrom());

//		this.toDropDownPanel = createDropDown(RelativeRiskDropDownPanel.TO,
//				dynamoTabDataManager.getToSet(chosenIndexSelection),
//				dynamoTabDataManager);
		this.toDropDownPanel = createDropDown(RelativeRiskDropDownPanel.TO,
				null,
				dynamoTabDataManager);
		HelpTextListenerUtil.addHelpTextListeners(
				toDropDownPanel.getDropDown(), RelativeRiskDropDownPanel.TO);
//		toDropDownPanel.selectConfiguredValue(dynamoTabDataManager
//				.getConfiguredTo());
	}

	private RelativeRiskDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet,
			RelativeRiskTabDataManager dynamoTabDataManager)
			throws ConfigurationException {
		// RelativeRiskFactorDataAction updateRelativeRiskFactorDataAction = new
		// RelativeRiskFactorDataAction();
		return new RelativeRiskDropDownPanel(group, label, 2,
				dynamoTabDataManager, helpGroup, selectablePropertiesSet);
	}

	public void refreshSelectionDropDown() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {

		// updateAvaillableRRsForThisTab();

		// this.fromDropDownPanel.refresh();
		// this.toDropDownPanel.refresh();

	}

	private void handleLayout() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3; // changed from -3
		group.setLayout(gridLayout);
	}

	private void handleLayoutData() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 6);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(44, 0);
		group.setLayoutData(formData);
	}

	// private void updateAvaillableRRsForThisTab() {
	// Set<String> diseaseNames = this.dynamoTabDataManager
	// .getDynamoSimulationObject().getDiseaseConfigurations()
	// .keySet();
	//
	// RelativeRiskTabPlatformDataManager dataManager =
	// (RelativeRiskTabPlatformDataManager) this.dynamoTabDataManager;
	//
	// /*
	// * removes RR that have been selected in other tabs or have become
	// * impossible by other tabs
	// */
	//
	// dataManager.getAvaillableRRs().removeRRSelectedInOtherTabs(
	// dataManager.getConfigurations(), diseaseNames,
	// dataManager.getSingleConfiguration());
	// }

	// public GenericComboModifyListener getFromDropDownModifyListener() {
	// return this.fromDropDownModifyListener;
	// }

	// public GenericComboModifyListener getToDropDownModifyListener() {
	// return this.toDropDownModifyListener;
	// }
}
