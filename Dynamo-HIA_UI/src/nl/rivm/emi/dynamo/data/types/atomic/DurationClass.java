package nl.rivm.emi.dynamo.data.types.atomic;


public class DurationClass extends AbstractClassIndex  {
	static final protected String XMLElementName = "durationclass";

	public DurationClass() {
		super(XMLElementName, new Integer(1), hardUpperLimit);
	}

	public Integer getDefaultValue() {
			return new Integer(1);
	}
}
