package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

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


public class TransitionDriftGroup {
	Group theGroup;

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public TransitionDriftGroup(Shell shell, @SuppressWarnings("rawtypes") TypedHashMap lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);		
		// Retrieve the Risk_Factor name and Transition name 
		String[] entityArray = Util.deriveEntityLabelAndValueFromTransitionSourceNode(selectedNode);
		EntityNamePanel riskSourcePanel = new EntityNamePanel(theGroup,
		entityArray[0], entityArray[1], null);
		riskSourcePanel.putFirstInContainer(30);
//		EntityNamePanel entityNameGroup = new EntityNamePanel(theGroup,
//				entityArray[2], entityArray[3]);
//		entityNameGroup.putMiddleInContainer(riskSourcePanel.group, 30);
		
		TransitionDriftParameterGroup parameterGroup = new TransitionDriftParameterGroup(
				theGroup, lotsOfData, dataBindingContext, helpGroup);		
//		parameterGroup.handlePlacementInContainer(entityNameGroup.group);
		parameterGroup.handlePlacementInContainer(riskSourcePanel.group);
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
