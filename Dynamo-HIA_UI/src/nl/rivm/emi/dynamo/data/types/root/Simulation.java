package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class Simulation extends RootElementType {
	static final protected String XMLElementName = "simulation";
	public Simulation() {
		super(XMLElementName);
	}
}
