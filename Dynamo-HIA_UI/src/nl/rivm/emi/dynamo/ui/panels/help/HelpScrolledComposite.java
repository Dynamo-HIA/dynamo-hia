package nl.rivm.emi.dynamo.ui.panels.help;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class HelpScrolledComposite extends ScrolledComposite{
	Log log = LogFactory.getLog(this.getClass().getName());
	Composite parent = null;
	Label label = null;
	Point preferredSize = null;
	String borderText;
	int count = 0;

	public HelpScrolledComposite(Composite parent, String borderText,
			String elementName) {
		super(parent, SWT.V_SCROLL);
		this.parent = parent;
		this.borderText = borderText;
		FillLayout fillLayout = new FillLayout();
		setLayout(fillLayout);
		setHelpText(null);
	}

	private void setHelpText(String helpText) {
		if (label != null) {
			// label.dispose();
			// .setLayout(null);
			// .setLayout(new FillLayout());
		} else {
			label = new Label(this, SWT.WRAP);
		}
		Rectangle clientArea = getClientArea();
		// log.debug("PrePack: " + clientArea);
		// .pack(true);
		log.debug("PostPack: " + clientArea);
		Point clientAreaSize = new Point(clientArea.width, clientArea.height);
		log.debug("ClientArea size after early pack of group: "
				+ clientAreaSize);
		Point groupSize = getSize();
		log.debug("Group size: " + groupSize);
		Point groupComp = computeSize(0, 0);
		log.debug("Computed group size: " + groupComp);
		preferredSize = label.toControl(groupComp);
		log.debug("Preferred size from computed group size: " + preferredSize);
		preferredSize = label.toControl(clientArea.width, clientArea.height);
		log.debug("Preferred size from positive inverted clientArea: "
				+ preferredSize);
		// label.setSize(preferredSize);
		// label.setSize(new Point(clientArea.width, clientArea.height));// if
		// (helpText == null) {
		label.setBounds(clientArea);
		String labelOutput = "Clientarea: " + clientArea + helpText
				+ " Count: " + count;
		label.setText(labelOutput);
		log.debug(labelOutput);
		// } else {
		// label.setText(helpText);
		// log.debug(helpText);
		// }
		Point computedSize = label
				.computeSize(preferredSize.x, preferredSize.y);
		log.debug("Computed size after fill: " + computedSize);
		groupComp = computeSize(0, 0);
		log.debug("Group size after fill: " + groupComp);
		label.setBackground(new Color(null, 0xee, 0xee, 0x00));
		label.update();
		count++;
	}

	public void setHelpText(int id) {
		Rectangle clientArea = getClientArea();
		String updatedLabelOutput = "x: "
				+ clientArea.x
				+ " y: "
				+ clientArea.y
				+ " h: "
				+ clientArea.height
				+ " w: "
				+ clientArea.width
				+ " Append Bots: "
				+ id
				+ " kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak."
				+ " kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak."
				+ " kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak."
				+ " kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak."
				+ " kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak."
				+ " kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak kwak.";
		setHelpText(updatedLabelOutput);
	}
}
