package nl.rivm.emi.dynamo.data.objects.tabconfigs;

import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITabDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabStoreConfiguration;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.DALYWeightsFileName;
import nl.rivm.emi.dynamo.data.types.atomic.ExessMortFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IncFileName;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TabDiseaseConfigurationData implements ITabDiseaseConfiguration, ITabStoreConfiguration {

	Log log = LogFactory.getLog(this.getClass().getName());

	String name;
	String prevalenceFileName;
	String incidenceFileName;
	String excessMortalityFileName;
	String dalyWeightsFileName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrevalenceFileName() {
		return prevalenceFileName;
	}

	public void setPrevalenceFileName(String prevalenceFileName) {
		this.prevalenceFileName = prevalenceFileName;
	}

	public String getIncidenceFileName() {
		return incidenceFileName;
	}

	public void setIncidenceFileName(String incidenceFileName) {
		this.incidenceFileName = incidenceFileName;
	}

	public String getExcessMortalityFileName() {
		return excessMortalityFileName;
	}

	public void setExcessMortalityFileName(String excessMortalityFileName) {
		this.excessMortalityFileName = excessMortalityFileName;
	}

	public String getDalyWeightsFileName() {
		return dalyWeightsFileName;
	}

	public void setDalyWeightsFileName(String dalyWeightsFileName) {
		this.dalyWeightsFileName = dalyWeightsFileName;
	}

	public void initialize(Object name, ArrayList<AtomicTypeObjectTuple> modelDiseaseData) {
		setName((String) name);
		for (AtomicTypeObjectTuple tuple : modelDiseaseData) {
			XMLTagEntity type = tuple.getType();
			
			/*
			
			if (type instanceof UniqueName) {
				setName( (String) ((WritableValue) 
						tuple.getValue()).doGetValue());
			} else {
				
				*/
				
			if (type instanceof PrevFileName) {
				setPrevalenceFileName( (String) ((WritableValue) 
						tuple.getValue()).doGetValue());
			} else {
				if (type instanceof IncFileName) {
					setIncidenceFileName( (String) ((WritableValue) 
							tuple.getValue()).doGetValue());
				} else {
					if (type instanceof ExessMortFileName) {
						setExcessMortalityFileName( (String) ((WritableValue) 
								tuple.getValue()).doGetValue());
					} else {
						if (type instanceof DALYWeightsFileName) {
							setDalyWeightsFileName( (String) ((WritableValue) 
									tuple.getValue()).doGetValue());
						} else {
							log.fatal("Unexpected type \""
									+ type.getXMLElementName()
									+ "\" in getDiseasesConfigurations()");
						}
					}
				}
			}
		}
		
			
		//}
		
		
	}

	public TypedHashMap<? extends XMLTagEntity> putInTypedHashMap(
			TypedHashMap<? extends XMLTagEntity> diseasesMap) {
		ArrayList<AtomicTypeObjectTuple> diseaseModelData = new ArrayList<AtomicTypeObjectTuple>();
		
		//FIX: UNIQUE NAME HAS TO BE INCLUDED TOO
		/*
		AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.UNIQUENAME.getTheType(),
				new WritableValue(getPrevalenceFileName(),
						String.class));
		diseaseModelData.add(tuple);*/
		AtomicTypeObjectTuple
		tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.PREVFILENAME.getTheType(),
				new WritableValue(getPrevalenceFileName(),
						String.class));
		diseaseModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.INCFILENAME
				.getTheType(), new WritableValue(getIncidenceFileName(), String.class));
		diseaseModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.EXESSMORTFILENAME.getTheType(),
				new WritableValue(getExcessMortalityFileName(),
						String.class));
		diseaseModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.DALYWEIGHTSFILENAME.getTheType(),
				new WritableValue(getDalyWeightsFileName(),
						String.class));
		diseaseModelData.add(tuple);
		diseasesMap.put(name, diseaseModelData);
		return diseasesMap;
	}
}

