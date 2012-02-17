package nl.rivm.emi.dynamo.ui.panels.help;

/**
 * First actual helptekst panel.
 * Commented out from the working debug version.
 */

/* hendriek changed the group into browser, permitting html formatting of the text */
/* the finding of the default help text (not hovering) seems different now, should be looked at 
 * not a showstopper */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ElementNameScrollableHelpGroup {
	Log log = LogFactory.getLog(this.getClass().getName());
	final Composite parent;
	//final public Group theGroup;
	final public Browser theGroup;
	//final Label label;
	Point preferredSize = null;
	String borderText;
	int count = 0;
	private String helpDirectoryPath;
	private String helpKey = "Init";

	public ElementNameScrollableHelpGroup(Composite parent, String borderText,
			String elementName, String helpDirectoryPath) {
		this.parent = parent;
		this.borderText = borderText;
		this.helpDirectoryPath = helpDirectoryPath;
		//theGroup = new Group(parent, SWT.V_SCROLL);
		theGroup  = new Browser(parent, SWT.NONE);
		
		
		theGroup.setText(borderText);
		FillLayout fillLayout = new FillLayout();
		theGroup.setLayout(fillLayout);
		//label = new Label(theGroup, SWT.WRAP);
		doSetHelpText(elementName);
	}

	private void doSetHelpText(String helpKey) {
		this.helpKey = helpKey;
		String helpContent = "";
		try {
			if (!helpContent.equalsIgnoreCase(helpKey)) {
				String helpFilePath = helpDirectoryPath + File.separator
						+ helpKey + ".txt";
				File helpFile = new File(helpFilePath);
				if (helpFile.exists()) {
					if (helpFile.isFile()) {
						if (helpFile.canRead()) {
							FileReader reader = new FileReader(helpFile);
							// StringBuffer stringBuffer = new StringBuffer();
							char[] charArray = new char[(int) helpFile.length()];
							reader.read(charArray);
							helpContent = new String(charArray);
						} else {
							helpContent = "Can't read helpfile for: " + helpKey;
						}
					} else {
						helpContent = "Help for: \"" + helpKey
								+ "\" does not point to a file.";
					}
				} else {
					helpContent = "Helpfile for: \"" + helpKey
							+ "\" does not exist.(Searched at: "
							+ helpFile.getAbsolutePath() + ")";
				}
			}
		} catch (FileNotFoundException e) {
			helpContent = "Exception! Helpfile for: \"" + helpKey
					+ "\" could not be found.";
		} catch (IOException e) {
			helpContent = "Exception! Helpfile for: \"" + helpKey
					+ "\" threw an IOException.";
		} finally {
			synchronized (theGroup) {
				if (!theGroup.isDisposed()) {
					// Sometimes a race condition occurs where the group has
					// been disposed of before the helptext is set...
				//	Rectangle clientArea = theGroup.getClientArea();
				
				//	label.setBounds(clientArea);
				//	label.setText(helpContent);
				//	label.update();		
				theGroup.setText(helpContent);	
				theGroup.update();
				}
			}
		}
	}

//	public Group getGroup() {
	//	return theGroup;
//	}
	
	
	public Browser getGroup() {
		return theGroup;
	}

	public void setHelpText(String elementName) {
		String updatedLabelOutput;
		if ("Blank".equals(elementName)) {
			updatedLabelOutput = "";
		} else {
			// TODO Add indirection.
			updatedLabelOutput = elementName;
		}
		doSetHelpText(updatedLabelOutput);
	}

	public String getHelpKey() {
		return helpKey;
	}
}
