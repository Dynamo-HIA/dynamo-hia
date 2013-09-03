/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;



import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;


/**
 * @author  boshuizh
 */
public class Output_PyramidTab  {
	
	Log log = LogFactory
	.getLog("nl.rivm.emi.dynamo.Output_PyramidTab");

	private TabFolder tabFolder;
	/* plotGenerator is the general object that contains the data and makes all possible plots */
	DynamoPlotFactory plotGenerator;
	ButtonStates plotInfo;
	/* this is the object that is used by the listener to make plots based on the
	 * information in the button states 
	 */
	PyramidChartFactory plotFactory;
     ChartComposite chartComposite;
	private Composite plotComposite;

	/**
	 * @param tabfolder
	 * @param output
	 */
	public Output_PyramidTab(TabFolder tabfolder , DynamoPlotFactory output) {
	this.tabFolder=tabfolder;
	this.plotGenerator=output;
	this.plotFactory=new PyramidChartFactory(output);
    	
	makeIt();
	}
	
	/**
	 * makes the tabfolder
	 */
	public void makeIt(){
		
		/*
		 * the composite has two elements: - a column with control elements
		 * where the user can make choices - a plot area
		 * 
		 * plotComposite= the overall composite
		 * controlComposite= the composite with the controls
		 */

		this.plotComposite = new Composite(this.tabFolder, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.plotComposite.setLayout(gridLayout);
		
		/* create a composite that contains the control elements */
		/* this has two columns: one with the slider, the other with the other controls */
		Composite controlComposite = new Composite(this.plotComposite, SWT.NONE);

		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 2;
		GridData controlData = new GridData(GridData.FILL_VERTICAL);
		controlData.grabExcessVerticalSpace = true;
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		/* put the default plot information in the object plotInfo */
		this.plotInfo=new ButtonStates();
		this.plotInfo.currentYear=0;
		this.plotInfo.currentAge=0;
		this.plotInfo.plotType=0;
		this.plotInfo.differencePlot=false;
		/* currentDisease: 0=none, 1=disability, 2=all diseases 3+: diseasenames */
		this.plotInfo.currentDisease=1;
		if (this.plotGenerator.output.getNDiseases()>0)  this.plotInfo.currentDisease=2;
		this.plotInfo.currentScen = 1;
		if (this.plotGenerator.output.getNScen()==0) this.plotInfo.currentScen=0;

		
		
		
		
		/* make the slider */
		
		if (this.plotGenerator.output.getStepsInRun()>0) {
		final Scale scale = new Scale(controlComposite, SWT.VERTICAL);
		scale.setToolTipText("change year of population pyramid");
		
		// scale.setBounds(0, 0, 40, 200);
		scale.setMaximum(this.plotGenerator.output.getStepsInRun());
		scale.setMinimum(0);
		scale.setIncrement(1);
		scale.setPageIncrement(1);
		scale.setSelection(this.plotGenerator.output.getStepsInRun());

		GridData data3 = new GridData(GridData.FILL_VERTICAL);
		data3.verticalSpan = 4;
		data3.grabExcessVerticalSpace = true;
		scale.setLayoutData(data3);
		
		/* the text that shows the current year */
		
		final StyledText value = new StyledText(controlComposite, SWT.SINGLE | SWT.BOLD|SWT.LONG);
        value.setText("Year: "
						+  this.plotGenerator.output.getStartYear());
		value.setEditable(false);
		final StyleRange style1 = new StyleRange();
	    style1.start = 0;
	    style1.length = 10;
	     
	    
	    style1.fontStyle = SWT.BOLD;
	    style1. background = this.tabFolder.getBackground();
	    ;
	    Font font = new Font(this.tabFolder.getParent().getDisplay(), "SansSerif", 13, SWT.BOLD);
	    value.setFont(font);

	    value.setStyleRange(style1);

	    
		
		
		scale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int userValue = scale.getMaximum()
						- scale.getSelection() + scale.getMinimum();
				value.setText("Year: "
						+ (userValue + Output_PyramidTab.this.plotGenerator.output.getStartYear()));
				value.setStyleRange(style1);
				Output_PyramidTab.this.plotInfo.currentYear = userValue;
				Output_PyramidTab.this.plotFactory.drawChartAction(Output_PyramidTab.this.plotInfo, Output_PyramidTab.this.chartComposite );
				

			}

		});
		}
		
		
		
		
		/* the plot area  (needs to be constructed first as the controls need to use this)*/
		
		JFreeChart pyramidChart = this.plotGenerator.makePyramidChartIncludingDisease(
				this.plotInfo.currentScen, this.plotInfo.currentYear, this.plotInfo.currentDisease-3,this.plotInfo.differencePlot,this.plotInfo.blackAndWhite);
		
		// RowData rowData3 = new RowData(450, 500);
//	 this.chartComposite =  new ChartComposite(this.plotComposite, SWT.NONE,
	//	pyramidChart, true, true ,true,true, true);
	 log.fatal(this.chartComposite.DEFAULT_HEIGHT+" "+this.chartComposite.DEFAULT_WIDTH+  " "+this.chartComposite.DEFAULT_MINIMUM_DRAW_HEIGHT
			 +  " "+this.chartComposite.DEFAULT_MINIMUM_DRAW_WIDTH+  " "+this.chartComposite.DEFAULT_MAXIMUM_DRAW_HEIGHT
			 +  " "+this.chartComposite.DEFAULT_MAXIMUM_DRAW_WIDTH);

	    this.chartComposite= new ChartComposite(this.plotComposite, SWT.NONE, pyramidChart, 680, 420, 300, 200, 800,600, true, true ,true,true, true, true);
	    this.chartComposite.setDisplayToolTips(true);
	    this.chartComposite.setHorizontalAxisTrace(false);
	    this.chartComposite.setVerticalAxisTrace(false);
		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		this.chartComposite.setLayoutData(chartData);
		// chartComposite.setLayoutData(rowData3);

		
		/* add the other controls: */
		
		/* scenario choice */
		new ScenarioChoiceGroup(controlComposite, this.chartComposite, this.plotFactory, this.plotInfo, this.plotGenerator.output.getScenarioNames());
		String[] items = new String[2];
		if (this.plotGenerator.output.getNDiseases()>0)  items = new String[this.plotGenerator.output.getNDiseases() + 3];
		
		
		/* disease choice */
		String[] names = this.plotGenerator.output.getDiseaseNames();
		items[0] = "none";
		items[1] = "disability";
		if (items.length>2) {items[2] = "total disease";
		for (int i = 0; i < names.length; i++)
			items[i + 3] = names[i];}
		new DiseaseChoiceGroup(controlComposite, this.chartComposite,this. plotFactory, this.plotInfo, items);
		
		/* difference choice */
		new DifferenceChoiceGroup(controlComposite, chartComposite, this. plotFactory, this.plotInfo);
			
		new AutoRunButton(controlComposite, chartComposite, this. plotFactory, this.plotInfo, this.plotGenerator.output.getStepsInRun());
		new ColorChoiceGroup(controlComposite, chartComposite, this.plotFactory,this.plotInfo);
			
		/* make the tab */
		TabItem item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("Population Pyramid");
		item.setControl(this.plotComposite);
		
       
	}
	/**
	 * 
	 */
	public void redraw(){
		
		Control[] subcomp= this.plotComposite.getChildren();
		this.plotFactory.drawChartAction(this.plotInfo, (PyramidChartComposite) subcomp[1]);
		//this.plotComposite.redraw();
		
	}
		}