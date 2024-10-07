/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.global.ScenarioParameters;
import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.CSVLEwriter;
import nl.rivm.emi.dynamo.output.CSVWriter;
import nl.rivm.emi.dynamo.output.ExcelReadableXMLWriter;
import nl.rivm.emi.dynamo.ui.main.main.GraphicalDynSimRunPR;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
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

/**
 * @author boshuizh
 */
public class Output_WriteOutputTab {

	private TabFolder tabFolder;
	CDMOutputFactory output;
	ExcelReadableXMLWriter xmlWriter;
	CSVLEwriter leWriter;
	CSVWriter csvWriter;
	// String baseDir;
	Shell outputShell;

	boolean singleFile = true;
	boolean combinedGenderFile = true;
	boolean cohortStyle;
	boolean lifexp = false;
	boolean sullivan = false;
	protected boolean csvFormat = true;
	ScenarioParameters params;
	String[] scenarioNamesToWrite;
	String userFileName;
	/*
	 * calculation of life-expectancies takes time, so writing this file will be
	 * done in the background To prevent simultaneous execution of two or more
	 * writing actions ( they uses the same xmlWriter-instance so things might
	 * go wrong) a threadpool with only a single thread is used (better save
	 * than sorry)
	 * 
	 * View results
	 */
	ExecutorService exec = Executors.newFixedThreadPool(1);
	/*
	 * currentPath in first instance is the directory results in the simulation
	 * directory, but is changed to the directory given by the user
	 */
	String currentPath;

	/**
	 * @param outputShell

	 * @param data
	 *            .getBaseDir()

	 * @param tabfolder
	 * @param outputFactory
	 */
	public Output_WriteOutputTab(Shell outputShell, String currentPath,
			TabFolder tabfolder, CDMOutputFactory outputFactory,
			ScenarioParameters params) {
		this.tabFolder = tabfolder;
		this.output = outputFactory;
		// this.baseDir=baseDir;
		this.outputShell = outputShell;
		this.currentPath = currentPath;
		this.params = params;
		this.xmlWriter = new ExcelReadableXMLWriter(outputFactory, params);
		this.csvWriter = new CSVWriter(outputFactory, params);
		this.leWriter = new CSVLEwriter(outputFactory, params, new GraphicalDynSimRunPR(outputShell));

		makeIt();
	}

