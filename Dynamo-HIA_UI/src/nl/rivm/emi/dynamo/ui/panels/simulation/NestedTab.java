package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 * The nested tab object, basis for all sub tabs
 * 
 * @author schutb
 * 
 */
public abstract class NestedTab extends Tab {

	protected Set<String> selections;
	protected TabPlatform myTabPlatform = null;
	/**
	 * Flag indicating the construction of the NestedTab has succeeded.
	 * Nescessary because the propagation of an Exception has side-effects.
	 */
	protected boolean youCanUseMe = false;

	public NestedTab(Set<String> selections, TabFolder tabFolder,
			String tabName, DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode, HelpGroup helpGroup,
			DataBindingContext dataBindingContext, TabPlatform myTabPlatform)
			throws ConfigurationException {
		super(tabFolder, tabName, dynamoSimulationObject, selectedNode,
				helpGroup, dataBindingContext);
		this.selections = selections;
		this.myTabPlatform = myTabPlatform;
		// Yes, make it here
		try {
			// The RelativeRiskTab does it at its own level.
			if (!(this instanceof RelativeRiskTab)) {
				makeIt();

				// The TabItem can only be created AFTER makeIt()
				TabItem item = new TabItem(tabFolder, SWT.NONE);
				item.setText(tabName);
				item.setControl(this.plotComposite);
				item.addListener(SWT.SELECTED, new Listener() {
					public void handleEvent(Event event) {
						TabItem item = (TabItem) event.item;
						String tabId = item.getText();
						log.fatal("THIS TAB IS SELECTED" + tabId);
					}
				});
				youCanUseMe = true;
			}
		} catch (NoMoreDataException e) {
			displayMessage(tabFolder.getParent().getDisplay(), e.getMessage()
					+ " \nNo new tab is made");
			
			e.printStackTrace();
			
		}
	}

	public boolean isYouCanUseMe() {
		return youCanUseMe;
	}
}
