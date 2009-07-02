package nl.rivm.emi.dynamo.ui.treecontrol.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.cdm.exceptions.ErrorMessageUtil;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.util.TreeStructureException;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StructureTestUtil {

	private static Log log = LogFactory.getLog(StructureTestUtil.class);

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
