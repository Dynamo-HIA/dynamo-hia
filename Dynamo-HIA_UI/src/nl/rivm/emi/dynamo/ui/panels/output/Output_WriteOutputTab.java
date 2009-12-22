/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.ExcelReadableXMLWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author  boshuizh
 */
public class Output_WriteOutputTab  {
	
	
	private TabFolder tabFolder;
	CDMOutputFactory output;
	ExcelReadableXMLWriter writer;
	//String baseDir;
	Shell outputShell;
	boolean singleFile = true;
	boolean cohortStyle;
	ScenarioParameters params;
	/* currentPath in first instance is the directory results in the simulation directory, 
	 * but is changed to the directory given by the user
	 */
	String currentPath;
	/**
	 * @param outputShell
	 * @param baseDir
	 * @param tabfolder
	 * @param outputFactory
	 */
	public Output_WriteOutputTab(Shell outputShell, String currentPath,TabFolder tabfolder , CDMOutputFactory outputFactory,ScenarioParameters params) {
	this.tabFolder=tabfolder;
	this.output=outputFactory;
	//this.baseDir=baseDir;
	this.outputShell=outputShell;
	this.currentPath=currentPath ;
	this.params= params;
	this.writer=new ExcelReadableXMLWriter(outputFactory,params);
	
	makeIt();
	}
	
