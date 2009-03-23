/**
 * 
 */
package nl.rivm.emi.dynamo.output;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author  boshuizh
 */
public class Output_LifeExpTab  {
	
	
	private TabFolder tabFolder;
	private DynamoOutputFactory output;
	private ButtonStates plotInfo;
	private LifeExpectancyChartFactory factory;
	private Composite plotComposite;

	public Output_LifeExpTab(TabFolder tabfolder , DynamoOutputFactory output) {
	this.tabFolder=tabfolder;
	this.output=output;
	this.factory=new LifeExpectancyChartFactory(output);
	
	
	
	makeIt();
	}
	
	public void makeIt(){
		/* put the default plot information in the object plotInfo */
		this.plotComposite = new Composite(tabFolder, SWT.FILL);
		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		plotComposite.setLayout(gridLayout);
		
		
		plotComposite.setLayout(gridLayout);
		plotInfo=new ButtonStates();
		plotInfo.currentScen = 0;
		plotInfo.currentDisease = 1;
		plotInfo.currentYear = 0;

		Composite controlComposite = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);
		
		final ChartComposite chartComposite = new ChartComposite(
				plotComposite, SWT.NONE, output.makeLifeExpectancyPlot(1), true);
		
		 GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
					| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL);
		
		chartComposite.setLayoutData(chartData);
		String[] items = new String[output.getNDiseases() + 2];
		String[] names = output.getDiseaseNames();
		items[0] = "none";
		items[1] = "all";
		for (int i = 0; i < names.length; i++)
			items[i + 2] = names[i];

		
		new DiseaseChoiceGroup(controlComposite, chartComposite, factory, plotInfo, items);
		
		
		final int minA = output.getMinAgeInSimulation();
		int length = output.getMaxAgeInSimulation()
				- output.getMinAgeInSimulation() + 1;
		String[] ageNames = new String[length];
		if (minA == 0)
			ageNames[0] = "at birth";
		else
			ageNames[0] = ((Integer) minA).toString();
		for (int i = 1; i < length; i++)
			ageNames[i] = ((Integer) (minA + i)).toString();
		new AgeChoiceGroup(controlComposite, chartComposite, factory, plotInfo, ageNames);
		// chartComposite4.setBounds(0, 0, 400, 500);
		TabItem item4 = new TabItem(tabFolder, SWT.NONE);
		item4.setText("life expectancy plots");
		item4.setControl(plotComposite);
       
	}
	public void redraw(){
		Control[] subcomp= plotComposite.getChildren();
		factory.drawChartAction(plotInfo, (ChartComposite) subcomp[1]);
		plotComposite.redraw();
	}
		}