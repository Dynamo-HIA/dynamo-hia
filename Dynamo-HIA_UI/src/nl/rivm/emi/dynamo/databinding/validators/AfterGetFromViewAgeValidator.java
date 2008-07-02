package nl.rivm.emi.dynamo.databinding.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class AfterGetFromViewAgeValidator implements IValidator {
	private int MINIMUM_VALUE = 0;
	private int MAXIMUM_VALUE = 95;

	DataBindingContext myDataBindingContext = null;
	
	private AfterGetFromViewAgeValidator() {
	}

	public AfterGetFromViewAgeValidator(DataBindingContext dbc) {
		myDataBindingContext = dbc;
	}

	public IStatus validate(Object candidate) {
//		log.debug( debugInsert + " validate(Object) entered.");
		IStatus validationStatus = ValidationStatus.ok();
		if (candidate instanceof Integer) {
			validationStatus = validate((Integer) candidate);
		} else {
			if (candidate instanceof String) {
				validationStatus = validate((String) candidate);
			} else {
				validationStatus = ValidationStatus.error("Wrong Class >"
						+ candidate.getClass().getName() + "< for an age");
			}
		}
//		if(validationStatus.getSeverity() != ValidationStatus.OK){
//			myDataBindingContext.updateTargets();
//		}
		return validationStatus;
	}

	@SuppressWarnings("finally")
	private IStatus validate(String candidate) {
//		log.debug(debugInsert + " validate(String) entered:" + candidate);
		IStatus validationStatus = ValidationStatus.ok();
		try {
			if ("".equals(candidate)) {
				validationStatus = ValidationStatus
						.warning("Empty field is not allowed when running.");
			} else {
				Integer integerCandidate = Integer.decode((String) candidate);
				validationStatus = validate(integerCandidate);
			}
		} catch (NumberFormatException e) {
			validationStatus = ValidationStatus.error("Wrong String value >"
					+ candidate + "< for an Age");
		} finally {
			return validationStatus;
		}
	}

	private IStatus validate(Integer candidate) {
//		log.debug(debugInsert + " validate(Integer) entered: " + candidate.toString());
		IStatus validationStatus = ValidationStatus.ok();
		int intCandidate = candidate.intValue();
		if (!((intCandidate >= MINIMUM_VALUE) && (intCandidate <= MAXIMUM_VALUE))) {
			validationStatus = ValidationStatus.error("Wrong value >"
					+ ((Integer) candidate).toString() + "< for an Age");
		}
		return validationStatus;
	}
}
