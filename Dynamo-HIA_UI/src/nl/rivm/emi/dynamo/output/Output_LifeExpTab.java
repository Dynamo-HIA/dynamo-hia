/**
 * 
 */
package nl.rivm.emi.dynamo.output;

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
public class Output_LifeExpTab  {
	
	
	private TabFolder tabFolder;
	private DynamoOutputFactory output;
	private ButtonStates plotInfo;
	private LifeExpectancyChartFactory factory;
	private Composite plotComposite;

	/**makes the folder for the life expectancy plots
	 * @param tabfolder
	 * @param output
	 */
	public Output_LifeExpTab(final TabFolder tabfolder , final DynamoOutputFactory output) {
	this.tabFolder=tabfolder;
	this.output=output;
	this.factory=new LifeExpectancyChartFactory(output);
	
	
	
	makeIt();
	}
	
	/**
	 * makes the tabfolder
	 */
	public void makeIt(){
		/* put the default plot information in the object plotInfo */
		this.plotComposite = new Composite(this.tabFolder, SWT.FILL);
		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		this.plotComposite.setLayout(gridLayout);
		
		
		this.plotComposite.setLayout(gridLayout);
		this.plotInfo=new ButtonStates();
		this.plotInfo.currentScen = 0;
		this.plotInfo.currentDisease = 1;
		this.plotInfo.currentYear = 0;

		Composite controlComposite = new Composite(this.plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);
		
		final ChartComposite chartComposite = new ChartComposite(
				this.plotComposite, SWT.NONE, this.output.makeLifeExpectancyPlot(0), true);
		
		 GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
					| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL);
		
		chartComposite.setLayoutData(chartData);
		String[] items = new String[this.output.getNDiseases() + 2];
		String[] names = this.output.getDiseaseNames();
		items[0] = "none";
		items[1] = "all";
		for (int i = 0; i < names.length; i++)
			items[i + 2] = names[i];

		
		new DiseaseChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo, items);
		
		
		final int minA = Math.max(0,this.output.getMinAgeInSimulation());
		int length = this.output.getMaxAgeInSimulation()
				- minA + 1;
		String[] ageNames = new String[length];
		if (minA == 0)
			ageNames[0] = "at birth";
		else
			ageNames[0] = ((Integer) minA).toString();
		for (int i = 1; i < length; i++)
			ageNames[i] = ((Integer) (minA + i)).toString();
		new AgeChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo, ageNames);
		// chartComposite4.setBounds(0, 0, 400, 500);
		TabItem item4 = new TabItem(this.tabFolder, SWT.NONE);
		item4.setText("life expectancy plots");
		item4.setControl(this.plotComposite);
       
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