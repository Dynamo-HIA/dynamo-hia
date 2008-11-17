package nl.rivm.emi.dynamo.estimation;
/**
 * 
 */
import java.util.Random;

import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;

import Jama.Matrix;

/**
 * DynamoLib is a library of static methods needed in the DYNAMO model
 * 
 * @author Hendriek
 * 
 * 
 */

/**
 * @author Hendriek
 *
 */
public class DynamoLib {

	static private DynamoLib instance = null;
    static private int NumberInTable=100;
	/**
	 * @field cfdTable: a table with the normal cumulative density in 100 bins
	 *        between -4 sd to + 4 sd. The figures add up to 1. The first bin is
	 *        around -4+0.5* 8(=range from -4 to 4)/100, thus goes from minus
	 *        infinity to -4+8/100 The last bin goes form 4-8/100 to plus
	 *        infinity. All other bins have length 8/100
	 */
	private static double[] cdfTable ;

	private DynamoLib(int NinTable) {
		cdfTable= new double[NinTable];
		double prefCDF = 0; // previous value of CDF
		double nextCDF = 0;
		for (int i = 0; i < NinTable; i++) {
			if (i == NinTable-1){
				cdfTable[i] = 1 - prefCDF;}
			else {
				nextCDF = normp(-4.0 + (i + 1.0) * 8.0 / NinTable);
			    cdfTable[i] = nextCDF - prefCDF;
			    prefCDF = nextCDF; }
		}
	};
	
	
	
	public static void makeCDFTable(int NinTable) {
	/* check whether the right CDF table has already  been made, in that case 
	 * do not make it again	
	 */
		if (NinTable != NumberInTable){
			NumberInTable=NinTable;	
		cdfTable= new double[NinTable];
		double prefCDF = 0; // previous value of CDF
		double nextCDF = 0;
		for (int i = 0; i < NinTable; i++) {
			if (i == NinTable-1){
				cdfTable[i] = 1 - prefCDF;}
			else {
				nextCDF = normp(-4.0 + (i + 1.0) * 8.0 / NinTable);
			    cdfTable[i] = nextCDF - prefCDF;
			    prefCDF = nextCDF; }
		}}
	};

	/**
	 * DynamoLib pietje = DynamoLib.getInstance();
	 * 
	 * @return
	 */
	/* check whether the right CDF table has already  been made, in that case 
	 * do not make it again	
	 */
	synchronized static public DynamoLib getInstance(int NinTable) {
		if (instance == null ||NinTable != NumberInTable) {
			NumberInTable=NinTable;	
			instance = new DynamoLib(NinTable);
		}
		return instance;
	}

	public double[] getCdfTable() {
		return cdfTable;
	}

	/**
	 * method draw generates a random draws from an array with percentages
	 * 
	 * @param double p: array with percentages
	 * @param Random
	 *            rand: a random value generator
	 * @author Hendriek
	 */
	public static int draw(float p[], Random rand) {
		// Generates a random draws from an array with percentages
		// To do: check if sum p=1 otherwise error
		double cump = 0; // cump is cumulative p

		double d = rand.nextDouble(); // d is random value between 0 and 1
		int i;
		for (i = 0; i < p.length - 1; i++) {
			cump = +p[i];
			if (d < cump)
				break;
		}
		return i;

	}

	/**
	 * Method for Inverse Cumulative Standard Normal Distribution Function *
	 * 
	 * @param double p: (cumulative) probability
	 * 
	 * 
	 *        This function returns an approximation of the inverse cumulative
	 *        standard normal distribution function. I.e., given P, it returns
	 *        an approximation to the X satisfying P = Pr{Z <= X} where Z is a
	 *        random variable from the standard normal distribution.
	 * 
	 *        The algorithm uses a minimax approximation by rational functions
	 *        and the result has a relative error whose absolute value is less
	 *        than 1.15e-9.
	 * 
	 * @Author: Peter J. Acklam /Javascript version by Alankar Misra @ Digital
	 *          Sutras (alankar@digitalsutras.com)) / JAVA version by Hendriek
	 *          E-mail: pjacklam@online.no WWW URL:
	 *          http://home.online.no/~pjacklam
	 * 
	 */

