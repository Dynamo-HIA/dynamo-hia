package nl.rivm.emi.dynamo.ui.treecontrol;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

public class MouseHoverListener implements Listener {
	final TreeViewer treeViewer;

	public MouseHoverListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	@Override
	public void handleEvent(Event arg0) {
		// treeViewer.getControl().setToolTipText(
		// "Widget: " + arg0.widget.toString() + " x: " + arg0.x + " y: "
		// + arg0.y);
		StringBuffer toolTipText = new StringBuffer();
		TreeItem theTreeItem = treeViewer.getTree().getItem(
				new Point(arg0.x, arg0.y));
		if (theTreeItem != null) {
			if (!theTreeItem.getExpanded() && (theTreeItem.getItemCount() > 0)) {
				toolTipText.append("Click on + to expand.\n");
				toolTipText.append("Right click to see possible action(s).");
			} else {
				toolTipText
				.append("Right click to see possible action(s).");
				Image okFileImage = Util.getImageRegistry().get(
						Util.imageRegistrySupportedXMLFileRightPlaceKey);
				Image okFolderImage = Util.getImageRegistry().get(
						Util.imageRegistryFolderKey);
				if ((okFolderImage != null)
						&& (!okFolderImage.equals(theTreeItem.getImage()))) {
					if ((okFileImage != null)
							&& (okFileImage.equals(theTreeItem.getImage()))) {
					} else {
						toolTipText.delete(0, toolTipText.length());
						toolTipText
								.append("This file has a wrong format,\n"
										+ "right clicking will generate a cryptic error message.");
					}
				}
			}
		}
		treeViewer.getControl().setToolTipText(toolTipText.toString());
	}
}
