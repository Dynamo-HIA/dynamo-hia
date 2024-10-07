package nl.rivm.emi.dynamo.data.interfaces;

public interface ITabRelativeRisksConfiguration {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRelativeRiskConfigurationData#getFrom()
	 */
	public abstract String getFrom();

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRelativeRiskConfigurationData#setFrom(java.lang.String)
	 */
	public abstract void setFrom(String from);

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRelativeRiskConfigurationData#getTo()
	 */
	public abstract String getTo();

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRelativeRiskConfigurationData#setTo(java.lang.String)
	 */
	public abstract void setTo(String to);

	public abstract String getDataFileName();

	public abstract void setDataFileName(String dataFileName);

}