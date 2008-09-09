package nl.rivm.emi.dynamo.ui.parametercontrols.test;

import nl.rivm.emi.dynamo.ui.parametercontrols.AgeBiGenderList;
import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.DemoTableViewer;
import nl.rivm.emi.dynamo.ui.parametercontrols.YearIntegerDataRow;
import nl.rivm.emi.dynamo.ui.parametercontrols.YearIntegerList;
import nl.rivm.emi.dynamo.ui.parametercontrols.DemoTableViewer.DummyElement;
import nl.rivm.emi.dynamo.ui.parametercontrols.prototype.test.ScrollListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class TestAgeBiGenderList {
	Log log = LogFactory.getLog(getClass().getName());
	Display display = null;
	Shell shell = null;
	ScrolledComposite scrolledContainer = null;


	@Before
	public void setup() {
		display = new Display();
		shell = new Shell(display);
		shell.setSize(400, 400);
		shell.setLayout(new FormLayout());
		scrolledContainer = new ScrolledComposite(shell, SWT.BORDER
			        | SWT.H_SCROLL | SWT.V_SCROLL);
			 
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		scrolledContainer.setLayoutData(formData);
		FillLayout fillLayout = new FillLayout();
		scrolledContainer.setLayout(fillLayout);
		scrolledContainer.setBackground(new Color(null, 0x00, 0x00, 0xee));
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
	public void ageGenderList() {
		DatabindableAgeGenderRow[] rows = { new DatabindableAgeGenderRow(),
				new DatabindableAgeGenderRow(), new DatabindableAgeGenderRow(),
				new DatabindableAgeGenderRow(), new DatabindableAgeGenderRow(),
				new DatabindableAgeGenderRow(), new DatabindableAgeGenderRow(),
				new DatabindableAgeGenderRow() };
		shell.setText("AgeGenderList");
		AgeBiGenderList testComposite = new AgeBiGenderList(scrolledContainer,
				SWT.V_SCROLL);
		testComposite.putData(rows);
		// testComposite.putTestLabel(testComposite);
	}


	@Test
	public void scrolledYearIntegerList() {
		YearIntegerDataRow[] rows = { new YearIntegerDataRow(1984, 1234),
				new YearIntegerDataRow(1985, 2345), new YearIntegerDataRow(1986, 3456),
				new YearIntegerDataRow(1987, 456789), new YearIntegerDataRow(1989, 67890),
				new YearIntegerDataRow(1990, 56789), new YearIntegerDataRow(1991, 78345),
				new YearIntegerDataRow(1992, 87654) };
		shell.setText("YearIntegerList");
		YearIntegerList testComposite = new YearIntegerList(scrolledContainer,
				SWT.SCROLL_LINE);
		testComposite.putData(rows);
	    scrolledContainer.setContent(testComposite);
	    scrolledContainer.setExpandHorizontal(true);
	    scrolledContainer.setExpandVertical(true);
	    scrolledContainer.setMinSize(testComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    Control[] controls = testComposite.getChildren();
	    ScrollListener listener = new ScrollListener(scrolledContainer);
	    for (int i = 0; i < controls.length; i++) {
	      controls[i].addListener(SWT.Activate, listener);
	    }
	}

	@Test
	public void demoTableViewer(){
		DummyElement[] datas = new DummyElement[] {
			new DummyElement(new RGB(255, 12, 40), "row1col2", "row1col3"),
			new DummyElement(new RGB(70, 255, 40), "row2col2", "row2col3") };

		DemoTableViewer idt = new DemoTableViewer();
			idt.put(scrolledContainer, datas);
	}
}