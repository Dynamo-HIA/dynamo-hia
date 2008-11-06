package nl.rivm.emi.dynamo.data.types.atomic;


public enum AtomicTypeEnum {
	AGE((TypeBase)new Age()),
	SEX((TypeBase)new Sex()),
	PERCENTAGE((TypeBase) new Percentage()),
	PROBABILITY((TypeBase)new Probability()),
	STANDARDVALUE((TypeBase)new StandardValue());
	
	private final TypeBase theType;

	private AtomicTypeEnum(TypeBase type) {
		this.theType = type;
	}

	public TypeBase getTheType() {
		return theType;
	}

	public String getElementName() {
		return theType.XMLElementName;
	}
}