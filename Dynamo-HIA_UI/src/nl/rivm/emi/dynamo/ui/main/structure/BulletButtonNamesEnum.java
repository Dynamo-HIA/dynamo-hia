package nl.rivm.emi.dynamo.ui.main.structure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Enum containing all possible  bullet button names in the DYNAMO-HIA configuration
 * files: InputBulletsFreeXMLFileAction, InputBulletsTrialog
 * 
 * @author mondeelr
 * 
 */

public enum BulletButtonNamesEnum {

	ZERO("Zero", null),
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
