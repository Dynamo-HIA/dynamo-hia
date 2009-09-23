package nl.rivm.emi.dynamo.ui.listeners.verify;

/**
 * Listener that can to be registered to a Text field in the user interface.
 * It will then be called each time a character is typed, thereby checking 
 * whether the input characters are going anywhere.
 */

import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class UniqueScenarioNameVerifyListener extends
		AbstractNonSAPVerifyListener {

	public UniqueScenarioNameVerifyListener(
			DataAndFileContainer encompassingModal) {
		super(encompassingModal);
	}

	public void verifyText(VerifyEvent arg0) {
		Text myText = (Text) arg0.widget;
		String currentContent = myText.getText();
		String candidateContent = currentContent.substring(0, arg0.start)
				+ arg0.text
				+ currentContent.substring(arg0.end, currentContent.length());
		// Positive thinking, AOK.
		arg0.doit = true;
		myText.setBackground(new Color(null, 0xff, 0xff, 0xff)); // White
		if (candidateContent.length() == 0) {
			myText.setBackground(new Color(null, 0xff, 0xff, 0xcc)); // Yellow
		} else {
			// Test for unacceptable gibberish.
			if (!(((Name) XMLTagEntityEnum.NAME.getTheType()).matchPattern
					.matcher(candidateContent)).matches()) {
				arg0.doit = false;
				myText.setBackground(new Color(null, 0xff, 0xbb, 0xbb));
			} else {
				// Test for uniqueness.
				DynamoSimulationObject modelObject = (DynamoSimulationObject) encompassingModal
						.getData();
				Set<String> scenarioNames = modelObject
						.getScenarioConfigurations().keySet();
				if (scenarioNames.contains(candidateContent)) {
					arg0.doit = false;
					myText.setBackground(new Color(null, 0xff, 0xbb, 0xbb));
				}
			}
		}
		if (arg0.doit) {
			encompassingModal.setChanged(true);
		}
	}
}