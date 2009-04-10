/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

public class DiseasesTab extends TabPlatform {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String DISEASES = "Diseases";
	private static final String DISEASE = "Disease";
	
	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public DiseasesTab(TabFolder tabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(tabFolder, DISEASES, selectedNode, dynamoSimulationObject, dataBindingContext, helpGroup);
	}

	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections, 
			Map<String, String> oldState) throws ConfigurationException {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		String tabName = DISEASE + newTabNumber;				
		return new DiseaseTab(defaultSelections, oldState, this.getTabManager().getTabFolder(), 
				tabName, dynamoSimulationObject, 
				dataBindingContext, selectedNode, helpGroup);		
	}
	
	@Override
	public NestedTab createNestedTab() throws ConfigurationException {
		return createNestedDefaultTab(null, null);
	}	
	
	@Override
	public String getNestedTabPrefix() {
		return DISEASE;
	}

	@Override
	//TODO: Upgrade for Relative Risks: public Set<Set<String>> getConfigurations() {
	public Set<String> getConfigurations() {
		Map<String, IDiseaseConfiguration> configurations = 
			this.dynamoSimulationObject.getDiseaseConfigurations();
		return configurations.keySet();
	}

	@Override
	public void deleteNestedTab(String name) {
		//Map<tabName, IDiseaseConfiguration> tabConfiguration
		// TODO Auto-generated method stub
		//Map<String, String> oldState = new LinkedHashMap<String, String>();		
		
		
		Map<String, IDiseaseConfiguration> configurations = 
			this.dynamoSimulationObject.getDiseaseConfigurations();
		
		/*
		String removedDisease = getSelectedString(configurations, index);
		IDiseaseConfiguration oldConfig = 
			configurations.get(removedDisease);*/
		
		// Copy the contents of the old state before deletion
		/*
		oldState.put(DiseaseSelectionGroup.DISEASE, oldConfig.getName());
		oldState.put(DiseaseResultGroup.DISEASE_PREVALENCE, oldConfig.getPrevalenceFileName());
		oldState.put(DiseaseResultGroup.INCIDENCE, oldConfig.getExcessMortalityFileName());
		oldState.put(DiseaseResultGroup.EXCESS_MORTALITY, oldConfig.getExcessMortalityFileName());
		oldState.put(DiseaseResultGroup.DALY_WEIGHTS, oldConfig.getDalyWeightsFileName());
		*/
		//log.debug("removedDisease: " + removedDisease);
		//configurations.remove(getSelectedString(configurations, index));
		configurations.remove(name);
		this.dynamoSimulationObject.setDiseaseConfigurations(configurations);
	}
	
	public String getSelectedString(Map<String, IDiseaseConfiguration> 
		configurations, int selectedIndex) {
		return (String) ((IDiseaseConfiguration) 
				configurations.values().toArray()[selectedIndex]).getName();
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab) throws ConfigurationException {
		DiseaseTab diseaseTab = (DiseaseTab) nestedTab;
		diseaseTab.refreshSelectionGroup();
	}
	
	
	
}