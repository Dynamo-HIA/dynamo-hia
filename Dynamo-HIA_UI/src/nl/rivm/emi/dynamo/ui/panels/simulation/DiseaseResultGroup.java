package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
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
import org.eclipse.swt.widgets.Group;


public class DiseaseResultGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public static final String DISEASE_PREVALENCE = "Disease Prevalence";
	public static final String INCIDENCE = "Incidence";
	public static final String EXCESS_MORTALITY = "Excess Mortality";
	public static final String DALY_WEIGHTS = "DALY Weights";
	protected Group group;
	private Composite plotComposite;
	private GenericComboModifyListener diseaseDropDownModifyListener;
	private BaseNode selectedNode;
	private Map<String, IDiseaseConfiguration> configuration;
	private Set<String> selections;
	private DynamoTabDataManager dynamoTabDataManager;
	
	
	public DiseaseResultGroup(Set<String> selections, Composite plotComposite, 
			BaseNode selectedNode, HelpGroup helpGroup,
			Composite topNeighbour, 
			GenericComboModifyListener diseaseDropDownModifyListener,
			DynamoTabDataManager dynamoTabDataManager) throws ConfigurationException {
		this.selections = selections;
		//this.configuration = configuration;
		this.selectedNode = selectedNode;
		this.plotComposite = plotComposite;
		this.diseaseDropDownModifyListener = diseaseDropDownModifyListener;
		this.dynamoTabDataManager = dynamoTabDataManager;
		
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = -5;
		group.setLayout(gridLayout);			
		createDropDownArea(topNeighbour);
	}

	private void createDropDownArea(Composite topNeighbour) throws ConfigurationException {		
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 0);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, 0);
		group.setLayoutData(formData);
		
		 
		DropDownPropertiesSet prevSet = new DropDownPropertiesSet();
		//chosenDiseaseName === default will be from IDiseaseConfiguration for Initialization!

		String chosenDiseaseName = null;
		if (this.selections != null) {
			for (String chosenName : selections) {
				chosenDiseaseName = chosenName;		
			}
		}
		GenericDropDownPanel diseasePrevalenceDropDownPanel = 
			createDropDown(DISEASE_PREVALENCE, 
					dynamoTabDataManager.getDropDownSet(DISEASE_PREVALENCE, chosenDiseaseName), 
					null);
		// Register with the drop down from the selector
		this.diseaseDropDownModifyListener.
			registerDropDown(diseasePrevalenceDropDownPanel);
		
		
		//AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) diseaseObject.get(XMLTagEntityEnum.UNITTYPE.getElementName());
		GenericDropDownPanel incidenceDropDownPanel = 
			createDropDown(INCIDENCE, 
					dynamoTabDataManager.getDropDownSet(INCIDENCE, chosenDiseaseName), 
					null);
		this.diseaseDropDownModifyListener.
			registerDropDown(incidenceDropDownPanel);
		
		
		GenericDropDownPanel excessMortalityDropDownPanel = 
			createDropDown(EXCESS_MORTALITY, 
					dynamoTabDataManager.getDropDownSet(EXCESS_MORTALITY, chosenDiseaseName), 
					null);
		this.diseaseDropDownModifyListener.
			registerDropDown(excessMortalityDropDownPanel);		
		
		//AtomicTypeObjectTuple transitionTuple = (AtomicTypeObjectTuple) lotsOfData.get(XMLTagEntityEnum.UNITTYPE.getElementName());
		GenericDropDownPanel dalyWeightsDropDownPanel = 
			createDropDown(DALY_WEIGHTS, 
					dynamoTabDataManager.getDropDownSet(DALY_WEIGHTS, chosenDiseaseName), 
					null);
		this.diseaseDropDownModifyListener.
			registerDropDown(dalyWeightsDropDownPanel);
				
		// Set the nested contents
		//this.diseaseDropDownModifyListener.setNestedContents(nestedComboMapsContents);				
	}

	private GenericDropDownPanel createDropDown(String label, 
			DropDownPropertiesSet selectablePropertiesSet, 
			AtomicTypeObjectTuple tuple) {
		return new GenericDropDownPanel(group, label, 2,
				selectablePropertiesSet, 
				null, this.dynamoTabDataManager);		
	}

}
