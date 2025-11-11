package nl.rivm.emi.dynamo.data.objects.tabconfigs;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabStoreConfiguration;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.SuccessRate;
import nl.rivm.emi.dynamo.data.types.atomic.TargetMaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.TargetMinAge;
import nl.rivm.emi.dynamo.data.types.atomic.TargetSex;
import nl.rivm.emi.dynamo.data.types.atomic.TransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TabScenarioConfigurationData implements ITabScenarioConfiguration,
		ITabStoreConfiguration {
	Log log = LogFactory.getLog(this.getClass().getName());

	@SuppressWarnings("rawtypes")
	WritableValue observableName;
	@SuppressWarnings("rawtypes")
	WritableValue observableSuccessRate;
	Integer minAge;
	Integer maxAge;
	Integer targetSex;
	String altTransitionFileName;
	String altPrevalenceFileName;

	public String getName() {
		return (String) observableName.doGetValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setName(String name) {
		if (this.observableName == null) {
			setObservableName(new WritableValue(name, String.class));	
		}
		observableName.doSetValue(name);
	}

	@SuppressWarnings("rawtypes")
	public WritableValue getObservableName() {
		return observableName;
	}

	public void setObservableName(@SuppressWarnings("rawtypes") WritableValue name) {		
		this.observableName = name;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setSuccessRate(Integer successRate) {
		if (this.observableSuccessRate == null) {
			setObservableSuccessRate(new WritableValue(successRate, Integer.class));	
		}
		observableSuccessRate.doSetValue(successRate);		
	}
	
	@SuppressWarnings("rawtypes")
	public WritableValue getObservableSuccessRate() {
		return observableSuccessRate;
	}

	public void setObservableSuccessRate(@SuppressWarnings("rawtypes") WritableValue successRate) {
		this.observableSuccessRate = successRate;
	}

	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public Integer getTargetSex() {
		return targetSex;
	}

	public void setTargetSex(Integer targetSex) {
		this.targetSex = targetSex;
	}

	public String getAltTransitionFileName() {
		return altTransitionFileName;
	}

	public void setAltTransitionFileName(String altTransitionFileName) {
		log.debug(this + " Setting alternative transition-filename to: " + altTransitionFileName);
		this.altTransitionFileName = altTransitionFileName;
	}

	public String getAltPrevalenceFileName() {
		return altPrevalenceFileName;
	}

	public void setAltPrevalenceFileName(String altPrevalenceFileName) {
		this.altPrevalenceFileName = altPrevalenceFileName;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initialize(Object name, ArrayList<AtomicTypeObjectTuple> list) {
		log.debug("Initializing: " + this);
		setObservableName(new WritableValue(name, String.class));
		for (AtomicTypeObjectTuple tuple : list) {
			XMLTagEntity type = tuple.getType();
			if (type instanceof SuccessRate) {
				setObservableSuccessRate((WritableValue) tuple.getValue());
			} else {
				if (type instanceof TargetMinAge) {
					setMinAge((Integer) ((WritableValue) tuple.getValue())
							.doGetValue());
				} else {
					if (type instanceof TargetMaxAge) {
						setMaxAge((Integer) ((WritableValue) tuple.getValue())
								.doGetValue());
					} else {
						if (type instanceof TargetSex) {
							setTargetSex((Integer) ((WritableValue) tuple
									.getValue()).doGetValue());
						} else {
							if (type instanceof TransFileName) {
								
								log.debug("Alternative transition-file name: " + 
										((WritableValue) tuple
										.getValue()).doGetValue());
								
								setAltTransitionFileName((String) ((WritableValue) tuple
										.getValue()).doGetValue());
							} else {
								if (type instanceof PrevFileName) {
	
									log.debug("Alternative prevalence-file name: " + 
											((WritableValue) tuple
											.getValue()).doGetValue());
									
									setAltPrevalenceFileName((String) ((WritableValue) tuple
											.getValue()).doGetValue());
								} else {
									log
											.fatal("Unexpected type \""
													+ type.getXMLElementName()
													+ "\" initializing " + this.getClass().getSimpleName());
								}
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TypedHashMap<? extends XMLTagEntity> putInTypedHashMap(
			TypedHashMap<? extends XMLTagEntity> theMap) {
		ArrayList<AtomicTypeObjectTuple> scenarioModelData = new ArrayList<AtomicTypeObjectTuple>();
		AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.SUCCESSRATE.getTheType(),
				getObservableSuccessRate());
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TARGETMINAGE
				.getTheType(), new WritableValue(getMinAge(), Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TARGETMAXAGE
				.getTheType(), new WritableValue(getMaxAge(), Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TARGETSEX
				.getTheType(), new WritableValue(getTargetSex(), Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TRANSFILENAME
				.getTheType(), new WritableValue(getAltTransitionFileName(),
				String.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.PREVFILENAME
				.getTheType(), new WritableValue(getAltPrevalenceFileName(),
				String.class));
		scenarioModelData.add(tuple);
		
		String name = (String) observableName.doGetValue();
		theMap.put(name, scenarioModelData);
		return theMap;
	}
	
}
