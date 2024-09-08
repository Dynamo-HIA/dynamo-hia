package nl.rivm.emi.dynamo.global;

import nl.rivm.emi.dynamo.data.util.TreeStructureException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StructureTestUtil {

	private static Log log = LogFactory.getLog(StructureTestUtil.class);

/**
 * Method that does just what is called.
 * 
 * @param selectedNode
 * @param depthToCheck
 * @return
 * @throws TreeStructureException
 */
	static public boolean hasNoFileNodeChildren(DirectoryNode selectedNode,
			int depthToCheck) throws TreeStructureException {
		// Innocent until found guilty.
		boolean hasNoFileNodeChildren = true;
		depthToCheck -= 1;
		if (selectedNode instanceof ParentNode) {
			Object[] children = ((ParentNode) selectedNode).getChildren();
			for (Object child : children) {
				if (child instanceof FileNode) {
					hasNoFileNodeChildren = false;
				} else {
					if (child instanceof DirectoryNode) {
						if (depthToCheck > 0) {
							hasNoFileNodeChildren = hasNoFileNodeChildren(
									(DirectoryNode) child, depthToCheck);
						} else {
							throw new TreeStructureException(
									"Unexpected NodeType:"
											+ child.getClass().getName());
						}
					}
				}
				if (!hasNoFileNodeChildren) {
					break;
				}
			}
		}
		return hasNoFileNodeChildren;
	}
}
