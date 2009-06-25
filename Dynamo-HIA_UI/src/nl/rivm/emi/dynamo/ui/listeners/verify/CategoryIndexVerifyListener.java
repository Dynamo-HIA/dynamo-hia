package nl.rivm.emi.dynamo.ui.listeners.verify;

import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.XMLTagEntitySingleton;
import nl.rivm.emi.dynamo.data.types.atomic.DynamoClass;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.atomic.base.AbstractClassIndex;
import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;
import nl.rivm.emi.dynamo.data.types.atomic.base.NumberRangeTypeBase;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class CategoryIndexVerifyListener extends AbstractNonSAPVerifyListener {
	Log log = LogFactory.getLog(this.getClass().getName());
	NumberRangeTypeBase<Integer> myAtomicType = null;

	public CategoryIndexVerifyListener(DataAndFileContainer myModal,
			AtomicTypeBase<Integer> myAtomicType) {
		super(myModal);
		if (myAtomicType instanceof NumberRangeTypeBase) {
			this.myAtomicType = (NumberRangeTypeBase<Integer>) myAtomicType;
		}
	}

	public void verifyText(VerifyEvent arg0) {
		Text myText = (Text) arg0.widget;
		String currentContent = myText.getText();
		String candidateContent = currentContent.substring(0, arg0.start)
				+ arg0.text
				+ currentContent.substring(arg0.end, currentContent.length());
		log.debug("VerifyEvent with current content: " + currentContent
				+ " , candidate content: " + candidateContent);
		arg0.doit = false;
		myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
		try {
			if (candidateContent.length() == 0) {
				myText.setBackground(new Color(null, 0xff, 0xff, 0xcc));
				arg0.doit = true;
			} else {
				if ((((Index) XMLTagEntityEnum.INDEX.getTheType()).matchPattern
						.matcher(candidateContent)).matches()) {
					Integer candidateInteger = Integer
							.valueOf(candidateContent);
					if (myAtomicType.inRange(candidateInteger)) {
						arg0.doit = true;
						myText.setBackground(new Color(null, 0xff, 0xff, 0xff));
					}
				} else {
					arg0.doit = false;
					myText.setBackground(new Color(null, 0xff, 0xbb, 0xbb));
				}
			}
			log.debug("verifyText, normal exit with doIt=" + arg0.doit);
		} catch (Exception e) {
			arg0.doit = false;
			log.debug("verifyText, exception exit with doIt=" + arg0.doit);
		} finally {
			if (arg0.doit) {
				encompassingModal.setChanged(true);
			}
		}
	}
}