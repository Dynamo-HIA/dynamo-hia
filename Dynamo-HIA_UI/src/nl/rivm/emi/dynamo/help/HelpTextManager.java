package nl.rivm.emi.dynamo.help;

import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

/**
 * @author mondeelr<br/>
 * 
 *         Singleton catering helptexts to the current modal window based on
 *         various events fired by the user interface because of user actions.
 * 
 *         BEWARE: The implementation is fully dependent on the fact, that ONE
 *         modal datawindow can be open at a time. When more windows can be open
 *         a context must be maintained for each open window.
 * 
 */
public class HelpTextManager {
	private static HelpTextManager instance = null;
	private HelpGroup helpGroup;
	/**
	 * For now the helptexts for the lower helpwindow are managed. Focus related
	 * texts(activated by selecting or tabbing) have a low priority (index=0).
	 * Mouse-track related (activated by moving the mouse-pointer over for
	 * instance a button) have a higher priority (index = 1).
	 */
	private String[] stackedFieldHelpTexts = new String[2];

	private HelpTextManager(HelpGroup helpGroup) {
		setHelpGroup(helpGroup);
	}

	/**
	 * Create and initialize the instance for the singleton. When a null
	 * HelpGroup reference is passed no instance is created.
	 * 
	 * @param helpGroup
	 */
	synchronized public static void initialize(HelpGroup helpGroup) {
		if (instance == null) {
			if (helpGroup != null) {
				instance = new HelpTextManager(helpGroup);
			}
		} else {
			if (helpGroup != null) {
				instance.setHelpGroup(helpGroup);
			}
		}
	}

	/**
	 * Sets this HelpGroup as the target for the helptexts.
	 * 
	 * @param helpGroup
	 *            The HelpGroup of the current modal window.
	 */
	private void setHelpGroup(HelpGroup helpGroup) {
		this.helpGroup = helpGroup;
	}

	/**
	 * Get the single instance.
	 * 
	 * @return The properly initialized HelpTextManager or null otherwise.
	 */
	public static HelpTextManager getInstance() {
		return instance;
	}

	/**
	 * Updates the field helptext following a getFocus event.
	 * 
	 * @param text
	 *            The text to put into the field part of the HelpGroup.
	 */
	synchronized public void setFocusText(String text) {
		if (text != null) {
			this.stackedFieldHelpTexts[0] = text;
			if (stackedFieldHelpTexts[1] == null) {
				helpGroup.getFieldHelpGroup().setHelpText(
						stackedFieldHelpTexts[0]);
			}
		}
	}

	/**
	 * Removes the field helptext that was set after the last getFocus event. If
	 * this text was stacked on a previous text, the previous text is put in the
	 * HelpGroup again.
	 * 
	 */
	synchronized public void resetFocusText() {
		this.stackedFieldHelpTexts[0] = null;
		if (!(stackedFieldHelpTexts[1] == null)) {
			helpGroup.getFieldHelpGroup().setHelpText(stackedFieldHelpTexts[1]);
		} else {
			helpGroup.getFieldHelpGroup().setHelpText("");
		}
	}

	/**
	 * Updates the field helptext following a mouseTrack event.
	 * 
	 * @param text
	 *            The text to put into the field part of the HelpGroup.
	 */
	synchronized public void setMouseTrackText(String text) {
		if ((text != null)
				&& (!text.equalsIgnoreCase(stackedFieldHelpTexts[1]))) {
			stackedFieldHelpTexts[1] = text;
			helpGroup.getFieldHelpGroup().setHelpText(stackedFieldHelpTexts[1]);
		}
	}

	/**
	 * Removes the field helptext that was set after the last mouseTrack event.
	 * If this text was stacked on a previous text, the previous text is put in
	 * the HelpGroup again.
	 * 
	 */
	synchronized public void resetMouseTrackText() {
		this.stackedFieldHelpTexts[1] = null;
		if (stackedFieldHelpTexts[0] != null) {
			helpGroup.getFieldHelpGroup().setHelpText(stackedFieldHelpTexts[0]);
		} else {
			helpGroup.getFieldHelpGroup().setHelpText("");
		}
	}
}
