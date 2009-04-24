package nl.rivm.emi.dynamo.ui.panels.listeners;

import java.util.HashSet;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

public class DistributionTypeComboModifyListener implements ModifyListener {

	/**
	 * The value in the model-object to update.
	 */
	WritableValue distributionTypeWritableValue;

	/**
	 * The labels in the user
	 */
	HashSet<Label> registeredLabels = new HashSet<Label>();

	public DistributionTypeComboModifyListener(WritableValue distributionTypeWritableValue) {
		super();
		this.distributionTypeWritableValue = distributionTypeWritableValue;
	}

	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		if(distributionTypeWritableValue!= null){
			distributionTypeWritableValue.doSetValue(newText);
		}
	}

	public String getCurrentValue(){
		return (String) distributionTypeWritableValue.doGetValue();
	}

}
