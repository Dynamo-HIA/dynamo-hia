package nl.rivm.emi.dynamo.ui.startup;

import nl.rivm.emi.dynamo.estimation.BaseDirectory;
import nl.rivm.emi.dynamo.global.SchemaFileProviderInitializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ApplicationWrapper {

	Log log = LogFactory.getLog(this.getClass().getName());
	Display display;
	Shell shell;

	public ApplicationWrapper() {
		super();
	}

	public void startApplication() throws Exception {
		BaseStorageTreeScreen application = null;
		String baseDirectoryPath = null;
		try {
			display = new Display();
			do {
				BaseDirectoryHandler baseDirectoryHandler = new BaseDirectoryHandler(
						display);
				baseDirectoryPath = baseDirectoryHandler.provideBaseDirectory();
				if (baseDirectoryPath != null) {
					BaseDirectory.getInstance(baseDirectoryPath);
					application = new BaseStorageTreeScreen(baseDirectoryPath);
					shell = application.open(display);
					shell.forceActive(); // added in 2025 to force the window on top
					Plugin plugin = ResourcesPlugin.getPlugin();
					log.debug("Before initialize: Plugin is " + plugin);
					SchemaFileProviderInitializer.initialize(plugin);
					log.debug("test completed normally.");
					if (shell != null) {
						shell.setText(ApplicationStatics.APPBASENAME + " "
								+ ApplicationStatics.RELEASE_TAG);
						shell.open();
						while (!shell.isDisposed()) {
							// plugin = ResourcesPlugin.getPlugin();
							// log.debug("Plugin is " +plugin);
							if (!display.readAndDispatch())
								display.sleep();
						}
					}
				}
			} while ((application != null)
					&& BaseStorageTreeScreen.RESTART.equals(application
							.getRestartMessage())
					&& (baseDirectoryPath != null));
			display.dispose();
		} catch (Exception e) {
			log.fatal("Exception caught: " + e.getClass().getName()
					+ " with message: " + e.getMessage());
			e.printStackTrace();
			// External behaviour adapted to CZM_MAIN Application.
			if ((shell != null) && (!shell.isDisposed())) {
				synchronized (shell) {
					MessageBox box = new MessageBox(this.shell,
							SWT.ERROR_UNSPECIFIED);
					box.setText("Exception caught.");
					box.setMessage("An unexpected error occurred:\n"
							+ e.getClass().getSimpleName() + "\n"
							+ e.getMessage() + "\n" + dumpTopOfStackTrace(e));
					box.open();
					this.shell.dispose();
				}
			}
			throw e;
		}
		// Adapted to CZM_MAIN Application.
		catch (Throwable t) {
			log.fatal("Throwable caught: " + t.getClass().getName()
					+ " with message: " + t.getMessage());
			t.printStackTrace();
			if ((shell != null) && (!shell.isDisposed())) {
				synchronized (shell) {
					MessageBox box = new MessageBox(this.shell,
							SWT.ERROR_UNSPECIFIED);
					box.setText("Throwable caught.");
					box.setMessage("An unexpected error occurred:\n"
							+ t.getClass().getSimpleName() + "\n"
							+ t.getMessage() + "\n" + dumpTopOfStackTrace(t));
					box.open();
					this.shell.dispose();
				}
			}
			throw new Exception(t);
		}
	}

	private String dumpTopOfStackTrace(Throwable thrown) {
		final Integer topSize = 3;
		StringBuffer resultBuffer = new StringBuffer();
		StackTraceElement[] stackTraceElementArray = thrown.getStackTrace();
		for (int count = 0; (count < topSize)
				&& (count < stackTraceElementArray.length); count++) {
			resultBuffer.append(stackTraceElementArray[count].getClassName()
					+ "." + stackTraceElementArray[count].getMethodName() + "("
					+ stackTraceElementArray[count].getLineNumber() + ")\n");
		}
		return resultBuffer.toString();
	}
}