	/**
	 * 
	 */
	public void makeIt() {
		Composite UIComposite = new Composite(this.tabFolder, SWT.FILL);
		TabItem item1 = new TabItem(this.tabFolder, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		UIComposite.setLayout(gridLayout);


		/* create a composite that contains the control elements */

		Composite controlComposite = new Composite(UIComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.FILL);
		controlData.grabExcessHorizontalSpace = true;
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		makeTypeOfFileButton(controlComposite);
		makeGenderOutputButton(controlComposite);
		makeDiseaseStyleButton(controlComposite);

		enable(controlComposite);
		// makeFileFormatButton(controlComposite);

		Button runButton = new Button(controlComposite, SWT.PUSH);
		runButton.setText("Write data");
		/* disable the choices */

		/*
		 * make new scenarionames that are part of the standard file name which
		 * do not contain any underscores any more
		 */
		this.scenarioNamesToWrite = cleanUpScenarioNames(this.output
				.getScenarioNames());

		runButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(
						Output_WriteOutputTab.this.outputShell, SWT.SAVE);

				fd.setText("Give filename for reference scenario:");
				// TODO: juiste basedir meegeven

				String directoryName = Output_WriteOutputTab.this.currentPath;
				File directory = new File(directoryName);
				boolean isDirectory = directory.isDirectory();
				if (!isDirectory)
					directory.mkdirs();
				boolean canWrite = directory.canWrite();

				// InputStream
				// inputStream=this.getClass().getResourceAsStream("wait30trans.gif");
				if (canWrite)
					fd.setFilterPath(Output_WriteOutputTab.this.currentPath);
				String[] filterExt = { "*.xml" };


				if (Output_WriteOutputTab.this.csvFormat)
					filterExt[0] = "*.csv";

				fd.setFilterExtensions(filterExt);
				if (!Output_WriteOutputTab.this.lifexp
						&& !Output_WriteOutputTab.this.csvFormat) {
					if (Output_WriteOutputTab.this.cohortStyle)
						fd.setFileName("cohortdata.xml");

					if (!Output_WriteOutputTab.this.cohortStyle)
						fd.setFileName("yeardata.xml");
				} else if (!Output_WriteOutputTab.this.lifexp
						&& Output_WriteOutputTab.this.csvFormat)
					fd.setFileName("output.csv");
				else {
					if (!Output_WriteOutputTab.this.sullivan)
						fd.setFileName("cohortLE.csv");

					if (Output_WriteOutputTab.this.sullivan)
						fd.setFileName("SullivanLE.csv");
				}

				String path = fd.open();

				if (path != null) {

					Output_WriteOutputTab.this.currentPath = fd.getFilterPath();
					/* remove the extension from the path */
					Output_WriteOutputTab.this.userFileName = fd.getFileName();
					userFileName = cleanPath(userFileName);
					// poging tot mooiere wait:
					// Image image = new Image(e.widget.getDisplay(),
					// this.getClass().getResourceAsStream("wait30trans.gif"));
					// ((Button) e.widget).setImage(image);
					Runnable worker = new Runnable() {

						public void run() {
							writeFiles(scenarioNamesToWrite, userFileName);
						}
					};
					BusyIndicator
							.showWhile(Output_WriteOutputTab.this.outputShell
									.getDisplay(), worker);
					((Button) e.widget).setImage(null);
				}

			}

			public void writeFiles(final String[] scenarioNamesToWrite,
					String userFileName) {

				/* XML files */
				if (Output_WriteOutputTab.this.cohortStyle
						&& !Output_WriteOutputTab.this.combinedGenderFile
						&& !Output_WriteOutputTab.this.csvFormat
						&& !Output_WriteOutputTab.this.lifexp)
					writeAggregatedCohortFile(scenarioNamesToWrite,
							userFileName);
				
				if (Output_WriteOutputTab.this.cohortStyle
						&& Output_WriteOutputTab.this.combinedGenderFile
						&& !Output_WriteOutputTab.this.csvFormat
						&& !Output_WriteOutputTab.this.lifexp)
					writeSingleCohortFile(scenarioNamesToWrite,
							userFileName);
				

				else if (!Output_WriteOutputTab.this.cohortStyle
						&& !Output_WriteOutputTab.this.combinedGenderFile
						&& !Output_WriteOutputTab.this.csvFormat
						&& !Output_WriteOutputTab.this.lifexp)
					writeAggregateYearFile(scenarioNamesToWrite, userFileName);
				else if (!Output_WriteOutputTab.this.cohortStyle
						&& Output_WriteOutputTab.this.combinedGenderFile
						&& !Output_WriteOutputTab.this.csvFormat
						&& !Output_WriteOutputTab.this.lifexp)
					writeSingleYearFile(scenarioNamesToWrite, userFileName);
				/* CSV files with data */
				/* by year */
				else if (Output_WriteOutputTab.this.csvFormat
						&& Output_WriteOutputTab.this.singleFile
						&& !Output_WriteOutputTab.this.cohortStyle
						&& !Output_WriteOutputTab.this.lifexp)
					this.writeCSVfile(scenarioNamesToWrite, userFileName);
				else if (Output_WriteOutputTab.this.csvFormat
						&& !Output_WriteOutputTab.this.singleFile
						&& !Output_WriteOutputTab.this.cohortStyle
						&& !Output_WriteOutputTab.this.lifexp)
					this.writeCSVfileByScen(scenarioNamesToWrite, userFileName);
                /* by cohort */
				else if (Output_WriteOutputTab.this.csvFormat
						&& Output_WriteOutputTab.this.singleFile
						&& Output_WriteOutputTab.this.cohortStyle
						&& !Output_WriteOutputTab.this.lifexp)
					this.writeCSVCohortfile(scenarioNamesToWrite, userFileName);
				else if (Output_WriteOutputTab.this.csvFormat
						&& !Output_WriteOutputTab.this.singleFile
						&& Output_WriteOutputTab.this.cohortStyle
						&& !Output_WriteOutputTab.this.lifexp)
					this.writeCSVCohortfileByScen(scenarioNamesToWrite,
							userFileName);
				/* CSV files with life expectancies */
				else if (Output_WriteOutputTab.this.lifexp
						&& Output_WriteOutputTab.this.sullivan)
					this
							.writeLESullivanfile(scenarioNamesToWrite,
									userFileName);
				else if (Output_WriteOutputTab.this.lifexp
						&& !Output_WriteOutputTab.this.sullivan)
					this.writeLECohortfile(scenarioNamesToWrite, userFileName);

			}

			private void writeAggregateYearFile(
					final String[] scenarioNamesToWrite, String userFileName) {

				for (int scen = 0; scen < Output_WriteOutputTab.this.output
						.getNScen() + 1; scen++) {
					String fileName = Output_WriteOutputTab.this.currentPath
							+ File.separator + userFileName + "_men_"
							+ scenarioNamesToWrite[scen] + ".xml";
					try {
						Output_WriteOutputTab.this.xmlWriter
								.writeWorkBookXMLbyYear(fileName, 0, scen);

						fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator + userFileName + "_women_"
								+ scenarioNamesToWrite[scen] + ".xml";

						Output_WriteOutputTab.this.xmlWriter
								.writeWorkBookXMLbyYear(fileName, 1, scen);

						// ErrorMessageWindow("finished with writing of files",outputShell);
					} catch (FileNotFoundException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (FactoryConfigurationError e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (DynamoOutputException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					}
				}
			}

			private void writeAggregatedCohortFile(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done;
				for (int scen = 0; scen < Output_WriteOutputTab.this.output
						.getNScen() + 1; scen++) {

					try {

						String fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator
								+ userFileName
								+ "_men_"
								+ Output_WriteOutputTab.this.output
										.getScenarioNames()[scen] + ".xml";

						Output_WriteOutputTab.this.xmlWriter
								.writeWorkBookXMLbyCohort(fileName, 0, scen);

						fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator + userFileName + "_women_"
								+ scenarioNamesToWrite[scen] + ".xml";

						Output_WriteOutputTab.this.xmlWriter
								.writeWorkBookXMLbyCohort(fileName, 1, scen);
						done = true;// new
						// ErrorMessageWindow("finished with writing of files",outputShell);
					} catch (FileNotFoundException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (FactoryConfigurationError e1) {
						// TODO: handle this exception
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (DynamoOutputException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					}
				}
			}

			private void writeSingleYearFile(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done;
				for (int scen = 0; scen < Output_WriteOutputTab.this.output
						.getNScen() + 1; scen++) {
					String fileName = Output_WriteOutputTab.this.currentPath
							+ File.separator + userFileName + "_"
							+ scenarioNamesToWrite[scen] + ".xml";
					try {
						Output_WriteOutputTab.this.xmlWriter
								.writeWorkBookXMLbyYear(fileName, 2, scen);
						done = true;// new
						// ErrorMessageWindow("finished with writing of  "+fileName,outputShell);
					} catch (FileNotFoundException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (FactoryConfigurationError e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (DynamoOutputException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					}
				}
			}
			private void writeSingleCohortFile(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done;
				for (int scen = 0; scen < Output_WriteOutputTab.this.output
						.getNScen() + 1; scen++) {

					try {

						String fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator
								+ userFileName+
								"_"
								+ Output_WriteOutputTab.this.output
										.getScenarioNames()[scen] + ".xml";

						Output_WriteOutputTab.this.xmlWriter
								.writeWorkBookXMLbyCohort(fileName, 2, scen);

						
						done = true;// new
						// ErrorMessageWindow("finished with writing of files",outputShell);
					} catch (FileNotFoundException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (FactoryConfigurationError e1) {
						// TODO: handle this exception
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					} catch (DynamoOutputException e1) {
						new ErrorMessageWindow(e1,
								Output_WriteOutputTab.this.outputShell);
						e1.printStackTrace();
					}
				}
			}

			private void writeCSVfile(final String[] scenarioNamesToWrite,
					String userFileName) {
				boolean done = false;

				String fileName = Output_WriteOutputTab.this.currentPath
						+ File.separator + userFileName + ".csv";
				try {
					
					Output_WriteOutputTab.this.csvWriter
							.writeBatchOutputCSV(fileName, !Output_WriteOutputTab.this.combinedGenderFile);

					done = true;

				} catch (DynamoOutputException e1) {
					new ErrorMessageWindow(e1,
							Output_WriteOutputTab.this.outputShell);

				}

			}

			private void writeCSVfileByScen(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done = false;
				for (int scen = 0; scen < Output_WriteOutputTab.this.output
						.getNScen() + 1; scen++)
					if (!Output_WriteOutputTab.this.combinedGenderFile)
					for (int g = 0; g < 2; g++) {
						String gender = "men";
						if (g == 1)
							gender = "women";
						String fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator
								+ userFileName
								+ "_"
								+ scenarioNamesToWrite[scen]
								+ "_"
								+ gender
								+ ".csv";
						try {

							Output_WriteOutputTab.this.csvWriter
									.writeWorkBookCSVbyYear(fileName, g, scen);
							done = true;

						} catch (DynamoOutputException e1) {
							new ErrorMessageWindow(e1,
									Output_WriteOutputTab.this.outputShell);

						}

					}
					else {
						
						
						String fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator
								+ userFileName
								+ "_"
								+ scenarioNamesToWrite[scen]
								+ ".csv";
						try {

							Output_WriteOutputTab.this.csvWriter
									.writeWorkBookCSVbyYear(fileName, 2, scen);
							done = true;

						} catch (DynamoOutputException e1) {
							new ErrorMessageWindow(e1,
									Output_WriteOutputTab.this.outputShell);

						}

					}
			}

			private void writeCSVCohortfileByScen(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done = false;
				for (int scen = 0; scen < Output_WriteOutputTab.this.output
						.getNScen() + 1; scen++)
					if (!Output_WriteOutputTab.this.combinedGenderFile)
					for (int g = 0; g < 2; g++) {
						String gender = "men";
						if (g == 1)
							gender = "women";
						String fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator
								+ userFileName
								+ "_"
								+ scenarioNamesToWrite[scen]
								+ "_"
								+ gender
								+ ".csv";
						try {
							Output_WriteOutputTab.this.csvWriter
									.writeWorkBookCSVbyCohort(fileName, g, scen);

							done = true;

						} catch (DynamoOutputException e1) {
							new ErrorMessageWindow(e1,
									Output_WriteOutputTab.this.outputShell);

						}

					}
					else {
						
						String fileName = Output_WriteOutputTab.this.currentPath
								+ File.separator
								+ userFileName
								+ "_"
								+ scenarioNamesToWrite[scen]
								+ ".csv";
						try {
							Output_WriteOutputTab.this.csvWriter
									.writeWorkBookCSVbyCohort(fileName,2, scen);

							done = true;

						} catch (DynamoOutputException e1) {
							new ErrorMessageWindow(e1,
									Output_WriteOutputTab.this.outputShell);

						}

					
						
					}
			}

			private void writeCSVCohortfile(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done = false;

				String fileName = Output_WriteOutputTab.this.currentPath
						+ File.separator + userFileName + ".csv";
				try {
					Output_WriteOutputTab.this.csvWriter
							.writeWorkBookCSVbyCohort(fileName,!Output_WriteOutputTab.this.combinedGenderFile);

					done = true;

				} catch (DynamoOutputException e1) {
					new ErrorMessageWindow(e1,
							Output_WriteOutputTab.this.outputShell);

				}

			}

			private void writeLECohortfile(final String[] scenarioNamesToWrite,
					String userFileName) {

				String fileName = Output_WriteOutputTab.this.currentPath
						+ File.separator + userFileName;
				FileWriter writer;
				try {
					writer = new FileWriter(fileName + ".csv");

					Output_WriteOutputTab.this.leWriter.setWriter(writer);
					Output_WriteOutputTab.this.leWriter.setFilename(fileName
							+ ".csv");
					Output_WriteOutputTab.this.leWriter.setSullivan(false);
					Output_WriteOutputTab.this.leWriter.run();
				} catch (IOException e) {
					new ErrorMessageWindow("file " + fileName
							+ " can not be written. Please make sure that"
							+ " this file is not in use by another program.",
							Output_WriteOutputTab.this.outputShell);
					e.printStackTrace();
				}
				// new ErrorMessageWindow("file " + fileName
				// + " can not be written. Please make sure that"
				// + " this file is not in use by another program.",
				// Output_WriteOutputTab.this.outputShell);

			}

			private void writeLESullivanfile(
					final String[] scenarioNamesToWrite, String userFileName) {
				boolean done = false;

				String fileName = Output_WriteOutputTab.this.currentPath
						+ File.separator + userFileName + ".csv";
				try {

					FileWriter writer = new FileWriter(fileName);
					Output_WriteOutputTab.this.leWriter.setWriter(writer);
					Output_WriteOutputTab.this.leWriter.setFilename(fileName);
					Output_WriteOutputTab.this.leWriter.setSullivan(true);
					Output_WriteOutputTab.this.leWriter.run();

				} catch (IOException e) {

					new ErrorMessageWindow("file " + fileName
							+ " can not be written. Please make sure that"
							+ " this file is not in use by another program.",
							Output_WriteOutputTab.this.outputShell);
				}

			}

			/**
			 * @param path
			 * @return
			 */
			private String cleanPath(String path) {
				/* first remove the extension .xml or .csv */
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

		String[] scenarioNamesToWrite = new String[scenarioNames.length];

		for (int i = 0; i < scenarioNames.length; i++)
			scenarioNamesToWrite[i] = scenarioNames[i];

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
	 * disease or diseasestate
	 * 
	 * @param controlComposite: parent controlComposite
	 *            : parent controlComposite
	 */
	private void makeDiseaseStyleButton(Composite controlComposite) {
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("disease information to write:");
		radiogroup1.setToolTipText("choose whether to sum disease states");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayout = new GridLayout();
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		radiogroup1.setLayout(gridLayout);
		radiogroup1.setLayoutData(gridData);
		// yearButton.setBounds(10,10,20,100);
		Button singleDiseaseButton = new Button(radiogroup1, SWT.RADIO);
		singleDiseaseButton.setText("per disease");
		if (this.csvWriter.isDetails())
			singleDiseaseButton.setSelection(false);
		else
			singleDiseaseButton.setSelection(true);

		singleDiseaseButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.xmlWriter.setDetails(false);
					Output_WriteOutputTab.this.csvWriter.setDetails(false);
				}
			}
		}));
		Button stateButton = new Button(radiogroup1, SWT.RADIO);
		stateButton.setText("per combination of disease");
		if (this.xmlWriter.isDetails())
			stateButton.setSelection(true);
		else
			stateButton.setSelection(false);
		// ageButton.setBounds(10,50,20,100);
		stateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.xmlWriter.setDetails(true);
					Output_WriteOutputTab.this.csvWriter.setDetails(true);
				}
			}
		}));
	}

	/**

	 * makes a radio group for chosing the type of output to write: by cohort or
	 * by year

	 * 
	 * @param controlComposite: parent controlComposite
	 *            : parent controlComposite
	 */
	private void makeTypeOfFileButton(final Composite controlComposite) {
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("files to write:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayout = new GridLayout();
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		radiogroup1.setLayout(gridLayout);
		radiogroup1.setLayoutData(gridData);

		// yearButton.setBounds(10,10,20,100);

		Button yearButton = new Button(radiogroup1, SWT.RADIO);

		yearButton.setText("excel readable XML by year of simulation");

		
		
		
		yearButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.cohortStyle = false;
					Output_WriteOutputTab.this.csvFormat = false;
					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = false;
					enable(controlComposite);
					
				}

			}
		}));


		Button cohortButton = new Button(radiogroup1, SWT.RADIO);
		cohortButton.setText("excel readable XML by cohort");

		// ageButton.setBounds(10,50,20,100);


		cohortButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.cohortStyle = true;
					Output_WriteOutputTab.this.csvFormat = false;
					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = false;
					enable(controlComposite);
					
				}

			}
		}));
		/* choice csv */

		Button csvButton = new Button(radiogroup1, SWT.RADIO);
		csvButton.setText("CSV (yearly data, in one file)");
		csvButton.setSelection(true);

		csvButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.singleFile = true;
					Output_WriteOutputTab.this.csvFormat = true;
					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = false;
					Output_WriteOutputTab.this.cohortStyle = false;
					enable(controlComposite);
				}

			}
		}));

		Button csv2Button = new Button(radiogroup1, SWT.RADIO);
		csv2Button.setText("CSV (yearly data, per scenario)");
		csv2Button.setSelection(false);

		csv2Button.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.singleFile = false;
					Output_WriteOutputTab.this.csvFormat = true;
					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = false;
					Output_WriteOutputTab.this.cohortStyle = false;
					enable(controlComposite);
				}

			}
		}));
		Button csv3Button = new Button(radiogroup1, SWT.RADIO);
		csv3Button.setText("CSV (cohort data, single file)");
		csv3Button.setSelection(false);

		csv3Button.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.singleFile = true;
					Output_WriteOutputTab.this.csvFormat = true;
					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = false;
					Output_WriteOutputTab.this.cohortStyle = true;
					enable(controlComposite);
				}

			}
		}));

		Button csv4Button = new Button(radiogroup1, SWT.RADIO);
		csv4Button.setText("CSV (cohort data, per scenario)");
		csv4Button.setSelection(false);

		csv4Button.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.singleFile = false;
					Output_WriteOutputTab.this.csvFormat = true;
					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = false;
					Output_WriteOutputTab.this.cohortStyle = true;
					enable(controlComposite);
				}

			}
		}));

		Button sullivanButton = new Button(radiogroup1, SWT.RADIO);
		sullivanButton.setText("life expectancy - Sullivan");
		sullivanButton.setSelection(false);

		sullivanButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.sullivan = true;
					Output_WriteOutputTab.this.lifexp = true;
					disable(controlComposite);
				}

			}
		}));

		Button cohortLEButton = new Button(radiogroup1, SWT.RADIO);
		cohortLEButton.setText("life expectancy - Cohort");
		cohortLEButton.setSelection(false);

		cohortLEButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					Output_WriteOutputTab.this.sullivan = false;
					Output_WriteOutputTab.this.lifexp = true;
					disable(controlComposite);
				}

			}
		}));


	}

	/**
	 * makes a radio group for chosing the output format of the output to write:
	 * excel readable XML or csv -- NOT USED !!!!!!!!!!!!!!!!!!-- *
	 * 
	 * @param controlComposite
	 *            : the parent controlComposite
	 */
	private void makeFileFormatButton(final Composite controlComposite) {
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);
	


		radiogroup1.setText("format of files:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayout = new GridLayout();
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		radiogroup1.setLayout(gridLayout);
		radiogroup1.setLayoutData(gridData);

		// yearButton.setBounds(10,10,20,100);

		Button xmlButton = new Button(radiogroup1, SWT.RADIO);
		xmlButton.setText("excel readable XML");
		xmlButton.setSelection(true);

		xmlButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.csvFormat = false;
					enable(controlComposite);
				}

			}
		}));

		Button csvButton = new Button(radiogroup1, SWT.RADIO);
		csvButton.setText("CSV");
		// ageButton.setBounds(10,50,20,100);

		csvButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.csvFormat = true;
					enable(controlComposite);
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
		
		
		this.combinedGenderFile = false;
		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);
		radiogroup1
				.setToolTipText("choose whether to sum data of men and women");
		radiogroup1.setText("files to write:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayout = new GridLayout();
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		radiogroup1.setLayout(gridLayout);
		radiogroup1.setLayoutData(gridData);

		// yearButton.setBounds(10,10,20,100);

		Button separateButton = new Button(radiogroup1, SWT.RADIO);
		separateButton.setText("separate for men and women");
		separateButton.setSelection(true);

		separateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.combinedGenderFile = false;

				}

			}
		}));
		Button bothButton = new Button(radiogroup1, SWT.RADIO);
		bothButton.setSelection(false);

		bothButton.setText("total population");
		// ageButton.setBounds(10,50,20,100);
		bothButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					Output_WriteOutputTab.this.combinedGenderFile = true;
				}

			}
		}));
	}

	/**
	 * enables the groups that are not applicable for life expectancy files
	 * 
	 * @param controlComposite
	 *            : parent composite of the groups that should be enabled
	 */
	private void enable(final Composite controlComposite) {
		Control[] otherControls = controlComposite.getChildren();
		for (int i = 0; i < otherControls.length; i++) {

			if (otherControls[i].getToolTipText() == "choose whether to sum data of men and women"
					|| otherControls[i].getToolTipText() == "choose whether to sum disease states") {
				otherControls[i].setEnabled(true);

				Control[] childControls = ((Composite) otherControls[i])
						.getChildren();
				for (int j = 0; j < childControls.length; j++) {
					childControls[j].setEnabled(true);

				}
			}

		}

	}

	/**
	 * disables the groups that are not applicable for life expectancy files
	 * 
	 * @param controlComposite
	 *            : parent composite of the groups that should be disabled
	 */
	private void disable(final Composite controlComposite) {
		Control[] otherControls = controlComposite.getChildren();
		for (int i = 0; i < otherControls.length; i++) {

			if (otherControls[i].getToolTipText() == "choose whether to sum data of men and women"
					|| otherControls[i].getToolTipText() == "choose whether to sum disease states") {
				otherControls[i].setEnabled(false);

				Control[] childControls = ((Composite) otherControls[i])
						.getChildren();
				for (int j = 0; j < childControls.length; j++) {
					childControls[j].setEnabled(false);

				}
			}

		}

	}
	

}