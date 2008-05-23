package nl.rivm.emi.cdm.prngutil;

import javax.xml.parsers.ParserConfigurationException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

public class DOMRNGSeedWriter {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.characteristic.CharacteristicWriter");

	public static void generateDOM( Long seedValue, Element parentElement)
			throws ParserConfigurationException {
		String elementName = "rngseed"; // characteristicValue.getElementName()
		Element element = parentElement.getOwnerDocument().createElement(
				elementName);
		element.setAttribute("vl", seedValue.toString());
		parentElement.appendChild(element);
	}
}
