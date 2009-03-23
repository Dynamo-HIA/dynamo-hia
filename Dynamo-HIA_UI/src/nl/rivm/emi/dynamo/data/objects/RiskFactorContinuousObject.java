package nl.rivm.emi.dynamo.data.objects;

/**
 * The putters are designed to insert Observables only!!!!!!
 */
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.interfaces.IReferenceValue;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class RiskFactorContinuousObject extends
		GroupConfigurationObjectServiceLayer implements IReferenceValue {
	private static final long serialVersionUID = -3767037849437274799L;

	Log log = LogFactory.getLog(this.getClass().getName());

	public RiskFactorContinuousObject(LinkedHashMap<String, Object> content) {
		super();
		super.putAll(content);
	}

	public Float getReferenceValue() {
		return getSingleRootChildFloatValue(XMLTagEntityEnum.REFERENCEVALUE
				.getElementName());
	}

	public WritableValue getObservableReferenceValue() {
		return getSingleRootChildWritableValue(XMLTagEntityEnum.REFERENCEVALUE
				.getElementName());
	}

	public Object putReferenceValue(Float value) {
		return putSingleRootChildFloatValue(XMLTagEntityEnum.REFERENCEVALUE
				.getElementName(), value);
	}
}
