package nl.rivm.emi.dynamo.ui.parametercontrols.prototype.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.dynamo.databinding.validators.AfterGetFromViewAgeValidator;
import nl.rivm.emi.dynamo.ui.parametercontrols.AgeGenderList;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.DemoTableViewer;
import nl.rivm.emi.dynamo.ui.parametercontrols.YearIntegerDataRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.YearIntegerList;
import nl.rivm.emi.dynamo.ui.parametercontrols.DemoTableViewer.DummyElement;
import nl.rivm.emi.dynamo.ui.parametercontrols.prototype.AgeGenderDatabindingPrototype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
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

public class TestAgeGenderList {
//	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;
	Composite container = null;

	@Before
	public void setup() {
		display = new Display();
		shell = new Shell(display);
		shell.setSize(400, 400);
		shell.setLayout(new FormLayout());
		container = new Composite(shell, SWT.NONE);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		container.setLayoutData(formData);
		FillLayout fillLayout = new FillLayout();
		container.setLayout(fillLayout);
		container.setBackground(new Color(null, 0x00, 0x00, 0xee));
	}

	@After
	public void teardown() {
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	@Test
	public void runSnippet11() {
		Runnable snippet11 = new AgeGenderDatabindingPrototype(container);
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				snippet11);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.dynamo.ui.parametercontrols.prototype.test.TestAgeGenderList.class);
	}

}