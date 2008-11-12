package nl.rivm.emi.dynamo.data.types.atomic;


public enum AtomicTypeEnum {
	AGE((AtomicTypeBase)new Age()),
	SEX((AtomicTypeBase)new Sex()),
	PERCENTAGE((AtomicTypeBase) new Percentage()),
	PROBABILITY((AtomicTypeBase)new Probability()),
	STANDARDVALUE((AtomicTypeBase)new Value());
	
	private final AtomicTypeBase theType;

	private AtomicTypeEnum(AtomicTypeBase type) {
		this.theType = type;
	}

	public AtomicTypeBase getTheType() {
		return theType;
	}

	public String getElementName() {
		return theType.XMLElementName;
	}
}