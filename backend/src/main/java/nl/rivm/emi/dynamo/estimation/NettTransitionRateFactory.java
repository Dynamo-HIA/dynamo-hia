package nl.rivm.emi.dynamo.estimation;

import java.util.Arrays;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

/**
 * @author Hendriek Boshuizen
 * 
 *         NB heb zitten rommelen met static: gaat dat wel goed???
 * 
 */
public class NettTransitionRateFactory {
	private double[][] transitionRates; /* for the last age and sex group */
	boolean lognorm;
	// drift and nettdrift have the following indexes: type of value (first
	// index) age (second index), sex (thirdindex)
	// . Types of Values are:
	// 0: drift in mean (for lognormal this is the mean on the logscale) (index
	// 2=0),
	// 1: relative increase in std (for lognormal this is again the std on the
	// logscale) [index 2=1] and
	// 2: (only for lognormal): increase in offset [index 2=2]
	// the last on the normal/regular scale
	private float[][][] nettDrift = null; /*
										 * nett drift, where the first index is
										 * 0
										 */

	private float[][][] drift; /*
								 * the drift after including the user given
								 * drift of the mean
								 */

	private double[][] sigma;
	private double[][] mu;
	private double[][] offset;

	public NettTransitionRateFactory() {
		super();
	};

	// first constructor for continuous variables (to calculate drift)

