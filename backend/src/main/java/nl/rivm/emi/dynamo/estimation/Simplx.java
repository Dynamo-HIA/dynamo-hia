package nl.rivm.emi.dynamo.estimation;

import nl.rivm.emi.cdm.exceptions.DynamoConfigurationException;
//* Simple algorithm for numerical recipies in C  //


public class Simplx {
	
	//output parameters a, icase, izrov, and iposv are defined as states;
	int icase;
	int kp=0; //these are indexes that are passed to and from methods
	int ip=0;
	int iposv[];
	int izrov[];
	// On output, the tableau a is indexed by two returned arrays of integers. iposv[j]
	//contains, for j= 1. . .M, the number i whose original variable xi is now represented
	//by row j+1 of a. These are thus the left-hand variables in the solution. (The first row
	//of a is of course the z-row.)
	// A value i > N indicates that the variable is a yi rather
	// than an xi, xN+j = yj .
	//Likewise, izrov[j] contains, for j= 1. . .N, the number i
	//whose original variable xi is now a right-hand variable, represented by column j+1
	//of a. These variables are all zero in the solution. The meaning ofi > N is the same
	//as above, except that i >N +m1 +m2 denotes an artificial or slack variable which
	//was used only internally and should now be entirely ignored.
	double[][] a;
// er zit een goto in die anders moet worden georganiseerd. */
	
	
	public Simplx(double[][]ainput,int m, int n, int m1, int m2, int m3) throws DynamoConfigurationException {

		// Here EPS is the absolute precision, which should be adjusted to the
		// scale of your variables.

		// define is C-preprocessor taal, en definieert wat er gebeurt als
		// verderop FREEALL wordt gezegd
		// voor java is dit opruimen niet van belang

		// #define FREEALL free_ivector(l3,1,m);free_ivector(l2,1,m);\
		// double free_ivector [l1][1][n+1];

		// Simplex method for linear programming. Input parameters a, m (aantal constraints, n (aantal vars), mp,
		// np, m1 (kleiner dan constraints),
		// m2 (groter dan constraints), and m3 (gelijk constraints),
		// and output parameters a, icase, izrov, and iposv are described above.

		/*
		 * this are function declarations that are not needed in Java
		 * 
		 * void simp1(float **a, int mm, int ll[], int nll, int iabf, int *kp,
		 * float *bmax); void simp2(float **a, int n, int l2[], int nl2, int
		 * *ip, int kp, float *q1); void simp3(float **a, int i1, int k1, int
		 * ip, int kp);
		 */
		
		
		double EPS = 1.0e-12;
		int i = 0;
		int ir = 0;
		int is = 0;
		int k = 0;
		int kh = 0;
		int m12 = 0;
		int nl1 = 0;
		int nl2 = 0;
		izrov = new int [n+1];
		iposv = new int [m+1];
		// first copy input to array a starting at 1;
		/*
		 * This input occupies the M + 1 rows and N + 1 columns of a[1..m+1][1..n+1].
		 * Note, however, that reference is made internally to row M + 2 of a 
		 * (used for the auxiliary objective function, just as in 10.8.18). */
		a=new double[m+3][n+2];
		for (int ii=1;ii<=m+1;ii++) for (int jj=1;jj<=n+1;jj++)
		a[ii][jj]=ainput[ii-1][jj-1];
			
		
		boolean goto_one = false; // this is an added flag needed in stead of a goto-statement;
		double q1 = 0;
		double bmax = 0;
		if (m != (m1 + m2 + m3))
			throw new DynamoConfigurationException("Error in simplex algorithm: Bad input constraint counts in simplx. \nThis might be caused by zero prevalence of consequative riskfactor categories");
			
		int[] l1 = new int[n + 2];
		int[] l2 = new int[m+1]; //  indexes hier een hoger maken  (vanwege start bij 1 ipv 0)
		int[] l3 = new int[m+1]; // alle loops hier hebben ook <= als stop conditie ipv <

		nl1 = n; // Initially make all variables right-hand.
		for (k = 1; k <= n; k++)
			{l1[k] = k;
			izrov[k] = k; }// Initialize index lists.
		nl2 = m; // Make all artificial variables left-hand,
		for (i = 1; i <= m; i++) { // and initialize those lists.
			if (a[i + 1][1] < 0.0)
				throw new DynamoConfigurationException("Error in simplex algorithm: Bad input tableau in simplx.");
			// Constants bi must be nonnegative.
			l2[i] = i;
			iposv[i] = n + i;
		}
		for (i = 1; i <= m2; i++)
			l3[i] = 1; // Used later, but initialized here.
		ir = 0;
		// This flag setting means we are in phase two, i.e have a feasible
		// starting
		// solution. Go to
		// phase two if origin is a feasible solution.
		if (m2 + m3 == 1) {
			ir = 1;
			
			// here start of real algorithm
			
			// Flag meaning that we must start out in phase one.
			for (k = 1; k <= (n + 1); k++) { // Compute the auxiliary
												// objective funcq1=0.0;
				for (i = m1 + 1; i <= m; i++)
					q1 += a[i + 1][k];
				a[m + 2][k] = -q1;
			}
			do {
				bmax=simp1(a, m + 1, l1, nl1, 0);
				
				// Find max. coefficients of auxiliary objective fnt.
				if (bmax <= EPS && a[m + 2][1] < -EPS) {
					icase = -1;
					// Auxiliary objective function is still negative and can't
					// be improved, hence
					// no
					// feasible solution exists.
					return;
				} else if (bmax <= EPS && a[m + 2][1] <= EPS) {
					m12 = m1 + m2 + 1;

					// Auxiliary objective function is zero and can't be
					// improved. This signals that
					// we have a feasible starting vector. Clean out the
					// artificial variables by goto
					// one
					// and then move on to phase two.
					for (ip = m12; ip <= m; ip++) {
						if (iposv[ip] == (ip + n)) {
							bmax=simp1(a, ip, l1, nl1, 1);
							if (bmax > 0.0) {
								goto_one=true;
								break;
								/* goto one */
							}
							;
						}
					} // end for loop;
					if (!goto_one) {
					ir = 0; // Set flag indicating we have reached
					--m12; // phase two.
					for (i = m1 + 1; i <= m12; i++)
						if (l3[i - m1] == 1)
							for (k = 1; k <= n + 1; k++)
								a[i + 1][k] = -a[i + 1][k];
					break; // Go to phase two.
				}}

				if (!goto_one) {
				q1=simp2(a, n, l2, nl2,  kp,q1); // Locate a pivot element
													// (phase one).
						if (ip == 0) { // Maximum of auxiliary objective function
				// is unbounded, so no feasible
					// solution exists.
					icase = -1;
					return;
				}}
				goto_one=false; // we reached the point of the goto statement;
				a=simp3(a, m + 1, n, ip, kp);
				// Exchange a left- and a right-hand variable (phase one), then
				// update
				// lists.
				if (iposv[ip] >= (n + m1 + m2 + 1)) {
					for (k = 1; k <= nl1; k++)
						if (l1[k] == kp)
							break;
					--nl1;
					for (is = k; is <= nl1; is++)
						l1[is] = l1[is + 1];
					++a[m + 2][kp + 1];
					for (i = 1; i <= m + 2; i++)
						a[i][kp + 1] = -a[i][kp + 1];
				} else {
					if (iposv[ip] >= (n + m1 + 1)) {
						kh = iposv[ip] - m1 - n;
						if (l3[kh] == 1) {
							l3[kh] = 0;
							++a[m + 2][kp + 1];
							for (i = 1; i <= m + 2; i++)
								a[i][kp + 1] = -a[i][kp + 1];
						}
					}
				}
				is = izrov[kp];
				izrov[kp] = iposv[ip];
				iposv[ip] = is;
			} while (ir == 1); // If still in phase one, go back to the
		} // do.
		// End of phase one code for _nding an initial feasible solution. Now,
		// in phase
		// two, optimize
		// it.
		for (;;) {
			bmax=simp1(a, 0, l1, nl1, 0);
			// Test the z-row for doneness.
			if (bmax <= 0.0) { // Done. Solution found. Return with
				icase = 0; 
				// the good news.
				return;
			}
			q1=simp2(a, n, l2, kp, nl2, q1); // Locate a pivot element (phase
												// two).
			
			if (ip == 0) { // Objective function is unbounded. Re*
				icase = 1; // port and return.
				return;
			}
			a=simp3(a, m, n, ip, kp); // Exchange a left- and a right-hand
			is = izrov[kp]; // variable (phase two),
			izrov[kp] = iposv[ip];
			iposv[ip] = is;
		} // and return for another iteration.
	}

