package nl.rivm.emi.dynamo.estimation;
public class Morbidity {

	/**
	 * @param DiseaseClusterData: object with the general input data on the disease cluster
	 * @param RR: array of double data giving the relative risks for each person i
	* @param prev0: array of double giving the baseline prevalence rates for all diseases
	* @FIELD DOUBLE PROB [][]: Probability of the combination of disease 1 and 2
	* If both indexes are the same than this is the probability of having this disease
	* 
	* */

	/*
	 * morbidity contains for all combinations of diseases within a cluster
	 * the probability of having both diseases. for a single disease it gives
	 * the probability of having this disease
	 * 
	 * the is a class to be used on the individual level
	 */
	public double prob[][];
	//public int nDis;
	//public int dStart;

	public Morbidity(DiseaseClusterData D, DiseaseClusterStructure S, double[] RR, double[] prevOdds0) {
		
		int nDis = S.nInCluster;
		int dStart = S.diseaseNumber[0]; /* number of first disease */

		/*
		 * p-dependent = rr(risk)*inc0*(1*(1-Pindep)+RRdis(Pindep))=
		 * rr(risk)*inc0*(1+Pindep(RRdis-1))= Pindep= inc0*RR(risk-Pindep)
		 */

		prob = new double[nDis][nDis];
		// d1 , d2 , d3 are disease index within the current cluster
		for (int d1 = 0; d1 < nDis; d1++) {
			double prob1 =RR[d1 + dStart]
								* prevOdds0[d1 + dStart]/(1+RR[d1 + dStart]
																* prevOdds0[d1 + dStart]);
			for (int d2 = 0; d2 < nDis; d2++) {
				double prob2 =RR[d2 + dStart]
									* prevOdds0[d2 + dStart]/(1+RR[d2 + dStart]
																	* prevOdds0[d2 + dStart]);
				
				if (d1 == d2) {
					// prob[d1][d1]= prob [d1]
					// if d1 and d2 are independent, p[d1] is just prob1, the probability if only risk factors count
					prob[d1][d2] = prob1; 
					// if d1==d2 is dependent disease, 
					//p(d)=sum(all combis of presence/absence indep) of p(combi)*p(d|combi)
					//== something recurrent
					// loop over all independent 
					// diseases to get their effects
					if (S.dependentDisease[d1] == true) {
						prob[d1][d2] = 0;
						// loop through all combinations of independent diseases
						// each combination contributes p(combi)*(p(d1|combi) to p(d1)
						for (int combi = 0; combi < Math.pow(S.NIndep,2); combi++) {
							//calculate RR for this combi ;
							// calculate prob(combi);
							double RRcombi=1;
							double probCombi=1;
							for (int d3=0;d3<S.nInCluster;d3++) {
								int Ndi=S.diseaseNumber[d3];
								double prob3=RR[Ndi ]
												* prevOdds0[Ndi ]/(1+RR[Ndi]
																				* prevOdds0[Ndi]);
					
							   if ((combi & (1<<d3))==(1<<d3) ) RRcombi*=D.RRdisExtended[d3][d1];
							   if ((combi & (1<<d3))==(1<<d3) ) probCombi*= prob3; 
							   else probCombi*=(1-prob3);
							   }
							// probability of d1 in those with this combination prob3=
							prob[d1][d2] += probCombi*RRcombi*prevOdds0[d1 + dStart]/(1+RRcombi* prevOdds0[d1 + dStart]);
						}}}
						
				else if (S.dependentDisease[d1] == false
						& S.dependentDisease[d2] == false)
					prob[d1][d2] = prob1*prob2;

				else if (S.dependentDisease[d1] == false
						& S.dependentDisease[d2] == true)
					prob[d1][d2] = prob1 * (D.RRdisExtended[d1][d2]*RR[d2 + dStart]	* prevOdds0[d2 + dStart])/
					(1+ D.RRdisExtended[d1][d2]*RR[d2 + dStart]	* prevOdds0[d2 + dStart]);
				else if (S.dependentDisease[d1] == true
						& S.dependentDisease[d2 ] == false)
					prob[d1][d2] = prob2 * (D.RRdisExtended[d2][d1]*RR[d1 + dStart]	* prevOdds0[d1 + dStart])/
					(1+ D.RRdisExtended[d2][d1]*RR[d1 + dStart]	* prevOdds0[d1 + dStart]);
				else if (S.dependentDisease[d1] == true
						& S.dependentDisease[d2] == true)
					// now we need to loop through all combinations of independent diseases
					// each contributes p(combi)P(d1^d2|combi) to the overall p(d1^d2)
					
					for (int combi = 0; combi < Math.pow(S.NIndep,2); combi++) {
						//calculate RR for this combi ;
						// calculate prob(combi);
						double RRcombi1=1; // relative risk from particular combination of diseases for disease d1
						double RRcombi2=1; // relative risk from particular combination of diseases for disease d2
						double probCombi=1;
						for (int d3=0;d3<S.nInCluster;d3++) {
							
							int Ndi= S.diseaseNumber[d3];
							// d3 = current disease within the combination (loops over all diseases)
							double prob3=RR[Ndi ]
											* prevOdds0[Ndi ]/(1+RR[Ndi]
																			* prevOdds0[Ndi]);
							
						   if ((combi & (1<<d3))==(1<<d3) )
						   {RRcombi1*=D.RRdisExtended[d3][d1];
						   RRcombi2*=D.RRdisExtended[d3][d2];
						   probCombi*= prob3; }
						   else probCombi*=(1-prob3);
						   } // calculations for single combination
						// add to probability of d1^d1 
						prob[d1][d2] += probCombi*(RR[d1 + dStart]*RRcombi1*prevOdds0[d1 + dStart]/(1+RR[d1 + dStart]*RRcombi1* prevOdds0[d1 + dStart]))
						*(RR[d2 + dStart]*RRcombi2*prevOdds0[d2 + dStart]/(1+RR[d2 + dStart]*RRcombi2* prevOdds0[d2 + dStart]));
					}}}
					
					
					
				// ideas behind dependent diseases
				// there are clusters of dependent diseases
				// in each cluster there are dependent and independent diseases
				// there is a matrix of dimension n-dependent by n-independent
				// that contains the RR's.
				// The probability of a dependent disease is p(r)*RR*p(indep),
				// where p(r) is the probability of the disease for health
				// persons with riskfactors r
				// RR the vector of relative risk on the dependent disease (part
				// of the matrix) and p(independent) the probability on the independent
				// diseases

			}
		
	

