package nl.rivm.emi.cdm_v0.time;
/**
 * Singleton standard timebase.
 * 
 * @author mondeelr
 *
 */
public class TimeBase_0_85plus_step5 extends TimeBaseBase {

	static private TimeBase_0_85plus_step5 instance = null;
	
	private TimeBase_0_85plus_step5() {
		super();
		for (float count = 0; count < 90; count += 5) {
			add(new Float(count));
		}
	}
	
	synchronized static public TimeBase_0_85plus_step5 getInstance(){
	if(instance == null){
		instance = new TimeBase_0_85plus_step5();
	}
	return instance;	
	}
}
