package nl.rivm.emi.dynamo.ui.panels;

import java.io.File;

import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;
import nl.rivm.emi.dynamo.ui.panels.help.ElementNameScrollableHelpGroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class HelpGroup {
	private Shell modalShell;
	private DataAndFileContainer theModal;
	Group theGroup = null;
	private ElementNameScrollableHelpGroup windowHelpGroup;
	private ElementNameScrollableHelpGroup fieldHelpGroup;

	public HelpGroup(DataAndFileContainer theModal, Composite buttonPane) {
		modalShell = theModal.getShell();
		theGroup = new Group(modalShell, SWT.NONE);
		this.theModal = theModal;
		handlePlacementInContainer(theGroup, buttonPane);
		String helpDirectoryPath = System.getProperty("user.dir") + File.separator + "help";
		GridLayout layout = createGridLayout();
		theGroup.setLayout(layout);
		theGroup.setText("Help");
		GridData layoutData = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_VERTICAL);
		windowHelpGroup = new ElementNameScrollableHelpGroup(theGroup,
				"Window", this.theModal.getRootElementName(), helpDirectoryPath);
		windowHelpGroup.theGroup.setLayoutData(layoutData);

		 fieldHelpGroup = new ElementNameScrollableHelpGroup(theGroup,
		 "Field",
		 "fieldInit", helpDirectoryPath);
		 fieldHelpGroup.theGroup.setLayoutData(layoutData);
		theGroup.pack();
	}

	private GridLayout createGridLayout() {
		GridLayout rowLayout = new GridLayout();
		rowLayout.numColumns = 1;
		rowLayout.marginWidth = 3;
		rowLayout.marginHeight = 3;
		return rowLayout;
	}

	public Group getGroup() {
		return theGroup;
	}

	private void handlePlacementInContainer(Composite myComposite,
			Composite buttonPane) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(buttonPane, -5);
		formData.left = new FormAttachment(100, -155);
		myComposite.setLayoutData(formData);
	}

	public ElementNameScrollableHelpGroup getWindowHelpGroup() {
		return windowHelpGroup;
	}

	public ElementNameScrollableHelpGroup getFieldHelpGroup() {
		return fieldHelpGroup;
	}

	public Shell getModalShell() {
		return modalShell;
	}

	public DataAndFileContainer getTheModal() {
		return theModal;
	}
}
