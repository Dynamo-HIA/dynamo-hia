package nl.rivm.emi.dynamo.data.objects.tabconfigs;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITabScenarioConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabStoreConfiguration;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.DALYWeightsFileName;
import nl.rivm.emi.dynamo.data.types.atomic.ExessMortFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IncFileName;
import nl.rivm.emi.dynamo.data.types.atomic.MaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.SuccessRate;
import nl.rivm.emi.dynamo.data.types.atomic.TargetMinAge;
import nl.rivm.emi.dynamo.data.types.atomic.TargetSex;
import nl.rivm.emi.dynamo.data.types.atomic.TransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

public class TabScenarioConfigurationData implements ITabScenarioConfiguration, ITabStoreConfiguration {
	Log log = LogFactory.getLog(this.getClass().getName());

	String name;
	Integer successRate;
	Integer minAge;
	Integer maxAge;
	Integer targetSex;
	String altTransitionFileName;
	String altPrevalenceFileName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Integer successRate) {
		this.successRate = successRate;
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
		this.altTransitionFileName = altTransitionFileName;
	}

	public String getAltPrevalenceFileName() {
		return altPrevalenceFileName;
	}

	public void setAltPrevalenceFileName(String altPrevalenceFileName) {
		this.altPrevalenceFileName = altPrevalenceFileName;
	}

	public void initialize(Object name, ArrayList<AtomicTypeObjectTuple> list) {
		setName((String) name);
		for (AtomicTypeObjectTuple tuple : list) {
			XMLTagEntity type = tuple.getType();
			if (type instanceof SuccessRate) {
				setSuccessRate((Integer) ((WritableValue) 
						tuple.getValue()).doGetValue());
			} else {
				if (type instanceof TargetMinAge) {
					setMinAge((Integer) ((WritableValue) 
							tuple.getValue()).doGetValue());
				} else {
					if (type instanceof MaxAge) {
						setMaxAge((Integer) ((WritableValue) 
								tuple.getValue()).doGetValue());
					} else {
						if (type instanceof TargetSex) {
							setTargetSex((Integer) ((WritableValue) 
									tuple.getValue()).doGetValue());
						} else {
							if (type instanceof PrevFileName) {
								setAltPrevalenceFileName((String) ((WritableValue) tuple
										.getValue()).doGetValue());
							} else {
								if (type instanceof TransFileName) {
									setAltTransitionFileName((String) ((WritableValue) tuple
											.getValue()).doGetValue());
								} else {
									log.fatal("Unexpected type \""
											+ type.getXMLElementName()
											+ "\" in getDiseasesConfigurations()");
								}
							}
						}
					}
				}
			}
		}
	}

	public TypedHashMap<? extends XMLTagEntity> putInTypedHashMap(
			TypedHashMap<? extends XMLTagEntity> theMap) {
		ArrayList<AtomicTypeObjectTuple> scenarioModelData = new ArrayList<AtomicTypeObjectTuple>();
		AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.SUCCESSRATE.getTheType(), new WritableValue(
						getSuccessRate(), Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TARGETMINAGE
				.getTheType(), new WritableValue(getMinAge(),
				Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TARGETMAXAGE
				.getTheType(), new WritableValue(getMaxAge(),
				Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TARGETSEX
				.getTheType(), new WritableValue(getTargetSex(),
				Integer.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.PREVFILENAME
				.getTheType(), new WritableValue(getAltPrevalenceFileName(),
				String.class));
		scenarioModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TRANSFILENAME
				.getTheType(), new WritableValue(getAltTransitionFileName(),
				String.class));
		scenarioModelData.add(tuple);
		theMap.put(name, scenarioModelData);
		return theMap;
	}
}
