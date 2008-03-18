package nl.rivm.emi.cdm_v0.parameter;

import nl.rivm.emi.cdm_v0.time.TimeBase_0_85plus_step5;

public class Parameter_0_85plus_step5_zeroes extends ParameterInTimeArray{

	private static final long serialVersionUID = -5570041176662896908L;

	static private Parameter_0_85plus_step5_zeroes instance = null;
	
	private Parameter_0_85plus_step5_zeroes() {
		super(TimeBase_0_85plus_step5.getInstance().size(), 0);
	}
	
	synchronized static public Parameter_0_85plus_step5_zeroes getInstance(){
	if(instance == null){
		instance = new Parameter_0_85plus_step5_zeroes();
	}
	return instance;	
	}
}
