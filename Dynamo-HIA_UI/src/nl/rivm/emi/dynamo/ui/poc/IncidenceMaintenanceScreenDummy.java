/*
 * Created on Jun 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nl.rivm.emi.dynamo.ui.poc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IncidenceMaintenanceScreenDummy {
	ShellMouseMoveListener shellMouseMoveListener;

	public void show() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Incidence Demo");
		shell.setBounds(100, 100, 600, 400);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
		shellMouseMoveListener = new ShellMouseMoveListener(shell);
		Composite buttonPanel = ButtonPanel.generate(shell);
		Composite helpPanel = HelpPanel.generate(shell,buttonPanel);
		Composite diseasePanel = DiseasePanel.generate(shell, helpPanel);
		Composite incidencePanel = IncidencePanel.generate(shell, diseasePanel, helpPanel, buttonPanel, null);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

//	private void putArrayPane(Shell shell) {
//		ScrolledComposite arrayPane = arrayPane(shell);
//		fillArrayContainer(arrayPane);
//	}
//
//	private ScrolledComposite arrayPane(Shell shell) {
//		final ScrolledComposite composite = new ScrolledComposite(shell,
//				SWT.BORDER | SWT.V_SCROLL);
//		final ScrollBar vBar = composite.getVerticalBar();
//		vBar.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				System.out.println("ArrayContainer-size: "
//						+ composite.getChildren()[0].getSize());
//				Point location = composite.getLocation();
//				System.out.println("ArrayPane-location: " + location);
//				location.y = -vBar.getSelection();
//				composite.setLocation(location);
//			}
//		});
//		composite.setSize(150, 400);
//		composite.pack();
//		return composite;
//	}
//
//	private void fillArrayContainer(Composite parent) {
//		Composite arrayContainer = new Composite(parent, SWT.NONE);
//		final GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 3;
//		// gridLayout.makeColumnsEqualWidth = true;
//		gridLayout.horizontalSpacing = 0;
//		FormData formData = new FormData();
//		formData.left = new FormAttachment(0, 2);
//		formData.right = new FormAttachment(75, -2);
//		formData.top = new FormAttachment(0, 2);
//		formData.bottom = new FormAttachment(100, -35);
//		arrayContainer.setLayoutData(formData);
//		arrayContainer.setLayout(gridLayout);
//		{
//			final Label label = new Label(arrayContainer, SWT.LEFT);
//			final GridData gridData = new GridData();
//			// gridData.horizontalSpan = 1;
//			gridData.horizontalAlignment = SWT.FILL;
//			gridData.widthHint = 40;
//			gridData.heightHint = 15;
//			label.setLayoutData(gridData);
//			label.setText("Age");
//		}
//		{
//			final Label label = new Label(arrayContainer, SWT.NONE);
//			final GridData gridData = new GridData();
//			// gridData.horizontalSpan = 2;
//			gridData.horizontalAlignment = SWT.FILL;
//			gridData.widthHint = 100;
//			gridData.heightHint = 15;
//			label.setLayoutData(gridData);
//			label.setText("Female");
//		}
//		{
//			final Label label = new Label(arrayContainer, SWT.NONE);
//			final GridData gridData = new GridData();
//			// gridData.horizontalSpan = 2;
//			gridData.horizontalAlignment = SWT.FILL;
//			gridData.widthHint = 100;
//			gridData.heightHint = 15;
//			label.setLayoutData(gridData);
//			label.setText("Male");
//		}
//		for (int count = 1; count <= 50; count++) {
//			addArrayLine(arrayContainer, count);
//		}
//		arrayContainer.pack();
//		System.err.println("ArrayContainer size: " + arrayContainer.getSize());
//	}
//
//	private void addArrayLine(Composite arrayPane, int count) {
//		{
//			final Label label = new Label(arrayPane, SWT.NONE);
//			final GridData gridData = new GridData();
//			// gridData.horizontalSpan = 2;
//			gridData.horizontalAlignment = SWT.RIGHT;
//			gridData.widthHint = 40;
//			gridData.heightHint = 15;
//			label.setLayoutData(gridData);
//			label.setText(new Integer(count).toString());
//		}
//		{
//			final Text text = new Text(arrayPane, SWT.BORDER);
//			final GridData gridData = new GridData();
//			// gridData.horizontalSpan = 3;
//			// gridData.grabExcessHorizontalSpace = true;
//			gridData.horizontalAlignment = SWT.FILL;
//			gridData.widthHint = 100;
//			gridData.heightHint = 15;
//			text.setLayoutData(gridData);
//			text.setText("0,123456");
//		}
//		{
//			final Text text = new Text(arrayPane, SWT.BORDER);
//			final GridData gridData = new GridData();
//			// gridData.horizontalSpan = 3;
//			gridData.horizontalAlignment = SWT.FILL;
//			gridData.widthHint = 100;
//			gridData.heightHint = 15;
//			text.setLayoutData(gridData);
//			text.setText("0,4567890");
//		}
//	}
//
//	private void putMainPane(Shell shell) {
//		Composite helpPane = helpPane(shell);
//		FormData formData = new FormData();
//		formData.left = new FormAttachment(75, 0);
//		formData.right = new FormAttachment(100, -5);
//		formData.top = new FormAttachment(0, 2);
//		formData.bottom = new FormAttachment(100, -35);
//		helpPane.setLayoutData(formData);
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 1;
//		helpPane.setLayout(gridLayout);
//		putHelpTextAlg(helpPane);
//		putGridHelpTextSpec(helpPane);
//		putGridDebugText(helpPane);
//	}
//
//	private void putFieldHelpPane(Shell shell) {
//		Composite helpPane = helpPane(shell);
//		FormData formData = new FormData();
//		formData.left = new FormAttachment(75, 0);
//		formData.right = new FormAttachment(100, -5);
//		formData.top = new FormAttachment(0, 2);
//		formData.bottom = new FormAttachment(100, -35);
//		helpPane.setLayoutData(formData);
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 1;
//		helpPane.setLayout(gridLayout);
//		putHelpTextAlg(helpPane);
//		putGridHelpTextSpec(helpPane);
//		putGridDebugText(helpPane);
//	}

	private void putText(Shell shell) {
		Text text = new Text(shell, SWT.MULTI | SWT.READ_ONLY);
		text.setText("Dit is een read-only, multiline text widget.");
		FormData formData = new FormData();
		// formData.left = new FormAttachment(50, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -50);
		text.setLayoutData(formData);
	}

	private void putGridDebugText(Composite parent) {
		Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		String theText = "Parent-Breed: " + parent.getSize().x + " hoog: "
				+ parent.getSize().y + "\n" + "Breed: " + text.getSize().x
				+ " hoog: " + text.getSize().y;
		text.setText(theText);
		text.addMouseMoveListener(new DebugTextMouseMoveListener(parent, text));
	}

	class ShellMouseMoveListener implements MouseMoveListener {
		Shell myShell = null;

		public ShellMouseMoveListener(Shell shell) {
			super();
			myShell = shell;
		}

		public void mouseMove(MouseEvent arg0) {
			System.out.println("The mouse is moving!");
			String theText = "Shell-Breed: " + myShell.getSize().x + " hoog: "
					+ myShell.getSize().y;
			myShell.setText(theText);
			myShell.redraw();
		}
	}

	class DebugTextMouseMoveListener implements MouseMoveListener {
		Composite myParent = null;

		Text myText = null;

		public DebugTextMouseMoveListener(Composite parent, Text text) {
			super();
			myParent = parent;
			myText = text;
		}

		public void mouseMove(MouseEvent arg0) {
			System.out.println("The mouse is moving!");
			String theText = "Parent-Breed: " + myParent.getSize().x
					+ " hoog: " + myParent.getSize().y + "\n" + "Breed: "
					+ myText.getSize().x + " hoog: " + myText.getSize().y;
			myText.setText(theText);
			myText.redraw();
		}
	}

	private void putTabFolder(Shell shell) {
		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		for (int i = 1; i < 4; i++) {
			TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
			tabItem.setText("Tab " + i);
			Composite composite = new Composite(tabFolder, SWT.NULL);
			tabItem.setControl(composite);
			Button button = new Button(composite, SWT.PUSH);
			button.setBounds(25, 25, 100, 25);
			button.setText("Click Me Now");
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					((Button) event.widget).setText("I Was Clicked");
				}
			});
		}
	}

	private Button clickAwareButton(Composite composite) {
		final Button button = new Button(composite, SWT.PUSH);
		button.setBounds(25, 25, 100, 75);
		button.setText("Click Me Now");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				button.setText("I Was Clicked");
			}
		});
		return button;
	}

	public static void main(String[] args) {
		IncidenceMaintenanceScreenDummy theDemo = new IncidenceMaintenanceScreenDummy();
		theDemo.show();
	}
}
