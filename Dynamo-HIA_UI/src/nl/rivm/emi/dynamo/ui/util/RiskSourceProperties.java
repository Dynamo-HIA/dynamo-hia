package nl.rivm.emi.dynamo.ui.util;

public class RiskSourceProperties {

	private String fileNameMainPart;
	private String rootElementName;
	private Integer numberOfCategories;

	public RiskSourceProperties() {
		super();
	}

	public RiskSourceProperties(String fileNameMainPart,
			String rootElementName, Integer numberOfCategories) {
		super();
		this.fileNameMainPart = fileNameMainPart;
		this.rootElementName = rootElementName;
		this.numberOfCategories = numberOfCategories;
	}

	public String getFileNameMainPart() {
		return fileNameMainPart;
	}

	public void setFileNameMainPart(String fileNameMainPart) {
		this.fileNameMainPart = fileNameMainPart;
	}

	public String getRootElementName() {
		return rootElementName;
	}

	public void setRootElementName(String rootElementName) {
		this.rootElementName = rootElementName;
	}

	public Integer getNumberOfCategories() {
		return numberOfCategories;
	}

	public void setNumberOfCategories(Integer numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}
}
