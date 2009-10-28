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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Shows the result drop downs of the disease tab
 * 
 * @author schutb
 * 
 */
public class DiseaseResultGroup {

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());

	public static final String DISEASE_PREVALENCE = "Disease Prevalence";
	public static final String INCIDENCE = "Incidence";
	public static final String EXCESS_MORTALITY = "Excess Mortality";
	public static final String DALY_WEIGHTS = "DALY Weights";
	protected Composite group;
	// private Composite plotComposite;
	private GenericComboModifyListener diseaseDropDownModifyListener;
	// private BaseNode selectedNode;
	// private Map<String, ITabDiseaseConfiguration> configuration;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	private HelpGroup helpGroup;

	public DiseaseResultGroup(Set<String> selections, Composite plotComposite,
			BaseNode selectedNode, HelpGroup helpGroup, Composite topNeighbour,
			GenericComboModifyListener diseaseDropDownModifyListener,
			DynamoTabDataManager dynamoTabDataManager)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		this.selections = selections;
		// this.selectedNode = selectedNode;
		// this.plotComposite = plotComposite;
		this.diseaseDropDownModifyListener = diseaseDropDownModifyListener;
		this.dynamoTabDataManager = dynamoTabDataManager;
		this.helpGroup = helpGroup;

		group = new Composite(plotComposite, SWT.NONE);
		handleLayout();
		createDropDownArea(topNeighbour);
	}


	private void createDropDownArea(Composite topNeighbour)
			throws ConfigurationException, NoMoreDataException,
			DynamoNoValidDataException {
		handleLayoutData(topNeighbour);

		// chosenDiseaseName is retrieved from IDiseaseConfiguration for
		// Initialization!
		String chosenDiseaseName = null;
		if (this.selections != null) {
			for (String chosenName : selections) {
				chosenDiseaseName = chosenName;
			}
		}
		GenericDropDownPanel diseasePrevalenceDropDownPanel = createDropDown(
				DISEASE_PREVALENCE, dynamoTabDataManager.getDropDownSet(
						DISEASE_PREVALENCE, chosenDiseaseName));

		// Register with the drop down from the selector
		this.diseaseDropDownModifyListener
				.registerDropDown(diseasePrevalenceDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(
				diseasePrevalenceDropDownPanel.getDropDown(),
				DISEASE_PREVALENCE);
		GenericDropDownPanel incidenceDropDownPanel = createDropDown(INCIDENCE,
				dynamoTabDataManager.getDropDownSet(INCIDENCE,
						chosenDiseaseName));
		this.diseaseDropDownModifyListener
				.registerDropDown(incidenceDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(incidenceDropDownPanel
				.getDropDown(), INCIDENCE);
		GenericDropDownPanel excessMortalityDropDownPanel = createDropDown(
				EXCESS_MORTALITY, dynamoTabDataManager.getDropDownSet(
						EXCESS_MORTALITY, chosenDiseaseName));
		this.diseaseDropDownModifyListener
				.registerDropDown(excessMortalityDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(excessMortalityDropDownPanel
				.getDropDown(), EXCESS_MORTALITY);
		GenericDropDownPanel dalyWeightsDropDownPanel = createDropDown(
				DALY_WEIGHTS, dynamoTabDataManager.getDropDownSet(DALY_WEIGHTS,
						chosenDiseaseName));
		this.diseaseDropDownModifyListener
				.registerDropDown(dalyWeightsDropDownPanel);
		HelpTextListenerUtil.addHelpTextListeners(dalyWeightsDropDownPanel
				.getDropDown(), DALY_WEIGHTS);
	}

	private GenericDropDownPanel createDropDown(String label,
			DropDownPropertiesSet selectablePropertiesSet)
			throws ConfigurationException {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, this.dynamoTabDataManager, helpGroup);
	}
	private void handleLayout() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3; // Changed from -5.
		group.setLayout(gridLayout);
	}

	private void handleLayoutData(Composite topNeighbour) {
		FormData formData = new FormData();
//		formData.top = new FormAttachment(topNeighbour, 0);
		formData.top = new FormAttachment(topNeighbour, 6);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
//		formData.bottom = new FormAttachment(100, 0);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
