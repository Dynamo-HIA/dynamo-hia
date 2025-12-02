package nl.rivm.emi.dynamo.databinding.updatevaluestrategy;

import nl.rivm.emi.dynamo.databinding.converters.SimpleViewConverterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.UpdateValueStrategy;

public class ViewUpdateValueStrategies {

	static Log log = LogFactory.getLog("nl.rivm.emi.dynamo.databinding.converters.ViewUpdateValueStrategies");

	@SuppressWarnings("rawtypes")
	public static UpdateValueStrategy getStrategy(Object objectType) {
		UpdateValueStrategy resultStrategy = null;
		if (objectType instanceof Integer | (objectType.equals(Integer.class))) {
			log.debug("Constructing viewvalueupdatestrategy for " + objectType.getClass().getName());
			resultStrategy = assembleIntegerViewValueUpdateStrategy(objectType);
		} else {
			log.error("No viewvalueupdatestrategy found for " + objectType.getClass().getName());
		}
		return resultStrategy;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static UpdateValueStrategy assembleIntegerViewValueUpdateStrategy(Object type) {
		UpdateValueStrategy integerUpdateValueStrategy = new UpdateValueStrategy();
		integerUpdateValueStrategy.setConverter(SimpleViewConverterFactory.getConverter(type));	
		return integerUpdateValueStrategy;
	}

}
