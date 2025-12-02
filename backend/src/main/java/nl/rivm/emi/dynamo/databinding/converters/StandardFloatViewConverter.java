package nl.rivm.emi.dynamo.databinding.converters;

import org.eclipse.core.databinding.conversion.IConverter;

@SuppressWarnings("rawtypes")
public class StandardFloatViewConverter implements IConverter {
	// Log log = LogFactory.getLog(this.getClass());
	String debugString = "";

	public StandardFloatViewConverter(String debugString) {
		this.debugString = debugString;
	}

	public Object convert(Object arg0) {
		// log.debug(debugString + " convert(Object) entered with:" +
		// arg0.toString());
		String floatString = "NoFloat";
		try {
			if (arg0 == null) {
				floatString = "";
			} else {
				if (arg0 instanceof Float) {
					floatString = ((Float) arg0).toString();
				}
			}
			return floatString;
		} catch (Exception e) {
			floatString = e.getClass().getName();
			return floatString;
		}
	}

	public Object getFromType() {
		// log.debug(debugString + " getFromType() entered.");
		return (Object) Float.class;
	}

	public Object getToType() {
		// log.debug(debugString + " getToType() entered.");
		return (Object) String.class;
	}
}
