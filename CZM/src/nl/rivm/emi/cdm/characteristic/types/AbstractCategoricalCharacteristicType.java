package nl.rivm.emi.cdm.characteristic.types;


abstract public class AbstractCategoricalCharacteristicType extends
		AbstractCharacteristicType {

	protected AbstractCategoricalCharacteristicType(String type) {
		super(type);
	}


	abstract public boolean addPossibleValue(Object value);
	
	abstract public Object getValue(int index); 

	abstract public Integer getNumberOfPossibleValues(); 

	public boolean isCategoricalType(){
		return true; 
	}
}
