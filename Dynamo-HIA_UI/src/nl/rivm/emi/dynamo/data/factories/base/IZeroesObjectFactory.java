package nl.rivm.emi.dynamo.data.factories.base;

/**
 * Interface that Classes must implement that deal with XML-configurations that 
 * can contain different structures inside the same rootelement.
 */

public interface IZeroesObjectFactory<S, O> {

	public S constructAllZeroesModel();

	public O constructObservableAllZeroesModel();

}