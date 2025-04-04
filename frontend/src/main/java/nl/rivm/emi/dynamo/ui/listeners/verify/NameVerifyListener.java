package nl.rivm.emi.dynamo.ui.listeners.verify;

/**
 * Listener that can to be registered to a Text field in the user interface.
 * It will then be called each time a character is typed, thereby checking 
 * whether the input characters are going anywhere.
 */

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.global.DataAndFileContainer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class NameVerifyListener extends AbstractNonSAPVerifyListener {

	public NameVerifyListener(DataAndFileContainer encompassingModal) {
		super(encompassingModal);
	}

	public void verifyText(VerifyEvent arg0) {
		Text myText = (Text) arg0.widget;
		String currentContent = myText.getText();
		String candidateContent = currentContent.substring(0, arg0.start)
				+ arg0.text
				+ currentContent.substring(arg0.end, currentContent.length());
		arg0.doit = true;
		myText.setBackground(new Color(null, 0xff, 0xff, 0xff)); // White
		if (candidateContent.length() == 0) {
			myText.setBackground(new Color(null, 0xff, 0xff, 0xcc)); // Yellow
		} else {
			if (!(((Name) XMLTagEntityEnum.NAME.getTheType()).matchPattern
					.matcher(candidateContent)).matches()) {
				arg0.doit = false;
				myText.setBackground(new Color(null, 0xff, 0xbb, 0xbb));
			}
		}
		if (arg0.doit) {
			encompassingModal.setChanged(true);
		}
	}
}