	/**
	 * 
	 * constructor for continuous variables (to calculate drift)
	 * 
	 * @param meanRisk
	 *            : array with mean values of riskfactor per age and sex
	 * @param stdRisk
	 *            : array of standard deviation of riskfactor per age
	 * @param skewRisk
	 *            : array of skewness of riskfactor per age and sex
	 * @param baselineMort
	 *            : baseline all cause mortality by age and sex
	 * @param RR
	 *            : relative risk for all cause mortality by age and sex
	 * 
	 *            For lognormal variables this is not very acurate: not even
	 *            with 100000 simulated values per age This should be improved
	 *            by some form of calibration
	 * 
	 * @param refCat
	 *            : reference category for risk factor (value of riskfactor for
	 *            which the mortality is equal to the baseline mortality
	 * @throws Exception
	 *             in case the mean, std and skewness of lognormal distribution
	 *             can not be recalculated into a mu, sigma and offset
	 */
	public float[][][] makeNettTransitionRates(float[][] meanRisk,
			float[][] stdRisk, float[][] skewRisk, float[][] baselineMort,
			float[][] RR, float refCat, float trend) {
		int nAgeGroups = meanRisk.length;
		int nSim = 100;
		// sigma and mu are the parameters of the lognormal distribution (on the
		// log scale)
		sigma = new double[nAgeGroups][2];
		mu = new double[nAgeGroups][2];
		offset = new double[nAgeGroups][2];

		DynamoLib instance = DynamoLib.getInstance(nSim);
		double[] pdfTable = instance.getCdfTable();
		nettDrift = new float[3][nAgeGroups][2];
		for (int s = 0; s < 2; s++) {
			double[][] riskfactor = new double[nSim][nAgeGroups];

			double survival;
			double[] logriskfactor = new double[nSim];

			// New variables are after selective mortality
			double[] meanNew = new double[nAgeGroups];
			double[] checkMeanNew = new double[nAgeGroups]; // to calibrate away
			// the
			// effect of the
			// simulation
			double[] stdNew = new double[nAgeGroups];
			double[] checkStdNew = new double[nAgeGroups];
			// to calibrate away the effect of the simulation
			double[] skewNew = new double[nAgeGroups];
			// for the lognormal distribution, also needed are the parameters of
			// the
			// distribution on the logscale

			double[] checkSkewNew = new double[nAgeGroups]; // to calibrate
			double[] sigmaNew = new double[nAgeGroups];
			double[] checkSigmaNew = new double[nAgeGroups];
			double[] muNew = new double[nAgeGroups];
			double[] checkMuNew = new double[nAgeGroups];
			double[] offsetNew = new double[nAgeGroups];

			for (int a = 0; a < nAgeGroups; a++) {
				// first calculate the distribution in survivors after one year;

				// start with simulating a distribution
				// here we use equidistant values in the range -4 to 4 sd
				// and we weight with the probability of having this value
				// this weight is preprocessed in DynamoLib.cdfTable
				// this is most accurate for calculating standard deviations
				if (a == 20) {

					int stop = 0;
					stop++;

				}
				if (skewRisk[a][s] == 0) {
					lognorm = false;
					for (int i = 0; i < nSim; i++) {

						riskfactor[i][a] = meanRisk[a][s] + stdRisk[a][s]
								* (-4.0 + (8.0 / nSim) * (i + 0.5));

					}
				} else {
					lognorm = true;

					sigma[a][s] = DynamoLib.findSigma(skewRisk[a][s]);
					mu[a][s] = 0.5 * (Math.log(stdRisk[a][s] * stdRisk[a][s])
							- Math
									.log(Math.exp(sigma[a][s] * sigma[a][s]) - 1.0) - sigma[a][s]
							* sigma[a][s]);
					offset[a][s] = meanRisk[a][s]
							- Math.exp(mu[a][s] + 0.5 * sigma[a][s]
									* sigma[a][s]);

					for (int i = 0; i < nSim; i++)

						/*
						 * riskfactor[i][a] = DynamoLib.logNormInv2( ((i + 0.5)
						 * / nSim), skewRisk[a], meanRisk[a], stdRisk[a]); old:
						 * equiprobability version
						 */

						riskfactor[i][a] = offset[a][s]
								+ Math.exp(mu[a][s] + sigma[a][s]
										* (-4.0 + (8.0 / nSim) * (i + 0.5)));

				}
				;
				// Now calculate the survival in each i and a combination;
				meanNew[a] = 0;
				stdNew[a] = 0;
				skewNew[a] = 0;
				muNew[a] = 0;
				sigmaNew[a] = 0;
				// the check variables do the calculation for RR=1
				checkMeanNew[a] = 0;
				checkStdNew[a] = 0;
				checkSkewNew[a] = 0;
				checkMuNew[a] = 0;
				checkSigmaNew[a] = 0;
				double checksum = 0;
				double meanSurv = 0;
				double checkMeanSurv = 0;
				for (int i = 0; i < nSim; i++) {

					survival = Math.exp(-baselineMort[a][s]
							* Math.pow(RR[a][s], (riskfactor[i][a] - refCat)));
					checkMeanSurv += Math.exp(-baselineMort[a][s])
							* pdfTable[i];
					meanSurv += survival * pdfTable[i];
					meanNew[a] += riskfactor[i][a] * survival * pdfTable[i];
					checkMeanNew[a] += riskfactor[i][a]
							* Math.exp(-baselineMort[a][s]) * pdfTable[i];
					// first
					// moment
					// around
					// the
					// origin
					stdNew[a] += survival * pdfTable[i]
							* Math.pow(riskfactor[i][a], 2);// second moment
					// around
					// the origin
					// skewness is not needed in this way of calculation

					// skewNew[a] += survival*
					// DynamoLib.cdfTable[i]*Math.pow(riskfactor[i][a],3);}
					// //third
					// moment around the origin
					// we assume that the offset will not change due to the
					// selective mortality
					// we therefore only fit a new normal distribution on the
					// logscale

					checkStdNew[a] += Math.exp(-baselineMort[a][s])
							* pdfTable[i] * Math.pow(riskfactor[i][a], 2);

					if (lognorm == true) {
						checksum += pdfTable[i];
						logriskfactor[i] = Math.log(riskfactor[i][a]
								- offset[a][s]);
						muNew[a] += survival * pdfTable[i]
								* Math.log(riskfactor[i][a] - offset[a][s]);
						checkMuNew[a] += Math.exp(-baselineMort[a][s])
								* pdfTable[i]
								* Math.log(riskfactor[i][a] - offset[a][s]);

						checkSigmaNew[a] += Math.exp(-baselineMort[a][s])
								* pdfTable[i]
								* Math.pow(Math.log(riskfactor[i][a]
										- offset[a][s]), 2);
						sigmaNew[a] += survival
								* pdfTable[i]
								* Math.pow(Math.log(riskfactor[i][a]
										- offset[a][s]), 2);
					}

					// apply formulae to calculate central moments from moments
					// around the origin
					// note: do not change order in case you want to calculate
					// skewness,
					// as stdNew in second formula should be the central moment
					// while it is overwritten in the third by the moment around
					// the
					// mean
				}
				
				/*
				 * with zero stdnew numerical errors sometimes make the
				 * difference slightly larger than 0
				 */
				if (stdNew[a] / meanSurv > meanNew[a] * meanNew[a] / meanSurv
						/ meanSurv)
					stdNew[a] = Math.sqrt(stdNew[a] / meanSurv - meanNew[a]
							* meanNew[a] / meanSurv / meanSurv);
				else
					stdNew[a] = 0;
				// second moment around the mean
				if (checkStdNew[a] / checkMeanSurv > checkMeanNew[a]
						* checkMeanNew[a] / checkMeanSurv / checkMeanSurv)

					checkStdNew[a] = Math.sqrt(checkStdNew[a] / checkMeanSurv
							- checkMeanNew[a] * checkMeanNew[a] / checkMeanSurv
							/ checkMeanSurv);
				else
					checkStdNew[a] = 0;// second moment
				// around the mean
				meanNew[a] = meanNew[a] / meanSurv; // first moment around the
				// mean
				checkMeanNew[a] = checkMeanNew[a] / checkMeanSurv;

				// skewNew[a]=skewNew[a]/meanSurv-3*meanNew[a]*stdNew[a]/meanSurv
				// +2*Math.pow(meanNew[a],3);// third moment around the mean

				if (lognorm == true) {
					muNew[a] = muNew[a] / meanSurv; // ditto for the lognormal
					// mean
					checkMuNew[a] = checkMuNew[a] / checkMeanSurv;

					if (sigmaNew[a] / meanSurv > muNew[a] * muNew[a])
						sigmaNew[a] = Math.sqrt(sigmaNew[a] / meanSurv
								- muNew[a] * muNew[a]);

					else
						sigmaNew[a] = 0;
					// second moment around the mean for
					// the
					// lognormal
					// calibrated:
					if (checkSigmaNew[a] / checkMeanSurv > checkMuNew[a]
							* checkMuNew[a])

						checkSigmaNew[a] = Math
								.sqrt(checkSigmaNew[a] / checkMeanSurv
										- checkMuNew[a] * checkMuNew[a]);
					else
						checkSigmaNew[a] = 0;// second
					// moment
					// around the mean
					// for the
					// lognormal
				}
				// calibrated:
				if (checkMeanNew[a] != 0)
					meanNew[a] = meanNew[a] * meanRisk[a][s]
							/ (checkMeanNew[a]);
				/* if checkMeanNew[a]== 0 do not calibrate */
				// calibrated:
				if (checkStdNew[a] != 0)
					stdNew[a] = stdNew[a] * stdRisk[a][s] / (checkStdNew[a]);
				/* if checkSTDNew[a]== 0 do not calibrate */
				if (lognorm == true) {
					// calibrated:
					if (checkMuNew[a] != 0)
						muNew[a] = muNew[a] * mu[a][s] / (checkMuNew[a]);
					// calibrated:
					if (checkSigmaNew[a] != 0)
						sigmaNew[a] = sigmaNew[a] * sigma[a][s]
								/ (checkSigmaNew[a]);
				}

				// skewNew[a]=skewNew[a]/Math.pow(stdNew[a],3); //skewness
				// if (lognorm) {
				// sigmaNew[a]=DynamoLib.findRoot(skewNew[a]);
				// muNew[a]=0.5*(Math.log(stdNew[a]*stdNew[a])-Math.log(Math.exp(
				// sigmaNew[a]*sigmaNew[a])-1)-sigmaNew[a]*sigmaNew[a]);
				// offsetNew[a]=meanNew[a]-Math.exp(muNew[a]+0.5*sigmaNew[a]*
				// sigmaNew[a]);}
				offsetNew[a] = offset[a][s];

				/*
				 * fill the return variable, and add the extra drift: for normal
				 * variables to the (mean)drift, for lognormal variables to the
				 * offset
				 */
				/*
				 * NB: the "New" variables are the old variables adjusted for
				 * mortality, the variable names without new are the new targets
				 */
				if (a == 21) {

					int stop = 0;
					stop++;

				}
				if (a > 0) {
					if (!lognorm) {

						nettDrift[0][a - 1][s] = (float) (meanRisk[a][s]
								- meanNew[a - 1] + trend);
						if ((double)stdRisk[a][s] > stdNew[a - 1]) {
							nettDrift[1][a - 1][s] = (float) Math
									.sqrt((((double)stdRisk[a][s] * (double)stdRisk[a][s]))
											- stdNew[a - 1] * stdNew[a - 1]);
							// old:
							// divided
							// by
							// stdNew[a
							// - 1];
						} else
							nettDrift[1][a - 1][s] = 0;
						nettDrift[2][a - 1][s] = 0; // not defined, but make it
						// 0 anyway
					} else {
						nettDrift[0][a - 1][s] = (float) (mu[a][s] - muNew[a - 1]);
						if (sigma[a][s] > sigmaNew[a - 1]) {
							nettDrift[1][a - 1][s] = (float) Math
									.sqrt(sigma[a][s] * sigma[a][s]
											- sigmaNew[a - 1] * sigmaNew[a - 1]); // old:
							// divided
							// by
							// sigmaNew[a
							// -
							// 1];

						}

						else {
							nettDrift[1][a - 1][s] = 0;
						}

						nettDrift[2][a - 1][s] = (float) (offset[a][s]
								- offsetNew[a - 1] + trend);
					}
					;

				}
			} // end loop over age
		} // end loop over gender
		return nettDrift;
	} // end method