	/**
	 * 
	 *This method calculates the normal cumulative distribution function.
	 *<p>
	 *It is based upon algorithm 5666 for the error function, from:
	 * <p>
	 * 
	 * <pre>
	 *       Hart, J.F. et al, 'Computer Approximations', Wiley 1968
	 *</pre>
	 *<p>
	 * The FORTRAN programmer was Alan Miller. The documentation in the FORTRAN
	 * code claims that the function is "accurate to 1.e-15."
	 * <p>
	 * Steve Verrill translated the FORTRAN code (the March 30, 1986 version)
	 * into Java. This translation was performed on January 10, 2001.
	 * 
	 *@param z
	 *            The method returns the value of the normal cumulative
	 *            distribution function at z.
	 * 
	 *@version .5 --- January 10, 2001
	 * 
	 */

	/*
	 * 
	 * Here is a copy of the documentation in the FORTRAN code:
	 * 
	 * SUBROUTINE NORMP(Z, P, Q, PDF) C C Normal distribution probabilities
	 * accurate to 1.e-15. C Z = no. of standard deviations from the mean. C P,
	 * Q = probabilities to the left & right of Z. P + Q = 1. C PDF = the
	 * probability density. C C Based upon algorithm 5666 for the error
	 * function, from: C Hart, J.F. et al, 'Computer Approximations', Wiley 1968
	 * C C Programmer: Alan Miller C C Latest revision - 30 March 1986 C
	 */

	public static double normp(double z) {

		double zabs;
		double p;
		double expntl, pdf;

		final double p0 = 220.2068679123761;
		final double p1 = 221.2135961699311;
		final double p2 = 112.0792914978709;
		final double p3 = 33.91286607838300;
		final double p4 = 6.373962203531650;
		final double p5 = .7003830644436881;
		final double p6 = .3526249659989109E-01;

		final double q0 = 440.4137358247522;
		final double q1 = 793.8265125199484;
		final double q2 = 637.3336333788311;
		final double q3 = 296.5642487796737;
		final double q4 = 86.78073220294608;
		final double q5 = 16.06417757920695;
		final double q6 = 1.755667163182642;
		final double q7 = .8838834764831844E-1;

		final double cutoff = 7.071;
		final double root2pi = 2.506628274631001;

		zabs = Math.abs(z);

		// |z| > 37

		if (z > 37.0) {

			p = 1.0;

			return p;

		}

		if (z < -37.0) {

			p = 0.0;

			return p;

		}

		// |z| <= 37.

		expntl = Math.exp(-.5 * zabs * zabs);

		pdf = expntl / root2pi;

		// |z| < cutoff = 10/sqrt(2).

		if (zabs < cutoff) {

			p = expntl
					* ((((((p6 * zabs + p5) * zabs + p4) * zabs + p3) * zabs + p2)
							* zabs + p1)
							* zabs + p0)
					/ (((((((q7 * zabs + q6) * zabs + q5) * zabs + q4) * zabs + q3)
							* zabs + q2)
							* zabs + q1)
							* zabs + q0);

		} else {

			p = pdf
					/ (zabs + 1.0 / (zabs + 2.0 / (zabs + 3.0 / (zabs + 4.0 / (zabs + 0.65)))));

		}

		if (z < 0.0) {

			return p;

		} else {

			p = 1.0 - p;

			return p;

		}

	}

