package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TabRelativeRiskConfigurationsProxy extends
		LinkedHashMap<Integer, TabRelativeRiskConfigurationData> {
	private static final long serialVersionUID = -40812919647534350L;

	Log log = LogFactory.getLog(this.getClass().getSimpleName());

	DynamoSimulationObject dynamoSimulationObject;
	RelativeRiskTabPlatformDataManager myDataManager;
	/**
	 * Flag for suppressing changes that go through this proxy.
	 * 
	 */
	boolean thisIsNotABackdoor = false;

	public TabRelativeRiskConfigurationsProxy(
			DynamoSimulationObject dynamoSimulationObject,
			RelativeRiskTabPlatformDataManager dataManager) {
		super();
		this.dynamoSimulationObject = dynamoSimulationObject;
		myDataManager = dataManager;
		dynamoSimulationObject.setBackDoorListener(this);
	}

	public TabRelativeRiskConfigurationData get(Integer index) {
		log.debug("Getting configuration at index: " + index);
		return dynamoSimulationObject.getRelativeRiskConfigurations()
				.get(index);
	}

	public TabRelativeRiskConfigurationData update(
			TabRelativeRiskConfigurationData configuration)
			throws ConfigurationException {
		Integer index = configuration.getIndex();
		synchronized (dynamoSimulationObject) {
			if (index == -1) {
				// New configuration, add index.
				Map<Integer, TabRelativeRiskConfigurationData> currentConfigurations = dynamoSimulationObject
						.getRelativeRiskConfigurations();
				int numberOfConfigs = currentConfigurations.size();
				index = numberOfConfigs + 1; // Index is base 1, I think....
				configuration.setIndex(index);
			}
			Map<Integer, TabRelativeRiskConfigurationData> currentConfigurations = dynamoSimulationObject
					.getRelativeRiskConfigurations();
			TabRelativeRiskConfigurationData oldConfiguration = currentConfigurations
					.put(index, configuration);
			if (oldConfiguration != null) {
				log.debug("Updated configuration at index: " + index
						+ " changes: "
						+ oldConfiguration.compareReport(configuration));
			} else {
				log.debug("Added configuration at index: " + index);
			}
			thisIsNotABackdoor = true;
			dynamoSimulationObject
					.setRelativeRiskConfigurations(currentConfigurations);
			thisIsNotABackdoor = false;
//			myDataManager.removeAndRebuildAllTabs();
		}
		return configuration;
	}

	public void remove(TabRelativeRiskConfigurationData configuration)
			throws ConfigurationException {
		if (configuration != null) {
			Integer index = configuration.getIndex();
			synchronized (dynamoSimulationObject) {
				Map<Integer, TabRelativeRiskConfigurationData> currentConfigurations = dynamoSimulationObject
						.getRelativeRiskConfigurations();
				if (currentConfigurations.containsKey(index)) {
					currentConfigurations.remove(index);
					log.debug("Removed configuration from index: " + index);
					// Added by Hendriek: renumber the indexes so that the
					// number are in
					// accord with the
					// numbering of the tabs
					Map<Integer, TabRelativeRiskConfigurationData> newConfigurations = new LinkedHashMap<Integer, TabRelativeRiskConfigurationData>();
					int newIndex = 0;
					for (Integer mapIndex : currentConfigurations.keySet()) {
						TabRelativeRiskConfigurationData singleConfiguration = currentConfigurations
								.get(mapIndex);
						singleConfiguration.setIndex(newIndex);
						newConfigurations.put((Integer) newIndex,
								singleConfiguration);
						newIndex++;
					}
					dynamoSimulationObject.getRelativeRiskConfigurations()
							.clear();
					thisIsNotABackdoor = true;
					dynamoSimulationObject
							.setRelativeRiskConfigurations(newConfigurations);
					thisIsNotABackdoor = false;
					myDataManager.removeAndRebuildAllTabs();
				} else {
					log.debug("Could not remove configuration from index: "
							+ index);
				}
			}
		} else {
			Exception e = new Exception();
			log.error("Null configuration passed for removing! ");
			e.printStackTrace(System.err);
		}
	}

	/*
	 * added by Hendriek Also check if the disease names in the relative risks
	 * are still valid If not remove Both to and from can be diseasenames and
	 * should be checked
	 */
	public void updateDependentRelativeRisks(String removedDisease) {
		Map<Integer, TabRelativeRiskConfigurationData> relRiskConfiguration = dynamoSimulationObject
				.getRelativeRiskConfigurations();

		TabRelativeRiskConfigurationData singleRRconfiguration;

		for (Iterator<TabRelativeRiskConfigurationData> iter = relRiskConfiguration
				.values().iterator(); iter.hasNext();) {

			// for (Integer key2 : relRiskConfiguration.keySet())

			singleRRconfiguration = iter.next();

			if (singleRRconfiguration.getFrom().equals(removedDisease)
					|| singleRRconfiguration.getTo().equals(removedDisease))
				iter.remove();

			log.info("stop5: " + "size: " + relRiskConfiguration.size());
		}
		dynamoSimulationObject
				.setRelativeRiskConfigurations(relRiskConfiguration);
	}


	/**
	 * Just for dropdown generation.
	 */
	public DynamoSimulationObject getDynamoSimulationObject() {
		return dynamoSimulationObject;
	}

	/**
	 * Dump the content of the configurations for debugging.
	 * 
	 * @return
	 */
	public String report() {
		StringBuffer reportBuffer = new StringBuffer(
				"Content of the current RelativeRisk configurations: \n");
		Map map = this.dynamoSimulationObject.getRelativeRiskConfigurations();
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			TabRelativeRiskConfigurationData conf = (TabRelativeRiskConfigurationData) map
					.get(key);
			reportBuffer.append("Index: " + conf.getIndex() + " from: "
					+ conf.getFrom() + " to: " + conf.getTo() + " file: "
					+ conf.getDataFileName() + "\n");
		}
		return reportBuffer.toString();
	}

	public void backdoorUsed() {
		log.debug("Suspected backdoor update, thisIsNotABackdoor = "
				+ thisIsNotABackdoor + " current configuration: " + report());
		if (!thisIsNotABackdoor) {
			myDataManager.removeAndRebuildAllTabs();
		}
	}

	// SuperClass methods, just for information.
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return super.containsValue(value);
	}

	@Override
	public TabRelativeRiskConfigurationData get(Object key) {
		// TODO Auto-generated method stub
		return super.get(key);
	}

	@Override
	protected boolean removeEldestEntry(
			java.util.Map.Entry<Integer, TabRelativeRiskConfigurationData> eldest) {
		// TODO Auto-generated method stub
		return super.removeEldestEntry(eldest);
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return super.containsKey(key);
	}

	@Override
	public Set<java.util.Map.Entry<Integer, TabRelativeRiskConfigurationData>> entrySet() {
		// TODO Auto-generated method stub
		return super.entrySet();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return super.isEmpty();
	}

	@Override
	public Set<Integer> keySet() {
		// TODO Auto-generated method stub
		return super.keySet();
	}

	@Override
	public TabRelativeRiskConfigurationData put(Integer key,
			TabRelativeRiskConfigurationData value) {
		// TODO Auto-generated method stub
		return super.put(key, value);
	}

	@Override
	public void putAll(
			Map<? extends Integer, ? extends TabRelativeRiskConfigurationData> m) {
		// TODO Auto-generated method stub
		super.putAll(m);
	}

	@Override
	public TabRelativeRiskConfigurationData remove(Object key) {
		// TODO Auto-generated method stub
		return super.remove(key);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}

	@Override
	public Collection<TabRelativeRiskConfigurationData> values() {
		// TODO Auto-generated method stub
		return super.values();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
