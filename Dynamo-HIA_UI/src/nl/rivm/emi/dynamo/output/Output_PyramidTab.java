/**
 * 
 */
package nl.rivm.emi.dynamo.output;





import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author  boshuizh
 */
public class Output_PyramidTab  {
	
	
	private TabFolder tabFolder;
	private DynamoOutputFactory output;
	private ButtonStates plotInfo;
	private PyramidChartFactory factory;
	ChartComposite chartComposite;
	private Composite plotComposite;

	public Output_PyramidTab(TabFolder tabfolder , DynamoOutputFactory output) {
	this.tabFolder=tabfolder;
	this.output=output;
	this.factory=new PyramidChartFactory(output);
    
	
	makeIt();
	}
	
	public void makeIt(){
		/* put the default plot information in the object plotInfo */
		
		this.plotComposite = new Composite(tabFolder, SWT.FILL);
		
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

		
		this.plotInfo=new ButtonStates();
		plotInfo.currentYear=0;
		plotInfo.plotType=0;
		plotInfo.currentDisease=1;
		plotInfo.currentScen = 1;
		if (output.getNScen()==0) plotInfo.currentScen=0;

		
		
		
		
		
		
		/*
		 * the composite has two elements: - a column with control elements
		 * where the user can make choices - a plot area
		 */

		
		final Scale scale = new Scale(controlComposite, SWT.VERTICAL);
		// scale.setBounds(0, 0, 40, 200);
		scale.setMaximum(output.getStepsInRun());
		scale.setMinimum(0);
		scale.setIncrement(1);
		scale.setPageIncrement(1);
		scale.setSelection(output.getStepsInRun());

		GridData data3 = new GridData(GridData.FILL_VERTICAL);
		data3.verticalSpan = 4;
		data3.grabExcessVerticalSpace = true;
		scale.setLayoutData(data3);
		
		
		
		final StyledText value = new StyledText(controlComposite, SWT.SINGLE | SWT.BOLD|SWT.LONG);
        value.setText("Year: "
						+  output.getStartYear());
		value.setEditable(false);
		final StyleRange style1 = new StyleRange();
	    style1.start = 0;
	    style1.length = 10;
	     
	    
	    style1.fontStyle = SWT.BOLD;
	    style1. background = tabFolder.getBackground();
	    ;
	    Font font = new Font(tabFolder.getParent().getDisplay(), "SansSerif", 13, SWT.BOLD);
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
						+ (userValue + output.getStartYear()));
				value.setStyleRange(style1);
				plotInfo.currentYear = userValue;
				factory.drawChartAction(plotInfo, chartComposite );
				

			}

		});
		
		JFreeChart pyramidChart = output.makePyramidChartIncludingDisease(
				plotInfo.currentScen, plotInfo.currentYear, -1);
		
		// RowData rowData3 = new RowData(450, 500);
	    chartComposite = new ChartComposite(this.plotComposite, SWT.NONE,
				pyramidChart, true);
		chartComposite.setDisplayToolTips(true);
		chartComposite.setHorizontalAxisTrace(false);
		chartComposite.setVerticalAxisTrace(false);
		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite.setLayoutData(chartData);
		// chartComposite.setLayoutData(rowData3);
		new ScenarioChoiceGroup(controlComposite, chartComposite, factory, plotInfo, output.getScenarioNames());

		String[] items = new String[output.getNDiseases() + 2];
		String[] names = output.getDiseaseNames();
		items[0] = "none";
		items[1] = "all";
		for (int i = 0; i < names.length; i++)
			items[i + 2] = names[i];
		new DiseaseChoiceGroup(controlComposite, chartComposite, factory, plotInfo, items);

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("population Pyramid");
		item.setControl(this.plotComposite);
		
		
       
	}
	public void redraw(){
		
		Control[] subcomp= plotComposite.getChildren();
		factory.drawChartAction(plotInfo, (ChartComposite) subcomp[1]);
		plotComposite.redraw();
		
	}
		}