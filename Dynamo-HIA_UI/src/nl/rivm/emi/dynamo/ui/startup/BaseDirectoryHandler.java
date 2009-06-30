package nl.rivm.emi.dynamo.ui.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BaseDirectoryHandler {
	Log log = LogFactory.getLog(this.getClass().getName());

	private static final String TREE_BASE = "tree.base";
	private static final String defaultDataPath = System
			.getProperty("user.dir")
			+ File.separator
			+ ".."
			+ File.separator
			+ ".."
			+ File.separator
			+ ApplicationWrapper.APPBASENAME;
	private static final String iniFilePath = System.getProperty("user.dir")
			+ File.separator + "ini" + File.separator + "lastopened.ini";

	Display display;
	
	public BaseDirectoryHandler(Display display) {
		super();
		this.display = display;
	}

	public String provideBaseDirectory() throws ConfigurationException{
		String baseDirectoryPath = determineDataDirectory();
		baseDirectoryPath = selectBaseDirectory(baseDirectoryPath);
		storeDataDirectory(baseDirectoryPath);
		return baseDirectoryPath;

	}

	private String determineDataDirectory() {
		String baseDirectoryPath = defaultDataPath;
		Properties iniProperties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(iniFilePath);
			iniProperties.load(fis);
			String propertyDirectoryPath = iniProperties.getProperty(TREE_BASE);
			if (propertyDirectoryPath != null) {
				baseDirectoryPath = propertyDirectoryPath;
			}
			return baseDirectoryPath;
		} catch (FileNotFoundException e) {
			return baseDirectoryPath;
		} catch (IOException e) {
			return baseDirectoryPath;
		} finally {
			log.fatal("Tree-Base-Directory: " + baseDirectoryPath);
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
