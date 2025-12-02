package nl.rivm.emi.dynamo.data.factories.rootchild;

import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * @author mondeelr
 * 
 *         Factory for a single RootChild-configuration node.
 * 
 */
public class AgnosticSingleRootChildFactory implements RootChildFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Manufactures a modelobject-part based on the passed node of the
	 * configurationfile.
	 * 
	 * @param node
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public AtomicTypeObjectTuple manufacture(ConfigurationNode node)
			throws ConfigurationException, DynamoInconsistentDataException {
		return manufacture(node, false);
	}

	/**
	 * Manufactures a default modelobject-part based on the passed type.
	 * 
	 * @param type
	 * @return
	 * @throws DynamoInconsistentDataException
	 */
	public AtomicTypeObjectTuple manufactureDefault(AtomicTypeBase<?> type)
			throws DynamoInconsistentDataException {
		return manufactureDefault(type, false);
	}

	/**
	 * Manufactures a modelobject-part that can be used for databinding based on
	 * the passed node of the configurationfile.
	 * 
	 * @param node
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public AtomicTypeObjectTuple manufactureObservable(ConfigurationNode node)
			throws ConfigurationException, DynamoInconsistentDataException {
		return manufacture(node, true);
	}

	/**
	 * Manufactures a default modelobject-part that can be used for databinding
	 * based on the passed type.
	 * 
	 * @param type
	 * @return
	 * @throws DynamoInconsistentDataException
	 */
	public AtomicTypeObjectTuple manufactureObservableDefault(
			AtomicTypeBase<?> type) throws DynamoInconsistentDataException {
		return manufactureDefault(type, true);
	}

	/**
	 * Precondition is that a dispatcher has chosen this factory based on the
	 * root-tagname.
	 * 
	 * @param makeObservable
	 * 
	 * @return TypedHashMap HashMap that contains the data of the given file and
	 *         the type of the data
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	private AtomicTypeObjectTuple manufacture(ConfigurationNode node,
			boolean makeObservable) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Processing node \"" + node.getName() + "\" with value \""
				+ node.getValue() + "\"");
		AtomicTypeObjectTuple underConstruction = null;
		String nodeName = node.getName();
		Object nodeValueObject = node.getValue();
		if (nodeValueObject instanceof String) {
			String nodeValueString = (String) nodeValueObject;
			AtomicTypeBase<?> nodeType = (AtomicTypeBase<?>) XMLTagEntitySingleton
					.getInstance().get(nodeName);
			if (nodeType != null) {
				Object nodeModelValue = nodeType.convert4Model(nodeValueString);
				if (nodeModelValue != null) {
					underConstruction = createTuple(nodeType, nodeModelValue,
							makeObservable);
				} else {
					throw new DynamoInconsistentDataException(
							"Could not convert for node \"" + nodeName
									+ "\" with value \"" + node.getValue()
									+ "\"");
				}
			} else {
				throw new DynamoInconsistentDataException(
						"Could not find type for node \"" + nodeName + "\"");
			}
		} else {
			throw new DynamoInconsistentDataException("Node \"" + nodeName
					+ "\" does not contain the required String, but a \""
					+ nodeValueObject.getClass().getName() + "\".");
		}
		return underConstruction;
	}

	/**
	 * Creates an "empty" Object with the default value produced by the type in
	 * the object.
	 * 
	 * @param type
	 * @param makeObservable
	 * @return
	 * @throws DynamoInconsistentDataException
	 */
	protected AtomicTypeObjectTuple manufactureDefault(AtomicTypeBase<?> type,
			boolean makeObservable) throws DynamoInconsistentDataException {
		Object defaultValueObject = type.getDefaultValue();
		if (defaultValueObject == null) {
			throw new DynamoInconsistentDataException("Type \""
					+ type.getClass().getName()
					+ "\" returned a null default value.");
		}
		String defaultValueString = type.convert4View(defaultValueObject);
		if (defaultValueString == null) {
			throw new DynamoInconsistentDataException("Type \""
					+ type.getClass().getName()
					+ "\" could not convert its own default value.");
		}
		AtomicTypeObjectTuple underConstruction = createTuple(type,
				defaultValueObject, makeObservable);
		return underConstruction;
	}

	/**
	 * Construct the tuple with or without an observable as required.
	 * 
	 * @param nodeType
	 * @param nodeModelValue
	 * @param makeObservable
	 * @return
	 */
	private AtomicTypeObjectTuple createTuple(AtomicTypeBase<?> nodeType,
			Object nodeModelValue, boolean makeObservable) {
		AtomicTypeObjectTuple underConstruction;
		if (!makeObservable) {
			underConstruction = new AtomicTypeObjectTuple(nodeType,
					nodeModelValue);
		} else {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			WritableValue observableNodeModelValue = new WritableValue(
					nodeModelValue, nodeModelValue.getClass());
			underConstruction = new AtomicTypeObjectTuple(nodeType,
					observableNodeModelValue);
		}
		return underConstruction;
	}
}
