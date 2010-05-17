package nl.rivm.emi.cdm.log4j.appender;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nl.rivm.emi.cdm.log4j.appender.SwingAppender.SwingLogOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MainSwingWindow extends JPanel implements ActionListener {

	Log log = LogFactory.getLog(this.getClass().getSimpleName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JFrame frame = null;

	// private final GridBagConstraints constraints;

	private JTextArea textArea;
	private JProgressBar progressBar;
	private final JButton startButton;
	private final JButton compileButton;
	private final JButton stopButton;

	private LogSwingWorker logSwingWorker = null;

	private CompilerWorker compilerWorker = null;

	public MainSwingWindow() {
		log.info("Constructing.");
		frame = new JFrame("LogWindow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(250, 150));
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.NORTH);
		progressBar = new JProgressBar(0, 100);
		progressBar.setPreferredSize(new Dimension(250, 25));
		add(progressBar, BorderLayout.SOUTH);
		startButton = makeButton("Start");
		add(startButton, BorderLayout.LINE_START);
		compileButton = makeButton("Compile");
		add(compileButton, BorderLayout.CENTER);
		stopButton = makeButton("Stop");
		add(stopButton, BorderLayout.LINE_END);
		stopButton.setEnabled(false);
		frame.setContentPane(this);
		// frame.setSize(250, 150);
		frame.pack();
		frame.setVisible(true);
		// Autostart logging..
		startButton.setEnabled(false);
		compileButton.setEnabled(true);
		stopButton.setEnabled(true);
		logSwingWorker = this.new LogSwingWorker();
		logSwingWorker.execute();
	}

	private JButton makeButton(String caption) {
		JButton b = new JButton(caption);
		b.setPreferredSize(new Dimension(75, 50));
		b.setActionCommand(caption);
		b.addActionListener(this);
		return b;
	}

	public void actionPerformed(ActionEvent e) {
		if ("Start" == e.getActionCommand()) {
			startButton.setEnabled(false);
			compileButton.setEnabled(true);
			stopButton.setEnabled(true);
			logSwingWorker = this.new LogSwingWorker();
			logSwingWorker.execute();
		} else if ("Compile" == e.getActionCommand()) {
			// startButton.setEnabled(false);
			compileButton.setEnabled(false);
			// stopButton.setEnabled(false);
			askInAndOut();
			compilerWorker = new CompilerWorker(this, null, null);
			compilerWorker
					.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer) evt
										.getNewValue());
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});

			compilerWorker.execute();
		} else if ("CompileDone" == e.getActionCommand()) {
			compileButton.setEnabled(true);
			compilerWorker = null;
		} else if ("Stop" == e.getActionCommand()) {
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			logSwingWorker.cancel(true);
			logSwingWorker = null;
		}
	}

	private void askInAndOut() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(MainSwingWindow.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// This is where a real application would open the file.
			log.debug("Opening: " + file.getName());
		} else {
			log.debug("Open command cancelled by user.");
		}
		String classPathString = System.getProperty("java.class.path");
		String firstUserClassDirectory = null;
		StringTokenizer classPathTokenizer = new StringTokenizer(
				classPathString, ";");
		while (classPathTokenizer.hasMoreTokens()) {
			String token = classPathTokenizer.nextToken();
			// if(token.indexOf("nl.rivm.emi.cdm") != -1){
			firstUserClassDirectory = token;
			break;
		}
		log.debug("ClassPath: " + classPathString);
		log.debug("firstUserClassPath: " + firstUserClassDirectory);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		returnVal = fc.showOpenDialog(MainSwingWindow.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// This is where a real application would save the file.
			log.debug("Saving: " + file.getAbsolutePath());
		} else {
			log.debug("Save command cancelled by user.");
		}
	}

	public static void main(String s[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainSwingWindow();
			}
		});
	}

	public class LogSwingWorker extends SwingWorker<Void, String> {
		Log workerLog = LogFactory.getLog(getClass().getSimpleName());
		SwingLogOutputStream logStream = null;
		String logLine;

		ArrayList<String> logList = new ArrayList<String>();

		public LogSwingWorker() {
			super();
		}

		@Override
		public Void doInBackground() {
			workerLog.info("doInBackground() called.");
			Void pietje = null;
			try {
				int count = 0;
				while (!isCancelled()) {
					if (logStream == null) {
						workerLog.info("Trying to get the logStream.");
						Swing2LogConnectorSingleton connector = Swing2LogConnectorSingleton
								.getInstance();
						logStream = connector.getTheStream();
						Thread.sleep(1000);
					} else {
						if (logLine == null) {
							logLine = logStream.read();
							String publishString = null;
							if (logLine != null) {
								publishString = logLine;
								logList.add(publishString);
								if (logList.size() > 10) {
									logList.remove(0);
								}
								publish(publishString);
								workerLog.info("Published: " + publishString);
								logLine = null;
							} else {
								// publishString = Integer.toString(count);
							}
							// publish(publishString);
							// workerLog.info("Published: " + publishString);
						}
						Thread.sleep(100);
					}
					count++;
				}
				return (pietje);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return (pietje);
			}
		}

		protected void process(List<String> logLines) {
			workerLog.info("process() called.");
			// String logLine = logLines.get(logLines.size() - 1);
			StringBuffer logWindowSB = new StringBuffer();
			for (String logEntry : logList) {
				logWindowSB.append(logEntry);
			}
			textArea.setText(logWindowSB.toString());
		}

		@Override
		public void done() {
			workerLog.info("done() called.");
		}
	};
}
