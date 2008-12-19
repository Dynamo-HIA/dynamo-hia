package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FileTreeLabelProvider extends LabelProvider {
	public String getText(Object element) {
		return ((BaseNode) element).toString();
	}

	public Image getImage(Object element) {
		if (element instanceof DirectoryNode) {
			return Util.getImageRegistry().get("folder");
		} else {
			if (element instanceof FileNode) {
				return Util.getImageRegistry().get("file");
			} else {
				return Util.getImageRegistry().get("error");
			}
		}
	}
}
