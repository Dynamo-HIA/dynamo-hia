package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Shows the result drop downs of the relative risks
 * 
 * @author schutb
 * 
 */
public class RelativeRiskResultGroup implements RelativeRiskDropDownGroup{

	protected Composite group;
	// private Composite plotComposite;

	// private BaseNode selectedNode;

	private Set<String> selections;
	private RelativeRiskTabDataManager relativeRiskTabDataManager;
	@SuppressWarnings("unused")
	private RelativeRiskSelectionGroup selectionGroup;
	private HelpGroup helpGroup;

	public RelativeRiskResultGroup(Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup,
			RelativeRiskSelectionGroup selectionGroup,
			RelativeRiskTabDataManager relativeRiskTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
	//	this.selections = selections; // removed 2025 as seems nonsense
		// this.plotComposite = plotComposite;
		this.selectionGroup = selectionGroup;
		this.relativeRiskTabDataManager = relativeRiskTabDataManager;
		this.helpGroup = helpGroup;
		group = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3; // changed from -5
		group.setLayout(gridLayout);
		createDropDownArea(selectionGroup.group);
	}

	private void createDropDownArea(Composite topNeighbour)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {

		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(77, 0);
		group.setLayoutData(formData);

		@SuppressWarnings("unused")
		String chosenIndexSelection = null;
		if (this.selections != null) {
			for (String chosenIndex : selections) {
				chosenIndexSelection = chosenIndex;
			}
		}
//		DropDownPropertiesSet fileNameSet = relativeRiskTabDataManager.getFileSet(relativeRiskTabDataManager.getConfiguredFrom(), relativeRiskTabDataManager.getConfiguredTo());
//
//		RelativeRiskDropDownPanel relativeRiskDropDownPanel = createDropDown(
//				RelativeRiskDropDownPanel.RELATIVE_RISK, relativeRiskTabDataManager.getDropDownSet(
//						RelativeRiskDropDownPanel.RELATIVE_RISK, chosenIndexSelection));
		RelativeRiskDropDownPanel relativeRiskDropDownPanel = createDropDown(
				RelativeRiskDropDownPanel.RELATIVE_RISK, null);
		// this.selectionGroup.getFromDropDownModifyListener().registerDropDown(
		// relativeRiskDropDownPanel);

		// this.selectionGroup.getToDropDownModifyListener().registerDropDown(
		// relativeRiskDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(relativeRiskDropDownPanel
				.getDropDown(), RelativeRiskDropDownPanel.RELATIVE_RISK);
		relativeRiskDropDownPanel.selectConfiguredValue(relativeRiskTabDataManager
				.getConfiguredFileName());
}

	private RelativeRiskDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet)
			throws ConfigurationException {
		return new RelativeRiskDropDownPanel(group, label, 2,
				this.relativeRiskTabDataManager,
				helpGroup, null);
	}
}
