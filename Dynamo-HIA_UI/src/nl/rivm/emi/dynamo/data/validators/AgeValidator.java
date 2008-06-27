package nl.rivm.emi.dynamo.data.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class AgeValidator implements IValidator {
	private int MINIMUM_VALUE = 0;
	private int MAXIMUM_VALUE = 95;

	public AgeValidator() {
	}

	public IStatus validate(Object candidate) {
		IStatus validationStatus = ValidationStatus.ok();
		if (candidate instanceof Integer) {
			validationStatus = validate((Integer)candidate);
		} else {
			if (candidate instanceof String) {
				validationStatus = validate((String)candidate);
			} else {
				validationStatus = ValidationStatus.error("Wrong Class >"
						+ candidate.getClass().getName() + "< for an age");
			}
		}
		return validationStatus;
	}

	@SuppressWarnings("finally")
	private IStatus validate(String candidate) {
		IStatus validationStatus = ValidationStatus.ok();
		try {
			Integer integerCandidate = Integer.decode((String) candidate);
			validationStatus = validate(integerCandidate);
		} catch (NumberFormatException e) {
			validationStatus = ValidationStatus.error("Wrong String value >"
					+ candidate + "< for an Age");
		} finally {
			return validationStatus;
		}
	}

	private IStatus validate(Integer candidate) {
		IStatus validationStatus = ValidationStatus.ok();
		int intCandidate = candidate.intValue();
		if (!((intCandidate >= MINIMUM_VALUE) && (intCandidate <= MAXIMUM_VALUE))) {
			validationStatus = ValidationStatus.error("Wrong value >"
					+ ((Integer) candidate).toString() + "< for an Age");
		}
		return validationStatus;
	}
}