	// The preceding routine makes use of the following utility functions.
	// #include <math.h>;
	double simp1(double[][] a, int mm, int ll[], int nll, int iabf)
	// Determines the maximum of those elements whose index is contained in the
	// supplied list ll,
	// either with or without taking the absolute value, as flagged by iabf.
	
	//return variable= bmax en kp, last one is made a field of the object in
	// order to do so
	{
		
		
		int k;
		double test;
		double bmax;
		this.kp = ll[1]; /* kijken of dit goed gaat met pointers */
		bmax = a[mm + 1][this.kp + 1];
		for (k = 2; k <= nll; k++) {
			if (iabf == 0)
				test = a[mm + 1][ll[k] + 1] - (bmax);
			else
				test = Math.abs(a[mm + 1][ll[k] + 1]) - Math.abs(bmax);
			if (test > 0.0) {
				bmax = a[mm + 1][ll[k] + 1];
				this.kp = ll[k];
			}
		} return bmax;
	}

	double simp2(double[][] a, int n, int l2[],int kp, int nl2,  double q1)
	// Locate a pivot element, taking degeneracy into account.
	
	// return variables are ip and q1
	// ip is field of object in order to do so;
	{
		double EPS = 1.0e-6;
		
		int k;
		int ii;
		int i;
		double qp = 0;
		double q0 = 0;
		double q;
		this.ip=0;
		for (i = 1; i <= nl2; i++)
			if (a[l2[i] + 1][kp + 1] < -EPS)
				break; // Any possible pivots?
		if (i > nl2)
			return q1;
		q1 = -a[l2[i] + 1][1] / a[l2[i] + 1][kp + 1];
		this.ip = l2[i];
		for (i = i + 1; i <= nl2; i++) {
			ii = l2[i];
			if (a[ii + 1][kp + 1] < -EPS) {
				q = -a[ii + 1][1] / a[ii + 1][kp + 1];
				if (q < q1) {
					this.ip = ii;
					q1 = q;
				} else if (q == q1) { // We have a degeneracy.
					for (k = 1; k <= n; k++) {
						qp = -a[this.ip + 1][k + 1] / a[this.ip + 1][kp + 1];
						q0 = -a[ii + 1][k + 1] / a[ii + 1][kp + 1];
						if (q0 != qp)
							break;
					}
					if (q0 < qp)
						this.ip = ii;
				}
			}
		} return q1 ;
	}

