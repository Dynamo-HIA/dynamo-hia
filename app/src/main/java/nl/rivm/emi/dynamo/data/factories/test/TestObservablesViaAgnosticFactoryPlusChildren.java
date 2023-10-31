package nl.rivm.emi.dynamo.data.factories.test;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestObservablesViaAgnosticFactoryPlusChildren {
	Log log = LogFactory.getLog(getClass().getName());

	@Before
	public void setup() {
	}

	@After
	public void teardown() {
	}

	@Test
	public void testPopulationSize() {
		RunnablePopulationSizeManufacturing runablePopSizeMan = new RunnablePopulationSizeManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runablePopSizeMan);
	}

	@Test
	public void testOverallMortality() {
		RunnableOverallMortalityManufacturing runableMan = new RunnableOverallMortalityManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testOverallDALYWeights() {
		RunnableOverallDALYWeightsManufacturing runableMan = new RunnableOverallDALYWeightsManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
		}

	@Test
	public void testTransitionMatrix() {
		RunnableTransitionMatrixManufacturing runableMan = new RunnableTransitionMatrixManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testCategoricalRiskFactorPrevalences() {
		RunnableCategoricalRiskfactorPrevalencesManufacturing runableMan = new RunnableCategoricalRiskfactorPrevalencesManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testRelRiskForDeathCategorical() {
		RunnableRelRiskForDeathCategoricalManufacturing runableMan = new RunnableRelRiskForDeathCategoricalManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testRelRiskForDeathContinuous() {
		RunnableRelRiskForDeathContinuousManufacturing runableMan = new RunnableRelRiskForDeathContinuousManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testDiseasePrevalences() {
		RunnableDiseasePrevalencesManufacturing runableMan = new RunnableDiseasePrevalencesManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testDiseaseIncidences() {
		RunnableDiseaseIncidencesManufacturing runableMan = new RunnableDiseaseIncidencesManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}
	
	@Test
	public void testRelRiskForRiskFactorCategorical() {
		RunnableRelRiskForRiskFactorCategoricalManufacturing runableMan = new RunnableRelRiskForRiskFactorCategoricalManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testRelRiskForRiskFactorContinuous() {
		RunnableRelRiskForRiskFactorContinuousManufacturing runableMan = new RunnableRelRiskForRiskFactorContinuousManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testRelRiskFromOtherDisease() {
		RunnableRelRiskFromOtherDiseaseManufacturing runableMan = new RunnableRelRiskFromOtherDiseaseManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	@Test
	public void testDALYWeights() {
		RunnableDALYWeightsManufacturing runableMan = new RunnableDALYWeightsManufacturing();
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				runableMan);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestObservablesViaAgnosticFactoryPlusChildren.class);
	}
}
