package nl.rivm.emi.dynamo.data.factories;

/**
 * @author mondeelr
 * 
 *         Interface factories for categorical data must implement.
 */
public interface CategoricalFactory {

	/**
	 * @param numberOfCategories
	 *            the expected number of categories that the manufactured Object
	 *            must have.
	 */
	public void setNumberOfCategories(Integer numberOfCategories);
}
