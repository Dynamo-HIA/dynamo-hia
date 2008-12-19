package nl.rivm.emi.dynamo.data.structure;
/**
 * A 
 */
import java.util.ArrayList;

public class TemplateTree extends ArrayList<TemplateTreeNode>{

	BaseDirectoryNode baseDirectoryNode;

	private TemplateTree(BaseDirectoryNode baseDirectoryNode) {
		super();
		this.baseDirectoryNode = baseDirectoryNode;
	}

	public boolean add(TemplateTreeNode arg0) {
		return baseDirectoryNode.add(arg0);
	}
	

}
