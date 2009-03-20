package nl.rivm.emi.dynamo.data.types;

import nl.rivm.emi.dynamo.data.types.atomic.Age;
import nl.rivm.emi.dynamo.data.types.atomic.Alfa;
import nl.rivm.emi.dynamo.data.types.atomic.Begin;
import nl.rivm.emi.dynamo.data.types.atomic.CatContainer;
import nl.rivm.emi.dynamo.data.types.atomic.Classes;
import nl.rivm.emi.dynamo.data.types.atomic.Cutoff;
import nl.rivm.emi.dynamo.data.types.atomic.Cutoffs;
import nl.rivm.emi.dynamo.data.types.atomic.DALYWeightsFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Disease;
import nl.rivm.emi.dynamo.data.types.atomic.Diseases;
import nl.rivm.emi.dynamo.data.types.atomic.Duration;
import nl.rivm.emi.dynamo.data.types.atomic.DurationClass;
import nl.rivm.emi.dynamo.data.types.atomic.DynamoClass;
import nl.rivm.emi.dynamo.data.types.atomic.End;
import nl.rivm.emi.dynamo.data.types.atomic.ExessMortFileName;
import nl.rivm.emi.dynamo.data.types.atomic.HasNewborns;
import nl.rivm.emi.dynamo.data.types.atomic.IncFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Index;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRFileName;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRFrom;
import nl.rivm.emi.dynamo.data.types.atomic.IsRRTo;
import nl.rivm.emi.dynamo.data.types.atomic.MaxAge;
import nl.rivm.emi.dynamo.data.types.atomic.Mean;
import nl.rivm.emi.dynamo.data.types.atomic.MinAge;
import nl.rivm.emi.dynamo.data.types.atomic.Name;
import nl.rivm.emi.dynamo.data.types.atomic.Number;
import nl.rivm.emi.dynamo.data.types.atomic.NumberOfYears;
import nl.rivm.emi.dynamo.data.types.atomic.Percent;
import nl.rivm.emi.dynamo.data.types.atomic.PopFileName;
import nl.rivm.emi.dynamo.data.types.atomic.PrevFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Probability;
import nl.rivm.emi.dynamo.data.types.atomic.RR;
import nl.rivm.emi.dynamo.data.types.atomic.RRs;
import nl.rivm.emi.dynamo.data.types.atomic.RandomSeed;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceClass;
import nl.rivm.emi.dynamo.data.types.atomic.ReferenceValue;
import nl.rivm.emi.dynamo.data.types.atomic.ResultType;
import nl.rivm.emi.dynamo.data.types.atomic.RiskFactor;
import nl.rivm.emi.dynamo.data.types.atomic.RiskFactorTransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.Scenario;
import nl.rivm.emi.dynamo.data.types.atomic.Scenarios;
import nl.rivm.emi.dynamo.data.types.atomic.Sex;
import nl.rivm.emi.dynamo.data.types.atomic.SimPopSize;
import nl.rivm.emi.dynamo.data.types.atomic.StartingYear;
import nl.rivm.emi.dynamo.data.types.atomic.SuccessRate;
import nl.rivm.emi.dynamo.data.types.atomic.TargetSex;
import nl.rivm.emi.dynamo.data.types.atomic.TimeStep;
import nl.rivm.emi.dynamo.data.types.atomic.TransFileName;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionDestination;
import nl.rivm.emi.dynamo.data.types.atomic.TransitionSource;
import nl.rivm.emi.dynamo.data.types.atomic.UniqueName;
import nl.rivm.emi.dynamo.data.types.atomic.Unit;
import nl.rivm.emi.dynamo.data.types.atomic.Value;
import nl.rivm.emi.dynamo.data.types.atomic.VirtualCutoffIndex;
import nl.rivm.emi.dynamo.data.types.atomic.Year;
import nl.rivm.emi.dynamo.data.types.atomic.base.XMLTagEntity;

