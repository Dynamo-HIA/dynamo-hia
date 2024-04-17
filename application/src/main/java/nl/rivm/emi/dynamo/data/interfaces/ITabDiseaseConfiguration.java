package nl.rivm.emi.dynamo.data.interfaces;

/**
 * Interface for use by the User Interface and the Storage Interface.
 * 
 * @author mondeelr
 * 
 */
public interface ITabDiseaseConfiguration extends INameConfiguration {

	/**
	 * @return String The prevalence file name
	 */
	public String getPrevalenceFileName();

	/**
	 * @param prevalenceFileName
	 */
	public void setPrevalenceFileName(String prevalenceFileName);

	/**
	 * @return String The incidence file name
	 */
	public String getIncidenceFileName();

	/**
	 * @param incidenceFileName
	 */
	public void setIncidenceFileName(String incidenceFileName);

	/**
	 * @return String The excess mortality file name
	 */
	public String getExcessMortalityFileName();

	/**
	 * @param excessMortalityFileName
	 */
	public void setExcessMortalityFileName(
			String excessMortalityFileName);

	/**
	 * @return String daly weights file name
	 */
	public String getDalyWeightsFileName();

	
	/**
	 * @param dalyWeightsFileName
	 */
	public void setDalyWeightsFileName(String dalyWeightsFileName);

	public String getValueForDropDown(String name);
	
	public void setValueFromDropDown(String name, String value);

}