package nl.rivm.emi.cdm.rules.update;


import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;

/**
 * Update rule for incrementing the age Characteristic. Has been made specific
 * to that characteristic.
 * 
 * @author mondeelr
 * 
 */
public class SexOneToOneUpdateRule extends OneToOneUpdateRuleBase implements
		CharacteristicSpecific {

	private int characteristicId;

	/**
	 * Default constructor for loading via classname.
	 */
	public SexOneToOneUpdateRule() {
		super();
	}

	public SexOneToOneUpdateRule(int characteristicId) {
		super();
		setCharacteristicId(characteristicId);
	}

	public int getCharacteristicId() {
		return this.characteristicId;
	}

	public void setCharacteristicId(int characteristicId) {
		this.characteristicId = characteristicId;
	}

	public Integer update(Object currentValue) throws CDMUpdateRuleException {
		if (currentValue instanceof Integer) {
			return (Integer) currentValue;
		} else {
			throw new CDMUpdateRuleException(String.format(
					CDMUpdateRuleException.wrongParameterMessage, currentValue
							.getClass().getSimpleName()));
		}
	}
}
