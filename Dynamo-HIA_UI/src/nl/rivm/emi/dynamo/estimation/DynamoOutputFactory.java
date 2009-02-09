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
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;

import java.awt.Font;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jfree.chart.ChartPanel;

public class DynamoOutputFactory {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.estimation.ConfigurationFromXMLFactory");

	/* different arrays with summation data */
	/* all are by sex, timestep and scenario */
	/* some are by risk or by age or both (indicated by their names) */
	/* p=proportion (of age/risk/sex class) */
	/* n= total number */
	/*
	 * sequence of indexes: scenario, time , disease, risk class, age, and sex
	 */
	int[] nInSimulation = new int[2]; /* index: sex */
	int[][] nInSimulationByAge;
	int[][][] nInSimulationByRiskClassByAge;

	private double pPop[][][]; /*
								 * percentage of survivors by scenario, time and
								 * sex
								 */
	private double pPopByAge[][][][];/*
									 * percentage of survivors by scenario, age,
									 * time, and sex
									 */
	private double pPopByRiskClass[][][][]; /*
											 * percentage of survivors of each
											 * risk class by scenario,risk class
											 * time and sex
											 */

	private double pRiskClass[][][][]; /*
										 * prevalence of each risk class by
										 * scenario,risk class time and sex
										 */
	private double pDisease[][][][];/*
									 * prevalence of disease ; indexes are
									 * scenario,time,disease, and sex
									 */
	private double pPopPerRiskClass[][][][]; /*
											 * percentage of survivors by
											 * scenario, time, risk class and
											 * sex
											 */
	/*
	 * NB this array is reused, so it contains something different before and
	 * after applying method makeSummaryArrays
	 */
	private double pDiseaseStateByRiskClassByAge[][][][][][]; /*
															 * number of
															 * survivors in
															 * scenario each
															 * diseaseState by
															 * scenario, time,
															 * disease state
															 * risk class and
															 * age sex
															 */

	private double nDiseaseStateByRiskClassByAge[][][][][][]; /*
															 * number of
															 * survivors in each
															 * diseaseState by
															 * scenario,
															 * time,disease
															 * state risk class
															 * age and sex TODO
															 */

	private double pDiseasePerRiskClass[][][][][];/*
												 * prevalence of disease ;
												 * indexes are scenario,time
												 * disease,risk class and sex
												 */
	private double nDiseasePerRiskClass[][][][][];/*
												 * numbers for each disease/risk
												 * factor combi ; indexes are
												 * scenario,time,disease, risk
												 * class and sex
												 */
	private double pDiseaseByAge[][][][][];/*
											 * numbers for each disease/risk
											 * factor combi ; indexes are
											 * scenario,time,disease, risk class
											 * and sex
											 */
	/*
	 * NB this array is reused, so it contains something different before and
	 * after applying method makeSummaryArrays
	 */
	private double pDiseaseByRiskClassByAge[][][][][][];/*
														 * prevalence of disease
														 * ; indexes are
														 * scenario time,
														 * disease, risk class
														 * age, and sex
														 */

	private double nDiseaseByRiskClassByAge[][][][][][];/*
														 * number of persons
														 * with each disease ;
														 * indexes are
														 * scenario,time
														 * disease, risk class
														 * age, and sex
														 */
	/*
	 * NB this array is reused, so it contains something different before and
	 * after applying method makeSummaryArrays
	 */
	private double[][][][][] pPopByRiskClassByAge; /*
													 * prevalence of risk class
													 * by Age; indexes are:
													 * scenario time risk class
													 * age, and sex
													 */

	/*
	 * NB this array is reused, so it contains something different before and
	 * after applying method makeSummaryArrays
	 */
	private double[][][][][] meanRiskByRiskClassByAge; /*
														 * prevalence of risk
														 * class by Age; indexes
														 * are: scenario time
														 * risk class, age, and
														 * sex
														 */

	private double[][][][][] nPopByRiskClassByAge; /*
													 * number in risk class by
													 * Age; indexes are:
													 * scenario, time risk class
													 * age and sex
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
	private boolean details;
	private float[] succesrate = null;
	private float[] minAge = null;
	private float[] maxAge = null;
	private boolean[] scenInitial;
	private boolean[] scenTrans;
	private float[] cutoffs;
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
	private float[][][][] newPrevalence; /*
										 * for each scenario the new prevalence
										 * rates
										 */
	private float[][][] oldPrevalence; /*
										 * the prevalence rates of the reference
										 * scenario
										 */
	private String baseDir;
	private String simulationName;
	private String[] stateNames;
	private String[] diseaseNames;
	private String[] scenarioNames;
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

	public DynamoOutputFactory(ScenarioInfo scenInfo, String simName) {
		super();
		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */
		baseDir = BaseDirectory.getInstance("c:\\hendriek\\java\\dynamohome\\")
				.getBaseDir();
		this.simulationName = simName;
		setRiskType(scenInfo.getRiskType());
		setNScen(scenInfo.getNScenarios());
		scenInitial = scenInfo.getInitialPrevalenceType();
		scenTrans = scenInfo.getTransitionType();
		nTransScenarios = 0;
		oneScenPopulation = false;
		isOneScenPopulation = new boolean[nScen + 1];
		popToScenIndex = new int[nScen + 1];
		succesrate = scenInfo.getSuccesrate();
		for (int i = 0; i < succesrate.length; i++)
			succesrate[i] = succesrate[i] / 100;
		minAge = scenInfo.getMinAge();
		maxAge = scenInfo.getMaxAge();
		scenarioNames = scenInfo.getScenarioNames();
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

		int nClasses = getNRiskFactorClasses();

		pPop = new double[nScen + 1][stepsInRun][2];
		pPopByAge = new double[nScen + 1][stepsInRun][stepsInRun + 96][2];
		pPopByRiskClass = new double[nScen + 1][stepsInRun][nClasses][2];
		pRiskClass = new double[nScen + 1][stepsInRun][nClasses][2];
		pDisease = new double[nScen + 1][stepsInRun][nDiseases][2];

		pPopByRiskClassByAge = new double[nScen + 1][stepsInRun][nClasses][96 + stepsInRun][2];
		nPopByRiskClassByAge = new double[nScen + 1][stepsInRun][nClasses][96 + stepsInRun][2];
		if (riskType ==2)
			meanRiskByRiskClassByAge = new double[nScen + 1][stepsInRun][nClasses][96 + stepsInRun][2];
		if (riskType ==3)
			meanRiskByRiskClassByAge = new double[nScen + 1][stepsInRun][nClasses][96 + stepsInRun][2];

		pDiseaseByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseases][nClasses][96 + stepsInRun][2];

