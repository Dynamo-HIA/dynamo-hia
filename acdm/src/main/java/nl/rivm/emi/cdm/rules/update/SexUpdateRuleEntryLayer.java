package nl.rivm.emi.cdm.rules.update;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.ManyToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.NeedsSeed;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation, must be generified later to UpdateRuleEntryLayer.
 * 
 * @author mondeelr
 * 
 */
public class SexUpdateRuleEntryLayer extends ManyToOneUpdateRuleBase implements
		ConfigurationEntryPoint, NeedsSeed {

	Log log = LogFactory.getLog(this.getClass().getName());

	private final int SEXINDEX = 1;

	private final int SPININDEX = 3;

	// TreeMap<Integer, SpinAsLeafUpdateRuleLayer> delegatesMap = null;

	SpinAsLeafUpdateRuleLayer[] delegates = null;

	//@Override
	/*
	public Object update(Object[] currentValues) {
		Object newValue = null;
		if (currentValues[SEXINDEX] instanceof Integer) {
			int currentIndex = ((Integer) currentValues[SEXINDEX]).intValue();
			newValue = delegates[currentIndex].update(currentValues[SPININDEX]);
		}
		return newValue;
	}*/

	@SuppressWarnings("unused")
	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		try {
		
		
			boolean success = false;
			XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
					configurationFile);
			
			// Validate the xml by xsd schema
			// WORKAROUND: clear() is put after the constructor (also calls load()). 
			// The config cannot be loaded twice,
			// because the contents will be doubled.
			configurationFileConfiguration.clear();
			
			// Validate the xml by xsd schema
			configurationFileConfiguration.setValidating(true);			
			configurationFileConfiguration.load();
			
			@SuppressWarnings("unchecked")
			List<SubnodeConfiguration> snConf = configurationFileConfiguration
					.configurationsAt("transitionmatrix");
			if ((snConf == null) || (snConf.isEmpty() || (snConf.size() > 1))) {
				throw new ConfigurationException(
						String
								.format(
										CDMConfigurationException.invalidUpdateRuleConfigurationFileFormatMessage,
										configurationFile.getName(), this
												.getClass().getSimpleName()));
			}
			log.info("Handling " + snConf.get(0).getRootNode().getName()
					+ " at level " + 1);
			Object temp = handleLevel(snConf.get(0), 1);
			return (delegates != null);		
		} catch (ConfigurationException e) {
			ErrorMessageUtil.handleErrorMessage(log, e.getMessage(), e, configurationFile.getAbsolutePath());
		}		
		return (delegates != null);
	}

	
	private Object handleLevel(SubnodeConfiguration snConf, int levelNumber) {
		Object resultObject = null;
		@SuppressWarnings("unchecked")
		List<SubnodeConfiguration> levelConfs = snConf.configurationsAt("level"
				+ levelNumber);
		if (!levelConfs.isEmpty()) {
			// TreeMap<Integer, Object> levelMap = new TreeMap<Integer,
			// Object>();
			TreeMap<Integer, SpinAsLeafUpdateRuleLayer> levelMap = new TreeMap<Integer, SpinAsLeafUpdateRuleLayer>();
			int inLevelCount = 1;
			for (SubnodeConfiguration levelConf : levelConfs) {
				log.info("Handling " + levelConf.getRootNode().getName()
						+ " at level " + (levelNumber + 1));
				SpinAsLeafUpdateRuleLayer deeperResult = new SpinAsLeafUpdateRuleLayer();
				((SpinAsLeafUpdateRuleLayer) deeperResult).handleFirstLevel(
						levelConf, levelNumber + 1);
				levelMap.put(inLevelCount, deeperResult);
				inLevelCount++;
			}
			delegates = new SpinAsLeafUpdateRuleLayer[levelMap.size() + 1];
			for (int count = 1; count <= levelMap.size(); count++) {
				delegates[count] = levelMap.get(count);
			}
		}
		return resultObject;
	}

	/**
	 * Testing only.
	 * 
	 * @param daTree
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String dumpTreeMapTree(TreeMap daTree) {
		StringBuffer result = new StringBuffer();
		for (int count = 1; count <= daTree.size(); count++) {
			Object configNode = daTree.get(count);
			if (configNode instanceof TreeMap) {
				result.append(count + " >> "
						+ dumpTreeMapTree((TreeMap) configNode));
			} else {
				if (configNode instanceof Float) {
					result.append(count + " >> " + configNode + " <<\n");
				}
			}
		}
		return result.toString();
	}

	/**
	 * TODO How to make sure the same seed isn't used twice.
	 */
	public long setAndNextSeed(long seed) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object update(Object[] currentValues, Long seed)
			throws CDMUpdateRuleException, CDMUpdateRuleException {
		Object newValue = null;
		if (currentValues[SEXINDEX] instanceof Integer) {
			int currentIndex = ((Integer) currentValues[SEXINDEX]).intValue();
			newValue = delegates[currentIndex].update(currentValues[SPININDEX]);
		}
		return newValue;
	}



}
