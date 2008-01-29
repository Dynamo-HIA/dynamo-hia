package nl.rivm.emi.cdm_v0.parameter;

import nl.rivm.emi.cdm_v0.time.TimeBase_0_85plus_step5;

public class Parameter_0_85plus_step5_ones extends ParameterInTimeArray {

	private static final long serialVersionUID = 3062676691901849064L;

	static private Parameter_0_85plus_step5_ones instance = null;

	private Parameter_0_85plus_step5_ones() {
		super(TimeBase_0_85plus_step5.getInstance().size(), 1);
	}

	synchronized static public Parameter_0_85plus_step5_ones getInstance() {
		if (instance == null) {
			instance = new Parameter_0_85plus_step5_ones();
		}
		return instance;
	}
}
