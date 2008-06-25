package nl.rivm.emi.cdm;
/**
 * Abstract base-<code>Class</code> offering forward navigation to the same tags on the same level
 *  in a DOM-tree.
 */
import org.w3c.dom.Node;

public abstract class XMLConfiguredObjectFactory {

	/**
	 * Name of the tag that represents the DataContainer in configuration
	 * XML-files.
	 * 
	 */
	String elementName = null;
/**
 * Constructor.
 * @param elementName The elementname this Factory-Base-
 * <code>Class</code> uses.
 */
	public XMLConfiguredObjectFactory(String elementName) {
		super();
		this.elementName = elementName;
	}

/**
 *  @author mondeelr 
 *  @param The elementName in the XML-file that corresponds to this Object.
 */
	protected void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 *  @author mondeelr 
	 *  @return The elementName in the XML-file that corresponds to this Object.
	 */
	protected String getElementName() {
		return elementName;
	}

	/**
	 * Search the DOM-tree horizontally for elements with name cooresponding to
	 *  the elementName configured in this <code>Class</code>.
	 * @param node <code>Node</code> where the search starts. 
	 * @return Next <code>Node</code> with the configured elementname 
	 *              in the DOM-tree. Null if none is found.              
	 */
	protected Node findMyNodeAtThisLevel(Node node) {
		Node nextNode = node;
		Node foundNode = null;
		while ((nextNode != null) && (foundNode == null)) {
			if ((elementName != null)
					&& (elementName.equals(nextNode.getNodeName()))) {
				foundNode = nextNode;
			}
			nextNode = nextNode.getNextSibling(); 
		}
		return foundNode;
	}
	
	/**
	 * Search the DOM-tree horizontally for elements with name cooresponding to
	 *  the elementName configured in this <code>Class</code>. This method skips
	 *  the <code>Node</code> passed as a parameter.
	 * @param node <code>Node</code> where the search starts. 
	 * @return Next <code>Node</code> with the configured elementname 
	 *              in the DOM-tree. Null if none is found.              
	 */
	protected Node findMyNextNodeAtThisLevel(Node node) {
		Node nextNode = node.getNextSibling(); 
		return findMyNodeAtThisLevel(nextNode);
	}
}
