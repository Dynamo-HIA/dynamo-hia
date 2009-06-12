package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.ui.panels.help.ElementNameScrollableHelpGroup;
import nl.rivm.emi.dynamo.ui.panels.help.HelpScrolledComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class HelpGroup {
	Shell modalShell;
	Group theGroup = null;
	ElementNameScrollableHelpGroup windowHelpGroup;
	ElementNameScrollableHelpGroup fieldHelpGroup;
//	HelpScrolledComposite tryTwo;

	public HelpGroup(Shell shell, Composite buttonPane) {
		theGroup = new Group(shell, SWT.NONE);
		modalShell = shell;
		handlePlacementInContainer(theGroup, buttonPane);

		// FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		// fillLayout.marginHeight = 2;
		// fillLayout.marginWidth = 2;
		// theGroup.setLayout(fillLayout);
		// RowLayout rowLayout = createRowLayout();
		GridLayout layout = createGridLayout();
		theGroup.setLayout(layout);
		theGroup.setText("Help");
		GridData layoutData = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_VERTICAL);
		windowHelpGroup = new ElementNameScrollableHelpGroup(theGroup,
				"Window", "element");
		windowHelpGroup.theGroup.setLayoutData(layoutData);

		 fieldHelpGroup = new ElementNameScrollableHelpGroup(theGroup,
		 "Field",
		 "field");
		 fieldHelpGroup.theGroup.setLayoutData(layoutData);
//		tryTwo = new HelpScrolledComposite(theGroup, "Border", "field");
//		tryTwo.setLayoutData(layoutData);
		theGroup.pack();
	}

	private RowLayout createRowLayout() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.fill = true;
		rowLayout.marginWidth = 3;
		rowLayout.marginHeight = 3;
		rowLayout.pack = true;
		rowLayout.wrap = true;
		return rowLayout;
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
}
