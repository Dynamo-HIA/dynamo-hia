package nl.rivm.emi.dynamo.help;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;

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
		this.helpGroup = helpGroup;
	}

	/**
	 * Create and initialize the instance for the singleton. When a null
	 * HelpGroup reference is passed no instance is created.
	 * 
	 * @param helpGroup
	 */
	synchronized public static void initialize(HelpGroup helpGroup) {
		if ((instance == null) && (helpGroup != null)) {
			instance = new HelpTextManager(helpGroup);
		}
	}

	/**
	 * 
	 * @return The properly initialized HelpTextManager of null otherwise.
	 */
	public static HelpTextManager getInstance() {
		return instance;
	}

	synchronized public void setFocusText(String text) {
		if (text != null){
		this.stackedFieldHelpTexts[0] = text;
		if(stackedFieldHelpTexts[1] == null){
			helpGroup.getFieldHelpGroup().setHelpText(stackedFieldHelpTexts[0] );
		}
		}
	}

	synchronized public void resetFocusText() {
		this.stackedFieldHelpTexts[0] = null;
		if(stackedFieldHelpTexts[1] == null){
			helpGroup.getFieldHelpGroup().setHelpText("");
		}
	}

	synchronized public void setMouseTrackText(String text) {
		if ((text != null)
				&& (!text.equalsIgnoreCase(stackedFieldHelpTexts[1]))) {
			stackedFieldHelpTexts[1] = text;
			helpGroup.getFieldHelpGroup().setHelpText(stackedFieldHelpTexts[1] );
		}
	}

	synchronized public void resetMouseTrackText() {
		this.stackedFieldHelpTexts[1] = null;
		if (stackedFieldHelpTexts[0] != null) {
			helpGroup.getFieldHelpGroup().setHelpText(stackedFieldHelpTexts[0]);
		} else{
			helpGroup.getFieldHelpGroup().setHelpText("");
		}
	}
}
