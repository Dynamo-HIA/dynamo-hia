package nl.rivm.emi.dynamo.data.types.atomic.base;

/**
 * @author mondeelr
 * 
 * <br/>
 *         Provides the functionality nescessary for XML element support.
 * 
 */
public abstract class XMLTagEntity {
	final protected String XMLElementName;

	/**
	 * Binds the instance to an XML elementname.<br/>
	 * Each elementname should only appear once, because otherwise eclipsing may
	 * occur when the Objects are put in a Map by elementname.
	 * 
	 * @param tagName
	 */
	protected XMLTagEntity(String tagName) {
		XMLElementName = tagName;
	}

	/**
	 * 
	 * @return The name of the supported XML element.
	 */
	public String getXMLElementName() {
		return XMLElementName;
	}
}
