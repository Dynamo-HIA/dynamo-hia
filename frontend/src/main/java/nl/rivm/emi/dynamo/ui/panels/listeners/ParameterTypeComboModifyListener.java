package nl.rivm.emi.dynamo.ui.panels.listeners;

import java.util.HashSet;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

public class ParameterTypeComboModifyListener implements ModifyListener {

	/**
	 * The value in the model-object to update.
	 */
	@SuppressWarnings("rawtypes")
	WritableValue parameterTypeWritableValue;

	/**
	 * The labels in the user interface.
	 */
	HashSet<Label> registeredLabels = new HashSet<Label>();

	public ParameterTypeComboModifyListener(@SuppressWarnings("rawtypes") WritableValue parameterTypeWritableValue) {
		super();
		this.parameterTypeWritableValue = parameterTypeWritableValue;
	}

	public String registerLabel(Label label) {
		registeredLabels.add(label);
		return getCurrentValueAsLabelText();
	}

	public void unRegisterLabel(Label label) {
		registeredLabels.remove(label);
	}

	@SuppressWarnings("unchecked")
	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		if(parameterTypeWritableValue!= null){
			parameterTypeWritableValue.doSetValue(newText);
		}
		for (Label label : registeredLabels) {
			label.setText(newText);
		}
	}
	public String getCurrentValueAsLabelText(){
		return getCurrentValue();
	}
	public String getCurrentValue(){
		return (String) parameterTypeWritableValue.doGetValue();
	}

}
