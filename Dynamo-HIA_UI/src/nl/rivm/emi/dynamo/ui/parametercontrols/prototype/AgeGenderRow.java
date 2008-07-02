package nl.rivm.emi.dynamo.ui.parametercontrols.prototype;
public class AgeGenderRow {
	public Float age;
	public Float femaleParam;
	public Float maleParam;

	public AgeGenderRow(Float age, Float femaleParam, Float maleParam) {
		super();
		this.age = age;
		this.femaleParam = femaleParam;
		this.maleParam = maleParam;
	}

	public Float getAge() {
		return age;
	}

	public String getAgeAsString() {
		return age.toString();
	}

	public void setAge(Float age) {
		this.age = age;
	}

	public Float getFemaleParam() {
		return femaleParam;
	}

	public String getFemaleParamAsString() {
		return femaleParam.toString();
	}

	public void setFemaleParam(Float femaleParam) {
		this.femaleParam = femaleParam;
	}

	public Float getMaleParam() {
		return maleParam;
	}

	public String getMaleParamAsString() {
		return maleParam.toString();
	}

	public void setMaleParam(Float maleParam) {
		this.maleParam = maleParam;
	}
}
