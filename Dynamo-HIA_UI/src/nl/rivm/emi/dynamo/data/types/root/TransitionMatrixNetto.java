package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class TransitionMatrixNetto extends RootElementType {
	static final protected String XMLElementName = "transitionmatrix_netto";
	public TransitionMatrixNetto() {
		super(XMLElementName);
	}
}
