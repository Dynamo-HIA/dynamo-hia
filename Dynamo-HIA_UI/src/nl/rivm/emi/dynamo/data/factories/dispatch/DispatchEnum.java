package nl.rivm.emi.dynamo.data.factories.dispatch;

/**
 * Enumeration mapping the relations between the name of a root-element in a configuration 
 * file and the Factory Object to turn it into a Configuration Model Object.
 */

import nl.rivm.emi.dynamo.data.factories.DALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.DiseaseIncidencesFactory;
import nl.rivm.emi.dynamo.data.factories.DiseasePrevalencesFactory;
import nl.rivm.emi.dynamo.data.factories.DummyPlaceholderFactory;
import nl.rivm.emi.dynamo.data.factories.DurationDistributionFactory;
import nl.rivm.emi.dynamo.data.factories.DynamoSimulationFactory;
import nl.rivm.emi.dynamo.data.factories.ExcessMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.NewbornsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallDALYWeightsFactory;
import nl.rivm.emi.dynamo.data.factories.OverallMortalityFactory;
import nl.rivm.emi.dynamo.data.factories.PopulationSizeFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDeathContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskForDisabilityContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromOtherDiseaseFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RelRiskFromRiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorCompoundFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.RiskFactorPrevalencesContinuousFactory;
import nl.rivm.emi.dynamo.data.factories.RootLevelFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionDriftFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionDriftNettoFactory;
import nl.rivm.emi.dynamo.data.factories.TransitionMatrixFactory;
import nl.rivm.emi.dynamo.data.types.XMLTagEntityEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.ui.main.DataAndFileContainer;

public enum DispatchEnum {
	/* W01 */
	SIMULATION(RootElementNamesEnum.SIMULATION.getNodeLabel(),
			new DynamoSimulationFactory(), null),
	/* W11 */
	POPULATIONSIZE(RootElementNamesEnum.POPULATIONSIZE.getNodeLabel(),
			new PopulationSizeFactory(), null),
	/* W12 */
	OVERALLMORTALITY(RootElementNamesEnum.OVERALLMORTALITY.getNodeLabel(),
			new OverallMortalityFactory(), null),
	/* W13 */
	NEWBORNS(RootElementNamesEnum.NEWBORNS.getNodeLabel(),
			new NewbornsFactory(), null),
	/* W14 */
	OVERALLDALYWEIGHTS(RootElementNamesEnum.OVERALLDALYWEIGHTS.getNodeLabel(),
			new OverallDALYWeightsFactory(), null), // TODO
	/* W20Cat */
	RISKFACTOR_CATEGORICAL(RootElementNamesEnum.RISKFACTOR_CATEGORICAL
			.getNodeLabel(), new RiskFactorCategoricalFactory(), null),
	/* W20Con */
	RISKFACTOR_CONTINUOUS(RootElementNamesEnum.RISKFACTOR_CONTINUOUS
			.getNodeLabel(), new RiskFactorContinuousFactory(), null),
	/* W20Cmp */
	RISKFACTOR_COMPOUND(
			RootElementNamesEnum.RISKFACTOR_COMPOUND.getNodeLabel(),
			new RiskFactorCompoundFactory(), null),
	/* W21TdMA */
	TRANSITIONDRIFT(RootElementNamesEnum.TRANSITIONDRIFT.getNodeLabel(),
			new TransitionDriftFactory(), null), // TODO
	/* W21TdId */// TODO
	/* W21TdFp */// TODO
	TRANSITIONDRIFT_NETTO(RootElementNamesEnum.TRANSITIONDRIFT_NETTO
			.getNodeLabel(), new TransitionDriftNettoFactory(),
			null), // 
	/* W21TmMA */

