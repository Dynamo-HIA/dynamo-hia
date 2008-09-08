package nl.rivm.emi.cdm.prngutil;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

public class DOMRNGSeedWriter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.characteristic.CharacteristicWriter");

	public static void generateDOM(Long seedValue, Element parentElement)
			throws ParserConfigurationException {
		String elementName = "rngseed"; // characteristicValue.getElementName()
		Element element = parentElement.getOwnerDocument().createElement(
				elementName);
		String seedString = "null";
		if (seedValue != null) {
			seedString = seedValue.toString();
		}
		element.setAttribute("vl", seedString);
		parentElement.appendChild(element);
	}
}
