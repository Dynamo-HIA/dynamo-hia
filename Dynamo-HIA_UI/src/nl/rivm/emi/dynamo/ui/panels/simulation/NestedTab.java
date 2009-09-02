package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * The nested tab object, basis for all sub tabs
 * 
 * @author schutb
 *
 */
public abstract class NestedTab extends Tab {

	protected Set<String> selections;

	public NestedTab(Set<String> selections, TabFolder tabFolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(tabFolder, tabName, dynamoSimulationObject,
				dataBindingContext,
				selectedNode, helpGroup);	
		this.selections = selections;
	}

	
	
}
