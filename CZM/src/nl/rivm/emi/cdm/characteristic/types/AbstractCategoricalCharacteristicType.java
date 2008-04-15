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
	abstract public ArrayList getPossibleValues();

	abstract public Integer getNumberOfPossibleValues();

	public boolean isCategoricalType() {
		return true;
	}

	@Override
	abstract public String humanReadableReport();
}
