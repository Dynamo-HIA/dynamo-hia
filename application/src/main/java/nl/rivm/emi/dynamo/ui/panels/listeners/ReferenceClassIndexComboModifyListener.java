package nl.rivm.emi.dynamo.ui.panels.listeners;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;

public class ReferenceClassIndexComboModifyListener implements ModifyListener {

	/**
	 * The value in the model-object to update.
	 */
	WritableValue referenceClassIndexWritableValue;

	public ReferenceClassIndexComboModifyListener(
			WritableValue referenceClassIndexWritableValue) {
		super();
		this.referenceClassIndexWritableValue = referenceClassIndexWritableValue;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		if(referenceClassIndexWritableValue!= null){
			referenceClassIndexWritableValue.doSetValue( Integer.decode(newText));
		}
	}

	public String getCurrentValue(){
		return (String) referenceClassIndexWritableValue.doGetValue().toString();
	}

}
