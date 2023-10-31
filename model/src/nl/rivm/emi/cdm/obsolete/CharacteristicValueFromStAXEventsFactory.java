package nl.rivm.emi.cdm.obsolete;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.exceptions.CDMRunException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CharacteristicValueFromStAXEventsFactory extends NopStAXEventConsumerBase {
	Log log = LogFactory.getLog(getClass().getName());

	HashMap<String, String> attributeMap = new HashMap();

	public CharacteristicValueFromStAXEventsFactory() {
//		super("ch");
	}

	@Override
	protected Object processStartElement(XMLEvent event) {
		IntCharacteristicValue icv = null;
		try {
			StartElement startElement = (StartElement) event;
			String localName = startElement.getName().getLocalPart();
			log.debug("StartElement " + localName);
//			if (getXmlElementName().equalsIgnoreCase(localName)) {
				Iterator iterator = startElement.getAttributes();
				int attributeCount = 0;
				while (iterator.hasNext() && !(attributeCount == 2)) {
					Attribute attribute = (Attribute) iterator.next();
					String name = attribute.getName().getLocalPart();
					String value = attribute.getValue();
					if ("id".equalsIgnoreCase(name)) {
						attributeMap.put(name, value);
						attributeCount++;
					} else {
						if ("vl".equalsIgnoreCase(name)) {
							attributeMap.put(name, value);
							attributeCount++;
						} else {
							log.debug("Unexpected attribute " + name
									+ " with value " + value);
//							state = ERROR;
						}
					}
				}
				int index = Integer.decode(attributeMap.get("id"));
				int value = Integer.decode(attributeMap.get("vl"));
				icv = new IntCharacteristicValue(1, index);
				icv.appendValue(value);
//				state = StAXEventConsumerBase.FINISHED;
//			}
		} catch (CDMRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return icv;
		}

	}

	@Override
	protected Object processEndElement(XMLEvent event) {
		EndElement endElement = (EndElement) event;
		String localName = endElement.getName().getLocalPart();
		log.debug("EndElement " + localName);
//		if (getXmlElementName().equalsIgnoreCase(localName)) {
//			state = FINISHED;
//		}
		return null;
	}

	@Override
	protected Object processAttribute(XMLEvent event) {
		log.debug("Attribute");
		return null;
	}

	@Override
	protected Object processCharacters(XMLEvent event) {
		log.debug("Characters");
		return null;
	}
}
