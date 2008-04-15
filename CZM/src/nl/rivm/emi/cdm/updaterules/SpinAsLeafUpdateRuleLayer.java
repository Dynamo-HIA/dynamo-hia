package nl.rivm.emi.cdm.updaterules;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.prngcollection.MersenneTwister;
import nl.rivm.emi.cdm.updaterules.base.ConfigurationLevel;
import nl.rivm.emi.cdm.updaterules.base.NeedsSeed;
import nl.rivm.emi.cdm.updaterules.base.OneToOneUpdateRuleBase;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpinAsLeafUpdateRuleLayer extends OneToOneUpdateRuleBase implements
		ConfigurationLevel, NeedsSeed {

	Log log = LogFactory.getLog(this.getClass().getName());
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


	public boolean handleFirstLevel(SubnodeConfiguration snConf, int levelNumber) {
		log.info("Handling " + snConf.getRootNode().getName()
				+ " at level " + levelNumber);
		Object temp = handleLevel(snConf, levelNumber);
		if (temp instanceof TreeMap) {
			if (consistencyCheck((TreeMap) temp, true, null)) {
				transitionConfiguration = (TreeMap) temp;
			}
		}
		return (transitionConfiguration != null);
	}

	
	public Object handleLevel(SubnodeConfiguration snConf, int levelNumber) {
		Object resultObject = null;
		List<SubnodeConfiguration> levelConfs = snConf.configurationsAt("level"
				+ levelNumber);
		if (!levelConfs.isEmpty()) {
			TreeMap<Integer, Object> levelMap = new TreeMap<Integer, Object>();
			int inLevelCount = 1;
			for (SubnodeConfiguration levelConf : levelConfs) {
				log.info("Handling " + levelConf.getRootNode().getName()
						+ " at level " + (levelNumber + 1));
				Object deeperResult = handleLevel(levelConf, levelNumber + 1);
				levelMap.put(inLevelCount, deeperResult);
				inLevelCount++;
			}
			resultObject = levelMap;
		} else {
			resultObject = handleLeaf(snConf);
			log.info("Leaf " + ((Float)resultObject)
					+ " at level " + (levelNumber + 1));
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
