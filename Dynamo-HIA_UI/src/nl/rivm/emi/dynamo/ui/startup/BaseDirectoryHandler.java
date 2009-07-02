package nl.rivm.emi.dynamo.ui.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class BaseDirectoryHandler {
	Log log = LogFactory.getLog(this.getClass().getName());

	private static final String TREE_BASE = "tree.base";
	private static String defaultDataPath;
	private static final String iniFilePath = System.getProperty("user.dir")
			+ File.separator + "ini" + File.separator + "lastopened.ini";

	Display display;

	public BaseDirectoryHandler(Display display) {
		super();
		this.display = display;
		String userDir = System.getProperty("user.dir");
		String userDirOneUp = userDir.substring(0, userDir
				.lastIndexOf(File.separator));
		String userDirTwoUp = userDirOneUp.substring(0, userDirOneUp
				.lastIndexOf(File.separator));
		defaultDataPath = userDirTwoUp + File.separator
				+ ApplicationStatics.APPBASENAME + File.separator
				+ ApplicationStatics.DEFAULTWORKDATADIRECTORY;
		log.debug("Constructed defaultWorkDirectoryPath: " + defaultDataPath);
	}

	/**
	 * The method tries to read the persisted workDirectory-location, provides a
	 * default if it doesn't succeed. Then it allows the user to select a
	 * workDirectory, create a new one or quit altogether.
	 * 
	 * 
	 * @return the workDirectoryPath, null when the user wants to exit.
	 * 
	 * @throws ConfigurationException
	 */
	public String provideBaseDirectory() throws ConfigurationException {
		Boolean workDirectoryOK = false;
		boolean tryAgain = true;
		String workDirectoryPath = readDataDirectory();
		log.debug("Read workDirectoryPath: " + workDirectoryPath);
		do {
			workDirectoryOK = false;
			workDirectoryPath = selectBaseDirectory(workDirectoryPath);
			log.debug("Selected workDirectoryPath: " + workDirectoryPath);
			File workDirectory = new File(workDirectoryPath);
			String[] expectedDirectoryNames = workDirectory
					.list(new ValidDataDirectoryFilter(workDirectory));
			if (expectedDirectoryNames != null) {
				log.debug("ExpectedLength: " + expectedDirectoryNames.length);
				if (!(expectedDirectoryNames.length == 2)) {
					workDirectoryOK = handleNonWorkDirectory(workDirectoryOK);
				} else {
					workDirectoryOK = true;
				}
			} else {
				tryAgain = handleNonDirectory();
			}
		} while ((workDirectoryOK != null) && !workDirectoryOK && tryAgain);
		if ((workDirectoryOK != null) && workDirectoryOK) {
			storeDataDirectory(workDirectoryPath);
		} else {
			workDirectoryPath = null;
		}
		return workDirectoryPath;
	}

	private Boolean handleNonWorkDirectory(Boolean baseDirectoryOK) {
		Shell messageShell = new Shell(display);
		MessageBox messageBox = new MessageBox(messageShell, SWT.YES | SWT.NO
				| SWT.CANCEL);
		messageBox
				.setMessage("This directory is not an existing workdirectory.\n"
						+ "Create a new one?");
		int returnCode = messageBox.open();
		log.fatal("ReturnCode: " + returnCode);
		switch (returnCode) {
		case (SWT.YES):
			baseDirectoryOK = true;
			break;
		case (SWT.CANCEL):
			baseDirectoryOK = null;
			break;
		default:
		}
		messageShell.dispose();
		return baseDirectoryOK;
	}

	private boolean handleNonDirectory() {
		boolean tryAgain = false;
		Shell messageShell = new Shell(display);
		MessageBox messageBox = new MessageBox(messageShell, SWT.YES | SWT.NO);
		messageBox.setMessage("You have not chosen a directory.\n"
				+ "Try again?");
		int returnCode = messageBox.open();
		log.fatal("ReturnCode: " + returnCode);
		if (SWT.YES == returnCode) {
			tryAgain = true;
		}
		messageShell.dispose();
		return tryAgain;
	}

	static class ValidDataDirectoryFilter implements FilenameFilter {
		File baseDir = null;

		public ValidDataDirectoryFilter(File validDirectory) {
			super();
			baseDir = validDirectory;
		}

		@Override
		public boolean accept(File dir, String name) {
			boolean validated = true;
			if (!((dir != null) && (dir.equals(baseDir)))) {
				validated = false;
			}
			if (!(StandardTreeNodeLabelsEnum.REFERENCEDATA.getNodeLabel()
					.equals(name) || StandardTreeNodeLabelsEnum.SIMULATIONS
					.getNodeLabel().equals(name))) {
				validated = false;
			}
			return validated;
		}
	}

	private String readDataDirectory() {
		String workDirectoryPath = defaultDataPath;
		Properties iniProperties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			iniProperties.load(fis);
			String propertyDirectoryPath = iniProperties.getProperty(TREE_BASE);
			if (propertyDirectoryPath != null) {
				workDirectoryPath = propertyDirectoryPath;
			}
			log.fatal("Returning workDirectoryPath: " + workDirectoryPath);
			return workDirectoryPath;
		} catch (FileNotFoundException e) {
			return workDirectoryPath;
		} catch (IOException e) {
			return workDirectoryPath;
		} finally {
			log.fatal("Tree-Base-Directory: " + workDirectoryPath);
		}
	}

	private String selectBaseDirectory(String parBaseDirectoryPath)
			throws ConfigurationException {
		Shell myShell = new Shell(display);
		DirectoryDialog directoryDialog = new DirectoryDialog(myShell);
		if (parBaseDirectoryPath != null) {
			directoryDialog.setFilterPath(parBaseDirectoryPath);
		} else {
			directoryDialog.setFilterPath(defaultDataPath);
		}
		directoryDialog.setText("Choose the DYNAMO-HIA home directory.");
		directoryDialog
				.setMessage("In the DYNAMO-HIA home directory data and simulations are stored.");
		directoryDialog.open();
		String newBaseDirectoryPath = directoryDialog.getFilterPath();
		myShell.dispose();
		return newBaseDirectoryPath;
	}

	private void storeDataDirectory(String baseDirectoryPath) {
		try {
			if (baseDirectoryPath != null) {
				Properties iniProperties = new Properties();
				iniProperties.put(TREE_BASE, baseDirectoryPath);
				FileOutputStream fos = new FileOutputStream(iniFilePath);
				iniProperties.store(fos, "No comment");
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

}
