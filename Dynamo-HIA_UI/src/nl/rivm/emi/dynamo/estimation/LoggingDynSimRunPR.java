package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingDynSimRunPR implements DynSimRunPRInterface {
Log log = LogFactory.getLog(this.getClass().getSimpleName());

	public LoggingDynSimRunPR() {
		super();
	}

	@Override
	public void communicateErrorMessage(DynamoSimulationRunnable dynSimRun, Exception e, String simulationFilePath) {
		String cause = "";
		if (e.getCause() != null) {
			cause += dynSimRun.handleErrorMessage("", e, simulationFilePath);
		}
		log.error("Errors during configuration of the model"
				+ " Message given: " + e.getMessage() + cause);
	}

	@Override
	public void usedToBeErrorMessageWindow(Exception e) {
		log.error("error while calculating output."
						+ " Message given: " + e.getMessage()
						+ ". ");
		e.printStackTrace();
	}

	@Override
	public void usedToBeErrorMessageWindow(String string) {
		log.error(string);
	}

	@Override
	public ProgressIndicatorInterface createProgressIndicator(String message) {
		ProgressIndicatorInterface instance = new ProgressLogger(message); 
		return instance;
	}

	@Override
	public void createOutput_UI(CDMOutputFactory output,
			ScenarioParameters scenarioParameters, String currentpath) {
		// TODO Auto-generated method stub
		
	}
}