	/**
	 * 
	 */
	public void makeIt(){
		Composite UIComposite = new Composite(this.tabFolder, SWT.FILL);
		TabItem item1 = new TabItem(this.tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		UIComposite.setLayout(gridLayout);

		/*
		 * the composite has two elements: - a column with control elements
		 * where the user can make choices - a plot area
		 */

		/* create a composite that contains the control elements */

		Composite controlComposite = new Composite(UIComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		makeCohortStyleButton(controlComposite);
		makeGenderOutputButton(controlComposite);
		makeDiseaseStyleButton(controlComposite);
		Button runButton = new Button(controlComposite, SWT.PUSH);
		runButton.setText("Write data");
		/*
		 * make new scenarionames that are part of the standard file name which
		 * do not contain any underscores any more
		 */
		final String[] scenarioNamesToWrite = cleanUpScenarioNames(this.output
				.getScenarioNames());

		runButton.addSelectionListener(new SelectionAdapter() {
			

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Output_WriteOutputTab.this.outputShell, SWT.SAVE);

				fd.setText("Give filename for reference scenario:");
				// TODO: juiste basedir meegeven
				
				String directoryName =Output_WriteOutputTab.this.currentPath;
				File directory = new File(directoryName);
				boolean isDirectory = directory.isDirectory();
				if (!isDirectory) 				
					directory.mkdirs();
				boolean canWrite = directory.canWrite();
				
				
				if (canWrite) fd.setFilterPath(Output_WriteOutputTab.this.currentPath
						);
				String[] filterExt = { "*.xml" };
				fd.setFilterExtensions(filterExt);
				if (Output_WriteOutputTab.this.cohortStyle)
					fd.setFileName("excelcohortdata.xml");

				if (!Output_WriteOutputTab.this.cohortStyle)
					fd.setFileName("excelyeardata.xml");

				String path = fd.open();

				if (path != null) {
					
					Output_WriteOutputTab.this.currentPath=fd.getFilterPath();
					/* remove the extension from the path */
					String userFileName=fd.getFileName();
					userFileName = cleanPath(userFileName);

					if (Output_WriteOutputTab.this.cohortStyle && Output_WriteOutputTab.this.singleFile)
						for (int scen = 0; scen < Output_WriteOutputTab.this.output.getNScen() + 1; scen++) {
							String fileName = Output_WriteOutputTab.this.currentPath+File.separator+userFileName + "_"
									+ scenarioNamesToWrite[scen] + ".xml";
							try {
								Output_WriteOutputTab.this.writer.writeWorkBookXMLbyCohort(fileName, 2,
										scen);
							} catch (FileNotFoundException e1) {
								new ErrorMessageWindow(e1, Output_WriteOutputTab.this.outputShell);
								e1.printStackTrace();
							} catch (FactoryConfigurationError e1) {
								new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (XMLStreamException e1) {
								new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
								
							} catch (DynamoOutputException e1) {
								new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
								
							}

						}
					else {
						if (!Output_WriteOutputTab.this.cohortStyle && Output_WriteOutputTab.this.singleFile)
							for (int scen = 0; scen < Output_WriteOutputTab.this.output.getNScen() + 1; scen++) {
								String fileName = Output_WriteOutputTab.this.currentPath+File.separator+userFileName + "_"
										+ scenarioNamesToWrite[scen] + ".xml";
								try {
									Output_WriteOutputTab.this.writer.writeWorkBookXMLbyYear(fileName, 2,
											scen);
								} catch (FileNotFoundException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (FactoryConfigurationError e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (XMLStreamException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (DynamoOutputException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								}
							}
						else if (Output_WriteOutputTab.this.cohortStyle && !Output_WriteOutputTab.this.singleFile) {
							for (int scen = 0; scen < Output_WriteOutputTab.this.output.getNScen() + 1; scen++) {
								String fileName = Output_WriteOutputTab.this.currentPath+File.separator+userFileName + "_men_"
										+ Output_WriteOutputTab.this.output.getScenarioNames()[scen]
										+ ".xml";
								try {
									Output_WriteOutputTab.this.writer.writeWorkBookXMLbyCohort(fileName,
											0, scen);

									fileName = userFileName + "_women_"
											+ scenarioNamesToWrite[scen]
											+ ".xml";

									Output_WriteOutputTab.this.writer.writeWorkBookXMLbyCohort(fileName,
											1, scen);
								} catch (FileNotFoundException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (FactoryConfigurationError e1) {
									// TODO: handle this exception
									e1.printStackTrace();
								} catch (XMLStreamException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (DynamoOutputException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								}
							}
						} else if (!Output_WriteOutputTab.this.cohortStyle && !Output_WriteOutputTab.this.singleFile) {
							for (int scen = 0; scen < Output_WriteOutputTab.this.output.getNScen() + 1; scen++) {
								String fileName = Output_WriteOutputTab.this.currentPath+File.separator+ userFileName + "_men_"
										+ Output_WriteOutputTab.this.output.getScenarioNames()[scen]
										+ ".xml";
								try {
									Output_WriteOutputTab.this.writer.writeWorkBookXMLbyYear(fileName, 0,
											scen);

									fileName = userFileName + "_women_"
											+ scenarioNamesToWrite[scen]
											+ ".xml";

									Output_WriteOutputTab.this.writer.writeWorkBookXMLbyYear(fileName, 1,
											scen);
								} catch (FileNotFoundException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (FactoryConfigurationError e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (XMLStreamException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								} catch (DynamoOutputException e1) {
									new ErrorMessageWindow( e1, Output_WriteOutputTab.this.outputShell);
									e1.printStackTrace();
								}
							}
						}

					}
				}
			}

			/**
			 * @param path
			 * @return
			 */
			private String cleanPath(String path) {
				String delims = "[.]";
				String[] tokens = path.split(delims);
				path = tokens[0];
				/* now remove the older references to scenario and gender */
				/*
				 * note that this might give unexpected results for the user in
				 * case the selected files were not made earlier by the program,
				 * or when scenarioNames have underscores in them therefore
				 * alternative scenarionames are used without underscores
				 */
				delims = "[_]";
				String[] tokens2 = path.split(delims);
				/*
				 * last part is the indication of scenario and should be left
				 * out
				 */
				int numberToAdd = tokens2.length - 1;
				/*
				 * if the last part before that is a gender indicator, also
				 * leave that out
				 */

				if (tokens2[numberToAdd].compareToIgnoreCase("men") == 0
						|| tokens2[numberToAdd].compareToIgnoreCase("women") == 0)
					numberToAdd--;
				/*
				 * of course when there is no last part left, then still take
				 * the first part
				 */

				path = tokens2[0];
				for (int j = 1; j < numberToAdd; j++) {

					path = path + "_" + tokens2[j];
				}
				return path;
			}
		});
		item1.setText("Write output");
		item1.setControl(UIComposite);

	}

	/**
	 * makes new scenarionames that are part of the standard file name which do
	 * not contain any underscores or spaces any more
	 * 
	 * @param scenarioNamesToWrite
	 */
	private String[] cleanUpScenarioNames(String[] scenarioNames) {
		String[] scenarioNamesToWrite = scenarioNames;
		for (int i = 0; i < scenarioNames.length; i++) {
			/*
			 * remove any underscore and spaces from the file name as this will
			 * give problems with respectively removing the added part from the
			 * filename, or with the operation system
			 */
			String delims = "[_]";
			String[] parts = scenarioNamesToWrite[i].split(delims);
			if (parts.length > 1) {
				scenarioNamesToWrite[i] = parts[0];
				for (int j = 1; j < parts.length; j++) {
					scenarioNamesToWrite[i] = scenarioNamesToWrite[i]
							+ parts[j];
				}
			}

			delims = "[ ]";
			parts = scenarioNamesToWrite[i].split(delims);
			if (parts.length > 1) {
				scenarioNamesToWrite[i] = parts[0];
				for (int j = 1; j < parts.length; j++) {
					scenarioNamesToWrite[i] = scenarioNamesToWrite[i]
							+ parts[j];
				}
			}
		}
		return scenarioNamesToWrite;
	}

	/**
	 * makes a radio group for chosing the style of the output to write: by
	 * cohort or by year
	 * 
	 * @param controlComposite
	 */
	private void makeDiseaseStyleButton(Composite controlComposite) {
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("disease information to write:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup1.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button singleDiseaseButton = new Button(radiogroup1, SWT.RADIO);
		singleDiseaseButton.setText("per disease");
		if (this.writer.isDetails())
			singleDiseaseButton.setSelection(false);
		else
			singleDiseaseButton.setSelection(true);

		singleDiseaseButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.writer.setDetails(false);

				}

			}
		}));
		Button stateButton = new Button(radiogroup1, SWT.RADIO);
		stateButton.setText("per combination of disease");
		if (this.writer.isDetails())
			stateButton.setSelection(true);
		else
			stateButton.setSelection(false);

		// ageButton.setBounds(10,50,20,100);
		stateButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.writer.setDetails(true);
				}

			}
		}));
	}

	/**
	 * makes a radio group for chosing the style of the output to write: by
	 * cohort or by year
	 * 
	 * @param controlComposite
	 */
	private void makeCohortStyleButton(Composite controlComposite) {
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("files to write:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup1.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button yearButton = new Button(radiogroup1, SWT.RADIO);
		yearButton.setText("per year of simulation");
		yearButton.setSelection(true);
		

	
	  yearButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.cohortStyle=false;
				}

			}
		}));
		
		
		Button ageButton = new Button(radiogroup1, SWT.RADIO);
		ageButton.setText("by cohort");
		// ageButton.setBounds(10,50,20,100);
		
		
		
		ageButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.cohortStyle=true;
				}

			}
		}));
	}

	

	/**
	 * makes a radio group for chosing the style of the output to write: by
	 * gender or combined
	 * 
	 * @param controlComposite
	 */
	private void makeGenderOutputButton(Composite controlComposite) {
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("files to write:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup1.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button separateButton = new Button(radiogroup1, SWT.RADIO);
		separateButton.setText("separate for men and women");
		separateButton.setSelection(false);

		separateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.singleFile = false;

				}

			}
		}));
		Button bothButton = new Button(radiogroup1, SWT.RADIO);
		bothButton.setSelection(true);

		bothButton.setText("total population");
		// ageButton.setBounds(10,50,20,100);
		bothButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.singleFile = true;
				}

			}
		}));
	}
		}