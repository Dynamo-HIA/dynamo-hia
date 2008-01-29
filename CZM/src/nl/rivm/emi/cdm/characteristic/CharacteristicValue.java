package nl.rivm.emi.cdm.characteristic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharacteristicValue {
	Log log = LogFactory.getLog(getClass().getName());

	Integer value;

	public CharacteristicValue(Integer value) {
		this.value = value; // TODO refactor
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(String value) {
		try {
			this.value = Integer.decode(value);
		} catch (NumberFormatException e) {
			this.value = new Integer(-1);
		}
	}
}
