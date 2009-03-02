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

public class IntegerXMLHandler extends
		BaseXMLHandler<Integer> {


	public IntegerXMLHandler(String xmlElementName) {
		super(xmlElementName);
	}
	
	  protected  Integer convert(String valueString) {
		Integer result;
		result = Integer.decode(valueString);
		return result;
	}


	protected String streamValue(Integer value) {
				return value.toString();
	}
}
