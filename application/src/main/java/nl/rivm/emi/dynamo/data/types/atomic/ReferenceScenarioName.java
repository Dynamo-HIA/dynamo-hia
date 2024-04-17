package nl.rivm.emi.dynamo.data.types.atomic;



import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString2;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.interfaces.PayloadType;
import nl.rivm.emi.dynamo.data.types.interfaces.WrapperType;

public class ReferenceScenarioName extends AbstractString implements PayloadType<String>{
	static final protected String XMLElementName = "refScenarioName";

	public ReferenceScenarioName(){
		super(XMLElementName);
	}

	public WrapperType getNextWrapper() {
		return null;
		
	}

	public String getDefaultValue() {
		return new String("Reference Scenario");
	}
}
