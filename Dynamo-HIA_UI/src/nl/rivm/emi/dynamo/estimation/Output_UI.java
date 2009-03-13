/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.rules.update.UpdateRules4SimulationFromXMLFactory;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 * 
 */
public class Output_UI {

	final Shell parentShell ;
	Scale scale;
	Text value;
	JFreeChart pyramidChart;
	int plottedScen;
	ChartComposite chartComposite;
	int stepsInRun;
	int startYear;
	DynamoOutputFactory output;

	// Contains the base directory of the application data
	private String baseDir;
	// TODO baseDir
	public Output_UI(Shell shell, ScenarioInfo scen, String simName, 
			Population[] pop, String baseDir) {

		parentShell=shell;
		output = new DynamoOutputFactory(scen, simName);
		stepsInRun = output.getStepsInRun();
		startYear = output.getStartYear();
		try {
			output.extractArraysFromPopulations(pop);
			output.makeArraysWithNumbers();
			/*
			 * output.makeLifeExpectancyPlot(0); makePopulationPyramidPlot();
			 * 
			 * output.makePrevalencePlots(0); output.makePrevalencePlots(1);
			 * 
			 * output.makeRiskFactorPlots(0);
			 */
			makeOutputDisplay();
			/*
			 * if (scen.getRiskType()==2) output.makeMeanPlots(0);
			 * output.makePrevalenceByRiskFactorPlots(0);
			 * output.makePrevalenceByRiskFactorPlots(1); JFreeChart
			 * chart=output.makeSurvivalPlot(0);
			 * chart=output.makeSurvivalPlot(1);
			 * chart=output.makeSurvivalPlot(2); chart
			 * =output.makeLifeExpectancyPlot(0);
			 */
		} catch (Exception e) {

			displayErrorMessage(e);

			e.printStackTrace();

		}

	}

	/**
	 * @param e
	 */
	private void displayErrorMessage(Exception e) {

		Shell shell = new Shell(parentShell);
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox
				.setMessage("error while calculating output."
						+ " Message given: " + e.getMessage()
						+ ". Program will close.");
		e.printStackTrace();
		messageBox.open();
	}

	public void makeOutputDisplay() throws DynamoOutputException {

		Shell shell = new Shell(parentShell);
		shell.setText("Dynamo Output");
		shell.setBounds(30, 30, 750, 650);
		/* tab for pyramid plots */
		TabFolder tabFolder1 = new TabFolder(shell, SWT.FILL);
		tabFolder1.setLayout(new FillLayout());
		tabFolder1.setBounds(10, 10, 730, 580);

		makePyramidTab(tabFolder1);

		/* tab for plot1 */

		makeDiseasePlotTab(tabFolder1);

		/* tab for plot2 */
		makeRiskFactorTab(tabFolder1);

		/* tab for plot3 */
		makeLifeExpectancyTab(tabFolder1);

		/* tab for plot4 */
		makeMortalityTab(tabFolder1);

		/* tab for output */
		makeUITab(tabFolder1);

		/* tab for output */
		makeChangeScenarioTab(tabFolder1);

		shell.open();
		
	}

	/* fields giving the selections made for the writing of files */
	boolean cohortStyle = false;

