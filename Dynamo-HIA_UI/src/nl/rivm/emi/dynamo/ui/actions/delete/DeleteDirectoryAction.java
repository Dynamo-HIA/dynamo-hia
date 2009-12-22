package nl.rivm.emi.dynamo.ui.actions.delete;

/**
 * Develop with populationSize as concrete implementation.
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.ui.actions.ActionBase;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DeleteDirectoryAction extends ActionBase {
	Log log = LogFactory.getLog(this.getClass().getName());
	private MessageStrings messageStrings;
	private int searchDepth;

	public DeleteDirectoryAction(Shell shell, TreeViewer v, BaseNode node,
			MessageStrings theStrings, int searchDepth) {
		super(shell, v, node, "IsNeverNeeded");
		this.messageStrings = theStrings;
		this.searchDepth = searchDepth;
	}

	@Override
	public void run() {
		try {
			String filePath = "";
			// Delete directories only.
			if (node instanceof DirectoryNode) {
				/*
				 * boolean hasNoFileNodeChildren; hasNoFileNodeChildren =
				 * StructureTestUtil .hasNoFileNodeChildren((DirectoryNode)
				 * node, searchDepth); if (hasNoFileNodeChildren) {
				 */MessageBox messageBox = new MessageBox(shell, SWT.YES
						| SWT.NO);
				messageBox.setText(messageStrings.getMessageBoxText());
				messageBox.setMessage(messageStrings.getMessagePart1()
						+ node.deriveNodeLabel()
						+ messageStrings.getMessagePart2());
				int returnCode = messageBox.open();
				boolean error = false;
				switch (returnCode) {
				case SWT.YES:
					log.debug("MessageBox returns: " + "SWT.YES");
					// error = recursiveDeleteOfDirectoryNodes((DirectoryNode)
					// node);
					error = initRecursiveUnderWaterDeleteOfAllChildren((DirectoryNode) node);
					// if (!error) {
					// removeNodeFromParent((DirectoryNode) node);
					// }
					break;
				case SWT.OK:
					log.debug("MessageBox returns: " + "SWT.OK");
					// error = recursiveDeleteOfDirectoryNodes((DirectoryNode)
					// node);
					error = initRecursiveUnderWaterDeleteOfAllChildren((DirectoryNode) node);
					// if (!error) {
					// removeNodeFromParent((DirectoryNode) node);
					// }
					break;
				case SWT.CANCEL:
					log.debug("MessageBox returns: " + "SWT.CANCEL");
					break;
				case SWT.NO:
					log.debug("MessageBox returns: " + "SWT.NO");
					break;
				case SWT.RETRY:
					log.debug("MessageBox returns: " + "SWT.RETRY");
					break;
				case SWT.ABORT:
					log.debug("MessageBox returns: " + "SWT.ABORT");
					break;
				case SWT.IGNORE:
					log.debug("MessageBox returns: " + "SWT.IGNORE");
					break;
				default:
					log
							.debug("MessageBox returns something unexpected. Returncode: "
									+ returnCode);
				}
				/*
				 * } else { MessageBox messageBox = new MessageBox(shell,
				 * SWT.OK);
				 * messageBox.setText(messageStrings.getMessageBoxText());
				 * messageBox.setMessage(messageStrings.getMessageNoGo()); int
				 * returnCode = messageBox.open(); }
				 */
			} else {
				MessageBox messageBox = new MessageBox(shell,
						SWT.ERROR_UNSPECIFIED | SWT.OK);
				messageBox
						.setText("This action is intended for use on a directory Node.");
				int returnCode = messageBox.open();
			}
			return;
		} catch (TreeStructureException e) {
			MessageBox messageBox = new MessageBox(shell,
					SWT.ERROR_ITEM_NOT_REMOVED | SWT.OK);
			messageBox.setText("Removing risk-factor.");
			messageBox.setMessage("Unexpected nodes were found.");
			int returnCode = messageBox.open();
		}
	}

	private void deleteNode(File savedFile) {
		MessageBox messageBox;
		if (savedFile.delete()) {
			ParentNode parentNode = ((ChildNode) node).getParent();
			parentNode.removeChild((ChildNode) node);
			theViewer.refresh();
		} else {
			messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
			messageBox.open();
		}
	}

	private boolean recursiveDeleteOfDirectoryNodes(DirectoryNode directoryNode)
			throws TreeStructureException {
		MessageBox messageBox;
		boolean error = false;
		if (directoryNode instanceof ParentNode) {
			Object[] children = ((ParentNode) directoryNode).getChildren();
			for (Object child : children) {
				if (child instanceof DirectoryNode) {
					error = recursiveDeleteOfDirectoryNodes((DirectoryNode) child);
					if (!error) {
						directoryNode.removeChild((ChildNode) child);
					} else {
						break;
					}
				} else {
					if (child instanceof FileNode) {
						error = true;
						break;
					} else {
						throw new TreeStructureException("Unexpected NodeType:"
								+ child.getClass().getName());
					}
				}
			}
			if (!error) {
				File file = ((BaseNode) directoryNode).getPhysicalStorage();
				file.delete();
			}
			theViewer.refresh();
		} else {
			messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
			messageBox.open();
		}
		return error;
	}

	private boolean recursiveDeleteOfAllChildren(DirectoryNode directoryNode)
			throws TreeStructureException {
		MessageBox messageBox;
		boolean error = false;
		if (directoryNode instanceof ParentNode) {
			Object[] children = ((ParentNode) directoryNode).getChildren();
			for (Object child : children) {
				if (child instanceof DirectoryNode) {
					error = recursiveDeleteOfAllChildren((DirectoryNode) child);
					if (!error) {
						directoryNode.removeChild((ChildNode) child);
					} else {
						break;
					}
				} else {
					if (child instanceof FileNode) {
						directoryNode.removeChild((ChildNode) child);
						// error = true;
						// break;
					} else {
						throw new TreeStructureException("Unexpected NodeType:"
								+ child.getClass().getName());
					}
				}
			}
			if (!error) {
				File file = ((BaseNode) directoryNode).getPhysicalStorage();
				file.delete();
			}
			theViewer.refresh();
		} else {
			messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
			messageBox.open();
		}
		return error;
	}

	private boolean initRecursiveUnderWaterDeleteOfAllChildren(
			DirectoryNode directoryNode) throws TreeStructureException {
		MessageBox messageBox;
		boolean success = false;
		File directoryFile = null;
		if (directoryNode instanceof ParentNode) {
			success = true;
			directoryFile = directoryNode.getPhysicalStorage();
			success = recursiveUnderWaterDeleteOfAllChildren(directoryFile);
			if (success) {
				success = positiveRemoveNodeFromParent((DirectoryNode) node);
			// Incorporated in the above function.	success = directoryFile.delete();
			}
			if (!success) {
				messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
				messageBox.setMessage("Node: "
						+ directoryNode.deriveNodeLabel()
						+ " could not be deleted.");
				messageBox.open();
			}
			theViewer.refresh();
		} else {
			log.error("DirectoryNode: " + directoryNode.deriveNodeLabel()
					+ " is not a ParentNode");
			messageBox = new MessageBox(shell, SWT.ERROR_INVALID_PARENT);
			messageBox.setMessage("Delete doesn't work on this node.");
			messageBox.open();
		}
		// Calling method expects an error flag...
		return !success;
	}

	private boolean recursiveUnderWaterDeleteOfAllChildren(File directoryFile)
			throws TreeStructureException {
		MessageBox messageBox;
		boolean success = true;
		File[] children = directoryFile.listFiles();
		File child = null;
		if (children != null) {
			for (int count = 0; count < children.length; count++) {
				child = children[count];
				if (child.isDirectory()) {
					success = recursiveUnderWaterDeleteOfAllChildren(child);
				} else {
					if (child.isFile()) {
						success = child.delete();
					} else {
						throw new TreeStructureException("Unexpected NodeType:"
								+ child.getClass().getName());
					}
				}
				if (!success) {
					break;
				}
			}
		}
		if (success) {
			success = directoryFile.delete();
			if (!success) {
				messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
				messageBox.setMessage("Node: "
						+ directoryFile.getAbsolutePath()
						+ " could not be deleted.");
				messageBox.open();
			}
		} else {
			messageBox = new MessageBox(shell, SWT.ERROR_ITEM_NOT_REMOVED);
			if (child == null) {
				messageBox.setMessage("Delete doesn't work on this node.");
				log.error("Tried to delete a null child.");
			} else {
				messageBox.setMessage(child.getAbsolutePath()
						+ " could not be deleted.");
				log.error(child.getAbsolutePath() + " could not be deleted.");
			}
			messageBox.open();
		}
		return success;
	}

	private boolean positiveRemoveNodeFromParent(DirectoryNode directoryNode) {
				return !removeNodeFromParent(directoryNode);
	}

	private boolean removeNodeFromParent(DirectoryNode directoryNode) {
		MessageBox messageBox;
		boolean error = false;
		if (directoryNode instanceof ChildNode) {
			ParentNode parent = directoryNode.getParent();
			parent.removeChild(directoryNode);
			theViewer.refresh();
		} else {
			error = true;
		}
		return error;
	}

	private static class nodeSelection implements ISelection {

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
