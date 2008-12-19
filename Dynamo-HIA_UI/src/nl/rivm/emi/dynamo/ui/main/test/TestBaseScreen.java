package nl.rivm.emi.dynamo.ui.main.test;

import nl.rivm.emi.dynamo.ui.main.BaseScreen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBaseScreen {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;
	Composite container = null;

	@Before
	public void setup() {
		display = new Display();
	}

	private void instantiateContainer() {
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
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

//	@Test
//	public void testAgeBiGenderModal() {
//		shell.setText("AgeBiGenderModal");
//		AgeBiGenderModal testComposite = new AgeBiGenderModal(
//				shell, testModel);
//		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
//				testComposite);
//	}

	@Test
	public void testBaseScreen() {
	BaseScreen application = new BaseScreen();
	shell = application.open(display);
}
}