package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RelativeRisksCompoundGroup {
	private Log log = LogFactory.getLog(this.getClass().getName());
	Group theGroup;

	public RelativeRisksCompoundGroup(Shell shell, TypedHashMap<?> modelObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup, int durationClassIndex, BaseNode riskSourceNode)
			throws ConfigurationException, DynamoInconsistentDataException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		RelativeRiskContextInterface relRiskPanel = null;
		if(riskSourceNode != null){
		relRiskPanel = new RelativeRiskContextPanel(
				theGroup, riskSourceNode, selectedNode);
		} else {
			relRiskPanel = new RelativeRiskForDStarContextPanel(
					theGroup, selectedNode);
		}
		relRiskPanel.handlePlacementInContainer();
		log.debug("Now for RelativeRisksCompoundParameterGroup");
		RelativeRisksCompoundParameterGroup parameterGroup = new RelativeRisksCompoundParameterGroup(
				theGroup, modelObject, dataBindingContext, helpGroup,
				durationClassIndex);
		parameterGroup.handlePlacementInContainer(relRiskPanel.getGroup());
//		parameterGroup.handlePlacementInContainer(null);
		}

	public void setFormData(Composite rightNeighbour, Composite lowerNeighbour) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(rightNeighbour, -2);
		formData.bottom = new FormAttachment(lowerNeighbour, -5);
		theGroup.setLayoutData(formData);
	}
}
