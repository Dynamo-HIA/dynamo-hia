package nl.rivm.emi.cdm.updating;

/**
 * OBSOLETE
 * 
 * Class containing static update rules that provide only minimal functionality.
 * 
 * @author mondeelr
 * 
 */
public class SimpleUpdateRules extends UpdateRuleBaseClass {
	public SimpleUpdateRules(int characteristicIndex, int stepSize) {
		super(characteristicIndex, stepSize);
	}

	/**
	 * 
	 * @param previous
	 *            return the value passed to it.
	 * @return
	 */
	public static int updateUnchanged(int previous) {
		return previous;
	}

	/**
	 * 
	 * @param previous
	 *            return the value passed to it plus one.
	 * @return
	 */
	public static int updateAddOne(int previous) {
		return (previous + 1);
	}

	@Override
	public int updateSelf(int currentValue) {
		// TODO Auto-generated method stub
		return 0;
	}

}
