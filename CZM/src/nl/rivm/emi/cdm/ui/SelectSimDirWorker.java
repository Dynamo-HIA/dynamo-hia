package nl.rivm.emi.cdm.ui;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SelectSimDirWorker extends SwingWorker<File, String> {
	Log log = LogFactory.getLog(getClass().getSimpleName());

	String lastChosenSimulationDirectoryPropertiesFileName = "lastsimdir.ini";
	String lastChosenSimulationDirectoryPropertyKey = "lastsimdir";

	private ActionListener callbackWindow = null;
	File simulationDirectory = null;

	public SelectSimDirWorker(ActionListener callbackWindow) {
		super();
		log.info("Constructing.");
		this.callbackWindow = callbackWindow;
	}

	@Override
	public File doInBackground() {
		publish("doInBackground() called.");
		simulationDirectory = askSimulationDirectory();
		simulationDirectory = checkSimulationDirectory(simulationDirectory);
		return simulationDirectory;
	}

	private File askSimulationDirectory() {
		File simulationDirectory = getLastSimDir();
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (simulationDirectory != null) {
			fc.setCurrentDirectory(simulationDirectory);
		}
		int returnVal = fc
				.showOpenDialog((MainSwingGroupLayoutWindow) callbackWindow);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			simulationDirectory = fc.getSelectedFile();
			// This is where a real application would open the file.
			publish("Simulation directory chosen: "
					+ simulationDirectory.getName());
		} else {
			publish("No simulation directory chosen.");
		}
		if (simulationDirectory != null) {
			storeLastSimDir(simulationDirectory);
		}
		return simulationDirectory;
	}

	private File checkSimulationDirectory(File simulationDirectory) {
		if (simulationDirectory.isDirectory()) {

			DirectoryChecker initPopChecker = new DirectoryChecker();
			initPopChecker.setDebugLabel("InitPop");
			// Two files
			initPopChecker.setCorrectNumberOfFiles(2);
			// No subdirectories.
			Map<String, DirectoryChecker> initPopCorrectNames = new HashMap<String, DirectoryChecker>();
			initPopChecker.setExpectedSubDirectoryNames(initPopCorrectNames);

			DirectoryChecker characteristicsNameDirChecker = new DirectoryChecker();
			characteristicsNameDirChecker.setDebugLabel("CharName");
			// A Java file at least.
			// TODO Elaborate.
			characteristicsNameDirChecker.setCorrectNumberOfFiles(1);
			// A directory called "parameters" with unchecked content.
			Map<String, DirectoryChecker> expectedCharacteristicNameSubDirNames = new HashMap<String, DirectoryChecker>();
			expectedCharacteristicNameSubDirNames.put("Parameters", null);
			characteristicsNameDirChecker
					.setExpectedSubDirectoryNames(expectedCharacteristicNameSubDirNames);

			DirectoryChecker updateRulesChecker = new DirectoryChecker();
			updateRulesChecker.setDebugLabel("UpRules");
			// No files
			updateRulesChecker.setCorrectNumberOfFiles(0);
			// An unknown number of subdirectory. TODO Make a link with the
			// Characteristics configuration.
			Map<String, DirectoryChecker> anySubDirChecker = new HashMap<String, DirectoryChecker>();
			updateRulesChecker.setCheckExpectedSubDirectoryNames(false);

			DirectoryChecker simDirChecker = new DirectoryChecker();
			simDirChecker.setDebugLabel("SimDir");
			simDirChecker.setCorrectNumberOfFiles(1);
			Map<String, DirectoryChecker> correctNames = new HashMap<String, DirectoryChecker>();
			correctNames.put("Initial Population", initPopChecker);
			correctNames.put("Update Rules", updateRulesChecker);
			simDirChecker.setExpectedSubDirectoryNames(correctNames);
			simulationDirectory = simDirChecker
					.checkDirectoryContent(simulationDirectory);
		} else {
			publish("Software logic error: Simulation directory is no directory.");
		}
		return simulationDirectory;
	}

	@Override
	protected void process(List<String> chunks) {
		for (String chunk : chunks) {
			log.info("VIA PUBLISH: " + chunk);
		}
		super.process(chunks);
	}

	@Override
	public void done() {
		log.info("done() called.");
		((SelectSimDirInterface) callbackWindow)
				.finishSelectSimDir(simulationDirectory);
	}

	private File getLastSimDir() {
		File lastSimDir = null;
		try {
			File propFile = new File(
					lastChosenSimulationDirectoryPropertiesFileName);
			if (propFile.exists() && propFile.isFile() && propFile.canRead()) {
				FileInputStream propFileInputStream = new FileInputStream(
						propFile);
				Properties props = new Properties();
				props.load(propFileInputStream);
				String lastSimDirName = (String) props
						.get(lastChosenSimulationDirectoryPropertyKey);
				if (lastSimDirName != null) {
					lastSimDir = new File(lastSimDirName);
					if (lastSimDir.exists() && lastSimDir.isDirectory()
							&& lastSimDir.canRead()) {
						log
								.info("- Success - Simulation directory preloaded from properties");
					} else {
						lastSimDir = null;
						log
								.info("- Failure - Simulation directory could not be preloaded from properties");
						// propFile.delete();
					}
				} else {
					log
							.info("- Failure - Simulation directory not found in properties file.");
					// propFile.delete();

				}
			}
		} catch (FileNotFoundException e) {
			log.error("- Exception - " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("- Exception - " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			log.error("- Throwable - " + t.getClass().getSimpleName()
					+ " with message: " + t.getMessage());
			t.printStackTrace();
		}
		return lastSimDir;
	}

	private void storeLastSimDir(File lastSimDir) {
		try {
			File propFile = new File(
					lastChosenSimulationDirectoryPropertiesFileName);
			if (propFile.exists() && propFile.isFile()) {
				if (propFile.canRead() && propFile.canWrite()) {
					FileInputStream propFileInputStream = new FileInputStream(
							propFile);
					Properties props = new Properties();
					props.load(propFileInputStream);
					propFileInputStream.close();
					props.setProperty(
							lastChosenSimulationDirectoryPropertyKey,
							lastSimDir.getAbsolutePath());
					FileOutputStream propFileOutputStream = new FileOutputStream(
							propFile);
					props.store(propFileOutputStream, "Saved at: "
							+ Calendar.getInstance().getTime());
					propFileOutputStream.flush();
					propFileOutputStream.close();
					log
							.info("- Success - Simulation directory stored to properties.");
				} else {
					log
							.info("- Failure - Simulation directory could not be stored to existing properties file.");
				}
			} else {
				if (propFile.createNewFile()) {
					FileOutputStream propFileOutputStream = new FileOutputStream(
							propFile);
					Properties props = new Properties();
					props.store(propFileOutputStream, "Saved at: "
							+ Calendar.getInstance().getTime());
					propFileOutputStream.flush();
					propFileOutputStream.close();
					log
							.info("- Success - Simulation directory stored to new properties file.");
				} else {
					log
							.info("- Failure - Could not create new properties file.");
				}
			}
		} catch (FileNotFoundException e) {
			log.error("- Exception - " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("- Exception - " + e.getClass().getSimpleName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			log.error("- Throwable - " + t.getClass().getSimpleName()
					+ " with message: " + t.getMessage());
			t.printStackTrace();
		}
	}

	private class DirectoryChecker {

		String debugLabel = "";

		int correctNumberOfFiles = 0;

		Map<String, DirectoryChecker> expectedSubDirectoryNames = null;

		boolean checkExpectedSubDirectoryNames = true;

		private File checkDirectoryContent(File simulationDirectory) {
			publish(" " + debugLabel + " Checking directory: "
					+ simulationDirectory.getName());
			boolean errors = false;
			File[] directoryContent = simulationDirectory.listFiles();
			int numberOfCorrectlyNamedDirectories = 0;
			int numberOfFiles = 0;
			for (int count = 0; count < directoryContent.length; count++) {
				File contentItem = directoryContent[count];
				if (contentItem.isFile()) {
					numberOfFiles++;
				} else {
					String name = contentItem.getName();
					Set<String> correctNamesOnly = expectedSubDirectoryNames
							.keySet();
					for (String correctName : correctNamesOnly) {
						if (correctName.equalsIgnoreCase(name)) {
							numberOfCorrectlyNamedDirectories++;
							DirectoryChecker contentItemChecker = expectedSubDirectoryNames
									.get(correctName);
							if (contentItemChecker != null) {
								contentItem = contentItemChecker
										.checkDirectoryContent(contentItem);
								if (contentItem == null) {
									errors = true;
									break;
								}
							}
							break;
						}
					}
				}
			}
			if (!(numberOfFiles == correctNumberOfFiles)) {
				publish(" " + debugLabel + " Content of directory: "
						+ simulationDirectory.getName()
						+ " is not correct.\nThe number of files in it is: "
						+ numberOfFiles + " but should be: "
						+ correctNumberOfFiles);
				errors = true;
			}
			if (!(numberOfCorrectlyNamedDirectories == expectedSubDirectoryNames
					.size())) {
				publish(" " + debugLabel + " Content of directory: "
						+ simulationDirectory.getName() + " is not correct\n"
						+ "The number of correctly named directories is: "
						+ numberOfCorrectlyNamedDirectories
						+ " but should be: " + expectedSubDirectoryNames.size());
				errors = true;
			}
			if (errors) {
				simulationDirectory = null;
			}
			return simulationDirectory;
		}

		public void setCorrectNumberOfFiles(int correctNumberOfFiles) {
			this.correctNumberOfFiles = correctNumberOfFiles;
		}

		public void setExpectedSubDirectoryNames(
				Map<String, DirectoryChecker> correctNames) {
			this.expectedSubDirectoryNames = correctNames;
		}

		public void setCheckExpectedSubDirectoryNames(
				boolean checkExpectedSubDirectoryNames) {
			this.checkExpectedSubDirectoryNames = checkExpectedSubDirectoryNames;
		}

		public void setDebugLabel(String debugLabel) {
			this.debugLabel = debugLabel;
		}
	}
}
