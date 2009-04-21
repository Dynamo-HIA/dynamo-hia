/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.LinkedHashMap;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * Handles all actions of the nested disease tabs
 * 
 * @author schutb
 *
 */
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
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections 
			) throws ConfigurationException {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		String tabName = DISEASE + newTabNumber;				
		return new DiseaseTab(defaultSelections, this.getTabManager().getTabFolder(), 
				tabName, dynamoSimulationObject, 
				dataBindingContext, selectedNode, helpGroup);
	}
	
	@Override
	public NestedTab createNestedTab() throws ConfigurationException {
		return createNestedDefaultTab(null);
	}
	
	@Override
	public String getNestedTabPrefix() {
		return DISEASE;
	}

	@Override
	public Set<String> getConfigurations() {
		LinkedHashMap<String, ITabDiseaseConfiguration> configurations = 
			(LinkedHashMap<String, ITabDiseaseConfiguration>) this.dynamoSimulationObject.getDiseaseConfigurations();
		return configurations.keySet();
	}

	@Override
	public void deleteNestedTab(NestedTab nestedTab) throws ConfigurationException {
		DiseaseTab diseaseTab = (DiseaseTab) nestedTab;
		diseaseTab.removeTabDataObject();
	}
	
	@Override
	public void refreshNestedTab(NestedTab nestedTab) throws ConfigurationException {
		DiseaseTab diseaseTab = (DiseaseTab) nestedTab;
		diseaseTab.refreshSelectionGroup();
	}	
}