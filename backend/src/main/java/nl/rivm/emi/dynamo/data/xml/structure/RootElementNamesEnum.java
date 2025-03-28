package nl.rivm.emi.dynamo.data.xml.structure;

/**
 * Enum containing all possible rootelementnames in the dynamo-HIA configuration
 * files.
 * 
 * This Enum was created later on during development and has not been factored
 * in pervasively. All literal references to rootelementnames should eventually
 * be changed to run through this Enum. This will greatly decrease the
 * vulnerability to refactoring resulting in inconsistent names across the codebase.
 * 
 * @author mondeelr
 * 20090313 mondeelr Bij "*fromriskfactor*" en "*fromdisease*" "relative"prefix 
 * teruggebracht naar "rel".
 */

import nl.rivm.emi.dynamo.data.types.atomic.AttributableMortalities;
import nl.rivm.emi.dynamo.data.types.atomic.BaselineFatalIncidences;
import nl.rivm.emi.dynamo.data.types.atomic.BaselineIncidences;
import nl.rivm.emi.dynamo.data.types.atomic.BaselineOtherMortalities;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.root.Alphas;
import nl.rivm.emi.dynamo.data.types.root.Alphas4Parameters;
import nl.rivm.emi.dynamo.data.types.root.AlphasOtherMortality;
import nl.rivm.emi.dynamo.data.types.root.BaselineAbility;
import nl.rivm.emi.dynamo.data.types.root.DALYWeights;
import nl.rivm.emi.dynamo.data.types.root.DiseaseIncidences;
import nl.rivm.emi.dynamo.data.types.root.DiseasePrevalences;
import nl.rivm.emi.dynamo.data.types.root.ExcessMortality;
import nl.rivm.emi.dynamo.data.types.root.Newborns;
import nl.rivm.emi.dynamo.data.types.root.OverallDALYWeights;
import nl.rivm.emi.dynamo.data.types.root.OverallMortality;
import nl.rivm.emi.dynamo.data.types.root.PopulationSize;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForAbilityCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForAbilityContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDeathCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDeathCompound;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDeathContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDisabilityCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDisabilityCompound;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDisabilityContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorCategorical4Parameters;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorCompound;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorCompound4Parameters;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorContinuous4Parameters;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisksCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisksCluster;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisksFromDisease;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisks_Begin;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisks_End;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisks_OtherMort_Begin;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisks_OtherMort_Categorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisks_OtherMort_Continuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisks_OtherMort_End;
import nl.rivm.emi.dynamo.data.types.root.RiskFactorCompound;
import nl.rivm.emi.dynamo.data.types.root.RiskFactorContinuous;
import nl.rivm.emi.dynamo.data.types.root.RiskFactorPrevalencesContinuous;
import nl.rivm.emi.dynamo.data.types.root.RiskFactorPrevalencesDuration;
import nl.rivm.emi.dynamo.data.types.root.RiskFactorPrevalencesDurationUniform;
import nl.rivm.emi.dynamo.data.types.root.RiskfactorCategorical;
import nl.rivm.emi.dynamo.data.types.root.RiskfactorPrevalencesCategorical;
import nl.rivm.emi.dynamo.data.types.root.Simulation;
import nl.rivm.emi.dynamo.data.types.root.TransitionDrift;
import nl.rivm.emi.dynamo.data.types.root.TransitionDriftNetto;
import nl.rivm.emi.dynamo.data.types.root.TransitionDriftZero;
import nl.rivm.emi.dynamo.data.types.root.TransitionMatrix;
import nl.rivm.emi.dynamo.data.types.root.TransitionMatrixNetto;
import nl.rivm.emi.dynamo.data.types.root.TransitionMatrixZero;
import nl.rivm.emi.dynamo.data.xml.structure.check.FileLocationCheck;
import nl.rivm.emi.dynamo.global.FileNode;
import nl.rivm.emi.dynamo.global.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum RootElementNamesEnum /* implements RootElementType */{

	/**
	 * This represents the list of root element names, used in the xml, xsd, and
	 * application
	 */

	SIMULATION(new Simulation(), null, StandardTreeNodeLabelsEnum.SIMULATIONS
			.getNodeLabel(), null), // Comment to block reformatting.
	POPULATIONSIZE(new PopulationSize(), null,
			StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	OVERALLMORTALITY(new OverallMortality(), null,
			StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	NEWBORNS(new Newborns(), null, StandardTreeNodeLabelsEnum.POPULATIONS
			.getNodeLabel(), null), //
	OVERALLDALYWEIGHTS(new OverallDALYWeights(), null,
			StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	RISKFACTOR_CATEGORICAL(new RiskfactorCategorical(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), null), //
	RISKFACTOR_CONTINUOUS(new RiskFactorContinuous(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), null), //
	RISKFACTOR_COMPOUND(new RiskFactorCompound(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), null), //
	TRANSITIONMATRIX(new TransitionMatrix(),
			StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONMATRIX_ZERO(new TransitionMatrixZero(),
			StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONMATRIX_NETTO(new TransitionMatrixNetto(),
			StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONDRIFT(new TransitionDrift(),
			StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONDRIFT_ZERO(new TransitionDriftZero(),
			StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONDRIFT_NETTO(new TransitionDriftNetto(),
			StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_CATEGORICAL(new RiskfactorPrevalencesCategorical(),
			StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_CONTINUOUS(new RiskFactorPrevalencesContinuous(),
			StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_DURATION_UNIFORM(
			new RiskFactorPrevalencesDurationUniform(),
			StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
					.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_DURATION(new RiskFactorPrevalencesDuration(),
			StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
					.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDEATH_CATEGORICAL(new RelativeRiskForDeathCategorical(),
			StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDEATH_CONTINUOUS(new RelativeRiskForDeathContinuous(),
			StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDEATH_COMPOUND(new RelativeRiskForDeathCompound(),
			StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDISABILITY_CATEGORICAL(
			new RelativeRiskForDisabilityCategorical(),
			StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel(),
			null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDISABILITY_CONTINUOUS(
			new RelativeRiskForDisabilityContinuous(),
			StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel(),
			null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDISABILITY_COMPOUND(
			new RelativeRiskForDisabilityCompound(),
			StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel(),
			null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	DISEASEPREVALENCES(new DiseasePrevalences(),
			StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	DISEASEINCIDENCES(new DiseaseIncidences(),
			StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	EXCESSMORTALITY(new ExcessMortality(),
			StandardTreeNodeLabelsEnum.EXCESSMORTALITIES.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL(
			new RelativeRiskFromRiskFactorCategorical(),
			StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
					.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES
					.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS(
			new RelativeRiskFromRiskFactorContinuous(),
			StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
					.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES
					.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND(
			new RelativeRiskFromRiskFactorCompound(),
			StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR
					.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES
					.getNodeLabel()), //
	RELATIVERISKSFROMDISEASE(
			new RelativeRisksFromDisease(),
			StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(),
			null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	DALYWEIGHTS(new DALYWeights(), StandardTreeNodeLabelsEnum.DALYWEIGHTS
			.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES
			.getNodeLabel()),
	/* Estimated parameters. */
	ATTRIBUTABLEMORTALITIES(new AttributableMortalities(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	BASELINEFATALINCIDENCES(new BaselineFatalIncidences(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	BASELINEINCIDENCES(new BaselineIncidences(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	BASELINEOTHERMORTALITIES(new BaselineOtherMortalities(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS(new RelativeRisksCategorical(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKSCLUSTER(new RelativeRisksCluster(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	/*
	 * TRANSITIONMATRIXPARAMETERS(new TransitionMatrix(),
	 * StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
	 * StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	 */

	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL4P(
			new RelativeRiskFromRiskFactorCategorical4Parameters(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS4P(
			new RelativeRiskFromRiskFactorContinuous4Parameters(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND4P(
			new RelativeRiskFromRiskFactorCompound4Parameters(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS_OTHERMORT_CATEGORICAL(
			new RelativeRisks_OtherMort_Categorical(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS_OTHERMORT_CONTINUOUS(
			new RelativeRisks_OtherMort_Continuous(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	ALFAS(new Alphas(), StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(),
			null, StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	ALPHAS4P(new Alphas4Parameters(), StandardTreeNodeLabelsEnum.PARAMETERS
			.getNodeLabel(), null, StandardTreeNodeLabelsEnum.SIMULATIONS
			.getNodeLabel()), //

	ALPHASOTHERMORTALITY(new AlphasOtherMortality(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS_OTHERMORT_BEGIN(new RelativeRisks_OtherMort_Begin(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS_OTHERMORT_END(new RelativeRisks_OtherMort_End(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS_BEGIN(new RelativeRisks_Begin(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RELATIVERISKS_END(new RelativeRisks_End(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	BASELINE_ABILITY(new BaselineAbility(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //

	RR_RISKFACTOR_ABILITY_END(new RelativeRisks_End(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RR_RISKFACTOR_ABILITY_BEGIN(new RelativeRisks_Begin(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RR_RISKFACTOR_ABILITY_ALPHA(new Alphas4Parameters(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RR_RISKFACTOR_ABILITY_CONT(new RelativeRiskForAbilityContinuous(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	RR_RISKFACTOR_ABILITY_CAT(new RelativeRiskForAbilityCategorical(),
			StandardTreeNodeLabelsEnum.PARAMETERS.getNodeLabel(), null,
			StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel()), //
	;

	Log log = LogFactory.getLog(this.getClass().getName());

	XMLTagEntity theType;
	FileLocationCheck fileLocationCheck;

	private RootElementNamesEnum(XMLTagEntity type,
			String expectedParentNodeLabel,
			String expectedGrandParentNodeLabel,
			String expectedGreatGrandParentNodeLabel) {
		theType = type;
		fileLocationCheck = new FileLocationCheck(expectedParentNodeLabel,
				expectedGrandParentNodeLabel, expectedGreatGrandParentNodeLabel);
	}

	public String getNodeLabel() {
		String nodeLabel = ((XMLTagEntity) theType).getXMLElementName();
		log.debug("Returning nodeLabel: " + nodeLabel);
		return nodeLabel;
	}

	public boolean isLocationOK(FileNode node) {
		if (!theType.getXMLElementName().equals("transitionmatrix")) {
			return fileLocationCheck.test(node);
		} else {
			log.debug(theType.getXMLElementName());
			return fileLocationCheck.test(node);
		}
	}
}
