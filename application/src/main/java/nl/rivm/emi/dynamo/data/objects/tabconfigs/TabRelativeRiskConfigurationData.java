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

	public TabRelativeRiskConfigurationData() {
		super();
		index = -1;
	}

	public String report(){
		StringBuffer resBuffer = new StringBuffer();
		resBuffer.append("Index: " + index + " from: " + from + " to: " + to + " file: " + dataFileName);
		return resBuffer.toString();
	}
	
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

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
							dataFileName = (String) ((WritableValue) tuple
									.getValue()).doGetValue();
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
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.ISRRTO.getTheType(),
				new WritableValue(getTo(), String.class));
		diseaseModelData.add(tuple);
		tuple = new AtomicTypeObjectTuple(XMLTagEntityEnum.ISRRFILENAME
				.getTheType(), new WritableValue(getDataFileName(),
				String.class));
		diseaseModelData.add(tuple);
		Integer newIndex = theMap.size();
		theMap.put(newIndex, diseaseModelData);
		return theMap;
	}

	public String compareReport(
			TabRelativeRiskConfigurationData newConfiguration) {
		StringBuffer reportBuffer = new StringBuffer();
		if (newConfiguration != null) {
			if ((this.index != null)&&(this.index != newConfiguration.getIndex())) {
				reportBuffer.append("Index changed from: \"" + this.index
						+ "\" to \"" + newConfiguration.getIndex() + "\n");
			}
			if((this.from!=null)&& (!this.from.equals(newConfiguration.getFrom()))) {
				reportBuffer.append("From changed from: \"" + this.from
						+ "\" to \"" + newConfiguration.getFrom() + "\n");
			}
			if ((this.to != null)&&(!this.to.equals(newConfiguration.getTo()))) {
				reportBuffer.append("To changed from: \"" + this.to
						+ "\" to \"" + newConfiguration.getTo() + "\n");
			}
			if ((this.dataFileName != null)&&(!this.dataFileName.equals(newConfiguration.getDataFileName()))) {
				reportBuffer.append("DataFileName changed from: \""
						+ this.dataFileName + "\" to \""
						+ newConfiguration.getDataFileName() + "\n");
			}
			if (reportBuffer.length() == 0) {
				reportBuffer.append("Compared configurations are identical");
			}
		} else {
			reportBuffer.append("No newConfiguration, current content: index: "
					+ index + " from: " + from + " to: " + to + " fileName: "
					+ dataFileName);
		}
		return reportBuffer.toString();
	}
}
