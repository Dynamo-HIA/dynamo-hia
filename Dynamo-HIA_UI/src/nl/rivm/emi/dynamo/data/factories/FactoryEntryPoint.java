package nl.rivm.emi.dynamo.data.factories;

/**
 * Base Factory for hierarchic configuration files.
 * Current limitations: Simple Object (Integer, Float) at the deepest level.
 * 
 * 20080918 Agestep fixed at 1. Ages are Integers. 
 * 20081111 Implementation from HashMapto LinkedHashMap to preserve ordering of the elements.
 * 20081117 Constructing of IObservables added.
 * 20081120 Made class abstract and external interface protected to force inheritance. 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.PopulationSizeObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.TODO_Object;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;
import nl.rivm.emi.dynamo.data.types.markers.LeafType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

abstract public class FactoryEntryPoint extends ConfigurationObjectBase {
	protected FactoryEntryPoint(RootElementNamesEnum rootElement,
			boolean observable) {
		super(rootElement, observable);
	}

	private Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Abstract method to allow polymorphism.
	 * 
	 * @param configurationFile
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	abstract public ConfigurationObjectBase manufacture(
			String configurationFilePath) throws ConfigurationException,
			DynamoInconsistentDataException;

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param modelObject
	 *            TODO
	 * @param makeObservable
	 *            TODO
	 * @throws DynamoInconsistentDataException
	 */
	protected ConfigurationObjectBase manufacture(
			ConfigurationObjectBase modelObject, String configurationFilePath)
			throws ConfigurationException, DynamoInconsistentDataException {
		synchronized (this) {
			log.debug(" Starting manufacture.");
			File configurationFile = new File(configurationFilePath);
			if (!configurationFile.exists()) {
				modelObject = handleRootChildren(modelObject, null);
			} else {
				if (!configurationFile.isFile()) {
					throw new ConfigurationException("Configurationfile "
							+ configurationFile.getAbsolutePath()
							+ " is not a file.");
				} else {
					if (!configurationFile.canRead()) {
						throw new ConfigurationException("Configurationfile "
								+ configurationFile.getAbsolutePath()
								+ " cannot be read.");
					} else {
						XMLConfiguration configurationFromFile;
						try {
							configurationFromFile = new XMLConfiguration(
									configurationFile);
							ConfigurationNode rootNode = configurationFromFile
									.getRootNode();
							List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) rootNode
									.getChildren();
							modelObject = handleRootChildren(modelObject,
									rootChildren);
							return modelObject;
						} catch (ConfigurationException e) {
							log.error("Caught Exception of type: "
									+ e.getClass().getName()
									+ " with message: " + e.getMessage());
							e.printStackTrace();
							throw e;
						} catch (Exception exception) {
							log.error("Caught Exception of type: "
									+ exception.getClass().getName()
									+ " with message: "
									+ exception.getMessage());
							exception.printStackTrace();
							throw new DynamoInconsistentDataException(
									"Caught Exception of type: "
											+ exception.getClass().getName()
											+ " with message: "
											+ exception.getMessage()
											+ " inside "
											+ this.getClass().getName());
						}
					}
				}
			}
		}
		return modelObject;
	}

	/**
	 * The concrete implementation is typespecific, so a callback to a lower
	 * level is used.
	 * 
	 * @param modelObject
	 * @param makeObservable
	 * @param rootChildren
	 *            The direct children of the rootElement of the
	 *            configurationfile. When null is passed an Object with default
	 *            values must be constructed.
	 * @return
	 * @throws ConfigurationException
	 */
	abstract protected ConfigurationObjectBase handleRootChildren(
			ConfigurationObjectBase modelObject,
			List<ConfigurationNode> rootChildren) throws ConfigurationException;

}
