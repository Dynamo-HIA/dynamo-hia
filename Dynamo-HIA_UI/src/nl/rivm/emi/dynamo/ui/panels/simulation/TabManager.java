package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

public class TabManager {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private Map<String, NestedTab> nestedTabs = 
		new LinkedHashMap<String, NestedTab>();
	
	private TabFolder tabFolder = null;

	private SelectionListener selectionListener;
	
	public TabManager(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup) {
/*
		Composite plotComposite = new Group(parent, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		plotComposite.setLayout(formLayout);
		plotComposite.setBackground(new Color(null, 0x00, 0x00,0x00)); //White
	*/	
		// Create the tabFolder
		//this.tabFolder = new TabFolder(plotComposite, SWT.FILL);
		this.tabFolder = new TabFolder(parent, SWT.FILL);
		this.tabFolder.setLayout(new FillLayout());
		this.tabFolder.setBackground(new Color(null, 0x00, 0x00,0x00)); // white		
		
		final PopulationTab tab1 = new PopulationTab(tabFolder, dynamoSimulationObject, 
				dataBindingContext, selectedNode, helpGroup);
		/*
		tabFolder.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
                TabItem item = (TabItem) event.item;
	            String tabId=item.getText();
			    
			    if (tabId=="Population") tab1.redraw();        }

	    }); */
		
		// Initially, there are no nested tabs
		this.selectionListener = new SelectionListener(nestedTabs);		
	}

	// tabName is identifier for the nestedTab to be created
	public void createNestedTab(NestedTab nestedTab) {
		// Create an unique number
		int tabNumber = this.nestedTabs.size() + 1;
		// Create a new NestedTab on the TabPlatform		
		//this.nestedTabs.put(nestedTab.getName() + tabNumber, nestedTab);
		this.nestedTabs.put(tabNumber + "", nestedTab);
		this.updateListener();
		// Redraw the tabPlatform
		this.redraw();
	}
	
	public void deleteNestedTab(int tabNumber) {
		Control[] controls = tabFolder.getTabList();
		tabFolder.changed(controls);
		tabFolder.update();
		// Remove the nestedTab from the TabPlatform		
		this.nestedTabs.remove(tabNumber + "");
		this.updateListener();
		// Redraw the tabPlatform
		this.redraw();
	}
	
	public void redraw() {
		this.tabFolder.redraw();
	}	
	
	public void updateListener() {
		log.debug("HALLO IK BEN HIER");
		log.debug("Tab Size: " + this.nestedTabs.size());
		tabFolder.removeListener(SWT.Selection, this.selectionListener);		
		tabFolder.addListener(SWT.Selection, new SelectionListener(nestedTabs));
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public int getNumberOfTabs() {
		return this.nestedTabs.size();
	}
	
}
