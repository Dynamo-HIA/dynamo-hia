package nl.rivm.emi.dynamo.ui.panels.simulation;


import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabScenarioConfigurationData;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;

/**
 * 
 * Handles the simulation object data actions of the scenario tabs
 * 
 * @author schutb
 * 
 */
public class ScenarioTabDataManager implements DynamoTabDataManager {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static final String MALE = "Male";
	private static final String FEMALE = "Female";
	private static final String MALE_FEMALE = "Male and Female";

	private TreeAsDropdownLists treeLists;
	private DynamoSimulationObject dynamoSimulationObject;
	//private Map<String, ITabScenarioConfiguration> configurations;
	private ITabScenarioConfiguration singleConfiguration;
	private Set<String> initialSelection;

	public ScenarioTabDataManager(BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			Set<String> selections) throws ConfigurationException {
		this.treeLists = TreeAsDropdownLists.getInstance(selectedNode);
		this.dynamoSimulationObject = dynamoSimulationObject;
		
		this.initialSelection = selections;
		this.singleConfiguration = this.dynamoSimulationObject
		.getScenarioConfigurations().get(getInitialName());
	}

	public Set<String> getContents(String name, String chosenRiskFactorName)
			throws ConfigurationException {
		Set<String> contents = new LinkedHashSet<String>();
		// The name is still empty
		if (chosenRiskFactorName == null) {
			chosenRiskFactorName = this.getInitialRiskFactorName();
		}
		log.debug("HIERO chosenRiskFactorName DATAMANAGER: "
				+ chosenRiskFactorName);
		if (ScenarioSelectionGroup.MIN_AGE.equals(name)) {
			for (int age = 0; age < 96; age++) {
				String showAge = age + "";
				contents.add(showAge);
			}
			log.debug("contents0" + contents);
			log.debug("getContents MIN_AGE: " + contents);
		} else if (ScenarioSelectionGroup.MAX_AGE.equals(name)) {
			for (int age = 0; age < 96; age++) {
				String showAge = age + "";
				contents.add(showAge);
			}
			log.debug("contents1" + contents);
		} else if (ScenarioSelectionGroup.GENDER.equals(name)) {
			contents.add(MALE);
			contents.add(FEMALE);
			contents.add(MALE_FEMALE);
			log.debug("contents2" + contents);
		} else if (ScenarioResultGroup.TRANSITION.equals(name)) {
			/*  This has been added by hendriek */
			contents = deepcopy(this.treeLists.getTransitions(chosenRiskFactorName));
			Set<String>	contentsForPrevalence = this.treeLists.getRiskFactorPrevalences(chosenRiskFactorName);
			Map<String, TabRiskFactorConfigurationData> riskFactorConfiguration = dynamoSimulationObject
			.getRiskFactorConfigurations();
	       /* a scenario must differ from the referent scenario case in either transition or prevalence
			 * file, thus both the same as the reference scenario is not allowed
			 * if the prevalence is already the same, the transition rate can not be, so remove the filename
			 * from the contents
			 */
			TabRiskFactorConfigurationData riskfactorData=riskFactorConfiguration.get(chosenRiskFactorName);
	        String referentScenarioPrevalenceFileName=riskfactorData.getPrevalenceFileName();
	        
	        String currentPrevalenceFile=null;
	               
	        
			if (this.singleConfiguration!=null && 
					!(this.singleConfiguration.getAltPrevalenceFileName()==null))
				currentPrevalenceFile=this.singleConfiguration.getAltPrevalenceFileName();
			
			/* in case the is a new scenario, take the last items both for scenario prevalence and scenario 
	         * transitions */
			else if (contents.size()>1){
				
				for (String  potentialPrevFile : contentsForPrevalence) currentPrevalenceFile=potentialPrevFile;
				this.singleConfiguration.setAltPrevalenceFileName(currentPrevalenceFile);
			} 
			/* unless there is only one transition available, then we should take the prevalence
			 * that differs from the reference scenario 
			 */
			else if (contentsForPrevalence.size()>1) {
				for (String  potentialPrevFile : contentsForPrevalence) 
					if (!potentialPrevFile.equals(referentScenarioPrevalenceFileName)) currentPrevalenceFile=potentialPrevFile;
				/* and if this is not possible, no scenario is possible */
				this.singleConfiguration.setAltPrevalenceFileName(currentPrevalenceFile);
			}  else contents=null;       
			
			
			if (contents !=null && (currentPrevalenceFile.equals(referentScenarioPrevalenceFileName) 
					|| contentsForPrevalence.size()==1))
			contents.remove(riskfactorData.getTransitionFileName());
			if (contents.isEmpty()) contents=null;
			
			/* end addition hendriek */
			log.debug("contents3" + contents);
		} else if (ScenarioResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
		/* addition by hendriek */
			contents = deepcopy(this.treeLists.getRiskFactorPrevalences(chosenRiskFactorName));
			Set<String>	contentsForTransitions = this.treeLists.getTransitions(chosenRiskFactorName);
			
			/* a scenario must differ from the referent scenario case in either transition or prevalence
			 * file, thus both the same as the reference scenario is not allowed.
			 * So if the transition file is already the same, remove the prevalence file from the contents
			 * 
			 */
			Map<String, TabRiskFactorConfigurationData> riskFactorConfiguration = dynamoSimulationObject
			.getRiskFactorConfigurations();
			TabRiskFactorConfigurationData riskfactorData=riskFactorConfiguration.get(chosenRiskFactorName);
		       
			
			
			String referentScenarioTransFileName=riskfactorData.getTransitionFileName();
			String currentTransFile=null;
			if (this.singleConfiguration!=null && !(this.singleConfiguration.getAltTransitionFileName()==null))
			 currentTransFile=this.singleConfiguration.getAltTransitionFileName();
		
 			
			/* in case the is a new scenario, take the last items both for scenario prevalence and scenario 
	         * transitions */
			else if (contents.size()>1){
				
				for (String  potentialTransFile : contentsForTransitions) currentTransFile=potentialTransFile;
				this.singleConfiguration.setAltTransitionFileName(currentTransFile);
			
			} 
			/* unless there is only one prevalence availlable, then we should take the transition
			 * that differs from the reference scenario 
			 */
			else if (contentsForTransitions.size()>1){
				for (String  potentialTransFile : contentsForTransitions) 
					if (!potentialTransFile.equals(referentScenarioTransFileName)) currentTransFile=potentialTransFile;
				
				this.singleConfiguration.setAltTransitionFileName(currentTransFile);
			}/* and if this is not possible, no scenario is possible */ 
			else contents=null;       
		
			
						
			if (currentTransFile.equals(referentScenarioTransFileName)|| contentsForTransitions.size()==1)
			contents.remove(riskfactorData.getPrevalenceFileName());
			if (contents.isEmpty()) contents=null;
			/* end addition by hendriek */
			log.debug("contents4" + contents);
		}
		log.debug("contentsLast" + contents);
		return contents;
	}

