package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class Tab {

	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private String tabName;
	protected Composite plotComposite;
	protected DynamoSimulationObject dynamoSimulationObject;
	protected DataBindingContext dataBindingContext;
	protected HelpGroup helpGroup;
	protected BaseNode selectedNode;
	
	public Tab(TabFolder tabFolder, String tabName, 
			DynamoSimulationObject dynamoSimulationObject, 
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode, HelpGroup helpGroup) throws DynamoConfigurationException {
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.helpGroup = helpGroup;
		this.selectedNode = selectedNode;
		this.tabName = tabName;
		this.setLayoutStyle(tabFolder);
		log.debug("Tab::this.plotComposite: " + this.plotComposite);	
		
		// Yes, make it here
		makeIt();
		
		// The TabItem can only be created after makeIt()
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(tabName);
		item.setControl(this.plotComposite);
	}
	
	protected void setLayoutStyle(Composite parent) {
		this.plotComposite = new Group(parent, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		this.plotComposite.setLayout(formLayout);
		//this.plotComposite.setBackground(new Color(null, 0xbb, 0xbb,0xbb));		
	}

	protected abstract void makeIt() throws DynamoConfigurationException;

	/**
	 * 
	 */
	public void redraw(){
		this.plotComposite.redraw();		
	}

	public String getName() {
		return tabName;
	}
	
}