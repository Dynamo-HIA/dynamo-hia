package nl.rivm.emi.dynamo.datahandling;

public interface IRiskFactorConfiguration {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRiskFactorConfigurationData#getName()
	 */
	public abstract String getName();

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRiskFactorConfigurationData#setName(java.lang.String)
	 */
	public abstract void setName(String name);

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRiskFactorConfigurationData#getType()
	 */
	public abstract String getType();

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.data.types.functional.IRiskFactorConfigurationData#setType(java.lang.String)
	 */
	public abstract void setType(String type);

}