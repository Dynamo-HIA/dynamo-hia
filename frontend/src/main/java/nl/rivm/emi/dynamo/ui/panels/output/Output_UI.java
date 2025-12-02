/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import java.io.File;

import nl.rivm.emi.dynamo.global.ScenarioParameters;
import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author boshuizh
 * 
 */
public class Output_UI {

	final Shell parentShell;
	final Shell outputShell;
	Log log = LogFactory.getLog(this.getClass().getName());
	CDMOutputFactory output;

	// Contains the base directory of the application data
	private String currentPath;
//	private String outputPath;
	
	private DynamoPlotFactory plotFactory;
	private ScenarioParameters scenarioParameters;

	// public Output_UI(Shell shell, ScenarioInfo scen, String simName,
	// Population[] pop, String baseDir) {
	public Output_UI(Shell shell, CDMOutputFactory output,ScenarioParameters scenarioParameters, String currentPath
			) {
        this.currentPath=currentPath;
        /* nb you need a double escape sequence here to make this work 
         * TO be tested for other platforms as Windows */
        String delim="[\\"+File.separator+"]";
        String [] tokens = currentPath.split(delim);
     //   for (int i=0;i<tokens.length-1;i++){}
		this.parentShell = shell;
		outputShell = new Shell(parentShell,SWT.MAX | SWT.RESIZE |SWT.MIN | SWT.CLOSE);
		outputShell.setText("Dynamo Output for simulation:  "+tokens[tokens.length-2]);
		outputShell.setBounds(30, 30, 1090, 950);
		shell.setLayout(new FillLayout());
        this.scenarioParameters=scenarioParameters;
		this.output = output;
        log.info("start making plotFactory");
		this.plotFactory=new DynamoPlotFactory(output,scenarioParameters);
        makeOutputDisplay(outputShell);
		

	}

	/**
	 * make the output window containing 6 tabfolders
	 * 
	 * @param shell
	 *            : the shell under which the output window should be placed
	 * 
	 */
	public void makeOutputDisplay(Shell shell) {

		/* tab for pyramid plots */
		log.debug("start making tabfolder");
		TabFolder tabFolder1 = new TabFolder(shell, SWT.FILL);
		FillLayout layout = new FillLayout();
		//layout.marginHeight=5;
		//layout.marginWidth=5;
		tabFolder1.setLayout(layout);
		
		
		
       // tabFolder1.setLayout(new GridLayout());
       // tabFolder1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		
    	tabFolder1.setBounds(10, 10, 1050, 860); // this is needed to show something or pack works also

		/* tab for changing the parameters of the scenarios */

		final Output_PyramidTab tab0 = new Output_PyramidTab(tabFolder1, this.plotFactory);
		log.debug("tab pyramid made");
		
		final Output_RiskFactorTab tab2 = new Output_RiskFactorTab(tabFolder1,
				this.plotFactory);
		log.debug("tab risk factor made");
		final Output_DiseaseTab tab1 = new Output_DiseaseTab(tabFolder1, this.plotFactory);
		log.debug("tab diseases made");
		final Output_IncidenceTab tab7 = new Output_IncidenceTab(tabFolder1, this.plotFactory);
		log.debug("tab incidences made");
		final Output_SurvivalTab tab4 = new Output_SurvivalTab(tabFolder1,
				this.plotFactory);
		log.debug("tab survival made");
		final Output_LifeExpTab tab3 = new Output_LifeExpTab(tabFolder1, this.plotFactory);
		final Output_WriteOutputTab tab5 = new Output_WriteOutputTab(
				outputShell, currentPath, tabFolder1, this.output, scenarioParameters);
		log.debug("tab writing made");
		final Output_ChangeScenarioTab tab6 = new Output_ChangeScenarioTab(
				tabFolder1, this.output,this.scenarioParameters);
		log.debug("tab scenarioparams made");
				
		shell.open();
		tabFolder1.addListener(SWT.Selection, new Listener() {
			/**
			 * transfer of scenario parameters is not necessary, tried to see if this solves the refresh problem
			 */
			public void handleEvent(Event event) {
				TabItem item = (TabItem) event.item;
				String tabId = item.getText();
				if (tabId == "Population Pyramid")
					tab0.redraw();
				if (tabId == "Prevalence")
					tab1.redraw();
				if (tabId == "Incidence")
					tab7.redraw();
				if (tabId == "Risk factor")
					tab2.redraw();
				if (tabId == "Life expectancy")
					tab3.redraw();
				if (tabId == "Mortality/Survival")
					tab4.redraw();
			}

		});
		
	}
}
