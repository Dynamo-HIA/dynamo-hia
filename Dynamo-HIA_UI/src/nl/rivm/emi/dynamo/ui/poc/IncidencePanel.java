package nl.rivm.emi.dynamo.ui.poc;

import nl.rivm.emi.dynamo.ui.poc.IncidenceDataTable.DummyElement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jfree.experimental.chart.swt.ChartComposite;

public class IncidencePanel {

	static DummyElement[] datas = new DummyElement[] {
			new DummyElement(new RGB(255, 12, 40), "row1col2", "row1col3"),
			new DummyElement(new RGB(70, 255, 40), "row2col2", "row2col3") };

	static public Composite generate(Shell shell, Composite neighbourTop,
			Composite neighbourRight, Composite neighbourBottom, Composite neighbourLeft) {
		Group group = new Group(shell, SWT.NONE);
		handlePlacementInContainer(group, neighbourTop, neighbourRight,
				neighbourBottom);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		Text incidenceName = putIncidenceNamePanel(group);
		ChartComposite incidenceChart = IncidenceValuesChart.generate(group);
		IncidenceValuesChart.handlePlacementInGroup(incidenceChart, incidenceName);
		IncidenceDataTable idt = new IncidenceDataTable();
		FormData fData = new FormData();
		fData.top = new FormAttachment(incidenceName, 5);
		fData.bottom = new FormAttachment(100, -2);
		fData.left = new FormAttachment(0, 2);
		fData.right = new FormAttachment(incidenceChart, -2);
//		fData.right = new FormAttachment(100, -2);
				idt.putInFormLayout(group, datas, fData);
		return group;
	}

	static private void handlePlacementInContainer(Composite myComposite,
			Composite neighbourTop, Composite neighbourRight,
			Composite neighbourBottom) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(neighbourTop, 3);
		formData.right = new FormAttachment(neighbourRight, -2);
		formData.bottom = new FormAttachment(neighbourBottom, -5);
		formData.left = new FormAttachment(0, 5);
		myComposite.setLayoutData(formData);
	}

	static private Text putIncidenceNamePanel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Incidence name:");
		Text text = new Text(parent, SWT.SINGLE | SWT.FILL);
		text.setText("EPI-32");
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 5);
		labelFormData.right = new FormAttachment(0, 100);
		label.setLayoutData(labelFormData);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(label, 2);
		textFormData.right = new FormAttachment(100, -5);
		text.setLayoutData(textFormData);
		return text;
	}

}
