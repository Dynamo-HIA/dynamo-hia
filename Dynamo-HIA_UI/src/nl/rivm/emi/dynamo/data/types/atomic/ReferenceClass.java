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

import nl.rivm.emi.dynamo.data.interfaces.IReferenceCategory;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.objects.layers.ReferenceCategoryObjectImplementation;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class ReferenceClass extends XMLTagEntity implements IHandlerType {

	public ReferenceClass() {
		super("referenceclass");
	}

	public ConfigurationObjectBase handle(ConfigurationObjectBase modelObject,
			ConfigurationNode node)
			throws ConfigurationException {
//		if (!(modelObject instanceof ReferenceCategoryObjectImplementation)) {
//			throw new ConfigurationException("Incorrect type of modelObject: "
//					+ modelObject.getClass().getName());
//		}
		Integer index = null;
		if (XMLElementName.equals(node.getName())) {
			index = Integer.decode((String) node.getValue());
		} else {
			throw new ConfigurationException("Incorrect tag \""
					+ node.getName() + "\" found, \"" + XMLElementName
					+ "\" expected.");
		}

		if (index != null) {
			((IReferenceCategory) modelObject).putReferenceCategory(index);
		} else {
			throw new ConfigurationException("Incorrect \"" + XMLElementName + "\" tag.");
		}
		return modelObject;
	}
}
