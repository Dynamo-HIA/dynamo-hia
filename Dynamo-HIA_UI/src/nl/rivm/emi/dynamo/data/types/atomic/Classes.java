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

import nl.rivm.emi.dynamo.data.interfaces.ICategoricalObject;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class Classes extends XMLTagEntity implements IHandlerType {

	public Classes() {
		super("classes");
	}

	public ConfigurationObjectBase handle(ConfigurationObjectBase modelObject,
			ConfigurationNode node)
			throws ConfigurationException {
		ICategoricalObject localModelObject = null;
		if (!(modelObject instanceof ICategoricalObject)) {
			throw new ConfigurationException("Incorrect type of modelObject: "
					+ modelObject.getClass().getName());
		} else {
			localModelObject = (ICategoricalObject) modelObject;
		}
		List<ConfigurationNode> children = node.getChildren();
		for (ConfigurationNode child : children) {
			localModelObject = handleClassTag(localModelObject, child);
		}
		return modelObject;
	}

	private ICategoricalObject handleClassTag(
			ICategoricalObject modelObject, ConfigurationNode node) throws ConfigurationException {
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
			modelObject.putCategory(index, name);
		} else {
			throw new ConfigurationException("Incomplete \"class\" tag.");
		}
		return modelObject;
	}
}