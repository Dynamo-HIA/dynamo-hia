package nl.rivm.emi.dynamo.databinding.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.conversion.IConverter;

public class SimpleModelConverterFactory {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.databinding.converters.ModelConverters");

	public static IConverter getConverter(Object objectType) {
		IConverter resultConverter = null;
		//FIXME: Fix this properly
		if (objectType instanceof Integer | (objectType.equals(Integer.class))) {
			resultConverter = new IntegerModelConverter("");
			log.debug("Modelconverter constructed for "
					+ objectType);
		} else {
			if (objectType instanceof Float | (objectType.equals(Float.class))) {
				resultConverter = new StandardFloatModelConverter("");
				log.debug("Modelconverter constructed for "
						+ objectType.getClass().getName());
			} else {
				log.error("No modelconverter found for "
						+ objectType.getClass().getName());
				//FIXME:Remove
				throw new Error("No modelconverter found for "+ objectType.getClass().getName());
			}
		}
		return resultConverter;
	}
}
