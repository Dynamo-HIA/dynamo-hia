package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;

public class DiseaseConfigurationData implements IDiseaseConfiguration {
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
}

