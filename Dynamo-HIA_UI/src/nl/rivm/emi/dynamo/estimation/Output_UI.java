/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

/**
 * @author boshuizh
 * 
 */
public class Output_UI {

	Display display = new Display();
	Scale scale;
	Text value;
	JFreeChart pyramidChart;
	int plottedScen;
	ChartComposite chartComposite;
	int stepsInRun;
	int startYear;
	DynamoOutputFactory output;

	public Output_UI(ScenarioInfo scen, String simName, Population[] pop) {

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

		Shell shell = new Shell(display);
		MessageBox messageBox = new MessageBox(shell, SWT.OK);
		messageBox
				.setMessage("error while calculating output."
						+ " Message given: " + e.getMessage()
						+ ". Program will close.");
		if (messageBox.open() == SWT.OK) {
			shell.dispose();
		}

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void makeOutputDisplay() throws DynamoOutputException {

		Shell shell = new Shell(display);
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

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

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
								perspectiveValue, 0);
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
				plottedScen, timestep, 0);
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
	/*
	 * plotType=0: by sex plotType=1: by scenario plotType=2: by riskfactor
	 */
	private boolean axisIsAge = false;

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
		currentDisease = 0;
		currentYear = 0;
		plotType = 0;
		axisIsAge = false;
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
					try {axisIsAge=false;
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
					try {axisIsAge=true;
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
		Group radiogroup3 = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup3.setText("separate curves:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup3.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button byRiskClassButton = new Button(radiogroup3, SWT.RADIO);
		byRiskClassButton.setText("by riskclass");

		byRiskClassButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				if (button.getSelection()) {
					try {plotType=0;
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
		Button byScenarioButton = new Button(radiogroup3, SWT.RADIO);
		byScenarioButton.setText("by scenario");
		// ageButton.setBounds(10,50,20,100);
		byScenarioButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {plotType=1;
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

		Button bySexButton = new Button(radiogroup3, SWT.RADIO);
		bySexButton.setText("by gender");
		bySexButton.setSelection(true);
		// ageButton.setBounds(10,50,20,100);
		bySexButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {plotType=2;
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

		Button byNoneButton = new Button(radiogroup3, SWT.RADIO);
		byNoneButton.setText("total");
		// ageButton.setBounds(10,50,20,100);
		byNoneButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					try {
						JFreeChart chart = makeDiseaseChart();
						chartComposite2.setChart(chart);
						chartComposite2.forceRedraw();
					} catch (DynamoOutputException e1) {
						// TODO Auto-generated catch block: naar messagebox
						e1.printStackTrace();
						displayErrorMessage(e1);
					}
				}
				;// do plot
			}
		}));
		/*
		 * first list of choice
		 */

		Group listgroup1 = new Group(controlComposite, SWT.VERTICAL
				| SWT.BORDER);
		listgroup1.setText("scenario:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		listgroup1.setLayout(new RowLayout(SWT.VERTICAL));

		final List list1 = new List(listgroup1, SWT.SINGLE);
		String[] scenNames = output.getScenarioNames();
		list1.setItems(scenNames);

		/*
		 * listeners for the lists
		 */

		list1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List list1 = (List) e.getSource();
				currentScen = list1.getSelectionIndex();

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

		;

		/*
		 * second list of choice
		 */

		Group listgroup2 = new Group(controlComposite, SWT.VERTICAL);

		listgroup2.setText("disease:");
		//    
		listgroup2.setLayout(new RowLayout(SWT.VERTICAL));

		final List list2 = new List(listgroup2, SWT.SINGLE | SWT.BORDER);
		list2.setItems(output.getDiseaseNames());

		list2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List list2 = (List) e.getSource();
				currentDisease = list2.getSelectionIndex();
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

		Group listgroup3 = new Group(controlComposite, SWT.VERTICAL);
		listgroup3.setText("year:");
		listgroup3.setLayout(new RowLayout(SWT.VERTICAL));
		final List list3 = new List(listgroup3, SWT.SINGLE | SWT.V_SCROLL);
		String[] yearNames = new String[stepsInRun + 1];
		for (int i = 0; i < stepsInRun + 1; i++)
			yearNames[i] = ((Integer) (startYear + i)).toString();
		list3.setItems(yearNames);

		list3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List list3 = (List) e.getSource();
				currentYear = list3.getSelectionIndex();
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
			if ((plotType == 0) && !axisIsAge)
				chart = output.makeYearPrevalenceByGenderPlot(currentScen,
						currentDisease);
			if ((plotType == 1) && !axisIsAge)
				chart = output.makeYearPrevalenceByGenderPlot(currentScen,
						currentDisease);
			if ((plotType == 2) && !axisIsAge)
				chart = output.makeYearPrevalenceByRiskFactorPlots(currentScen,
						currentDisease);
			if ((plotType == 0) && axisIsAge)
				chart = output.makeAgePrevalenceByGenderPlot(currentScen,
						currentDisease, currentYear);
			if ((plotType == 1) && axisIsAge)
				chart = output.makeAgePrevalenceByGenderPlot(currentScen,
						currentDisease, currentYear);
			if ((plotType == 2) && axisIsAge)
				chart = output.makeAgePrevalenceByGenderPlot(currentScen,
						currentDisease, currentYear);

			return chart;

		} catch (DynamoOutputException e1) {
			e1.printStackTrace();
			throw new DynamoOutputException(e1.getMessage());

		}

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
		List list2 = new List(plotComp1, SWT.MULTI);
		list2.setItems(output.getRiskClassnames());
		list2.setBounds(0, 0, 100, 200);
		ChartComposite chartComposite3 = new ChartComposite(plotComp1,
				SWT.NONE, output.makeRiskFactorPlots(1), true);
		chartComposite3.setBounds(100, 0, 400, 500);
		TabItem item3 = new TabItem(tabFolder1, SWT.NONE);
		item3.setText("risk factor plots");
		item3.setControl(plotComp1);
	}

	public void makePopulationPyramidPlot() {

		Display display = new Display();
		Shell shell = new Shell(display);
		RowLayout rowLayout = new RowLayout();
		shell.setSize(600, 600);
		shell.setLayout(rowLayout);
		shell.setText("Population PyramidPlot");
		shell.setLayout(rowLayout);

		plottedScen = 1;
		int timestep = 0;

		scale = new Scale(shell, SWT.VERTICAL);
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
								perspectiveValue, 0);
				chartComposite.setChart(pyramidChart);
				chartComposite.redraw();
				chartComposite.forceRedraw();

			}

		});
		value = new Text(shell, SWT.BORDER | SWT.SINGLE);

		value.setEditable(false);
		RowData rowData4 = new RowData(55, 25);
		value.setLayoutData(rowData4);
		JFreeChart pyramidChart = output.makePyramidChartIncludingDisease(
				plottedScen, timestep, 0);
		RowData rowData3 = new RowData(450, 500);
		chartComposite = new ChartComposite(shell, SWT.NONE, pyramidChart, true);
		chartComposite.setDisplayToolTips(true);
		chartComposite.setHorizontalAxisTrace(false);
		chartComposite.setVerticalAxisTrace(false);
		chartComposite.setLayoutData(rowData3);

		shell.open();

		// ChartComposite.forceRedraw() to redraw

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

		// frame.setVisible(true);
		// frame.setSize(200, 200);
		// frame.pack();

	}

}
