/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class RelativeRisksTab extends TabPlatform {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private static final String RELATIVE_RISKS = "Relative Risks";
	private static final String RELATIVE_RISK = "Relative Risk";
	
	private DynamoSimulationObject modelObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;

	/**
	 * @param tabfolder
	 * @param output
	 */
	public RelativeRisksTab(TabFolder tabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) {
		super(tabFolder, RELATIVE_RISKS, selectedNode, dynamoSimulationObject, dataBindingContext, helpGroup);
	}

	@Override
	public NestedTab getNestedTab() {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		return new RelativeRiskTab(this.getTabManager().getTabFolder(), RELATIVE_RISK + newTabNumber, modelObject, dataBindingContext, selectedNode, helpGroup);
	}	
	
	@Override
	public String getNestedTabPrefix() {
		return RELATIVE_RISK;
	}
	
	
}