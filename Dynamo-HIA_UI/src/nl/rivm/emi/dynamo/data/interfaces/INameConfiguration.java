package nl.rivm.emi.dynamo.data.interfaces;

/**
 * @author schutb
 *
 */
public interface INameConfiguration {
	/**
	 * @return The key name of the object that inherits this interface
	 */
	public String getName();

	/**
	 * @param name
	 */
	public void setName(String name);
}
