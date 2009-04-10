package nl.rivm.emi.dynamo.ui.panels.simulation;

public enum DynamoTabGroupComboEnums {
	DALY_WEIGHTS("DALY Weights", 1);
	

	private final String name;

	private final int value;
	
	private DynamoTabGroupComboEnums(String name, int value) {
		this.name = name;
		this.value = value;
	}

	/*
	public static String getName() {
		return name;
	}*/

	public static DynamoTabGroupComboEnums getValueByName(String name) {
		return DynamoTabGroupComboEnums.DALY_WEIGHTS;
	}

}
