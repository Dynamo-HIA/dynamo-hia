package nl.rivm.emi.dynamo.data.structure;
/**
 * A 
 */
import java.util.ArrayList;

public class TemplateTree extends ArrayList<TemplateTreeNode>{

	/* BaseDirectoryNode */ String baseDirectoryNode;

	TemplateTree(String baseDirectoryAbsolutePath) {
		super();
		this.baseDirectoryNode = baseDirectoryAbsolutePath;
	}

//	public boolean add(TemplateTreeNode arg0) {
//		return baseDirectoryNode.add(arg0);
//	}
	

}
