package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.cdm.characteristic.values.CharacteristicValueBase;
import nl.rivm.emi.cdm.characteristic.values.DOMCharacteristicValueWriter;
import nl.rivm.emi.cdm.characteristic.values.FloatCharacteristicValue;
import nl.rivm.emi.cdm.characteristic.values.IntCharacteristicValue;
import nl.rivm.emi.cdm.individual.DOMIndividualWriter;
import nl.rivm.emi.cdm.individual.Individual;
import nl.rivm.emi.cdm.individual.IndividualFromDOMFactory;
import nl.rivm.emi.cdm.population.DOMPopulationWriter;
import nl.rivm.emi.cdm.population.Population;
import nl.rivm.emi.cdm.prngutil.DOMRNGSeedWriter;
import nl.rivm.emi.dynamo.datahandling.BaseDirectory;
import nl.rivm.emi.dynamo.datahandling.ConfigurationFileData;
import nl.rivm.emi.dynamo.estimation.DynamoLib;

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
	

	public InitialPopulationFactory() {
		super();
	}

	public void writeInitialPopulation(ModelParameters parameters, int nSim, String simulationName,int seed,
			String filenameInitPop) throws ParserConfigurationException,
			TransformerException {
		Population pop = manufactureInitialPopulation(parameters, simulationName,  nSim,seed );
		File initPopXMLfile = new File(filenameInitPop+ ".XML");
		
		writeToXMLFile(pop, 0, initPopXMLfile);
	}

	/** nsim= number of simulated individuals per age and gender */
	public Population manufactureInitialPopulation(ModelParameters parameters,String simulationName,
			int nSim, int seed) {

		/* at this moment: simulate all ages */
		/* First make some indexes that are needed */
		
		int nClasses = parameters.prevRisk[0][0].length;
		int[][] nDuurClasses = new int[96][2];
		int[] cumulativeNSimPerClass;
		int[] nSimPerClass;
		int[] nSimPerDurationClass;
		int[] cumulativeNSimPerDurationClass;
		float[] rest;
		float[] restDuration;
		int currentRiskValue = 0;
		
		
		
		DynamoLib.getInstance(nSim);
		BaseDirectory baseDir=BaseDirectory.getInstance("c:");
		Random rand = new Random(seed);
		
		Population initialPopulation = new Population(simulationName,null);
		
		
		// TODO nakijken of juiste directory naam 
		
		if (parameters.riskType == 3) {
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++) {
					nDuurClasses[a][g] = parameters.duurFreq[a][g].length;
				}
		}

		for (int a = 0; a < 96; a++)
			for (int g = 0; g < 2; g++) {
				// calculate the number of persons in each risk factor class
				cumulativeNSimPerClass = new int[nClasses];
				cumulativeNSimPerDurationClass = new int[nDuurClasses[a][g]];
				rest = new float[nClasses];
				restDuration = new float[nDuurClasses[a][g]];
				if (parameters.riskType == 1) {
					// NB all statements here are copied to the next part
					// (risktype==3)
					// so any faulth found here should be mended there too.
					nSimPerClass = new int[nClasses];

					int c = 0;
					

					for (c = 0; c < nClasses; c++) {
						nSimPerClass[c] = (int) Math
								.floor(parameters.prevRisk[a][g][c] * nSim);
						rest[c] = parameters.prevRisk[a][g][c]
								- (float) nSimPerClass[c] / (float) nSim;
						if (c > 0)
							cumulativeNSimPerClass[c] = nSimPerClass[c]
									+ cumulativeNSimPerClass[c - 1];
						else
							cumulativeNSimPerClass[c] = nSimPerClass[c];
					}
					float totrest=0;
					for (int c2 = 0; c2 < nClasses; c2++) {
						totrest+= rest[c2];
					}
					for (int c2 = 0; c2 < nClasses; c2++) {
						rest[c2] = rest[c2]
								/totrest;}
					
					;

				}

				if (parameters.riskType == 3) {
					nSimPerClass = new int[nClasses];

					int c = 0;
					
					nSimPerDurationClass = new int[nClasses];
					for (c = 0; c < nClasses; c++) {
						nSimPerClass[c] = (int) Math
								.floor(parameters.prevRisk[a][g][c] * nSim);
						rest[c] = parameters.prevRisk[a][g][c]
								- (float) nSimPerClass[c] / (float) nSim;
						if (c > 0)
							cumulativeNSimPerClass[c] = nSimPerClass[c]
									+ cumulativeNSimPerClass[c - 1];
						else
							cumulativeNSimPerClass[c] = nSimPerClass[c];
					}
					float totrest=0;
					for (int c2 = 0; c2 < nClasses; c2++) {
						totrest+= rest[c2];
					}
					for (int c2 = 0; c2 < nClasses; c2++) {
						rest[c2] = rest[c2]
								/totrest;
					}
					;

					
					for (c = 0; c < nDuurClasses[a][g]; c++) {
						nSimPerDurationClass[c] = (int) Math
								.floor(parameters.duurFreq[a][g][c]
										* nSimPerClass[parameters.durationClass]);
						restDuration[c] = parameters.duurFreq[a][g][c]
								- (float) nSimPerDurationClass[c]
								/ (float) nSimPerClass[parameters.durationClass];
						if (c > 0)
							cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c]
									+ cumulativeNSimPerDurationClass[c - 1];
						else
							cumulativeNSimPerDurationClass[c] = nSimPerDurationClass[c];
					}
					totrest=0;
					for (int c2 = 0; c2 < nDuurClasses[a][g]; c2++) {
						totrest+= restDuration[c2];
					}
					for (int c2 = 0; c2 < nDuurClasses[a][g]; c2++) {
						restDuration[c2] = restDuration[c2]
								/ totrest;
					}
				}
				/* werktte niet omdat Class Individual protected;
				 * veranderd in SOR code
				 */
				
				for (int i = 0; i < nSim; i++) {
					Individual currentIndividual = new Individual("ind",
							"ind_"+i);

					
					
					
					
					/* first characteristic is age
					 * if i==1 generate the configuration of the characteristic and of the simulation
					 */
					
                   
                    	
                    /* now generate the characteristic in the initial population
                     * 	
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

					/* third and possibly fourth characteristic is risk factor
					   now use index  for this */
					int characteristicIndex = 3;
					// if categorical then fill in proportionally;
					// randomly draw the last elements needed to get
					// exactly nSim persons
					boolean flagForRandomlyAdded=false;
					if (parameters.riskType == 1 || parameters.riskType == 3) {
						
						int c;
						
						for (c = 0; c < nClasses; c++) {
							if (i < cumulativeNSimPerClass[c])
								break;
						}
						currentRiskValue = c;
						/* if the loop was performed until the very end 
						 * this means that i is equal or above highest cumulative value
						 * In that case we are going to draw randomly */
						if (c==nClasses){
							currentRiskValue = DynamoLib.draw(
									rest, rand);
							/* if a duration class value is draw, a duration should also be drawn */
						if (currentRiskValue==parameters.getDurationClass())flagForRandomlyAdded=true;	
						}

						currentIndividual.add(new IntCharacteristicValue(0,
								characteristicIndex, currentRiskValue));

						characteristicIndex++;
					}
					float riskFactorValue = 0;
					if (parameters.riskType == 2) {
						riskFactorValue = parameters.meanRisk[a][g]
								+ parameters.stdDevRisk[a][g]
								* (float) DynamoLib.normInv((i + 0.5) / nSim);
						// simulate equi-probable points
						currentIndividual.add(new FloatCharacteristicValue(0,
								characteristicIndex, riskFactorValue));
						characteristicIndex++;
					}

					float currentDurationValue = 0;
					if (parameters.riskType == 3) {
						if (flagForRandomlyAdded) currentDurationValue = DynamoLib.draw(
								parameters.duurFreq[a][g], rand); else{
						int relativeI;
						if (currentRiskValue == parameters.durationClass) {
							int c;
							if (parameters.durationClass == 0)
								relativeI = i;
							else
								relativeI = i
										- cumulativeNSimPerClass[parameters.durationClass - 1];
							for (c = 0; c < nDuurClasses[a][g]; c++) {

								if (relativeI < cumulativeNSimPerDurationClass[c])
									break;
							}
							/* TODO checken of dit wel goed gaat */
							currentDurationValue = c;
							if (c == nDuurClasses[a][g])
								currentDurationValue = DynamoLib.draw(
										restDuration, rand);
						}
							currentIndividual.add(new FloatCharacteristicValue(
									0, characteristicIndex,
									currentDurationValue));

							characteristicIndex++;
							characteristicIndex++;
						}
					}

					/*
					 * 
					 * DISEASES
					 * generate disease info
					 */

					double log2 = Math.log(2.0);
					for (int cluster = 0; cluster < parameters.nCluster; cluster++) {

						/*
						 * first make the logit of the probability for
						 * all diseases in this cluster based only on riskfactor
						 * information
						 */
						double[] logitDisease = new double[parameters.clusterStructure[cluster].nInCluster];
						Arrays.fill(logitDisease, 0);
						for (int d = 0; d < parameters.clusterStructure[cluster].nInCluster; d++) {

							/*
							 * for each disease calculate its contribution to
							 * the probability of the combination
							 */
							// zie boven doe eerst een
							// analyse van conditionele
							// onafhankelijk
							int dnumber = parameters.clusterStructure[cluster].diseaseNumber[d];

							/* only needed if disease is present */
							if (parameters.baselinePrevalenceOdds[a][g][dnumber] != 0)

							{
								logitDisease[d] = Math
										.log(parameters.baselinePrevalenceOdds[a][g][dnumber]);
								if (parameters.riskType == 1)
									logitDisease[d] += Math
											.log(parameters.relRiskClass[a][g][currentRiskValue][dnumber]);
								if (parameters.riskType == 2)
									logitDisease[d] += Math
											.log(Math
													.pow(
															riskFactorValue
																	- parameters.refClassCont,
															parameters.relRiskContinue[a][g][dnumber]));
								if (parameters.riskType == 3) {
									if (currentRiskValue != parameters.durationClass)
										logitDisease[d] += Math
												.log(parameters.relRiskClass[a][g][currentRiskValue][dnumber]);
									else
										logitDisease[d] += Math
												.log((parameters.relRiskDuurBegin[a][g][dnumber] - parameters.relRiskDuurEnd[a][g][dnumber])
														* Math
																.exp(-currentDurationValue
																		* parameters.alfaDuur[a][g][dnumber])
														+ parameters.relRiskDuurEnd[a][g][dnumber]);
								}
							}
						}
						/*
						 * CALCULATE PROBABILITY OF EACH COMBINATION OF DISEASES
						 * number of combinations= 2^Ndiseases
						 */
						int nDiseases = parameters.clusterStructure[cluster].nInCluster;
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
							 * in het algemeen:
							 *  p-combi= product [P(each dep
							 * disease|all independent
							 * diseases)]product[P(independent disease)] dus:
							 * GENERAL:   product [p (disease |causes (if any)]
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
								int d1number = parameters.clusterStructure[cluster].diseaseNumber[d1];
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

									if (parameters.clusterStructure[cluster].dependentDisease[d1]) {
										if (parameters.baselinePrevalenceOdds[a][g][d1number] != 0) {
											double logitCurrent = logitDisease[d1];

											for (int d2 = 0; d2 < parameters.clusterStructure[cluster].nInCluster; d2++) {
												if ((combi & (1 << d2)) == (1 << d2)) {

													logitCurrent += Math
															.log(parameters.relRiskDiseaseOnDisease[a][g][cluster][d2][d1]);
												}
												probCurrent = 1 / (1 + Math
														.exp(-logitCurrent));
											}
										} else
											probCurrent = 0;

									} else /* == independent disease */
										probCurrent = 1 / (1 + Math
												.exp(-logitDisease[d1]));

								}/* end if disease d1 ==1 */ else{
									/* now if d1 is zero in combination */;
									if (parameters.baselinePrevalenceOdds[a][g][d1number] != 0) {
										
								if (parameters.clusterStructure[cluster].dependentDisease[d1]) {
										double logitCurrent = logitDisease[d1];

										for (int d2 = 0; d2 < parameters.clusterStructure[cluster].nInCluster; d2++)
											if ((combi & (1 << d2)) == (1 << d2))
												logitCurrent += Math
														.log(parameters.relRiskDiseaseOnDisease[a][g][cluster][d2][d1]);
										/*
										 * NB now exp(+x) a this is 1-p in stead
										 * of p
										 */
										probCurrent = 1 / (1 + Math
												.exp(logitCurrent));
									} else
										probCurrent = 1 / (1 + Math
												.exp(logitDisease[d1]));
								} else
									
								{probCurrent = 1;}
								

							}probCombi *= probCurrent;} // end loop over d1
							float value = (float) probCombi;
							currentIndividual.add(new FloatCharacteristicValue(
									0, characteristicIndex, value));
							characteristicIndex++;
						} // end loop over combi
					} // end loop over clusters
					// tot slot nog characteristiek voor
					// survival;

					currentIndividual.add(new FloatCharacteristicValue(0,
							characteristicIndex, 1F));

					initialPopulation.addIndividual(currentIndividual);

					;

					;
				}// end sim loop
				;

			}
		// end age and sex group
		return initialPopulation;
	}
	

	public  void writeToXMLFile(Population population, int stepNumber, File xmlFileName)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = (DocumentBuilder) dbfac
				.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		String elementName = population.getElementName();
		Element element = document.createElement(population.xmlElementName);
		Element nameElement = document.createElement(population.xmlLabelAttributeName);
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
}
	
	


