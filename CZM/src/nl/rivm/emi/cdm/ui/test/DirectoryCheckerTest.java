package nl.rivm.emi.cdm.ui.test;

import static org.junit.Assert.assertNull;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import junit.framework.JUnit4TestAdapter;

import nl.rivm.emi.cdm.ui.SelectSimDirWorker;
import nl.rivm.emi.cdm.util.log4j.Swing2LogConnectorSingleton;
import nl.rivm.emi.cdm.util.log4j.SwingAppender.SwingLogOutputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DirectoryCheckerTest implements ActionListener{

	Log log = LogFactory.getLog(this.getClass().getSimpleName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame = null;

	public final String simulationCaption = "Select";
	public final String simulationDirectoryFieldInitialText = "First select a simulation directory";
	public final String compileCaption = "Compile";
	public final String loadDataCaption = "Load data";
	public final String runCaption = "Run";
	public final String interruptCaption = "Interrupt";

	private SelectSimDirWorker simSelectWorker = null;

	File simulationDirectory = null;

	@Before
	public void setup() throws ConfigurationException {
		simSelectWorker = new SelectSimDirWorker(null);
		simSelectWorker.execute();
	}

	@After
	public void teardown() {
	}

	@Test
	public void DirectoryCheckerTest() {
		simSelectWorker.doInBackground();
		try {
			File simulationDirectory = simSelectWorker.get();
		} catch (InterruptedException e) {
			assertNull(e);
			e.printStackTrace();
		} catch (ExecutionException e) {
			assertNull(e);
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
