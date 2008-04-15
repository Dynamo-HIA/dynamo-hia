package nl.rivm.emi.cdm.characteristic.values;

import nl.rivm.emi.cdm.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract public class CharacteristicValueBase {
	Log log = LogFactory.getLog(getClass().getName());

	String elementName;

	int index;

	private CharacteristicValueBase() {
	}

	CharacteristicValueBase(String elementName, int index) {
		this.elementName = elementName;
		this.index = index;
	}

	public String getElementName() {
		return elementName;
	}

	public int getIndex() {
		return index;
	}

	abstract public Object getValue(int step);

	abstract public Object getCurrentValue() throws CDMRunException;

}
