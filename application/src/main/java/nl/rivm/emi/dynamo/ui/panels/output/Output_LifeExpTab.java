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
 * @author  boshuizh
 */
public class Output_LifeExpTab  {
	
	
	private TabFolder tabFolder;
	private CDMOutputFactory output;
	/* plotGenerator is the general object that contains the data and makes all possible plots */

	private DynamoPlotFactory plotGenerator;
	private ButtonStates plotInfo;
	/* factory is the object that is used by the listener to make plots based on the
	 * information in the button states 
	 */
	private LifeExpectancyChartFactory factory;
	private Composite plotComposite;

	/**makes the folder for the life expectancy plots
	 * @param tabfolder
	 * @param plotFactory
	 */
	public Output_LifeExpTab(final TabFolder tabfolder , final DynamoPlotFactory dfact) {
	this.tabFolder=tabfolder;
	this.output=dfact.output;
	this.plotGenerator=dfact;
	this.factory=new LifeExpectancyChartFactory(this.plotGenerator);
	
	
	
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
/* standard starting is total disease */		
		this.plotInfo.currentDisease = 2;
		if (this.output.getNDiseases()==0) this.plotInfo.currentDisease = 1;
		this.plotInfo.currentYear = 0;
		this.plotInfo.newborns=this.output.isWithNewborns();

		Composite controlComposite = new Composite(this.plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		
		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);
		
		final ChartComposite chartComposite = new ChartComposite(
				this.plotComposite, SWT.NONE, this.plotGenerator.makeCohortHealthyLifeExpectancyPlot(this.plotInfo.currentAge,this.plotInfo.currentDisease-3,this.plotInfo.blackAndWhite, this.plotInfo.cumulative), true);
		
		 GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
					| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL);
		
		chartComposite.setLayoutData(chartData);
		// chartComposite4.setBounds(0, 0, 400, 500);
		
		
		
		
		/* make disease choice group */
		String[] items =new String[2];		
		if (this.output.getNDiseases()>0){ items = new String[this.output.getNDiseases() + 3];
		items[2] = "total disease";
		String[] names = this.output.getDiseaseNames();
		for (int i = 0; i < names.length; i++)
			items[i + 3] = names[i];}
		items[0] = "none";
		items[1] = "disability";
		new DiseaseChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo, items);
		
		/* make Sullivan/cohort choice group */
		new SullivanChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo);
		
		
		/* make age choice group */
		final int minA = Math.max(0,this.output.getMinAgeInSimulation());
		plotInfo.maxAge=this.output.getMaxAgeInSimulation();
		plotInfo.currentAge=minA;
		int length = this.output.getMaxAgeInSimulation()
				- minA + 1;
		String[] ageNames = new String[length];
		if (minA == 0)
			ageNames[0] = "at birth";
		else
			ageNames[0] = ((Integer) minA).toString();
		for (int i = 1; i < length; i++)
			ageNames[i] = ((Integer) (minA + i)).toString();
		plotInfo.minAge=minA;		
		new AgeChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo, ageNames);
		
		
		/* make year choice group */		
		int start=output.getStartYear();
		int maxyears=output.getStepsInRun();
		if (!this.output.isWithNewborns()) maxyears=Math.min(output.getStepsInRun(),95); 
		String[] yearNames=new String [maxyears];
		for (int y=0;y<maxyears;y++)
			yearNames[y] = ((Integer) (y+start)).toString();		
		new YearChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo, yearNames);
		
		
		/* make cumulative choice group */		
		if  (output.getNScen()>0) new CumulativeChoiceGroup(controlComposite, chartComposite, this.factory, this.plotInfo);
		
		
		/* make color choice group */
	    new ColorChoiceGroup(controlComposite, chartComposite, this.factory,this.plotInfo);
			
	    
	    
	    
		TabItem item4 = new TabItem(this.tabFolder, SWT.NONE);
		// cahnge dd 22/11/ 2011 item4.setText("life expectancy plots");
		item4.setText("Life expectancy");
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