	/**
	 * this method calculates the offsetdrift, sigmadrift and mu-drift for a
	 * lognormal distribution needed to simulate a user-given mean drift. It can
	 * only be used after
	 * 
	 * @param drift
	 *            : (float [][]): user given drift for age and sex
	 * @throws Exception
	 */

	public float[][][] makeUserGivenTransitionRates(float[][] meanRisk,
			float[][] stdRisk, float[][] skewRisk, float[][] baselineMort,
			float[][] RR, float refCat, float[][] userDrift)

	{

		drift = makeNettTransitionRates(meanRisk, stdRisk, skewRisk,
				baselineMort, RR, refCat, 0);
		if (lognorm) {
			for (int s = 0; s < 2; s++)
				for (int a = 0; a < meanRisk.length - 1; a++) {

					/*
					 * choice made: the offset is unaffected, the extra drift is
					 * only influencing mu
					 * 
					 * 
					 * 
					 * average of lognormal= exp(mu+0.5sigma)+offset
					 * 
					 * userdrift[a] = newmean(a+1)-oldmean(a)= exp(mu(a+1)
					 * +0.5sigma(a+1))- exp(mu(a) +0.5 sigma(a))
					 * +offset(a+1)-offset(a)
					 * 
					 * thus exp(mu(a+1)) [userdrift+exp{mu(a)+0.5
					 * sigma(a)}-offset(a+1)+offset(a)]exp(-sigma(a+1)/2)=
					 */
					double expMuNew = (userDrift[a][s]
							+ Math.exp(mu[a][s] + sigma[a][s] / 2)
							- offset[a + 1][s] + offset[a][s])
							* Math.exp(-sigma[a + 1][s] / 2);
					drift[0][a][s] = (float) (Math.log(expMuNew) - mu[a][s]);

				}

		} else {
			for (int s = 0; s < 2; s++)
				for (int a = 0; a < meanRisk.length; a++) {
					drift[0][a][s] = userDrift[a][s];

				}
		}

		return drift;
	}

