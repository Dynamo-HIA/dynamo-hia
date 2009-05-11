package nl.rivm.emi.dynamo.data.objects.xml;
// TODO remove.

public class IntegerXMLHandler extends
		BaseXMLHandler<Integer> {


	public IntegerXMLHandler(String xmlElementName) {
		super(xmlElementName, null);
	}
	
	  protected  Integer convert(String valueString) {
		Integer result;
		result = Integer.valueOf(valueString);
		return result;
	}


	protected String streamValue(Integer value) {
				return value.toString();
	}
}
