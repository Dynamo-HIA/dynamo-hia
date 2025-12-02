/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.global.ScenarioParameters;
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
 * @author  boshuizh
 */
public class Output_SurvivalTab  {
	
	
	private TabFolder tabFolder;
	private CDMOutputFactory output;
	private ButtonStates plotInfo;
	/* this is the object that is used by the listener to make plots based on the
	 * information in the button states 
	 */
	private SurvivalChartFactory factory;
	/* plotGenerator is the general object that contains the data and makes all possible plots */
	private DynamoPlotFactory  plotGenerator;

	private Composite plotComposite;

	public Output_SurvivalTab(TabFolder tabfolder , DynamoPlotFactory dfact) {
	this.tabFolder=tabfolder;
	this.output=dfact.output;
	this.plotGenerator= dfact;
	
	this.factory=new SurvivalChartFactory(dfact);
	
	makeIt();
	}
	
	public void makeIt(){
		/* put the default plot information in the object plotInfo */
		plotInfo=new ButtonStates();
		plotInfo.currentScen = 0;
		if (output.getNScen() > 0)
		plotInfo.currentScen = 1;
		plotInfo.currentDisease = 0;
		plotInfo.currentYear = 0;
		plotInfo.plotType = 1;
		plotInfo.population = false;
		plotInfo.differencePlot = false;
		plotInfo.axisIsAge = false;
		plotInfo.numbers = false;
		plotInfo.genderChoice = 2;
		plotInfo.riskClassChoice=0;/* first choice = none; other choices give nonsense outcomes,
		so no other choice is offered any longer */
		plotInfo.survival=false;
		/* plotComposite is the highest level composite in the folder
		* it has to children: control composite containing the controls, and a chartcomposite containing the plot
		*/
		this.plotComposite = new Composite(tabFolder, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		plotComposite.setLayout(gridLayout);
		
		// control composite contains the controls

		Composite controlComposite = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		
		
		/* draw chart for the startup-situation */
		final ChartComposite chartComposite = new ChartComposite(
				plotComposite, SWT.NONE, null, true);
        factory.drawChartAction(plotInfo, chartComposite);
		
        
        GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite.setLayoutData(chartData);
/* draw the buttons */
		
		new AxisChoiceGroup(controlComposite, chartComposite, factory, plotInfo);
		 new NumberChoiceGroup(controlComposite, chartComposite, factory, plotInfo);
		 new DifferenceChoiceGroup(controlComposite, chartComposite, factory, plotInfo);
			
        new SurvivalChoiceGroup(controlComposite, chartComposite, factory, plotInfo);
        /* last year of simulation has no mortality , but survival has, so t
         * can be chosen; if chosen for mortality the plot will be empty*/
        String[] yearNames = new String[output.getStepsInRun()+1 ];
		for (int i = 0; i < output.getStepsInRun() +1; i++)
			yearNames[i] = ((Integer) (output.getStartYear() + i)).toString();
		String[] classNames=new String[output.getRiskClassnames().length+1];
		classNames[0]="all";
		for (int i = 0; i < output.getRiskClassnames().length; i++)
			classNames[i+1] = output.getRiskClassnames()[i];
	new By2ChoiceGroup(controlComposite, chartComposite, factory,plotInfo);
	new YearChoiceGroup(controlComposite, chartComposite, factory,plotInfo,yearNames);
	new ScenarioChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo,this.output.getScenarioNames());
    
	
	new GenderChoiceGroup(controlComposite, chartComposite, factory,plotInfo);
	// this does not work correctly, as mortality by riskclass cannot be calculated simply from 
	// survival in case on non-zero transitions 
	//new RiskClassChoiceGroup(controlComposite, chartComposite, factory,plotInfo, classNames);
	 new ColorChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo);
		
		

		

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		//change dd 22/11/2011 
		item.setText("Mortality/Survival");
		item.setControl(plotComposite);
		

		
		
		/*item2.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event arg0) {
				JFreeChart chart = makeDiseaseChart();
				chartComposite2.setChart(chart);
				chartComposite2.forceRedraw();
				
			}
		});*/
	}
	
	public void redraw(){
	
		Control[] subcomp= plotComposite.getChildren();
		factory.drawChartAction(plotInfo, (ChartComposite) subcomp[1]);
	//	plotComposite.redraw();unnecessary, already part of action above
	}
		}