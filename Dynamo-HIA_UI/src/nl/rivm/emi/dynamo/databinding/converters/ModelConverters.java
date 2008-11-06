package nl.rivm.emi.dynamo.databinding.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.conversion.IConverter;

public class ModelConverters {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.databinding.converters.ModelConverters");

	public static IConverter getConverter(Object objectType) {
		IConverter resultConverter = null;
		if (objectType instanceof Integer) {
			resultConverter = new IntegerModelConverter("");
			log.debug("Modelconverter constructed for "
					+ objectType.getClass().getName());
		} else {
			if (objectType instanceof Float) {
				resultConverter = new StandardFloatModelConverter("");
				log.debug("Modelconverter constructed for "
						+ objectType.getClass().getName());
			} else {
				log.error("No modelconverter found for "
						+ objectType.getClass().getName());
			}
		}
		return resultConverter;
	}
}
