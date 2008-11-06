package nl.rivm.emi.dynamo.databinding.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.conversion.IConverter;

public class ViewConverters {

	static Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.databinding.converters.ViewConverters");

	public static IConverter getConverter(Object objectType) {
		IConverter resultConverter = null;
		if (objectType instanceof Integer) {
			resultConverter = new IntegerViewConverter("");
			log.debug("Viewconverter constructed for "
					+ objectType.getClass().getName());
		} else {
			if (objectType instanceof Float) {
				resultConverter = new StandardFloatViewConverter("");
				log.debug("Viewconverter constructed for "
						+ objectType.getClass().getName());
			} else {
				log.error("No viewconverter found for "
						+ objectType.getClass().getName());
			}
		}
		return resultConverter;
	}
}
