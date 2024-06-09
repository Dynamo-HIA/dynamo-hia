package nl.rivm.emi.dynamo.ui.treecontrol;



public interface ParentNode {

	public int numberOfChildren();

	public Object[] getChildren();

	public void addChild(ChildNode storageTreeNode) throws StorageTreeException;

	public int removeChild(ChildNode storageTreeNode);

	public int populateChildren() throws StorageTreeException;

}
