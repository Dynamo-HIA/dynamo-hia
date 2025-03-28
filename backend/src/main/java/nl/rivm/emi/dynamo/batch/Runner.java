package nl.rivm.emi.dynamo.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.estimation.DynSimRunPRInterface;
import nl.rivm.emi.dynamo.estimation.DynamoSimulationRunnable;
import nl.rivm.emi.dynamo.estimation.LoggingDynSimRunPR;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.SchemaFileProviderInitializer;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Runner {
	static private Log statLog = LogFactory
			.getLog("nl.rivm.emi.dynamo.batch.Runner");
	static final String defaultBatchFileName = "simulationbatch.lst";

	static private Runner runner = null;

	public void run() {
		Runner.main(new String[] { "arg", "arg" });
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SchemaFileProviderInitializer.initialize(null);
       
		StringBuilder sb = new StringBuilder("Started, arguments: ");
		for (String arg : args) {
			sb.append(arg + ", ");
		}
		System.out.print(sb.toString());
		System.out.flush();
		String resultString = sb.toString();
		statLog.debug(resultString.substring(0, resultString.length() - 2));
		String usedBatchFileName = defaultBatchFileName;
		if (args.length > 0) {
			usedBatchFileName = args[0];
		}
		statLog.debug("Using batchfilename: " + usedBatchFileName);
		File batchFile = new File(usedBatchFileName);
		if (batchFile.exists()) {
			if (batchFile.isFile()) {
				if (batchFile.canRead()) {
					runner = new Runner();
					runner.handleFile(batchFile);
				} else {
					statLog.warn("Cannot read " + batchFile.getAbsolutePath());
				}
			} else {
				statLog.warn(batchFile.getAbsolutePath() + " is not a file.");
			}
		} else {
			statLog.warn(batchFile.getAbsolutePath() + " does not exist.");
		}
	}

	private void handleFile(File batchFile) {
		try {
			String batchFilePath = batchFile.getAbsolutePath();
			// veranderd in april 2014 omdat niet werkt met een basedirpath eindigend op een file separator. 
			// dat is anders dan verwacht bi de ui
			//was:
		//	String baseDirectoryPath = batchFilePath.substring(0, batchFilePath
		//			.lastIndexOf(File.separatorChar) + 1);
			String baseDirectoryPath = batchFilePath.substring(0, batchFilePath
					.lastIndexOf(File.separatorChar) );
			BaseDirectory.getInstance(baseDirectoryPath);
			DynSimRunPRInterface dsi = new LoggingDynSimRunPR();
			FileReader reader = new FileReader(batchFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			if (bufferedReader.ready()) {
				String simulationName = null;
				while ((simulationName = bufferedReader.readLine()) != null) 
				if (!simulationName.equals("")){
					String simulationConfigurationPath = baseDirectoryPath + File.separator
							+ StandardTreeNodeLabelsEnum.SIMULATIONS
									.getNodeLabel()
							+ File.separator
							+ simulationName
							+ File.separator
							+ StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
									.getNodeLabel() + ".xml";
					statLog
							.debug("Going to run simulation at configuration path: "
									+ simulationConfigurationPath);
					boolean exceptionless = runSimulation(dsi, simulationName,
							baseDirectoryPath);
					if (!exceptionless) {
						statLog.fatal("Simulation at configuration path: "
								+ simulationConfigurationPath
								+ " had internal trouble.");
					}
				}
			} else {
				statLog
						.warn("The bufferedReader is not ready. File may be empty.");
			}
		} catch (FileNotFoundException e) {
			statLog.fatal(e.getClass().getSimpleName()
					+ " should not happen, because this has been tested for.");
			e.printStackTrace();
		} catch (IOException e) {
			statLog.fatal(e.getClass().getSimpleName() + " " + e.getMessage());
			e.printStackTrace(System.err);
			System.err.flush();
		}
	}

	private boolean runSimulation(DynSimRunPRInterface dsi,
			String simulationName, String baseDirectoryPath) {
		try {
			DynamoSimulationRunnable R = new DynamoSimulationRunnable(dsi,
					simulationName, baseDirectoryPath);
			R.run();
			return true;
		} catch (DynamoInconsistentDataException e) {
			statLog.info(e.getMessage());
			e.printStackTrace(System.err);
			System.err.flush();
			return false;
		}
	}
}
