package nl.rivm.emi.cdm.characteristic.types;

import java.util.HashMap;

public class CharacteristicTypesContainer extends HashMap<String, Class> {
	/**
	 * Class stuff.
	 */
	private static final long serialVersionUID = 5915327029198478046L;

	static private CharacteristicTypesContainer myInstance = null;

	static synchronized public CharacteristicTypesContainer getInstance() {
		if (myInstance == null) {
			myInstance = new CharacteristicTypesContainer();
		}
		return myInstance;
	}

	/**
	 * Instance stuff.
	 * 
	 */
	private CharacteristicTypesContainer() {
		super();
		put(StringCategoricalCharacteristicType.myTypeLabel,
				StringCategoricalCharacteristicType.class);
		put(IntegerCategoricalCharacteristicType.myTypeLabel,
				IntegerCategoricalCharacteristicType.class);
		put(NumericalContinuousCharacteristicType.myTypeLabel,
				NumericalContinuousCharacteristicType.class);
	}
}
