/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class RiskFactorTab {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private DynamoSimulationObject dynamoSimulationObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;
	
	private TabFolder tabFolder;
	private Set<String> selections;
	private Composite plotComposite;

	
	private DynamoTabDataManager dynamoTabDataManager;
	
	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public RiskFactorTab(
			TabFolder tabfolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		
		this.tabFolder = tabfolder;
		this.dataBindingContext = dataBindingContext; 
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.helpGroup = helpGroup;
		this.selectedNode = selectedNode;
		
		this.selections = new LinkedHashSet<String>();
		
		Set<String> defaultTabKeyValues = this.getConfigurations();
		for (String defaultTabKeyValue : defaultTabKeyValues) {
			Set<String> keyValues = new LinkedHashSet<String>();
			this.selections.add(defaultTabKeyValue);
		}
		
		makeIt();
		

	}

	private Set<String> getConfigurations() {
		LinkedHashMap<String, TabRiskFactorConfigurationData> configurations = 
			(LinkedHashMap<String, TabRiskFactorConfigurationData>) this.dynamoSimulationObject.getRiskFactorConfigurations();
		return configurations.keySet();
	}

	/**
	 * makes the tabfolder
	 * @throws ConfigurationException 
	 */
	public void makeIt() throws ConfigurationException{
		this.plotComposite = new Group(this.tabFolder, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		this.plotComposite.setLayout(formLayout);
		//this.plotComposite.setBackground(new Color(null, 0xff, 0xff,0xff)); //White
		
		this.dynamoTabDataManager =
			new RiskFactorTabDataManager(selectedNode, 
					dynamoSimulationObject,
					this.selections);
		
		RiskFactorSelectionGroup riskFactorSelectionGroup =
			new RiskFactorSelectionGroup( 
					this.selections, this.plotComposite,
					selectedNode, helpGroup,
					dynamoTabDataManager
					);
		
		RiskFactorResultGroup riskFactorResultGroup =
			new RiskFactorResultGroup(this.selections, this.plotComposite,
					selectedNode, helpGroup,
					riskFactorSelectionGroup.group,
					riskFactorSelectionGroup.getDropDownModifyListener(), 
					dynamoTabDataManager);

		TabItem item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("Risk Factor");
		item.setControl(this.plotComposite);		
	}
	
	/**
	 * Redraws the tab component 
	 */
	public void redraw(){
		log.debug("REDRAW THIS");
		this.plotComposite.redraw();
	}	
	
}