	TRANSITIONMATRIX(RootElementNamesEnum.TRANSITIONMATRIX.getNodeLabel(),
			new TransitionMatrixFactory(), null),
	/* W21TmId */
	TRANSITIONMATRIX_ZERO(RootElementNamesEnum.TRANSITIONMATRIX_ZERO
			.getNodeLabel(), /*
							 * new TransitionMatrixFactory()
							 */
	new DummyPlaceholderFactory(), null), // TODO
	/* W21TmFp */
	TRANSITIONMATRIX_NETTO(RootElementNamesEnum.TRANSITIONMATRIX_NETTO
			.getNodeLabel(), /*
							 * new TransitionMatrixFactory()
							 */
	new DummyPlaceholderFactory(), null), // TODO
	/* W22CatCom */
	RISKFACTORPREVALENCES_CATEGORICAL(
			RootElementNamesEnum.RISKFACTORPREVALENCES_CATEGORICAL
					.getNodeLabel(),
			new RiskFactorPrevalencesCategoricalFactory(), null), // TODO
	/* W22Con */
	RISKFACTORPREVALENCES_CONTINUOUS(
			RootElementNamesEnum.RISKFACTORPREVALENCES_CONTINUOUS
					.getNodeLabel(), new RiskFactorPrevalencesContinuousFactory(), null), // TODO
	/* W22ComDur */
	RISKFACTORPREVALENCES_DURATION(
			RootElementNamesEnum.RISKFACTORPREVALENCES_DURATION.getNodeLabel(),
			new DurationDistributionFactory(), null),
	/* W23Cat */
	RELRISKFORDEATH_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKSFORDEATH_CATEGORICAL
					.getNodeLabel(), new RelRiskForDeathCategoricalFactory(),
			null),
	/* W23Con */
	RELRISKFORDEATH_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKSFORDEATH_CONTINUOUS
					.getNodeLabel(), new RelRiskForDeathContinuousFactory(),
			null),
	/* W23Cmp */
	RELRISKFORDEATH_COMPOUND(
			RootElementNamesEnum.RELATIVERISKSFORDEATH_COMPOUND.getNodeLabel(),
			new RelRiskForDeathCompoundFactory(), null),
	/* W23Cat */
	RELRISKFORDISABLITY_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CATEGORICAL
					.getNodeLabel(),
			new RelRiskForDisabilityCategoricalFactory(), null),
	/* W23Con */
	RELRISKFORDISABLITY_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_CONTINUOUS
					.getNodeLabel(),
			new RelRiskForDisabilityContinuousFactory(), null),
	/* W23Cmp */
	RELRISKFORDISABLITY_COMPOUND(
			RootElementNamesEnum.RELATIVERISKSFORDISABILITY_COMPOUND
					.getNodeLabel(), new RelRiskForDisabilityCompoundFactory(), null), // TODO
	DISEASEPREVALENCES(RootElementNamesEnum.DISEASEPREVALENCES.getNodeLabel(),
			new DiseasePrevalencesFactory(), null), // TODO
	/* W32 */
	DISEASEINCIDENCES(RootElementNamesEnum.DISEASEINCIDENCES.getNodeLabel(),
			new DiseaseIncidencesFactory(), null),
	/* W33 */
	EXCESSMORTALITY(XMLTagEntityEnum.EXCESSMORTALITY.getElementName(),
			new ExcessMortalityFactory(), null),
	/* W34Cat */
	RRISKFORRISKFACTOR_CATEGORICAL(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CATEGORICAL
					.getNodeLabel(),
			new RelRiskFromRiskFactorCategoricalFactory(), null),
	/* W34Con */
	RRISKFORRISKFACTOR_CONTINUOUS(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_CONTINUOUS
					.getNodeLabel(),
			new RelRiskFromRiskFactorContinuousFactory(), null),
	/* W34Cmp */
	RRISKFORRISKFACTOR_COMPOUND(
			RootElementNamesEnum.RELATIVERISKSFROMRISKFACTOR_COMPOUND
					.getNodeLabel(), new RelRiskFromRiskFactorCompoundFactory(), null), // TODO
	/* W35 */
	RRISKFROMDISEASE(RootElementNamesEnum.RELATIVERISKSFROMDISEASE
			.getNodeLabel(), new RelRiskFromOtherDiseaseFactory(), null),
	/* W?? */
	DALYWEIGHTS(RootElementNamesEnum.DALYWEIGHTS.getNodeLabel(),
			new DALYWeightsFactory(), null);

	private final String rootNodeName;
	private final RootLevelFactory theFactory;
	private final DataAndFileContainer theModalClass;

	private DispatchEnum(String rootNodeName, RootLevelFactory theFactory,
			DataAndFileContainer modalClass) {
		this.theFactory = theFactory;
		this.rootNodeName = rootNodeName;
		theModalClass = modalClass;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public RootLevelFactory getTheFactory() {
		return theFactory;
	}

	public DataAndFileContainer getTheModalClass() {
		return theModalClass;
	}
}