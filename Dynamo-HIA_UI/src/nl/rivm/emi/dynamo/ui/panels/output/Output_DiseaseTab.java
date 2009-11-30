/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory_old;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author  boshuizh
 */
public class Output_DiseaseTab  {
	
	
	private TabFolder tabFolder;
	/* plotGenerator is the general object that contains the data and makes all possible plots */
	private DynamoPlotFactory  plotGenerator;
	private ButtonStates plotInfo;
	
	/* factory is the object that is used by the listener to make plots based on the
	 * information in the button states 
	 */
	private DiseaseChartFactory factory;
	private Composite plotComposite;
	private CDMOutputFactory  output;

	/**
	 * @param tabfolder
	 * @param plotFactory
	 */
	public Output_DiseaseTab(TabFolder tabfolder , DynamoPlotFactory dFactory) {
	this.tabFolder=tabfolder;
	this.plotGenerator=dFactory;
	this.output=dFactory.output;
	/* this is the object that is used by the listener to make plots based on the
	 * information in the button states 
	 */
	this.factory=new DiseaseChartFactory(plotGenerator);
	
	makeIt();
	}
	
	/**
	 * 
	 */
	public void makeIt(){
		/* put the default plot information resides in the object plotInfo */
		this.plotInfo=new ButtonStates();
		this.plotInfo.currentScen = 0;
		if (this.output.getNScen() > 0)
			this.plotInfo.currentScen = 1;
		
		this.plotInfo.currentDisease = 0;
		this.plotInfo.currentYear = 0;
		this.plotInfo.currentAge = 0;
		this.plotInfo.plotType = 1;
		this.plotInfo.differencePlot = false;
		this.plotInfo.axisIsAge = false;
		this.plotInfo.numbers = false;
		this.plotInfo.genderChoice = 2;
		/* plotComposite is the highest level composite in the folder
		* it has to children: control composite containing the controls, and a chartcomposite containing the plot
		*/
		this.plotComposite = new Composite(this.tabFolder, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.plotComposite.setLayout(gridLayout);
		
		// control composite contains the controls

		Composite controlComposite = new Composite(this.plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		
		
		/* draw chart for the startup-situation */
		final ChartComposite chartComposite = new ChartComposite(
				this.plotComposite, SWT.NONE, null, true);
        this.factory.drawChartAction(this.plotInfo, chartComposite);
		
        
        GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite.setLayoutData(chartData);
/* draw the buttons */
		
		new AxisChoiceGroup(controlComposite, chartComposite,this.factory, this.plotInfo);
		new NumberChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo);
		new DifferenceChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo);
			
       new ByChoiceGroup(controlComposite, chartComposite,this. factory,this. plotInfo);
		new ScenarioChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo,this.output.getScenarioNames());
        
		
		String [] names=new String [1];
		if (this.output.getNDiseases()>0){ names=new String[this.output.getDiseaseNames().length+1];
        for (int i=0; i<this.output.getDiseaseNames().length;i++)
        names[i]=this.output.getDiseaseNames()[i];}
        names[names.length-1]="disability";
        
		new DiseaseChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo,names);
		String[] yearNames = new String[this.output.getStepsInRun() + 1];
		for (int i = 0; i < this.output.getStepsInRun() + 1; i++)
			yearNames[i] = ((Integer) (this.output.getStartYear() + i)).toString();
		new YearChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo,yearNames);
		new GenderChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo);
        new ColorChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo);
		

		TabItem item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("disease plots");
		item.setControl(this.plotComposite);
		/*item2.addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event arg0) {
				JFreeChart chart = makeDiseaseChart();
				chartComposite2.setChart(chart);
				chartComposite2.forceRedraw();
				
			}
		});*/
	}
	
	/**
	 * 
	 */
	public void redraw(){
		
		Control[] subcomp= this.plotComposite.getChildren();
		this.factory.drawChartAction(this.plotInfo, (ChartComposite) subcomp[1]);
		this.plotComposite.redraw();
		
	}
		}