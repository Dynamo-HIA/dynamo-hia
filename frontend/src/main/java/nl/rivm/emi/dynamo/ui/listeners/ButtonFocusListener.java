package nl.rivm.emi.dynamo.ui.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;

import nl.rivm.emi.dynamo.ui.help.HelpTextManager;

/**
 * 
 * Cannot generalize the type to Control, because Control has no getText()....
 * 
 * @author mondeelr
 * 
 */
public class ButtonFocusListener implements FocusListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	Button myButton;

	// HelpGroup helpGroup;

	public ButtonFocusListener(Button myButton) {
		super();
		this.myButton = myButton;
		// log.debug("Hooking on button with text: " + myButton.getText());
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		String helpKey = myButton.getText();
		helpKey = helpKey.toLowerCase();
		helpKey = helpKey.replace(' ', '_');
		log.debug("focusGained for: " + helpKey + " on button: " + myButton);
		HelpTextManager instance = HelpTextManager.getInstance();
		// Make robust for helpless screens.
		if (instance != null) {
			instance.setFocusText(helpKey);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// log.debug("focusLost after: "
		// + helpGroup.getFieldHelpGroup().getHelpKey() + " on button: "
		// + myButton);
		// helpGroup.getFieldHelpGroup().setHelpText("Blank");
		HelpTextManager instance = HelpTextManager.getInstance();
		// Make robust for helpless screens.
		if (instance != null) {
			instance.resetFocusText();
		}
	}

}
