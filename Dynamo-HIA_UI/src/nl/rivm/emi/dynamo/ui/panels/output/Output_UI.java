/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import java.io.File;

import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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
        
		this.parentShell = shell;
		outputShell = new Shell(parentShell);
		outputShell.setText("Dynamo Output for simulation:  "+tokens[tokens.length-2]);
		outputShell.setBounds(30, 30, 750, 650);
        this.scenarioParameters=scenarioParameters;
		this.output = output;
        log.fatal("start making plotFactory");
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
		log.fatal("start making tabfolder");
		TabFolder tabFolder1 = new TabFolder(shell, SWT.FILL);

		tabFolder1.setLayout(new FillLayout());
		tabFolder1.setBounds(10, 10, 730, 580);

		/* tab for changing the parameters of the scenarios */

		final Output_PyramidTab tab0 = new Output_PyramidTab(tabFolder1, this.plotFactory);
		log.fatal("tab pyramid made");
		final Output_DiseaseTab tab1 = new Output_DiseaseTab(tabFolder1, this.plotFactory);
		log.fatal("tab diseases made");
		final Output_RiskFactorTab tab2 = new Output_RiskFactorTab(tabFolder1,
				this.plotFactory);
		log.fatal("tab risk factor made");
		final Output_LifeExpTab tab3 = new Output_LifeExpTab(tabFolder1, this.plotFactory);
		final Output_SurvivalTab tab4 = new Output_SurvivalTab(tabFolder1,
				this.plotFactory);
		log.fatal("tab survival made");
		final Output_WriteOutputTab tab5 = new Output_WriteOutputTab(
				outputShell, currentPath, tabFolder1, this.output, scenarioParameters);
		log.fatal("tab writing made");
		final Output_ChangeScenarioTab tab6 = new Output_ChangeScenarioTab(
				tabFolder1, this.output,this.scenarioParameters);
		log.fatal("tab scenarioparams made");

		shell.open();
		tabFolder1.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				TabItem item = (TabItem) event.item;
				String tabId = item.getText();
				if (tabId == "population Pyramid")
					tab0.redraw();
				if (tabId == "disease plots")
					tab1.redraw();
				if (tabId == "riskfactor plots")
					tab2.redraw();
				if (tabId == "life expectancy plots")
					tab3.redraw();
				if (tabId == "mortality/survival plots")
					tab4.redraw();
			}

		});
		// Free the memory in the very large output-object for next run ;
		// this.output=null;

		/*
		 * tabFolder1.addSelectionListener(new SelectionListener() {
		 * 
		 * public void widgetDefaultSelected(SelectionEvent e) {
		 * 
		 * 
		 * 
		 * 
		 * }
		 * 
		 * public void widgetSelected(SelectionEvent arg0) {
		 * 
		 * }
		 * 
		 * });
		 */

	}
}