	/* added by hendriek */
	private Set<String> deepcopy(Set<String> inSet) {
		Set<String> returnSet= new LinkedHashSet() ;
		if (inSet==null) returnSet=null;
		else 
		
		for (String key:inSet)
		returnSet.add(key);
		
		return returnSet;
	}

	public String getCurrentValue(String name)
			throws ConfigurationException {
		log.debug("GET CURRENT VALUE");
		log.debug("singleConfigurationXXX: " + singleConfiguration);
		String value = null;
		Integer age = null;
		Integer gender = null;
		if (this.singleConfiguration != null) {
			if (ScenarioSelectionGroup.MIN_AGE.equals(name)) {
				age = singleConfiguration.getMinAge();
				// For all Integer values
				value = (age==null) ? null : age.toString();
				log.debug("VALUE: " + value);
			} else if (ScenarioSelectionGroup.MAX_AGE.equals(name)) {
				age = singleConfiguration.getMaxAge();
				// For all Integer values
				value = (age == null) ? null : age.toString();
				log.debug("value" + value);
			} else if (ScenarioSelectionGroup.GENDER.equals(name)) {
				gender = singleConfiguration.getTargetSex();
				log.debug("GENDER value " + gender);
				// For all Integer values
				value = convertToText(gender);
				log.debug("value" + value);
			} else if (ScenarioResultGroup.TRANSITION.equals(name)) {
				value = singleConfiguration.getAltTransitionFileName();
				log.debug("TRANSITION value " + value);
			} else if (ScenarioResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
				value = singleConfiguration.getAltPrevalenceFileName();
				log.debug("RISK_FACTOR_PREVALENCE value " + value);
			}
		}
		return value;
	}

