package nl.rivm.emi.cdm;

import org.w3c.dom.Node;

public abstract class XMLConfiguredObjectFactory {

	/**
	 * Name of the tag that represents the DataContainer in configuration
	 * XML-files.
	 * 
	 */
	String elementName = null;

	public XMLConfiguredObjectFactory(String elementName) {
		super();
		this.elementName = elementName;
	}

	/**
	 * The elementName in the XML-file that corresponds to this Object.
	 */
	void setElementName(String elementName) {
		this.elementName = elementName;
	}

	String getElementName() {
		return elementName;
	}

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
}
