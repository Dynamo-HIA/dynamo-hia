/**
 * 
 */
package nl.rivm.emi.dynamo.output;



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
	
	
	private TabFolder tabFolder;
	DynamoOutputFactory output;
	ButtonStates plotInfo;
	PyramidChartFactory factory;
	ChartComposite chartComposite;
	private Composite plotComposite;

	/**
	 * @param tabfolder
	 * @param output
	 */
	public Output_PyramidTab(TabFolder tabfolder , DynamoOutputFactory output) {
	this.tabFolder=tabfolder;
	this.output=output;
	this.factory=new PyramidChartFactory(output);
    
	
	makeIt();
	}
	
	/**
	 * makes the tabfolder
	 */
	public void makeIt(){
		
		/*
		 * the composite has two elements: - a column with control elements
		 * where the user can make choices - a plot area
		 */

		this.plotComposite = new Composite(this.tabFolder, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.plotComposite.setLayout(gridLayout);
		
		/* create a composite that contains the control elements */
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
		this.plotInfo.plotType=0;
		this.plotInfo.currentDisease=1;
		this.plotInfo.currentScen = 1;
		if (this.output.getNScen()==0) this.plotInfo.currentScen=0;

		
		
		
		
		
		
		
		
		final Scale scale = new Scale(controlComposite, SWT.VERTICAL);
		// scale.setBounds(0, 0, 40, 200);
		scale.setMaximum(this.output.getStepsInRun());
		scale.setMinimum(0);
		scale.setIncrement(1);
		scale.setPageIncrement(1);
		scale.setSelection(this.output.getStepsInRun());

		GridData data3 = new GridData(GridData.FILL_VERTICAL);
		data3.verticalSpan = 4;
		data3.grabExcessVerticalSpace = true;
		scale.setLayoutData(data3);
		
		
		
		final StyledText value = new StyledText(controlComposite, SWT.SINGLE | SWT.BOLD|SWT.LONG);
        value.setText("Year: "
						+  this.output.getStartYear());
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

	    
		
		// RowData rowData4 = new RowData(55, 25);
		// value.setLayoutData(rowData4);
		// RowData rowData2 = new RowData(40, 500);
		// scale.setLayoutData(rowData2);
		scale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int userValue = scale.getMaximum()
						- scale.getSelection() + scale.getMinimum();
				value.setText("Year: "
						+ (userValue + Output_PyramidTab.this.output.getStartYear()));
				value.setStyleRange(style1);
				Output_PyramidTab.this.plotInfo.currentYear = userValue;
				Output_PyramidTab.this.factory.drawChartAction(Output_PyramidTab.this.plotInfo, Output_PyramidTab.this.chartComposite );
				

			}

		});
		
		JFreeChart pyramidChart = this.output.makePyramidChartIncludingDisease(
				this.plotInfo.currentScen, this.plotInfo.currentYear, -1);
		
		// RowData rowData3 = new RowData(450, 500);
	    this.chartComposite = new ChartComposite(this.plotComposite, SWT.NONE,
				pyramidChart, true);
	    this.chartComposite.setDisplayToolTips(true);
	    this.chartComposite.setHorizontalAxisTrace(false);
	    this.chartComposite.setVerticalAxisTrace(false);
		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		this.chartComposite.setLayoutData(chartData);
		// chartComposite.setLayoutData(rowData3);
		new ScenarioChoiceGroup(controlComposite, this.chartComposite, this.factory, this.plotInfo, this.output.getScenarioNames());

		String[] items = new String[this.output.getNDiseases() + 2];
		String[] names = this.output.getDiseaseNames();
		items[0] = "none";
		items[1] = "all";
		for (int i = 0; i < names.length; i++)
			items[i + 2] = names[i];
		new DiseaseChoiceGroup(controlComposite, this.chartComposite,this. factory, this.plotInfo, items);

		TabItem item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("population Pyramid");
		item.setControl(this.plotComposite);
		
		
       
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