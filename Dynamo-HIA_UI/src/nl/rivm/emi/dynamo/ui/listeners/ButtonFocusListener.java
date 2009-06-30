package nl.rivm.emi.dynamo.ui.listeners;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;

public class ButtonFocusListener implements FocusListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	Button myButton;
	HelpGroup helpGroup;

	public ButtonFocusListener(Button myButton, HelpGroup helpGroup) {
		super();
		this.myButton = myButton;
		log.debug("Hooking on button with text: " + myButton.getText());
		this.helpGroup = helpGroup;
		log.debug("HelpGroup-\"pointer\": " + helpGroup);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		String helpKey = myButton.getText();
		log.debug("focusGained for: " + helpKey + " on button: " + myButton);
		helpGroup.getFieldHelpGroup().setHelpText(helpKey);
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		log.debug("focusLost after: " + helpGroup.getFieldHelpGroup().getHelpKey() + " on button: " + myButton);
		helpGroup.getFieldHelpGroup().setHelpText("Blank");
	}

}
