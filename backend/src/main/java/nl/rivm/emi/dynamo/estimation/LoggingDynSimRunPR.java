package nl.rivm.emi.dynamo.estimation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.CSVLEwriter;
import nl.rivm.emi.dynamo.output.CSVWriter;
import nl.rivm.emi.dynamo.global.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

public class LoggingDynSimRunPR implements DynSimRunPRInterface {
	Log log = LogFactory.getLog(this.getClass().getSimpleName());

	public LoggingDynSimRunPR() {
		super();
	}

	@Override
	public void communicateErrorMessage(DynamoSimulationRunnable dynSimRun,
			Throwable e, String simulationFilePath) {
		String cause = "";
		if (e.getCause() != null) {
			cause += dynSimRun.handleErrorMessage("", (Exception) e,
					simulationFilePath);
		}
		log.error("Errors during configuration of the model"
				+ " Message given: " + e.getMessage() + cause);
	}

	@Override
	public void usedToBeErrorMessageWindow(Exception e) {
		log.error("error while calculating output." + " Message given: "
				+ e.getMessage() + ". ");
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
	public void createOutput(CDMOutputFactory output,
			ScenarioParameters scenarioParameters, String currentpath) {
		/* creates output for DYNAMO-BATCH runner */
		log.debug("start writing CSV");
		CSVWriter writer = new CSVWriter(output, scenarioParameters);
		CSVLEwriter lewriter = new CSVLEwriter(output, scenarioParameters, null);
		lewriter.setSullivan(true);
		lewriter.setFilename(currentpath + File.separator + "sullivan.csv");
		try {
			lewriter.setWriter(new FileWriter(lewriter.getFilename()));
		} catch (IOException e1) {

			usedToBeErrorMessageWindow("file " + lewriter.getFilename()
					+ " can not be written. \nPlease make sure that"
					+ " this file is not in use by another program.");

			e1.printStackTrace();

		}
		String fileName = currentpath + File.separator + "batchoutput.csv";
		try {
			writer.writeBatchOutputCSV(fileName, true);
			lewriter.run();
			lewriter.setSullivan(false);
			lewriter.setFilename(currentpath + File.separator + "cohortLE.csv");
			try {
				lewriter.setWriter(new FileWriter(lewriter.getFilename()));
			} catch (IOException e1) {

				usedToBeErrorMessageWindow("file " + lewriter.getFilename()
						+ " can not be written. \nPlease make sure that"
						+ " this file is not in use by another program.");

				e1.printStackTrace();

			}
			lewriter.run();
		} catch (FactoryConfigurationError e) {
			usedToBeErrorMessageWindow(e);

		} catch (DynamoOutputException e) {
			usedToBeErrorMessageWindow(e);

		}

	}

	private void usedToBeErrorMessageWindow(FactoryConfigurationError e) {
		log.error("error while writing output." + " Message given: "
				+ e.getMessage() + ". ");
		e.printStackTrace();

	}

	@Override
	public void updateProgressIndicator() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		//NOTHING

	}

	
	@Override
	public void dispatchProgressBar() {
	}

	@Override
	public Display getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProgressIndicatorInterface createProgressIndicator(String message,
			Boolean indeterminate) {
		ProgressIndicatorInterface instance = new ProgressLogger(message);
		return instance;
	}

}
