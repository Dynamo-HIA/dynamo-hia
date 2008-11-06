package nl.rivm.emi.dynamo.databinding.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.conversion.IConverter;

public class AgeIntegerToStringConverter implements IConverter {
//	Log log = LogFactory.getLog(this.getClass());
	String debugString = "";

	public AgeIntegerToStringConverter(String debugString) {
		this.debugString = debugString;
	}

	public Object convert(Object arg0) {
//		log.debug(debugString + " convert(Object) entered with:" + arg0.toString());
		String integerString = ((Integer)arg0).toString();
		return integerString;
	}

	public Object getFromType() {
//		log.debug(debugString + " getFromType() entered.");
		return (Object) Integer.class;
	}

	public Object getToType() {
//		log.debug(debugString + " getToType() entered.");
		return (Object) String.class;
	}

}
