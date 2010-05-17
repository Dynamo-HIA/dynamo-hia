package nl.rivm.emi.cdm.characteristic.values;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mondeelr
 * 
 *         Contains methods for parsing Integer or Float values from a String.
 *         The methods pretest the String using regular expressions and log an
 *         error when this pretest fails. Parsing should then always succeed.
 *         When it doesn't anyway a fatal level is logged.
 * 
 *         When the pretest or the parsing fails a null Object is returned.
 */
public class CharacteristicValueStringParser {
	static Log log = LogFactory
			.getLog("nl.rivm.emi.cdm.characteristic.CSVCharacteristicValueReader");
	static String floatRegex1 = "^\\d++\\.?\\d*$";
	static Pattern floatPattern1 = Pattern.compile(floatRegex1);
	static String floatRegex2 = "^\\.\\d*$";
	static Pattern floatPattern2 = Pattern.compile(floatRegex2);
	static String intRegex = "^\\d++$";
	static Pattern intPattern = Pattern.compile(intRegex);

	/**
	 * Tries to parse the String submitted to it to an Integer.
	 * 
	 * @param characteristicValueString
	 * @return The parsing result, null when parsing didn't succeed.
	 */
	public static Integer parseStringToInteger(String characteristicValueString) {
		Integer resultNumber = null;
		Matcher intMatcher = intPattern.matcher(characteristicValueString);
		boolean intSuccess = intMatcher.matches();
		try {
			if (intSuccess) {
				resultNumber = Integer.parseInt(characteristicValueString);
			} else {
				log.error("String: " + characteristicValueString
						+ " is not acceptable for an Integer.");
			}
		} catch (NumberFormatException e) {
			log.fatal("String: " + characteristicValueString
					+ " matches but can't be parsed to an Integer.");
		}
		return resultNumber;
	}

	/**
	 * Tries to parse the String submitted to it to a Float.
	 * 
	 * @param characteristicValueString
	 * @return The parsing result, null when parsing didn't succeed.
	 */
	public static Float parseStringToFloat(String characteristicValueString) {
		Float resultNumber = null;
		Matcher floatMatcher1 = floatPattern1.matcher(characteristicValueString);
		boolean floatSuccess1 = floatMatcher1.matches();
		Matcher floatMatcher2 = floatPattern2.matcher(characteristicValueString);
		boolean floatSuccess2 = floatMatcher2.matches();
		try {
			if (floatSuccess1||floatSuccess2) {
				resultNumber = Float.parseFloat(characteristicValueString);
			} else {
				log.error("String: " + characteristicValueString
						+ " is not acceptable for a Float.");
			}
		} catch (NumberFormatException e) {
			log.error("String: " + characteristicValueString
					+ " matches but can't be parsed to a Float.");
		}
		return resultNumber;
	}
}
