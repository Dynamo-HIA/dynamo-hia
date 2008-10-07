package nl.rivm.emi.dynamo.databinding.updatevaluestrategy;

import nl.rivm.emi.dynamo.databinding.converters.ModelConverters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class ModelUpdateValueStrategies {

	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.databinding.converters.ModelUpdateValueStrategies");

	public static UpdateValueStrategy getStrategy(Object objectType) {
		UpdateValueStrategy resultStrategy = null;
		if (objectType instanceof Integer) {
			log.debug("Constructing modelvalueupdatestrategy for "
					+ objectType.getClass().getName());
			resultStrategy = assembleIntegerModelValueUpdateStrategy(objectType);
		} else {
			if (objectType instanceof Float) {
				log.debug("Constructing modelvalueupdatestrategy for "
						+ objectType.getClass().getName());
				resultStrategy = assembleFloatModelValueUpdateStrategy(objectType);
			} else {
				log.error("No modelvalueupdatestrategy found for "
						+ objectType.getClass().getName());
			}
		}
		return resultStrategy;
	}

	private static UpdateValueStrategy assembleIntegerModelValueUpdateStrategy(
			Object type) {
		UpdateValueStrategy integerUpdateValueStrategy = new UpdateValueStrategy();
		integerUpdateValueStrategy.setConverter(ModelConverters
				.getConverter(type));
		return integerUpdateValueStrategy;
	}

	private static UpdateValueStrategy assembleFloatModelValueUpdateStrategy(
			Object type) {
		UpdateValueStrategy floatUpdateValueStrategy = new UpdateValueStrategy();
		floatUpdateValueStrategy.setConverter(ModelConverters
				.getConverter(type));
		return floatUpdateValueStrategy;
	}
}
