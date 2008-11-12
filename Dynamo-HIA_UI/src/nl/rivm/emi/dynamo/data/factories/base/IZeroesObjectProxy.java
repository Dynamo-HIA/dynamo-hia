package nl.rivm.emi.dynamo.data.factories.base;
/**
 * Interface that Classes must implement that deal with XML-configurations that 
 * can contain different structures inside the same rootelement.
 */

public interface IZeroesObjectProxy<S, O> {

	public S constructAllZeroesModel(String testElementName);

	public O constructObservableAllZeroesModel(String testElementName);

}