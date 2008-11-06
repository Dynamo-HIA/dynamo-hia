package nl.rivm.emi.dynamo.ui.parametercontrols;
public class YearIntegerDataRow {
	public Integer year;
	public Integer integer;

	public YearIntegerDataRow(Integer year, Integer integer) {
		super();
		this.year = year;
		this.integer = integer;
	}

	public String getYearAsString() {
		return year.toString();
	}

	public String getIntegerAsString() {
		return integer.toString();
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getInteger() {
		return integer;
	}

	public void setInteger(Integer integer) {
		this.integer = integer;
	}
}
