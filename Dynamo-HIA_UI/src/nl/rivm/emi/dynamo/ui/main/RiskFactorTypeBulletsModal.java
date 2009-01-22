package nl.rivm.emi.dynamo.ui.main;

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;
import java.util.Set;

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesSingleton;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RiskFactorTypeBulletsModal implements Runnable {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private Shell shell;
	private HelpGroup helpPanel;
	private BaseNode selectedNode;
	private String newFilePath = "";
	/** Three type radiobuttons. */
	private Button[] radioButtons = new Button[3];
	/* Initialized separately because setSelection does not generate an event. */
	private String selectedRootElementName = RootElementNamesEnum.RISKFACTOR_CATEGORICAL
			.getNodeLabel();

	public RiskFactorTypeBulletsModal(Shell parentShell,
			String configurationFilePath, BaseNode selectedNode) {
		this.selectedNode = selectedNode;
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		shell.setText("Choose the risk factor type:");
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	public synchronized void open() {
		radioButtons[0] = new Button(shell, SWT.RADIO);
		radioButtons[0].setText("categorical");
		radioButtons[0].addListener(SWT.Selection, new Listener() {
			RootElementNamesEnum myRootElementNamesEnum = RootElementNamesEnum.RISKFACTOR_CATEGORICAL;

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					selectedRootElementName = myRootElementNamesEnum
							.getNodeLabel();
					radioButtons[1].setSelection(false);
					radioButtons[2].setSelection(false);
				}
			}
		});

		FormData radio1FormData = new FormData();
		radio1FormData.left = new FormAttachment(0, 15);
		radio1FormData.right = new FormAttachment(100, -15);
		radio1FormData.top = new FormAttachment(0, 10);
		radioButtons[0].setLayoutData(radio1FormData);
		radioButtons[1] = new Button(shell, SWT.RADIO);
		radioButtons[1].setText("continuous");
		radioButtons[1].addListener(SWT.Selection, new Listener() {
			RootElementNamesEnum myRootElementNamesEnum = RootElementNamesEnum.RISKFACTOR_CONTINUOUS;

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					selectedRootElementName = myRootElementNamesEnum
							.getNodeLabel();
					radioButtons[0].setSelection(false);
					radioButtons[2].setSelection(false);
				}
			}
		});
		FormData radio2FormData = new FormData();
		radio2FormData.left = new FormAttachment(0, 15);
		radio2FormData.right = new FormAttachment(100, -15);
		radio2FormData.top = new FormAttachment(radioButtons[0], 5);
		radioButtons[1].setLayoutData(radio2FormData);
		radioButtons[2] = new Button(shell, SWT.RADIO);
		radioButtons[2].setText("compound");
		radioButtons[2].addListener(SWT.Selection, new Listener() {
			RootElementNamesEnum myRootElementNamesEnum = RootElementNamesEnum.RISKFACTOR_COMPOUND;

			public void handleEvent(Event arg0) {
				Button myWidget = (Button) arg0.widget;
				if (myWidget.getSelection()) {
					selectedRootElementName = myRootElementNamesEnum
							.getNodeLabel();
					radioButtons[0].setSelection(false);
					radioButtons[1].setSelection(false);
				}
			}
		});
		FormData radio3FormData = new FormData();
		radio3FormData.left = new FormAttachment(0, 15);
		radio3FormData.right = new FormAttachment(100, -15);
		radio3FormData.top = new FormAttachment(radioButtons[1], 5);
		radioButtons[2].setLayoutData(radio3FormData);
		// Default.
		radioButtons[0].setSelection(true);
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
				newFilePath = selectedNode.getPhysicalStorage()
						.getAbsolutePath()
						+ File.separator + "configuration.xml";
				shell.dispose();
			}

		});
		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		FormData cancelButtonFormData = new FormData();
		cancelButtonFormData.left = new FormAttachment(okButton, 15);
		cancelButtonFormData.bottom = new FormAttachment(100, -15);
		cancelButton.setLayoutData(cancelButtonFormData);
		okButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}

		});
		shell.pack();
		// This is the first place this works.
		shell.setSize(300, 200);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public void run() {
		open();
	}

	static private void handlePlacementInContainer(Composite myComposite) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, -5);
		myComposite.setLayoutData(formData);
	}

	public BaseNode getSelectedNode() {
		return selectedNode;
	}

	public String getNewFilePath() {
		return newFilePath;
	}

	public String getSelectedRootElementName() {
		return selectedRootElementName;
	}

}
