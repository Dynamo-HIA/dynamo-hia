package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class Tab {

	private Log log = LogFactory.getLog(this.getClass().getName());

	protected String tabName;
	protected Composite plotComposite;
	private DynamoSimulationObject dynamoSimulationObject;
	protected DataBindingContext dataBindingContext;
	protected HelpGroup helpGroup;
	protected BaseNode selectedNode;
	protected Set<String> selections;

	public Tab(Set<String> selections, TabFolder tabFolder, String tabName,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		this.selections = selections;
		this.setDynamoSimulationObject(dynamoSimulationObject);
		this.dataBindingContext = dataBindingContext;
		this.helpGroup = helpGroup;
		this.selectedNode = selectedNode;
		this.tabName = tabName;

		this.setLayoutStyle(tabFolder);
		log.debug("Tab::this.plotComposite: " + this.plotComposite);

		// Yes, make it here
		try {
			makeIt();

			// The TabItem can only be created AFTER makeIt()
			TabItem item = new TabItem(tabFolder, SWT.NONE);
			item.setText(tabName);
			item.setControl(this.plotComposite);
			item.addListener(SWT.SELECTED, new Listener() {
				public void handleEvent(Event event) {
					TabItem item = (TabItem) event.item;
					String tabId = item.getText();
					log.debug("THIS TAB IS SELECTED" + tabId);
				}
			});
		} catch (NoMoreDataException e) {
			displayMessage(tabFolder.getParent().getDisplay(), e.getMessage()
					+ " \nNo new tab is made");
			
			e.printStackTrace();
		}
	}

	protected void setLayoutStyle(Composite parent) {
		this.plotComposite = new Composite(parent, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		this.plotComposite.setLayout(formLayout);
		// this.plotComposite.setBackground(new Color(null, 0xbb, 0xbb,0xbb));
	}

	protected abstract void makeIt() throws DynamoConfigurationException,
			ConfigurationException, NoMoreDataException;

	/**
	 * 
	 */
	public void redraw() {
		this.plotComposite.redraw();
	}

	public String getName() {
		return tabName;
	}

	public void displayMessage(Display display, String message) {

		Shell messageShell = new Shell(display);
		MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
		messageBox.setMessage(message);
		messageShell.open();

		if (messageBox.open() == SWT.OK) {
			messageShell.dispose();
		}

	}

	public void setDynamoSimulationObject(DynamoSimulationObject dynamoSimulationObject) {
		this.dynamoSimulationObject = dynamoSimulationObject;
	}

	public DynamoSimulationObject getDynamoSimulationObject() {
		return dynamoSimulationObject;
	}

}
