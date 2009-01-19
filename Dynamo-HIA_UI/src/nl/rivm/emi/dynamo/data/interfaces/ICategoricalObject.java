package nl.rivm.emi.dynamo.data.interfaces;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

public interface ICategoricalObject {

	public abstract Object putCategory(Integer index, String name);

	public abstract String getCategoryName(Integer index);

	public abstract int getNumberOfCategories();
}