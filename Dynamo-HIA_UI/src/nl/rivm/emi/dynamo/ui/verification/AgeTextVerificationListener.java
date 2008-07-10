package nl.rivm.emi.dynamo.ui.verification;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class AgeTextVerificationListener implements VerifyListener {

	public void verifyText(VerifyEvent arg0) {
		Text myText = (Text) arg0.widget;
		String currentContent = myText.getText();
		String candidateContent = currentContent.substring(0, arg0.start)
				+ arg0.text
				+ currentContent.substring(arg0.end, currentContent.length());
		// Be optimistic for once...
		arg0.doit = true;
		myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
		try {
			if (candidateContent.length() == 0) {
				myText.setBackground(new Color(null, 0xff, 0xff, 0xcc));
			} else {
				Integer candidateInteger = Integer.decode(candidateContent);
				int candidateInt = candidateInteger.intValue();
				if ((candidateInt < 0) || (candidateInt > 95)) {
					arg0.doit = false;
				}
			}
		} catch (Exception e) {
			arg0.doit = false;
		}
	}

}