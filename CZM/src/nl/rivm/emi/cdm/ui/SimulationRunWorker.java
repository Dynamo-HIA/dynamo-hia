package nl.rivm.emi.cdm.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.SwingWorker;

import nl.rivm.emi.cdm.exceptions.CDMRunException;
import nl.rivm.emi.cdm.simulation.Simulation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimulationRunWorker extends SwingWorker<Void, Void> implements SetProgressInterface{
	Log compilerLog = LogFactory.getLog(getClass().getSimpleName());

	private ActionListener callbackWindow = null;

	private Simulation simulation = null;

	private File sourceBaseDirectory;

	private File outputBaseDirectory;

	public SimulationRunWorker(ActionListener callbackWindow,
			Simulation simulation, File sourceBaseDirectory,
			File outputBaseDirectory) {
		super();
		compilerLog.info("Constructing.");
		this.callbackWindow = callbackWindow;
		this.simulation = simulation;
		this.sourceBaseDirectory = sourceBaseDirectory;
		this.outputBaseDirectory = outputBaseDirectory;
	}

	@Override
	public Void doInBackground() {
		compilerLog.info("doInBackground() called.");
		// return javaToolsUnvolendete();
		return doSimulationRun();
	}

	private Void doSimulationRun() {
		Void bogus = null;
		try {
			simulation.run((SetProgressInterface)this, 1);
		} catch (CDMRunException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bogus;
	}

	@Override
	public void done() {
		compilerLog.info("done() called.");
		ActionEvent event = new ActionEvent(this, 4711, "RunDone");
		callbackWindow.actionPerformed(event);
	}

	@Override
	public void setMyProgress(int progress) {
	try{
		setProgress(progress);
			Thread.sleep(250);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
