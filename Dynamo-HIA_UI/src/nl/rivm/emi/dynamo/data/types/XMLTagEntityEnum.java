package nl.rivm.emi.dynamo.data.types;

import nl.rivm.emi.dynamo.data.types.atomic.AcutelyFatalType;
import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Alfa;
import nl.rivm.emi.dynamo.data.types.atomic.Amount;
import nl.rivm.emi.dynamo.data.types.atomic.Amounts;
import nl.rivm.emi.dynamo.data.types.atomic.Begin;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.types.atomic.Classes;
import nl.rivm.emi.dynamo.data.types.atomic.CuredFraction;
import nl.rivm.emi.dynamo.data.types.atomic.Cutoff;
import nl.rivm.emi.dynamo.data.types.atomic.Cutoffs;
import nl.rivm.emi.dynamo.data.types.atomic.DALYWeightsFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Disease;
import nl.rivm.emi.dynamo.data.types.atomic.Diseases;
import nl.rivm.emi.dynamo.data.types.atomic.DistributionType;
import nl.rivm.emi.dynamo.data.types.atomic.Duration;
import nl.rivm.emi.dynamo.data.types.atomic.DurationClass;
import nl.rivm.emi.dynamo.data.types.atomic.DynamoClass;
import nl.rivm.emi.dynamo.data.types.atomic.End;
import nl.rivm.emi.dynamo.data.types.atomic.ExessMortFileName;
import nl.rivm.emi.dynamo.data.types.atomic.FlexDex;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewborns;
import nl.rivm.emi.dynamo.data.types.atomic.IncFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Incidence;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRFrom;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRTo;
import nl.rivm.emi.dynamo.data.types.atomic.MaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.MaxAge_LowerCase;
import nl.rivm.emi.dynamo.data.types.atomic.Mean;
import nl.rivm.emi.dynamo.data.types.atomic.MinAge;
import nl.rivm.emi.dynamo.data.types.atomic.MinAge_LowerCase;
import nl.rivm.emi.dynamo.data.types.atomic.Mortalities;
import nl.rivm.emi.dynamo.data.types.atomic.Mortality;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.atomic.NumberOfYears;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.PopFileName;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Prevalence;
import nl.rivm.emi.dynamo.data.types.atomic.Prevalences;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.RR;
import nl.rivm.emi.dynamo.data.types.atomic.RRs;
import nl.rivm.emi.dynamo.data.types.atomic.RandomSeed;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceValue;
import nl.rivm.emi.dynamo.data.types.atomic.RelRiskForDeath;
import nl.rivm.emi.dynamo.data.types.atomic.RelRiskForDisability;
import nl.rivm.emi.dynamo.data.types.atomic.RelativeRisk;
import nl.rivm.emi.dynamo.data.types.atomic.RelativeRiskIndex;
import nl.rivm.emi.dynamo.data.types.atomic.ResultType;
import nl.rivm.emi.dynamo.data.types.atomic.RiskFactor;
import nl.rivm.emi.dynamo.data.types.atomic.RiskFactorTransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.RiskFactors;
import nl.rivm.emi.dynamo.data.types.atomic.Scenario;
import nl.rivm.emi.dynamo.data.types.atomic.Scenarios;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.SexRatio;
import nl.rivm.emi.dynamo.data.types.atomic.SimPopSize;
import nl.rivm.emi.dynamo.data.types.atomic.Size;
import nl.rivm.emi.dynamo.data.types.atomic.Skewness;
import nl.rivm.emi.dynamo.data.types.atomic.StandardDeviation;
import nl.rivm.emi.dynamo.data.types.atomic.StartingYear;
import nl.rivm.emi.dynamo.data.types.atomic.SuccessRate;
import nl.rivm.emi.dynamo.data.types.atomic.TargetMaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.TargetMinAge;
import nl.rivm.emi.dynamo.data.types.atomic.TargetSex;
import nl.rivm.emi.dynamo.data.types.atomic.TimeStep;
import nl.rivm.emi.dynamo.data.types.atomic.TransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Transition;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.Trend;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.Unit;
import nl.rivm.emi.dynamo.data.types.atomic.UnitType;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.VirtualCutoffIndex;
import nl.rivm.emi.dynamo.data.types.atomic.Weight;
import nl.rivm.emi.dynamo.data.types.atomic.Year;
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

