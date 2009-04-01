package nl.rivm.emi.dynamo.ui.panels.listeners;

import java.util.HashSet;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

public class UnitTypeComboModifyListener implements ModifyListener {
	HashSet<Label> registeredLabels = new HashSet<Label>();

	public void registerLabel(Label label) {
		registeredLabels.add(label);
	}

	public void unRegisterLabel(Label label) {
		registeredLabels.remove(label);
	}

	@Override
	public void modifyText(ModifyEvent event) {
		Combo myCombo = (Combo) event.widget;
		String newText = myCombo.getText();
		for (Label label : registeredLabels) {
			label.setText("Unit: " + newText);
		}
	}

}
