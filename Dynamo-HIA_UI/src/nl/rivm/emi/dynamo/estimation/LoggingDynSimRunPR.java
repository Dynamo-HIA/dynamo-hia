package nl.rivm.emi.dynamo.estimation;

import java.io.File;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.output.CDMOutputFactory;
import nl.rivm.emi.dynamo.output.CSVWriter;
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
	public void createOutput(CDMOutputFactory output,
			ScenarioParameters scenarioParameters, String currentpath) {
		/* creates output for DYNAMO-BATCH runner */
		CSVWriter writer= new CSVWriter(output, scenarioParameters);
		String fileName=currentpath+File.separator+"batchoutput";
		try {
			writer.writeBatchOutputCSV(fileName);
		} catch (FactoryConfigurationError e) {
			usedToBeErrorMessageWindow(e);
		
		} catch (XMLStreamException e) {
			usedToBeErrorMessageWindow(e);
			
		} catch (DynamoOutputException e) {
			usedToBeErrorMessageWindow(e);
			
		}
		
		
		
		
	}

	private void usedToBeErrorMessageWindow(FactoryConfigurationError e) {
		log.error("error while writing output."
				+ " Message given: " + e.getMessage()
				+ ". ");
    e.printStackTrace();
		
	}
}
