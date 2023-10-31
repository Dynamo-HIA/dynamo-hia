package nl.rivm.emi.dynamo.databinding.converters;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author mondeelr
 * 
 *         Converts text entered through the view to a <code>Float</code> if
 *         possible.
 *         
 */
public class FloatModelConverter implements IConverter {
	// Log log = LogFactory.getLog(this.getClass());
	String debugString = "";

	/**
	 * @param debugString
	 */
	public FloatModelConverter(String debugString) {
		this.debugString = debugString;
	}

	public Object convert(Object arg0) {
		// log.debug(debugString + " convert(Object) entered with:" +
		// arg0.toString());
		try {
			Float floatCandidate = 4711F;
			if (arg0 instanceof String) {
				floatCandidate = Float.parseFloat((String) arg0);
			}
			return floatCandidate;
		} catch (Exception e) {
			return 4712F;
		}
	}

	public Object getFromType() {
		// log.debug(debugString + " getFromType() entered.");
		return (Object) String.class;
	}

	public Object getToType() {
		// log.debug(debugString + " getToType() entered.");
		return (Object) Float.class;
	}

}
