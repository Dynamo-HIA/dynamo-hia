package nl.rivm.emi.dynamo.ui.support;

import java.util.HashMap;

import java.util.AbstractCollection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.NoSuchElementException;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.simulation.DiseaseSelectionGroup;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * Represents a String Set of diseases that have already been chosen. Is used to
 * compile a list of chosable diseases.
 * 
 * @author schutb
 * 
 * @param <String>
 */
public class RelRisksCollectionForDropdown<String> extends
		LinkedHashSet<String> {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static RelRisksCollectionForDropdown thisObject = null;
	HashMap<String, HashMap<String, Set<String>>> availlableRelRisks = new HashMap<String, HashMap<String, Set<String>>>();

	private RelRisksCollectionForDropdown() {
		super();
	}

	/**
	 * Get the single instance. There can be only one.
	 * 
	 * @param selectedNode
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public RelRisksCollectionForDropdown getInstance(
			DynamoSimulationObject dynamoSimulationObject, BaseNode selectedNode)
			throws ConfigurationException {
		if (thisObject == null) {
			thisObject = new RelRisksCollectionForDropdown();
		}
		thisObject.refresh(dynamoSimulationObject, selectedNode);
		return thisObject;
	}

	/**
	 * This method fills the object with a list containing all relative risks
	 * that still can be choosen
	 * 
	 * 
	 * @param dynamoSimulationObject
	 *            : the simulation data that contains the information on which
	 *            diseases and riskfactors and relative risks have allready been
	 *            choosen
	 * @throws ConfigurationException
	 * 
	 */
	public void refresh(DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode) throws ConfigurationException {

		Set<String> validFromValues = new LinkedHashSet<String>();
		Set<String> validToValues = new LinkedHashSet<String>();
		Map<String, TabRiskFactorConfigurationData> rfc = (Map<String, TabRiskFactorConfigurationData>) dynamoSimulationObject
				.getRiskFactorConfigurations();
		Set<String> riskfactors = rfc.keySet();
		if (riskfactors != null)
			validFromValues.addAll(riskfactors);
		Set<String> diseases = (Set<String>) dynamoSimulationObject
				.getDiseaseConfigurations().keySet();
		if (diseases != null)
			validToValues.addAll(diseases);
		validToValues.add((String) "Death");
		validToValues.add((String) "Disability");
		if (diseases != null)
			validFromValues.addAll(diseases);
		TreeAsDropdownLists treeLists = TreeAsDropdownLists
				.getInstance(selectedNode);
		/*
		 * first get all possible relative risk based on the availlable XML
		 * files in the selected Node location
		 */
		Object tmp = treeLists.getValidRelRisk();

		this.availlableRelRisks = (HashMap<String, HashMap<String, Set<String>>>) tmp;

		Set<String> fromList = (Set<String>) availlableRelRisks.keySet();

		/*
		 * remove all entries of diseases and riskfactors which have not been
		 * selected in the current simulation
		 */

		Iterator<String> it = fromList.iterator();
		while (it.hasNext()) {
			if (!validFromValues.contains(it.next()))
				remove(it);
			else {
				Iterator<String> it2 = iterator();
				while (it2.hasNext()) {
					if (!validToValues.contains(it2.next()))
						remove(it2);

				}

			}
		}
		Map<Integer, TabRelativeRiskConfigurationData> selectedRelRisks = dynamoSimulationObject
				.getRelativeRiskConfigurations();

		removeSelected(selectedRelRisks, diseases);

	}

	/**
	 * 
	 * Removes the relative risks that are not possible due to selections of
	 * relative risks. To do this the method should know which entries are disease
	 * names as it checks that dependent diseases can not be causal diseases
	 * 
	 * @param selectedRelRisk (field from DynamoSimulationObject)
	 * @param selectedDiseaseNames (field from DynamoSimulationObject)
	 */
	public void removeSelected(
			Map<Integer, TabRelativeRiskConfigurationData> selectedRelRisks,
			Set<String> diseaseNames) {

		/*
		 * check first if the particular relative risk has already been selected
		 * if yes, remove it
		 */
		{
			Iterator<Integer> it1 = selectedRelRisks.keySet().iterator();
			while (it1.hasNext()) {
				TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
						.get(it1.next());
				Iterator<String> it2 = availlableRelRisks.keySet().iterator();
				while (it2.hasNext()) {
					String currentFrom = it2.next();
					HashMap<String, Set<String>> currentRRForChoice = availlableRelRisks
							.get(currentFrom);
					if (currentFrom.equals(currentSelectedRR.getFrom())) {
						Iterator<String> it3 = currentRRForChoice.keySet()
								.iterator();
						while (it3.hasNext()) {
							if (it3.next().equals(currentSelectedRR.getTo()))
								it3.remove();
						}
					}
					if (currentRRForChoice.isEmpty())
						it2.remove();
				}

			}
		}

		/*
		 * remove diseases that have been choosen as from diseases from the list
		 * of "to" diseases, as this is not allowed in Dynamo
		 */

		Iterator<Integer> it4 = selectedRelRisks.keySet().iterator();
		while (it4.hasNext()) {
			TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
					.get(it4.next());
			if (diseaseNames.contains(currentSelectedRR.getFrom())) {
				Iterator<String> it5 = availlableRelRisks.keySet().iterator();
				while (it5.hasNext()) {
					/* the key in this map is the name of the to disease */
					HashMap<String, Set<String>> currentRRToChoices = availlableRelRisks
							.get(it5.next());
					currentRRToChoices.remove(currentSelectedRR.getFrom());

				}

			}
		}
	};

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

		Set<String> toNamesToReturn = null;
		for (String key : this.availlableRelRisks.keySet())
			if (key == ChosenFrom)
				toNamesToReturn = this.availlableRelRisks.get(key).keySet();
		return toNamesToReturn;

	}

	/**
	 * method returns the set of possible choice for the source of a relative
	 * risk belonging to a particular to value
	 * 
	 * @param ChosenTo
	 *            : the "to" value for which to make this list
	 * @return list of
	 */
	public Set<String> updateFromList(String ChosenTo) {

		Set<String> toNamesToReturn = new LinkedHashSet<String>();
		for (String key : this.availlableRelRisks.keySet())
			if (this.availlableRelRisks.get(key).keySet().contains(ChosenTo))
				toNamesToReturn.add(key);
		if (toNamesToReturn.isEmpty())
			toNamesToReturn = null;
		return toNamesToReturn;

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

		Set<String> toNamesToReturn = new LinkedHashSet<String>();
		for (String fromKey : this.availlableRelRisks.keySet())
			if (fromKey == chosenFrom) {
				HashMap<String, Set<String>> toList = this.availlableRelRisks
						.get(fromKey);
				for (String toKey : toList.keySet())
					if (toKey == chosenTo)
						toNamesToReturn = toList.get(toKey);
			}
		if (toNamesToReturn.isEmpty())
			toNamesToReturn = null;
		return toNamesToReturn;

	}

	

}