public enum XMLTagEntityEnum {
	ACUTELYFATAL((XMLTagEntity) new AcutelyFatalType()), 
	AGE((XMLTagEntity) new Age()), //
	ALFA((XMLTagEntity) new Alfa()), //
	AMOUNT((XMLTagEntity) new Amount()), //
	AMOUNTS((XMLTagEntity) new Amounts()), //
	BEGIN((XMLTagEntity) new Begin()), //
	CAT((XMLTagEntity) new CatContainer()), //
	CLASS((XMLTagEntity) new DynamoClass()), //
	CLASSES((XMLTagEntity) new Classes()), //
	CUREDFRACTION((XMLTagEntity) new CuredFraction()), //
	CUTOFF((XMLTagEntity) new Cutoff()), // NB(mondeelr)
	CUTOFFS((XMLTagEntity) new Cutoffs()), //
	DALYWEIGHTSFILENAME((XMLTagEntity) new DALYWeightsFileName()), //
	DISEASE((XMLTagEntity) new Disease()), //
	DISEASES((XMLTagEntity) new Diseases()), //
	DISTRIBUTIONTYPE((XMLTagEntity) new DistributionType()), //
	DURATION((XMLTagEntity) new Duration()), //
	DURATIONCLASS((XMLTagEntity) new DurationClass()), //
	END((XMLTagEntity) new End()), //
	EXESSMORTFILENAME((XMLTagEntity) new ExessMortFileName()), //
	FLEXDEX((XMLTagEntity) new FlexDex()), //
	HASNEWBORNS((XMLTagEntity) new HasNewborns()), //
	INCFILENAME((XMLTagEntity) new IncFileName()), //
	INCIDENCE((XMLTagEntity) new Incidence()), //
	INDEX((XMLTagEntity) new Index()), //
	ISRRFILENAME((XMLTagEntity) new IsRRFileName()), //
	ISRRFROM((XMLTagEntity) new IsRRFrom()), //
	ISRRTO((XMLTagEntity) new IsRRTo()), //
	MAXAGE((XMLTagEntity) new MaxAge()), //
	MAXAGE_LOWERCASE((XMLTagEntity) new MaxAge_LowerCase()), //
	MEAN((XMLTagEntity) new Mean()), //
	MINAGE((XMLTagEntity) new MinAge()), //
	MINAGE_LOWERCASE((XMLTagEntity) new MinAge_LowerCase()), //
	MORTALITIES((XMLTagEntity) new Mortalities()), //
	MORTALITY((XMLTagEntity) new Mortality()), //
	NAME((XMLTagEntity) new Name()), //
	NUMBER((XMLTagEntity) new Number()), //
	NUMBEROFYEARS((XMLTagEntity) new NumberOfYears()), //
	PERCENTAGE((XMLTagEntity) new Percent()), // 
	POPFILENAME((XMLTagEntity) new PopFileName()), //
	PREVALENCE((XMLTagEntity) new Prevalence()), //
	PREVALENCES((XMLTagEntity) new Prevalences()), //
	PREVFILENAME((XMLTagEntity) new PrevFileName()), //
	PROBABILITY((XMLTagEntity) new Probability()), // 
	RANDOMSEED((XMLTagEntity) new RandomSeed()), //
	REFERENCECLASS((XMLTagEntity) new ReferenceClass()), // 
	REFERENCEVALUE((XMLTagEntity) new ReferenceValue()), // 
	RELATIVERISK((XMLTagEntity) new RelativeRisk()), //
	RELATIVERISKFORDEATH((XMLTagEntity) new RelRiskForDeath()), //
	RELATIVERISKFORDISABILITY((XMLTagEntity) new RelRiskForDisability()), //
	RESULTTYPE((XMLTagEntity) new ResultType()), //
	RISKFACTOR((XMLTagEntity) new RiskFactor()), //
	RISKFACTORS((XMLTagEntity) new RiskFactors()), //
	RISKFACTORTRANSFILENAME((XMLTagEntity) new RiskFactorTransFileName()), //
	RR((XMLTagEntity) new RR()), //
	RRINDEX((XMLTagEntity) new RelativeRiskIndex()), //
	RRS((XMLTagEntity) new RRs()), //
	SCENARIO((XMLTagEntity) new Scenario()), //
	SCENARIOS((XMLTagEntity) new Scenarios()), //
	SEX((XMLTagEntity) new Sex()), //
	SEXRATIO((XMLTagEntity) new SexRatio()), //
	SIMPOPSIZE((XMLTagEntity) new SimPopSize()), //
	SIZE((XMLTagEntity) new Size()), //
	SKEWNESS((XMLTagEntity) new Skewness()), //
	STANDARDVALUE((XMLTagEntity) new Value()), //
	STANDARDDEVIATION((XMLTagEntity) new StandardDeviation()), //
	STARTINGYEAR((XMLTagEntity) new StartingYear()), //
	SUCCESSRATE((XMLTagEntity) new SuccessRate()), //
	TARGETMAXAGE((XMLTagEntity) new TargetMaxAge()), //
	TARGETMINAGE((XMLTagEntity) new TargetMinAge()), //
	TARGETSEX((XMLTagEntity) new TargetSex()), //
	TIMESTEP((XMLTagEntity) new TimeStep()), //
	TRANSFILENAME((XMLTagEntity) new TransFileName()), //
	TRANSITION((XMLTagEntity) new Transition()), //
	TRANSITIONDESTINATION((XMLTagEntity) new TransitionDestination()), //
	TRANSITIONSOURCE((XMLTagEntity) new TransitionSource()), //
	TREND((XMLTagEntity) new Trend()), UNIQUENAME(
			(XMLTagEntity) new UniqueName()), //
	UNIT((XMLTagEntity) new Unit()), //
	UNITTYPE((XMLTagEntity) new UnitType()), VIRTUALCUTOFFINDEX(
			(XMLTagEntity) new VirtualCutoffIndex()), //
	WEIGHT((XMLTagEntity) new Weight()), //
	YEAR((XMLTagEntity) new Year()), //
	// RootElements
	DALYWEIGHTS(new DALYWeights()), //
	DISEASEINCIDENCES(new DiseaseIncidences()), //
	DISEASEPREVALENCES(new DiseasePrevalences()), //
	EXCESSMORTALITY(new ExcessMortality()), //
	NEWBORNS(new Newborns()), //
	OVERALLDALYWEIGHTS(new OverallDALYWeights()), //
	OVERALLMORTALITY(new OverallMortality()), //
	POPULATIONSIZE(new PopulationSize()), //
	RISKFACTORPREVALENCES_CATEGORICAL(new RiskfactorPrevalencesCategorical()), //
	RISKFACTORPREVALENCES_CONTINUOUS(new RiskFactorPrevalencesContinuous()), //
	RISKFACTORPREVALENCES_DURATION(new RiskFactorPrevalencesDuration()), //
	RELATIVERISKSFORDEATH_CATEGORICAL(new RelativeRiskForDeathCategorical()), //
	RELATIVERISKSFORDEATH_COMPOUND(new RelativeRiskForDeathCompound()), //
	RELATIVERISKSFORDEATH_CONTINUOUS(new RelativeRiskForDeathContinuous()), //
	RELATIVERISKSFORDISABILITY_CATEGORICAL(
			new RelativeRiskForDisabilityCategorical()), //
	RELATIVERISKSFORDISABILITY_COMPOUND(new RelativeRiskForDisabilityCompound()), //
	RELATIVERISKSFORDISABILITY_CONTINUOUS(
			new RelativeRiskForDisabilityContinuous()), //
	RELATIVERISKSFROMRISKFACTOR_CATEGORICAL(
			new RelativeRiskFromRiskFactorCategorical()), //
	RELATIVERISKSFROMRISKFACTOR_COMPOUND(
			new RelativeRiskFromRiskFactorCompound()), //
	RELATIVERISKSFROMRISKFACTOR_CONTINUOUS(
			new RelativeRiskFromRiskFactorContinuous()), //
	RELATIVERISKSFROMDISEASES(new RelativeRisksFromDisease()), //
	RISKFACTOR_CATEGORICAL(new RiskfactorCategorical()), //
	RISKFACTOR_COMPOUND(new RiskFactorCompound()), //
	RISKFACTOR_CONTINUOUS(new RiskFactorContinuous()), //
	SIMULATION(new Simulation()), //
	TRANSITIONDRIFT(new TransitionDrift()), //
	TRANSITIONDRIFT_NETTO(new TransitionDriftNetto()), //
	TRANSITIONDRIFT_ZERO(new TransitionDriftZero()), //
	TRANSITIONMATRIX(new TransitionMatrix()), //
	TRANSITIONMATRIX_NETTO(new TransitionMatrixNetto()), //
	TRANSITIONMATRIX_ZERO(new TransitionMatrixZero()); //

	Log log = LogFactory.getLog(this.getClass().getName());

	private final XMLTagEntity theType;

	private XMLTagEntityEnum(XMLTagEntity type) {
		this.theType = type;
	}

	public XMLTagEntity getTheType() {
		return theType;
	}

	public String getElementName() {
		String elementName = theType.getXMLElementName();
		log.debug("Returning nodeLabel: " + elementName);
		return elementName;
	}
}