/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author  boshuizh
 */
public class Output_WriteOutputTab  {
	
	
	private TabFolder tabFolder;
	private DynamoOutputFactory output;
	private String baseDir;
	Shell outputShell;
	private boolean cohortStyle=false;
	private boolean singleFile = true;
	public Output_WriteOutputTab(Shell outputShell, String baseDir,TabFolder tabfolder , DynamoOutputFactory output) {
	this.tabFolder=tabfolder;
	this.output=output;
	this.baseDir=baseDir;
	this.outputShell=outputShell;
	
	makeIt();
	}
	
	public void makeIt(){
		Composite UIComposite = new Composite(tabFolder, SWT.FILL);
		TabItem item1 = new TabItem(tabFolder, SWT.NONE);
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
		final String[] scenarioNamesToWrite = cleanUpScenarioNames(output
				.getScenarioNames());

		runButton.addSelectionListener(new SelectionAdapter() {
			private boolean cohortStyle;

			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(outputShell, SWT.SAVE);

				fd.setText("Give filename for reference scenario:");
				// TODO: juiste basedir meegeven
				fd.setFilterPath(baseDir + "simulation"
						+ File.separator + "results" + File.separator
						+ "excelcohortdata");
				String[] filterExt = { "*.xml", "*.*" };
				fd.setFilterExtensions(filterExt);
				if (cohortStyle)
					fd.setFileName("excelcohortdata");

				if (!cohortStyle)
					fd.setFileName("excelyeardata");

				String path = fd.open();

				if (path != null) {
					/* remove the extension from the path */
					path = cleanPath(path);

					if (cohortStyle && singleFile)
						for (int scen = 0; scen < output.getNScen() + 1; scen++) {
							String fileName = path + "_"
									+ scenarioNamesToWrite[scen] + ".xml";
							try {
								output.writeWorkBookXMLbyCohort(fileName, 2,
										scen);
							} catch (FileNotFoundException e1) {
								new ErrorMessageWindow(e1, outputShell);
								e1.printStackTrace();
							} catch (FactoryConfigurationError e1) {
								new ErrorMessageWindow( e1, outputShell);
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (XMLStreamException e1) {
								new ErrorMessageWindow( e1, outputShell);
								
							} catch (DynamoOutputException e1) {
								new ErrorMessageWindow( e1, outputShell);
								
							}

						}
					else {
						if (!cohortStyle && singleFile)
							for (int scen = 0; scen < output.getNScen() + 1; scen++) {
								String fileName = path + "_"
										+ scenarioNamesToWrite[scen] + ".xml";
								try {
									output.writeWorkBookXMLbyYear(fileName, 2,
											scen);
								} catch (FileNotFoundException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (FactoryConfigurationError e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (XMLStreamException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (DynamoOutputException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								}
							}
						else if (cohortStyle && !singleFile) {
							for (int scen = 0; scen < output.getNScen() + 1; scen++) {
								String fileName = path + "_men_"
										+ output.getScenarioNames()[scen]
										+ ".xml";
								try {
									output.writeWorkBookXMLbyCohort(fileName,
											0, scen);

									fileName = path + "_women_"
											+ scenarioNamesToWrite[scen]
											+ ".xml";

									output.writeWorkBookXMLbyCohort(fileName,
											1, scen);
								} catch (FileNotFoundException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (FactoryConfigurationError e1) {
									// TODO: handle this exception
									e1.printStackTrace();
								} catch (XMLStreamException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (DynamoOutputException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								}
							}
						} else if (!cohortStyle && !singleFile) {
							for (int scen = 0; scen < output.getNScen() + 1; scen++) {
								String fileName = path + "_men_"
										+ output.getScenarioNames()[scen]
										+ ".xml";
								try {
									output.writeWorkBookXMLbyYear(fileName, 0,
											scen);

									fileName = path + "_women_"
											+ scenarioNamesToWrite[scen]
											+ ".xml";

									output.writeWorkBookXMLbyYear(fileName, 1,
											scen);
								} catch (FileNotFoundException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (FactoryConfigurationError e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (XMLStreamException e1) {
									new ErrorMessageWindow( e1, outputShell);
									e1.printStackTrace();
								} catch (DynamoOutputException e1) {
									new ErrorMessageWindow( e1, outputShell);
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
		if (output.isDetails())
			singleDiseaseButton.setSelection(false);
		else
			singleDiseaseButton.setSelection(true);

		singleDiseaseButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					output.setDetails(false);

				}

			}
		}));
		Button stateButton = new Button(radiogroup1, SWT.RADIO);
		stateButton.setText("per combination of disease");
		if (output.isDetails())
			stateButton.setSelection(true);
		else
			stateButton.setSelection(false);

		// ageButton.setBounds(10,50,20,100);
		stateButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					output.setDetails(true);
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
					cohortStyle = false;

				}

			}
		}));
		Button ageButton = new Button(radiogroup1, SWT.RADIO);
		ageButton.setText("by cohort");
		// ageButton.setBounds(10,50,20,100);
		ageButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					cohortStyle = true;
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
					singleFile = false;

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
					singleFile = true;
				}

			}
		}));
	}
		}