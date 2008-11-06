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
