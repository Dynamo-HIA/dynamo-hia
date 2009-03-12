package nl.rivm.emi.dynamo.data.types.atomic;


public class ReferenceClass extends AbstractClassIndex {
	static final protected String XMLElementName = "referenceclass";

	static final protected Integer hardUpperLimit = new Integer(9);

	public ReferenceClass(){
		super(XMLElementName, new Integer(1), hardUpperLimit);
	}
}
