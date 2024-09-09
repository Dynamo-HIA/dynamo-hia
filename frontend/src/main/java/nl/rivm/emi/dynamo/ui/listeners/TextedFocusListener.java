package nl.rivm.emi.dynamo.ui.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

import nl.rivm.emi.dynamo.ui.help.HelpTextManager;

public class TextedFocusListener implements FocusListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	String myText;

	public TextedFocusListener(String myText) {
		super();
		this.myText = myText;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		String helpText = myText;
		log.debug("focusGained for: " + helpText);
		HelpTextManager.getInstance().setFocusText(helpText);
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		HelpTextManager.getInstance().resetFocusText();
	}

}
