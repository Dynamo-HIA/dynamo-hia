package nl.rivm.emi.dynamo.data.types.atomic.base;

/**
 * @author mondeelr
 * 
 * Provides the functionality nescessary for XML element support.
 * 
*/
public abstract class XMLTagEntity{
	final protected String XMLElementName;
	/**
	 * Binds the instance to an XML element.
	 * 
	 * @param tagName
	 */
	protected XMLTagEntity(String tagName) {
		XMLElementName = tagName;
	}

//	public boolean isMyElement(String elementName) {
//		boolean result = true;
//		if (!XMLElementName.equalsIgnoreCase(elementName)) {
//			result = false;
//		}
//		return result;
//	}

	/**
	 *  
	 * @return the name of the bound XML element.
	 */
	public String getXMLElementName() {
		return XMLElementName;
	}
}
