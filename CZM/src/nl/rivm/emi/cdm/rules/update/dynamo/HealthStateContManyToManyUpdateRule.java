/**
 * 
 */
package nl.rivm.emi.cdm.rules.update.dynamo;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import nl.rivm.emi.cdm.exceptions.CDMConfigurationException;
import nl.rivm.emi.cdm.exceptions.CDMUpdateRuleException;
import nl.rivm.emi.cdm.exceptions.DynamoUpdateRuleConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * @author Hendriek This call implements a version of
 *         HealthStateManyToManyUpdateRule specifically for categorical Risk
 *         Factors. Such specialisation provided as faster update method as it
 *         does not need to check the type of risk factor. 
 * 
 * 
 */

public class HealthStateContManyToManyUpdateRule 
	

	
	 extends
			HealthStateManyToManyUpdateRule {

		

		/**
		 * @throws ConfigurationException
		 * @throws CDMUpdateRuleException
		 */
int[][][] atIndex	;	
		/*
		 * indexes are : cluster number, disease number (within cluster) . For this combination there is an arrya
		 * that gives the locations within the transition rate matrix where attributable mortality for this disease should
		 * be added to the diagonal of the matrix
		 */
		
		int [][][] incIndex	;	
		/*
		 * indexes are : cluster number, disease number (within cluster) . For this combination there is an arrya
		 * that gives the locations within the transition rate matrix where incidence rate for this disease should
		 * be added to the diagonal of the matrix
		 */
		
		int [][][] incRowIndex	;	
		/*
		 * indexes are : cluster number, disease number (within cluster) . For this combination there is an arrya
		 * that gives the rows within the transition rate matrix where incidence rate for this disease should
		 * be added below the diagonal incidence entry indicated by incIndex
		 * 
		 */
		
		float [][][][][] RRdis;
		/*
		 *  indexes are :age, gender, cluster number, disease number (within cluster) . For this combination 
		 * RR gives the value of RR due to other diseases with which the incidence should be multiplied at this place
		 * 
		 */
		
		int [][][][] disFatalIndex; 
		           /*
		   		 *  indexes are :age, gender, cluster number. 
		   		 * disfatalIndex gives a list of numbers of disease within this cluster that have a fatal component 
		   		 */
		
		float [][][][][] RRdisFatal; 
		
		 /*
   		 *  indexes are :age, gender, cluster number, number of fatal disease (number as given in disFatalIndex). 
   		 * RRdisFatal gives the disease related RR's for each location in the matrix for the fatal diseases 
   		 * 	
   		 * 
   		 * 	 */
		
		float [][][]  nonCuredRatio;
		/*  indexes are :age, gender, cluster number
		 *  gives the ratio not-cured/total 
		 * 
		 */
		MatrixExponential matExp= new MatrixExponential();
		
		public HealthStateContManyToManyUpdateRule() throws ConfigurationException,
				CDMUpdateRuleException {
			super();
			// TODO Auto-generated constructor stub
		}

		public Object update(Object[] currentValues) throws CDMUpdateRuleException {

			float[] newValue = null;

			try {
				int ageValue = (int) getFloat(currentValues, getAgeIndex());
				int sexValue = getInteger(currentValues, getSexIndex());
				if (ageValue > 95)
					ageValue = 95;
				int riskFactorValue = getInteger(currentValues, riskFactorIndex1);
				float[] oldValue = getValues(currentValues,
						getCharacteristicIndex());
				newValue = new float[oldValue.length];
				float[] currentDiseaseStateValues = new float[oldValue.length];
				/*
				 * float totInStates=0; for (int i=0;i<oldValue.length-1;i++)
				 * {currentDiseaseStateValues[i+1]=oldValue[i];
				 * totInStates+=oldValue[i];}
				 * currentDiseaseStateValues[0]=1-totInStates;
				 */

				// array currentDiseaseValue holds the current values of the
				// disease-characteristics
				// 
				// private int[] numberOfDiseasesInCluster == array over clusters;
				// private int[] clusterStartsAtDiseaseNumber == array over
				// clusters;
				// private int totalNumberOfDiseases;
				// private int nCluster = -1;
				// private int[] DiseaseNumberWithinCluster;== array over diseases
				int currentStateNo = 0;
				double survival = 0;
				double survivalFraction = calculateOtherCauseSurvival(riskFactorValue,ageValue,sexValue);
			
				float[][] currentTransMat;
				double expAI ;
				double expI ;
				double expA ;
				int d;
				double incidence;
				double incidence2;
				double atMort;
				for (int c = 0; c < nCluster; c++) {
					
					if (numberOfDiseasesInCluster[c] == 1) {
                        
						d = clusterStartsAtDiseaseNumber[c];
						atMort=attributableMortality[ageValue][sexValue][d];
						incidence=calculateIncidence(riskFactorValue,ageValue,sexValue,d);
						/*
						 * for faster execution for results that are used
						 * multipletimes: only perform Math.exp once and save
						 * results
						 */
						expA = Math.exp( - atMort
								* timeStep);
						expI = Math.exp(-incidence 
							* timeStep);
						if (Math.abs(incidence - atMort) > 1E-15)
							expAI=expA/expI;
						else expAI=1;
						// finci = ((p0 * em - i) * exp((i - em) * time) + i * (1 -
						// p0))
						// / ((p0 * em - i) * exp((i - em) * time) + em * (1 - p0))
						if (expAI!=1)
							newValue[currentStateNo] = (float) (((oldValue[currentStateNo]
									* atMort - incidence)
									* expAI + incidence
									* (1 - (double) oldValue[currentStateNo])) / ((oldValue[currentStateNo]
									* atMort - incidence)
									* expAI + atMort
									* (1 - (double) oldValue[currentStateNo])));
						else
							newValue[currentStateNo] = (float) (1 - (1 - oldValue[currentStateNo])
									/ (1 + incidence
											* (1 - oldValue[currentStateNo])
											* timeStep));
						/*
						 * if incidence equal to attributable mortality, the
						 * denominator becomes zero and we need another formula
						 */
                        if (disFatalIndex[ageValue][sexValue][c][0]==0)
						survivalFraction *= Math.exp(-getTimeStep()
								* calculateFatalIncidence(riskFactorValue,ageValue,sexValue,d))
								* (atMort * (1 - oldValue[d])
										* expI + (atMort
										* oldValue[d] - incidence)
										* expA)
								/ (atMort - incidence);
                        else
                        	survivalFraction *=  (atMort * (1 - oldValue[d])
    										* expI + (atMort
    										* oldValue[d] - incidence)
    										* expA)
    								/ (atMort - incidence);
						currentStateNo++;
					} else if (withCuredFraction[c]) {
						 d = clusterStartsAtDiseaseNumber[c];
						 // TODO: change input with cured fraction in stead of two diseases
						 atMort=attributableMortality[ageValue][sexValue][d+1];
						 incidence2=calculateIncidence(riskFactorValue,ageValue,sexValue,d+1);
						 incidence=incidence2/nonCuredRatio[ageValue][sexValue][c];
						 
						                          						
						/*
						 * for faster execution for results that are used
						 * multipletimes: only perform Math.exp once and save
						 * results
						 */
						
						expI = Math.exp((-incidence)
								* -getTimeStep());
						expA = Math.exp(-atMort * -getTimeStep());
						double transMat10;
						double transMat20;
						if (incidence != 0)
							transMat10 = (1 - expI) * (incidence-incidence2)
									/ incidence;
						else
							transMat10 = 0;

						if (incidence == atMort)
							transMat20 = expI * incidence2;
						else
							transMat20 = (expA - expI)
									* incidence2
									/ (incidence - atMort);

						

						survival = (1 - oldValue[d] - oldValue[d + 1])
								* expI + oldValue[d] + oldValue[d + 1]
								* expA
						+ (1 - oldValue[d] - oldValue[d + 1])
								* (transMat10 + transMat20);
						newValue[currentStateNo] = (float) (((1 - oldValue[d] - oldValue[d + 1])
								* transMat10 + oldValue[d]) / survival);
						newValue[currentStateNo + 1] = (float) (((1 - oldValue[d] - oldValue[d + 1])
								* transMat20 + oldValue[d + 1] * expA) / survival);
/* NB disease with cured fraction can not be fatal at the same time */
						survivalFraction *= survival;

					}

                     else /* now cluster diseases */
					{

						/* Multiply the matrix with the old values (column vector) */
						double unconditionalNewValues[] = new double[nCombinations[c]];
						
						/* calculate the healthy state */
						double currentHealthyState = 1;
						for (int state = currentStateNo; state < currentStateNo
								+ nCombinations[c] - 1; state++)
							currentHealthyState -= oldValue[state];

						/*
						 * NB the unconditional new state starts at 0 with the
						 * healthy state, so oldvalue[1] belongs with
						 * unconditionalnewstate[0]
						 */
						

						/* make transition rate matrix */
						double[][] rateMatrix=new double [nCombinations[c]][nCombinations[c]];
						for (int dc=0;dc<numberOfDiseasesInCluster[c];dc++){
							d = clusterStartsAtDiseaseNumber[c]+dc;
							incidence=calculateIncidence(riskFactorValue,ageValue,sexValue,d);
						    atMort=attributableMortality[ageValue][sexValue][d];
							for (int loc=0;loc<nCombinations[c]>>1;loc++){
								rateMatrix[incIndex[c][dc][loc]][incIndex[c][dc][loc]]+=
									-incidence*RRdis[ageValue][sexValue][c][dc][loc];
								rateMatrix[incRowIndex[c][dc][loc]][incIndex[c][dc][loc]]+=
									incidence*RRdis[ageValue][sexValue][c][dc][loc];
								rateMatrix[atIndex[c][dc][loc]][atIndex[c][dc][loc]]+=-atMort;
									
							}// end loop locations of disease
							}// end loop over diseases in cluster
						
						/* now loop over the fatal diseases (separate, as this is only done if the
						 * disease is really fatal to save running time 
						 */
							if (disFatalIndex[ageValue][sexValue][c][0]>=0)
								for (int fataldis=0;fataldis<disFatalIndex[ageValue][sexValue][c].length;fataldis++){
								incidence=calculateFatalIncidence(riskFactorValue,ageValue,sexValue,fataldis+clusterStartsAtDiseaseNumber[c]);
								for (int loc=0;loc<nCombinations[c];loc++){
															
								rateMatrix[disFatalIndex[ageValue][sexValue][c][loc]]
								          [disFatalIndex[ageValue][sexValue][c][loc]]+=
								        	  -incidence*RRdisFatal[ageValue][sexValue][c][fataldis][loc];
								
										
						} // end loop over locations
							} //end loop over fatal diseases
						float [][] transMat = 	matExp
								.exponentiateFloatMatrix(rateMatrix);
							
						
						for (int state1 = 0; state1 < nCombinations[c]; state1++) // row
						{ /* transitionProbabilities are [to][from] */
							unconditionalNewValues[state1] = transMat[state1][0]
									* currentHealthyState;
							for (int state2 = 1; state2 < nCombinations[c]; state2++)
								// column=from

								unconditionalNewValues[state1] += transMat[state1][state2]
										* oldValue[state2 - 1 + currentStateNo];
						}
						/* calculate survival */

						for (int state = 0; state < nCombinations[c]; state++) {
							survival += unconditionalNewValues[state];
						}
						survivalFraction *= survival;
						for (int state = currentStateNo; state < currentStateNo
								+ nCombinations[c] - 1; state++) {
							newValue[state] = (float) (unconditionalNewValues[state
									- currentStateNo + 1] / survival);
						}
						;
						currentStateNo += nCombinations[c] - 1;
					} // end if statement for cluster diseases

				} // end loop over clusters
				newValue[currentStateNo] = (float) survivalFraction
						* oldValue[currentStateNo];

				return newValue;
			} catch (CDMUpdateRuleException e) {
				log.fatal(e.getMessage());
				log
						.fatal("this message was issued by HealthStateMultiToOneUpdateRule"
								+ " when updating characteristic number "
								+ characteristicIndex);
				e.printStackTrace();
				throw e;

			}

		}

		private double calculateIncidence(float riskFactorValue, int ageValue,
				int sexValue, int diseaseNumber) {
			double incidence = 0;

			
			incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
					* Math
							.pow(
									(riskFactorValue - referenceValueContinous),
									relRiskContinous[diseaseNumber][ageValue][sexValue]);
			return incidence;
		}

		private double calculateFatalIncidence(float riskFactorValue, int ageValue,
				int sexValue, int diseaseNumber) {
			double incidence = 0;
			incidence = baselineIncidence[diseaseNumber][ageValue][sexValue]
			                                   					* Math
			                                   							.pow(
			                                   									(riskFactorValue - referenceValueContinous),
			                                   									relRiskContinous[diseaseNumber][ageValue][sexValue]);
			

			return incidence;
		}

		private double calculateOtherCauseSurvival(int riskFactorValue,
				int ageValue, int sexValue) {
			double otherCauseSurvival = 0;

			otherCauseSurvival =Math.exp(- baselineOtherMort[ageValue][sexValue]
			 			                                   					* Math
				                                   							.pow(
				                                   									(riskFactorValue - referenceValueContinous),
				                                   									relRiskOtherMortContinous[ageValue][sexValue]));
				 
				
				
				

			return otherCauseSurvival;
		}

		public boolean loadConfigurationFile(File configurationFile)
				throws ConfigurationException {
			boolean success = false;
			try {
				XMLConfiguration configurationFileConfiguration = new XMLConfiguration(
						configurationFile);
				ConfigurationNode rootNode = configurationFileConfiguration
						.getRootNode();
				if (configurationFileConfiguration.getRootElementName() != globalTagName)

					throw new DynamoUpdateRuleConfigurationException(" Tagname "
							+ globalTagName
							+ " expected in file for updaterule ClusterDisease"
							+ " but found tag " + rootNode.getName());

				/* first handle the general information (not disease dependent) */
				handleCharID(configurationFileConfiguration);
				handleRiskType(configurationFileConfiguration);
				handleNCat(configurationFileConfiguration);
				handleNClusters(configurationFileConfiguration);
				handleOtherMort(configurationFileConfiguration);
				handleDiseaseData(rootNode);
				/* make matrixes with transition probabilities */
				MatrixExponential matExp = MatrixExponential.getInstance();
				
				atIndex = new  int[nCluster][][];
				incIndex = new  int[nCluster][][];
				incRowIndex = new  int[nCluster][][];
				RRdis= new  float[96][2][nCluster][][];
				RRdisFatal= new  float[96][2][nCluster][][];
				disFatalIndex= new  int[96][2][nCluster][];
				nonCuredRatio=new float[96][2][nCluster];
				for  (int c = 0; c < nCluster; c++){
					atIndex[c] = new  int[numberOfDiseasesInCluster[c]][nCombinations[c]/2];
					incIndex[c] = new  int[numberOfDiseasesInCluster[c]][nCombinations[c]/2];
					incRowIndex[c] = new  int[numberOfDiseasesInCluster[c]][nCombinations[c]/2];
					for (int a = 0; a < 96; a++)
						for (int g = 0; g < 2; g++){
					RRdis[a][g][c] = new  float[nCombinations[c]][nCombinations[c]/2];
					int nFatal=0;
					for (int d = 0; d < numberOfDiseasesInCluster[c]; d++){
						int dd=clusterStartsAtDiseaseNumber[c]+d;
						if (baselineFatalIncidence[dd][a][g]>0) nFatal++;
						nonCuredRatio[a][g][c]=baselineIncidence[dd+1][a][g]/(baselineIncidence[dd][a][g]+baselineIncidence[dd+1][a][g]);
					}
					if (nFatal>0){disFatalIndex[a][g][c]= new  int[nFatal];
					RRdisFatal[a][g][c]= new  float[nFatal][nCombinations[c]];
					}
					else {disFatalIndex[a][g][c]= new  int[1];
					disFatalIndex[a][g][c][0]=-1;}
					int indexFatal=0;
					for (int d = 0; d < numberOfDiseasesInCluster[c]; d++){
						int dd=clusterStartsAtDiseaseNumber[c]+d;
						if (baselineFatalIncidence[dd][a][g]>0)disFatalIndex[a][g][c][indexFatal]=d; 
						
					}
					
					
					}
						}
				
				                             
				
							/* int[] numberOfDiseasesInCluster == array over clusters;
							// int[] clusterStartsAtDiseaseNumber == array over clusters;
							// int totalNumberOfDiseases;
							// int nCluster = -1;
							// int[] DiseaseNumberWithinCluster;== array over diseases */

				int currentStateNo = 0;

				for (int c = 0; c < nCluster; c++) {
					if (numberOfDiseasesInCluster[c] == 1)  {
									
									
									
									
									
									
										

				currentStateNo++;
				} else if (withCuredFraction[c]) {
						currentStateNo+=2;
									
				} else // cluster of dependent diseases
				{
									
									
									

				
										
										
											
											/*
											 * Matrix entry is formed as: / -
											 * attributable Mortality for each
											 * disease that is 1 in combi - sum
											 * incidence to all other disease that
											 * are 0 in combi (including RR's as
											 * above) - sum fatal incidences
											 */
											

		 		    for (int d = 0; d < numberOfDiseasesInCluster[c]; d++) {
                        int nInc=0;
		 		        int nAt=0;
		 		    	for (int column = 0; column < nCombinations[c]; column++){
		 		    		
		 		    		
		 		    		
		 		    		/*
													 * first add fatal incidence
													 * irrespective of value of d
													 */
		 		    	
		 		    		for (int a = 0; a < 96; a++)
								for (int g = 0; g < 2; g++){
		 		    		if	(disFatalIndex[a][g][c][0]==-1){
		 		    		
								
		 		    			for (int f = 0; f< disFatalIndex[a][g][c].length; f++)
		 		    				if	(disFatalIndex[a][g][c][0]==d){
								RRdisFatal[a][g][c][disFatalIndex[a][g][c][f]][f] = 1;
									for (int dCause = 0; dCause < getNDiseases(); dCause++)
										if ((column & (1 << dCause)) == (1 << dCause))
											RRdisFatal[a][g][c][disFatalIndex[a][g][c][f]][f] *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];
		 		    				}}}
													
								if ((column & (1 << d)) != (1 << d)){
					/* d is 0, thus incidence should be added
								 */
									
		 					incIndex [c][d][nInc]=column;
								/* calculate the row where incidence should also be added */
							incRowIndex[c][d][nInc]=column+(1<<d);
							/* calculate the RR from diseases */
							for (int a = 0; a < 96; a++)
								for (int g = 0; g < 2; g++){
							RRdis[a][g][c][d][nInc] = 1;
							
							for (int dCause = 0; dCause < getNDiseases(); dCause++)
								if ((column & (1 << dCause)) == (1 << dCause))
									RRdis[a][g][c][d][nInc] *= relativeRiskDiseaseOnDisease[c][a][g][dCause][d];
									
		 					
		 									
								}	
								nInc++;	
									
										
										/* else  d=1, then atmort should be added */
									} else {
														
										atIndex [c][d][nAt]=column;					// add at
											nAt++;			
								}
		 		    	}
								// end loop over columns
												
		 		    }// end loop over diseases within cluster
		 		    
									currentStateNo += nCombinations[c] - 1;
		 		    
											
								} // end if statement for cluster diseases

							} // end loop over clusters

						

				success = true;
				return success;
			} catch (NoSuchElementException e) {
				throw new ConfigurationException(
						CDMConfigurationException.noConfigurationTagMessage
								+ nDiseasesLabel);
			} catch (DynamoUpdateRuleConfigurationException e) {
				log.fatal(e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return success;
		}

	}

