package nl.rivm.emi.cdm.updaterules.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.updaterules.AbstractUnboundOneToOneUpdateRule;
import nl.rivm.emi.cdm.updaterules.UpdateRuleStorage;
import nl.rivm.emi.cdm.updaterules.UpdateRulesByCharIdContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPrimitiveArray {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void storeByCharacteristicId() {
		int[] integertjes = new int[4];
		integertjes[2]= 4;
		System.out.println(integertjes[0]);
		assertNull(integertjes[0]);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.updaterules.test.TestPrimitiveArray.class);
	}

}
