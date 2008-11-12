package nl.rivm.emi.dynamo.data.types.atomic;

public class Abc extends CompoundTypeBase {

	protected Abc() {
		super(new String[]{"A","B","C"});
	}
	
	protected Abc(String[] tagNames) {
		super(tagNames);
		// TODO Auto-generated constructor stub
	}

}