	/**
	 * 
	 * constructor for risk class variable (only for one age group)
	 * 
	 * @param oldPrev
	 *            : prevalence rate of age to calculate transition rate in
	 * @param newPrev
	 *            : prevalence rate of next age
	 * @param baselineMort
	 *            : baseline all cause mortality
	 * @param RR
	 *            : relative risk for all cause mortality
	 * 
	 * 
	 */

	/***************************************************************************
	 * Allereest de kosten. Deze schrijven we als J = Sum(A*x). A is de
	 * kostenmatrix om van een klasse naar een andere te gaan. x zijn de (netto)
	 * transities, ook in matrixvorm. De totale kosten moeten minimaal zijn.
	 * 
	 * A is een symmetrische Toeplitz matrix is (zie
	 * http://en.wikipedia.org/wiki/Toeplitz_matrix). In het geval we drie
	 * klassen hebben vullen we in:
	 * 
	 * [0 1 3] A = [1 0 1] [3 0 1]
	 * 
	 * waarmee we zeggen dat er geen kosten zijn verbonden als je in een
	 * bepaalde klasse blijft, het 1 punt kost als je een klasse opschuift en
	 * het 3 punten kost als je 2 klassen opschuift.
	 * 
	 * Verder hebben dan nodig de prevalenties "voor" en de prevalenties "na".
	 * Laten we die even a en b noemen. De constrains die we hebben zijn dan:
	 * 
	 * rowSums(x) = a colSums(x) = b
	 * 
	 * wat betekent dat alles wat uit een klasse weggaat gelijk moet zijn aan de
	 * prevalentie "voor" van die klasse en alles wat een klasse inkomt gelijk
	 * moet zijn aan de prevalentie "na" van die klasse. Verder hebben we nog
	 * een constraint dat Sum(a) = Sum(b). Dat betekent ook dat alle x >= 0.
	 * 
	 * Tijd voor een getallen voorbeeld.
	 * 
	 * a = [0.7304662, 0.2169622, 0.05257154] b = [0.7189476, 0.2255193,
	 * 0.05553312]
	 * 
	 * Transities worden dan:
	 * 
	 * [0.44941386 0.2255193 0.05553304] T = [0.21696220 0.0000000 0.00000000]
	 * [0.05257154 0.0000000 0.00000000]
	 * 
	 * En de kansen zijn dan gelijk aan P = (T/a)'
	 * 
	 * [0.6152425 1 1] P = [0.3087334 0 0] [0.0760241 0 0]
	 * 
	 * Contole: Pa = b
	 * 
	 * HB: deze kosten matrix maximaliseert de overgangen. In ons geval moet
	 * juist zijn [3 2 0] A = [2 3 2] [0 2 3]
	 * 
	 * stap 2 + stap 0 < 2* stap 1 Dus in meer dimensies: stap 3+ stap 0 < stap
	 * 2+stap 1 < 3* stap 1
	 * 
	 * 
	 * 
	 * byv 6 5 3 0 (verschil steeds 1 extra) Voor 9: 36 35 33 30 26 21 15 8 0
	 * 
	 * Abstract: als ncat aantal categorien, dan bij 1 verschil = 1 verschil 2
	 * verschil = 1+2=3 verschil 3 verschil = 1+2+3=6 verschil .. ncat-1
	 * verschil = 1+2+..(ncat-1) verschil
	 * 
	 * 
	 * 
	 * 3 2 1 0 : alles even goed.
	 * 
	 * Altijd: kosten van uiterste: < losse stappen om er tekomen
	 * 
	 * @return
	 * @throws DynamoInconsistentDataException
	 * @throws DynamoConfigurationException 
	 */

