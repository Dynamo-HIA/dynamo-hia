package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.CompoundCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.DOMCharacteristicValueWriter;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.individual.DOMIndividualWriter;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.IndividualFromDOMFactory;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.prngutil.DOMRNGSeedWriter;
import nl.rivm.emi.dynamo.estimation.BaseDirectory;

import nl.rivm.emi.dynamo.estimation.DynamoLib;
import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InitialPopulationFactory {
	Log log = LogFactory.getLog(this.getClass().getName());
	private int numberOfElements; 
	/* number of disease states */

	/**
	 * @author Boshuizh This class generates the initial population and
	 *         populations of newborns
	 */
	public InitialPopulationFactory() {
		super();
	}

	/**
	 * @param parameters
	 * @param nSim
	 * @param simulationName
	 * @param seed
	 * @param newborns
	 *            : boolean: should this give an intial population or newborns
	 * @param scenarioInfo
	 *            : information on scenario
	 * @throws DynamoConfigurationException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void writeInitialPopulation(ModelParameters parameters, int nSim,
			String simulationName, int seed, boolean newborns,
			ScenarioInfo scenarioInfo) throws DynamoConfigurationException {
		
		numberOfElements = getNumberOfDiseaseStateElements(parameters);
		
		Population[] pop = manufactureInitialPopulation(parameters,
				simulationName, nSim, seed, newborns, scenarioInfo);

		String baseDir = BaseDirectory.getInstance(
				"c:\\hendriek\\java\\dynamohome\\").getBaseDir();
		String directoryName = baseDir + "Simulations\\" + simulationName;
		String popFileName;
		
		if (newborns)
			popFileName = directoryName + "\\modelconfiguration"
					+ "\\newborns.xml";
		else
			popFileName = directoryName + "\\modelconfiguration"
					+ "\\population.xml";
		File initPopXMLfile = new File(popFileName);
		try {
			writeToXMLFile(pop[0], 0, initPopXMLfile);
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"ParserConfigurationException while writing population"
							+ "to XML with message: " + e.getMessage());
		} catch (TransformerException e) {

			e.printStackTrace();
			throw new DynamoConfigurationException(
					"TransformerException while writing population"
							+ "to XML with message: " + e.getMessage());
		}
		if (pop.length > 1 && !newborns)
			for (int scen = 1; scen < pop.length; scen++) {
				popFileName = directoryName + "\\modelconfiguration"
						+ "\\population_scen_" + scen + ".xml";
				initPopXMLfile = new File(popFileName);
				if (pop[scen] != null)
					try {
						writeToXMLFile(pop[scen], 0, initPopXMLfile);
					} catch (ParserConfigurationException e) {

						e.printStackTrace();
						throw new DynamoConfigurationException(
								"ParserConfigurationException while writing population"
										+ "to XML with message: "
										+ e.getMessage());
					} catch (TransformerException e) {

						e.printStackTrace();
						throw new DynamoConfigurationException(
								"TransformerException while writing population"
										+ "to XML with message: "
										+ e.getMessage());
					}
				else if (scenarioInfo.getInitialPrevalenceType()[0])
					log
							.fatal("trying to write non existing initial population for scenario "
									+ scen);
			}

	}

	/**
	 * this method manufactures initial populations for 1. the reference
	 * situation 2. scenario's in case of categorical risk factors and
	 * scenario's in which only the initial situation is changed, only a single
	 * scenario population is manufactured, containing all subjects that are
	 * potentially changed under the scenario The effect of each individual
	 * scenario then is calculated by the post-processing (DynamoOutputfactory)
	 * 
	 * nSim= number of simulated individuals per age and gender
	 * */
	/**
	 * @param parameters
	 *            : Object with modelparameters
	 * @param simulationName
	 * @param nSim
	 *            : number of simulated individuals per age and gender ; the
	 *            method can add individuals if risk factors would not be
	 *            represented
	 * @param seed
	 * @param newborns
	 *            (boolean) should a population of newborns be generated
	 * @param scenarioInfo
	 *            ; object with scenario information
	 * @return
	 */
	public Population[] manufactureInitialPopulation(
			ModelParameters parameters, String simulationName, int nSim,
			long seed, boolean newborns, ScenarioInfo scenarioInfo) {

		/* at this moment: simulate all ages */
		/* First make some indexes that are needed */
		int nClasses = 1;
		if (parameters.getRiskType() != 2)
			nClasses = parameters.getPrevRisk()[0][0].length;
		int[][] nDuurClasses = new int[96][2];
		int[] cumulativeNSimPerClass;
		int[] nSimPerClass;
		int[] nSimPerDurationClass;
		int[] cumulativeNSimPerDurationClass;
		float[] rest;
		float[] restDuration;
		int currentRiskValue = 0;
		int[][] nSimNew = new int[96][2];

		DynamoLib.getInstance(nSim);
		BaseDirectory baseDir = BaseDirectory.getInstance("c:");
		Random rand = new Random(seed); // used to draw the initial population
		MTRand rand2 = new MTRand(seed + 1); // used to generate seeds in
		// update rules
		// TODO inlezen baseDir aanpassen aan userinterface

		int nPopulations;
		if (scenarioInfo.getNScenarios() == 0)
			nPopulations = 1;
		else if (parameters.getRiskType() != 2
				&& scenarioInfo.isWithInitialChange())
			nPopulations = 2;
		else
			nPopulations = scenarioInfo.nWithInitialChange() + 1;
		Population[] initialPopulation = new Population[nPopulations];

		initialPopulation[0] = new Population(simulationName, null);
		if (parameters.getRiskType() == 2)
			for (int scen = 0; scen < nPopulations; scen++) {
				initialPopulation[scen] = new Population(simulationName, null);
			}

		if (parameters.getRiskType() != 2 && scenarioInfo.getNScenarios() > 0
				&& scenarioInfo.isWithInitialChange() && !newborns)
			initialPopulation[1] = new Population(simulationName, null);

		boolean[][][][] shouldChangeInto = null;
		if (parameters.getRiskType() != 2
				&& scenarioInfo.getInitialPrevalenceType()[0] && !newborns) {
			int nCat = parameters.getPrevRisk()[0][0].length;
			float[] RR = new float[nCat];
			shouldChangeInto = new boolean[96][2][nCat][nCat];
			// initialize arrays;
			Arrays.fill(RR, 1);
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++)
					for (int r1 = 0; r1 < nCat; r1++)
						for (int r2 = 0; r2 < nCat; r2++)
							shouldChangeInto[a][g][r1][r2] = false;

			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++)
					for (int s = 0; s < scenarioInfo.getNScenarios(); s++) {

						float oldPrev[] = parameters.getPrevRisk()[a][g];
						float newPrev[] = scenarioInfo.getNewPrevalence()[s][a][g];
						float[][] trans = NettTransitionRates
								.makeNettTransitionRates(oldPrev, newPrev, 0,
										RR);
						for (int r1 = 0; r1 < nCat; r1++)
							for (int r2 = 0; r2 < nCat; r2++) {
								if (r1 != r2 && trans[r1][r2] > 0)
									shouldChangeInto[a][g][r1][r2] = true;
							}

					}
		}

		int agemax = 96;
		if (newborns)
			agemax = 1;
		if (parameters.getRiskType() == 3) {
			for (int a = 0; a < agemax; a++)
				for (int g = 0; g < 2; g++) {
					nDuurClasses[a][g] = parameters.getDuurFreq()[a][g].length;
				}
		}

		for (int a = 0; a < agemax; a++)
			for (int g = 0; g < 2; g++) {
				nSimNew[a][g] = nSim;
				// calculate the number of persons in each risk factor class
				cumulativeNSimPerClass = new int[nClasses];
				cumulativeNSimPerDurationClass = new int[nDuurClasses[a][g]];
				rest = new float[nClasses];
				restDuration = new float[nDuurClasses[a][g]];
				if (parameters.getRiskType() == 1) {
					// NB all statements here are copied to the next part
					// (risktype==3)
					// so any faulth found here should be mended there too.
					nSimPerClass = new int[nClasses];

					int c = 0;

					for (c = 0; c < nClasses; c++) {
						nSimPerClass[c] = (int) Math.floor(parameters
								.getPrevRisk()[a][g][c]
								* nSimNew[a][g]);
						/*
						 * if zero case in a class, add a person to the
						 * simulated population
						 */
						if (nSimPerClass[c] == 0) {
							nSimPerClass[c] = 1;
							nSimNew[a][g]++;

							/*
							 * in that case, the rest from other categories only
							 * needs to be distributed differently over the
							 * other categories, as this categorie already has a
							 * large part of the total as needed
							 * 
							 * We therefore recalculate the earlier
							 */

						}

						if (c > 0)
							cumulativeNSimPerClass[c] = nSimPerClass[c]
									+ cumulativeNSimPerClass[c - 1];
						else
							cumulativeNSimPerClass[c] = nSimPerClass[c];
					}
					/*
					 * calculated the parts of nSim that have not yet been
					 * allocated
					 */
					float totrest = 0;
					float totrestWithoutNegatives = 0;
					for (c = 0; c < nClasses; c++) {
						/*
						 * rest is the difference between the prevalence that is
						 * the aim to reach, and the prevalence that is reached
						 * already the rest of the data are drawn proportionally
						 * to that figure
						 */
						rest[c] = parameters.getPrevRisk()[a][g][c]
								- (float) nSimPerClass[c]
								/ (float) nSimNew[a][g];
						totrest += rest[c];
						/*
						 * is totrest is negative, it should be taken into
						 * account into totrest (part that is to be distributed
						 * later), but this should not be used in the
						 * distribution itself
						 */
						if (rest[c] < 0)
							rest[c] = 0;
						else
							totrestWithoutNegatives += rest[c];

					}

					for (c = 0; c < nClasses; c++) {
						rest[c] = rest[c] / totrestWithoutNegatives;
					}

					;

				}

				if (parameters.getRiskType() == 3) {
					// NB all statements here are copied to the next part
					// (risktype==3)
					// so any faulth found here should be mended there too.
					nSimPerClass = new int[nClasses];
					nSimPerDurationClass = new int[nDuurClasses[a][g]];
					int c = 0;

					for (c = 0; c < nClasses; c++) {
						nSimPerClass[c] = (int) Math.floor(parameters
								.getPrevRisk()[a][g][c]
								* nSimNew[a][g]);
						/*
						 * if zero case in a class, add a person to the
						 * simulated population
						 */
						if (nSimPerClass[c] == 0) {
							nSimPerClass[c] = 1;
							nSimNew[a][g]++;

							/*
							 * in that case, the rest from other categories only
							 * needs to be distributed differently over the
							 * other categories, as this category already has a
							 * large part of the total as needed
							 * 
							 * We therefore recalculate the earlier
							 */

						}

						if (c > 0)
							cumulativeNSimPerClass[c] = nSimPerClass[c]
									+ cumulativeNSimPerClass[c - 1];
						else
							cumulativeNSimPerClass[c] = nSimPerClass[c];
					}
					/*
					 * calculated the parts of nSim that have not yet been
					 * allocated
					 */
					float totrest = 0;
					float totrestWithoutNegatives = 0;
					for (c = 0; c < nClasses; c++) {
						/*
						 * rest is the difference between the prevalence that is
						 * the aim to reach, and the prevalence that is reached
						 * already the rest of the data are drawn proportionally
						 * to that figure
						 */
						rest[c] = parameters.getPrevRisk()[a][g][c]
								- (float) nSimPerClass[c]
								/ (float) nSimNew[a][g];
						totrest += rest[c];
						/*
						 * is totrest is negative, it should be taken into
						 * account into totrest (part that is to be distributed
						 * later), but this should not be used in the
						 * distribution itself
						 */
						if (rest[c] < 0)
							rest[c] = 0;
						else
							totrestWithoutNegatives += rest[c];

					}

					for (c = 0; c < nClasses; c++) {
						rest[c] = rest[c] / totrestWithoutNegatives;
					}

					;

					for (c = 0; c < nDuurClasses[a][g]; c++) {
						nSimPerDurationClass[c] = (int) Math.floor(parameters
								.getDuurFreq()[a][g][c]
								* nSimPerClass[parameters.getDurationClass()]);
						restDuration[c] = parameters.getDuurFreq()[a][g][c]
								- (float) nSimPerDurationClass[c]
								/ (float) nSimPerClass[parameters
										.getDurationClass()];
						if (c > 0)
							cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c]
									+ cumulativeNSimPerDurationClass[c - 1];
						else
							cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c];
					}
					totrest = 0;
					for (int c2 = 0; c2 < nDuurClasses[a][g]; c2++) {
						totrest += restDuration[c2];
					}
					for (int c2 = 0; c2 < nDuurClasses[a][g]; c2++) {
						restDuration[c2] = restDuration[c2] / totrest;
					}
				}
				/*
				 * werktte niet omdat Class Individual protected; veranderd in
				 * SOR code
				 */
				

				/*****************************************************************************
				 * 
				 * 
				 * LOOP OVER INDIVIDUALS
				 * 
				 * 
				 * 
				 * 
				 * 
				 */
				/*****************************************************************************/

				for (int i = 0; i < nSimNew[a][g]; i++) {
					Individual currentIndividual = new Individual("ind", "ind_"
							+ (i + a * nSimNew[a][g] * 2 + nSimNew[a][g] * g)
							+ "_bl");
					long seed2 = (long) rand2.random();
					currentIndividual.setRandomNumberGeneratorSeed(seed2);

					/*
					 * first characteristic is age if i==1 generate the
					 * configuration of the characteristic and of the simulation
					 */

					/*
					 * now generate the characteristic in the initial population
					 */
					currentIndividual.add(new FloatCharacteristicValue(0, 1,
							(float) a));

					// second characteristic is sex

					currentIndividual.add(new IntCharacteristicValue(0, 2, g));

					/*
					 * 
					 * 
					 * generate risk factors
					 */

					/*
					 * third and possibly fourth characteristic is risk factor
					 * now use index for this
					 */
					int characteristicIndex = 3;
					// if categorical then fill in proportionally;
					// randomly draw the last elements needed to get
					// exactly nSim persons
					boolean flagForRandomlyAdded = false;
					if (parameters.getRiskType() == 1
							|| parameters.getRiskType() == 3) {

						int c;

						for (c = 0; c < nClasses; c++) {
							if (i < cumulativeNSimPerClass[c])
								break;
						}
						currentRiskValue = c;
						/*
						 * if the loop was performed until the very end this
						 * means that i is equal or above highest cumulative
						 * value In that case we are going to draw randomly
						 */
						if (c == nClasses) {
							currentRiskValue = DynamoLib.draw(rest, rand);
							/*
							 * if a duration class value is draw, a duration
							 * should also be drawn
							 */
							if (currentRiskValue == parameters
									.getDurationClass())
								flagForRandomlyAdded = true;
						}

						currentIndividual.add(new IntCharacteristicValue(0,
								characteristicIndex, currentRiskValue));

						characteristicIndex++;
					}

					float riskFactorValue = 0;
					if (parameters.getRiskType() == 2) {
						if (parameters.getRiskTypeDistribution()
								.compareToIgnoreCase("normal") == 0) {

							// simulate equi-probable points
							riskFactorValue = parameters.getMeanRisk()[a][g]
									+ parameters.getStdDevRisk()[a][g]
									* (float) DynamoLib.normInv((i + 0.5)
											/ nSim);
						} else {
							riskFactorValue = (float) parameters
									.getOffsetRisk()[a][g]
									+ (float) Math
											.exp(parameters.getMeanRisk()[a][g]
													+ parameters
															.getStdDevRisk()[a][g]
													* (float) DynamoLib
															.normInv((i + 0.5)
																	/ nSim));

							
						}
						currentIndividual.add(new FloatCharacteristicValue(
								0, characteristicIndex, riskFactorValue));
						characteristicIndex++;
					}

					float currentDurationValue = 0;
					if (parameters.getRiskType() == 3) {
						if (flagForRandomlyAdded)
							currentDurationValue = DynamoLib.draw(parameters
									.getDuurFreq()[a][g], rand);
						else {
							int relativeI;
							if (currentRiskValue == parameters
									.getDurationClass()) {
								int c;
								if (parameters.getDurationClass() == 0)
									relativeI = i;
								else
									relativeI = i
											- cumulativeNSimPerClass[parameters
													.getDurationClass() - 1];
								for (c = 0; c < nDuurClasses[a][g]; c++) {

									if (relativeI < cumulativeNSimPerDurationClass[c])
										break;
								}

								currentDurationValue = c;
								if (c == nDuurClasses[a][g])
									currentDurationValue = DynamoLib.draw(
											restDuration, rand);
							}
							currentIndividual.add(new FloatCharacteristicValue(
									0, characteristicIndex,
									currentDurationValue));

							characteristicIndex++;

						}
					}

					/*
					 * 
					 * DISEASES /HEALTH STATE CHARACTERISTIC
					 * 
					 * generate initial probabilities of each disease state and
					 * put them in the array CharValues
					 */

					/*
					 * first calculate number of elements in the characteristic;
					 */
					float[] CharValues = calculateDiseaseStates(parameters,
							currentRiskValue, a, g, riskFactorValue,
							currentDurationValue);

					currentIndividual.add(new CompoundCharacteristicValue(0,
							characteristicIndex, numberOfElements, CharValues));

					initialPopulation[0].addIndividual(currentIndividual);

					/*
					 * adding individuals to populations for scenario
					
					for continuous riskfactors:
					 
					 */

					
					
					
					if (parameters.getRiskType() == 2 ) {
						int currentscen = 0;
						for (int population = 1; population < nPopulations; population++) {

							/* look for next scenario with initial type = true */
							while (!scenarioInfo.getInitialPrevalenceType()[currentscen]
									&& currentscen < scenarioInfo
											.getNScenarios())
								currentscen++;
							if (currentscen < scenarioInfo.getNScenarios()) {
								currentIndividual = new Individual("ind",
										"ind_" + (i + a * nSim * 2 + nSim * g)
												+ "_" + population);
								currentIndividual
										.setRandomNumberGeneratorSeed(seed2);
								currentIndividual
										.add(new FloatCharacteristicValue(0, 1,
												(float) a));
								currentIndividual
										.add(new IntCharacteristicValue(0, 2, g));
								
								/*
								 * for continuous risk factor we just give the new distribution,
								 * meaning that everyone maintains his/her old ranking in the population
								 * 
								 */
								
								if (parameters.getRiskTypeDistribution()
										.compareToIgnoreCase("normal") == 0)
									riskFactorValue = (scenarioInfo
											.getNewMean()[currentscen][a][g] + scenarioInfo
											.getNewStd()[currentscen][a][g])

											* (float) DynamoLib
													.normInv((i + 0.5) / nSim);
								// simulate equi-probable points
								else {
									riskFactorValue = (float) scenarioInfo
											.getNewOffset()[currentscen][a][g]
											+ (float) Math
													.exp(scenarioInfo
															.getNewMean()[currentscen][a][g]
															+ scenarioInfo
																	.getNewStd()[currentscen][a][g]
															* (float) DynamoLib
																	.normInv((i + 0.5)
																			/ nSim));
									;

								}

								currentIndividual
										.add(new FloatCharacteristicValue(0, 3,
												riskFactorValue));
                                
								/* newborns and zero year olds are not bothered disease histories, 
								 * so their diseases are recalculated from the new risk factor state
								 *  but not the others: they keep the disease probabilities based on
								 *  their old risk factors (before intervention)
								 */
								if (newborns || a==0) 
                                	
                                	CharValues = calculateDiseaseStates(parameters,
										currentRiskValue, a, g, riskFactorValue,
										currentDurationValue);
								
								
								currentIndividual
										.add(new CompoundCharacteristicValue(0,
												characteristicIndex,
												numberOfElements, CharValues));
								
									
								initialPopulation[population]
													.addIndividual(currentIndividual);
								currentscen++;
							}
						} // end loop over populations
						
					} // end for riskType==2
					
					 /*
					 * for categorical covariates, but not for newborns /0 year old
					 * as the latter will start "clean", that is without 
					 * having disease prevalences based on old history
					 *				 
					 * the label is an indicator of scenario for the "all in one"
					 * scenario population for categorical riskfactors.
					 * This gives the
					 * old (baseline) value of the individual (0-9) plus the new
					 * value (0-9) for categorical data
					
*/
					if (parameters.getRiskType() != 2
							&& shouldChangeInto != null && a!=0 && !newborns) {
						for (int r = 0; r < shouldChangeInto[a][g].length; r++) {
							if (shouldChangeInto[a][g][currentRiskValue][r]) {
								currentIndividual = new Individual(
										"ind",
										"ind_"
												+ (i + a * nSimNew[a][g] * 2 + nSimNew[a][g]
														* g) + "_"
												+ currentRiskValue + "_" + r);
								currentIndividual
										.setRandomNumberGeneratorSeed(seed2);
								currentIndividual
										.add(new FloatCharacteristicValue(0, 1,
												(float) a));
								currentIndividual
										.add(new IntCharacteristicValue(0, 2, g));
								if (parameters.getRiskType() == 1
										|| parameters.getRiskType() == 3)
									currentIndividual
											.add(new IntCharacteristicValue(0,
													3, r));
								// duration = 0, both for just stopped, and for
								// other categories

								if (parameters.getRiskType() == 3)
									currentIndividual
											.add(new FloatCharacteristicValue(
													0, 4, 0));
								/* newborns and zero year olds are not bothered disease histories, 
								 * so their diseases are recalculated from the new risk factor state
								 *  but not the others: they keep the disease probabilities based on
								 *  their old risk factors (before intervention)
								 */
								if (newborns || a==0) 
                                	
                                	CharValues = calculateDiseaseStates(parameters,
										currentRiskValue, a, g, riskFactorValue,
										currentDurationValue);
								
								currentIndividual
										.add(new CompoundCharacteristicValue(0,
												characteristicIndex,
												numberOfElements, CharValues));
								initialPopulation[1]
										.addIndividual(currentIndividual);
							}
						}

					}
					/*
					 * now the one year olds and newborns
					 * 
					 * 
					 */
					else if (parameters.getRiskType() != 2
							&& shouldChangeInto != null && ( a==0 || newborns))

						
						
					for (int r = 0; r < shouldChangeInto[a][g].length; r++) {
						if (shouldChangeInto[a][g][currentRiskValue][r]) {
							currentIndividual = new Individual(
									"ind",
									"ind_"
											+ (i + a * nSimNew[a][g] * 2 + nSimNew[a][g]
													* g) + "_"
											+ currentRiskValue + "_" + r);
							currentIndividual
									.setRandomNumberGeneratorSeed(seed2);
							currentIndividual
									.add(new FloatCharacteristicValue(0, 1,
											(float) a));
							currentIndividual
									.add(new IntCharacteristicValue(0, 2, g));
							if (parameters.getRiskType() == 1
									|| parameters.getRiskType() == 3)
								currentIndividual
										.add(new IntCharacteristicValue(0,
												3, r));
							// duration = 0, both for just stopped, and for
							// other categories

							if (parameters.getRiskType() == 3)
								currentIndividual
										.add(new FloatCharacteristicValue(
												0, 4, 0));

							currentIndividual
									.add(new CompoundCharacteristicValue(0,
											characteristicIndex,
											numberOfElements, CharValues));
							initialPopulation[1]
									.addIndividual(currentIndividual);
						}
					}

				
					
					

				}// end sim loop
				;

			}
		// end age and sex group
		return initialPopulation;
	}

	/**
	 * @param parameters
	 * @param currentRiskValue
	 * @param a
	 * @param g
	 * @param riskFactorValue
	 * @param currentDurationValue
	 * @param numberOfElements
	 * @return
	 */
	private float[] calculateDiseaseStates(ModelParameters parameters,
			int currentRiskValue, int a, int g, float riskFactorValue,
			float currentDurationValue) {
	
		float[] CharValues = new float[numberOfElements];
		
		int elementIndex = 0;
		for (int cluster = 0; cluster < parameters.getNCluster(); cluster++) {

			/*
			 * first make the logit of the probability for all
			 * diseases in this cluster based only on riskfactor
			 * information
			 */
			double[] logitDisease = new double[parameters
					.getClusterStructure()[cluster].getNInCluster()];
			Arrays.fill(logitDisease, 0);
			for (int d = 0; d < parameters.getClusterStructure()[cluster]
					.getNInCluster(); d++) {

				/*
				 * for each disease calculate its contribution to
				 * the probability of the combination
				 */
				// zie boven doe eerst een
				// analyse van conditionele
				// onafhankelijkheden
				int dnumber = parameters.getClusterStructure()[cluster]
						.getDiseaseNumber()[d];

				/* only needed if disease is present */
				if (parameters.getBaselinePrevalenceOdds()[a][g][dnumber] != 0)

				{
					logitDisease[d] = Math
							.log(parameters
									.getBaselinePrevalenceOdds()[a][g][dnumber]);
					if (parameters.getRiskType() == 1)
						logitDisease[d] += Math
								.log(parameters.getRelRiskClass()[a][g][currentRiskValue][dnumber]);
					if (parameters.getRiskType() == 2)
						logitDisease[d] += Math
								.log(Math
										.pow(
												parameters
														.getRelRiskContinue()[a][g][dnumber],
												riskFactorValue
														- parameters
																.getRefClassCont()));
					if (parameters.getRiskType() == 3) {
						if (currentRiskValue != parameters
								.getDurationClass())
							logitDisease[d] += Math
									.log(parameters
											.getRelRiskClass()[a][g][currentRiskValue][dnumber]);
						else
							logitDisease[d] += Math
									.log((parameters
											.getRelRiskDuurBegin()[a][g][dnumber] - parameters
											.getRelRiskDuurEnd()[a][g][dnumber])
											* Math
													.exp(-currentDurationValue
															* parameters
																	.getAlfaDuur()[a][g][dnumber])
											+ parameters
													.getRelRiskDuurEnd()[a][g][dnumber]);
					}
				}
			}
			/*
			 * CALCULATE PROBABILITY OF EACH COMBINATION OF DISEASES
			 * number of combinations= 2^Ndiseases
			 */
			int nDiseases = parameters.getClusterStructure()[cluster]
					.getNInCluster();
			for (int combi = 1; combi < Math.pow(2, nDiseases); combi++) {
				/*
				 * loop over diseases in combi; p (D en E en G)=
				 * P(D|E,G)P(E)P(G) (E en G onafhankelijk) log odds
				 * (D=1|E=1,G=1)= logRR(D|E)+logRR(D|
				 * G)+logoddsBaseline(D)--> P(D=1|E=1,G=1)
				 * 
				 * Nu D is gemeenschappelijke oorzaak van E en G p
				 * (D en E en G)= P(E|D)P(G|D)P(D) log odds
				 * (E=1|D=1)= logRR(E|D)+logoddsBaseline(E)-->
				 * P(E=1|D=1) log odds (G=1|D=1)=
				 * logRR(G|D)+logoddsBaseline(G)--> P(G=1|D=1)
				 * 
				 * in het algemeen: p-combi= product [P(each dep
				 * disease|all independent
				 * diseases)]product[P(independent disease)] dus:
				 * GENERAL: product [p (disease |causes (if any)]
				 */

				double probCombi = 1;

				// TODO: nog checken

				/*
				 * if this is a independent disease then this is all
				 * // if dependent disease, then relative risks
				 * should be // added for those causal disease that
				 * are equal to 1 in the combi
				 * 
				 * // must be: probability conditional on not having
				 * disease d // // we use RRextended and therefore
				 * can consider all // disease as causes of each
				 * other // as RR=1 if this is not the case // case
				 * // TODO check if RRextended is made OK //
				 */
				/*
				 * now loop throught all diseases in the combi, look
				 * if they are zero or one and calculate the
				 * probability given causal diseases
				 */
				for (int d1 = 0; d1 < nDiseases; d1++) {
					int d1number = parameters.getClusterStructure()[cluster]
							.getDiseaseNumber()[d1];
					double probCurrent = 1; /*
											 * probCurrent is the
											 * probability of the
											 * current disease in
											 * the combination (d1)
											 */
					/* look if d1 is one or zero in the combination */
					if ((combi & (1 << d1)) == (1 << d1)) {
						/* is one */
						/* so add prob(=1) given causes */

						if (parameters.getClusterStructure()[cluster]
								.getDependentDisease()[d1]) {
							if (parameters
									.getBaselinePrevalenceOdds()[a][g][d1number] != 0) {
								double logitCurrent = logitDisease[d1];

								for (int d2 = 0; d2 < parameters
										.getClusterStructure()[cluster]
										.getNInCluster(); d2++) {
									if ((combi & (1 << d2)) == (1 << d2)) {

										logitCurrent += Math
												.log(parameters
														.getRelRiskDiseaseOnDisease()[a][g][cluster][d2][d1]);
									}
									probCurrent = 1 / (1 + Math
											.exp(-logitCurrent));
								}
							} else
								probCurrent = 0;

						} else
							/* == independent disease */
							probCurrent = 1 / (1 + Math
									.exp(-logitDisease[d1]));

					}/* end if disease d1 ==1 */else {
						/* now if d1 is zero in combination */;
						if (parameters.getBaselinePrevalenceOdds()[a][g][d1number] != 0) {

							if (parameters.getClusterStructure()[cluster]
									.getDependentDisease()[d1]) {
								double logitCurrent = logitDisease[d1];

								for (int d2 = 0; d2 < parameters
										.getClusterStructure()[cluster]
										.getNInCluster(); d2++)
									if ((combi & (1 << d2)) == (1 << d2))
										logitCurrent += Math
												.log(parameters
														.getRelRiskDiseaseOnDisease()[a][g][cluster][d2][d1]);
								/*
								 * NB now exp(+x) a this is 1-p in
								 * stead of p
								 */
								probCurrent = 1 / (1 + Math
										.exp(logitCurrent));
							} else
								probCurrent = 1 / (1 + Math
										.exp(logitDisease[d1]));
						} else

						{
							probCurrent = 1;
						}

					}
					probCombi *= probCurrent;
				} // end loop over d1
				float value = (float) probCombi;

				CharValues[elementIndex] = value;
				elementIndex++;

			} // end loop over combi

		} // end loop over clusters

		// tot slot nog characteristiek voor
		// survival;
		CharValues[elementIndex] = 1;
		elementIndex++;

		if (elementIndex != numberOfElements)
			log.warn("number of element written does not"
					+ "fit number calculated, that is : "
					+ elementIndex + " not equal "
					+ numberOfElements);
		return CharValues;
	}

	/**
	 * @param parameters
	 * @return
	 */
	private int getNumberOfDiseaseStateElements(ModelParameters parameters) {
		int numberOfElements = 1;

		for (int c = 0; c < parameters.getNCluster(); c++) {
			DiseaseClusterStructure structure = parameters
					.getClusterStructure()[c];
			if (structure.getNInCluster() == 1)
				numberOfElements++;
			else if (structure.isWithCuredFraction())
				numberOfElements += 2;
			else
				numberOfElements += Math.pow(2, structure
						.getNInCluster()) - 1;
		}
		return numberOfElements;
	}

	public void writeToXMLFile(Population population, int stepNumber,
			File xmlFileName) throws ParserConfigurationException,
			TransformerException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = (DocumentBuilder) dbfac
				.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		String elementName = population.getElementName();
		Element element = document.createElement(population.xmlElementName);
		Element nameElement = document
				.createElement(population.xmlLabelAttributeName);
		nameElement.setTextContent(elementName);
		element.appendChild(nameElement);
		String label = population.getLabel();
		if (label != null && !"".equals(label)) {
			element.setAttribute("lb", label);
		}
		document.appendChild(element);
		Individual individual;
		while ((individual = population.nextIndividual()) != null) {
			DOMIndividualWriter.generateDOM(individual, stepNumber, element);
		}
		boolean isDirectory = xmlFileName.isDirectory();
		boolean canWrite = xmlFileName.canWrite();
		try {
			boolean isNew = xmlFileName.createNewFile();
			if (!isDirectory && (canWrite || isNew)) {
				Source source = new DOMSource(document);
				StreamResult result = new StreamResult(xmlFileName);
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(source, result);
			}
		} catch (IOException e) {
			log.warn("File exception: " + e.getClass().getName() + " message: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param p
	 * @param sim
	 * @param simName
	 * @param seed
	 * @param newborns
	 */

}
