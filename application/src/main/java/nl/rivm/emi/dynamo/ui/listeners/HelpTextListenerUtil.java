package nl.rivm.emi.dynamo.ui.listeners;

import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

public class HelpTextListenerUtil {
	static public void addHelpTextListeners(Button button) {
		button.addFocusListener(new ButtonFocusListener(button));
		button.addMouseTrackListener(new ButtonMouseTrackListener(button));
	}
	
	static public void addHelpTextListeners(Control control, AtomicTypeBase<?> myType) {
		control.addFocusListener(new TypedFocusListener(myType));
		control.addMouseTrackListener(new TypedMouseTrackListener(myType));
	}

	static public void addHelpTextListeners(Control control, String myText) {
		control.addFocusListener(new TextedFocusListener(myText));
		control.addMouseTrackListener(new TextedMouseTrackListener(myText));
	}
}
