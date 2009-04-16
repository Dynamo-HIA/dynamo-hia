/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
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
		log.debug(dynamoSimulationObject + "dynamoSimulationObject");
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
		LinkedHashMap<String, TabRiskFactorConfigurationData> configurations = 
			(LinkedHashMap<String, TabRiskFactorConfigurationData>) this.dynamoSimulationObject.getRiskFactorConfigurations();
		return configurations.keySet();
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
		relativeRiskTab.refreshSelectionGroup();
	}
	
	
}