	static public double normInv(double p) {
		// Coefficients in rational approximations
		double[] a = { -3.969683028665376e+01, 2.209460984245205e+02,
				-2.759285104469687e+02, 1.383577518672690e+02,
				-3.066479806614716e+01, 2.506628277459239e+00 };

		double[] b = { -5.447609879822406e+01, 1.615858368580409e+02,
				-1.556989798598866e+02, 6.680131188771972e+01,
				-1.328068155288572e+01 };

		double[] c = { -7.784894002430293e-03, -3.223964580411365e-01,
				-2.400758277161838e+00, -2.549732539343734e+00,
				4.374664141464968e+00, 2.938163982698783e+00 };

		double[] d = { 7.784695709041462e-03, 3.224671290700398e-01,
				2.445134137142996e+00, 3.754408661907416e+00 };

		// Define break-points.
		double plow = 0.02425;
		double phigh = 1 - plow;

		// Rational approximation for lower region:
		if (p < plow) {
			double q = Math.sqrt(-2 * Math.log(p));
			return (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4])
					* q + c[5])
					/ ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
		}

		// Rational approximation for upper region:
		if (phigh < p) {
			double q = Math.sqrt(-2 * Math.log(1 - p));
			return -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4])
					* q + c[5])
					/ ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
		}

		// Rational approximation for central region:
		double q = p - 0.5;
		double r = q * q;
		return (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5])
				* q
				/ (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1);
	}

	/**
	 * logNormInv give the inverse of the cumulative distribution of a lognormal
	 * variable; /**
	 * 
	 * @param p
	 *            : (cumulative) probability
	 * @param offset
	 *            : offset value for the distribution (0 for a standard
	 *            lognormal distribution, with values in the range (0 --- inf).
	 *            The offset increases the lower bound
	 * @param mu
	 *            : the mean (on the logscale) of the lognormal distribution
	 * @param sigma
	 *            : the standard deviation on the logscale of the lognormal
	 *            distribution;
	 */
	static double logNormInv(double p, double offset, double mu, double sigma) {

		return offset + Math.exp(mu + sigma * normInv(p));
	};

	// inverse cumulative distribution = exp(mu+sigma*invNorm(p))
	// where invnorm(p) is inverse normal distribution
	//

	/**
	 * logNormInv2 give the inverse of the cumulative distribution of a
	 * lognormal variable characterized by skewness, mean and std; /**
	 * 
	 * @param p
	 *            : (cumulative) probability
	 * @param skewness
	 *            : skewness of the distribution
	 * @param mu
	 *            : the mean on the linear scale (not on the logscale) of the
	 *            lognormal distribution
	 * @param sigma
	 *            : the standard deviation on the linear (not on the logscale)
	 *            of the lognormal distribution;
	 */
	static double logNormInv2(double p, double skewness, double mean, double std)
			throws DynamoInconsistentDataException {

		// TODO nakijken of deze formules correct zijn ; worden ook nog bij
		// berekenen drift gebruikt//
		if (std <= 0)
			throw new DynamoInconsistentDataException(
					" STD of lognormal distribution can not be zero or negative");
		double sigma = findRoot(skewness);
		double mu = 0.5 * (Math.log(std * std)
				- Math.log(Math.exp(sigma * sigma) - 1) - sigma * sigma);
		double offset = mean - Math.exp(mu + 0.5 * sigma * sigma);

		return offset + Math.exp(mu + sigma * normInv(p));
	};

	/**
	 * f is the function : skewness as function of sigma minus skewness finding
	 * the root of this functions gives sigma from skewness
	 */
	private static double f(double sigma, double skewness) {
		return (Math.exp(sigma * sigma) + 2)
				* Math.sqrt(Math.exp(sigma * sigma) - 1) - skewness;
	};

	/**
	 * function Sign indicates whether a double value is positive or negative;
	 * 
	 * @param f
	 *            : value of which to check the sign
	 * @return: -1 or 1 (f= positive or negative
	 */
	private static double Sign(double f) {
		if (f < 0)
			return -1;
		else
			return 1;

	};
	
	protected static double findSigma(double skew) throws Exception {
		double sigma=0; 
		double hulp=0;
		hulp=2/(2+Math.pow(skew,2)+Math.sqrt(4*Math.pow(skew,2)+Math.pow(skew,4)));
		hulp=Math.pow(hulp,1.0/3.0);
		sigma=Math.sqrt(Math.log(-1.0+hulp+1.0/hulp));
		
		return sigma;
		
	};
	

	/**
	 * findRoot solves sigma from the equation for skewness as a function of
	 * sigma in case of a lognormal distribution. Here sigma is the standard
	 * deviation parameter of the lognormal distribution. findRoot uses the
	 * Brent algorithm (see numerical recipies); By replacing method f with
	 * another function, and if necessary adapting the range in which a solution
	 * is sought, one can use this method also for finding roots of other
	 * functions
	 * 
	 * @param double skew: the given value of the variable (skewness) for which
	 *        the value of sigma should be found
	 * @return double : the sigma value for which the function is zero
	 */
	protected static double findRoot(double skew) throws Exception {
		// the following should be adapted in case of use for other functions;
		double m_xmin = 0;
		double m_xmax = 1000;
		double a = m_xmin;
		double b = m_xmax;
		double c = m_xmax;
		double d = 0.0;
		double e = 0.0;
		double min1;
		double min2;
		double fa = f(a, skew);
		double fb = f(b, skew);
		double fc = fb;
		double p;
		double q;
		double r;
		double s;
		double tol1 = 0.000000001;
		;
		double xm;
		double m_NbIterMax = 200;

		// Verification of range
		if (m_xmin >= m_xmax || Sign(fa) == Sign(fb))
		//	throw new DynamoInconsistentDataException(
			throw new Exception(
					" InvalidRange for searching root in estimation of lognormal parameters; possible cause: negative skewness");
		int iiter = 0;
		for (; iiter < m_NbIterMax; iiter++) {
			if (Sign(fb) == Sign(fc)) {
				c = a;
				fc = fa;
				e = d = b - a;
			}
			if (Math.abs(fc) < Math.abs(fb)) {
				a = b;
				fa = fb;
				b = c;
				fb = fc;
				c = a;
				fc = fa;
			}
			xm = 0.5 * (c - b);
			if (Math.abs(xm) <= tol1 || fb == 0.0)
				return (b);
			if (Math.abs(e) >= tol1 && Math.abs(fa) >= Math.abs(fa)) {
				s = fb / fa;
				if (a == c) {
					p = 2.0 * xm * s;
					q = 1.0 - s;
				} else {
					q = fa / fc;
					r = fb / fc;
					p = s * (2.0 * xm * q * (q - r) - (b - a) * (r - 1.0));
					q = (q - 1.0) * (r - 1.0) * (s - 1.0);
				}
				if (p > 0.0)
					q = -q;
				p = Math.abs(p);
				min1 = 3.0 * xm * q - Math.abs(tol1 * q);
				min2 = Math.abs(e * q);
				if (2.0 * p < Math.min(min1, min2)) {
					// Apply interpolation
					e = d;
					d = p / q;
				} else {
					// bisection method
					d = xm;
					e = d;
				}
			} else {
				// too slow, apply bisection method
				d = xm;
				e = d;
			}
			a = b;
			fa = fb;
			b += (Math.abs(d) > tol1 ? d : tol1 * Sign(xm));
			/*
			 * if(Math.Abs(d)>tol1) b+=d; else b+=sign(tol1,xm);
			 */
			fb = f(b, skew);
		}
		// Algorithm passes the maximum number of iterations
		throw new Exception(
				"Accuracy Not Reached after "
						+ iiter
						+ " iterations in rootfinding for estimation of parameters of lognormal distribution");

	}

	/**
	 * method regression(y,x) does a regression of array y on matrix x
	 * 
	 * @param double [] Y-values
	 * @param double [][] X-values (including intercept)
	 * @return double [] regression coefficients
	 * */

	/* no checking of dimensions is done */
	// TODO exceptions for wrong dimensions
	private double[] regression(double[] y_array, double[][] x_array) {
		Matrix X = new Matrix(x_array);
		Matrix Y = new Matrix(y_array, y_array.length);
		Matrix XT = X.transpose();
		Matrix XX = XT.times(X);
		Matrix inverseXX = XX.inverse();
		Matrix XY = XT.times(Y);
		// beta are the regression coefficients;
		Matrix Beta = inverseXX.times(XY);
		double coef[] = Beta.getColumnPackedCopy();
		return coef;
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		float[] p = { 0.1F, 0.4F, 0.5F };
		int int1;
		Random rand1 = new Random(123);
		for (int i = 1; i < 100; i++) {
			int1 = DynamoLib.draw(p, rand1);
			System.out.println("draw " + int1);
		}
		double perc97half = normInv(0.975);
		System.out.println(" test inverse 97.5 percentile " + perc97half);
		double test = normInv(0.50);
		System.out.println(" test inverse 50 percentile " + test);
		test = normInv(0.25);
		System.out.println(" test inverse 25 percentile " + test);
		test = logNormInv(0.5, 0, 0, 1);
		System.out.println(" test inverse 50 percentile lognormal " + test);
		test = logNormInv(0.5, 10, 0, 1);
		System.out.println(" test inverse 50 percentile lognormal +offset=10 "
				+ test);

		try {
			double sigma = findRoot(0.01);
			System.out.println(" sigma estimated from skewness=0.01 " + sigma);
			double sigma2 = findSigma(0.01);
			System.out.println(" sigma estimated from skewness=0.01 " + sigma2);
		}

		// let op : skewness = 0 werkt niet, dan normal distribution nemen.
		catch (Exception e) {

			System.err.println(e.getMessage());
		}

	}
}
