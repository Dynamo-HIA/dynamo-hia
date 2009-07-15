package nl.rivm.emi.dynamo.ui.listeners;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.TabItem;

public class TabItemFocusListener implements FocusListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	TabItem myTabItem;
	HelpGroup helpGroup;

	public TabItemFocusListener(TabItem myTabItem, HelpGroup helpGroup) {
		super();
		this.myTabItem = myTabItem;
		log.debug("Hooking on button with text: " + myTabItem.getText());
		this.helpGroup = helpGroup;
		log.debug("HelpGroup-\"pointer\": " + helpGroup);
	}

	public void focusGained(FocusEvent arg0) {
		String helpKey = myTabItem.getText();
		helpKey = helpKey.toLowerCase();
		helpKey = helpKey.replace(' ', '_');
		log.debug("focusGained for: " + helpKey + " on button: " + myTabItem);
		helpGroup.getFieldHelpGroup().setHelpText(helpKey);
	}

	public void focusLost(FocusEvent arg0) {
		log.debug("focusLost after: "
				+ helpGroup.getFieldHelpGroup().getHelpKey() + " on button: "
				+ myTabItem);
		helpGroup.getFieldHelpGroup().setHelpText("Blank");
	}
}
