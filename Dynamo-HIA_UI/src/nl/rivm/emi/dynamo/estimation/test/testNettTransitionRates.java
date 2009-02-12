package nl.rivm.emi.dynamo.estimation.test;


import nl.rivm.emi.dynamo.estimation.NettTransitionRates;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class testNettTransitionRates {	
	Log log = LogFactory.getLog(getClass().getName());
@Before
	public void setup() {
	log.fatal("Starting test. ");
}

@After
public void teardown() {
	log.fatal("Test completed ");
}

@Test
public void test() {

	
	
		// array for testing constructor for categorical riskfactor

		float[] poud = { 0.7304662F, 0.2169622F, 0.05257154F };
		float[] pnew = { 0.7189476F, 0.2255193F, 0.05553312F };
		float[] RR1 = { 1, 1, 1 };
		float[] RR2 = { 1, 2, 4 };
		double baseline = 0.2;

		// arrays for testing constructor for continuous risk factor
		double[] RRcont = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }; // ten agegroups
		double[] RRcont2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }; // ten agegroups
		double[] baselineMort = { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1,
				0.1, 0.1 };
		double[] mean = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		double[] std = { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 };
		double[] skew = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
		double[] skew0 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		// test categorical constructor
		NettTransitionRates transmat = new NettTransitionRates();
		transmat.makeNettTransitionRates(poud, pnew,
				baseline, RR1);

		/*
		 * outcome should be: [[0.8920389432461779, 0.10796105296799069, 0.0],
		 * [0.0, 0.7714595591272916, 0.22854049421101444], [0.0, 0.0,
		 * 0.9999999995989108]]
		 */

		// print comparison with expected outcome
		System.out.println("test netto transition matrixes: "
				+ transmat.getTransitionRates()[0][0] + "=0.9842310927741421 ? "
				+ transmat.getTransitionRates()[0][1] + "=0.015768911027463806? "
				+ transmat.getTransitionRates()[0][2] + "=0.0 ? "
				+ transmat.getTransitionRates()[1][0] + "=0.0  ? "
				+ transmat.getTransitionRates()[1][1] + "=0.9863498317200579 ? "
				+ transmat.getTransitionRates()[1][2] + "=0.013650192555478672 ? "
				+ transmat.getTransitionRates()[2][0] + "=0.0  ? "
				+ transmat.getTransitionRates()[2][1] + "=0.0 ? "
				+ transmat.getTransitionRates()[2][2] + "=0.9999999887155993? ");
		NettTransitionRates transmat2 = new NettTransitionRates();
		transmat.makeNettTransitionRates(poud, pnew,
				baseline, RR2);

		/*
		 * outcome should be: [[0.8920389432461779, 0.10796105296799069, 0.0],
		 * [0.0, 0.7714595591272916, 0.22854049421101444], [0.0, 0.0,
		 * 0.9999999995989108]]
		 */

		// print comparison with expected outcome
		System.out.println("test netto transition matrixes: "
				+ transmat2.getTransitionRates()[0][0] + " 0.8920389432461779? "
				+ transmat2.getTransitionRates()[0][1] + "0.10796105296799069? "
				+ transmat2.getTransitionRates()[0][2] + "= 0.0 ? "
				+ transmat2.getTransitionRates()[1][0] + "= 0.0  ? "
				+ transmat2.getTransitionRates()[1][1] + "= 0.7714595591272916 ? "
				+ transmat2.getTransitionRates()[1][2] + "= 0.22854049421101444 ? "
				+ transmat2.getTransitionRates()[2][0] + "= 0.0  ? "
				+ transmat2.getTransitionRates()[2][1] + "= 0.0 ? "
				+ transmat2.getTransitionRates()[2][2] + " = 0.9999999995989108? ");

		// test constructor for continuous risk factor
		try {
			NettTransitionRates contmat = new NettTransitionRates();
			contmat.makeNettTransitionRates(mean, std,
					skew0, baselineMort, RRcont, 1.0);
			// TODO test genereren
			System.out
					.println("test netto transitions for continuous outcomes normal distribution: (no mortality selection) "
							+ contmat.getDrift()[0][0]
							+ " =1 "
							+ contmat.getDrift()[0][1]
							+ " =1 "
							+ contmat.getDrift()[0][2]
							+ " =0 "
							+ " dubbelcheck"
							+ contmat.getDrift()[8][0]
							+ " =1 "
							+ contmat.getDrift()[8][1]
							+ " =1 "
							+ contmat.getDrift()[8][2] + " =0 ");

			NettTransitionRates contmat2 = new NettTransitionRates();
			contmat2.makeNettTransitionRates(mean, std,
					skew, baselineMort, RRcont, 1.0);
			// TODO test genereren
			System.out
					.println("test netto transitions for continuous outcomes log normal distribution:(no mortality selection) "
							+ contmat2.getDrift()[0][0]
							+ " =0 "
							+ contmat2.getDrift()[0][1]
							+ " =1 "
							+ contmat2.getDrift()[0][2]
							+ " =1 "
							+ " dubbelcheck"
							+ contmat2.getDrift()[8][0]
							+ " =0 "
							+ contmat2.getDrift()[8][1]
							+ " =1 "
							+ contmat2.getDrift()[8][2] + " =1 ");
			NettTransitionRates contmat3 = new NettTransitionRates();
			contmat2.makeNettTransitionRates(mean, std,
					skew0, baselineMort, RRcont2, 0.0);
			// calculated using Mathematica : drift eerste tijdstip: 1.06992
			// drift ratio voor eerste tijdstip: 1.08542
			// 
			System.out
					.println("test netto transitions for continuous outcomes with mortality selection: "
							+ contmat3.getDrift()[0][0]
							+ " 1.03585  "
							+ contmat3.getDrift()[0][1]
							+ " 1.02388 "
							+ contmat3.getDrift()[0][2]
							+ " =0 "
							+ " dubbelcheck"
							+ contmat3.getDrift()[8][0]
							+ " 1.03585  "
							+ contmat3.getDrift()[8][1]
							+ " 1.02388 "
							+ contmat3.getDrift()[8][2] + " =0 ");

			NettTransitionRates contmat4 = new NettTransitionRates();
			contmat2.makeNettTransitionRates(mean, std,
					skew, baselineMort, RRcont2, 1.0);
			// calculated using Mathematica : drift eerste tijdstip: 1.03585
			// drift ratio voor eerste tijdstip: 1.02388
			// 
			System.out
					.println("test netto transitions for lognormal continuous outcomes with mortality selection: "
							+ contmat3.getDrift()[0][0]
							+ " 0.0529358 "
							+ contmat3.getDrift()[0][1]
							+ " 1.04111"
							+ contmat3.getDrift()[0][2]
							+ " =1 "
							+ " dubbelcheck"
							+ contmat3.getDrift()[8][0]
							+ " 0.0529358 "
							+ contmat3.getDrift()[8][1]
							+ " 1.04111"
							+ contmat3.getDrift()[8][2] + " =1 ");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * getallen voorbeeld. Toelichting:
		 * 
		 * a = [0.7304662, 0.2169622, 0.05257154] b = [0.7189476, 0.2255193,
		 * 0.05553312]
		 * 
		 * Transities worden dan:
		 * 
		 * [0.44941386 0.2255193 0.05553304] T = [0.21696220 0.0000000
		 * 0.00000000] [0.05257154 0.0000000 0.00000000] (bij kosten matrix
		 * andersom)
		 * 
		 * En de kansen zijn dan gelijk aan P = (T/a)'
		 * 
		 * [0.6152425 1 1] P = [0.3087334 0 0] [0.0760241 0 0]
		 * 
		 * 
		 * Goede versie is [[0.7189475893974304, 0.011518657207489014, 0.0],
		 * [0.0, 0.2140006422996521, 0.002961575984954834], [0.0, 0.0,
		 * 0.05257154256105423]]
		 * 
		 * gedeeld door oude prevalentie: [[0.9842310927741421,
		 * 0.015768911027463806, 0.0], [ 0.0, 0.9863498317200579,
		 * 0.013650192555478672], [ 0.0, 0.0, 0.9999999887155993]] Met RR=1,2,4
		 * en baseline mort=0.2 krijg je sterke mortaliteit selectie: om dat te
		 * compenseren zijn meer overgangen naar hogere klassen nodig:
		 * 
		 * [[0.8920389432461779, 0.10796105296799069, 0.0], [0.0,
		 * 0.7714595591272916, 0.22854049421101444], [0.0, 0.0,
		 * 0.9999999995989108]]
		 */

	}
}
