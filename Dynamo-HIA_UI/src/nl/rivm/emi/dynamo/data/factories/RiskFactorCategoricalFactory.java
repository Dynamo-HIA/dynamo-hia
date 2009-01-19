package nl.rivm.emi.dynamo.data.factories;

import java.io.File;
import java.util.List;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.DiseaseIncidencesObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.layers.ConfigurationObjectBase;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.markers.IHandlerType;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RiskFactorCategoricalFactory extends FactoryEntryPoint {
	protected RiskFactorCategoricalFactory(RootElementNamesEnum rootElement,
			boolean observable) {
		super(rootElement, observable);
		// TODO Auto-generated constructor stub
	}

	private Log log = LogFactory.getLog(this.getClass().getName());

	public RiskFactorCategoricalObject manufacture(
			RiskFactorCategoricalObject modelObject, File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		modelObject = (RiskFactorCategoricalObject) manufacture(modelObject,
				configurationFile);
		return modelObject;
	}

	protected ConfigurationObjectBase handleRootChildren(
			ConfigurationObjectBase modelObject,
			List<ConfigurationNode> rootChildren) throws ConfigurationException {
		for (ConfigurationNode rootChild : rootChildren) {
			String childName = rootChild.getName();
			log.debug("Handle rootChild: " + childName);
			XMLTagEntity entity = XMLTagEntitySingleton.getInstance().get(
					childName);
			if ((entity != null) && (entity instanceof IHandlerType)) {
				modelObject = ((IHandlerType) entity).handle(modelObject,
						rootChild);
			}
		}
		return modelObject;
	}

	@Override
	public ConfigurationObjectBase manufacture(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationObjectBase manufactureDefault()
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationObjectBase manufactureObservable(File configurationFile)
			throws ConfigurationException, DynamoInconsistentDataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationObjectBase manufactureObservableDefault()
			throws ConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

}
