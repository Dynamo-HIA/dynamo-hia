package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.interfaces.IPopFileName;
import nl.rivm.emi.dynamo.data.interfaces.IResultType;
import nl.rivm.emi.dynamo.data.types.atomic.PopFileNameType;
import nl.rivm.emi.dynamo.data.types.atomic.ResultTypeType;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class PopFileNameImpl extends PopFileNameType implements IPopFileName {
	Log log = LogFactory.getLog(this.getClass().getName());
	String popFileName = null;

	WritableValue observablePopFileName = null;

	boolean isObservable = false;

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private PopFileNameImpl() {
		super("");
	}

	public PopFileNameImpl(boolean isObservable, String baseDirectory) {
		super(baseDirectory);
		this.isObservable = isObservable;
	}

	public String getPopFileName() {
		String result = null;
		if (!isObservable) {
			result = popFileName;
		} else {
			result = ((String) observablePopFileName.doGetValue());
		}
		return result;
	}

	public void setPopFileName(String popFileName) {
		if (!isObservable) {
			this.popFileName = popFileName;
		} else {
			if (observablePopFileName == null) {
				observablePopFileName = new WritableValue(popFileName,
						String.class);
			} else {
				observablePopFileName.doSetValue(popFileName);
			}
		}
	}

	@Override
	public WritableValue getObservableValue() {
		return observablePopFileName;
	}

	@Override
	protected String streamValue() {
		String result = "";
		if (!isObservable) {
			result = popFileName;
		} else {
			result = ((String) observablePopFileName.doGetValue());
		}
		return result;
	}

	@Override
	public void setDefault() {
		setPopFileName(baseDirectory);
	}

	public String handle(ConfigurationNode node) throws ConfigurationException {
		String result = super.handle(node);
		setPopFileName(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (popFileName != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
			if (observablePopFileName.doGetValue() != null) {
				result = true;
			} else {
				log.error("Observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		}
		return result;
	}

}
