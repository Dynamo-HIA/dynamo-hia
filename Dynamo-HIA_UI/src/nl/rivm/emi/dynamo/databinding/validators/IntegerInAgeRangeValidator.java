package nl.rivm.emi.dynamo.databinding.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class IntegerInAgeRangeValidator {
	static private int MINIMUM_VALUE = 0;
	static private int MAXIMUM_VALUE = 95;

	static public boolean validate(Integer value) {
		boolean isValid = false;
		int intCandidate = value.intValue();
		if (((intCandidate >= IntegerInAgeRangeValidator.MINIMUM_VALUE) && (intCandidate <= IntegerInAgeRangeValidator.MAXIMUM_VALUE))) {
			isValid = true;
		}
		return isValid;
		}
}
