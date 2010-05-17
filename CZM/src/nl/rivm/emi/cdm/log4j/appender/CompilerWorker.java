package nl.rivm.emi.cdm.log4j.appender;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.SwingWorker;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class CompilerWorker extends SwingWorker<Void, Void> {
	Log compilerLog = LogFactory.getLog(getClass().getSimpleName());

	private MainSwingWindow callbackWindow = null;

	private File sourceBaseDirectory;

	private File outputBaseDirectory;

	public CompilerWorker(MainSwingWindow callbackWindow,
			File sourceBaseDirectory, File outputBaseDirectory) {
		super();
		compilerLog.info("Constructing.");
		callbackWindow = callbackWindow;
		this.sourceBaseDirectory = sourceBaseDirectory;
		this.outputBaseDirectory = outputBaseDirectory;
	}

	@Override
	public Void doInBackground() {
		compilerLog.info("doInBackground() called.");
		// return javaToolsUnvolendete();
		return antCompilationJob();
	}

	private Void antCompilationJob() {
		Void bogus = null;
		Project project = new Project();
		project.init();
		compilerLog.info("doInBackground() antProject initialized.");
		AntBuildListener4Log4J logListener = new AntBuildListener4Log4J();
		project.addBuildListener(logListener);
		compilerLog.info("doInBackground() ant BuildListener added.");

		File buildFile = new File("builduserupdaterules.xml");
		ProjectHelper.configureProject(project, buildFile);
		compilerLog.info("doInBackground() antProject configured.");
		project.setProperty("ant.file", buildFile.getAbsolutePath());
		project.setProperty("src.dir", "updaterulesrc");
		project.setProperty("build.dir", "bin");
		project
				.setProperty("class.path", System
						.getProperty("java.class.path"));
		compilerLog.info("doInBackground() antProject properties set.");
		try {
			project.executeTarget("main");
			compilerLog.info("doInBackground() antProject main target executed.");
			return (bogus);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return (bogus);
		}
	}

	private Void javaToolsUnvolendete() {
		compilerLog.info("doInBackground() called.");
		Void pietje = null;
		File[] files = new File[1]; // Error suppression.
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compilerLog.info("doInBackground() 1.");
			setProgress(10);
			Thread.sleep(1000);
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			compilerLog.info("doInBackground() 2.");
			setProgress(20);
			Thread.sleep(1000);
			StandardJavaFileManager fileManager = compiler
					.getStandardFileManager(diagnostics, null, null);
			fileManager.getJavaFileForInput(null, null, null);
			compilerLog.info("doInBackground() 3.");
			System.err.println("doInBackground() 3.");
			setProgress(30);
			Thread.sleep(1000);
			Iterable<? extends JavaFileObject> compilationUnits = fileManager
					.getJavaFileObjectsFromFiles(Arrays.asList(files));
			compilerLog.info("doInBackground() 3.5.");
			System.err.println("doInBackground() 3.5.");
			setProgress(40);
			Thread.sleep(1000);
			CompilationTask task = compiler.getTask(null, fileManager,
					diagnostics, null, null, compilationUnits);
			compilerLog.info("doInBackground() 3.75.");
			System.err.println("doInBackground() 3.75.");
			setProgress(50);
			Thread.sleep(1000);
			task.call();
			compilerLog.info("doInBackground() 4.");
			System.err.println("doInBackground() 4.");
			setProgress(60);
			Thread.sleep(1000);
			fileManager.close();
			compilerLog.info("doInBackground() 5.");
			setProgress(70);
			Thread.sleep(1000);

			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
					.getDiagnostics())
				compilerLog.error(String.format("Error on line %d in %d%n",
						diagnostic.getLineNumber(), diagnostic.getSource()
								.toUri()));
			compilerLog.info("doInBackground() completes normally.");
			setProgress(80);
			Thread.sleep(1000);
			return (pietje);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			compilerLog.info("doInBackground() threw an Exception.");
			setProgress(90);
			return (pietje);
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
			compilerLog.info("doInBackground() threw a Throwable: "
					+ t.getClass().getSimpleName() + " message: "
					+ t.getMessage());
			return (pietje);
		} finally {
			ActionEvent event = new ActionEvent(this, 4711, "CompileDone");
			callbackWindow.actionPerformed(event);
			setProgress(100);
		}
	}

	@Override
	public void done() {
		compilerLog.info("done() called.");
	}
}
