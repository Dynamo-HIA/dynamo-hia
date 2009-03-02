package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IResultType;
import nl.rivm.emi.dynamo.data.types.atomic.ResultTypeType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class ResultTypeImpl extends ResultTypeType implements IResultType {
	Log log = LogFactory.getLog(this.getClass().getName());
	String resultType = null;

	WritableValue observableResultType = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private ResultTypeImpl() {
		super(new String[] { "" });
	}

	public ResultTypeImpl(boolean isObservable, String[] possibleChoices) {
		super(possibleChoices);
		this.isObservable = isObservable;
	}

	public String getResultType() {
		String result = null;
		if (!isObservable) {
			result = resultType;
		} else {
			result = ((String) observableResultType.doGetValue());
		}
		return result;
	}

	public void setResultType(String resultType) {
		if (!isObservable) {
			this.resultType = resultType;
		} else {
			if (observableResultType == null) {
				observableResultType = new WritableValue(resultType,
						String.class);
			} else {
				observableResultType.doSetValue(resultType);
			}
		}
	}

	@Override
	public WritableValue getObservableValue() {
		return observableResultType;
	}

	@Override
	protected String streamValue() {
		String result = "";
		if (!isObservable) {
			result = resultType;
		} else {
			result = ((String) observableResultType.doGetValue());
		}
		return result.toString();
	}

	@Override
	public void setDefault() {
		setResultType(possibleChoices[0]);
	}

	public String handle(ConfigurationNode node) throws ConfigurationException {
		String result = super.handle(node);
		setResultType(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (resultType != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observableResultType.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

}
