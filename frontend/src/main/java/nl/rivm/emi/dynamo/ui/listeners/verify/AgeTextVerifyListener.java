package nl.rivm.emi.dynamo.ui.listeners.verify;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

import nl.rivm.emi.dynamo.global.DataAndFileContainer;

public class AgeTextVerifyListener extends AbstractNonSAPVerifyListener {

	public AgeTextVerifyListener(DataAndFileContainer encompassingModal) {
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
			Integer candidateInteger = /*
										 * AtomicTypesSingleton.getInstance().get
										 * ("age")
										 */Integer.valueOf(candidateContent);
			if (candidateInteger == null) {
				arg0.doit = false;
			}
		}
		if (arg0.doit) {
			encompassingModal.setChanged(true);
		}
	}
}