	public static float[][] makeNettTransitionRates(float[] oldPrevOriginal,
			float[] newPrevOriginal, double baselineMort, float[] RR)
			 throws DynamoInconsistentDataException, DynamoConfigurationException {

		int nCat = oldPrevOriginal.length;
		// first calculate oldPrev including selective mortality;
		// first make a copy in the long way, otherwise despite being private
		// etc.
		// the value of riskPrev in INPUT data changes
		double[] oldPrev = checkedRates (oldPrevOriginal);
		double[] newPrev = checkedRates (newPrevOriginal);
		boolean same=true;
		for (int i = 0; i < nCat; i++) {
			if (oldPrev[i] !=newPrev[i]) same=false;
		}
        if (baselineMort==0 && same)
        {  float [][] oneMatrix= new float [nCat][nCat];
        for (int i = 0; i < nCat; i++) 
        	oneMatrix[i][i]=1;
        	return oneMatrix;
        }
        	
        	
        	
        	
		double survtot = 0;
		for (int i = 0; i < nCat; i++) {
			survtot += Math.exp(-baselineMort * RR[i]) * oldPrev[i];
		}
		for (int i = 0; i < nCat; i++) {
			oldPrev[i] = Math.exp(-baselineMort * RR[i]) * oldPrev[i] / survtot;
		}

		int numEq = 2 * nCat + 1; // numEq=number of equations
		// number of equations = number of restrictions (one for all column sums
		// + one for each row sum, thus 2 times number of categories and then +
		// 1 ( the
		// equation to maximize)
		int numVar = nCat * nCat; // numVar = number of
		// transitions that need to be
		// estimated
		// the transition "staying in the same category" is also estimated as
		// all transitions from
		// a category should sum to 1

		double[][] table = new double[numEq][numVar + 1]; // linear table to
		// solve
		// see numberical recipes 18.10

		// first make the first row of the table (function to maximize)
		table[0][0] = 0; // first column = 0;
		for (int i = 0; i < nCat; i++)
			for (int j = 0; j < nCat; j++) { // looping over all variables

				table[0][nCat * i + j + 1] = costCalc(i - j, nCat); // other columns
				// are
				// costs;

			}
		;

		// now the first set of rows with column sums

		for (int ieq = 1; ieq < nCat + 1; ieq++) {

			for (int ifrom = 0; ifrom < nCat; ifrom++)
				for (int jto = 0; jto < nCat; jto++) { // looping over all
					// variables

					if (ifrom == ieq - 1)
						table[ieq][ifrom * nCat + jto + 1] = -1;// was eerst -

				}
			;
			table[ieq][0] = oldPrev[ieq - 1]; // zou ook -1 kunnen
			// zijn

		}
		// now the second set with row sums

		// now the first set of rows with column sums

		for (int ieq = nCat + 1; ieq < numEq; ieq++) {

			for (int ifrom = 0; ifrom < nCat; ifrom++)
				for (int jto = 0; jto < nCat; jto++) { // looping over all
					// variables

					if (jto == ieq - nCat - 1)
						table[ieq][ifrom * nCat + jto + 1] = -1;// 

				}
			;
			table[ieq][0] = newPrev[ieq - nCat - 1];

		}

		double[][] oldtable = table;
		
		Simplx result = new Simplx(table, 2 * nCat, numVar, 0, 0, 2 * nCat);

		// Extract transitionrates
		float[][] transitionRates = new float[nCat][nCat];
		for (int i = 0; i < nCat; i++)
			for (int j = 0; j < nCat; j++) {
				int variableNum = (i) * nCat + j + 1;
				for (int k = 1; k <= 2 * nCat; k++)
					if (result.iposv[k] == variableNum) {
						transitionRates[i][j] = (float) (result.a[k + 1][1] / oldPrev[i]);

						// K+1 WANT RIJ 1 BEVAT KOSTEN
					}
				/*
				 * in case the old prevalence is zero, there are no data to
				 * calculate nett transitions from Therefore in this case we use
				 * zero transitions for this category
				 */

				if (oldPrev[i] == 0 && i != j)
					transitionRates[i][j] = 0;
				if (oldPrev[i] == 0 && i == j)
					transitionRates[i][j] = 1;

			}
		/* in case of very small oldprev values, the accuracy can be too small
		 * yielding transition rates that do not sum to 100% */
		/* therefore these are adjusted, and the missing is added (or substracted) to
		 * the transitions nearest to the diagonal
		 * 
		 */
		for (int i = 0; i < nCat; i++)
			if (oldPrev[i] < 0.05) {
				double sum = 0;
				for (int j = 0; j < nCat; j++) {
					sum += transitionRates[i][j];
				}
				if (Math.abs(sum - 1.0) > 1E-5) {
					/* add the missing fraction to the categories closed to i */
					int closestUnder = nCat;
					int closestOver = nCat;
					boolean canStay = false;
					for (int j = 0; j < nCat; j++) {
						if (newPrev[j] > 0) {
							if (j > i && (j - i) < closestOver)
								closestOver = j - i;
							if (i > j && (i - j) < closestUnder)
								closestUnder = i - j;
							if (i == j)
								canStay = true;
						}
					}

					if (canStay)
						transitionRates[i][i] += (1 - sum);
					else if (closestUnder < nCat && closestOver < nCat) {
						transitionRates[i][i - closestUnder] += (1 - sum) / 2;
						transitionRates[i][i + closestOver] += (1 - sum) / 2;
					} else if (closestUnder < nCat) {
						transitionRates[i][i - closestUnder] += (1 - sum);

					} else if (closestOver < nCat) {

						transitionRates[i][i + closestOver] += (1 - sum);
					} else
						throw new DynamoInconsistentDataException(
								"something goes wrong with estimation of nett transitionrates"
										+ " because all new prevalence rates are zero");
				}

			}

		return transitionRates;

	}

