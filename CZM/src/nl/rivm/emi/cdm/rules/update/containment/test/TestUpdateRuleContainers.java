package nl.rivm.emi.cdm.rules.update.containment.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import junit.framework.JUnit4TestAdapter;
import nl.rivm.emi.cdm.rules.update.base.CharacteristicSpecific;
import nl.rivm.emi.cdm.rules.update.base.OneToOneUpdateRuleBase;
import nl.rivm.emi.cdm.rules.update.base.StepSizeSpecific;
import nl.rivm.emi.cdm.rules.update.base.UpdateRuleMarker;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRuleRepository;
import nl.rivm.emi.cdm.rules.update.containment.UpdateRulesByCharIdRepository;
import nl.rivm.emi.cdm.rules.update.obsolete.AbstractDoubleBoundUpdateRule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestUpdateRuleContainers {
	Log log = LogFactory.getLog(getClass().getName());

	static public class UpdateRuleOneOne extends AbstractDoubleBoundUpdateRule{
	private int characteristicId;
	private float stepSize;
		public UpdateRuleOneOne(int characteristicId, float stepSize) {
			super( characteristicId, stepSize);
			this.characteristicId = 1;
			this.stepSize = 1F;
		}

		public Object update(Object currentValue) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getCharacteristicId() {
			return this.characteristicId;
		}

		public void setCharacteristicId(int characteristicId) {
			this.characteristicId = characteristicId;
			
		}

		public float getStepSize() {
			return this.stepSize;
		}

		public void setStepSize(float stepSize) {
		this.stepSize = stepSize;	
		}
	}

	static public class UpdateRuleTwoTwo extends AbstractDoubleBoundUpdateRule{
		private int characteristicId;
		private float stepSize;
		public UpdateRuleTwoTwo(int characteristicId, float stepSize) {
			super( characteristicId, stepSize);
			this.characteristicId = 2;
			this.stepSize = 2F;
		}

		public int getCharacteristicId() {
			return this.characteristicId;
		}

		public void setCharacteristicId(int characteristicId) {
			this.characteristicId = characteristicId;
			
		}

		public float getStepSize() {
			return this.stepSize;
		}

		public void setStepSize(float stepSize) {
		this.stepSize = stepSize;	
		}

		public Object update(Object currentValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	static public class UpdateRuleSixFour extends  AbstractDoubleBoundUpdateRule{
		private int characteristicId;
		private float stepSize;
		public UpdateRuleSixFour(int characteristicId, float stepSize) {
			super( characteristicId, stepSize);
			this.characteristicId = 6;
			this.stepSize = 4F;
	}

		public int getCharacteristicId() {
			return this.characteristicId;
		}

		public void setCharacteristicId(int characteristicId) {
			this.characteristicId = characteristicId;
			
		}

		public float getStepSize() {
			return this.stepSize;
		}

		public void setStepSize(float stepSize) {
		this.stepSize = stepSize;	
		}

		public Object update(Object currentValue) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void storeByCharacteristicId() {
		UpdateRulesByCharIdRepository updateRulesByCharIdContainer = new UpdateRulesByCharIdRepository();
		assertNotNull(updateRulesByCharIdContainer);
		Set<UpdateRuleMarker> updateRules = updateRulesByCharIdContainer.getUpdateRuleSet(1);
		assertNull(updateRules);
		AbstractDoubleBoundUpdateRule updateRuleOneOne = new UpdateRuleOneOne(1,1F);
		assertNotNull(updateRuleOneOne);
		updateRulesByCharIdContainer.putUpdateRule(updateRuleOneOne);
		Set<UpdateRuleMarker> storedUpdateRule = updateRulesByCharIdContainer.getUpdateRuleSet(1);
		assertNotNull(storedUpdateRule);
	}

	@Test
	public void store() {
		UpdateRuleRepository storage = new UpdateRuleRepository();
		assertNotNull(storage);
		UpdateRuleMarker updateRuleOneOne = new UpdateRuleOneOne(1, 1F);
		assertNotNull(updateRuleOneOne);
		storage.addUpdateRule(updateRuleOneOne);
		UpdateRuleMarker updateRuleTwoTwo = new UpdateRuleTwoTwo(2,2F);
		assertNotNull(updateRuleTwoTwo);
		storage.addUpdateRule(updateRuleTwoTwo);
		UpdateRuleMarker updateRuleSixFour = new UpdateRuleSixFour(6, 4F);
		assertNotNull(updateRuleSixFour);
		storage.addUpdateRule(updateRuleSixFour);
		Set<UpdateRuleMarker> updateRules = storage.getUpdateRules(0,0F);
		assertNull(updateRules);
		updateRules = (Set<UpdateRuleMarker>)storage.getUpdateRules(1,1F);
		assertNotNull(updateRules);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(
				nl.rivm.emi.cdm.rules.update.containment.test.TestUpdateRuleContainers.class);
	}

}
