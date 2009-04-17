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
import java.util.List;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TransitionDriftNettoFactoryImplementation implements
		RootLevelFactory {
	protected Log log = LogFactory.getLog(this.getClass().getName());

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
	public TransitionDriftNettoObject manufacture(File configurationFile,
			boolean makeObservable, String rootElementName)
			throws ConfigurationException, DynamoInconsistentDataException {
		TransitionDriftNettoObject underConstruction = new TransitionDriftNettoObject();
		XMLConfiguration configurationFromFile;
		try {
			configurationFromFile = new XMLConfiguration(configurationFile);

			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls
			// load()).
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFromFile.clear();

			// Validate the xml by xsd schema
			configurationFromFile.setValidating(true);
			configurationFromFile.load();

			ConfigurationNode rootNode = configurationFromFile.getRootNode();

			// Check if the name of the first element of the file
			// is the same as that of the node name where the file is processes
			if (rootNode.getName() != null
					&& rootNode.getName().equalsIgnoreCase(rootElementName)) {
				List<?> list = rootNode.getChildren();
				List<ConfigurationNode> rootChildren = (List<ConfigurationNode>) list;
				for (ConfigurationNode rootChild : rootChildren) {
					log.info("Handle rootChild: " + rootChild.getName());
					underConstruction = handleRootChild(underConstruction,
							rootChild, makeObservable);
				} // for rootChildren
			} else {
				// The start/first element of the imported file does not match
				// the node name
				throw new DynamoInconsistentDataException(
						"The contents of the imported file does not match the node name");
			}
			return underConstruction;
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(this.log, e.getMessage(), e,
					configurationFile.getAbsolutePath());
			return underConstruction;
		}
	}

	/**
	 * Currently each rootchild contains a group of XML-elements (leafnodes) of
	 * which all but the last must be "container" datatypes. The result of
	 * successfull processing is the addition of a single Object (wrapped in a
	 * WritableValue when makeObservable is true) contained in a number of
	 * levels of TypedHashMap.
	 * 
	 * @param originalObject
	 * @param rootChild
	 * @param makeObservable
	 * @return
	 * @throws ConfigurationException
	 */
	private TransitionDriftNettoObject handleRootChild(
			TransitionDriftNettoObject originalObject,
			ConfigurationNode rootChild, boolean makeObservable)
			throws ConfigurationException {
		AtomicTypeBase<Float> expectedType = (AtomicTypeBase<Float>) XMLTagEntityEnum.TREND
				.getTheType();
		String rootChildName = rootChild.getName();
		log.debug("Handling rootchild: " + rootChildName);
		if (expectedType.getXMLElementName().equals(rootChildName)) {
			Float nakedModelValue = (Float) expectedType
					.convert4Model((String) rootChild.getValue());
			if (!makeObservable) {
				originalObject.setTrend(nakedModelValue);
			} else {
				WritableValue wrappedModelObject = new WritableValue(
						nakedModelValue, expectedType);
				originalObject.setObservableTrend(wrappedModelObject);
			}
		} else {

		}
		return originalObject;
	}

	public TransitionDriftNettoObject manufactureDefault(boolean makeObservable) throws DynamoConfigurationException {
		TransitionDriftNettoObject defaultObject = new TransitionDriftNettoObject();
		Float defaultValue = ((AtomicTypeBase<Float>) XMLTagEntityEnum.TREND
				.getTheType()).getDefaultValue();
		if (!makeObservable) {
			defaultObject.setTrend(defaultValue);
		} else {
			WritableValue wrappedModelObject = new WritableValue(
					defaultValue,Float.class);
			defaultObject.setObservableTrend(wrappedModelObject);
		}
		return defaultObject;
	}
}
