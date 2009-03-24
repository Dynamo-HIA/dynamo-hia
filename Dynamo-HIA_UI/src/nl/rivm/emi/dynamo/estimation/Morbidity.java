package nl.rivm.emi.dynamo.estimation;

public class Morbidity {

	/**
	 * @param DiseaseClusterData
	 *            : object with the general input data on the disease cluster
	 * @param RR
	 *            : array of double data giving the relative risks for each
	 *            person i
	 * @param prev0
	 *            : array of double giving the baseline prevalence rates for all
	 *            diseases
	 * @FIELD DOUBLE PROB [][]: Probability of the combination of disease 1 and
	 *        2 If both indexes are the same than this is the probability of
	 *        having this disease
	 * 
	 * */

	/*
	 * morbidity contains for all combinations of diseases within a cluster the
	 * probability of having both diseases. for a single disease it gives the
	 * probability of having this disease
	 * 
	 * the is a class to be used on the individual level
	 */
	private double prob[][];

	// public int nDis;
	// public int dStart;

	/**
	 * @param D
	 *            : data of the cluster
	 * @param S
	 *            : cluster structure
	 * @param RR
	 *            : relative risk of this person for each disease
	 * @param prevOdds0
	 *            : prevalence odds for the diseases
	 */
	public Morbidity(DiseaseClusterData D, DiseaseClusterStructure S,
			double[] RR, double[] prevOdds0) {

		int nDis = S.getNInCluster();
		int dStart = S.getDiseaseNumber()[0]; /* number of first disease */

		/*
		 * p-dependent = rr(risk)inc0(1(1-Pindep)+RRdis(Pindep))=
		 * rr(risk)inc0(1+Pindep(RRdis-1))= Pindep= inc0RR(risk-Pindep)
		 */

		/**
		 * prob [d1][d2] contains the probability of having both disease d1 and
		 * d2; so prob[d1][d2]==prob[d2][d1] prob [d1][d1] contains the
		 * probability of having d1
		 * 
		 * d1 and d2 independent:
		 * 
		 * P(d1^d2)=P(d1)*P(d2)
		 * 
		 * where P(d1) is the probability of d1 for this person based only on
		 * riskfactors
		 * 
		 * d1 --> d2 (d1 cause of d2)
		 * 
		 * P(d1^d2)=P(d1)*P(d2|d1)=P(d1)*P(d2)*RR(d2|d1)
		 * 
		 * d2 --> d1
		 * 
		 * P(d1^d2)=P(d2)*P(d1|d2)=P(d1)*P(d2)*RR(d1|d2)
		 * 
		 * d2 and d1 both dependent on other independent diseases: combi = each
		 * possible combination of other independent diseases:
		 * 
		 * P(d1^d2)=sum(over combi) of (P(combi)P(d1|combi)*P(d2|combi))=
		 * 
		 *== sum(over combi) of (P(combi)P(d1)P(d2)RR(d1|combi)*RR(d2|combi))
		 * 
		 */

		setProb(new double[nDis][nDis]);
		// d1 , d2 , d3 are disease index within the current cluster
		for (int d1 = 0; d1 < nDis; d1++) {
			double prob1 = RR[d1 + dStart] * prevOdds0[d1 + dStart]
					/ (1 + RR[d1 + dStart] * prevOdds0[d1 + dStart]);
			for (int d2 = 0; d2 < nDis; d2++) {
				double prob2 = RR[d2 + dStart] * prevOdds0[d2 + dStart]
						/ (1 + RR[d2 + dStart] * prevOdds0[d2 + dStart]);

				if (d1 == d2) {
					// prob[d1][d1]= prob [d1]
					// if d1 and d2 are independent, p[d1] is just prob1, the
					// probability if only risk factors count
					getProb()[d1][d2] = prob1;

					// if d1==d2 is dependent disease,
					// p(d)=sum(all combis of presence/absence indep) of
					// p(combi)*p(d|combi)

					// loop over all independent
					// diseases to get their effects
					if (S.getDependentDisease()[d1] == true) {
						getProb()[d1][d2] = 0;
						// loop through all combinations of independent diseases
						// each combination contributes p(combi)*(p(d1|combi) to
						// p(d1)
						for (int combi = 0; combi < Math.pow(2, S.getNIndep()); combi++) {
							// calculate RR for this combi ;
							// calculate prob(combi);
							double RRcombi = 1;
							double probCombi = 1;
							for (int d3 = 0; d3 < S.getNIndep(); d3++) {
								int Ndi = S.getIndexIndependentDiseases()[d3]
										+ dStart;
								double prob3 = RR[Ndi] * prevOdds0[Ndi]
										/ (1 + RR[Ndi] * prevOdds0[Ndi]);

								if ((combi & (1 << d3)) == (1 << d3))
									RRcombi *= D.getRRdisExtended()[S
											.getIndexIndependentDiseases()[d3]][d1];
								if ((combi & (1 << d3)) == (1 << d3))
									probCombi *= prob3;
								else
									probCombi *= (1 - prob3);
							}
							// probability of d1 in those with this combination
							// prob3=
							getProb()[d1][d2] += probCombi
									* RRcombi
									* RR[d1 + dStart]
									* prevOdds0[d1 + dStart]
									/ (1 + RRcombi * RR[d1 + dStart]
											* prevOdds0[d1 + dStart]);
						}
					}
				}

				else if (S.getDependentDisease()[d1] == false
						& S.getDependentDisease()[d2] == false)
					getProb()[d1][d2] = prob1 * prob2;

				else if (S.getDependentDisease()[d1] == false
						& S.getDependentDisease()[d2] == true)
					getProb()[d1][d2] = prob1
							* (D.getRRdisExtended()[d1][d2] * RR[d2 + dStart] * prevOdds0[d2
									+ dStart])
							/ (1 + D.getRRdisExtended()[d1][d2]
									* RR[d2 + dStart] * prevOdds0[d2 + dStart]);
				else if (S.getDependentDisease()[d1] == true
						& S.getDependentDisease()[d2] == false)
					getProb()[d1][d2] = prob2
							* (D.getRRdisExtended()[d2][d1] * RR[d1 + dStart] * prevOdds0[d1
									+ dStart])
							/ (1 + D.getRRdisExtended()[d2][d1]
									* RR[d1 + dStart] * prevOdds0[d1 + dStart]);
				else if (S.getDependentDisease()[d1] == true
						& S.getDependentDisease()[d2] == true)
					// now we need to loop through all combinations of
					// independent diseases
					// each contributes p(combi)P(d1^d2|combi) to the overall
					// p(d1^d2)

					for (int combi = 0; combi < Math.pow(2, S.getNIndep()); combi++) {
						// calculate RR for this combi ;
						// calculate prob(combi);
						double RRcombi1 = 1; // relative risk from particular
						// combination of diseases for
						// disease d1
						double RRcombi2 = 1; // relative risk from particular
						// combination of diseases for
						// disease d2
						double probCombi = 1;
						for (int d3 = 0; d3 < S.getNIndep(); d3++) {

							int Ndi = S.getIndexIndependentDiseases()[d3]
									+ dStart;
							// d3 = current disease within the combination
							// (loops over all diseases)
							double prob3 = RR[Ndi] * prevOdds0[Ndi]
									/ (1 + RR[Ndi] * prevOdds0[Ndi]);

							if ((combi & (1 << d3)) == (1 << d3)) {
								RRcombi1 *= D.getRRdisExtended()[S
										.getIndexIndependentDiseases()[d3]][d1];
								RRcombi2 *= D.getRRdisExtended()[S
										.getIndexIndependentDiseases()[d3]][d2];
								probCombi *= prob3;
							} else
								probCombi *= (1 - prob3);
						} // calculations for single combination
						// add to probability of d1^d1
						getProb()[d1][d2] += probCombi
								* (RR[d1 + dStart] * RRcombi1
										* prevOdds0[d1 + dStart] / (1 + RR[d1
										+ dStart]
										* RRcombi1 * prevOdds0[d1 + dStart]))
								* (RR[d2 + dStart] * RRcombi2
										* prevOdds0[d2 + dStart] / (1 + RR[d2
										+ dStart]
										* RRcombi2 * prevOdds0[d2 + dStart]));
					}
			}
		}

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

		int current = index[level] + DStart;
		/*
		 * pdisease = [RRdisprob(1)+(1-prob(1))]rrprob =
		 * rrprob(1+(RRdis-1)Prob(1))
		 */
		// current is the disease number of the current independent risk factor
		// (index in the entire array of diseases)
		// RRindep1/2 are the average RR's due to having the current disease on
		// dependent disease 1 and 2 respectively;
		double prevCurrent = RR[current] * prevOdds0[current]
				/ (1 + RR[current] * prevOdds0[current]);
		double RRindep1 = (1 + prevCurrent * (RRdis[index[level]][d1] - 1));
		double RRindep2 = (1 + prevCurrent * (RRdis[index[level]][d2] - 1));

		if (level == 0)
			prob = RRindep1 * RRindep2;
		else
			prob = RRindep1
					* RRindep2
					* calculateRRindep(index, level - 1, d1, d2, RRdis, RR,
							prevOdds0, DStart);
		return prob;
	}

