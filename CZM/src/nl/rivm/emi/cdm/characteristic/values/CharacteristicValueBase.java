package nl.rivm.emi.cdm.characteristic.values;

import nl.rivm.emi.cdm.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

abstract public class CharacteristicValueBase {
	Log log = LogFactory.getLog(getClass().getName());

	static String xmlElementName;

	int index;
	
	private CharacteristicValueBase() {
	}

	CharacteristicValueBase(String elementName, int index) {
		this.xmlElementName = elementName;
		this.index = index;
	}

	public String getElementName() {
		return xmlElementName;
	}

	public int getIndex() {
		return index;
	}

	abstract public Object getValue(int step);

	abstract public Object getCurrentValue() throws CDMRunException;
	/* added by hendriek as this is needed for getting a not yet updated value 
	 */
	abstract public Object getPreviousValue() throws CDMRunException;
	
	 


}
