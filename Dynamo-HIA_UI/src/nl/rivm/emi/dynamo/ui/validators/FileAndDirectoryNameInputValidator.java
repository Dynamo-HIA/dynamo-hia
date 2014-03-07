package nl.rivm.emi.dynamo.ui.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

public class FileAndDirectoryNameInputValidator implements IInputValidator{


	
	Pattern  matchPattern = Pattern.compile("^[a-zA-Z]\\w*$");
	
	@Override
	public String isValid(String arg0) {
		String errorMessage = null;
		Matcher nameMatcher = matchPattern.matcher(arg0);
		boolean match = nameMatcher.matches();
		/*toegevoegd door hendriek in januari 2014 om te voorkomen dat verboden namen worden ingevoerd */
		if ( arg0.equalsIgnoreCase("disability")) match=false;
		if ( arg0.equalsIgnoreCase("totaldisease")) match=false;
		if (!match) {
			errorMessage = "The change you just made resulted in an invalid name!";
		}
		return errorMessage;
	}
}
