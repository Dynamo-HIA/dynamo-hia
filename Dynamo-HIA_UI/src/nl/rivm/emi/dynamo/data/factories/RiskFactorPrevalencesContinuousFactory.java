package nl.rivm.emi.dynamo.data.factories;

/**
 * Factory to create the categorical, continuous and duration variations.
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.objects.RiskFactorContinuousPrevalencesObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.writers.FileControlSingleton;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * @author mondeelr
 *
 */
public class RiskFactorPrevalencesContinuousFactory extends AgnosticGroupFactory {
	// private Log log = LogFactory.getLog(this.getClass().getName());

	FileControlEnum myEnum = null;

	/**
	 * This constructor has been added to force error-detection in the
	 * FileControlEnum entries.
	 * 
	 * @throws DynamoConfigurationException
	 */
	public RiskFactorPrevalencesContinuousFactory(){
		super();
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufacture(java.io.File, java.lang.String)
	 */
	public RiskFactorContinuousPrevalencesObject manufacture(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, false, rootNodeName);
		return new RiskFactorContinuousPrevalencesObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservable(java.io.File, java.lang.String)
	 */
	public RiskFactorContinuousPrevalencesObject manufactureObservable(File configurationFile,
			String rootNodeName) throws ConfigurationException,
			DynamoInconsistentDataException {
		LinkedHashMap<String, Object> modelMap = super.manufacture(
				configurationFile, true, rootNodeName);
		return new RiskFactorContinuousPrevalencesObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureDefault()
	 */
	public RiskFactorContinuousPrevalencesObject manufactureDefault()
			throws DynamoConfigurationException {
		if(myEnum==null){
			myEnum = FileControlSingleton.getInstance().get(
					RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS.getNodeLabel());
		}
		LinkedHashMap<String, Object> modelMap = super
				.manufactureDefault(myEnum);
		return new RiskFactorContinuousPrevalencesObject(modelMap);
	}

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory#manufactureObservableDefault()
	 */
	public RiskFactorContinuousPrevalencesObject manufactureObservableDefault()
			throws DynamoConfigurationException {
		if(myEnum==null){
			myEnum = FileControlSingleton.getInstance().get(
					RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS.getNodeLabel());
		}
		LinkedHashMap<String, Object> modelMap = super
				.manufactureObservableDefault(myEnum);
		return new RiskFactorContinuousPrevalencesObject(modelMap);
	}
}