	public WritableValue getCurrentWritableValue(String name) {
		WritableValue writableValue = null;
		if (this.singleConfiguration != null) {
			if (ScenarioSelectionGroup.NAME.equals(name)) {
				writableValue = singleConfiguration.getObservableName();
				writableValue
						.addValueChangeListener(new IValueChangeListener() {
							@Override
							public void handleValueChange(ValueChangeEvent arg0) {
//								updateDynamoSimulationObject();
								Object oldValue = arg0.diff.getOldValue();
								Object newValue = arg0.diff.getNewValue();
								Map<String, ITabScenarioConfiguration> scenarioConfigurations = dynamoSimulationObject.getScenarioConfigurations();
								// Debugging.
								Set<String> currentNames = scenarioConfigurations.keySet();
								scenarioConfigurations.remove(oldValue);
								scenarioConfigurations.put((String)newValue, singleConfiguration);
								dynamoSimulationObject.setScenarioConfigurations(scenarioConfigurations);
								Set<String> newNames = scenarioConfigurations.keySet();
							}
						});
				log.debug("Getting writableValue with value: "
						+ writableValue.doGetValue());
			} else if (ScenarioSelectionGroup.SUCCESS_RATE.equals(name)) {
				writableValue = singleConfiguration.getObservableSuccessRate();
				writableValue
				.addValueChangeListener(new IValueChangeListener() {
					@Override
					public void handleValueChange(ValueChangeEvent arg0) {
						updateDynamoSimulationObject();
					}
				});
				// For all Integer writableValues
				log.debug("writableValue" + writableValue);
			}
		}
		return writableValue;
	}
/* chosenScenarioName is the just chosen text of a combibox, not relevant here as we do everything via
 * the dynamosimulation object 
 * name is the name of the box for which to get the dropdownset 
 * */
	public DropDownPropertiesSet getDropDownSet(String name,
			String chosenScenarioName) throws ConfigurationException,
			NoMoreDataException {
		log.debug("HIERALOOK");
		String chosenRiskFactorName = null;
		// The model object already exists, get the name
		if (singleConfiguration != null) {
			chosenRiskFactorName = this.getInitialRiskFactorName();
			log.debug("chosenRiskFactorName JUST CREATED"
					+ chosenRiskFactorName);
		}
		DropDownPropertiesSet set = new DropDownPropertiesSet();

		Set<String> contents = this.getContents(name, chosenRiskFactorName);

		// Contents can never be empty

		if (contents != null)
			set.addAll(contents);
		else throw new NoMoreDataException("no alternative prevalence or transition file" +
				" is availlable in the database. \nGo to the Reference_Data section and define such a file" +
				" for the chosen riskfactor");
	
		
		return set;
	}

	public DropDownPropertiesSet getRefreshedDropDownSet(String label)
			throws ConfigurationException, NoMoreDataException {
		return getDropDownSet(label, null);
	}

	public void removeFromDynamoSimulationObject()
			throws ConfigurationException {
		log.error("REMOVING OBJECT STATE");
		Map<String, ITabScenarioConfiguration> configurations = this.dynamoSimulationObject
		.getScenarioConfigurations();
		configurations.remove(this.singleConfiguration.getName());
		this.dynamoSimulationObject.setScenarioConfigurations(configurations);
	}

	public void removeOldDefaultValue(String label)
			throws ConfigurationException {
		// Will not be used

	}

	public void setDefaultValue(String name, String selectedValue)
			throws ConfigurationException {
		// Will not be used

	}

	/**
	 * This method only works for the dropdown-boxes.
	 */
	public void updateObjectState(String name, String selectedValue)
			throws ConfigurationException {
		log.debug(name + ": " + selectedValue);

		log.debug("UPDATING OBJECT STATE");
		// In case a new Tab is created, no model exists yet
		if (this.initialSelection == null && singleConfiguration == null) {
			log.debug("CREATING NEW TAB");
			createInDynamoSimulationObject();
		}
		log.debug("Going to update " + name + " in singleConfiguration to "
				+ selectedValue);
		if (ScenarioSelectionGroup.NAME.equals(name)) {
			singleConfiguration.setName(selectedValue);
		} else if (ScenarioSelectionGroup.SUCCESS_RATE.equals(name)) {
			singleConfiguration.setSuccessRate(new Integer(selectedValue));
		} else if (ScenarioSelectionGroup.MIN_AGE.equals(name)) {
			singleConfiguration.setMinAge(new Integer(selectedValue));
		} else if (ScenarioSelectionGroup.MAX_AGE.equals(name)) {
			singleConfiguration.setMaxAge(new Integer(selectedValue));
		} else if (ScenarioSelectionGroup.GENDER.equals(name)) {
			Integer gender = convertToInteger(selectedValue);
			singleConfiguration.setTargetSex(gender);
		} else if (ScenarioResultGroup.TRANSITION.equals(name)) {
			singleConfiguration.setAltTransitionFileName(selectedValue);
		} else if (ScenarioResultGroup.RISK_FACTOR_PREVALENCE.equals(name)) {
			singleConfiguration.setAltPrevalenceFileName(selectedValue);
		} else {
			log.fatal(name + " could not be handled!!!!!");
		}
		updateDynamoSimulationObject();
	}

