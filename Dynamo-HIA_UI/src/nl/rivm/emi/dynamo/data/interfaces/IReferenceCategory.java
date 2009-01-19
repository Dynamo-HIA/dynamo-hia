package nl.rivm.emi.dynamo.data.interfaces;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

public interface IReferenceCategory {

	public abstract Object putReferenceCategory(Integer index);

	public abstract Integer getReferenceCategory();
}