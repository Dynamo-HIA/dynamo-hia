package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;

public class TransitionMatrixZero extends RootElementType {
	static final protected String XMLElementName = "transitionmatrix_zero";
	public TransitionMatrixZero() {
		super(XMLElementName);
	}
}
