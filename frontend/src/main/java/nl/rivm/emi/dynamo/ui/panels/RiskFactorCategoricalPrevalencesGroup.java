package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

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

public class RiskFactorCategoricalPrevalencesGroup {
	Log log = LogFactory.getLog(this.getClass().getName());
	Group theGroup;

	public RiskFactorCategoricalPrevalencesGroup(Shell shell,
			TypedHashMap modelObject, DataBindingContext dataBindingContext,
			BaseNode selectedNode, HelpGroup helpGroup)
			throws ConfigurationException {
		log.debug("selectedNode-label: " + selectedNode.deriveNodeLabel());
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		String[] entityStrings = Util
				.deriveEntityLabelAndValueFromRiskSourceNode_Directories(selectedNode);
		EntityNamePanel entityNamePanel = new EntityNamePanel(theGroup,
				entityStrings[0], entityStrings[1], null);
		entityNamePanel.putInContainer();
		RiskFactorCategoricalPrevalencesParameterGroup parameterGroup = new RiskFactorCategoricalPrevalencesParameterGroup(
				theGroup, modelObject, dataBindingContext, helpGroup);
		parameterGroup.handlePlacementInContainer(entityNamePanel.group);
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