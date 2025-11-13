package nl.rivm.emi.dynamo.ui.panels.listeners;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

public class DurationClassIndexComboModifyListener implements ModifyListener {

	/**
	 * The value in the model-object to update.
	 */
	@SuppressWarnings("rawtypes")
	WritableValue durationClassIndexWritableValue;

	public DurationClassIndexComboModifyListener(
			@SuppressWarnings("rawtypes") WritableValue durationClassIndexWritableValue) {
		super();
		this.durationClassIndexWritableValue = durationClassIndexWritableValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		if(durationClassIndexWritableValue!= null){
			durationClassIndexWritableValue.doSetValue( Integer.decode(newText));
		}
	}

	public String getCurrentValue(){
		return (String) durationClassIndexWritableValue.doGetValue().toString();
	}

}
