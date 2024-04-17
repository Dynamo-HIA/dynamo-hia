package nl.rivm.emi.dynamo.ui.panels.listeners;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

public class PopulationFileNameComboModifyListener implements ModifyListener {

	/**
	 * The value in the model-object to update.
	 */
	WritableValue populationFileNameWritableValue;

	public PopulationFileNameComboModifyListener(WritableValue populationFileNameWritableValue) {
		super();
		this.populationFileNameWritableValue = populationFileNameWritableValue;
	}

	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		if(populationFileNameWritableValue!= null){
			populationFileNameWritableValue.doSetValue(newText);
		}
	}

	public String getCurrentValue(){
		return (String) populationFileNameWritableValue.doGetValue();
	}
}
