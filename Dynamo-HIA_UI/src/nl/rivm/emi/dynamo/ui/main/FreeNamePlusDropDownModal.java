package nl.rivm.emi.dynamo.ui.main;

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FreeNamePlusDropDownModal implements Runnable {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private Shell shell;
	private HelpGroup helpPanel;
	private BaseNode selectedNode;
	private String newFilePath;
	RiskSourcePropertiesMap list;
	private RiskSourceProperties rsProps;
	private Text freePart;
	private Combo dropDown;

	public FreeNamePlusDropDownModal(Shell parentShell,
			BaseNode selectedNode) {
		this.selectedNode = selectedNode;
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		shell.setText("Enter name and choose risk source:");
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	public synchronized void open() {
		try {
			list = RiskSourcePropertiesMapFactory
					.make(selectedNode);
		if ((list != null) && (list.size() != 0)) {
			freePart = new Text(shell, SWT.BORDER);
			FormData textFormData = new FormData();
			textFormData.left = new FormAttachment(0, 15);
			textFormData.right = new FormAttachment(100, -15);
			textFormData.top = new FormAttachment(0, 10);
			freePart.setLayoutData(textFormData);
			dropDown = new Combo(shell, SWT.DROP_DOWN);
			Set<String> keys = list.keySet();
			for (String item : keys) {
				dropDown.add(item);
			}
			dropDown.select(0);
			FormData comboFormData = new FormData();
			comboFormData.left = new FormAttachment(0, 15);
			comboFormData.right = new FormAttachment(100, -15);
			comboFormData.top = new FormAttachment(freePart, 10);
			dropDown.setLayoutData(comboFormData);
			Button okButton = new Button(shell, SWT.PUSH);
			okButton.setText("OK");
			FormData okButtonFormData = new FormData();
			okButtonFormData.left = new FormAttachment(0, 15);
			okButtonFormData.right = new FormAttachment(0, 60);
			okButtonFormData.bottom = new FormAttachment(100, -15);
			okButton.setLayoutData(okButtonFormData);
			okButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void widgetSelected(SelectionEvent arg0) {
					newFilePath = selectedNode.getPhysicalStorage().getAbsolutePath() + File.separator
							+ freePart.getText() + dropDown.getText() + ".xml";
					rsProps = list.get(dropDown.getText());
					shell.dispose();
				}

			});
			Button cancelButton = new Button(shell, SWT.PUSH);
			cancelButton.setText("Cancel");
			FormData cancelButtonFormData = new FormData();
			cancelButtonFormData.left = new FormAttachment(okButton, 15);
			cancelButtonFormData.bottom = new FormAttachment(100, -15);
			cancelButton.setLayoutData(cancelButtonFormData);
			shell.pack();
			// This is the first place this works.
			shell.setSize(300, 200);
			shell.open();
			Display display = shell.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} else {
			MessageBox messageBox = new MessageBox(shell);
			messageBox.setMessage("No risk sources could be found.");
			messageBox.open();
		}
		} catch (ConfigurationException e) {
			MessageBox messageBox = new MessageBox(shell,SWT.ERROR_CANNOT_GET_SELECTION);
			messageBox.setMessage("Could not collect data\nfrom risk factors.");
			messageBox.open();
			e.printStackTrace();
		}
	}

	public void run() {
		open();
	}

	public BaseNode getSelectedNode() {
		return selectedNode;
	}

	public String getNewFilePath() {
		return newFilePath;
	}

	public RiskSourceProperties getRsProps() {
		return rsProps;
	}

	public RiskSourcePropertiesMap getList() {
		return list;
	}
}
