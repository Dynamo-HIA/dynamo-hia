package nl.rivm.emi.dynamo.ui.listeners.verify;

import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.events.VerifyListener;

abstract public class AbstractNonSAPVerifyListener implements VerifyListener {
	DataAndFileContainer encompassingModal = null;

	public AbstractNonSAPVerifyListener(DataAndFileContainer encompassingModal) {
		super();
		this.encompassingModal = encompassingModal;
	}

}