package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;

public class TabRelativeRiskConfigurationsLinkedHashMap extends
		LinkedHashMap<Integer, TabRelativeRiskConfigurationData> {
	private static final long serialVersionUID = -40812919647534350L;

	Log log = LogFactory.getLog(this.getClass().getName());

	DynamoSimulationObject dynamoSimulationObject;

	public TabRelativeRiskConfigurationsLinkedHashMap(
			DynamoSimulationObject dynamoSimulationObject) {
		super();
		this.dynamoSimulationObject = dynamoSimulationObject;
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
			dynamoSimulationObject
					.setRelativeRiskConfigurations(currentConfigurations);
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
					dynamoSimulationObject
							.setRelativeRiskConfigurations(newConfigurations);
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