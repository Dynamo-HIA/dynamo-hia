package nl.rivm.emi.dynamo.ui.panels.help;

/**
 * First actual helptekst panel.
 * Commented out from the working debug version.
 */
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

	public ElementNameScrollableHelpGroup(Composite parent, String borderText,
			String elementName) {
		this.parent = parent;
		this.borderText = borderText;
		theGroup = new Group(parent, SWT.V_SCROLL);
		theGroup.setText(borderText);
		FillLayout fillLayout = new FillLayout();
		theGroup.setLayout(fillLayout);
		label = new Label(theGroup, SWT.WRAP);
		doSetHelpText("Init");
	}

	private void doSetHelpText(String helpText) {
		Rectangle clientArea = theGroup.getClientArea();
		// log.debug("PostPack: " + clientArea);
		// Point clientAreaSize = new Point(clientArea.width,
		// clientArea.height);
		// log.debug("ClientArea size after early pack of group: "
		// + clientAreaSize);
		// Point groupSize = theGroup.getSize();
		// log.debug("Group size: " + groupSize);
		// Point groupComp = theGroup.computeSize(0, 0);
		// log.debug("Computed group size: " + groupComp);
		// preferredSize = label.toControl(groupComp);
		// log.debug("Preferred size from computed group size: " +
		// preferredSize);
		// preferredSize = label.toControl(clientArea.width, clientArea.height);
		// log.debug("Preferred size from positive inverted clientArea: "
		// + preferredSize);
		label.setBounds(clientArea);
		// String labelOutput = "Clientarea: " + clientArea + helpText
		// + " Count: " + count;
		// label.setText(labelOutput);
		label.setText(helpText);
		// log.debug(labelOutput);
		// Point computedSize = label
		// .computeSize(preferredSize.x, preferredSize.y);
		// log.debug("Computed size after fill: " + computedSize);
		// groupComp = theGroup.computeSize(0, 0);
		// log.debug("Group size after fill: " + groupComp);
		// label.setBackground(new Color(null, 0xee, 0xee, 0x00));
		label.update();
		count++;
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
