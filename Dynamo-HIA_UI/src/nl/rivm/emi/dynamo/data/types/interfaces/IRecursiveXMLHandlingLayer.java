package nl.rivm.emi.dynamo.data.types.interfaces;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * A Class implementing this interface can handle part of the configuration.
 * 
 * @author mondeelr
 */
public interface IRecursiveXMLHandlingLayer<T> {
	
	public TypedHashMap<T> handle(TypedHashMap<T> originalObject,
			ConfigurationNode rootChild, boolean makeObservable)
			throws ConfigurationException ;

	public TypedHashMap<T> manufactureDefault(LeafNodeList leafNodeList,
			boolean makeObservable) throws ConfigurationException;
	
//	abstract T handle(ConfigurationNode node)
//			throws ConfigurationException;
//
//	abstract void setDefault();
//
	public void streamEvents(T value, XMLEventWriter writer, XMLEventFactory eventFactory)
			throws XMLStreamException;
//
//	abstract public boolean isConfigurationOK();
}
