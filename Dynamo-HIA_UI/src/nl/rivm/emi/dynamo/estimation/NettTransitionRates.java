package nl.rivm.emi.dynamo.estimation;
/**
 * @author Hendriek Boshuizen
 * 
 * NB heb zitten rommelen met static: gaat dat wel goed???
 * 
 */
public class NettTransitionRates {
	private double[][] transitionRates;
	private double[][] drift; // TODO dit in een apart methode ergens anders stoppen,

	// drift consists of index age (first index) and type of value [second
	// index]. Values are:
	// 0: drift in mean (for lognormal this is the mean on the logscale) (index
	// 2=0),
	// 1: relative increase in std (for lognormal this is again the std on the
	// logscale) [index 2=1] and
	// 2: (only for lognormal): increase in offset [index 2=2]
	// the last on the normal/regular scale

	// first constructor for continuous variables (to calculate drift)

	/**
	 * 
	 * constructor for continuous variables (to calculate drift)
	 * 
	 * @param meanRisk :
	 *            array with mean values of riskfactor per age
	 * @param stdRisk :
	 *            array of standard deviation of riskfactor per age
	 * @param skewRisk :
	 *            array of skewness of riskfactor per age
	 * @param baselineMort :
	 *            baseline all cause mortality
	 * @param RR :
	 *            relative risk for all cause mortality
	 * 
	 * For lognormal variables this is not very acurate: not even with 100000
	 * simulated values per age This should be improved by some form of
	 * calibration
	 * 
	 * @param refCat :
	 *            reference category for risk factor (value of riskfactor for
	 *            which the mortality is equal to the baseline mortality
	 * @throws Exception
	 *             in case the mean, std and skewness of lognormal distribution
	 *             can not be recalculated into a mu, sigma and offset
	 */
	public void makeNettTransitionRates(double[] meanRisk, double[] stdRisk,
			double[] skewRisk, double[] baselineMort, double[] RR, double refCat)
			throws Exception {
		int nAgeGroups = meanRisk.length;
		int nSim = 100;

		double[][] riskfactor = new double[nSim][nAgeGroups];
		double survival;
		double[] logriskfactor = new double[nSim];

		// New variables are after selective mortality
		double[] meanNew = new double[nAgeGroups];
		double[] checkMeanNew = new double[nAgeGroups]; // to calibrate away the
		// effect of the
		// simulation
		double[] stdNew = new double[nAgeGroups];
		double[] checkStdNew = new double[nAgeGroups];
		// to calibrate away the effect of the simulation
		double[] skewNew = new double[nAgeGroups];
		// for the lognormal distribution, also needed are the parameters of the
		// distribution on the logscale

		double[] checkSkewNew = new double[nAgeGroups]; // to calibrate
		double[] sigmaNew = new double[nAgeGroups];
		double[] checkSigmaNew = new double[nAgeGroups];
		double[] muNew = new double[nAgeGroups];
		double[] checkMuNew = new double[nAgeGroups];
		double[] offsetNew = new double[nAgeGroups];
		// sigma and mu are the parameters of the lognormal distribution (on the
		// log scale)
		double[] sigma = new double[nAgeGroups];
		double[] mu = new double[nAgeGroups];
		double[] offset = new double[nAgeGroups];
		boolean lognorm;
		DynamoLib instance = DynamoLib.getInstance(100);
		double[] pdfTable = instance.getCdfTable();
		drift = new double[nAgeGroups][3];
		for (int a = 0; a < nAgeGroups; a++) {
			// first calculate the distribution in survivors after one year;

			// start with simulating a distribution
			// here we use equidistant values in the range -4 to 4 sd
			// and we weight with the probability of having this value
			// this weight is preprocessed in DynamoLib.cdfTable
			// this is most accurate for calculating standard deviations
			if (skewRisk[a] == 0) {
				lognorm = false;
				for (int i = 0; i < nSim; i++) {

					
					riskfactor[i][a] = meanRisk[a] + stdRisk[a]
							* (-4.0 + (8.0 / nSim) * (i + 0.5));

				}
			} else {
				lognorm = true;

				sigma[a] = DynamoLib.findRoot(skewRisk[a]);
				mu[a] = 0.5 * (Math.log(stdRisk[a] * stdRisk[a])
						- Math.log(Math.exp(sigma[a] * sigma[a]) - 1.0) - sigma[a]
						* sigma[a]);
				offset[a] = meanRisk[a]
						- Math.exp(mu[a] + 0.5 * sigma[a] * sigma[a]);

				for (int i = 0; i < nSim; i++)

					/*
					 * riskfactor[i][a] = DynamoLib.logNormInv2( ((i + 0.5) /
					 * nSim), skewRisk[a], meanRisk[a], stdRisk[a]); old:
					 * equiprobability version
					 */

					riskfactor[i][a] = offset[a]
							+ Math.exp(mu[a] + sigma[a]
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

				survival = Math.exp(-baselineMort[a]
						* Math.pow(RR[a], (riskfactor[i][a] - refCat)));
				checkMeanSurv += Math.exp(-baselineMort[a]) * pdfTable[i];
				meanSurv += survival * pdfTable[i];
				meanNew[a] += riskfactor[i][a] * survival * pdfTable[i];
				checkMeanNew[a] += riskfactor[i][a]
						* Math.exp(-baselineMort[a]) * pdfTable[i];
				// first
				// moment
				// around
				// the
				// origin
				stdNew[a] += survival * pdfTable[i]
						* Math.pow(riskfactor[i][a], 2);// second moment around
				// the origin
				// skewness is not needed in this way of calculation

				// skewNew[a] += survival*
				// DynamoLib.cdfTable[i]*Math.pow(riskfactor[i][a],3);} //third
				// moment around the origin
				// we assume that the offset will not change due to the
				// selective mortality
				// we therefore only fit a new normal distribution on the
				// logscale

				checkStdNew[a] += Math.exp(-baselineMort[a]) * pdfTable[i]
						* Math.pow(riskfactor[i][a], 2);

				if (lognorm == true) {
					checksum += pdfTable[i];
					logriskfactor[i] = Math.log(riskfactor[i][a] - offset[a]);
					muNew[a] += survival * pdfTable[i]
							* Math.log(riskfactor[i][a] - offset[a]);
					checkMuNew[a] += Math.exp(-baselineMort[a]) * pdfTable[i]
							* Math.log(riskfactor[i][a] - offset[a]);

					checkSigmaNew[a] += Math.exp(-baselineMort[a])
							* pdfTable[i]
							* Math.pow(Math.log(riskfactor[i][a] - offset[a]),
									2);
					sigmaNew[a] += survival
							* pdfTable[i]
							* Math.pow(Math.log(riskfactor[i][a] - offset[a]),
									2);
				}

				// apply formulae to calculate central moments from moments
				// around the origin
				// note: do not change order in case you want to calculate
				// skewness,
				// as stdNew in second formula should be the central moment
				// while it is overwritten in the third by the moment around the
				// mean
			}
			stdNew[a] = Math.sqrt(stdNew[a] / meanSurv - meanNew[a]
					* meanNew[a] / meanSurv / meanSurv);
			// second moment around the mean
			checkStdNew[a] = Math.sqrt(checkStdNew[a] / checkMeanSurv
					- checkMeanNew[a] * checkMeanNew[a] / checkMeanSurv
					/ checkMeanSurv); // second moment
			// around the mean
			meanNew[a] = meanNew[a] / meanSurv; // first moment around the
			// mean
			checkMeanNew[a] = checkMeanNew[a] / checkMeanSurv;

			// skewNew[a]=skewNew[a]/meanSurv-3*meanNew[a]*stdNew[a]/meanSurv
			// +2*Math.pow(meanNew[a],3);// third moment around the mean

			if (lognorm == true) {
				muNew[a] = muNew[a] / meanSurv; // ditto for the lognormal mean
				checkMuNew[a] = checkMuNew[a] / checkMeanSurv;

				sigmaNew[a] = Math.sqrt(sigmaNew[a] / meanSurv - muNew[a]
						* muNew[a]); // second moment around the mean for the
				// lognormal
				// calibrated:
				checkSigmaNew[a] = Math.sqrt(checkSigmaNew[a] / checkMeanSurv
						- checkMuNew[a] * checkMuNew[a]); // second moment
				// around the mean
				// for the
				// lognormal
			}
			// calibrated:
			meanNew[a] = meanNew[a] * meanRisk[a] / (checkMeanNew[a]);
			// calibrated:
			stdNew[a] = stdNew[a] * stdRisk[a] / (checkStdNew[a]);

			if (lognorm == true) {
				// calibrated:
				muNew[a] = muNew[a] * mu[a] / (checkMuNew[a]);
				// calibrated:
				sigmaNew[a] = sigmaNew[a] * sigma[a] / (checkSigmaNew[a]);
			}

			// skewNew[a]=skewNew[a]/Math.pow(stdNew[a],3); //skewness
			// if (lognorm) {
			// sigmaNew[a]=DynamoLib.findRoot(skewNew[a]);
			// muNew[a]=0.5*(Math.log(stdNew[a]*stdNew[a])-Math.log(Math.exp(
			// sigmaNew[a]*sigmaNew[a])-1)-sigmaNew[a]*sigmaNew[a]);
			// offsetNew[a]=meanNew[a]-Math.exp(muNew[a]+0.5*sigmaNew[a]*
			// sigmaNew[a]);}
			offsetNew[a] = offset[a];

			if (a > 0) {
				if (!lognorm) {

					drift[a - 1][0] = meanRisk[a] - meanNew[a - 1];
					if (stdNew[a - 1] != 0) {
						drift[a - 1][1] = stdRisk[a] / stdNew[a - 1];
					} else
						drift[a - 1][1] = 1;
					drift[a - 1][2] = 0; // not defined, but make it 0 anyway
				} else {
					drift[a - 1][0] = mu[a] - muNew[a - 1];
					if (sigmaNew[a - 1] != 0) {
						drift[a - 1][1] = sigma[a] / sigmaNew[a - 1];

					}

					else {
						drift[a - 1][1] = 1;
					}

					drift[a - 1][2] = offset[a] - offsetNew[a - 1];
				}
				;

			}
		} // end loop over age
	} // end constructor

	// constructor for risk class variable (only for one age group

	/**
	 * 
	 * constructor for risk class variable (only for one age group)
	 * 
	 * @param oldPrev :
	 *            prevalence rate of age to calculate transition rate in
	 * @param newPrev :
	 *            prevalence rate of next age
	 * @param baselineMort :
	 *            baseline all cause mortality
	 * @param RR :
	 *            relative risk for all cause mortality
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
	 * @return 
	 */

	public void makeNettTransitionRates(float[] oldPrevOriginal, float[] newPrev,
			double baselineMort, float[] RR) {

		int nCat = oldPrevOriginal.length;
		// first calculate oldPrev including selective mortality;
		// first make a copy in the long way, otherwise despite being private etc. 
		// the value of riskPrev in INPUT data changes
        float[] oldPrev=new float [nCat];
        for (int i=0;i<nCat;i++){oldPrev[i]=oldPrevOriginal[i];}
        
		double survtot = 0;
		for (int i = 0; i < nCat; i++) {
			survtot += (1.0 - baselineMort * RR[i]) * oldPrev[i];
		}
		for (int i = 0; i < nCat; i++) {
			oldPrev[i] = (float) ((1.0 - baselineMort * RR[i]) * oldPrev[i] / survtot);
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

		float[][] table = new float[numEq][numVar + 1]; // linear table to solve
		// see numberical recipes 18.10

		// first make the first row of the table (function to maximize)
		table[0][0] = 0; // first column = 0;
		for (int i = 0; i < nCat; i++)
			for (int j = 0; j < nCat; j++) { // looping over all variables

				table[0][nCat * i + j + 1] = costCalc(i - j); // other columns
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
			table[ieq][0] = (float) oldPrev[ieq - 1]; // zou ook -1 kunnen
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
			table[ieq][0] = (float) newPrev[ieq - nCat - 1];

		}

		float[][] oldtable = table;
		Simplx result = new Simplx(table, 2 * nCat, numVar, 0, 0, 2 * nCat);

		// Extract transitionrates
		transitionRates = new double[nCat][nCat];
		for (int i = 0; i < nCat; i++)
			for (int j = 0; j < nCat; j++) {
				int variableNum = (i) * nCat + j + 1;
				for (int k = 1; k <= 2 * nCat; k++)
					if (result.iposv[k] == variableNum) {
						transitionRates[i][j] = (double) result.a[k + 1][1]
								/ oldPrev[i];
						// K+1 WANT RIJ 1 BEVAT KOSTEN
					}
				;
			}
		;
	}

	public NettTransitionRates() {
		// TODO Auto-generated constructor stub
	}

	public static float costCalc(int dif) {
		float cost;

		cost = 0;
		dif = Math.abs(dif);
		for (int i = 0; i <= dif; i++) {
			cost += i;
		}
		;
		cost = 100 - cost; // this is the wrong way round;
		// with only 10 categories 100 is enough to change around
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

	public double[][] getDrift() {
		return drift;
	}

	public void setDrift(double[][] drift) {
		this.drift = drift;
	}
}
