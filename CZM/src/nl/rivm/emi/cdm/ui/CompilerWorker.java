package nl.rivm.emi.cdm.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.SwingWorker;

import nl.rivm.emi.cdm.util.log4j.AntBuildListener4Log4J;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class CompilerWorker extends SwingWorker<Void, Void> {
	Log compilerLog = LogFactory.getLog(getClass().getSimpleName());

	private/* MainSwingBorderLayoutWindow */ActionListener callbackWindow = null;

	private File simulationDirectory;

	public CompilerWorker(
	/* MainSwingBorderLayoutWindow */ActionListener callbackWindow,
			File simulationDirectory) {
		super();
		compilerLog.info("Constructing.");
		this.callbackWindow = callbackWindow;
		this.simulationDirectory = simulationDirectory;
	}

	@Override
	public Void doInBackground() {
		compilerLog.info("doInBackground() called.");
		return traverseDirectories();
	}

	private Void traverseDirectories() {
		Void pietje = null;
		File updateRulesDirectory = null;
		File[] simDirContent = simulationDirectory.listFiles();
		for (int count = 0; count < simDirContent.length; count++) {
			File contentItem = simDirContent[count];
			if (contentItem.isDirectory()
					&& "Update Rules".equalsIgnoreCase(contentItem.getName())) {
				updateRulesDirectory = contentItem;
				break;
			}
		}
		if(updateRulesDirectory != null){
			File[] updateRulesDirContent = updateRulesDirectory.listFiles();
			for (int count = 0; count < updateRulesDirContent.length; count++) {
				File contentItem = updateRulesDirContent[count];
				if (contentItem.isDirectory()) {
				antCompilationJob(contentItem);
				}
			}	
		}
		return pietje;
	}

	private Void antCompilationJob(File updateRuleDirectory) {
		Void bogus = null;
		Project project = new Project();
		project.init();
		compilerLog.info("antProject initialized.");
		setProgress(10);
		AntBuildListener4Log4J logListener = new AntBuildListener4Log4J();
		project.addBuildListener(logListener);
		compilerLog.info("ant BuildListener added.");
		setProgress(20);
		File buildFile = new File("builduserupdaterules.xml");
		ProjectHelper.configureProject(project, buildFile);
		compilerLog.info("antProject configured.");
		setProgress(30);
		project.setProperty("ant.file", buildFile.getAbsolutePath());
//		project.setProperty("src.dir", "updaterulesrc");
		project.setProperty("src.dir", updateRuleDirectory.getAbsolutePath());
//		project.setProperty("build.dir", "bin");
		project.setProperty("build.dir", updateRuleDirectory.getAbsolutePath());
			project
				.setProperty("class.path", System
						.getProperty("java.class.path"));
		compilerLog.info("antProject properties set.");
		setProgress(40);
		try {
			project.executeTarget("main");
			compilerLog.info("antProject main target executed.");
			setProgress(50);
			return (bogus);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			setProgress(75);
			return (bogus);
		} finally {
		}
	}

	@Override
	public void done() {
		compilerLog.info("done() called.");
		ActionEvent event = new ActionEvent(this, 4711, "CompileDone");
		callbackWindow.actionPerformed(event);
		setProgress(100);
	}
}
