package nl.rivm.emi.dynamo.ui.listeners;

import nl.rivm.emi.dynamo.help.HelpTextManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Button;

public class ButtonMouseTrackListener implements MouseTrackListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	// Button myButton;
	String helpKey;

	public ButtonMouseTrackListener(Button myButton) {
		super();
		helpKey = myButton.getText();
		log.debug("Hooking on button with text: " + myButton.getText());
		// this.helpGroup = helpGroup;
		// log.debug("HelpGroup-\"pointer\": " + helpGroup);
	}

	@Override
	public void mouseEnter(MouseEvent arg0) {
		// String helpKey = myButton.getText();
		helpKey = helpKey.toLowerCase();
		helpKey = helpKey.replace(' ', '_');
		// log.debug("mouseEnter for: " + helpKey + " on button: " + myButton);
		HelpTextManager instance = HelpTextManager.getInstance();
		// Make robust for helpless screens.
		if (instance != null) {
			instance.setMouseTrackText(helpKey);
		}
	}

	@Override
	public void mouseExit(MouseEvent arg0) {
		// log.debug("mouseExit after: "
		// + helpGroup.getFieldHelpGroup().getHelpKey() + " on button: "
		// + myButton);
		HelpTextManager instance = HelpTextManager.getInstance();
		// Make robust for helpless screens.
		if (instance != null) {
			instance.resetMouseTrackText();
		}
	}

	@Override
	public void mouseHover(MouseEvent arg0) {
	}
}
