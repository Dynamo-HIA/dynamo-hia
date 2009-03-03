package nl.rivm.emi.dynamo.data.objects.xml;

// TODO remove.
public class StringXMLHandler extends
		BaseXMLHandler<String> {

	public StringXMLHandler(String xmlElementName) {
		super(xmlElementName, null);
	}

	  protected String convert(String valueString) {
		String result;
		result = valueString;
		return result;
	}

	protected String streamValue(String value) {
				return value;
	}
}
