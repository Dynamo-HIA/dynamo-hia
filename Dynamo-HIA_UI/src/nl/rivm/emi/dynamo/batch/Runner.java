package nl.rivm.emi.dynamo.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Runner {
	static private Log statLog = LogFactory
			.getLog("nl.rivm.emi.dynamo.batch.Runner");
	static final String defaultBatchFileName = "simulationbatch.lst";

	static private Runner runner = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder("Started, arguments: ");
		for (String arg : args) {
			sb.append(arg + ", ");
		}
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
			FileReader reader = new FileReader(batchFile);
			BufferedReader bufferedReader = new BufferedReader(reader);
			if (bufferedReader.ready()) {
				String simulationName = null;
				while ((simulationName = bufferedReader.readLine()) != null) {
					String batchFilePath = batchFile.getAbsolutePath();
					String baseDirectoryPath = batchFilePath.substring(0,
							batchFilePath.lastIndexOf(File.separatorChar)+1);
					String simulationConfigurationPath = baseDirectoryPath
							+ StandardTreeNodeLabelsEnum.SIMULATIONS
									.getNodeLabel()
							+ File.separator
							+ simulationName
							+ File.separator
							+ StandardTreeNodeLabelsEnum.CONFIGURATIONFILE
									.getNodeLabel() + ".xml";
					statLog.debug("SimulationConfigurationPath: "
							+ simulationConfigurationPath);
					// @@@@@@
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
			e.printStackTrace();
		}
	}
}
