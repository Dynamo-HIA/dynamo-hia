package nl.rivm.emi.dynamo.data.types.markers;

import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * A Class implementing this interface can handle part of the configuration.
 * 
 * @author mondeelr
 */
public interface IHandlerType {
	abstract ConfigurationObjectBase handle(
			ConfigurationObjectBase modelObject, ConfigurationNode node) throws ConfigurationException;

}
