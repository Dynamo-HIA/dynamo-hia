package nl.rivm.emi.dynamo.ui.listeners;

import nl.rivm.emi.dynamo.help.HelpTextManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;

public class TextedMouseTrackListener implements MouseTrackListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	String myText;

	public TextedMouseTrackListener(String myText) {
		super();
		this.myText = myText;
	}

	@Override
	public void mouseEnter(MouseEvent arg0) {
		String helpText = myText;
		log.debug("focusGained for: " + helpText);
		HelpTextManager.getInstance().setMouseTrackText(helpText);
	}

	@Override
	public void mouseExit(MouseEvent arg0) {
		HelpTextManager.getInstance().resetMouseTrackText();
	}

	@Override
	public void mouseHover(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
