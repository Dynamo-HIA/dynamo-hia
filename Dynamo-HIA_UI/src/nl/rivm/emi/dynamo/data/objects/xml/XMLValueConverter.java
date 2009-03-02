package nl.rivm.emi.dynamo.data.objects.xml;


public interface XMLValueConverter<T> {

	T convert(String valueString);

	String streamValue(T value);
}
