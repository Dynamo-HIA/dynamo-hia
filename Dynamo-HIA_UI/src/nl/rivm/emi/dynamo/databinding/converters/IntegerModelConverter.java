package nl.rivm.emi.dynamo.databinding.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.conversion.IConverter;

public class IntegerModelConverter implements IConverter {
//	Log log = LogFactory.getLog(this.getClass());
	String debugString = "";

	public IntegerModelConverter(String debugString) {
		this.debugString = debugString;
	}

	public Object convert(Object arg0) {
//		log.debug(debugString + " convert(Object) entered with:" + arg0.toString());
		Integer integerCandidate = Integer.decode((String) arg0);
		return integerCandidate;
	}

	public Object getFromType() {
//		log.debug(debugString + " getFromType() entered.");
		return (Object) String.class;
	}

	public Object getToType() {
//		log.debug(debugString + " getToType() entered.");
		return (Object) Integer.class;
	}

}
