package nl.rivm.emi.dynamo.ui.treecontrol;

import java.util.ArrayList;


public interface ParentNode {

	public int numberOfChildren();

	public Object[] getChildren();

	public void addChild(ChildNode storageTreeNode) throws StorageTreeException;

	public int populateChildren() throws StorageTreeException;

}
