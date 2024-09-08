package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;
import java.util.Set;

import nl.rivm.emi.dynamo.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.main.base.ModalStatics;
import nl.rivm.emi.dynamo.ui.util.RiskSourceProperties;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMap;
import nl.rivm.emi.dynamo.ui.util.RiskSourcePropertiesMapFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FreeNamePlusDropDownModal implements Runnable {
	private Log log = LogFactory.getLog(this.getClass().getName());
	final private Shell shell;
	// private HelpGroup helpPanel;
	private BaseNode selectedNode;
	private String newFilePath;
	RiskSourcePropertiesMap selectableRiskSourcePropertiesMap;
	private RiskSourceProperties rsProps;
	private Text freePart;
	private Combo dropDown;
	private String newDataFilePath;

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
		
		log.debug("Opening this thing: FreeNamePlusDropDownModal");
		
		try {
			selectableRiskSourcePropertiesMap = RiskSourcePropertiesMapFactory
					.makeMap4OneRiskSourceType(selectedNode);
			if ((selectableRiskSourcePropertiesMap != null) && (selectableRiskSourcePropertiesMap.size() != 0)) {
				freePart = new Text(shell, SWT.BORDER);
				FormData textFormData = new FormData();
				textFormData.left = new FormAttachment(0, 15);
				textFormData.right = new FormAttachment(100, -15);
				textFormData.top = new FormAttachment(0, 10);
				freePart.setLayoutData(textFormData);
				dropDown = new Combo(shell, SWT.DROP_DOWN);
				Set<String> keys = selectableRiskSourcePropertiesMap.keySet();
				for (String item : keys) {
					dropDown.add(item);
				}
				dropDown.select(0);
				FormData comboFormData = new FormData();
				comboFormData.left = new FormAttachment(0, 15);
				comboFormData.right = new FormAttachment(100, -15);
				comboFormData.top = new FormAttachment(freePart, 10);
				dropDown.setLayoutData(comboFormData);
				
				Button importButton = new Button(shell, SWT.PUSH);
				importButton.setText("Import");
				FormData importButtonFormData = new FormData();
				importButtonFormData.left = new FormAttachment(0, 15);
				importButtonFormData.right = new FormAttachment(0, 60);
				importButtonFormData.bottom = new FormAttachment(100, -15);
				importButton.setLayoutData(importButtonFormData);
				importButton.addSelectionListener(new SelectionListener() {
	
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
					}
	
					public void widgetSelected(SelectionEvent arg0) {
						// TODO insert import functionality here:
						// getImportFile()
						// Handle dataFile and newFile locations differently
						newDataFilePath = getImportFilePath();
						log.debug("Creating newfilepath");
						newFilePath = selectedNode.getPhysicalStorage().getAbsolutePath() + File.separator
								+ freePart.getText() + dropDown.getText() + ".xml";
						log.debug("Created newfilepath" + newFilePath);
						rsProps = selectableRiskSourcePropertiesMap.get(dropDown.getText());
						shell.dispose();
					}
	
				});				
				Button okButton = new Button(shell, SWT.PUSH);
				okButton.setText("OK");
				FormData okButtonFormData = new FormData();
				okButtonFormData.left = new FormAttachment(importButton, 15);
				okButtonFormData.bottom = new FormAttachment(100, -15);
				okButton.setLayoutData(okButtonFormData);
				okButton.addSelectionListener(new SelectionListener() {
	
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
	
					}
	
					public void widgetSelected(SelectionEvent arg0) {
						log.debug("Creating newfilepath");
						newFilePath = selectedNode.getPhysicalStorage().getAbsolutePath() + File.separator
								+ freePart.getText() + dropDown.getText() + ".xml";
						log.debug("Created newfilepath" + newFilePath);
						rsProps = selectableRiskSourcePropertiesMap.get(dropDown.getText());
						shell.dispose();
					}
	
				});
				Button cancelButton = new Button(shell, SWT.PUSH);
				cancelButton.setText("Cancel");
				FormData cancelButtonFormData = new FormData();
				cancelButtonFormData.left = new FormAttachment(okButton, 15);
				cancelButtonFormData.bottom = new FormAttachment(100, -15);
				cancelButton.setLayoutData(cancelButtonFormData);
				cancelButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						newFilePath = null;
						rsProps = null;
						shell.close();
					}
				});
				shell.pack();
				// This is the first place this works.
				shell.setSize(300, ModalStatics.defaultDataLessMessageHeight);
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
			ErrorMessageUtil.showErrorMessage(log, shell, e, "Could not collect data from risk factors. \n", SWT.ERROR_CANNOT_GET_SELECTION);
		}
	}

	/**
	 * @return File The selected import file
	 */
	protected String getImportFilePath() {
		FileDialog fileDialog = new FileDialog(this.shell);
		fileDialog.open();		
		return fileDialog.getFilterPath() + File.separator + fileDialog.getFileName();
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

	public String getDataFilePath() {
		return newDataFilePath;
	}
	
	public RiskSourceProperties getRsProps() {
		return rsProps;
	}

	public RiskSourcePropertiesMap getList() {
		return selectableRiskSourcePropertiesMap;
	}
}
