package nl.rivm.emi.cdm.updating;
/**
 * Class containing static update rules that provide only minimal
 * functionality.
 * 
 * @author mondeelr
 *
 */
public class SimpleUpdateRules implements UpdateRulesBase {
/**
 * 
 * @param previous return the value passed to it.
 * @return
 */
	public static Integer updateUnchanged(Integer previous){
		return previous;
	}
	/**
	 * 
	 * @param previous return the value passed to it plus one.
	 * @return
	 */
		public static Integer updateAddOne(Integer previous){
			return (previous + 1);
		}

}
