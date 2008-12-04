package nl.rivm.emi.dynamo.ui.treecontrol;
/**
 * The RootNode does not correspond to a physical directory. 
 * It's only child is the base directory of the Dynamo-HIA configuration.
 */
import java.util.ArrayList;

public class RootNode extends BaseNode implements ParentNode {

	ArrayList<ChildNode> children = new ArrayList<ChildNode>();

	/**
	 * Constructor for RootNode only.
	 * 
	 * @param rootChild
	 * @throws StorageTreeException
	 */
	public RootNode() throws StorageTreeException {
		super(null);
		log.info("Instantiating StorageTree-Root-Node.");
	}

	/**
	 * For debugging.
	 */
	public void report() {
		if (children != null) {
			for (ChildNode child : children) {
				child.report();
			}
		}
	}

	public void addChild(ChildNode storageTreeNode) throws StorageTreeException {
		if (children.size() == 0) {
			children.add(storageTreeNode);
		} else {
			throw new StorageTreeException(
					"RootNode may only have a single child.");
		}
	}

	/**
	 * 
	 * @return
	 */
	public int numberOfChildren() {
		int numberOfChildren = 0;
		if (children != null) {
			numberOfChildren = children.size();
		}
		return numberOfChildren;
	}

	public Object[] getChildren() {
		return children.toArray();
	}

	/** 
	 * A RootNode must have exactly one child, the basedirectory.
	 */ 
	public int populateChildren() throws StorageTreeException {
	if(children.size() == 1){
		((DirectoryNode)children.get(0)).populateChildren();
		} else {
		throw new StorageTreeException("A RootNode must have exactly one child.");
		}
		return 1;
	}
}