	private Integer convertToInteger(String selectedValue) {
		log.debug("convertToInteger: selectedValue:: " + selectedValue);
		Integer gender = null;
		if (this.MALE.equals(selectedValue)) {
			gender = new Integer(0);
		} else if (this.FEMALE.equals(selectedValue)) {
			gender = new Integer(1);
		} else if (this.MALE_FEMALE.equals(selectedValue)) {
			gender = new Integer(2);
		}
		log.debug("convertToInteger:  gender:: " + gender);
		return gender;
	}

	private String convertToText(Integer gender) {
		String valueSelected = null;
		if (gender == null)
			return null;
		switch (gender.intValue()) {
		case 0:
			valueSelected = MALE;
			break;
		case 1:
			valueSelected = FEMALE;
			break;
		case 2:
			valueSelected = MALE_FEMALE;
			break;
		}
		return valueSelected;
	}

	public void updateDynamoSimulationObject() {
		log.error("UPDATING");
		log.debug("singleConfiguration" + singleConfiguration);
		log.debug("singleConfiguration.getName()"
				+ singleConfiguration.getName());
		Map<String, ITabScenarioConfiguration> configurations = this.dynamoSimulationObject
		.getScenarioConfigurations();
		configurations.put(singleConfiguration.getName(),
				singleConfiguration);
		this.dynamoSimulationObject.setScenarioConfigurations(configurations);

		/**
		 * TODO REMOVE: LOGGING BELOW
		 */
		Map map = this.dynamoSimulationObject.getScenarioConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			ITabScenarioConfiguration conf = (ITabScenarioConfiguration) map
					.get(key);
			log.error("conf.getName()" + conf.getName());
			log.error("conf.getMinAge()" + conf.getMinAge());
			log.error("conf.getMaxAge()" + conf.getMaxAge());
			log.error("conf.getTargetSex()" + conf.getTargetSex());
			log.error("conf.getAltTransitionFileName()"
					+ conf.getAltTransitionFileName());
			log.error("conf.getAltPrevalenceFileName()"
					+ conf.getAltPrevalenceFileName());
		}
		log.debug("configurations.size()" + configurations.size());
		/**
		 * TODO REMOVE: LOGGING ABOVE
		 */
	}

	private Object getInitialName() {
		String defaultScenarioName = null;
		if (this.initialSelection != null) {
			for (String chosenName : this.initialSelection) {
				defaultScenarioName = chosenName;
			}
		}
		return defaultScenarioName;
	}

	private String getInitialRiskFactorName() {
		String chosenRiskFactorName = null;
		Map map = this.dynamoSimulationObject.getRiskFactorConfigurations();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			TabRiskFactorConfigurationData conf = (TabRiskFactorConfigurationData) map
					.get(key);
			log.error("conf.getName()" + conf.getName());
			chosenRiskFactorName = conf.getName();
		}
		return chosenRiskFactorName;
	}
/* added by Hendriek: refreshes the single configuration, which is needed when tabs are deleted 
 * */
	 
		public void refreshConfigurations(String name) {
			Map<String, ITabScenarioConfiguration> configurations = this.dynamoSimulationObject
			.getScenarioConfigurations();
		   this.singleConfiguration = configurations.get(name);
		   log.fatal("singleconfiguration"+this.singleConfiguration);

		}
	
	
	private void createInDynamoSimulationObject() {
		this.singleConfiguration = new TabScenarioConfigurationData();
	}

	@Override
	public DynamoSimulationObject getDynamoSimulationObject() {
		// TODO Auto-generated method stub
		return this.getDynamoSimulationObject();
	}

}
