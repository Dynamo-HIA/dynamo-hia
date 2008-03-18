package nl.rivm.emi.cdm_v0.time;
/**
 * Singleton standard timebase.
 * 
 * @author mondeelr
 *
 */
public class TimeBase_0_105plus_step1 extends TimeBaseBase {

	static private TimeBase_0_105plus_step1 instance = null;
	
	private TimeBase_0_105plus_step1() {
		super();
		for (float count = 0; count < 106; count += 1) {
			add(new Float(count));
		}
	}
	
	synchronized static public TimeBase_0_105plus_step1 getInstance(){
	if(instance == null){
		instance = new TimeBase_0_105plus_step1();
	}
	return instance;	
	}
}
