package nl.rivm.emi.dynamo.data.objects.tabconfigs;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITabRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabStoreConfiguration;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.TransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TabRiskFactorConfigurationData implements
		ITabRiskFactorConfiguration, ITabStoreConfiguration {
	Log log = LogFactory.getLog(this.getClass().getName());

	String name;
	String prevalenceFileName;
	String transitionFileName;

	public void initialize(Object name,
			ArrayList<AtomicTypeObjectTuple> list) {
		if (name instanceof String) {
			this.name = (String) name;
log.debug("Intitializing, name: " + name);
			for (int count = 0; count < list.size(); count++) {
				AtomicTypeObjectTuple tuple = list.get(count);
				XMLTagEntity type = tuple.getType();
				if (type instanceof TransFileName) {
					transitionFileName = (String) ((WritableValue) tuple
							.getValue()).doGetValue();
					log.debug("Intitializing, transitionFileName: " + transitionFileName);
				} else {
					if (type instanceof PrevFileName) {
						prevalenceFileName = (String) ((WritableValue) tuple
								.getValue()).doGetValue();
						log.debug("Intitializing, prevalenceFileName: " + prevalenceFileName);
					} else {
						log.fatal("Unexpected type \""
								+ type.getXMLElementName()
								+ "\" in getDiseasesConfigurations()");
					}
				}
			}

		} else {
			log.fatal("The name should be a String.");
		}
	}

	public TypedHashMap<? extends XMLTagEntity> putInTypedHashMap(
			TypedHashMap<? extends XMLTagEntity> theMap) {
		ArrayList<AtomicTypeObjectTuple> diseaseModelData = new ArrayList<AtomicTypeObjectTuple>();
		AtomicTypeObjectTuple 
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.TRANSFILENAME
				.getTheType(), new WritableValue(getTransitionFileName(),
				String.class));
		diseaseModelData.add(tuple);		
		tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.PREVFILENAME.getTheType(), new WritableValue(
						getPrevalenceFileName(), String.class));
		diseaseModelData.add(tuple);
		theMap.put(name, diseaseModelData);
		return theMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #getPrevalenceFileName()
	 */
	public String getPrevalenceFileName() {
		return prevalenceFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #setPrevalenceFileName(java.lang.String)
	 */
	public void setPrevalenceFileName(String prevalenceFileName) {
		this.prevalenceFileName = prevalenceFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #getTransitionFileName()
	 */
	public String getTransitionFileName() {
		return transitionFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #setTransitionFileName(java.lang.String)
	 */
	public void setTransitionFileName(String transitionFileName) {
		this.transitionFileName = transitionFileName;
	}
}
