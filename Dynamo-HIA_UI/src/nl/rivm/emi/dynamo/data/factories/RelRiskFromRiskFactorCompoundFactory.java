package nl.rivm.emi.dynamo.data.factories;

import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.objects.RelRiskFromRiskFactorCompoundObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Begin;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.types.atomic.End;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.data.util.LeafNodeList;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr
 *
 */
public class RelRiskFromRiskFactorCompoundFactory extends
		AgnosticCategoricalFactory {
	private Log log = LogFactory.getLog(this.getClass().getName());

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservable(java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufactureObservable(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, true,
				rootElementName);
		return new RelRiskFromRiskFactorCompoundObject(producedMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufacture(java.io.File, java.lang.String)
	 */
	public TypedHashMap<Age> manufacture(File configurationFile,
			String rootElementName) throws ConfigurationException,
			DynamoInconsistentDataException {
		log.debug("Starting manufacture.");
		TypedHashMap<Age> producedMap = manufacture(configurationFile, false,
				rootElementName);
		RelRiskFromRiskFactorCompoundObject result = new RelRiskFromRiskFactorCompoundObject(
				producedMap);
		return (result);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureDefault()
	 */
	@Override
	public TypedHashMap<Age> manufactureDefault() throws ConfigurationException {
		return manufactureDefault(false);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticFactory#manufactureObservableDefault()
	 */
	@Override
	public TypedHashMap<Age> manufactureObservableDefault()
			throws ConfigurationException {
		return manufactureDefault(true);
	}

	/**
	 * Manufactures a new Object from scratch. The AtomicTypeObjectTuples in the
	 * leafNodeList determine the structure and defaultvalues.
	 * 
	 * @param makeObservable
	 *            Flag indicating the Object must contain WritableValues at the
	 *            lowest level.
	 * @return The manufactured default Object.
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private RelRiskFromRiskFactorCompoundObject manufactureDefault(
			boolean makeObservable) throws ConfigurationException {
		log.debug("Starting manufacture.");
		LeafNodeList leafNodeList = new LeafNodeList();
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.AGE
				.getTheType(), null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.SEX
				.getTheType(), null));
		// XMLTagEntitySingleton xmlTagEntitySingleton =
		// XMLTagEntitySingleton.getInstance();
		CatContainer category = (CatContainer) XMLTagEntityEnum.CAT
				.getTheType();
		// TODO Clone to make threadsafe. Category clone = category.
		@SuppressWarnings("unused")
		Integer oldMaxValue = category.setMAX_VALUE(numberOfCategories);
		log.debug("Set Category. MAX_VALUE to: " + numberOfCategories);
		leafNodeList.add(new AtomicTypeObjectTuple(category, null));
		// leafNodeList.add(new
		// AtomicTypeObjectTuple(XMLTagEntityEnum.BEGIN.getTheType(), null));
		Begin customBegin = new Begin();
		customBegin.setDefaultValue(1F);
		leafNodeList.add(new AtomicTypeObjectTuple(customBegin, null));
		leafNodeList.add(new AtomicTypeObjectTuple(XMLTagEntityEnum.ALFA
				.getTheType(), null));
		// leafNodeList.add(new
		// AtomicTypeObjectTuple(XMLTagEntityEnum.END.getTheType(), null));
		End customEnd = new End();
		customEnd.setDefaultValue(1F);
		leafNodeList.add(new AtomicTypeObjectTuple(customEnd, null));
		TypedHashMap<Age> manufacturedMap = super.manufactureDefault(
				leafNodeList, makeObservable);
		RelRiskFromRiskFactorCompoundObject result = new RelRiskFromRiskFactorCompoundObject(
				manufacturedMap);
		return result;
	}

}
