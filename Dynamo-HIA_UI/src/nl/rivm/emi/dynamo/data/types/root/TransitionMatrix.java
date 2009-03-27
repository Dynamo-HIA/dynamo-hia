package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class TransitionMatrix extends RootElementType {
	static final protected String XMLElementName = "transitionmatrix";
	public TransitionMatrix() {
		super(XMLElementName);
	}
}
