package nl.rivm.emi.dynamo.data.types.root;

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class RelativeRisksCluster extends XMLTagEntity implements WrapperType{
	static final protected String XMLElementName = "relativerisks_diseaseondisease";

	public RelativeRisksCluster(){
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return null;
	}
}
