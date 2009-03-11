package nl.rivm.emi.dynamo.ui.main.structure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Enum containing all possible  bullet button names in the dynamo-HIA configuration
 * files.
 * 
 * This Enum was created later on during development and has not been factored
 * in pervasively. All literal references to bullet button names should eventually
 * be changed to run through this Enum. This will greatly decrease the
 * vulnerability to refactoring resulting in inconsistent names across the codebase.
 * 
 * @author mondeelr
 * 
 */

public enum BulletButtonNamesEnum {

	ZERO("Zero", null), // Comment to block reformatting.
	USER_SPECIFIED("User specified", null), //
	NETTO("Netto", null);

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	private String bulletButtonName;
	private String displayName;

	private BulletButtonNamesEnum(String bulletButtonName, String theDisplayName) {
		this.bulletButtonName = bulletButtonName;
		this.displayName = theDisplayName;
	}

	public String getBulletButtonName() {
		return bulletButtonName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
