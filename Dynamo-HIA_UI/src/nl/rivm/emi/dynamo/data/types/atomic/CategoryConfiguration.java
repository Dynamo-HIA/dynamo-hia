package nl.rivm.emi.dynamo.data.types.atomic;

/**
 * Handler for 
 * <classes>
 * 	<class>
 * 		<index>1</index>
 * 		<name>jan</name>
 * 	</class>
 * 	.......
 * </classes>
 * XML fragments.
 */
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class CategoryConfiguration extends XMLTagEntity implements IXMLHandlingLayer {
	static final protected String XMLElementName = "class";
Object localModelObject; // TODO Bogus bugfix.
ConfigurationObjectBase modelObject; // TODO Bogus bugfix.
	
	public CategoryConfiguration() {
		super(XMLElementName);
	}

	public ConfigurationObjectBase handle(ConfigurationNode node)
			throws ConfigurationException {
		List<ConfigurationNode> children = node.getChildren();
		for (ConfigurationNode child : children) {
//			localModelObject = handleClassTag(localModelObject, child);
		}
		return modelObject;
	}

	private ICategoricalObject handleClassTag(ConfigurationNode node) throws ConfigurationException {
		if (!"class".equals(node.getName())) {
			throw new ConfigurationException("Incorrect tag \""
					+ node.getName() + "\" found, \"class\" expected.");
		}
		List<ConfigurationNode> children = node.getChildren();
		Integer index = null;
		String name = null;
		for (ConfigurationNode child : children) {
			if ("index".equals(child.getName())) {
				index = Integer.decode((String) child.getValue());
			} else {
				if ("name".equals(child.getName())) {
					name = (String) child.getValue();
				} else {
					throw new ConfigurationException("Incorrect tag \""
							+ child.getName()
							+ "\" found, \"index\" or \"name\" expected.");
				}
			}
		}
		if ((index != null) && (name != null)) {
//			modelObject.putCategory(index, name);
		} else {
			throw new ConfigurationException("Incomplete \"class\" tag.");
		}
		return (ICategoricalObject)modelObject;
	}

	public boolean isConfigurationOK() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setDefault() {
		// TODO Auto-generated method stub
		
	}

	public void streamEvents(Object value, XMLEventWriter writer,
			XMLEventFactory eventFactory) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}
}
