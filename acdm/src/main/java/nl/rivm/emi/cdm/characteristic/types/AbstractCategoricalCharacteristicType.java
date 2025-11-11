package nl.rivm.emi.cdm.characteristic.types;

import java.util.ArrayList;

abstract public class AbstractCategoricalCharacteristicType extends
		AbstractCharacteristicType {

	protected AbstractCategoricalCharacteristicType(String type) {
		super(type);
	}

	abstract public boolean addPossibleValue(Object value);
	abstract public Object getPossibleValue(int index);
	
// Cannot be overridden correctly.
//	abstract public ArrayList<Object> getPossibleValues();
	@SuppressWarnings("rawtypes")
	abstract public ArrayList getPossibleValues();

	abstract public Integer getNumberOfPossibleValues();

	public boolean isCategoricalType() {
		return true;
	}
	
/* added by hendriek */
	public boolean isCompoundType() {
		return false;
	}

	@Override
	abstract public String humanReadableReport();
}
