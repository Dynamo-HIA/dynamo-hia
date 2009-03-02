package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.INumberOfYears;
import nl.rivm.emi.dynamo.data.types.atomic.NumberOfYearsType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class NumberOfYearsImpl extends NumberOfYearsType implements INumberOfYears {
	Log log = LogFactory.getLog(this.getClass().getName());
	Integer numberOfYears = null;

	WritableValue observableNumberOfYears = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private NumberOfYearsImpl() {

	}

	public NumberOfYearsImpl(boolean isObservable) {
		this.isObservable = isObservable;
	}

	@Override
	protected String streamValue() {
		Integer result = null;
		if (!isObservable) {
			result = numberOfYears;
		} else {
			result = ((Integer) observableNumberOfYears.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setNumberOfYears(0);
	}

	public Integer handle(ConfigurationNode node) throws ConfigurationException {
		Integer result = (Integer)super.handle(node);
		setNumberOfYears(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (numberOfYears != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableNumberOfYears.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

	public Integer getNumberOfYears() {
		Integer result = null;
		if (!isObservable) {
			result = numberOfYears;
		} else {
			result = ((Integer) observableNumberOfYears.doGetValue());
		}
		return result;
	}

	public void setNumberOfYears(Integer numberOfYears) {
		if (!isObservable) {
			this.numberOfYears = numberOfYears;
		} else {
			if (observableNumberOfYears == null) {
				observableNumberOfYears = new WritableValue(numberOfYears,
						Integer.class);
			} else {
				observableNumberOfYears.doSetValue(numberOfYears);
			}
		}
	}

	@Override
	public WritableValue getObservableValue() {
		return observableNumberOfYears;
	}

}
