package nl.rivm.emi.cdm.rules.update;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.prngcollection.MersenneTwister;
import nl.rivm.emi.cdm.rules.update.base.ConfigurationEntryPoint;
import nl.rivm.emi.cdm.rules.update.base.NeedsSeed;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

public class UpdateRuleIntermediateLayer extends OneToOneUpdateRuleBase implements
		ConfigurationEntryPoint, NeedsSeed {

	public TreeMap transitionConfiguration = null; // TODO remove public,

	final int maxRandInt = 100000;

	int[][] transitionLevels;

	// TODO Implement
	Random prnGenerator = new MersenneTwister();

	public long setAndNextSeed(long seed) {
		prnGenerator.setSeed(seed);
		return prnGenerator.nextLong();
	}

	@Override
	public Object update(Object currentValue) {
		int newValue = 0;
		if (currentValue instanceof Integer) {
			int currentIndex = ((Integer) currentValue).intValue();
			int randomValue = prnGenerator.nextInt(maxRandInt);
			for (int count = 1; count < transitionLevels[currentIndex].length; count++) {
				if (randomValue < transitionLevels[currentIndex][count]) {
					newValue = count;
					break;
				}
			}
		}
		return newValue;
	}

	public boolean loadConfigurationFile(File configurationFile)
			throws ConfigurationException {
		boolean success = false;
		XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
				configurationFile);
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
		Object temp = handleLevel(snConf.get(0), 1);
		if (temp instanceof TreeMap) {
			if (consistencyCheck((TreeMap) temp, true, null)) {
				transitionConfiguration = (TreeMap) temp;
			}
		}
		return (transitionConfiguration != null);
	}

	private Object handleLevel(SubnodeConfiguration snConf, int levelNumber) {
		Object resultObject = null;
		List<SubnodeConfiguration> levelConfs = snConf.configurationsAt("level"
				+ levelNumber);
		if (!levelConfs.isEmpty()) {
			TreeMap<Integer, Object> levelMap = new TreeMap<Integer, Object>();
			int inLevelCount = 1;
			for (SubnodeConfiguration levelConf : levelConfs) {
				Object deeperResult = handleLevel(levelConf, levelNumber + 1);
				levelMap.put(inLevelCount, deeperResult);
				inLevelCount++;
			}
			resultObject = levelMap;
		} else {
			resultObject = handleLeaf(snConf);
		}
		return resultObject;
	}

	public Float handleLeaf(SubnodeConfiguration snConf) {
		Object leafValueObject = snConf.getRootNode().getValue();
		Float leafValueFloat = null;
		if (leafValueObject instanceof String) {
			leafValueFloat = Float.parseFloat((String) leafValueObject);
		}
		return leafValueFloat;
	}

	/**
	 * Testing only.
	 * 
	 * @param daTree
	 * @return
	 */
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
	 * Testing only.
	 * 
	 * @param daTree
	 * @return
	 */
	public boolean consistencyCheck(TreeMap daTree, boolean consistent,
			int[] levels) {
		float totalOfLeafGroup = 0;
		boolean updateConsistent = false;
		if(transitionLevels == null){
		transitionLevels = new int[daTree.size()+1][daTree.size()+1];
		}
		for (int count = 1; count <= daTree.size(); count++) {
			System.out.print(count + "\t");
			Object configNode = daTree.get(count);
			if (configNode instanceof TreeMap) {
				consistent = consistencyCheck((TreeMap) configNode, consistent,
						transitionLevels[count]);
			} else {
				if (configNode instanceof Float) {
					totalOfLeafGroup += ((Float) configNode).floatValue();
					levels[count] = Math.round(totalOfLeafGroup * maxRandInt);
					System.out.print("**" + totalOfLeafGroup + "\n");
					updateConsistent = true;
				}
			}
		}
		// TODO Beware!!!!!!
		if (updateConsistent) {
			consistent = consistent
					&& (Math.abs(totalOfLeafGroup - 1F) < 0.000001);
		}
		System.out.print("##" + consistent + "\n");
		return consistent;
	}

}
