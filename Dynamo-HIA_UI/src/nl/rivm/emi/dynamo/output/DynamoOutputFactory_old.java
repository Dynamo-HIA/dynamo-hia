package nl.rivm.emi.dynamo.output;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.estimation.DiseaseClusterStructure;
import nl.rivm.emi.dynamo.estimation.NettTransitionRateFactory;
import nl.rivm.emi.dynamo.estimation.ScenarioInfo;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author boshuizh <br/>
 *         Container Class for the results of a simulation.<br/>
 * 
 *         The results are stored in different arrays with summation data to
 *         keep a clear view of things.<br/>
 *         Most summary arrays are recalculated each time they are needed, using
 *         a getXXX method, and are no longer fields. Only the most detailed
 *         arrays are kept as fields.<br/>
 *         Most arrays are by sex, timestep and scenario. Some are by risk or by
 *         age or both (indicated by their names)<br/>
 * <br/>
 * 
 *         p=proportion (of age/risk/sex class): these are temporary arrays not
 *         intended for the users, as for proportions it is always difficult to
 *         know what the denominator is<br/>
 *         n= total number with particular characterisitics in population <br/>
 *         nInSimulation= total numbers with the characteristics in the
 *         simulation the ones with JAVADOC documentation are suitable for
 *         outside use and have getters / sequence of indexes: scenario, time ,
 *         disease, risk class, age, and sex<br/>
 */
public class DynamoOutputFactory_old implements Serializable {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.DynamoOutputFactory");

	/**
	 * Summary array of simulated numbers: index=sex
	 */
	transient int[] nInSimulation = new int[2]; /* index: sex */
	/**
	 * Summary array of simulated numbers: index=age, sex
	 */
	transient int[][] nInSimulationByAge;
	/**
	 * Summary array of simulated numbers of newborns: index=age at the end of
	 * simulation, sex
	 */
	transient int[][] nNewBornsInSimulationByAge;
	/**
	 * Summary array of simulated numbers: index=riskclass, age, sex
	 */
	transient int[][][] nInSimulationByRiskClassByAge;
	/**
	 * Summary array of simulated numbers: index=riskclass, duration class, age,
	 * sex
	 */
	transient int[][][][] nInSimulationByRiskClassAndDurationByAge;

	/**
	 * Number of survivors in scenario and each diseaseState. Indexes: scenario,
	 * time, diseaseState, riskclass at beginning of simulation, age at
	 * beginning of simulation and sex
	 * 
	 */

	private double nDiseaseStateByOriRiskClassByOriAge[][][][][][];

	/* these are a temporary array for making nDiseaseStateByRiskClassByAge */
	/*
	 * NB this array is reused, so it contains something different before and
	 * after applying method makeSummaryArrays
	 */
	transient private double pDiseaseStateByRiskClassByAge[][][][][][];

	transient private double pDiseaseStateByOriRiskClassByAge[][][][][][];
	/**
	 * Number of survivors in each diseaseState by scenario, time, disease
	 * state, risk class, age and sex
	 */
	private double nDiseaseStateByRiskClassByAge[][][][][][];

	/**
	 * Percentage disability (or daly) by age and riskclass at start of
	 * simulation
	 */
	transient private double[][][][][] pDisabilityByOriRiskClassByOriAge;
	/**
	 ** Percentage disability (or daly) by age and riskclass during simulation
	 */
	transient private double[][][][][] pDisabilityByRiskClassByAge;

	/*
	 * temporary arrays that contain simulated survival by risk class by Age;
	 * indexes are: scenario, time, risk class, age, and sex NB this array is
	 * reused, so it contains something different before and after applying
	 * method makeSummaryArrays
	 */

	transient private double[][][][][] pSurvivalByRiskClassByAge;

	/**
	 * Average value of risk class by Age; indexes are: scenario time risk
	 * class, age, and sex
	 * 
	 * 
	 */
	private double[][][][][] meanRiskByRiskClassByAge;

	/**
	 * Average value of risk class by the age at the start of simulation within
	 * a category of the riskclass; indexes are: scenario time risk class, age,
	 * and sex
	 * 
	 * 
	 */
	private double[][][][][] meanRiskByOriRiskClassByOriAge;

	/*
	 * This summary array is an exception to the rule that only detailed arrays
	 * are fields as for riskfactortype=2 means can more easily be calculated
	 * from crude data.<br/> average value of riskvalue by Age; indexes are:
	 * scenario, time age, and sex
	 */
	private double[][][][] meanRiskByAge;

	/**
	 * Number in risk class by Age; indexes are: scenario, time risk class age
	 * and sex
	 */

	private double[][][][][] nPopByRiskClassByAge;
	/**
	 * Number in risk class by Age; indexes are: scenario, time risk class age
	 * and sex
	 */

	private double[][][][][] nPopByOriRiskClassByOriAge;

	/*
	 * temporary arrays with crude data from the one-for-all-scenarios
	 * population
	 */

	/* these data are copied by the constructor from the object "scenario.info" */
	DiseaseClusterStructure[] structure;
	private int riskType;
	private int nScen;
	private int nPopulations;
	private int stepsInRun;
	private int startYear;
	private int nDiseases;
	private int nRiskFactorClasses;
	private int nDiseaseStates;
	/**
	 * Details indicates whether information should be written on all disease
	 * combinations (true) or only per disease (marginals, when false
	 */

	private boolean details;
	private boolean[] scenInitial;
	private boolean[] scenTrans;
	private float[] cutoffs;
	private int durationClass;
	private boolean withNewborns;
	/*
	 * this array indicates how a population should be handled false= direct ;
	 * true= as a one-simulation for all scenarios population
	 */
	private boolean[] isOneScenPopulation;
	private boolean oneScenPopulation;

	private float[][] populationSize;
	private int[] newborns;
	private float mfratio;
	String[] riskClassnames;
	/**
	 * For each scenario the new prevalence rates
	 */
	private float[][][][] newPrevalence;
	/**
	 * The prevalence rates of the reference scenario
	 */
	private float[][][] oldPrevalence;
	/**
	 * The prevalence rates of the durationclasses in the reference scenario
	 */
	private float[][][] oldDurationNumbers;
	private String[] stateNames;
	private String[] diseaseNames;
	private String[] scenarioNames;
	private int nDim = 106;
	private float[][] baselineAbility;
	private float[][][] diseaseAbility;
	private float[][][] relRiskAbilityCat;
	private float[][] relRiskAbilityCont;
	private float[][] relRiskAbilityBegin;
	private float[][] relRiskAbilityEnd;
	private float[][] alfaAbility;
	private float referenceRiskFactorValue = 0;
	/**
	 * Succesrate is the successrate of the intervention can be reset at a
	 * different value for obtaining new results without having to redo the
	 * simulation. Set this value to the succesrate in percent (value between 0
	 * and 100)
	 */
	private float[] succesrate = null;
	/**
	 * The lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private int[] minAge = null;
	/**
	 * Indicates if the intervention is applied to women. It can be reset
	 * at a different value for obtaining new results without having to redo the
	 * simulation
	 */

	private boolean[] inWomen = null;
	/**
	 * Indicates if the intervention is applied to men. It can be reset
	 * at a different value for obtaining new results without having to redo the
	 * simulation
	 */

	private boolean[] inMen = null;
	/**
	 * The upper bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private int[] maxAge = null;

	/**
	 * The lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private int minAgeInSimulation = 100;
	/**
	 * The upper bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private int maxAgeInSimulation = 0;

	/**
	 * Indicates the first scenario that is simulated by a
	 * particular population
	 */
	private int[] popToScenIndex;
	/**
	 * Indicates whether the continuous variable is categorized in
	 * the output
	 */
	boolean categorized = true;

	transient private double[][][][][] pSurvivalByOriRiskClassByOriAge;

	/**
	 * The constructor initializes the fields (arrays with all values==0), and
	 * copies the information from the scenarioInfo object to the fields in this
	 * object. It also copies the scenario name, as it will write results to the
	 * results directory Secondly, it takes the simulated population and makes
	 * this into summary arrays
	 * 
	 * @param scenInfo
	 * @param pop
	 *            simulated population
	 * @throws DynamoScenarioException
	 * @throws DynamoOutputException
	 *             when newborns are not present with the right starting year
	 */
	public DynamoOutputFactory_old(ScenarioInfo scenInfo, Population[] pop)
			throws DynamoScenarioException, DynamoOutputException {

		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */

		initializeClassInfo(scenInfo);
		extractArraysFromPopulations(pop);
		makeArraysWithNumbers();

	}

	/*
	 * code for setting line width XYLineAndShapeRenderer renderer = new
	 * XYLineAndShapeRenderer(); renderer.setSeriesStroke(number of series, new
	 * BasicStroke(2.0f)). plot.setRenderer(renderer);
	 */