	/**
	 * This method adds the terms related to the attributable mortality to the
	 * matrix for a particular cluster
	 * 
	 * 
	 * @param baseMat
	 *            : matrix, over all diseases in the model, not only those in the cluster
	 * @param weight
	 *            : weight of the simulated datapoint
	 * @param S
	 *            : disease cluster structure
	 * @param otherMort
	 *            (boolean) is other mortality dependend on riskfactors
	 *            
	 * @param prevalence: the array with the overall prevalence : NB this is an array for all
	 * diseases, not for only those in the cluster!!!           
	 * @return
	 */
	public double[][] addBlock(double[][] baseMat, double weight,
			DiseaseClusterStructure S, boolean otherMort,float [] prevalence) {
		int nDis = S.getNInCluster();
		int dStart = S.getDiseaseNumber()[0]; /* number of first disease */

		for (int d1 = 0; d1 < nDis; d1++)
			for (int d2 = 0; d2 < nDis; d2++) {
				// calculate matrix V.
				// a row for disease d1 = [
				// average(probdisease (d1 ^ d2|i))-
				// {average(probdisease(d1|i)*probdisease(d2|i))/p(d1)}]
				// which is also
				// d1 = [
				// {average(probdisease (d1 ^d2|i))}/p(d1)-
				// {average(probdisease(d1|i)*probdisease(d2|i))/p(d1)}]

				// forget /p(d1) for the moment, than this can be calculated by
				// summing
				// probdisease (d1 & d2|i)-
				// (probdisease(d1|i)*probdisease(d2|i))
				// weighted for the probability of the riskfactor combination
				if (prevalence[d1+dStart] != 0 && prevalence[d2+dStart] != 0){
				
				if (otherMort)
					baseMat[d1 + dStart][d2 + dStart] += weight/prevalence[d1]
							* (this.getProb()[d1][d2] - this.getProb()[d1][d1]
									* this.getProb()[d2][d2]);

				else
					
					
					baseMat[d1 + dStart][d2 + dStart] += weight
					* (this.getProb()[d1][d2]/ prevalence[d1+dStart]- (this.getProb()[d2][d2]-
							 this.getProb()[d1][d2])/(1-prevalence[d2+dStart]));
				// TODO nog een keer checken of
																// dit klopt
				;
				}
				else{
				/*
				 * if prevalence=0 then attributable mortality is not
				 * estimable. In this case we make the attributable mortality 
				 * equal to the excess mortality corrected for other mortality (including fatal diseases)
				 * due to excess risk factors in those with this disease (attributable mortality becomes lefthand term 
				 * for this disease )
				 */
				if (d1 == d2)
					baseMat[d1 + dStart][d2 + dStart] = 1;
				else
					baseMat[d1 + dStart][d2 + dStart] = 0;}
			
			}
		return baseMat;
	}

	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setProb(double prob[][]) {
		this.prob = prob;
	}

	public double[][] getProb() {
		return prob;
	}

}
