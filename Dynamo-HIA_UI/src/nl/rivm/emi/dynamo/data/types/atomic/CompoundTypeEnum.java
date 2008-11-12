package nl.rivm.emi.dynamo.data.types.atomic;


public enum CompoundTypeEnum {
	XYZ((CompoundTypeBase)new Xyz()),
	ABC((CompoundTypeBase)new Abc());
	
	private final CompoundTypeBase theType;

	private CompoundTypeEnum(CompoundTypeBase type) {
		this.theType = type;
	}

	public CompoundTypeBase getTheType() {
		return theType;
	}

	public String[] getElementNames() {
		return theType.getElementNames();
	}
}