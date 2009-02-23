/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

/**
 * @author boshuizh
 *
 */
public class Output_UI {
	
	
	Scale scale;
	Text value;
	JFreeChart pyramidChart; 
	int plottedScen;
	ChartComposite chartComposite;
	int stepsInRun;
	DynamoOutputFactory output;
	public Output_UI(ScenarioInfo scen, String simName, Population[] pop){
		
		
		output = new DynamoOutputFactory(scen,simName);
		stepsInRun=output.getStepsInRun();
		try {
			output.extractArraysFromPopulations(pop);
			output.makeArraysWithNumbers();
			output.makeLifeExpectancyPlot(0);
			makePopulationPyramidPlot();
			
			output.makePrevalencePlots(0);
			output.makePrevalencePlots(1);
			
			output.makeRiskFactorPlots(0); 
			if (scen.getRiskType()==2) output.makeMeanPlots(0);
			output.makePrevalenceByRiskFactorPlots(0); 
			output.makePrevalenceByRiskFactorPlots(1); 
			JFreeChart chart=output.makeSurvivalPlot(0);
			chart=output.makeSurvivalPlot(1);
			chart=output.makeSurvivalPlot(2);
			output.makeLifeExpectancyPlot(0);	
		} catch (DynamoScenarioException e) {
			
			
			

			
			
			displayErrorMessage(e);


			
			e.printStackTrace();
		} catch (DynamoOutputException e) {
			displayErrorMessage(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}




	/**
	 * @param e
	 */
	private void displayErrorMessage(Exception e) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		MessageBox messageBox = 
			  new MessageBox(shell, SWT.OK);
		messageBox.setMessage("error while calculating output." +
				" Message given: "+e.getMessage()+
				". Program will close.");
			if (messageBox.open() == SWT.OK)
			{
			  shell.dispose();
			}

		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	
	
	
	public void makePopulationPyramidPlot() {


		Display display = new Display();
		Shell shell = new Shell(display);
		RowLayout rowLayout = new RowLayout();
		shell.setSize(600, 600);
		shell.setLayout(rowLayout);
		shell.setText("Population PyramidPlot");
		shell.setLayout(rowLayout);
		
		plottedScen=1;
		int timestep=0;
		
		    scale = new Scale(shell, SWT.VERTICAL);
		    scale.setBounds(0, 0, 40, 200);
		    scale.setMaximum(stepsInRun);
		    scale.setMinimum(0);
		    scale.setIncrement(1);
		    scale.setPageIncrement(1);
		    scale.setSelection(0);
		    RowData rowData2 = new RowData(40,500); 
		    scale.setLayoutData(rowData2);
		    scale.addListener(SWT.Selection, new Listener() {
		        public void handleEvent(Event event) {
		          int perspectiveValue = scale.getMaximum() - scale.getSelection() + scale.getMinimum();
		          value.setText("Year: " + (perspectiveValue+output.getStartYear()));
		          JFreeChart pyramidChart = output.makePyramidChart(plottedScen, perspectiveValue);
		          chartComposite.setChart(pyramidChart); 
		          chartComposite.redraw();
		          chartComposite.forceRedraw();
		          
		           }

		        
		        
		      }); 
		    value = new Text(shell, SWT.BORDER | SWT.SINGLE);

		    value.setEditable(false);
		    RowData rowData4 = new RowData(55,25); 
		    value.setLayoutData(rowData4);
		    JFreeChart pyramidChart = output.makePyramidChart(plottedScen, timestep);
			RowData rowData3 = new RowData(450,500); 
			chartComposite = new ChartComposite(shell, SWT.NONE,
					pyramidChart, true);
			chartComposite.setDisplayToolTips(true);
			chartComposite.setHorizontalAxisTrace(false);
			chartComposite.setVerticalAxisTrace(false);
			chartComposite.setLayoutData(rowData3);
			
		    shell.open();
		
		// ChartComposite.forceRedraw() to redraw

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

		// frame.setVisible(true);
		// frame.setSize(200, 200);
		// frame.pack();

		

	}


}
