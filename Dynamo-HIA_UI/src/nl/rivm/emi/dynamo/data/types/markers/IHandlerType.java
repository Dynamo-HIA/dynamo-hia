package nl.rivm.emi.dynamo.data.types.markers;

import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * The atomic types used in Dynamo-HIA are divided into ContainerTypes (that are
 * marked by this marker-interface) and LeafType-s that do not have this
 * interface.
 * 
 * @author mondeelr
 */
public interface IHandlerType {
	abstract ConfigurationObjectBase handle(
			ConfigurationObjectBase modelObject, ConfigurationNode node) throws ConfigurationException;

}
