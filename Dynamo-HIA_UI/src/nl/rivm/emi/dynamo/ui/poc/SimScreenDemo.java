/*
 * Created on Jun 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nl.rivm.emi.dynamo.ui.poc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimScreenDemo {

	public void show(){
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setText("Simulation Demo");
		shell.setBounds(100, 100, 600, 400);
		shell.setLayout(new FormLayout());
		putTabFolder(shell);

		FormData formData = new FormData();
		Button saveButton = new Button(shell, SWT.PUSH);
		saveButton.setText("Save...");
		formData = new FormData();
		formData.left = new FormAttachment(0,5);
		formData.bottom = new FormAttachment(100,-5);
		saveButton.setLayoutData(formData);

		Button estimateParametersButton = new Button(shell, SWT.PUSH);
		estimateParametersButton.setText("Estimate Parameters");
		formData = new FormData();
		formData.left = new FormAttachment(saveButton,15);
		formData.bottom = new FormAttachment(100,-5);
		estimateParametersButton.setLayoutData(formData);

		Button viewParametersButton = new Button(shell, SWT.PUSH);
		viewParametersButton.setText("View Parameters...");
		formData = new FormData();
		formData.left = new FormAttachment(estimateParametersButton,5);
		formData.bottom = new FormAttachment(100,-5);
		viewParametersButton.setLayoutData(formData);

		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		formData = new FormData();
		formData.right = new FormAttachment(100,-5);
		formData.bottom = new FormAttachment(100,-5);
		cancelButton.setLayoutData(formData);

		Button viewResultsButton = new Button(shell, SWT.PUSH);
		viewResultsButton.setText("View Results...");
		formData = new FormData();
		formData.right = new FormAttachment(cancelButton,-15);
		formData.bottom = new FormAttachment(100,-5);
		viewResultsButton.setLayoutData(formData);

		Button runButton = new Button(shell, SWT.PUSH);
		runButton.setText("Run...");
		formData = new FormData();
		formData.right = new FormAttachment(viewResultsButton,-5);
		formData.bottom = new FormAttachment(100,-5);
		runButton.setLayoutData(formData);

		shell.open();

		while (!shell.isDisposed ()) {
		   if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

	private void putTabFolder(Shell shell) {
		final TabFolder tabFolder = new TabFolder (shell, SWT.BORDER);
		for (int i = 1; i < 4; i++) {
			TabItem tabItem = new TabItem (tabFolder, SWT.NULL);
			tabItem.setText ("Tab " + i);
			Composite composite = new Composite (tabFolder, SWT.NULL);
			tabItem.setControl (composite);
			Button button = new Button (composite, SWT.PUSH);
			button.setBounds(25, 25, 100, 25);
			button.setText ("Click Me Now");
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					((Button)event.widget).setText("I Was Clicked");
				}
			});
		}
	}
		
	public static void main(String[] args) {
SimScreenDemo theDemo = new SimScreenDemo();
theDemo.show();
	}
}
