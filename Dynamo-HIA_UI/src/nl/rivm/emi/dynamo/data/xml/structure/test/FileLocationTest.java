package nl.rivm.emi.dynamo.data.xml.structure.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.RootNode;

public class FileLocationTest {
	Log log = LogFactory.getLog(getClass().getName());
	private final int LEVELSTOTEST = 3;
	private final int parentIndex = 0;
	private final int grandParentIndex = 1;
	private final int greatGrandParentIndex = 2;

	private String[] expectedNodeLabels = new String[LEVELSTOTEST];

	public FileLocationTest(String expectedParenNodeLabel,
			String expectedGrandParenNodeLabel,
			String expectedGreatGrandParenNodeLabel) {
		super();
		expectedNodeLabels[parentIndex] = expectedParenNodeLabel;
		expectedNodeLabels[grandParentIndex] = expectedGrandParenNodeLabel;
		expectedNodeLabels[greatGrandParentIndex] = expectedGreatGrandParenNodeLabel;
	}

	public boolean test(FileNode testNode) {
		String[] actualNodeLabels = new String[LEVELSTOTEST];
		boolean locationOK = true;
		ParentNode parentNode = ((ChildNode) testNode).getParent();
		if (!(parentNode == null) && !(parentNode instanceof RootNode)) {
			actualNodeLabels[parentIndex] = ((BaseNode) parentNode)
					.deriveNodeLabel();
			ParentNode grandParentNode = ((ChildNode) parentNode).getParent();
			if (!(grandParentNode == null)
					&& !(grandParentNode instanceof RootNode)) {
				actualNodeLabels[grandParentIndex] = ((BaseNode) grandParentNode)
						.deriveNodeLabel();
				ParentNode greatGrandParentNode = ((ChildNode) grandParentNode)
						.getParent();
				if (!(greatGrandParentNode == null)
						&& !(greatGrandParentNode instanceof RootNode)) {
					actualNodeLabels[greatGrandParentIndex] = ((BaseNode) greatGrandParentNode)
							.deriveNodeLabel();
				}
			}
		}
		log.debug("Testing: " + actualNodeLabels[parentIndex] + "-"
				+ actualNodeLabels[grandParentIndex] + "-"
				+ actualNodeLabels[greatGrandParentIndex] + " against: "
				+ expectedNodeLabels[parentIndex] + "-"
				+ expectedNodeLabels[grandParentIndex] + "-"
				+ expectedNodeLabels[greatGrandParentIndex]);
		// Testing, ik one test fails the location is not OK.
		for (int count = 0; count < LEVELSTOTEST; count++) {
			if (expectedNodeLabels[count] != null) {
				// Test needed.
				if (actualNodeLabels[count] == null) {
					locationOK = false;
					break;
				} else {
					// 20090629 Ignorecase added to prevent too many errors.
					if (!expectedNodeLabels[count]
							.equalsIgnoreCase(actualNodeLabels[count])) {
						locationOK = false;
						break;
					}
				}
			}
		}
		return locationOK;
	}
}
