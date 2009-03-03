package nl.rivm.emi.dynamo.data.objects.xml;
// TODO remove....

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

public class FloatXMLHandler extends
		BaseXMLHandler<Float> {

	public FloatXMLHandler(String xmlElementName) {
		super(xmlElementName, null);
	}

	protected Float convert(String valueString) {
		Float result;
		result = Float.valueOf(valueString);
		return result;
	}

	protected String streamValue(Float value) {
				return value.toString();
	}
}
