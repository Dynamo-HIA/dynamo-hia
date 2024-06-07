package nl.rivm.emi.cdm.rules.update.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.rules.update.AgeOneToOneUpdateRule;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRuleRepository;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRulesByCharIdRepository;
import nl.rivm.emi.cdm.rules.update.obsolete.AbstractDoubleBoundUpdateRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAgeOneToOneUpdateRule {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void staticHandling() {
		AgeOneToOneUpdateRule daRule = new AgeOneToOneUpdateRule(1, 0.4F);
		assertNotNull(daRule);
		assertEquals(1, daRule.getCharacteristicId());
		assertEquals(0.4F, daRule.getStepSize());
	}

	@Test
	public void simulation() {
		AgeOneToOneUpdateRule daRule = new AgeOneToOneUpdateRule(1, 0.4F);
		float startAge = 6F;
		float nextAge;
		try {
			nextAge = daRule.update(startAge);
			assertEquals(nextAge, startAge + daRule.getStepSize());
			double wrongAge = 7;
			nextAge = daRule.update(wrongAge);
			assertFalse(true); // Force error if Exception is not thrown.
		} catch (CDMUpdateRuleException e) {
			e.printStackTrace();
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.rules.update.containment.test.TestUpdateRuleContainers.class);
	}
}
