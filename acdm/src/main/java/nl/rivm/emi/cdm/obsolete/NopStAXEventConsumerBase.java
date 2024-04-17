package nl.rivm.emi.cdm.obsolete;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;

/**
 * Class containing purely example code as a template for functional code.
 * 
 * @author mondeelr
 * 
 */
public class NopStAXEventConsumerBase {
	Log log = LogFactory.getLog(getClass().getName());

	static protected final int START = 0;
	
	static protected final int INMYELEMENT = 5;

	static protected final int INDEX = 10;

	static protected final int VALUE = 20;

	static protected final int FINISHED = 99;

	static protected final int ERROR = 100;

	/**
	 * Event dispatcher. Can be used by derived classes.
	 * 
	 * @param reader
	 * @return
	 * @throws CDMConfigurationException
	 */
	static public Object processMyEvents(XMLEventReader reader)
			throws CDMConfigurationException {
		int state = START;
		Object whatEver = null;
		try {
			XMLEvent event;
			if ((event = reader.peek())!=null) { // Something to do.
//			while ((state != StAXEventConsumerBase.FINISHED)&&(state != StAXEventConsumerBase.ERROR)) {
//					if (event instanceof StartDocument) {
//						whatEver = processStartDocument(reader);
//					} else {
//						if (event instanceof EndDocument) {
//							whatEver = processEndDocument(reader);
//						} else {
//							if (event instanceof StartElement) {
//								whatEver = processStartElement(reader);
//							} else {
//								if (event instanceof EndElement) {
//									whatEver = processEndElement(reader);
//								} else {
//									if (event instanceof Attribute) {
//										whatEver = processAttribute(reader);
//									} else {
//										if (event instanceof Namespace) {
//											whatEver = processNamespace(reader);
//										} else {
//											if (event instanceof EntityReference) {
//												whatEver = processEntityReference(reader);
//											} else {
//												if (event instanceof ProcessingInstruction) {
//													whatEver = processProcessingInstruction(reader);
//												} else {
//													if (event instanceof Characters) {
//														whatEver = processCharacters(reader);
//													} else {
//
//													}
//												}
//											}
//										}
//									}
//								}
//							}
//						}
//					}
				} else {
					throw new CDMConfigurationException(
							"Tried to get beyond last event.");
				}
//			}
		} catch (XMLStreamException e) {
			throw new CDMConfigurationException("Exception: "
					+ e.getClass().getName() + " message: " + e.getMessage());
		} finally {
			return whatEver;
		}
	}

	protected Object processStartDocument(XMLEvent event) {
		log.debug("StartDocument " + ((StartDocument) event));
		return null;
	}

	protected Object processEndDocument(XMLEvent event) {
		log.debug("EndDocument " + ((EndDocument) event));
		return null;
	}

	protected Object processStartElement(XMLEvent event) {
		log.debug("StartElement "
				+ ((StartElement) event).getName().getLocalPart());
		return null;
	}

	protected Object processEndElement(XMLEvent event) {
		log
				.debug("EndElement "
						+ ((EndElement) event).getName().getLocalPart());
		return null;
	}

	protected Object processAttribute(XMLEvent event) {
		log.debug("Attribute " + ((Attribute) event).getName().getLocalPart());
		return null;
	}

	protected Object processNamespace(XMLEvent event) {
		log.debug("NameSpace " + ((Namespace) event).getName().getLocalPart());
		return null;
	}

	protected Object processEntityReference(XMLEvent event) {
		log.debug("EntityReference " + ((EntityReference) event).getName());
		return null;
	}

	protected Object processProcessingInstruction(XMLEvent event) {
		log.debug("ProcessingInstruction " + ((ProcessingInstruction) event));
		return null;
	}

	protected Object processCharacters(XMLEvent event) {
		log.debug("Characters " + ((Characters) event));
		return null;
	}

}