		/*
		 * NB the dimension can be nClasses (nClasses-1) but this makes life
		 * more difficult for now we suppose we have enough room for doing it
		 * this way
		 */

		pDiseaseStateByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseaseStates][nClasses][96 + stepsInRun][2];

		nDiseaseStateByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseaseStates][nClasses][96 + stepsInRun][2];

		nDiseaseByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseases][nClasses][96 + stepsInRun][2];

		pDiseasePerRiskClass = new double[nScen + 1][stepsInRun][nDiseases][nClasses][2];
		pDiseaseByAge = new double[nScen + 1][stepsInRun][nDiseases][96 + stepsInRun][2];

		for (int i = 0; i <= nScen; i++)
			for (int sex = 0; sex < 2; sex++)
				for (int steps = 0; steps < stepsInRun; steps++) {
					pPop[i][steps][sex] = 0;

					for (int age = 0; age < stepsInRun + 96; age++) {
						pPopByAge[i][steps][age][sex] = 0;
					}
					for (int nDis = 0; nDis < nDiseases; nDis++) {
						pDisease[i][steps][nDis][sex] = 0;
					}

					for (int r = 0; r < nRiskFactorClasses; r++) {
						pPopByRiskClass[i][steps][r][sex] = 0;
						pRiskClass[i][steps][r][sex] = 0;
						for (int age = 0; age < stepsInRun + 96; age++) {
							pPopByRiskClassByAge[i][steps][r][age][sex] = 0;
							if (riskType > 1)
								meanRiskByRiskClassByAge[i][steps][r][age][sex] = 0;

							nPopByRiskClassByAge[i][steps][r][age][sex] = 0;
						}
						for (int nDis = 0; nDis < nDiseases; nDis++) {
							pDiseasePerRiskClass[i][steps][nDis][r][sex] = 0;

							for (int age = 0; age < stepsInRun + 96; age++)

							{
								pDiseaseByAge[i][steps][nDis][age][sex] = 0;
								pDiseaseByRiskClassByAge[i][steps][nDis][r][age][sex] = 0;
								nDiseaseByRiskClassByAge[i][steps][nDis][r][age][sex] = 0;
							}
						}
						for (int nState = 0; nState < nDiseaseStates; nState++) {

							for (int age = 0; age < stepsInRun + 96; age++)

							{
								pDiseaseStateByRiskClassByAge[i][steps][nState][r][age][sex] = 0;
								nDiseaseStateByRiskClassByAge[i][steps][nState][r][age][sex] = 0;
							}
						}
					}
				}

	}

	/**
	 * this method extracts summary arrays from the simulated population the
	 * arrays are fields of the object DynamoOutPutFactory, named
	 * pPopByRiskClassByAge, pDiseaseStateByRiskClassByAge and
	 * pDiseaseByRiskClassByAge they contain the proportion of the initial
	 * population that is resp. still alive, in a particular diseases state, or
	 * has a particular disease
	 * 
	 * @param Population
	 *            [] pop: array with the simulated populations
	 * @throws DynamoScenarioException
	 */
	public void extractArraysFromPopulations(Population[] pop)
			throws DynamoScenarioException {

		// TODO newborns weighting
		float maxRisk = -1000000000;
		float minRisk = 1000000000;
		/*
		 * for continuous risk factor without defined cutoffs, define cutoffs
		 * based on 10% percentiles
		 */
		/* for this, first find minimum and maximum values */
		if (riskType == 2 && cutoffs == null) {
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
				nRiskFactorClasses = 10;
				cutoffs = new float[9];
				riskClassnames = new String[10];
				for (int i = 0; i < 9; i++) {
					cutoffs[i] = minRisk + i * (maxRisk - minRisk) * 0.1F;
					if (i > 0)
						riskClassnames[i] = cutoffs[i - 1] + "-" + cutoffs[i];

				}
				riskClassnames[0] = "<" + cutoffs[0];
				riskClassnames[9] = ">" + cutoffs[8];
			} else if (maxRisk == minRisk) {
				riskClassnames = new String[1];
				nRiskFactorClasses = 1;
				riskClassnames[0] = ((Float) maxRisk).toString();
			}

		}

		double[][][][][][] pDiseaseByRiskClassByAge_scen = new double[stepsInRun][nDiseases][nRiskFactorClasses][nRiskFactorClasses][96 + stepsInRun][2];
		double[][][][][] pPopByRiskClassByAge_scen = new double[stepsInRun][nRiskFactorClasses][nRiskFactorClasses][96 + stepsInRun][2];
		double[][][][][][] pDiseaseStateByRiskClassByAge_scen = new double[stepsInRun][nDiseaseStates][nRiskFactorClasses][nRiskFactorClasses][96 + stepsInRun][2];
		double[][][][][] meanRiskByRiskClassByAge_scen = null;
		if (riskType == 3)
			meanRiskByRiskClassByAge_scen = new double[stepsInRun][nRiskFactorClasses][nRiskFactorClasses][96 + stepsInRun][2];

		int sexIndex = 0;
		int ageIndex = 0;
		nInSimulation = new int[2];
		nInSimulationByAge = new int[96 + stepsInRun][2];
		nInSimulationByRiskClassByAge = new int[nRiskFactorClasses][96 + stepsInRun][2];

		double weight[][][] = new double[nRiskFactorClasses][96][2];

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
		
		Iterator<Individual> individualIterator1 = pop[0].iterator();

		while (individualIterator1.hasNext()) {
			Individual individual = individualIterator1.next();
            
			ageIndex = (int) Math
					.round(((Float) individual.get(1).getValue(0)));
			sexIndex = (int) (Integer) individual.get(2).getValue(0);
			nInSimulation[sexIndex]++;
			nInSimulationByAge[ageIndex][sexIndex]++;

			float riskValue;
    		if (riskType != 2) {
				int riskFactor = (int) (Integer) individual.get(3).getValue(0);
				nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;
               
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

		/*
		 * calculated weights : weighting factors for individuals in order to
		 * calculated all outcomes valid for a population with the distribution
		 * of risk factors given in oldPrevalence
		 */

		for (int s = 0; s < 2; s++)
			for (int age = 0; age < 96; age++) {

				for (int r = 0; r < nRiskFactorClasses; r++)
					if (riskType != 2)
						weight[r][age][s] = oldPrevalence[age][s][r]
								* nInSimulationByAge[age][s]
								/ nInSimulationByRiskClassByAge[r][age][s];
					else
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

			// now make detailed arrays summing the data for sex/age/year/risk
			// class combinations
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();
				double weightOfIndividual = 1;
				for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {
					/*
					 * get the information of this individual at the stepCount
					 * step for the simulation
					 */
					ageIndex = (int) Math.round(((Float) individual.get(1)
							.getValue(stepCount)));
					sexIndex = (int) (Integer) individual.get(2).getValue(
							stepCount);
					int riskFactor = 0;
					float riskValue = 0;
					
					if (riskType != 2)
						riskFactor = (int) (Integer) individual.get(3)
								.getValue(stepCount);
					else {
						riskValue = (float) (Float) individual.get(3).getValue(
								stepCount);
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
					}

					if (riskType == 3)

						riskValue = (float) (Float) individual.get(4).getValue(
								stepCount);

					/*
					 * the weighting is meant to make the distribution of the
					 * categorical risk factor in the reference scenario equal
					 * to what is given as distribution (despite the fact that
					 * because of a finite number of simulated cases, the
					 * distribution in the simulated population is different.
					 * the weight of an individual should be the same in each
					 * scenario. So the weight is determined by the value of the
					 * riskfactor in the reference scenario in changing initial
					 * prevalence scenarios this is the value of "from" In
					 * changing transition rate scenarios the initial
					 * distribution is the same in the reference population and
					 * the scenario population
					 */

					if (stepCount == 0 && riskType != 2)
						weightOfIndividual = weight[riskFactor][ageIndex][sexIndex];

					if (riskType == 3)
						compoundData = ((CompoundCharacteristicValue) individual
								.get(5)).getUnwrappedValue(stepCount);
					else
						compoundData = ((CompoundCharacteristicValue) individual
								.get(4)).getUnwrappedValue(stepCount);
					survival = compoundData[getNDiseaseStates() - 1];
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
							if (stepCount == 0)
								weightOfIndividual = weight[from][ageIndex][sexIndex];

						}

					if (thisPop > 0) {
						if (isOneScenPopulation[thisPop]) {
							pPopByRiskClassByAge_scen[stepCount][from][to][ageIndex][sexIndex] += weightOfIndividual
									* survival;
							if (riskType ==3)
								meanRiskByRiskClassByAge_scen[stepCount][from][to][ageIndex][sexIndex] += weightOfIndividual
										* riskValue * survival;

						} else {
							pPopByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
									* survival;
							if (riskType > 1)
								meanRiskByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* riskValue * survival;

						}
					}

					else {
						pPopByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
								* survival;
						if (riskType > 1)
							meanRiskByRiskClassByAge[0][stepCount][riskFactor][ageIndex][sexIndex] += weightOfIndividual
									* riskValue * survival;

						if (oneScenPopulation) {
							pPopByRiskClassByAge_scen[stepCount][riskFactor][riskFactor][ageIndex][sexIndex] += weightOfIndividual
									* survival;

							if (riskType ==3)
								meanRiskByRiskClassByAge_scen[stepCount][riskFactor][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* riskValue * survival;
						}
					}

					int currentDisease = 0;

					int currentClusterStartNumber = 0;
					for (int s = 0; s < nDiseaseStates; s++) {

						if (thisPop > 0)
							if (isOneScenPopulation[thisPop]) {
								pDiseaseStateByRiskClassByAge_scen[stepCount][s][from][to][ageIndex][sexIndex] += weightOfIndividual
										* compoundData[s] * survival;

							} else

								pDiseaseStateByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
										* compoundData[s] * survival;
						if (thisPop == 0) {
							pDiseaseStateByRiskClassByAge_scen[stepCount][s][riskFactor][riskFactor][ageIndex][sexIndex] += weightOfIndividual
									* compoundData[s] * survival;
							pDiseaseStateByRiskClassByAge[0][stepCount][s][riskFactor][ageIndex][sexIndex] += weightOfIndividual
									* compoundData[s] * survival;
						}

					}
					for (int c = 0; c < structure.length; c++) {

						for (int d = 0; d <= structure[c].getNInCluster(); d++) {

							for (int s = 1; s < Math.pow(2, structure[c]
									.getNInCluster()); s++) {

								if ((s & (1 << d)) == (1 << d)) {
									/*
									 * pDisease[thisScen][stepCount][currentDisease
									 * + d][sexIndex] +=
									 * compoundData[currentState + s - 1]
									 * survival
									 * weight[riskFactor][ageIndex][sexIndex];
									 * if (details)
									 */
									if (thisPop > 0) {
										if (isOneScenPopulation[thisPop]) {
											pDiseaseByRiskClassByAge_scen[stepCount][currentDisease
													+ d][from][to][ageIndex][sexIndex] += weightOfIndividual
													* compoundData[currentClusterStartNumber
															+ s - 1] * survival;
										} else
											pDiseaseByRiskClassByAge[popToScenIndex[thisPop] + 1][stepCount][currentDisease
													+ d][riskFactor][ageIndex][sexIndex] += weightOfIndividual
													* compoundData[currentClusterStartNumber
															+ s - 1] * survival;
									} else {
										pDiseaseByRiskClassByAge_scen[stepCount][currentDisease
												+ d][riskFactor][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[currentClusterStartNumber
														+ s - 1] * survival;
										pDiseaseByRiskClassByAge[0][stepCount][currentDisease
												+ d][riskFactor][ageIndex][sexIndex] += weightOfIndividual
												* compoundData[currentClusterStartNumber
														+ s - 1] * survival;

									}

									/*
									 * if (details)
									 * pDiseasePerRiskClass[thisScen
									 * ][stepCount][currentDisease +
									 * d][riskFactor][sexIndex] +=
									 * compoundData[currentState + s - 1]
									 * survival
									 * weight[riskFactor][ageIndex][sexIndex];
									 */
								}
							}
						}
						currentDisease += structure[c].getNInCluster();
						currentClusterStartNumber += Math.pow(2, structure[c]
								.getNInCluster()) - 1;

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
				} // end over stepCount
			}// end loop over individuals
		}// end loop populations

		/*
		 * 
		 * 
		 * combine data from the one-population-for-all-scenarios to separate
		 * scenario's
		 */

		// TODO: look into original number of simulated persons per
		// catagorie and how this should be included:
		// is using the weight enough?
		// TODO see whether this works for the later timesteps
		// to and from in scen are base on beginning, but those in the
		// reference cohort
		// are not in the current class
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

					for (int stepCount = 0; stepCount < stepsInRun; stepCount++)
						for (int a = 0; a < 96; a++)
							for (int s = 0; s < 2; s++)
							/*
							 * public static float[][]
							 * makeNettTransitionRates(float[] oldPrevOriginal,
							 * float[] newPrev,double baselineMort, float[] RR)
							 */
							{
								toChange = NettTransitionRates
										.makeNettTransitionRates(
												oldPrevalence[a][s],
												newPrevalence[scen][a][s], 0,
												dummy);
								for (from = 0; from < nRiskFactorClasses; from++)
									for (to = 0; to < nRiskFactorClasses; to++) {

										for (int state = 0; state < nDiseaseStates; state++)
											pDiseaseStateByRiskClassByAge[scen + 1][stepCount][state][to][a][s] += toChange[from][to]
													* pDiseaseStateByRiskClassByAge_scen[stepCount][state][from][to][a][s];
										for (int disease = 0; disease < nDiseases; disease++)

											pDiseaseByRiskClassByAge[scen + 1][stepCount][disease][to][a][s] += toChange[from][to]
													* pDiseaseByRiskClassByAge_scen[stepCount][disease][from][to][a][s];

										pPopByRiskClassByAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
												* pPopByRiskClassByAge_scen[stepCount][from][to][a][s];
										if (riskType > 1)
											meanRiskByRiskClassByAge[scen + 1][stepCount][to][a][s] += toChange[from][to]
													* meanRiskByRiskClassByAge_scen[stepCount][from][to][a][s];

										if (stepCount == 0
												&& toChange[from][to] > 0
												&& pPopByRiskClassByAge_scen[stepCount][from][to][a][s] == 0) {
											log
													.fatal(" not enough simulated information to calculate scenario "+ scen
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

	public void makeSummaryArrays() {

		/*
		 * 
		 * Make absolute numbers, using the population size at the start of the
		 * simulation
		 */
		for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {

			float originalNumber = 0;
			int originalAge = 0;
			float ratio = 0;
			for (int s = 0; s < 2; s++) {
				for (int a = 0; a < 96; a++) {
					/*
					 * get original number of persons in this birthcohort at
					 * time=zero
					 */
					if (a - stepCount >= 0) {
						originalNumber = populationSize[a - stepCount][s];
						originalAge = a - stepCount;
						ratio = originalNumber
								/ nInSimulationByAge[a - stepCount][s];
					} else {
						originalNumber = 0;
						originalAge = 0;
						ratio = 0;
					}
					// TODO if there are newborns
					// hieronder gok nog nagaan en zorgen dat alles goed
					// geinitialiseerd is
					// originalNumber=newborns[stepCount-a]*iets met mfratio
					/*
					 * nb: scen is the scenario number starting with scen =0 is
					 * the reference scenario. in arrays with scenario info the
					 * first (0) element refers to the first alternative
					 * scenario
					 */

					for (int scen = 0; scen <= nScen; scen++) {
						for (int r = 0; r < nRiskFactorClasses; r++) {

							/* make summary arrays */
							pPop[scen][stepCount][s] += pPopByRiskClassByAge[scen][stepCount][r][a][s];
							pPopByAge[scen][stepCount][a][s] += pPopByRiskClassByAge[scen][stepCount][r][a][s];
							pPopByRiskClass[scen][stepCount][r][s] += pPopByRiskClassByAge[scen][stepCount][r][a][s];

							for (int d = 0; d < nDiseases; d++) {

								/* make summary arrays */
								pDisease[scen][stepCount][d][s] += pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s];

								pDiseasePerRiskClass[scen][stepCount][d][r][s] += pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s];

								pDiseaseByAge[scen][stepCount][d][r][s] += pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s];

							}

							/* calculate absolute numbers */

							nPopByRiskClassByAge[scen][stepCount][r][a][s] = ratio
									* pPopByRiskClassByAge[scen][stepCount][r][a][s];

							for (int d = 0; d < nDiseases; d++) {

								nDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s] = ratio
										* pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s];

							}
							for (int state = 0; state < nDiseaseStates; state++) {

								nDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s] = ratio
										* pDiseaseStateByRiskClassByAge[scen][stepCount][state][r][a][s];

							}
						}
					}
				}// end loop age
			}// end loop sex

			/*
			 * uptill now all arrays contain the (weighted) sums of the
			 * simulated population in the category. The next part changes those
			 * into percentages (or means) by dividing by the right denominator
			 * (indicated by the name, and also by sex and timeStep (stepCount)
			 * and scenario
			 * 
			 * denominator: -- for survival (pop) the fraction of the initial
			 * population (stepcount=0) in the particular group -- for
			 * disease(state) : the fraction surviving (pop(stepcount))
			 */
			for (int scen = 0; scen <= nScen; scen++) {
				for (int s = 0; s < 2; s++) {
					if (riskType > 1)
						for (int r = 0; r < nRiskFactorClasses; r++)
							for (int a = 0; a < 96; a++) {
								if (pPopByRiskClassByAge[scen][stepCount][r][a][s] != 0)
									meanRiskByRiskClassByAge[scen][stepCount][r][a][s] = meanRiskByRiskClassByAge[scen][stepCount][r][a][s]
											/ pPopByRiskClassByAge[scen][stepCount][r][a][s];
								else
									meanRiskByRiskClassByAge[scen][stepCount][r][a][s] = -99999;
							}

					/* summary arrays for the disease prevalences */
					for (int d = 0; d < nDiseases; d++) {
						if (pPop[scen][stepCount][s] != 0)
							pDisease[scen][stepCount][d][s] = pDisease[scen][stepCount][d][s]
									/ pPop[scen][stepCount][s];
						else
							pDisease[scen][stepCount][d][s] = 0;
						for (int a = 0; a < 96; a++)

							if (pPopByAge[scen][stepCount][a][s] != 0)
								pDiseaseByAge[scen][stepCount][d][a][s] = pDiseaseByAge[scen][stepCount][d][a][s]
										/ pPopByAge[scen][stepCount][a][s];
							else
								pDiseaseByAge[scen][stepCount][d][a][s] = 0;

						for (int r = 0; r < nRiskFactorClasses; r++) {
							if (pPopByRiskClass[scen][stepCount][r][s] != 0)
								pDiseasePerRiskClass[scen][stepCount][d][r][s] = pDiseasePerRiskClass[scen][stepCount][d][r][s]
										/ pPopByRiskClass[scen][stepCount][r][s];
							else
								pDiseasePerRiskClass[scen][stepCount][d][r][s] = 0;
							for (int a = 0; a < 96; a++)
								if (pPopByRiskClassByAge[scen][stepCount][r][a][s] != 0)
									pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s] = pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s]
											/ pPopByRiskClassByAge[scen][stepCount][r][a][s];
								else
									pDiseaseByRiskClassByAge[scen][stepCount][d][r][a][s] = 0;

						}
					}
					/* summary arrays for risk factor prevalence */

					for (int r = 0; r < nRiskFactorClasses; r++) {
						if (pPop[scen][stepCount][s] != 0)
							pRiskClass[scen][stepCount][r][s] = pPopByRiskClass[scen][stepCount][r][s]
									/ pPop[scen][stepCount][s];
						else
							pRiskClass[scen][stepCount][r][s] = 0;

					}
					if (nInSimulation[s] != 0)
						pPop[scen][stepCount][s] = pPop[scen][stepCount][s]
								/ nInSimulation[s];
					else
						pPop[scen][stepCount][s] = 0;
					for (int a = 0; a < 96; a++)
						if (nInSimulationByAge[a][s] != 0)
							pPopByAge[scen][stepCount][a][s] = pPopByAge[scen][stepCount][a][s]
									/ nInSimulationByAge[a][s];
						else
							pPopByAge[scen][a][stepCount][s] = 0;

				}// end loop sex
			}// end loop scenarios
		}// end loop stepcount
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
	 */

	public void writeOutput(ScenarioInfo scenInfo) throws XMLStreamException,
			IOException {

		String baseDir = BaseDirectory.getBaseDir();
		// for (int scen = 0; scen < scenInfo.getNScenarios(); scen++) {
		/* make a n=new directory if it does not yet exists */
		String directoryName = baseDir + "Simulations" + File.separator
				+ simulationName + File.separator + "results";
		File directory = new File(directoryName);
		boolean isNewDirectory = directory.mkdirs();

		for (int scen = 0; scen <= nScen; scen++) {
			String fileName = baseDir + "Simulations" + File.separator
					+ simulationName + File.separator + "results"
					+ File.separator + "excel_year_male_scenario" + scen
					+ ".xml";
			writeWorkBookXMLbyYear(fileName, 0, scen);
			fileName = baseDir + "Simulations" + File.separator
					+ simulationName + File.separator + "results"
					+ File.separator + "excel_year_female_scenario" + scen
					+ ".xml";
			writeWorkBookXMLbyYear(fileName, 1, scen);
			fileName = baseDir + "Simulations" + File.separator
					+ simulationName + File.separator + "results"
					+ File.separator + "excel_year_scenario" + scen + ".xml";
			writeWorkBookXMLbyYear(fileName, 2, scen);
			fileName = baseDir + "Simulations" + File.separator
					+ simulationName + File.separator + "results"
					+ File.separator + "excel_cohort_male_scenario" + scen
					+ ".xml";
			writeWorkBookXMLbyCohort(fileName, 0, scen);
			fileName = baseDir + "Simulations" + File.separator
					+ simulationName + File.separator + "results"
					+ File.separator + "excel_cohort_female_scenario" + scen
					+ ".xml";
			writeWorkBookXMLbyCohort(fileName, 1, scen);
			fileName = baseDir + "Simulations" + File.separator
					+ simulationName + File.separator + "results"
					+ File.separator + "excel_cohort_scenario" + scen + ".xml";
			writeWorkBookXMLbyCohort(fileName, 2, scen);

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
	 */
	private void writeWorkBookXMLbyYear(String fileName, int sex, int thisScen)
			throws FileNotFoundException, FactoryConfigurationError,
			XMLStreamException, IOException {
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
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
		for (int year = 0; year < stepsInRun; year++) {
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

					} else {
						/* if details is false: then write the data of diseases */
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
		out.close();
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
	 */
	private void writeWorkBookXMLbyCohort(String fileName, int sex, int thisScen)
			throws FileNotFoundException, FactoryConfigurationError,
			XMLStreamException, IOException {
		OutputStream out = new FileOutputStream(fileName);
		log.fatal("output written to " + fileName);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
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
		/* make one worksheet per calendar year */
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

			/* write the data */
			/* each row is a risk-class / age combination */
			for (int rClass = 0; rClass < nRiskFactorClasses; rClass++)

				for (int year = 0; year < stepsInRun; year++) {
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

								toByAveragedRef[r] = meanRiskByRiskClassByAge[0][year][rClass][cohort
										+ year][sex];
								toByAveragedScen[r] = meanRiskByRiskClassByAge[thisScen][year][rClass][cohort
										+ year][sex];
								numbersRef[r] = nPopByRiskClassByAge[0][year][rClass][cohort
										+ year][sex];
								numbersScen[r] = nPopByRiskClassByAge[thisScen][year][rClass][cohort
										+ year][sex];
							}
						} else {
							toByAveragedRef = new double[nRiskFactorClasses * 2];
							toByAveragedScen = new double[nRiskFactorClasses * 2];
							numbersRef = new double[nRiskFactorClasses * 2];
							numbersScen = new double[nRiskFactorClasses * 2];
							for (int r = 0; r < nRiskFactorClasses; r++)
								for (int s = 0; s < 2; s++) {

									toByAveragedRef[r + s * nRiskFactorClasses] = meanRiskByRiskClassByAge[0][year][rClass][cohort
											+ year][s];
									toByAveragedScen[r + s * nRiskFactorClasses] = meanRiskByRiskClassByAge[thisScen][year][rClass][cohort
											+ year][s];
									numbersRef[r + s * nRiskFactorClasses] = nPopByRiskClassByAge[0][year][rClass][cohort
											+ year][s];
									numbersScen[r + s * nRiskFactorClasses] = nPopByRiskClassByAge[thisScen][year][rClass][cohort
											+ year][s];
								}
						}

						double mean = applySuccesrateToMean(toByAveragedRef,
								toByAveragedScen, numbersRef, numbersScen,
								thisScen, year, cohort + year);

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
									meanRiskByRiskClassByAge[0][year][rClass][cohort
											+ year][sex],
									meanRiskByRiskClassByAge[thisScen][year][rClass][cohort
											+ year][sex],
									nPopByRiskClassByAge[0][year][rClass][cohort
											+ year][sex],
									nPopByRiskClassByAge[thisScen][year][rClass][cohort
											+ year][sex], thisScen, year,
									cohort + year);

						} else {
							mean = applySuccesrateToMean(
									meanRiskByRiskClassByAge[0][year][rClass][cohort
											+ year],
									meanRiskByRiskClassByAge[thisScen][year][rClass][cohort
											+ year],
									nPopByRiskClassByAge[0][year][rClass][cohort
											+ year],
									nPopByRiskClassByAge[thisScen][year][rClass][cohort
											+ year], thisScen, year, cohort
											+ year);

						}

						writeCell(writer, mean);

					}

					/* write age */

					writeCell(writer, startYear + year);

					/* write total numbers in group(row) */
					double data = 0;
					if (sex < 2) {
						data = applySuccesrate(
								nPopByRiskClassByAge[0][year][rClass][cohort
										+ year][sex],
								nPopByRiskClassByAge[thisScen][year][rClass][cohort
										+ year][sex], thisScen, year, cohort
										+ year);

					} else {

						data = applySuccesrate(
								nPopByRiskClassByAge[0][year][rClass][cohort
										+ year],
								nPopByRiskClassByAge[thisScen][year][rClass][cohort
										+ year], thisScen, year, cohort + year);

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
										nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][cohort
												+ year][sex],
										nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][cohort
												+ year][sex], thisScen, year,
										cohort + year);

							} else {

								data = applySuccesrate(
										nDiseaseStateByRiskClassByAge[0][year][col - 4][rClass][cohort
												+ year],
										nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][cohort
												+ year], thisScen, year, cohort
												+ year);

							}
							writeCell(writer, data);
						}

					} else {
						/* if details is false: then write the data of diseases */
						for (int col = 4; col < nDiseases + 4; col++) {

							if (sex < 2) {
								data = applySuccesrate(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][cohort
												+ year][sex],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][cohort
												+ year][sex], thisScen, year,
										cohort + year);

							} else {

								data = applySuccesrate(
										nDiseaseByRiskClassByAge[0][year][col - 4][rClass][cohort
												+ year],
										nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][cohort
												+ year], thisScen, year, cohort
												+ year);

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
		out.close();
	}

	/**
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
	private double applySuccesrate(double[] inputRef, double[] inputScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		int nToAdd = inputRef.length;
		if (thisScen == 0)
			for (int i = 0; i < nToAdd; i++)
				data += inputRef[i];
		else {
			if (minAge[thisScen - 1] > a - year
					|| maxAge[thisScen - 1] < a - year)
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

	private double applySuccesrate(double inputRef, double inputScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		if (thisScen == 0)
			data = inputRef;
		else {
			if (minAge[thisScen - 1] > a - year
					|| maxAge[thisScen - 1] < a - year)
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
	 * of persons) it also needs the weights (numbers in each scenario)
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
		else {
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
	 * of persons) it also needs the weights (numbers in each scenario)
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
		} else {
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

	public JFreeChart makeSurvivalPlot(String outcomeName, int gender) {
		XYDataset xyDataset = null;

		for (int thisScen = 0; thisScen <= nScen; thisScen++) {
			XYSeries series = new XYSeries("Survival scenario " + thisScen);

			for (int steps = 0; steps < stepsInRun; steps++) {
				double indat = 0;
				/*
				 * popByAge has value 1 at steps= 0) // TODO this does not work
				 * OK when ageMax and min are applied
				 */
				for (int age = 0; age < 96 + stepsInRun; age++)
					indat += applySuccesrate(pPopByAge[0][steps][age][gender],
							pPopByAge[thisScen][steps][age][gender], thisScen,
							steps, age);

				series.add((double) steps, indat / 95);
			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(series);
			else
				((XYSeriesCollection) xyDataset).addSeries(series);
		}
		JFreeChart chart = ChartFactory.createXYLineChart(outcomeName,
				"years of simulation", "survival", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		String label;
		if (gender == 0)
			label = "men";
		else
			label = "women";
		ChartFrame frame1 = new ChartFrame("Survival Chart " + label, chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);
		try {
			writeCategoryChart(baseDir + File.separator + "simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "survivalplot_" + label
					+ ".jpg", chart);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart.");
		}
		return chart;
	}

	public void makePrevalencePlots(int thisScen) {

		for (int d = 0; d < nDiseases; d++) {

			XYSeries menSeries = new XYSeries(diseaseNames[d]

			+ " prevalence in men");
			XYSeries womenSeries = new XYSeries(diseaseNames[d]

			+ " prevalence in women");

			for (int steps = 0; steps < stepsInRun; steps++) {
				double indat = 0;
				for (int age = 0; age < 96 + stepsInRun; age++)
					indat += applySuccesrate(
							pDiseaseByAge[0][steps][d][age][0],
							pDiseaseByAge[thisScen][steps][d][age][0],
							thisScen, steps, age);

				menSeries.add((double) steps, indat);
				indat = 0;
				for (int age = 0; age < 96 + stepsInRun; age++)
					indat += applySuccesrate(
							pDiseaseByAge[0][steps][d][age][1],
							pDiseaseByAge[thisScen][steps][d][age][1],
							thisScen, steps, age);

				womenSeries.add((double) steps, indat);

			}
			XYDataset xyDataset = new XYSeriesCollection(menSeries);
			((XYSeriesCollection) xyDataset).addSeries(womenSeries);

			JFreeChart chart = ChartFactory.createXYLineChart(diseaseNames[d],
					"years of simulation", "prevalence rate", xyDataset,
					PlotOrientation.VERTICAL, true, true, false);
			ChartFrame frame1 = new ChartFrame(diseaseNames[d] + " prevalence",
					chart);
			frame1.setVisible(true);
			frame1.setSize(300, 300);

			final ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

			try {
				writeCategoryChart(baseDir + File.separator + "simulations"
						+ File.separator + simulationName + File.separator
						+ "results" + File.separator + "chartPrevalence" + d
						+ ".jpg", chart);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out
						.println("Problem occurred creating chart. for diseasenumber"
								+ d);
			}

		}
	}

	public void makePrevalenceByRiskFactorPlots(int thisScen) {
		XYDataset xyDataset = null;

		for (int d = 0; d < nDiseases; d++) {

			for (int r = 0; r < nRiskFactorClasses; r++) {

				XYSeries menSeries = new XYSeries(diseaseNames[d]
						+ " prevalence in men, risk factor class " + r);
				XYSeries womenSeries = new XYSeries(diseaseNames[d]
						+ " prevalence in women, risk factor class " + r);

				for (int steps = 0; steps < stepsInRun; steps++) {
					double indat = 0;

					for (int age = 0; age < 96 + stepsInRun; age++)
						indat += applySuccesrate(
								pDiseaseByRiskClassByAge[0][steps][d][r][age][0],
								pDiseaseByRiskClassByAge[thisScen][steps][d][r][age][0],
								thisScen, steps, age);
					menSeries.add((double) steps, indat);
					indat = 0;

					for (int age = 0; age < 96 + stepsInRun; age++)
						indat += applySuccesrate(
								pDiseaseByRiskClassByAge[0][steps][d][r][age][1],
								pDiseaseByRiskClassByAge[thisScen][steps][d][r][age][1],
								thisScen, steps, age);
					womenSeries.add((double) steps, indat);

				}
				if (r == 0)
					xyDataset = new XYSeriesCollection(menSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(menSeries);
				((XYSeriesCollection) xyDataset).addSeries(womenSeries);
			}
			JFreeChart chart = ChartFactory.createXYLineChart(diseaseNames[d],
					"years of simulation", "prevalence rate", xyDataset,
					PlotOrientation.VERTICAL, true, true, false);
			ChartFrame frame1 = new ChartFrame(diseaseNames[d]
					+ " prevalence by risk factor", chart);
			frame1.setVisible(true);
			frame1.setSize(300, 300);

			final ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

			try {

				ChartUtilities.saveChartAsJPEG(new File(baseDir
						+ File.separator + "simulations" + File.separator
						+ simulationName + File.separator + "results"
						+ File.separator + "chartPrevalenceByRiskClass" + d
						+ "scen" + thisScen + ".jpg"), chart, 500, 300);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out
						.println("Problem occurred creating chart. for diseasenumber"
								+ d);
			}

		}
	}

	public void makeRiskFactorPlots(int thisScen) {

		XYDataset xyDataset = null;
		for (int r = 0; r < nRiskFactorClasses; r++) {

			XYSeries menSeries = new XYSeries(" riskfactor prevalence in men");
			XYSeries womenSeries = new XYSeries(
					"riskfactor prevalence in women");

			for (int steps = 0; steps < stepsInRun; steps++) {
				double indat = 0;

				for (int age = 0; age < 96 + stepsInRun; age++)
					indat += applySuccesrate(
							pPopByRiskClassByAge[0][steps][r][age][0],
							pPopByRiskClassByAge[thisScen][steps][r][age][0],
							thisScen, steps, age);
				menSeries.add((double) steps, indat);
				indat = 0;

				for (int age = 0; age < 96 + stepsInRun; age++)
					indat += applySuccesrate(
							pPopByRiskClassByAge[0][steps][r][age][1],
							pPopByRiskClassByAge[thisScen][steps][r][age][1],
							thisScen, steps, age);

				womenSeries.add((double) steps, indat);

			}
			xyDataset = new XYSeriesCollection(menSeries);
			((XYSeriesCollection) xyDataset).addSeries(womenSeries);
		}

		JFreeChart chart = ChartFactory.createXYLineChart("riskfactor",
				"years of simulation", "prevalence rate", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		try {
			writeCategoryChart(baseDir + File.separator + "simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator
					+ "chartRiskFactorPrevalence.jpg", chart);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out
					.println("Problem occurred creating chart. for riskfactor");
		}

	}

	public void makeLifeExpectancyPlot() {

		/*
		 * for (int steps = 0; steps < stepsInRun; steps++) { double indat = 0;
		 * 
		 * for (int age = 0; age < 96 + stepsInRun; age++) indat +=
		 * applySuccesrate(pPopByAge[0][steps][age][gender],
		 * pPopByAge[thisScen][steps][age][gender], thisScen, steps, age);
		 * 
		 * series.add((double) steps, indat / 95);
		 */

		double[][] lifeExp = new double[nScen + 1][2];
		double baselinePop = 0;
		for (int scenario = 0; scenario < nScen + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				baselinePop = 0;
				for (int steps = 0; steps < stepsInRun; steps++)
					for (int age = 0; age < 96 + stepsInRun; age++) {
						lifeExp[scenario][s] += applySuccesrate(
								pPopByAge[0][steps][age][s],
								pPopByAge[scenario][steps][age][s], scenario,
								steps, age);
						if (steps == 0)
							baselinePop += applySuccesrate(
									pPopByAge[0][steps][age][s],
									pPopByAge[scenario][steps][age][s],
									scenario, steps, age);
					}

				if (baselinePop != 0)
					lifeExp[scenario][s] = lifeExp[scenario][s] / baselinePop;
				else
					lifeExp[scenario][s] = 0;

			}

		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				"scenario ", "gender ", lifeExp);

		JFreeChart chart = ChartFactory.createBarChart("LifeExpectancy", "",
				"years", dataset, PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);

		try {

			writeCategoryChart(baseDir + File.separator + "simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "chartLifeExpectancy.jpg",
					chart);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out
					.println("Problem occurred creating chart. for lifeExpectancy");
		}
	}

	public void makePopulationPyramidPlot(int thisScen, int timestep) {

		/*
		 * ChartFactory.createBarChart( "Bar Chart Demo", // chart title
		 * "Category", // domain axis label "Value", // range axis label
		 * dataset, // data PlotOrientation.VERTICAL, // orientation true, //
		 * include legend true, // tooltips? false // URLs?
		 * 
		 * 
		 * // set the background color for the chart...
		 * chart.setBackgroundPaint(Color.white);
		 * 
		 * ); // get a reference to the plot for further customisation... final
		 * CategoryPlot plot = chart.getCategoryPlot();
		 * plot.setBackgroundPaint(Color.lightGray);
		 * plot.setDomainGridlinePaint(Color.white);
		 * plot.setRangeGridlinePaint(Color.white); //set the range axis to
		 * display integers only... final NumberAxis rangeAxis = (NumberAxis)
		 * plot.getRangeAxis();
		 * rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		 * 
		 * // disable bar outlines... final BarRenderer renderer = (BarRenderer)
		 * plot.getRenderer(); renderer.setDrawBarOutline(false);
		 * 
		 * // set up gradient paints for series... final GradientPaint gp0 = new
		 * GradientPaint( 0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray );
		 * final GradientPaint gp1 = new GradientPaint( 0.0f, 0.0f, Color.green,
		 * 0.0f, 0.0f, Color.lightGray ); final GradientPaint gp2 = new
		 * GradientPaint( 0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray );
		 * renderer.setSeriesPaint(0, gp0); renderer.setSeriesPaint(1, gp1);
		 * renderer.setSeriesPaint(2, gp2);
		 * 
		 * org.jfree.chart.renderer.category.CategoryItemRendererState
		 * setBarWidth(double)
		 */

		double[][] pyramidData1 = new double[2][100];
		double[][] pyramidData2 = new double[2][100];
		double[][] nPopByAge = new double[100][2];
		double[][] nRefPopByAge = new double[100][2];
		for (int a = 0; a < 100; a++) {
			for (int r = 0; r < nRiskFactorClasses; r++) {
				nPopByAge[a][0] += applySuccesrate(
						pPopByRiskClassByAge[0][timestep][r][a][0],
						pPopByRiskClassByAge[thisScen][timestep][r][a][0],
						thisScen, timestep, a);
				nPopByAge[a][1] += applySuccesrate(
						pPopByRiskClassByAge[0][timestep][r][a][1],
						pPopByRiskClassByAge[thisScen][timestep][r][a][1],
						thisScen, timestep, a);
				nRefPopByAge[a][0] += pPopByRiskClassByAge[0][timestep][r][a][0];
				nRefPopByAge[a][1] += pPopByRiskClassByAge[0][timestep][r][a][1];

			}
			pyramidData1[0][a] = nRefPopByAge[a][0];
			pyramidData2[0][a] = -nRefPopByAge[a][1];
			// TODO hoe aanpakken als effect van richting verschilt per
			// leeftijdsgroep
			if (nPopByAge[a][0] >= nRefPopByAge[a][0]) {
				pyramidData1[1][a] = nPopByAge[a][0] - nRefPopByAge[a][0];
				pyramidData2[1][a] = -nPopByAge[a][1] + nRefPopByAge[a][1];
			} else {
				pyramidData1[1][a] = -nPopByAge[a][0] + nRefPopByAge[a][0];
				pyramidData2[1][a] = nPopByAge[a][1] - nRefPopByAge[a][1];
			}
		}
		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(" ",
				"age", pyramidData1);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(" ",
				"age", pyramidData2);

		JFreeChart chart = ChartFactory.createStackedBarChart("LifeExpectancy",
				"", "population size", dataset1, PlotOrientation.HORIZONTAL,
				false, false, false);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setDataset(1, dataset2);
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setItemMargin(0.0);
		renderer.setDrawBarOutline(true);

		ChartFrame frame = new ChartFrame("LifeExpectancy Chart", chart);
		final CategoryAxis domainAxis = new CategoryAxis("PopulationNumbers");
		renderer.setItemMargin(0.0);

		renderer.setItemLabelAnchorOffset(9.0);
		renderer.setSeriesPaint(0, Color.white);
		renderer.setSeriesPaint(1, Color.pink);
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

		frame.setVisible(true);
		frame.setSize(200, 200);
		frame.pack();

		try {
			writeCategoryChart(baseDir + File.separator + "simulations"
					+ File.separator + simulationName + File.separator
					+ "results" + File.separator + "chartPyramid.jpg", chart);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart. for Pyramid");
		}

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
			throws Exception {
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
			throw new Exception(e.getMessage());
		}
	}

}
