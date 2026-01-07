/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

/**
 * @author boshuizh
 */
public class Output_RiskFactorTab {

	private TabFolder tabFolder;
	private CDMOutputFactory output;
	private ButtonStates plotInfo;
	/* factory is the object that is used by the listener to make plots based on the
	 * information in the button states 
	 */
	private RiskFactorChartFactory factory;
	/* plotGenerator is the general object that contains the data and makes all possible plots */
	@SuppressWarnings("unused")
	private DynamoPlotFactory plotGenerator;

	private Composite plotComposite;

	public Output_RiskFactorTab(TabFolder tabfolder, DynamoPlotFactory dfact) {
		this.tabFolder = tabfolder;
		this.output = dfact.output;
		this.plotGenerator = dfact;
		this.factory = new RiskFactorChartFactory(dfact);

		makeIt();
	}

	public void makeIt() {
		/* put the default plot information in the object plotInfo */
		plotInfo = new ButtonStates();
		plotInfo.currentScen = 0;
		if (output.getNScen() > 0)
			plotInfo.currentScen = 1;
		plotInfo.currentDisease = 0;
		plotInfo.currentYear = 0;
		plotInfo.currentAge = 0;
		plotInfo.plotType = 1;
		plotInfo.differencePlot = false;
		plotInfo.axisIsAge = false;
		plotInfo.numbers = false;
		plotInfo.genderChoice = 2;
		/*
		 * plotComposite is the highest level composite in the folder it has two
		 * children: control composite containing the controls, and a
		 * chartcomposite containing the plot
		 */
		this.plotComposite = new Composite(tabFolder, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData compositeData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL); 
		//gridLayout.marginHeight=5;
		//gridLayout.marginWidth=5;
		this.plotComposite.setLayout(gridLayout);
		this.plotComposite.setLayoutData(compositeData);

		// control composite contains the controls

		Composite controlComposite = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		/* draw chart for the startup-situation */
		final ChartComposite chartComposite = new ChartComposite(plotComposite,
				SWT.NONE, null, true);
		factory.drawChartAction(plotInfo, chartComposite);

	//	GridLayout layoutChart= new GridLayout(1,true);
		//layoutChart.marginWidth=1;
		//layoutChart.marginHeight=1;
	//	layoutChart.marginWidth=1;		
	//	chartComposite.setLayout(layoutChart);
		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL 
				| GridData.GRAB_VERTICAL); 
		chartComposite.setLayoutData(chartData);
		/* draw the buttons */

		new AxisChoiceGroup(controlComposite, chartComposite, factory, plotInfo);
		new NumberChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo);
		new DifferenceChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo);

		String[] yearNames = new String[output.getStepsInRun() + 1];
		for (int i = 0; i < output.getStepsInRun() + 1; i++)
			yearNames[i] = ((Integer) (output.getStartYear() + i)).toString();
		
		/* if continuous riskfactor, then the user can also chose to see the mean value of the
		 * risk factor, so add this to the list of choices
		 */
		String [] classNames=output.getRiskClassnames();
		int nClasses=classNames.length;
		String [] temp=null;
		if (output.getRiskType()==2) {temp=new String [nClasses+1];
		for (int i=0; i<nClasses;i++) temp[i]=classNames[i];
		temp[nClasses]="average value";
		classNames=temp;
			}
		new RiskClassChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo, classNames);
		new YearChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo, yearNames);
		new GenderChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo);
		 new ColorChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo);
			
		TabItem item = new TabItem(tabFolder, SWT.NONE);
	//	change dd 22/11/2011 item.setText("riskfactor plots");
		item.setText("Risk factor");
		item.setControl(plotComposite);
		/*
		 * item2.addListener(SWT.Selection, new Listener() {
		 * 
		 * public void handleEvent(Event arg0) { JFreeChart chart =
		 * makeDiseaseChart(); chartComposite2.setChart(chart);
		 * chartComposite2.forceRedraw();
		 * 
		 * } });
		 */
	}

	public void redraw() {
		Control[] subcomp = plotComposite.getChildren();
		factory.drawChartAction(plotInfo, (ChartComposite) subcomp[1]);
	//	plotComposite.update();unnecessary, already part of action above
	}
}