	double [][] simp3(double[][] a, int i1, int k1, int ip, int kp)
	// Matrix operations to exchange a left-hand and right-hand variable (see
	// text).
	{
		int kk, ii;
		double piv;
		piv = 1.0f / a[ip + 1][kp + 1];
		for (ii = 1; ii <= i1 + 1; ii++)
			if (ii - 1 != ip) {
				a[ii][kp + 1] *= piv;
				for (kk = 1; kk <= k1 + 1; kk++)
					if (kk - 1 != kp)
						a[ii][kk] -= a[ip + 1][kk] * a[ii][kp + 1];
			}
		for (kk = 1; kk <= k1 + 1; kk++)
			if (kk - 1 != kp)
				a[ip + 1][kk] *= -piv;
		a[ip + 1][kp + 1] = piv;
		return a;
	}

	
	public static void main(String[] args) throws DynamoConfigurationException

	{
	//test: this should give the solution: eerste var: 15, tweede 22.5. maximum = -45
	//	double[][] augmented = { { 2.5, 5 ,1 ,0,150 }, {5,2,0,1,120 },{-1.5,-1,0,0,0 } };
	//	double [][]augmented = { {-1.5,-1,0,0,0 },{ 2.5, 5 ,1 ,0, 150 }, {5,2,0,1,120 } }; 
	//	double [][]augmented = { {0,-1.5,-1,0,0 },{ 150,2.5, 5 ,1 ,0 }, {120,5,2,0,1 } }; 
	
		// voorbeeld uit NR
		
		
		/*float [][]augmented = {{0,1,1,3,-0.5f,0,0,0},
				                {740,-1,0,-2,0,-1,0,0},
				                {0,0,-2,0,7,0,-1,0},
				                {0.5f,0,-1,1,-2,0,0,1},
				                {9,-1,-1,-1,-1,0,0,0}
		};
		// outcome should be:
		// z 17.03 -0.95 -0.05 -1.05 ...
		// x2 3.33 -0.35 -0.15 0.35 ...
		// x3 4.73 -0.55 0.5 -0.45 ...
		// x4 0.95 -.10 0.10 0.10 ...
		// y1 730.55 0.10 -0.10 0.90 ...
		Simplx result= new Simplx(augmented,4,4,2,1,1);
		/* m,n,m1,m2,m3 */
		double [][]augmented = {{0,2,-4},{2,-6,1},{8,3,-4}};
		// outcome should be x1=0 x2=3.33 x4=4.73 en x4=0.95 */
		
		Simplx result= new Simplx(augmented,2,2,0,0,2);
		System.out
		.println(result.a[1][1]+" "+ result.a[1][2]+" "+result.a[1][3]+" "+result.a[2][1]+" "+
				result.a[2][2]+" "+result.a[2][3]+" "+
				result.a[3][1]+" "+result.a[3][2]+" "+result.a[3][3]);
		
		// iposv geeft de nummers van variabelen die niet nul zijn in de oplossing
		// bij gehorende antwoord staat in a[j+1][1] (+1 vanwege z in eerste rij)
// izrow geeft variabelen die 0 zijn geworden //
	//	De nummering is daarbij: eerst variabelen (boven aan de tabel) , daarna constraints (rijen)
		
		System.out
		.println(" var zrow " + result.izrov[1] + " var zrow " + result.izrov[2] 
			+	"var posv " + result.iposv[1]+"var posv " + result.iposv[2] );
		
		/* test transitiekansen 
		a = [0.7304662, 0.2169622, 0.05257154]
				b = [0.7189476, 0.2255193, 0.05553312] */
		double [] poud={0.7304662F, 0.2169622F, 0.05257154F} ;
		double [] pnew={0.7189476F, 0.2255193F, 0.05553312F} ;
		@SuppressWarnings("unused")
		double [] hulp = new double[3];
		
		double[][]augmented2 = {{0,        3,  2,  0,  2,  3,  2,  0,  2,  3},
		                       {poud[0], -1, -1, -1,  0,  0,  0,  0,  0,  0},
		                       {poud[1],  0,  0,  0, -1, -1, -1,  0,  0,  0},
		                       {poud[2],  0,  0,  0,  0,  0,  0, -1, -1, -1},
		                       {pnew[0], -1,  0,  0, -1,  0,  0, -1,  0,  0},
		                       {pnew[1],  0, -1,  0,  0, -1,  0,  0, -1,  0},
		                       {pnew[2],  0,  0, -1,  0,  0, -1,  0,  0, -1}};
		Simplx result2= new Simplx(augmented2,6,9,0,0,6);
		System.out
		.println(" var posv " + result2.iposv[1] + " var posv " + result2.iposv[2] 
		      +	"var posv " + result2.iposv[3]+"var posv " + result2.iposv[4] 
			+	"var posv " + result2.iposv[5]+"var posv " + result2.iposv[6] ); 
		System.out
		.println(" results  1 " + result2.a[2][1] + " 2 " + result2.a[3][1]
		      +	" 3 " + result2.a[4][1]+" 4 " + result2.a[5][1]
			+	" 5 " + result2.a[6][1]+" 6 " + result2.a[7][1] ); 	
}

	public double[][] getA() {
		return a;
	}

	public int[] getIzrov() {
		return izrov;
	}

	public int[] getIposv() {
		return iposv;
	}}
