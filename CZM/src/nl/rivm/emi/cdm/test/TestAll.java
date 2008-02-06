package nl.rivm.emi.cdm.test;

import junit.framework.JUnit4TestAdapter;
/**
 * Does not work (yet).
 * @author mondeelr
 *
 */
public class TestAll {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.characteristic.test.TestCharacteristicValueFactory.class);
	}

}
