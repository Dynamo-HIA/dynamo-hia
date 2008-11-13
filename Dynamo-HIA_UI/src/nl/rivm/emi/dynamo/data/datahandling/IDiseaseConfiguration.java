package nl.rivm.emi.dynamo.datahandling;

/**
 * Interface for use by the User Interface and the Storage Interface.
 * 
 * @author mondeelr
 * 
 */
public interface IDiseaseConfiguration {
	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getPrevalenceFileName();

	public abstract void setPrevalenceFileName(String prevalenceFileName);

	public abstract String getIncidenceFileName();

	public abstract void setIncidenceFileName(String incidenceFileName);

	public abstract String getExcessMortalityFileName();

	public abstract void setExcessMortalityFileName(
			String excessMortalityFileName);

	public abstract String getDalyWeightsFileName();

	public abstract void setDalyWeightsFileName(String dalyWeightsFileName);

}