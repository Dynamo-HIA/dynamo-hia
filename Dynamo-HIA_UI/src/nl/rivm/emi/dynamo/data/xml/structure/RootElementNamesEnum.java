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
import nl.rivm.emi.dynamo.data.types.interfaces.RootElementType;
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
import nl.rivm.emi.dynamo.data.types.root.RiskfactorCategorical;
import nl.rivm.emi.dynamo.data.types.root.RiskfactorPrevalencesCategorical;
import nl.rivm.emi.dynamo.data.types.root.Simulation;
import nl.rivm.emi.dynamo.data.types.root.TransitionDrift;
import nl.rivm.emi.dynamo.data.types.root.TransitionDriftNetto;
import nl.rivm.emi.dynamo.data.types.root.TransitionDriftZero;
import nl.rivm.emi.dynamo.data.types.root.TransitionMatrix;
import nl.rivm.emi.dynamo.data.types.root.TransitionMatrixNetto;
import nl.rivm.emi.dynamo.data.types.root.TransitionMatrixZero;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum RootElementNamesEnum /* implements RootElementType */{

	/**
	 * This represents the list of root element names, used in the xml, xsd, and
	 * application
	 */

	SIMULATION(new Simulation()), // Comment to block reformatting.
	POPULATIONSIZE(new PopulationSize()), //
	OVERALLMORTALITY(new OverallMortality()), //
	NEWBORNS(new Newborns()), //
	OVERALLDALYWEIGHTS(new OverallDALYWeights()), //
	RISKFACTOR_CATEGORICAL(new RiskfactorCategorical()), //
	RISKFACTOR_CONTINUOUS(new RiskFactorContinuous()), //
	RISKFACTOR_COMPOUND(new RiskFactorCompound()), //
	TRANSITIONMATRIX(new TransitionMatrix()), //
	TRANSITIONMATRIX_ZERO(new TransitionMatrixZero()), //
	TRANSITIONMATRIX_NETTO(new TransitionMatrixNetto()), //
	TRANSITIONDRIFT(new TransitionDrift()), //
	TRANSITIONDRIFT_ZERO(new TransitionDriftZero()), //
	TRANSITIONDRIFT_NETTO(new TransitionDriftNetto()), //
	RISKFACTORPREVALENCES_CATEGORICAL(new RiskfactorPrevalencesCategorical()), //
	RISKFACTORPREVALENCES_CONTINUOUS(new RiskFactorPrevalencesContinuous()), //
	RISKFACTORPREVALENCES_DURATION_UNIFORM(new RiskFactorPrevalencesDuration()), //
	RISKFACTORPREVALENCES_DURATION(new RiskFactorPrevalencesDuration()), //
	RELATIVERISKSFORDEATH_CATEGORICAL(new RelativeRiskForDeathCategorical()), //
	RELATIVERISKSFORDEATH_CONTINUOUS(new RelativeRiskForDeathContinuous()), //
	RELATIVERISKSFORDEATH_COMPOUND(new RelativeRiskForDeathCompound()), //
	RELATIVERISKSFORDISABILITY_CATEGORICAL(new RelativeRiskForDisabilityCategorical()), //
	RELATIVERISKSFORDISABILITY_CONTINUOUS(new RelativeRiskForDisabilityContinuous()), //
	RELATIVERISKSFORDISABILITY_COMPOUND(new RelativeRiskForDisabilityCompound()), //
	DISEASEPREVALENCES(new DiseasePrevalences()), //
	DISEASEINCIDENCES(new DiseaseIncidences()), //
	EXCESSMORTALITY(new ExcessMortality()), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL(
			new RelativeRiskFromRiskFactorCategorical()), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS(new RelativeRiskFromRiskFactorContinuous()), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND(new RelativeRiskFromRiskFactorCompound()), //
	RELATIVERISKSFROMDISEASE(new RelativeRisksFromDisease()), //
	DALYWEIGHTS(new DALYWeights());

	Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * The enum
	 */
	// private String rootElementName;
	// private RootElementNamesEnum(String theRootElementName) {
	// this.rootElementName = theRootElementName;
	// }
	//
	// public String getNodeLabel() {
	// return rootElementName;
	// }
	XMLTagEntity theType;

	private RootElementNamesEnum(XMLTagEntity type) {
		theType = type;
	}

	public String getNodeLabel() {
		String nodeLabel = ((XMLTagEntity)theType).getXMLElementName();
		log.debug("Returning nodeLabel: " + nodeLabel);
		return nodeLabel;
	}
}
