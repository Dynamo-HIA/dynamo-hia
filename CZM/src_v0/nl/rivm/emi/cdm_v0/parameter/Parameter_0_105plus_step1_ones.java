package nl.rivm.emi.cdm_v0.parameter;

import nl.rivm.emi.cdm_v0.time.TimeBase_0_105plus_step1;

public class Parameter_0_105plus_step1_ones extends ParameterInTimeArray {

	private static final long serialVersionUID = 3377348536730092521L;

	static private Parameter_0_105plus_step1_ones instance = null;

	private Parameter_0_105plus_step1_ones() {
		super(TimeBase_0_105plus_step1.getInstance().size(), 1);
	}

	synchronized static public Parameter_0_105plus_step1_ones getInstance() {
		if (instance == null) {
			instance = new Parameter_0_105plus_step1_ones();
		}
		return instance;
	}
}
