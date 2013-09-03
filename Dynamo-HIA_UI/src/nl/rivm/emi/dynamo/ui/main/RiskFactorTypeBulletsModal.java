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

import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorTypeBulletsModal implements Runnable {
//	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	final private Shell shell;
	private BaseNode selectedNode;
	private String newFilePath = "";
	/** Three type radiobuttons. */
	final private Button[] radioButtons = new Button[3];
	/* Initialized separately because setSelection does not generate an event. */
	private String selectedRootElementName = RootElementNamesEnum.RISKFACTOR_CATEGORICAL
			.getNodeLabel();
	private int numberOfClasses;
	private int numberOfCutoffs;
	private int numberOfCompoundClasses;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param configurationFilePath
	 * @param selectedNode
	 */
	public RiskFactorTypeBulletsModal(Shell parentShell,
			String configurationFilePath, BaseNode selectedNode) {
		this.selectedNode = selectedNode;
		this.shell = new Shell(parentShell, SWT.TITLE | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		this.shell.setText("Choose the risk factor type:");
		FormLayout formLayout = new FormLayout();
		this.shell.setLayout(formLayout);
	}

	/**
	 * 
	 */
	public synchronized void open() {
		Group categoricalGroup = new Group(this.shell, SWT.NONE);
//		categoricalGroup.setText("cat");
		FormData catGroupFormData = new FormData();
		catGroupFormData.left = new FormAttachment(0, 2);
		catGroupFormData.right = new FormAttachment(100, -2);
		catGroupFormData.top = new FormAttachment(0, 2);
		categoricalGroup.setLayoutData(catGroupFormData);
		GridLayout categoricalGroupLayout = new GridLayout();
		categoricalGroupLayout.numColumns = 3;
		categoricalGroupLayout.makeColumnsEqualWidth = true;
		categoricalGroup.setLayout(categoricalGroupLayout);
		this.radioButtons[0] = new Button(categoricalGroup, SWT.RADIO);
		this.radioButtons[0].setText("categorical");
		this.radioButtons[0].addListener(SWT.Selection, new Listener() {
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
		Label numberOfClassesLabel = new Label(categoricalGroup, SWT.NONE);
		numberOfClassesLabel.setText("Pick the number of classes:");
		Combo numberOfClassesDropDown = new Combo(categoricalGroup,
				SWT.DROP_DOWN);
		numberOfClassesDropDown.add("2", 0);
		numberOfClassesDropDown.add("3", 1);
		numberOfClassesDropDown.add("4", 2);
		numberOfClassesDropDown.add("5", 3);
		numberOfClassesDropDown.add("6", 4);
		numberOfClassesDropDown.add("7", 5);
		numberOfClassesDropDown.add("8", 6);
		numberOfClassesDropDown.add("9", 7);
		numberOfClassesDropDown.add("10", 8);
		numberOfClassesDropDown.add("11", 9);
		numberOfClassesDropDown.add("12", 10);
		numberOfClassesDropDown.add("13", 11);
		numberOfClassesDropDown.add("14", 12);
		numberOfClassesDropDown.add("15", 13);
		numberOfClassesDropDown.add("16", 14);
		numberOfClassesDropDown.add("17", 15);
		numberOfClassesDropDown.add("18", 16);
	/*	
	 * unfortunately, more than 18 classes do not work with the prevalence window as to many widgets are created (unclear why)
	 * also, a blue square is on the lower part of the canvas
	 * 
	 * numberOfClassesDropDown.add("19", 17);
		numberOfClassesDropDown.add("20", 18);
		numberOfClassesDropDown.add("21", 19);
		numberOfClassesDropDown.add("22", 20);
		numberOfClassesDropDown.add("23", 21);
		numberOfClassesDropDown.add("24", 22);
		numberOfClassesDropDown.add("25", 23);
		numberOfClassesDropDown.add("26", 24);
		numberOfClassesDropDown.add("27", 25);
		numberOfClassesDropDown.add("28", 26);
		numberOfClassesDropDown.add("29", 27);
		numberOfClassesDropDown.add("30", 28);
		numberOfClassesDropDown.add("31", 29);
		numberOfClassesDropDown.add("32", 30);
		numberOfClassesDropDown.add("33", 31);
		numberOfClassesDropDown.add("34", 32);
		numberOfClassesDropDown.add("35", 33);
		numberOfClassesDropDown.add("36", 34);
		numberOfClassesDropDown.add("37", 35);
		numberOfClassesDropDown.add("38", 36);
		numberOfClassesDropDown.add("39", 37);
		numberOfClassesDropDown.add("40", 38);
		numberOfClassesDropDown.add("41", 39);
		numberOfClassesDropDown.add("42", 40);
		numberOfClassesDropDown.add("43", 41);
		numberOfClassesDropDown.add("44", 42);
		numberOfClassesDropDown.add("45", 43);
		numberOfClassesDropDown.add("46", 44);
		numberOfClassesDropDown.add("47", 45);
		numberOfClassesDropDown.add("48", 46);
		numberOfClassesDropDown.add("49", 47);
		numberOfClassesDropDown.add("50", 48);
		*/
		
		
		numberOfClassesDropDown.select(0);
		numberOfClasses = 2;
		numberOfClassesDropDown.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent event) {
				Combo myCombo = (Combo) event.widget;
				numberOfClasses = myCombo.getSelectionIndex() + 2;
			}
		});
		Group continuousGroup = new Group(this.shell, SWT.NONE);
		FormData continuousGroupFormData = new FormData();
		continuousGroupFormData.left = new FormAttachment(0, 2);
		continuousGroupFormData.right = new FormAttachment(100, -2);
		continuousGroupFormData.top = new FormAttachment(categoricalGroup, 2);
		continuousGroup.setLayoutData(continuousGroupFormData);
		GridLayout continuousGroupLayout = new GridLayout();
		continuousGroupLayout.numColumns = 3;
		continuousGroupLayout.makeColumnsEqualWidth = true;
		continuousGroup.setLayout(continuousGroupLayout);

		radioButtons[1] = new Button(continuousGroup, SWT.RADIO);
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
		Label numberOfCutoffsLabel = new Label(continuousGroup, SWT.NONE);
		numberOfCutoffsLabel.setText("Pick the number of cutoffs:");
		Combo numberOfCutOffsDropDown = new Combo(continuousGroup,
				SWT.DROP_DOWN);
		numberOfCutOffsDropDown.add("0", 0);
		numberOfCutOffsDropDown.add("1", 1);
		numberOfCutOffsDropDown.add("2", 2);
		numberOfCutOffsDropDown.add("3", 3);
		numberOfCutOffsDropDown.add("4", 4);
		numberOfCutOffsDropDown.add("5", 5);
		numberOfCutOffsDropDown.add("6", 6);
		numberOfCutOffsDropDown.add("7", 7);
		numberOfCutOffsDropDown.add("8", 8);
		numberOfCutOffsDropDown.add("9", 9);
		// Initialize.
		numberOfCutOffsDropDown.select(0);
		numberOfCutoffs = 0;
		numberOfCutOffsDropDown.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Combo myCombo = (Combo) event.widget;
				numberOfCutoffs = myCombo.getSelectionIndex();
			}
		});
		
		Group compoundGroup = new Group(this.shell, SWT.NONE);
		FormData compoundGroupFormData = new FormData();
		compoundGroupFormData.left = new FormAttachment(0, 2);
		compoundGroupFormData.right = new FormAttachment(100, -2);
		compoundGroupFormData.top = new FormAttachment(continuousGroup, 2);
		compoundGroup.setLayoutData(compoundGroupFormData);
		GridLayout compoundGroupLayout = new GridLayout();
		compoundGroupLayout.numColumns = 3;
		compoundGroupLayout.makeColumnsEqualWidth = true;
		compoundGroup.setLayout(compoundGroupLayout);
		  radioButtons[2] = new Button(compoundGroup, SWT.RADIO);
		  radioButtons[2].setText("compound");
		  radioButtons[2].addListener(SWT.Selection, new Listener() {
		  RootElementNamesEnum myRootElementNamesEnum =
		  RootElementNamesEnum.RISKFACTOR_COMPOUND;
		  
		  public void handleEvent(Event arg0) { 
			  Button myWidget = (Button)arg0.widget;
			  if (myWidget.getSelection())
			     {
              // Disabling (Next three lines were commented out, now enabled for testing).
				  selectedRootElementName =  myRootElementNamesEnum .getNodeLabel();
		  radioButtons[0].setSelection(false);
		  radioButtons[1].setSelection(false);
		  // Added for the disabling (Next line was added for disabling, now removed for testing).
		  // radioButtons[2].setSelection(false); 
				  } } }
		  ); 
			Label numberOfCompoundClassesLabel = new Label(compoundGroup, SWT.NONE);
			numberOfCompoundClassesLabel.setText("Pick the number of classes:");
			Combo numberOfCompoundClassesDropDown = new Combo(compoundGroup,
					SWT.DROP_DOWN);
			numberOfCompoundClassesDropDown.add("2", 0);
			numberOfCompoundClassesDropDown.add("3", 1);
			numberOfCompoundClassesDropDown.add("4", 2);
			numberOfCompoundClassesDropDown.add("5", 3);
			numberOfCompoundClassesDropDown.add("6", 4);
			numberOfCompoundClassesDropDown.add("7", 5);
			numberOfCompoundClassesDropDown.add("8", 6);
			numberOfCompoundClassesDropDown.add("9", 7);
			numberOfCompoundClassesDropDown.add("10", 8);
			numberOfCompoundClassesDropDown.select(0);
			numberOfCompoundClasses = 2;
			numberOfCompoundClassesDropDown.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent event) {
					Combo myCombo = (Combo) event.widget;
					numberOfCompoundClasses = myCombo.getSelectionIndex() + 2;
				}
			});

		// Default.
		radioButtons[0].setSelection(true);

		addPushButtons();
		shell.pack();
		// This is the first place this works.
		shell.setSize(350, 250);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * 
	 */
	private void addPushButtons() {
		Button okButton = new Button(shell, SWT.PUSH);
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
				newFilePath = selectedNode.getPhysicalStorage()
						.getAbsolutePath()
						+ File.separator + "configuration.xml";
				shell.dispose();
			}

		});
		Button cancelButton = new Button(shell, SWT.PUSH);
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
				log.debug("disposing small window");
				shell.dispose();
			}

		});
	}

	public void run() {
		open();
	}

//	static private void handlePlacementInContainer(Composite myComposite) {
//		FormData formData = new FormData();
//		formData.left = new FormAttachment(0, 5);
//		formData.right = new FormAttachment(100, -5);
//		formData.top = new FormAttachment(0, -5);
//		myComposite.setLayoutData(formData);
//	}

	public BaseNode getSelectedNode() {
		return selectedNode;
	}

	public String getNewFilePath() {
		return newFilePath;
	}

	public String getSelectedRootElementName() {
		return selectedRootElementName;
	}

	public int getNumberOfClasses() {
		log.debug("getNumberOfClasses() about to return " + numberOfClasses);
		return numberOfClasses;
	}

	public int getNumberOfCutoffs() {
		log.debug("getNumberOfCutoffs() about to return " + numberOfCutoffs);
		return numberOfCutoffs;
	}

	public int getNumberOfCompoundClasses() {
		log.debug("getNumberOfCompoundClasses() about to return " + numberOfCompoundClasses);
		return numberOfCompoundClasses;
	}
}
