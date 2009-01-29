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
	double pPop[][][]; /* percentage of survivors by scenario, time and sex */
	double pPopByAge[][][][];/*
							 * percentage of survivors by scenario, age, time,
							 * and sex
							 */
	double pPopByRiskClass[][][][]; /*
									 * percentage of survivors of each risk
									 * class by scenario,risk class time and sex
									 */

	double pRiskClass[][][][]; /*
								 * prevalence of each risk class by
								 * scenario,risk class time and sex
								 */
	double pDisease[][][][];/*
							 * prevalence of disease ; indexes are
							 * scenario,time,disease, and sex
							 */
	double pPopPerRiskClass[][][][]; /*
									 * percentage of survivors by scenario,
									 * time, risk class and sex
									 */

	double pDiseaseStateByRiskClassByAge[][][][][][]; /*
													 * number of survivors in
													 * scenario each
													 * diseaseState by scenario,
													 * time, disease state risk
													 * class and age sex
													 */
	double pDiseaseStateByRiskClassByAge_scen[][][][][]; /*
														 * number of survivors
														 * in scenario each
														 * diseaseState by time,
														 * disease state risk
														 * class and age sex
														 */

	double nDiseaseStateByRiskClassByAge[][][][][][]; /*
													 * number of survivors in
													 * each diseaseState by
													 * scenario, time,disease
													 * state risk class age and
													 * sex TODO
													 */

	double pDiseasePerRiskClass[][][][][];/*
										 * prevalence of disease ; indexes are
										 * scenario,time disease,risk class and
										 * sex
										 */
	double nDiseasePerRiskClass[][][][][];/*
										 * numbers for each disease/risk factor
										 * combi ; indexes are
										 * scenario,time,disease, risk class and
										 * sex
										 */
	double pDiseaseByAge[][][][][];/*
									 * numbers for each disease/risk factor
									 * combi ; indexes are
									 * scenario,time,disease, risk class and sex
									 */
	// TODO: not yet filled
	double pDiseaseByRiskClassByAge[][][][][][];/*
												 * prevalence of disease ;
												 * indexes are scenario time,
												 * disease, risk class age, and
												 * sex
												 */
	double pDiseaseByRiskClassByAge_scen[][][][][];/*
													 * prevalence of disease ;
													 * indexes are time,
													 * disease, risk class age,
													 * and sex
													 */

	double nDiseaseByRiskClassByAge[][][][][][];/*
												 * number of persons with each
												 * disease ; indexes are
												 * scenario,time disease, risk
												 * class age, and sex
												 */

	double[][][][][] pPopByRiskClassByAge; /*
											 * prevalence of risk class by Age;
											 * indexes are: scenario time risk
											 * class age, and sex
											 */
	double[][][][] pPopByRiskClassByAge_scen; /*
											 * prevalence of risk class by Age;
											 * indexes are: time risk class age,
											 * and sex
											 */

	double[][][][][] nPopByRiskClassByAge; /*
											 * number in risk class by Age;
											 * indexes are: scenario, time risk
											 * class age and sex
											 */

	/* these data are copied by the constructor from the object "scenario.info" */
	DiseaseClusterStructure[] structure;
	int riskType;
	int nScen;
	int stepsInRun;
	int startYear;
	int nDiseases;
	int nRiskFactorClasses;
	int nDiseaseStates;
	boolean details;
	float[][] populationSize;
	int[][] newborns;
	float mfratio;
	String[] riskClassnames;
	float[][][][] newPrevalence;
	float[][][] oldPrevalence;
	String baseDir;
	String simulationName;
	/*
	 * categorized indicates whether the continuous variable is categorized in
	 * the output
	 */
	boolean categorized = false;

	public DynamoOutputFactory(ScenarioInfo scenInfo, String simName) {
		super();

		baseDir = BaseDirectory.getInstance("c:\\hendriek\\java\\dynamohome\\")
				.getBaseDir();
		this.simulationName = simName;
		setRiskType(scenInfo.riskType);
		setNScen(scenInfo.getNScenarios());
		setStepsInRun(scenInfo.yearsInRun);
		setStructure(scenInfo.structure);
		setNDiseases(scenInfo.structure);
		setNDiseaseStates(scenInfo.structure);
		details = scenInfo.details;

		startYear = scenInfo.startYear;
		populationSize = scenInfo.populationSize;
		newborns = scenInfo.newborns;
		mfratio = scenInfo.maleFemaleRatio;
		riskClassnames = scenInfo.riskClassnames;
		oldPrevalence = scenInfo.oldPrevalence;

		newPrevalence = scenInfo.newPrevalence;
		if (newPrevalence == null)
			newPrevalence = new float[1][][][];
		newPrevalence[0] = oldPrevalence;

		if (getRiskType() == 1 || getRiskType() == 3)
			setNRiskFactorClasses(scenInfo.riskClassnames.length);
		else if (categorized) {// TODO}

		} else
			setNRiskFactorClasses(1);
		int nClasses = getNRiskFactorClasses();
		pPop = new double[nScen + 1][stepsInRun][2];
		pPopByAge = new double[nScen + 1][stepsInRun][stepsInRun + 96][2];
		pPopByRiskClass = new double[nScen + 1][stepsInRun][nClasses][2];
		pRiskClass = new double[nScen + 1][stepsInRun][nClasses][2];
		pDisease = new double[nScen + 1][stepsInRun][nDiseases][2];

		pPopByRiskClassByAge = new double[nScen + 1][stepsInRun][nClasses][96 + stepsInRun][2];
		pPopByRiskClassByAge_scen = new double[stepsInRun][nClasses * nClasses][96 + stepsInRun][2];

		nPopByRiskClassByAge = new double[nScen + 1][stepsInRun][nClasses][96 + stepsInRun][2];

		pDiseaseByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseases][nClasses][96 + stepsInRun][2];
		pDiseaseByRiskClassByAge_scen = new double[stepsInRun][nDiseases][nClasses
				* nClasses][96 + stepsInRun][2];
		pDiseaseStateByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseaseStates][nClasses][96 + stepsInRun][2];
		pDiseaseStateByRiskClassByAge_scen = new double[stepsInRun][nDiseaseStates][nClasses
				* nClasses][96 + stepsInRun][2];
		nDiseaseStateByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseaseStates][nClasses][96 + stepsInRun][2];

		nDiseaseByRiskClassByAge = new double[nScen + 1][stepsInRun][nDiseases][nClasses][96 + stepsInRun][2];

		pDiseasePerRiskClass = new double[nScen + 1][stepsInRun][nDiseases][nClasses][2];
		pDiseaseByAge = new double[nScen + 1][stepsInRun][nDiseases][96 + stepsInRun][2];

		for (int sex = 0; sex < 2; sex++)
			for (int age = 0; age < stepsInRun + 96; age++)
				for (int steps = 0; steps < stepsInRun; steps++)
					for (int r = 0; r < nRiskFactorClasses * nRiskFactorClasses; r++) {
						pPopByRiskClassByAge_scen[steps][r][age][sex] = 0;
						for (int nDis = 0; nDis < nDiseases; nDis++)
							pDiseaseByRiskClassByAge_scen[steps][nDis][r][age][sex] = 0;
						for (int nState = 0; nState < nDiseaseStates; nState++)
							pDiseaseStateByRiskClassByAge_scen[steps][nState][r][age][sex] = 0;
					}

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

	public void makeOutput(Population[] pop) {

		// TODO for scenario's with initial population changes
		int sexIndex = 0;
		int ageIndex = 0;

		int[] nInSimulation = new int[2];
		int[][] nInSimulationByAge = new int[96 + stepsInRun][2];
		int[][][] nInSimulationByRiskClassByAge = new int[nRiskFactorClasses][96 + stepsInRun][2];
		double weight[][][] = new double[nRiskFactorClasses][96][2];
		float[] compoundData;
		float survival;

		/*
		 * get information on the number of simulation subjects per risk class and use these to
		 * calculate weighting factors for individuals in order to calculated
		 * all outcomes valid for a population with the distribution of risk
		 * factors given in oldPrevalence
		 */
		Iterator<Individual> individualIterator1 = pop[0].iterator();

		while (individualIterator1.hasNext()) {
			Individual individual = individualIterator1.next();

			nInSimulation[sexIndex]++;
			nInSimulationByAge[ageIndex][sexIndex]++;
			ageIndex = (int) Math
					.round(((Float) individual.get(1).getValue(0)));
			sexIndex = (int) (Integer) individual.get(2).getValue(0);

			if (riskType != 2) {
				int riskFactor = (int) (Integer) individual.get(3).getValue(0);
				nInSimulationByRiskClassByAge[riskFactor][ageIndex][sexIndex]++;

			}

		}
		for (int s = 0; s < 2; s++)
			for (int age = 0; age < 96; age++) {
				// TODO for scenario's

				for (int r = 0; r < nRiskFactorClasses; r++)
					if (riskType != 2)
						weight[r][age][s] = oldPrevalence[age][s][r]
								* nInSimulationByAge[age][s]
								/ nInSimulationByRiskClassByAge[r][age][s];
					else
						weight[r][age][s] = 1;

			}
		int nPopulations = pop.length;
		
		
		

		for (int thisScen = 0; thisScen < nPopulations; thisScen++) {

			Iterator<Individual> individualIterator = pop[thisScen].iterator();

			// now make detailed arrays summing the data for sex/age/year/risk
			// class combinations
			while (individualIterator.hasNext()) {
				Individual individual = individualIterator.next();

				for (int stepCount = 0; stepCount < stepsInRun; stepCount++) {

					ageIndex = (int) Math.round(((Float) individual.get(1)
							.getValue(stepCount)));
					sexIndex = (int) (Integer) individual.get(2).getValue(
							stepCount);
					int riskFactor = 0;
					if (riskType != 2)
						riskFactor = (int) (Integer) individual.get(3)
								.getValue(stepCount);
					else {
						riskFactor = 0;
					}
					// TODO lezen continue

					if (riskType == 2)
						compoundData = ((CompoundCharacteristicValue) individual
								.get(5)).getUnwrappedValue(stepCount);
					else
						compoundData = ((CompoundCharacteristicValue) individual
								.get(4)).getUnwrappedValue(stepCount);
					survival = compoundData[getNDiseaseStates() - 1];
					/*
					 * pPop[thisScen][stepCount][sexIndex] +=
					 * survivalweight[riskFactor][ageIndex][sexIndex];
					 * 
					 * 
					 * pPopByAge[thisScen][stepCount][ageIndex][sexIndex] +=
					 * survivalweight[riskFactor][ageIndex][sexIndex]; if
					 * (details)
					 * pPopByRiskClass[thisScen][stepCount][riskFactor]
					 * [sexIndex] +=
					 * survivalweight[riskFactor][ageIndex][sexIndex]; if
					 * (details)
					 */

					pPopByRiskClassByAge[thisScen][stepCount][riskFactor][ageIndex][sexIndex] += survival;
					int currentDisease = 0;

					int currentClusterStartNumber = 0;
					for (int s = 0; s < nDiseaseStates; s++) {
						pDiseaseStateByRiskClassByAge[thisScen][stepCount][s][riskFactor][ageIndex][sexIndex] += compoundData[s]
								* survival;

					}
					for (int c = 0; c < structure.length; c++) {

						for (int d = 0; d <= structure[c].getNinCluster(); d++) {

							for (int s = 1; s < Math.pow(2, structure[c]
									.getNinCluster()); s++) {

								if ((s & (1 << d)) == (1 << d)) {
									/*
									 * pDisease[thisScen][stepCount][currentDisease
									 * + d][sexIndex] +=
									 * compoundData[currentState + s - 1]
									 * survival
									 * weight[riskFactor][ageIndex][sexIndex];
									 * if (details)
									 */
									pDiseaseByRiskClassByAge[thisScen][stepCount][currentDisease
											+ d][riskFactor][ageIndex][sexIndex] += compoundData[currentClusterStartNumber
											+ s - 1]
											* survival;

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
						currentDisease += structure[c].getNinCluster();
						currentClusterStartNumber += Math.pow(2, structure[c]
								.getNinCluster()) - 1;

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

			/*
			 * weight for the fact that the risk factor distribution in the
			 * initial population is not exactly equal to the risk factor
			 * distribution as intended and make summary arrays In the weights,
			 * the index a refers to the age at the start of the simulation
			 * 
			 * also make absolute numbers, using the population size at the
			 * start of the simulation
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

						for (int r = 0; r < nRiskFactorClasses; r++) {
							/*
							 * weight to remove riskfactor disbalance in
							 * simulated population
							 */

							pPopByRiskClassByAge[thisScen][stepCount][r][a][s] *= weight[r][originalAge][s];
							/* make summary arrays */
							pPop[thisScen][stepCount][s] += pPopByRiskClassByAge[thisScen][stepCount][r][a][s];
							pPopByAge[thisScen][stepCount][a][s] += pPopByRiskClassByAge[thisScen][stepCount][r][a][s];
							pPopByRiskClass[thisScen][stepCount][r][s] += pPopByRiskClassByAge[thisScen][stepCount][r][a][s];

							for (int d = 0; d < nDiseases; d++) {
								/*
								 * weight to remove riskfactor disbalance in
								 * simulated population
								 */

								pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s] *= weight[r][originalAge][s];
								/* make summary arrays */
								pDisease[thisScen][stepCount][d][s] += pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s];

								pDiseasePerRiskClass[thisScen][stepCount][d][r][s] += pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s];

								pDiseaseByAge[thisScen][stepCount][d][r][s] += pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s];

							}

							for (int state = 0; state < nDiseaseStates; state++) {
								/*
								 * weight to remove riskfactor disbalance in
								 * simulated population
								 */

								pDiseaseStateByRiskClassByAge[thisScen][stepCount][state][r][a][s] *= weight[r][originalAge][s];

								/*
								 * only needed for excel-readable output-xml
								 * files, so no summaries made
								 */

							}

							/* calculate absolute numbers */

							nPopByRiskClassByAge[thisScen][stepCount][r][a][s] = ratio
									* pPopByRiskClassByAge[thisScen][stepCount][r][a][s];

							for (int d = 0; d < nDiseases; d++) {

								nDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s] = ratio
										* pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s];

							}
							for (int state = 0; state < nDiseaseStates; state++) {

								nDiseaseStateByRiskClassByAge[thisScen][stepCount][state][r][a][s] = ratio
										* pDiseaseStateByRiskClassByAge[thisScen][stepCount][state][r][a][s];

							}
						}
					}// end loop sex
				}// end loop age

				/*
				 * uptill now all arrays contain the (weighted) sums of the
				 * simulated population in the category now make those into
				 * percentages by dividing by the right denominator (indicated
				 * by the name, and also by sex and timeStep (stepCount) and
				 * scenario
				 */

				for (int s = 0; s < 2; s++) {
					/* summary arrays for the disease prevalences */
					for (int d = 0; d < nDiseases; d++) {
						if (pPop[thisScen][stepCount][s] != 0)
							pDisease[thisScen][stepCount][d][s] = pDisease[thisScen][stepCount][d][s]
									/ pPop[thisScen][stepCount][s];
						else
							pDisease[thisScen][stepCount][d][s] = 0;
						for (int a = 0; a < 96; a++)

							if (pPopByAge[thisScen][stepCount][a][s] != 0)
								pDiseaseByAge[thisScen][stepCount][d][a][s] = pDiseaseByAge[thisScen][stepCount][d][a][s]
										/ pPopByAge[thisScen][stepCount][a][s];
							else
								pDiseaseByAge[thisScen][stepCount][d][a][s] = 0;

						for (int r = 0; r < nRiskFactorClasses; r++) {
							if (pPopByRiskClass[thisScen][stepCount][r][s] != 0)
								pDiseasePerRiskClass[thisScen][stepCount][d][r][s] = pDiseasePerRiskClass[thisScen][stepCount][d][r][s]
										/ pPopByRiskClass[thisScen][stepCount][r][s];
							else
								pDiseasePerRiskClass[thisScen][stepCount][d][r][s] = 0;
							for (int a = 0; a < 96; a++)
								if (pPopByRiskClassByAge[thisScen][stepCount][r][a][s] != 0)
									pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s] = pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s]
											/ pPopByRiskClassByAge[thisScen][stepCount][r][a][s];
								else
									pDiseaseByRiskClassByAge[thisScen][stepCount][d][r][a][s] = 0;

						}
					}
					/* summary arrays for risk factor prevalence */

					for (int r = 0; r < nRiskFactorClasses; r++) {
						if (pPop[thisScen][stepCount][s] != 0)
							pRiskClass[thisScen][stepCount][r][s] = pPopByRiskClass[thisScen][stepCount][r][s]
									/ pPop[thisScen][stepCount][s];
						else
							pRiskClass[thisScen][stepCount][r][s] = 0;

					}
					if (nInSimulation[s] != 0)
						pPop[thisScen][stepCount][s] = pPop[thisScen][stepCount][s]
								/ nInSimulation[s];
					else
						pPop[thisScen][stepCount][s] = 0;
					for (int a = 0; a < 96; a++)
						if (nInSimulationByAge[a][s] != 0)
							pPopByAge[thisScen][stepCount][a][s] = pPopByAge[thisScen][stepCount][a][s]
									/ nInSimulationByAge[a][s];
						else
							pPopByAge[thisScen][a][stepCount][s] = 0;

				}// end loop sex
			}// end loop stepcount
		}// end loop scenarios
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
		details = true;
		for (int scen = 0; scen < 1; scen++) {
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
				writer.writeStartElement("Cell");
				writer.writeStartElement("Data");
				writer.writeAttribute("ss:Type", "String");
				writer.writeCharacters("riskClass");
				writer.writeEndElement();
				writer.writeEndElement();
			} else {
				writer.writeStartElement("Cell");
				writer.writeStartElement("Data");
				writer.writeAttribute("ss:Type", "String");
				writer.writeCharacters("mean_riskFactor");
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeStartElement("Cell");
				writer.writeStartElement("Data");
				writer.writeAttribute("ss:Type", "String");
				writer.writeCharacters("std_riskFactor");
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeStartElement("Cell");
				writer.writeStartElement("Data");
				writer.writeAttribute("ss:Type", "String");
				writer.writeCharacters("skewness");
				writer.writeEndElement();
				writer.writeEndElement();
			}
			if (riskType == 3) {

				writer.writeStartElement("Cell");
				writer.writeStartElement("Data");
				writer.writeAttribute("ss:Type", "String");
				writer.writeCharacters("mean duration");
				writer.writeEndElement();
				writer.writeEndElement();
			}

			/* age */
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("age");
			writer.writeEndElement();
			writer.writeEndElement();

			/* total number */
			writer.writeStartElement("Cell");
			writer.writeStartElement("Data");
			writer.writeAttribute("ss:Type", "String");
			writer.writeCharacters("total number");
			writer.writeEndElement();
			writer.writeEndElement();

			/* disease info */
			if (details) {
				for (int col = 4; col < nDiseaseStates + 4; col++) {
					writer.writeStartElement("Cell");
					writer.writeStartElement("Data");
					writer.writeAttribute("ss:Type", "String");
					writer.writeCharacters("StateName" + col);
					// TODO: goede naam laten printen
					writer.writeEndElement();
					writer.writeEndElement();
				}
			} else {
				for (int col = 4; col < nDiseases + 4; col++) {
					writer.writeStartElement("Cell");
					writer.writeStartElement("Data");
					writer.writeAttribute("ss:Type", "String");
					writer.writeCharacters("DiseaseName" + col);
					// TODO: goede naam laten printen
					writer.writeEndElement();
					writer.writeEndElement();
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

						writer.writeStartElement("Cell");
						writer.writeStartElement("Data");
						writer.writeAttribute("ss:Type", "String");
						writer.writeCharacters(riskClassnames[rClass]);

						// TODO laten werken voor categorized continue
						writer.writeEndElement();
						writer.writeEndElement();
					} else

					{
						writer.writeStartElement("Cell");
						writer.writeStartElement("Data");
						writer.writeAttribute("ss:Type", "Number");
						writer.writeCharacters(((Integer) rClass).toString());
						// TODO vervangen door mean risk factor

						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeStartElement("Cell");
						writer.writeStartElement("Data");
						writer.writeAttribute("ss:Type", "Number");
						writer.writeCharacters(((Integer) rClass).toString());
						// TODO vervangen door std risk factor

						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeStartElement("Cell");
						writer.writeStartElement("Data");
						writer.writeAttribute("ss:Type", "Number");
						writer.writeCharacters(((Integer) rClass).toString());
						// TODO vervangen door skewness risk factor

						writer.writeEndElement();
						writer.writeEndElement();

					}

					if (riskType == 3) {
						writer.writeStartElement("Cell");
						writer.writeStartElement("Data");
						writer.writeAttribute("ss:Type", "Number");
						writer.writeCharacters(((Integer) a).toString());
						// TODO: vervangen door mean duration
						writer.writeEndElement();
						writer.writeEndElement();
					}

					/* write age */

					writer.writeStartElement("Cell");
					writer.writeStartElement("Data");
					writer.writeAttribute("ss:Type", "Number");
					writer.writeCharacters(((Integer) a).toString());
					writer.writeEndElement();
					writer.writeEndElement();

					/* write total numbers in group(row) */

					writer.writeStartElement("Cell");
					writer.writeStartElement("Data");
					writer.writeAttribute("ss:Type", "Number");
					if (sex < 2)
						writer
								.writeCharacters(((Double) nPopByRiskClassByAge[thisScen][year][rClass][a][sex])
										.toString());
					else
						writer
								.writeCharacters(((Double) (nPopByRiskClassByAge[thisScen][year][rClass][a][0] + nPopByRiskClassByAge[thisScen][year][rClass][a][1]))
										.toString());

					;
					writer.writeEndElement();
					writer.writeEndElement();

					/* write disease info */

					if (details) {
						/*
						 * last diseaseState is the survival, that is already
						 * part of the file thus not needed here; if +3 is made
						 * +4 then also survival is in the output
						 */
						for (int col = 4; col < nDiseaseStates + 3; col++) {
							writer.writeStartElement("Cell");
							writer.writeStartElement("Data");
							writer.writeAttribute("ss:Type", "Number");

							if (sex < 2)
								writer
										.writeCharacters(((Double) nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex])
												.toString());
							else
								writer
										.writeCharacters(((Double) (nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][0] + nDiseaseStateByRiskClassByAge[thisScen][year][col - 4][rClass][a][1]))
												.toString());

							writer.writeEndElement();
							writer.writeEndElement();
						}
					} else {

						for (int col = 4; col < nDiseases + 4; col++) {
							writer.writeStartElement("Cell");
							writer.writeStartElement("Data");
							writer.writeAttribute("ss:Type", "Number");
							if (sex < 2)
								writer
										.writeCharacters(((Double) nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][sex])
												.toString());
							else
								writer
										.writeCharacters(((Double) (nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][0] + nDiseaseByRiskClassByAge[thisScen][year][col - 4][rClass][a][1]))
												.toString());

							writer.writeEndElement();
							writer.writeEndElement();
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

	public JFreeChart makeSurvivalPlot(String outcomeName, int thisScen) {

		XYSeries menSeries = new XYSeries("Survival rates men");
		XYSeries womenSeries = new XYSeries("Survival rates women");

		for (int steps = 0; steps < stepsInRun; steps++) {
			menSeries.add((double) steps, pPop[thisScen][steps][0]);
			womenSeries.add((double) steps, pPop[thisScen][steps][1]);

		}

		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);

		JFreeChart chart = ChartFactory.createXYLineChart(outcomeName,
				"years of simulation", "survival", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("Survival Chart scenario "
				+ thisScen, chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);
		try {

			ChartUtilities
					.saveChartAsJPEG(new File(baseDir + File.separator
							+ "simulations" + File.separator + simulationName
							+ File.separator + "results" + File.separator
							+ "survivalplot_scen" + thisScen + ".jpg"), chart,
							500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Problem occurred creating chart.");
		}
		return chart;
	}

	public void makePrevalencePlots(int thisScen) {
		for (int c = 0; c < structure.length; c++) {
			for (int d = 0; d < structure[c].getNinCluster(); d++) {

				XYSeries menSeries = new XYSeries(structure[c].diseaseName
						.get(d)
						+ " prevalence in men");
				XYSeries womenSeries = new XYSeries(structure[c].diseaseName
						.get(d)
						+ " prevalence in women");
				int dNumber = structure[c].diseaseNumber[d];
				for (int steps = 0; steps < stepsInRun; steps++) {
					menSeries.add((double) steps,
							pDisease[thisScen][steps][dNumber][0]);
					womenSeries.add((double) steps,
							pDisease[thisScen][steps][dNumber][1]);

				}
				XYDataset xyDataset = new XYSeriesCollection(menSeries);
				((XYSeriesCollection) xyDataset).addSeries(womenSeries);

				JFreeChart chart = ChartFactory.createXYLineChart(
						structure[c].diseaseName.get(d), "years of simulation",
						"prevalence rate", xyDataset, PlotOrientation.VERTICAL,
						true, true, false);
				ChartFrame frame1 = new ChartFrame("DiseasePrevalence", chart);
				frame1.setVisible(true);
				frame1.setSize(300, 300);

				final ChartPanel chartPanel = new ChartPanel(chart);
				chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

				try {

					ChartUtilities.saveChartAsJPEG(new File(baseDir
							+ File.separator + "simulations" + File.separator
							+ simulationName + File.separator + "results"
							+ File.separator + "chartPrevalence" + dNumber
							+ ".jpg"), chart, 500, 300);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out
							.println("Problem occurred creating chart. for diseasenumber"
									+ dNumber);
				}

			}

		}
	}

	public void makePrevalenceByRiskFactorPlots(int thisScen) {
		XYDataset xyDataset = null;
		for (int c = 0; c < structure.length; c++) {
			for (int d = 0; d < structure[c].getNinCluster(); d++) {
				int dNumber = structure[c].diseaseNumber[d];
				for (int r = 0; r < nRiskFactorClasses; r++) {
					XYSeries menSeries = new XYSeries(structure[c].diseaseName
							.get(d)
							+ " prevalence in men, risk factor class " + r);
					XYSeries womenSeries = new XYSeries(
							structure[c].diseaseName.get(d)
									+ " prevalence in women, risk factor class "
									+ r);

					for (int steps = 0; steps < stepsInRun; steps++) {
						menSeries
								.add(
										(double) steps,
										pDiseasePerRiskClass[thisScen][steps][dNumber][r][0]);
						womenSeries
								.add(
										(double) steps,
										pDiseasePerRiskClass[thisScen][steps][dNumber][r][1]);

					}
					if (r == 0)
						xyDataset = new XYSeriesCollection(menSeries);
					else
						((XYSeriesCollection) xyDataset).addSeries(menSeries);
					((XYSeriesCollection) xyDataset).addSeries(womenSeries);
				}
				JFreeChart chart = ChartFactory.createXYLineChart(
						structure[c].diseaseName.get(d), "years of simulation",
						"prevalence rate", xyDataset, PlotOrientation.VERTICAL,
						true, true, false);
				ChartFrame frame1 = new ChartFrame("Survival Chart", chart);
				frame1.setVisible(true);
				frame1.setSize(300, 300);

				final ChartPanel chartPanel = new ChartPanel(chart);
				chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

				try {

					ChartUtilities.saveChartAsJPEG(new File(baseDir
							+ File.separator + "simulations" + File.separator
							+ simulationName + File.separator + "results"
							+ File.separator + "chartPrevalenceByRiskClass"
							+ dNumber + "scen" + thisScen + ".jpg"), chart,
							500, 300);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out
							.println("Problem occurred creating chart. for diseasenumber"
									+ dNumber);
				}

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

				menSeries
						.add((double) steps, pRiskClass[thisScen][steps][r][0]);
				womenSeries.add((double) steps,
						pRiskClass[thisScen][steps][r][1]);

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

			ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator
					+ "simulations" + File.separator + simulationName
					+ File.separator + "results" + File.separator
					+ "chartRiskFactorPrevalence.jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out
					.println("Problem occurred creating chart. for riskfactor");
		}

	}

	public void makeLifeExpectancyPlot() {

		double[][] lifeExp = new double[nScen + 1][2];

		for (int scenario = 0; scenario < nScen + 1; scenario++)
			for (int s = 0; s < 2; s++) {
				for (int steps = 0; steps < stepsInRun; steps++)

					lifeExp[scenario][s] += pPop[scenario][steps][s];

				lifeExp[scenario][s] = lifeExp[scenario][s]
						/ pPop[scenario][0][s];

			}

		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				"scenario ", "gender ", lifeExp);

		JFreeChart chart = ChartFactory.createBarChart("LifeExpectancy", "",
				"years", dataset, PlotOrientation.VERTICAL, true, true, false);
		ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart);
		frame1.setVisible(true);
		frame1.setSize(300, 300);

		try {

			ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator
					+ "simulations" + File.separator + simulationName
					+ File.separator + "results" + File.separator
					+ "chartLifeExpectancy.jpg"), chart, 500, 300);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out
					.println("Problem occurred creating chart. for lifeExpectancy");
		}
	}

	public void makePopulationPyramidPlot(int scenNumber, int timestep) {

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
		for (int a = 0; a < 100; a++) {
			pyramidData1[0][a] = pPopByAge[scenNumber][timestep][a][0];
			pyramidData2[0][a] = -pPopByAge[scenNumber][timestep][a][1];
			// pyramidData1[1][a]=difference between scenarios;
			// pyramidData2[1][a]=-difference between scenarios;
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

			ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator
					+ "simulations" + File.separator + simulationName
					+ File.separator + "results" + File.separator
					+ "chartPyramid.jpg"), chart, 300, 500);
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
			nDiseases += s[i].nInCluster;

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
			if (s[i].nInCluster == 1)
				nDiseaseStates++;
			else if (s[i].isWithCuredFraction())
				nDiseaseStates += 2;
			else
				nDiseaseStates += Math.pow(2, s[i].nInCluster) - 1;

		}
	}

	public void setNDiseaseStates(int input) {
		nDiseaseStates = input;
	}
}