public enum XMLTagEntityEnum {
	AGE((XMLTagEntity) new Age()), //
	ALFA((XMLTagEntity) new Alfa()), //
	BEGIN((XMLTagEntity) new Begin()), //
	CAT((XMLTagEntity) new CatContainer()), //
	CLASS((XMLTagEntity) new DynamoClass()), //
	CLASSES((XMLTagEntity) new Classes()), //
/*	CUTOFF((XMLTagEntity) new Cutoff()), // NB(mondeelr)
	CUTOFFS((XMLTagEntity) new Cutoffs()), // */
	DALYWEIGHTSFILENAME((XMLTagEntity) new DALYWeightsFileName()), //
	DISEASE((XMLTagEntity) new Disease()), //
	DISEASES((XMLTagEntity) new Diseases()), //
	DURATION((XMLTagEntity) new Duration()), //
	DURATIONCLASS((XMLTagEntity) new DurationClass()), //
	END((XMLTagEntity) new End()), //
	EXESSMORTFILENAME((XMLTagEntity) new ExessMortFileName()), //
	HASNEWBORNS((XMLTagEntity) new HasNewborns()), //
	INCFILENAME((XMLTagEntity) new IncFileName()), //
	INDEX((XMLTagEntity) new Index()), //
	ISRRFILENAME((XMLTagEntity) new IsRRFileName()), //
	ISRRFROM((XMLTagEntity) new IsRRFrom()), //
	ISRRTO((XMLTagEntity) new IsRRTo()), //
	MAXAGE((XMLTagEntity) new MaxAge()), //
	MEAN((XMLTagEntity) new Mean()), //
	MINAGE((XMLTagEntity) new MinAge()), //
	NAME((XMLTagEntity) new Name()), //
	NUMBER((XMLTagEntity) new Number()), //
	NUMBEROFYEARS((XMLTagEntity) new NumberOfYears()), //
	PERCENTAGE((XMLTagEntity) new Percent()), // 
	POPFILENAME((XMLTagEntity) new PopFileName()), //
	PREVFILENAME((XMLTagEntity) new PrevFileName()), //
	PROBABILITY((XMLTagEntity) new Probability()), // 
	RANDOMSEED((XMLTagEntity) new RandomSeed()), //
	REFERENCECLASS((XMLTagEntity) new ReferenceClass()), // 
	REFERENCEVALUE((XMLTagEntity) new ReferenceValue()), // 
	RESULTTYPE((XMLTagEntity) new ResultType()), //
	RISKFACTOR((XMLTagEntity) new RiskFactor()), //
	RISKFACTORTRANSFILENAME((XMLTagEntity) new RiskFactorTransFileName()), //
	RR((XMLTagEntity) new RR()), //
	RRS((XMLTagEntity) new RRs()), //
	SCENARIO((XMLTagEntity) new Scenario()), //
	SCENARIOS((XMLTagEntity) new Scenarios()), //
	SEX((XMLTagEntity) new Sex()), //
	SIMPOPSIZE((XMLTagEntity) new SimPopSize()), //
	STANDARDVALUE((XMLTagEntity) new Value()), //
	STARTINGYEAR((XMLTagEntity) new StartingYear()), //
	SUCCESSRATE((XMLTagEntity) new SuccessRate()), //
	TARGETSEX((XMLTagEntity) new TargetSex()), //
	TIMESTEP((XMLTagEntity) new TimeStep()), //
	TRANSFILENAME((XMLTagEntity) new TransFileName()), //
	TRANSITIONDESTINATION((XMLTagEntity) new TransitionDestination()), //
	TRANSITIONSOURCE((XMLTagEntity) new TransitionSource()), //
	/* TREND((XMLTagEntity)new Trend()), // NB(mondeelr) */
	UNIQUENAME((XMLTagEntity) new UniqueName()), //
	UNIT((XMLTagEntity) new Unit()), //
	VIRTUALCUTOFFINDEX((XMLTagEntity) new VirtualCutoffIndex()), //
	YEAR((XMLTagEntity) new Year());

	private final XMLTagEntity theType;

	private XMLTagEntityEnum(XMLTagEntity type) {
		this.theType = type;
	}

	public XMLTagEntity getTheType() {
		return theType;
	}

	public String getElementName() {
		return theType.getXMLElementName();
	}
}