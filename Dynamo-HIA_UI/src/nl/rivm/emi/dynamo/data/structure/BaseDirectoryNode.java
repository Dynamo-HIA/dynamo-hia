package nl.rivm.emi.dynamo.data.structure;

import java.util.ArrayList;

/**
 * Node that should provide the only connection of the TemplateTree
 * to the real world.
 * 
 * @author mondeelr
 *
 */
public class BaseDirectoryNode extends ArrayList<TemplateTreeNode>{

	private String absolutePath;

	private BaseDirectoryNode(String absolutePath) {
		super();
		this.absolutePath = absolutePath;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}
	
}
