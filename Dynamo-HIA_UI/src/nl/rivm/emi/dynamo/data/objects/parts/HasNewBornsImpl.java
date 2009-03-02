package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IHasNewborns;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewbornsType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class HasNewBornsImpl extends HasNewbornsType implements IHasNewborns {
	Log log = LogFactory.getLog(this.getClass().getName());
	Boolean hasNewBorns = null;

	WritableValue observableHasNewBorns = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private HasNewBornsImpl() {

	}

	public HasNewBornsImpl(boolean isObservable) {
		this.isObservable = isObservable;
	}

	public boolean isHasNewborns() {
		boolean result = false;
		if (!isObservable) {
			result = hasNewBorns.booleanValue();
		} else {
			result = ((Boolean) observableHasNewBorns.doGetValue())
					.booleanValue();
		}
		return result;
	}

	public void setHasNewborns(boolean newborns) {
		if (!isObservable) {
			hasNewBorns = new Boolean(newborns);
		} else {
			observableHasNewBorns = new WritableValue(new Boolean(newborns),
					Boolean.class);
		}
	}

	@Override
	public WritableValue getObservableValue() {
		return observableHasNewBorns;
	}
	@Override
	protected String streamValue() {
		Boolean result = false;
		if (!isObservable) {
			result = hasNewBorns;
		} else {
			result = ((Boolean) observableHasNewBorns.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setHasNewborns(false);
	}

	public Boolean handle(ConfigurationNode node) throws ConfigurationException {
		Boolean result = super.handle(node);
		setHasNewborns(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (hasNewBorns != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableHasNewBorns.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

}