	public static float costCalc(int dif, int nCat) {
		float cost;

		cost = 0;
		dif = Math.abs(dif);
		for (int i = 0; i <= dif; i++) {
			cost += i;
		}
		;
		cost = nCat*nCat - cost; // this is the wrong way round;
		// with only 10 categories 100 is enough to change around
		//but with 36 it is not!!! We made this nCat ^2
		
		// also when there are regular patterns with zero's the equality of costs can be a problem
	    
		return cost;

	};

	// main is only for testing

	/**
	 * main conducts a number of tests.
	 * 
	 * @param args
	 */

	public static void display(double matrix[][]) {
		int i;
		int j;
		System.out.println("\n The solution is : ");
		for (i = 0; i < matrix.length; i++)
			for (j = 0; i < matrix[1].length; j++)
				System.out.println(" from " + i + " to " + j + "="
						+ matrix[i][j]);
	}

	public double[][] getTransitionRates() {
		return transitionRates;
	}

	public void setTransitionRates(double[][] transitionRates) {
		this.transitionRates = transitionRates;
	}

	public float[][][] getDrift() {
		return nettDrift;
	}

	public void setDrift(float[][][] drift) {
		this.nettDrift = drift;
	}
	
	
	public static  double[] checkedRates(float[] prevalence) throws DynamoInconsistentDataException{
		double sumP = 0;
		double[] returnp=new double[prevalence.length];
		for (int i = 0; i < prevalence.length; i++){
			returnp[i]=prevalence[i];
			sumP +=  returnp[i];
		
		}
					
			if (Math.abs(sumP - 1.0) > 1E-3)
				throw new DynamoInconsistentDataException(
						"Risk factor prevalence does not sum "
								+ "to 100% but to " + 100 * sumP + "%"
								);
			else if (Math.abs(sumP - 1.0) !=0)
				for (int i = 0; i < prevalence.length; i++) returnp[i] = returnp[i]/sumP;
						return returnp;
	}
	
	
	/** makes nett transition rates without taking mortality into account
	 * @param oldPrevOriginal
	 * @param newPrevOriginal
	 * @return
	 * @throws DynamoInconsistentDataException
	 * @throws DynamoConfigurationException 
	 */
	public static float[][] makeNettTransitionRates(float[] oldPrevOriginal,
			float[] newPrevOriginal)
			throws DynamoInconsistentDataException, DynamoConfigurationException {
		
		float[] RR= new float[oldPrevOriginal.length];
		double baselineMort=0;
		Arrays.fill(RR,1);
		return makeNettTransitionRates( oldPrevOriginal,
				 newPrevOriginal, baselineMort,  RR);
				
}}
