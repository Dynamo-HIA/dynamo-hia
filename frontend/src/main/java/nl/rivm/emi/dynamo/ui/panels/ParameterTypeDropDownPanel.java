package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.objects.ExcessMortalityObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.ParameterTypeComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ParameterTypeDropDownPanel {

	Log log = LogFactory.getLog(this.getClass().getName());


	public Group group;
	private Label label;
	private Combo dropDown;
	@SuppressWarnings("unused")
	private HelpGroup theHelpGroup;
	private DropDownPropertiesSet selectableParameterTypePropertiesSet;
	private ParameterTypeComboModifyListener parameterTypeModifyListener;
	private int selectedIndex;

	public ParameterTypeDropDownPanel(Composite parent, @SuppressWarnings("rawtypes") WritableValue writableValue) {
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		label = new Label(group, SWT.LEFT);
		label.setText( ExcessMortalityObject.ParameterTypeHelperClass.CHOOSE + ":");
		layoutLabel();
		dropDown = new Combo(group, SWT.DROP_DOWN|SWT.READ_ONLY);
		createDropDownPropertiesMap();
		this.fill(selectableParameterTypePropertiesSet);
		this.parameterTypeModifyListener = new ParameterTypeComboModifyListener(
				writableValue);
		dropDown.addModifyListener(parameterTypeModifyListener);
		String currentValue = parameterTypeModifyListener.getCurrentValue();
		int currentIndex = selectableParameterTypePropertiesSet
				.getSelectedIndex(currentValue);
		dropDown.select(currentIndex);
		layoutDropDown(label);

	}

	public void fill(DropDownPropertiesSet set) {
		int index = 0;
		for (String item : set) {
			dropDown.add(item, index);
			index++;
		}
	}	
	
	private void createDropDownPropertiesMap() {
		selectableParameterTypePropertiesSet = new DropDownPropertiesSet();
		selectableParameterTypePropertiesSet.add(ExcessMortalityObject.ParameterTypeHelperClass.ACUTELY_FATAL);
		selectableParameterTypePropertiesSet.add(ExcessMortalityObject.ParameterTypeHelperClass.CURED_FRACTION);
	}

	public String getParameterType() {
		return (String) selectableParameterTypePropertiesSet
				.getSelectedString(this.selectedIndex);
	}

	public void setHelpGroup(HelpGroup helpGroup) {
		theHelpGroup = helpGroup;
//		dropDown.addFocusListener(new TypedFocusListener(XMLTagEntityEnum.UNITTYPE.getTheType(), theHelpGroup));
		HelpTextListenerUtil.addHelpTextListeners(dropDown, (AtomicTypeBase<?>) XMLTagEntityEnum.PARAMETERTYPE.getTheType());
	}

	/**
	 * 
	 * Place the first group in the container
	 * 
	 * @param height
	 */

	public ParameterTypeComboModifyListener getParameterTypeModifyListener() {
		return parameterTypeModifyListener;
	}

	public Combo getDropDown() {
		return dropDown;
	}


	/**
	 * Layout stuff.
	 */
	public void handlePlacementInContainer() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		group.setLayoutData(formData);
	}

	public void putFirstInContainer(int height) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(0, 5 + height);
		group.setLayoutData(formData);
	}

	public void putMiddleInContainer(Group topNeighbour, int height) {
		// TODO Auto-generated method stub
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(6, 5 + height);
		group.setLayoutData(formData);
	}

	public void putNextInContainer(Group topNeighbour, int height) {
		// TODO Auto-generated method stub
		FormData formData = new FormData();
		formData.top = new FormAttachment(topNeighbour, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		// formData.bottom = new FormAttachment(topNeighbour, 5 + height);
		group.setLayoutData(formData);
	}

	private void layoutLabel() {
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0, 2);
		// labelFormData.right = new FormAttachment(100, -5);
		labelFormData.top = new FormAttachment(0, 2);
		labelFormData.bottom = new FormAttachment(100, -2);
		label.setLayoutData(labelFormData);
	}

	private void layoutDropDown(Label label) {
		FormData comboFormData = new FormData();
		comboFormData.left = new FormAttachment(label, 5);
		comboFormData.right = new FormAttachment(100, -5);
		comboFormData.top = new FormAttachment(0, 2);
		comboFormData.bottom = new FormAttachment(100, -2);
		dropDown.setLayoutData(comboFormData);
	}
}
