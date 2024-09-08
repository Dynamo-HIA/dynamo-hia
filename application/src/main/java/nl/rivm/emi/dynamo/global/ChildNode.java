package nl.rivm.emi.dynamo.ui.treecontrol;

import java.io.File;

public interface ChildNode {

	public File getPhysicalStorage();

	public void report();

	public ParentNode getParent();

}
