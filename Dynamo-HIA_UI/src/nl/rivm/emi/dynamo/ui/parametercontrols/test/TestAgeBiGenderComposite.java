package nl.rivm.emi.dynamo.ui.parametercontrols.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.data.factories.notinuse.SomethingPerAgeDataFromXMLFactory;
import nl.rivm.emi.dynamo.ui.main.BaseScreen;
import nl.rivm.emi.dynamo.ui.parametercontrols.AgeBiGenderModal;
import nl.rivm.emi.dynamo.ui.parametercontrols.AgeBiGenderRunnable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
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

public class TestAgeBiGenderComposite {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;
	Composite container = null;
	AgeSteppedContainer<BiGenderSteppedContainer<Integer>> testModel;

	@Before
	public void setup() {
		display = new Display();
		shell = new Shell(display);
		shell.setSize(400, 400);
		shell.setLayout(new FormLayout());
		instantiateContainer();
		manufactureModel();
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

	public void manufactureModel() {
		String configurationFilePath = "datatemplates" + File.separator
				+ "5agestep_2gender_popsize.xml";
		File configurationFile = new File(configurationFilePath);
		log.fatal(configurationFile.getAbsolutePath());
		testModel = SomethingPerAgeDataFromXMLFactory
				.manufacture(configurationFile);
		assertNotNull(testModel);

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

	@Test
	public void testAgeBiGenderComposite() {
		shell.setText("AgeBiGenderComposite");
		AgeBiGenderRunnable testComposite = new AgeBiGenderRunnable(
				container, SWT.V_SCROLL, testModel);
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				testComposite);
	}
}