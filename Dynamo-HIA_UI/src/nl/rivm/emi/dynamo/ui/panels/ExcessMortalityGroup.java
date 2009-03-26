package nl.rivm.emi.dynamo.ui.panels;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

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


public class ExcessMortalityGroup {
	private static final String UNIT = "Unit";
	private static final String MEDIAN_SURVIVAL = "Median Survival";
	private static final String RATE = "Rate";
	private Group theGroup;
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	public ExcessMortalityGroup(Shell shell, ExcessMortalityObject lotsOfData,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws DynamoConfigurationException {
		Map<String, String> selectableExcessMortalityPropertiesMap = 
			new LinkedHashMap<String, String>();		
		theGroup = new Group(shell, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		theGroup.setLayout(formLayout);
		EntityInDefaultDirNamePanel entityNameGroup = 
			new EntityInDefaultDirNamePanel(theGroup,
				selectedNode, helpGroup);
		String entityLabel = UNIT;
		selectableExcessMortalityPropertiesMap.put(MEDIAN_SURVIVAL, 
				MEDIAN_SURVIVAL);
		selectableExcessMortalityPropertiesMap.put(RATE, RATE);
		EntityDropDownPanel dropDownGroup = new EntityDropDownPanel(theGroup, entityLabel,
				selectableExcessMortalityPropertiesMap);
		
		dropDownGroup.putFirstInContainer(30); 
		dropDownGroup.putMiddleInContainer(entityNameGroup.group, 30);
		
		ExcessMortalityParameterGroup parameterGroup = new ExcessMortalityParameterGroup(
				theGroup, lotsOfData, dataBindingContext, 
				helpGroup, dropDownGroup.getUnitType());
		parameterGroup.handlePlacementInContainer(entityNameGroup.group);
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
