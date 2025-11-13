package nl.rivm.emi.dynamo.ui.panels;

import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.listeners.HelpTextListenerUtil;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.DistributionTypeComboModifyListener;
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

public class RiskFactorContinuousPrevalencesDropDownPanel {

	Log log = LogFactory.getLog(this.getClass().getName());

	private static final String LABEL = "Distribution";
	private static final Set<String> choices = new LinkedHashSet<String>();

	public Group group;
	private Label label;
	private Combo dropDown;
	@SuppressWarnings("unused")
	private HelpGroup theHelpGroup;
	private DropDownPropertiesSet selectableDistributionTypePropertiesSet;
	private DistributionTypeComboModifyListener distributionTypeModifyListener;
	private int selectedIndex;

	public RiskFactorContinuousPrevalencesDropDownPanel(Composite parent, @SuppressWarnings("rawtypes") WritableValue writableValue) {
		choices.add("Normal");
		choices.add("Log normal");
		group = new Group(parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		group.setLayout(formLayout);
		label = new Label(group, SWT.LEFT);
		label.setText(LABEL + ":");
		layoutLabel();
		dropDown = new Combo(group, SWT.DROP_DOWN|SWT.READ_ONLY);
		createDropDownPropertiesSet();
		this.fill(selectableDistributionTypePropertiesSet);
		this.distributionTypeModifyListener = new DistributionTypeComboModifyListener(
				writableValue);
		dropDown.addModifyListener(distributionTypeModifyListener);
		String currentValue = distributionTypeModifyListener.getCurrentValue();
		int currentIndex = selectableDistributionTypePropertiesSet
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
	
	private void createDropDownPropertiesSet() {
		selectableDistributionTypePropertiesSet = new DropDownPropertiesSet();
		for(String choice:choices){
		selectableDistributionTypePropertiesSet.add(choice);
		}
	}

	public String getDistributionType() {
		return (String) selectableDistributionTypePropertiesSet
				.getSelectedString(this.selectedIndex);
	}

	public void setHelpGroup(HelpGroup helpGroup) {
		theHelpGroup = helpGroup;
//		dropDown.addFocusListener(new TypedFocusListener(XMLTagEntityEnum.DISTRIBUTIONTYPE.getTheType(), theHelpGroup));
		HelpTextListenerUtil.addHelpTextListeners(dropDown, (AtomicTypeBase<?>) XMLTagEntityEnum.DISTRIBUTIONTYPE.getTheType());
}

	/**
	 * 
	 * Place the first group in the container
	 * 
	 * @param height
	 */

	public DistributionTypeComboModifyListener getDistributionTypeModifyListener() {
		return distributionTypeModifyListener;
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
