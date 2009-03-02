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
import nl.rivm.emi.dynamo.data.interfaces.IDurationClass;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class DurationClass extends Index  {

	public DurationClass() {
		super("durationclass", new Integer(1), hardUpperLimit);
	}

	public ConfigurationObjectBase handle(ConfigurationObjectBase modelObject,
			ConfigurationNode node)
			throws ConfigurationException {
		if (!(modelObject instanceof IDurationClass)) {
			throw new ConfigurationException("Incorrect type of modelObject: "
					+ modelObject.getClass().getName());
		}
		Integer index = null;
		if (getXMLElementName().equals(node.getName())) {
			index = Integer.decode((String) node.getValue());
		} else {
			throw new ConfigurationException("Incorrect tag \""
					+ node.getName() + "\" found, \"" + XMLElementName
					+ "\" expected.");
		}
		if (index != null) {
			((IDurationClass) modelObject).putDurationClass(index);
		} else {
			throw new ConfigurationException("Incorrect \"" + XMLElementName + "\" tag.");
		}
		return modelObject;
	}
}
