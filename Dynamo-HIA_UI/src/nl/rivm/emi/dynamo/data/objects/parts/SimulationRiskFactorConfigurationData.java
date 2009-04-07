package nl.rivm.emi.dynamo.data.objects.parts;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.WritableValue;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ISimulationRiskFactorConfiguration;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.TransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Transition;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

public class SimulationRiskFactorConfigurationData implements
		ISimulationRiskFactorConfiguration {

	String name;
	WritableValue prevalenceFileName;
	WritableValue transitionFileName;

	public SimulationRiskFactorConfigurationData(
			TypedHashMap<UniqueName> dataFromModelObject) {
		super();
		if (dataFromModelObject.size() == 1) {
			Set keySet = dataFromModelObject.keySet();
			for (Object key : keySet) {
				if (key instanceof String) {
					name = (String) key;
					ArrayList<AtomicTypeObjectTuple> parameters = (ArrayList<AtomicTypeObjectTuple>) dataFromModelObject
							.get(key);
					for (int count = 0; count < parameters.size(); count++) {
						AtomicTypeObjectTuple tuple = parameters.get(count);
						XMLTagEntity type = tuple.getType();
						if (type instanceof PrevFileName) {
							prevalenceFileName = (WritableValue) tuple
									.getValue();
						} else {
							if (type instanceof TransFileName) {
								transitionFileName = (WritableValue) tuple
										.getValue();
							} else {
							}
						}
					}
				}
			}
		} else {
			// TODO(mondeelr) Panic.
		}
	}

	public TypedHashMap<UniqueName> createTypedHashMap() {
		AtomicTypeObjectTuple prevFileNameTuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.PREVFILENAME.getTheType(), prevalenceFileName);
		AtomicTypeObjectTuple transFileNameTuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.TRANSFILENAME.getTheType(), prevalenceFileName);
		ArrayList<AtomicTypeObjectTuple> theList = new ArrayList<AtomicTypeObjectTuple>();
		theList.add(prevFileNameTuple);
		theList.add(transFileNameTuple);
		TypedHashMap<UniqueName> theMap = new TypedHashMap<UniqueName>();
		theMap.put(name, theList);
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
	public WritableValue getPrevalenceFileName() {
		return prevalenceFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #setPrevalenceFileName(java.lang.String)
	 */
	public void setPrevalenceFileName(WritableValue prevalenceFileName) {
		this.prevalenceFileName = prevalenceFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #getTransitionFileName()
	 */
	public WritableValue getTransitionFileName() {
		return transitionFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.rivm.emi.dynamo.data.objects.parts.ISimulationRiskFactorConfiguration
	 * #setTransitionFileName(java.lang.String)
	 */
	public void setTransitionFileName(WritableValue transitionFileName) {
		this.transitionFileName = transitionFileName;
	}
}
