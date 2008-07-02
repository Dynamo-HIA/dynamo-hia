package nl.rivm.emi.dynamo.databinding.validators.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.rivm.emi.dynamo.databinding.validators.AfterGetFromViewAgeValidator;
import nl.rivm.emi.dynamo.ui.parametercontrols.AgeGenderList;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.DemoTableViewer;
import nl.rivm.emi.dynamo.ui.parametercontrols.YearIntegerDataRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.YearIntegerList;
import nl.rivm.emi.dynamo.ui.parametercontrols.DemoTableViewer.DummyElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestValidators {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void ageValidatorNoValueString() {
		AfterGetFromViewAgeValidator ageValidator = new AfterGetFromViewAgeValidator();
		IStatus validationStatus = (IStatus) ageValidator.validate("Aap");
		assertTrue( validationStatus  instanceof IStatus);
		assertFalse(validationStatus .isOK());
	}
	
	@Test
	public void ageValidatorWrongValueString() {
		AfterGetFromViewAgeValidator ageValidator = new AfterGetFromViewAgeValidator();
		IStatus validationStatus = (IStatus) ageValidator.validate("101");
		assertTrue( validationStatus instanceof IStatus);
		assertFalse(validationStatus.isOK());
	}
	
	@Test
	public void ageValidatorRightValueString() {
		AfterGetFromViewAgeValidator ageValidator = new AfterGetFromViewAgeValidator();
		IStatus validationStatus = (IStatus) ageValidator.validate("42");
		assertTrue( validationStatus instanceof IStatus);
		assertTrue(validationStatus.isOK());
	}
	
	@Test
	public void ageValidatorWrongInteger() {
		AfterGetFromViewAgeValidator ageValidator = new AfterGetFromViewAgeValidator();
		IStatus validationStatus = (IStatus) ageValidator.validate(new Integer(101));
		assertTrue( validationStatus instanceof IStatus);
		assertFalse(validationStatus.isOK());
	}
	
	@Test
	public void ageValidatorRightInteger() {
		AfterGetFromViewAgeValidator ageValidator = new AfterGetFromViewAgeValidator();
		IStatus validationStatus = (IStatus) ageValidator.validate(new Integer(42));
		assertTrue( validationStatus instanceof IStatus);
		assertTrue(validationStatus.isOK());
	}
}