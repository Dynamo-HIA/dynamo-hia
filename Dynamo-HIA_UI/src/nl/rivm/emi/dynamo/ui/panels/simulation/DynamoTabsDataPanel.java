package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DynamoTabsDataPanel {

	protected DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private BaseNode selectedNode;
	
	public DynamoTabsDataPanel(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup) {
		this.myParent = parent;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.selectedNode = selectedNode;
		makeDynamoTabsDisplay(parent);
	}

	
	/**
	 * Create the 5 tabfolders
	 * 
	 */
	public void makeDynamoTabsDisplay(Composite parent)  {

		/* tab for pyramid plots */
		TabFolder tabFolder1 = new TabFolder(parent, SWT.FILL);

		tabFolder1.setLayout(new FillLayout());
		tabFolder1.setBackground(new Color(null, 0x00, 0x00,0x00)); // white		
		
		final RiskFactorTab tab0 = new RiskFactorTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		final PopulationTab tab1 = new PopulationTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		
		tabFolder1.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
                TabItem item = (TabItem) event.item;
	            String tabId=item.getText();
			    if (tabId=="Risk Factor") tab0.redraw();
			    if (tabId=="Population") tab1.redraw();
        }

	    });

	}
	
	
}