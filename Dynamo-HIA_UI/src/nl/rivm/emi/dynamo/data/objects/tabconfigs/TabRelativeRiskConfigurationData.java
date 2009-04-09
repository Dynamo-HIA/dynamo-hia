package nl.rivm.emi.dynamo.data.objects.tabconfigs;

// TODO(mondeelr)
import java.util.ArrayList;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.interfaces.ITabRelativeRisksConfiguration;
import nl.rivm.emi.dynamo.data.interfaces.ITabStoreConfiguration;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRFrom;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRTo;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.util.AtomicTypeObjectTuple;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class TabRelativeRiskConfigurationData implements
		ITabRelativeRisksConfiguration, ITabStoreConfiguration {
	Log log = LogFactory.getLog(this.getClass().getName());

	
	Integer index;
	String from;
	String to;
	String dataFileName;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public void initialize(Object index, ArrayList<AtomicTypeObjectTuple> list) {
		if (index instanceof Integer) {
			this.index = (Integer) index;
			for (int count = 0; count < list.size(); count++) {
				AtomicTypeObjectTuple tuple = list.get(count);
				XMLTagEntity type = tuple.getType();
				if (type instanceof IsRRFrom) {
					from = (String) ((WritableValue) tuple.getValue())
							.doGetValue();
				} else {
					if (type instanceof IsRRTo) {
						to = (String) ((WritableValue) tuple.getValue())
								.doGetValue();
					} else {
						if (type instanceof IsRRFileName) {
							dataFileName = (String) ((WritableValue) tuple.getValue())
									.doGetValue();
						} else {
							log.fatal("Unexpected type \""
									+ type.getXMLElementName()
									+ "\" in getDiseasesConfigurations()");
						}
					}
				}
			}

		} else {
			log.fatal("The name should be a String.");
		}
	}

	public TypedHashMap<? extends XMLTagEntity> putInTypedHashMap(
			TypedHashMap<? extends XMLTagEntity> theMap) {
		ArrayList<AtomicTypeObjectTuple> diseaseModelData = new ArrayList<AtomicTypeObjectTuple>();
		AtomicTypeObjectTuple tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.ISRRFROM.getTheType(), new WritableValue(
						getFrom(), String.class));
		diseaseModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.ISRRTO.getTheType(), new WritableValue(
						getTo(), String.class));
		diseaseModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(
				XMLTagEntityEnum.ISRRFILENAME.getTheType(), new WritableValue(
						getDataFileName(), String.class));
		diseaseModelData.add(tuple);
		Integer newIndex = theMap.size();
		theMap.put(newIndex, diseaseModelData);
		return theMap;
	}
}
