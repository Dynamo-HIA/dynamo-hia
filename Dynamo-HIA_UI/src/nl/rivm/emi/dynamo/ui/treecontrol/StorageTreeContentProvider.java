package nl.rivm.emi.dynamo.ui.treecontrol;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class StorageTreeContentProvider implements ITreeContentProvider {

	RootNode rootNode = null;

	public StorageTreeContentProvider(RootNode rootNode)
			throws StorageTreeException {
		if (rootNode != null) {
			this.rootNode = rootNode;
		} else {
			throw new StorageTreeException(
					"Content provider requires a non null RootNode reference.");
		}
	}

	public Object[] getElements(Object inputElement) {
		return ((ParentNode) inputElement).getChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return ((ParentNode) parentElement).getChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		ParentNode parentNode = null;
		if (element != null) {
			parentNode = ((ChildNode) element).getParent();
		}
		return parentNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if ((element != null) && (element instanceof ParentNode)
				&& ((ParentNode) element).numberOfChildren() != 0) {
			hasChildren = true;
		}
		return hasChildren;
	}

}