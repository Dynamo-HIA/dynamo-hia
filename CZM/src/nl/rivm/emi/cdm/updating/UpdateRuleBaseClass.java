package nl.rivm.emi.cdm.updating;

/**
 * BaseClass for updaterules, providing storage for the index of the
 * Characteristic the UpdateRule is meant to update and the stepsize the
 * updaterule pertains to.
 * 
 * @author mondeelr
 * 
 */
public abstract class UpdateRuleBaseClass {
	/**
	 * The characteristic-id's start from 1 (one). ID's lower than 1 result in a
	 * non-useable update-rule.
	 */
	int characteristicID = 0;

	/**
	 * A stepsize of -1 means the rule goes for all stepsizes.
	 * TODO QUESTION What should the step timebase be? (day, month....) 
	 */
	int stepSize = 0;

	/**
	 * The signature of an update-rule indicates which Characteristics are
	 * nescessary input for the rule. If a Characteristic is input, the boolean
	 * at its Id should be set to "true", otherwise it should be set to
	 * false.
	 * When, at some future point in time the amount of Characteristics is greater than the signature caters for, non-configured Characteristics are considered to be configured with "false".
	 * TODO Maybe an automatic conversion could be implemented.
	 */
	Signature signature;

	private UpdateRuleBaseClass() {
		super();
	}

	public UpdateRuleBaseClass(int characteristicId, int stepSize) {
		this.characteristicID = characteristicId;
		this.stepSize = stepSize;

	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
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
	 * Update the value for a Characteristic that depends only on the previous
	 * value for the same Characteristic. Checking depends entirely on the
	 * implementer.
	 * 
	 * @param currentValue
	 * @return
	 */
	abstract public int updateSelf(int currentValue);
}