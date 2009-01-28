package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Enumeration mapping the relations between the name of a root-element in a configuration 
 * file and the Factory Object to turn it into a Configuration Model Object.
 */

import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.DiseaseIncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.DiseasePrevalencesFactory;
import nl.rivm.emi.dynamo.data.factories.DummyPlaceholderFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromOtherDiseaseFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesDurationFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;

public enum DispatchEnum {
	/* W01 */
	SIMULATION(RootElementNamesEnum.SIMULATION.getNodeLabel(), /*
																 * new
																 * SimulationFactory
																 * ()
																 */
	new DummyPlaceholderFactory()), // TODO
	/* W11 */
	POPULATIONSIZE(RootElementNamesEnum.POPULATIONSIZE.getNodeLabel(),
			new PopulationSizeFactory()),
	/* W12 */
	OVERALLMORTALITY(RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel(),
			new OverallMortalityFactory()),
	/* W13 */
	NEWBORNS(RootElementNamesEnum.NEWBORNS.getNodeLabel(), /*
															 * new
															 * NewBornsFactory()
															 */
	new DummyPlaceholderFactory()), // TODO
	/* W14 */
	OVERALLDALYWEIGHTS(RootElementNamesEnum.OVERALLDALYWEIGHTS.getNodeLabel(),
			new OverallDALYWeightsFactory()), // TODO
	/* W20Cat */
	/* W20Con */
	/* W20Cmp */
	RISKFACTOR_COMPOUND(RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(), /* new RiskFactorFactory() */
	new DummyPlaceholderFactory()), // TODO
	/* W21TmId */
	TRANSITIONMATRIX_ZERO(RootElementNamesEnum.TRANSITIONMATRIX_ZERO.getNodeLabel(), /*
													 * new
													 * TransitionMatrixFactory()
													 */
	new DummyPlaceholderFactory()), // TODO
	/* W21TmFp */
	TRANSITIONMATRIX_NETTO(RootElementNamesEnum.TRANSITIONMATRIX_NETTO.getNodeLabel(), /*
													 * new
													 * TransitionMatrixFactory()
													 */
	new DummyPlaceholderFactory()), // TODO
	/* W21TmMA */
	TRANSITIONMATRIX(RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel(), new TransitionMatrixFactory()),
	/* W21TdId */
	/* W21TdFp */
	/* W21TdMA */
	TRANSITIONDRIFT(RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(), /* new TransitionDriftFactory() */
	new DummyPlaceholderFactory()), // TODO
	/* W22CatCom */

	RISKFACTORPREVALENCES_CATEGORICAL(RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL.getNodeLabel(),
			new DummyPlaceholderFactory()), // TODO
	/* W22Con */
	RISKFACTORPREVALENCES_CONTINUOUS(RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS.getNodeLabel(),
			new DummyPlaceholderFactory()), // TODO
	/* W22ComDur */
	RISKFACTORPREVALENCES_DURATION(RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION.getNodeLabel(),
			new RiskFactorPrevalencesDurationFactory()),
	/* W23Cat */
	RELRISKFORDEATH_CATEGORICAL(RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL.getNodeLabel(),
			new RelRiskForDeathCategoricalFactory()),
	/* W23Con */
	RELRISKFORDEATH_CONTINUOUS(RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS.getNodeLabel(),
			new RelRiskForDeathContinuousFactory()),
	/* W23Cmp */
	RELRISKFORDEATH_COMPOUND(RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND.getNodeLabel(),
			new DummyPlaceholderFactory()), // TODO
	DISEASEPREVALENCES(RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel(), new DiseasePrevalencesFactory()), // TODO
	/* W32 */
	DISEASEINCIDENCES(RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel(), new DiseaseIncidencesFactory()),
	/* W33 */
	EXCESSMORTALITY(RootElementNamesEnum.EXCESSMORTALITY.getNodeLabel(), new DummyPlaceholderFactory()), // TODO
	/* W34Cat */
	RRISKFORRISKFACTOR_CATEGORICAL(RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL.getNodeLabel(),
			new RelRiskFromRiskFactorCategoricalFactory()),
	/* W34Con */
	RRISKFORRISKFACTOR_CONTINUOUS(RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory()),
	/* W34Cmp */
	RRISKFORRISKFACTOR_COMPOUND(RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND.getNodeLabel(),
			new DummyPlaceholderFactory()), // TODO
	/* W35 */
	RRISKFROMDISEASE(RootElementNamesEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(), new RelRiskFromOtherDiseaseFactory()),
	/* W?? */
	DALYWEIGHTS(RootElementNamesEnum.DALYWEIGHTS.getNodeLabel(), new DALYWeightsFactory());

	private final String rootNodeName;
	private final AgnosticFactory theFactory;

	private DispatchEnum(String rootNodeName, AgnosticFactory theFactory) {
		this.theFactory = theFactory;
		this.rootNodeName = rootNodeName;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public AgnosticFactory getTheFactory() {
		return theFactory;
	}
}