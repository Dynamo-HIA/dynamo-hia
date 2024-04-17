package nl.rivm.emi.cdm.obsolete;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PopulationFromStAXEventsFactory extends NopStAXEventConsumerBase{

	Log log = LogFactory.getLog(getClass().getName());

	public PopulationFromStAXEventsFactory() {
//		super("pop");
	}
//	
//	public Object processMyEvents(XMLEventReader reader) {
//		Population population = null;
//		try {
//			// TODO Refactor some more.
//			while (reader.hasNext()) {
//				XMLEvent event = reader.nextEvent();
//				if (event.isStartElement()) {
//					StartElement element = (StartElement) event;
//					System.out.println("Start Element: " + element.getName());
//					Iterator iterator = element.getAttributes();
//					while (iterator.hasNext()) {
//						Attribute attribute = (Attribute) iterator.next();
//						QName name = attribute.getName();
//						String value = attribute.getValue();
//						System.out.println("Attribute name/value: " + name
//								+ "/" + value);
//					}
//				}
//				if (event.isEndElement()) {
//					EndElement element = (EndElement) event;
//					System.out.println("End element:" + element.getName());
//				}
//				if (event.isCharacters()) {
//					Characters characters = (Characters) event;
//					System.out.println("Text: " + characters.getData());
//				}
//			}
//		} catch (XMLStreamException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return (Object)population;
//	}
}
