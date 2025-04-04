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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Shows the selection drop downs of the disease tab
 * 
 * @author schutb
 * 
 */
public class DiseaseSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());

	public static final String DISEASE = "Disease";
	protected Composite group;
	// private Composite plotComposite;
	private GenericComboModifyListener dropDownModifyListener;
	// private BaseNode selectedNode;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private GenericDropDownPanel diseaseDropDownPanel;
	private HelpGroup helpGroup;

	public DiseaseSelectionGroup(String tabName, Set<String> selections,
			Composite plotComposite, BaseNode selectedNode,
			HelpGroup helpGroup, DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		this.selections = selections;
		// this.plotComposite = plotComposite;
		// this.selectedNode = selectedNode;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;
		try {
			// if no data, no group should be made so this should be inside a
			// try statement
			group = new Composite(plotComposite, SWT.FILL);

			handleLayout();
			// group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???
			log.debug("diseaseSelectionGroup: " + group + " hashCode(): "
					+ hashCode() + " tabName: " + tabName);
			createDropDownArea();
		} catch (NoMoreDataException e) {
			throw new NoMoreDataException(e.getMessage());
		} catch (DynamoNoValidDataException e) {
			throw new DynamoNoValidDataException(e.getMessage());
		}
	}

	private void createDropDownArea() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		// log.debug("Creating dropdown area for hash: " + this.hashCode());
		handleLayoutData();

		// RLM Not used.
		// TreeAsDropdownLists treeLists = TreeAsDropdownLists
		// .getInstance(selectedNode);
		// DropDownPropertiesSet validDiseasesSet = new DropDownPropertiesSet();
		// validDiseasesSet.addAll(treeLists.getValidDiseaseNames());

		// selections either is null or contains a single disease-name.
		String chosenDiseaseName = null;
		if (this.selections != null) {
			for (String chosenName : this.selections) {
				chosenDiseaseName = chosenName;
			}
		}// added by hendriek 2009-10-31: removed as this has already been done so this is done twice here
		// else {
			// In case a new tab is created, check if there are diseases left
			// and if so add this to the dynamosimulationobject
//
		//	Set<String> contents = this.dynamoTabDataManager.getContents(
		//			this.DISEASE, null);
		//	if (contents != null)
		//.updateObjectState(this.DISEASE, null);

		//	else
		//		throw new NoMoreDataException(
		//				"there are no more diseases to chose");
	//	}
		// end addition 2009-10-31
		DropDownPropertiesSet dropDownset = this.dynamoTabDataManager
				.getDropDownSet(DISEASE, chosenDiseaseName);
		if (dropDownset != null && !dropDownset.isEmpty()) {
			diseaseDropDownPanel = createDropDown(DISEASE, dynamoTabDataManager
					.getDropDownSet(DISEASE, chosenDiseaseName),
					dynamoTabDataManager);
	
		
			this.dropDownModifyListener = diseaseDropDownPanel
					.getGenericComboModifyListener();
			HelpTextListenerUtil.addHelpTextListeners(diseaseDropDownPanel
					.getDropDown(), DISEASE);
		} else if (chosenDiseaseName == null)
			throw new NoMoreDataException("there are no more diseases to chose");
		else
			throw new DynamoNoValidDataException(
					"configuration contains disease " + chosenDiseaseName
							+ " that is" + " no longer availlable");

		// rlm moved up this.dropDownModifyListener = diseaseDropDownPanel
		// .getGenericComboModifyListener();
	}

	private GenericDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet,
			DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException {
		// DiseaseFactorDataAction updateDiseaseFactorDataAction = new
		// DiseaseFactorDataAction();
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, dynamoTabDataManager, helpGroup);
	}

	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}

	public void remove() {
		this.group.dispose();
	}

	public void refreshSelectionDropDown() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		this.diseaseDropDownPanel.refresh();
	}

	private void handleLayout() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3; // Changed from -3.
		group.setLayout(gridLayout);
	}

	private void handleLayoutData() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 6);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		// formData.bottom = new FormAttachment(25, 0);
		formData.bottom = new FormAttachment(44, 0);
		group.setLayoutData(formData);
	}
}
