package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DynamoTabsDataPanel {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	protected DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private BaseNode selectedNode;
	
	public DynamoTabsDataPanel(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup) throws ConfigurationException {
		this.myParent = parent;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.selectedNode = selectedNode;
		makeDynamoTabsDisplay(parent);
	}

	
	/**
	 * Create the 5 tabfolders
	 * @throws ConfigurationException 
	 * 
	 */
	public void makeDynamoTabsDisplay(Composite parent) throws ConfigurationException  {

		log.debug(dynamoSimulationObject + "dynamoSimulationObject");
		
		/* tab for pyramid plots */
		TabFolder tabFolder1 = new TabFolder(parent, SWT.FILL);

		tabFolder1.setLayout(new FillLayout());
		//tabFolder1.setBackground(new Color(null, 0x00, 0x00,0x00)); // white		
		
		final RiskFactorTab tab0 = new RiskFactorTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		final PopulationTab tab1 = new PopulationTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		final DiseasesTab tab2 = new DiseasesTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		final RelativeRisksTab tab3 = new RelativeRisksTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);		
		final ScenariosTab tab4 = new ScenariosTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);			
		
		
		tabFolder1.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
                TabItem item = (TabItem) event.item;
	            String tabId=item.getText();
			    
	            if (tabId=="Risk Factor") tab0.redraw();
			    if (tabId=="Population") tab1.redraw();
			    if (tabId=="Diseases") tab2.redraw();
			    if (tabId=="Relative Risks") tab3.redraw();
			    if (tabId=="Scenarios") tab4.redraw();
			    
        }

	    });

	}
	
	
}
