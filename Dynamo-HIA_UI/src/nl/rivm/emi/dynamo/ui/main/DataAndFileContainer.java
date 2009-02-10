package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

public interface DataAndFileContainer {
	public Object getData();
	public String getFilePath();
	public Object getRootElementName();
	public BaseNode getSelectedNode();
}
