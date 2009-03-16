/**
 * 
 */
package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.cdm.characteristic.types.CompoundCharacteristicType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jfree.chart.ChartPanel;

public class DynamoOutputFactory {
	private static final float[][][] nInSimulationByDurationByRiskClassByAge = null;

	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.estimation.ConfigurationFromXMLFactory");

	/*
	 * different arrays with summation data to keep things overzichtelijk, most
	 * summary arrays are recalculated each time they are needed, using a getXXX
	 * method, and are no longer fields only the most detailed arrays are kept
	 * as fields
	 */
	/* all are by sex, timestep and scenario */
	/* some are by risk or by age or both (indicated by their names) */
	/*
	 * p=proportion (of age/risk/sex class): these are temporary arrays not
	 * intended for the users, as for proportions it is always difficult to know
	 * what the denominator is
	 */
	/* n= total number with particular characterisitics in population */
	/*
	 * nInSimulation= total numbers with the characteristics in the simulation
	 * the ones with JAVADOC documentation are suitable for outside use and have
	 * getters / sequence of indexes: scenario, time , disease, risk class, age,
	 * and sex
	 */
	/**
	 * summary array of simulated numbers: index=sex
	 */
	int[] nInSimulation = new int[2]; /* index: sex */
	/**
	 * summary array of simulated numbers: index=age, sex
	 */
	int[][] nInSimulationByAge;
	/**
	 * summary array of simulated numbers of newborns: index=age at the end of
	 * simulation, sex
	 */
	int[][] nNewBornsInSimulationByAge;
	/**
	 * summary array of simulated numbers: index=riskclass, age, sex
	 */
	int[][][] nInSimulationByRiskClassByAge;
	/**
	 * summary array of simulated numbers: index=riskclass, duration class, age,
	 * sex
	 */
	int[][][][] nInSimulationByRiskClassAndDurationByAge;

	/**
	 * number of survivors in scenario and each diseaseState. Indexes: scenario,
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
	private double pDiseaseStateByRiskClassByAge[][][][][][];
	private double pDiseaseStateByOriRiskClassByAge[][][][][][];
	/**
	 * number of survivors in each diseaseState by scenario, time,disease state
	 * risk class, age and sex
	 */
	private double nDiseaseStateByRiskClassByAge[][][][][][];

	/*
	 * temporary arrays that contain simulated survival by risk class by Age;
	 * indexes are: scenario, time, risk class, age, and sex NB this array is
	 * reused, so it contains something different before and after applying
	 * method makeSummaryArrays
	 */
	private double[][][][][] pSurvivalByOriRiskClassByAge;
	private double[][][][][] pSurvivalByRiskClassByAge;

	/**
	 * average value of risk class by Age; indexes are: scenario time risk
	 * class, age, and sex
	 * 
	 * 
	 */
	private double[][][][][] meanRiskByRiskClassByAge;

	/**
	 * average value of risk class by the age at the start of simulation within
	 * a category of the riskclass; indexes are: scenario time risk class, age,
	 * and sex
	 * 
	 * 
	 */
	private double[][][][][] meanRiskByOriRiskClassByOriAge;

	/*
	 * this summary array is an exception to the rule that only detailed arrays
	 * are fields as for riskfactortype=2 means can more easily be calculated
	 * from crude data
	 */
	/**
	 * average value of riskvalue by Age; indexes are: scenario, time age, and
	 * sex
	 */

	private double[][][][] meanRiskByAge;

	/**
	 * number in risk class by Age; indexes are: scenario, time risk class age
	 * and sex
	 */

	private double[][][][][] nPopByRiskClassByAge;
	/**
	 * number in risk class by Age; indexes are: scenario, time risk class age
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
	 * details indicates whether information should be written on all disease
	 * combinations (true) or only per disease (marginals, when false
	 */

	private boolean details;
	private boolean[] scenInitial;
	private boolean[] scenTrans;
	private float[] cutoffs;
	private int durationClass;
	/*
	 * this array indicates how a population should be handled false= direct ;
	 * true= as a one-simulation for all scenarios population
	 */
	private boolean[] isOneScenPopulation;
	private boolean oneScenPopulation;
	private int nTransScenarios;
	private float[][] populationSize;
	private int[] newborns;
	private float mfratio;
	String[] riskClassnames;
	/**
	 * for each scenario the new prevalence rates
	 */
	private float[][][][] newPrevalence;
	/**
	 * the prevalence rates of the reference scenario
	 */
	private float[][][] oldPrevalence;
	/**
	 * the prevalence rates of the durationclasses in the reference scenario
	 */
	private float[][][] oldDurationNumbers;
	private String baseDir;
	private String simulationName;
	private String[] stateNames;
	private String[] diseaseNames;
	private String[] scenarioNames;
	private int nDim = 106;
	/**
	 * succesrate is the successrate of the intervention can be reset at a
	 * different value for obtaining new results without having to redo the
	 * simulation. Set this value to the succesrate in percent (value between 0
	 * and 100)
	 */
	private float[] succesrate = null;
	/**
	 * minAge is the lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private float[] minAge = null;
	/**
	 * minAge is the lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private float[] maxAge = null;

	/*
	 * popToScenIndex indicates the first scenario that is simulated by a
	 * particular population
	 */
	private int minAgeInSimulation = 100;
	/**
	 * minAge is the lower bound of the age that is affected by the
	 * intervention. It can be reset at a different value for obtaining new
	 * results without having to redo the simulation
	 */
	private int maxAgeInSimulation = 0;

	/*
	 * popToScenIndex indicates the first scenario that is simulated by a
	 * particular population
	 */
	private int[] popToScenIndex;
	/*
	 * categorized indicates whether the continuous variable is categorized in
	 * the output
	 * 
	 * // TODO: implementation of categorized=false;
	 */
	boolean categorized = true;

