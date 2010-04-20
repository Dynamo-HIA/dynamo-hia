package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CompoundRiskFactorDurationClassIndexModal implements Runnable {
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	final private Shell shell;
	private int numberOfCategories;
	private int durationCategoryIndex = -1;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param configurationFilePath
	 * @param selectedNode
	 */
	public CompoundRiskFactorDurationClassIndexModal(Shell parentShell,
			int numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
		this.shell = new Shell(parentShell, SWT.TITLE | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		this.shell.setText("Duration class index");
		FormLayout formLayout = new FormLayout();
		this.shell.setLayout(formLayout);
	}

	public void run() {
		open();
	}

	public int getDurationCategoryIndex() {
		return durationCategoryIndex;
	}

	/**
	 * 
	 */
	private synchronized void open() {
		Composite compoundGroup = new Composite(this.shell, SWT.NONE);
		FormData compoundGroupFormData = new FormData();
		compoundGroupFormData.left = new FormAttachment(0, 2);
		compoundGroupFormData.right = new FormAttachment(100, -2);
		compoundGroupFormData.top = new FormAttachment(0, 2);
		compoundGroupFormData.bottom = new FormAttachment(100, -2);
		compoundGroup.setLayoutData(compoundGroupFormData);
		FormLayout compositeLayout = new FormLayout();
		compoundGroup.setLayout(compositeLayout);
		Label durationClassIndexLabel = new Label(compoundGroup, SWT.NONE);
		durationClassIndexLabel.setText("Choose the index of duration class:");
		FormData labelLayoutData = new FormData();
		labelLayoutData.top = new FormAttachment(0, 15);
		labelLayoutData.left = new FormAttachment(0, 10);
		durationClassIndexLabel.setLayoutData(labelLayoutData);
		createDropdown(compoundGroup, durationClassIndexLabel);
		addPushButtons(compoundGroup);
		shell.pack();
		// This is the first place this works.
		shell.setSize(250, 150);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * @param compoundGroup
	 * @param durationClassIndexLabel
	 *            TODO
	 */
	private void createDropdown(Composite compoundGroup,
			Label durationClassIndexLabel) {
		Combo durationClassIndexDropDown = new Combo(compoundGroup,
				SWT.DROP_DOWN);
		for (int categoryCount = 0; categoryCount < numberOfCategories; categoryCount++) {
			durationClassIndexDropDown.add(Integer.toString(categoryCount + 1),
					categoryCount);
		}
		durationClassIndexDropDown.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Combo myCombo = (Combo) event.widget;
				durationCategoryIndex = myCombo.getSelectionIndex() + 1;
			}
		});
		durationClassIndexDropDown.select(0);
		FormData layoutData = new FormData();
		layoutData.top = new FormAttachment(0, 15);
		layoutData.left = new FormAttachment(durationClassIndexLabel, 10);
		// layoutData.right = new FormAttachment(100,-15);
		durationClassIndexDropDown.setLayoutData(layoutData);
	}

	/**
	 * @param compoundComposite
	 *            TODO
	 * 
	 */
	private void addPushButtons(Composite compoundComposite) {
		Button cancelButton = new Button(compoundComposite, SWT.PUSH);
		cancelButton.setText("Cancel");
		FormData cancelButtonFormData = new FormData();
		cancelButtonFormData.left = new FormAttachment(100, -70);
		cancelButtonFormData.right = new FormAttachment(100, -10);
		cancelButtonFormData.bottom = new FormAttachment(100, -10);
		cancelButton.setLayoutData(cancelButtonFormData);
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent arg0) {
				durationCategoryIndex = -1;
				shell.dispose();
			}
		});
		Button okButton = new Button(compoundComposite, SWT.PUSH);
		okButton.setText("OK");
		FormData okButtonFormData = new FormData();
		okButtonFormData.left = new FormAttachment(100, -140);
		okButtonFormData.right = new FormAttachment(100, -80);
		okButtonFormData.bottom = new FormAttachment(100, -10);
		okButton.setLayoutData(okButtonFormData);
		okButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}
		});
	}

}
