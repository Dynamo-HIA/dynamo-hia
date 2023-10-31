package nl.rivm.emi.cdm.data;

/**
 * Class for reading the configuration of groups of lists into a HashMap.
 */

import java.io.FileInputStream;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ConfigurationDefaultHandler extends DefaultHandler {
	Log logger = LogFactory.getLog(getClass().getName());

	final String itemElementName;

	final String firstPropertyElementName;

	final String secondPropertyElementName;

	String physicalBaseDirectory;

	AbstractItemLinkedHashMap resultLHMap;

	FileInputStream iStream;

	Stack<String> qNameStack;

	StringBuffer currentCharacters;

	/**
	 * Stack size when itemElementName is on top.
	 */
	int itemDepth;

	String firstPropertyBuffer = null;

	String secondPropertyBuffer = null;

	public ConfigurationDefaultHandler(String itemElementName,
			String firstPropertyElementName, String secondPropertyElementName,
			String physicalBaseDirectory, AbstractItemLinkedHashMap resultLHMap) {
		super();
		this.itemElementName = itemElementName;
		this.firstPropertyElementName = firstPropertyElementName;
		this.secondPropertyElementName = secondPropertyElementName;
		this.physicalBaseDirectory = physicalBaseDirectory;
		if (resultLHMap == null) {
			logger.error("resultLHMap not initialized!");
		}
		this.resultLHMap = resultLHMap;
		itemDepth = -1;
		qNameStack = new Stack<String>();
	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	// ContentHandler
	public void startDocument() throws SAXException {
		/* Instantiate just in time. */
		logger.debug("startDocument called.");
	}

	public void endDocument() throws SAXException {
		logger.debug("endDocument called.");
	}

	public void startElement(String namespaceURI, String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		logger.debug("startElement() namespaceURI:" + namespaceURI + " lName:"
				+ lName + " qName:" + qName);
		// Set itemDepth only once.
		if ((qNameStack.size() > 0)
				&& (qNameStack.peek().equals(this.itemElementName))
				&& (itemDepth == -1)) {
			itemDepth = qNameStack.size();
		}
		qNameStack.push(qName);
	}

	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) throws SAXException {
		logger.debug("endElement() namespaceURI:" + namespaceURI + " sName:"
				+ sName + " qName:" + qName);
		if (qNameStack.size() == itemDepth + 1) {
			if (firstPropertyElementName.equals(qName)) {
				if (firstPropertyBuffer == null) {
					firstPropertyBuffer = currentCharacters.toString();
					currentCharacters = null;
				} else {
					logger.error("firstProperty " + currentCharacters
							+ " out of sequence!");
				}
			} else {
				if (secondPropertyElementName.equals(qName)) {
					if (secondPropertyBuffer == null) {
						secondPropertyBuffer = currentCharacters.toString();
						currentCharacters = null;
						logger.debug("endElement() Calling resultLHMap.add()");
						resultLHMap.add(firstPropertyBuffer,
								secondPropertyBuffer, physicalBaseDirectory);
						firstPropertyBuffer = null;
						secondPropertyBuffer = null;
					} else {
						logger.error("secondProperty " + currentCharacters
								+ " out of sequence!");
					}
				}
			}
		}
		if (qNameStack.size() > 0) {
			qNameStack.pop();
		}
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		String content = (new String(buf, offset, len)).trim();
		logger.debug("characters() content:>" + content + "<");
		if( qNameStack.size() == itemDepth + 1){
			logger.debug("adding content:>" + content + "< to currentCharacters.");
		if (currentCharacters != null) {
			currentCharacters = currentCharacters.append(content);
		} else {
			currentCharacters = new StringBuffer(content);
		}
		} else {
			currentCharacters = null;
		}


	}

	/**
	 * DTDHandler
	 */
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		logger.debug("notationDecl called.");
	}

	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) {
		logger.debug("unparsedEntityDecl called.");
	}

	/**
	 * ErrorHandler
	 */
	public void error(SAXParseException exception) {
		logger.debug("error called. Exception.message: "
				+ exception.getMessage());
	}

	public void fatalError(SAXParseException exception) {
		logger.debug("fatalError called. Exception.message: "
				+ exception.getMessage());
	}

	public void warning(SAXParseException exception) {
		logger.debug("warning called. Exception.message: "
				+ exception.getMessage());
	}
}
