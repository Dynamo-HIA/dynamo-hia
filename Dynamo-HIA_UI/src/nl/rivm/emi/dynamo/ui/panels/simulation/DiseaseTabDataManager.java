package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Handles the simulation object data actions of the disease tabs
 * 
 * @author schutb
 * 
 */
public class DiseaseTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private DiseasesTabPlatform myTabPlatform;

	public DiseasesTabPlatform getMyTabPlatform() {
		return myTabPlatform;
	}

	private String tabName;
	private DynamoSimulationObject dynamoSimulationObject;

	// private ITabDiseaseConfiguration singleConfiguration;
	private SingleConfigurationManager singConMan;
	// private Set<String> initialSelection;

	/**
	 * Flag to indicate whether the tab is being constructed. When
	 * "initialSelection" is not empty/null the values contained in the
	 * singleConfiguration must be put into the screen.
	 */
	private boolean constructingFlag = true;

	/**
	 * 
	 * Constructor
	 * 
	 * @param selectedNode
	 * @param dynamoSimulationObject
	 * @param initialSelection
	 * @param tabPlatform
	 *            TODO
	 * @param tabName
	 *            TODO
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public DiseaseTabDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> initialSelection, DiseasesTabPlatform tabPlatform,
			String tabName) throws ConfigurationException, NoMoreDataException {
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.myTabPlatform = tabPlatform;
		// this.initialSelection = initialSelection;
		this.tabName = tabName;
		singConMan = new SingleConfigurationManager(selectedNode,
				dynamoSimulationObject, initialSelection, myTabPlatform
						.getChoosableDiseaseNameManager(), tabName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * getRefreshedDropDownSet(java.lang.String)
	 */
	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException, NoMoreDataException {
		return getDropDownSet(label, null);
	}

	/**
	 *
	 */
	public DropDownPropertiesSet getDropDownSet(String name, String chosenName)
			throws ConfigurationException, NoMoreDataException {
		return singConMan.getDropDownSetAndUpdateModel(name, chosenName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getContents
	 * (java.lang.String, java.lang.String)
	 */
	public Set<String> getContents(String fieldName, String chosenDiseaseName)
			throws ConfigurationException, NoMoreDataException {
		return singConMan.getDropDownSetForField(fieldName, chosenDiseaseName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#getCurrentValue
	 * (java.lang.String)
	 */
	public String getValueFromSingleConfiguration(String name)
			throws ConfigurationException {
		return singConMan.getValueFromSingleConfiguration(name);
	}

	/**
	 * 
	 * Updates the object model every time a selection is made
	 * 
	 * @param dropDownName
	 * @param selectedValue
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 */
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException, NoMoreDataException {
		singConMan
				.updateFieldInSingleConfigurationAndUpdateSimulationObjectIfChanged(
						name, selectedValue);
	}

	public void updateDynamoSimulationObject() {
		singConMan.putSingleConfigurationIntoDynamoSimulationObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenl.rivm.emi.dynamo.ui.panels.simulation.DynamoTabDataManager#
	 * removeFromDynamoSimulationObject()
	 */
	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		singConMan
				.removeMyConfigurationFromDynamoSimulationObjectAndMakeChoosable();
	}

	/**
	 * 20091012 mondeelr Methods sets the chosen diseasename to the central
	 * logic. Added the tabname to catch double use of diseasenames.
	 */
	public void setDefaultValue(String dropDownName, String selectedName)
			throws ConfigurationException {
		singConMan
				.putMyConfigurationIntoDynamoSimulationObjectAndRemoveFromChoosableListWhenNew();
	}

	/**
	 * This method is used when the diseaseName for a tab is changed.
	 * 
	 * 
	 * 
	 */
	public void removeOldDefaultValue(String dropDownName)
			throws ConfigurationException {
		singConMan
				.removeMyConfigurationFromDynamoSimulationObjectAndMakeChoosable();
	}

	public WritableValue getCurrentWritableValue(String successRate) {
		// return null;
		throw new RuntimeException("Thou shalt not use this method.");
	}

	@Override
	public DynamoSimulationObject getDynamoSimulationObject() {
		// return this.dynamoSimulationObject;
		throw new RuntimeException("Thou shalt not use this method.");
	}

	public void touchConfigurations() {
		singConMan.touchConfigurations();
	}

	// public ITabDiseaseConfiguration getSingleConfiguration() {
	// return singConMan.singleConfiguration;
	// }
	//
	// public void setConfigurations(
	// Map<String, ITabDiseaseConfiguration> newConfig) {
	//
	// this.dynamoSimulationObject.setDiseaseConfigurations(newConfig);
	// }

	// public void setSingleConfiguration(
	// ITabDiseaseConfiguration singleConfiguration) {
	// singConMan.singleConfiguration = singleConfiguration;
	// }

	// public Map<String, ITabDiseaseConfiguration> getConfigurations() {
	// Map<String, ITabDiseaseConfiguration> configurations = this
	// .getDynamoSimulationObject().getDiseaseConfigurations();
	// return configurations;
	// }

}
