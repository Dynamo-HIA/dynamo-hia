package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IMaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.MaxAgeType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class MaxAgeImpl extends MaxAgeType implements IMaxAge {
	Log log = LogFactory.getLog(this.getClass().getName());
	Integer maxAge = null;

	WritableValue observableMaxAge = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private MaxAgeImpl() {

	}

	public MaxAgeImpl(boolean isObservable) {
		this.isObservable = isObservable;
	}

	public void setMaxAge(Integer maxAge) {
		if (!isObservable) {
			this.maxAge = maxAge;
		} else {
			if (observableMaxAge == null) {
				observableMaxAge = new WritableValue(maxAge,
						Integer.class);
			} else {
				observableMaxAge.doSetValue(maxAge);
			}
		}
	}

	@Override
	protected String streamValue() {
		Integer result = null;
		if (!isObservable) {
			result = maxAge;
		} else {
			result = ((Integer) observableMaxAge.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setMaxAge(0);
	}

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		Integer result = (Integer)super.handle(node);
		setMaxAge(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (maxAge != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableMaxAge.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	public Integer getMaxAge() {
		Integer result = null;
		if (!isObservable) {
			result = maxAge;
		} else {
			result = ((Integer) observableMaxAge.doGetValue());
		}
		return result;
	}

	@Override
	public WritableValue getObservableValue() {
		return observableMaxAge;
	}
}