	public double calculateRRindep(int[] index, int level, int d1, int d2,
			double RRdis[][], double RR[], double prevOdds0[], int DStart) {
		double prob;
		
		// OBSOLETE 
		
		// level counts the independent disease to circle through
		// it starts with Nindependent-1 if this method is called first,
		// and is decreased at each recursive call
		//

		int current = index[level]+DStart;
		/*
		 * pdisease = [RRdis*prob(1)+(1-prob(1))]*rr*prob =
		 * rr*prob*(1+(RRdis-1)Prob(1))
		 */
		// current is the disease number of the current independent risk factor (index in the entire array of diseases)
		// RRindep1/2 are the average RR's due to having the current disease on
		// dependent disease 1 and 2 respectively;
		double prevCurrent=RR[current] * prevOdds0[current]/(1+ RR[current] *prevOdds0[current]);
		double RRindep1 = (1 +  prevCurrent
				* (RRdis[index[level]][d1] - 1));
		double RRindep2 = (1 + prevCurrent
				* (RRdis[index[level]][d2] - 1));

		if (level == 0)
			prob = RRindep1 * RRindep2;
		else
			prob = RRindep1
					* RRindep2
					* calculateRRindep(index, level - 1, d1, d2, RRdis, RR,
							prevOdds0, DStart);
		return prob;
	}
  public double [][] addBlock(double [][] baseMat, double weight, DiseaseClusterStructure S)
  { int nDis = S.nInCluster;
	int dStart = S.diseaseNumber[0]; /* number of first disease */

	  for (int d1=0;d1<nDis;d1++)for (int d2=0;d2<nDis;d2++){
	//calculate matrix V= 
		//	average(probdisease (d1 &d2|i))/p(d1)-
		//	      average(probdisease(d1|i)*probdisease(d2|i))/p(d1)
	  
	  // forget /p(d1) for the moment, than this can be calculated by summing
	  // probdisease (d1 &d2|i))- (probdisease(d1|i)*probdisease(d2|i))
	  // weighted for the probability of the riskfactor combination
	  
	  baseMat[d1+dStart][d2+dStart]+=weight*(this.prob[d1][d2]-this.prob[d1][d1]*this.prob[d2][d2]);
	  ;}
  return baseMat;}
	  
  
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
