/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

public class RelativeRisksTab extends TabPlatform {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String RELATIVE_RISKS = "Relative Risks";
	private static final String RELATIVE_RISK = "Relative Risk";
	

	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public RelativeRisksTab(TabFolder tabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(tabFolder, RELATIVE_RISKS, selectedNode, dynamoSimulationObject, dataBindingContext, helpGroup);
	}

	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections) throws ConfigurationException {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		String tabName = RELATIVE_RISK + newTabNumber;
		return new RelativeRiskTab(defaultSelections, this.getTabManager().getTabFolder(), 
				tabName, dynamoSimulationObject, 
				dataBindingContext, selectedNode, helpGroup);
	}	
	
	@Override
	public String getNestedTabPrefix() {
		return RELATIVE_RISK;
	}

	@Override
	public NestedTab createNestedTab() throws DynamoConfigurationException,
			ConfigurationException {
		return createNestedDefaultTab(null);
	}

	@Override
	public void deleteNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
		relativeRiskTab.removeTabDataObject();		
	}

	public Set<String> getConfigurations() {
		Map<Integer, TabRelativeRiskConfigurationData> configurations = 
			this.dynamoSimulationObject.getRelativeRiskConfigurations();
		// Conversion into a String keyset
		Set<String> keySet = new LinkedHashSet<String>();
		for (Integer index : configurations.keySet()) {
			keySet.add(index.toString());
		}
		return keySet;
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		if (nestedTab != null) {
			RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
			relativeRiskTab.refreshSelectionGroup();			
		}
	}

	public void refreshFirstTab() throws ConfigurationException {
		refreshNestedTab(this.getTabManager().nestedTabs.get(RELATIVE_RISK + "1"));		
	}
	
	
}