package nl.rivm.emi.dynamo.ui.listeners.verify;

import nl.rivm.emi.dynamo.ui.main.base.DataAndFileContainer;

import org.eclipse.swt.events.VerifyListener;

/**
 * @author mondeelr <br/>
 *         Layer that provides the context for setting the changed status of the
 *         processed file when a change verifies OK.
 */
abstract public class AbstractNonSAPVerifyListener implements VerifyListener {
/**
 * Object that contains the changed functionality.
 */
	DataAndFileContainer encompassingModal = null;

	public AbstractNonSAPVerifyListener(DataAndFileContainer encompassingModal) {
		super();
		this.encompassingModal = encompassingModal;
	}

}