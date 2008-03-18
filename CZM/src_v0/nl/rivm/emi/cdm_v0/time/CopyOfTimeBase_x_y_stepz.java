package nl.rivm.emi.cdm_v0.time;

public class CopyOfTimeBase_x_y_stepz extends TimeBaseBase {

	static private CopyOfTimeBase_x_y_stepz instance = null;
	
	private CopyOfTimeBase_x_y_stepz(int startAge, int ageStep) {
		super();
		for (float count = 0; count < 90; count += 5) {
			add(new Float(count));
		}
	}
	static public CopyOfTimeBase_x_y_stepz getInstance(){
	return instance;	
	}
}
