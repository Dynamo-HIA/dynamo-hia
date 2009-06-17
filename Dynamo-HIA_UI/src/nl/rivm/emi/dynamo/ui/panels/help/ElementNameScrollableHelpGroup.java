package nl.rivm.emi.dynamo.ui.panels.help;

/**
 * First actual helptekst panel.
 * Commented out from the working debug version.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ElementNameScrollableHelpGroup {
	Log log = LogFactory.getLog(this.getClass().getName());
	Composite parent = null;
	public Group theGroup = null;
	Label label = null;
	Point preferredSize = null;
	String borderText;
	int count = 0;
	private String helpDirectoryPath;

	public ElementNameScrollableHelpGroup(Composite parent, String borderText,
			String elementName, String helpDirectoryPath) {
		this.parent = parent;
		this.borderText = borderText;
		this.helpDirectoryPath = helpDirectoryPath;
		theGroup = new Group(parent, SWT.V_SCROLL);
		theGroup.setText(borderText);
		FillLayout fillLayout = new FillLayout();
		theGroup.setLayout(fillLayout);
		label = new Label(theGroup, SWT.WRAP);
		doSetHelpText(elementName);
	}

	private void doSetHelpText(String helpText) {
		String helpContent = "Initial";
		try {
			String helpFilePath = helpDirectoryPath + File.separator + helpText
					+ ".txt";
			File helpFile = new File(helpFilePath);
			if (helpFile.exists()) {
				if (helpFile.isFile()) {
					if (helpFile.canRead()) {
						FileReader reader = new FileReader(helpFile);
						StringBuffer stringBuffer = new StringBuffer();
						char[] charArray = new char[(int) helpFile.length()];
						reader.read(charArray);
						helpContent = new String(charArray);

					} else {
						helpContent = "Can't read helpfile for: " + helpText;
					}
				} else {
					helpContent = "Help for: \"" + helpText
							+ "\" does not point to a file.";
				}
			} else {
				helpContent = "Helpfile for: \"" + helpText
						+ "\" does not exist.(Searched at: " + helpFile.getAbsolutePath() + ")";
			}
		} catch (FileNotFoundException e) {
			helpContent = "Exception! Helpfile for: \"" + helpText
			+ "\" could not be found.";
		} catch (IOException e) {
			helpContent = "Exception! Helpfile for: \"" + helpText
			+ "\" threw an IOException.";
		} finally {
			Rectangle clientArea = theGroup.getClientArea();
			label.setBounds(clientArea);
			label.setText(helpContent);
			label.update();
		}
	}

	public Group getGroup() {
		return theGroup;
	}

	public void setHelpText(String elementName) {
		String updatedLabelOutput;
		if ("Blank".equals(elementName)) {
			updatedLabelOutput = "Testing one two. Testing one two. Testing one two. Testing one two. Testing one two."
					+ " Testing one two. Testing one two. Testing one two. Testing one two. Testing one two."
					+ " Testing one two. Testing one two. Testing one two. Testing one two. Testing one two."
					+ " Testing one two. Testing one two. Testing one two. Testing one two. Testing one two."
					+ " Testing one two. Testing one two. Testing one two. Testing one two. Testing one two."
					+ " Testing one two. Testing one two. Testing one two. Testing one two. Testing one two.";
		} else {
			// TODO Add indirection.
			updatedLabelOutput = elementName;
		}
		doSetHelpText(updatedLabelOutput);
	}
}
