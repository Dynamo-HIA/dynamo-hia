package nl.rivm.emi.dynamo.estimation;

import org.eclipse.swt.widgets.Shell;

/**
 * @author mondeelr<br/>
 *         Interface that separates the calculations from the
 *         "public relations".<br/>
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
			Exception e, String simulationFilePath);

	/**
	 * Gets the graphical shell.
	 * 
	 * @return A reference to the Shell Object, null if no graphical context is
	 *         present.
	 */
	public Shell getShell();
}