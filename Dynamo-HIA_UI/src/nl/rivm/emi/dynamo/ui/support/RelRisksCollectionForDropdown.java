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
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
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
 * Represents a Set containing all relative risks that still can be choosen
 * There are two such Sets maintained: one that contains all feasible RRs based
 * on the diseases and riskfactor choices (availlableRRs) , and one of that only
 * contains the choices for the current Relative Risk Tab (after aqt least one
 * other RR has already been choosen
 * 
 * @author hendriek boshuizen
 * 
 * @param <String>
 */
public class RelRisksCollectionForDropdown  {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static RelRisksCollectionForDropdown thisObject = null;
	/* configuredRelRisks are all the configured RR in the model, irrespective of any choice made in the
	 * simulation screen
	 */
	private static HashMap<String, HashMap<String, Set<String>>> configuredRelRisks = new HashMap<String, HashMap<String, Set<String>>>();
	/* availlableRelRisks are all RR availlable in the simulation-screen, given the choices made for riskfactor and disease in the
	 * simulation screen. This list will change if other diseases or riskfactors are choosen
	 */
	
	HashMap<String, HashMap<String, Set<String>>> availlableRelRisks =            new HashMap<String, HashMap<String, Set<String>>>();
	/* configuredRelRisksForDropDown are the RR that still can be choosen in a particular relative
	 * risk tab, given the choice made in other risk tabs
	 */
	
	private HashMap<String, HashMap<String, Set<String>>> availlableRelRisksForDropdown = new HashMap<String, HashMap<String, Set<String>>>();

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
			DynamoSimulationObject dynamoSimulationObject,
			BaseNode selectedNode) throws ConfigurationException {
		if (thisObject == null) {
			thisObject = new RelRisksCollectionForDropdown();
		}
		TreeAsDropdownLists treeLists=TreeAsDropdownLists.getInstance(selectedNode);
		configuredRelRisks=makeDeepCopyRR(treeLists.getValidRelativeRiskCollection());
		thisObject.refresh(dynamoSimulationObject);
		return thisObject;
	}
	

	/**alternative getting of single instance 
	 * @param dynamoSimulationObject
	 * @param treeLists
	 * @return
	 * @throws ConfigurationException 
	 */
	public synchronized static RelRisksCollectionForDropdown getInstance(
			DynamoSimulationObject dynamoSimulationObject,
			TreeAsDropdownLists treeLists) throws ConfigurationException {
		
		if (thisObject == null) {
			thisObject = new RelRisksCollectionForDropdown();
			;
		}
		configuredRelRisks=makeDeepCopyRR(treeLists.getValidRelativeRiskCollection());
		thisObject.refresh(dynamoSimulationObject);
		return thisObject;
		
	}

	/**
	 * Get the single instance. There can be only one. This method can only be used in places where
	 * the object has already been instantiated. As instantiation requires data that are not
	 * reachable from everywhere, this parameter free method is also provided
	 * 
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	static synchronized public RelRisksCollectionForDropdown getInstance(
			
			) throws ConfigurationException {
		if (thisObject == null) throw new ConfigurationException(" instance called of non-instantiated" +
				"RelRiskCollectionForDropdown. =Programming error");
		
		
		return thisObject;
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
	
	public void refresh(DynamoSimulationObject dynamoSimulationObject
		) throws ConfigurationException {
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

		/*
		 * first get all possible relative risk based on the availlable XML
		 * files in the selected Node location
		 */
		

		this.availlableRelRisks=makeDeepCopyRR(configuredRelRisks);
		

		/*
		 * remove all entries of diseases and riskfactors which have not been
		 * selected in the current simulation
		 */
/* remove from values */
		Iterator<String> it = availlableRelRisks.keySet().iterator();
		while (it.hasNext()) {
			String currentFrom=it.next();
			
			if (!validFromValues.contains( currentFrom)){
				it.remove();
				log.fatal(currentFrom+" is removed from fromlist");	
			}
		
		
			else {
				HashMap<String, Set<String>> toObject = availlableRelRisks.get(currentFrom);
				
				Iterator<String> it2 = toObject.keySet().iterator();
				while (it2.hasNext()) {
					String currentTo=it2.next();
					if (!validToValues.contains(currentTo)){
						it2.remove();
						
						log.fatal(currentTo+" is removed from to-list");	
				}}
				availlableRelRisks.put(currentFrom,toObject);
			}
		}
		this.availlableRelRisksForDropdown=makeDeepCopyRR(this.availlableRelRisks);

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

	public void removeRRSelectedInOtherTabs(
			Map<Integer, TabRelativeRiskConfigurationData> selectedRelRisks,
			Set<String> diseaseNames, TabRelativeRiskConfigurationData singleConfiguration) {

		
		
		this.availlableRelRisksForDropdown=makeDeepCopyRR(this.availlableRelRisks);
		/* in case the tab is created, singleConfiguration is null, and entries need to be removed */
		
		/*
		 * check first if the particular relative risk has already been selected
		 * if yes, remove it
		 */

		{
			
			
			
			
			
			
			Iterator<Integer> it1 = selectedRelRisks.keySet().iterator();
			while (it1.hasNext()) {
				/*
				 * do this only if the RR is not the RR that is being selected
				 * by the current tab
				 */
				/*
				 * the selectedRelRisks have an Integer as key, while this has converted
				 * to a string in the currentSelection. First we cast the
				 * currentSelection to Integer
				 */
				Integer currentConfigurationElementNumber = it1.next();
				if  (singleConfiguration==null ||!(singleConfiguration.getIndex()==currentConfigurationElementNumber)) {
					TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
							.get(currentConfigurationElementNumber);

					Iterator<String> it2 = getAvaillableRelRisksForDropdown().keySet()
							.iterator();
					while (it2.hasNext()) {
						String currentFrom = it2.next();
						HashMap<String, Set<String>> currentRRForChoice = getAvaillableRelRisksForDropdown()
								.get(currentFrom);
						if (currentFrom.equals(currentSelectedRR.getFrom())) {
							Iterator<String> it3 = currentRRForChoice.keySet()
									.iterator();
							while (it3.hasNext()) {
								if (it3.next()
										.equals(currentSelectedRR.getTo()))
									it3.remove();
							}
						}
						if (currentRRForChoice.isEmpty())
							it2.remove();
					}
				}
			}
		}

		/*
		 * remove diseases that have been choosen as from diseases from the list
		 * of "to" diseases, as this is not allowed in Dynamo
		 */

		Iterator<Integer> it4 = selectedRelRisks.keySet().iterator();
		while (it4.hasNext()) {
			Integer currentConfigurationElementNumber = it4.next();
			if (singleConfiguration==null ||!(singleConfiguration.getIndex()==currentConfigurationElementNumber)) {
				TabRelativeRiskConfigurationData currentSelectedRR = selectedRelRisks
						.get(currentConfigurationElementNumber);
				if (diseaseNames.contains(currentSelectedRR.getFrom())) {
					Iterator<String> it5 = getAvaillableRelRisksForDropdown().keySet()
							.iterator();
					while (it5.hasNext()) {
						/* the key in this map is the name of the to disease */
						HashMap<String, Set<String>> currentRRToChoices = getAvaillableRelRisksForDropdown()
								.get(it5.next());
						currentRRToChoices.remove(currentSelectedRR.getFrom());

					}

				}
			}
		}
	}

	private static HashMap<String, HashMap<String, Set<String>>> makeDeepCopyRR(HashMap<String, HashMap<String, Set<String>>> original) {
		/* first make a copy of availlableRelRisks to	availlableRelRisksForDropdown */
		/*  this is a deepcopy as it should not change the original Map*/
		
		HashMap<String, HashMap<String, Set<String>>> copy=new HashMap<String, HashMap<String, Set<String>>>();
		
		
		Iterator<String> itA = original.keySet().iterator();
		while (itA.hasNext()){
			String currentA=itA.next().toString();
			HashMap<String, Set<String>> toDataToCopy=original.get(currentA);
			Iterator<String> itB = toDataToCopy.keySet().iterator();
			HashMap<String, Set<String>> newToData=new HashMap<String, Set<String>> ();
			while (itB.hasNext()){
				String currentB=(itB.next()).toString();
				 Set<String> fileDataToCopy=toDataToCopy.get(currentB);
				 Iterator<String> itC = fileDataToCopy.iterator();
				 Set<String> newFileSet=new LinkedHashSet<String> ();
					while (itC.hasNext()){
						 newFileSet.add((String)itC.next().toString());}
					newToData.put(currentB,newFileSet);
			}
				
			copy.put(currentA,newToData);
			
		}   copy.keySet();
		return copy;
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

		Set<String> toNamesToReturn = null;
		for (String key : this.getAvaillableRelRisksForDropdown().keySet())
			if (key.equals(ChosenFrom))
				toNamesToReturn = this.getAvaillableRelRisksForDropdown().get(key)
						.keySet();
		return toNamesToReturn;

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

		Set<String> toNamesToReturn = new LinkedHashSet<String>();
		for (String key : this.getAvaillableRelRisksForDropdown().keySet())
			if (this.getAvaillableRelRisksForDropdown().get(key).keySet().contains(
					ChosenTo))
				toNamesToReturn.add(key);
		if (toNamesToReturn.isEmpty())
			toNamesToReturn = null;
		return toNamesToReturn;

	}
	
	
	/**
	 * method returns the set of possible choice for the source of a relative
	 * risk.  It does not check combination with the chosen To.
	 * 
	 * @param ChosenTo
	 *            : the "to" value for which to make this list
	 * @return list of
	 */
	public Set<String> updateFromList() {

		Set<String> toNamesToReturn = new LinkedHashSet<String>();
		for (String key : this.getAvaillableRelRisksForDropdown().keySet())
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
		for (String fromKey : this.getAvaillableRelRisksForDropdown().keySet())
			if (fromKey.equals(chosenFrom)) {
				HashMap<String, Set<String>> toList = this.getAvaillableRelRisksForDropdown()
						.get(fromKey);
				for (String toKey : toList.keySet())
					if (toKey.equals(chosenTo))
						toNamesToReturn = toList.get(toKey);
			}
		if (toNamesToReturn.isEmpty())
			toNamesToReturn = null;
		return toNamesToReturn;

	}

	public String getFirstRRFileList() {

		Set<String> fileNames = new LinkedHashSet<String>();
		String returnName = null;
		for (String fromKey : this.getAvaillableRelRisksForDropdown().keySet()) {
			HashMap<String, Set<String>> toList = this.getAvaillableRelRisksForDropdown()
					.get(fromKey);
			for (String toKey : toList.keySet()) {

				fileNames = toList.get(toKey);
				break;
			}
			break;
		}
		if (fileNames.isEmpty())
			returnName = null;
		else
			for (String nameKey : fileNames) {
				returnName = nameKey;
				break;
			}

		return returnName;

	}

	public String getFirstTo() {

		String name = null;
		for (String fromKey : this.getAvaillableRelRisksForDropdown().keySet()) {
			HashMap<String, Set<String>> toList = this.getAvaillableRelRisksForDropdown()
					.get(fromKey);
			for (String toKey : toList.keySet()) {
				name = toKey;
				break;
			}
			break;
		}

		return name;

	}
	
	
	public String getFirstTo(String currentFrom) {

		String name = null;
		
		;
		 if (!this.getAvaillableRelRisksForDropdown().isEmpty()){
		
			
			HashMap<String, Set<String>> toList = this.availlableRelRisksForDropdown.get(currentFrom);
		
			 if (!toList.isEmpty())
			for (String toKey : toList.keySet()) {
				name = toKey;
				break;
			}
			
		 }

		return name;

	}

	

	public String getFirstFrom() {

		String name = null;
		for (String fromKey : this.getAvaillableRelRisksForDropdown().keySet()) {
			HashMap<String, Set<String>> toList = this.getAvaillableRelRisksForDropdown()
					.get(fromKey);

			name = fromKey;
			break;
		}

		return name;

	}

	public boolean isEmpty() {
		boolean value=false;
		if (this.getAvaillableRelRisksForDropdown().isEmpty()) value=true;
		return value;
	}

	public void setAvaillableRelRisksForDropdown(
			HashMap<String, HashMap<String, Set<String>>> availlableRelRisksForDropdown) {
		this.availlableRelRisksForDropdown = availlableRelRisksForDropdown;
	}

	public HashMap<String, HashMap<String, Set<String>>> getAvaillableRelRisksForDropdown() {
		return availlableRelRisksForDropdown;
	}

	

}
