package nl.rivm.emi.dynamo.data.types.atomic.base;


public abstract class XMLTagEntity{
	final protected String XMLElementName;
	/**
	 * 
	 * @param tagName
	 * @param aValue is needed to be able to return the type.
	 */
	protected XMLTagEntity(String tagName) {
		XMLElementName = tagName;
	}

	public boolean isMyElement(String elementName) {
		boolean result = true;
		if (!XMLElementName.equalsIgnoreCase(elementName)) {
			result = false;
		}
		return result;
	}

	public String getXMLElementName() {
		return XMLElementName;
	}
}
