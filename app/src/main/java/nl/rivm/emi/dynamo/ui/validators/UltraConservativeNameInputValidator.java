package nl.rivm.emi.dynamo.ui.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

public class UltraConservativeNameInputValidator implements IInputValidator{


	
	Pattern  matchPattern = Pattern.compile("^durationprevalence$");
	
	@Override
	public String isValid(String arg0) {
		String errorMessage = null;
		Matcher nameMatcher = matchPattern.matcher(arg0);
		boolean match = nameMatcher.matches();
		if (!match) {
			errorMessage = "The change you just made resulted in an invalid name!"+"\nThe only valid name here is: durationprevalence";
		}
		return errorMessage;
	}
}