	/**
	 * @param tabFolder1
	 */
	private void makeUITab(TabFolder tabFolder1) {
		Composite UIComposite = new Composite(tabFolder1, SWT.FILL);
		TabItem item1 = new TabItem(tabFolder1, SWT.NONE);
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
		Button runButton = new Button(controlComposite, SWT.PUSH);
		runButton.setText("Write data");
		runButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (cohortStyle)
					for (int scen = 0; scen < output.getNScen() + 1; scen++) {
						String fileName = Output_UI.this.baseDir 
								+ File.separator + "excel_cohort_all_"
								+ output.getScenarioNames()[scen] + ".xml";
						try {
							output.writeWorkBookXMLbyCohort(fileName, 2, scen);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FactoryConfigurationError e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (XMLStreamException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DynamoOutputException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				else {
					for (int scen = 0; scen < output.getNScen() + 1; scen++) {
						String fileName = Output_UI.this.baseDir
								+ File.separator + "excel_year_all_"
								+ output.getScenarioNames()[scen] + ".xml";
						try {
							output.writeWorkBookXMLbyYear(fileName, 2, scen);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (FactoryConfigurationError e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (XMLStreamException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DynamoOutputException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		});
		item1.setText("Write output");
		item1.setControl(UIComposite);

	}

	/**
	 * method fills the tab with the pyramid plot
	 * 
	 * @param tabFolder1
	 *            : parent folder
	 */
	private void makePyramidTab(TabFolder tabFolder1) {
		/* create the overall composite within the tab */

		Composite pyramidComposite = new Composite(tabFolder1, SWT.NONE);
		TabItem item1 = new TabItem(tabFolder1, SWT.NONE);

		/*
		 * the composite has two elements: - a column with control elements
		 * where the user can make choices - a plot area
		 */

		/* create a composite that contains the control elements */
		Composite controlComposite = new Composite(pyramidComposite, SWT.NONE);
		controlComposite.setBounds(0, 0, 50, 300);

		RowLayout rowLayout = new RowLayout();
		controlComposite.setSize(600, 600);
		controlComposite.setLayout(rowLayout);
		/* create the plot composite */

		pyramidComposite.setSize(600, 600);
		pyramidComposite.setLayout(rowLayout);

		plottedScen = 1;
		int timestep = 0;

		scale = new Scale(pyramidComposite, SWT.VERTICAL);
		scale.setBounds(0, 0, 40, 200);
		scale.setMaximum(stepsInRun);
		scale.setMinimum(0);
		scale.setIncrement(1);
		scale.setPageIncrement(1);
		scale.setSelection(stepsInRun);
		RowData rowData2 = new RowData(40, 500);
		scale.setLayoutData(rowData2);
		scale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int perspectiveValue = scale.getMaximum()
						- scale.getSelection() + scale.getMinimum();
				value.setText("Year: "
						+ (perspectiveValue + output.getStartYear()));
				JFreeChart pyramidChart = output
						.makePyramidChartIncludingDisease(plottedScen,
								perspectiveValue, -1);
				chartComposite.setChart(pyramidChart);
				chartComposite.redraw();
				chartComposite.forceRedraw();

			}

		});
		value = new Text(pyramidComposite, SWT.BORDER | SWT.SINGLE);

		value.setEditable(false);
		RowData rowData4 = new RowData(55, 25);
		value.setLayoutData(rowData4);
		JFreeChart pyramidChart = output.makePyramidChartIncludingDisease(
				plottedScen, timestep, -1);
		RowData rowData3 = new RowData(450, 500);
		chartComposite = new ChartComposite(pyramidComposite, SWT.NONE,
				pyramidChart, true);
		chartComposite.setDisplayToolTips(true);
		chartComposite.setHorizontalAxisTrace(false);
		chartComposite.setVerticalAxisTrace(false);
		chartComposite.setLayoutData(rowData3);
		item1.setText("Pyramid");
		item1.setControl(pyramidComposite);

	}

	/*
	 * these fields are needed to hold the scenario or disease that the user has
	 * selected for plotting
	 */
	/*
	 * these names are used for the tabfolder diseaseplots do not reuse them
	 * elsewhere, otherwise this will mess up the working of this folder
	 */
	private int currentScen;
	private int currentDisease;
	private int currentYear;
	private int plotType;
	private int genderChoice;
	/*
	 * plotType=0: by riskType plotType=1: by scenario plotType=2: by riskfactor
	 */
	private boolean axisIsAge = false;
	private boolean differencePlot = false;
	private boolean numbers = false;

	/*
	 * if false, axis=year
	 */

	/**
	 * @param tabFolder1
	 *            : parent folder
	 * @throws DynamoOutputException
	 */

	private void makeDiseasePlotTab(TabFolder tabFolder1)
			throws DynamoOutputException {
		Composite plotComposite = new Composite(tabFolder1, SWT.FILL);
		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		plotComposite.setLayout(gridLayout);
		currentScen = 0;
		if (output.getNScen() > 0)
			currentScen = 1;
		currentDisease = 0;
		currentYear = 0;
		plotType = 1;
		differencePlot = false;
		axisIsAge = false;
		numbers = false;
		genderChoice = 2;
		// plotComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		Composite controlComposite = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		// controlData.widthHint = 100;

		// controlComposite.setLayout(new RowLayout(SWT.VERTICAL));
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		final ChartComposite chartComposite2 = new ChartComposite(
				plotComposite, SWT.NONE, makeDiseaseChart(), true);

		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite2.setLayoutData(chartData);

		/*
		 * first radio group
		 */
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("X-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup1.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button yearButton = new Button(radiogroup1, SWT.RADIO);
		yearButton.setText("Year");
		yearButton.setSelection(true);

		yearButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						axisIsAge = false;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}

				}

			}
		}));
		Button ageButton = new Button(radiogroup1, SWT.RADIO);
		ageButton.setText("Age");
		// ageButton.setBounds(10,50,20,100);
		ageButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						axisIsAge = true;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}

				}

			}
		}));

		/*
		 * second radio group
		 */
		Group radiogroup2 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup2.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup2.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button rateButton = new Button(radiogroup2, SWT.RADIO);
		rateButton.setText("scenario prevalence");
		rateButton.setSelection(true);

		rateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						differencePlot = false;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}

				}

			}
		}));
		Button differenceButton = new Button(radiogroup2, SWT.RADIO);
		differenceButton.setText("Difference with reference scenario");
		// ageButton.setBounds(10,50,20,100);
		differenceButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						differencePlot = true;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}

				}

			}
		}));

		/*
		 * third radio group
		 */
		Group radiogroup3 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup3.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));

		radiogroup3.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button rate2Button = new Button(radiogroup3, SWT.RADIO);
		rate2Button.setText("Prevalence rate");
		rate2Button.setSelection(true);

		rate2Button.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						numbers = false;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}

				}

			}
		}));
		Button numberButton = new Button(radiogroup3, SWT.RADIO);
		numberButton.setText("number of cases (not yet implemented)");
		// ageButton.setBounds(10,50,20,100);
		numberButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						numbers = true;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}

				}

			}
		}));

		/*
		 * fourth radio group
		 */
		Group radiogroup4 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup4.setText("separate curves:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayoutGroup4 = new GridLayout();
		gridLayoutGroup4.numColumns = 2;
		radiogroup4.setLayout(gridLayoutGroup4);
		// yearButton.setBounds(10,10,20,100);

		Button byRiskClassButton = new Button(radiogroup4, SWT.RADIO);
		byRiskClassButton.setText("by riskclass");

		byRiskClassButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				if (button.getSelection()) {
					try {
						plotType = 0;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						e1.printStackTrace();
						displayErrorMessage(e1);
					}
				}
			}
		}

		));
		Button byScenarioButton = new Button(radiogroup4, SWT.RADIO);
		byScenarioButton.setText("by scenario");
		byScenarioButton.setSelection(true);
		// ageButton.setBounds(10,50,20,100);
		byScenarioButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						plotType = 1;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						displayErrorMessage(e1);

						e1.printStackTrace();
					}
				}
				;// do plot
			}
		}));

		Button bySexButton = new Button(radiogroup4, SWT.RADIO);
		bySexButton.setText("by gender");

		// ageButton.setBounds(10,50,20,100);
		bySexButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						plotType = 2;
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {

						displayErrorMessage(e1);
						e1.printStackTrace();
					}
				}
				;// do plot
			}
		}));

		/*
		 * first list of choice
		 */

		Group listgroup1 = new Group(controlComposite, SWT.VERTICAL);
		listgroup1.setText("scenario:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		listgroup1.setLayout(new RowLayout(SWT.VERTICAL));
		final Combo combo1 = new Combo(listgroup1, SWT.DROP_DOWN
				| SWT.READ_ONLY);

		String[] scenNames = output.getScenarioNames();
		combo1.setItems(scenNames);
		if (scenNames.length > 1)
			combo1.select(1);
		else
			combo1.select(0);

		/*
		 * listeners for the lists
		 */

		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo1 = (Combo) e.getSource();
				currentScen = combo1.getSelectionIndex();

				try {
					JFreeChart chart = makeDiseaseChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();
				} catch (DynamoOutputException e1) {

					displayErrorMessage(e1);
					e1.printStackTrace();
				}

			}
		});

		/*
		 * 1 second lis of choice
		 */

		Group listgroup2 = new Group(controlComposite, SWT.VERTICAL);

		listgroup2.setText("disease:");
		//    
		listgroup2.setLayout(new RowLayout(SWT.VERTICAL));

		final Combo combo2 = new Combo(listgroup2, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		combo2.setItems(output.getDiseaseNames());
		combo2.select(0);
		combo2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo2 = (Combo) e.getSource();
				currentDisease = combo2.getSelectionIndex();
				JFreeChart chart = null;
				try {
					chart = makeDiseaseChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();
				} catch (DynamoOutputException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				chartComposite2.setChart(chart);

			}
		});

		/*
		 * third list of choices: year to be plotted. only needed for axis=age
		 */

		Group listgroup3 = new Group(controlComposite, SWT.VERTICAL
				| SWT.V_SCROLL);
		listgroup3.setText("year:");
		GridLayout gridLayoutGroup3 = new GridLayout();
		gridLayoutGroup3.numColumns = 3;
		listgroup3.setLayout(gridLayoutGroup3);

		final Combo combo3 = new Combo(listgroup3, SWT.DROP_DOWN
				| SWT.READ_ONLY);

		String[] yearNames = new String[stepsInRun + 1];
		for (int i = 0; i < stepsInRun + 1; i++)
			yearNames[i] = ((Integer) (startYear + i)).toString();
		combo3.setItems(yearNames);
		combo3.select(0);

		combo3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo3 = (Combo) e.getSource();
				currentYear = combo3.getSelectionIndex();
				JFreeChart chart = null;
				try {
					chart = makeDiseaseChart();
					;
					chartComposite2.setChart(chart);

					chartComposite2.forceRedraw();
				} catch (DynamoOutputException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				chartComposite2.setChart(chart);

			}
		});
		/*
		 * forth list of choice
		 */

		Group listgroup4 = new Group(controlComposite, SWT.VERTICAL);
		listgroup4.setText("gender (applies only to by scenario):");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		listgroup4.setLayout(new RowLayout(SWT.VERTICAL));
		final Combo combo4 = new Combo(listgroup4, SWT.DROP_DOWN
				| SWT.READ_ONLY);

		String[] choices = { "men", "women", "both" };
		combo4.setItems(choices);
		combo4.select(2);

		/*
		 * listeners for the lists
		 */

		combo4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo4 = (Combo) e.getSource();
				genderChoice = combo4.getSelectionIndex();

				try {
					JFreeChart chart = makeDiseaseChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();
				} catch (DynamoOutputException e1) {

					displayErrorMessage(e1);
					e1.printStackTrace();
				}

			}
		});

		// list1.setSize(100,200);

		/*
		 * Parameters: comp - The parent. style - The style of the composite.
		 * jfreechart - the chart. width - the preferred width of the panel.
		 * height - the preferred height of the panel. minimumDrawW - the
		 * minimum drawing width. minimumDrawH - the minimum drawing height.
		 * maximumDrawW - the maximum drawing width. maximumDrawH - the maximum
		 * drawing height. usingBuffer - a flag that indicates whether to use
		 * the off-screen buffer to improve performance (at the expense of
		 * memory). properties - a flag indicating whether or not the chart
		 * property editor should be available via the popup menu. save - a flag
		 * indicating whether or not save options should be available via the
		 * popup menu. print - a flag indicating whether or not the print option
		 * should be available via the popup menu. zoom - a flag indicating
		 * whether or not zoom options should be added to the popup menu.
		 * tooltips - a flag indicating whether or not tooltips should be
		 * enabled for the chart.
		 */

		/* draw chart for the startup-situation */

		TabItem item2 = new TabItem(tabFolder1, SWT.NONE);
		item2.setText("disease plots");
		item2.setControl(plotComposite);
	}

	/*
	 * this chooses the right chart to plot
	 */
	private JFreeChart makeDiseaseChart() throws DynamoOutputException {
		JFreeChart chart = null;
		try {

			/*
			 * plotType= 0: by sex 1: by scenario 2: by risk class
			 * 
			 * TODO
			 */
			if ((plotType == 2) && !axisIsAge)
				chart = output.makeYearPrevalenceByGenderPlot(currentScen,
						currentDisease, differencePlot, numbers);
			if ((plotType == 1) && !axisIsAge)
				chart = output.makeYearPrevalenceByScenarioPlots(genderChoice,
						currentDisease, differencePlot, numbers);
			if ((plotType == 0) && !axisIsAge)
				chart = output.makeYearPrevalenceByRiskFactorPlots(currentScen,
						currentDisease, genderChoice, differencePlot, numbers);
			if ((plotType == 2) && axisIsAge)
				chart = output.makeAgePrevalenceByGenderPlot(currentScen,
						currentDisease, currentYear, differencePlot, numbers);
			if ((plotType == 1) && axisIsAge)
				chart = output.makeAgePrevalenceByScenarioPlot(2,
						currentDisease, currentYear, differencePlot, numbers);
			if ((plotType == 0) && axisIsAge)
				chart = output.makeAgePrevalenceByRiskFactorPlots(currentScen,
						currentDisease, currentYear, differencePlot, numbers);

			return chart;

		} catch (DynamoOutputException e1) {
			e1.printStackTrace();
			throw new DynamoOutputException(e1.getMessage());

		}

	}

	/* fields for mortality plotting */
	boolean mortAxisIsAge = false;
	boolean mortNumbers = true;
	boolean mortDifference = false;
	boolean survival = false;
	int mortPlotType = 0;

	/* fields for riskfactor plotting */

	private boolean differencePlot2;
	private boolean numbers2;

	private JFreeChart makeMortalityChart() {
		JFreeChart chart = null;

		/*
		 * plotType= 0: by sex 1: by scenario 2: by risk class
		 * 
		 * TODO
		 */
		if ((mortPlotType == 2) && !mortAxisIsAge && !mortNumbers) {
			chart = output.makeYearMortalityPlotByScenario(currentScen,
					differencePlot, mortNumbers);

			if ((mortPlotType == 1) && !mortAxisIsAge && !mortNumbers)
				chart = output.makeYearMortalityPlotByScenario(currentScen,
						differencePlot, mortNumbers);
			if ((mortPlotType == 0) && !mortAxisIsAge && !mortNumbers)
				chart = output.makeYearMortalityPlotByScenario(currentScen,
						differencePlot, mortNumbers);
			if ((mortPlotType == 2) && mortAxisIsAge && !mortNumbers)
				chart = output.makeYearMortalityPlotByScenario(currentScen,
						differencePlot, mortNumbers);
			if ((mortPlotType == 1) && mortAxisIsAge && !mortNumbers)
				chart = output.makeYearMortalityPlotByScenario(currentScen,
						differencePlot, mortNumbers);
			if ((mortPlotType == 0) && mortAxisIsAge && !mortNumbers)
				chart = output.makeYearMortalityPlotByScenario(currentScen,
						differencePlot, mortNumbers);

			return chart;
		}
		return chart;

	}

	/**
	 * @param tabFolder1
	 * @throws DynamoOutputException
	 */
	private void makeLifeExpectancyTab(TabFolder tabFolder1)
			throws DynamoOutputException {
		Composite plotComp2 = new Composite(tabFolder1, SWT.NONE);

		ChartComposite chartComposite4 = new ChartComposite(plotComp2,
				SWT.NONE, output.makeLifeExpectancyPlot(1), true);
		chartComposite4.setBounds(0, 0, 400, 500);
		TabItem item4 = new TabItem(tabFolder1, SWT.NONE);
		item4.setText("life expectancy plots");
		item4.setControl(plotComp2);
	}

	/**
	 * @param tabFolder1
	 * @throws DynamoOutputException
	 */
	private void makeRiskFactorTab(TabFolder tabFolder1)
			throws DynamoOutputException {
		Composite plotComp1 = new Composite(tabFolder1, SWT.NONE);
		;
		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		plotComp1.setLayout(gridLayout);
		Composite controlComposite = new Composite(plotComp1, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);
		final ChartComposite chartComposite2 =  new ChartComposite(plotComp1,
				SWT.NONE, output.makeYearRiskFactorByGenderPlot(1,
						differencePlot2, numbers2), true);
	
		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite2.setLayoutData(chartData);
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("X-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup1.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button yearButton = new Button(radiogroup1, SWT.RADIO);
		yearButton.setText("Year");
		yearButton.setSelection(true);

		yearButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortAxisIsAge = false;
					JFreeChart chart = output.makeYearRiskFactorByGenderPlot(1,
							differencePlot2, numbers2);
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}

		}));
		Button ageButton = new Button(radiogroup1, SWT.RADIO);
		ageButton.setText("Age");
		// ageButton.setBounds(10,50,20,100);
		ageButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortAxisIsAge = true;
					JFreeChart chart = output.makeYearRiskFactorByGenderPlot(1,
							differencePlot2, numbers2);
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}
			}
		}));
		
		/*
		 * second radio group
		 */
		Group radiogroup2 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup2.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup2.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button rateButton = new Button(radiogroup2, SWT.RADIO);
		rateButton.setText("scenario data");
		rateButton.setSelection(true);

		rateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortDifference = false;
					JFreeChart chart = output.makeYearRiskFactorByGenderPlot(1,
							differencePlot2, numbers2);
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}
		}));
		Button differenceButton = new Button(radiogroup2, SWT.RADIO);
		differenceButton.setText("Difference with reference scenario");
		// ageButton.setBounds(10,50,20,100);
		differenceButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortDifference = true;
					JFreeChart chart =output.makeYearRiskFactorByGenderPlot(1,
							differencePlot2, numbers2);
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}
		}));

		/*
		 * third radio group
		 */
		Group radiogroup3 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup3.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));

		radiogroup3.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button rate2Button = new Button(radiogroup3, SWT.RADIO);
		rate2Button.setText("Prevalence rate");
		rate2Button.setSelection(true);

		rate2Button.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					numbers = false;
					JFreeChart chart = output.makeYearRiskFactorByGenderPlot(1,
							differencePlot2, numbers2);
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}
		}));
		Button numberButton = new Button(radiogroup3, SWT.RADIO);
		numberButton.setText("number of cases (not yet implemented)");
		// ageButton.setBounds(10,50,20,100);
		numberButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					numbers = true;
					JFreeChart chart = output.makeYearRiskFactorByGenderPlot(1,
							differencePlot2, numbers2);
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}
		}));

		
		
		
		
		TabItem item3 = new TabItem(tabFolder1, SWT.NONE);
		item3.setText("risk factor plots");
		item3.setControl(plotComp1);
	}

	/**
	 * @param tabFolder1
	 * @throws DynamoOutputException
	 */

	private void makeChangeScenarioTab(TabFolder tabFolder1)
			throws DynamoOutputException {
		Composite tabComposite = new Composite(tabFolder1, SWT.NONE);

		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout(SWT.VERTICAL, axisIsAge);
		gridLayout.numColumns = 7;
		tabComposite.setLayout(gridLayout);
		Label heading1 = new Label(tabComposite, SWT.NONE);
		heading1.setText("Scenario:");

		Label heading2 = new Label(tabComposite, SWT.NONE);
		heading2.setText("Succes rate:");
		GridData data2 = new GridData();
		// data2.widthHint = 60;
		data2.horizontalSpan = 2;
		heading2.setLayoutData(data2);
		Label heading3 = new Label(tabComposite, SWT.NONE);
		heading3.setText("Minimum Age:");
		GridData data3 = new GridData();
		// data3.widthHint = 60;
		data3.horizontalSpan = 2;
		heading3.setLayoutData(data3);
		Label heading4 = new Label(tabComposite, SWT.NONE);
		heading4.setText("Maximum Age:");
		GridData data4 = new GridData();
		// data4.widthHint = 60;
		data4.horizontalSpan = 2;
		heading4.setLayoutData(data4);

		Label[] label = new Label[output.getNScen()];
		final Slider[] slider1 = new Slider[output.getNScen()];
		final Text[] value1 = new Text[output.getNScen()];
		final Text[] value2 = new Text[output.getNScen()];
		final Text[] value3 = new Text[output.getNScen()];
		final Slider[] slider2 = new Slider[output.getNScen()];
		final Slider[] slider3 = new Slider[output.getNScen()];
		for (int i = 0; i < output.getNScen(); i++) {
			label[i] = new Label(tabComposite, SWT.NONE);
			label[i].setText(output.getScenarioNames()[i + 1]);
			
			slider1[i] = new Slider(tabComposite, SWT.HORIZONTAL);
			value1[i] = new Text(tabComposite, SWT.BORDER | SWT.SINGLE);
			value1[i].setEditable(false);
			slider1[i].setMaximum(110);/* the slider needs 10 for the thump */
			slider1[i].setMinimum(0);
			slider1[i].setIncrement(1);
			slider1[i].setThumb(10);
			slider1[i].setSelection((int) output.getSuccesrate()[i]);
			value1[i].setText(((Float) output.getSuccesrate()[i]).toString());
			value1[i].setLayoutData(fixedSpace());
			slider1[i].setLayoutData(new GridData());

			slider1[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					Slider slider = (Slider) ((SelectionEvent) event)
							.getSource();
					int perspectiveValue = +slider.getSelection();
					int currenti = 0;
					for (int i = 0; i < output.getNScen(); i++) {
						if (slider1[i] == slider)
							currenti = i;
					}

					value1[currenti]
							.setText(((Float) output.getSuccesrate()[currenti])
									.toString());
					output.setSuccesrate(perspectiveValue, currenti);

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}
			});
			slider2[i] = new Slider(tabComposite, SWT.HORIZONTAL);
			value2[i] = new Text(tabComposite, SWT.BORDER | SWT.SINGLE);
			value2[i].setEditable(false);
			value2[i].setLayoutData(fixedSpace());
			slider2[i].setMaximum(105);/* 10 is needed for thumb */
			slider2[i].setMinimum(0);
			slider2[i].setIncrement(1);
			slider2[i].setPageIncrement(1);
			slider2[i].setThumb(10);
			slider2[i].setSelection((int) output.getMinAge()[i]);
			value2[i].setText(((Float) output.getMinAge()[i]).toString());

			slider2[i].setLayoutData(new GridData());

			slider2[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					Slider slider = (Slider) ((SelectionEvent) event)
							.getSource();
					int perspectiveValue = slider.getSelection();
					int currenti = 0;
					for (int i = 0; i < output.getNScen(); i++) {
						if (slider2[i] == slider)
							currenti = i;
					}

					value2[currenti]
							.setText(((Float) output.getMinAge()[currenti])
									.toString());
					output.setMinAge(perspectiveValue, currenti);

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}
			});
			slider3[i] = new Slider(tabComposite, SWT.HORIZONTAL);
			value3[i] = new Text(tabComposite, SWT.BORDER | SWT.SINGLE);
			value3[i].setEditable(false);
			value3[i].setLayoutData(fixedSpace());
			slider3[i].setMaximum(105);
			slider3[i].setMinimum(0);
			slider3[i].setIncrement(1);
			slider3[i].setPageIncrement(1);
			slider3[i].setSelection((int) output.getMaxAge()[i]);
			slider3[i].setThumb(10);
			value3[i].setText(((Float) output.getMaxAge()[i]).toString());

			slider3[i].setLayoutData(new GridData());

			slider3[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					Slider slider = (Slider) ((SelectionEvent) event)
							.getSource();
					int perspectiveValue = slider.getSelection();
					int currenti = 0;
					for (int i = 0; i < output.getNScen(); i++) {
						if (slider3[i] == slider)
							currenti = i;
					}

					value3[currenti]
							.setText(((Float) output.getMaxAge()[currenti])
									.toString());
					output.setMaxAge(perspectiveValue, currenti);

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}

			});
		}

		/*
		 * 
		 * } GridData[] data1=new GridData[output.getNScen()+1]; Label[]
		 * heading=new Label[output.getNScen()]; Label empty=new
		 * Label(tabComposite,SWT.NONE);
		 * 
		 * for (int scen=0; scen<output.getNScen();scen++){ heading[scen]=new
		 * Label(tabComposite,SWT.NONE);
		 * heading[scen].setText(output.getScenarioNames()[scen]); } final
		 * Text[] text1=new Text[output.getNScen()]; final Text[] text2=new
		 * Text[output.getNScen()]; final Text[] text3=new
		 * Text[output.getNScen()]; Label label1=new
		 * Label(tabComposite,SWT.NONE); label1.setText("success rate"); for
		 * (int scen=0; scen<output.getNScen();scen++){ text1[scen]= new
		 * Text(tabComposite, SWT.BORDER | SWT.FILL);
		 * text1[scen].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		 * text1[scen].setText(
		 * ((Float)output.getSuccesrate()[scen]).toString());
		 * text1[scen].setTextLimit(10); final int currentscen=scen;
		 * text1[scen].addModifyListener(new ModifyListener() { public void
		 * modifyText(ModifyEvent e) { float cValue = Float.parseFloat(((Text)
		 * e.getSource()).getText()); output.setSuccesrate(cValue,currentscen);
		 * ;} } );
		 * 
		 * text1[scen].addVerifyListener(new VerifyListener() { public void
		 * verifyText(VerifyEvent e) { try { Float.parseFloat(e.text);
		 * e.doit=true; } catch (NumberFormatException e1) { e.doit=false;
		 * e.text="error"; } return; } }
		 * 
		 * ); } Label label2=newLabel(tabComposite,SWT.NONE);label2.setText(
		 * "minimum age of intervention group"); for (int scen=0;
		 * scen<output.getNScen();scen++){ text2[scen]= new Text(tabComposite,
		 * SWT.BORDER| SWT.FILL); text2[scen].setText(
		 * ((Float)output.getMinAge()[scen]).toString());
		 * text2[scen].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		 * text2[scen].setTextLimit(10); } Label label3=new
		 * Label(tabComposite,SWT
		 * .NONE);label3.setText("maximum age of intervention group"); for (int
		 * scen=0; scen<output.getNScen();scen++){ text3[scen]= new
		 * Text(tabComposite, SWT.BORDER| SWT.FILL); text3[scen].setText(
		 * ((Float)output.getMaxAge()[scen]).toString());
		 * text3[scen].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		 * text3[scen].setTextLimit(10); }
		 */

		TabItem item4 = new TabItem(tabFolder1, SWT.NONE);
		item4.setText("change scenario settings");
		item4.setControl(tabComposite);

	}

	private GridData fixedSpace() {
		GridData data = new GridData();
		data.widthHint = 30;
		return data;
	}

	/**
	 * @param tabFolder1
	 * @throws DynamoOutputException
	 */
	

	private void makeMortalityTab(TabFolder tabFolder1)
			throws DynamoOutputException {
		survival=false;
		Composite plotComp1 = new Composite(tabFolder1, SWT.NONE);
		;
		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		plotComp1.setLayout(gridLayout);
		Composite controlComposite = new Composite(plotComp1, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);
		final ChartComposite chartComposite2 = new ChartComposite(plotComp1,
				SWT.NONE, output
						.makeYearMortalityPlotByScenario(2, false, true), true);
		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite2.setLayoutData(chartData);
		Group radiogroup1 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup1.setText("X-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup1.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button yearButton = new Button(radiogroup1, SWT.RADIO);
		yearButton.setText("Year");
		yearButton.setSelection(true);

		yearButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortAxisIsAge = false;
					JFreeChart chart = makeMortalityChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}

		}));
		Button ageButton = new Button(radiogroup1, SWT.RADIO);
		ageButton.setText("Age");
		// ageButton.setBounds(10,50,20,100);
		ageButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortAxisIsAge = true;
					JFreeChart chart = makeMortalityChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}
			}
		}));
		
		/*
		 * second radio group
		 */
		Group radiogroup2 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup2.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup2.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button rateButton = new Button(radiogroup2, SWT.RADIO);
		rateButton.setText("scenario data");
		rateButton.setSelection(true);

		rateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortDifference = false;
					JFreeChart chart = makeMortalityChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}
		}));
		Button differenceButton = new Button(radiogroup2, SWT.RADIO);
		differenceButton.setText("Difference with reference scenario");
		// ageButton.setBounds(10,50,20,100);
		differenceButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					mortDifference = true;
					JFreeChart chart = makeMortalityChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();

				}

			}
		}));

		
		/*
		 * fourth radio group
		 */
		Group radiogroup4 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup4.setText("outcome:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayoutGroup4 = new GridLayout();
		gridLayoutGroup4.numColumns = 2;
		radiogroup4.setLayout(gridLayoutGroup4);
		// yearButton.setBounds(10,10,20,100);

		Button mortButton = new Button(radiogroup4, SWT.RADIO);
		mortButton.setText("mortality");

		mortButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				if (button.getSelection()) {
					survival=false;
					JFreeChart chart = makeMortalityChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();
				}
			}
		}

		));
		Button survivalButton = new Button(radiogroup4, SWT.RADIO);
		survivalButton.setText("survival");
		survivalButton.setSelection(true);
		// ageButton.setBounds(10,50,20,100);
		survivalButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					survival=true;
					JFreeChart chart = makeMortalityChart();
					chartComposite2.setChart(chart);
					chartComposite2.forceRedraw();
				}
				;// do plot
			}
		}));

		
		
		
		TabItem item3 = new TabItem(tabFolder1, SWT.NONE);
		item3.setText("mortality/survival plots");
		item3.setControl(plotComp1);
	}

	
}
