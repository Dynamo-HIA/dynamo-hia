package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
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
 * Shows the result drop downs of the relative risks
 * 
 * @author schutb
 * 
 */
public class RelativeRiskResultGroup {

	public static final String RELATIVE_RISK = "Relative Risk";
	protected Composite group;
//	private Composite plotComposite;

//	private BaseNode selectedNode;

	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private RelativeRiskSelectionGroup selectionGroup;
	private HelpGroup helpGroup;

	public RelativeRiskResultGroup(Set<String> selections,
			Composite plotComposite, BaseNode selectedNode,
			HelpGroup helpGroup, RelativeRiskSelectionGroup selectionGroup,
			DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		this.selections = selections;
//		this.plotComposite = plotComposite;
		this.selectionGroup = selectionGroup;
		this.dynamoTabDataManager = dynamoTabDataManager;
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

		String chosenIndexSelection = null;
		if (this.selections != null) {
			for (String chosenIndex : selections) {
				chosenIndexSelection = chosenIndex;
			}
		}

		GenericDropDownPanel relativeRiskDropDownPanel = createDropDown(
				RELATIVE_RISK, dynamoTabDataManager.getDropDownSet(
						RELATIVE_RISK, chosenIndexSelection));
		this.selectionGroup.getFromDropDownModifyListener().registerDropDown(
				relativeRiskDropDownPanel);

		this.selectionGroup.getToDropDownModifyListener().registerDropDown(
				relativeRiskDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(relativeRiskDropDownPanel
				.getDropDown(), RELATIVE_RISK);
	}

	private GenericDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet)
			throws ConfigurationException {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, null, this.dynamoTabDataManager, helpGroup);
	}
}
