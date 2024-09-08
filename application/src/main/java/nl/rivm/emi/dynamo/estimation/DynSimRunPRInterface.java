package nl.rivm.emi.dynamo.estimation;

import org.eclipse.swt.widgets.Display;

import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.global.ScenarioParameters;

/**
 * @author mondeelr<br/>
 *         Interface that separates the calculations from the
 *         "public relations = the user interface".<br/>
 *         In the initial use-case the "public relations" are delivered through
 *         the RCP user interface, using graphical widgets.<br/>
 *         In the use-case for the batch-runner there will be no graphical
 *         interface, so PR must be handled via logging or the console.
 */
public interface DynSimRunPRInterface {

	/**
	 * @param message
	 * @return
	 */
	public ProgressIndicatorInterface createProgressIndicator(String message);

	
	public ProgressIndicatorInterface createProgressIndicator(String message, Boolean indeterminate);
	
	
	public void updateProgressIndicator();


	/**
	 * @param e
	 */
	public void usedToBeErrorMessageWindow(Exception e);

	/**
	 * @param e
	 */
	public void usedToBeErrorMessageWindow(String message);

	/**
	 * @param e
	 * @param simulationFilePath
	 */
	public void communicateErrorMessage(DynamoSimulationRunnable dynSimRun,
			Throwable e, String simulationFilePath);

	/**
	 * Create the output screen for graphical version or output on file for the batch version. 
	
	 * @param output
	 * @param scenarioParameters
	 * @param currentpath
	 */
	public void createOutput( CDMOutputFactory output,ScenarioParameters scenarioParameters, String currentpath);


	/**
	 * update progress bar
	 */
	public void update();

	public Display getDisplay();

	public void dispatchProgressBar();

}
