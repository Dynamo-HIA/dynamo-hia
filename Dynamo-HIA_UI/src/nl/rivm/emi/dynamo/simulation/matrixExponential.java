package nl.rivm.emi.dynamo.simulation;



/**
 * @author Hendriek (translation VBA to java)
 * 
 * This algorithm is taken from:
 * S. Gallivan, M. Utley, M. Jit, C. Pagel
 * A computational algorithm associated with patient progress modelling
 * CMS (2007) 4:283-299
 * \c Springer-Verlag 2006
 * 
 * it calculates (approximates) exp(A) where A is a matrix
 *
 */
public final class matrixExponential {

	double[] TwoPower = {1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384, 32768,65536, 131072,262144,

			524288,1048576};
	
	int NExpIterations=10;// Number of iterations to achieve required accuracy
	// there are NExpIterations + 1 terms in the series
	// estimate for the exponential
	
	static private matrixExponential instance = null;
            // Look up table of 2 to the power
										// MDivisor

	// which should be set external to this code

	public matrixExponential() {
		
		super();
		
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param AT:
	 *            Matrix to be exponentiated
	 * @return: Exp (AT);
	 */
	synchronized static public matrixExponential getInstance() {
		if (instance == null) {
			instance = new matrixExponential();
		}
		return instance;
	}
	public void setNumberOfIterations(int Niterations){NExpIterations=Niterations;};
	
	public double[][] exponentiateMatrix(double[][] AT) {
		// ----------------------------------------------------------------------------------------------------------------
		// External variables whose values are shared with the calling program
		// ----------------------------------------------------------------------------------------------------------------
		// AT = Matrix to be exponentiated
		int dim = AT.length;
		double[][] ExpMatrix = new double[dim][dim];// Resulting exponential
													// (**the final result**)
		

		// Maximum index of AT ( i.e. array size -1)
		// ----------------------------------------------------------------------------------------------------------------
		// Internal variables
		// ----------------------------------------------------------------------------------------------------------------
		double ATMat[][] = new double[dim][dim]; // Copy of AT used within
													// the algorithm
		// (to preserve AT)
		int MDivisor; // Minimum divisor required to reduce sum of moduli
		// of diagonal elements of ATMat to below 1/4
		double[][] Nth_Term = new double[dim][dim];// Matrix to hold n-th term
													// in the NExpIterations+1
		// terms of the series estimate of the exponetial of AT
		double[][] DumMat = new double[dim][dim];// Dummy matrix for storage
													// of interim calculations
		double ModulusDiagSum;// Sum of the moduli of diagonal elements of
								// ATMat
		int[] NNonZero = new int[dim];// NNonZero(J) is number of non-zero
										// entries
		// in column J of ATMat
		int[][] NonZeroRows = new int[dim][dim];// NonZeroRows(J,n) is row index
												// for nth non-zero
		// entry in col J of AMat (note that this is indexed
		// from 1 since it is only used when there is at least 1
		// non-zero entry in a given column).

		// A computational algorithm associated with patient progress modelling
		// 297
		// ============================================================================

		// ==========
		// Subroutine to calculate exp(At)for matrices with zero column-sums and
		// non-negative entries
		// off the diagonal
		// ----------------------------------------------------------------------------------------------------------------
		// Initialise arrays used in the program
		// Note that NNonZero and NonZeroRows are used to speed up processing by
		// not calculating entries known
		// in advance to be zero due to the structure of matrices being
		// multiplied.
		// ----------------------------------------------------------------------------------------------------------------
		for (int j1 = 0; j1 < dim; j1++) { // Initialise arrays
			NNonZero[j1] = 0;// Set ATMat matrix
			for (int j2 = 0; j2 < dim; j2++) {
				ATMat[j1][j2] = AT[j1][j2];
				if (j1 == j2) {

					ExpMatrix[j1][j2] = 1;// Set ExpMatrix to Identity matrix
					Nth_Term[j1][j2] = 1;// Set Nth_Term to Identity matrix
				} else {
					ExpMatrix[j1][j2] = 0;// Set ExpMatrix to Identity matrix
					Nth_Term[j1][j2] = 0;// Set Nth_Term to Identity matrix
				}

				if (ATMat[j1][j2] != 0) {
					NNonZero[j2] = NNonZero[j2] + 1;// Count number of non-zero
													// column entries
					NonZeroRows[j2][NNonZero[j2]] = j1; // Set pointer to
														// non-zero entry
				}
			}
		}
		// ----------------------------------------------------------------------------------------------------------------
		// Calculate the sum of the moduli of the diagonal entries of ATMat
		// ----------------------------------------------------------------------------------------------------------------
		ModulusDiagSum = 0.0;
		for (int j1 = 0; j1 < dim; j1++)
			// Note: diagonal elements are non-positive
			ModulusDiagSum = ModulusDiagSum - ATMat[j1][j1];

		// ----------------------------------------------------------------------------------------------------------------
		// Divide ATMat by 2^M if necessary so that the modulus of the diagonal
		// sum is less than 1/4
		// ----------------------------------------------------------------------------------------------------------------
		MDivisor = ((int) Math.floor(Math.log(ModulusDiagSum) / Math.log(2.0)) + 3);// Divisor
																					// to
																					// reduce
																					// diagonal
																					// sum
																					// to
																					// below
																					// 1/4.
		if (MDivisor > 0) // If MDivisor > 0 then we need to divide ATMat
			// by 2^MDivisor
			for (int j1 = 0; j1 < dim; j1++)
				for (int j2 = 0; j2 < dim; j2++)
					// Reduce modulus of diagonal sum to below 1/4
					ATMat[j1][j2] = ATMat[j1][j2] / TwoPower[MDivisor];

		// ---------------------------------------------------------------------------------------------------------------
		// Algorithm 1: core processing for estimating the exponential of
		// (possibly modified) ATMat
		// ----------------------------------------------------------------------------------------------------------------
		for (int Iterations = 1; Iterations <= NExpIterations; Iterations++) { // Add
																				// NExpIterations
																				// terms
																				// to
																				// the
																				// expansion.
			// Note that initially, ExpMatrix is set to the
			// identity matrix.
			for (int j1 = 0; j1 < dim; j1++)
				for (int j2 = 0; j2 < dim; j2++)// First step in calculating
												// N-th term of exponential

				{
					DumMat[j1][j2] = 0;// Initially each term of DumMat is zero

					for (int j3 = 0; j3 <= NNonZero[j2]; j3++)
						// J3 loop is for explicit matrix multiplication
						// NNonZero allows us to consider only the non-zero
						// elements of ATMat(,) in this multiplication
						DumMat[j1][j2] = DumMat[j1][j2]
								+ Nth_Term[j1][NonZeroRows[j2][j3]]
								* ATMat[NonZeroRows[j2][j3]][j2];
					// Note the above should be a continuous line of code
				}
			// Now DumMat=ATMat^n/(n-1)!
			for (int j1 = 0; j1 < dim; j1++)
				for (int j2 = 0; j2 < dim; j2++) // so Nth_Term(,)is DumMat/n
				// Calculate Nth_Term(,) and add to ExpMatrix(,)
				// Note: CDbl( ) is a function within Visual Basic that
				// returns a double precision variable from an integer
				// argument. A look-up table could be used instead.
				{
					Nth_Term[j1][j2] = DumMat[j1][j2] / ((double) Iterations);
					ExpMatrix[j1][j2] = ExpMatrix[j1][j2] + Nth_Term[j1][j2];
				}
		} // end iterations loop
		// ----------------------------------------------------------------------------------------------------------------
		// To save processing time, we need only store the non-zero elements of
		// ExpMatrix
		// ----------------------------------------------------------------------------------------------------------------
		for (int j2 = 0; j2 < dim; j2++) {
			NNonZero[j2] = 0;
			for (int j1 = 0; j1 < dim; j1++) {
				if (ExpMatrix[j1][j2] != 0) {
					NNonZero[j2] = NNonZero[j2] + 1; // Reset NNon Zero and
														// NonZeroRows to now
														// hold
					NonZeroRows[j2][NNonZero[j2]] = j1; // information about
														// non-zero entries of
														// ExpMatrix(,)

				}
			}
		}

		// ----------------------------------------------------------------------------------------------------------------
		// If necessary, successively square ExpMatrix MDivisor times to get
		// estimate of exp(At)
		// Note that if MDivisor <= 0 nothing happens.
		// ----------------------------------------------------------------------------------------------------------------

		// NPower; Dummy index used in squaring matricesfor
		for (int NPower = 1; NPower <= MDivisor; NPower++) { // Successively
																// square ExpMat
																// MDivisor
																// times
			for (int j1 = 0; j1 < dim; j1++)
				for (int j2 = 0; j2 < dim; j2++) {// Square ExpMat(,)

					DumMat[j1][j2] = 0; // Reset DumMat elements to be zero
					for (int j3 = 0; j3 <= NNonZero[j2]; j3++)
						// Calculate square of ExpMatrix
						// Only consider non-zero elements of ExpMatrix
						DumMat[j1][j2] = DumMat[j1][j2]
								+ ExpMatrix[j1][NonZeroRows[j2][j3]]
								* ExpMatrix[NonZeroRows[j2][j3]][j2];
					// Note the above should be a continuous line of code

				}
			for (int j1 = 0; j1 < dim; j1++)
				for (int j2 = 0; j2 < dim; j2++)
					// Set ExpMatrix(,) to ExpMatrix(,)*ExpMatrix(,)

					ExpMatrix[j1][j2] = DumMat[j1][j2];
		}// end loop NPower
		return ExpMatrix;
	}

}
