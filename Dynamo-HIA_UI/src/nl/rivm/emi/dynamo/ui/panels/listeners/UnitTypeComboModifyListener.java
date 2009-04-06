package nl.rivm.emi.dynamo.ui.panels.listeners;

import java.util.HashSet;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

public class UnitTypeComboModifyListener implements ModifyListener {

	/**
	 * The value in the model-object to update.
	 */
	WritableValue unitTypeWritableValue;

	/**
	 * The labels in the user
	 */
	HashSet<Label> registeredLabels = new HashSet<Label>();

	public UnitTypeComboModifyListener(WritableValue unitTypeWritableValue) {
		super();
		this.unitTypeWritableValue = unitTypeWritableValue;
	}

	public String registerLabel(Label label) {
		registeredLabels.add(label);
		return getCurrentValueAsLabelText();
	}

	public void unRegisterLabel(Label label) {
		registeredLabels.remove(label);
	}

	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		if(unitTypeWritableValue!= null){
			unitTypeWritableValue.doSetValue(newText);
		}
		for (Label label : registeredLabels) {
			label.setText("Unit: " + newText);
		}
	}
	public String getCurrentValueAsLabelText(){
		return "Unit: " + getCurrentValue();
	}
	public String getCurrentValue(){
		return (String) unitTypeWritableValue.doGetValue();
	}

}
