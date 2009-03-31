package nl.rivm.emi.dynamo.ui.panels;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
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
		entityNameGroup.handlePlacementInContainer();
		String entityLabel = UNIT;
		selectableExcessMortalityPropertiesMap.put(MEDIAN_SURVIVAL, 
				MEDIAN_SURVIVAL);
		selectableExcessMortalityPropertiesMap.put(RATE, RATE);
		EntityDropDownPanel dropDownGroup = new EntityDropDownPanel(theGroup, entityLabel,
				selectableExcessMortalityPropertiesMap, null);
//		dropDownGroup.putFirstInContainer(30); 
//		dropDownGroup.putMiddleInContainer(entityNameGroup.group, 30);
		dropDownGroup.putNextInContainer(entityNameGroup.group, 30);
		ExcessMortalityParameterGroup parameterGroup = new ExcessMortalityParameterGroup(
				theGroup, lotsOfData, dataBindingContext, 
				helpGroup, dropDownGroup.getUnitTypeModifyListener() );
		parameterGroup.handlePlacementInContainer(dropDownGroup.group);

		// Set the selected item from the stored values in the xml
		AtomicTypeObjectTuple tuple = (AtomicTypeObjectTuple) lotsOfData.get(XMLTagEntityEnum.UNITTYPE.getElementName());
		WritableValue writableValue = (WritableValue) tuple.getValue();
		String stringValue = (String) writableValue.doGetValue();
		String[] items = (dropDownGroup.getDropDown()).getItems();
		String[] newItems = new String[items.length+1];
		int count = 0;
		for(String item:items){
			newItems[count] = items[count];
			if(item.equals(stringValue)){
				break;
			}
			count++;
		}
		// Nothing found.
		if(count == items.length){
			newItems[count] = stringValue;
			(dropDownGroup.getDropDown()).setItems(newItems);
			(dropDownGroup.getDropDown()).select(newItems.length-1);
		} else {
			(dropDownGroup.getDropDown()).select(count);
		}
		
		
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
