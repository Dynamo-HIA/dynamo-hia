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

import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;
import nl.rivm.emi.dynamo.data.types.root.DALYWeights;
import nl.rivm.emi.dynamo.data.types.root.DiseaseIncidences;
import nl.rivm.emi.dynamo.data.types.root.DiseasePrevalences;
import nl.rivm.emi.dynamo.data.types.root.ExcessMortality;
import nl.rivm.emi.dynamo.data.types.root.Newborns;
import nl.rivm.emi.dynamo.data.types.root.OverallDALYWeights;
import nl.rivm.emi.dynamo.data.types.root.OverallMortality;
import nl.rivm.emi.dynamo.data.types.root.PopulationSize;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDeathCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDeathCompound;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDeathContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDisabilityCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDisabilityCompound;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskForDisabilityContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorCategorical;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorCompound;
import nl.rivm.emi.dynamo.data.types.root.RelativeRiskFromRiskFactorContinuous;
import nl.rivm.emi.dynamo.data.types.root.RelativeRisksFromDisease;
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
import nl.rivm.emi.dynamo.data.xml.structure.test.FileLocationTest;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum RootElementNamesEnum /* implements RootElementType */{

	/**
	 * This represents the list of root element names, used in the xml, xsd, and
	 * application
	 */

	SIMULATION(new Simulation(), null, StandardTreeNodeLabelsEnum.SIMULATIONS.getNodeLabel(), null), // Comment to block reformatting.
	POPULATIONSIZE(new PopulationSize(), null, StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	OVERALLMORTALITY(new OverallMortality(), null, StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	NEWBORNS(new Newborns(), null, StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	OVERALLDALYWEIGHTS(new OverallDALYWeights(), null, StandardTreeNodeLabelsEnum.POPULATIONS.getNodeLabel(), null), //
	RISKFACTOR_CATEGORICAL(new RiskfactorCategorical(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), null), //
	RISKFACTOR_CONTINUOUS(new RiskFactorContinuous(), null,StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), null), //
	RISKFACTOR_COMPOUND(new RiskFactorCompound(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel(), null), //
	TRANSITIONMATRIX(new TransitionMatrix(), StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONMATRIX_ZERO(new TransitionMatrixZero(), StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONMATRIX_NETTO(new TransitionMatrixNetto(), StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONDRIFT(new TransitionDrift(), StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONDRIFT_ZERO(new TransitionDriftZero(), StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	TRANSITIONDRIFT_NETTO(new TransitionDriftNetto(), StandardTreeNodeLabelsEnum.TRANSITIONS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_CATEGORICAL(new RiskfactorPrevalencesCategorical(), StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_CONTINUOUS(new RiskFactorPrevalencesContinuous(), StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_DURATION_UNIFORM(new RiskFactorPrevalencesDurationUniform(), StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RISKFACTORPREVALENCES_DURATION(new RiskFactorPrevalencesDuration(), StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDEATH_CATEGORICAL(new RelativeRiskForDeathCategorical(), StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDEATH_CONTINUOUS(new RelativeRiskForDeathContinuous(), StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDEATH_COMPOUND(new RelativeRiskForDeathCompound(), StandardTreeNodeLabelsEnum.RELRISKFORDEATHDIR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDISABILITY_CATEGORICAL(new RelativeRiskForDisabilityCategorical(), StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDISABILITY_CONTINUOUS(new RelativeRiskForDisabilityContinuous(), StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	RELATIVERISKSFORDISABILITY_COMPOUND(new RelativeRiskForDisabilityCompound(), StandardTreeNodeLabelsEnum.RELRISKFORDISABILITYDIR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.RISKFACTORS.getNodeLabel()), //
	DISEASEPREVALENCES(new DiseasePrevalences(), StandardTreeNodeLabelsEnum.PREVALENCES.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	DISEASEINCIDENCES(new DiseaseIncidences(), StandardTreeNodeLabelsEnum.INCIDENCES.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	EXCESSMORTALITY(new ExcessMortality(), StandardTreeNodeLabelsEnum.EXCESSMORTALITIES.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL(
			new RelativeRiskFromRiskFactorCategorical(), StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS(new RelativeRiskFromRiskFactorContinuous(), StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND(new RelativeRiskFromRiskFactorCompound(), StandardTreeNodeLabelsEnum.RELATIVERISKSFROMRISKFACTOR.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	RELATIVERISKSFROMDISEASE(new RelativeRisksFromDisease(), StandardTreeNodeLabelsEnum.RELATIVERISKSFROMDISEASES.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel()), //
	DALYWEIGHTS(new DALYWeights(), StandardTreeNodeLabelsEnum.DALYWEIGHTS.getNodeLabel(), null, StandardTreeNodeLabelsEnum.DISEASES.getNodeLabel());

	Log log = LogFactory.getLog(this.getClass().getName());

	XMLTagEntity theType;
	FileLocationTest fileLocationTest;

	private RootElementNamesEnum(XMLTagEntity type, String expectedParentNodeLabel, String expectedGrandParentNodeLabel, String expectedGreatGrandParentNodeLabel) {
		theType = type;
		fileLocationTest = new FileLocationTest(expectedParentNodeLabel, expectedGrandParentNodeLabel, expectedGreatGrandParentNodeLabel);
	}

	public String getNodeLabel() {
		String nodeLabel = ((XMLTagEntity)theType).getXMLElementName();
		log.debug("Returning nodeLabel: " + nodeLabel);
		return nodeLabel;
	}
	public boolean isLocationOK(FileNode node){
		return fileLocationTest.test(node);
	}
}
