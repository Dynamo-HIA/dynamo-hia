package nl.rivm.emi.dynamo.data.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TemplateTreeNode extends ArrayList<TemplateTreeNode> {
	/**
	 * The parent TemplateTreeNode of this one.
	 */
	private TemplateTreeNode parent = null;
	/**
	 * Field indicating whether a node should be created when it isn't yet
	 * present. 
	 * Presently these all are Directories. 
	 */
	private boolean standard = false;
	/**
	 * The String 
	 */
	private String templatePathSegment = "";

	private boolean exactMatch = true;
	/**
	 * Maybe WAGNI.
	 */
	private String extension =".xml";

}
