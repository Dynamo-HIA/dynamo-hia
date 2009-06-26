package nl.rivm.emi.dynamo.ui.panels;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorContinuousPrevalencesObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class RiskFactorContinuousPrevalencesGroup {
	private Group theGroup;

	private Log log = LogFactory.getLog(this.getClass().getName());

	public RiskFactorContinuousPrevalencesGroup(Shell shell,
			RiskFactorContinuousPrevalencesObject lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		EntityInDefaultDirNamePanel entityNameGroup = new EntityInDefaultDirNamePanel(
				theGroup, selectedNode, helpGroup);
		entityNameGroup.handlePlacementInContainer();
		// Set the selected item from the stored values in the xml
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) lotsOfData
				.get(XMLTagEntityEnum.DISTRIBUTIONTYPE.getElementName());
		WritableValue writableValue = (WritableValue) tuple.getValue();
		RiskFactorContinuousPrevalencesDropDownPanel dropDownGroup = new RiskFactorContinuousPrevalencesDropDownPanel(
				theGroup, writableValue);
		dropDownGroup.setHelpGroup(helpGroup);
		dropDownGroup.putNextInContainer(entityNameGroup.group, 30);
		RiskFactorContinuousPrevalencesParameterGroup parameterGroup = new RiskFactorContinuousPrevalencesParameterGroup(
				theGroup, lotsOfData, dataBindingContext, helpGroup,
				dropDownGroup.getDistributionTypeModifyListener());
		parameterGroup.handlePlacementInContainer(dropDownGroup.group);
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
