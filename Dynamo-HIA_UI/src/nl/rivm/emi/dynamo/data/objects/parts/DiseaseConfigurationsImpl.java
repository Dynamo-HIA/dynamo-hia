package nl.rivm.emi.dynamo.data.objects.parts;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.IDiseaseConfigurations;
import nl.rivm.emi.dynamo.data.interfaces.IPopFileName;
import nl.rivm.emi.dynamo.data.interfaces.IResultType;
import nl.rivm.emi.dynamo.data.types.atomic.PopFileNameType;
import nl.rivm.emi.dynamo.data.types.atomic.ResultTypeType;
import nl.rivm.emi.dynamo.data.types.interfaces.IXMLHandlingLayer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class DiseaseConfigurationsImpl extends PopFileNameType implements
		IDiseaseConfigurations, IXMLHandlingLayer<String> {
	Log log = LogFactory.getLog(this.getClass().getName());
	Boolean isObservable = false; // TODO Bogus bugfix.
	TypedHashMap<IDiseaseConfiguration> diseaseConfigurations = new TypedHashMap<IDiseaseConfiguration>(
			new DiseaseConfigurationData());

	/**
	 * Block default construction.
	 */
	@SuppressWarnings("unused")
	private DiseaseConfigurationsImpl() {
		super("");
	}

	public DiseaseConfigurationsImpl(boolean isObservable, String baseDirectory) {
		super(baseDirectory);
		this.isObservable = isObservable;
	}

	@Override
	public WritableValue getObservableValue() {
		return null;
	}

	@Override
	protected String streamValue() {
		String result = "";
		if (!isObservable) {
			result = "nix";
		} else {
			result = ((String) getObservableValue().doGetValue());
		}
		return result;
	}

	@Override
	public void setDefault() {
	}

	public String handle(ConfigurationNode node) throws ConfigurationException {
		String result = super.handle(node);
		// setPopFileName(result);
		return result;
	}

	public boolean isConfigurationOK() {
		boolean result = false;
		if (!isObservable) {
			if (isObservable != null) {
				result = true;
			} else {
				log.error("Non observable \"" + getXMLElementName()
						+ "\" contains a null value.");
			}
		} else {
//			if (observablePopFileName.doGetValue() != null) {
//				result = true;
//			} else {
//				log.error("Observable \"" + getXMLElementName()
//						+ "\" contains a null value.");
//			}
		}
		return result;
	}

	public DiseaseConfigurationData getDiseaseConfigurationData(String name) {
//		DiseaseConfigurationData theData = diseaseConfigurations
//				.get((Object) name);
		return null;
	}

	public DiseaseConfigurationData setDiseaseConfiguration(
			DiseaseConfigurationData theData) {
		// TODO Auto-generated method stub
		return null;
	}

}
