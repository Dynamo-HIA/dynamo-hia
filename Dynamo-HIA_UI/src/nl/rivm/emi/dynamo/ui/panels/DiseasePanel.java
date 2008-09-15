package nl.rivm.emi.dynamo.ui.panels;

import org.eclipse.swt.SWT;
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

public class DiseasePanel {

	public static Composite generate(Shell shell, Composite neighbour) {
		Group group = new Group(shell, SWT.NONE);
		handlePlacementInContainer(group, neighbour);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		putDiseaseTexts(group);
		return group;
	}

	static private void handlePlacementInContainer(Composite myComposite,
			Composite neighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 3);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(neighbour, -2);
		formData.bottom = new FormAttachment(0, 40);
		myComposite.setLayoutData(formData);
	}

	static private void putDiseaseTexts(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Disease:");
		Text text = new Text(parent, SWT.SINGLE);
		text.setText("Lung cancer");
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 5);
		labelFormData.right = new FormAttachment(0, 100);
		label.setLayoutData(labelFormData);
		FormData textFormData = new FormData();
		textFormData.left = new FormAttachment(label, 2);
		textFormData.right = new FormAttachment(100, -5);
		text.setLayoutData(textFormData);
	}
}
