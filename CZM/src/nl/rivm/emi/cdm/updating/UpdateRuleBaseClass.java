package nl.rivm.emi.cdm.updating;

public abstract class UpdateRuleBaseClass {

	int characteristicID = 0;

	int stepSize = 0;
	
	private UpdateRuleBaseClass(){
		super();
	}
	public UpdateRuleBaseClass(int characteristicId, int stepSize){
		this.characteristicID = characteristicId;
		this.stepSize = stepSize;
		
	}
	public int getCharacteristicID() {
		return characteristicID;
	}
	public void setCharacteristicID(int characteristicID) {
		this.characteristicID = characteristicID;
	}
	public int getStepSize() {
		return stepSize;
	}
	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}
	/**
	 * Update the value for a Characteristic that depends only on the 
	 * previous value for the same Characteristic.
	 * Checking depends entirely on the implementer.
	 * @param currentValue
	 * @return
	 */
	abstract public int updateSelf(int currentValue);
}