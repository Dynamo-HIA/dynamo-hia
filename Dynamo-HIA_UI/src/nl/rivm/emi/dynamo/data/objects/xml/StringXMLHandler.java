package nl.rivm.emi.dynamo.data.objects.xml;

/**
 * Handler for 
 * <classes>
 * 	<class>
 * 		<index>1</index>
 * 		<name>jan</name>
 * 	</class>
 * 	.......
 * </classes>
 * XML fragments.
 */

public class StringXMLHandler extends
		BaseXMLHandler<String> {

	public StringXMLHandler(String xmlElementName) {
		super(xmlElementName);
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
