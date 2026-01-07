package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.global.DirectoryNode;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.ParentNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

//import com.sun.org.apache.xerces.internal.dom.ChildNode;

/**
 * Started as a copy of the RelativeRiskContextPanel because the logic becomes
 * too convoluted for the combination.
 *
 * @author mondeelr
 *
 */
public class RelativeRiskForDStarContextPanel implements RelativeRiskContextInterface{
	public Group getGroup() {
		return group;
	}

	@SuppressWarnings("unused")
	private static final String FROM = "From ";
	@SuppressWarnings("unused")
	private static final String TO = "To ";
	Group group;
	Label nameLabel;

	Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * @param parent The window this contextPanel will be incorporated in.
	 * @param selectedNode The node who's contextmenu was chosen to get here.
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	public RelativeRiskForDStarContextPanel(Composite parent, BaseNode selectedNode)
			throws ConfigurationException, DynamoInconsistentDataException {
		createPanel(parent, selectedNode);
	}

	public void createPanel(Composite parent, BaseNode selectedNode)
			throws DynamoInconsistentDataException {
		ParentNode relativeRisksNode = null;
		if(selectedNode instanceof DirectoryNode){
			relativeRisksNode = (ParentNode) selectedNode;
		}else{
			if(selectedNode instanceof FileNode){
				relativeRisksNode = (ParentNode) (((FileNode)selectedNode).getParent());
			}else{
				throw new DynamoInconsistentDataException(
						"Unexpected node-type \"" + selectedNode.getClass().getSimpleName() 
						+ "\".\n Can only handle File- and Directory-Nodes.");
				
			}	
		}
		String relativeRisksNodeLabel = ((BaseNode)relativeRisksNode).deriveNodeLabel();
        String riskTargetLabel = null;
		if(StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel().equals(relativeRisksNodeLabel)){
			riskTargetLabel = "Death"; 
		} else {
			if(StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel().equals(relativeRisksNodeLabel)){
				riskTargetLabel = "Disability"; 
		} else {
			throw new DynamoInconsistentDataException(
					"Unexpected directoryNode-name \"" + relativeRisksNodeLabel + "\".\n Can only handle \"" 
					+ StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel()
					+ "\" and \"" + StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel()
					+ "\".");
		}
		}
		ParentNode riskFactorNode = ((DirectoryNode)relativeRisksNode).getParent();
		String riskFactorLabel = ((BaseNode)riskFactorNode).deriveNodeLabel();
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		// log.fatal("RiskSourceNode: " + riskSourceNode + ", selectedNode: " +
		// selectedNode);
		EntityNamePanel riskSourcePanel = new EntityNamePanel(group, "From" , riskFactorLabel, "");
		EntityNamePanel riskTargetPanel =  new EntityNamePanel(group, "To" , riskTargetLabel, "");
		riskTargetPanel.putFirstInContainer(30);
		riskSourcePanel.putLastInContainer(riskTargetPanel.group);
	}

	public void handlePlacementInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}
}
