package nl.rivm.emi.dynamo.ui.listeners.verify;

import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractString;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.ui.main.base.DataAndFileContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class AbstractStringVerifyListener extends AbstractNonSAPVerifyListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	private AtomicTypeBase<?> type = null;

	public AbstractStringVerifyListener(DataAndFileContainer myModal,
			AtomicTypeBase<?> typeParam) {
		super(myModal);
		this.type = typeParam;
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
			if (!(((AbstractString) this.type).matchPattern
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