	/**
	 * The constructor initializes the fields (arrays with all values==0), and
	 * copies the information from the scenarioInfo object to the fields in this
	 * object. it also copies the scenario name, as it will write results to the
	 * results directory
	 * 
	 * @param scenInfo
	 * @param simName
	 */
	public DynamoOutputFactory(ScenarioInfo scenInfo, String simName) {
		super();
		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */

		BaseDirectory.getInstance("");
		baseDir = BaseDirectory.getBaseDir();
		this.simulationName = simName;
		setRiskType(scenInfo.getRiskType());
		setNScen(scenInfo.getNScenarios());
		durationClass = scenInfo.getIndexDurationClass();
		scenInitial = scenInfo.getInitialPrevalenceType();
		scenTrans = scenInfo.getTransitionType();
		nTransScenarios = 0;
		oneScenPopulation = false;
		isOneScenPopulation = new boolean[nScen + 1];
		popToScenIndex = new int[nScen + 1];
		setSuccesrate(scenInfo.getSuccesrate());

		minAge = scenInfo.getMinAge();
		maxAge = scenInfo.getMaxAge();
		scenarioNames = new String[scenInfo.getScenarioNames().length + 1];
		scenarioNames[0] = "reference scenario";
		for (int i = 1; i <= scenInfo.getScenarioNames().length; i++)
			scenarioNames[i] = scenInfo.getScenarioNames()[i - 1];
		cutoffs = scenInfo.getCutoffs();
		popToScenIndex[0] = 0;
		int currentPop = 1;
		for (int i = 0; i < nScen; i++) {
			isOneScenPopulation[currentPop] = false;
			popToScenIndex[currentPop] = i;
			if (scenTrans[i]) {
				nTransScenarios++;
				currentPop++;
			}
			if (scenInitial[i] && riskType != 2 && !oneScenPopulation) {
				/*
				 * first scenario of this type will have a population attached,
				 * further scenarios will not have a population so for those no
				 * population is present in that case currentPop is not
				 * increased in value, and popToScenIndex will be overwritten in
				 * the next loop
				 */
				oneScenPopulation = true;
				isOneScenPopulation[currentPop] = true;
				currentPop++;

			} else if (scenInitial[i] && riskType == 2) {
				currentPop++;
			}

		}

		if (oneScenPopulation)
			nPopulations = 2;
		else
			nPopulations = nScen + 1 - nTransScenarios;

		setStepsInRun(scenInfo.getYearsInRun());
		setStructure(scenInfo.getStructure());
		setNDiseases(scenInfo.getStructure());
		setNDiseaseStates(scenInfo.getStructure());

		stateNames = new String[nDiseaseStates];
		diseaseNames = new String[nDiseases];
		int currentDis = 0;
		int currentState = 0;
		for (int c = 0; c < structure.length; c++) {
			if (structure[c].getNInCluster() > 1
					&& !structure[c].isWithCuredFraction())
				for (int i = 1; i < Math.pow(structure[c].getNInCluster(), 2); i++) {
					stateNames[currentState] = "";
					for (int d1 = 0; d1 < structure[c].getNInCluster(); d1++) {

						if ((i & (1 << d1)) == (1 << d1))
							if (stateNames[currentState] == "")
								stateNames[currentState] = structure[c]
										.getDiseaseName().get(d1);
							else
								stateNames[currentState] = stateNames[currentState]
										+ "+"
										+ structure[c].getDiseaseName().get(d1);
					}
					currentState++;
				}

			for (int d = 0; d < structure[c].getNInCluster(); d++) {
				diseaseNames[currentDis] = structure[c].getDiseaseName().get(d);
				currentDis++;
				if (structure[c].getNInCluster() == 1
						|| structure[c].isWithCuredFraction()) {

					stateNames[currentState] = structure[c].getDiseaseName()
							.get(d);
					currentState++;
				}

			}
		}

		details = scenInfo.isDetails();

		startYear = scenInfo.getStartYear();
		populationSize = scenInfo.getPopulationSize();
		newborns = scenInfo.getNewborns();
		mfratio = scenInfo.getMaleFemaleRatio();
		riskClassnames = scenInfo.getRiskClassnames();
		oldPrevalence = scenInfo.getOldPrevalence();
		oldDurationNumbers = scenInfo.getOldDurationClasses();

		newPrevalence = scenInfo.getNewPrevalence();
		// TODO remove this temporary solution
		if (newPrevalence == null) {
			newPrevalence = new float[1][][][];
			newPrevalence[0] = oldPrevalence;
		}

		if (getRiskType() == 1 || getRiskType() == 3)
			setNRiskFactorClasses(scenInfo.getRiskClassnames().length);
		else {

			if (cutoffs == null)
				setNRiskFactorClasses(10);
			/*
			 * NB the names and cutoffs are taken from the data, this is part of
			 * the method extractArraysFroPopulations
			 */
			else {
				setNRiskFactorClasses(cutoffs.length + 1);
				riskClassnames = new String[getNRiskFactorClasses()];
				for (int i = 0; i < cutoffs.length; i++) {
					if (i > 0)
						riskClassnames[i] = cutoffs[i - 1] + "-" + cutoffs[i];
				}
				riskClassnames[0] = "<" + cutoffs[0];
				riskClassnames[cutoffs.length] = ">"
						+ cutoffs[cutoffs.length - 1];

			}
			;
		}
		/*
		 * as the starting situation is also part of the results, the dimension
		 * of the arrays should be stepsInRun+1
		 */
		int nClasses = getNRiskFactorClasses();
		meanRiskByAge = new double[nScen + 1][stepsInRun + 1][nDim][2];
		pSurvivalByRiskClassByAge = new double[nScen + 1][stepsInRun + 1][nClasses][96 + stepsInRun][2];
		pSurvivalByOriRiskClassByAge = new double[nScen + 1][nDim][nClasses][96][2];
		nPopByRiskClassByAge = new double[nScen + 1][stepsInRun + 1][nClasses][96 + stepsInRun][2];
		nPopByOriRiskClassByOriAge = new double[nScen + 1][nDim][nClasses][96][2];

		if (riskType == 2)
			meanRiskByRiskClassByAge = new double[nScen + 1][stepsInRun + 1][nClasses][96 + stepsInRun][2];
		if (riskType == 3)
			meanRiskByRiskClassByAge = new double[nScen + 1][stepsInRun + 1][nClasses][96 + stepsInRun][2];

		/*
		 * NB the dimension can be nClasses (nClasses-1) but this makes life
		 * more difficult for now we suppose we have enough room for doing it
		 * this way
		 */
		pDiseaseStateByRiskClassByAge = new double[nScen + 1][stepsInRun + 1][nDiseaseStates][nClasses][nDim][2];
		nDiseaseStateByRiskClassByAge = new double[nScen + 1][stepsInRun + 1][nDiseaseStates][nClasses][nDim][2];
		pDiseaseStateByOriRiskClassByAge = new double[nScen + 1][nDim][nDiseaseStates][nClasses][96][2];
		nDiseaseStateByOriRiskClassByOriAge = new double[nScen + 1][nDim][nDiseaseStates][nClasses][96][2];

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
	 * 
	 * @param Population
	 *            [] pop: array with the simulated populations
	 * @throws DynamoScenarioException
	 */
	public void extractArraysFromPopulations(Population[] pop)
			throws DynamoScenarioException {

		// TODO newborns weighting
		if (riskType == 2 && cutoffs == null)
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
		 * from(=orginal), riskfactor class to (=in scenario) age, sex
		 */
		double[][][][][] pSurvivalByRiskClassByAge_scen = new double[stepsInRun + 1][nRiskFactorClasses][nRiskFactorClasses][nDim][2];
		double[][][][][][] pDiseaseStateByRiskClassByAge_scen = new double[stepsInRun + 1][nDiseaseStates][nRiskFactorClasses][nRiskFactorClasses][nDim][2];
		double[][][][][] pSurvivalByOriRiskClassByAge_scen = new double[nDim][nRiskFactorClasses][nRiskFactorClasses][96][2];
		double[][][][][][] pDiseaseStateByOriRiskClassByAge_scen = new double[nDim][nDiseaseStates][nRiskFactorClasses][nRiskFactorClasses][96][2];

		double[][][][][] MeanRiskByRiskClassByAge_scen = null;
		double[][][][][] MeanRiskByOriRiskClassByAge_scen = null;
		if (riskType == 3) {
			MeanRiskByRiskClassByAge_scen = new double[stepsInRun][nRiskFactorClasses][nRiskFactorClasses][nDim][2];
			MeanRiskByOriRiskClassByAge_scen = new double[nDim][nRiskFactorClasses][nRiskFactorClasses][96][2];
		}
		int sexIndex = 0;
		int ageIndex = 0;
		nInSimulation = new int[2];
		nInSimulationByAge = new int[nDim][2];
		nNewBornsInSimulationByAge = new int[stepsInRun][2];
		nInSimulationByRiskClassByAge = new int[nRiskFactorClasses][nDim][2];
		nInSimulationByRiskClassAndDurationByAge = new int[nRiskFactorClasses][100][nDim][2];

		double weight[][][] = new double[nRiskFactorClasses][96][2];
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

				for (int r = 0; r < nRiskFactorClasses; r++)
					if (riskType != 2) {
						weight[r][age][s] = oldPrevalence[age][s][r]
								* nInSimulationByAge[age][s]
								/ nInSimulationByRiskClassByAge[r][age][s];
						if (riskType == 3 && r == durationClass)
							for (int duur = 0; duur < oldDurationNumbers[age][s].length; duur++)
								weight2[duur][age][s] = oldPrevalence[age][s][r]
										* oldDurationNumbers[age][s][duur]
										* nInSimulationByAge[age][s]
										/ nInSimulationByRiskClassAndDurationByAge[r][duur][age][s];
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

		for (int thisPop = 0; thisPop < nPopulations; thisPop++) {

			Iterator<Individual> individualIterator = pop[thisPop].iterator();

			/*
			 * make detailed arrays summing the data for sex/age/year/risk class
			 * combinations
			 */

			/* start with reading the data from the population */
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				double weightOfIndividual = 1;

				int riskClassAtStart = -1;

				int ageAtStart = (int) Math.round(((Float) individual.get(1)
						.getValue(0)));

				int nSteps = 105 - ageAtStart;
				if (ageAtStart < 0)
					nSteps = stepsInRun + 1;

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

						if (riskType != 2) {
							riskFactor = (int) (Integer) individual.get(3)
									.getValue(stepCount);

						} else {
							riskValue = (float) (Float) individual.get(3)
									.getValue(stepCount);
							int i = 0;
							if (riskClassnames.length > 1) {
								if (riskValue <= cutoffs[0])
									riskFactor = 0;

								else {
									for (i = 1; i < cutoffs.length; i++) {
										if (riskValue <= cutoffs[i]
												&& riskValue > cutoffs[i - 1])
											break;
									}

									riskFactor = i;
									/* just to be sure that it goes OK: */
									if (riskValue > cutoffs[cutoffs.length - 1])
										riskFactor = cutoffs.length;
								}
							} else { /*
									 * only one single value present for
									 * riskValue
									 */
								riskFactor = 0;

							}
						}

						if (riskType == 3)

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
						 * reference scenario in changing initial prevalence
						 * scenarios this is the value of "from" In changing
						 * transition rate scenarios the initial distribution is
						 * the same in the reference population and the scenario
						 * population
						 */

						if (stepCount == 0 && riskType != 2) {
							if (ageIndex > 100)
								log.fatal(stepCount + " " + riskFactor + " "
										+ ageIndex + " " + sexIndex);
						}
						/* is start year for this individual */
						if ((stepCount == 0 || ageIndex == 0) && riskType != 2)
							weightOfIndividual = weight[riskFactor][ageIndex][sexIndex];

						if ((stepCount == 0 || ageIndex == 0) && riskType == 3
								&& riskFactor == durationClass)
							weightOfIndividual = weight2[riskDurationValue][ageIndex][sexIndex];

						if (riskType == 3)
							compoundData = ((CompoundCharacteristicValue) individual
									.get(5)).getUnwrappedValue(stepCount);
						else
							compoundData = ((CompoundCharacteristicValue) individual
									.get(4)).getUnwrappedValue(stepCount);
						survival = compoundData[getNDiseaseStates() - 1];

						/*
						 * if the population is a one-for-all population, read
						 * the from (risk factor without the scenario change)
						 * and to (with the scenario change) from the label of
						 * the individual
						 */

						if (thisPop > 0)
							if (isOneScenPopulation[thisPop]) {
								indLabel = individual.getLabel();
								tokens = indLabel.split(delims);
								from = Integer.parseInt(tokens[2]);
								to = Integer.parseInt(tokens[3]);
								/*
								 * overwrite the weight with the weight of the
								 * reference population
								 */
								if (stepCount == 0 || ageIndex == 0)
									weightOfIndividual = weight[from][ageIndex][sexIndex];

							}
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

							if (isOneScenPopulation[thisPop]) {
								if (stepCount <= stepsInRun)
									pSurvivalByRiskClassByAge_scen[stepCount][from][to][ageIndex][sexIndex] += weightOfIndividual
											* survival;
								if (ageAtStart >= 0)
									pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][ageAtStart][sexIndex] += weightOfIndividual
											* survival;
								if (riskType == 3) {
									if (stepCount <= stepsInRun)
										MeanRiskByRiskClassByAge_scen[stepCount][from][to][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
								}

								/* for a "one-for-one" scenario */

							} else {
								if (stepCount <= stepsInRun)
									pSurvivalByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* survival;
								if (ageAtStart >= 0)
									pSurvivalByOriRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* survival;

								if (riskType == 2) {
									if (stepCount <= stepsInRun)
										meanRiskByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskValue * survival;
									if (ageAtStart >= 0)
										meanRiskByOriRiskClassByOriAge[popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskValue * survival;

									if (stepCount <= stepsInRun)
										meanRiskByAge[popToScenIndex[thisPop] + 1][stepCount][ageIndex][sexIndex] += weightOfIndividual
												* riskValue * survival;
								}
								if (riskType == 3) {
									if (stepCount <= stepsInRun)
										meanRiskByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										meanRiskByOriRiskClassByOriAge[popToScenIndex[thisPop] + 1][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;

								}
							}
						}
						/*
						 * for the reference scenario
						 */
						else {
							if (stepCount <= stepsInRun)
								pSurvivalByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* survival;
							if (ageAtStart >= 0)
								pSurvivalByOriRiskClassByAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
										* survival;

							if (riskType == 2) {
								if (stepCount <= stepsInRun)
									meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* riskValue * survival;
								if (stepCount <= stepsInRun)
									meanRiskByAge[0][stepCount][ageIndex][sexIndex] += weightOfIndividual
											* riskValue * survival;
								if (ageAtStart >= 0)
									meanRiskByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* riskValue * survival;

							}
							if (riskType == 3) {
								if (stepCount <= stepsInRun)
									meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* riskDurationValue * survival;
								if (ageAtStart >= 0)
									meanRiskByOriRiskClassByOriAge[0][stepCount][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* riskDurationValue * survival;

							}

							/*
							 * in case there is a one-for-all scenario, the
							 * reference scenario should be added to the
							 * summary-array of this scenario, because
							 * not-changing is also a part of possible scenarios
							 */

							if (oneScenPopulation) {
								if (stepCount <= stepsInRun)
									pSurvivalByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageIndex][sexIndex] += weightOfIndividual
											* survival;
								if (ageAtStart >= 0)
									pSurvivalByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* survival;

								if (riskType == 3) {
									if (stepCount <= stepsInRun)
										MeanRiskByRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageIndex][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
									if (ageAtStart >= 0)
										MeanRiskByOriRiskClassByAge_scen[stepCount][riskClassAtStart][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* riskDurationValue * survival;
								}
							}
						}
						/*
						 * add disease states to disease state arrays in a
						 * similar fashion
						 */
						for (int s = 0; s < nDiseaseStates; s++) {

							if (thisPop > 0)
								if (isOneScenPopulation[thisPop]) {
									if (stepCount <= stepsInRun)
										pDiseaseStateByRiskClassByAge_scen[stepCount][s][from][to][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										pDiseaseStateByOriRiskClassByAge_scen[stepCount][s][from][to][ageAtStart][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;

								} else {
									if (stepCount <= stepsInRun)
										pDiseaseStateByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
									if (ageAtStart >= 0)
										pDiseaseStateByOriRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][s][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
												* compoundData[s] * survival;
								}
							if (thisPop == 0) {
								if (stepCount <= stepsInRun)
									pDiseaseStateByRiskClassByAge_scen[stepCount][s][riskClassAtStart][riskClassAtStart][ageIndex][sexIndex] += weightOfIndividual

											* compoundData[s] * survival;
								if (ageAtStart >= 0)
									pDiseaseStateByOriRiskClassByAge_scen[stepCount][s][riskClassAtStart][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;

								if (stepCount <= stepsInRun)
									pDiseaseStateByRiskClassByAge[0][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;
								if (ageAtStart >= 0)
									pDiseaseStateByOriRiskClassByAge[0][stepCount][s][riskClassAtStart][ageAtStart][sexIndex] += weightOfIndividual
											* compoundData[s] * survival;

							}

						}

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
		if (oneScenPopulation) {
			float[] dummy = new float[nRiskFactorClasses];
			Arrays.fill(dummy, 1);
			float[][] toChange;

			for (int scen = 0; scen < nScen; scen++)
				if (scenInitial[scen]) {
					/*
					 * calculate the transitions needed from old to new
					 * prevalence
					 */
					for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
						for (int a = 0; a < 96 + stepCount; a++)
							for (int s = 0; s < 2; s++)

							{
								/* for safety, initialize arrays */
								for (int r = 0; r < nRiskFactorClasses; r++) {
									pSurvivalByRiskClassByAge[scen + 1][stepCount][r][a][s] = 0;
									for (int state = 0; state < nDiseaseStates; state++)
										pDiseaseStateByRiskClassByAge[scen + 1][stepCount][state][r][a][s] = 0;
								}
								if (a >= stepCount)
									toChange = NettTransitionRateFactory
											.makeNettTransitionRates(
													oldPrevalence[a - stepCount][s],
													newPrevalence[scen][a
															- stepCount][s], 0,
													dummy);
								else
									/* for newborns */
									toChange = NettTransitionRateFactory
											.makeNettTransitionRates(
													oldPrevalence[0][s],
													newPrevalence[scen][0][s],
													0, dummy);
								for (from = 0; from < nRiskFactorClasses; from++)
									for (to = 0; to < nRiskFactorClasses; to++) {

										for (int state = 0; state < nDiseaseStates; state++)
											pDiseaseStateByRiskClassByAge[scen + 1][stepCount][state][to][a][s] += toChange[from][to]
													* pDiseaseStateByRiskClassByAge_scen[stepCount][state][from][to][a][s];

										pSurvivalByRiskClassByAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
												* pSurvivalByRiskClassByAge_scen[stepCount][from][to][a][s];
										if (riskType > 1)
											meanRiskByRiskClassByAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
													* MeanRiskByRiskClassByAge_scen[stepCount][from][to][a][s];
										/*
										 * check if there are the required
										 * persons in the "one-for-all-scenario"
										 * situation do this only at step 0, as
										 * they might legitimately be zero at
										 * later times
										 */
										if ((stepCount == 0)
												&& toChange[from][to] > 0
												&& pSurvivalByRiskClassByAge_scen[stepCount][from][to][a][s] == 0) {
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

									}// end to-from loop

							}// end age , sex and stepCount loop
					/*
					 * repeat
					 * 
					 * 
					 * for the arrays based on original age and riskfactors
					 */

					for (int stepCount = 0; stepCount < nDim; stepCount++)
						for (int a = 0; a < 96; a++)
							for (int s = 0; s < 2; s++)

							{

								/* for safety, initialize arrays */
								for (int r = 0; r < nRiskFactorClasses; r++) {
									pSurvivalByOriRiskClassByAge[scen + 1][stepCount][r][a][s] = 0;
									for (int state = 0; state < nDiseaseStates; state++)
										pDiseaseStateByOriRiskClassByAge[scen + 1][stepCount][state][r][a][s] = 0;
								}

								toChange = NettTransitionRateFactory
										.makeNettTransitionRates(
												oldPrevalence[a][s],
												newPrevalence[scen][a][s], 0,
												dummy);
								for (from = 0; from < nRiskFactorClasses; from++)
									for (to = 0; to < nRiskFactorClasses; to++) {

										for (int state = 0; state < nDiseaseStates; state++)
											pDiseaseStateByOriRiskClassByAge[scen + 1][stepCount][state][to][a][s] += toChange[from][to]
													* pDiseaseStateByOriRiskClassByAge_scen[stepCount][state][from][to][a][s];

										pSurvivalByOriRiskClassByAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
												* pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][a][s];
										if (riskType > 1)
											meanRiskByOriRiskClassByOriAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
													* MeanRiskByOriRiskClassByAge_scen[stepCount][from][to][a][s];

										if (stepCount == 0
												&& toChange[from][to] > 0
												&& pSurvivalByOriRiskClassByAge_scen[stepCount][from][to][a][s] == 0) {
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

									}// end to-from loop

							}// end age , sex and stepCount loop

				}// end loop for scenario

		}

	}

	/**
	 * makes a array of mortality by scenario, year, age and sex It is not
	 * possible to do so also by riskfactor or by disease In order to do so,
	 * this should be included as state in the update rule
	 * 
	 * @param numbers
	 * @return
	 */
	private double[][][][] makeMortalityArray(boolean numbers) {
		double[][][][] mortality = new double[nScen + 1][stepsInRun][nDim][2];

		for (int scen = 0; scen < nScen + 1; scen++)
			for (int a = 0; a < nDim - 1; a++)
				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
						double denominator = 0;
						double nominator = 0;
						double personsAtnextAge = 0;
						for (int r = 0; r < nRiskFactorClasses; r++) {
							denominator += nPopByRiskClassByAge[scen][stepCount][r][a][g];
							nominator += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									- nPopByRiskClassByAge[scen][stepCount + 1][r][a + 1][g];
							personsAtnextAge += nPopByRiskClassByAge[scen][stepCount + 1][r][a + 1][g];
						}

						if (denominator != 0 && !numbers
								&& personsAtnextAge > 0)
							mortality[scen][stepCount][a][g] = nominator
									/ denominator;
						if (denominator != 0 && personsAtnextAge > 0 && numbers)
							mortality[scen][stepCount][a][g] = nominator;
						if (denominator == 0 || personsAtnextAge == 0)
							mortality[scen][stepCount][a][g] = -1;

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
		double[][][][][] incidence = new double[nScen + 1][stepsInRun + 1][nDiseases][nDim][2];
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int scen = 0; scen < nScen + 1; scen++)
			for (int d = 0; d < nDiseases; d++)
				for (int a = 0; a < nDim - 1; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
							double denominator = 0;
							double nominator = 0;

							for (int r = 0; r < nRiskFactorClasses; r++) {
								denominator += nPopByRiskClassByAge[scen][stepCount][r][a][g]
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

		double diseaseArray[][][][][][] = new double[dim1][dim2][nDiseaseStates][dim4][dim5][dim6];

		for (int c = 0; c < structure.length; c++) {

			for (int d = 0; d <= structure[c].getNInCluster(); d++) {

				for (int state = 1; state < Math.pow(2, structure[c]
						.getNInCluster()); state++) {

					if ((state & (1 << d)) == (1 << d)) {
						/*
						 * pDisease[thisScen][stepCount][currentDisease +
						 * d][sexIndex] += compoundData[currentState + s - 1]
						 * survival weight[riskFactor][ageIndex][ sexIndex]; if
						 * (details)
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
			currentClusterStart += Math.pow(2, structure[c].getNInCluster()) - 1;
		}
		return diseaseArray;
	}

	private double[][][] makeDiseaseArray(double[][][][][][] stateArray,
			int scen, int stepCount, int g) {
		int currentDisease = 0;
		int currentClusterStart = 0;
		int dim4 = stateArray[0][0][0].length;
		int dim5 = stateArray[0][0][0][0].length;
		double diseaseArray[][][] = new double[nDiseaseStates][dim4][dim5];

		for (int c = 0; c < structure.length; c++) {

			for (int d = 0; d <= structure[c].getNInCluster(); d++) {

				for (int state = 1; state < Math.pow(2, structure[c]
						.getNInCluster()); state++) {

					if ((state & (1 << d)) == (1 << d)) {
						/*
						 * pDisease[thisScen][stepCount][currentDisease +
						 * d][sexIndex] += compoundData[currentState + s - 1]
						 * survival weight[riskFactor][ageIndex][ sexIndex]; if
						 * (details)
						 */
						for (int r = 0; r < dim4; r++)
							for (int a = 0; a < dim5; a++)
								diseaseArray[currentDisease][r][a] += stateArray[scen][stepCount][currentClusterStart
										+ state - 1][r][a][g];
					}
				}
				currentDisease++;
			}
			currentClusterStart += Math.pow(2, structure[c].getNInCluster()) - 1;
		}
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

		double healthyPersonsByRiskClass[][][][][] = new double[nScen + 1][stepsInRun + 1][nRiskFactorClasses][105][2];
		double diseasedPersons[][][][] = new double[nScen + 1][stepsInRun + 1][105][2];

		for (int scen = 0; scen < nScen + 1; scen++)
			for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
				for (int a = 0; a < 105; a++)
					for (int g = 0; g < 2; g++) {

						for (int r = 0; r < nRiskFactorClasses; r++) {
							healthyPersonsByRiskClass[scen][stepCount][r][a][g] = nPopByRiskClassByAge[scen][stepCount][r][a][g];
							int currentClusterStart = 0;
							for (int c = 0; c < structure.length; c++) {
								double nWithDisease = 0;

								for (int state = 0; state < Math.pow(2,
										structure[c].getNInCluster() - 1); state++) {

									nWithDisease += nDiseaseStateByRiskClassByAge[scen][stepCount][currentClusterStart
											+ state][r][a][g];
								}
								currentClusterStart += Math.pow(2, structure[c]
										.getNInCluster()) - 1;
								if (nPopByRiskClassByAge[scen][stepCount][r][a][g] > 0)
									healthyPersonsByRiskClass[scen][stepCount][r][a][g] *= (nPopByRiskClassByAge[scen][stepCount][r][a][g] - nWithDisease)
											/ nPopByRiskClassByAge[scen][stepCount][r][a][g];
								else
									healthyPersonsByRiskClass[scen][stepCount][r][a][g] = 0;
							}
							diseasedPersons[scen][stepCount][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g]
									- healthyPersonsByRiskClass[scen][stepCount][r][a][g];
						}
					}
		return diseasedPersons;
	}

	private double[][][] getNumberOfOriDiseasedPersons(int age) {

		double healthyPersons[][][][] = new double[nScen + 1][nDim][nRiskFactorClasses][2];
		double diseasedPersons[][][] = new double[nScen + 1][nDim][2];

		for (int scen = 0; scen < nScen + 1; scen++)
			for (int steps = 0; steps < nDim; steps++)

				for (int g = 0; g < 2; g++) {

					for (int r = 0; r < nRiskFactorClasses; r++) {
						double nWithDisease = 0;
						healthyPersons[scen][steps][r][g] += nPopByOriRiskClassByOriAge[scen][steps][r][age][g];
						int currentClusterStart = 0;
						for (int c = 0; c < structure.length; c++) {

							for (int state = 0; state < Math.pow(2,
									structure[c].getNInCluster() - 1); state++) {

								/*
								 * pDisease[thisScen][stepCount][currentDisease
								 * + d][sexIndex] += compoundData[currentState +
								 * s - 1] survival weight[riskFactor][ageIndex][
								 * sexIndex]; if (details)
								 */

								nWithDisease += nDiseaseStateByOriRiskClassByOriAge[scen][steps][currentClusterStart
										+ state][r][age][g];
							}
							currentClusterStart += Math.pow(2, structure[c]
									.getNInCluster()) - 1;
							if (nPopByOriRiskClassByOriAge[scen][steps][r][age][g] > 0)
								healthyPersons[scen][steps][r][g] *= (nPopByOriRiskClassByOriAge[scen][steps][r][age][g] - nWithDisease)
										/ nPopByOriRiskClassByOriAge[scen][steps][r][age][g];
							else
								healthyPersons[scen][steps][r][g] = 0;
						}
						diseasedPersons[scen][steps][g] += nPopByOriRiskClassByOriAge[scen][steps][r][age][g]
								- healthyPersons[scen][steps][r][g];
					} // end loop over r
				}
		return diseasedPersons;
	}

	/**
	 * @param pop
	 */
	private void extractNumberInSimulationFromPopulation(Population[] pop) {
		int sexIndex;
		int ageIndex;
		minAgeInSimulation = 100;
		maxAgeInSimulation = 0;
		Iterator<Individual> individualIterator1 = pop[0].iterator();

		while (individualIterator1.hasNext()) {
			Individual individual = individualIterator1.next();

			ageIndex = (int) Math
					.round(((Float) individual.get(1).getValue(0)));
			sexIndex = (int) (Integer) individual.get(2).getValue(0);
			if (ageIndex > maxAgeInSimulation)
				maxAgeInSimulation = ageIndex;
			if (ageIndex < minAgeInSimulation)
				minAgeInSimulation = ageIndex;

			if (ageIndex < 0)
				nNewBornsInSimulationByAge[ageIndex + stepsInRun][sexIndex]++;
			else {
				nInSimulation[sexIndex]++;
				nInSimulationByAge[ageIndex][sexIndex]++;

				float riskValue;
				int durationValue;
				if (riskType != 2) {
					int riskFactor = (int) (Integer) individual.get(3)
							.getValue(0);
					nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;
					if (riskType == 3) {
						durationValue = Math.round((float) (Float) individual
								.get(4).getValue(0));
						nInSimulationByRiskClassAndDurationByAge[riskFactor][durationValue][ageIndex][sexIndex]++;
					}
				} else {
					riskValue = (float) (Float) individual.get(3).getValue(0);
					int riskFactor;
					int i = 0;
					if (riskClassnames.length > 1) {
						if (riskValue <= cutoffs[0])
							riskFactor = 0;

						else {
							for (i = 1; i < cutoffs.length; i++) {
								if (riskValue <= cutoffs[i]
										&& riskValue > cutoffs[i - 1])
									break;
							}

							riskFactor = i;
							/* just to be sure that it goes OK: */
							if (riskValue > cutoffs[cutoffs.length - 1])
								riskFactor = cutoffs.length;
						}
					} else { /* only one single value present for riskValue */
						riskFactor = 0;
					}
					nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;

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
			for (int ipop = 0; ipop < nPopulations; ipop++) {
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
				nRiskFactorClasses = 5;
				cutoffs = new float[4];
				riskClassnames = new String[5];
				for (int i = 0; i < 4; i++) {
					cutoffs[i] = minRisk + i * (maxRisk - minRisk) * 0.1F;
					if (i > 0)
						riskClassnames[i] = cutoffs[i - 1] + "-" + cutoffs[i];

				}
				riskClassnames[0] = "<" + cutoffs[0];
				riskClassnames[4] = ">" + cutoffs[3];
			} else if (maxRisk == minRisk) {
				riskClassnames = new String[1];
				nRiskFactorClasses = 1;
				riskClassnames[0] = ((Float) maxRisk).toString();
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
		for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++) {

			float originalNumber = 0;
			int originalAge = 0;
			float ratio = 0;

			for (int s = 0; s < 2; s++) {
				for (int a = 0; a < Math.min(nDim, (96 + stepCount)); a++) {
					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (a - stepCount >= 0) {
						/* for those in the initial cohort */
						originalNumber = populationSize[a - stepCount][s];
						originalAge = a - stepCount;
						if (nInSimulationByAge[a - stepCount][s] != 0)
							ratio = originalNumber
									/ nInSimulationByAge[a - stepCount][s];
						else
							ratio = 0;
					} else {
						/* for newborns (born during simulation ) */
						if (s == 0) /* males */
							originalNumber = newborns[stepCount - 1] * mfratio
									/ (1 + mfratio);
						else
							/* females */
							originalNumber = newborns[stepCount - 1]
									* (1 - mfratio / (1 + mfratio));
						originalAge = -stepCount;
						if (nNewBornsInSimulationByAge[stepCount - 1][s] != 0)
							ratio = originalNumber
									/ nNewBornsInSimulationByAge[stepCount - 1][s];
						else
							ratio = 0;
					}

					// TODO hierboven gok nog nagaan en zorgen dat alles goed
					// geinitialiseerd is

					/*
					 * nb: scen is the scenario number starting with scen =0 is
					 * the reference scenario. in arrays with scenario info the
					 * first (0) element refers to the first alternative
					 * scenario
					 */

					for (int scen = 0; scen <= nScen; scen++) {

						for (int r = 0; r < nRiskFactorClasses; r++) {

							nPopByRiskClassByAge[scen][stepCount][r][a][s] = ratio
									* pSurvivalByRiskClassByAge[scen][stepCount][r][a][s];

							for (int state = 0; state < nDiseaseStates; state++) {

								nDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s] = ratio
										* pDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s];

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
			for (int stepCount = 0; stepCount < nDim - a; stepCount++) {
				float ratio = 0;
				for (int s = 0; s < 2; s++) {

					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (nInSimulationByAge[a][s] != 0)
						ratio = populationSize[a][s] / nInSimulationByAge[a][s];
					/* newborns are not included in the cohort based arrays */

					for (int scen = 0; scen <= nScen; scen++) {
						for (int r = 0; r < nRiskFactorClasses; r++) {
							nPopByOriRiskClassByOriAge[scen][stepCount][r][a][s] = ratio
									* pSurvivalByOriRiskClassByAge[scen][stepCount][r][a][s];
							for (int state = 0; state < nDiseaseStates; state++) {
								nDiseaseStateByOriRiskClassByOriAge[scen][stepCount][state][r][a][s] = ratio
										* pDiseaseStateByOriRiskClassByAge[scen][stepCount][state][r][a][s];

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

	public void writeOutput(ScenarioInfo scenInfo) throws XMLStreamException,
			IOException, FactoryConfigurationError {

		String baseDir = BaseDirectory.getBaseDir();
		// for (int scen = 0; scen < scenInfo.getNScenarios(); scen++) {
		/* make a n=new directory if it does not yet exists */
		String directoryName = baseDir + File.separator + "Simulations"
				+ File.separator + simulationName + File.separator + "results";
		log.debug("directoryName" + directoryName);
		File directory = new File(directoryName);
		boolean isNewDirectory = directory.mkdirs();

		for (int scen = 0; scen <= nScen; scen++) {
			String fileName = baseDir + File.separator + "Simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "excel_year_male_scenario"
					+ scen + ".xml";
			try {
				writeWorkBookXMLbyYear(fileName, 0, scen);
			} catch (DynamoOutputException e2) {
				// TODO Auto-generated catch block write warning message to user
				e2.printStackTrace();
			}
			fileName = baseDir + File.separator + "Simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "excel_year_female_scenario"
					+ scen + ".xml";
			try {
				writeWorkBookXMLbyYear(fileName, 1, scen);
			} catch (DynamoOutputException e1) {
				// TODO Auto-generated catch block write warning message to user
				e1.printStackTrace();
			}
			fileName = baseDir + File.separator + "Simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "excel_year_scenario" + scen
					+ ".xml";
			try {
				writeWorkBookXMLbyYear(fileName, 2, scen);
			} catch (DynamoOutputException e1) {
				// TODO Auto-generated catch block write warning message to user
				e1.printStackTrace();
			}
			fileName = baseDir + File.separator + "Simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "excel_cohort_male_scenario"
					+ scen + ".xml";
			try {
				writeWorkBookXMLbyCohort(fileName, 0, scen);
			} catch (DynamoOutputException e) {
				// TODO Auto-generated catch block: write warning message to
				// user

				e.printStackTrace();
			}
			fileName = baseDir + File.separator + "Simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator
					+ "excel_cohort_female_scenario" + scen + ".xml";
			try {
				writeWorkBookXMLbyCohort(fileName, 1, scen);
			} catch (DynamoOutputException e) {
				// TODO Auto-generated catch block write warning message to user
				e.printStackTrace();
			}
			fileName = baseDir + File.separator + "Simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "excel_cohort_scenario"
					+ scen + ".xml";
			try {
				writeWorkBookXMLbyCohort(fileName, 2, scen);
			} catch (DynamoOutputException e) {
				// TODO Auto-generated catch block write warning message to user
				e.printStackTrace();
			}

		}
	}

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
		for (int year = 0; year < stepsInRun + 1; year++) {
			writer.writeStartElement("Worksheet");
			writer.writeAttribute("ss:Name", "year " + (startYear + year));
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (riskType == 1 || riskType == 3 || categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
				writeCell(writer, "std_riskFactor");
				writeCell(writer, "skewness");

			}
			if (riskType == 3) {

				writeCell(writer, "mean duration");

			}
			if (riskType == 2 && categorized) {

				writeCell(writer, "mean riskFactor");

			}

			/* age */
			writeCell(writer, "age");

			/* total number */
			writeCell(writer, "total number");

			/* disease info */
			if (details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < nDiseaseStates + 3; col++) {
					writeCell(writer, stateNames[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < nDiseases + 4; col++) {
					writeCell(writer, diseaseNames[col - 4]);
					// TODO: goede naam laten printen

				}
			}
			writer.writeEndElement();// </row>

			/* write the data */
			/* each row is a risk-class / age combination */
			for (int rClass = 0; rClass < nRiskFactorClasses; rClass++)

				for (int a = 0; a < 96; a++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
					if (riskType == 1 || riskType == 3 || categorized) {

						writeCell(writer, riskClassnames[rClass]);

					}

					/*
					 * write the mean value of the continuous risk factor or the
					 * duration
					 */
					if (riskType == 2 && !categorized) {

						/*
						 * Calculate the average over all risk factor classes
						 * 
						 * / make arrays with the data needed to be averaged
						 */
						double[] toByAveragedRef;
						double[] toByAveragedScen;
						double[] numbersRef;
						double[] numbersScen;

						if (sex < 2) {
							toByAveragedRef = new double[nRiskFactorClasses];
							toByAveragedScen = new double[nRiskFactorClasses];
							numbersRef = new double[nRiskFactorClasses];
							numbersScen = new double[nRiskFactorClasses];
							for (int r = 0; r < nRiskFactorClasses; r++) {

								toByAveragedRef[r] = meanRiskByRiskClassByAge[0][year][rClass][a][sex];
								toByAveragedScen[r] = meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex];
								numbersRef[r] = nPopByRiskClassByAge[0][year][rClass][a][sex];
								numbersScen[r] = nPopByRiskClassByAge[thisScen][year][rClass][a][sex];
							}
						} else {
							toByAveragedRef = new double[nRiskFactorClasses * 2];
							toByAveragedScen = new double[nRiskFactorClasses * 2];
							numbersRef = new double[nRiskFactorClasses * 2];
							numbersScen = new double[nRiskFactorClasses * 2];
							for (int r = 0; r < nRiskFactorClasses; r++)
								for (int s = 0; s < 2; s++) {

									toByAveragedRef[r + s * nRiskFactorClasses] = meanRiskByRiskClassByAge[0][year][rClass][a][s];
									toByAveragedScen[r + s * nRiskFactorClasses] = meanRiskByRiskClassByAge[thisScen][year][rClass][a][s];
									numbersRef[r + s * nRiskFactorClasses] = nPopByRiskClassByAge[0][year][rClass][a][s];
									numbersScen[r + s * nRiskFactorClasses] = nPopByRiskClassByAge[thisScen][year][rClass][a][s];
								}
						}

						double mean = applySuccesrateToMean(toByAveragedRef,
								toByAveragedScen, numbersRef, numbersScen,
								thisScen, year, a);

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

					if (riskType == 2 && !categorized) {

						writeCell(writer, rClass);
						// TODO vervangen door std risk factor

						writeCell(writer, rClass);

					}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
					 */
					if ((riskType == 2 && categorized) || riskType == 3) {

						double mean = 0;

						if (sex < 2) {

							mean = applySuccesrateToMean(
									meanRiskByRiskClassByAge[0][year][rClass][a][sex],
									meanRiskByRiskClassByAge[thisScen][year][rClass][a][sex],
									nPopByRiskClassByAge[0][year][rClass][a][sex],
									nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
									thisScen, year, a);

						} else {
							mean = applySuccesrateToMean(
									meanRiskByRiskClassByAge[0][year][rClass][a],
									meanRiskByRiskClassByAge[thisScen][year][rClass][a],
									nPopByRiskClassByAge[0][year][rClass][a],
									nPopByRiskClassByAge[thisScen][year][rClass][a],
									thisScen, year, a);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, a);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = applySuccesrate(
								nPopByRiskClassByAge[0][year][rClass][a][sex],
								nPopByRiskClassByAge[thisScen][year][rClass][a][sex],
								thisScen, year, a);

					} else {

						data = applySuccesrate(
								nPopByRiskClassByAge[0][year][rClass][a],
								nPopByRiskClassByAge[thisScen][year][rClass][a],
								thisScen, year, a);

					}
					writeCell(writer, data);
					/* write disease info */

					if (details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < nDiseaseStates + 3; col++) {
							if (sex < 2) {
								data = applySuccesrate(
										nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a][sex],
										nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
										thisScen, year, a);

							} else {

								data = applySuccesrate(
										nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][a],
										nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a],
										thisScen, year, a);

							}
							writeCell(writer, data);
						}

					} else { /*
							 * if details is false: then write the data of
							 * diseases
							 */
						/* make summary array */
						double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);

						for (int col = 4; col < nDiseases + 4; col++) {

							if (sex < 2) {
								data = applySuccesrate(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][a][sex],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex],
										thisScen, year, a);

							} else {

								data = applySuccesrate(
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
					+ startYear);
			writer.writeStartElement("Table");
			writer.writeStartElement("Row");
			/* write column headings */

			/* risk factor info */
			if (riskType == 1 || riskType == 3 || categorized) {
				writeCell(writer, "riskClass");
			} else {
				writeCell(writer, "mean_riskFactor");
				writeCell(writer, "std_riskFactor");
				writeCell(writer, "skewness");

			}
			if (riskType == 3) {

				writeCell(writer, "mean duration");

			}
			if (riskType == 2 && categorized) {

				writeCell(writer, "mean riskFactor");

			}

			/* age */
			writeCell(writer, "year");

			/* total number */
			writeCell(writer, "total number");

			/* disease info */
			if (details) {
				/*
				 * last disease state is survival and is equal to total numbers
				 * so not included
				 */
				for (int col = 4; col < nDiseaseStates + 3; col++) {
					writeCell(writer, stateNames[col - 4]);
					// TODO: goede naam laten printen

				}
			} else {
				for (int col = 4; col < nDiseases + 4; col++) {
					writeCell(writer, diseaseNames[col - 4]);
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
			for (int rClass = 0; rClass < nRiskFactorClasses; rClass++)

				for (int year = 0; year < nDim - cohort - 1; year++) {
					writer.writeStartElement("Row");
					/* write risk factor info */
					if (riskType == 1 || riskType == 3 || categorized) {

						writeCell(writer, riskClassnames[rClass]);

					}

					/*
					 * write the mean value of the continuous risk factor or the
					 * duration
					 */
					if (riskType == 2 && !categorized) {

						/*
						 * Calculate the average over all risk factor classes
						 * 
						 * / make arrays with the data needed to be averaged
						 */
						double[] toBeAveragedRef;
						double[] toBeAveragedScen;
						double[] numbersRef;
						double[] numbersScen;

						if (sex < 2) {
							toBeAveragedRef = new double[nRiskFactorClasses];
							toBeAveragedScen = new double[nRiskFactorClasses];
							numbersRef = new double[nRiskFactorClasses];
							numbersScen = new double[nRiskFactorClasses];
							for (int r = 0; r < nRiskFactorClasses; r++) {

								toBeAveragedRef[r] = meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
								toBeAveragedScen[r] = meanRiskByRiskClassByAge[thisScen][year][rClass][cohort][sex];
								numbersRef[r] = nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex];
								numbersScen[r] = nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex];
							}
						} else {
							toBeAveragedRef = new double[nRiskFactorClasses * 2];
							toBeAveragedScen = new double[nRiskFactorClasses * 2];
							numbersRef = new double[nRiskFactorClasses * 2];
							numbersScen = new double[nRiskFactorClasses * 2];
							for (int r = 0; r < nRiskFactorClasses; r++)
								for (int s = 0; s < 2; s++) {

									toBeAveragedRef[r + s * nRiskFactorClasses] = meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][s];
									toBeAveragedScen[r + s * nRiskFactorClasses] = meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][s];
									numbersRef[r + s * nRiskFactorClasses] = nPopByRiskClassByAge[0][year][rClass][cohort][s];
									numbersScen[r + s * nRiskFactorClasses] = nPopByRiskClassByAge[thisScen][year][rClass][cohort][s];
								}
						}

						double mean = applySuccesrateToMean(toBeAveragedRef,
								toBeAveragedScen, numbersRef, numbersScen,
								thisScen, 0, cohort);

						writeCell(writer, mean);
					}
					/* write the standard deviation of the continuous riskfactor */

					if (riskType == 2 && !categorized) {

						writeCell(writer, rClass);
						// TODO vervangen door std risk factor

						writeCell(writer, rClass);

					}

					/*
					 * write the mean value of the continuous riskfactor within
					 * a category of a riskfactor
					 */
					if ((riskType == 2 && categorized) || riskType == 3) {

						double mean = 0;

						if (sex < 2) {

							mean = applySuccesrateToMean(
									meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
									meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
									nPopByRiskClassByAge[0][year][rClass][cohort][sex],
									nPopByRiskClassByAge[thisScen][year][rClass][cohort][sex],
									thisScen, 0, cohort);

						} else {
							mean = applySuccesrateToMean(
									meanRiskByOriRiskClassByOriAge[0][year][rClass][cohort],
									meanRiskByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
									nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
									nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
									thisScen, 0, cohort);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, startYear + year);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = applySuccesrate(
								nPopByOriRiskClassByOriAge[0][year][rClass][cohort][sex],
								nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort][sex],
								thisScen, 0, cohort);

					} else {

						data = applySuccesrate(
								nPopByOriRiskClassByOriAge[0][year][rClass][cohort],
								nPopByOriRiskClassByOriAge[thisScen][year][rClass][cohort],
								thisScen, 0, cohort);

					}
					writeCell(writer, data);
					/* write disease info */

					if (details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < nDiseaseStates + 3; col++) {
							if (sex < 2) {
								data = applySuccesrate(
										nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
										nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
										thisScen, 0, cohort);

							} else {

								data = applySuccesrate(
										nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
										nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
										thisScen, 0, cohort);

							}
							writeCell(writer, data);
						}

					} else {
						/* if details is false: then write the data of diseases */
						for (int col = 4; col < nDiseases + 4; col++) {
							double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
							if (sex < 2) {
								data = applySuccesrate(
										nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort][sex],
										nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort][sex],
										thisScen, 0, cohort);

							} else {

								data = applySuccesrate(
										nDiseaseStateByOriRiskClassByOriAge[0][year][col - 4][rClass][cohort],
										nDiseaseStateByOriRiskClassByOriAge[thisScen][year][col - 4][rClass][cohort],
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
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied This should be zero for "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrate(double[] inputRef, double[] inputScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		int nToAdd = inputRef.length;
		if (thisScen == 0)
			for (int i = 0; i < nToAdd; i++)
				data += inputRef[i];
		else if (a >= year) /* if not newborns */{
			if (minAge[thisScen - 1] > a - year
					|| maxAge[thisScen - 1] < a - year)
				for (int i = 0; i < nToAdd; i++)
					data += inputRef[i];
			else
				for (int i = 0; i < nToAdd; i++)
					data += (1 - succesrate[thisScen - 1]) * inputRef[i]
							+ (succesrate[thisScen - 1]) * inputScen[i];
		} else /* if newborns */
		{
			if (minAge[thisScen - 1] > 0)
				for (int i = 0; i < nToAdd; i++)
					data += inputRef[i];
			else
				for (int i = 0; i < nToAdd; i++)
					data += (1 - succesrate[thisScen - 1]) * inputRef[i]
							+ (succesrate[thisScen - 1]) * inputScen[i];
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
	 *            maximum should be applied This should be zero for "ori" arrays
	 * @param a
	 *            : age: the current age of the cohort, or the age at start of
	 *            simulation (for "ori" arrays). In the last case, year above
	 *            should be zero
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * */

	private double applySuccesrate(double inputRef, double inputScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		if (thisScen == 0)
			data = inputRef;
		/* if not newborns */
		else if (a - year >= 0) {
			if (minAge[thisScen - 1] > a - year
					|| maxAge[thisScen - 1] < a - year)
				data = inputRef;
			else
				data = (1 - succesrate[thisScen - 1]) * inputRef
						+ (succesrate[thisScen - 1]) * inputScen;
		} else {
			if (minAge[thisScen - 1] > 0)
				data = inputRef;
			else
				data = (1 - succesrate[thisScen - 1]) * inputRef
						+ (succesrate[thisScen - 1]) * inputScen;
		}
		;
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
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToMean(double[] inputRef, double[] inputScen,
			double[] nInRef, double[] nInScen, int thisScen, int year, int a) {
		double data = 0.0;
		double denominator = 0;
		double numinator = 0;
		int nToAdd = inputRef.length;
		if (thisScen == 0)
			for (int i = 0; i < nToAdd; i++) {
				numinator += inputRef[i] * nInRef[i];
				denominator += nInRef[i];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (minAge[thisScen - 1] > a - year
					|| maxAge[thisScen - 1] < a - year)
				for (int i = 0; i < nToAdd; i++) {
					numinator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					numinator += (1 - succesrate[thisScen - 1]) * inputRef[i]
							* nInRef[i] + (succesrate[thisScen - 1])
							* inputScen[i] * nInScen[i];
					;
					denominator += (1 - succesrate[thisScen - 1]) * nInRef[i]
							+ (succesrate[thisScen - 1]) * nInScen[i];
				}
			}
		} else /* if newborns */
		{
			if (minAge[thisScen - 1] > 0)
				for (int i = 0; i < nToAdd; i++) {
					numinator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					numinator += (1 - succesrate[thisScen - 1]) * inputRef[i]
							* nInRef[i] + (succesrate[thisScen - 1])
							* inputScen[i] * nInScen[i];
					;
					denominator += (1 - succesrate[thisScen - 1]) * nInRef[i]
							+ (succesrate[thisScen - 1]) * nInScen[i];
				}
			}
		}
		;
		if (denominator != 0)
			data = numinator / denominator;
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
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToMean(double inputRef, double inputScen,
			double nInRef, double nInScen, int thisScen, int year, int a) {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;

		if (thisScen == 0) {

			nominator += inputRef * nInRef;
			denominator += nInRef;
		} else if (a - year >= 0)/* if not newborns */{
			if (minAge[thisScen - 1] > a - year
					|| maxAge[thisScen - 1] < a - year) {
				nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {

				nominator += (1 - succesrate[thisScen - 1]) * inputRef * nInRef
						+ (succesrate[thisScen - 1]) * inputScen * nInScen;
				;
				denominator += (1 - succesrate[thisScen - 1]) * nInRef
						+ (succesrate[thisScen - 1]) * nInScen;

			}
		} else { /* for newborns */
			if (minAge[thisScen - 1] > 0) {
				nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {

				nominator += (1 - succesrate[thisScen - 1]) * inputRef * nInRef
						+ (succesrate[thisScen - 1]) * inputScen * nInScen;
				;
				denominator += (1 - succesrate[thisScen - 1]) * nInRef
						+ (succesrate[thisScen - 1]) * nInScen;

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
	 * @param outcomeName
	 *            : string to be printed on the plot
	 * @param gender
	 *            : 0= for men; 1= for women; 2= for entire population
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) if true absolute numbers are plotted in stead of
	 *            percentage of starting population
	 * 
	 * @return: freechart plot
	 * @throws DynamoOutputException
	 *             when there are no persons in the population at step 0
	 */
	public JFreeChart makeSurvivalPlotByScenario(int gender,
			boolean differencePlot, boolean numbers) {
		XYDataset xyDataset = null;
		double[][][][] nPopByAge = getNPopByOriAge();
		int nDim2 = nPopByAge[0][0].length;
		for (int thisScen = 0; thisScen <= nScen; thisScen++) {
			XYSeries series = new XYSeries(scenarioNames[thisScen]);
			double dat0 = 0;
			double dat0r = 0;
			for (int steps = 0; steps < stepsInRun + 1; steps++) {
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
								thisScen, steps, age);
						indatr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, steps, age);
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
			label = " for men";
		else if (gender == 1)
			label = " for women";
		else
			label = " for both sexes";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle + label,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);

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
	 * makes a plot of the prevalence of disease d mby gender with year on the
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
	 * @throws DynamoOutputException
	 */
	public JFreeChart makeYearPrevalenceByGenderPlot(int thisScen, int d,
			boolean differencePlot, boolean numbers) {

		XYSeries menSeries = new XYSeries(diseaseNames[d]
				+ " prevalence in men");
		XYSeries womenSeries = new XYSeries(diseaseNames[d]
				+ " prevalence in women");
		XYSeries totalSeries = new XYSeries(diseaseNames[d]
				+ " overall prevalence");

		menSeries = new XYSeries("men");
		womenSeries = new XYSeries("women");
		totalSeries = new XYSeries("men+women");

		for (int steps = 0; steps < stepsInRun + 1; steps++) {
			double indat = 0;
			double indatr = 0;
			indat = calculateAveragePrevalence(thisScen, steps, d, 0, numbers);
			indatr = calculateAveragePrevalence(0, steps, d, 0, numbers);
			if (!differencePlot)
				menSeries.add((double) steps, indat);
			else
				menSeries.add((double) steps, indat - indatr);
			indat = calculateAveragePrevalence(thisScen, steps, d, 1, numbers);
			indatr = calculateAveragePrevalence(0, steps, d, 1, numbers);
			if (!differencePlot)
				womenSeries.add((double) steps, indat);
			else
				womenSeries.add((double) steps, indat - indatr);
			indat = calculateAveragePrevalence(thisScen, steps, d, 2, numbers);
			indatr = calculateAveragePrevalence(0, steps, d, 2, numbers);
			if (!differencePlot)
				totalSeries.add((double) steps, indat);
			else
				totalSeries.add((double) steps, indat - indatr);

		}
		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);
		((XYSeriesCollection) xyDataset).addSeries(totalSeries);
		JFreeChart chart;
		String chartTitle = "prevalence of " + diseaseNames[d]
				+ " in scenario: " + thisScen;
		if (numbers && differencePlot)
			chartTitle = "Excess number with " + diseaseNames[d]
					+ " in scenario: " + thisScen
					+ " compared to the ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "Excess prevalence of " + diseaseNames[d]
					+ " in scenario: " + thisScen
					+ " compared to the ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + diseaseNames[d]
					+ " in scenario: " + thisScen;

		String yTitle = "prevalence rate (%)" + diseaseNames[d];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + diseaseNames[d];
		if (!differencePlot && numbers)
			yTitle = "number with " + diseaseNames[d];
		if (differencePlot && numbers)
			yTitle = "excess number with " + diseaseNames[d];

		chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
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
	 * @return
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
		double[][][][][] nDiseaseByAge = getNDiseaseByAge();
		int nDim = nPopByAge[0][0].length;

		/*
		 * first calculate for men and women separately
		 */

		for (int age = 0; age < nDim; age++) {
			indat0 = applySuccesrate(nDiseaseByAge[0][year][d][age][0],
					nDiseaseByAge[thisScen][year][d][age][0], thisScen, year,
					age);
			npop0 = applySuccesrate(nPopByAge[0][year][age][0],
					nPopByAge[thisScen][year][age][0], thisScen, year, age);
			indat0r = nDiseaseByAge[0][year][d][age][0];
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
			indat1 = applySuccesrate(nDiseaseByAge[0][year][d][age][1],
					nDiseaseByAge[thisScen][year][d][age][1], thisScen, year,
					age);
			npop1 = applySuccesrate(nPopByAge[0][year][age][1],
					nPopByAge[thisScen][year][age][1], thisScen, year, age);
			indat1r = nDiseaseByAge[thisScen][year][d][age][1];
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
		String yTitle = "prevalence rate (%)" + diseaseNames[d];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + diseaseNames[d];
		if (!differencePlot && numbers)
			yTitle = "number with " + diseaseNames[d];
		if (differencePlot && numbers)
			yTitle = "excess number with " + diseaseNames[d];

		String chartTitle = "prevalence of " + diseaseNames[d]
				+ " in scenario: " + scenarioNames[thisScen];
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen]
					+ "compared to the ref scenario";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen]
					+ "compared to the ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen];
		;

		chart = ChartFactory.createXYLineChart(chartTitle, "age", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));

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
	 * @return
	 * 
	 */
	public JFreeChart makeAgePrevalenceByScenarioPlot(int gender, int d,
			int year, boolean differencePlot, boolean numbers) {

		XYSeries scenSeries[] = new XYSeries[nScen + 1];
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
		double[][][][][] nDiseaseByAge = getNDiseaseByAge();
		int nDim = nPopByAge[0][0].length;

		/*
		 * first calculate for men and women separately
		 */

		for (int thisScen = 0; thisScen < nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(scenarioNames[thisScen]);

			for (int age = 0; age < nDim; age++) {
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
							nDiseaseByAge[0][year][d][age][gender],
							nDiseaseByAge[thisScen][year][d][age][gender],
							thisScen, year, age);
					npop0 += applySuccesrate(nPopByAge[0][year][age][gender],
							nPopByAge[thisScen][year][age][gender], thisScen,
							year, age);
					indat0r += nDiseaseByAge[0][year][d][age][gender];
					npop0r += nPopByAge[0][year][age][gender];
				} else {
					indat0 += applySuccesrate(
							nDiseaseByAge[0][year][d][age][0],
							nDiseaseByAge[thisScen][year][d][age][0], thisScen,
							year, age);
					npop0 += applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age);
					indat1 += applySuccesrate(
							nDiseaseByAge[0][year][d][age][1],
							nDiseaseByAge[thisScen][year][d][age][1], thisScen,
							year, age);
					npop1 += applySuccesrate(nPopByAge[0][year][age][1],
							nPopByAge[thisScen][year][age][1], thisScen, year,
							age);
					indat0r += nDiseaseByAge[0][year][d][age][0];
					npop0r += nPopByAge[0][year][age][0];
					indat1r += nDiseaseByAge[0][year][d][age][1];
					npop1r += nPopByAge[0][year][age][1];

				}

				if (gender < 2) {
					if (npop0 != 0 && !differencePlot && !numbers)
						scenSeries[thisScen].add((double) age, 100 * indat0
								/ npop0);
					if (differencePlot && npop0 != 0 && npop0r != 0 && !numbers)
						scenSeries[thisScen].add((double) age, 100
								* (indat0 / npop0) - (indat0r / npop0r));
					if (npop0 != 0 && !differencePlot && numbers)
						scenSeries[thisScen].add((double) age, indat0);
					if (differencePlot && npop0 != 0 && npop0r != 0 && numbers)
						scenSeries[thisScen].add((double) age, (indat0)
								- (indat0r));
				} else {

					if ((npop0 + npop1) != 0 && !differencePlot && !numbers)

						scenSeries[thisScen].add((double) age,
								(indat0 + indat1) / (npop0 + npop1));
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
		String string = "";
		if (gender == 0)
			string = " in men";
		if (gender == 1)
			string = " in women";
		String chartTitle = "prevalence of " + diseaseNames[d] + string;
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + diseaseNames[d] + string
					+ " compared to the ref scenario";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + diseaseNames[d] + string
					+ " compared to the ref scenario";
		if (numbers && !differencePlot && gender == 0)
			chartTitle = "number of men with " + diseaseNames[d];
		if (numbers && !differencePlot && gender == 1)
			chartTitle = "number of women with " + diseaseNames[d];

		if (numbers && !differencePlot && gender == 2)
			chartTitle = "number of persons with " + diseaseNames[d];

		chart = ChartFactory.createXYLineChart(chartTitle, "age",
				"prevalence rate", xyDataset, PlotOrientation.VERTICAL, true,
				true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		return chart;
	}

	/**
	 * method plots the mean value of the riskFactor
	 * 
	 * @param thisScen
	 *            : scenario
	 * @throws DynamoOutputException
	 */
	public void makeMeanPlots(int gender) throws DynamoOutputException {
		double[][][][] nPopByAge = getNPopByAge();
		int nDim = nPopByAge[0][0].length;
		XYSeries dataSeries = null;

		XYDataset xyDataset = null;
		for (int thisScen = 0; thisScen < nScen; thisScen++) {
			for (int steps = 0; steps < stepsInRun + 1; steps++) {
				double meandat = 0;
				if (thisScen == 0) {
					dataSeries = new XYSeries("reference scenario");
				} else
					dataSeries = new XYSeries("scenario "
							+ scenarioNames[thisScen]);
				for (int age = 0; age < nDim; age++)

					meandat += applySuccesrateToMean(
							meanRiskByAge[0][steps][age][gender],
							meanRiskByAge[thisScen][steps][age][gender],
							nPopByAge[0][steps][age][gender],
							nPopByAge[thisScen][steps][age][gender], thisScen,
							steps, age);

				dataSeries.add((double) steps, meandat);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(dataSeries);
			else
				((XYSeriesCollection) xyDataset).addSeries(dataSeries);
		}
		JFreeChart chart;

		chart = ChartFactory.createXYLineChart(
				"mean value of riskfactor for sex=" + gender,
				"years of simulation", "prevalence rate", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
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
	 * @param d
	 *            : disease number
	 * @param steps
	 * @return: prevalence averaged over all age groups
	 * 
	 */
	private double calculateAveragePrevalence(int thisScen, int steps, int d,
			int gender, boolean numbers) {
		double indat = 0;
		double npop = 0;
		double[][][][] nPopByAge = getNPopByAge();
		double[][][][][] nDiseaseByAge = getNDiseaseByAge();
		int nDim = nPopByAge[0][0].length;
		indat = 0;
		npop = 0;
		if (gender < 2) {
			for (int age = 0; age < nDim; age++) {
				indat += applySuccesrate(
						nDiseaseByAge[0][steps][d][age][gender],
						nDiseaseByAge[thisScen][steps][d][age][gender],
						thisScen, steps, age);
				npop += applySuccesrate(nPopByAge[0][steps][age][gender],
						nPopByAge[thisScen][steps][age][gender], thisScen,
						steps, age);

			}
		} else {
			for (int age = 0; age < nDim; age++) {
				indat += applySuccesrate(nDiseaseByAge[0][steps][d][age][0],
						nDiseaseByAge[thisScen][steps][d][age][0], thisScen,
						steps, age)
						+ applySuccesrate(nDiseaseByAge[0][steps][d][age][1],
								nDiseaseByAge[thisScen][steps][d][age][1],
								thisScen, steps, age);
				;
				npop += applySuccesrate(nPopByAge[0][steps][age][0],
						nPopByAge[thisScen][steps][age][0], thisScen, steps,
						age)
						+ applySuccesrate(nPopByAge[0][steps][age][1],
								nPopByAge[thisScen][steps][age][1], thisScen,
								steps, age);
			}

		}
		if (npop > 0 && !numbers)
			indat = 100 * indat / npop;

		if (npop == 0)
			indat = 0;

		return indat;
	}

	/**
	 * method that averages the prevalence over all ages
	 * 
	 * @param thisScen
	 * @param d
	 *            : disease number
	 * @param steps
	 * @param numbers
	 *            (boolean) indicates whether prevalences should be returned or
	 *            absolute numbers
	 * @return: prevalence IN PERCENT averaged over all age groups, or absolute
	 *          numbers
	 */
	private double calculateAveragePrevalenceByRiskClass(int thisScen, int d,
			int r, int steps, int gender, boolean numbers) {
		double indat;
		double npop;
		double[][][][] nPopByAge = getNPopByAge();
		int nDim = nPopByAge[0][0].length;
		indat = 0;
		npop = 0;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);

		if (gender < 2) {
			for (int age = 0; age < nDim; age++) {
				indat += applySuccesrate(
						nDiseaseByRiskClassByAge[0][steps][d][r][age][gender],
						nDiseaseByRiskClassByAge[thisScen][steps][d][r][age][gender],
						thisScen, steps, age);
				npop += applySuccesrate(
						nPopByRiskClassByAge[0][steps][r][age][gender],
						nPopByRiskClassByAge[thisScen][steps][r][age][gender],
						thisScen, steps, age);
			}
		} else {
			for (int age = 0; age < nDim; age++) {
				indat += applySuccesrate(
						nDiseaseByRiskClassByAge[0][steps][d][r][age][0],
						nDiseaseByRiskClassByAge[thisScen][steps][d][r][age][0],
						thisScen, steps, age)
						+ applySuccesrate(
								nDiseaseByRiskClassByAge[0][steps][d][r][age][1],
								nDiseaseByRiskClassByAge[thisScen][steps][d][r][age][1],
								thisScen, steps, age);
				;
				npop += applySuccesrate(
						nPopByRiskClassByAge[0][steps][r][age][0],
						nPopByRiskClassByAge[thisScen][steps][r][age][0],
						thisScen, steps, age)
						+ applySuccesrate(
								nPopByRiskClassByAge[0][steps][r][age][1],
								nPopByRiskClassByAge[thisScen][steps][r][age][1],
								thisScen, steps, age);
			}

		}
		if (npop > 0 && !numbers)
			indat = 100 * indat / npop;
		else if (npop == 0)
			indat = 0;
		/*
		 * else throw new DynamoOutputException(
		 * "zero persons in initial population for risk factor class: " +r+
		 * "  no disease " + "prevalence can be calculated ");
		 */
		return indat;
	}

	/**
	 * makes prevalence plot by riskfactor for scenario thisScen
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
	public JFreeChart makeYearPrevalenceByRiskFactorPlots(int gender,int thisScen, int d,
			 boolean differencePlot, boolean numbers) {
		XYDataset xyDataset = null;

		for (int r = 0; r < nRiskFactorClasses; r++) {
			XYSeries series = null;
			if (gender == 0)
				series = new XYSeries(riskClassnames[r] + ", men");
			if (gender == 1)
				series = new XYSeries(riskClassnames[r] + ", women");
			if (gender == 2)
				series = new XYSeries(riskClassnames[r]);
			for (int steps = 0; steps < stepsInRun + 1; steps++) {
				/*
				 * calculateAveragePrevalenceByRiskClass already multiplies the
				 * prevalence rate with 100 to get percentages
				 */
				double indat0 = calculateAveragePrevalenceByRiskClass(thisScen,
						d, r, steps, gender, numbers);
				double refdat0 = calculateAveragePrevalenceByRiskClass(0, d, r,
						steps, gender, numbers);
				if (!differencePlot && indat0 != 0)
					series.add((double) steps, indat0);
				if (differencePlot && indat0 != 0)
					series.add((double) steps, (indat0 - refdat0));

			}
			if (r == 0)
				xyDataset = new XYSeriesCollection(series);

			if (r > 0)
				((XYSeriesCollection) xyDataset).addSeries(series);

		}
		String chartTitle = "prevalence of " + diseaseNames[d]
				+ " in scenario: " + scenarioNames[thisScen];
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen];
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen];
		if (numbers && !differencePlot && gender == 0)
			chartTitle = "number of men with " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen];
		if (numbers && !differencePlot && gender == 1)
			chartTitle = "number of women with " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen];
		if (numbers && !differencePlot && gender == 2)
			chartTitle = "number of persons with " + diseaseNames[d]
					+ " in scenario: " + scenarioNames[thisScen];
		String yTitle = "prevalence rate (%)" + diseaseNames[d];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + diseaseNames[d];
		if (!differencePlot && numbers)
			yTitle = "number with " + diseaseNames[d];
		if (differencePlot && numbers)
			yTitle = "excess number with " + diseaseNames[d];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
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
	 * @return
	 * 
	 */
	public JFreeChart makeYearPrevalenceByScenarioPlots(int gender, int d,
			boolean differencePlot, boolean numbers)
	/* throws DynamoOutputException */{
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[nScen + 1];
		for (int thisScen = 0; thisScen < nScen + 1; thisScen++) {
			scenSeries[thisScen] = new XYSeries(scenarioNames[thisScen]);
			for (int steps = 0; steps < stepsInRun + 1; steps++) {
				double indat = 0;
				double npop = 0;
				double indatr = 0;
				double npopr = 0;
				if (gender < 2) {
					for (int r = 0; r < nRiskFactorClasses; r++) {
						for (int age = 0; age < nDim; age++) {
							indat += applySuccesrate(
									nDiseaseByRiskClassByAge[0][steps][d][r][age][gender],
									nDiseaseByRiskClassByAge[thisScen][steps][d][r][age][gender],
									thisScen, steps, age);
							npop += applySuccesrate(
									nPopByRiskClassByAge[0][steps][r][age][gender],
									nPopByRiskClassByAge[thisScen][steps][r][age][gender],
									thisScen, steps, age);
							indatr += nDiseaseByRiskClassByAge[0][steps][d][r][age][gender];
							npopr += nPopByRiskClassByAge[0][steps][r][age][gender];
						}
					}
				} else {
					for (int r = 0; r < nRiskFactorClasses; r++) {

						for (int age = 0; age < nDim; age++) {
							indat += applySuccesrate(
									nDiseaseByRiskClassByAge[0][steps][d][r][age][0],
									nDiseaseByRiskClassByAge[thisScen][steps][d][r][age][0],
									thisScen, steps, age)
									+ applySuccesrate(
											nDiseaseByRiskClassByAge[0][steps][d][r][age][1],
											nDiseaseByRiskClassByAge[thisScen][steps][d][r][age][1],
											thisScen, steps, age);
							;
							npop += applySuccesrate(
									nPopByRiskClassByAge[0][steps][r][age][0],
									nPopByRiskClassByAge[thisScen][steps][r][age][0],
									thisScen, steps, age)
									+ applySuccesrate(
											nPopByRiskClassByAge[0][steps][r][age][1],
											nPopByRiskClassByAge[thisScen][steps][r][age][1],
											thisScen, steps, age);

							indatr += nDiseaseByRiskClassByAge[0][steps][d][r][age][0]
									+ nDiseaseByRiskClassByAge[0][steps][d][r][age][1];
							npopr += nPopByRiskClassByAge[0][steps][r][age][0]
									+ nPopByRiskClassByAge[0][steps][r][age][1];
						}

					}
				}
				if (npop != 0 && !differencePlot && !numbers) {
					indat = 100 * indat / npop;
					scenSeries[thisScen].add((double) steps, indat);
				}
				if (npop != 0 && npopr != 0 && differencePlot && !numbers) {
					indat = (indat / npop) - (indatr / npopr);
					scenSeries[thisScen].add((double) steps, 100 * indat);
				}
				if (npop != 0 && !differencePlot && numbers) {

					scenSeries[thisScen].add((double) steps, indat);
				}
				if (npop != 0 && npopr != 0 && differencePlot && numbers) {
					indat = (indat) - (indatr);
					scenSeries[thisScen].add((double) steps, indat);
				}
			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}

		String chartTitle = "prevalence of " + diseaseNames[d];
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + diseaseNames[d]
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + diseaseNames[d]
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + diseaseNames[d];
		String yTitle = "prevalence rate (%) " + diseaseNames[d];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + diseaseNames[d];
		if (!differencePlot && numbers)
			yTitle = "number with " + diseaseNames[d];
		if (differencePlot && numbers)
			yTitle = "excess number with " + diseaseNames[d];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
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
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);

		for (int r = 0; r < nRiskFactorClasses; r++) {

			XYSeries menSeries = new XYSeries(" men, " + riskClassnames[r]);
			XYSeries womenSeries = new XYSeries(" women, " + riskClassnames[r]);
			XYSeries totSeries = new XYSeries(riskClassnames[r]);
			double mendat = 0;
			double womendat = 0;
			double menpop = 0;
			double womenpop = 0;
			double mendatr = 0;
			double womendatr = 0;
			double menpopr = 0;
			double womenpopr = 0;
			for (int age = 0; age < nDim; age++) {
				mendat = applySuccesrate(
						nDiseaseByRiskClassByAge[0][year][d][r][age][0],
						nDiseaseByRiskClassByAge[thisScen][year][d][r][age][0],
						thisScen, year, age);
				menpop = applySuccesrate(
						nPopByRiskClassByAge[0][year][r][age][0],
						nPopByRiskClassByAge[thisScen][year][r][age][0],
						thisScen, year, age);
				mendatr = nDiseaseByRiskClassByAge[0][year][d][r][age][0];
				menpopr = nPopByRiskClassByAge[0][year][r][age][0];
				if (menpop != 0 && !differencePlot)
					menSeries.add((double) age, 100 * mendat / menpop);
				if (menpop != 0 && menpopr != 0 && differencePlot)
					menSeries.add((double) age, 100 * (mendat / menpop) - 100
							* (mendatr / menpopr));
				womendat = applySuccesrate(
						nDiseaseByRiskClassByAge[0][year][d][r][age][1],
						nDiseaseByRiskClassByAge[thisScen][year][d][r][age][1],
						thisScen, year, age);
				womenpop = applySuccesrate(
						nPopByRiskClassByAge[0][year][r][age][1],
						nPopByRiskClassByAge[thisScen][year][r][age][1],
						thisScen, year, age);
				womendatr = nDiseaseByRiskClassByAge[0][year][d][r][age][1];
				womenpopr = nPopByRiskClassByAge[0][year][r][age][1];
				if (womenpop != 0 && !numbers)
					womenSeries.add((double) age, 100 * womendat / womenpop);
				if (womenpop != 0 && womenpopr != 0 && differencePlot
						&& !numbers)
					womenSeries.add((double) age, (100 * womendat / womenpop)
							- (100 * womendatr / womenpopr));
				if ((menpop + womenpop) != 0 && !differencePlot && !numbers)
					totSeries.add((double) age, 100 * (womendat + mendat)
							/ (menpop + womenpop));
				if ((menpop + womenpop) != 0 && (menpopr + womenpopr) != 0
						&& differencePlot && !numbers)
					totSeries.add((double) age, 100 * (womendat + mendat)
							/ (menpop + womenpop) - 100 * (womendatr + mendatr)
							/ (menpopr + womenpopr));
				if (womenpop != 0 && numbers)
					womenSeries.add((double) age, womendat);
				if (womenpop != 0 && womenpopr != 0 && differencePlot
						&& numbers)
					womenSeries.add((double) age, (womendat - womendatr));
				if ((menpop + womenpop) != 0 && !differencePlot && numbers)
					totSeries.add((double) age, (womendat + mendat));
				if ((menpop + womenpop) != 0 && (menpopr + womenpopr) != 0
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
		String label = "";
		if (gender==0) label=" in men";

		if (gender==1) label=" in women";
		String chartTitle = "prevalence of " + diseaseNames[d]
				+label+ " in scenario " + scenarioNames[thisScen];
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + diseaseNames[d]
					+ label+" in scenario " + scenarioNames[thisScen];
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + diseaseNames[d]
					+ label+" in scenario " + scenarioNames[thisScen];
		if (numbers && !differencePlot) if (gender==0)
			chartTitle = "number of men with " + diseaseNames[d]
					+ " in scenario " + scenarioNames[thisScen];
		else if (gender==1)chartTitle = "number of women with " + diseaseNames[d]
		                                                     					+ " in scenario " + scenarioNames[thisScen];
		else chartTitle = "number of persons with " + diseaseNames[d]
		                                  					+ " in scenario " + scenarioNames[thisScen];
		String yTitle = "prevalence rate (%) " + diseaseNames[d];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + diseaseNames[d];
		if (!differencePlot && numbers)
			yTitle = "number with " + diseaseNames[d];
		if (differencePlot && numbers)
			yTitle = "excess number with " + diseaseNames[d];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "age",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
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
	 * print riskfactorclass data for a single scenario separate for men and
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
	 * @return
	 * @throws DynamoOutputException
	 */
	public JFreeChart makeYearRiskFactorByScenarioPlot(int gender,
			int riskClass, boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[nScen + 1];
		double[][][][] nPopByAge = getNPopByAge();
		for (int thisScen = 0; thisScen < nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(scenarioNames[thisScen]);

			for (int steps = 0; steps < stepsInRun + 1; steps++) {
				double indat = 0;
				double denominator = 0;
				double indatr = 0;
				double denominatorr = 0;
				int nDim = nPopByAge[0][0].length;
				for (int age = 0; age < nDim; age++) {
					if (gender < 2) {
						indat += applySuccesrate(
								nPopByRiskClassByAge[0][steps][riskClass][age][gender],
								nPopByRiskClassByAge[thisScen][steps][riskClass][age][gender],
								thisScen, steps, age);
						denominator += applySuccesrate(
								nPopByAge[0][steps][age][gender],
								nPopByAge[thisScen][steps][age][gender],
								thisScen, steps, age);
						indatr += nPopByRiskClassByAge[0][steps][riskClass][age][gender];
						denominatorr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(
								nPopByRiskClassByAge[0][steps][riskClass][age][0],
								nPopByRiskClassByAge[thisScen][steps][riskClass][age][0],
								thisScen, steps, age)
								+ applySuccesrate(
										nPopByRiskClassByAge[0][steps][riskClass][age][1],
										nPopByRiskClassByAge[thisScen][steps][riskClass][age][1],
										thisScen, steps, age);
						;
						denominator += applySuccesrate(
								nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, steps, age);
						indatr += nPopByRiskClassByAge[0][steps][riskClass][age][0]
								+ nPopByRiskClassByAge[0][steps][riskClass][age][1];
						denominatorr += nPopByAge[0][steps][age][0]
								+ nPopByAge[0][steps][age][1];
					}

				}
				if (denominator != 0 && !numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps, indat
							/ denominator);
				if (denominator != 0 && denominatorr != 0 && !numbers
						&& differencePlot)
					scenSeries[thisScen].add((double) steps,
							(indat / denominator) - (indatr / denominatorr));
				if (denominator != 0 && numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps, indat);
				if (denominator != 0 && denominatorr != 0 && numbers
						&& differencePlot)
					scenSeries[thisScen].add((double) steps, indat - indatr);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}

		String chartTitle = "prevalence of " + riskClassnames[riskClass];
		if (numbers && differencePlot)
			chartTitle = "excess number of " + riskClassnames[riskClass]
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + riskClassnames[riskClass]
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of " + riskClassnames[riskClass];
		String yTitle = "prevalence rate (%) " + riskClassnames[riskClass];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + riskClassnames[riskClass];
		if (!differencePlot && numbers)
			yTitle = "number with " + riskClassnames[riskClass];
		if (differencePlot && numbers)
			yTitle = "excess number of " + riskClassnames[riskClass];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
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
	 * print riskfactorclass data for a single scenario separate for men and
	 * women
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            : riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @return
	 * @throws DynamoOutputException
	 */
	public JFreeChart makeAgeRiskFactorByScenarioPlot(int year, int gender,
			int riskClass, boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[nScen + 1];
		double[][][][] nPopByAge = getNPopByAge();
		for (int thisScen = 0; thisScen < nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(scenarioNames[thisScen]);

			double indat = 0;
			double denominator = 0;
			double indatR = 0;
			double denominatorR = 0;
			int nDim = nPopByAge[0][0].length;
			for (int age = 0; age < nDim; age++) {
				if (gender < 2) {
					indat = applySuccesrate(
							nPopByRiskClassByAge[0][year][riskClass][age][gender],
							nPopByRiskClassByAge[thisScen][year][riskClass][age][gender],
							thisScen, year, age);
					denominator = applySuccesrate(
							nPopByAge[0][year][age][gender],
							nPopByAge[thisScen][year][age][gender], thisScen,
							year, age);
					indatR = nPopByRiskClassByAge[0][year][riskClass][age][gender];
					denominatorR = nPopByAge[0][year][age][gender];
				} else {
					indat = applySuccesrate(
							nPopByRiskClassByAge[0][year][riskClass][age][0],
							nPopByRiskClassByAge[thisScen][year][riskClass][age][0],
							thisScen, year, age)
							+ applySuccesrate(
									nPopByRiskClassByAge[0][year][riskClass][age][1],
									nPopByRiskClassByAge[thisScen][year][riskClass][age][1],
									thisScen, year, age);
					;
					denominator = applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age)
							+ applySuccesrate(nPopByAge[0][year][age][1],
									nPopByAge[thisScen][year][age][1],
									thisScen, year, age);
					indatR = nPopByRiskClassByAge[0][year][riskClass][age][0]
							+ nPopByRiskClassByAge[0][year][riskClass][age][1];
					denominatorR = nPopByAge[0][year][age][0]
							+ nPopByAge[0][year][age][1];
				}

				if (denominator != 0 && !numbers && !differencePlot)
					scenSeries[thisScen].add((double) age, indat / denominator);
				if (denominator != 0 && denominatorR != 0 && !numbers
						&& differencePlot)
					scenSeries[thisScen].add((double) age,
							(indat / denominator) - (indatR / denominatorR));
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
		String label = "";
		if (gender == 0)
			label = " (men)";
		if (gender == 1)
			label = " (women)";

		String chartTitle = "prevalence of " + riskClassnames[riskClass]
				+ " in " + (startYear + year);
		if (numbers && differencePlot)
			chartTitle = "excess number of " + riskClassnames[riskClass]
					+ " compared to ref scenario" + " in " + (startYear + year);
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of " + riskClassnames[riskClass]
					+ " compared to ref scenario" + " in " + (startYear + year);
		if (numbers && !differencePlot)
			chartTitle = "number of " + riskClassnames[riskClass] + " in "
					+ (startYear + year);
		chartTitle = chartTitle + label;
		String yTitle = "prevalence rate (%) " + riskClassnames[riskClass];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + riskClassnames[riskClass];
		if (!differencePlot && numbers)
			yTitle = "number with " + riskClassnames[riskClass];
		if (differencePlot && numbers)
			yTitle = "excess number of " + riskClassnames[riskClass];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "age",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
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

	// lklk;

	public JFreeChart makeYearMortalityPlotByScenario(int gender,
			boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		double[][][][] mortality = makeMortalityArray(true);/*
															 * get number of
															 * persons who died
															 * during this year
															 */
		double[][][][] nPopByAge = getNPopByAge();

		XYSeries scenSeries[] = new XYSeries[nScen + 1];

		for (int thisScen = 0; thisScen < nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(scenarioNames[thisScen]);
			/*
			 * mortality is calculated from the difference between the previous
			 * year and the current year therefor there is one less datapoint
			 * for mortality than for most other outcomes
			 */
			for (int steps = 0; steps < stepsInRun; steps++) {
				double indat0 = 0;
				double denominator0 = 0;
				double indat1 = 0;
				double denominator1 = 0;
				double indat0r = 0;
				double denominator0r = 0;
				double indat1r = 0;
				double denominator1r = 0;

				for (int age = 0; age < nDim; age++) {
					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */

					if (mortality[0][steps][age][0] >= 0
							&& mortality[thisScen][steps][age][0] >= 0) {

						indat0 += applySuccesrate(mortality[0][steps][age][0],
								mortality[thisScen][steps][age][0], thisScen,
								steps, age);

						denominator0 += applySuccesrate(
								nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age);

						indat0r += mortality[0][steps][age][0];
						denominator0r += nPopByAge[0][steps][age][0];
					}
					if (mortality[0][steps][age][1] >= 0
							&& mortality[thisScen][steps][age][1] >= 0)
						denominator1 = 0;

					{
						indat1 += applySuccesrate(mortality[0][steps][age][1],
								mortality[thisScen][steps][age][1], thisScen,
								steps, age);
						denominator1 += applySuccesrate(
								nPopByAge[0][steps][age][1],
								nPopByAge[thisScen][steps][age][1], thisScen,
								steps, age);
					}
					indat1r += mortality[0][steps][age][1];
					denominator1r += nPopByAge[0][steps][age][1];

				}

				if (gender == 0 && denominator0 != 0 && !numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps, indat0
							/ denominator0);
				if (gender == 1 && denominator1 != 0 && !numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps, indat1
							/ denominator1);
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& !numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps, (indat0 + indat1)
							/ (denominator0 + denominator1));
				if (gender == 0 && denominator0 != 0 && numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps, indat0);
				if (gender == 1 && denominator1 != 0 && numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) steps, indat1);
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& numbers && !differencePlot)
					scenSeries[thisScen].add((double) steps, (indat0 + indat1));

				/*
				 * repeat for difference plots
				 */
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
			label = " for men";
		else if (gender == 1)
			label = " for women";
		else
			label = " for both sexes";

		String chartTitle = "mortality";
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
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);

		return chart;

	}

	public JFreeChart makeAgeMortalityPlotByScenario(int year, int gender,
			boolean differencePlot, boolean numbers) {

		XYDataset xyDataset = null;
		double[][][][] mortality = makeMortalityArray(true);/*
															 * get number of
															 * persons who died
															 * during this year
															 */
		double[][][][] nPopByAge = getNPopByAge();

		XYSeries scenSeries[] = new XYSeries[nScen + 1];
		double indat0 = 0;
		double denominator0 = 0;
		double indat1 = 0;
		double denominator1 = 0;
		double indat0r = 0;
		double denominator0r = 0;
		double indat1r = 0;
		double denominator1r = 0;
		for (int thisScen = 0; thisScen < nScen + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(scenarioNames[thisScen]);
			/*
			 * mortality is calculated from the difference between the previous
			 * year and the current year therefor there is one less datapoint
			 * for mortality than for most other outcomes
			 */

			for (int age = 0; age < nDim; age++) {
				/*
				 * check if mortality is present (next age in dataset)
				 * mortality=-1 flags absence
				 */
				if (mortality[0][year][age][0] >= 0
						&& mortality[thisScen][year][age][0] >= 0) {

					indat0 = applySuccesrate(mortality[0][year][age][0],
							mortality[thisScen][year][age][0], thisScen, year,
							age);
					denominator0 = applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age);

					indat0r = mortality[0][year][age][0];
					denominator0r = nPopByAge[0][year][age][0];
				}
				if (mortality[0][year][age][1] >= 0
						&& mortality[thisScen][year][age][1] >= 0)

				{
					indat1 = applySuccesrate(mortality[0][year][age][1],
							mortality[thisScen][year][age][1], thisScen, year,
							age);
					denominator1 = applySuccesrate(nPopByAge[0][year][age][1],
							nPopByAge[thisScen][year][age][1], thisScen, year,
							age);
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
					scenSeries[thisScen].add((double) age, (indat0 + indat1)
							/ (denominator0 + denominator1));
				if (gender == 0 && denominator0 != 0 && numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) age, indat0);
				if (gender == 1 && denominator1 != 0 && numbers
						&& !differencePlot)
					scenSeries[thisScen].add((double) age, indat1);
				if (gender == 2 && (denominator0 + denominator1) != 0
						&& numbers && !differencePlot)
					scenSeries[thisScen].add((double) age, (indat0 + indat1));

				/*
				 * repeat for difference plots
				 */
				if (gender == 0 && denominator0 != 0 && denominator0r != 0
						&& !numbers && differencePlot)
					scenSeries[thisScen]
							.add((double) age, (indat0 / denominator0)
									- (indat0r / denominator0r));
				if (gender == 1 && denominator1 != 0 && denominator1r != 0
						&& !numbers && differencePlot)
					scenSeries[thisScen]
							.add((double) age, (indat1 / denominator1)
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
					scenSeries[thisScen].add((double) age, indat0 - indat0r);
				if (gender == 1 && denominator1 != 0 && denominator1r != 0
						&& numbers && differencePlot)
					scenSeries[thisScen].add((double) age, indat1 - indat1r);
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
			label = " for men";
		else if (gender == 1)
			label = " for women";
		else
			label = " for both sexes";
		String label2 = " in " + (startYear + year);
		String chartTitle = "mortality";
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

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle + label
				+ label2, "age", yTitle, xyDataset, PlotOrientation.VERTICAL,
				true, true, false);

		return chart;

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
	 * 
	 */
	public JFreeChart makeLifeExpectancyPlot(int age) {

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
		double[][] lifeExp = new double[nScen + 1][2];
		double baselinePop = 0;
		double[][][][] nPopByAge = getNPopByOriAge();
		int yearsLeft = nDim - age;
		for (int scenario = 0; scenario < nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				baselinePop = 0;
				for (int steps = 0; steps < yearsLeft; steps++) {
					lifeExp[scenario][s] += applySuccesrate(
							nPopByAge[0][steps][age][s],
							nPopByAge[scenario][steps][age][s], scenario, 0,
							age);
					if (steps == 0)
						baselinePop += applySuccesrate(
								nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age);
				}

				if (baselinePop != 0)
					lifeExp[scenario][s] = lifeExp[scenario][s] / baselinePop;
				else
					lifeExp[scenario][s] = 0;

			}
		String[] gender = { "male", "female" };
		String[] legend = new String[nScen + 1];
		legend[0] = "ref. scenario";
		for (int scen = 1; scen < nScen + 1; scen++)
			legend[scen] = "scenario " + scen;
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				legend, gender, lifeExp);
		String chartTitle = ("LifeExpectancy");
		if (age == 0)
			chartTitle = chartTitle + " at age " + age;
		else
			chartTitle = chartTitle + (" at birth");
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, "", "years",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		// ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart);
		Plot plot = chart.getPlot();
		/* assign a generator to a CategoryItemRenderer, */
		CategoryItemRenderer renderer = ((CategoryPlot) plot).getRenderer();
		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);
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
	 * the method produces a bargraph for life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a longitudinal
	 * life-expectancy. It only gives proper results when the stepsize used in
	 * the simulation is high enough. In Dynamo this is set at age 105 (105-age
	 * when not life expectancy at birth); This means that everyone who survives
	 * until age 105 is expected to die at age 105.
	 * 
	 * @param age
	 *            : age at which the life expectancy is calculated
	 * 
	 */
	public JFreeChart makeHealthyLifeExpectancyPlot(int age, int disease) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		String[] genderLabel = { "men", "women" };
		double[][] lifeExp = new double[(nScen + 1)][2];
		double[][] withDiseaseExp = new double[(nScen + 1)][2];
		double baselinePop = 0;
		double[][][][] nPopByAge = getNPopByOriAge();

		double[][][] diseased;
		if (disease < 0)
			diseased = getNumberOfOriDiseasedPersons(age);
		else
			diseased = getNDiseaseByOriAge(age, disease);

		int scenGenderCombi = 0;
		int yearsLeft = nDim - age;
		for (int scenario = 0; scenario < nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {

				baselinePop = 0;
				scenGenderCombi = s * (nScen + 1) + scenario + s;
				for (int steps = 0; steps < yearsLeft; steps++) {
					lifeExp[scenario][s] += applySuccesrate(
							nPopByAge[0][steps][age][s],
							nPopByAge[scenario][steps][age][s], scenario, 0,
							age);
					withDiseaseExp[scenario][s] += applySuccesrate(
							diseased[0][steps][s],
							diseased[scenario][steps][s], scenario, 0, age);
					if (steps == 0)
						baselinePop += applySuccesrate(
								nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age);

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
							scenarioNames[scenario] + "(healthy)",
							genderLabel[s]);
					dataset.addValue(withDiseaseExp[scenario][s],
							scenarioNames[scenario] + "(withDisease)",
							genderLabel[s]);
				}
			}

		String[] legend = new String[2];
		legend[0] = "healthy";
		if (disease < 0)
			legend[1] = "with disease";
		else
			legend[1] = "with " + diseaseNames[disease];

		String chartTitle = ("LifeExpectancy with and without ");
		if (disease < 0)
			chartTitle = chartTitle + "disease";
		else
			chartTitle = chartTitle + diseaseNames[disease];
		if (age == 0)
			chartTitle = chartTitle + " at age " + age;
		else
			chartTitle = chartTitle + (" at birth");
		JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle, "",
				"years", dataset, PlotOrientation.VERTICAL, true, true, false);

		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		KeyToGroupMap map = new KeyToGroupMap(scenarioNames[0]);
		map.mapKeyToGroup("healthy", scenarioNames[0]);
		map.mapKeyToGroup("with disease", scenarioNames[0]);

		for (int scenario = 1; scenario < nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				map.mapKeyToGroup(scenarioNames[scenario] + "(healthy)",
						scenarioNames[scenario]);
				map.mapKeyToGroup(scenarioNames[scenario] + "(withDisease)",
						scenarioNames[scenario]);
			}
		renderer.setSeriesToGroupMap(map);

		SubCategoryAxis domainAxis = new SubCategoryAxis("");
		domainAxis.setCategoryMargin(0.2); // gap between men and women: does
											// not work
		for (int scenario = 0; scenario < nScen + 1; scenario++)
			domainAxis.addSubCategory(scenarioNames[scenario]);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setDomainAxis(domainAxis);
		// plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
		renderer.setItemMargin(0.15); // between the scenarios;
		int currentSeries = 0;
		for (int scenario = 0; scenario < nScen + 1; scenario++)
			for (int s = 0; s < 2; s++) {
				renderer.setSeriesPaint(currentSeries, Color.pink);
				renderer.setSeriesVisibleInLegend(currentSeries, false);
				currentSeries++;
				renderer.setSeriesPaint(currentSeries, Color.red);
				renderer.setSeriesVisibleInLegend(currentSeries, false);
				currentSeries++;

			}
		renderer.setSeriesVisibleInLegend(0, true);
		renderer.setSeriesVisibleInLegend(1, true);

		plot.setRenderer(renderer);
		// plot.setFixedLegendItems(makeLegend(disease));

		CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));
		StandardCategoryItemLabelGenerator generator2 = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));

		/*
		 * assign a generator to a CategoryItemRenderer, in order to show the
		 * numbers
		 */
		CategoryItemRenderer renderer2 = ((CategoryPlot) plot).getRenderer();

		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		return chart;
	}

	private LegendItemCollection makeLegend(int disease) {

		LegendItemCollection legend = new LegendItemCollection();
		LegendItem item1 = new LegendItem("healthy");

		legend.add(item1);
		Paint paint1 = item1.getFillPaint();

		LegendItem item2;

		if (disease >= 0)
			item2 = new LegendItem("with " + diseaseNames[disease]);
		else
			item2 = new LegendItem("with disease");
		legend.add(item2);

		return legend;
	}

	/**
	 * method makePyramidChart makes a population pyramid chart for scenario
	 * "thisScen" compared to the reference scenario and year "timestep"
	 * 
	 * @param thisScen
	 *            : number of the scenario
	 * @param timestep
	 * @return
	 */
	public JFreeChart makePyramidChart(int thisScen, int timestep) {

		double[][] pyramidData1 = new double[2][100];
		double[][] pyramidData2 = new double[2][100];
		double[][] nPopByAge = new double[100][2];
		double[][] nRefPopByAge = new double[100][2];
		if (scaleRange == 0) {
			/* do only once */
			int maxPopulationSize = getMaxPop();
			scaleRange = 10000 * Math.ceil(maxPopulationSize * 1.1 / 10000);
		}
		for (int a = 0; a < 100; a++) {
			for (int r = 0; r < nRiskFactorClasses; r++) {
				nPopByAge[a][0] += applySuccesrate(
						nPopByRiskClassByAge[0][timestep][r][a][0],
						nPopByRiskClassByAge[thisScen][timestep][r][a][0],
						thisScen, timestep, a);
				nPopByAge[a][1] += applySuccesrate(
						nPopByRiskClassByAge[0][timestep][r][a][1],
						nPopByRiskClassByAge[thisScen][timestep][r][a][1],
						thisScen, timestep, a);
				nRefPopByAge[a][0] += nPopByRiskClassByAge[0][timestep][r][a][0];
				nRefPopByAge[a][1] += nPopByRiskClassByAge[0][timestep][r][a][1];

			}
			/*
			 * as it is printed upside down we change the order in the dataset
			 * by putting a in 99-a
			 */

			// TODO hoe aanpakken als effect van richting verschilt per
			// leeftijdsgroep
			/*
			 * round as plot gives the numbers and integer persons are strange
			 * to users
			 */
			if (nPopByAge[a][0] >= nRefPopByAge[a][0]) {
				pyramidData1[0][99 - a] = Math.round(nRefPopByAge[a][0]);
				pyramidData2[0][99 - a] = -Math.round(nRefPopByAge[a][1]);
				pyramidData1[1][99 - a] = Math.round(nPopByAge[a][0]
						- nRefPopByAge[a][0]);
				pyramidData2[1][99 - a] = -Math.round(nPopByAge[a][1]
						- nRefPopByAge[a][1]);
			} else {
				pyramidData1[0][99 - a] = Math.round(nPopByAge[a][0]);
				pyramidData2[0][99 - a] = -Math.round(nPopByAge[a][1]);
				pyramidData1[1][99 - a] = Math.round(-nPopByAge[a][0]
						+ nRefPopByAge[a][0]);
				pyramidData2[1][99 - a] = -Math.round(-nPopByAge[a][1]
						+ nRefPopByAge[a][1]);
			}
		}
		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset("",
				"", pyramidData1);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset("",
				"", pyramidData2);
		/* the last three booleans are for: legend , ? , */
		JFreeChart chart = ChartFactory.createStackedBarChart(
				"Population pyramid for scenario " + scenarioNames[thisScen]
						+ " versus" + " reference scenario at year "
						+ (startYear + timestep), "", "population size",
				dataset1, PlotOrientation.HORIZONTAL, false, true, true);
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis catAxis = (CategoryAxis) plot.getDomainAxis();

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
		renderer.setItemMargin(0.0);
		renderer.setDrawBarOutline(true);

		// ChartFrame frame = new ChartFrame("LifeExpectancy Chart", chart);
		final CategoryAxis domainAxis = new CategoryAxis("PopulationNumbers");
		renderer.setItemMargin(0.0);

		renderer.setItemLabelAnchorOffset(9.0);
		renderer.setSeriesPaint(0, Color.white);
		renderer.setSeriesPaint(1, Color.gray);
		renderer.setDrawBarOutline(true);
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijnen
		CategoryAxis categoryAxis = plot.getDomainAxis();
		categoryAxis.setCategoryMargin(0.0); // ruimte tussen de balken
		categoryAxis.setUpperMargin(0.02); // ruimte boven bovenste balk
		categoryAxis.setLowerMargin(0.02);// ruimte onder onderste balk
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
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
				if (populationSize[a][s] > maximum)
					maximum = Math.round(populationSize[a][s]);
		if (mfratio > 1) {
			for (int y = 0; y < newborns.length; y++)
				if (newborns[y] * mfratio / (1 + mfratio) > maximum)
					maximum = Math.round(newborns[y] * mfratio / (1 + mfratio));
		} else {
			for (int y = 0; y < newborns.length; y++)
				if (newborns[y] / (1 + mfratio) > maximum)
					maximum = Math.round(newborns[y] / (1 + mfratio));
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
	 * @param diseaseNumber
	 *            : number of the disease (not yet implemented)
	 * @return
	 */
	public JFreeChart makePyramidChartIncludingDisease(int thisScen,
			int timestep, int d) {

		/*
		 * pyramid data 1 are the data for men, pyramiddata2 those for women;
		 */
		double[][] pyramidData1 = new double[6][105];
		double[][] pyramidData2 = new double[6][105];
		double[][] nHealthyByAge = new double[105][2];
		double[][] nRefHealthyByAge = new double[105][2];
		double[][] nDiseaseByAge = new double[105][2];
		double[][] nRefDiseaseByAge = new double[105][2];
		double[][][][] withDisease;
		if (d < 0)
			withDisease = getNumberOfDiseasedPersons();
		else
			withDisease = getNDiseaseByAge(d);
		double[][][][] nPopByAge = getNPopByAge();

		/* the arrays with Key give the names of the columns in the plot */
		String[] typeKey1 = { "with disease", "ref-scen (disease)",
				"scen-ref (disease)", "healthy", "ref-scen (total)",
				"scen-ref (total)" };
		String[] typeKey2 = { "with disease", "ref-scen (disease)",
				"scen-ref (disease)", "healthy", "ref-scen (total)",
				"scen-ref (total)" };

		if (d >= 0) {
			typeKey1[0] = diseaseNames[d];
			typeKey2[0] = diseaseNames[d];
		}

		String[] ageKey = new String[105];
		for (int a = 0; a < 105; a++) {
			// if (Math.floor(a/5)==a)
			ageKey[104 - a] = ((Integer) a).toString();
			// else ageKey[104-a]="";

			nDiseaseByAge[a][0] += applySuccesrate(
					withDisease[0][timestep][a][0],
					withDisease[thisScen][timestep][a][0], thisScen, timestep,
					a);
			nHealthyByAge[a][0] += applySuccesrate(nPopByAge[0][timestep][a][0]
					- withDisease[0][timestep][a][0],
					nPopByAge[thisScen][timestep][a][0]
							- withDisease[thisScen][timestep][a][0], thisScen,
					timestep, a);
			nDiseaseByAge[a][1] += applySuccesrate(
					withDisease[0][timestep][a][1],
					withDisease[thisScen][timestep][a][1], thisScen, timestep,
					a);
			nHealthyByAge[a][1] += applySuccesrate(nPopByAge[0][timestep][a][1]
					- withDisease[0][timestep][a][1],
					nPopByAge[thisScen][timestep][a][1]
							- withDisease[thisScen][timestep][a][1], thisScen,
					timestep, a);

			nRefDiseaseByAge[a][0] += withDisease[0][timestep][a][0];
			nRefDiseaseByAge[a][1] += withDisease[0][timestep][a][1];
			nRefHealthyByAge[a][0] += nPopByAge[0][timestep][a][0]
					- withDisease[0][timestep][a][0];
			nRefHealthyByAge[a][1] += nPopByAge[0][timestep][a][1]
					- withDisease[0][timestep][a][1];

			/*
			 * as it is printed upside down we change the order in the dataset
			 * by putting a in 99-a
			 */

			/*
			 * round as plot gives the numbers and integer persons are strange
			 * to users
			 */

			/*
			 * pyramid data 1 are the data for men, 2 those for women; they
			 * contain 6 data parts: <br> 0: minimum of ( # withdisease
			 * reference scenario, # withdisease scenario) <br>1:maximum of (#
			 * withdisease reference scenario-# withdisease scenario, 0)
			 * <br>2:maximum of (# withdisease scenario-# withdisease reference
			 * scenario, 0)<br> 3:minimum of (# total reference scenario , #
			 * total scenario ) - total (0 - 2)<br> 4:maximum of (# total
			 * reference scenario-# total scenario, 0) <br>5:maximum of (# total
			 * scenario-# total reference scenario, 0)
			 */

			pyramidData1[0][104 - a] = Math.max(0, Math
					.round(nRefDiseaseByAge[a][0]));
			pyramidData2[0][104 - a] = -Math.max(0, Math
					.round(nRefDiseaseByAge[a][1]));
			pyramidData1[1][104 - a] = Math.max(0, Math
					.round(nRefDiseaseByAge[a][0] - nDiseaseByAge[a][0]));
			pyramidData2[1][104 - a] = -Math.max(0, Math
					.round(nRefDiseaseByAge[a][1] - nDiseaseByAge[a][1]));

			pyramidData1[2][104 - a] = Math.max(0, Math
					.round(nDiseaseByAge[a][0])
					- nRefDiseaseByAge[a][0]);
			pyramidData2[2][104 - a] = -Math.max(0, Math
					.round(nDiseaseByAge[a][1])
					- nRefDiseaseByAge[a][1]);

			pyramidData1[3][104 - a] = Math.round(Math.min(
					(nRefHealthyByAge[a][0] + nRefDiseaseByAge[a][0]),
					(nHealthyByAge[a][0] + nDiseaseByAge[a][0])))
					- pyramidData1[0][104 - a]
					- pyramidData1[1][104 - a]
					- pyramidData1[2][104 - a];
			pyramidData2[3][104 - a] = -Math.round(Math.min(
					(nRefHealthyByAge[a][1] + nRefDiseaseByAge[a][1]),
					(nHealthyByAge[a][1] + nDiseaseByAge[a][1])))
					- pyramidData2[0][104 - a]
					- pyramidData2[1][104 - a]
					- pyramidData2[2][104 - a];

			pyramidData1[4][104 - a] = Math.max(0, Math
					.round(nRefHealthyByAge[a][0] + nRefDiseaseByAge[a][0]
							- nHealthyByAge[a][0] - nDiseaseByAge[a][0]));
			pyramidData2[4][104 - a] = -Math.max(0, Math
					.round(nRefHealthyByAge[a][1] + nRefDiseaseByAge[a][1]
							- nHealthyByAge[a][1] - nDiseaseByAge[a][1]));

			pyramidData1[5][104 - a] = Math.max(0, Math
					.round(nHealthyByAge[a][0] + nDiseaseByAge[a][0]
							- nRefHealthyByAge[a][0] - nRefDiseaseByAge[a][0]));
			pyramidData2[5][104 - a] = -Math.max(0, Math
					.round(nHealthyByAge[a][1] + nDiseaseByAge[a][1]
							- nRefHealthyByAge[a][1] - nRefDiseaseByAge[a][1]));

		}

		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(
				typeKey1, ageKey, pyramidData1);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(
				typeKey2, ageKey, pyramidData2);
		/* find the maximum value of the current population */
		/* assume that scenarios will not increase this by more than 50% */

		if (scaleRange == 0) {
			/* do only once */
			int maxPopulationSize = getMaxPop();
			scaleRange = 10000 * Math.ceil(maxPopulationSize * 1.1 / 10000);
		}

		/* the last three booleans are for: legend , ? , */

		JFreeChart chart = ChartFactory.createStackedBarChart(
				"Population pyramid for scenario " + scenarioNames[thisScen]
						+ " versus" + " reference scenario at year "
						+ (startYear + timestep), "", "population size",
				dataset1, PlotOrientation.HORIZONTAL, true, true, true);

		CategoryPlot plot = chart.getCategoryPlot();

		CategoryAxis catAxis = (CategoryAxis) plot.getDomainAxis();

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
		final CategoryAxis domainAxis = new CategoryAxis("PopulationNumbers");

		renderer.setItemLabelAnchorOffset(9.0);
		renderer2.setItemLabelAnchorOffset(9.0);
		// renderer.setSeriesVisibleInLegend(1,false);
		renderer.setSeriesPaint(0, Color.pink);
		renderer.setSeriesPaint(1, Color.orange);
		renderer.setSeriesPaint(2, Color.red);
		renderer.setSeriesPaint(3, Color.white);
		renderer.setSeriesPaint(4, Color.black);
		renderer.setSeriesPaint(5, Color.gray);
		renderer2.setSeriesPaint(0, Color.pink);
		renderer2.setSeriesPaint(1, Color.orange);
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
		rangeAxis.setRange(-scaleRange, scaleRange);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setUpperMargin(0.10);

		plot.setRenderer(0, renderer);
		renderer2.setBaseSeriesVisibleInLegend(false);
		plot.setRenderer(1, renderer2);
		return chart;
	}

	public int getRiskType() {
		return riskType;
	}

	public void setRiskType(int riskType) {
		this.riskType = riskType;
	}

	public int getNScen() {
		return nScen;
	}

	public void setNScen(int scen) {
		nScen = scen;
	}

	public int getStepsInRun() {
		return stepsInRun;
	}

	public void setStepsInRun(int stepsInRun) {
		this.stepsInRun = stepsInRun;
	}

	public DiseaseClusterStructure[] getStructure() {
		return structure;
	}

	public void setStructure(DiseaseClusterStructure[] structure) {
		this.structure = structure;
	}

	/**
	 * @param structure2
	 */
	private void setNDiseases(DiseaseClusterStructure[] s) {
		nDiseases = 0;
		for (int i = 0; i < s.length; i++) {
			nDiseases += s[i].getNInCluster();

		}
		// TODO Auto-generated method stub

	}

	public int getNDiseases() {
		return nDiseases;
	}

	public int getNRiskFactorClasses() {
		return nRiskFactorClasses;
	}

	public void setNRiskFactorClasses(int riskFactorClasses) {
		nRiskFactorClasses = riskFactorClasses;
	}

	public int getNDiseaseStates() {
		return nDiseaseStates;
	}

	public void setNDiseaseStates(DiseaseClusterStructure[] s) {
		nDiseaseStates = 1;

		for (int i = 0; i < s.length; i++) {
			if (s[i].getNInCluster() == 1)
				nDiseaseStates++;
			else if (s[i].isWithCuredFraction())
				nDiseaseStates += 2;
			else
				nDiseaseStates += Math.pow(2, s[i].getNInCluster()) - 1;

		}
	}

	public void setNDiseaseStates(int input) {
		nDiseaseStates = input;
	}

	public void writeCategoryChart(String fileName, JFreeChart chart)
			throws DynamoOutputException {
		File outFile = new File(fileName);
		String directoryName = outFile.getParent();
		File directory = new File(directoryName);
		boolean isDirectory = outFile.isDirectory();
		boolean canWrite = outFile.canWrite();
		try {
			boolean isNewDirectory = directory.mkdirs();
			boolean isNew = outFile.createNewFile();
			if (!isDirectory && (canWrite || isNew))

				ChartUtilities.saveChartAsJPEG(new File(fileName), chart, 300,
						500);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new DynamoOutputException(e.getMessage());
		}
	}

	public int[] getNInSimulation() {
		return nInSimulation;
	}

	public int[][] getNInSimulationByAge() {
		return nInSimulationByAge;
	}

	public void setNInSimulationByAge(int[][] inSimulationByAge) {
		nInSimulationByAge = inSimulationByAge;
	}

	public double[][][][][][] getNDiseaseStateByRiskClassByAge() {
		return nDiseaseStateByRiskClassByAge;
	}

	public void setNDiseaseStateByRiskClassByAge(
			double[][][][][][] diseaseStateByRiskClassByAge) {
		nDiseaseStateByRiskClassByAge = diseaseStateByRiskClassByAge;
	}

	public float[] getSuccesrate() {
		float[] returnvalue = new float[succesrate.length];
		for (int i = 0; i < succesrate.length; i++)
			returnvalue[i] = succesrate[i] * 100;
		return returnvalue;
	}

	public void setSuccesrate(float[] succesrate) {

		this.succesrate = new float[succesrate.length];
		for (int i = 0; i < succesrate.length; i++)
			this.succesrate[i] = succesrate[i] / 100;

	}

	public void setSuccesrate(float succesrate, int i) {
		this.succesrate[i] = succesrate / 100;

	}

	public float[] getMinAge() {
		return minAge;
	}

	public void setMinAge(float[] minAge) {
		this.minAge = minAge;
	}

	public void setMinAge(float minAge, int i) {
		this.minAge[i] = minAge;
	}

	public float[] getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(float[] maxAge) {
		this.maxAge = maxAge;
	}

	public void setMaxAge(float maxAge, int i) {
		this.maxAge[i] = maxAge;
	}

	public int[][] getNNewBornsInSimulationByAge() {
		return nNewBornsInSimulationByAge;
	}

	public int[][][] getNInSimulationByRiskClassByAge() {
		return nInSimulationByRiskClassByAge;
	}

	public double[][][][][] getNDiseaseByRiskClass() {
		double[][][][][] nDiseaseByRiskClass = new double[nScen + 1][stepsInRun + 1][nDiseases][nRiskFactorClasses][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < nDim; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)

								nDiseaseByRiskClass[scen][stepCount][d][r][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByRiskClass;
	}

	public double[][][][] getNDisease() {
		double[][][][] nDisease = new double[nScen + 1][stepsInRun + 1][nDiseases][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < nDim; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
								nDisease[scen][stepCount][d][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDisease;
	}

	public double[][][][][] getNDiseaseByAge() {
		double[][][][][] nDiseaseByAge = new double[nScen + 1][stepsInRun + 1][nDiseases][nDim][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < nDim; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
								nDiseaseByAge[scen][stepCount][d][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByAge;
	}

	public double[][][][] getNDiseaseByAge(int disease) {
		double[][][][] nDiseaseByAge = new double[nScen + 1][stepsInRun + 1][nDim][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);
		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < nDim; a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
							nDiseaseByAge[scen][stepCount][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][disease][r][a][g];
		return nDiseaseByAge;
	}

	public double[][] getNDiseaseByAge(int scen, int stepCount, int g) {
		double[][] nDiseaseByAge = new double[nDiseases][nDim];
		;
		double[][][] nDiseaseByRiskClassByAge = makeDiseaseArray(
				nDiseaseStateByRiskClassByAge, scen, stepCount, g);
		for (int r = 0; r < nRiskFactorClasses; r++)
			for (int a = 0; a < nDim; a++)
				for (int d = 0; d < nDiseases; d++)
					nDiseaseByAge[d][a] += nDiseaseByRiskClassByAge[d][r][a];
		return nDiseaseByAge;
	}

	public double[][][][][] getNDiseaseByOriAge() {
		double[][][][][] nDiseaseByAge = new double[nScen + 1][nDim][nDiseases][96][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByOriRiskClassByOriAge);
		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < nDim; stepCount++)
								nDiseaseByAge[scen][stepCount][d][a][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][g];
		return nDiseaseByAge;
	}

	public double[][][] getNDiseaseByOriAge(int age, int d) {
		double[][][] nDiseaseByAge = new double[nScen + 1][nDim - age][2];
		;
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByOriRiskClassByOriAge);
		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)

				for (int g = 0; g < 2; g++)

					for (int stepCount = 0; stepCount < nDim - age; stepCount++)
						nDiseaseByAge[scen][stepCount][g] += nDiseaseByRiskClassByAge[scen][stepCount][d][r][age][g];
		return nDiseaseByAge;
	}

	public double[][][][][][] getNDiseaseByRiskClassByAge() {
		double[][][][][][] nDiseaseByRiskClassByAge = makeDiseaseArray(nDiseaseStateByRiskClassByAge);

		return nDiseaseByRiskClassByAge;
	}

	public double[][][][][] getMeanRiskByRiskClassByAge() {
		return meanRiskByRiskClassByAge;
	}

	public double[][][][][] getNPopByRiskClassByAge() {
		return nPopByRiskClassByAge;
	}

	public double[][][][] getNPopByAge() {

		double[][][][] nPopByAge = new double[nScen + 1][stepsInRun + 1][nDim][2];

		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < nDim; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < stepsInRun + 1; stepCount++)
							nPopByAge[scen][stepCount][a][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	public double[] getNPopByAge(int scen, int stepCount, int g) {
		double[] nPopByAge = new double[nDim];
		for (int r = 0; r < nRiskFactorClasses; r++)
			for (int a = 0; a < nDim; a++)
				nPopByAge[a] += nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPopByAge;
	}

	public double[][][] getNPop() {

		double[][][] nPop = new double[nScen + 1][stepsInRun + 1][2];

		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < nDim; a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < nDiseases; d++)
							for (int stepCount = 0; stepCount < nDim; stepCount++)
								nPop[scen][stepCount][g] += nPopByRiskClassByAge[scen][stepCount][r][a][g];
		return nPop;

	}

	public double[][][][] getNPopByOriAge() {

		double[][][][] nPopByAge = new double[nScen + 1][nDim][96][2];

		for (int r = 0; r < nRiskFactorClasses; r++)

			for (int scen = 0; scen < nScen + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < nDim; stepCount++)
							nPopByAge[scen][stepCount][a][g] += nPopByOriRiskClassByOriAge[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public String[] getRiskClassnames() {
		return riskClassnames;
	}

	public void setRiskClassnames(String[] riskClassnames) {
		this.riskClassnames = riskClassnames;
	}

	public String[] getDiseaseNames() {
		return diseaseNames;
	}

	public void setDiseaseNames(String[] diseaseNames) {
		this.diseaseNames = diseaseNames;
	}

	public String[] getScenarioNames() {
		return scenarioNames;
	}

	public void setScenarioNames(String[] scenarioNames) {
		this.scenarioNames = scenarioNames;
	}

	public boolean isDetails() {
		return details;
	}

	public void setDetails(boolean details) {
		this.details = details;
	}

	public int getMaxAgeInSimulation() {
		return maxAgeInSimulation;
	}

	public void setMaxAgeInSimulation(int maxAgeInSimulation) {
		this.maxAgeInSimulation = maxAgeInSimulation;
	}

	public int getMinAgeInSimulation() {
		return minAgeInSimulation;
	}

	public void setMinAgeInSimulation(int minAgeInSimulation) {
		this.minAgeInSimulation = minAgeInSimulation;
	}

}
