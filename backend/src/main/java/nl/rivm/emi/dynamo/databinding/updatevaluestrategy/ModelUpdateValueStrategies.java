package nl.rivm.emi.dynamo.databinding.updatevaluestrategy;

import nl.rivm.emi.dynamo.databinding.converters.SimpleModelConverterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class ModelUpdateValueStrategies {

	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.databinding.converters.ModelUpdateValueStrategies");
@SuppressWarnings("rawtypes")
	public static UpdateValueStrategy getStrategy(Object objectType) {
		UpdateValueStrategy resultStrategy = null;
		if ((objectType instanceof Integer) | (objectType.equals(Integer.class))) {
			log.debug("Constructing modelvalueupdatestrategy for "
					+ objectType.getClass().getName());
			resultStrategy = assembleIntegerModelValueUpdateStrategy(objectType);
		} else {
			if (objectType instanceof Float | (objectType.equals(Float.class))) {
				log.debug("Constructing modelvalueupdatestrategy for "
						+ objectType.getClass().getName());
				resultStrategy = assembleFloatModelValueUpdateStrategy(objectType);
			} else {
				log.error("No modelvalueupdatestrategy found for "
						+ objectType.getClass().getName() + " " + objectType, new Exception("whoops"));
			}
		}
		return resultStrategy;
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static UpdateValueStrategy assembleIntegerModelValueUpdateStrategy(
			Object type) {
	
		UpdateValueStrategy integerUpdateValueStrategy = new UpdateValueStrategy();
		integerUpdateValueStrategy.setConverter(SimpleModelConverterFactory
				.getConverter(type));
		return integerUpdateValueStrategy;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static UpdateValueStrategy assembleFloatModelValueUpdateStrategy(
			Object type) {
	
		UpdateValueStrategy floatUpdateValueStrategy = new UpdateValueStrategy();
		floatUpdateValueStrategy.setConverter(SimpleModelConverterFactory
				.getConverter(type));
		return floatUpdateValueStrategy;
	}
}
