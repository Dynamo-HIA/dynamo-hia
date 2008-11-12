package nl.rivm.emi.dynamo.data.factories.base;

public interface Factory<S, O> extends IObjectFromXMLFactory<S, O>,
		IZeroesObjectFactory<S, O> {
}
