/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.LinkedHashSet;
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
	public NestedTab getNestedDefaultTab(Set<String> defaultSelections) throws ConfigurationException {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		return new DiseaseTab(defaultSelections, this.getTabManager().getTabFolder(), 
				DISEASE + newTabNumber, dynamoSimulationObject, 
				dataBindingContext, selectedNode, helpGroup);		
	}
	
	@Override
	public NestedTab getNestedTab() throws ConfigurationException {
		return getNestedDefaultTab(null);
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
	public void deleteNestedTab(int index) {
		// TODO Auto-generated method stub
		Map<String, IDiseaseConfiguration> configurations = 
			this.dynamoSimulationObject.getDiseaseConfigurations();
		
		configurations.remove(getSelectedString(configurations, index));

		this.dynamoSimulationObject.setDiseaseConfigurations(configurations);
	}
	
	public String getSelectedString(Map<String, IDiseaseConfiguration> 
		configurations, int selectedIndex) {
		return (String) ((IDiseaseConfiguration) 
				configurations.values().toArray()[selectedIndex]).getName();
	}
	
	
	
}