	/**
	 * @param scenInfo
	 * @param simName
	 * @throws DynamoOutputException
	 */
	private void initializeClassInfo(ScenarioInfo scenInfo)
			throws DynamoOutputException {

		setRiskType(scenInfo.getRiskType());
		setNScen(scenInfo.getNScenarios());
		this.durationClass = scenInfo.getIndexDurationClass();
		this.scenInitial = scenInfo.getInitialPrevalenceType();
		this.scenTrans = scenInfo.getTransitionType();
		this.withNewborns = scenInfo.isWithNewBorns();
		/*
		 * this is an indicator that indicates whether one-for-all populations
		 * exist at all
		 */
		this.oneScenPopulation = false;
		this.isOneScenPopulation = scenInfo.getIsOneScenPopulation();
		for (int i = 0; i < this.isOneScenPopulation.length; i++)
			if (this.isOneScenPopulation[i])
				this.oneScenPopulation = true;
		this.popToScenIndex = scenInfo.getPopToScenIndex();
		setSuccesrate(scenInfo.getSuccesrate());
		this.inMen = scenInfo.getInMen();
		this.inWomen = scenInfo.getInWomen();
		this.minAge = scenInfo.getMinAge();
		this.maxAge = scenInfo.getMaxAge();
		this.scenarioNames = new String[scenInfo.getScenarioNames().length + 1];
		this.scenarioNames[0] = "reference scenario";

		for (int i = 1; i <= scenInfo.getScenarioNames().length; i++)
			this.scenarioNames[i] = scenInfo.getScenarioNames()[i - 1];
		this.cutoffs = scenInfo.getCutoffs();

		this.nPopulations = scenInfo.getNPopulations();

		/* calculate the number of one-population-for-all-scenarios situation */

		setStepsInRun(scenInfo.getYearsInRun());
		setStructure(scenInfo.getStructure());
		setNDiseases(scenInfo.getStructure());
		setNDiseaseStates(scenInfo.getStructure());

		this.baselineAbility = scenInfo.getBaselineAbility();
		this.diseaseAbility = scenInfo.getDiseaseAbility();
		this.relRiskAbilityCat = scenInfo.getRelRiskAbilityCat();
		this.relRiskAbilityCont = scenInfo.getRelRiskAbilityCont();
		this.relRiskAbilityBegin = scenInfo.getRelRiskAbilityBegin();
		this.relRiskAbilityEnd = scenInfo.getRelRiskAbilityEnd();
		this.alfaAbility = scenInfo.getAlfaAbility();
		this.referenceRiskFactorValue = scenInfo.getReferenceRiskFactorValue();

		this.stateNames = new String[this.nDiseaseStates];
		this.diseaseNames = new String[this.nDiseases];
		int currentDis = 0;
		int currentState = 0;
		if (this.nDiseases > 0)
			for (int c = 0; c < this.structure.length; c++) {
				if (this.structure[c].getNInCluster() > 1
						&& !this.structure[c].isWithCuredFraction())
					for (int i = 1; i < Math.pow(this.structure[c]
							.getNInCluster(), 2); i++) {
						this.stateNames[currentState] = "";
						for (int d1 = 0; d1 < this.structure[c].getNInCluster(); d1++) {

							if ((i & (1 << d1)) == (1 << d1))
								if (this.stateNames[currentState] == "")
									this.stateNames[currentState] = this.structure[c]
											.getDiseaseName().get(d1);
								else
									this.stateNames[currentState] = this.stateNames[currentState]
											+ "+"
											+ this.structure[c]
													.getDiseaseName().get(d1);
						}
						currentState++;
					}

				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {
					this.diseaseNames[currentDis] = this.structure[c]
							.getDiseaseName().get(d);
					currentDis++;
					if (this.structure[c].getNInCluster() == 1
							|| this.structure[c].isWithCuredFraction()) {

						this.stateNames[currentState] = this.structure[c]
								.getDiseaseName().get(d);
						currentState++;
					}

				}
			}

	//	this.details = scenInfo.isDetails();

		this.startYear = scenInfo.getStartYear();
		this.populationSize = scenInfo.getPopulationSize();
		int newbornStart = scenInfo.getNewbornStartYear();
		if (newbornStart == this.startYear)
			this.newborns = scenInfo.getNewborns();
		else if (newbornStart < this.startYear && this.withNewborns) {
			int nYears = scenInfo.getNewborns().length;
			int difference = this.startYear - newbornStart;
			this.newborns = new int[nYears - difference];
			int[] oldData = scenInfo.getNewborns();
			for (int year = 0; year < scenInfo.getNewborns().length
					- difference; year++)
				this.newborns[year] = oldData[year + difference];
		} else if (newbornStart > this.startYear && this.withNewborns) {
			throw new DynamoOutputException(
					"first year with newborns is larger than first year in simulation");
		}
		/*
		 * in case the length of newborns is shorter than the number of newborn
		 * generations needed, use the last newborn number for all further
		 * generations
		 */
		if (this.withNewborns && this.stepsInRun > this.newborns.length) {
			int[] temp = new int[this.stepsInRun];
			for (int y = 0; y < this.newborns.length; y++)
				temp[y] = this.newborns[y];
			for (int y = this.newborns.length; y < this.stepsInRun; y++)
				temp[y] = this.newborns[this.newborns.length - 1];
			this.newborns = temp;
		}
		this.mfratio = scenInfo.getMaleFemaleRatio();
		this.riskClassnames = scenInfo.getRiskClassnames();
		if (this.riskType != 2) {
			this.oldPrevalence = scenInfo.getOldPrevalence();
			this.newPrevalence = scenInfo.getNewPrevalence();
		}
		this.oldDurationNumbers = scenInfo.getOldDurationClasses();

		// TODO remove this temporary solution
		if (this.newPrevalence == null) {
			this.newPrevalence = new float[1][][][];
			this.newPrevalence[0] = this.oldPrevalence;
		}

		if (getRiskType() == 1 || getRiskType() == 3)
			setNRiskFactorClasses(scenInfo.getRiskClassnames().length);
		else {

			if (this.cutoffs == null)
				setNRiskFactorClasses(5);
			/*
			 * NB the names and cutoffs are taken from the data, this is part of
			 * the method extractArraysFroPopulations
			 */
			else {
				setNRiskFactorClasses(this.cutoffs.length + 1);
				this.riskClassnames = new String[getNRiskFactorClasses()];
				for (int i = 0; i < this.cutoffs.length; i++) {
					if (i > 0)
						this.riskClassnames[i] = this.cutoffs[i - 1] + "-"
								+ this.cutoffs[i];
				}
				this.riskClassnames[0] = "<" + this.cutoffs[0];
				this.riskClassnames[this.cutoffs.length] = ">"
						+ this.cutoffs[this.cutoffs.length - 1];

			}
			;
		}
		/*
		 * as the starting situation is also part of the results, the dimension
		 * of the arrays should be stepsInRun+1
		 */
		int nClasses = getNRiskFactorClasses();
		this.meanRiskByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDim][2];
		this.pSurvivalByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.pSurvivalByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		this.pDisabilityByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.pDisabilityByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];

		this.nPopByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		this.nPopByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		this.meanRiskByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][nClasses][96][2];
		if (this.riskType == 2)
			this.meanRiskByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];
		if (this.riskType == 3)
			this.meanRiskByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][nClasses][96 + this.stepsInRun][2];

		/*
		 * NB the dimension can be nClasses (nClasses-1) but this makes life
		 * more difficult for now we suppose we have enough room for doing it
		 * this way
		 */
		this.pDiseaseStateByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseaseStates][nClasses][96 + this.stepsInRun][2];
		this.nDiseaseStateByRiskClassByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseaseStates][nClasses][96 + this.stepsInRun][2];
		this.pDiseaseStateByOriRiskClassByAge = new double[this.nScen + 1][this.nDim][this.nDiseaseStates][nClasses][96][2];
		this.nDiseaseStateByOriRiskClassByOriAge = new double[this.nScen + 1][this.nDim][this.nDiseaseStates][nClasses][96][2];
	}

	/**
	 * this method extracts summary arrays from the simulated population the
	 * arrays are fields of the object DynamoOutPutFactory, named
	 * pPopByRiskClassByAge, pDiseaseStateByRiskClassByAge
	 * pDiseaseByRiskClassByAge and meanRiskByRiskClassByAge. The first three
	 * contain the proportion of the initial population (of the particular age
	 * and ses) that is resp. - still alive and in a particular riskclasse -
	 * still alive and in a particular riskclass and in a particular diseases
	 * state, or - still alive and in a particular riskclass and has a
	 * particular disease the last one give the average riskfactor state of the
	 * persons still alive (with a particular age and sex and possibly
	 * riskfactor state )
	 * 
	 * @param pop
	 *            simulated population
	 * 
	 * 
	 * @throws DynamoScenarioException
	 * 
	 */
	public void extractArraysFromPopulations(Population[] pop)
			throws DynamoScenarioException {

		// TODO newborns weighting
		// TODO weighting of initial scenario that is not a one-for-all scenario
		/*
		 * check if all the bookkeeping with number of populations agrees with
		 * the real number of populations Is redundant if correct, but just to
		 * be sure
		 */
		if (pop.length != this.nPopulations)
			throw new DynamoScenarioException(
					"something goes wrong "
							+ " in calculating the number of simulated populations. \nThere are "
							+ this.nPopulations
							+ " populations expected but only " + pop.length
							+ " found");

		if (this.riskType == 2 && this.cutoffs == null)
			setCutoffs(pop);

		/* set dimension of arrays */

		/* intialized arrays */
		/* these arrays will contain the results of the all-for-one population */
		/*
		 * plus the data from the reference population (when to (index2) equal
		 * to from (index3)) the latter are the data for individual with
		 * riskfactor to=from at the start of the simulation
		 * 
		 * indexes are: simulation year,[diseasestate] riskfactor clas
		 * from(=orginal), riskfactor class to (=in scenario),current riskfactor
		 * class, age, sex
		 */
		double[][][][][][] pSurvivalByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][2];
		double[][][][][][] pDisabilityByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][2];

		double[][][][][][][] pDiseaseStateByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nDiseaseStates][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][2];
		double[][][][][][] pSurvivalByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][96][2];
		double[][][][][][] pDisabilityByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][96][2];

		double[][][][][][][] pDiseaseStateByOriRiskClassByAge_scen = new double[this.nDim][this.nDiseaseStates][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][96][2];

		double[][][][][][] MeanRiskByRiskClassByAge_scen = null;
		double[][][][][][] MeanRiskByOriRiskClassByAge_scen = null;
		if (this.riskType == 3) {
			MeanRiskByRiskClassByAge_scen = new double[this.stepsInRun + 1][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nDim][2];
			MeanRiskByOriRiskClassByAge_scen = new double[this.nDim][this.nRiskFactorClasses][this.nRiskFactorClasses][this.nRiskFactorClasses][96][2];
		}
		int sexIndex = 0;
		int ageIndex = 0;
		this.nInSimulation = new int[2];
		this.nInSimulationByAge = new int[this.nDim][2];
		this.nNewBornsInSimulationByAge = new int[this.stepsInRun][2];
		this.nInSimulationByRiskClassByAge = new int[this.nRiskFactorClasses][this.nDim][2];
		this.nInSimulationByRiskClassAndDurationByAge = new int[this.nRiskFactorClasses][100][this.nDim][2];

		double weight[][][] = new double[this.nRiskFactorClasses][96][2];
		double[][][] weight2 = new double[100][96][2];

		float[] compoundData;
		float survival;

		/*
		 * for categorical / compound variables: get information on the number
		 * of simulation subjects per risk class and use these to calculate
		 * weighting factors for individuals in order to calculated all outcomes
		 * valid for a population with the distribution of risk factors given in
		 * oldPrevalence
		 * 
		 * 
		 * 
		 * for all riskfactors: get the numbers of simulated persons
		 */

		extractNumberInSimulationFromPopulation(pop);
		if (minAgeInSimulation == 100)
			throw new DynamoScenarioException(
					" empty populations in simulation");

		/*
		 * calculated weights : weighting factors for individuals in order to
		 * calculated all outcomes valid for a population with the distribution
		 * of risk factors given in oldPrevalence
		 * 
		 * the numbers sum up to nSim= nInSimulationByAge at the start of
		 * simulation
		 */

		for (int s = 0; s < 2; s++)
			for (int age = 0; age < 96; age++) {

				for (int r = 0; r < this.nRiskFactorClasses; r++)
					if (this.riskType != 2) {
						weight[r][age][s] = this.oldPrevalence[age][s][r]
								* this.nInSimulationByAge[age][s]
								/ this.nInSimulationByRiskClassByAge[r][age][s];
						if (this.riskType == 3 && r == this.durationClass)
							for (int duur = 0; duur < this.oldDurationNumbers[age][s].length; duur++)
								weight2[duur][age][s] = this.oldPrevalence[age][s][r]
										* this.oldDurationNumbers[age][s][duur]
										* this.nInSimulationByAge[age][s]
										/ this.nInSimulationByRiskClassAndDurationByAge[r][duur][age][s];
					} else
						weight[r][age][s] = 1;

			}

		/*
		 * 
		 * 
		 * extract the information from the simulated populations
		 */

		int from = 0;
		int to = 0;
		String indLabel;
		String delims = "[_]";
		String[] tokens = new String[4];

		double[] weightOfReferenceIndividual = new double[pop[0].size()];

		for (int thisPop = 0; thisPop < this.nPopulations; thisPop++) {
			int currentIndividualNo = -1;
			Iterator<Individual> individualIterator = pop[thisPop].iterator();

			/*
			 * make detailed arrays summing the data for sex/age/year/risk class
			 * combinations
			 */

			/* start with reading the data from the population */
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				currentIndividualNo++;
				double weightOfIndividual = 1;
				if (thisPop == 0)
					weightOfReferenceIndividual[currentIndividualNo] = 1;

				int riskClassAtStart = -1;

				int ageAtStart = (int) Math.round(((Float) individual.get(1)
						.getValue(0)));

				int nSteps = 105 - ageAtStart;
				if (ageAtStart < 0)
					nSteps = this.stepsInRun + 1;

				if (ageAtStart == 27) {
					int ii = 0;
					ii++;
				}

				for (int stepCount = 0; stepCount < nSteps; stepCount++) {
					/*
					 * get the information of this individual at the stepCount
					 * step for the simulation
					 */
					ageIndex = (int) Math.round(((Float) individual.get(1)
							.getValue(stepCount)));
					sexIndex = (int) (Integer) individual.get(2).getValue(
							stepCount);

					if (ageIndex >= 0) {
						int riskFactor = 0;
						float riskValue = 0;
						int riskDurationValue = 0;

						if (this.riskType != 2) {
							riskFactor = (int) (Integer) individual.get(3)
									.getValue(stepCount);

						} else {
							riskValue = (float) (Float) individual.get(3)
									.getValue(stepCount);
							int i = 0;
							if (this.riskClassnames.length > 1) {
								if (riskValue <= this.cutoffs[0])
									riskFactor = 0;

								else {
									for (i = 1; i < this.cutoffs.length; i++) {
										if (riskValue <= this.cutoffs[i]
												&& riskValue > this.cutoffs[i - 1])
											break;
									}

									riskFactor = i;
									/* just to be sure that it goes OK: */
									if (riskValue > this.cutoffs[this.cutoffs.length - 1])
										riskFactor = this.cutoffs.length;
								}
							} else { /*
									 * only one single value present for
									 * riskValue
									 */
								riskFactor = 0;

							}
						}

						if (this.riskType == 3)

							riskDurationValue = Math
									.round((float) (Float) individual.get(4)
											.getValue(stepCount));

						/*
						 * calculate the age and riskclass at start NB: this
						 * will be missing for newborns; they will not be
						 * included in the summary arrays for calculating
						 * life-expectancy
						 */

						if (stepCount == 0) {
							ageAtStart = ageIndex;

						}
						/* this will also work for newborns */
						if (riskClassAtStart == -1) {
							riskClassAtStart = riskFactor;

						}

						/*
						 * the weighting is meant to make the distribution of
						 * the categorical risk factor in the reference scenario
						 * equal to what is given as distribution (despite the
						 * fact that because of a finite number of simulated
						 * cases, the distribution in the simulated population
						 * is different. the weight of an individual should be
						 * the same in each scenario. So the weight is
						 * determined by the value of the riskfactor in the
						 * reference scenario
						 */
						/*
						 * In changing initial prevalence scenarios this is the
						 * value of "from". In changing transition rate
						 * scenarios the initial distribution is the same in the
						 * reference population and the scenario population
						 */

						if (stepCount == 0 && this.riskType != 2) {
							if (ageIndex > 100)
								log.debug(stepCount + " " + riskFactor + " "
										+ ageIndex + " " + sexIndex);
						}
						/* is start year for this individual */
						if ((stepCount == 0 || ageIndex == 0)
								&& this.riskType != 2) {
							if (thisPop == 0)
								weightOfReferenceIndividual[currentIndividualNo] = weight[riskFactor][ageIndex][sexIndex];
							weightOfIndividual = weightOfReferenceIndividual[currentIndividualNo];
						}
						if ((stepCount == 0 || ageIndex == 0)
								&& this.riskType == 3
								&& riskFactor == this.durationClass) {
							if (thisPop == 0)
								weightOfReferenceIndividual[currentIndividualNo] = weight2[riskDurationValue][ageIndex][sexIndex];
							weightOfIndividual = weightOfReferenceIndividual[currentIndividualNo];
						}
						// log.fatal(ageIndex+" =age; stepCount="+stepCount);
						if (this.riskType == 3)
							compoundData = ((CompoundCharacteristicValue) individual
									.get(5)).getUnwrappedValue(stepCount);

						else
							compoundData = ((CompoundCharacteristicValue) individual
									.get(4)).getUnwrappedValue(stepCount);
						survival = compoundData[getNDiseaseStates() - 1];
						double daly = makeDaly(compoundData, riskFactor,
								riskValue, riskDurationValue, ageIndex,
								sexIndex);
						/*
						 * if the population is a one-for-all population, read
						 * the from (risk factor without the scenario change)
						 * and to (with the scenario change) from the label of
						 * the individual
						 */

						if (thisPop > 0)
							if (this.isOneScenPopulation[thisPop]) {
								indLabel = individual.getLabel();
								tokens = indLabel.split(delims);
								from = Integer.parseInt(tokens[2]);
								to = Integer.parseInt(tokens[3]);
								/*
								 * overwrite the weight with the weight of the
								 * reference population
								 */
								if (stepCount == 0 || ageIndex == 0) {

									weightOfIndividual = weight[from][ageIndex][sexIndex];

								}

							}

						if (ageAtStart == 3 && sexIndex == 0 && stepCount < 3
								&& thisPop > 0)
							log
									.debug("a,g,step,r: " + ageIndex + " "
											+ sexIndex + " " + stepCount + " "
											+ riskFactor + " weight "
											+ weightOfIndividual + " surv: "
											+ survival);

						/*
						 * add the data read to the summary arrays
						 * 
						 * start with adding survival to the summary survival
						 * arrays
						 */
						/*
						 * for a scenario population
						 */
						if (thisPop > 0) {

							/*
							 * for a all-for-one population
							 */

							if (this.isOneScenPopulation[thisPop]) {
								if (stepCount <= this.stepsInRun) {
									pSurvivalByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival;
									pDisabilityByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival * daly;
								}
								if (ageAtStart >= 0) {
									pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
											* survival;
									pDisabilityByOriRiskClassByAge_scen[stepCount][from][to][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
											* survival * daly;
								}
								if (this.riskType == 3) {
									if (stepCount <= this.stepsInRun)
										MeanRiskByRiskClassByAge_scen[stepCount][from][to][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
								}

								/* for a "one-for-one" scenario */

							} else {
								if (stepCount <= this.stepsInRun) {
									this.pSurvivalByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival;
									this.pDisabilityByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* daly * survival;

								}
								if (ageAtStart >= 0) {
									this.pSurvivalByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* survival;
									this.pDisabilityByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* daly * survival;
								}
								if (this.riskType == 2) {
									if (stepCount <= this.stepsInRun)
										this.meanRiskByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskValue * survival;
									if (ageAtStart >= 0)
										this.meanRiskByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskValue * survival;

									if (stepCount <= this.stepsInRun)
										this.meanRiskByAge[this.popToScenIndex[thisPop] + 1][stepCount][ageIndex][sexIndex] += weightOfIndividual
												* riskValue * survival;
								}
								if (this.riskType == 3) {
									if (stepCount <= this.stepsInRun)
										this.meanRiskByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										this.meanRiskByOriRiskClassByOriAge[this.popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;

								}
							}
						}

						/*
						 * for the reference scenario
						 */
						else {
							if (stepCount <= this.stepsInRun) {
								this.pSurvivalByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* survival;
								this.pDisabilityByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* survival * daly;
							}
							if (ageAtStart >= 0) {
								this.pSurvivalByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
										* survival;
								this.pDisabilityByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
										* survival * daly;
							}
							if (this.riskType == 2) {
								if (stepCount <= this.stepsInRun)
									this.meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* riskValue * survival;
								if (stepCount <= this.stepsInRun)
									this.meanRiskByAge[0][stepCount][ageIndex][sexIndex] += weightOfIndividual
											* riskValue * survival;
								if (ageAtStart >= 0)
									this.meanRiskByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* riskValue * survival;

							}
							if (this.riskType == 3) {
								if (stepCount <= this.stepsInRun)
									this.meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* riskDurationValue * survival;
								if (ageAtStart >= 0)
									this.meanRiskByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* riskDurationValue * survival;

							}

							/*
							 * in case there is a one-for-all scenario, the
							 * reference scenario should be added to the
							 * summary-array of this scenario, because
							 * not-changing is also a part of possible scenarios
							 */

							if (this.oneScenPopulation) {
								if (stepCount <= this.stepsInRun) {
									/*
									 * the "from" value is identical to the
									 * riskClassAtStart in this case
									 */
									pSurvivalByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival;
									pDisabilityByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival * daly;
								}
								if (ageAtStart >= 0) {
									pSurvivalByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
											* survival;
									pDisabilityByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
											* survival * daly;
								}
								if (this.riskType == 3) {
									if (stepCount <= this.stepsInRun)
										MeanRiskByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										MeanRiskByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
								}
							}
						}
						if (ageAtStart == 3 && sexIndex == 0 && stepCount < 5
								&& stepCount < this.stepsInRun && thisPop > 0)
							log
									.debug("pSurvival reference for riskfactor "
											+ riskFactor
											+ " = "
											+ this.pSurvivalByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex]);

						if (ageAtStart == 3 && sexIndex == 0 && stepCount < 5
								&& stepCount < this.stepsInRun && thisPop > 0)
							log
									.debug("pSurvival_scen voor stepcount "
											+ stepCount
											+ " from=to "
											+ riskFactor
											+ " age "
											+ ageIndex
											+ " = "
											+ pSurvivalByRiskClassByAge_scen[stepCount][riskFactor][riskFactor][riskFactor][ageIndex][sexIndex]);
						/*
						 * add disease states to disease state arrays in a
						 * similar fashion
						 */
						for (int s = 0; s < this.nDiseaseStates; s++) {

							if (thisPop > 0)
								if (this.isOneScenPopulation[thisPop]) {
									if (stepCount <= this.stepsInRun)
										pDiseaseStateByRiskClassByAge_scen[stepCount][s][from][to][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										pDiseaseStateByOriRiskClassByAge_scen[stepCount][s][from][to][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;

								} else {
									if (stepCount <= this.stepsInRun)
										this.pDiseaseStateByRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										this.pDiseaseStateByOriRiskClassByAge[this.popToScenIndex[thisPop] + 1][stepCount][s][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
								}
							if (thisPop == 0) {
								if (stepCount <= this.stepsInRun) /*
																 * riskClassAtStart
																 * plays role of
																 * from here
																 */

									pDiseaseStateByRiskClassByAge_scen[stepCount][s][riskClassAtStart][riskClassAtStart][riskFactor][ageIndex][sexIndex] += weightOfIndividual

											* compoundData[s] * survival;
								if (ageAtStart >= 0)
									pDiseaseStateByOriRiskClassByAge_scen[stepCount][s][riskClassAtStart][riskClassAtStart][riskFactor][ageAtStart][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;

								if (stepCount <= this.stepsInRun)
									this.pDiseaseStateByRiskClassByAge[0][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;
								if (ageAtStart >= 0)
									this.pDiseaseStateByOriRiskClassByAge[0][stepCount][s][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;

							}

						}
						if (ageAtStart == 3 && sexIndex == 0 && stepCount < 5
								&& stepCount < this.stepsInRun)
							log
									.debug("pSurvival_scen voor from "
											+ from
											+ " to "
											+ to
											+ " "
											+ pSurvivalByRiskClassByAge_scen[stepCount][from][to][to][ageIndex][sexIndex]);

						// TODO for with cured fraction
						// float [] disease = (float[]) individual.get(4)
						// .getValue(stepCount);

						// simulatedRiskFactorPrevalence[stepCount][ageIndex][sexIndex][riskFactor]++;
						// simulatedDiseasePrevalence[stepCount][ageIndex][sexIndex]
						// +=
						// disease[0];
						// simulatedSurvival[stepCount][ageIndex][sexIndex] +=
						// disease[3];
					} // end if age>=0
				}// end over stepCount
			}// end loop over individuals
		}// end loop populations

		/*
		 * 
		 * 
		 * combine data from the one-population-for-all-scenarios to separate
		 * scenario's
		 * 
		 * first for the arrays based on current age and current risk
		 * factorstatus
		 */
		if (this.oneScenPopulation) {
			float[] dummy = new float[this.nRiskFactorClasses];
			Arrays.fill(dummy, 1);
			float[][] toChange;
			int minSimAge = Math.max(0, this.minAgeInSimulation);
			int maxSimAge = this.maxAgeInSimulation;

			for (int scen = 0; scen < this.nScen; scen++)

				if (this.scenInitial[scen] && !this.scenTrans[scen]) {
					/*
					 * calculate the transitions needed from old to new
					 * prevalence
					 */
					for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++) {
						int amax = Math.min(maxSimAge + 1 + stepCount, nDim);
						/* only do for ages that are in simulation */
						int minimumForLoop = minSimAge + stepCount;
						/* if there are newborns, then always start at zero */
						if (this.minAgeInSimulation < 0)
							minimumForLoop = 0;
						for (int a = minimumForLoop; a < amax; a++)
							for (int s = 0; s < 2; s++)

							{
								/* for safety, initialize arrays */
								for (int r = 0; r < this.nRiskFactorClasses; r++) {
									this.pSurvivalByRiskClassByAge[scen + 1][stepCount][r][a][s] = 0;
									this.pDisabilityByRiskClassByAge[scen + 1][stepCount][r][a][s] = 0;
									for (int state = 0; state < this.nDiseaseStates; state++)
										this.pDiseaseStateByRiskClassByAge[scen + 1][stepCount][state][r][a][s] = 0;
								}
								if (a >= stepCount)
									toChange = NettTransitionRateFactory
											.makeNettTransitionRates(
													this.oldPrevalence[a
															- stepCount][s],
													this.newPrevalence[scen][a
															- stepCount][s], 0,
													dummy);
								else
									/* for newborns */
									toChange = NettTransitionRateFactory
											.makeNettTransitionRates(
													this.oldPrevalence[0][s],
													this.newPrevalence[scen][0][s],
													0, dummy);

								for (from = 0; from < this.nRiskFactorClasses; from++)
									for (to = 0; to < this.nRiskFactorClasses; to++) {
										for (int r = 0; r < this.nRiskFactorClasses; r++) {

											for (int state = 0; state < this.nDiseaseStates; state++)
												if (pDiseaseStateByRiskClassByAge_scen[stepCount][state][from][to][r][a][s] != 0)
													this.pDiseaseStateByRiskClassByAge[scen + 1][stepCount][state][r][a][s] += toChange[from][to]
															* pDiseaseStateByRiskClassByAge_scen[stepCount][state][from][to][r][a][s];
											if (pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s] != 0)
												this.pSurvivalByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s];
											if (pDisabilityByRiskClassByAge_scen[stepCount][from][to][r][a][s] != 0)
												this.pDisabilityByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pDisabilityByRiskClassByAge_scen[stepCount][from][to][r][a][s];

											if (this.riskType > 1
													&& MeanRiskByRiskClassByAge_scen[stepCount][from][to][r][a][s] != 0)
												this.meanRiskByRiskClassByAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* MeanRiskByRiskClassByAge_scen[stepCount][from][to][r][a][s];
										}
										/*
										 * check if there are the required
										 * persons in the "one-for-all-scenario"
										 * situation do this only at step 0, as
										 * they might legitimately be zero at
										 * later times
										 */
										if (stepCount == 0) {
											double sum = 0;
											for (int r = 0; r < this.nRiskFactorClasses; r++)
												sum += pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s];
											if (sum == 0
													&& toChange[from][to] > 0) {
												log
														.fatal(" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to
																+ " for age "
																+ a);
												throw new DynamoScenarioException(
														" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to
																+ " for age "
																+ a);

											}
										}
									}// end to-from loop

							}
					}// end age , sex and stepCount loop
					/*
					 * repeat
					 * 
					 * 
					 * for the arrays based on original age and riskfactors
					 */

					for (int stepCount = 0; stepCount < this.nDim; stepCount++)
						for (int a = minSimAge; a < maxSimAge + 1; a++)
							for (int s = 0; s < 2; s++)

							{

								/* for safety, initialize arrays */
								for (int r = 0; r < this.nRiskFactorClasses; r++) {
									this.pSurvivalByOriRiskClassByOriAge[scen + 1][stepCount][r][a][s] = 0;
									this.pDisabilityByOriRiskClassByOriAge[scen + 1][stepCount][r][a][s] = 0;
									for (int state = 0; state < this.nDiseaseStates; state++)
										this.pDiseaseStateByOriRiskClassByAge[scen + 1][stepCount][state][r][a][s] = 0;
								}

								toChange = NettTransitionRateFactory
										.makeNettTransitionRates(
												this.oldPrevalence[a][s],
												this.newPrevalence[scen][a][s],
												0, dummy);
								for (from = 0; from < this.nRiskFactorClasses; from++)
									for (to = 0; to < this.nRiskFactorClasses; to++) {
										for (int r = 0; r < this.nRiskFactorClasses; r++) {

											for (int state = 0; state < this.nDiseaseStates; state++)

												if (pDiseaseStateByOriRiskClassByAge_scen[stepCount][state][from][to][r][a][s] != 0)
													this.pDiseaseStateByOriRiskClassByAge[scen + 1][stepCount][state][r][a][s] += toChange[from][to]
															* pDiseaseStateByOriRiskClassByAge_scen[stepCount][state][from][to][r][a][s];
											if (pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][r][a][s] != 0)
												this.pSurvivalByOriRiskClassByOriAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][r][a][s];
											if (pDisabilityByOriRiskClassByAge_scen[stepCount][from][to][r][a][s] != 0)
												this.pDisabilityByOriRiskClassByOriAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* pDisabilityByOriRiskClassByAge_scen[stepCount][from][to][r][a][s];

											if (this.riskType > 1
													&& MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][r][a][s] != 0)
												this.meanRiskByOriRiskClassByOriAge[scen + 1][stepCount][r][a][s] += toChange[from][to]
														* MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][r][a][s];
										}
										if (stepCount == 0) {
											double sum = 0;
											for (int r = 0; r < this.nRiskFactorClasses; r++)
												sum += pSurvivalByRiskClassByAge_scen[stepCount][from][to][r][a][s];

											if (sum == 0
													&& toChange[from][to] > 0) {
												log
														.fatal(" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to);
												throw new DynamoScenarioException(
														" not enough simulated information to calculate scenario "
																+ scen
																+ ". No simulated information on changing riskfactor class "
																+ from
																+ " into class "
																+ to);
											}
										}
									}// end to-from loop

							}// end age , sex and stepCount loop

				}// end loop for scenario

		}

	}

	/**
	 * @param compoundData
	 * @param riskFactor
	 * @param riskValue
	 * @param riskDurationValue
	 * @param ageIndex
	 * @param sexIndex
	 * @return
	 */
	private double makeDaly(float[] compoundData, int riskFactor,
			float riskValue, int riskDurationValue, int age, int sex) {

		int age2 = Math.min(95, age);
		int currentClusterStart = 0;
		double ability = 1;
		if (this.nDiseases > 0)
			for (int c = 0; c < this.structure.length; c++) {
				if (!this.structure[c].isWithCuredFraction()) {
					double clusterAbility = 0;
					for (int state = 0; state < Math.pow(2, this.structure[c]
							.getNInCluster()); state++) {
						double probState = 0;
						if (state == 0) {
							probState = 1;
							for (int state2 = 0; state2 < (Math.pow(2,
									this.structure[c].getNInCluster()) - 1); state2++)
								probState -= compoundData[currentClusterStart
										+ state2];
						} else
							probState = compoundData[currentClusterStart
									+ state - 1];
						double abilityState = 1;
						for (int d = 0; d < this.structure[c].getNInCluster(); d++) {

							if ((state & (1 << d)) == (1 << d))
								abilityState *= diseaseAbility[age2][sex][this.structure[c]
										.getDiseaseNumber()[d]];
						}
						clusterAbility += abilityState * probState;

					}
					currentClusterStart += Math.pow(2, this.structure[c]
							.getNInCluster()) - 1;
					ability *= clusterAbility;
				} else {
					/* with cured fraction */

					double probDisease = compoundData[currentClusterStart]
							+ compoundData[currentClusterStart + 1];
					ability *= (1 - probDisease + probDisease
							* diseaseAbility[age2][sex][this.structure[c]
									.getDiseaseNumber()[0]]);
					currentClusterStart += 2;
				}

			}

		// TODO including riskfactor
		double daly = 0;
		if (this.riskType == 1)
			daly = (1 - this.baselineAbility[age2][sex]
					* this.relRiskAbilityCat[age2][sex][riskFactor] * ability);
		if (this.riskType == 2)
			daly = (1 - this.baselineAbility[age2][sex]
					* Math.pow(this.relRiskAbilityCont[age2][sex], riskValue
							- this.referenceRiskFactorValue) * ability);
		if (this.riskType == 3) {
			double RR = 1;
			if (riskFactor == this.durationClass)
				RR = (this.relRiskAbilityBegin[age2][sex] - this.relRiskAbilityEnd[age2][sex])
						* Math.exp(-riskDurationValue
								* this.alfaAbility[age2][sex])
						+ this.relRiskAbilityEnd[age2][sex];
			else
				RR = this.relRiskAbilityCat[age2][sex][riskFactor];
			daly = (1 - this.baselineAbility[age2][sex] * RR * ability);
			if (this.alfaAbility[age2][sex] < 0)
				log.fatal("!!!! NEGATIVE alfa-ability");
		}
		if (age == 0) {
			int b = 0;
			b++;

		}

		return daly;
	}

	/**
	 * makes a array of mortality by scenario, year, age and sex It is not
	 * possible to do so also by riskfactor or by disease In order to do so,
	 * this should be included as state in the update rule
	 * 
	 * @param numbers
	 * @return
	 */
	private double[][][][] makeMortalityArray(boolean numbers, int riskFactor) {
		double[][][][] mortality = null;
		int loopBegin = riskFactor;
		int loopEnd = riskFactor + 1;
		if (riskFactor < 0) {
			loopBegin = 0;
			loopEnd = this.nRiskFactorClasses;
		}
		if (this.stepsInRun > 0) {
			mortality = new double[this.nScen + 1][this.stepsInRun][96 + this.stepsInRun][2];
			/*
			 * number of persons now and after a year are calculated for all
			 * possible combinations of age and years, regardless of whether
			 * there are persons available at a particular age and year
			 * combination
			 */
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun - 1; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun; stepCount++) {
							double denominator = 0;
							double nominator = 0;
							double personsAtnextAge = 0;
							for (int r = loopBegin; r < loopEnd; r++) {
								denominator += applySuccesrate(
										this.nPopByRiskClassByAge[0][stepCount][r][a][g],
										this.nPopByRiskClassByAge[scen][stepCount][r][a][g],
										scen, stepCount, a, g);

								personsAtnextAge += applySuccesrate(
										this.nPopByRiskClassByAge[0][stepCount + 1][r][a + 1][g],
										this.nPopByRiskClassByAge[scen][stepCount + 1][r][a + 1][g],
										scen, stepCount + 1, a + 1, g);
							}
							nominator = denominator - personsAtnextAge;

							if (denominator != 0 && !numbers
									&& personsAtnextAge > 0)
								mortality[scen][stepCount][a][g] = nominator
										/ denominator;
							if (denominator != 0 && personsAtnextAge > 0
									&& numbers)
								mortality[scen][stepCount][a][g] = nominator;
							if (denominator == 0 || personsAtnextAge == 0)
								mortality[scen][stepCount][a][g] = -1;
							/*
							 * also make mortality -1 for the highest age group
							 * as there is never a higher age available with
							 * which to calculate mortality
							 */
							if (a == (this.nDim - 2))
								mortality[scen][stepCount][this.nDim - 1][g] = -1;

						}
		}
		return mortality;
	}

	/**
	 * makes an array of incidence by scenario, year,disease, age and sex It is
	 * not possible to do so also by riskfactor as part of the postprocessing In
	 * order to do so, this should be included as state in the update rule This
	 * method is obsolete
	 * 
	 * @numbers boolean indicating whether absolute numbers or yearly incidence
	 *          (number/diseasefree persons at the beginning of the period)
	 *          should be returned
	 * @return an array of incident numbers (or numbers divided by population at
	 *         risk at the beginning of the year) by scenario, year,disease, age
	 *         and sex
	 */
	private double[][][][][] makeIncidenceArray(boolean numbers) {
		double[][][][][] incidence = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseases][96 + this.stepsInRun][2];
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int d = 0; d < this.nDiseases; d++)
				for (int a = 0; a < 96 + this.stepsInRun - 1; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun; stepCount++) {
							double denominator = 0;
							double nominator = 0;

							for (int r = 0; r < this.nRiskFactorClasses; r++) {
								denominator += this.nPopByRiskClassByAge[scen][stepCount][r][a][g]
										- nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
								nominator += nDiseaseByRiskClassByAge[scen][stepCount + 1][d][r][a + 1][g]
										- nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
							}
							if (numbers)
								incidence[scen][stepCount][d][a][g] = nominator;
							if (denominator != 0 && !numbers)
								incidence[scen][stepCount][d][a][g] = nominator
										/ denominator;

							if (denominator == 0)
								incidence[scen][stepCount][d][a][g] = 0;

						}
		return incidence;
	}

	/**
	 * The array takes a 6-dimensional array with disease state, and returns a 6
	 * dimensional array in which the diseasestates are summed to diseases
	 * 
	 * @param stateArray
	 *            : 6-dimensional array where the third index indicates the
	 *            disease state = combination of diseases
	 * @return 6-dimensional array where the third index indicates the disease
	 */
	private double[][][][][][] makeDiseaseArray(double[][][][][][] stateArray) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim1 = stateArray.length;
		int dim2 = stateArray[0].length;

		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		int dim6 = stateArray[0][0][0][0][0].length;

		double diseaseArray[][][][][][] = new double[dim1][dim2][this.nDiseases][dim4][dim5][dim6];

		if (this.structure != null) {
			for (int c = 0; c < this.structure.length; c++) {

				if (!this.structure[c].isWithCuredFraction()) {
					for (int d = 0; d < this.structure[c].getNInCluster(); d++) {

						for (int state = 1; state < Math.pow(2,
								this.structure[c].getNInCluster()); state++) {

							if ((state & (1 << d)) == (1 << d)) {
								/*
								 * pDisease[thisScen][stepCount][currentDisease
								 * + d][sexIndex] += compoundData[currentState +
								 * s - 1] survival weight[riskFactor][ageIndex][
								 * sexIndex]; if (details)
								 */
								for (int scen = 0; scen < dim1; scen++)
									for (int a = 0; a < dim5; a++)
										for (int g = 0; g < dim6; g++)
											for (int r = 0; r < dim4; r++)
												for (int stepCount = 0; stepCount < dim2; stepCount++)
													diseaseArray[scen][stepCount][currentDisease][r][a][g] += stateArray[scen][stepCount][currentClusterStart
															+ state - 1][r][a][g];
							}
						}
						currentDisease++;
					}
					currentClusterStart += Math.pow(2, this.structure[c]
							.getNInCluster()) - 1;
				} else {
					/* with cured fraction */
					for (int scen = 0; scen < dim1; scen++)
						for (int a = 0; a < dim5; a++)
							for (int g = 0; g < dim6; g++)
								for (int r = 0; r < dim4; r++)
									for (int stepCount = 0; stepCount < dim2; stepCount++) {
										diseaseArray[scen][stepCount][currentDisease][r][a][g] += stateArray[scen][stepCount][currentClusterStart][r][a][g];

										diseaseArray[scen][stepCount][currentDisease + 1][r][a][g] += stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];

									}
					currentDisease += 2;
					currentClusterStart += 2;
				}

			}
		} else {
			diseaseArray = null;
		}

		return diseaseArray;
	}

	/**
	 * Makes an array with diseaseNumbers from the array with diseasestates
	 * 
	 * @param stateArray
	 * @param scen
	 * @param stepCount
	 * @param g
	 * @return array [][][] of diseases, riskclass, age for the case
	 *         scenario=scen, year=stepcount and gender=g
	 */
	private double[][][] makeDiseaseArray(double[][][][][][] stateArray,
			int scen, int stepCount, int g) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		double diseaseArray[][][] = new double[this.nDiseases][dim4][dim5];

		for (int c = 0; c < this.structure.length; c++) {
			if (!this.structure[c].isWithCuredFraction()) {
				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {

					for (int state = 1; state < Math.pow(2, this.structure[c]
							.getNInCluster()); state++) {

						if ((state & (1 << d)) == (1 << d)) {
							/*
							 * pDisease[thisScen][stepCount][currentDisease +
							 * d][sexIndex] += compoundData[currentState + s -
							 * 1] survival weight[riskFactor][ageIndex][
							 * sexIndex]; if (details)
							 */
							for (int r = 0; r < dim4; r++)
								for (int a = 0; a < dim5; a++)
									diseaseArray[currentDisease][r][a] += stateArray[scen][stepCount][currentClusterStart
											+ state - 1][r][a][g];
						}
					}
					currentDisease++;
				}
				currentClusterStart += Math.pow(2, this.structure[c]
						.getNInCluster()) - 1;
			} else {
				for (int r = 0; r < dim4; r++)
					for (int a = 0; a < dim5; a++) {
						diseaseArray[currentDisease][r][a] += stateArray[scen][stepCount][currentClusterStart][r][a][g];
						diseaseArray[currentDisease + 1][r][a] += stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];
						currentDisease += 2;
					}
				currentClusterStart += 2;

			}
		}
		return diseaseArray;
	}

	/**
	 * combines from the stateArray the contributions for disease with
	 * diseasenumber disease , and selects only those data from the simulated
	 * year given by stepCount
	 * 
	 * @param stateArray
	 * @param disease
	 *            : disease number
	 * @param stepCount
	 * @return array [][][][] of scenario, riskclass, age, sex for the case
	 *         year=stepcount and diseasenumber=disease
	 */

	private double[][][][] makeDiseaseArray(double[][][][][][] stateArray,
			int stepCount, int disease) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim6 = stateArray[0][0][0][0][0].length;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		double diseaseArray[][][][] = new double[stateArray.length][dim4][dim5][dim6];
		boolean diseaseFound = false;
		/*
		 * no check on whether structure exists, as there will not be a state
		 * array without diseases
		 */
		for (int c = 0; c < this.structure.length; c++) {
			if (!this.structure[c].isWithCuredFraction()) {
				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {
					if (this.structure[c].getDiseaseNumber()[d] == disease) {
						diseaseFound = true;

						for (int state = 1; state < Math.pow(2,
								this.structure[c].getNInCluster()); state++) {

							if ((state & (1 << d)) == (1 << d)) {
								/*
								 * pDisease[thisScen][stepCount][currentDisease
								 * + d][sexIndex] += compoundData[currentState +
								 * s - 1] survival weight[riskFactor][ageIndex][
								 * sexIndex]; if (details)
								 */
								for (int scen = 0; scen < stateArray.length; scen++)
									for (int r = 0; r < dim4; r++)
										for (int a = 0; a < dim5; a++)
											for (int g = 0; g < dim6; g++)
												diseaseArray[scen][r][a][g] += stateArray[scen][stepCount][currentClusterStart
														+ state - 1][r][a][g];
							}
						}
						break;
					}
					currentDisease++;
				}
				currentClusterStart += Math.pow(2, this.structure[c]
						.getNInCluster()) - 1;
				if (diseaseFound)
					break;
			} else {

				if (this.structure[c].getDiseaseNumber()[0] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int a = 0; a < dim5; a++)
								for (int g = 0; g < dim6; g++)
									diseaseArray[scen][r][a][g] = stateArray[scen][stepCount][currentClusterStart][r][a][g];
				}
				if (this.structure[c].getDiseaseNumber()[1] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int a = 0; a < dim5; a++)
								for (int g = 0; g < dim6; g++)
									diseaseArray[scen][r][a][g] = stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];
				}

				currentDisease += 2;

				currentClusterStart += 2;
				if (diseaseFound)
					break;
			} // end if with cured fraction
		}// end loop over clusters
		return diseaseArray;
	}

	/**
	 * combines from the stateArray the contributions for disease with
	 * diseasenjumber disease ,
	 * 
	 * @param stateArray
	 * @param disease
	 *            : disease number
	 * 
	 * @return array [][][][][] of scenario,year riskclass, age, sex for the
	 *         case diseasenumber=disease
	 */

	private double[][][][][] makeDiseaseArray(double[][][][][][] stateArray,
			int disease) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim6 = stateArray[0][0][0][0][0].length;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		int dim2 = stateArray[0].length;
		double diseaseArray[][][][][] = new double[stateArray.length][dim2][dim4][dim5][dim6];
		boolean diseaseFound = false;

		/*
		 * no check on whether structure exists is necessary as there will not
		 * be a statearray without
		 */

		for (int c = 0; c < this.structure.length; c++) {
			if (!this.structure[c].isWithCuredFraction()) {
				for (int d = 0; d < this.structure[c].getNInCluster(); d++) {
					if (this.structure[c].getDiseaseNumber()[d] == disease) {
						diseaseFound = true;

						for (int state = 1; state < Math.pow(2,
								this.structure[c].getNInCluster()); state++) {

							if ((state & (1 << d)) == (1 << d)) {
								/*
								 * if d =1 in the state, add this state to the
								 * disease
								 */
								for (int scen = 0; scen < stateArray.length; scen++)
									for (int r = 0; r < dim4; r++)
										for (int stepCount = 0; stepCount < dim2; stepCount++)
											for (int a = 0; a < dim5; a++)
												for (int g = 0; g < dim6; g++)
													diseaseArray[scen][stepCount][r][a][g] += stateArray[scen][stepCount][currentClusterStart
															+ state - 1][r][a][g];
							}
						}
						break;
					}
					currentDisease++;
				}
				currentClusterStart += Math.pow(2, this.structure[c]
						.getNInCluster()) - 1;
				if (diseaseFound)
					break;
			} else {

				if (this.structure[c].getDiseaseNumber()[0] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int stepCount = 0; stepCount < dim2; stepCount++)
								for (int a = 0; a < dim5; a++)
									for (int g = 0; g < dim6; g++)
										diseaseArray[scen][stepCount][r][a][g] = stateArray[scen][stepCount][currentClusterStart][r][a][g];
				}
				if (this.structure[c].getDiseaseNumber()[1] == disease) {
					diseaseFound = true;

					for (int scen = 0; scen < stateArray.length; scen++)
						for (int r = 0; r < dim4; r++)
							for (int stepCount = 0; stepCount < dim2; stepCount++)
								for (int a = 0; a < dim5; a++)
									for (int g = 0; g < dim6; g++)
										diseaseArray[scen][stepCount][r][a][g] = stateArray[scen][stepCount][currentClusterStart + 1][r][a][g];
				}

				currentDisease += 2;

				currentClusterStart += 2;
				if (diseaseFound)
					break;
			} // end if with cured fraction
		}// end loop over clusters

		return diseaseArray;
	}

	/**
	 * The array returns a 5 dimensional array (scenario, time, age, sex) of
	 * healthy persons in the population
	 * 
	 * 
	 * 
	 * @return 4-dimensional array of healthy persons in the population by
	 *         scenario, time, age and sex
	 */
	private double[][][][] getNumberOfDiseasedPersons() {

		double healthyPersonsByRiskClass[][][][][] = new double[this.nScen + 1][this.stepsInRun + 1][this.nRiskFactorClasses][96 + this.stepsInRun][2];
		double diseasedPersons[][][][] = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++) {

						for (int r = 0; r < this.nRiskFactorClasses; r++) {
							healthyPersonsByRiskClass[scen][stepCount][r][a][g] = this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
							int currentClusterStart = 0;
							/*
							 * if there are no diseases in the model, structure
							 * is null
							 */
							if (this.structure != null) {
								for (int c = 0; c < this.structure.length; c++) {
									double nWithDisease = 0;

									if (!this.structure[c]
											.isWithCuredFraction()) {
										for (int state = 0; state < (Math.pow(
												2, this.structure[c]
														.getNInCluster()) - 1); state++) {

											nWithDisease += this.nDiseaseStateByRiskClassByAge[scen][stepCount][currentClusterStart
													+ state][r][a][g];
										}
										currentClusterStart += Math.pow(2,
												this.structure[c]
														.getNInCluster()) - 1;
									} else {
										/* with cured fraction */
										nWithDisease = this.nDiseaseStateByRiskClassByAge[scen][stepCount][currentClusterStart][r][a][g]
												+ this.nDiseaseStateByRiskClassByAge[scen][stepCount][currentClusterStart + 1][r][a][g];

										currentClusterStart += 2;
									}
									if (this.nPopByRiskClassByAge[scen][stepCount][r][a][g] > 0)
										healthyPersonsByRiskClass[scen][stepCount][r][a][g] *= (this.nPopByRiskClassByAge[scen][stepCount][r][a][g] - nWithDisease)
												/ this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
									else
										healthyPersonsByRiskClass[scen][stepCount][r][a][g] = 0;
								}
								diseasedPersons[scen][stepCount][a][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g]
										- healthyPersonsByRiskClass[scen][stepCount][r][a][g];
							} else {
								/* if no diseases in the model */

								diseasedPersons[scen][stepCount][a][g] = 0;
							}
						}
					}
		return diseasedPersons;
	}

	/**
	 * returns the array if future numbers of total disease numbers for the
	 * group of persons with age age at the start of simulation
	 * 
	 * @param age
	 *            age
	 * @return array (indexes; scenario, timesteps (= up to very old age as
	 *         given by nDim) sex
	 */
	private double[][][] getNumberOfOriDiseasedPersons(int age) {

		double healthyPersons[][][][] = new double[this.nScen + 1][this.nDim][this.nRiskFactorClasses][2];
		double diseasedPersons[][][] = new double[this.nScen + 1][this.nDim][2];

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int steps = 0; steps < this.nDim; steps++)

				for (int g = 0; g < 2; g++) {

					for (int r = 0; r < this.nRiskFactorClasses; r++) {

						healthyPersons[scen][steps][r][g] += this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g];
						int currentClusterStart = 0;
						if (this.structure != null) {
							for (int c = 0; c < this.structure.length; c++) {
								double nWithDisease = 0;
								if (!this.structure[c].isWithCuredFraction()) {
									for (int state = 0; state < (Math.pow(2,
											this.structure[c].getNInCluster()) - 1); state++) {

										/*
										 * pDisease[thisScen][stepCount][currentDisease
										 * + d][sexIndex] +=
										 * compoundData[currentState + s - 1]
										 * survival
										 * weight[riskFactor][ageIndex][
										 * sexIndex]; if (details)
										 */

										nWithDisease += this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][currentClusterStart
												+ state][r][age][g];
									}
									currentClusterStart += Math.pow(2,
											this.structure[c].getNInCluster()) - 1;
								} else { /* with cured fraction */
									nWithDisease = this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][currentClusterStart][r][age][g]
											+ this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][currentClusterStart + 1][r][age][g];

									currentClusterStart += 2;
								}
								if (this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] > 0)
									healthyPersons[scen][steps][r][g] *= (this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] - nWithDisease)
											/ this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g];
								else
									healthyPersons[scen][steps][r][g] = 0;
								if (steps == 70) {
									log
											.fatal("population "
													+ this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]);
									log
											.fatal("c "
													+ c
													+ " healthy "
													+ healthyPersons[scen][steps][r][g]);
									// volgen
									log
											.fatal("nwithDisease "
													+ this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][currentClusterStart - 1][r][age][g]);

									log.fatal("n with Disease new "
											+ nWithDisease);
									log
											.fatal("prob disease "
													+ (this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g] - nWithDisease)
													/ this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]);
								}

							}
							diseasedPersons[scen][steps][g] += this.nPopByOriRiskClassByOriAge[scen][steps][r][age][g]
									- healthyPersons[scen][steps][r][g];

						} else { /* no diseases in the model */

							diseasedPersons[scen][steps][g] = 0;

						}
					} // end loop over r
				}
		return diseasedPersons;
	}

	/**
	 * returns the array if future numbers of total disabled numbers for the
	 * group of persons with age age at the start of simulation
	 * 
	 * @param age
	 *            age
	 * @return array (indexes; scenario, timesteps (= up to very old age as
	 *         given by nDim) sex
	 */
	private double[][][] getNumberOfOriDisabledPersons(int age) {

		double[][][] nDisabled = new double[this.nScen + 1][this.nDim][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)

				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < this.nDim; stepCount++)
						if (pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][age][g] != 0)
							nDisabled[scen][stepCount][g] += nPopByOriRiskClassByOriAge[scen][stepCount][r][age][g]
									* pDisabilityByOriRiskClassByOriAge[scen][stepCount][r][age][g]
									/ pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][age][g];
						else
							nDisabled[scen][stepCount][g] += 0;

		return nDisabled;

	}

	/**
	 * @param pop
	 */
	private void extractNumberInSimulationFromPopulation(Population[] pop) {
		int sexIndex;
		int ageIndex;
		this.minAgeInSimulation = 100;
		this.maxAgeInSimulation = 0;
		Iterator<Individual> individualIterator1 = pop[0].iterator();

		while (individualIterator1.hasNext()) {
			Individual individual = individualIterator1.next();

			ageIndex = (int) Math
					.round(((Float) individual.get(1).getValue(0)));
			sexIndex = (int) (Integer) individual.get(2).getValue(0);
			if (ageIndex > this.maxAgeInSimulation)
				this.maxAgeInSimulation = ageIndex;
			if (ageIndex < this.minAgeInSimulation)
				this.minAgeInSimulation = ageIndex;

			if (ageIndex < 0)
				this.nNewBornsInSimulationByAge[ageIndex + this.stepsInRun][sexIndex]++;
			else {
				this.nInSimulation[sexIndex]++;
				this.nInSimulationByAge[ageIndex][sexIndex]++;

				float riskValue;
				int durationValue;
				if (this.riskType != 2) {
					int riskFactor = (int) (Integer) individual.get(3)
							.getValue(0);
					this.nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;
					if (this.riskType == 3) {
						durationValue = Math.round((float) (Float) individual
								.get(4).getValue(0));
						this.nInSimulationByRiskClassAndDurationByAge[riskFactor][durationValue][ageIndex][sexIndex]++;
					}
				} else {
					riskValue = (float) (Float) individual.get(3).getValue(0);
					int riskFactor;
					int i = 0;
					if (this.riskClassnames.length > 1) {
						if (riskValue <= this.cutoffs[0])
							riskFactor = 0;

						else {
							for (i = 1; i < this.cutoffs.length; i++) {
								if (riskValue <= this.cutoffs[i]
										&& riskValue > this.cutoffs[i - 1])
									break;
							}

							riskFactor = i;
							/* just to be sure that it goes OK: */
							if (riskValue > this.cutoffs[this.cutoffs.length - 1])
								riskFactor = this.cutoffs.length;
						}
					} else { /* only one single value present for riskValue */
						riskFactor = 0;
					}
					this.nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;

				}
			}
		}
	}

	/**
	 * @param pop
	 */
	private void setCutoffs(Population[] pop) {
		float maxRisk = -1000000000;
		float minRisk = 1000000000;

		/*
		 * for continuous risk factor without defined cutoffs, define cutoffs
		 * based on 10% percentiles
		 */
		/* for this, first find minimum and maximum values */
		{
			for (int ipop = 0; ipop < this.nPopulations; ipop++) {
				Iterator<Individual> individualIterator2 = pop[ipop].iterator();

				while (individualIterator2.hasNext()) {
					Individual individual = individualIterator2.next();

					float riskValue;

					riskValue = (float) (Float) individual.get(3).getValue(0);
					if (riskValue > maxRisk)
						maxRisk = riskValue;
					if (riskValue < minRisk)
						minRisk = riskValue;
				}
			}

			if (maxRisk > minRisk) {
				this.nRiskFactorClasses = 5;
				this.cutoffs = new float[4];
				this.riskClassnames = new String[5];
				for (int i = 1; i < 5; i++) {
					this.cutoffs[i - 1] = minRisk + i * (maxRisk - minRisk)
							* 0.2F;
					/*
					 * names can be made only if both cutoffs are already
					 * calculated
					 */
					if (i != 1)
						this.riskClassnames[i - 1] = this.cutoffs[i - 2] + "-"
								+ this.cutoffs[i - 1];

				}
				this.riskClassnames[0] = "<" + this.cutoffs[0];
				this.riskClassnames[4] = ">" + this.cutoffs[3];
			} else if (maxRisk == minRisk) {
				this.riskClassnames = new String[1];
				this.nRiskFactorClasses = 1;
				this.riskClassnames[0] = ((Float) maxRisk).toString();
			}

		}

	}

	/**
	 * This method takes the detailed arrays (...byRiskClassByAge) and makes
	 * arrays of number of persons in the simulated population by applying the
	 * population numbers to them
	 * 
	 */
	public void makeArraysWithNumbers() {

		/*
		 * 
		 * Make absolute numbers, using the population size at the start of the
		 * simulation
		 */
		for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++) {

			float originalNumber = 0;
			float ratio = 0;

			for (int s = 0; s < 2; s++) {
				for (int a = 0; a < Math.min(this.nDim, (96 + stepCount)); a++) {
					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (a - stepCount >= 0) {
						/* for those in the initial cohort */
						originalNumber = this.populationSize[a - stepCount][s];
						if (this.nInSimulationByAge[a - stepCount][s] != 0)
							ratio = originalNumber
									/ this.nInSimulationByAge[a - stepCount][s];
						else
							ratio = 0;
					} else if (this.withNewborns) {
						/* for newborns (born during simulation ) */
						if (s == 0) /* males */
							originalNumber = this.newborns[stepCount - 1 - a]
									* this.mfratio / (1 + this.mfratio);
						else
							/* females */
							originalNumber = this.newborns[stepCount - 1 - a]
									* (1 - this.mfratio / (1 + this.mfratio));
						if (this.nNewBornsInSimulationByAge[stepCount - 1][s] != 0)
							ratio = originalNumber
									/ this.nNewBornsInSimulationByAge[stepCount - 1][s];
						else
							ratio = 0;
						// if
						// (s==0)log.fatal("step: "+stepCount+" age "+a+" basic pop taken "+
						// this.newborns[stepCount - 1-a]);
					} else
						ratio = 0;

					// TODO hierboven gok nog nagaan en zorgen dat alles goed
					// geinitialiseerd is

					/*
					 * nb: scen is the scenario number starting with scen =0 is
					 * the reference scenario. in arrays with scenario info the
					 * first (0) element refers to the first alternative
					 * scenario
					 */

					for (int scen = 0; scen <= this.nScen; scen++) {

						for (int r = 0; r < this.nRiskFactorClasses; r++) {

							this.nPopByRiskClassByAge[scen][stepCount][r][a][s] = ratio
									* this.pSurvivalByRiskClassByAge[scen][stepCount][r][a][s];

							for (int state = 0; state < this.nDiseaseStates; state++) {

								this.nDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s] = ratio
										* this.pDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s];

							}
						}
					}
				}// end loop age
			} // end loop sex
		} // end loop stepcount
		/*
		 * repeat for the cohort-based arrays based on original age and original
		 * riskclass
		 */
		for (int a = 0; a < 96; a++) {
			for (int stepCount = 0; stepCount < this.nDim - a; stepCount++) {
				float ratio = 0;
				for (int s = 0; s < 2; s++) {

					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (this.nInSimulationByAge[a][s] != 0)
						ratio = this.populationSize[a][s]
								/ this.nInSimulationByAge[a][s];
					/* newborns are not included in the cohort based arrays */

					for (int scen = 0; scen <= this.nScen; scen++) {
						for (int r = 0; r < this.nRiskFactorClasses; r++) {
							this.nPopByOriRiskClassByOriAge[scen][stepCount][r][a][s] = ratio
									* this.pSurvivalByOriRiskClassByOriAge[scen][stepCount][r][a][s];
							for (int state = 0; state < this.nDiseaseStates; state++) {
								this.nDiseaseStateByOriRiskClassByOriAge[scen][stepCount][state][r][a][s] = ratio
										* this.pDiseaseStateByOriRiskClassByAge[scen][stepCount][state][r][a][s];

							}
						}
					}
				}// end loop sex

			} // end loop stepcount
		} // end loop age

		/*
		 * uptill now all arrays contain numbers of simulated population in the
		 * category. The next part changes those into percentages (or means) by
		 * dividing by the right denominator (indicated by the name, and also by
		 * sex and timeStep (stepCount) and scenario
		 * 
		 * denominator: -- for survival the numbers in the initial population
		 * (stepcount=0) in the particular group -- for disease(state) : the
		 * fraction surviving (pop(stepcount))
		 */

	}

	/**
	 * method writeOutput writes Excel-readable XML files The following files
	 * are produced: depending on user input (TODO) - separate workbooks for for
	 * men and/or women, or for the total population (sum of men and women) -
	 * separate workbooks per scenario - by choice each worksheet in the
	 * workbook is a calendar year or a birth cohort - for continuous variables
	 * the choice is either overall results (including riskfactor average and
	 * std) or a classification, or The files are written in the directory
	 * "simulationName\results\". Their names are:
	 * excel_cohort_(fe)male_scenario#.xml (men or women) or
	 * excel_cohort_scenario#.xml (both combined)
	 * excel_year_(fe)male_scenario#.xml (men or women) or
	 * excel_year_scenario#.xml (both combined) * where # is the scenario number
	 * Either prevalences per disease are written (field details=false) or the
	 * originally calculated diseases states are written (field details=true)
	 * 
	 * @param scenInfo
	 *            : (ScenarioInfo) object with information on scenarios
	 * @param simulationName
	 *            (String): name of the simulation
	 * @throws XMLStreamException
	 * @throws IOException
	 * 
	 * @throws FactoryConfigurationError
	 */

	/**
	 * @param fileName
	 *            : name to write to
	 * @param sex
	 *            : sex to write: 0=men, 1=female, 2=both
	 * @param thisScen
	 *            : number of scenario to write
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws DynamoOutputException
	 */
	void writeWorkBookXMLbyYear(String fileName, int sex, int thisScen)
			throws FileNotFoundException, FactoryConfigurationError,
			XMLStreamException, DynamoOutputException {
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer;
		try {
			writer = factory.createXMLStreamWriter(out);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}

		/* in the current version all possbile notebooks are written */

		writer.writeStartDocument();
		writer.writeStartElement("Workbook");
		/*
		 * write information for excel might be partially unnecessary, but this
		 * works
		 */
		writer.writeAttribute("xmlns",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:o",
				"urn:schemas-microsoft-com:office:office");
		writer.writeAttribute("xmlns:x",
				"urn:schemas-microsoft-com:office:excel");
		writer.writeAttribute("xmlns:ss",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:html", "http://www.w3.org/TR/REC-html40");

		/* make one worksheet per calendar year */
		for (int year = 0; year < this.stepsInRun + 1; year++) {
			writer.writeStartElement("Worksheet");
			writer.writeAttribute("ss:Name", "year " + (this.startYear + year));
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (this.riskType == 1 || this.riskType == 3 || this.categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
				writeCell(writer, "std_riskFactor");
				writeCell(writer, "skewness");

			}
			if (this.riskType == 3) {

				writeCell(writer, "mean duration");

			}
			if (this.riskType == 2 && this.categorized) {

				writeCell(writer, "mean riskFactor");

			}

			/* age */
			writeCell(writer, "age");

			/* total number */
			writeCell(writer, "total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.nDiseaseStates + 3; col++) {
					writeCell(writer, this.stateNames[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.nDiseases + 4; col++) {
					writeCell(writer, this.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			writer.writeEndElement();// </row>

			/* write the data */
			/* each row is a risk-class / age combination */
			for (int rClass = 0; rClass < this.nRiskFactorClasses; rClass++)

				for (int a = 0; a < 96; a++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
					if (this.riskType == 1 || this.riskType == 3
							|| this.categorized) {

						writeCell(writer, this.riskClassnames[rClass]);

					}

					/*
					 * write the mean value of the continuous risk factor or the
					 * duration
					 */
					if (this.riskType == 2 && !this.categorized) {

						/*
						 * Calculate the average over all risk factor classes
						 * 
						 * / make arrays with the data needed to be averaged
						 */
						double[] toByAveragedRef;
						double[] toByAveragedScen;
						double[] numbersRef;
						double[] numbersScen;
						double mean;
						if (sex < 2) {
							toByAveragedRef = new double[this.nRiskFactorClasses];
							toByAveragedScen = new double[this.nRiskFactorClasses];
							numbersRef = new double[this.nRiskFactorClasses];
							numbersScen = new double[this.nRiskFactorClasses];
							for (int r = 0; r < this.nRiskFactorClasses; r++) {

								toByAveragedRef[r] = this.meanRiskByRiskClassByAge[0][year][rClass][a][sex];
								toByAveragedScen[r] = this.meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex];
								numbersRef[r] = this.nPopByRiskClassByAge[0][year][rClass][a][sex];
								numbersScen[r] = this.nPopByRiskClassByAge[thisScen][year][rClass][a][sex];
							}
							mean = applySuccesrateToMean(toByAveragedRef,
									toByAveragedScen, numbersRef, numbersScen,
									thisScen, year, a, sex);

						} else {
							toByAveragedRef = new double[this.nRiskFactorClasses * 2];
							toByAveragedScen = new double[this.nRiskFactorClasses * 2];
							numbersRef = new double[this.nRiskFactorClasses * 2];
							numbersScen = new double[this.nRiskFactorClasses * 2];
							for (int s = 0; s < 2; s++)

								for (int r = 0; r < this.nRiskFactorClasses; r++) {

									toByAveragedRef[r + s
											* this.nRiskFactorClasses] = this.meanRiskByRiskClassByAge[0][year][rClass][a][s];
									toByAveragedScen[r + s
											* this.nRiskFactorClasses] = this.meanRiskByRiskClassByAge[thisScen][year][rClass][a][s];
									numbersRef[r + s * this.nRiskFactorClasses] = this.nPopByRiskClassByAge[0][year][rClass][a][s];
									numbersScen[r + s * this.nRiskFactorClasses] = this.nPopByRiskClassByAge[thisScen][year][rClass][a][s];
								}
							mean = applySuccesrateToMean(toByAveragedRef,
									toByAveragedScen, numbersRef, numbersScen,
									thisScen, year, a, 2);
						}

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

					if (this.riskType == 2 && !this.categorized) {

						writeCell(writer, rClass);
						// TODO vervangen door std risk factor

						writeCell(writer, rClass);

					}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
					 */
					if ((this.riskType == 2 && this.categorized)
							|| this.riskType == 3) {

						double mean = 0;

						if (sex < 2) {

							mean = applySuccesrateToMean(
									this.meanRiskByRiskClassByAge[0][year][rClass][a][sex],
									this.meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex],
									this.nPopByRiskClassByAge[0][year][rClass][a][sex],
									this.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
									thisScen, year, a, sex);

						} else {
							mean = applySuccesrateToMean(
									this.meanRiskByRiskClassByAge[0][year][rClass][a],
									this.meanRiskByRiskClassByAge[thisScen][year][rClass][a],
									this.nPopByRiskClassByAge[0][year][rClass][a],
									this.nPopByRiskClassByAge[thisScen][year][rClass][a],
									thisScen, year, a, sex);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, a);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = applySuccesrate(
								this.nPopByRiskClassByAge[0][year][rClass][a][sex],
								this.nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
								thisScen, year, a, sex);

					} else {

						data = applySuccesrateToBothGenders(
								this.nPopByRiskClassByAge[0][year][rClass][a],
								this.nPopByRiskClassByAge[thisScen][year][rClass][a],
								thisScen, year, a);

					}
					writeCell(writer, data);
					/* write disease info */

					if (this.details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < this.nDiseaseStates + 3; col++) {
							if (sex < 2) {
								data = applySuccesrate(
										this.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a][sex],
										this.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
										thisScen, year, a, sex);

							} else {

								data = applySuccesrateToBothGenders(
										this.nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a],
										this.nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a],
										thisScen, year, a);

							}
							writeCell(writer, data);
						}

					} else { /*
							 * if details is false: then write the data of
							 * diseases
							 */
						/* make summary array */
						double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);

						for (int col = 4; col < this.nDiseases + 4; col++) {

							if (sex < 2) {
								data = applySuccesrate(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a][sex],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
										thisScen, year, a, sex);

							} else {

								data = applySuccesrateToBothGenders(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a],
										thisScen, year, a);

							}
							writeCell(writer, data);
						}

					}

					writer.writeEndElement();// </row>
				}// end risk class and age loop

			writer.writeEndElement();
			writer.writeEndElement();// end table and worksheet

		}// end loop over years
		writer.writeEndDocument();

		writer.flush();
		writer.close();
		try {
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
	}

	/**
	 * @param fileName
	 *            : name to write to
	 * @param sex
	 *            : sex to write: 0=men, 1=female, 2=both
	 * @param thisScen
	 *            : number of scenario to write
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws DynamoOutputException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * 
	 */
	void writeWorkBookXMLbyCohort(String fileName, int sex, int thisScen)
			throws FactoryConfigurationError, XMLStreamException,
			DynamoOutputException, FileNotFoundException {
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		XMLStreamWriter writer = null;
		try {
			writer = factory.createXMLStreamWriter(out);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
		/* in the current version all possbile notebooks are written */

		writer.writeStartDocument();
		writer.writeStartElement("Workbook");
		/*
		 * write information for excel might be partially unnecessary, but this
		 * works
		 */
		writer.writeAttribute("xmlns",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:o",
				"urn:schemas-microsoft-com:office:office");
		writer.writeAttribute("xmlns:x",
				"urn:schemas-microsoft-com:office:excel");
		writer.writeAttribute("xmlns:ss",
				"urn:schemas-microsoft-com:office:spreadsheet");
		writer.writeAttribute("xmlns:html", "http://www.w3.org/TR/REC-html40");
		// for (int year = 0; year < stepsInRun; year++)
		/* make one worksheet per cohort */
		for (int cohort = 0; cohort < 96; cohort++) {
			writer.writeStartElement("Worksheet");
			writer.writeAttribute("ss:Name", "age " + cohort + "in "
					+ this.startYear);
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (this.riskType == 1 || this.riskType == 3 || this.categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
				writeCell(writer, "std_riskFactor");
				writeCell(writer, "skewness");

			}
			if (this.riskType == 3) {

				writeCell(writer, "mean duration");

			}
			if (this.riskType == 2 && this.categorized) {

				writeCell(writer, "mean riskFactor");

			}

			/* age */
			writeCell(writer, "year");

			/* total number */
			writeCell(writer, "total number");

			/* disease info */
			if (this.details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < this.nDiseaseStates + 3; col++) {
					writeCell(writer, this.stateNames[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < this.nDiseases + 4; col++) {
					writeCell(writer, this.diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			writer.writeEndElement();// </row>

			/*
			 * 
			 * 
			 * write the data
			 */
			/* each row is a risk-class / age combination */
			for (int rClass = 0; rClass < this.nRiskFactorClasses; rClass++)

				for (int year = 0; year < this.nDim - cohort - 1; year++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
					if (this.riskType == 1 || this.riskType == 3
							|| this.categorized) {

						writeCell(writer, this.riskClassnames[rClass]);

					}

					/*
					 * write the mean value of the continuous risk factor or the
					 * duration
					 */
					if (this.riskType == 2 && !this.categorized) {

						/*
						 * Calculate the average over all risk factor classes
						 * 
						 * / make arrays with the data needed to be averaged
						 */
						double[] toBeAveragedRef;
						double[] toBeAveragedScen;
						double[] numbersRef;
						double[] numbersScen;
						double mean;

						if (sex < 2) {
							toBeAveragedRef = new double[this.nRiskFactorClasses];
							toBeAveragedScen = new double[this.nRiskFactorClasses];
							numbersRef = new double[this.nRiskFactorClasses];
							numbersScen = new double[this.nRiskFactorClasses];
							for (int r = 0; r < this.nRiskFactorClasses; r++) {

								toBeAveragedRef[r] = this.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
								toBeAveragedScen[r] = this.meanRiskByRiskClassByAge[thisScen][year][rClass][cohort][sex];
								numbersRef[r] = this.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
								numbersScen[r] = this.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
							}

							mean = applySuccesrateToMean(toBeAveragedRef,
									toBeAveragedScen, numbersRef, numbersScen,
									thisScen, 0, cohort, sex);
						} else {
							double[][] toBeAveragedRef2 = new double[this.nRiskFactorClasses * 2][2];
							double[][] toBeAveragedScen2 = new double[this.nRiskFactorClasses * 2][2];
							double[][] numbersRef2 = new double[this.nRiskFactorClasses * 2][2];
							double[][] numbersScen2 = new double[this.nRiskFactorClasses * 2][2];
							for (int r = 0; r < this.nRiskFactorClasses; r++)

								for (int s = 0; s < 2; s++) {
									toBeAveragedScen2[r][s] = this.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][s];
									toBeAveragedRef2[r][s] = this.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][s];
									numbersRef2[r][s] = this.nPopByRiskClassByAge[0][year][rClass][cohort][s];
									numbersScen2[r][s] = this.nPopByRiskClassByAge[thisScen][year][rClass][cohort][s];

									;
								}
							mean = applySuccesrateToMeanToBothGenders(
									toBeAveragedRef2, toBeAveragedScen2,
									numbersRef2, numbersScen2, thisScen, 0,
									cohort);
						}
						// TODO veranderen

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

					if (this.riskType == 2 && !this.categorized) {

						writeCell(writer, rClass);
						// TODO vervangen door std risk factor

						writeCell(writer, rClass);

					}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
					 */
					if ((this.riskType == 2 && this.categorized)
							|| this.riskType == 3) {

						double mean = 0;

						if (sex < 2) {

							mean = applySuccesrateToMean(
									this.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
									this.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
									this.nPopByRiskClassByAge[0][year][rClass][cohort][sex],
									this.nPopByRiskClassByAge[thisScen][year][rClass][cohort][sex],
									thisScen, 0, cohort, sex);

						} else {
							mean = applySuccesrateToMeanToBothGenders(
									this.meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort],
									this.meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
									this.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
									this.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
									thisScen, 0, cohort);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, this.startYear + year);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = applySuccesrate(
								this.nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
								this.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
								thisScen, 0, cohort, sex);

					} else {

						data = applySuccesrateToBothGenders(
								this.nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
								this.nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
								thisScen, 0, cohort);

					}
					writeCell(writer, data);
					/* write disease info */

					if (this.details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < this.nDiseaseStates + 3; col++) {
							if (sex < 2) {
								data = applySuccesrate(
										this.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
										this.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
										thisScen, 0, cohort, sex);

							} else {

								data = applySuccesrateToBothGenders(
										this.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
										this.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
										thisScen, 0, cohort);

							}
							writeCell(writer, data);
						}

					} else {
						/* if details is false: then write the data of diseases */
						for (int col = 4; col < this.nDiseases + 4; col++) {
							if (sex < 2) {
								data = applySuccesrate(
										this.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
										this.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
										thisScen, 0, cohort, sex);

							} else {

								data = applySuccesrateToBothGenders(
										this.nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
										this.nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
										thisScen, 0, cohort);

							}
							writeCell(writer, data);
						}

					}

					writer.writeEndElement();// </row>
				}// end risk class and age loop

			writer.writeEndElement();
			writer.writeEndElement();// end table and worksheet

		}// end loop over years
		writer.writeEndDocument();

		writer.flush();
		writer.close();
		try {
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new DynamoOutputException("file " + fileName
					+ " can not be written. Please make sure that"
					+ " this file is not in use by another program.");
			// TODO Auto-generated catch block

		}
	}

	/**
	 * applies succesrates for men and women together.
	 * 
	 * @param inputRef
	 *            : array[2] with data for reference scenario for men and women
	 * @param inputRef
	 *            : array[2] with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied This should be zero for "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result
	 * 
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToBothGenders(double[] inputRef,
			double[] inputScen, int thisScen, int year, int a) {
		double data = 0.0;

		if (thisScen == 0)

			for (int i = 0; i < 2; i++)
				data += inputRef[i];
		else if (a >= year) /* if not newborns */{
			if (this.minAge[thisScen - 1] > a - year
					|| this.maxAge[thisScen - 1] < a - year)
				for (int i = 0; i < 2; i++)
					data += inputRef[i];
			else {
				if (this.inMen[thisScen - 1])
					data += (1 - this.succesrate[thisScen - 1]) * inputRef[0]
							+ (this.succesrate[thisScen - 1]) * inputScen[0];
				else
					data += inputRef[0];
				if (this.inWomen[thisScen - 1])
					data += (1 - this.succesrate[thisScen - 1]) * inputRef[1]
							+ (this.succesrate[thisScen - 1]) * inputScen[1];
				else
					data += inputRef[1];
			}
		} else /* if newborns */
		{
			if (this.minAge[thisScen - 1] > 0)
				for (int i = 0; i < 2; i++)
					data += inputRef[i];
			else {
				if (this.inMen[thisScen - 1])
					data += (1 - this.succesrate[thisScen - 1]) * inputRef[0]
							+ (this.succesrate[thisScen - 1]) * inputScen[0];
				else
					data += inputRef[0];
				if (this.inWomen[thisScen - 1])
					data += (1 - this.succesrate[thisScen - 1]) * inputRef[1]
							+ (this.succesrate[thisScen - 1]) * inputScen[1];
				else
					data += inputRef[1];
			}
		}
		;
		return data;
	}

	/**
	 * applies the succesrate, minimum target age, maximum target age and target
	 * gender (todo) to the
	 * 
	 * @param inputRef
	 *            : value with data for reference scenario
	 * @param inputRef
	 *            : value with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied. This should be zero for "ori"
	 *            arrays
	 * @param a
	 *            : age: the current age of the cohort, or the age at start of
	 *            simulation (for "ori" arrays). In the last case, year above
	 *            should be zero
	 * @param gender
	 *            : gender (0 or 1 for men or women)
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * */

	private double applySuccesrate(double inputRef, double inputScen,
			int thisScen, int year, int a, int gender) {
		double data = 0.0;
		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;
		else {
			if (gender == 0 && !this.inMen[thisScen - 1])
				doApply = false;
			if (gender == 1 && !this.inWomen[thisScen - 1])
				doApply = false;

		}
		if (!doApply)
			data = inputRef;
		/* if not newborns */
		else if (a - year >= 0) {
			if (this.minAge[thisScen - 1] > a - year
					|| this.maxAge[thisScen - 1] < a - year)
				data = inputRef;
			else
				data = (1 - this.succesrate[thisScen - 1]) * inputRef
						+ (this.succesrate[thisScen - 1]) * inputScen;
		} else {
			if (this.minAge[thisScen - 1] > 0)
				data = inputRef;
			else
				data = (1 - this.succesrate[thisScen - 1]) * inputRef
						+ (this.succesrate[thisScen - 1]) * inputScen;
		}
		;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome array after applying
	 * successrates. As this needs to be a weighted mean (weighted by the number
	 * of persons) it also needs the weights (numbers in each scenario) This
	 * method uses the fields: successrate, minage and maxage, inMen and
	 * inWomen, so changing these fields will give different outputs If in
	 * gender=2, the array should be of dimension [2], and inMen and InWomen are
	 * to applied
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario: all data are for the
	 *            same age and year
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario:
	 *            all data are for the same age and year
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step)
	 * @param a
	 *            : age
	 * @param gender
	 *            : gender: 0=men, 1=women, 2=both, inplying that the
	 *            application of succesrates to gender already took place
	 *            earlier, and only averaging over the current array is needed
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * @throws DynamoOutputException
	 */
	private double applySuccesrateToMean(double[] inputRef, double[] inputScen,
			double[] nInRef, double[] nInScen, int thisScen, int year, int a,
			int gender) throws DynamoOutputException {

		double data = 0.0;
		if (gender == 2) {
			data = applySuccesrateToMeanToBothGenders(inputRef, inputScen,
					nInRef, nInScen, thisScen, year, a);

		} else {

			double denominator = 0;
			double nominator = 0;
			int nToAdd = inputRef.length;
			boolean doApply = true;
			if (thisScen == 0)
				doApply = false;
			else {
				if (gender == 0 && !this.inMen[thisScen - 1])
					doApply = false;
				if (gender == 1 && !this.inWomen[thisScen - 1])
					doApply = false;

			}
			if (!doApply)
				for (int i = 0; i < nToAdd; i++) {
					nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else if (a - year >= 0) /* if not newborns */{
				if (this.minAge[thisScen - 1] > a - year
						|| this.maxAge[thisScen - 1] < a - year)
					for (int i = 0; i < nToAdd; i++) {
						nominator += inputRef[i] * nInRef[i];
						denominator += nInRef[i];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						nominator += (1 - this.succesrate[thisScen - 1])
								* inputRef[i] * nInRef[i]
								+ (this.succesrate[thisScen - 1])
								* inputScen[i] * nInScen[i];
						;
						denominator += (1 - this.succesrate[thisScen - 1])
								* nInRef[i] + (this.succesrate[thisScen - 1])
								* nInScen[i];
					}
				}
			} else /* if newborns */
			{
				if (this.minAge[thisScen - 1] > 0)
					for (int i = 0; i < nToAdd; i++) {
						nominator += inputRef[i] * nInRef[i];
						denominator += nInRef[i];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						nominator += (1 - this.succesrate[thisScen - 1])
								* inputRef[i] * nInRef[i]
								+ (this.succesrate[thisScen - 1])
								* inputScen[i] * nInScen[i];
						;
						denominator += (1 - this.succesrate[thisScen - 1])
								* nInRef[i] + (this.succesrate[thisScen - 1])
								* nInScen[i];
					}
				}
			}
			;
			if (denominator != 0)
				data = nominator / denominator;
			else
				data = -99999;
		}
		return data;
	}

	private double applySuccesrateToMeanToBothGenders(double[] inputRef,
			double[] inputScen, double[] nInRef, double[] nInScen,
			int thisScen, int year, int a) throws DynamoOutputException {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;
		if (inputRef.length != 2)
			throw new DynamoOutputException(
					" (development error code) gender array has length "
							+ inputRef.length + " in stead of 2");
		if (thisScen == 0)

			for (int i = 0; i < 2; i++) {
				nominator += inputRef[i] * nInRef[i];
				denominator += nInRef[i];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (this.minAge[thisScen - 1] > a - year
					|| this.maxAge[thisScen - 1] < a - year)
				for (int i = 0; i < 2; i++) {
					nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				if (this.inMen[thisScen - 1]) {
					nominator += (1 - this.succesrate[thisScen - 1])
							* inputRef[0] * nInRef[0]
							+ (this.succesrate[thisScen - 1]) * inputScen[0]
							* nInScen[0];
					;
					denominator += (1 - this.succesrate[thisScen - 1])
							* nInRef[0] + (this.succesrate[thisScen - 1])
							* nInScen[0];
				} else {
					nominator += inputRef[0] * nInRef[0];
					denominator += nInRef[0];
				}
				if (this.inWomen[thisScen - 1]) {
					nominator += (1 - this.succesrate[thisScen - 1])
							* inputRef[1] * nInRef[1]
							+ (this.succesrate[thisScen - 1]) * inputScen[1]
							* nInScen[1];
					;
					denominator += (1 - this.succesrate[thisScen - 1])
							* nInRef[1] + (this.succesrate[thisScen - 1])
							* nInScen[1];
				} else {
					nominator += inputRef[1] * nInRef[1];
					denominator += nInRef[1];
				}

			}
		} else /* if newborns */
		{
			if (this.minAge[thisScen - 1] > 0)
				for (int i = 0; i < 2; i++) {
					nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				if (this.inMen[thisScen - 1]) {
					nominator += (1 - this.succesrate[thisScen - 1])
							* inputRef[0] * nInRef[0]
							+ (this.succesrate[thisScen - 1]) * inputScen[0]
							* nInScen[0];
					;
					denominator += (1 - this.succesrate[thisScen - 1])
							* nInRef[0] + (this.succesrate[thisScen - 1])
							* nInScen[0];
				} else {
					nominator += inputRef[0] * nInRef[0];
					denominator += nInRef[0];
				}
				if (this.inWomen[thisScen - 1]) {
					nominator += (1 - this.succesrate[thisScen - 1])
							* inputRef[1] * nInRef[1]
							+ (this.succesrate[thisScen - 1]) * inputScen[1]
							* nInScen[1];
					;
					denominator += (1 - this.succesrate[thisScen - 1])
							* nInRef[1] + (this.succesrate[thisScen - 1])
							* nInScen[1];
				} else {
					nominator += inputRef[1] * nInRef[1];
					denominator += nInRef[1];
				}
			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome after applying
	 * successrates, using data from both sexes. As this needs to be a weighted
	 * mean (weighted by the number of persons) it also needs the weights
	 * (numbers in each scenario) This method uses the fields: successrate,
	 * minage and maxage, so changing these fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step)
	 * @param a
	 *            : age
	 * 
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToMeanToBothGenders(double[][] inputRef,
			double[][] inputScen, double[][] nInRef, double[][] nInScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		double denominator0 = 0;
		double nominator0 = 0;
		double denominator1 = 0;
		double nominator1 = 0;
		int nToAdd = inputRef.length;

		if (thisScen == 0)
			for (int i = 0; i < nToAdd; i++) {
				nominator0 += inputRef[i][0] * nInRef[i][0];
				denominator0 += nInRef[i][0];
				nominator1 += inputRef[i][1] * nInRef[i][1];
				denominator1 += nInRef[i][1];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (this.minAge[thisScen - 1] > a - year
					|| this.maxAge[thisScen - 1] < a - year)
				for (int i = 0; i < nToAdd; i++) {
					nominator0 += inputRef[i][0] * nInRef[i][0];
					denominator0 += nInRef[i][0];
					nominator1 += inputRef[i][1] * nInRef[i][1];
					denominator1 += nInRef[i][1];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					if (this.inMen[thisScen - 1]) {
						nominator0 += (1 - this.succesrate[thisScen - 1])
								* inputRef[i][0] * nInRef[i][0]
								+ (this.succesrate[thisScen - 1])
								* inputScen[i][0] * nInScen[i][0];
						;
						denominator0 += (1 - this.succesrate[thisScen - 1])
								* nInRef[i][0]
								+ (this.succesrate[thisScen - 1])
								* nInScen[i][0];
					} else {
						nominator0 += inputRef[i][0] * nInRef[i][0];
						denominator0 += nInRef[i][0];
					}
					if (this.inWomen[thisScen - 1]) {
						nominator1 += (1 - this.succesrate[thisScen - 1])
								* inputRef[i][1] * nInRef[i][1]
								+ (this.succesrate[thisScen - 1])
								* inputScen[i][1] * nInScen[i][1];
						;
						denominator1 += (1 - this.succesrate[thisScen - 1])
								* nInRef[i][1]
								+ (this.succesrate[thisScen - 1])
								* nInScen[i][1];
					}

					else {
						nominator1 += inputRef[i][1] * nInRef[i][1];
						denominator1 += nInRef[i][1];
					}
				}
			}
		} else /* if newborns */
		{
			if (this.minAge[thisScen - 1] > 0)
				for (int i = 0; i < nToAdd; i++) {
					nominator0 += inputRef[i][0] * nInRef[i][0];
					denominator0 += nInRef[i][0];
					nominator1 += inputRef[i][1] * nInRef[i][1];
					denominator1 += nInRef[i][1];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					if (this.inMen[thisScen - 1]) {
						nominator0 += (1 - this.succesrate[thisScen - 1])
								* inputRef[i][0] * nInRef[i][0]
								+ (this.succesrate[thisScen - 1])
								* inputScen[i][0] * nInScen[i][0];
						;
						denominator0 += (1 - this.succesrate[thisScen - 1])
								* nInRef[i][0]
								+ (this.succesrate[thisScen - 1])
								* nInScen[i][0];
					} else {
						nominator0 += inputRef[i][0] * nInRef[i][0];
						denominator0 += nInRef[i][0];
					}
					if (this.inWomen[thisScen - 1]) {
						nominator1 += (1 - this.succesrate[thisScen - 1])
								* inputRef[i][1] * nInRef[i][1]
								+ (this.succesrate[thisScen - 1])
								* inputScen[i][1] * nInScen[i][1];
						;
						denominator1 += (1 - this.succesrate[thisScen - 1])
								* nInRef[i][1]
								+ (this.succesrate[thisScen - 1])
								* nInScen[i][1];
					} else {
						nominator1 += inputRef[i][1] * nInRef[i][1];
						denominator1 += nInRef[i][1];
					}
				}
			}
		}
		;
		if (denominator0 + denominator1 != 0)
			data = (nominator0 + nominator1) / (denominator0 + denominator1);
		else
			data = -99999;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome after applying
	 * successrates. As this needs to be a weighted mean (weighted by the number
	 * of persons) it also needs the weights (numbers in each scenario) This
	 * method uses the fields: successrate, minage and maxage, so changing these
	 * fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step)
	 * @param a
	 *            : age
	 * @param gender
	 *            : gender
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToMean(double inputRef, double inputScen,
			double nInRef, double nInScen, int thisScen, int year, int a,
			int gender) {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;

		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;
		else {
			if (gender == 0 && !this.inMen[thisScen - 1])
				doApply = false;
			if (gender == 1 && !this.inWomen[thisScen - 1])
				doApply = false;

		}
		if (!doApply) {

			nominator += inputRef * nInRef;
			denominator += nInRef;
		} else if (a - year >= 0)/* if not newborns */{
			if (this.minAge[thisScen - 1] > a - year
					|| this.maxAge[thisScen - 1] < a - year) {
				nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {

				nominator += (1 - this.succesrate[thisScen - 1]) * inputRef
						* nInRef + (this.succesrate[thisScen - 1]) * inputScen
						* nInScen;
				;
				denominator += (1 - this.succesrate[thisScen - 1]) * nInRef
						+ (this.succesrate[thisScen - 1]) * nInScen;

			}
		} else { /* for newborns */
			if (this.minAge[thisScen - 1] > 0) {
				nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {

				nominator += (1 - this.succesrate[thisScen - 1]) * inputRef
						* nInRef + (this.succesrate[thisScen - 1]) * inputScen
						* nInScen;
				;
				denominator += (1 - this.succesrate[thisScen - 1]) * nInRef
						+ (this.succesrate[thisScen - 1]) * nInScen;

			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;
		return data;
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, int toWrite)
			throws XMLStreamException {

		/*
		 * check for NaN by demanding that the value is larger then -99999999
		 */
		if (toWrite > -99999999) {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "Number");
			writer.writeCharacters(((Integer) toWrite).toString());
			writer.writeEndElement();
			writer.writeEndElement();
		} else {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("NaN");
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, float toWrite)
			throws XMLStreamException {

		/*
		 * check for NaN by demanding that the value is larger then -99999999
		 */
		if (toWrite > -99999999) {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "Number");
			writer.writeCharacters(((Float) toWrite).toString());
			writer.writeEndElement();
			writer.writeEndElement();
		} else {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("NaN");
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, double toWrite)
			throws XMLStreamException {

		/*
		 * check for NaN by demanding that the value is larger then -99999999
		 */
		if (toWrite > -99999999) {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "Number");
			writer.writeCharacters(((Double) toWrite).toString());
			writer.writeEndElement();
			writer.writeEndElement();
		} else {
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("NaN");
			writer.writeEndElement();
			writer.writeEndElement();
		}
	}

	/**
	 * this method writes the XML for a cell of an excell spreadsheet
	 * 
	 * @param writer
	 *            stream to write to
	 * @param toWrite
	 *            number to write
	 * @throws XMLStreamException
	 */
	private void writeCell(XMLStreamWriter writer, String toWrite)
			throws XMLStreamException {

		writer.writeStartElement("Cell");
		writer.writeStartElement("Data");
		writer.writeAttribute("ss:Type", "String");
		writer.writeCharacters(toWrite);
		writer.writeEndElement();
		writer.writeEndElement();
	}

	/**
	 * This method makes a plot for survival over simulation time for all
	 * scenario's. <br>
	 * Survival plotted is that of all individuals in the population without the
	 * newborns <br>
	 * Survival is the fraction of persons with whom the simulation starts
	 * 
	 * @param gender
	 *            : 0= for men; 1= for women; 2= for entire population
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) if true absolute numbers are plotted in stead of
	 *            percentage of starting population
	 * 
	 * @return freechart plot
	 */
	public JFreeChart makeSurvivalPlotByScenario(int gender, int riskClass,
			boolean differencePlot, boolean numbers) {
		XYDataset xyDataset = null;
		double[][][][] nPopByAge = null;
		if (riskClass < 0)
			nPopByAge = getNPopByOriAge();
		else
			nPopByAge = getNPopByOriAge(riskClass);
		int nDim2 = nPopByAge[0][0].length;
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			XYSeries series = new XYSeries(this.scenarioNames[thisScen]);
			double dat0 = 0;

			for (int steps = 0; steps < this.stepsInRun + 1; steps++) {
				double indat = 0;
				double indatr = 0;
				/*
				 * popByAge has value 1 at steps= 0) // TODO this does not work
				 * OK when ageMax and min are applied
				 */

				for (int age = 0; age < nDim2; age++)
					if (gender < 2) {
						indat += applySuccesrate(
								nPopByAge[0][steps][age][gender],
								nPopByAge[thisScen][steps][age][gender],
								thisScen, steps, age, gender);
						indatr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age, 0)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, steps, age, 1);
						indatr += nPopByAge[0][steps][age][0]
								+ nPopByAge[0][steps][age][1];
					}

				if (steps == 0)
					dat0 = indat;

				if (dat0 > 0) {
					if (differencePlot && !numbers)
						series.add((double) steps,
								100 * ((indat / dat0) - (indatr / dat0)));
					if (!differencePlot && !numbers)
						series.add((double) steps, 100 * (indat / dat0));
					if (differencePlot && numbers)
						series.add((double) steps, (indat) - (indatr));
					if (!differencePlot && numbers)
						series.add((double) steps, indat);

				}
			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(series);
			else
				((XYSeriesCollection) xyDataset).addSeries(series);
		}
		String label;
		String chartTitle = "survival ";
		if (numbers && differencePlot)
			chartTitle = "excess numbers in population"
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess survival" + " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "numbers in population ";
		String yTitle = "survival (%)";
		if (differencePlot && !numbers)
			yTitle = "excess survival (%)";
		if (!differencePlot && numbers)
			yTitle = "population numbers";
		if (differencePlot && numbers)
			yTitle = "excess population numbers";

		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		if (this.stepsInRun == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);

		/*
		 * ChartFrame frame1 = new ChartFrame("Survival Chart " + label, chart);
		 * frame1.setVisible(true); frame1.setSize(300, 300); try {
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "survivalplot_" + label + ".jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage());
		 * System.out.println("Problem occurred creating chart."); }
		 */
		return chart;
	}

	/**
	 * makes a plot of the prevalence of disease d by gender with year on the
	 * axis for one scenario
	 * 
	 * @param thisScen
	 *            : scenario number
	 * @param d
	 *            : disease number
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) plot absolute numbers in stead of percentages
	 * 
	 * @return JFreeChart with the plot
	 */
	public JFreeChart makeYearPrevalenceByGenderPlot(int thisScen, int d,
			boolean differencePlot, boolean numbers) {

		XYSeries menSeries = new XYSeries("men");

		XYSeries womenSeries = new XYSeries("women");
		XYSeries totalSeries = new XYSeries("total");
		for (int steps = 0; steps < this.stepsInRun + 1; steps++) {
			double indat = 0;
			double indatr = 0;
			indat = calculateAveragePrevalence(thisScen, steps, d, 0, numbers);
			indatr = calculateAveragePrevalence(0, steps, d, 0, numbers);
			if (!differencePlot)
				menSeries.add((double) steps + this.startYear, indat);
			else
				menSeries.add((double) steps + this.startYear, indat - indatr);
			indat = calculateAveragePrevalence(thisScen, steps, d, 1, numbers);
			indatr = calculateAveragePrevalence(0, steps, d, 1, numbers);
			if (!differencePlot)
				womenSeries.add((double) steps + this.startYear, indat);
			else
				womenSeries
						.add((double) steps + this.startYear, indat - indatr);
			indat = calculateAveragePrevalence(thisScen, steps, d, 2, numbers);
			indatr = calculateAveragePrevalence(0, steps, d, 2, numbers);
			if (!differencePlot)
				totalSeries.add((double) steps + this.startYear, indat);
			else
				totalSeries
						.add((double) steps + this.startYear, indat - indatr);

		}
		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);
		((XYSeriesCollection) xyDataset).addSeries(totalSeries);
		JFreeChart chart;

		String name = "disability (average dalyweight)";
		if (d < nDiseases)
			name = this.diseaseNames[d];
		String chartTitle = "prevalence of " + name;
		if (numbers && differencePlot)
			chartTitle = "Excess number with " + name
					+ " compared to the ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "Excess prevalence of " + name
					+ " compared to the ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + name;

		String label = "" + this.scenarioNames[thisScen];

		String yTitle = "prevalence rate (%)" + name;
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;

		chart = ChartFactory.createXYLineChart(chartTitle, "year", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.stepsInRun == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int gender = 0; gender <= 2; gender++) {
			renderer.setSeriesStroke(gender, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(gender, false);
		}
		plot.setRenderer(renderer);

		return chart;
	}

	/*
	 * in the tryout phase of the project the charts were written to file, but
	 * this is no longer needed as the plot can be saved by the user with a
	 * standard menu from chartcomposite Still this is kept here commented out
	 * in case of future return
	 * 
	 * try { writeCategoryChart(baseDir + File.separator + "simulations" +
	 * File.separator + simulationName + File.separator + "results" +
	 * File.separator + "chartPrevalence" + d + ".jpg", chart); } catch
	 * (Exception e) { System.out.println(e.getMessage()); System.out
	 * .println("Problem occurred creating chart. for diseasenumber" + d); }
	 */

	/**
	 * makes a plot of the prevalence of disease d by gender with age on the
	 * axis for one scenario
	 * 
	 * @param thisScen
	 *            : scenario number
	 * @param d
	 *            : disease number
	 * @param year
	 *            : year for which to plot
	 * @param differencePlot
	 *            : (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) plot absolute numbers in stead of percentages
	 * 
	 * 
	 * @return JFreeChart plot of the prevalence of disease d by gender with age
	 *         on the axis for one scenario
	 * 
	 */
	public JFreeChart makeAgePrevalenceByGenderPlot(int thisScen, int d,
			int year, boolean differencePlot, boolean numbers) {

		XYSeries menSeries = new XYSeries("men");
		XYSeries womenSeries = new XYSeries("women");
		XYSeries totalSeries = new XYSeries("total");

		double indat0 = 0; /* diseasenumbers for men */
		double indat1 = 0;/* diseasenumbers for women */
		double npop0 = 0;/* total numbers for women */
		double npop1 = 0;/* total numbers for women */
		double indat0r = 0; /* diseasenumbers for men in reference scenario */
		double indat1r = 0;/* diseasenumbers for women in reference scenario */
		double npop0r = 0;/* total numbers for women in reference scenario */
		double npop1r = 0;/* total numbers for women in reference scenario */
		double[][][][] nPopByAge = getNPopByAge();
		double[][][][] nDiseaseByAge = null;
		if (d < this.nDiseases)
			nDiseaseByAge = getNDiseaseByAge(d);
		else
			nDiseaseByAge = getNDisabledByAge();

		/*
		 * first calculate for men and women separately
		 */

		for (int age = 0; age < 96 + this.stepsInRun; age++) {
			indat0 = applySuccesrate(nDiseaseByAge[0][year][age][0],
					nDiseaseByAge[thisScen][year][age][0], thisScen, year, age,
					0);
			npop0 = applySuccesrate(nPopByAge[0][year][age][0],
					nPopByAge[thisScen][year][age][0], thisScen, year, age, 0);
			indat0r = nDiseaseByAge[0][year][age][0];
			npop0r = nPopByAge[0][year][age][0];
			if (npop0 != 0 && !differencePlot && !numbers)
				menSeries.add((double) age, 100 * indat0 / npop0);
			if (npop0 != 0 && npop0r != 0 && differencePlot && !numbers)
				menSeries.add((double) age, 100 * (indat0 / npop0) - 100
						* (indat0r / npop0r));
			if (npop0 != 0 && !differencePlot && numbers)
				menSeries.add((double) age, indat0);
			if (npop0 != 0 && npop0r != 0 && differencePlot && numbers)
				menSeries.add((double) age, (indat0) - (indat0r));
			indat1 = applySuccesrate(nDiseaseByAge[0][year][age][1],
					nDiseaseByAge[thisScen][year][age][1], thisScen, year, age,
					0);
			npop1 = applySuccesrate(nPopByAge[0][year][age][1],
					nPopByAge[thisScen][year][age][1], thisScen, year, age, 1);
			indat1r = nDiseaseByAge[thisScen][year][age][1];
			npop1r = nPopByAge[thisScen][year][age][1];
			if (npop1 != 0 && !differencePlot && !numbers)

				womenSeries.add((double) age, 100 * indat1 / npop1);

			if (npop1 != 0 && npop1r != 0 && differencePlot && !numbers)
				womenSeries.add((double) age, 100 * (indat1 / npop1) - 100
						* (indat1r / npop1r));

			if (npop0 + npop1 != 0 && !differencePlot && !numbers)
				totalSeries.add((double) age,
						(100 * (indat1 + indat0) / (npop1 + npop0)));

			if (npop0 + npop1 != 0 && npop0r + npop1r != 0 && differencePlot
					&& !numbers)
				totalSeries.add((double) age, (100 * (indat1 + indat0)
						/ (npop1 + npop0) - 100 * (indat1r + indat0r)
						/ (npop1r + npop0r)));
			if (npop1 != 0 && !differencePlot && numbers)

				womenSeries.add((double) age, indat1);

			if (npop1 != 0 && npop1r != 0 && differencePlot && numbers)
				womenSeries.add((double) age, (indat1) - (indat1r));

			if (npop0 + npop1 != 0 && !differencePlot && numbers)
				totalSeries.add((double) age, ((indat1 + indat0)));

			if (npop0 + npop1 != 0 && npop0r + npop1r != 0 && differencePlot
					&& numbers)
				totalSeries.add((double) age,
						((indat1 + indat0) - (indat1r + indat0r)));

		}

		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);
		((XYSeriesCollection) xyDataset).addSeries(totalSeries);
		JFreeChart chart;
		String name = "disability (average dalyweight)";
		if (d < nDiseases)
			name = this.diseaseNames[d];
		String yTitle = "prevalence rate (%)" + name;
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;

		String label = this.scenarioNames[thisScen] + "; "
				+ (this.startYear + year);

		String chartTitle = "prevalence of " + name;
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + name
					+ "compared to ref scenario";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + name
					+ "compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + name;

		chart = ChartFactory.createXYLineChart(chartTitle, "age", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.maxAgeInSimulation == this.minAgeInSimulation)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int gender = 0; gender <= 2; gender++) {
			renderer.setSeriesStroke(gender, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(gender, false);
		}
		plot.setRenderer(renderer);

		return chart;
	}

	/**
	 * makes a plot of the prevalence of disease d by scenario with age on the
	 * axis for one scenario
	 * 
	 * @param gender
	 *            : 2= both
	 * @param d
	 *            : disease number
	 * @param year
	 *            : year for which to plot
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) plot absolute numbers in stead of percentages
	 * 
	 * @return plot with prevalence of disease d by scenario with age on the
	 *         axis for one scenario
	 * 
	 */
	public JFreeChart makeAgePrevalenceByScenarioPlot(int gender, int d,
			int year, boolean differencePlot, boolean numbers) {

		XYSeries scenSeries[] = new XYSeries[this.nScen + 1];
		XYDataset xyDataset = null;
		double indat0 = 0; /* diseasenumbers for men */
		double indat1 = 0;/* diseasenumbers for women */
		double npop0 = 0;/* total numbers for women */
		double npop1 = 0;/* total numbers for women */

		double indat0r = 0; /* diseasenumbers for men in reference scenario */
		double indat1r = 0;/* diseasenumbers for women in reference scenario */
		double npop0r = 0;/* total numbers for women in reference scenario */
		double npop1r = 0;/* total numbers for women in reference scenario */
		double[][][][] nPopByAge = getNPopByAge();
		double[][][][] nDiseaseByAge = null;
		if (d < this.nDiseases)
			nDiseaseByAge = getNDiseaseByAge(d);
		else
			nDiseaseByAge = getNDisabledByAge();

		/*
		 * first calculate for men and women separately
		 */

		for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(this.scenarioNames[thisScen]);

			for (int age = 0; age < 96 + this.stepsInRun; age++) {
				indat0 = 0;
				indat1 = 0;
				npop0 = 0;
				npop1 = 0;
				indat0r = 0;
				indat1r = 0;
				npop0r = 0;
				npop1r = 0;
				if (gender < 2) {
					indat0 += applySuccesrate(
							nDiseaseByAge[0][year][age][gender],
							nDiseaseByAge[thisScen][year][age][gender],
							thisScen, year, age, gender);
					npop0 += applySuccesrate(nPopByAge[0][year][age][gender],
							nPopByAge[thisScen][year][age][gender], thisScen,
							year, age, gender);
					indat0r += nDiseaseByAge[0][year][age][gender];
					npop0r += nPopByAge[0][year][age][gender];
				} else {
					indat0 += applySuccesrate(nDiseaseByAge[0][year][age][0],
							nDiseaseByAge[thisScen][year][age][0], thisScen,
							year, age, 0);
					npop0 += applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age, 0);
					indat1 += applySuccesrate(nDiseaseByAge[0][year][age][1],
							nDiseaseByAge[thisScen][year][age][1], thisScen,
							year, age, 1);
					npop1 += applySuccesrate(nPopByAge[0][year][age][1],
							nPopByAge[thisScen][year][age][1], thisScen, year,
							age, 1);
					indat0r += nDiseaseByAge[0][year][age][0];
					npop0r += nPopByAge[0][year][age][0];
					indat1r += nDiseaseByAge[0][year][age][1];
					npop1r += nPopByAge[0][year][age][1];

				}

				if (gender < 2) {
					if (npop0 != 0 && !differencePlot && !numbers)
						scenSeries[thisScen].add((double) age, 100 * indat0
								/ npop0);
					if (differencePlot && npop0 != 0 && npop0r != 0 && !numbers) {
						/* get rid of numerical errors */
						if (Math.abs((indat0 / npop0) - (indat0r / npop0r)) < 0.1 / npop0)
							scenSeries[thisScen].add((double) age, 0);
						else
							scenSeries[thisScen].add((double) age, 100
									* (indat0 / npop0) - (indat0r / npop0r));
					}
					if (npop0 != 0 && !differencePlot && numbers)
						scenSeries[thisScen].add((double) age, indat0);
					if (differencePlot && npop0 != 0 && npop0r != 0 && numbers) {
						/* get rid of numerical errors */
						if (Math.abs(indat0 - indat0r) < 0.1)
							scenSeries[thisScen].add((double) age, 0);
						else
							scenSeries[thisScen].add((double) age, (indat0)
									- (indat0r));
					}
				} else {

					if ((npop0 + npop1) != 0 && !differencePlot && !numbers)

						scenSeries[thisScen].add((double) age, 100
								* (indat0 + indat1) / (npop0 + npop1));
					if ((npop0 + npop1) != 0 && (npop0r + npop1r) != 0
							&& differencePlot && !numbers)
						scenSeries[thisScen].add((double) age, 100
								* (indat0 + indat1) / (npop0 + npop1) - 100
								* (indat0r + indat1r) / (npop0r + npop1r));
					if ((npop0 + npop1) != 0 && !differencePlot && numbers)

						scenSeries[thisScen].add((double) age,
								(indat0 + indat1));
					if ((npop0 + npop1) != 0 && (npop0r + npop1r) != 0
							&& differencePlot && numbers)
						scenSeries[thisScen].add((double) age,
								(indat0 + indat1) - (indat0r + indat1r));
				}
			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);
		}

		JFreeChart chart;
		String label = "" + (this.startYear + year);
		if (gender == 0)
			label = " men; " + (this.startYear + year);
		if (gender == 1)
			label = " women; " + (this.startYear + year);
		String name = "disability (average dalyweight)";
		if (d < nDiseases)
			name = this.diseaseNames[d];

		String chartTitle = "prevalence of " + name;
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + name
					+ " compared to ref scenario";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + name
					+ " compared to ref scenario";
		if (numbers && !differencePlot && gender == 0)
			chartTitle = "number of men with " + name;
		if (numbers && !differencePlot && gender == 1)
			chartTitle = "number of women with " + name;

		if (numbers && !differencePlot && gender == 2)
			chartTitle = "number of persons with " + name;

		String yTitle = "prevalence rate (%)" + name;
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;

		chart = ChartFactory.createXYLineChart(chartTitle, "age", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.maxAgeInSimulation == this.minAgeInSimulation)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);
		return chart;
	}

	/**
	 * method plots the mean value of the riskFactor
	 * 
	 * @param gender
	 * @param differencePlot
	 */
	public void makeYearMeanOfRiskfactorByScenarioPlots(int gender,
			boolean differencePlot) {
		double[][][][] nPopByAge = getNPopByAge();

		XYSeries dataSeries = null;

		XYDataset xyDataset = null;
		for (int thisScen = 0; thisScen < this.nScen; thisScen++) {
			for (int steps = 0; steps < this.stepsInRun + 1; steps++) {
				double meandat = 0;
				double meandatR = 0;
				if (thisScen == 0) {
					dataSeries = new XYSeries("reference scenario");
				} else
					dataSeries = new XYSeries("scenario "
							+ this.scenarioNames[thisScen]);
				for (int age = 0; age < 96 + this.stepsInRun; age++) {

					meandat += applySuccesrateToMean(
							this.meanRiskByAge[0][steps][age][gender],
							this.meanRiskByAge[thisScen][steps][age][gender],
							nPopByAge[0][steps][age][gender],
							nPopByAge[thisScen][steps][age][gender], thisScen,
							steps, age, gender);
					meandat += this.meanRiskByAge[0][steps][age][gender];
				}

				if (!differencePlot)
					dataSeries.add((double) steps + this.startYear, meandat);
				else
					dataSeries.add((double) steps + this.startYear, meandat
							- meandatR);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(dataSeries);
			else
				((XYSeriesCollection) xyDataset).addSeries(dataSeries);
		}
		JFreeChart chart;
		String label = "both sexes";
		if (gender == 0)
			label = "men";
		if (gender == 1)
			label = "women";
		String chartTitle = "mean value of riskfactor ";
		if (differencePlot)
			chartTitle = "mean value of riskfactor compared to reference scenario";
		chart = ChartFactory.createXYLineChart(chartTitle, "year",
				"mean value", xyDataset, PlotOrientation.VERTICAL, true, true,
				false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);

		/*
		 * ChartFrame frame1 = new ChartFrame("RiskfactorAverage", chart);
		 * frame1.setVisible(true); frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try { writeCategoryChart( baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "riskfactorAverage" + ".jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println
		 * ("Problem occurred creating chart. for average of riskfactor"); }
		 */

	}

	/**
	 * method that averages the prevalence over all ages for a particular
	 * scenario
	 * 
	 * @param thisScen
	 * 
	 * @param year
	 *            * @param d : disease number
	 * @param gender
	 *            : gender: 0=male, 1=female, 2=both
	 * @param numbers
	 *            : if true, return numbers in stead of prevalence
	 * @return prevalence averaged over all age groups
	 * 
	 */
	private double calculateAveragePrevalence(int thisScen, int year, int d,
			int gender, boolean numbers) {
		double nominator = 0;
		double denominator = 0;
		double[][][] nPopByAge = getNPopByAge(year);
		double[][][] nDiseaseByAge = null;
		if (d < this.nDiseases)
			nDiseaseByAge = getNDiseaseByAge(d, year);
		else
			nDiseaseByAge = getNDisabledByAge(year);

		nominator = 0;
		denominator = 0;
		if (gender < 2) {
			for (int age = 0; age < 96 + this.stepsInRun; age++) {
				nominator += applySuccesrate(nDiseaseByAge[0][age][gender],
						nDiseaseByAge[thisScen][age][gender], thisScen, year,
						age, gender);
				denominator += applySuccesrate(nPopByAge[0][age][gender],
						nPopByAge[thisScen][age][gender], thisScen, year, age,
						gender);

			}
		} else {
			for (int age = 0; age < 96 + this.stepsInRun; age++) {
				nominator += applySuccesrate(nDiseaseByAge[0][age][0],
						nDiseaseByAge[thisScen][age][0], thisScen, year, age, 0)
						+ applySuccesrate(nDiseaseByAge[0][age][1],
								nDiseaseByAge[thisScen][age][1], thisScen,
								year, age, 1);
				;
				denominator += applySuccesrate(nPopByAge[0][age][0],
						nPopByAge[thisScen][age][0], thisScen, year, age, 0)
						+ applySuccesrate(nPopByAge[0][age][1],
								nPopByAge[thisScen][age][1], thisScen, year,
								age, 1);
			}

		}
		if (denominator > 0 && !numbers)
			nominator = 100 * nominator / denominator;

		if (denominator == 0)
			nominator = 0;

		return nominator;
	}

	/**
	 * method that averages the prevalence over all ages
	 * 
	 * @param thisScen
	 * @param d
	 *            : disease number * @param r: riskfactor class
	 * @param steps
	 * @param numbers
	 *            (boolean) indicates whether prevalences should be returned or
	 *            absolute numbers
	 * @return prevalence IN PERCENT averaged over all age groups, or absolute
	 *         numbers
	 */
	private double calculateAveragePrevalenceByRiskClass(int thisScen,
			int steps, int d, int r, int gender, boolean numbers) {
		double nominator = 0;
		double denominator = 0;
		denominator = 0;
		double[][][][] nDiseaseByRiskClassByAge = null;
		if (d < this.nDiseases)
			nDiseaseByRiskClassByAge = makeDiseaseArray(
					this.nDiseaseStateByRiskClassByAge, steps, d);
		else
			nDiseaseByRiskClassByAge = getNDisabledByRiskClassByAge(steps);

		if (gender < 2) {
			for (int age = 0; age < 96 + this.stepsInRun; age++) {
				if (d >= 0)
					nominator += applySuccesrate(
							nDiseaseByRiskClassByAge[0][r][age][gender],
							nDiseaseByRiskClassByAge[thisScen][r][age][gender],
							thisScen, steps, age, gender);
				denominator += applySuccesrate(
						this.nPopByRiskClassByAge[0][steps][r][age][gender],
						this.nPopByRiskClassByAge[thisScen][steps][r][age][gender],
						thisScen, steps, age, gender);
			}
		} else {
			for (int age = 0; age < 96 + this.stepsInRun; age++) {
				nominator += applySuccesrate(
						nDiseaseByRiskClassByAge[0][r][age][0],
						nDiseaseByRiskClassByAge[thisScen][r][age][0],
						thisScen, steps, age, 0)
						+ applySuccesrate(
								nDiseaseByRiskClassByAge[0][r][age][1],
								nDiseaseByRiskClassByAge[thisScen][r][age][1],
								thisScen, steps, age, 1);
				;
				denominator += applySuccesrate(
						this.nPopByRiskClassByAge[0][steps][r][age][0],
						this.nPopByRiskClassByAge[thisScen][steps][r][age][0],
						thisScen, steps, age, 0)
						+ applySuccesrate(
								this.nPopByRiskClassByAge[0][steps][r][age][1],
								this.nPopByRiskClassByAge[thisScen][steps][r][age][1],
								thisScen, steps, age, 1);
			}

		}
		if (denominator > 0 && !numbers)
			nominator = 100 * nominator / denominator;
		else if (denominator == 0)
			nominator = 0;
		/*
		 * denominator new DynamoOutdenominator
		 * "zero persons in initial population for risk factor class: " +r+
		 * "  no disease " + "prevalence can be calculated ");
		 */
		return nominator;
	}

	/**
	 * makes prevalence plot by riskfactor for scenario thisScen
	 * 
	 * @param gender
	 *            : gender (0=men, 1=women, 2=both)
	 * 
	 * @param thisScen
	 *            : scenario number for which to make the plot
	 * @param d
	 *            : disease number
	 * @param differencePlot
	 *            : plot difference between scenario and reference scenario in
	 *            stead of the prevalence for the scenario
	 * @param numbers
	 *            plot absolute numbers in stead of percentages in the
	 *            population
	 * @return plot (JFreeChart)
	 */
	public JFreeChart makeYearPrevalenceByRiskFactorPlots(int gender,
			int thisScen, int d, boolean differencePlot, boolean numbers) {
		XYDataset xyDataset = null;

		for (int r = 0; r < this.nRiskFactorClasses; r++) {
			XYSeries series = null;

			series = new XYSeries(this.riskClassnames[r]);
			for (int steps = 0; steps < this.stepsInRun + 1; steps++) {
				/*
				 * calculateAveragePrevalenceByRiskClass already multiplies the
				 * prevalence rate with 100 to get percentages
				 */
				double indat0 = calculateAveragePrevalenceByRiskClass(thisScen,
						steps, d, r, gender, numbers);
				double refdat0 = calculateAveragePrevalenceByRiskClass(0,
						steps, d, r, gender, numbers);
				if (!differencePlot)
					series.add((double) steps + this.startYear, indat0);
				if (differencePlot)
					series.add((double) steps + this.startYear,
							(indat0 - refdat0));

			}
			if (r == 0)
				xyDataset = new XYSeriesCollection(series);

			if (r > 0)
				((XYSeriesCollection) xyDataset).addSeries(series);

		}
		String label = this.scenarioNames[thisScen];
		if (gender == 0)
			label = "men; " + this.scenarioNames[thisScen];
		if (gender == 1)
			label = "women; " + this.scenarioNames[thisScen];
		String name = "disability (average dalyweight)";
		if (d < nDiseases)
			name = this.diseaseNames[d];
		String chartTitle = "prevalence of " + name;
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + name
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + name
					+ " compared to ref scenario";
		if (numbers && !differencePlot && gender == 0)
			chartTitle = "number of men with " + name;
		if (numbers && !differencePlot && gender == 1)
			chartTitle = "number of women with " + name;
		if (numbers && !differencePlot && gender == 2)
			chartTitle = "number of persons with " + name;
		String yTitle = "prevalence rate (%)" + name;
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.stepsInRun == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r < this.nRiskFactorClasses; r++) {
			renderer.setSeriesStroke(r, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(r, false);
		}
		plot.setRenderer(renderer);

		return chart;
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * makes a plot of the prevalence against year of simulation, or for the
	 * difference in prevalence compared to the reference scenario for disease d
	 * and gender g
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param d
	 *            : diseasenumber
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            : the outcome are numbers in stead of percentages
	 * @return JFreechart plot of the prevalence against year of simulation, or
	 *         for the difference in prevalence compared to the reference
	 *         scenario for disease d and gender g
	 * 
	 */
	public JFreeChart makeYearPrevalenceByScenarioPlots(int gender, int d,
			boolean differencePlot, boolean numbers)
	/* throws DynamoOutputException */{
		double[][][][][] nDiseaseByRiskClassByAge = null;
		if (d < this.nDiseases)
			nDiseaseByRiskClassByAge = makeDiseaseArray(
					this.nDiseaseStateByRiskClassByAge, d);
		else
			nDiseaseByRiskClassByAge = getNDisabledByRiskClassByAge();
		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[this.nScen + 1];
		for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {
			scenSeries[thisScen] = new XYSeries(this.scenarioNames[thisScen]);
			for (int steps = 0; steps < this.stepsInRun + 1; steps++) {
				double indat = 0;
				double npop = 0;
				double indatr = 0;
				double npopr = 0;
				if (gender < 2) {
					for (int r = 0; r < this.nRiskFactorClasses; r++) {
						for (int age = 0; age < 96 + this.stepsInRun; age++) {
							indat += applySuccesrate(
									nDiseaseByRiskClassByAge[0][steps][r][age][gender],
									nDiseaseByRiskClassByAge[thisScen][steps][r][age][gender],
									thisScen, steps, age, gender);
							npop += applySuccesrate(
									this.nPopByRiskClassByAge[0][steps][r][age][gender],
									this.nPopByRiskClassByAge[thisScen][steps][r][age][gender],
									thisScen, steps, age, gender);
							indatr += nDiseaseByRiskClassByAge[0][steps][r][age][gender];
							npopr += this.nPopByRiskClassByAge[0][steps][r][age][gender];
						}
					}
				} else {
					for (int r = 0; r < this.nRiskFactorClasses; r++) {

						for (int age = 0; age < 96 + this.stepsInRun; age++) {
							indat += applySuccesrate(
									nDiseaseByRiskClassByAge[0][steps][r][age][0],
									nDiseaseByRiskClassByAge[thisScen][steps][r][age][0],
									thisScen, steps, age, 0)
									+ applySuccesrate(
											nDiseaseByRiskClassByAge[0][steps][r][age][1],
											nDiseaseByRiskClassByAge[thisScen][steps][r][age][1],
											thisScen, steps, age, 1);
							;
							npop += applySuccesrate(
									this.nPopByRiskClassByAge[0][steps][r][age][0],
									this.nPopByRiskClassByAge[thisScen][steps][r][age][0],
									thisScen, steps, age, 0)
									+ applySuccesrate(
											this.nPopByRiskClassByAge[0][steps][r][age][1],
											this.nPopByRiskClassByAge[thisScen][steps][r][age][1],
											thisScen, steps, age, 1);

							indatr += nDiseaseByRiskClassByAge[0][steps][r][age][0]
									+ nDiseaseByRiskClassByAge[0][steps][r][age][1];
							npopr += this.nPopByRiskClassByAge[0][steps][r][age][0]
									+ this.nPopByRiskClassByAge[0][steps][r][age][1];
						}

					}
				}
				if (npop != 0 && !differencePlot && !numbers) {
					indat = 100 * indat / npop;
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat);
				}
				if (npop != 0 && npopr != 0 && differencePlot && !numbers) {
					indat = (indat / npop) - (indatr / npopr);
					/* get rid of numerical error in case no difference */
					if (Math.abs(indat) < 0.1 / npop)
						indat = 0;
					scenSeries[thisScen].add((double) steps + this.startYear,
							100 * indat);
				}
				if (npop != 0 && !differencePlot && numbers) {

					scenSeries[thisScen].add((double) steps + this.startYear,
							indat);
				}
				if (npop != 0 && npopr != 0 && differencePlot && numbers) {

					indat = (indat) - (indatr);
					/* get rid of numerical error in case no difference */
					if (Math.abs(indat) < 0.1)
						indat = 0;
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat);
				}
			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}
		String label = "";
		if (gender == 0)
			label = "men";
		if (gender == 1)
			label = "women";
		String name = "disability (average dalyweight)";
		if (d < nDiseases)
			name = this.diseaseNames[d];
		String chartTitle = "prevalence of " + name;
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + name
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + name
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + name;
		String yTitle = "prevalence rate (%) " + name;
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.stepsInRun == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);

		return chart;
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * makes a plot of the prevalence against age, or for the difference in
	 * prevalence compared to the reference scenario for disease d and gender g
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param thisScen
	 *            : scenario for which to plot
	 * @param d
	 *            : diseasenumber
	 * @param year
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            : the outcome are numbers in stead of percentages
	 * 
	 * @return JFreeChart plot
	 * 
	 */
	public JFreeChart makeAgePrevalenceByRiskFactorPlots(int gender,
			int thisScen, int d, int year, boolean differencePlot,
			boolean numbers) {
		XYDataset xyDataset = null;
		double[][][][] nDiseaseByRiskClassByAge = null;
		if (d < this.nDiseases)
			nDiseaseByRiskClassByAge = makeDiseaseArray(
					this.nDiseaseStateByRiskClassByAge, year, d);
		else
			nDiseaseByRiskClassByAge = getNDisabledByRiskClassByAge(year);
		for (int r = 0; r < this.nRiskFactorClasses; r++) {

			XYSeries menSeries = new XYSeries(this.riskClassnames[r]);
			XYSeries womenSeries = new XYSeries(this.riskClassnames[r]);
			XYSeries totSeries = new XYSeries(this.riskClassnames[r]);
			double mendat = 0;
			double womendat = 0;
			double menpop = 0;
			double womenpop = 0;
			double mendatr = 0;
			double womendatr = 0;
			double menpopr = 0;
			double womenpopr = 0;
			for (int age = 0; age < 96 + this.stepsInRun; age++) {
				mendat = applySuccesrate(
						nDiseaseByRiskClassByAge[0][r][age][0],
						nDiseaseByRiskClassByAge[thisScen][r][age][0],
						thisScen, year, age, 0);

				menpop = applySuccesrate(
						this.nPopByRiskClassByAge[0][year][r][age][0],
						this.nPopByRiskClassByAge[thisScen][year][r][age][0],
						thisScen, year, age, 0);
				mendatr = nDiseaseByRiskClassByAge[0][r][age][0];
				menpopr = this.nPopByRiskClassByAge[0][year][r][age][0];
				if (menpop != 0 && !differencePlot && !numbers)
					menSeries.add((double) age, 100 * mendat / menpop);
				if (menpop != 0 && menpopr != 0 && differencePlot && !numbers)
					menSeries.add((double) age, 100 * (mendat / menpop) - 100
							* (mendatr / menpopr));

				if (menpop != 0 && !differencePlot && numbers)
					menSeries.add((double) age, mendat);
				/*
				 * in case of zero persons in one of the two populations, still
				 * show numbers in the other population, so only no results when
				 * both are zero
				 */
				if (!(menpop == 0 && menpopr == 0) && differencePlot && numbers)
					menSeries.add((double) age, (mendat - mendatr));

				womendat = applySuccesrate(
						nDiseaseByRiskClassByAge[0][r][age][1],
						nDiseaseByRiskClassByAge[thisScen][r][age][1],
						thisScen, year, age, 1);
				womenpop = applySuccesrate(
						this.nPopByRiskClassByAge[0][year][r][age][1],
						this.nPopByRiskClassByAge[thisScen][year][r][age][1],
						thisScen, year, age, 1);
				womendatr = nDiseaseByRiskClassByAge[0][r][age][1];
				womenpopr = this.nPopByRiskClassByAge[0][year][r][age][1];
				if (womenpop != 0 && !numbers && !differencePlot)
					womenSeries.add((double) age, 100 * womendat / womenpop);
				if (womenpop != 0 && womenpopr != 0 && differencePlot
						&& !numbers)
					womenSeries.add((double) age, (100 * womendat / womenpop)
							- (100 * womendatr / womenpopr));
				if (womenpop != 0 && numbers && !differencePlot)
					womenSeries.add((double) age, womendat);
				/*
				 * in case of zero persons in one of the two populations, still
				 * show numbers in the other population, so only no results when
				 * both are zero
				 */

				if (!(womenpop == 0 && womenpopr == 0) && differencePlot
						&& numbers)
					womenSeries.add((double) age, (womendat - womendatr));

				if ((menpop + womenpop) != 0 && !differencePlot && !numbers)
					totSeries.add((double) age, 100 * (womendat + mendat)
							/ (menpop + womenpop));
				if ((menpop + womenpop) != 0 && (menpopr + womenpopr) != 0
						&& differencePlot && !numbers)
					totSeries.add((double) age, 100 * (womendat + mendat)
							/ (menpop + womenpop) - 100 * (womendatr + mendatr)
							/ (menpopr + womenpopr));
				if ((menpop + womenpop) != 0 && !differencePlot && numbers)
					totSeries.add((double) age, (womendat + mendat));

				/*
				 * in case of zero persons in one of the two populations, still
				 * show numbers in the other population
				 */
				if (!((menpop + womenpop) == 0 && (menpopr + womenpopr) == 0)
						&& differencePlot && numbers)
					totSeries.add((double) age, (womendat + mendat)
							- (womendatr + mendatr));

			}
			switch (gender) {
			case 0:
				if (r == 0)
					xyDataset = new XYSeriesCollection(menSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(menSeries);
				break;
			case 1:
				if (r == 0)
					xyDataset = new XYSeriesCollection(womenSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(womenSeries);
				break;
			case 2:
				if (r == 0)
					xyDataset = new XYSeriesCollection(totSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(totSeries);
				break;
			}

		}
		String label = this.scenarioNames[thisScen] + "; "
				+ (this.startYear + year);
		if (gender == 0)
			label = "men; " + this.scenarioNames[thisScen] + "; "
					+ (this.startYear + year);

		if (gender == 1)
			label = "women; " + this.scenarioNames[thisScen] + "; "
					+ (this.startYear + year);
		String name = "disability (average dalyweight)";
		if (d < nDiseases)
			name = this.diseaseNames[d];
		String chartTitle = "prevalence of " + name;
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + name
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + name
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			if (gender == 0)
				chartTitle = "number of men with " + name;
			else if (gender == 1)
				chartTitle = "number of women with " + name;
			else
				chartTitle = "number of persons with " + name;
		String yTitle = "prevalence rate (%) " + name;
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "age",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.maxAgeInSimulation == this.minAgeInSimulation)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r < this.nRiskFactorClasses; r++) {
			renderer.setSeriesStroke(r, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(r, false);
		}
		plot.setRenderer(renderer);
		return chart;
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * plot riskfactorclass data for a single scenario separate for men and
	 * women
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @return :plot of riskfactorclass data for a single scenario separate for
	 *         men and women
	 */
	public JFreeChart makeYearRiskFactorByScenarioPlot(int gender,
			int riskClass, boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[this.nScen + 1];
		double[][][][] nPopByAge = getNPopByAge();
		for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(this.scenarioNames[thisScen]);

			for (int steps = 0; steps < this.stepsInRun + 1; steps++) {
				double indat = 0;
				double denominator = 0;
				double indatr = 0;
				double denominatorr = 0;
				int nDimLocal = nPopByAge[0][0].length;
				for (int age = 0; age < nDimLocal; age++) {
					if (gender < 2) {
						indat += applySuccesrate(
								this.nPopByRiskClassByAge[0][steps][riskClass][age][gender],
								this.nPopByRiskClassByAge[thisScen][steps][riskClass][age][gender],
								thisScen, steps, age, gender);
						denominator += applySuccesrate(
								nPopByAge[0][steps][age][gender],
								nPopByAge[thisScen][steps][age][gender],
								thisScen, steps, age, gender);
						indatr += this.nPopByRiskClassByAge[0][steps][riskClass][age][gender];
						denominatorr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(
								this.nPopByRiskClassByAge[0][steps][riskClass][age][0],
								this.nPopByRiskClassByAge[thisScen][steps][riskClass][age][0],
								thisScen, steps, age, 0)
								+ applySuccesrate(
										this.nPopByRiskClassByAge[0][steps][riskClass][age][1],
										this.nPopByRiskClassByAge[thisScen][steps][riskClass][age][1],
										thisScen, steps, age, 1);
						;
						denominator += applySuccesrate(
								nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age, 0)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, steps, age, 1);
						indatr += this.nPopByRiskClassByAge[0][steps][riskClass][age][0]
								+ this.nPopByRiskClassByAge[0][steps][riskClass][age][1];
						denominatorr += nPopByAge[0][steps][age][0]
								+ nPopByAge[0][steps][age][1];
					}

				}
				if (denominator != 0 && !numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat * 100 / denominator);
				if (denominator != 0 && denominatorr != 0 && !numbers
						&& differencePlot)
					scenSeries[thisScen]
							.add(
									(double) steps + this.startYear,
									100 * ((indat / denominator) - (indatr / denominatorr)));
				if (denominator != 0 && numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat);
				if (denominator != 0 && denominatorr != 0 && numbers
						&& differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat - indatr);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}
		String label = "";
		if (gender == 0)
			label = "men";
		if (gender == 1)
			label = "women";
		String chartTitle = "prevalence of " + this.riskClassnames[riskClass];
		if (numbers && differencePlot)
			chartTitle = "excess number of " + this.riskClassnames[riskClass]
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of "
					+ this.riskClassnames[riskClass]
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of " + this.riskClassnames[riskClass] + label;
		String yTitle = "prevalence rate (%) " + this.riskClassnames[riskClass];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) "
					+ this.riskClassnames[riskClass];
		if (!differencePlot && numbers)
			yTitle = "number with " + this.riskClassnames[riskClass];
		if (differencePlot && numbers)
			yTitle = "excess number of " + this.riskClassnames[riskClass];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.stepsInRun == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);

		// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
		// frame1.setVisible(true);
		// frame1.setSize(300, 300);
		/*
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try { writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for riskfactor"); }
		 */
		return chart;

	}

	/**
	 * plot riskfactorclass data for a single scenario separate for men and
	 * women
	 * 
	 * @param year
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            : riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @return JFreeChart plot of riskfactorclass data for a single scenario
	 *         separate for men and women
	 */
	public JFreeChart makeAgeRiskFactorByScenarioPlot(int year, int gender,
			int riskClass, boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[this.nScen + 1];
		double[][][][] nPopByAge = getNPopByAge();
		for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(this.scenarioNames[thisScen]);

			double indat = 0;
			double denominator = 0;
			double indatR = 0;
			double denominatorR = 0;

			for (int age = 0; age < this.maxAgeInSimulation + year; age++) {
				if (gender < 2) {
					indat = applySuccesrate(
							this.nPopByRiskClassByAge[0][year][riskClass][age][gender],
							this.nPopByRiskClassByAge[thisScen][year][riskClass][age][gender],
							thisScen, year, age, gender);
					denominator = applySuccesrate(
							nPopByAge[0][year][age][gender],
							nPopByAge[thisScen][year][age][gender], thisScen,
							year, age, gender);
					indatR = this.nPopByRiskClassByAge[0][year][riskClass][age][gender];
					denominatorR = nPopByAge[0][year][age][gender];
				} else {
					indat = applySuccesrate(
							this.nPopByRiskClassByAge[0][year][riskClass][age][0],
							this.nPopByRiskClassByAge[thisScen][year][riskClass][age][0],
							thisScen, year, age, 0)
							+ applySuccesrate(
									this.nPopByRiskClassByAge[0][year][riskClass][age][1],
									this.nPopByRiskClassByAge[thisScen][year][riskClass][age][1],
									thisScen, year, age, 1);
					;
					denominator = applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age, 0)
							+ applySuccesrate(nPopByAge[0][year][age][1],
									nPopByAge[thisScen][year][age][1],
									thisScen, year, age, 1);
					indatR = this.nPopByRiskClassByAge[0][year][riskClass][age][0]
							+ this.nPopByRiskClassByAge[0][year][riskClass][age][1];
					denominatorR = nPopByAge[0][year][age][0]
							+ nPopByAge[0][year][age][1];
				}

				if (denominator != 0 && !numbers && !differencePlot)
					scenSeries[thisScen].add((double) age, 100 * indat
							/ denominator);
				if (denominator != 0 && denominatorR != 0 && !numbers
						&& differencePlot)
					scenSeries[thisScen]
							.add(
									(double) age,
									100 * ((indat / denominator) - (indatR / denominatorR)));
				if (denominator != 0 && numbers && !differencePlot)
					scenSeries[thisScen].add((double) age, indat);
				if (denominator != 0 && denominatorR != 0 && numbers
						&& differencePlot)
					scenSeries[thisScen].add((double) age, indat - indatR);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}
		String label = " " + (this.startYear + year);
		if (gender == 0)
			label = "men; " + (this.startYear + year);
		if (gender == 1)
			label = "women; " + (this.startYear + year);

		String chartTitle = "prevalence of " + this.riskClassnames[riskClass];
		if (numbers && differencePlot)
			chartTitle = "excess number of " + this.riskClassnames[riskClass]
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of "
					+ this.riskClassnames[riskClass]
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of " + this.riskClassnames[riskClass];
		chartTitle = chartTitle + label;
		String yTitle = "prevalence rate (%) " + this.riskClassnames[riskClass];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) "
					+ this.riskClassnames[riskClass];
		if (!differencePlot && numbers)
			yTitle = "number with " + this.riskClassnames[riskClass];
		if (differencePlot && numbers)
			yTitle = "excess number of " + this.riskClassnames[riskClass];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "age",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.maxAgeInSimulation == this.minAgeInSimulation)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);

		// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
		// frame1.setVisible(true);
		// frame1.setSize(300, 300);
		/*
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try { writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for riskfactor"); }
		 */
		return chart;

	}

	/**
	 * plot mean value of riskfactor (continuous only) against age for either
	 * men, women or both by scenario women. Age is age during simulation, not
	 * age at the start of simulation
	 * 
	 * @param year
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * 
	 * @param differencePlot
	 *            : plot difference with reference scenario : plot absolute
	 *            numbers
	 * @return JFreeChart plot of mean value of riskfactor (continuous only)
	 *         against age for either men, women or both by scenario.
	 */
	public JFreeChart makeAgeMeanRiskFactorByScenarioPlot(int year, int gender,
			boolean differencePlot) {
		if (this.riskType == 2) {
			XYDataset xyDataset = null;
			XYSeries[] scenSeries = new XYSeries[this.nScen + 1];
			double[][][][] nPopByAge = getNPopByAge();
			for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {

				scenSeries[thisScen] = new XYSeries(
						this.scenarioNames[thisScen]);

				double indat = 0;

				double indatR = 0;
				boolean dataPresent = true;
				int nDimLocal = nPopByAge[0][0].length;
				for (int age = 0; age < nDimLocal; age++) {
					if (gender < 2) {
						indat = applySuccesrateToMean(
								this.meanRiskByAge[0][year][age][gender],
								this.meanRiskByAge[thisScen][year][age][gender],
								nPopByAge[0][year][age][gender],
								nPopByAge[thisScen][year][age][gender],
								thisScen, year, age, gender);

						indatR = this.meanRiskByAge[0][year][age][gender];
						if (applySuccesrate(nPopByAge[0][year][age][gender],
								nPopByAge[thisScen][year][age][gender],
								thisScen, year, age, gender) == 0)
							dataPresent = false;
						if (nPopByAge[0][year][age][gender] == 0
								&& differencePlot)
							dataPresent = false;
					} else {
						double nMen = applySuccesrate(
								nPopByAge[0][year][age][0],
								nPopByAge[thisScen][year][age][0], thisScen,
								year, age, 0);
						double nWomen = applySuccesrate(
								nPopByAge[0][year][age][1],
								nPopByAge[thisScen][year][age][1], thisScen,
								year, age, 1);
						if (nMen + nWomen == 0)
							dataPresent = false;
						indat = nMen
								* applySuccesrateToMean(
										this.meanRiskByAge[0][year][age][0],
										this.meanRiskByAge[thisScen][year][age][0],
										nPopByAge[0][year][age][0],
										nPopByAge[thisScen][year][age][0],
										thisScen, year, age, 0)
								+ nWomen
								* applySuccesrateToMean(
										this.meanRiskByAge[0][year][age][1],
										this.meanRiskByAge[thisScen][year][age][1],
										nPopByAge[0][year][age][1],
										nPopByAge[thisScen][year][age][1],
										thisScen, year, age, 1);
						if (dataPresent)
							indat = indat / (nMen * nWomen);

						indatR = this.meanRiskByAge[0][year][age][0]
								* nPopByAge[0][year][age][0]
								+ this.meanRiskByAge[0][year][age][1]
								* nPopByAge[0][year][age][1];
						if (nPopByAge[0][year][age][0]
								+ nPopByAge[0][year][age][1] == 0
								&& differencePlot)
							dataPresent = false;
						if (dataPresent)
							indatR = indatR
									/ (nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]);

					}

					if (!differencePlot && dataPresent)
						scenSeries[thisScen].add((double) age, indat);
					if (differencePlot && dataPresent)
						scenSeries[thisScen]
								.add((double) age, (indat - indatR));

				}
				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			}
			String label = "" + (this.startYear + year);
			if (gender == 0)
				label = "men; " + (this.startYear + year);
			if (gender == 1)
				label = "women; " + (this.startYear + year);

			String chartTitle = "mean value of riskFactor";
			if (differencePlot)
				chartTitle = "difference with ref scenario of mean risk factor value";

			chartTitle = chartTitle + label;
			String yTitle = "mean value";
			if (differencePlot)
				yTitle = "difference in mean value";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			if (this.maxAgeInSimulation == this.minAgeInSimulation)
				drawMarkers(chart);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
				renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
				renderer.setSeriesShapesVisible(thisScen, false);
			}
			plot.setRenderer(renderer);

			// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
			// frame1.setVisible(true);
			// frame1.setSize(300, 300);
			/*
			 * final ChartPanel chartPanel = new ChartPanel(chart);
			 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			 * 
			 * try { writeCategoryChart(baseDir + File.separator + "simulations"
			 * + File.separator + simulationName + File.separator + "results" +
			 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
			 * (Exception e) { System.out.println(e.getMessage()); System.out
			 * .println("Problem occurred creating chart. for riskfactor"); }
			 */
			return chart;
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	/**
	 * plot mean value of riskfactor (continuous only) against age for either
	 * men, women or both by scenario women. Age is age during simulation, not
	 * age at the start of simulation
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * 
	 * @param differencePlot
	 *            : plot difference with reference scenario : plot absolute
	 *            numbers
	 * @return JFreeChart plot mean value of riskfactor (continuous only)
	 *         against age for either men, women or both by scenario women. Age
	 *         is age during simulation, not age at the start of simulation
	 */
	public JFreeChart makeYearMeanRiskFactorByScenarioPlot(int gender,
			boolean differencePlot) {
		if (this.riskType == 2) {
			XYDataset xyDataset = null;
			XYSeries[] scenSeries = new XYSeries[this.nScen + 1];
			double[][][][] nPopByAge = getNPopByAge();

			for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {
				scenSeries[thisScen] = new XYSeries(
						this.scenarioNames[thisScen]);
				for (int year = 0; year < this.stepsInRun + 1; year++) {

					double indat = 0;
					double weight = 0;
					double indatR = 0;
					double weightR = 0;
					double mean = 0;
					double meanR = 0;
					double sumweight = 0;
					double sumweightR = 0;

					int nDimLocal = nPopByAge[0][0].length;
					for (int age = 0; age < nDimLocal; age++) {
						if (gender < 2) {
							indat = applySuccesrateToMean(
									this.meanRiskByAge[0][year][age][gender],
									this.meanRiskByAge[thisScen][year][age][gender],
									nPopByAge[0][year][age][gender],
									nPopByAge[thisScen][year][age][gender],
									thisScen, year, age, gender);

							indatR = this.meanRiskByAge[0][year][age][gender];
							weight = applySuccesrate(
									nPopByAge[0][year][age][gender],
									nPopByAge[thisScen][year][age][gender],
									thisScen, year, age, gender);
							weightR = nPopByAge[0][year][age][gender];
							mean += indat * weight;
							meanR += indatR * weightR;
							sumweight += weight;
							sumweightR += weightR;

						} else {
							double nMen = applySuccesrate(
									nPopByAge[0][year][age][0],
									nPopByAge[thisScen][year][age][0],
									thisScen, year, age, 0);
							double nWomen = applySuccesrate(
									nPopByAge[0][year][age][1],
									nPopByAge[thisScen][year][age][1],
									thisScen, year, age, 1);
							indat = nMen
									* applySuccesrateToMean(
											this.meanRiskByAge[0][year][age][0],
											this.meanRiskByAge[thisScen][year][age][0],
											nPopByAge[0][year][age][0],
											nPopByAge[thisScen][year][age][0],
											thisScen, year, age, 0)
									+ nWomen
									* applySuccesrateToMean(
											this.meanRiskByAge[0][year][age][1],
											this.meanRiskByAge[thisScen][year][age][1],
											nPopByAge[0][year][age][1],
											nPopByAge[thisScen][year][age][1],
											thisScen, year, age, 1);
							indatR = this.meanRiskByAge[0][year][age][0]
									* nPopByAge[0][year][age][0]
									+ this.meanRiskByAge[0][year][age][1]
									* nPopByAge[0][year][age][1];
							indatR = indatR
									/ (nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]);
							indat = indat / (nMen * nWomen);
							weight = nMen + nWomen;
							weightR = (nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]);
							mean += indat * weight;
							meanR += indatR * weightR;
							sumweight += weight;
							sumweightR += weightR;

						}
					}
					if (!differencePlot && sumweight > 0)
						scenSeries[thisScen].add((double) year, mean
								/ sumweight);
					if (differencePlot && sumweight > 0 && sumweightR > 0)
						scenSeries[thisScen].add((double) year, mean
								/ sumweight - (meanR / sumweightR));
				}
				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			}
			String label = "";
			if (gender == 0)
				label = "men";
			if (gender == 1)
				label = "women";

			String chartTitle = "mean value of riskFactor";
			if (differencePlot)
				chartTitle = "difference with ref scenario of mean risk factor value";

			chartTitle = chartTitle + label;
			String yTitle = "mean value";
			if (differencePlot)
				yTitle = "difference in mean value";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			if (this.stepsInRun == 0)
				drawMarkers(chart);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
				renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
				renderer.setSeriesShapesVisible(thisScen, false);
			}
			plot.setRenderer(renderer);

			// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
			// frame1.setVisible(true);
			// frame1.setSize(300, 300);
			/*
			 * final ChartPanel chartPanel = new ChartPanel(chart);
			 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			 * 
			 * try { writeCategoryChart(baseDir + File.separator + "simulations"
			 * + File.separator + simulationName + File.separator + "results" +
			 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
			 * (Exception e) { System.out.println(e.getMessage()); System.out
			 * .println("Problem occurred creating chart. for riskfactor"); }
			 */
			return chart;
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	public JFreeChart makeEmptyPlot() {
		JFreeChart chart = null;
		XYSeries nullSeries = new XYSeries("not calculated");
		nullSeries.add((double) 0, 0);
		XYDataset xyDataset = new XYSeriesCollection(nullSeries);
		chart = ChartFactory.createXYLineChart("not availlable",
				"years of simulation", "empty", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);

		return chart;
	}

	/**
	 * makes plot of mortality by scenario
	 * 
	 * @param gender
	 * @param differencePlot
	 * @param numbers
	 * @return plot of mortality by scenario
	 */
	public JFreeChart makeYearMortalityPlotByScenario(int gender,
			int riskClass, boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		double[][][][] mortality = makeMortalityArray(true, riskClass);
		/*
		 * get number of persons who died during this year
		 * 
		 * Succesrate etc are already included
		 */
		double[][][][] nPopByAge;
		if (riskClass < 0)
			nPopByAge = getNPopByAge();
		else
			nPopByAge = getNPopByAgeForRiskclass(riskClass);

		XYSeries scenSeries[] = new XYSeries[this.nScen + 1];

		for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(this.scenarioNames[thisScen]);
			/*
			 * mortality is calculated from the difference between the previous
			 * year and the current year therefor there is one less datapoint
			 * for mortality than for most other outcomes
			 */
			for (int steps = 0; steps < this.stepsInRun; steps++) {
				double indat0 = 0;
				double denominator0 = 0;
				double indat1 = 0;
				double denominator1 = 0;
				double indat0r = 0;
				double denominator0r = 0;
				double indat1r = 0;
				double denominator1r = 0;

				for (int age = 0; age < 96 + this.stepsInRun; age++) {
					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */

					if (mortality[0][steps][age][0] >= 0
							&& mortality[thisScen][steps][age][0] >= 0) {

						indat0 += mortality[thisScen][steps][age][0];

						denominator0 += applySuccesrate(
								nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age, 0);

						indat0r += mortality[0][steps][age][0];
						denominator0r += nPopByAge[0][steps][age][0];
					}
					if (mortality[0][steps][age][1] >= 0
							&& mortality[thisScen][steps][age][1] >= 0) {

						indat1 += mortality[thisScen][steps][age][1];
						denominator1 += applySuccesrate(
								nPopByAge[0][steps][age][1],
								nPopByAge[thisScen][steps][age][1], thisScen,
								steps, age, 1);
						indat1r += mortality[0][steps][age][1];
						denominator1r += nPopByAge[0][steps][age][1];
					}

				}

				if (gender == 0 && denominator0 != 0 && !numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat0 / denominator0);
				if (gender == 1 && denominator1 != 0 && !numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat1 / denominator1);
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& !numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							(indat0 + indat1) / (denominator0 + denominator1));
				if (gender == 0 && denominator0 != 0 && numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat0);
				if (gender == 1 && denominator1 != 0 && numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							indat1);
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps + this.startYear,
							(indat0 + indat1));

				/*
				 * repeat for difference plots
				 */

				/* first get rid of the numerical error in case no differences */

				if (Math.abs(indat0 - indat0r) < 0.1
						&& Math.abs(denominator0 - denominator0r) < 0.1) {
					indat0r = indat0;
					denominator0r = denominator0;
				}
				if (Math.abs(indat1 - indat1r) < 0.1
						&& Math.abs(denominator1 - denominator1r) < 0.1) {
					indat1r = indat1;
					denominator1r = denominator1;
				}

				if (gender == 0 && denominator0 != 0 && denominator0r != 0
						&& !numbers && differencePlot)
					scenSeries[thisScen]
							.add((double) steps, (indat0 / denominator0)
									- (indat0r / denominator0r));
				if (gender == 1 && denominator1 != 0 && denominator1r != 0
						&& !numbers && differencePlot)
					scenSeries[thisScen]
							.add((double) steps, (indat1 / denominator1)
									- (indat1r / denominator1r));
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& (denominator0r + denominator1r) != 0 && !numbers
						&& differencePlot)
					scenSeries[thisScen]
							.add(
									(double) steps,
									((indat0 + indat1) / (denominator0 + denominator1))
											- ((indat0r + indat1r) / (denominator0r + denominator1r)));
				if (gender == 0 && denominator0 != 0 && denominator0r != 0
						&& numbers && differencePlot)
					scenSeries[thisScen].add((double) steps, indat0 - indat0r);
				if (gender == 1 && denominator1 != 0 && denominator1r != 0
						&& numbers && differencePlot)
					scenSeries[thisScen].add((double) steps, indat1 - indat1r);
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& (denominator0r + denominator1r) != 0 && numbers
						&& differencePlot)
					scenSeries[thisScen].add((double) steps, indat0 + indat1
							- indat0r - indat1r);

			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		} // end scenario loop
		String label;
		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		String chartTitle = "mortality ";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of death"
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess mortality rate" + " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of deaths ";
		String yTitle = "mortality rate";
		if (differencePlot && !numbers)
			yTitle = "excess mortality rate";
		if (!differencePlot && numbers)
			yTitle = "number of deaths";
		if (differencePlot && numbers)
			yTitle = "excess number of deaths";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle + label,
				"year", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
				true, false);
		if (this.stepsInRun == 1)
			drawMarkers(chart);

		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
			renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
			renderer.setSeriesShapesVisible(thisScen, false);
		}
		plot.setRenderer(renderer);

		return chart;

	}

	private void drawMarkers(JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
	}

	/**
	 * makes plot of mortality by scenario with age on the x-axis
	 * 
	 * @param year
	 * @param gender
	 * @param differencePlot
	 * @param numbers
	 * @return JFreeChart plot of mortality by scenario with age on the x-axis
	 */
	public JFreeChart makeAgeMortalityPlotByScenario(int year, int gender,
			int riskClass, boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;

		double[][][][] mortality = makeMortalityArray(true, riskClass);/*
																		 * get
																		 * number
																		 * of
																		 * persons
																		 * who
																		 * died
																		 * during
																		 * this
																		 * year
																		 * for
																		 * all
																		 * riskclass
																		 * together
																		 */
		if (mortality != null && year < this.stepsInRun) {
			double[][][][] nPopByAge = getNPopByAge();

			XYSeries scenSeries[] = new XYSeries[this.nScen + 1];

			for (int thisScen = 0; thisScen < this.nScen + 1; thisScen++) {

				scenSeries[thisScen] = new XYSeries(
						this.scenarioNames[thisScen]);
				/*
				 * mortality is calculated from the difference between the
				 * previous year and the current year therefor there is one less
				 * datapoint for mortality than for most other outcomes
				 */

				for (int age = 0; age < 96 + this.stepsInRun; age++) {

					double indat0 = 0;
					double denominator0 = 0;
					double indat1 = 0;
					double denominator1 = 0;
					double indat0r = 0;
					double denominator0r = 0;
					double indat1r = 0;
					double denominator1r = 0;
					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */
					if (mortality[0][year][age][0] >= 0
							&& mortality[thisScen][year][age][0] >= 0) {

						indat0 = mortality[thisScen][year][age][0];
						denominator0 = applySuccesrate(
								nPopByAge[0][year][age][0],
								nPopByAge[thisScen][year][age][0], thisScen,
								year, age, 0);

						indat0r = mortality[0][year][age][0];
						denominator0r = nPopByAge[0][year][age][0];
					}
					if (mortality[0][year][age][1] >= 0
							&& mortality[thisScen][year][age][1] >= 0)

					{
						indat1 = mortality[thisScen][year][age][1];
						denominator1 = applySuccesrate(
								nPopByAge[0][year][age][1],
								nPopByAge[thisScen][year][age][1], thisScen,
								year, age, 1);
						indat1r = mortality[0][year][age][1];

						denominator1r = nPopByAge[0][year][age][1];

					}

					if (gender == 0 && denominator0 != 0 && !numbers
							&& !differencePlot)
						scenSeries[thisScen].add((double) age, indat0
								/ denominator0);
					if (gender == 1 && denominator1 != 0 && !numbers
							&& !differencePlot)
						scenSeries[thisScen].add((double) age, indat1
								/ denominator1);
					if (gender == 2 && (denominator0 + denominator1) != 0
							&& !numbers && !differencePlot)
						scenSeries[thisScen].add((double) age,
								(indat0 + indat1)
										/ (denominator0 + denominator1));
					if (gender == 0 && denominator0 != 0 && numbers
							&& !differencePlot)
						scenSeries[thisScen].add((double) age, indat0);
					if (gender == 1 && denominator1 != 0 && numbers
							&& !differencePlot)
						scenSeries[thisScen].add((double) age, indat1);
					if (gender == 2 && (denominator0 + denominator1) != 0
							&& numbers && !differencePlot)
						scenSeries[thisScen].add((double) age,
								(indat0 + indat1));

					/*
					 * repeat for difference plots
					 */
					/*
					 * first get rid of the numerical error in case no
					 * differences
					 */

					if (Math.abs(indat0 - indat0r) < 0.1
							&& Math.abs(denominator0 - denominator0r) < 0.1) {
						indat0r = indat0;
						denominator0r = denominator0;
					}
					if (Math.abs(indat1 - indat1r) < 0.1
							&& Math.abs(denominator1 - denominator1r) < 0.1) {
						indat1r = indat1;
						denominator1r = denominator1;
					}
					if (gender == 0 && denominator0 != 0 && denominator0r != 0
							&& !numbers && differencePlot)
						scenSeries[thisScen].add((double) age,
								(indat0 / denominator0)
										- (indat0r / denominator0r));
					if (gender == 1 && denominator1 != 0 && denominator1r != 0
							&& !numbers && differencePlot)
						scenSeries[thisScen].add((double) age,
								(indat1 / denominator1)
										- (indat1r / denominator1r));
					if (gender == 2 && (denominator0 + denominator1) != 0
							&& (denominator0r + denominator1r) != 0 && !numbers
							&& differencePlot)
						scenSeries[thisScen]
								.add(
										(double) age,
										((indat0 + indat1) / (denominator0 + denominator1))
												- ((indat0r + indat1r) / (denominator0r + denominator1r)));
					if (gender == 0 && denominator0 != 0 && denominator0r != 0
							&& numbers && differencePlot)
						scenSeries[thisScen]
								.add((double) age, indat0 - indat0r);
					if (gender == 1 && denominator1 != 0 && denominator1r != 0
							&& numbers && differencePlot)
						scenSeries[thisScen]
								.add((double) age, indat1 - indat1r);
					if (gender == 2 && (denominator0 + denominator1) != 0
							&& (denominator0r + denominator1r) != 0 && numbers
							&& differencePlot)
						scenSeries[thisScen].add((double) age, indat0 + indat1
								- indat0r - indat1r);

				}

				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			} // end scenario loop
			String label;
			if (gender == 0)
				label = "men; " + (this.startYear + year);
			else if (gender == 1)
				label = "women; " + (this.startYear + year);
			else
				label = "" + (this.startYear + year);

			String chartTitle = "mortality";
			if (numbers && differencePlot)
				chartTitle = "excess numbers of death"
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "excess mortality rate"
						+ " compared to ref scenario";
			if (numbers && !differencePlot)
				chartTitle = "number of deaths ";
			String yTitle = "mortality rate";
			if (differencePlot && !numbers)
				yTitle = "excess mortality rate";
			if (!differencePlot && numbers)
				yTitle = "number of deaths";
			if (differencePlot && numbers)
				yTitle = "excess number of deaths";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.nScen; thisScen++) {
				renderer.setSeriesStroke(thisScen, new BasicStroke(2.0f));
				renderer.setSeriesShapesVisible(thisScen, false);
			}
			plot.setRenderer(renderer);
			return chart;
		} else
			return makeEmptyPlot();
	}

	/**
	 * the method produces a bargraph for cohort life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a longitudinal
	 * life-expectancy. It only gives proper results when the stepsize used in
	 * the simulation is high enough. In Dynamo this is set at age 105 (105-age
	 * when not life expectancy at birth); This means that everyone who survives
	 * until age 105 is expected to die at age 105.
	 * 
	 * @param age
	 *            : age at which the life expectancy is calculated
	 * @return JFreeChart with bargraph for life-expectancy in different
	 *         scenario's for those who have age a at baseline
	 * 
	 */
	public JFreeChart makeCohortLifeExpectancyPlot(int age,
			boolean differencePlot) {

		/*
		 * for (int steps = 0; steps < stepsInRun; steps++) { double indat = 0;
		 * 
		 * for (int age = 0; age < nDim; age++) indat +=
		 * applySuccesrate(pPopByAge[0][steps][age][gender],
		 * pPopByAge[thisScen][steps][age][gender], thisScen, steps, age);
		 * 
		 * series.add((double) steps, indat / 95);
		 */

		// TODO throw exception if stepsInrun < 105-age
		double[][] lifeExp = new double[this.nScen + 1][2];
		double baselinePop = 0;
		double[][][][] nPopByAge = getNPopByOriAge();
		int yearsLeft = this.nDim - age;
		/*
		 * age of the simulated population is the exact age , so everyone is
		 * assumed to have her/his birthday For life-expectancy we have to
		 * integrate over all ages, also those in between. This is done using
		 * simple Euler integration, thus number in population between age 0 and
		 * 1 is taken as the average of the numbers in simulation at age 0 and
		 * 1.. For age 1, this is the average between 1 and 2. So all ages are
		 * added twice with weight 0.5, thus alternatively added once with
		 * weight 1, except the first and the last, who are only added 0.5
		 * times.
		 * 
		 * Here we neglect what happens at the highest age, as this is already
		 * assuming that almost everyone will have died at those ages. If not,
		 * we now implicitly assume that everyone at the last birthday in the
		 * simulation has a life-expectancy left of 0.5 years
		 */

		for (int scenario = 0; scenario < this.nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				baselinePop = 0;
				double ageWeight = 0.5;
				for (int steps = 0; steps < yearsLeft; steps++) {
					lifeExp[scenario][s] += ageWeight
							* applySuccesrate(nPopByAge[0][steps][age][s],
									nPopByAge[scenario][steps][age][s],
									scenario, 0, age, s);
					if (steps == 0) {
						baselinePop += applySuccesrate(
								nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age, s);
						ageWeight = 1;
					}
				}

				if (baselinePop != 0)
					lifeExp[scenario][s] = lifeExp[scenario][s] / baselinePop;
				else
					lifeExp[scenario][s] = 0;

			}
		String[] gender = { "men", "women" };
		String[] legend = this.scenarioNames;

		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				legend, gender, lifeExp);
		String chartTitle = ("Cohort life expectancy");
		if (age > 0)
			chartTitle = chartTitle + " at age " + age;
		else
			chartTitle = chartTitle + (" at birth");
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, "", "years",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));

		// ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart);
		Plot plot = chart.getPlot();

		/* assign a generator to a CategoryItemRenderer, */
		CategoryItemRenderer renderer = ((CategoryPlot) plot).getRenderer();
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f));
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
		// renderer.setSeriesPaint(0, Color.gray);
		// renderer.setSeriesPaint(1, Color.orange);
		BarRenderer renderer1 = (BarRenderer) ((CategoryPlot) plot)
				.getRenderer();
		renderer1.setDrawBarOutline(true);

		for (int scen = 0; scen < this.nScen + 1; scen++)
			/* RGB with increasing number of red */
			renderer1.setSeriesPaint(scen, new Color(178, 100, scen * 255
					/ (this.nScen + 1)));

		// frame1.setVisible(true);
		// frame1.setSize(300, 300);
		/*
		 * try {
		 * 
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "chartLifeExpectancy.jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for lifeExpectancy");
		 * throw newDynamoOutputException(
		 * "Problem occurred creating chart. for lifeExpectancy with" +
		 * " message: "+e.getMessage()); }
		 */
		return chart;
	}

	/**
	 * the method produces a bargraph for calendar year cross-sectional
	 * life-expectancy in different scenario's for those who have ageOfLE a at
	 * baseline. This is a Sullivan life-expectancy. It only gives proper
	 * results when all ages up to age 95 are in the simulation. Dynamo does not
	 * check for this.
	 * 
	 * @param year
	 *            : year for which the life expectancy is calculated (0= first
	 *            year of simulation)
	 * @param ageOfLE
	 *            : age at which the life expectancy is calculated
	 * 
	 * @return a jfreechart with a bargraph for life-expectancy in different
	 *         scenario's in year year for those who have ageOfLE a at baseline
	 * 
	 */
	public JFreeChart makeYearLifeExpectancyPlot(int year, int ageOfLE,
			boolean differencePlot) {

		if (this.stepsInRun > 0) {
			double[][] lifeExp = new double[this.nScen + 1][2];
			String chartTitle = ("Cross-sectional life expectancy");
			if (ageOfLE > 95)
				chartTitle = (" no simulated persons of age 95 and younger in year " + (this.startYear + year));
			else {

				double[][][][] mortality = makeMortalityArray(false, -1);
				/* check the range of age values availlable */

				/*
				 * the possible maximum of maxAge is nDim-1 , as for the maximum
				 * age nDim it is not possible to calculate the mortality as
				 * there are no data stored of higher ages that make it possible
				 * to calculate the mortality from
				 */
				int[] maxAgeInSimulation = { -1, -1 };
				int maxAge = Math.min(96 + year, nDim - 1);
				for (int sex = 0; sex < 2; sex++) {
					for (int age = ageOfLE; age < maxAge; age++) {
						if (mortality[0][year][age][sex] < 0)
							break;
						maxAgeInSimulation[sex] = age;
					}
					if (maxAgeInSimulation[sex] < 0)
						maxAgeInSimulation[sex] = maxAge;
				}

				/*
				 * complete possibilities although hopefully only the first is
				 * possible
				 */
				if (maxAgeInSimulation[0] < 95 || maxAgeInSimulation[1] < 95) {
					if (maxAgeInSimulation[0] == maxAgeInSimulation[1])
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE + " and age " + maxAgeInSimulation[0]);
					else if (maxAgeInSimulation[0] < 95
							&& maxAgeInSimulation[1] >= 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and total life expectancy at age "
								+ ageOfLE + " (women)");
					else if (maxAgeInSimulation[0] >= 95
							&& maxAgeInSimulation[1] < 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[1]
								+ " (women) and total life expectancye at age "
								+ ageOfLE + " (men)");
					else
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and between age "
								+ ageOfLE
								+ " and age " + maxAgeInSimulation[1] + " (women)");

				}
				// TODO test if partial life-expectancy works , both in health
				// expectancy and simple life expectancy
				double[][][] nPopByAge = new double[this.nScen + 1][maxAge + 1][2];

				for (int scenario = 0; scenario < this.nScen + 1; scenario++)
					for (int sex = 0; sex < 2; sex++) {
						nPopByAge[scenario][ageOfLE][sex] = 1;
						lifeExp[scenario][sex] = 0;
						for (int age = ageOfLE + 1; age <= maxAgeInSimulation[sex]; age++) {

							nPopByAge[scenario][age][sex] = nPopByAge[scenario][age - 1][sex]
									* (1 - mortality[scenario][year][age - 1][sex]);
							lifeExp[scenario][sex] += 0.5 * (nPopByAge[scenario][age - 1][sex] + nPopByAge[scenario][age][sex]);
						}
						/*
						 * add the life-expectancy of the non-simulated ages
						 * above age 95 using the assumption of constant
						 * mortality
						 */
						if (maxAgeInSimulation[sex] >= 95) {
							double rate = -Math
									.log(1 - mortality[scenario][year][maxAgeInSimulation[sex]][sex]);
							lifeExp[scenario][sex] += nPopByAge[scenario][maxAgeInSimulation[sex]][sex]
									/ rate;
						}
					}
			}

			String[] gender = { "men", "women" };
			String[] legend = this.scenarioNames;

			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
					legend, gender, lifeExp);

			JFreeChart chart = ChartFactory.createBarChart(chartTitle, "",
					"years", dataset, PlotOrientation.VERTICAL, true, true,
					false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));

			// ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart",
			// chart);
			Plot plot = chart.getPlot();
			String label;
			if (ageOfLE > 0)
				label = " at age " + ageOfLE + " in year " + (startYear + year);
			else
				label = " at birth" + " in year " + (startYear + year);
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			/* assign a generator to a CategoryItemRenderer, */
			CategoryItemRenderer renderer = ((CategoryPlot) plot).getRenderer();
			renderer.setBaseOutlinePaint(Color.black);
			renderer.setBaseOutlineStroke(new BasicStroke(1.5f));
			CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
					"{2}", new DecimalFormat("0.00"));
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);
			// renderer.setSeriesPaint(0, Color.gray);
			// renderer.setSeriesPaint(1, Color.orange);
			BarRenderer renderer1 = (BarRenderer) ((CategoryPlot) plot)
					.getRenderer();
			renderer1.setDrawBarOutline(true);

			for (int scen = 0; scen < this.nScen + 1; scen++)
				/* RGB with increasing number of red */
				renderer1.setSeriesPaint(scen, new Color(178, 100, scen * 255
						/ (this.nScen + 1)));

			// frame1.setVisible(true);
			// frame1.setSize(300, 300);
			/*
			 * try {
			 * 
			 * writeCategoryChart(baseDir + File.separator + "simulations" +
			 * File.separator + simulationName + File.separator + "results" +
			 * File.separator + "chartLifeExpectancy.jpg", chart); } catch
			 * (Exception e) { System.out.println(e.getMessage()); System.out
			 * .println("Problem occurred creating chart. for lifeExpectancy");
			 * throw newDynamoOutputException(
			 * "Problem occurred creating chart. for lifeExpectancy with" +
			 * " message: "+e.getMessage()); }
			 */
			return chart;
		} else {
			JFreeChart chart = this.makeEmptyPlot();
			return chart;
		}
	}

	/**
	 * the method produces a bargraph for life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a Sullivan
	 * life-expectancy. It only gives proper results when all ages up to age 95
	 * are in the simulation. Dynamo does not check for this.
	 * 
	 * @param year
	 *            : year for which the life expectancy is calculated (0= first
	 *            year of simulation)
	 * @param ageOfLE
	 *            : age at which the life expectancy is calculated
	 * 
	 * @param disease
	 *            : -2= disability, -1: all diseases
	 * @return a jfreechart with a bargraph for life-expectancy in different
	 *         scenario's in year year for those who have ageOfLE a at baseline
	 * 
	 */

	public JFreeChart makeYearHealthyLifeExpectancyPlot(int year, int ageOfLE,
			int disease, boolean differencePlot) {
		if (this.stepsInRun > 0) {
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			String[] genderLabel = { "men", "women" };
			double[][] lifeExp = new double[(this.nScen + 1)][2];
			double[][] withDiseaseExp = new double[(this.nScen + 1)][2];

			double[][][][] diseased;
			if (disease == -1)
				diseased = getNumberOfDiseasedPersons();
			else if (disease == -2)
				diseased = getNDisabledByAge();
			else
				diseased = getNDiseaseByAge(disease);

			if (!this.withNewborns && (ageOfLE < year))
				ageOfLE = year;

			String chartTitle = ("Cross-sectional life expectancy with and without");
			if (ageOfLE > 95)
				chartTitle = (" no simulated persons of age 95 and younger in year " + (this.startYear + year));
			else {
				double[][][][] mortality = makeMortalityArray(false, -1);
				/* check the range of age values availlable */

				/*
				 * the possible maximum of maxAge is nDim-1 , as for the maximum
				 * age nDim it is not possible to calculate the mortality as
				 * there are no data stored of higher ages that make it possible
				 * to calculate the mortality from
				 */
				int[] maxAgeInSimulation = { -1, -1 };
				int maxAge = Math.min(96 + year, nDim - 1);
				for (int sex = 0; sex < 2; sex++) {
					for (int age = ageOfLE; age < maxAge; age++) {
						if (mortality[0][year][age][sex] < 0)
							break;
						maxAgeInSimulation[sex] = age;
					}
					if (maxAgeInSimulation[sex] < 0)
						maxAgeInSimulation[sex] = maxAge;
				}

				/*
				 * complete possibilities although hopefully only the first is
				 * possible
				 */
				if (maxAgeInSimulation[0] < 95 || maxAgeInSimulation[1] < 95) {
					if (maxAgeInSimulation[0] == maxAgeInSimulation[1])
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE + " and age " + maxAgeInSimulation[0] + " with and without ");
					else if (maxAgeInSimulation[0] < 95
							&& maxAgeInSimulation[1] >= 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and total life expectancy at age "
								+ ageOfLE + " (women)" + " with and without ");
					else if (maxAgeInSimulation[0] >= 95
							&& maxAgeInSimulation[1] < 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[1]
								+ " (women) and total life expectancye at age "
								+ ageOfLE + " (men)" + " with and without ");
					else
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[1]
								+ " (women)" + " with and without ");

				}

				double[][][][] nPopByAge = getNPopByAge();
				double[][][] nInLifeTable = new double[this.nScen + 1][maxAge + 1][2];
				boolean errorFlag = false;
				for (int scenario = 0; scenario < this.nScen + 1; scenario++)
					for (int sex = 0; sex < 2; sex++) {

						double diseasePerc = 0;
						double nPop;
						nInLifeTable[scenario][ageOfLE][sex] = 1;
						lifeExp[scenario][sex] = 0;
						withDiseaseExp[scenario][sex] = 0;
						for (int age = ageOfLE + 1; age <= maxAgeInSimulation[sex]; age++) {

							nInLifeTable[scenario][age][sex] = nInLifeTable[scenario][age - 1][sex]
									* (1 - mortality[scenario][year][age - 1][sex]);
							lifeExp[scenario][sex] += 0.5 * (nInLifeTable[scenario][age - 1][sex] + nInLifeTable[scenario][age][sex]);
							nPop = applySuccesrate(
									nPopByAge[0][year][age][sex],
									nPopByAge[scenario][year][age][sex],
									scenario, year, age, sex);
							diseasePerc = applySuccesrate(
									diseased[0][year][age][sex],
									diseased[scenario][year][age][sex],
									scenario, year, age, sex);

							if (nPop != 0)
								diseasePerc = diseasePerc / nPop;
							else
								errorFlag = true;
							withDiseaseExp[scenario][sex] += 0.5
									* (nInLifeTable[scenario][age - 1][sex] + nInLifeTable[scenario][age][sex])
									* diseasePerc;

						}

						if (maxAgeInSimulation[sex] >= 95) {
							double rate = -Math
									.log(1 - mortality[scenario][year][maxAgeInSimulation[sex]][sex]);
							lifeExp[scenario][sex] += nInLifeTable[scenario][maxAgeInSimulation[sex]][sex]
									/ rate;
							diseasePerc = applySuccesrate(
									diseased[0][year][maxAgeInSimulation[sex]][sex],
									diseased[scenario][year][maxAgeInSimulation[sex]][sex],
									scenario, year, maxAgeInSimulation[sex],
									sex);
							nPop = applySuccesrate(
									nPopByAge[0][year][maxAgeInSimulation[sex]][sex],
									nPopByAge[scenario][year][maxAgeInSimulation[sex]][sex],
									scenario, year, maxAgeInSimulation[sex],
									sex);
							if (nPop != 0)
								diseasePerc = diseasePerc / nPop;
							else
								errorFlag = true;
							withDiseaseExp[scenario][sex] += (nInLifeTable[scenario][maxAgeInSimulation[sex]][sex] / rate)
									* diseasePerc;
						}

						/*
						 * the legend plots the labels of scenario 0, so here we
						 * use a general different label
						 */
						if (errorFlag)
							withDiseaseExp[scenario][sex] = 0;
						if (scenario == 0) {
							dataset.addValue(lifeExp[scenario][sex]
									- withDiseaseExp[scenario][sex], "healthy",
									genderLabel[sex]);
							dataset.addValue(withDiseaseExp[scenario][sex],
									"with disease", genderLabel[sex]);
						} else {
							dataset.addValue(lifeExp[scenario][sex]
									- withDiseaseExp[scenario][sex],
									this.scenarioNames[scenario] + "(healthy)",
									genderLabel[sex]);
							dataset
									.addValue(withDiseaseExp[scenario][sex],
											this.scenarioNames[scenario]
													+ "(withDisease)",
											genderLabel[sex]);
						}
					}
			}

			String[] legend = new String[2];
			legend[0] = "healthy";
			if (disease == -1)
				legend[1] = "with disease";
			else if (disease == -2)
				legend[1] = "with disablity";
			else
				legend[1] = "with " + this.diseaseNames[disease];

			String label;
			if (disease == -1)
				chartTitle = chartTitle + "disease" + " (Sullivan Method)";
			else if (disease == -2)
				chartTitle = chartTitle + "disability" + " (Sullivan Method)";
			else
				chartTitle = chartTitle + this.diseaseNames[disease]
						+ " (Sullivan Method)";

			if (ageOfLE > 0)
				label = (this.startYear + year) + ", at age " + ageOfLE;
			else
				label = "" + (this.startYear + year) + ", at birth";
			JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle,
					"", "years", dataset, PlotOrientation.VERTICAL, true, true,
					false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
			KeyToGroupMap map = new KeyToGroupMap(this.scenarioNames[0]);
			map.mapKeyToGroup("healthy", this.scenarioNames[0]);
			map.mapKeyToGroup("with disease", this.scenarioNames[0]);
			map.mapKeyToGroup("with disability", this.scenarioNames[0]);

			for (int scenario = 1; scenario < this.nScen + 1; scenario++)

				for (int s = 0; s < 2; s++) {
					map.mapKeyToGroup(this.scenarioNames[scenario]
							+ "(healthy)", this.scenarioNames[scenario]);
					map.mapKeyToGroup(this.scenarioNames[scenario]
							+ "(withDisease)", this.scenarioNames[scenario]);
				}
			renderer.setSeriesToGroupMap(map);

			SubCategoryAxis domainAxis = new SubCategoryAxis("");
			domainAxis.setCategoryMargin(0.2); // gap between men and women:
			// does
			// not work
			for (int scenario = 0; scenario < this.nScen + 1; scenario++)
				domainAxis.addSubCategory(this.scenarioNames[scenario]);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setDomainAxis(domainAxis);
			// plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
			renderer.setItemMargin(0.15);

			// dikte van de lijn// between the scenarios;
			int currentSeries = 0;
			for (int scenario = 0; scenario < this.nScen + 1; scenario++)
				for (int s = 0; s < 2; s++) {
					renderer.setSeriesPaint(currentSeries, Color.pink);
					renderer.setSeriesVisibleInLegend(currentSeries, false);
					currentSeries++;
					renderer.setSeriesPaint(currentSeries, Color.pink.darker());
					renderer.setSeriesVisibleInLegend(currentSeries, false);
					currentSeries++;

				}
			renderer.setSeriesVisibleInLegend(0, true);
			renderer.setSeriesVisibleInLegend(1, true);
			renderer.setDrawBarOutline(true);
			renderer.setBaseOutlinePaint(Color.black);
			renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van
			// de
			// lijn

			plot.setRenderer(renderer);
			// plot.setFixedLegendItems(makeLegend(disease));

			CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
					"{2}", new DecimalFormat("0.00"));
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);
			/*
			 * this is necessary as otherwise the tooltips are made invisible
			 * together with the in legend invisibility
			 */
			renderer
					.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

			return chart;
			/*
			 * mortality can only be calculated if there is at least one year of
			 * follow-up (stepsinrun>0) If this is not the case, return an empty
			 * plot
			 */
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	private LegendItemCollection makeLegend(int disease) {

		LegendItemCollection legend = new LegendItemCollection();
		LegendItem item1 = new LegendItem("healthy");

		legend.add(item1);
		LegendItem item2;

		if (disease >= 0)
			item2 = new LegendItem("with " + this.diseaseNames[disease]);
		else
			item2 = new LegendItem("with disease");
		legend.add(item2);

		return legend;
	}

	/**
	 * the method produces a bargraph for life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a longitudinal
	 * life-expectancy. It only gives proper results when the stepsize used in
	 * the simulation is high enough. In Dynamo this is set at age 105 (105-age
	 * when not life expectancy at birth); This means that everyone who survives
	 * until age 105 is expected to die at age 105.
	 * 
	 * @param age
	 *            : age at which the life expectancy is calculated
	 * @param disease
	 * @return a jfreechart with a bargraph for life-expectancy in different
	 *         scenario's for those who have age a at baseline
	 * 
	 * 
	 */
	public JFreeChart makeCohortHealthyLifeExpectancyPlot(int age, int disease) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		String[] genderLabel = { "men", "women" };
		double[][] lifeExp = new double[(this.nScen + 1)][2];
		double[][] withDiseaseExp = new double[(this.nScen + 1)][2];
		double baselinePop = 0;
		double[][][][] nPopByAge = getNPopByOriAge();

		double[][][] diseased;
		if (disease == -1)
			diseased = getNumberOfOriDiseasedPersons(age);
		else if (disease == -2)
			diseased = getNumberOfOriDisabledPersons(age);

		else
			diseased = getNDiseaseByOriAge(age, disease);

		int yearsLeft = this.nDim - age;
		for (int scenario = 0; scenario < this.nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {

				baselinePop = 0;
				/*
				 * age of the simulated population is the exact age , so
				 * everyone is assumed to have her/his birthday For
				 * life-expectancy we have to integrate over all ages, also
				 * those in between. This is done using simple Euler
				 * integration, thus number in population between age 0 and 1 is
				 * taken as the average of the numbers in simulation at age 0
				 * and 1.. For age 1, this is the average between 1 and 2. So
				 * all ages are added twice with weight 0.5, thus alternatively
				 * added once with weight 1, except the first and the last, who
				 * are only added 0.5 times.
				 * 
				 * Here we neglect what happens at the highest age, as this is
				 * already assuming that almost everyone will have died at those
				 * ages. If not, we now implicitly assume that everyone at the
				 * last birthday in the simulation has a life-expectancy left of
				 * 0.5 years
				 */
				double ageWeight = 0.5;
				for (int steps = 0; steps < yearsLeft; steps++) {

					lifeExp[scenario][s] += ageWeight
							* applySuccesrate(nPopByAge[0][steps][age][s],
									nPopByAge[scenario][steps][age][s],
									scenario, 0, age, s);
					withDiseaseExp[scenario][s] += ageWeight
							* applySuccesrate(diseased[0][steps][s],
									diseased[scenario][steps][s], scenario, 0,
									age, s);
					if (steps == 0) {
						baselinePop += applySuccesrate(
								nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age, s);
						ageWeight = 1;
					}

				}

				if (baselinePop != 0) {
					lifeExp[scenario][s] = lifeExp[scenario][s] / baselinePop;
					withDiseaseExp[scenario][s] = withDiseaseExp[scenario][s]
							/ baselinePop;
				} else {
					lifeExp[scenario][s] = 0;
					withDiseaseExp[scenario][s] = 0;
				}
				/*
				 * the legend plots the labels of scenario 0, so here we use a
				 * general different label
				 */
				if (scenario == 0) {
					dataset.addValue(lifeExp[scenario][s]
							- withDiseaseExp[scenario][s], "healthy",
							genderLabel[s]);
					dataset.addValue(withDiseaseExp[scenario][s],
							"with disease", genderLabel[s]);
				} else {
					dataset.addValue(lifeExp[scenario][s]
							- withDiseaseExp[scenario][s],
							this.scenarioNames[scenario] + "(healthy)",
							genderLabel[s]);
					if (disease < -1)
						dataset
								.addValue(withDiseaseExp[scenario][s],
										this.scenarioNames[scenario]
												+ "(with disease)",
										genderLabel[s]);
					else
						dataset.addValue(withDiseaseExp[scenario][s],
								this.scenarioNames[scenario]
										+ "(with disability)", genderLabel[s]);
				}
			}

		String[] legend = new String[2];
		legend[0] = "healthy";
		if (disease == -1)
			legend[1] = "with disease";
		else if (disease == -2)
			legend[1] = "with disability";
		else
			legend[1] = "with " + this.diseaseNames[disease];

		String chartTitle = ("Cohort life expectancy with and without ");

		String label;
		if (disease == -2)
			chartTitle = chartTitle + "disability";
		else if (disease == -1)
			chartTitle = chartTitle + "disease";
		else
			chartTitle = chartTitle + this.diseaseNames[disease];

		if (age > 0)
			label = " at age " + age;
		else
			label = " at birth";
		JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle, "",
				"years", dataset, PlotOrientation.VERTICAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		KeyToGroupMap map = new KeyToGroupMap(this.scenarioNames[0]);
		map.mapKeyToGroup("healthy", this.scenarioNames[0]);
		map.mapKeyToGroup("with disease", this.scenarioNames[0]);
		map.mapKeyToGroup("with disability", this.scenarioNames[0]);

		for (int scenario = 1; scenario < this.nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				map.mapKeyToGroup(this.scenarioNames[scenario] + "(healthy)",
						this.scenarioNames[scenario]);
				map.mapKeyToGroup(this.scenarioNames[scenario]
						+ "(with disease)", this.scenarioNames[scenario]);
				map.mapKeyToGroup(this.scenarioNames[scenario]
						+ "(with disability)", this.scenarioNames[scenario]);
			}
		renderer.setSeriesToGroupMap(map);

		SubCategoryAxis domainAxis = new SubCategoryAxis("");
		domainAxis.setCategoryMargin(0.2); // gap between men and women: does
		// not work
		for (int scenario = 0; scenario < this.nScen + 1; scenario++)
			domainAxis.addSubCategory(this.scenarioNames[scenario]);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setDomainAxis(domainAxis);
		// plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
		renderer.setItemMargin(0.15);

		// dikte van de lijn// between the scenarios;
		int currentSeries = 0;
		for (int scenario = 0; scenario < this.nScen + 1; scenario++)
			for (int s = 0; s < 2; s++) {
				renderer.setSeriesPaint(currentSeries, Color.pink);
				renderer.setSeriesVisibleInLegend(currentSeries, false);
				currentSeries++;
				renderer.setSeriesPaint(currentSeries, Color.pink.darker());
				renderer.setSeriesVisibleInLegend(currentSeries, false);
				currentSeries++;

			}
		renderer.setSeriesVisibleInLegend(0, true);
		renderer.setSeriesVisibleInLegend(1, true);
		renderer.setDrawBarOutline(true);
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijn

		plot.setRenderer(renderer);
		// plot.setFixedLegendItems(makeLegend(disease));

		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
		renderer
				.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		return chart;
	}

	/**
	 * method makePyramidChart makes a population pyramid chart for scenario
	 * "thisScen" compared to the reference scenario and year "timestep"
	 * 
	 * @param thisScen
	 *            : number of the scenario
	 * @param timestep
	 * @return a population pyramid chart for scenario "thisScen" compared to
	 *         the reference scenario and year "timestep"
	 */
	public JFreeChart makePyramidChart(int thisScen, int timestep,
			boolean differencePlot) {

		double[][] pyramidDataMales = null;
		double[][] pyramidDataFemales = null;
		String[] typeKey = null;
		if (differencePlot) {
			pyramidDataMales = new double[2][105];
			pyramidDataFemales = new double[2][105];
			typeKey = new String[2];
		} else {
			pyramidDataMales = new double[1][105];
			pyramidDataFemales = new double[1][105];
			typeKey = new String[1];
		}

		double[][] nPopByAge = new double[105][2];
		double[][] nRefPopByAge = null;
		if (differencePlot)
			nRefPopByAge = new double[105][2];
		if (this.scaleRange == 0) {
			/* do only once */
			int maxPopulationSize = getMaxPop();
			this.scaleRange = 10000 * Math
					.ceil(maxPopulationSize * 1.1 / 10000);
		}

		String[] ageKey = new String[105];

		// if (Math.floor(a/5)==a)
		int maxAge = Math.min(96 + this.stepsInRun, 105);
		// else ageKey[104-a]="";
		for (int a = 0; a < 105; a++) {
			ageKey[104 - a] = ((Integer) a).toString();
			if (a < maxAge) {
				for (int r = 0; r < this.nRiskFactorClasses; r++) {
					nPopByAge[a][0] += applySuccesrate(
							this.nPopByRiskClassByAge[0][timestep][r][a][0],
							this.nPopByRiskClassByAge[thisScen][timestep][r][a][0],
							thisScen, timestep, a, 0);
					nPopByAge[a][1] += applySuccesrate(
							this.nPopByRiskClassByAge[0][timestep][r][a][1],
							this.nPopByRiskClassByAge[thisScen][timestep][r][a][1],
							thisScen, timestep, a, 1);
					if (differencePlot) {
						nRefPopByAge[a][0] += this.nPopByRiskClassByAge[0][timestep][r][a][0];
						nRefPopByAge[a][1] += this.nPopByRiskClassByAge[0][timestep][r][a][1];
					}

				}

				/*
				 * as it is printed upside down we change the order in the
				 * dataset by putting a in 99-a
				 */

				// TODO hoe aanpakken als effect van richting verschilt per
				// leeftijdsgroep
				/*
				 * round as plot gives the numbers and integer persons are
				 * strange to users
				 */

				if (!differencePlot) {
					pyramidDataMales[0][104 - a] = -Math.round(nPopByAge[a][0]);
					pyramidDataFemales[0][104 - a] = Math
							.round(nPopByAge[a][1]);
					typeKey[0] = this.scenarioNames[thisScen];
				}

				else if (nPopByAge[a][0] >= nRefPopByAge[a][0]) {

					pyramidDataMales[0][104 - a] = -Math
							.round(nRefPopByAge[a][0]);
					pyramidDataFemales[0][104 - a] = Math
							.round(nRefPopByAge[a][1]);
					typeKey[0] = "reference scenario";

					pyramidDataMales[1][104 - a] = -Math.round(nPopByAge[a][0]
							- nRefPopByAge[a][0]);
					pyramidDataFemales[1][104 - a] = Math.round(nPopByAge[a][1]
							- nRefPopByAge[a][1]);
					typeKey[1] = this.scenarioNames[thisScen] + "-reference";

				} else {
					pyramidDataMales[0][104 - a] = -Math.round(nPopByAge[a][0]);
					pyramidDataFemales[0][104 - a] = Math
							.round(nPopByAge[a][1]);

					typeKey[0] = this.scenarioNames[thisScen];

					pyramidDataMales[1][104 - a] = -Math.round(-nPopByAge[a][0]
							+ nRefPopByAge[a][0]);
					pyramidDataFemales[1][104 - a] = Math

					.round(-nPopByAge[a][1] + nRefPopByAge[a][1]);
					typeKey[1] = "reference-" + this.scenarioNames[thisScen];

				}
			} else { // no data
				pyramidDataMales[0][104 - a] = 0;
				pyramidDataFemales[0][104 - a] = 0;
				typeKey[0] = "reference scenario";
				if (differencePlot) {
					pyramidDataMales[1][104 - a] = 0;
					pyramidDataFemales[1][104 - a] = 0;
					typeKey[1] = this.scenarioNames[thisScen] + "-reference";
				}
			}
		}

		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataMales);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataFemales);

		/* the last three booleans are for: legend , tooltips ,url */
		JFreeChart chart = ChartFactory.createStackedBarChart(
				"Population pyramid for " + this.scenarioNames[thisScen]
						+ " versus" + " ref scenario", "", "population size",
				dataset1, PlotOrientation.HORIZONTAL, false, true, true);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		String label = "" + (this.startYear + timestep);
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		CategoryPlot plot = chart.getCategoryPlot();

		plot.setDataset(1, dataset2);
		/* the category anchor does not work yet */

		CategoryTextAnnotation annotation1 = new CategoryTextAnnotation(
				"women", "98", this.scaleRange * 0.78);
		CategoryTextAnnotation annotation2 = new CategoryTextAnnotation("men",
				"98", -this.scaleRange * 0.78);

		annotation1.setFont(new Font("SansSerif", Font.BOLD, 14));
		annotation2.setFont(new Font("SansSerif", Font.BOLD, 14));
		plot.addAnnotation(annotation1);
		plot.addAnnotation(annotation2);

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setItemMargin(0.0);
		renderer.setDrawBarOutline(true);

		renderer.setItemMargin(0.0);

		renderer.setItemLabelAnchorOffset(9.0);
		renderer.setSeriesPaint(0, Color.white);
		if (differencePlot)
			renderer.setSeriesPaint(1, Color.gray);
		renderer.setDrawBarOutline(true);
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijnen

		CategoryAxis categoryAxis = plot.getDomainAxis();
		categoryAxis.setCategoryMargin(0.0); // ruimte tussen de balken
		categoryAxis.setUpperMargin(0.02); // ruimte boven bovenste balk
		categoryAxis.setLowerMargin(0.02);// ruimte onder onderste balk

		/*
		 * only show ages every 5 years make color white for years in between
		 */

		Paint background = chart.getBackgroundPaint();
		for (int i = 0; i < 105; i++)
			if (5 * Math.floor(i / 5) != i) {
				categoryAxis.setTickLabelPaint(((Integer) i).toString(),
						background);
				categoryAxis.setTickLabelFont(((Integer) i).toString(),
						new Font("SansSerif", Font.PLAIN, 2));
			}

		categoryAxis.setTickLabelsVisible(true);
		boolean[] show = new boolean[105];
		for (int a = 0; a < 105; a++)
			if (Math.floor(a) == a)
				show[a] = true;
			else
				show[a] = false;

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(-this.scaleRange, this.scaleRange);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setUpperMargin(0.10);

		plot.setRenderer(1, renderer);
		plot.setRenderer(2, renderer);
		return chart;
	}

	/**
	 * method find the largest populationnumbers
	 * 
	 * @return
	 */
	private int getMaxPop() {
		int maximum = 0;
		for (int a = 0; a < 96; a++)
			for (int s = 0; s < 2; s++)
				if (this.populationSize[a][s] > maximum)
					maximum = Math.round(this.populationSize[a][s]);
		if (this.withNewborns) {
			if (this.mfratio > 1) {
				for (int y = 0; y < this.newborns.length; y++)
					if (this.newborns[y] * this.mfratio / (1 + this.mfratio) > maximum)
						maximum = Math.round(this.newborns[y] * this.mfratio
								/ (1 + this.mfratio));
			} else {
				for (int y = 0; y < this.newborns.length; y++)
					if (this.newborns[y] / (1 + this.mfratio) > maximum)
						maximum = Math.round(this.newborns[y]
								/ (1 + this.mfratio));
			}
		}
		return maximum;
	}

	double scaleRange = 0;

	/**
	 * method makePyramidChart makes a population pyramid chart for scenario
	 * "thisScen" compared to the reference scenario and year "timestep",
	 * indication also the persons in the population with any modelled disease
	 * 
	 * @param thisScen
	 *            : number of the scenario
	 * @param timestep
	 * @param d
	 *            : number of the disease: -2=disability; -1=all diseases; >=0:
	 *            diseaseNumber
	 * @return a population pyramid chart for scenario "thisScen" compared to
	 *         the reference scenario and year "timestep", indication also the
	 *         persons in the population with any modelled disease
	 */
	public JFreeChart makePyramidChartIncludingDisease(int thisScen,
			int timestep, int d, boolean differencePlot) {

		/*
		 * pyramid data 1 are the data for men, pyramiddata2 those for women;
		 */

		double[][] pyramidDataMales = new double[2][105];
		double[][] pyramidDataFemales = new double[2][105];
		double[][] nHealthyByAge = new double[105][2];
		double[][] nRefHealthyByAge = new double[105][2];
		double[][] nDiseaseByAge = new double[105][2];
		double[][] nRefDiseaseByAge = new double[105][2];

		if (differencePlot) {
			pyramidDataMales = new double[6][105];
			pyramidDataFemales = new double[6][105];
		}

		String diseaseName = "disease";
		if (d == -2)
			diseaseName = "disability";
		if (d >= 0)
			diseaseName = this.diseaseNames[d];
		double[][][][] withDisease;

		if (d < 0)
			if (d == -2)
				withDisease = getNDisabledByAge();
			else
				withDisease = getNumberOfDiseasedPersons();
		else
			withDisease = getNDiseaseByAge(d);
		double[][][][] nPopByAge = getNPopByAge();

		/* the arrays with Key give the names of the columns in the plot */

		String[] typeKey = { "with " + diseaseName,
				"ref-scen (" + diseaseName + ")",
				"scen-ref (" + diseaseName + ")", "healthy",
				"ref-scen (total)", "scen-ref (total)" };

		if (!differencePlot) {
			String[] temp1 = { "with " + diseaseName, "healthy" };
			typeKey = temp1;

		}

		String[] ageKey = new String[105];
		int maxAge = Math.min(96 + this.stepsInRun, 105);
		for (int a = 0; a < 105; a++) {
			// if (Math.floor(a/5)==a)
			ageKey[104 - a] = ((Integer) a).toString();
			// else ageKey[104-a]="";
			if (a < maxAge) {
				nDiseaseByAge[a][0] += applySuccesrate(
						withDisease[0][timestep][a][0],
						withDisease[thisScen][timestep][a][0], thisScen,
						timestep, a, 0);
				nHealthyByAge[a][0] += applySuccesrate(
						nPopByAge[0][timestep][a][0]
								- withDisease[0][timestep][a][0],
						nPopByAge[thisScen][timestep][a][0]
								- withDisease[thisScen][timestep][a][0],
						thisScen, timestep, a, 0);
				nDiseaseByAge[a][1] += applySuccesrate(
						withDisease[0][timestep][a][1],
						withDisease[thisScen][timestep][a][1], thisScen,
						timestep, a, 1);
				nHealthyByAge[a][1] += applySuccesrate(
						nPopByAge[0][timestep][a][1]
								- withDisease[0][timestep][a][1],
						nPopByAge[thisScen][timestep][a][1]
								- withDisease[thisScen][timestep][a][1],
						thisScen, timestep, a, 1);

				if (differencePlot) {
					nRefDiseaseByAge[a][0] += withDisease[0][timestep][a][0];
					nRefDiseaseByAge[a][1] += withDisease[0][timestep][a][1];
					nRefHealthyByAge[a][0] += nPopByAge[0][timestep][a][0]
							- withDisease[0][timestep][a][0];
					nRefHealthyByAge[a][1] += nPopByAge[0][timestep][a][1]
							- withDisease[0][timestep][a][1];
				}
				/*
				 * as it is printed upside down we change the order in the
				 * dataset by putting a in 99-a
				 */

				/*
				 * round as plot gives the numbers and integer persons are
				 * strange to users
				 */

				/*
				 * pyramid data males are the data for men, females those for
				 * women; for the difference plot they contain 6 data parts:
				 * <br> 0: minimum of ( # withdisease reference scenario, #
				 * withdisease scenario) <br>1:maximum of (# withdisease
				 * reference scenario-# withdisease scenario, 0) <br>2:maximum
				 * of (# withdisease scenario-# withdisease reference scenario,
				 * 0)<br> 3:minimum of (# total reference scenario , # total
				 * scenario ) - total (0 - 2)<br> 4:maximum of (# total
				 * reference scenario-# total scenario, 0) <br>5:maximum of (#
				 * total scenario-# total reference scenario, 0)
				 */
				if (differencePlot) {
					pyramidDataMales[0][104 - a] = -Math.max(0, Math
							.round(nRefDiseaseByAge[a][0]));
					pyramidDataFemales[0][104 - a] = Math.max(0, Math
							.round(nRefDiseaseByAge[a][1]));
					pyramidDataMales[1][104 - a] = -Math.max(0,
							Math.round(nRefDiseaseByAge[a][0]
									- nDiseaseByAge[a][0]));
					pyramidDataFemales[1][104 - a] = Math.max(0,
							Math.round(nRefDiseaseByAge[a][1]
									- nDiseaseByAge[a][1]));

					pyramidDataMales[2][104 - a] = -Math.max(0, Math
							.round(nDiseaseByAge[a][0])
							- nRefDiseaseByAge[a][0]);
					pyramidDataFemales[2][104 - a] = Math.max(0, Math
							.round(nDiseaseByAge[a][1])
							- nRefDiseaseByAge[a][1]);

					pyramidDataMales[3][104 - a] = -Math.round(Math.min(
							(nRefHealthyByAge[a][0] + nRefDiseaseByAge[a][0]),
							(nHealthyByAge[a][0] + nDiseaseByAge[a][0])))
							- pyramidDataMales[0][104 - a]
							- pyramidDataMales[1][104 - a]
							- pyramidDataMales[2][104 - a];
					pyramidDataFemales[3][104 - a] = Math.round(Math.min(
							(nRefHealthyByAge[a][1] + nRefDiseaseByAge[a][1]),
							(nHealthyByAge[a][1] + nDiseaseByAge[a][1])))
							- pyramidDataFemales[0][104 - a]
							- pyramidDataFemales[1][104 - a]
							- pyramidDataFemales[2][104 - a];

					pyramidDataMales[4][104 - a] = -Math.max(0,
							Math
									.round(nRefHealthyByAge[a][0]
											+ nRefDiseaseByAge[a][0]
											- nHealthyByAge[a][0]
											- nDiseaseByAge[a][0]));
					pyramidDataFemales[4][104 - a] = Math.max(0,
							Math
									.round(nRefHealthyByAge[a][1]
											+ nRefDiseaseByAge[a][1]
											- nHealthyByAge[a][1]
											- nDiseaseByAge[a][1]));

					pyramidDataMales[5][104 - a] = -Math.max(0, Math
							.round(nHealthyByAge[a][0] + nDiseaseByAge[a][0]
									- nRefHealthyByAge[a][0]
									- nRefDiseaseByAge[a][0]));
					pyramidDataFemales[5][104 - a] = Math.max(0, Math
							.round(nHealthyByAge[a][1] + nDiseaseByAge[a][1]
									- nRefHealthyByAge[a][1]
									- nRefDiseaseByAge[a][1]));
				} else { /* if no difference plot */
					pyramidDataMales[0][104 - a] = -Math.max(0, Math
							.round(nDiseaseByAge[a][0]));
					pyramidDataFemales[0][104 - a] = Math.max(0, Math
							.round(nDiseaseByAge[a][1]));
					pyramidDataMales[1][104 - a] = -Math.max(0, Math
							.round(nHealthyByAge[a][0]));
					pyramidDataFemales[1][104 - a] = Math.max(0, Math
							.round(nHealthyByAge[a][1]));

				}

			} else {
				pyramidDataMales[0][104 - a] = 0;
				pyramidDataFemales[0][104 - a] = 0;
				pyramidDataMales[1][104 - a] = 0;
				pyramidDataFemales[1][104 - a] = 0;

				pyramidDataMales[2][104 - a] = 0;
				pyramidDataFemales[2][104 - a] = 0;

				pyramidDataMales[3][104 - a] = 0;
				pyramidDataFemales[3][104 - a] = 0;

				pyramidDataMales[4][104 - a] = 0;
				pyramidDataFemales[4][104 - a] = 0;

				pyramidDataMales[5][104 - a] = 0;
				pyramidDataFemales[5][104 - a] = 0;
			}
		}

		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataMales);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataFemales);
		/* find the maximum value of the current population */
		/* assume that scenarios will not increase this by more than 50% */

		if (this.scaleRange == 0) {
			/* do only once */
			int maxPopulationSize = getMaxPop();
			this.scaleRange = 10000 * Math
					.ceil(maxPopulationSize * 1.1 / 10000);
		}
		String chartTitle = "";
		if (differencePlot)
			chartTitle = "Population pyramid for "
					+ this.scenarioNames[thisScen] + " versus"
					+ " ref scenario";
		else
			chartTitle = "Population pyramid for "
					+ this.scenarioNames[thisScen];

		/* the last three booleans are for: legend ,tooltips ,url */

		JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle,
				"age", "population size", dataset1, PlotOrientation.HORIZONTAL,
				true, true, true);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		String label = "" + (this.startYear + timestep);
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		CategoryPlot plot = chart.getCategoryPlot();

		CategoryAxis catAxis = (CategoryAxis) plot.getDomainAxis();

		// this does not work
		// catAxis.setLabel(null);
		catAxis.setTickLabelsVisible(true);
		boolean[] show = new boolean[105];
		for (int a = 0; a < 105; a++)
			if (Math.floor(a) == a)
				show[a] = true;
			else
				show[a] = false;
		// catAxis.setTickLabelPaint(show);
		plot.setDataset(1, dataset2);

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		/*
		 * make a separate renderer for the female part of the plot, so that the
		 * legend can be made invisible here
		 */
		StackedBarRenderer renderer2 = new StackedBarRenderer();

		renderer2
				.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		plot.setRenderer(1, renderer2);
		renderer.setItemMargin(0.0);
		renderer2.setItemMargin(0.0);
		renderer.setDrawBarOutline(true);
		renderer2.setDrawBarOutline(true);

		// ChartFrame frame = new ChartFrame("LifeExpectancy Chart", chart);

		renderer.setItemLabelAnchorOffset(9.0);
		renderer2.setItemLabelAnchorOffset(9.0);
		// renderer.setSeriesVisibleInLegend(1,false);
		renderer.setSeriesPaint(0, Color.pink);
		if (differencePlot)
			renderer.setSeriesPaint(1, Color.orange);

		else
			renderer.setSeriesPaint(1, Color.white);
		renderer.setSeriesPaint(2, Color.red);
		renderer.setSeriesPaint(3, Color.white);
		renderer.setSeriesPaint(4, Color.black);
		renderer.setSeriesPaint(5, Color.gray);
		renderer2.setSeriesPaint(0, Color.pink);
		if (differencePlot)
			renderer2.setSeriesPaint(1, Color.orange);
		else
			renderer2.setSeriesPaint(1, Color.white);
		renderer2.setSeriesPaint(2, Color.red);
		renderer2.setSeriesPaint(3, Color.white);
		renderer2.setSeriesPaint(4, Color.black);
		renderer2.setSeriesPaint(5, Color.gray);
		renderer.setDrawBarOutline(true);
		renderer2.setDrawBarOutline(true);
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f));
		renderer2.setBaseOutlinePaint(Color.black);
		renderer2.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijnen
		CategoryAxis categoryAxis = plot.getDomainAxis();
		categoryAxis.setCategoryMargin(0.0); // ruimte tussen de balken
		categoryAxis.setUpperMargin(0.02); // ruimte boven bovenste balk
		categoryAxis.setLowerMargin(0.02);// ruimte onder onderste balk
		/*
		 * only show ages every 5 years make color white for years in between
		 */
		Paint background = chart.getBackgroundPaint();
		for (int i = 0; i < 105; i++)
			if (5 * Math.floor(i / 5) != i) {
				categoryAxis.setTickLabelPaint(((Integer) i).toString(),
						background);
				categoryAxis.setTickLabelFont(((Integer) i).toString(),
						new Font("SansSerif", Font.PLAIN, 2));
			}

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(-this.scaleRange, this.scaleRange);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setUpperMargin(0.10);

		plot.setRenderer(0, renderer);
		renderer2.setBaseSeriesVisibleInLegend(false);
		plot.setRenderer(1, renderer2);

		/* the category anchor does not work yet */

		CategoryTextAnnotation annotation1 = new CategoryTextAnnotation(
				"women", "98", this.scaleRange * 0.78);
		CategoryTextAnnotation annotation2 = new CategoryTextAnnotation("men",
				"98", -this.scaleRange * 0.78);
		annotation1.setFont(new Font("SansSerif", Font.BOLD, 14));
		annotation2.setFont(new Font("SansSerif", Font.BOLD, 14));
		plot.addAnnotation(annotation1);
		plot.addAnnotation(annotation2);
		return chart;
	}

	/**
	 * 
	 */
	public void writeDataToDisc(String dataName) {
		final String dataFile = dataName;

		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(dataFile)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int scen = 0; scen < this.nScen + 1; scen++)

			for (int r = 0; r < this.nRiskFactorClasses; r++)
				for (int age = 0; age < 96; age++)
					for (int s = 0; s < 2; s++) {
						int yearsleft = this.nDim - age;

						for (int steps = 0; steps < yearsleft; steps++) {
							try {
								out
										.writeDouble(this.nPopByOriRiskClassByOriAge[scen][steps][r][age][s]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									out
											.writeDouble(this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][d][r][age][s]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							;
						}
					}

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int steps = 0; steps < this.stepsInRun + 1; steps++)
				for (int r = 0; r < this.nRiskFactorClasses; r++)
					for (int age = 0; age < 96 + this.stepsInRun; age++)
						for (int s = 0; s < 2; s++) {
							try {
								out
										.writeDouble(this.nPopByRiskClassByAge[scen][steps][r][age][s]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									out
											.writeDouble(this.nDiseaseStateByRiskClassByAge[scen][steps][d][r][age][s]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}

		// inlezen: price = in.readDouble();catch (EOFException e) {}
		//

		// TODO exception afhandelen + toevoegen wegschrijven van mean van
		// riskfactor

	}

	public void readDataFromDisc(String dataName) {
		final String dataFile = dataName;

		DataInputStream indata = null;
		try {
			indata = new DataInputStream(new BufferedInputStream(
					new FileInputStream(dataFile)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int scen = 0; scen < this.nScen + 1; scen++)

			for (int r = 0; r < this.nRiskFactorClasses; r++)
				for (int age = 0; age < 96; age++)
					for (int s = 0; s < 2; s++) {
						int yearsleft = this.nDim - age;

						for (int steps = 0; steps < yearsleft; steps++) {
							try {
								this.nPopByOriRiskClassByOriAge[scen][steps][r][age][s] = indata
										.readDouble();
							} catch (EOFException e) {
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									this.nDiseaseStateByOriRiskClassByOriAge[scen][steps][d][r][age][s] = indata
											.readDouble();
								} catch (EOFException e) {
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							;
						}
					}

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int steps = 0; steps < this.stepsInRun + 1; steps++)
				for (int r = 0; r < this.nRiskFactorClasses; r++)
					for (int age = 0; age < 96 + this.stepsInRun; age++)
						for (int s = 0; s < 2; s++) {
							try {
								this.nPopByRiskClassByAge[scen][steps][r][age][s] = indata
										.readDouble();
							} catch (EOFException e) {
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							for (int d = 0; d < this.nDiseaseStates; d++)
								try {
									this.nDiseaseStateByRiskClassByAge[scen][steps][d][r][age][s] = indata
											.readDouble();
								} catch (EOFException e) {
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}

		//

		// TODO exception afhandelen + toevoegen wegschrijven van mean van
		// riskfactor

	}

	/**
	 * @return
	 */
	public int getRiskType() {
		return this.riskType;
	}

	/**
	 * @param riskType
	 */
	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	/**
	 * @return number of scenarios (reference scenario not included)
	 */
	public int getNScen() {
		return this.nScen;
	}

	/**
	 * @param scen
	 */
	public void setNScen(int scen) {
		this.nScen = scen;
	}

	/**
	 * @return steps in the current run (= years of simulation)
	 */
	public int getStepsInRun() {
		return this.stepsInRun;
	}

	/**
	 * @param stepsInRun
	 */
	public void setStepsInRun(int stepsInRun) {
		this.stepsInRun = stepsInRun;
	}

	/**
	 * @return array with objects (one for each disease cluster) containing the
	 *         information in each cluster
	 */
	public DiseaseClusterStructure[] getStructure() {
		return this.structure;
	}

	/**
	 * @param structure
	 */
	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}

	/**
	 * @param structure2
	 */
	private void setNDiseases(DiseaseClusterStructure[] s) {
		this.nDiseases = 0;
		// if no diseases are present, DiseaseClusterStructure=null
		if (s != null)
			for (int i = 0; i < s.length; i++) {
				this.nDiseases += s[i].getNInCluster();

			}

	}

	/**
	 * @return number of diseases
	 */
	public int getNDiseases() {
		return this.nDiseases;
	}

	/**
	 * @return number of riskfactor classes
	 */
	public int getNRiskFactorClasses() {
		return this.nRiskFactorClasses;
	}

	/**
	 * @param riskFactorClasses
	 */
	public void setNRiskFactorClasses(int riskFactorClasses) {
		this.nRiskFactorClasses = riskFactorClasses;
	}

	/**
	 * @return number of diseasestates
	 */
	public int getNDiseaseStates() {
		return this.nDiseaseStates;
	}

	/**
	 * @param s
	 */
	public void setNDiseaseStates(DiseaseClusterStructure[] s) {
		this.nDiseaseStates = 1;
		// if no diseases are present, s==null
		if (s != null)
			for (int i = 0; i < s.length; i++) {
				if (s[i].getNInCluster() == 1)
					this.nDiseaseStates++;
				else if (s[i].isWithCuredFraction())
					this.nDiseaseStates += 2;
				else
					this.nDiseaseStates += Math.pow(2, s[i].getNInCluster()) - 1;

			}
	}

	/**
	 * @param input
	 */
	public void setNDiseaseStates(int input) {
		this.nDiseaseStates = input;
	}

	/**
	 * @param fileName
	 * @param chart
	 * @throws DynamoOutputException
	 */
	public void writeCategoryChart(String fileName, JFreeChart chart)
			throws DynamoOutputException {
		File outFile = new File(fileName);
		boolean isDirectory = outFile.isDirectory();
		boolean canWrite = outFile.canWrite();
		try {
			boolean isNew = outFile.createNewFile();
			if (!isDirectory && (canWrite || isNew))

				ChartUtilities.saveChartAsJPEG(new File(fileName), chart, 300,
						500);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new DynamoOutputException(e.getMessage());
		}
	}

	/**
	 * @return number of simulated persons by gender
	 */
	public int[] getNInSimulation() {
		return this.nInSimulation;
	}

	/**
	 * @return array [][] number of simulated persons by age and gender
	 */
	public int[][] getNInSimulationByAge() {
		return this.nInSimulationByAge;
	}

	/**
	 * @param inSimulationByAge
	 */
	public void setNInSimulationByAge(int[][] inSimulationByAge) {
		this.nInSimulationByAge = inSimulationByAge;
	}

	/**
	 * @return number of persons in the disease states by risk class and age
	 */
	public double[][][][][][] getNDiseaseStateByRiskClassByAge() {
		return this.nDiseaseStateByRiskClassByAge;
	}

	/**
	 * @param diseaseStateByRiskClassByAge
	 */
	public void setNDiseaseStateByRiskClassByAge(
			double[][][][][][] diseaseStateByRiskClassByAge) {
		this.nDiseaseStateByRiskClassByAge = diseaseStateByRiskClassByAge;
	}

	/**
	 * @return succesrate of intervention
	 */
	public float[] getSuccesrate() {
		float[] returnvalue = new float[this.succesrate.length];
		for (int i = 0; i < this.succesrate.length; i++)
			returnvalue[i] = this.succesrate[i] * 100;
		return returnvalue;
	}

	/**
	 * @param succesrateIn
	 */
	public void setSuccesrate(float[] succesrateIn) {

		this.succesrate = new float[succesrateIn.length];
		for (int i = 0; i < succesrateIn.length; i++)
			this.succesrate[i] = succesrateIn[i] / 100;

	}

	/**
	 * @param succesrate
	 * @param i
	 */
	public void setSuccesrate(float succesrate, int i) {
		this.succesrate[i] = succesrate / 100;

	}

	/**
	 * @return minimum age of intervention
	 */
	public int[] getMinAge() {
		return this.minAge;
	}

	/**
	 * @param minAge
	 */
	public void setMinAge(int[] minAge) {
		this.minAge = minAge;
	}

	/**
	 * @param minAge
	 * @param i
	 */
	public void setMinAge(int minAge, int i) {
		this.minAge[i] = minAge;
	}

	/**
	 * @return maximum age of intervention
	 */
	public int[] getMaxAge() {
		return this.maxAge;
	}

	/**
	 * @param maxAge
	 */
	public void setMaxAge(int[] maxAge) {
		this.maxAge = maxAge;
	}

	/**
	 * @param maxAge
	 * @param i
	 */
	public void setMaxAge(int maxAge, int i) {
		this.maxAge[i] = maxAge;
	}

	/**
	 * @return number of newborns in the simulation by age (=year when added)
	 *         and gender
	 */
	public int[][] getNNewBornsInSimulationByAge() {
		return this.nNewBornsInSimulationByAge;
	}

	/**
	 * @return
	 */
	public int[][][] getNInSimulationByRiskClassByAge() {
		return nInSimulationByRiskClassByAge;
	}

	/**
	 * @return number of persons with the disease by riskclass
	 */
	public double[][][][][] getNDiseaseByRiskClass() {
		double[][][][][] nDiseaseByRiskClass = new double[nScen + 1][stepsInRun + 1][nDiseases][this.nRiskFactorClasses][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)

								nDiseaseByRiskClass[scen][stepCount][d][r][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByRiskClass;
	}

	/**
	 * @return number of persons with the disease
	 */
	public double[][][][] getNDisease() {
		double[][][][] nDisease = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseases][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
								nDisease[scen][stepCount][d][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDisease;
	}

	/**
	 * @return number of persons with the disease by age
	 */
	public double[][][][][] getNDiseaseByAge() {
		double[][][][][] nDiseaseByAge = new double[this.nScen + 1][this.stepsInRun + 1][this.nDiseases][96 + this.stepsInRun][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.nDiseases; d++)
							for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
								nDiseaseByAge[scen][stepCount][d][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * gets number of persons with disease
	 * 
	 * @param disease
	 *            : diseasenumber
	 * @return array [][][][] with number of persons with this disease (index:
	 *         scenario, year of follow-up (simulation), age, sex)
	 */
	public double[][][][] getNDiseaseByAge(int disease) {
		double[][][][] nDiseaseByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
							nDiseaseByAge[scen][stepCount][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][disease][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * gets number of persons with disease
	 * 
	 * @param disease
	 *            : diseasenumber
	 * @return array [][][] with number of persons with this disease (index:
	 *         scenario, age,sex)
	 */
	public double[][][] getNDiseaseByAge(int disease, int stepCount) {
		double[][][] nDiseaseByAge = new double[this.nScen + 1][96 + this.stepsInRun][2];
		;

		double[][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, stepCount, disease);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nDiseaseByAge[scen][a][g] += nDiseaseByRiskClassByAge[scen][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * 
	 * @return array [][][][] with numbers of disabled persons by scenario,
	 *         years in follow-uo , age and sex
	 */
	public double[][][][] getNDisabledByAge() {

		double[][][][] nDisabledByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
							if (pSurvivalByRiskClassByAge[scen][stepCount][r][a][g] != 0)
								nDisabledByAge[scen][stepCount][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
										* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g]
										/ pSurvivalByRiskClassByAge[scen][stepCount][r][a][g];
							else
								nDisabledByAge[scen][stepCount][a][g] += 0;

		return nDisabledByAge;
	}

	/**
	 * @ param stepCount Year for which to get the
	 * 
	 * @return array [][][] with numbers of disabled persons by scenario, , age
	 *         and sex
	 */

	public double[][][] getNDisabledByAge(int stepCount) {

		double[][][] nDisabledByAge = new double[this.nScen + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						if (pSurvivalByRiskClassByAge[scen][stepCount][r][a][g] != 0)
							nDisabledByAge[scen][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g]
									/ pSurvivalByRiskClassByAge[scen][stepCount][r][a][g];
						else
							nDisabledByAge[scen][a][g] += 0;

		return nDisabledByAge;
	}

	/**
	 * 
	 * @return array [][][][][] with numbers of disabled persons by scenario,
	 *         years in follow-uo ,riskclass, age and sex
	 */
	public double[][][][][] getNDisabledByRiskClassByAge() {

		double[][][][][] nDisabledByAge = new double[this.nScen + 1][this.stepsInRun + 1][nRiskFactorClasses][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
							if (pSurvivalByRiskClassByAge[scen][stepCount][r][a][g] != 0)
								nDisabledByAge[scen][stepCount][r][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
										* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g]
										/ pSurvivalByRiskClassByAge[scen][stepCount][r][a][g];
							else
								nDisabledByAge[scen][stepCount][r][a][g] += 0;

		return nDisabledByAge;
	}

	/**
	 * 
	 * @return array [][][][] with numbers of disabled persons by scenario,
	 *         ,riskclass, age and sex , selected for year=stepCount
	 */
	public double[][][][] getNDisabledByRiskClassByAge(int stepCount) {

		double[][][][] nDisabledByAge = new double[this.nScen + 1][nRiskFactorClasses][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						if (pSurvivalByRiskClassByAge[scen][stepCount][r][a][g] != 0)
							nDisabledByAge[scen][r][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									* pDisabilityByRiskClassByAge[scen][stepCount][r][a][g]
									/ pSurvivalByRiskClassByAge[scen][stepCount][r][a][g];
						else
							nDisabledByAge[scen][r][a][g] += 0;

		return nDisabledByAge;
	}

	/**
	 * @param scen
	 * @param stepCount
	 * @param g
	 * @return nm
	 */
	public double[][] getNDiseaseByAge(int scen, int stepCount, int g) {
		double[][] nDiseaseByAge = new double[this.nDiseases][96 + this.stepsInRun];
		;
		double[][][] nDiseaseByRiskClassByAge = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, scen, stepCount, g);
		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int a = 0; a < 96 + this.stepsInRun; a++)
				for (int d = 0; d < this.nDiseases; d++)
					nDiseaseByAge[d][a] += nDiseaseByRiskClassByAge[d][r][a];
		return nDiseaseByAge;
	}

	/**
	 * @return
	 */
	public double[][][][][] getNDiseaseByOriAge() {
		double[][][][][] nDiseaseByAge = new double[nScen + 1][nDim][nDiseases][96][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByOriRiskClassByOriAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < nDim; stepCount++)
								nDiseaseByAge[scen][stepCount][d][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByAge;
	}

	/**
	 * @param age
	 * @param d
	 * @return
	 */
	public double[][][] getNDiseaseByOriAge(int age, int d) {
		double[][][] nDiseaseByAge = new double[this.nScen + 1][this.nDim - age][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(this.nDiseaseStateByOriRiskClassByOriAge);
		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)

				for (int g = 0; g < 2; g++)

					for (int stepCount = 0; stepCount < this.nDim - age; stepCount++)
						nDiseaseByAge[scen][stepCount][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][age][g];
		return nDiseaseByAge;
	}

	/**
	 * get the array of diseaseNumbers for disease d.
	 * 
	 * @param d
	 *            : diseasenumber of disease to return
	 * @return: array with diseaseNumbers. indexes scenario, year of follow-up,
	 *          risk factorclass, age, sex
	 */
	public double[][][][] getNDiseaseByRiskClassByAge(int steps, int d) {

		double[][][][] returnArray = makeDiseaseArray(
				this.nDiseaseStateByRiskClassByAge, steps, d);

		return returnArray;
	}

	/**
	 * get the array of diseaseNumbers by age at start of simulation and
	 * riskclass at start of simulation for disease d.
	 * 
	 * @param d
	 *            : diseasenumber of disease to return
	 * @return: array with diseaseNumbers. indexes scenario, year of follow-up,
	 *          risk factorclass, age, sex
	 */
	public double[][][][][] getNDiseaseByOriRiskClassByOriAge(int d) {
		double[][][][][][] nDiseaseByOriRiskClassByOriAge = makeDiseaseArray(this.nDiseaseStateByOriRiskClassByOriAge);
		double[][][][][] returnArray = new double[this.nScen + 1][this.nDim][this.nRiskFactorClasses][96][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int g = 0; g < 2; g++)
					for (int a = 0; a < 96; a++)
						for (int stepCount = 0; stepCount < this.nDim - a; stepCount++)
							returnArray[scen][stepCount][r][a][g] = nDiseaseByOriRiskClassByOriAge[scen][stepCount][d][r][a][g];

		return returnArray;
	}

	/**
	 * @return
	 */
	public double[][][][][] getMeanRiskByRiskClassByAge() {
		return this.meanRiskByRiskClassByAge;
	}

	/**
	 * @return number in population by riskclass and age
	 */
	public double[][][][][] getNPopByRiskClassByAge() {
		return this.nPopByRiskClassByAge;
	}

	/**
	 * @return number in population by age
	 */
	public double[][][][] getNPopByAge() {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < this.stepsInRun + 1; stepCount++)
							nPopByAge[scen][stepCount][a][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	/**
	 * @return number in population by age in year stepCount
	 */
	public double[][][] getNPopByAge(int stepCount) {

		double[][][] nPopByAge = new double[this.nScen + 1][96 + this.stepsInRun][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nPopByAge[scen][a][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	/**
	 * @return number in population by age in riskFactorClass riskClass
	 */
	public double[][][][] getNPopByAgeForRiskclass(int riskClass) {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.stepsInRun + 1][96 + this.stepsInRun][2];

		for (int stepcount = 0; stepcount < this.stepsInRun + 1; stepcount++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)

						nPopByAge[scen][stepcount][a][g] += this.nPopByRiskClassByAge[scen][stepcount][riskClass][a][g];
		return nPopByAge;

	}

	/**
	 * @param scen
	 * @param stepCount
	 * @param g
	 * @return
	 */
	public double[] getNPopByAge(int scen, int stepCount, int g) {
		double[] nPopByAge = new double[96 + this.stepsInRun];
		for (int r = 0; r < this.nRiskFactorClasses; r++)
			for (int a = 0; a < 96 + this.stepsInRun; a++)
				nPopByAge[a] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;
	}

	/**
	 * @return
	 */
	public double[][][] getNPop() {

		double[][][] nPop = new double[this.nScen + 1][this.stepsInRun + 1][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96 + this.stepsInRun; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.nDiseases; d++)
							for (int stepCount = 0; stepCount < this.nDim; stepCount++)
								nPop[scen][stepCount][g] += this.nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPop;

	}

	/**
	 * @return
	 */
	public double[][][][] getNPopByOriAge() {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.nDim][96][2];

		for (int r = 0; r < this.nRiskFactorClasses; r++)

			for (int scen = 0; scen < this.nScen + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < this.nDim; stepCount++)
							nPopByAge[scen][stepCount][a][g] += this.nPopByOriRiskClassByOriAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	public double[][][][] getNPopByOriAge(int riskClass) {

		double[][][][] nPopByAge = new double[this.nScen + 1][this.nDim][96][2];

		for (int scen = 0; scen < this.nScen + 1; scen++)
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < this.nDim; stepCount++)
						nPopByAge[scen][stepCount][a][g] = this.nPopByOriRiskClassByOriAge[scen][stepCount][riskClass][a][g];
		return nPopByAge;

	}

	/**
	 * @return start year of simulation
	 */
	public int getStartYear() {
		return this.startYear;
	}

	/**
	 * @param startYear
	 */
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	/**
	 * @return names of riskclasses
	 */
	public String[] getRiskClassnames() {
		return this.riskClassnames;
	}

	/**
	 * @param riskClassnames
	 */
	public void setRiskClassnames(String[] riskClassnames) {
		this.riskClassnames = riskClassnames;
	}

	/**
	 * @return String [] diseaseNames
	 */
	public String[] getDiseaseNames() {
		return this.diseaseNames;
	}

	/**
	 * @param diseaseNames
	 */
	public void setDiseaseNames(String[] diseaseNames) {
		this.diseaseNames = diseaseNames;
	}

	/**
	 * @return String [] scenarioNames
	 */
	public String[] getScenarioNames() {
		return this.scenarioNames;
	}

	/**
	 * @param scenarioNames
	 */
	public void setScenarioNames(String[] scenarioNames) {
		this.scenarioNames = scenarioNames;
	}

	/**
	 * @return whether to output disease states=combination of diseases (true)
	 *         or only diseases
	 */
	public boolean isDetails() {
		return this.details;
	}

	/**
	 * @param details
	 */
	public void setDetails(boolean details) {
		this.details = details;
	}

	/**
	 * @return maximum age of the simulated population
	 */
	public int getMaxAgeInSimulation() {
		return this.maxAgeInSimulation;
	}

	public void setMaxAgeInSimulation(int maxAgeInSimulation) {
		this.maxAgeInSimulation = maxAgeInSimulation;
	}

	/**
	 * @return minimum age of the simulated population
	 */
	public int getMinAgeInSimulation() {
		return this.minAgeInSimulation;
	}

	/**
	 * @param minAgeInSimulation
	 */
	public void setMinAgeInSimulation(int minAgeInSimulation) {
		this.minAgeInSimulation = minAgeInSimulation;
	}

	/**
	 * @return boolean []: array giving whether the intervention is applied to
	 *         women (index: scenario)
	 */
	public boolean[] getInWomen() {
		return this.inWomen;
	}

	/**
	 * @param inWomen
	 */
	public void setInWomen(boolean[] inWomen) {
		this.inWomen = inWomen;
	}

	/**
	 * @param i
	 * @param inWomen
	 */
	public void setInWomen(int i, boolean inWomen) {
		this.inWomen[i] = inWomen;
	}

	/**
	 * @return boolean []: array giving whether the intervention is applied to
	 *         men (index: scenario)
	 */
	public boolean[] getInMen() {
		return this.inMen;
	}

	/**
	 * @param inMen
	 */
	public void setInMen(boolean[] inMen) {
		this.inMen = inMen;
	}

	/**
	 * @param i
	 * @param inMen
	 */
	public void setInMen(int i, boolean inMen) {
		this.inMen[i] = inMen;
	}

	public boolean isWithNewborns() {
		return withNewborns;
	}

	public void setWithNewborns(boolean withNewborns) {
		this.withNewborns = withNewborns;
	}

}