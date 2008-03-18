package nl.rivm.emi.cdm_v0.parameter;

import nl.rivm.emi.cdm_v0.time.TimeBase_0_105plus_step1;

public class Parameter_0_105plus_step1_zeroes extends ParameterInTimeArray {

	private static final long serialVersionUID = 3377348536730092521L;

	static private Parameter_0_105plus_step1_zeroes instance = null;

	private Parameter_0_105plus_step1_zeroes() {
		super(TimeBase_0_105plus_step1.getInstance().size(), 0);
	}

	synchronized static public Parameter_0_105plus_step1_zeroes getInstance() {
		if (instance == null) {
			instance = new Parameter_0_105plus_step1_zeroes();
		}
		return instance;
	}
}
