package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * Represents a Set containing all relative risks that still can be choosen
 * There are two such Sets maintained: one that contains all feasible RRs based
 * on the diseases and riskfactor choices (availlableRRs) , and one of that only
 * contains the choices for the current Relative Risk Tab (after at least one
 * other RR has already been choosen
 * 
 * @author hendriek boshuizen
 * 
 * @param <String>
 */
public class RelRisksCollectionForDropdown {

	private static Log statLog = LogFactory
			.getLog("RelRisksCollectionForDropdown");
	private Log instLog = LogFactory.getLog(getClass().getName());

	private static RelRisksCollectionForDropdown instance = null;

	private TreeAsDropdownLists treeLists;
	/*
	 * possibleRelRisks are all the possible RR in the model between any valid
	 * source and any valid target, irrespective of any choice made in the
	 * simulation screen.
	 */
	private/* static */RelativeRiskFileNamesBySourceAndTargetNameMap possibleRelRisks = new RelativeRiskFileNamesBySourceAndTargetNameMap();
	/*
	 * availlableRelRisks are all RR availlable in the simulation-screen, given
	 * the choices made for riskfactor and disease in the simulation screen.
	 * This collection is a subset of the possibleRelRisks collection. This list
	 * will change if other diseases or riskfactors are choosen
	 */

	RelativeRiskFileNamesBySourceAndTargetNameMap availlableRelRisks = new RelativeRiskFileNamesBySourceAndTargetNameMap();

	/*
	 * configuredRelRisksForDropDown are the RR that still can be choosen in a
	 * particular relative risk tab, given the choice made in other relativerisk
	 * tabs. This is a further limitation from the availableRelRiskscollection.
	 * Subtracted are relative risks that have already been configured plus
	 * relative risks that define a relation between a source-target pair that
	 * has already been configured.
	 */
	private RelativeRiskFileNamesBySourceAndTargetNameMap availlableRelRisksForDropdown = new RelativeRiskFileNamesBySourceAndTargetNameMap();

	private RelRisksCollectionForDropdown() {
		super();
	}

	/**
	 * Get the single instance. There can be only one.
	 * 
	 * @param treeLists
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public RelRisksCollectionForDropdown getInstance(
			DynamoSimulationObject dynamoSimulationObject, BaseNode selectedNode)
			throws ConfigurationException {
		if (instance == null) {
			instance = new RelRisksCollectionForDropdown();
		}
		instance.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		RelativeRiskFileNamesBySourceAndTargetNameMap rrCollection = instance.treeLists
				.getValidRelativeRiskCollection();
		statLog.debug(rrCollection.dump4Log());
		instance.possibleRelRisks = makeDeepCopyRR(rrCollection);
		statLog.debug(instance.possibleRelRisks.dump4Log());
		instance.refresh(dynamoSimulationObject);
		return instance;
	}

	/**
	 * alternative getting of single instance
	 * 
	 * @param dynamoSimulationObject
	 * @param treeLists
	 * @return
	 * @throws ConfigurationException
	 */
	public synchronized static RelRisksCollectionForDropdown getInstance(
			DynamoSimulationObject dynamoSimulationObject,
			TreeAsDropdownLists treeLists) throws ConfigurationException {

		if (instance == null) {
			instance = new RelRisksCollectionForDropdown();
			;
		}
		RelativeRiskFileNamesBySourceAndTargetNameMap rrCollection = treeLists
				.getValidRelativeRiskCollection();
		statLog.debug(rrCollection.dump4Log());
		instance.possibleRelRisks = makeDeepCopyRR(rrCollection);
		instance.refresh(dynamoSimulationObject);
		return instance;
	}

	/**
	 * Get the single instance. There can be only one. This method can only be
	 * used in places where the object has already been instantiated. As
	 * instantiation requires data that are not reachable from everywhere, this
	 * parameter free method is also provided
	 * 
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public RelRisksCollectionForDropdown getInstance(

	) throws ConfigurationException {
		if (instance == null)
			throw new ConfigurationException(
					" instance called of non-instantiated"
							+ "RelRiskCollectionForDropdown. =Programming error");

		return instance;
	}

	/**
	 * This method fills the
	 * 
	 * 
	 * @param dynamoSimulationObject
	 *            : the simulation data that contains the information on which
	 *            diseases and riskfactors and relative risks have allready been
	 *            choosen
	 * 
	 * @throws ConfigurationException
	 * 
	 */

	public void refresh(DynamoSimulationObject dynamoSimulationObject)
			throws ConfigurationException {
		synchronized (this) {
			instLog.debug("Initial availableRelativeRisks");

			Set<String> configuredFromValues = new LinkedHashSet<String>();
			Set<String> configuredToValues = new LinkedHashSet<String>();
			Map<String, TabRiskFactorConfigurationData> rfc = (Map<String, TabRiskFactorConfigurationData>) dynamoSimulationObject
					.getRiskFactorConfigurations();
			Set<String> riskfactors = rfc.keySet();
			Set<String> diseases = (Set<String>) dynamoSimulationObject
					.getDiseaseConfigurations().keySet();
			if (riskfactors != null)
				configuredFromValues.addAll(riskfactors);
			if (diseases != null) {
				configuredFromValues.addAll(diseases);
				configuredToValues.addAll(diseases);
			}
			// These two are always present.
			configuredToValues.add((String) "death");
			configuredToValues.add((String) "disability");
			availlableRelRisks = deriveAvailableRelativeRisks(
					configuredFromValues, configuredToValues);
			this.availlableRelRisksForDropdown = makeDeepCopyRR(this.availlableRelRisks);
		}
	}

	/**
	 * This method fills the
	 * 
	 * 
	 * @param dynamoSimulationObject
	 *            : the simulation data that contains the information on which
	 *            diseases and riskfactors and relative risks have allready been
	 *            choosen
	 * 
	 * @throws ConfigurationException
	 * 
	 */

	// 20090918 public void relRiskRefresh(DynamoSimulationObject
	// dynamoSimulationObject)
	// throws ConfigurationException {
	// synchronized (this) {
	// instLog.debug("Initial availableRelativeRisks");
	//
	// Set<String> configuredFromValues = new LinkedHashSet<String>();
	// Set<String> configuredToValues = new LinkedHashSet<String>();
	// Map<String, TabRiskFactorConfigurationData> rfc = (Map<String,
	// TabRiskFactorConfigurationData>) dynamoSimulationObject
	// .getRiskFactorConfigurations();
	// Set<String> riskfactors = rfc.keySet();
	// Set<String> diseases = (Set<String>) dynamoSimulationObject
	// .getDiseaseConfigurations().keySet();
	// if (riskfactors != null)
	// configuredFromValues.addAll(riskfactors);
	// if (diseases != null) {
	// configuredFromValues.addAll(diseases);
	// configuredToValues.addAll(diseases);
	// }
	// // These two are always present.
	// configuredToValues.add((String) "death");
	// configuredToValues.add((String) "disability");
	// availlableRelRisks = deriveAvailableRelativeRisks(
	// configuredFromValues, configuredToValues);
	// // The first line is already present in the second method....
	// this.availlableRelRisksForDropdown =
	// makeDeepCopyRR(this.availlableRelRisks);
	// // removeRRAlreadyPresent(dynamoSimulationObject);
	// }
	// }
	/**
	 * This method fills the
	 * 
	 * 
	 * @param dynamoSimulationObject
	 *            : the simulation data that contains the information on which
	 *            diseases and riskfactors and relative risks have allready been
	 *            choosen
	 * 
	 * @throws ConfigurationException
	 * 
	 */

	public void relRiskRefresh4Init(
			TabRelativeRiskConfigurationData configuration,
			DynamoSimulationObject dynamoSimulationObject)
			throws ConfigurationException {
		synchronized (this) {
			instLog.debug("Initial availableRelativeRisks");

			Set<String> configuredFromValues = new LinkedHashSet<String>();
			Set<String> configuredToValues = new LinkedHashSet<String>();
			Map<String, TabRiskFactorConfigurationData> rfc = (Map<String, TabRiskFactorConfigurationData>) dynamoSimulationObject
					.getRiskFactorConfigurations();
			Set<String> riskfactors = rfc.keySet();
			Set<String> diseases = (Set<String>) dynamoSimulationObject
					.getDiseaseConfigurations().keySet();
			if (riskfactors != null)
				configuredFromValues.addAll(riskfactors);
			if (diseases != null) {
				configuredFromValues.addAll(diseases);
				configuredToValues.addAll(diseases);
			}
			// These two are always present.
			configuredToValues.add((String) "death");
			configuredToValues.add((String) "disability");
			availlableRelRisks = deriveAvailableRelativeRisks(
					configuredFromValues, configuredToValues);
			// The first line is already present in the second method....
			// this.availlableRelRisksForDropdown =
			// makeDeepCopyRR(this.availlableRelRisks);
			removeRRSelectedInOtherTabs(dynamoSimulationObject
					.getRelativeRiskConfigurations(), dynamoSimulationObject
					.getDiseaseConfigurations().keySet(), configuration);
		}
	}

	private RelativeRiskFileNamesBySourceAndTargetNameMap deriveAvailableRelativeRisks(
			Set<String> configuredFromValues, Set<String> configuredToValues) {
		synchronized (this) {
			/*
			 * first get all possible relative risk based on the availlable XML
			 * files in the selected Node location
			 */
			RelativeRiskFileNamesBySourceAndTargetNameMap localAvaillableRelRisks = makeDeepCopyRR(possibleRelRisks);
			instLog.debug("Copy of all possibleRelativeRisks: "
					+ localAvaillableRelRisks.dump4Log());
			/*
			 * remove all entries of diseases and riskfactors which have not
			 * been selected in the current simulation
			 */
			/* remove unconfigured from values */
			Iterator<String> it = localAvaillableRelRisks.keySet().iterator();
			while (it.hasNext()) {
				String currentFrom = it.next();

				if (!configuredFromValues.contains(currentFrom)) {
					it.remove();
					instLog.debug(currentFrom + " is removed from fromlist");
				} else {
					HashMap<String, Set<String>> toObject = localAvaillableRelRisks
							.get(currentFrom);
					Iterator<String> it2 = toObject.keySet().iterator();
					while (it2.hasNext()) {
						String currentTo = it2.next();
						if (!configuredToValues.contains(currentTo)) {
							it2.remove();

							instLog.debug(currentTo
									+ " is removed from to-list");
						}
					}
					// localAvaillableRelRisks.put(currentFrom, toObject);
				}
			}
			instLog.debug("PossibleRelativeRisks after cleaning: "
					+ localAvaillableRelRisks.dump4Log());

			return localAvaillableRelRisks;
		}
	}

	/**
	 * Started as a copy of the method below. The difference is that it must not
	 * be used during the creation of a relative risk, but after it has been
	 * committed.
	 * 
	 * Removes the relative risks that are not possible due to the selections of
	 * relative risks that have been made already. To do this the method should
	 * know which entries are disease names as it checks that dependent diseases
	 * can not be causal diseases Also it should now the relative risks that
	 * have been selected These are found in the configuration object
	 * "selectedRelRisk" This, however, also might contain the selection of the
	 * current tab, which when the tab is active should be treated as one of the
	 * possible choices, and therefore has to be excluded from the already
	 * choosen relative risks
	 * 
	 * @param selectedRelRisks
	 *            (field from DynamoSimulationObject)
	 * @param selectedDiseaseNames
	 *            (field from DynamoSimulationObject)
	 * @param singleConfiguration
	 */

	private void removeRRAlreadyPresent(
			DynamoSimulationObject dynamoSimulationObject) {
		synchronized (this) {
			RelativeRiskFileNamesBySourceAndTargetNameMap workingMap = makeDeepCopyRR(this.availlableRelRisks);
			Map<Integer, TabRelativeRiskConfigurationData> selectedRelRisks = dynamoSimulationObject
					.getRelativeRiskConfigurations();
			Set<String> diseaseNames = dynamoSimulationObject
					.getDiseaseConfigurations().keySet();
			/*
			 * in case the tab is created, singleConfiguration is null, and
			 * entries need to be removed
			 */

			/*
			 * check first if the particular relative risk has already been
			 * selected if yes, remove it
			 */

			{
				Set<Integer> keySet = selectedRelRisks.keySet();
				Iterator<Integer> selKeyIterator = keySet.iterator();
				while (selKeyIterator.hasNext()) {
					Integer currentConfigurationElementNumber = selKeyIterator
							.next();
					/*
					 * do this only if the RR is not the RR that is being
					 * selected by the current tab
					 */
					/*
					 * the selectedRelRisks have an Integer as key, while this
					 * has converted to a string in the currentSelection. First
					 * we cast the currentSelection to Integer
					 */
					TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
							.get(currentConfigurationElementNumber);
					Set<String> aRR4DDKeySet = workingMap.keySet();
					Iterator<String> aKeyIterator = aRR4DDKeySet.iterator();
					while (aKeyIterator.hasNext()) {
						String currentFrom = aKeyIterator.next();
						HashMap<String, Set<String>> currentRRForChoice = workingMap
								.get(currentFrom);
						if (currentFrom.equals(currentSelectedRR.getFrom())) {
							synchronized (currentRRForChoice) {
								Set<String> cRR4CKeySet = currentRRForChoice
										.keySet();
								Iterator<String> keyIterator = cRR4CKeySet
										.iterator();
								while (keyIterator.hasNext()) {
									String currentRRForChoiceKey = keyIterator
											.next();
									if ((currentRRForChoiceKey != null)
											&& (currentRRForChoiceKey
													.equals(currentSelectedRR
															.getTo())))
										keyIterator.remove();
								}
							}
						}
						if (currentRRForChoice.isEmpty())
							workingMap
									.remove(currentConfigurationElementNumber);
					}
				}
			}
			/*
			 * remove diseases that have been choosen as from diseases from the
			 * list of "to" diseases, as this is not allowed in Dynamo
			 */

			Set<Integer> selectedRelRisksKeySet = selectedRelRisks.keySet();
			Iterator<Integer> selKeyIterator = selectedRelRisksKeySet
					.iterator();
			while (selKeyIterator.hasNext()) {
				Integer selectedRelRisksKey = selKeyIterator.next();
				TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
						.get(selectedRelRisksKey);
				if (diseaseNames.contains(currentSelectedRR.getFrom())) {
					Set<String> aRR4DDKeySet = workingMap.keySet();
					Iterator<String> keyIterator = aRR4DDKeySet.iterator();
					while (keyIterator.hasNext()) {
						String currentFrom = keyIterator.next();
						/* the key in this map is the name of the to disease */
						HashMap<String, Set<String>> currentRRToChoices = workingMap
								.get(currentFrom);
						currentRRToChoices.remove(currentSelectedRR.getFrom());
					}
				}
			}
			this.availlableRelRisksForDropdown = makeDeepCopyRR(workingMap);
		}
	}

	/**
	 * 
	 * Removes the relative risks that are not possible due to the selections of
	 * relative risks that have been made already. To do this the method should
	 * know which entries are disease names as it checks that dependent diseases
	 * can not be causal diseases Also it should now the relative risks that
	 * have been selected These are found in the configuration object
	 * "selectedRelRisk" This, however, also might contain the selection of the
	 * current tab, which when the tab is active should be treated as one of the
	 * possible choices, and therefore has to be excluded from the already
	 * choosen relative risks
	 * 
	 * @param selectedRelRisks
	 *            (field from DynamoSimulationObject)
	 * @param selectedDiseaseNames
	 *            (field from DynamoSimulationObject)
	 * @param singleConfiguration
	 */

	private void removeRRSelectedInOtherTabs(
			Map<Integer, TabRelativeRiskConfigurationData> selectedRelRisks,
			Set<String> diseaseNames,
			TabRelativeRiskConfigurationData singleConfiguration) {

		synchronized (availlableRelRisks) {
			RelativeRiskFileNamesBySourceAndTargetNameMap workingMap = makeDeepCopyRR(this.availlableRelRisks);
			/*
			 * in case the tab is created, singleConfiguration is null, and
			 * entries need to be removed
			 */

			/*
			 * check first if the particular relative risk has already been
			 * selected if yes, remove it
			 */

			{
				Set<Integer> keySet = selectedRelRisks.keySet();
				Iterator<Integer> selKeyIterator = keySet.iterator();
				while (selKeyIterator.hasNext()) {
					Integer currentConfigurationElementNumber = selKeyIterator
							.next();
					/*
					 * do this only if the RR is not the RR that is being
					 * selected by the current tab
					 */
					/*
					 * the selectedRelRisks have an Integer as key, while this
					 * has converted to a string in the currentSelection. First
					 * we cast the currentSelection to Integer
					 */
					if (singleConfiguration == null
							|| !(singleConfiguration.getIndex() == currentConfigurationElementNumber)) {
						TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
								.get(currentConfigurationElementNumber);
						Set<String> aRR4DDKeySet = workingMap.keySet();
						Iterator<String> aKeyIterator = aRR4DDKeySet.iterator();
						while (aKeyIterator.hasNext()) {
							String currentFrom = aKeyIterator.next();
							HashMap<String, Set<String>> resultRR4CMap = new HashMap<String, Set<String>>();
							HashMap<String, Set<String>> currentRRForChoice = workingMap
									.get(currentFrom);
							if (currentFrom.equals(currentSelectedRR.getFrom())) {
								Set<String> cRR4CKeySet = currentRRForChoice
										.keySet();
								Iterator<String> keyIterator = cRR4CKeySet
										.iterator();
								while (keyIterator.hasNext()) {
									String currentRRForChoiceKey = keyIterator
											.next();
									if ((currentRRForChoiceKey != null)
											&& (currentRRForChoiceKey
													.equals(currentSelectedRR
															.getTo()))) {
										keyIterator.remove();
									}
								}
							}

							if (currentRRForChoice.isEmpty())
								getAvaillableRelRisksForDropdown().remove(
										currentConfigurationElementNumber);
						}
					}
				}
			}
			/*
			 * remove diseases that have been choosen as from diseases from the
			 * list of "to" diseases, as this is not allowed in Dynamo
			 */

			Set<Integer> selectedRelRisksKeySet = selectedRelRisks.keySet();
			Iterator<Integer> selKeyIterator = selectedRelRisksKeySet
					.iterator();
			while (selKeyIterator.hasNext()) {
				Integer selectedRelRisksKey = selKeyIterator.next();
				if (singleConfiguration == null
						|| !(singleConfiguration.getIndex() == selectedRelRisksKey)) {
					TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
							.get(selectedRelRisksKey);
					if (diseaseNames.contains(currentSelectedRR.getFrom())) {
						Set<String> aRR4DDKeySet = workingMap.keySet();
						Iterator<String> keyIterator = aRR4DDKeySet.iterator();
						while (keyIterator.hasNext()) {
							String currentFrom = keyIterator.next();
							/* the key in this map is the name of the to disease */
							HashMap<String, Set<String>> currentRRToChoices = workingMap
									.get(currentFrom);
							currentRRToChoices.remove(currentSelectedRR
									.getFrom());
						}
					}
				}
			}
			this.availlableRelRisksForDropdown = makeDeepCopyRR(workingMap);
		}
	}

	private static RelativeRiskFileNamesBySourceAndTargetNameMap makeDeepCopyRR(
			RelativeRiskFileNamesBySourceAndTargetNameMap original) {
		synchronized (original) {

			/*
			 * first make a copy of availlableRelRisks to
			 * availlableRelRisksForDropdown
			 */
			/* this is a deepcopy as it should not change the original Map */

			RelativeRiskFileNamesBySourceAndTargetNameMap copy = new RelativeRiskFileNamesBySourceAndTargetNameMap();

			Iterator<String> itA = original.keySet().iterator();
			while (itA.hasNext()) {
				String currentA = itA.next().toString();
				HashMap<String, Set<String>> toDataToCopy = original
						.get(currentA);
				Iterator<String> itB = toDataToCopy.keySet().iterator();
				HashMap<String, Set<String>> newToData = new HashMap<String, Set<String>>();
				while (itB.hasNext()) {
					String currentB = (itB.next()).toString();
					Set<String> fileDataToCopy = toDataToCopy.get(currentB);
					Iterator<String> itC = fileDataToCopy.iterator();
					Set<String> newFileSet = new LinkedHashSet<String>();
					while (itC.hasNext()) {
						newFileSet.add((String) itC.next().toString());
					}
					newToData.put(currentB, newFileSet);
				}

				copy.put(currentA, newToData);

			}
			// ? copy.keySet();
			return copy;
		}
	}

	// TODO methods that return the dropdown set that are necessary
	/**
	 * method returns the set of possible choice for the target of a relative
	 * risk belonging to a particular from value
	 * 
	 * @param ChosenFrom
	 *            : the from value for which to make this list
	 * @return
	 */
	public Set<String> updateToList(String ChosenFrom) {
		synchronized (this) {
			Set<String> toNamesToReturn = new LinkedHashSet<String>();
			RelativeRiskFileNamesBySourceAndTargetNameMap map = this
					.getAvaillableRelRisksForDropdown();
			Iterator<String> fromIterator = map.keySet().iterator();
			while (fromIterator.hasNext()) {
				String key = fromIterator.next();
				if (key.equals(ChosenFrom))
					toNamesToReturn.addAll(this
							.getAvaillableRelRisksForDropdown().get(key)
							.keySet());
			}
			return toNamesToReturn;
		}
	}

	/**
	 * method returns the set of possible choice for the source of a relative
	 * risk belonging to a particular to value of chosenTo
	 * 
	 * @param ChosenTo
	 *            : the "to" value for which to make this list
	 * @return list of
	 */
	public Set<String> updateFromList(String ChosenTo) {
		synchronized (this) {
			Set<String> fromNamesToReturn = new LinkedHashSet<String>();
			Iterator<String> fromIterator = this
					.getAvaillableRelRisksForDropdown().keySet().iterator();
			while (fromIterator.hasNext()) {
				String key = fromIterator.next();
				if (this.getAvaillableRelRisksForDropdown().get(key).keySet()
						.contains(ChosenTo))
					fromNamesToReturn.add(key);
			}
			if (fromNamesToReturn.isEmpty())
				fromNamesToReturn = null;
			return fromNamesToReturn;
		}
	}

	/**
	 * method returns the set of possible choice for the source of a relative
	 * risk. It does not check combination with the chosen To.
	 * 
	 * @param ChosenTo
	 *            : the "to" value for which to make this list
	 * @return list of
	 */
	public Set<String> updateFromList() {
		synchronized (this) {
			Set<String> fromNamesToReturn = new LinkedHashSet<String>();
			RelativeRiskFileNamesBySourceAndTargetNameMap map = this
					.getAvaillableRelRisksForDropdown();
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (map.get(key).size() != 0) {
					fromNamesToReturn.add(key);
				}
			}
			// if (toNamesToReturn.isEmpty())
			// toNamesToReturn = null;
			return fromNamesToReturn;
		}
	}

	/**
	 * method returns the set of possible choice for the relative risks with as
	 * source "ChosenFrom" and target "chosenTo"
	 * 
	 * @param ChosenFrom
	 *            : the "from" value for which to make this list
	 * @param ChosenTo
	 *            : the "to" value for which to make this list
	 * @return list of filenames with RR data
	 */
	public Set<String> updateRRFileList(String chosenFrom, String chosenTo) {
		synchronized (this) {
			Set<String> fileNamesToReturn = new LinkedHashSet<String>();
			Iterator<String> fromIterator = this
					.getAvaillableRelRisksForDropdown().keySet().iterator();
			while (fromIterator.hasNext()) {
				String fromKey = fromIterator.next();
				if (fromKey.equals(chosenFrom)) {
					HashMap<String, Set<String>> toList = this
							.getAvaillableRelRisksForDropdown().get(fromKey);
					Iterator<String> toIterator = toList.keySet().iterator();
					while (toIterator.hasNext()) {
						String toKey = toIterator.next();
						if (toKey.equals(chosenTo))
							fileNamesToReturn = toList.get(toKey);
					}
				}
			}
			// if (toNamesToReturn.isEmpty())
			// toNamesToReturn = null;
			return fileNamesToReturn;
		}
	}

	public String getFirstRRFileList() {
		synchronized (this) {
			Set<String> fileNames = new LinkedHashSet<String>();
			String returnName = null;
			Iterator<String> fromIterator = this
					.getAvaillableRelRisksForDropdown().keySet().iterator();
			while (fromIterator.hasNext()) {
				String fromKey = fromIterator.next();
				HashMap<String, Set<String>> toList = this
						.getAvaillableRelRisksForDropdown().get(fromKey);
				Iterator<String> toIterator = toList.keySet().iterator();
				while (toIterator.hasNext()) {
					String toKey = toIterator.next();
					fileNames = toList.get(toKey);
					break;
				}
				break;
			}
			// if (fileNames.isEmpty())
			// returnName = null;
			// else
			// for (String nameKey : fileNames) {
			// returnName = nameKey;
			// break;
			// }

			return returnName;
		}
	}

	public String getFirstTo() {
		synchronized (this) {
			String name = null;
			Iterator<String> fromIterator = this
					.getAvaillableRelRisksForDropdown().keySet().iterator();
			while (fromIterator.hasNext()) {
				String fromKey = fromIterator.next();
				HashMap<String, Set<String>> toList = this
						.getAvaillableRelRisksForDropdown().get(fromKey);
				Iterator<String> toIterator = toList.keySet().iterator();
				while (toIterator.hasNext()) {
					String toKey = toIterator.next();
					name = toKey;
					break;
				}
				break;
			}

			return name;
		}
	}

	public String getFirstTo(String currentFrom) {
		synchronized (this) {

			String name = null;

			;
			if (!this.getAvaillableRelRisksForDropdown().isEmpty()) {

				HashMap<String, Set<String>> toList = this.availlableRelRisksForDropdown
						.get(currentFrom);

				if (!toList.isEmpty()) {
					Iterator<String> toIterator = toList.keySet().iterator();
					while (toIterator.hasNext()) {
						String toKey = toIterator.next();
						name = toKey;
						break;
					}
				}
			}

			return name;
		}
	}

	public String getFirstFrom() {
		synchronized (this) {
			String name = null;
			Iterator<String> fromIterator = this
					.getAvaillableRelRisksForDropdown().keySet().iterator();
			while (fromIterator.hasNext()) {
				String fromKey = fromIterator.next();
				HashMap<String, Set<String>> toList = this
						.getAvaillableRelRisksForDropdown().get(fromKey);

				name = fromKey;
				break;
			}

			return name;
		}
	}

	public boolean isEmpty() {
		synchronized (this) {
			boolean value = false;
			if (this.getAvaillableRelRisksForDropdown().isEmpty())
				value = true;
			return value;
		}
	}

	public void setAvaillableRelRisksForDropdown(
			RelativeRiskFileNamesBySourceAndTargetNameMap availlableRelRisksForDropdown) {
		synchronized (this) {
			instLog.debug("setAvaillableRelRisksForDropdown()");
			this.availlableRelRisksForDropdown = availlableRelRisksForDropdown;
		}
	}

	public RelativeRiskFileNamesBySourceAndTargetNameMap getAvaillableRelRisksForDropdown() {
		synchronized (this) {
			instLog.debug(">>>>Configured relative risks: "
					+ possibleRelRisks.dump4Log());
			instLog.info(">>>>getAvaillableRelRisksForDropdown()>>>>>:\n" + availlableRelRisksForDropdown.dump4Log());
			return availlableRelRisksForDropdown;
		}
	}
}
