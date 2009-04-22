package nl.rivm.emi.dynamo.ui.panels.button;

import nl.rivm.emi.dynamo.ui.listeners.selection.RunSelectionListener;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RunButtonPanel {

	Group group;
	public Button runButton;
	/**
	 * Height of runbutton panel in pixels.
	 */
	int height = 50;

	public RunButtonPanel(Composite parent, DataAndFileContainer theModal) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		runButton = putRunButton(group);
		setModalParent(theModal);
	}

	public void setModalParent(DataAndFileContainer theModalParent) {
		runButton
				.addSelectionListener(new RunSelectionListener(theModalParent));
	}

	static private Button putRunButton(Composite parent) {
		FormData formData = new FormData();
		Button runButton = new Button(parent, SWT.PUSH);
		runButton.setText("Run");
		formData = new FormData();
		formData.left = new FormAttachment(0, 100);
		formData.bottom = new FormAttachment(100, /*-200 */ -5);
		runButton.setLayoutData(formData);
		return runButton;
	}

	/**
	 * 
	 * Place the last (and second) group in the container
	 * 
	 * @param topNeighbour
	 */
	public void putLastInContainer(Composite topNeighbour) {
		FormData formData = new FormData();
//		formData.top = new FormAttachment(topNeighbour, 5);
		formData.top = new FormAttachment(100, -(5 + height));
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
