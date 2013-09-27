/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;
import nl.rivm.emi.dynamo.ui.panels.output.ScenarioParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

/**
 * @author boshuizh
 * 
 */
public class DynamoPlotFactory {
	static private Log log = LogFactory
			.getLog("nl.rivm.emi.dynamo.output.DynamoPlotFactory");

	/* object with the output data */
	public CDMOutputFactory output;

	private ScenarioParameters params;

	/**
	 * 
	 * 
	 * 
	 * The constructor initializes the fields (arrays with all values==0), and
	 * copies the information from the scenarioInfo object to the fields in this
	 * object. It also copies the scenario name, as it will write results to the
	 * results directory Secondly, it takes the simulated population and makes
	 * this into summary arrays
	 * 
	 * @param scenInfo
	 * @param pop
	 *            simulated population
	 * @throws DynamoScenarioException
	 * @throws DynamoOutputException
	 *             when newborns are not present with the right starting year
	 */
	public DynamoPlotFactory(CDMOutputFactory outputFactory,
			ScenarioParameters scenarioParameters) {
		this.output = outputFactory;
		this.params = scenarioParameters;
		/*
		 * copy the information from scenInfo into the current object (as
		 * fields)
		 */

	}

	/**
	 * applies succesrates for men and women together.
	 * 
	 * @param inputRef
	 *            : array[2] with data for reference scenario for men and women
	 * @param inputRef
	 *            : array[2] with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied This should be zero for "ori" arrays
	 * @param a
	 *            : age: the age for which to return the result
	 * 
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToBothGenders(double[] inputRef,
			double[] inputScen, int thisScen, int year, int a) {
		double data = 0.0;

		if (thisScen == 0)

			for (int i = 0; i < 2; i++)
				data += inputRef[i];
		else if (a >= year) /* if not newborns */{
			if (this.params.getMinAge(thisScen - 1) > a - year
					|| this.params.getMaxAge(thisScen - 1) < a - year)
				for (int i = 0; i < 2; i++)
					data += inputRef[i];
			else {
				if (this.params.getInMen(thisScen - 1))
					data += (1 - this.params.getSuccesrate(thisScen - 1))
							* inputRef[0]
							+ (this.params.getSuccesrate(thisScen - 1))
							* inputScen[0];
				else
					data += inputRef[0];
				if (this.params.getInWomen(thisScen - 1))
					data += (1 - this.params.getSuccesrate(thisScen - 1))
							* inputRef[1]
							+ (this.params.getSuccesrate(thisScen - 1))
							* inputScen[1];
				else
					data += inputRef[1];
			}
		} else /* if newborns */
		{
			if (this.params.getMinAge()[thisScen - 1] > 0)
				for (int i = 0; i < 2; i++)
					data += inputRef[i];
			else {
				if (this.params.getInMen()[thisScen - 1])
					data += (1 - this.params.getSuccesrate()[thisScen - 1])
							* inputRef[0]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* inputScen[0];
				else
					data += inputRef[0];
				if (this.params.getInWomen()[thisScen - 1])
					data += (1 - this.params.getSuccesrate()[thisScen - 1])
							* inputRef[1]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* inputScen[1];
				else
					data += inputRef[1];
			}
		}
		;
		return data;
	}

	/**
	 * applies the succesrate, minimum target age, maximum target age and target
	 * gender to the
	 * 
	 * @param inputRef
	 *            : value with data for reference scenario
	 * @param inputScen
	 *            : value with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario, with 0=reference scenario
	 * @param year
	 *            : year (step): years after the age to which minimum and
	 *            maximum should be applied. This should be zero for "ori"
	 *            arrays
	 * @param a
	 *            : age: the current age of the cohort, or the age at start of
	 *            simulation (for "ori" arrays). In the last case, year above
	 *            should be zero
	 * @param gender
	 *            : gender (0 or 1 for men or women)
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * */
	/*
	 * 
	 * NB copies of this method is in ScenarioParameters, so any errors here
	 * must be also changed there
	 */
	private double applySuccesrate(double inputRef, double inputScen,
			int thisScen, int year, int a, int gender) {
		double data = 0.0;
		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;
		else {
			if (gender == 0 && !this.params.getInMen()[thisScen - 1])
				doApply = false;
			if (gender == 1 && !this.params.getInWomen()[thisScen - 1])
				doApply = false;

		}
		if (!doApply)
			data = inputRef;
		/* if not newborns */
		else if (a - year >= 0) {
			if (this.params.getMinAge(thisScen - 1) > a - year
					|| this.params.getMaxAge(thisScen - 1) < a - year)
				data = inputRef;
			else
				data = (1 - this.params.getSuccesrate()[thisScen - 1])
						* inputRef
						+ (this.params.getSuccesrate()[thisScen - 1])
						* inputScen;
		} else {
			if (this.params.getMinAge()[thisScen - 1] > 0)
				data = inputRef;
			else
				data = (1 - this.params.getSuccesrate()[thisScen - 1])
						* inputRef
						+ (this.params.getSuccesrate()[thisScen - 1])
						* inputScen;
		}
		;

		return data;
	}

	/**
	 * the methods calculates the mean of an outcome array after applying
	 * successrates. As this needs to be a weighted mean (weighted by the number
	 * of persons) it also needs the weights (numbers in each scenario) This
	 * method uses the fields: successrate, minage and maxage, inMen and
	 * inWomen, so changing these fields will give different outputs If in
	 * gender=2, the array should be of dimension [2], and inMen and InWomen are
	 * to applied
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario: all data are for the
	 *            same age and year
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario:
	 *            all data are for the same age and year
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step)
	 * @param a
	 *            : age
	 * @param gender
	 *            : gender: 0=men, 1=women, 2=both, inplying that the
	 *            application of succesrates to gender already took place
	 *            earlier, and only averaging over the current array is needed
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 * @throws DynamoOutputException
	 */
	private double applySuccesrateToMean(double[] inputRef, double[] inputScen,
			double[] nInRef, double[] nInScen, int thisScen, int year, int a,
			int gender) throws DynamoOutputException {

		double data = 0.0;
		if (gender == 2) {
			data = applySuccesrateToMeanToBothGenders(inputRef, inputScen,
					nInRef, nInScen, thisScen, year, a);

		} else {

			double denominator = 0;
			double nominator = 0;
			int nToAdd = inputRef.length;
			boolean doApply = true;
			if (thisScen == 0)
				doApply = false;
			else {
				if (gender == 0 && !this.params.getInMen(thisScen - 1))
					doApply = false;
				if (gender == 1 && !this.params.getInWomen(thisScen - 1))
					doApply = false;

			}
			if (!doApply)
				for (int i = 0; i < nToAdd; i++) {
					nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else if (a - year >= 0) /* if not newborns */{
				if (this.params.getMinAge()[thisScen - 1] > a - year
						|| this.params.getMaxAge(thisScen - 1) < a - year)
					for (int i = 0; i < nToAdd; i++) {
						nominator += inputRef[i] * nInRef[i];
						denominator += nInRef[i];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						nominator += (1 - this.params.getSuccesrate()[thisScen - 1])
								* inputRef[i]
								* nInRef[i]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* inputScen[i] * nInScen[i];
						;
						denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
								* nInRef[i]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* nInScen[i];
					}
				}
			} else /* if newborns */
			{
				if (this.params.getMinAge()[thisScen - 1] > 0)
					for (int i = 0; i < nToAdd; i++) {
						nominator += inputRef[i] * nInRef[i];
						denominator += nInRef[i];
					}
				else {
					for (int i = 0; i < nToAdd; i++) {
						nominator += (1 - this.params.getSuccesrate()[thisScen - 1])
								* inputRef[i]
								* nInRef[i]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* inputScen[i] * nInScen[i];
						;
						denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
								* nInRef[i]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* nInScen[i];
					}
				}
			}
			;
			if (denominator != 0)
				data = nominator / denominator;
			else
				data = -99999;
		}
		return data;
	}

	private double applySuccesrateToMeanToBothGenders(double[] inputRef,
			double[] inputScen, double[] nInRef, double[] nInScen,
			int thisScen, int year, int a) throws DynamoOutputException {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;
		if (inputRef.length != 2)
			throw new DynamoOutputException(
					" (development error code) gender array has length "
							+ inputRef.length + " in stead of 2");
		if (thisScen == 0)

			for (int i = 0; i < 2; i++) {
				nominator += inputRef[i] * nInRef[i];
				denominator += nInRef[i];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (this.params.getMinAge(thisScen - 1) > a - year
					|| this.params.getMaxAge(thisScen - 1) < a - year)
				for (int i = 0; i < 2; i++) {
					nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				if (this.params.getInMen(thisScen - 1)) {
					nominator += (1 - this.params.getSuccesrate(thisScen - 1))
							* inputRef[0] * nInRef[0]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* inputScen[0] * nInScen[0];
					;
					denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* nInRef[0]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* nInScen[0];
				} else {
					nominator += inputRef[0] * nInRef[0];
					denominator += nInRef[0];
				}
				if (this.params.getInWomen()[thisScen - 1]) {
					nominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* inputRef[1]
							* nInRef[1]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* inputScen[1] * nInScen[1];
					;
					denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* nInRef[1]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* nInScen[1];
				} else {
					nominator += inputRef[1] * nInRef[1];
					denominator += nInRef[1];
				}

			}
		} else /* if newborns */
		{
			if (this.params.getMinAge()[thisScen - 1] > 0)
				for (int i = 0; i < 2; i++) {
					nominator += inputRef[i] * nInRef[i];
					denominator += nInRef[i];
				}
			else {
				if (this.params.getInMen()[thisScen - 1]) {
					nominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* inputRef[0]
							* nInRef[0]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* inputScen[0] * nInScen[0];
					;
					denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* nInRef[0]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* nInScen[0];
				} else {
					nominator += inputRef[0] * nInRef[0];
					denominator += nInRef[0];
				}
				if (this.params.getInWomen()[thisScen - 1]) {
					nominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* inputRef[1]
							* nInRef[1]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* inputScen[1] * nInScen[1];
					;
					denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
							* nInRef[1]
							+ (this.params.getSuccesrate()[thisScen - 1])
							* nInScen[1];
				} else {
					nominator += inputRef[1] * nInRef[1];
					denominator += nInRef[1];
				}
			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome after applying
	 * successrates, using data from both sexes. As this needs to be a weighted
	 * mean (weighted by the number of persons) it also needs the weights
	 * (numbers in each scenario) This method uses the fields: successrate,
	 * minage and maxage, so changing these fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step)
	 * @param a
	 *            : age
	 * 
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToMeanToBothGenders(double[][] inputRef,
			double[][] inputScen, double[][] nInRef, double[][] nInScen,
			int thisScen, int year, int a) {
		double data = 0.0;
		double denominator0 = 0;
		double nominator0 = 0;
		double denominator1 = 0;
		double nominator1 = 0;
		int nToAdd = inputRef.length;

		if (thisScen == 0)
			for (int i = 0; i < nToAdd; i++) {
				nominator0 += inputRef[i][0] * nInRef[i][0];
				denominator0 += nInRef[i][0];
				nominator1 += inputRef[i][1] * nInRef[i][1];
				denominator1 += nInRef[i][1];
			}
		else if (a - year >= 0) /* if not newborns */{
			if (this.params.getMinAge(thisScen - 1) > a - year
					|| this.params.getMaxAge(thisScen - 1) < a - year)
				for (int i = 0; i < nToAdd; i++) {
					nominator0 += inputRef[i][0] * nInRef[i][0];
					denominator0 += nInRef[i][0];
					nominator1 += inputRef[i][1] * nInRef[i][1];
					denominator1 += nInRef[i][1];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					if (this.params.getInMen()[thisScen - 1]) {
						nominator0 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* inputRef[i][0]
								* nInRef[i][0]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* inputScen[i][0] * nInScen[i][0];
						;
						denominator0 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* nInRef[i][0]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* nInScen[i][0];
					} else {
						nominator0 += inputRef[i][0] * nInRef[i][0];
						denominator0 += nInRef[i][0];
					}
					if (this.params.getInWomen()[thisScen - 1]) {
						nominator1 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* inputRef[i][1]
								* nInRef[i][1]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* inputScen[i][1] * nInScen[i][1];
						;
						denominator1 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* nInRef[i][1]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* nInScen[i][1];
					}

					else {
						nominator1 += inputRef[i][1] * nInRef[i][1];
						denominator1 += nInRef[i][1];
					}
				}
			}
		} else /* if newborns */
		{
			if (this.params.getMinAge()[thisScen - 1] > 0)
				for (int i = 0; i < nToAdd; i++) {
					nominator0 += inputRef[i][0] * nInRef[i][0];
					denominator0 += nInRef[i][0];
					nominator1 += inputRef[i][1] * nInRef[i][1];
					denominator1 += nInRef[i][1];
				}
			else {
				for (int i = 0; i < nToAdd; i++) {
					if (this.params.getInMen()[thisScen - 1]) {
						nominator0 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* inputRef[i][0]
								* nInRef[i][0]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* inputScen[i][0] * nInScen[i][0];
						;
						denominator0 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* nInRef[i][0]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* nInScen[i][0];
					} else {
						nominator0 += inputRef[i][0] * nInRef[i][0];
						denominator0 += nInRef[i][0];
					}
					if (this.params.getInWomen()[thisScen - 1]) {
						nominator1 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* inputRef[i][1]
								* nInRef[i][1]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* inputScen[i][1] * nInScen[i][1];
						;
						denominator1 += (1 - this.params.getSuccesrate()[thisScen - 1])
								* nInRef[i][1]
								+ (this.params.getSuccesrate()[thisScen - 1])
								* nInScen[i][1];
					} else {
						nominator1 += inputRef[i][1] * nInRef[i][1];
						denominator1 += nInRef[i][1];
					}
				}
			}
		}
		;
		if (denominator0 + denominator1 != 0)
			data = (nominator0 + nominator1) / (denominator0 + denominator1);
		else
			data = -99999;
		return data;
	}

	/**
	 * the methods calculates the mean of an outcome after applying
	 * successrates. As this needs to be a weighted mean (weighted by the number
	 * of persons) it also needs the weights (numbers in each scenario) This
	 * method uses the fields: successrate, minage and maxage, so changing these
	 * fields will give different outputs
	 * 
	 * @param inputRef
	 *            : array with data for reference scenario
	 * @param inputRef
	 *            : array with data for 100% successfull alternative scenario
	 * @param thisScen
	 *            : number of scenario
	 * @param year
	 *            : year (step)
	 * @param a
	 *            : age
	 * @param gender
	 *            : gender
	 * 
	 * @return the result for a scenario to which the successrates and
	 *         min-maximum age have been applied
	 */
	private double applySuccesrateToMean(double inputRef, double inputScen,
			double nInRef, double nInScen, int thisScen, int year, int a,
			int gender) {
		double data = 0.0;
		double denominator = 0;
		double nominator = 0;

		boolean doApply = true;
		if (thisScen == 0)
			doApply = false;
		else {
			if (gender == 0 && !this.params.getInMen()[thisScen - 1])
				doApply = false;
			if (gender == 1 && !this.params.getInWomen()[thisScen - 1])
				doApply = false;

		}
		if (!doApply) {

			nominator += inputRef * nInRef;
			denominator += nInRef;
		} else if (a - year >= 0)/* if not newborns */{
			if (this.params.getMinAge(thisScen - 1) > a - year
					|| this.params.getMaxAge(thisScen - 1) < a - year) {
				nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {

				nominator += (1 - this.params.getSuccesrate(thisScen - 1))
						* inputRef * nInRef
						+ (this.params.getSuccesrate()[thisScen - 1])
						* inputScen * nInScen;
				;
				denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
						* nInRef + (this.params.getSuccesrate()[thisScen - 1])
						* nInScen;

			}
		} else { /* for newborns */
			if (this.params.getMinAge()[thisScen - 1] > 0) {
				nominator += inputRef * nInRef;
				denominator += nInRef;
			} else {

				nominator += (1 - this.params.getSuccesrate()[thisScen - 1])
						* inputRef * nInRef
						+ (this.params.getSuccesrate()[thisScen - 1])
						* inputScen * nInScen;
				;
				denominator += (1 - this.params.getSuccesrate()[thisScen - 1])
						* nInRef + (this.params.getSuccesrate()[thisScen - 1])
						* nInScen;

			}
		}
		;
		if (denominator != 0)
			data = nominator / denominator;
		else
			data = -99999;
		return data;
	}

	/**
	 * makes a array of mortality by scenario, year, age and sex It is not
	 * possible to do so also by riskfactor or by disease In order to do so,
	 * this should be included as state in the update rule
	 * 
	 * @param numbers
	 *            (boolean) : if true returns numbers, otherwise rates
	 * @param riskFactor
	 *            : value of the riskfactor ; -1= total mortality (irrespective
	 *            of risk factor)
	 * @return array of mortality by scenario, year, age and sex
	 */
	public double[][][][] getMortality(boolean numbers, int riskFactor) {

		double[][][][] mortality = new double[this.output.nScen + 1][this.output.stepsInRun][this.output.nDim][2];

		int loopBegin = riskFactor;
		int loopEnd = riskFactor + 1;
		if (riskFactor < 0) {
			loopBegin = 0;
			loopEnd = this.output.nRiskFactorClasses;
		}
		if (this.output.stepsInRun > 0) {
			/*
			 * number of persons now and after a year are calculated for all
			 * possible combinations of age and years, regardless of whether
			 * there are persons available at a particular age and year
			 * combination
			 */
			for (int scen = 0; scen < this.output.nScen + 1; scen++)
				for (int a = 0; a < Math.min(96 + this.output.stepsInRun - 1,
						this.output.nDim - 1); a++)
					for (int g = 0; g < 2; g++)
						for (int stepCount = 0; stepCount < this.output.stepsInRun; stepCount++) {
							double denominator = 0;
							double nominator = 0;

							for (int r = loopBegin; r < loopEnd; r++) {
								denominator += applySuccesrate(
										this.output.nPopByRiskClassByAge[0][stepCount][r][a][g],
										this.output.nPopByRiskClassByAge[scen][stepCount][r][a][g],
										scen, stepCount, a, g);

								nominator += applySuccesrate(
										this.output
												.getMortalityByRiskClassByAge()[0][stepCount][r][a][g],
										this.output
												.getMortalityByRiskClassByAge()[scen][stepCount][r][a][g],
										scen, stepCount, a, g);
							}

							if (denominator != 0 && !numbers)
								mortality[scen][stepCount][a][g] = nominator
										/ denominator;
							if (denominator != 0 && numbers)
								mortality[scen][stepCount][a][g] = nominator;
							if (denominator == 0)
								mortality[scen][stepCount][a][g] = -1;
							/*
							 * also make mortality -1 for the highest age group
							 * as there is never a higher age available with
							 * which to calculate mortality
							 */
							if (a == (this.output.nDim - 2))
								mortality[scen][stepCount][this.output.nDim - 1][g] = -1;

						}
		}
		return mortality;
	}

	/**
	 * This method makes a plot for survival over simulation time for all
	 * scenario's. <br>
	 * Survival plotted is that of all individuals in the population without the
	 * newborns <br>
	 * Survival is the fraction of persons with whom the simulation starts
	 * 
	 * @param gender
	 *            : 0= for men; 1= for women; 2= for entire population
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) if true absolute numbers are plotted in stead of
	 *            percentage of starting population
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return freechart plot
	 */
	public JFreeChart makeSurvivalPlotByScenario(int gender, int riskClass,
			boolean differencePlot, boolean numbers, boolean blackAndWhite) {
		XYDataset xyDataset = null;
		double[][][][] nPopByAge = null;
		if (riskClass < 0)
			nPopByAge = getNPopByOriAge();
		else
			nPopByAge = getNPopByOriAge(riskClass);
		int nDim2 = nPopByAge[0][0].length;
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			XYSeries series = new XYSeries(
					this.output.getScenarioNames()[thisScen]);
			double dat0 = 0;

			for (int steps = 0; steps < this.output.getStepsInRun() + 1; steps++) {
				double indat = 0;
				double indatr = 0;
				/*
				 * popByAge has value 1 at steps= 0) // TODO this does not work
				 * OK when ageMax and min are applied
				 */

				for (int age = 0; age < nDim2; age++)
					if (gender < 2) {
						indat += applySuccesrate(
								nPopByAge[0][steps][age][gender],
								nPopByAge[thisScen][steps][age][gender],
								thisScen, 0, age, gender);
						indatr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								0, age, 0)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, 0, age, 1);
						indatr += nPopByAge[0][steps][age][0]
								+ nPopByAge[0][steps][age][1];
					}

				if (steps == 0)
					dat0 = indat;

				if (dat0 > 0) {
					if (differencePlot && !numbers)
						series.add((double) steps,
								100 * ((indat / dat0) - (indatr / dat0)));
					if (!differencePlot && !numbers)
						series.add((double) steps, 100 * (indat / dat0));
					if (differencePlot && numbers)
						series.add((double) steps, (indat) - (indatr));
					if (!differencePlot && numbers)
						series.add((double) steps, indat);

				}
			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(series);
			else
				((XYSeriesCollection) xyDataset).addSeries(series);
		}
		String label;
		String chartTitle = "survival ";
		if (numbers && differencePlot)
			chartTitle = "excess numbers in population"
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess survival" + " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "numbers in population ";
		String yTitle = "survival (%)";
		if (differencePlot && !numbers)
			yTitle = "excess survival (%)";
		if (!differencePlot && numbers)
			yTitle = "population numbers";
		if (differencePlot && numbers)
			yTitle = "excess population numbers";

		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		/*
		 * ChartFrame frame1 = new ChartFrame("Survival Chart " + label, chart);
		 * frame1.setVisible(true); frame1.setSize(300, 300); try {
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "survivalplot_" + label + ".jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage());
		 * System.out.println("Problem occurred creating chart."); }
		 */
		return chart;
	}

	/**
	 * 
	 * This method makes a plot for survival over simulation time for all
	 * scenario's. <br>
	 * 
	 * Survival plotted is that of all individuals in the population without the
	 * newborns <br>
	 * Survival is the fraction of persons with whom the simulation starts
	 * 
	 * @param gender
	 *            : 0= for men; 1= for women; 2= for entire population
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) if true absolute numbers are plotted in stead of
	 *            percentage of starting population
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return freechart plot
	 */

	public JFreeChart makeSurvivalPlotByRiskClass(int gender, int thisScen,
			boolean differencePlot, boolean numbers, boolean blackAndWhite) {

		XYDataset xyDataset = null;
		double[][][][][] nPopByAge = this.output
				.getNPopByOriRiskClassByOriAge();

		int nDim2 = nPopByAge[0][0][0].length;
		for (int riskClass = 0; riskClass < this.output.getNRiskFactorClasses(); riskClass++) {
			XYSeries series = new XYSeries(
					this.output.getRiskClassnames()[riskClass]);
			double dat0 = 0;

			for (int steps = 0; steps < this.output.getStepsInRun() + 1; steps++) {
				double indat = 0;
				double indatr = 0;
				/*
				 * popByAge has value 1 at steps= 0) // TODO this does not work
				 * OK when ageMax and min are applied
				 */

				for (int age = 0; age < nDim2; age++)
					if (gender < 2) {
						indat += applySuccesrate(
								nPopByAge[0][steps][riskClass][age][gender],
								nPopByAge[thisScen][steps][riskClass][age][gender],
								thisScen, 0, age, gender);
						indatr += nPopByAge[0][steps][riskClass][age][gender];
					} else {
						indat += applySuccesrate(
								nPopByAge[0][steps][riskClass][age][0],
								nPopByAge[thisScen][steps][riskClass][age][0],
								thisScen, 0, age, 0)
								+ applySuccesrate(
										nPopByAge[0][steps][riskClass][age][1],
										nPopByAge[thisScen][steps][riskClass][age][1],
										thisScen, 0, age, 1);
						indatr += nPopByAge[0][steps][riskClass][age][0]
								+ nPopByAge[0][steps][riskClass][age][1];
					}

				if (steps == 0)
					dat0 = indat;

				if (dat0 > 0) {
					if (differencePlot && !numbers)
						series.add((double) steps,
								100 * ((indat / dat0) - (indatr / dat0)));
					if (!differencePlot && !numbers)
						series.add((double) steps, 100 * (indat / dat0));
					if (differencePlot && numbers)
						series.add((double) steps, (indat) - (indatr));
					if (!differencePlot && numbers)
						series.add((double) steps, indat);

				}
			}

			if (riskClass == 0)
				xyDataset = new XYSeriesCollection(series);
			else
				((XYSeriesCollection) xyDataset).addSeries(series);
		}
		String label;
		String chartTitle = "survival ";
		if (numbers && differencePlot)
			chartTitle = "excess numbers in population"
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess survival" + " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "numbers in population ";
		String yTitle = "survival (%)";
		if (differencePlot && !numbers)
			yTitle = "excess survival (%)";
		if (!differencePlot && numbers)
			yTitle = "population numbers";
		if (differencePlot && numbers)
			yTitle = "excess population numbers";

		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
				"years of simulation", yTitle, xyDataset,
				PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r <= this.output.getNRiskFactorClasses(); r++) {
			setLineProperties(renderer, r, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		/*
		 * ChartFrame frame1 = new ChartFrame("Survival Chart " + label, chart);
		 * frame1.setVisible(true); frame1.setSize(300, 300); try {
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "survivalplot_" + label + ".jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage());
		 * System.out.println("Problem occurred creating chart."); }
		 */
		return chart;
	}

	/**
	 * This method makes a plot for the numbers in the population over time for
	 * all scenario's. <br>
	 * Survival plotted is that of all individuals in the population without the
	 * newborns <br>
	 * Survival is the fraction of persons with whom the simulation starts
	 * 
	 * @param gender
	 *            : 0= for men; 1= for women; 2= for entire population
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) if true absolute numbers are plotted in stead of
	 *            percentage of starting population
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return freechart plot
	 */
	public JFreeChart makeYearPopulationNumberPlotByScenario(int gender,
			int riskClass, boolean differencePlot, boolean blackAndWhite) {
		XYDataset xyDataset = null;
		double[][][][] nPopByAge = null;
		if (riskClass < 0)

			nPopByAge = this.output.getNPopByAge();
		else
			nPopByAge = this.output.getNPopByAgeForRiskclass(riskClass);
		int nDim2 = nPopByAge[0][0].length;
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			XYSeries series = new XYSeries(
					this.output.getScenarioNames()[thisScen]);
			double dat0 = 0;

			for (int steps = 0; steps < this.output.getStepsInRun() + 1; steps++) {
				double indat = 0;
				double indatr = 0;
				/*
				 * popByAge has value 1 at steps= 0) // TODO this does not work
				 * OK when ageMax and min are applied
				 */

				for (int age = 0; age < nDim2; age++)
					if (gender < 2) {
						indat += applySuccesrate(
								nPopByAge[0][steps][age][gender],
								nPopByAge[thisScen][steps][age][gender],
								thisScen, steps, age, gender);
						indatr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age, 0)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, steps, age, 1);
						indatr += nPopByAge[0][steps][age][0]
								+ nPopByAge[0][steps][age][1];
					}

				if (steps == 0)
					dat0 = indat;

				if (dat0 > 0) {

					if (differencePlot)
						series.add((double) steps + this.output.getStartYear(),
								(indat) - (indatr));
					if (!differencePlot)
						series.add((double) steps + this.output.getStartYear(),
								indat);

				}
			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(series);
			else
				((XYSeriesCollection) xyDataset).addSeries(series);
		}
		String label;
		String chartTitle = "population numbers ";
		if (differencePlot)
			chartTitle = "excess numbers in population"
					+ " compared to ref scenario";

		if (!differencePlot)
			chartTitle = "numbers in population ";
		String yTitle = "numbers in population";

		if (!differencePlot)
			yTitle = "population numbers";

		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		/*
		 * ChartFrame frame1 = new ChartFrame("Survival Chart " + label, chart);
		 * frame1.setVisible(true); frame1.setSize(300, 300); try {
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "survivalplot_" + label + ".jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage());
		 * System.out.println("Problem occurred creating chart."); }
		 */
		return chart;
	}

	/**
	 * This method makes a plot for the numbers in the population over time for
	 * all risk factor levels. <br>
	 * Survival plotted is that of all individuals in the population without the
	 * newborns <br>
	 * Survival is the fraction of persons with whom the simulation starts
	 * 
	 * @param gender
	 *            : 0= for men; 1= for women; 2= for entire population
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param thisScen
	 *            scenario for which to plot
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return freechart plot
	 */
	/**
	 * @param gender
	 * @param thisScen
	 * @param differencePlot
	 * @param blackAndWhite
	 * @return
	 */
	public JFreeChart makeYearPopulationNumberPlotByRiskClass(int gender,
			int thisScen, boolean differencePlot, boolean blackAndWhite) {
		XYDataset xyDataset = null;
		double[][][][][] nPopByAge = this.output.getNPopByRiskClassByAge();

		int nDim2 = nPopByAge[0][0][0].length;
		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
			XYSeries series = new XYSeries(this.output.getRiskClassnames()[r]);
			double dat0 = 0;

			for (int steps = 0; steps < this.output.getStepsInRun() + 1; steps++) {
				double indat = 0;
				double indatr = 0;
				/*
				 * popByAge has value 1 at steps= 0) // TODO this does not work
				 * OK when ageMax and min are applied
				 */

				for (int age = 0; age < nDim2; age++)
					if (gender < 2) {
						indat += applySuccesrate(
								nPopByAge[0][steps][r][age][gender],
								nPopByAge[thisScen][steps][r][age][gender],
								thisScen, steps, age, gender);
						indatr += nPopByAge[0][steps][r][age][gender];
					} else {
						indat += applySuccesrate(
								nPopByAge[0][steps][r][age][0],
								nPopByAge[thisScen][steps][r][age][0],
								thisScen, steps, age, 0)
								+ applySuccesrate(
										nPopByAge[0][steps][r][age][1],
										nPopByAge[thisScen][steps][r][age][1],
										thisScen, steps, age, 1);
						indatr += nPopByAge[0][steps][r][age][0]
								+ nPopByAge[0][steps][r][age][1];
					}

				if (steps == 0)
					dat0 = indat;

				if (dat0 > 0) {

					if (differencePlot)
						series.add((double) steps + this.output.getStartYear(),
								(indat) - (indatr));
					if (!differencePlot)
						series.add((double) steps + this.output.getStartYear(),
								indat);

				}
			}

			if (r == 0)
				xyDataset = new XYSeriesCollection(series);
			else
				((XYSeriesCollection) xyDataset).addSeries(series);
		}
		String label;
		String chartTitle = "population numbers ";
		if (differencePlot)
			chartTitle = "excess numbers in population"
					+ " compared to ref scenario";

		if (!differencePlot)
			chartTitle = "numbers in population ";
		String yTitle = "numbers in population";

		if (!differencePlot)
			yTitle = "population numbers";

		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);

		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r <= this.output.getNRiskFactorClasses(); r++) {
			setLineProperties(renderer, r, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));

		/*
		 * ChartFrame frame1 = new ChartFrame("Survival Chart " + label, chart);
		 * frame1.setVisible(true); frame1.setSize(300, 300); try {
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "survivalplot_" + label + ".jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage());
		 * System.out.println("Problem occurred creating chart."); }
		 */
		return chart;
	}

	/**
	 * makes a plot of the prevalence of disease d by gender with year on the
	 * axis for one scenario
	 * 
	 * @param thisScen
	 *            : scenario number
	 * @param d
	 *            : disease number
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) plot absolute numbers in stead of percentages
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return JFreeChart with the plot
	 */
	public JFreeChart makeYearDiseaseByGenderPlot(int thisScen, int d,
			boolean differencePlot, boolean numbers, boolean blackAndWhite,
			boolean prevalence) {

		XYSeries menSeries = new XYSeries("men");

		XYSeries womenSeries = new XYSeries("women");
		XYSeries totalSeries = new XYSeries("total");

		int plusone = 0;
		if (prevalence)
			plusone = 1;
		for (int steps = 0; steps < this.output.getStepsInRun() + plusone; steps++) {

			double indat = 0;
			double indatr = 0;
			indat = calculateAveragePrevalence(thisScen, steps, d, 0, numbers,
					prevalence);
			indatr = calculateAveragePrevalence(0, steps, d, 0, numbers,
					prevalence);
			if (!differencePlot)
				menSeries.add((double) steps + this.output.getStartYear(),
						indat);
			else
				menSeries.add((double) steps + this.output.getStartYear(),
						indat - indatr);
			indat = calculateAveragePrevalence(thisScen, steps, d, 1, numbers,
					prevalence);
			indatr = calculateAveragePrevalence(0, steps, d, 1, numbers,
					prevalence);
			if (!differencePlot)
				womenSeries.add((double) steps + this.output.getStartYear(),
						indat);
			else
				womenSeries.add((double) steps + this.output.getStartYear(),
						indat - indatr);
			indat = calculateAveragePrevalence(thisScen, steps, d, 2, numbers,
					prevalence);
			indatr = calculateAveragePrevalence(0, steps, d, 2, numbers,
					prevalence);
			if (!differencePlot)
				totalSeries.add((double) steps + this.output.getStartYear(),
						indat);
			else
				totalSeries.add((double) steps + this.output.getStartYear(),
						indat - indatr);

		}
		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);
		((XYSeriesCollection) xyDataset).addSeries(totalSeries);
		JFreeChart chart;

		String name = "disability (average dalyweight)";
		if (d < output.getNDiseases())
			name = this.output.getDiseaseNames()[d];
		String chartTitle = "prevalence of " + name;

		String yTitle = "prevalence rate (%)" + name;

		if (prevalence) {
			if (!prevalence)
				chartTitle = "incidence of " + name;
			if (numbers && differencePlot)
				chartTitle = "Excess number with " + name
						+ " compared to the ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "Excess prevalence of " + name
						+ " compared to the ref scenario";
			if (numbers && !differencePlot)
				chartTitle = "Number of persons with " + name;

			if (differencePlot && !numbers)
				yTitle = "excess prevalence rate (%) " + name;
			if (!differencePlot && numbers)
				yTitle = "number with " + name;
			if (differencePlot && numbers)
				yTitle = "excess number with " + name;
		} else {
			chartTitle = "incidence of " + name;
			yTitle = "incidence rate (per 100 py)" + name;
			if (numbers && differencePlot)
				chartTitle = "Excess new cases of " + name
						+ " compared to the ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "Excess incidence rate of " + name
						+ " compared to the ref scenario";
			if (numbers && !differencePlot)
				chartTitle = "New cases of " + name;

			if (differencePlot && !numbers)
				yTitle = "excess incidence rate (per 100 py) " + name;
			if (!differencePlot && numbers)
				yTitle = "new cases of " + name;
			if (differencePlot && numbers)
				yTitle = "excess new cases of " + name;
		}
		String label = this.output.getScenarioNames()[thisScen];
		chart = ChartFactory.createXYLineChart(chartTitle, "year", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int gender = 0; gender <= 2; gender++) {
			setLineProperties(renderer, gender, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;
	}

	/*
	 * in the tryout phase of the project the charts were written to file, but
	 * this is no longer needed as the plot can be saved by the user with a
	 * standard menu from chartcomposite Still this is kept here commented out
	 * in case of future return
	 * 
	 * try { writeCategoryChart(baseDir + File.separator + "simulations" +
	 * File.separator + simulationName + File.separator + "results" +
	 * File.separator + "chartPrevalence" + d + ".jpg", chart); } catch
	 * (Exception e) { System.out.println(e.getMessage()); System.out
	 * .println("Problem occurred creating chart. for diseasenumber" + d); }
	 */

	/**
	 * makes a plot of the prevalence of disease d by gender with age on the
	 * axis for one scenario
	 * 
	 * @param thisScen
	 *            : scenario number
	 * @param d
	 *            : disease number
	 * @param year
	 *            : year for which to plot
	 * @param differencePlot
	 *            : (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) plot absolute numbers in stead of percentages
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * 
	 * @return JFreeChart plot of the prevalence of disease d by gender with age
	 *         on the axis for one scenario
	 * 
	 */

	public JFreeChart makeAgeDiseaseByGenderPlot(int thisScen, int d, int year,
			boolean differencePlot, boolean numbers, boolean blackAndWhite,
			boolean prevalence) {

		XYSeries menSeries = new XYSeries("men");
		XYSeries womenSeries = new XYSeries("women");
		XYSeries totalSeries = new XYSeries("total");

		double indat0 = 0; /* diseasenumbers for men */
		double indat1 = 0;/* diseasenumbers for women */
		double npop0 = 0;/* total numbers for men */
		double npop1 = 0;/* total numbers for women */
		double indat0r = 0; /* diseasenumbers for men in reference scenario */
		double indat1r = 0;/* diseasenumbers for women in reference scenario */
		double npop0r = 0;/* total numbers for men in reference scenario */
		double npop1r = 0;/* total numbers for women in reference scenario */
		double[][][][] nPopByAge = this.output.getNPopByAge();
		double[][][][] nDiseaseByAge = null;
		if (prevalence && d < this.output.getNDiseases())
			nDiseaseByAge = this.output.getNDiseaseByAge(d);
		else if (prevalence)
			nDiseaseByAge = this.output.getNDisabledByAge();
		else
			nDiseaseByAge = this.output.getNewCasesByAge(d);

		/*
		 * first calculate for men and women separately
		 */

		if (prevalence || year < this.output.getStepsInRun())
			if (prevalence || year < this.output.getStepsInRun())
				for (int age = 0; age < 96 /* + this.output.getStepsInRun() */; age++) {
					indat0 = applySuccesrate(nDiseaseByAge[0][year][age][0],
							nDiseaseByAge[thisScen][year][age][0], thisScen,
							year, age, 0);
					npop0 = applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age, 0);
					indat0r = nDiseaseByAge[0][year][age][0];
					npop0r = nPopByAge[0][year][age][0];
					indat1 = applySuccesrate(nDiseaseByAge[0][year][age][1],
							nDiseaseByAge[thisScen][year][age][1], thisScen,
							year, age, 1);
					npop1 = applySuccesrate(nPopByAge[0][year][age][1],
							nPopByAge[thisScen][year][age][1], thisScen, year,
							age, 1);
					indat1r = nDiseaseByAge[0][year][age][1];
					npop1r = nPopByAge[0][year][age][1];
					addToSeries(differencePlot, numbers, menSeries, indat0,
							npop0, indat0r, npop0r, age);

					addToSeries(differencePlot, numbers, womenSeries, indat1,
							npop1, indat1r, npop1r, age);

					addToSeries(differencePlot, numbers, totalSeries, indat1
							+ indat0, npop1 + npop0, indat1r + indat0r, npop1r
							+ npop0r, age);

				}

		XYDataset xyDataset = new XYSeriesCollection(menSeries);
		((XYSeriesCollection) xyDataset).addSeries(womenSeries);
		((XYSeriesCollection) xyDataset).addSeries(totalSeries);
		JFreeChart chart;
		String name = "disability (average dalyweight)";
		if (d < this.output.getNDiseases())
			name = this.output.getDiseaseNames()[d];
		String yTitle = "prevalence rate (%)" + name;

		if (prevalence) {
			if (differencePlot && !numbers)
				yTitle = "excess prevalence rate (%) " + name;
			if (!differencePlot && numbers)
				yTitle = "number with " + name;
			if (differencePlot && numbers)
				yTitle = "excess number with " + name;
		} else {
			yTitle = "incidence rate (per 100 py)" + name;

			if (differencePlot && !numbers)
				yTitle = "excess incidence rate (per 100 py) " + name;
			if (!differencePlot && numbers)
				yTitle = "new cases of " + name;
			if (differencePlot && numbers)
				yTitle = "excess new cases of " + name;
		}

		String label = this.output.getScenarioNames()[thisScen] + "; "
				+ (this.output.getStartYear() + year);

		String chartTitle = "Prevalence of " + name;
		if (!prevalence)
			chartTitle = "Incidence of " + name;
		if (prevalence) {
			if (!numbers && differencePlot)
				chartTitle = "Excess prevalence of " + name
						+ "compared to ref scenario";
			if (numbers && differencePlot)
				chartTitle = "Excess numbers of " + name
						+ "compared to ref scenario";
			if (numbers && !differencePlot)
				chartTitle = "Number of persons with " + name;
		} else {
			if (!numbers && differencePlot)
				chartTitle = "Excess incidence of " + name
						+ "compared to ref scenario";
			if (numbers && differencePlot)
				chartTitle = "Excess new cases of " + name
						+ "compared to ref scenario";
			if (numbers && !differencePlot)
				chartTitle = "Number of new cases of " + name;
		}

		chart = ChartFactory.createXYLineChart(chartTitle, "age", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getMaxAgeInSimulation() == this.output
				.getMinAgeInSimulation())
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int gender = 0; gender <= 2; gender++) {
			setLineProperties(renderer, gender, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;
	}

	private void addToSeries(boolean differencePlot, boolean numbers,
			XYSeries series, double indat, double npop, double indatr,
			double npopr, int age) {

		/* first get rid of the numerical error in case no differences */

		if (Math.abs(indat - indatr) < 0.1 && Math.abs(npop - npopr) < 0.1) {
			indatr = indat;
			npopr = npop;
		}

		if (npop != 0 && !differencePlot && !numbers)

			series.add((double) age, 100 * indat / npop);

		if (npop != 0 && npopr != 0 && differencePlot && !numbers)
			series.add((double) age, 100 * (indat / npop) - 100
					* (indatr / npopr));

		if (npop != 0 && !differencePlot && numbers)

			series.add((double) age, indat);
		/*
		 * in case of zero persons in one of the two populations, still show
		 * numbers in the other population, so only no results when both are
		 * zero
		 */
		if (!(npop == 0 && npopr == 0) && differencePlot && numbers)
			series.add((double) age, (indat) - (indatr));

	}

	/**
	 * makes a plot of the prevalence of disease d by scenario with age on the
	 * axis for one scenario
	 * 
	 * @param gender
	 *            : 2= both
	 * @param d
	 *            : disease number
	 * @param year
	 *            : year for which to plot
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            (boolean) plot absolute numbers in stead of percentages
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return plot with prevalence of disease d by scenario with age on the
	 *         axis for one scenario
	 * 
	 */
	public JFreeChart makeAgeDiseaseByScenarioPlot(int gender, int d, int year,
			boolean differencePlot, boolean numbers, boolean blackAndWhite,
			boolean prevalence) {

		XYSeries scenSeries[] = new XYSeries[this.output.getNScen() + 1];
		XYDataset xyDataset = null;
		double indat0 = 0; /* diseasenumbers for men */
		double indat1 = 0;/* diseasenumbers for women */
		double npop0 = 0;/* total numbers for women */
		double npop1 = 0;/* total numbers for women */

		double indat0r = 0; /* diseasenumbers for men in reference scenario */
		double indat1r = 0;/* diseasenumbers for women in reference scenario */
		double npop0r = 0;/* total numbers for women in reference scenario */
		double npop1r = 0;/* total numbers for women in reference scenario */
		double[][][][] nPopByAge = this.output.getNPopByAge();
		double[][][][] nDiseaseByAge = null;
		if (d < this.output.getNDiseases() && prevalence)
			nDiseaseByAge = this.output.getNDiseaseByAge(d);
		else if (prevalence)
			nDiseaseByAge = this.output.getNDisabledByAge();
		else
			nDiseaseByAge = this.output.getNewCasesByAge(d);
		/*
		 * first calculate for men and women separately
		 */

		for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(
					this.output.getScenarioNames()[thisScen]);

			if (prevalence || year < this.output.getStepsInRun())
				for (int age = 0; age < 96 /* + this.output.getStepsInRun() */; age++) {
					indat0 = 0;
					indat1 = 0;
					npop0 = 0;
					npop1 = 0;
					indat0r = 0;
					indat1r = 0;
					npop0r = 0;
					npop1r = 0;
					if (gender < 2) {
						indat0 += applySuccesrate(
								nDiseaseByAge[0][year][age][gender],
								nDiseaseByAge[thisScen][year][age][gender],
								thisScen, year, age, gender);
						npop0 += applySuccesrate(
								nPopByAge[0][year][age][gender],
								nPopByAge[thisScen][year][age][gender],
								thisScen, year, age, gender);
						indat0r += nDiseaseByAge[0][year][age][gender];
						npop0r += nPopByAge[0][year][age][gender];
					} else {
						indat0 += applySuccesrate(
								nDiseaseByAge[0][year][age][0],
								nDiseaseByAge[thisScen][year][age][0],
								thisScen, year, age, 0);
						npop0 += applySuccesrate(nPopByAge[0][year][age][0],
								nPopByAge[thisScen][year][age][0], thisScen,
								year, age, 0);
						indat1 += applySuccesrate(
								nDiseaseByAge[0][year][age][1],
								nDiseaseByAge[thisScen][year][age][1],
								thisScen, year, age, 1);
						npop1 += applySuccesrate(nPopByAge[0][year][age][1],
								nPopByAge[thisScen][year][age][1], thisScen,
								year, age, 1);
						indat0r += nDiseaseByAge[0][year][age][0];
						npop0r += nPopByAge[0][year][age][0];
						indat1r += nDiseaseByAge[0][year][age][1];
						npop1r += nPopByAge[0][year][age][1];

					}

					if (gender < 2) {
						addToSeries(differencePlot, numbers,
								scenSeries[thisScen], indat0, npop0, indat0r,
								npop0r, age);

					} else {
						addToSeries(differencePlot, numbers,
								scenSeries[thisScen], indat1 + indat0, npop1
										+ npop0, indat1r + indat0r, npop1r
										+ npop0r, age);

					}
				}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);
		}

		JFreeChart chart;
		String type = "incidence";
		if (prevalence)
			type = "prevalence";
		String newcases = "";
		if (!prevalence)
			newcases = "new ";

		String label = "" + (this.output.getStartYear() + year);
		if (gender == 0)
			label = " men; " + (this.output.getStartYear() + year);
		if (gender == 1)
			label = " women; " + (this.output.getStartYear() + year);
		String name = "disability (average dalyweight)";
		if (d < this.output.getNDiseases())
			name = this.output.getDiseaseNames()[d];

		String chartTitle = type + " of " + name;
		String yTitle = "prevalence rate (%)" + name;
		if (!prevalence)
			yTitle = "incidence rate (per 100 py)" + name;

		if (!numbers && differencePlot)
			chartTitle = "excess " + type + " of " + name
					+ " compared to ref scenario";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + newcases + name
					+ " compared to ref scenario";
		if (numbers && !differencePlot && gender == 0)
			chartTitle = "number of men with " + newcases + name;
		if (numbers && !differencePlot && gender == 1)
			chartTitle = "number of women with " + newcases + name;

		if (numbers && !differencePlot && gender == 2)
			chartTitle = "number of persons with " + newcases + name;

		if (differencePlot && !numbers)
			yTitle = "excess " + type + " rate (%) " + name;
		if (!prevalence && differencePlot && !numbers)
			yTitle = "excess " + type + " rate (per 100 py) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + newcases + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + newcases + name;

		chart = ChartFactory.createXYLineChart(chartTitle, "age", yTitle,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getMaxAgeInSimulation() == this.output
				.getMinAgeInSimulation())
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;
	}

	/**
	 * method that averages the prevalence over all ages for a particular
	 * scenario
	 * 
	 * @param thisScen
	 * 
	 * @param year
	 *            * @param d : disease number
	 * @param gender
	 *            : gender: 0=male, 1=female, 2=both
	 * @param numbers
	 *            : if true, return numbers in stead of prevalence
	 * @return prevalence averaged over all age groups
	 * 
	 */
	private double calculateAveragePrevalence(int thisScen, int year, int d,
			int gender, boolean numbers, boolean prevalence) {

		double nominator = 0;
		double denominator = 0;
		double[][][] nPopByAge = this.output.getNPopByAge(year);
		double[][][] nDiseaseByAge = null;
		if (d < this.output.getNDiseases() && prevalence)
			nDiseaseByAge = this.output.getNDiseaseByAge(d, year);
		else if (prevalence)
			nDiseaseByAge = this.output.getNDisabledByAge(year);
		else
			nDiseaseByAge = this.output.getNewCasesByAge(d, year);
		nominator = 0;
		denominator = 0;
		if (gender < 2) {
			for (int age = 0; age < 96 + this.output.getStepsInRun(); age++) {
				nominator += applySuccesrate(nDiseaseByAge[0][age][gender],
						nDiseaseByAge[thisScen][age][gender], thisScen, year,
						age, gender);
				denominator += applySuccesrate(nPopByAge[0][age][gender],
						nPopByAge[thisScen][age][gender], thisScen, year, age,
						gender);

			}
		} else {
			for (int age = 0; age < 96 + this.output.getStepsInRun(); age++) {
				nominator += applySuccesrate(nDiseaseByAge[0][age][0],
						nDiseaseByAge[thisScen][age][0], thisScen, year, age, 0)
						+ applySuccesrate(nDiseaseByAge[0][age][1],
								nDiseaseByAge[thisScen][age][1], thisScen,
								year, age, 1);
				;
				denominator += applySuccesrate(nPopByAge[0][age][0],
						nPopByAge[thisScen][age][0], thisScen, year, age, 0)
						+ applySuccesrate(nPopByAge[0][age][1],
								nPopByAge[thisScen][age][1], thisScen, year,
								age, 1);
			}

		}
		/* added to get rid of numerical error */
		if (nominator < 0.1)
			nominator = Math.round(nominator);
		if (denominator > 0 && !numbers)
			nominator = 100 * nominator / denominator;

		if (denominator == 0)
			nominator = 0;

		return nominator;
	}

	/**
	 * method that averages the prevalence over all ages
	 * 
	 * @param thisScen
	 * @param d
	 *            : disease number *
	 * @param r
	 *            : riskfactor class
	 * @param steps
	 * @param numbers
	 *            : (boolean) indicates whether prevalences should be returned
	 *            or absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return prevalence IN PERCENT averaged over all age groups, or absolute
	 *         numbers
	 */
	private double calculateAveragePrevalenceByRiskClass(int thisScen,
			int steps, int d, int r, int gender, boolean numbers,
			boolean prevalence) {
		double nominator = 0;
		double denominator = 0;
		denominator = 0;
		double[][][][] nDiseaseByRiskClassByAge = null;
		if (d < this.output.getNDiseases() && prevalence)
			nDiseaseByRiskClassByAge = this.output.getNDiseaseByRiskClassByAge(
					d, steps);
		else if (prevalence)
			nDiseaseByRiskClassByAge = this.output
					.getNDisabledByRiskClassByAge(steps);

		else
			nDiseaseByRiskClassByAge = this.output.getNewCasesByRiskClassByAge(
					d, steps);

		if (gender < 2) {
			for (int age = 0; age < 96 + this.output.getStepsInRun(); age++) {
				if (d >= 0)
					nominator += applySuccesrate(
							nDiseaseByRiskClassByAge[0][r][age][gender],
							nDiseaseByRiskClassByAge[thisScen][r][age][gender],
							thisScen, steps, age, gender);
				denominator += applySuccesrate(
						this.output.getNPopByRiskClassByAge()[0][steps][r][age][gender],
						this.output.getNPopByRiskClassByAge()[thisScen][steps][r][age][gender],
						thisScen, steps, age, gender);
			}
		} else {
			for (int age = 0; age < 96 + this.output.getStepsInRun(); age++) {
				nominator += applySuccesrate(
						nDiseaseByRiskClassByAge[0][r][age][0],
						nDiseaseByRiskClassByAge[thisScen][r][age][0],
						thisScen, steps, age, 0)
						+ applySuccesrate(
								nDiseaseByRiskClassByAge[0][r][age][1],
								nDiseaseByRiskClassByAge[thisScen][r][age][1],
								thisScen, steps, age, 1);
				;
				denominator += applySuccesrate(
						this.output.getNPopByRiskClassByAge()[0][steps][r][age][0],
						this.output.getNPopByRiskClassByAge()[thisScen][steps][r][age][0],
						thisScen, steps, age, 0)
						+ applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][steps][r][age][1],
								this.output.getNPopByRiskClassByAge()[thisScen][steps][r][age][1],
								thisScen, steps, age, 1);
			}

		}
		/* to get rid of numerical errors */
		if (nominator < 0.1)
			nominator = Math.round(nominator);
		if (denominator > 0 && !numbers)
			nominator = 100 * nominator / denominator;
		else if (denominator == 0)
			nominator = 0;
		/*
		 * denominator new DynamoOutdenominator
		 * "zero persons in initial population for risk factor class: " +r+
		 * "  no disease " + "prevalence can be calculated ");
		 */
		return nominator;
	}

	/**
	 * makes prevalence plot by riskfactor for scenario thisScen
	 * 
	 * @param gender
	 *            : gender (0=men, 1=women, 2=both)
	 * 
	 * @param thisScen
	 *            : scenario number for which to make the plot
	 * @param d
	 *            : disease number
	 * @param differencePlot
	 *            : plot difference between scenario and reference scenario in
	 *            stead of the prevalence for the scenario
	 * @param numbers
	 *            plot absolute numbers in stead of percentages in the
	 *            population
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return plot (JFreeChart)
	 */
	public JFreeChart makeYearDiseaseByRiskFactorPlots(int gender,
			int thisScen, int d, boolean differencePlot, boolean numbers,
			boolean blackAndWhite, boolean prevalence) {
		XYDataset xyDataset = null;

		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
			XYSeries series = null;

			series = new XYSeries(this.output.getRiskClassnames()[r]);

			int plusone = 0;
			if (prevalence)
				plusone = 1;
			for (int steps = 0; steps < this.output.getStepsInRun() + plusone; steps++) { /*
																						 * calculateAveragePrevalenceByRiskClass
																						 * already
																						 * multiplies
																						 * the
																						 * prevalence
																						 * rate
																						 * with
																						 * 100
																						 * to
																						 * get
																						 * percentages
																						 */

				double indat0 = calculateAveragePrevalenceByRiskClass(thisScen,
						steps, d, r, gender, numbers, prevalence);
				double refdat0 = calculateAveragePrevalenceByRiskClass(0,
						steps, d, r, gender, numbers, prevalence);
				if (!differencePlot)
					series.add((double) steps + this.output.getStartYear(),
							indat0);
				if (differencePlot)
					series.add((double) steps + this.output.getStartYear(),
							(indat0 - refdat0));

			}
			if (r == 0)
				xyDataset = new XYSeriesCollection(series);

			if (r > 0)
				((XYSeriesCollection) xyDataset).addSeries(series);

		}
		String label = this.output.getScenarioNames()[thisScen];
		if (gender == 0)
			label = "men; " + this.output.getScenarioNames()[thisScen];
		if (gender == 1)
			label = "women; " + this.output.getScenarioNames()[thisScen];
		String name = "disability (average dalyweight)";
		if (d < this.output.getNDiseases())
			name = this.output.getDiseaseNames()[d];
		String chartTitle = "prevalence of " + name;

		if (!prevalence)
			chartTitle = "incidence of " + name;
		String yTitle = "prevalence rate (%)" + name;

		if (prevalence) {
			if (numbers && differencePlot)
				chartTitle = "excess numbers of " + name
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "excess prevalence of " + name
						+ " compared to ref scenario";
			if (numbers && !differencePlot && gender == 0)
				chartTitle = "number of men with " + name;
			if (numbers && !differencePlot && gender == 1)
				chartTitle = "number of women with " + name;
			if (numbers && !differencePlot && gender == 2)
				chartTitle = "number of persons with " + name;
			if (differencePlot && !numbers)
				yTitle = "excess prevalence rate (%) " + name;
			if (!differencePlot && numbers)
				yTitle = "number with " + name;
			if (differencePlot && numbers)
				yTitle = "excess number with " + name;
		} else {
			if (numbers && differencePlot)
				chartTitle = "excess new cases of " + name
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "excess incidence rate of " + name
						+ " compared to ref scenario";
			if (numbers && !differencePlot && gender == 0)
				chartTitle = "number of new male cases of " + name;
			if (numbers && !differencePlot && gender == 1)
				chartTitle = "number of new female cases of " + name;
			if (numbers && !differencePlot && gender == 2)
				chartTitle = "number of new cases of " + name;
			yTitle = "incidence rate (per 100 py)" + name;
			if (differencePlot && !numbers)
				yTitle = "excess incidence rate (per 100 py) " + name;
			if (!differencePlot && numbers)
				yTitle = "new cases of " + name;
			if (differencePlot && numbers)
				yTitle = "excess new cases of " + name;
		}

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
			setLineProperties(renderer, r, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * makes mortality plot by riskfactor for scenario thisScen
	 * 
	 * @param gender
	 *            : gender (0=men, 1=women, 2=both)
	 * 
	 * @param thisScen
	 *            : scenario number for which to make the plot
	 * 
	 * @param differencePlot
	 *            : plot difference between scenario and reference scenario in
	 *            stead of the prevalence for the scenario
	 * @param numbers
	 *            plot absolute numbers in stead of percentages in the
	 *            population
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * 
	 * @return plot (JFreeChart)
	 */
	public JFreeChart makeYearMortalityByRiskFactorPlots(int gender,
			int thisScen, boolean differencePlot, boolean numbers,
			boolean blackAndWhite) {
		XYDataset xyDataset = null;
		double[][][][] nMortalityByRiskClassByAge = this.output.mortalityByRiskClassByAge[thisScen];
		double[][][][] nMortalityByRiskClassByAger = this.output.mortalityByRiskClassByAge[0];

		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
			XYSeries series = null;

			series = new XYSeries(this.output.getRiskClassnames()[r]);

			for (int year = 0; year < this.output.getStepsInRun(); year++) { /*
																			 * calculateAveragePrevalenceByRiskClass
																			 * already
																			 * multiplies
																			 * the
																			 * prevalence
																			 * rate
																			 * with
																			 * 100
																			 * to
																			 * get
																			 * percentages
																			 */

				double mendat = 0;
				double womendat = 0;
				double mendatr = 0;
				double womendatr = 0;

				double menpop = 0;
				double womenpop = 0;
				double menpopr = 0;
				double womenpopr = 0;

				for (int age = 0; age < this.output.nDim; age++) {
					mendat += applySuccesrate(
							nMortalityByRiskClassByAger[year][r][age][0],
							nMortalityByRiskClassByAge[year][r][age][0],
							thisScen, year, age, 0);

					menpop += applySuccesrate(
							this.output.getNPopByRiskClassByAge()[0][year][r][age][0],
							this.output.getNPopByRiskClassByAge()[thisScen][year][r][age][0],
							thisScen, year, age, 0);
					mendatr += nMortalityByRiskClassByAger[year][r][age][0];
					menpopr += this.output.getNPopByRiskClassByAge()[0][year][r][age][0];

					womendat += applySuccesrate(
							nMortalityByRiskClassByAger[year][r][age][1],
							nMortalityByRiskClassByAge[year][r][age][1],
							thisScen, year, age, 1);
					womenpop += applySuccesrate(
							this.output.getNPopByRiskClassByAge()[0][year][r][age][1],
							this.output.getNPopByRiskClassByAge()[thisScen][year][r][age][1],
							thisScen, year, age, 1);
					womendatr += nMortalityByRiskClassByAger[year][r][age][1];
					womenpopr += this.output.getNPopByRiskClassByAge()[0][year][r][age][1];

				}
				if (numbers) {
					if (gender == 0) {
						if (!differencePlot)
							series.add((double) year
									+ this.output.getStartYear(), mendat);
						if (differencePlot)
							series.add((double) year
									+ this.output.getStartYear(),
									(mendat - mendatr));
					} else if (gender == 1) {
						if (!differencePlot)
							series.add((double) year
									+ this.output.getStartYear(), womendat);
						if (differencePlot)
							series.add((double) year
									+ this.output.getStartYear(),
									(womendat - womendatr));
					}

					else if (gender == 2) {
						if (!differencePlot)
							series.add((double) year
									+ this.output.getStartYear(), mendat
									+ womendat);
						if (differencePlot)
							series.add((double) year
									+ this.output.getStartYear(), (mendat
									+ womendat - mendatr - womendatr));
					}
				} else {
					if (gender == 0) {
						if (!differencePlot && menpop != 0)
							series.add((double) year
									+ this.output.getStartYear(), 100 * mendat
									/ menpop);
						if (differencePlot && menpop != 0 && menpopr != 0)
							series.add((double) year
									+ this.output.getStartYear(), 100 * (mendat
									/ menpop - mendatr / menpopr));
					} else if (gender == 1) {
						if (!differencePlot && womenpop != 0)
							series.add((double) year
									+ this.output.getStartYear(), 100
									* womendat / womenpop);
						if (differencePlot && womenpop != 0 && womenpopr != 0)
							series.add((double) year
									+ this.output.getStartYear(),
									100 * (womendat / womenpop - womendatr
											/ womenpopr));
					}

					else if (gender == 2) {
						if (!differencePlot && (menpop + womenpop) != 0)
							series
									.add((double) year
											+ this.output.getStartYear(), 100
											* (mendat + womendat)
											/ (menpop + womenpop));
						if (differencePlot && (menpop + womenpop) != 0
								&& (menpopr + womenpopr) != 0)
							series
									.add(
											(double) year
													+ this.output
															.getStartYear(),
											100 * ((mendat + womendat)
													/ (menpop + womenpop) - (mendatr - womendatr)
													/ (menpopr + womenpopr)));
					}
				}

			}
			if (r == 0)
				xyDataset = new XYSeriesCollection(series);

			if (r > 0)
				((XYSeriesCollection) xyDataset).addSeries(series);

		}
		String label = this.output.getScenarioNames()[thisScen];
		if (gender == 0)
			label = "men; " + this.output.getScenarioNames()[thisScen];
		if (gender == 1)
			label = "women; " + this.output.getScenarioNames()[thisScen];

		String chartTitle = "mortality ";

		String yTitle = "mortality rate (per 100 py)";

		if (numbers && differencePlot)
			chartTitle = "excess number of deaths"
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of deaths"
					+ " compared to ref scenario";
		if (numbers && !differencePlot && gender == 0)
			chartTitle = "number of men dying";
		if (numbers && !differencePlot && gender == 1)
			chartTitle = "number of women dying";
		if (numbers && !differencePlot && gender == 2)
			chartTitle = "number of persons dying";

		if (differencePlot && !numbers)
			yTitle = "excess mortality rate (per 100 py)";
		if (!differencePlot && numbers)
			yTitle = "number of deaths";
		if (differencePlot && numbers)
			yTitle = "excess number of deaths";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
			setLineProperties(renderer, r, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;

	}

	/**
	 * set the line properties for series seriesNumber. If blackAndWhite =true,
	 * the first 12 series are plotted in black or gray, and in dashed forms
	 * 
	 * @param renderer
	 * @param seriesNumber
	 * @param blackAndWhite
	 */
	private void setLineProperties(XYLineAndShapeRenderer renderer,
			int seriesNumber, boolean blackAndWhite) {
		renderer.setSeriesStroke(seriesNumber, new BasicStroke(2.0f));
		renderer.setSeriesShapesVisible(seriesNumber, false);
		/*
		 * if black and white than we use 0: dotted black 1: long dash black 2:
		 * solid black 3: dotted gray 4: long dash gray 5: solid gray 6: short
		 * dash black 7: dotted dash black 8: short dash gray 9: dotted dash
		 * gray 10: repeat of 0 11: repeat of 1 12: repeat of 2 etc.
		 */

		if (blackAndWhite) {

			renderer.setDrawSeriesLineAsPath(true);
			int seriesType = (int) (seriesNumber - Math
					.floor(seriesNumber / 10) * 10);
			switch (seriesType) {
			case 0:
				renderer.setSeriesPaint(seriesNumber, Color.BLACK);
				renderer.setSeriesStroke(seriesNumber, DOTTED);
				break;
			case 1:
				renderer.setSeriesPaint(seriesNumber, Color.BLACK);
				renderer.setSeriesStroke(seriesNumber, VERYSHORT_DASH);
				break;
			case 2:
				renderer.setSeriesPaint(seriesNumber, Color.BLACK);
				renderer.setSeriesStroke(seriesNumber, SOLID);
				break;
			case 3:
				renderer.setSeriesPaint(seriesNumber, Color.GRAY);
				renderer.setSeriesStroke(seriesNumber, DOTTED);
				break;
			case 4:
				renderer.setSeriesPaint(seriesNumber, Color.GRAY);
				renderer.setSeriesStroke(seriesNumber, VERYSHORT_DASH);
				break;
			case 5:
				renderer.setSeriesPaint(seriesNumber, Color.GRAY);
				renderer.setSeriesStroke(seriesNumber, SOLID);
				break;
			case 6:
				renderer.setSeriesPaint(seriesNumber, Color.BLACK);
				renderer.setSeriesStroke(seriesNumber, DOTTED_DASH);
				break;
			case 7:
				renderer.setSeriesPaint(seriesNumber, Color.BLACK);
				renderer.setSeriesStroke(seriesNumber, SHORT_DASH);
				break;
			case 8:
				renderer.setSeriesPaint(seriesNumber, Color.GRAY);
				renderer.setSeriesStroke(seriesNumber, DOTTED_DASH);
				break;
			case 9:
				renderer.setSeriesPaint(seriesNumber, Color.GRAY);
				renderer.setSeriesStroke(seriesNumber, SHORT_DASH);
				break;
			}

		} else {

			switch (seriesNumber) {
			case 0:
				renderer.setSeriesPaint(seriesNumber, Color.BLUE);

				break;
			case 1:
				renderer.setSeriesPaint(seriesNumber, Color.RED);

				break;
			case 2:
				renderer.setSeriesPaint(seriesNumber, Color.MAGENTA);

				break;
			case 3:
				renderer.setSeriesPaint(seriesNumber, Color.GREEN);
				break;
			case 4:
				renderer.setSeriesPaint(seriesNumber, Color.CYAN);
				break;
			case 5:
				renderer.setSeriesPaint(seriesNumber, new Color(0xC0, 0xC0,
						0x00));
				/* dark yellow */
				break;
			case 6:
				renderer.setSeriesPaint(seriesNumber, Color.GRAY);
				break;
			case 7:
				renderer.setSeriesPaint(seriesNumber, new Color(0x60, 0x60,
						0x00));
				/* very dark yellow= greenish */
				break;
			case 8:
				renderer.setSeriesPaint(seriesNumber, Color.BLACK);

				break;
			case 9:
				renderer.setSeriesPaint(seriesNumber, Color.ORANGE);
				break;

			}

		}

	}

	/* the stroke patterns used for black and white lines */
	BasicStroke SOLID = new BasicStroke(2.0f);
	BasicStroke DOTTED_DASH = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1.0f,
			new float[] { 8.0F, 2.0F, 1.0F, 2.0F }, 0.0f);
	BasicStroke DOTTED = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1.0f, new float[] { 1.0F, 4.0F }, 0.0f);
	BasicStroke LONG_DASH = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1.0f, new float[] { 12.0f, 2.0f }, 0.0f);
	BasicStroke SHORT_DASH = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1.0f, new float[] { 8.0f, 2.0f }, 0.0f);
	BasicStroke VERYSHORT_DASH = new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 1.0f, new float[] { 5.0f, 2.0f }, 0.0f);

	/**
	 * makes a plot of the prevalence against year of simulation, or for the
	 * difference in prevalence compared to the reference scenario for disease d
	 * and gender g
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param d
	 *            : diseasenumber
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            : the outcome are numbers in stead of percentages
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreechart plot of the prevalence against year of simulation, or
	 *         for the difference in prevalence compared to the reference
	 *         scenario for disease d and gender g
	 * 
	 */
	public JFreeChart makeYearDiseaseByScenarioPlots(int gender, int d,
			boolean differencePlot, boolean numbers, boolean blackAndWhite,
			boolean prevalence)
	/* throws DynamoOutputException */{
		double[][][][][] nDiseaseByRiskClassByAge = null;
		if (d < this.output.getNDiseases() && prevalence)
			nDiseaseByRiskClassByAge = this.output
					.getNDiseaseByRiskClassByAge(d);
		else if (prevalence)
			nDiseaseByRiskClassByAge = this.output
					.getNDisabledByRiskClassByAge();
		else
			nDiseaseByRiskClassByAge = this.output
					.getNewCasesByRiskClassByAge(d);
		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[this.output.getNScen() + 1];
		for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {
			scenSeries[thisScen] = new XYSeries(
					this.output.getScenarioNames()[thisScen]);

			int plusone = 0;
			if (prevalence)
				plusone = 1;
			for (int steps = 0; steps < this.output.getStepsInRun() + plusone; steps++) {
				double indat = 0;

				double npop = 0;
				double indatr = 0;
				double npopr = 0;
				if (gender < 2) {
					for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
						for (int age = 0; age < 96 + this.output
								.getStepsInRun(); age++) {
							indat += applySuccesrate(
									nDiseaseByRiskClassByAge[0][steps][r][age][gender],
									nDiseaseByRiskClassByAge[thisScen][steps][r][age][gender],
									thisScen, steps, age, gender);
							npop += applySuccesrate(
									this.output.getNPopByRiskClassByAge()[0][steps][r][age][gender],
									this.output.getNPopByRiskClassByAge()[thisScen][steps][r][age][gender],
									thisScen, steps, age, gender);
							indatr += nDiseaseByRiskClassByAge[0][steps][r][age][gender];
							npopr += this.output.getNPopByRiskClassByAge()[0][steps][r][age][gender];
						}
					}
				} else {
					for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {

						for (int age = 0; age < 96 + this.output
								.getStepsInRun(); age++) {
							indat += applySuccesrate(
									nDiseaseByRiskClassByAge[0][steps][r][age][0],
									nDiseaseByRiskClassByAge[thisScen][steps][r][age][0],
									thisScen, steps, age, 0)
									+ applySuccesrate(
											nDiseaseByRiskClassByAge[0][steps][r][age][1],
											nDiseaseByRiskClassByAge[thisScen][steps][r][age][1],
											thisScen, steps, age, 1);
							;
							npop += applySuccesrate(
									this.output.getNPopByRiskClassByAge()[0][steps][r][age][0],
									this.output.getNPopByRiskClassByAge()[thisScen][steps][r][age][0],
									thisScen, steps, age, 0)
									+ applySuccesrate(
											this.output
													.getNPopByRiskClassByAge()[0][steps][r][age][1],
											this.output
													.getNPopByRiskClassByAge()[thisScen][steps][r][age][1],
											thisScen, steps, age, 1);

							indatr += nDiseaseByRiskClassByAge[0][steps][r][age][0]
									+ nDiseaseByRiskClassByAge[0][steps][r][age][1];
							npopr += this.output.getNPopByRiskClassByAge()[0][steps][r][age][0]
									+ this.output.getNPopByRiskClassByAge()[0][steps][r][age][1];
						}

					}
				}
				addToSeries(differencePlot, numbers, scenSeries[thisScen],
						indat, npop, indatr, npopr, steps
								+ this.output.getStartYear());

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}
		String label = "";

		String type = "prevalence";
		if (!prevalence)
			type = "incidence";

		if (gender == 0)
			label = "men";
		if (gender == 1)
			label = "women";
		String name = "disability (average dalyweight)";
		if (d < this.output.getNDiseases())
			name = this.output.getDiseaseNames()[d];

		String chartTitle = type + " of " + name;

		if (numbers && differencePlot)
			chartTitle = "excess numbers of " + name
					+ " compared to ref scenario";
		if (!prevalence && numbers && differencePlot)
			chartTitle = "excess new cases of " + name
					+ " compared to ref scenario";
		if (!numbers && differencePlot)

			chartTitle = "excess " + type + " of " + name

			+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of persons with " + name;
		if (!prevalence && numbers && !differencePlot)
			chartTitle = "number of new cases with " + name;
		String yTitle = "prevalence rate (%) " + name;

		if (!prevalence)
			yTitle = "incidence rate (per 100 py) " + name;

		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) " + name;
		if (!prevalence && differencePlot && !numbers)
			yTitle = "excess incidence rate (per 100 py) " + name;
		if (!differencePlot && numbers)
			yTitle = "number with " + name;
		if (!prevalence && !differencePlot && numbers)
			yTitle = "new cases with " + name;
		if (differencePlot && numbers)
			yTitle = "excess number with " + name;
		if (!prevalence && differencePlot && numbers)
			yTitle = "excess new cases with " + name;

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * makes a plot of the prevalence against age, or for the difference in
	 * prevalence compared to the reference scenario for disease d and gender g
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param thisScen
	 *            : scenario for which to plot
	 * @param d
	 *            : diseasenumber. 0=first disease, last number (=nDiseases) is
	 *            disability
	 * @param year
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            : the outcome are numbers in stead of percentages
	 * 
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot
	 * 
	 */
	public JFreeChart makeAgeDiseaseByRiskFactorPlots(int gender, int thisScen,
			int d, int year, boolean differencePlot, boolean numbers,
			boolean blackAndWhite, boolean prevalence) {
		XYDataset xyDataset = null;
		double[][][][] nDiseaseByRiskClassByAge = null;
		if (d < this.output.getNDiseases() && prevalence)
			nDiseaseByRiskClassByAge = this.output.getNDiseaseByRiskClassByAge(
					d, year);
		else if (prevalence)
			nDiseaseByRiskClassByAge = this.output
					.getNDisabledByRiskClassByAge(year);
		else
			nDiseaseByRiskClassByAge = this.output.getNewCasesByRiskClassByAge(
					d, year);
		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {

			XYSeries menSeries = new XYSeries(
					this.output.getRiskClassnames()[r]);
			XYSeries womenSeries = new XYSeries(
					this.output.getRiskClassnames()[r]);
			XYSeries totSeries = new XYSeries(
					this.output.getRiskClassnames()[r]);
			double mendat = 0;
			double womendat = 0;
			double menpop = 0;
			double womenpop = 0;
			double mendatr = 0;
			double womendatr = 0;
			double menpopr = 0;
			double womenpopr = 0;
			if (prevalence || year < this.output.getStepsInRun())
				if (prevalence || year < this.output.getStepsInRun())
					for (int age = 0; age < 96/* + this.output.getStepsInRun() */; age++) {

						mendat = applySuccesrate(
								nDiseaseByRiskClassByAge[0][r][age][0],
								nDiseaseByRiskClassByAge[thisScen][r][age][0],
								thisScen, year, age, 0);

						menpop = applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][year][r][age][0],
								this.output.getNPopByRiskClassByAge()[thisScen][year][r][age][0],
								thisScen, year, age, 0);
						mendatr = nDiseaseByRiskClassByAge[0][r][age][0];
						menpopr = this.output.getNPopByRiskClassByAge()[0][year][r][age][0];
						addToSeries(differencePlot, numbers, menSeries, mendat,
								menpop, mendatr, menpopr, age);

						womendat = applySuccesrate(
								nDiseaseByRiskClassByAge[0][r][age][1],
								nDiseaseByRiskClassByAge[thisScen][r][age][1],
								thisScen, year, age, 1);
						womenpop = applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][year][r][age][1],
								this.output.getNPopByRiskClassByAge()[thisScen][year][r][age][1],
								thisScen, year, age, 1);
						womendatr = nDiseaseByRiskClassByAge[0][r][age][1];
						womenpopr = this.output.getNPopByRiskClassByAge()[0][year][r][age][1];

						addToSeries(differencePlot, numbers, womenSeries,
								womendat, womenpop, womendatr, womenpopr, age);
						addToSeries(differencePlot, numbers, totSeries,
								womendat + mendat, womenpop + menpop, womendatr
										+ mendatr, womenpopr + menpopr, age);

					}
			switch (gender) {
			case 0:
				if (r == 0)
					xyDataset = new XYSeriesCollection(menSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(menSeries);
				break;
			case 1:
				if (r == 0)
					xyDataset = new XYSeriesCollection(womenSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(womenSeries);
				break;
			case 2:
				if (r == 0)
					xyDataset = new XYSeriesCollection(totSeries);
				else
					((XYSeriesCollection) xyDataset).addSeries(totSeries);
				break;
			}

		}
		String label = this.output.getScenarioNames()[thisScen] + "; "
				+ (this.output.getStartYear() + year);
		if (gender == 0)
			label = "men; " + this.output.getScenarioNames()[thisScen] + "; "
					+ (this.output.getStartYear() + year);

		if (gender == 1)
			label = "women; " + this.output.getScenarioNames()[thisScen] + "; "
					+ (this.output.getStartYear() + year);
		String name = "disability (average dalyweight)";
		if (d < this.output.getNDiseases())
			name = this.output.getDiseaseNames()[d];

		String chartTitle = "Prevalence of " + name;
		String yTitle = "prevalence rate (%) " + name;

		if (prevalence) {
			chartTitle = "Prevalence of " + name;
			if (numbers && differencePlot)
				chartTitle = "Excess numbers of " + name
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "Excess prevalence of " + name
						+ " compared to ref scenario";
			if (numbers && !differencePlot)
				if (gender == 0)
					chartTitle = "Number of men with " + name;
				else if (gender == 1)
					chartTitle = "Number of women with " + name;
				else
					chartTitle = "Number of persons with " + name;
		} else {
			chartTitle = "Incidence of " + name;
			yTitle = "incidence rate (per 100 py) " + name;

			if (numbers && differencePlot)
				chartTitle = "Excess new cases of " + name
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "Excess incidence of " + name
						+ " compared to ref scenario";
			if (numbers && !differencePlot)
				if (gender == 0)
					chartTitle = "Number of new male cases of " + name;
				else if (gender == 1)
					chartTitle = "Number of new female cases of " + name;
				else
					chartTitle = "Number of new cases of " + name;

			if (differencePlot && !numbers)
				yTitle = "excess incidence rate (%) " + name;
			if (!differencePlot && numbers)
				yTitle = "new cases of " + name;
			if (differencePlot && numbers)
				yTitle = "excess new cases of " + name;
		}

		if (prevalence) {
			if (differencePlot && !numbers)
				yTitle = "excess prevalence rate (%) " + name;
			if (!differencePlot && numbers)
				yTitle = "number with " + name;
			if (differencePlot && numbers)
				yTitle = "excess number with " + name;
		} else {
			chartTitle = "Incidence of " + name;
			yTitle = "incidence rate (per 100 py) " + name;

			if (numbers && differencePlot)
				chartTitle = "Excess new cases of " + name
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "Excess incidence of " + name
						+ " compared to ref scenario";
			if (numbers && !differencePlot)
				if (gender == 0)
					chartTitle = "Number of new male cases of " + name;
				else if (gender == 1)
					chartTitle = "Number of new female cases of " + name;
				else
					chartTitle = "Number of new cases of " + name;

			if (differencePlot && !numbers)
				yTitle = "excess incidence rate (%) " + name;
			if (!differencePlot && numbers)
				yTitle = "new cases of " + name;
			if (differencePlot && numbers)
				yTitle = "excess new cases of " + name;
		}

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "age",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getMaxAgeInSimulation() == this.output
				.getMinAgeInSimulation())
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
			setLineProperties(renderer, r, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);
		return chart;
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * makes a plot of the mortality against age, or for the difference in
	 * prevalence compared to the reference scenario for disease d and gender g
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param thisScen
	 *            : scenario for which to plot
	 * 
	 * @param year
	 * @param differencePlot
	 *            (boolean) true if the difference with the reference scenario
	 *            should be plotted
	 * @param numbers
	 *            : the outcome are numbers in stead of percentages
	 * 
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot
	 * 
	 */
	public JFreeChart makeAgeMortalityByRiskFactorPlots(int gender,
			int thisScen, int year, boolean differencePlot, boolean numbers,
			boolean blackAndWhite) {
		XYDataset xyDataset = null;
		// arrays with mortality of thisscen and of reference scenario
		double[][][][] nMortalityByRiskClassByAge = this.output.mortalityByRiskClassByAge[thisScen];
		double[][][][] nMortalityByRiskClassByAger = this.output.mortalityByRiskClassByAge[0];
		if (year < this.output.getStepsInRun()) {
			for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {

				XYSeries menSeries = new XYSeries(this.output
						.getRiskClassnames()[r]);
				XYSeries womenSeries = new XYSeries(this.output
						.getRiskClassnames()[r]);
				XYSeries totSeries = new XYSeries(this.output
						.getRiskClassnames()[r]);
				double mendat = 0;
				double womendat = 0;
				double menpop = 0;
				double womenpop = 0;
				double mendatr = 0;
				double womendatr = 0;
				double menpopr = 0;
				double womenpopr = 0;
				if (year < this.output.getStepsInRun())
					for (int age = 0; age < 96; age++) {

						mendat = applySuccesrate(
								nMortalityByRiskClassByAger[year][r][age][0],
								nMortalityByRiskClassByAge[year][r][age][0],
								thisScen, year, age, 0);

						menpop = applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][year][r][age][0],
								this.output.getNPopByRiskClassByAge()[thisScen][year][r][age][0],
								thisScen, year, age, 0);
						mendatr = nMortalityByRiskClassByAger[year][r][age][0];
						menpopr = this.output.getNPopByRiskClassByAge()[0][year][r][age][0];
						addToSeries(differencePlot, numbers, menSeries, mendat,
								menpop, mendatr, menpopr, age);

						womendat = applySuccesrate(
								nMortalityByRiskClassByAger[year][r][age][1],
								nMortalityByRiskClassByAge[year][r][age][1],
								thisScen, year, age, 1);
						womenpop = applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][year][r][age][1],
								this.output.getNPopByRiskClassByAge()[thisScen][year][r][age][1],
								thisScen, year, age, 1);
						womendatr = nMortalityByRiskClassByAger[year][r][age][1];
						womenpopr = this.output.getNPopByRiskClassByAge()[0][year][r][age][1];

						addToSeries(differencePlot, numbers, womenSeries,
								womendat, womenpop, womendatr, womenpopr, age);
						addToSeries(differencePlot, numbers, totSeries,
								womendat + mendat, womenpop + menpop, womendatr
										+ mendatr, womenpopr + menpopr, age);

					}
				switch (gender) {
				case 0:
					if (r == 0)
						xyDataset = new XYSeriesCollection(menSeries);
					else
						((XYSeriesCollection) xyDataset).addSeries(menSeries);
					break;
				case 1:
					if (r == 0)
						xyDataset = new XYSeriesCollection(womenSeries);
					else
						((XYSeriesCollection) xyDataset).addSeries(womenSeries);
					break;
				case 2:
					if (r == 0)
						xyDataset = new XYSeriesCollection(totSeries);
					else
						((XYSeriesCollection) xyDataset).addSeries(totSeries);
					break;
				}

			}
			String label = this.output.getScenarioNames()[thisScen] + "; "
					+ (this.output.getStartYear() + year);
			if (gender == 0)
				label = "men; " + this.output.getScenarioNames()[thisScen]
						+ "; " + (this.output.getStartYear() + year);

			if (gender == 1)
				label = "women; " + this.output.getScenarioNames()[thisScen]
						+ "; " + (this.output.getStartYear() + year);

			String chartTitle = "Mortality";
			String yTitle = "Mortality rate (per 100 py) ";

			if (numbers && differencePlot)
				chartTitle = "Excess mortality numbers  "
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "Excess mortality " + " compared to ref scenario";
			if (numbers && !differencePlot)
				if (gender == 0)
					chartTitle = "Number of men dying ";
				else if (gender == 1)
					chartTitle = "Number of women dying";
				else
					chartTitle = "Number of persons dying";

			if (differencePlot && !numbers)
				yTitle = "excess mortality rate (per 100 py) ";
			if (!differencePlot && numbers)
				yTitle = "number of deaths";
			if (differencePlot && numbers)
				yTitle = "excess number of deaths";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			if (this.output.getMaxAgeInSimulation() == this.output
					.getMinAgeInSimulation())
				drawMarkers(chart);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
				setLineProperties(renderer, r, blackAndWhite);
			}
			plot.setRenderer(renderer);
			XYItemRenderer renderer2 = plot.getRenderer();
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			plot.setRenderer(renderer2);
			return chart;
		} else
			return makeEmptyPlot();
		/*
		 * ChartFrame frame1 = new ChartFrame(diseaseNames[d] +
		 * " prevalence by risk factor", chart); frame1.setVisible(true);
		 * frame1.setSize(300, 300);
		 * 
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try {
		 * 
		 * ChartUtilities.saveChartAsJPEG(new File(baseDir + File.separator +
		 * "simulations" + File.separator + simulationName + File.separator +
		 * "results" + File.separator + "chartPrevalenceByRiskClass" + d +
		 * "scen" + thisScen + ".jpg"), chart, 500, 300); } catch (Exception e)
		 * { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for diseasenumber" + d); }
		 */

	}

	/**
	 * plot riskfactorclass data for a single scenario separate for men and
	 * women resultsObject
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return :plot of riskfactorclass data for a single scenario separate for
	 *         men and women
	 */
	public JFreeChart makeYearRiskFactorByScenarioPlot(int gender,
			int riskClass, boolean differencePlot, boolean numbers,
			boolean blackAndWhite) {

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[this.output.getNScen() + 1];
		double[][][][] nPopByAge = this.output.getNPopByAge();
		for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(
					this.output.getScenarioNames()[thisScen]);

			for (int steps = 0; steps < this.output.getStepsInRun() + 1; steps++) {
				double indat = 0;
				double denominator = 0;
				double indatr = 0;
				double denominatorr = 0;
				int nDimLocal = nPopByAge[0][0].length;
				for (int age = 0; age < nDimLocal; age++) {
					if (gender < 2) {
						indat += applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][steps][riskClass][age][gender],
								this.output.getNPopByRiskClassByAge()[thisScen][steps][riskClass][age][gender],
								thisScen, steps, age, gender);
						denominator += applySuccesrate(
								nPopByAge[0][steps][age][gender],
								nPopByAge[thisScen][steps][age][gender],
								thisScen, steps, age, gender);
						indatr += this.output.getNPopByRiskClassByAge()[0][steps][riskClass][age][gender];
						denominatorr += nPopByAge[0][steps][age][gender];
					} else {
						indat += applySuccesrate(
								this.output.getNPopByRiskClassByAge()[0][steps][riskClass][age][0],
								this.output.getNPopByRiskClassByAge()[thisScen][steps][riskClass][age][0],
								thisScen, steps, age, 0)
								+ applySuccesrate(
										this.output.getNPopByRiskClassByAge()[0][steps][riskClass][age][1],
										this.output.getNPopByRiskClassByAge()[thisScen][steps][riskClass][age][1],
										thisScen, steps, age, 1);
						;
						denominator += applySuccesrate(
								nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age, 0)
								+ applySuccesrate(nPopByAge[0][steps][age][1],
										nPopByAge[thisScen][steps][age][1],
										thisScen, steps, age, 1);
						indatr += this.output.getNPopByRiskClassByAge()[0][steps][riskClass][age][0]
								+ this.output.getNPopByRiskClassByAge()[0][steps][riskClass][age][1];
						denominatorr += nPopByAge[0][steps][age][0]
								+ nPopByAge[0][steps][age][1];
					}

				}
				addToSeries(differencePlot, numbers, scenSeries[thisScen],
						indat, denominator, indatr, denominatorr, steps
								+ this.output.startYear);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}
		String label = "";
		if (gender == 0)
			label = "men";
		if (gender == 1)
			label = "women";
		String chartTitle = "prevalence of "
				+ this.output.getRiskClassnames()[riskClass];
		if (numbers && differencePlot)
			chartTitle = "excess number of "
					+ this.output.getRiskClassnames()[riskClass]
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of "
					+ this.output.getRiskClassnames()[riskClass]
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of "
					+ this.output.getRiskClassnames()[riskClass] + label;
		String yTitle = "prevalence rate (%) "
				+ this.output.getRiskClassnames()[riskClass];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) "
					+ this.output.getRiskClassnames()[riskClass];
		if (!differencePlot && numbers)
			yTitle = "number with "
					+ this.output.getRiskClassnames()[riskClass];
		if (differencePlot && numbers)
			yTitle = "excess number of "
					+ this.output.getRiskClassnames()[riskClass];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "year",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getStepsInRun() == 0)
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
		// frame1.setVisible(true);
		// frame1.setSize(300, 300);
		/*
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try { writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for riskfactor"); }
		 */
		return chart;

	}

	/**
	 * plot riskfactorclass data for a single scenario separate for men and
	 * women
	 * 
	 * @param year
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            : riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot of riskfactorclass data for a single scenario
	 *         separate for men and women
	 */
	public JFreeChart makeAgeRiskFactorByScenarioPlot(int year, int gender,
			int riskClass, boolean differencePlot, boolean numbers,
			boolean blackAndWhite) {

		XYDataset xyDataset = null;
		XYSeries[] scenSeries = new XYSeries[this.output.getNScen() + 1];
		double[][][][] nPopByAge = this.output.getNPopByAge();
		for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(
					this.output.getScenarioNames()[thisScen]);

			double indat = 0;
			double denominator = 0;
			double indatR = 0;
			double denominatorR = 0;

			for (int age = 0; age < Math.min(this.output
					.getMaxAgeInSimulation()
					+ year, 96); age++) {
				if (gender < 2) {
					indat = applySuccesrate(
							this.output.getNPopByRiskClassByAge()[0][year][riskClass][age][gender],
							this.output.getNPopByRiskClassByAge()[thisScen][year][riskClass][age][gender],
							thisScen, year, age, gender);
					denominator = applySuccesrate(
							nPopByAge[0][year][age][gender],
							nPopByAge[thisScen][year][age][gender], thisScen,
							year, age, gender);
					indatR = this.output.getNPopByRiskClassByAge()[0][year][riskClass][age][gender];
					denominatorR = nPopByAge[0][year][age][gender];
				} else {
					indat = applySuccesrate(
							this.output.getNPopByRiskClassByAge()[0][year][riskClass][age][0],
							this.output.getNPopByRiskClassByAge()[thisScen][year][riskClass][age][0],
							thisScen, year, age, 0)
							+ applySuccesrate(
									this.output.getNPopByRiskClassByAge()[0][year][riskClass][age][1],
									this.output.getNPopByRiskClassByAge()[thisScen][year][riskClass][age][1],
									thisScen, year, age, 1);
					;
					denominator = applySuccesrate(nPopByAge[0][year][age][0],
							nPopByAge[thisScen][year][age][0], thisScen, year,
							age, 0)
							+ applySuccesrate(nPopByAge[0][year][age][1],
									nPopByAge[thisScen][year][age][1],
									thisScen, year, age, 1);
					indatR = this.output.getNPopByRiskClassByAge()[0][year][riskClass][age][0]
							+ this.output.getNPopByRiskClassByAge()[0][year][riskClass][age][1];
					denominatorR = nPopByAge[0][year][age][0]
							+ nPopByAge[0][year][age][1];
				}
				addToSeries(differencePlot, numbers, scenSeries[thisScen],
						indat, denominator, indatR, denominatorR, age);

			}
			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		}
		String label = " " + (this.output.getStartYear() + year);
		if (gender == 0)
			label = "men; " + (this.output.getStartYear() + year);
		if (gender == 1)
			label = "women; " + (this.output.getStartYear() + year);

		String chartTitle = "prevalence of "
				+ this.output.getRiskClassnames()[riskClass];
		if (numbers && differencePlot)
			chartTitle = "excess number of "
					+ this.output.getRiskClassnames()[riskClass]
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess prevalence of "
					+ this.output.getRiskClassnames()[riskClass]
					+ " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of "
					+ this.output.getRiskClassnames()[riskClass];
		chartTitle = chartTitle + "; " + label;
		String yTitle = "prevalence rate (%) "
				+ this.output.getRiskClassnames()[riskClass];
		if (differencePlot && !numbers)
			yTitle = "excess prevalence rate (%) "
					+ this.output.getRiskClassnames()[riskClass];
		if (!differencePlot && numbers)
			yTitle = "number with "
					+ this.output.getRiskClassnames()[riskClass];
		if (differencePlot && numbers)
			yTitle = "excess number of "
					+ this.output.getRiskClassnames()[riskClass];

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "age",
				yTitle, xyDataset, PlotOrientation.VERTICAL, true, true, false);
		if (this.output.getMaxAgeInSimulation() == this.output
				.getMinAgeInSimulation())
			drawMarkers(chart);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
		// frame1.setVisible(true);
		// frame1.setSize(300, 300);
		/*
		 * final ChartPanel chartPanel = new ChartPanel(chart);
		 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 * 
		 * try { writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for riskfactor"); }
		 */
		return chart;

	}

	/**
	 * plot mean value of riskfactor (continuous only) against age for either
	 * men, women or both by scenario women. Age is age during simulation, not
	 * age at the start of simulation
	 * 
	 * @param year
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * 
	 * @param differencePlot
	 *            : plot difference with reference scenario : plot absolute
	 *            numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot of mean value of riskfactor (continuous only)
	 *         against age for either men, women or both by scenario.
	 */
	public JFreeChart makeAgeMeanRiskFactorByScenarioPlot(int year, int gender,
			boolean differencePlot, boolean blackAndWhite) {
		if (this.output.getRiskType() == 2) {
			XYDataset xyDataset = null;
			XYSeries[] scenSeries = new XYSeries[this.output.getNScen() + 1];
			double[][][][] nPopByAge = this.output.getNPopByAge();
			for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

				scenSeries[thisScen] = new XYSeries(this.output
						.getScenarioNames()[thisScen]);

				double indat = 0;

				double indatR = 0;
				boolean dataPresent = true;
				int nDimLocal = Math.min(95, nPopByAge[0][0].length);
				for (int age = 0; age < nDimLocal; age++) {
					if (gender < 2) {
						indat = applySuccesrateToMean(
								this.output.getMeanRiskByAge()[0][year][age][gender],
								this.output.getMeanRiskByAge()[thisScen][year][age][gender],
								nPopByAge[0][year][age][gender],
								nPopByAge[thisScen][year][age][gender],
								thisScen, year, age, gender);

						indatR = this.output.getMeanRiskByAge()[0][year][age][gender];
						if (applySuccesrate(nPopByAge[0][year][age][gender],
								nPopByAge[thisScen][year][age][gender],
								thisScen, year, age, gender) == 0)
							dataPresent = false;
						if (nPopByAge[0][year][age][gender] == 0
								&& differencePlot)
							dataPresent = false;
						else
							dataPresent = true;
					} else {
						double nMen = applySuccesrate(
								nPopByAge[0][year][age][0],
								nPopByAge[thisScen][year][age][0], thisScen,
								year, age, 0);
						double nWomen = applySuccesrate(
								nPopByAge[0][year][age][1],
								nPopByAge[thisScen][year][age][1], thisScen,
								year, age, 1);
						if (nMen + nWomen == 0)
							dataPresent = false;
						else
							dataPresent = true;
						indat = nMen
								* applySuccesrateToMean(
										this.output.getMeanRiskByAge()[0][year][age][0],
										this.output.getMeanRiskByAge()[thisScen][year][age][0],
										nPopByAge[0][year][age][0],
										nPopByAge[thisScen][year][age][0],
										thisScen, year, age, 0)
								+ nWomen
								* applySuccesrateToMean(
										this.output.getMeanRiskByAge()[0][year][age][1],
										this.output.getMeanRiskByAge()[thisScen][year][age][1],
										nPopByAge[0][year][age][1],
										nPopByAge[thisScen][year][age][1],
										thisScen, year, age, 1);
						if (dataPresent)
							indat = indat / (nMen + nWomen);

						indatR = this.output.getMeanRiskByAge()[0][year][age][0]
								* nPopByAge[0][year][age][0]
								+ this.output.getMeanRiskByAge()[0][year][age][1]
								* nPopByAge[0][year][age][1];
						if ((nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]) == 0
								&& differencePlot)
							dataPresent = false;
						if (dataPresent)
							indatR = indatR
									/ (nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]);

					}

					if (!differencePlot && dataPresent)
						scenSeries[thisScen].add((double) age, indat);
					if (differencePlot && dataPresent)
						scenSeries[thisScen]
								.add((double) age, (indat - indatR));

				}
				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			}
			String label = "" + (this.output.getStartYear() + year);
			if (gender == 0)
				label = "men; " + (this.output.getStartYear() + year);
			if (gender == 1)
				label = "women; " + (this.output.getStartYear() + year);

			String chartTitle = "mean value of risk factor";
			if (differencePlot)
				chartTitle = " mean value of risk factor (difference with reference scenario)";

			String yTitle = "mean value";
			if (differencePlot)
				yTitle = "difference in mean value";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			if (this.output.getMaxAgeInSimulation() == this.output
					.getMinAgeInSimulation())
				drawMarkers(chart);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
				setLineProperties(renderer, thisScen, blackAndWhite);
			}
			plot.setRenderer(renderer);
			XYItemRenderer renderer2 = plot.getRenderer();
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			plot.setRenderer(renderer2);

			// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
			// frame1.setVisible(true);
			// frame1.setSize(300, 300);
			/*
			 * final ChartPanel chartPanel = new ChartPanel(chart);
			 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			 * 
			 * try { writeCategoryChart(baseDir + File.separator + "simulations"
			 * + File.separator + simulationName + File.separator + "results" +
			 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
			 * (Exception e) { System.out.println(e.getMessage()); System.out
			 * .println("Problem occurred creating chart. for riskfactor"); }
			 */
			return chart;
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	/**
	 * plot mean value of riskfactor (continuous only) against age for either
	 * men, women or both by scenario women. Age is age during simulation, not
	 * age at the start of simulation
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * 
	 * @param differencePlot
	 *            : plot difference with reference scenario : plot absolute
	 *            numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot mean value of riskfactor (continuous only)
	 *         against age for either men, women or both by scenario women. Age
	 *         is age during simulation, not age at the start of simulation
	 */
	public JFreeChart makeYearMeanRiskFactorByScenarioPlot(int gender,
			boolean differencePlot, boolean blackAndWhite) {
		if (this.output.getRiskType() == 2) {
			XYDataset xyDataset = null;
			XYSeries[] scenSeries = new XYSeries[this.output.getNScen() + 1];
			double[][][][] nPopByAge = this.output.getNPopByAge();

			for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {
				scenSeries[thisScen] = new XYSeries(this.output
						.getScenarioNames()[thisScen]);
				for (int year = 0; year < this.output.getStepsInRun() + 1; year++) {

					double indat = 0;
					double weight = 0;
					double indatR = 0;
					double weightR = 0;
					double mean = 0;
					double meanR = 0;
					double sumweight = 0;
					double sumweightR = 0;

					int nDimLocal = nPopByAge[0][0].length;
					for (int age = 0; age < nDimLocal; age++) {
						if (gender < 2) {
							indat = applySuccesrateToMean(
									this.output.getMeanRiskByAge()[0][year][age][gender],
									this.output.getMeanRiskByAge()[thisScen][year][age][gender],
									nPopByAge[0][year][age][gender],
									nPopByAge[thisScen][year][age][gender],
									thisScen, year, age, gender);

							indatR = this.output.getMeanRiskByAge()[0][year][age][gender];
							weight = applySuccesrate(
									nPopByAge[0][year][age][gender],
									nPopByAge[thisScen][year][age][gender],
									thisScen, year, age, gender);
							weightR = nPopByAge[0][year][age][gender];

							if (weight > 0) {
								mean += indat * weight;
								sumweight += weight;
							}
							if (weightR > 0) {
								meanR += indatR * weightR;
								sumweightR += weightR;
							}

						} else {
							double nMen = applySuccesrate(
									nPopByAge[0][year][age][0],
									nPopByAge[thisScen][year][age][0],
									thisScen, year, age, 0);
							double nWomen = applySuccesrate(
									nPopByAge[0][year][age][1],
									nPopByAge[thisScen][year][age][1],
									thisScen, year, age, 1);
							indat = nMen
									* applySuccesrateToMean(
											this.output.getMeanRiskByAge()[0][year][age][0],
											this.output.getMeanRiskByAge()[thisScen][year][age][0],
											nPopByAge[0][year][age][0],
											nPopByAge[thisScen][year][age][0],
											thisScen, year, age, 0)
									+ nWomen
									* applySuccesrateToMean(
											this.output.getMeanRiskByAge()[0][year][age][1],
											this.output.getMeanRiskByAge()[thisScen][year][age][1],
											nPopByAge[0][year][age][1],
											nPopByAge[thisScen][year][age][1],
											thisScen, year, age, 1);
							indatR = this.output.getMeanRiskByAge()[0][year][age][0]
									* nPopByAge[0][year][age][0]
									+ this.output.getMeanRiskByAge()[0][year][age][1]
									* nPopByAge[0][year][age][1];

							indatR = indatR
									/ (nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]);
							indat = indat / (nMen + nWomen);
							weight = nMen + nWomen;
							weightR = (nPopByAge[0][year][age][0] + nPopByAge[0][year][age][1]);
							if (weight > 0) {
								mean += indat * weight;
								sumweight += weight;
							}
							if (Double.isNaN(mean)) {
								int stop = 0;
								stop++;

							}
							if (weightR > 0) {
								meanR += indatR * weightR;
								sumweightR += weightR;
							}

						}
					}
					if (!differencePlot && sumweight > 0)
						scenSeries[thisScen].add((double) year
								+ this.output.startYear, mean / sumweight);
					if (differencePlot && sumweight > 0 && sumweightR > 0)
						scenSeries[thisScen].add((double) year
								+ this.output.startYear, mean / sumweight
								- (meanR / sumweightR));
				}
				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			}
			String label = "";
			if (gender == 0)
				label = "men";
			if (gender == 1)
				label = "women";

			String chartTitle = "mean value of riskFactor";
			if (differencePlot)
				chartTitle = "difference with ref scenario of mean risk factor value";

			chartTitle = chartTitle + label;
			String yTitle = "mean value";
			if (differencePlot)
				yTitle = "difference in mean value";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"year", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			if (this.output.getStepsInRun() == 0)
				drawMarkers(chart);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
				setLineProperties(renderer, thisScen, blackAndWhite);
			}
			plot.setRenderer(renderer);
			XYItemRenderer renderer2 = plot.getRenderer();
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			plot.setRenderer(renderer2);

			// ChartFrame frame1 = new ChartFrame("RiskFactor Chart", chart);
			// frame1.setVisible(true);
			// frame1.setSize(300, 300);
			/*
			 * final ChartPanel chartPanel = new ChartPanel(chart);
			 * chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			 * 
			 * try { writeCategoryChart(baseDir + File.separator + "simulations"
			 * + File.separator + simulationName + File.separator + "results" +
			 * File.separator + "chartRiskFactorPrevalence.jpg", chart); } catch
			 * (Exception e) { System.out.println(e.getMessage()); System.out
			 * .println("Problem occurred creating chart. for riskfactor"); }
			 */
			return chart;
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	public JFreeChart makeEmptyPlot() {
		JFreeChart chart = null;
		XYSeries nullSeries = new XYSeries("not calculated");
		nullSeries.add((double) 0, 0);
		XYDataset xyDataset = new XYSeriesCollection(nullSeries);
		chart = ChartFactory.createXYLineChart("not availlable",
				"years of simulation", "empty", xyDataset,
				PlotOrientation.VERTICAL, true, true, false);

		return chart;
	}

	/**
	 * makes plot of mortality by scenario
	 * 
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            : riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot of riskfactorclass data for a single scenario
	 *         separate for men and women
	 * @return plot of mortality by scenario
	 */
	public JFreeChart makeYearMortalityPlotByScenario(int gender,
			int riskClass, boolean differencePlot, boolean numbers,
			boolean blackAndWhite) {

		XYDataset xyDataset = null;
		double[][][][] mortality = this.getMortality(true, riskClass);
		/*
		 * get number of persons who died during this year
		 * 
		 * Succesrate etc are already included
		 */
		double[][][][] nPopByAge;
		if (riskClass < 0)
			nPopByAge = this.output.getNPopByAge();
		else
			nPopByAge = this.output.getNPopByAgeForRiskclass(riskClass);

		XYSeries scenSeries[] = new XYSeries[this.output.getNScen() + 1];

		for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

			scenSeries[thisScen] = new XYSeries(
					this.output.getScenarioNames()[thisScen]);
			/*
			 * mortality is calculated from the difference between the previous
			 * year and the current year therefor there is one less datapoint
			 * for mortality than for most other outcomes
			 */
			for (int steps = 0; steps < this.output.getStepsInRun(); steps++) {
				double indat0 = 0;
				double denominator0 = 0;
				double indat1 = 0;
				double denominator1 = 0;
				double indat0r = 0;
				double denominator0r = 0;
				double indat1r = 0;
				double denominator1r = 0;

				for (int age = 0; age < Math.min(96 + this.output
						.getStepsInRun(), 106); age++) {
					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */

					if (mortality[0][steps][age][0] >= 0
							&& mortality[thisScen][steps][age][0] >= 0) {

						indat0 += mortality[thisScen][steps][age][0];

						denominator0 += applySuccesrate(
								nPopByAge[0][steps][age][0],
								nPopByAge[thisScen][steps][age][0], thisScen,
								steps, age, 0);

						indat0r += mortality[0][steps][age][0];
						denominator0r += nPopByAge[0][steps][age][0];
					}
					if (mortality[0][steps][age][1] >= 0
							&& mortality[thisScen][steps][age][1] >= 0) {

						indat1 += mortality[thisScen][steps][age][1];
						denominator1 += applySuccesrate(
								nPopByAge[0][steps][age][1],
								nPopByAge[thisScen][steps][age][1], thisScen,
								steps, age, 1);
						indat1r += mortality[0][steps][age][1];
						denominator1r += nPopByAge[0][steps][age][1];
					}

				}
				if (gender == 0)
					addToSeries(differencePlot, numbers, scenSeries[thisScen],
							indat0, denominator0, indat0r, denominator0r, steps
									+ this.output.getStartYear());
				if (gender == 1)
					addToSeries(differencePlot, numbers, scenSeries[thisScen],
							indat1, denominator1, indat1r, denominator1r, steps
									+ this.output.getStartYear());

				if (gender == 2)
					addToSeries(differencePlot, numbers, scenSeries[thisScen],
							indat0 + indat1, denominator0 + denominator1,
							indat0r + indat1r, denominator0r + denominator1r,
							steps + this.output.getStartYear());
			}

			if (thisScen == 0)
				xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
			else
				((XYSeriesCollection) xyDataset)
						.addSeries(scenSeries[thisScen]);

		} // end scenario loop
		String label;
		if (gender == 0)
			label = "men";
		else if (gender == 1)
			label = "women";
		else
			label = "";

		String chartTitle = "mortality ";
		if (numbers && differencePlot)
			chartTitle = "excess numbers of death"
					+ " compared to ref scenario";
		if (!numbers && differencePlot)
			chartTitle = "excess mortality rate" + " compared to ref scenario";
		if (numbers && !differencePlot)
			chartTitle = "number of deaths ";
		String yTitle = "mortality rate";
		if (differencePlot && !numbers)
			yTitle = "excess mortality rate";
		if (!differencePlot && numbers)
			yTitle = "number of deaths";
		if (differencePlot && numbers)
			yTitle = "excess number of deaths";

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle + label,
				"year", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
				true, false);
		if (this.output.getStepsInRun() == 1)
			drawMarkers(chart);

		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
			setLineProperties(renderer, thisScen, blackAndWhite);
		}
		plot.setRenderer(renderer);
		XYItemRenderer renderer2 = plot.getRenderer();
		renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		plot.setRenderer(renderer2);

		return chart;

	}

	private void drawMarkers(JFreeChart chart) {
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
				.getRenderer();
		renderer.setBaseShapesVisible(true);
		renderer.setDrawOutlines(true);
	}

	/**
	 * makes plot of mortality by scenario with age on the x-axis
	 * 
	 * @param year
	 *            : year for which to plot (0=start of simulation)
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            : riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot of riskfactorclass data for a single scenario
	 *         separate for men and women
	 * @return JFreeChart plot of mortality by scenario with age on the x-axis
	 */
	public JFreeChart makeAgeMortalityPlotByScenario(int year, int gender,
			int riskClass, boolean differencePlot, boolean numbers,
			boolean blackAndWhite) {

		XYDataset xyDataset = null;

		double[][][][] mortality = this.getMortality(true, riskClass);/*
																	 * get
																	 * number of
																	 * persons
																	 * who died
																	 * during
																	 * this year
																	 * for all
																	 * riskclass
																	 * together
																	 */

		/*
		 * NB: getMortality already applies succesrate etc., so no apply
		 * succesrates should' be used on mortality
		 */

		if (mortality != null && year < this.output.getStepsInRun()) {
			double[][][][] nPopByAge = this.output.getNPopByAge();

			XYSeries scenSeries[] = new XYSeries[this.output.getNScen() + 1];

			for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

				scenSeries[thisScen] = new XYSeries(this.output
						.getScenarioNames()[thisScen]);
				/*
				 * mortality is calculated from the difference between the
				 * previous year and the current year therefor there is one less
				 * datapoint for mortality than for most other outcomes
				 */

				for (int age = 0; age < 96 /* + this.output.getStepsInRun() */; age++) {

					double indat0 = 0;
					double denominator0 = 0;
					double indat1 = 0;
					double denominator1 = 0;
					double indat0r = 0;
					double denominator0r = 0;
					double indat1r = 0;
					double denominator1r = 0;
					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */
					if (mortality[0][year][age][0] >= 0
							&& mortality[thisScen][year][age][0] >= 0) {

						indat0 = mortality[thisScen][year][age][0];
						denominator0 = applySuccesrate(
								nPopByAge[0][year][age][0],
								nPopByAge[thisScen][year][age][0], thisScen,
								year, age, 0);

						indat0r = mortality[0][year][age][0];
						denominator0r = nPopByAge[0][year][age][0];
					}
					if (mortality[0][year][age][1] >= 0
							&& mortality[thisScen][year][age][1] >= 0)

					{
						indat1 = mortality[thisScen][year][age][1];
						denominator1 = applySuccesrate(
								nPopByAge[0][year][age][1],
								nPopByAge[thisScen][year][age][1], thisScen,
								year, age, 1);
						indat1r = mortality[0][year][age][1];

						denominator1r = nPopByAge[0][year][age][1];

					}
					if (gender == 0)
						addToSeries(differencePlot, numbers,
								scenSeries[thisScen], indat0, denominator0,
								indat0r, denominator0r, age);

					if (gender == 1)
						addToSeries(differencePlot, numbers,
								scenSeries[thisScen], indat1, denominator1,
								indat1r, denominator1r, age);

					if (gender == 2)
						addToSeries(differencePlot, numbers,
								scenSeries[thisScen], indat0 + indat1,
								denominator0 + denominator1, indat0r + indat1r,
								denominator0r + denominator1r, age);
				}

				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			} // end scenario loop
			String label;
			if (gender == 0)
				label = "men; " + (this.output.getStartYear() + year);
			else if (gender == 1)
				label = "women; " + (this.output.getStartYear() + year);
			else
				label = "" + (this.output.getStartYear() + year);

			String chartTitle = "mortality";
			if (numbers && differencePlot)
				chartTitle = "excess numbers of death"
						+ " compared to ref scenario";
			if (!numbers && differencePlot)
				chartTitle = "excess mortality rate"
						+ " compared to ref scenario";
			if (numbers && !differencePlot)
				chartTitle = "number of deaths ";
			String yTitle = "mortality rate (per 100 py)";
			if (differencePlot && !numbers)
				yTitle = "excess mortality rate (per 100 py)";
			if (!differencePlot && numbers)
				yTitle = "number of deaths";
			if (differencePlot && numbers)
				yTitle = "excess number of deaths";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
				setLineProperties(renderer, thisScen, blackAndWhite);
			}
			plot.setRenderer(renderer);
			XYItemRenderer renderer2 = plot.getRenderer();
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			plot.setRenderer(renderer2);
			return chart;
		} else
			return makeEmptyPlot();
	}

	/**
	 * makes plot of mortality by scenario with age on the x-axis
	 * 
	 * @param year
	 *            : year for which to plot (0=start of simulation)
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param riskClass
	 *            : riskClass to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot of riskfactorclass data for a single scenario
	 *         separate for men and women
	 * @return JFreeChart plot of mortality by scenario with age on the x-axis
	 */
	public JFreeChart makeAgePopulationNumberPlotByScenario(int year,
			int gender, int riskClass, boolean differencePlot,
			boolean blackAndWhite) {

		XYDataset xyDataset = null;

		double[][][][] population;
		if (riskClass < 0)
			population = this.output.getNPopByAge();
		else
			population = this.output.getNPopByAgeForRiskclass(riskClass);

		if (population != null && year <= this.output.getStepsInRun()) {

			XYSeries scenSeries[] = new XYSeries[this.output.getNScen() + 1];

			for (int thisScen = 0; thisScen < this.output.getNScen() + 1; thisScen++) {

				scenSeries[thisScen] = new XYSeries(this.output
						.getScenarioNames()[thisScen]);
				/*
				 * mortality is calculated from the difference between the
				 * previous year and the current year therefor there is one less
				 * datapoint for mortality than for most other outcomes
				 */

				for (int age = 0; age < 96 /* + this.output.getStepsInRun() */; age++) {

					double indat0 = 0;

					double indat1 = 0;

					double indat0r = 0;

					double indat1r = 0;

					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */
					if (population[0][year][age][0] >= 0
							&& population[thisScen][year][age][0] >= 0) {

						indat0 = population[thisScen][year][age][0];

						indat0r = population[0][year][age][0];

					}
					if (population[0][year][age][1] >= 0
							&& population[thisScen][year][age][1] >= 0)

					{
						indat1 = population[thisScen][year][age][1];

						indat1r = population[0][year][age][1];

					}
					if (gender == 0)
						addToSeries(differencePlot, true, scenSeries[thisScen],
								indat0, 1, indat0r, 1, age);

					if (gender == 1)
						addToSeries(differencePlot, true, scenSeries[thisScen],
								indat1, 1, indat1r, 1, age);

					if (gender == 2)
						addToSeries(differencePlot, true, scenSeries[thisScen],
								indat0 + indat1, 1, indat0r + indat1r, 1, age);
				}

				if (thisScen == 0)
					xyDataset = new XYSeriesCollection(scenSeries[thisScen]);
				else
					((XYSeriesCollection) xyDataset)
							.addSeries(scenSeries[thisScen]);

			} // end scenario loop
			String label;
			if (gender == 0)
				label = "men; " + (this.output.getStartYear() + year);
			else if (gender == 1)
				label = "women; " + (this.output.getStartYear() + year);
			else
				label = "" + (this.output.getStartYear() + year);

			String chartTitle = "population numbers";
			if (differencePlot)
				chartTitle = "excess numbers of in population"
						+ " compared to ref scenario";

			if (!differencePlot)
				chartTitle = "number in population ";
			String yTitle = "numbers in population";

			if (!differencePlot)
				yTitle = "numbers in population";
			if (differencePlot)
				yTitle = "excess number in population";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int thisScen = 0; thisScen <= this.output.getNScen(); thisScen++) {
				setLineProperties(renderer, thisScen, blackAndWhite);
			}
			plot.setRenderer(renderer);
			XYItemRenderer renderer2 = plot.getRenderer();
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			plot.setRenderer(renderer2);
			return chart;
		} else
			return makeEmptyPlot();
	}

	/**
	 * makes plot of population numbers by riskclass with age on the x-axis
	 * 
	 * @param year
	 *            : year for which to plot (0=start of simulation)
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param thisScen
	 *            : scenario to plot
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param numbers
	 *            : plot absolute numbers
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return JFreeChart plot of riskfactorclass data for a single scenario
	 *         separate for men and women
	 * @return JFreeChart plot of mortality by scenario with age on the x-axis
	 */
	public JFreeChart makeAgePopulationNumberPlotByRiskClass(int year,
			int gender, int thisScen, boolean differencePlot,
			boolean blackAndWhite) {

		XYDataset xyDataset = null;

		double[][][][][] population = this.output.getNPopByRiskClassByAge();
		if (population != null && year <= this.output.getStepsInRun()) {

			XYSeries scenSeries[] = new XYSeries[this.output
					.getNRiskFactorClasses()];

			for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {

				scenSeries[r] = new XYSeries(this.output.getRiskClassnames()[r]);
				/*
				 * mortality is calculated from the difference between the
				 * previous year and the current year therefor there is one less
				 * datapoint for mortality than for most other outcomes
				 */

				for (int age = 0; age < 96 /* + this.output.getStepsInRun() */; age++) {

					double indat0 = 0;

					double indat1 = 0;

					double indat0r = 0;

					double indat1r = 0;

					/*
					 * check if mortality is present (next age in dataset)
					 * mortality=-1 flags absence
					 */
					if (population[0][year][r][age][0] >= 0
							&& population[thisScen][year][r][age][0] >= 0) {

						indat0 = population[thisScen][year][r][age][0];

						indat0r = population[0][year][r][age][0];

					}
					if (population[0][year][r][age][1] >= 0
							&& population[thisScen][year][r][age][1] >= 0)

					{
						indat1 = population[thisScen][year][r][age][1];

						indat1r = population[0][year][r][age][1];

					}
					if (gender == 0)
						addToSeries(differencePlot, true, scenSeries[r],
								indat0, 1, indat0r, 1, age);

					if (gender == 1)
						addToSeries(differencePlot, true, scenSeries[r],
								indat1, 1, indat1r, 1, age);

					if (gender == 2)
						addToSeries(differencePlot, true, scenSeries[r], indat0
								+ indat1, 1, indat0r + indat1r, 1, age);
				}

				if (r == 0)
					xyDataset = new XYSeriesCollection(scenSeries[r]);
				else
					((XYSeriesCollection) xyDataset).addSeries(scenSeries[r]);

			} // end scenario loop
			String label;
			if (gender == 0)
				label = "men; " + (this.output.getStartYear() + year);
			else if (gender == 1)
				label = "women; " + (this.output.getStartYear() + year);
			else
				label = "" + (this.output.getStartYear() + year);

			String chartTitle = "population numbers";
			if (differencePlot)
				chartTitle = "excess numbers of in population"
						+ " compared to ref scenario";

			if (!differencePlot)
				chartTitle = "number in population ";
			String yTitle = "numbers in population";

			if (!differencePlot)
				yTitle = "numbers in population";
			if (differencePlot)
				yTitle = "excess number in population";

			JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
					"age", yTitle, xyDataset, PlotOrientation.VERTICAL, true,
					true, false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			XYPlot plot = (XYPlot) chart.getPlot();
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
				setLineProperties(renderer, r, blackAndWhite);
			}
			plot.setRenderer(renderer);
			XYItemRenderer renderer2 = plot.getRenderer();
			renderer2.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
			plot.setRenderer(renderer2);
			return chart;
		} else
			return makeEmptyPlot();
	}

	/**
	 * the method produces a bargraph for cohort life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a longitudinal
	 * life-expectancy. It only gives proper results when the stepsize used in
	 * the simulation is high enough. In Dynamo this is set at age 105 (105-age
	 * when not life expectancy at birth); This means that everyone who survives
	 * until age 105 is expected to die at age 105.
	 * 
	 * @param age
	 *            : age at which the life expectancy is calculated
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @param cumulative
	 *            : indicates type of daly: 0=daly, 1= cumulative, 2= single
	 *            3=none
	 * @return JFreeChart with bargraph for life-expectancy in different
	 *         scenario's for those who have age a at baseline
	 * 
	 */
	public JFreeChart makeCohortLifeExpectancyPlot(int age,
			boolean differencePlot, boolean blackAndWhite, int cumulative) {

		/*
		 * for (int steps = 0; steps < stepsInRun; steps++) { double indat = 0;
		 * 
		 * for (int age = 0; age < nDim; age++) indat +=
		 * applySuccesrate(pPopByAge[0][steps][age][gender],
		 * pPopByAge[thisScen][steps][age][gender], thisScen, steps, age);
		 * 
		 * series.add((double) steps, indat / 95);
		 */

		// TODO throw exception if stepsInrun < 105-age
		double[][] lifeExp = new double[this.output.getNScen() + 1][2];
		double[][] cumlifeExp = new double[this.output.getNScen() + 1][2];
		double[][] singleLifeExp = new double[this.output.getNScen() + 1][2];
		double[][] cumDALY = new double[this.output.getNScen() + 1][2];
		double baselinePop = 0;
		double[][][][] nPopByAge = getNPopByOriAge();
		/* enable 3 statements for daly in bars */
		LabelGenerator generator1 = new LabelGenerator("years of life",
				this.output.getNScen(), false, this.output.getScenTrans());
		int yearsLeft = this.output.getNDim() - age;
		/*
		 * age of the simulated population is the exact age , so everyone is
		 * assumed to have her/his birthday For life-expectancy we have to
		 * integrate over all ages, also those in between. This is done using
		 * simple Euler integration, thus number in population between age 0 and
		 * 1 is taken as the average of the numbers in simulation at age 0 and
		 * 1.. For age 1, this is the average between 1 and 2. So all ages are
		 * added twice with weight 0.5, thus alternatively added once with
		 * weight 1, except the first and the last, who are only added 0.5
		 * times.
		 * 
		 * Here we neglect what happens at the highest age, as this is already
		 * assuming that almost everyone will have died at those ages. If not,
		 * we now implicitly assume that everyone at the last birthday in the
		 * simulation has a life-expectancy left of 0.5 years
		 */

		for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				baselinePop = 0;
				double ageWeight = 0.5;
				for (int steps = 0; steps < yearsLeft; steps++) {
					lifeExp[scenario][s] += ageWeight
							* applySuccesrate(nPopByAge[0][steps][age][s],
									nPopByAge[scenario][steps][age][s],
									scenario, 0, age, s);
					if (steps == 0) {
						baselinePop += applySuccesrate(
								nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age, s);
						ageWeight = 1;
					}
				}

				if (baselinePop != 0)
					lifeExp[scenario][s] = lifeExp[scenario][s] / baselinePop;
				else
					lifeExp[scenario][s] = 0;
				if (cumulative == 2)
					generator1.setDaly(scenario, s, baselinePop
							* lifeExp[scenario][s]);

			}

		if (cumulative == 1)
			for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)

				for (int s = 0; s < 2; s++) {
					cumlifeExp[scenario][s] = 0;
					for (int a = age; a <= this.output.getMaxAgeInSimulation(); a++) {
						double ageWeight = 0.5;
						int yearsLeftcum = this.output.getNDim() - a;
						for (int steps = 0; steps < yearsLeftcum; steps++) {
							cumlifeExp[scenario][s] += ageWeight
									* applySuccesrate(
											nPopByAge[0][steps][a][s],
											nPopByAge[scenario][steps][a][s],
											scenario, 0, a, s);
							ageWeight = 1;

						}
					}

					generator1.setDaly(scenario, s, cumlifeExp[scenario][s]);

				}

		if (cumulative == 2)
			for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)
				for (int s = 0; s < 2; s++) {

					singleLifeExp[scenario][s] = 0;
					{
						double ageWeight = 0.5;
						int yearsLeftcum = this.output.getNDim() - age;
						for (int steps = 0; steps < yearsLeftcum; steps++) {
							singleLifeExp[scenario][s] += ageWeight
									* applySuccesrate(
											nPopByAge[0][steps][age][s],
											nPopByAge[scenario][steps][age][s],
											scenario, 0, age, s);
							ageWeight = 1;

						}
					}

					generator1.setDaly(scenario, s, singleLifeExp[scenario][s]);

				}

		if (cumulative == 0)

			for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)

				for (int s = 0; s < 2; s++) {
					cumDALY[scenario][s] = 0;
					for (int a = age; a <= this.output.getMaxAgeInSimulation(); a++) {
						
						cumDALY[scenario][s] += applySuccesrate(this.output
								.getPopDALY()[0][a][s], this.output
								.getPopDALY()[scenario][a][s], scenario, 0, a,
								s);

					}

					generator1.setDaly(scenario, s, cumDALY[scenario][s]);

				}

		String[] gender = { "men", "women" };
		String[] legend = this.output.getScenarioNames();

		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
				legend, gender, lifeExp);
		String chartTitle = ("Cohort life expectancy");
		if (age > 0)
			chartTitle = chartTitle + " at age " + age;
		else
			chartTitle = chartTitle + (" at birth");
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, "", "years",
				dataset, PlotOrientation.HORIZONTAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));

		// ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart", chart);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();

		domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
		domainAxis.setCategoryLabelPositionOffset(10);
		domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 12));

		if (this.output.getNScen() > 4)
			domainAxis.setCategoryMargin(0.1);

		// domainAxis.setMaximumCategoryLabelWidthRatio(3f);

		/* assign a generator to a CategoryItemRenderer, */
		CategoryItemRenderer renderer = ((CategoryPlot) plot).getRenderer();
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f));
		/* disable next 2 statements for daly in bars */
		CategoryItemLabelGenerator generator0 = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));
		if (cumulative == 3)
			renderer.setBaseItemLabelGenerator(generator0);
		else
			renderer.setBaseItemLabelGenerator(generator1);

		renderer
				.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		/* if it does not fit, no text is shown */
		if (this.output.getNScen() > 9)
			renderer
					.setBaseItemLabelFont(new Font("SansSerif", Font.PLAIN, 10));
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));/*
															 * ,TextAnchor.
															 * BASELINE_CENTER,
															 * /*-Math.PI/2 //
															 * 0));
															 */
		// renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
		// ItemLabelAnchor.CENTER, TextAnchor.CENTER , TextAnchor.CENTER,
		// /*-Math.PI/2
		// */ 0));
		renderer.setBaseItemLabelsVisible(true);

		renderer.setSeriesPaint(0, new Color(200, 200, 200));
		// renderer.setSeriesPaint(0, Color.gray);
		// renderer.setSeriesPaint(1, Color.orange);
		BarRenderer renderer1 = (BarRenderer) ((CategoryPlot) plot)
				.getRenderer();
		renderer1.setDrawBarOutline(true);

		renderer1.setBarPainter(new StandardBarPainter());

		// plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
		renderer1.setItemMargin(0.15);

		GrayPaintScale tint = new GrayPaintScale(1, Math.max(2, this.output
				.getNScen() + 1));
		renderer.setSeriesPaint(0, new Color(200, 200, 200));

		renderer.setBaseItemLabelPaint(Color.black);
		if (cumulative < 3)
			renderer.setBaseToolTipGenerator(new TooltipGenerator(generator1));
		else
			renderer
					.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		for (int scen = 0; scen < this.output.getNScen() + 1; scen++)
			/* RGB with increasing number of red */
			if (blackAndWhite) {
				renderer1.setSeriesPaint(scen, tint.getPaint(scen + 1));
				// clear general settings, otherwise it does not work ;

				renderer.setSeriesItemLabelPaint(scen, Color.black);

				if (scen == 0 || scen < ((this.output.getNScen() + 1) / 3))
					renderer.setSeriesItemLabelPaint(scen, Color.white);
			} else
				renderer1.setSeriesPaint(scen, new Color(178, 100, scen * 255
						/ (this.output.getNScen() + 1)));

		// frame1.setVisible(true);
		// frame1.setSize(300, 300);
		/*
		 * try {
		 * 
		 * writeCategoryChart(baseDir + File.separator + "simulations" +
		 * File.separator + simulationName + File.separator + "results" +
		 * File.separator + "chartLifeExpectancy.jpg", chart); } catch
		 * (Exception e) { System.out.println(e.getMessage()); System.out
		 * .println("Problem occurred creating chart. for lifeExpectancy");
		 * throw newDynamoOutputException(
		 * "Problem occurred creating chart. for lifeExpectancy with" +
		 * " message: "+e.getMessage()); }
		 */
		return chart;
	}

	/**
	 * the method produces a bargraph for calendar year cross-sectional
	 * life-expectancy in different scenario's for those who have ageOfLE a at
	 * baseline. This is a Sullivan life-expectancy. It only gives proper
	 * results when all ages up to age 95 are in the simulation. Dynamo does not
	 * check for this.output.
	 * 
	 * @param year
	 *            : year for which the life expectancy is calculated (0= first
	 *            year of simulation)
	 * @param ageOfLE
	 *            : age at which the life expectancy is calculated
	 * @param year
	 *            : year for which to plot (0=start of simulation)
	 * @param gender
	 *            : 0=men, 1=women, 2=both
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @param oldSullivan
	 *            : (boolean) indicates whether to use the Sullivan calculation
	 *            from DYNAMO-1
	 * 
	 * 
	 * @return a jfreechart with a bargraph for life-expectancy in different
	 *         scenario's in year year for those who have ageOfLE a at baseline
	 * 
	 */
	public JFreeChart makeYearLifeExpectancyPlot(int year, int ageOfLE,
			boolean differencePlot, boolean blackAndWhite) {

		/* enable 3 statements for daly in bars */
		// LabelGenerator generator=new
		// LabelGenerator("years of life",this.output.getNScen(),false);
		if (this.output.getStepsInRun() > 0) {
			double[][] lifeExp = new double[this.output.getNScen() + 1][2];
			String chartTitle = ("Cross-sectional life expectancy");

			if (ageOfLE > 95)
				chartTitle = (" no simulated persons of age 95 and younger in year " + (this.output
						.getStartYear() + year));
			else {

				double[][][][] mortality = this.getMortality(false, -1);
				/* check the range of age values availlable */

				int[] maxAgeInSimulation = getmaxAgeInSimulation(year, ageOfLE);

				/*
				 * complete possibilities although hopefully only the first is
				 * possible
				 */
				if (maxAgeInSimulation[0] < 95 || maxAgeInSimulation[1] < 95) {
					if (maxAgeInSimulation[0] == maxAgeInSimulation[1])
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE + " and age " + maxAgeInSimulation[0]);
					else if (maxAgeInSimulation[0] < 95
							&& maxAgeInSimulation[1] >= 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and total life expectancy at age "
								+ ageOfLE + " (women)");
					else if (maxAgeInSimulation[0] >= 95
							&& maxAgeInSimulation[1] < 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[1]
								+ " (women) and total life expectancye at age "
								+ ageOfLE + " (men)");
					else
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and between age "
								+ ageOfLE
								+ " and age " + maxAgeInSimulation[1] + " (women)");

				}
				// TODO test if partial life-expectancy works , both in health
				// expectancy and simple life expectancy
				double[][][] nPopByAge = new double[this.output.getNScen() + 1][Math
						.min(96 + year, this.output.getNDim() - 1) + 1][2];

				for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)
					for (int sex = 0; sex < 2; sex++) {
						nPopByAge[scenario][ageOfLE][sex] = 1;
						lifeExp[scenario][sex] = 0;
						for (int age = ageOfLE + 1; age <= maxAgeInSimulation[sex]; age++) {

							nPopByAge[scenario][age][sex] = nPopByAge[scenario][age - 1][sex]
									* (1 - mortality[scenario][year][age - 1][sex]);

							lifeExp[scenario][sex] += 0.5 * (nPopByAge[scenario][age - 1][sex] + nPopByAge[scenario][age][sex]);
						}
						/*
						 * in next case, the loop above was not executed, the
						 * next statements were considered to remedy this
						 * however, there is only a point/spike is can be used
						 * to let
						 */

						/*
						 * if (ageOfLE == maxAgeInSimulation[sex]) {
						 * lifeExp[scenario][sex] += 0.5 *
						 * (nPopByAge[scenario][ageOfLE][sex] ); }
						 */

						/*
						 * add the life-expectancy of the non-simulated ages
						 * above age 95 using the assumption of constant
						 * mortality
						 */
						if (maxAgeInSimulation[sex] >= 95) {
							double rate = -Math
									.log(1 - mortality[scenario][year][maxAgeInSimulation[sex]][sex]);
							lifeExp[scenario][sex] += nPopByAge[scenario][maxAgeInSimulation[sex]][sex]
									/ rate;
						}
						double totalPopulationYearsOfLife = applySuccesrate(
								getNPopByOriAge(scenario, year, ageOfLE, sex),
								getNPopByOriAge(scenario, year, ageOfLE, sex),
								scenario, 0, ageOfLE, sex)
								* lifeExp[scenario][sex];
						// generator.setDaly(scenario,
						// sex,totalPopulationYearsOfLife);
					}
			}
			String[] gender = { "men", "women" };
			String[] legend = this.output.getScenarioNames();

			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
					legend, gender, lifeExp);

			JFreeChart chart = ChartFactory.createBarChart(chartTitle, "",
					"years", dataset, PlotOrientation.HORIZONTAL, true, true,
					false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));

			// ChartFrame frame1 = new ChartFrame("LifeExpectancy Chart",
			// chart);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();

			String label;
			if (ageOfLE > 0)
				label = " at age " + ageOfLE + " in year "
						+ (this.output.getStartYear() + year);
			else
				label = " at birth" + " in year "
						+ (this.output.getStartYear() + year);
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
			domainAxis.setCategoryLabelPositionOffset(10);
			domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 12));

			if (this.output.getNScen() > 4)
				domainAxis.setCategoryMargin(0.1);

			if (this.output.getNScen() > 4)
				domainAxis.setCategoryMargin(0.1);
			/* assign a generator to a CategoryItemRenderer, */
			BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
			renderer1.setBarPainter(new StandardBarPainter());
			renderer1.setDrawBarOutline(true);

			CategoryItemRenderer renderer = plot.getRenderer();
			renderer.setBaseOutlinePaint(Color.black);

			renderer.setBaseOutlineStroke(new BasicStroke(1.5f));
			// renderer.setBaseItemLabelGenerator(generator);
			/* disable next 2 statements for daly in bars */
			CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
					"{2}", new DecimalFormat("0.00"));

			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
					ItemLabelAnchor.CENTER, TextAnchor.CENTER,
					TextAnchor.CENTER,/*-Math.PI/2*/0));
			renderer.setBaseItemLabelsVisible(true);
			GrayPaintScale tint = new GrayPaintScale(1, Math.max(2, this.output
					.getNScen() + 1));

			renderer.setBaseItemLabelPaint(Color.black);

			for (int scen = 0; scen < this.output.getNScen() + 1; scen++)
				/* RGB with increasing number of red */
				if (blackAndWhite) {
					renderer1.setSeriesPaint(scen, tint.getPaint(scen + 1));
					renderer.setSeriesItemLabelPaint(scen, Color.black);
					if (scen == 0 || scen < ((this.output.getNScen() + 1) / 3))
						renderer.setSeriesItemLabelPaint(scen, Color.white);
				} else
					renderer1.setSeriesPaint(scen, new Color(178, 100, scen
							* 255 / (this.output.getNScen() + 1)));

			// frame1.setVisible(true);
			// frame1.setSize(300, 300);
			/*
			 * try {
			 * 
			 * writeCategoryChart(baseDir + File.separator + "simulations" +
			 * File.separator + simulationName + File.separator + "results" +
			 * File.separator + "chartLifeExpectancy.jpg", chart); } catch
			 * (Exception e) { System.out.println(e.getMessage()); System.out
			 * .println("Problem occurred creating chart. for lifeExpectancy");
			 * throw newDynamoOutputException(
			 * "Problem occurred creating chart. for lifeExpectancy with" +
			 * " message: "+e.getMessage()); }
			 */
			return chart;
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	/**
	 * A custom label generator.
	 */

	static class LabelGenerator extends AbstractCategoryItemLabelGenerator
			implements CategoryItemLabelGenerator {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String type;
		boolean stacked;
		private double[][] dalys;/* index: scenario sex */
		private DecimalFormat df2 = new DecimalFormat("#.00");
		private boolean[] notADalyScen;

		public double[][] getDalys() {
			return dalys;
		}

		/**
		 * set the daly label in the label generator
		 * 
		 * @param scenario
		 * @param sex
		 * @param d
		 *            : value of daly value in personyears
		 */
		public void setDaly(int scenario, int sex, double d) {
			if (dalys != null)
				dalys[scenario][sex] = d;

		}

		/**
		 * sets the health years from the daly scenario. Scenario 0 is the
		 * reference scenario
		 * 
		 * @param dalys
		 */
		public void setDalys(double[][] dalys) {
			this.dalys = dalys;

		}

		/**
		 * constructor of custom label generator
		 * 
		 * @param type
		 *            : a string containing the type of difference (disability,
		 *            disease free years etc)
		 * @param nScen
		 *            : number of scenarios
		 * @param stacked
		 *            : if the bars are stacked
		 * @param notADalyScen
		 *            : when true: this scenario does not have a daly calculated
		 *            NB here 0 is the first alternative scenario, not the
		 *            reference scenario
		 */
		public LabelGenerator(String type, int nScen, boolean stacked,
				boolean[] notADalyScen) {
			super("", NumberFormat.getInstance());
			this.type = type;
			this.dalys = new double[nScen + 1][2];
			this.stacked = stacked;
			this.notADalyScen = notADalyScen;
		}

		/**
		 * Generates a label for the specified item.
		 * 
		 * @param dataset
		 *            the dataset. null not permitted).
		 * @param series
		 *            the series index (zero-based).
		 * @param category
		 *            the category index (zero-based).
		 * 
		 * 
		 * @return the label (possibly <code>null</code>).
		 */
		public String generateLabel(CategoryDataset dataset, int series,
				int category) {
			String result = null;

			double value = (double) (Double) dataset.getValue(series, category);
			double reference_value = (double) (Double) dataset.getValue(0,
					category);
			log.debug("series: " + series + " category: " + category
					+ " value: " + value);

			if (value != 0) {

				String qualifyer = "pop. gain in " + this.type + ": ";
				if (reference_value > value)
					qualifyer = "pop. loss of " + type + ": ";
				result = df2.format(value);

				/* category: sex */
				/*
				 * stacked series: 0=with /1=without disease 2/ without
				 * scenario1 3/ with scenario1 etc
				 */
				/* unstacked series =scenario */
				int scenario = series;
				if (stacked)
					scenario = (int) (series / 2);
				boolean outputPopulationDifference = false;
				if (2 * scenario == series || !stacked)
					outputPopulationDifference = true;

				if (scenario > 0 && !notADalyScen[scenario - 1])
					if (scenario > 0 && outputPopulationDifference) {
						/* 0.5 is added in order to round */
						double dalyResult = (Math.abs(dalys[scenario][category]
								- dalys[0][category]) + 0.5);
						/* only give 3 significant digits */
						if (dalyResult > 1000)
							dalyResult = 10 * Math.round(dalyResult / 10);
						if (dalyResult > 10000)
							dalyResult = 100 * Math.round(dalyResult / 100);
						if (dalyResult > 100000)
							dalyResult = 1000 * Math.round(dalyResult / 1000);
						int dalyResultI = (int) dalyResult;
						result += " (" + qualifyer + dalyResultI + ")";
						log.debug("result" + result);
					}

			}

			return result;
		}

		/* column keys are:[0] men,[1] women */
		public String generateColumnLabel(CategoryDataset dataset, int arg) {
			return dataset.getColumnKey(arg).toString();
		}

		/*
		 * row keys are: [0]healthy reference scen,[1] with disease reference
		 * scen [2] healthy scen1 [3] with disease scen 1 etc
		 */
		/* data: first index= rows, second index=columns */
		public String generateRowLabel(CategoryDataset dataset, int arg) {
			return dataset.getRowKey(arg).toString();
		}

	}

	/**********************************************************************************************************/

	/**
	 * A custom tooltip generator using the labelgenerator used for the category
	 * item labels to make the same tooltip labels.
	 */

	static class TooltipGenerator implements CategoryToolTipGenerator {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private LabelGenerator theGenerator;

		/**
		 * constructor of custom label generator
		 * 
		 * @param type
		 *            : a string containing the type of difference (disability,
		 *            disease free years etc)
		 * @param nScen
		 *            : number of scenarios
		 * @param stacked
		 *            : if the bars are stacked
		 * @param notADalyScen
		 *            : when true: this scenario does not have a daly calculated
		 *            NB here 0 is the first alternative scenario, not the
		 *            reference scenario
		 */
		public TooltipGenerator(LabelGenerator generator) {
			super();
			this.theGenerator = generator;

		}

		/**
		 * Generates a label for the specified item.
		 * 
		 * @param dataset
		 *            the dataset. null not permitted).
		 * @param series
		 *            the series index (zero-based).
		 * @param category
		 *            the category index (zero-based).
		 * 
		 * 
		 * @return the label (possibly <code>null</code>).
		 */
		public String generateToolTip(CategoryDataset dataset, int series,
				int category) {
			String result = this.theGenerator.generateLabel(dataset, series,
					category);

			return result;
		}

		/* column keys are:[0] men,[1] women */
		public String generateColumnLabel(CategoryDataset dataset, int arg) {
			return dataset.getColumnKey(arg).toString();
		}

		/*
		 * row keys are: [0]healthy reference scen,[1] with disease reference
		 * scen [2] healthy scen1 [3] with disease scen 1 etc
		 */
		/* data: first index= rows, second index=columns */
		public String generateRowLabel(CategoryDataset dataset, int arg) {
			return dataset.getRowKey(arg).toString();
		}

	}

	/****************************************************************************************************/

	/**
	 * the method produces a bargraph for life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a Sullivan
	 * life-expectancy. It only gives proper results when all ages up to age 95
	 * are in the simulation. Dynamo does not check for this.output.
	 * 
	 * @param year
	 *            : year for which the life expectancy is calculated (0= first
	 *            year of simulation)
	 * @param ageOfLE
	 *            : age at which the life expectancy is calculated
	 * 
	 * @param disease
	 *            : -2= disability, -1: all diseases
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return a jfreechart with a bargraph for life-expectancy in different
	 *         scenario's in year year for those who have ageOfLE a at baseline
	 * 
	 */

	public JFreeChart makeYearHealthyLifeExpectancyPlot(int year, int ageOfLE,
			int disease, boolean differencePlot, boolean blackAndWhite,
			boolean oldSullivan) {

		/*
		 * health expectancy can be only made when at least one step is in the
		 * run
		 */
		if (this.output.getStepsInRun() > 0) {

			/* make the chart labels */
			String[] genderLabel = { "men", "women" };

			if (!this.output.isWithNewborns() && (ageOfLE < year))
				ageOfLE = year;
			String chartTitle = ("Cross-sectional life expectancy with and without ");

			if (ageOfLE > 95)
				chartTitle = (" no simulated persons of age 95 and younger in year " + (this.output
						.getStartYear() + year));
			else {

				/* check the range of age values availlable */

				int[] maxAgeInSimulation = getmaxAgeInSimulation(year, ageOfLE);

				/*
				 * complete possibilities although hopefully only the first is
				 * possible
				 */
				if (maxAgeInSimulation[0] < 95 || maxAgeInSimulation[1] < 95) {
					if (maxAgeInSimulation[0] == maxAgeInSimulation[1])
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE + " and age " + maxAgeInSimulation[0] + " with and without ");
					else if (maxAgeInSimulation[0] < 95
							&& maxAgeInSimulation[1] >= 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and total life expectancy at age "
								+ ageOfLE + " (women)" + " with and without ");
					else if (maxAgeInSimulation[0] >= 95
							&& maxAgeInSimulation[1] < 95)
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[1]
								+ " (women) and total life expectancye at age "
								+ ageOfLE + " (men)" + " with and without ");
					else
						chartTitle = ("Cross-sectional partial life expectancy between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[0]
								+ " (men) and between age "
								+ ageOfLE
								+ " and age "
								+ maxAgeInSimulation[1]
								+ " (women)" + " with and without ");

				}
			}

			String[] legend = new String[2];
			legend[0] = "healthy";
			if (disease == -1) {
				legend[1] = "with disease";
			} else if (disease == -2) {
				legend[0] = "without disability";
				legend[1] = "with disability";
			} else {
				legend[0] = "without " + this.output.getDiseaseNames()[disease];
				legend[1] = "with " + this.output.getDiseaseNames()[disease];
			}

			String label;
			String dalyLabel;
			if (disease == -1) {
				chartTitle = chartTitle + "disease" + " (Sullivan Method)";
				dalyLabel = "years without disease";
			} else if (disease == -2) {
				chartTitle = chartTitle + "disability" + " (Sullivan Method)";
				dalyLabel = "dalys";
			} else {
				chartTitle = chartTitle
						+ this.output.getDiseaseNames()[disease]
						+ " (Sullivan Method)";
				dalyLabel = "years without "
						+ this.output.getDiseaseNames()[disease];
			}

			if (ageOfLE > 0)
				label = (this.output.getStartYear() + year) + ", at age "
						+ ageOfLE;
			else
				label = "" + (this.output.getStartYear() + year) + ", at birth";

			// enable this (three times) for DALY in bars

			// LabelGenerator labelGenerator=new
			// LabelGenerator(dalyLabel,this.output.getNScen(),true, null);

			/* make data */

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++) {
				double[][] HLE;
				if (!oldSullivan)
					HLE = calculateSullivanLifeExpectancy(year, ageOfLE,
							disease, scenario);
				else
					HLE = calculateSullivanLifeExpectancy_old(year, ageOfLE,
							disease, scenario);

				for (int sex = 0; sex < 2; sex++) {
					if (HLE[0][0] == -1) {
						HLE[sex][1] = 0;
						HLE[sex][0] = 0;
					}

					if (scenario == 0) {
						dataset.addValue(HLE[sex][0] - HLE[sex][1], legend[0],
								genderLabel[sex]);
						dataset.addValue(HLE[sex][1], legend[1],
								genderLabel[sex]);
					} else {
						dataset.addValue(HLE[sex][0] - HLE[sex][1], this.output
								.getScenarioNames()[scenario]
								+ "(" + legend[0] + ")", genderLabel[sex]);
						dataset.addValue(HLE[sex][1], this.output
								.getScenarioNames()[scenario]
								+ "(" + legend[1] + ")", genderLabel[sex]);
					}
					// labelGenerator.setDaly(scenario,sex,HLE[sex][2]);
				}
			}

			JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle,
					"", "years", dataset, PlotOrientation.HORIZONTAL, true,
					true, false);
			TextTitle title = chart.getTitle();
			title.setFont(new Font("SansSerif", Font.BOLD, 14));
			TextTitle subTitle = new TextTitle(label);
			subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
			chart.addSubtitle(subTitle);
			GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
			renderer.setBarPainter(new StandardBarPainter());
			KeyToGroupMap map = new KeyToGroupMap(this.output
					.getScenarioNames()[0]);
			map.mapKeyToGroup(legend[0], this.output.getScenarioNames()[0]);
			map.mapKeyToGroup(legend[1], this.output.getScenarioNames()[0]);

			for (int scenario = 1; scenario < this.output.getNScen() + 1; scenario++)

				for (int s = 0; s < 2; s++) {
					map.mapKeyToGroup(this.output.getScenarioNames()[scenario]
							+ "(" + legend[0] + ")", this.output
							.getScenarioNames()[scenario]);
					map.mapKeyToGroup(this.output.getScenarioNames()[scenario]
							+ "(" + legend[1] + ")", this.output
							.getScenarioNames()[scenario]);
				}
			renderer.setSeriesToGroupMap(map);

			SubCategoryAxis domainAxis = new SubCategoryAxis("");
			domainAxis.setCategoryMargin(0.2); // gap between men and women:
			if (this.output.getNScen() > 4)
				domainAxis.setCategoryMargin(0.1);

			/* sub labels are the scenario names */
			domainAxis.setSubLabelFont(new Font("SansSerif", Font.PLAIN, 11));
			/* put a little more space for the scenario name */
			domainAxis.setCategoryLabelPositionOffset(25);
			/* put a little more space for the scenario name */

			for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)
				domainAxis
						.addSubCategory(this.output.getScenarioNames()[scenario]
								+ " ");
			/*
			 * make more room for the scenario labels default=0.3 of axis
			 * length)
			 */
			domainAxis.setMaximumCategoryLabelWidthRatio(0.4F);

			domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 14));
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			// This should work in newer versions of JFREECHART to get the
			// labels visible:
			// plot.setMaximumCategoryLabelWidthRatio(0.4);
			AxisSpace space = new AxisSpace();
			space.setTop(30);/* enough space is needed for the titles */
			space.setLeft(100);/*
								 * enough space in needed for the scenario names
								 */
			space.setBottom(10);
			plot.setFixedRangeAxisSpace(space);
			plot.setDomainAxis(domainAxis);
			// plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
			renderer.setItemMargin(0.15);
			GrayPaintScale tint = null;
			if (blackAndWhite)
				tint = new GrayPaintScale(1, 4);

			// dikte van de lijn// between the scenarios;
			int currentSeries = 0;
			for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)
				for (int s = 0; s < 2; s++) {
					renderer.setSeriesPaint(currentSeries, Color.pink);
					if (blackAndWhite)
						renderer
								.setSeriesPaint(currentSeries, tint.getPaint(2));
					renderer.setSeriesVisibleInLegend(currentSeries, false);
					currentSeries++;
					renderer.setSeriesPaint(currentSeries, Color.pink.darker());
					if (blackAndWhite)
						renderer
								.setSeriesPaint(currentSeries, tint.getPaint(3));
					renderer.setSeriesVisibleInLegend(currentSeries, false);
					currentSeries++;

				}

			// renderer.setBaseItemLabelGenerator(labelGenerator);

			renderer.setSeriesVisibleInLegend(0, true);
			renderer.setSeriesVisibleInLegend(1, true);
			renderer.setDrawBarOutline(true);
			renderer.setBaseOutlinePaint(Color.black);
			renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte
			// van
			// de
			// lijn

			plot.setRenderer(renderer);
			// plot.setFixedLegendItems(makeLegend(disease));
			/* disable next 2 statements for daly in bars */
			CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
					"{2}", new DecimalFormat("0.00"));
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);
			/*
			 * this is necessary as otherwise the tooltips are made invisible
			 * together with the in legend invisibility
			 */
			renderer
					.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

			return chart;
			/*
			 * mortality can only be calculated if there is at least one year of
			 * follow-up (stepsinrun>0) If this is not the case, return an empty
			 * plot
			 */
		} else {
			JFreeChart chart = makeEmptyPlot();
			return chart;
		}
	}

	/**
	 * Old method from DYNAMO-1 that calculates the HLE in an inefficient way
	 * 
	 * @param year
	 *            : simulation year for which to calculate the LE
	 * @param ageOfLE
	 *            : age at which to calculate the LE
	 * @param disease
	 *            : disease number, or -1 for all disease and -2 for disability
	 *            weighted life expectancy
	 * @param scenario
	 * @return array [sex][type] where type=0 gives total life expectancy and
	 *         type=1 gives the healthy life expectancy and type=2 the starting
	 *         population; [0][0] is made -1 if calculation fails
	 */
	public double[][] calculateSullivanLifeExpectancy_old2(int year,
			int ageOfLE, int disease, int scenario) {
		/* is vooral langzaam met disease=-2 */
		double[][] HLE = new double[2][3];
		boolean errorFlag = false;
		double[][][] nInLifeTable = new double[this.output.getNScen() + 1][Math
				.min(96 + year, this.output.getNDim() - 1) + 1][2];

		double[][][] nPopByAge = this.output.getNPopByAge(year);
		double[][][] diseased;
		if (disease == -1)
			diseased = this.output.getNDiseaseByAgeInYear(-1, year);
		else if (disease == -2)
			diseased = this.output.getNDisabledByAge(year);
		else
			diseased = this.output.getNDiseaseByAge(disease, year);

		if (ageOfLE <= 95) {
			double[][][][] mortality = this.getMortality(false, -1);
			/* check the range of age values availlable */

			/*
			 * the possible maximum of maxAge is nDim-1 , as for the maximum age
			 * nDim it is not possible to calculate the mortality as there are
			 * no data stored of higher ages that make it possible to calculate
			 * the mortality from
			 */
			int[] maxAgeInSimulation = getmaxAgeInSimulation(year, ageOfLE);

			/*
			 * complete possibilities although hopefully only the first is
			 * possible
			 */

			for (int sex = 0; sex < 2; sex++) {

				double diseasePerc = 0;
				double nPop;
				nInLifeTable[scenario][ageOfLE][sex] = 1;
				HLE[sex][0] = 0;
				HLE[sex][1] = 0;
				HLE[sex][2] = applySuccesrate(nPopByAge[0][ageOfLE][sex],
						nPopByAge[scenario][ageOfLE][sex], scenario, year,
						ageOfLE, sex); /* starting population */

				for (int age = ageOfLE + 1; age <= maxAgeInSimulation[sex]; age++) {

					if (mortality[scenario][year][age - 1][sex] > -1)
						nInLifeTable[scenario][age][sex] = nInLifeTable[scenario][age - 1][sex]
								* (1 - mortality[scenario][year][age - 1][sex]);
					HLE[sex][0] += 0.5 * (nInLifeTable[scenario][age - 1][sex] + nInLifeTable[scenario][age][sex]);
					nPop = applySuccesrate(nPopByAge[0][age][sex],
							nPopByAge[scenario][age][sex], scenario, year, age,
							sex);
					diseasePerc = applySuccesrate(diseased[0][age][sex],
							diseased[scenario][age][sex], scenario, year, age,
							sex);

					if (nPop != 0)
						diseasePerc = diseasePerc / nPop;
					else
						errorFlag = true;

					HLE[sex][1] += 0.5
							* (nInLifeTable[scenario][age - 1][sex] + nInLifeTable[scenario][age][sex])
							* diseasePerc;

				}
				/*
				 * if maxAgeInSimulation == ageOfLE wordt bovenstaande stuk
				 * overgeslagen en is HLE 0 en 1 nog steeds 0 volgens stukje was
				 * gemaakt om daar een half van te maken, maar dat is niet
				 * terecht.
				 */

				/*
				 * 
				 * if (maxAgeInSimulation[sex] == ageOfLE) {
				 * 
				 * 
				 * HLE[sex][0] += 0.5 * nInLifeTable[scenario][ageOfLE][sex]; /*
				 * disease percentage is that of the starting year
				 *//*
					 * nPop = HLE[sex][2]; diseasePerc =
					 * applySuccesrate(diseased[0][year][ageOfLE][sex],
					 * diseased[scenario][year][ageOfLE][sex], scenario, year,
					 * ageOfLE, sex);
					 * 
					 * if (nPop != 0) diseasePerc = diseasePerc / nPop; else
					 * errorFlag = true;
					 * 
					 * HLE[sex][1] += 0.5 nInLifeTable[scenario][ageOfLE][sex]
					 * diseasePerc;
					 * 
					 * }
					 */

				if (maxAgeInSimulation[sex] >= 95) {
					double rate = -Math
							.log(1 - mortality[scenario][year][maxAgeInSimulation[sex] - 1][sex]);
					HLE[sex][0] += nInLifeTable[scenario][maxAgeInSimulation[sex]][sex]
							/ rate;
					diseasePerc = applySuccesrate(
							diseased[0][maxAgeInSimulation[sex]][sex],
							diseased[scenario][maxAgeInSimulation[sex]][sex],
							scenario, year, maxAgeInSimulation[sex], sex);
					nPop = applySuccesrate(
							nPopByAge[0][maxAgeInSimulation[sex]][sex],
							nPopByAge[scenario][maxAgeInSimulation[sex]][sex],
							scenario, year, maxAgeInSimulation[sex], sex);
					if (nPop != 0)
						diseasePerc = diseasePerc / nPop;
					else
						errorFlag = true;
					HLE[sex][1] += (nInLifeTable[scenario][maxAgeInSimulation[sex]][sex] / rate)
							* diseasePerc;
				}
				HLE[sex][2] *= (HLE[sex][0] - HLE[sex][1]);
				/*
				 * the legend plots the labels of scenario 0, so here we use a
				 * general different label
				 */

			}
		} else
			HLE[0][0] = -1;
		if (errorFlag)
			HLE[0][0] = -1;
		return HLE;
	}

	/**
	 * Calculates HLE as in DYNAMO-1, but in a version that is faster
	 * 
	 * @param year
	 *            : simulation year for which to calculate the LE
	 * @param ageOfLE
	 *            : age at which to calculate the LE
	 * @param disease
	 *            : disease number, or -1 for all disease and -2 for disability
	 *            weighted life expectancy
	 * @param scenario
	 * @return array [sex][type] where type=0 gives total life expectancy and
	 *         type=1 gives the healthy life expectancy and type=2 the starting
	 *         population; [0][0] is made -1 if calculation fails
	 */
	public double[][] calculateSullivanLifeExpectancy_old(int year,
			int ageOfLE, int disease, int scenario) {
		/* is vooral langzaam met disease=-2 */
		double[][] HLE = new double[2][3];
		boolean errorFlag = false;
		double[][] nInLifeTable = new double[Math.min(96 + year, this.output
				.getNDim() - 1) + 1][2];

		if (ageOfLE <= 95) {

			/* check the range of age values availlable */

			/*
			 * the possible maximum of maxAge is nDim-1 , as for the maximum age
			 * nDim it is not possible to calculate the mortality as there are
			 * no data stored of higher ages that make it possible to calculate
			 * the mortality from
			 */
			int[] maxAgeInSimulation = getmaxAgeInSimulation(year, ageOfLE);

			/*
			 * complete possibilities although hopefully only the first is
			 * possible
			 */

			for (int sex = 0; sex < 2; sex++) {

				double nPop0 = 0;
				double nPopScen = 0;
				double nPop0_2 = 0;
				double nPopScen_2 = 0;
				double mort0 = 0;
				double mortScen = 0;
				double mort0_2 = 0;
				double mortScen_2 = 0;
				double disease0 = 0;
				double diseaseScen = 0;
				double nPop_2 = 0;
				double mort_2 = 0;

				for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
					nPop0 += this.output.getNPopByRiskClassByAge()[0][year][r][ageOfLE][sex];
					nPopScen += this.output.getNPopByRiskClassByAge()[scenario][year][r][ageOfLE][sex];
				}
				double diseasePerc = 0;

				nInLifeTable[ageOfLE][sex] = 1;
				HLE[sex][0] = 0;
				HLE[sex][1] = 0;
				HLE[sex][2] = applySuccesrate(nPop0, nPopScen, scenario, year,
						ageOfLE, sex); /* starting population */
				double nPop = HLE[sex][2];

				/*
				 * we add the data at the end of each interval, so start at
				 * ageOfLE +1 the period after age 95 is added on top of this at
				 * the end of the loop However, in case ageOfLE = 95 in the
				 * first year we only need the last part of the loop, so we do
				 * need to get through the loop without adding the "normal"
				 * part, but adding the last part
				 */
				int start = ageOfLE + 1;
				if (ageOfLE == 95 && maxAgeInSimulation[sex] == 95)
					start = ageOfLE;

				/*
				 * here we add the data of the period between age-1 and age If
				 * maxAgeInSimulation is equal to 95, then we need to add also
				 * the period after, we do this in the loop where age=95
				 */
				for (int age = start; age <= maxAgeInSimulation[sex]; age++) {
					nPop0 = 0;
					nPopScen = 0;
					nPop0_2 = 0;
					nPopScen_2 = 0;
					mort0 = 0;
					mortScen = 0;
					mort0_2 = 0;
					mortScen_2 = 0;
					disease0 = 0;
					diseaseScen = 0;

					for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
						nPop0 += this.output.getNPopByRiskClassByAge()[0][year][r][age - 1][sex];
						nPopScen += this.output.getNPopByRiskClassByAge()[scenario][year][r][age - 1][sex];
						mort0 += this.output.getMortalityByRiskClassByAge()[0][year][r][age - 1][sex];
						mortScen += this.output.getMortalityByRiskClassByAge()[scenario][year][r][age - 1][sex];
						if (age == maxAgeInSimulation[sex]) {
							mort0_2 += this.output
									.getMortalityByRiskClassByAge()[0][year][r][age][sex];
							mortScen_2 += this.output
									.getMortalityByRiskClassByAge()[scenario][year][r][age][sex];
						}
						nPop0_2 += this.output.getNPopByRiskClassByAge()[0][year][r][age][sex];
						nPopScen_2 += this.output.getNPopByRiskClassByAge()[scenario][year][r][age][sex];

						/*
						 * hieronder is steeds age-1 in age veranderd (10 maal)
						 * voor consistentie met DYNAMO-1 waar de jaren in het
						 * interval worden vermenigvuldigd met het ziekte
						 * percentage aan het einde van het inderval eigenlijk
						 * zou het het gemiddelde moeten zijn
						 */

						if (disease == -2) {
							disease0 += this.output
									.getPDisabilityByRiskClassByAge(0, year, r,
											age, sex)
									* this.output.getNPopByRiskClassByAge()[0][year][r][age][sex];
							diseaseScen += this.output
									.getPDisabilityByRiskClassByAge(scenario,
											year, r, age, sex)
									* this.output.getNPopByRiskClassByAge()[scenario][year][r][age][sex];
						}

						if (disease == -1) {
							disease0 += this.output
									.getPTotalDiseaseByRiskClassByAge(0, year,
											r, age, sex)
									* this.output.getNPopByRiskClassByAge()[0][year][r][age][sex];
							diseaseScen += this.output
									.getPTotalDiseaseByRiskClassByAge(scenario,
											year, r, age, sex)
									* this.output.getNPopByRiskClassByAge()[scenario][year][r][age][sex];

						}

						if (disease > -1) {
							disease0 += this.output
									.getNDiseaseByRiskClassByAge()[0][year][disease][r][age][sex];
							diseaseScen += this.output
									.getNDiseaseByRiskClassByAge()[scenario][year][disease][r][age][sex];

						}
					}
					if (age == 50) {

						int i = 0;
						i++;

					}
					nPop = applySuccesrate(nPop0, nPopScen, scenario, year,
							age - 1, sex);
					nPop_2 = applySuccesrate(nPop0_2, nPopScen_2, scenario,
							year, age, sex);
					double mort = applySuccesrate(mort0, mortScen, scenario,
							year, age - 1, sex);
					if (age == maxAgeInSimulation[sex])
						mort_2 = applySuccesrate(mort0_2, mortScen_2, scenario,
								year, age, sex);

					if (nPop > 0
							&& (ageOfLE != 95 || maxAgeInSimulation[sex] > 95))
						nInLifeTable[age][sex] = nInLifeTable[age - 1][sex]
								* (1 - mort / nPop);

					if (ageOfLE != 95 || maxAgeInSimulation[sex] > 95)
						HLE[sex][0] += 0.5 * (nInLifeTable[age - 1][sex] + nInLifeTable[age][sex]);

					diseasePerc = applySuccesrate(disease0, diseaseScen,
							scenario, year, age, sex);

					if (nPop_2 != 0)
						diseasePerc = diseasePerc / nPop_2;
					else
						errorFlag = true;

					/*
					 * 
					 * The life table numbers between age a and a+1 are L(a) and
					 * L(a+1). Number of years lived in the interval: 0.5[
					 * L(a)+L(a+1) ] This should be multiplied by the average
					 * disease prevalence which is 0.5 [d(a)+d(a+1)]
					 * 
					 * HOWEVER, WE HERE PROGRAMMED THIS DIFFERENTLY
					 * 
					 * The years lived are multiplied by the prevalence at the
					 * end of the interval
					 */
					if (ageOfLE != 95 || maxAgeInSimulation[sex] > 95)
						HLE[sex][1] += 0.5
								* (nInLifeTable[age - 1][sex] + nInLifeTable[age][sex])
								* diseasePerc;

				}
				/*
				 * if ageOfLE == 95 worden geen geen HLE data toegevoegd en is
				 * HLE 0 en 1 nog steeds 0 volgens stukje was gemaakt om daar
				 * een half van te maken, maar dat is niet terecht.
				 */

				/*
				 * 
				 * if (maxAgeInSimulation[sex] == ageOfLE) {
				 * 
				 * 
				 * HLE[sex][0] += 0.5 * nInLifeTable[scenario][ageOfLE][sex]; /*
				 * disease percentage is that of the starting year
				 *//*
					 * nPop = HLE[sex][2]; diseasePerc =
					 * applySuccesrate(diseased[0][year][ageOfLE][sex],
					 * diseased[scenario][year][ageOfLE][sex], scenario, year,
					 * ageOfLE, sex);
					 * 
					 * if (nPop != 0) diseasePerc = diseasePerc / nPop; else
					 * errorFlag = true;
					 * 
					 * HLE[sex][1] += 0.5 nInLifeTable[scenario][ageOfLE][sex]
					 * diseasePerc;
					 * 
					 * }
					 */

				/* volgende alleen voor MaxAgeInSimulation >=95 */
				if (maxAgeInSimulation[sex] >= 95) {
					double rate = -Math.log(1 - mort_2 / nPop_2);
					HLE[sex][0] += nInLifeTable[maxAgeInSimulation[sex]][sex]
							/ rate;

					HLE[sex][1] += (nInLifeTable[maxAgeInSimulation[sex]][sex] / rate)
							* diseasePerc;
				}
				HLE[sex][2] *= (HLE[sex][0] - HLE[sex][1]);
				/*
				 * the legend plots the labels of scenario 0, so here we use a
				 * general different label
				 */

			}
		} else
			HLE[0][0] = -1;
		if (errorFlag)
			HLE[0][0] = -1;
		return HLE;
	}

	/**
	 * Calculates HLE in the new nway for DYNAMO-2: this means that the average
	 * years lived in the one-year period are multiplied with the average
	 * prevalence, in stead of the prevalence at the end
	 * 
	 * @param year
	 *            : simulation year for which to calculate the LE
	 * @param ageOfLE
	 *            : age at which to calculate the LE
	 * @param disease
	 *            : disease number, or -1 for all disease and -2 for disability
	 *            weighted life expectancy
	 * @param scenario
	 * @return array [sex][type] where type=0 gives total life expectancy and
	 *         type=1 gives the healthy life expectancy and type=2 the starting
	 *         population; [0][0] is made -1 if calculation fails
	 */
	public double[][] calculateSullivanLifeExpectancy(int year, int ageOfLE,
			int disease, int scenario) {
		/* is vooral langzaam met disease=-2 */
		double[][] HLE = new double[2][3];
		boolean errorFlag = false;
		double[][] nInLifeTable = new double[Math.min(96 + year, this.output
				.getNDim() - 1) + 1][2];

		if (ageOfLE <= 95) {

			/* check the range of age values availlable */

			/*
			 * the possible maximum of maxAge is nDim-1 , as for the maximum age
			 * nDim it is not possible to calculate the mortality as there are
			 * no data stored of higher ages that make it possible to calculate
			 * the mortality from
			 */
			int[] maxAgeInSimulation = getmaxAgeInSimulation(year, ageOfLE);

			/*
			 * complete possibilities although hopefully only the first is
			 * possible
			 */

			for (int sex = 0; sex < 2; sex++) {

				double nPop0 = 0;
				double nPopScen = 0;
				double nPop0_2 = 0;
				double nPopScen_2 = 0;
				double mort0 = 0;
				double mortScen = 0;
				double mort0_2 = 0;
				double mortScen_2 = 0;
				double disease0 = 0;
				double diseaseScen = 0;
				double disease0_2 = 0;
				double diseaseScen_2 = 0;
				double nPop_2 = 0;
				double mort_2 = 0;

				for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
					nPop0 += this.output.getNPopByRiskClassByAge()[0][year][r][ageOfLE][sex];
					nPopScen += this.output.getNPopByRiskClassByAge()[scenario][year][r][ageOfLE][sex];
				}
				double diseasePerc = 0;

				nInLifeTable[ageOfLE][sex] = 1;
				HLE[sex][0] = 0;
				HLE[sex][1] = 0;
				HLE[sex][2] = applySuccesrate(nPop0, nPopScen, scenario, year,
						ageOfLE, sex); /* starting population */
				double nPop = HLE[sex][2];

				/*
				 * we add the data at the end of each interval, so start at
				 * ageOfLE +1 the period after age 95 is added on top of this at
				 * the end of the loop However, in case ageOfLE = 95 in the
				 * first year we only need the last part of the loop, so we do
				 * need to get through the loop without adding the "normal"
				 * part, but adding the last part
				 */
				int start = ageOfLE + 1;
				if (ageOfLE == 95 && maxAgeInSimulation[sex] == 95)
					start = ageOfLE;

				/*
				 * here we add the data of the period between age-1 and age If
				 * maxAgeInSimulation is equal to 95, then we need to add also
				 * the period after, we do this in the loop where age=95
				 */
				for (int age = start; age <= maxAgeInSimulation[sex]; age++) {
					nPop0 = 0;
					nPopScen = 0;
					nPop0_2 = 0;
					nPopScen_2 = 0;
					mort0 = 0;
					mortScen = 0;
					mort0_2 = 0;
					mortScen_2 = 0;
					disease0 = 0;
					diseaseScen = 0;

					disease0_2 = 0;
					diseaseScen_2 = 0;

					for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
						nPop0 += this.output.getNPopByRiskClassByAge()[0][year][r][age - 1][sex];
						nPopScen += this.output.getNPopByRiskClassByAge()[scenario][year][r][age - 1][sex];
						mort0 += this.output.getMortalityByRiskClassByAge()[0][year][r][age - 1][sex];
						mortScen += this.output.getMortalityByRiskClassByAge()[scenario][year][r][age - 1][sex];
						if (age == maxAgeInSimulation[sex]) {
							mort0_2 += this.output
									.getMortalityByRiskClassByAge()[0][year][r][age][sex];
							mortScen_2 += this.output
									.getMortalityByRiskClassByAge()[scenario][year][r][age][sex];
						}
						nPop0_2 += this.output.getNPopByRiskClassByAge()[0][year][r][age][sex];
						nPopScen_2 += this.output.getNPopByRiskClassByAge()[scenario][year][r][age][sex];

						/*
						 * hieronder is steeds age-1 in age veranderd (10 maal)
						 * voor consistentie met DYNAMO-1 waar de jaren in het
						 * interval worden vermenigvuldigd met het ziekte
						 * percentage aan het einde van het inderval eigenlijk
						 * zou het het gemiddelde moeten zijn
						 */

						if (disease == -2) {
							disease0 += this.output
									.getPDisabilityByRiskClassByAge(0, year, r,
											age - 1, sex)
									* this.output.getNPopByRiskClassByAge()[0][year][r][age - 1][sex];
							diseaseScen += this.output
									.getPDisabilityByRiskClassByAge(scenario,
											year, r, age - 1, sex)
									* this.output.getNPopByRiskClassByAge()[scenario][year][r][age - 1][sex];
							disease0_2 += this.output
									.getPDisabilityByRiskClassByAge(0, year, r,
											age, sex)
									* this.output.getNPopByRiskClassByAge()[0][year][r][age][sex];
							diseaseScen_2 += this.output
									.getPDisabilityByRiskClassByAge(scenario,
											year, r, age, sex)
									* this.output.getNPopByRiskClassByAge()[scenario][year][r][age][sex];

						}

						if (disease == -1) {
							disease0 += this.output
									.getPTotalDiseaseByRiskClassByAge(0, year,
											r, age - 1, sex)
									* this.output.getNPopByRiskClassByAge()[0][year][r][age - 1][sex];
							diseaseScen += this.output
									.getPTotalDiseaseByRiskClassByAge(scenario,
											year, r, age - 1, sex)
									* this.output.getNPopByRiskClassByAge()[scenario][year][r][age - 1][sex];
							disease0_2 += this.output
									.getPTotalDiseaseByRiskClassByAge(0, year,
											r, age, sex)
									* this.output.getNPopByRiskClassByAge()[0][year][r][age][sex];
							diseaseScen_2 += this.output
									.getPTotalDiseaseByRiskClassByAge(scenario,
											year, r, age, sex)
									* this.output.getNPopByRiskClassByAge()[scenario][year][r][age][sex];

						}

						if (disease > -1) {
							disease0 += this.output
									.getNDiseaseByRiskClassByAge()[0][year][disease][r][age - 1][sex];
							diseaseScen += this.output
									.getNDiseaseByRiskClassByAge()[scenario][year][disease][r][age - 1][sex];
							disease0_2 += this.output
									.getNDiseaseByRiskClassByAge()[0][year][disease][r][age][sex];
							diseaseScen_2 += this.output
									.getNDiseaseByRiskClassByAge()[scenario][year][disease][r][age][sex];

						}
					}
					if (age == 50) {

						int i = 0;
						i++;

					}
					nPop = applySuccesrate(nPop0, nPopScen, scenario, year,
							age - 1, sex);
					nPop_2 = applySuccesrate(nPop0_2, nPopScen_2, scenario,
							year, age, sex);
					double mort = applySuccesrate(mort0, mortScen, scenario,
							year, age - 1, sex);
					if (age == maxAgeInSimulation[sex])
						mort_2 = applySuccesrate(mort0_2, mortScen_2, scenario,
								year, age, sex);

					if (nPop > 0
							&& (ageOfLE != 95 || maxAgeInSimulation[sex] > 95))
						nInLifeTable[age][sex] = nInLifeTable[age - 1][sex]
								* (1 - mort / nPop);

					if (ageOfLE != 95 || maxAgeInSimulation[sex] > 95)
						HLE[sex][0] += 0.5 * (nInLifeTable[age - 1][sex] + nInLifeTable[age][sex]);
					// here calculate the sum of two years, which by dividing by
					// the sum of two years later will be ok
					diseasePerc = applySuccesrate(disease0, diseaseScen,
							scenario, year, age - 1, sex)
							+ applySuccesrate(disease0_2, diseaseScen_2,
									scenario, year, age, sex);
					if ((nPop_2 + nPop) != 0)
						diseasePerc = diseasePerc / (nPop_2 + nPop);
					else
						errorFlag = true;

					/*
					 * 
					 * The life table numbers between age a and a+1 are L(a) and
					 * L(a+1). Number of years lived in the interval: 0.5[
					 * L(a)+L(a+1) ] This should be multiplied by the average
					 * disease prevalence which is 0.5 [d(a)+d(a+1)]
					 */
					if (ageOfLE != 95 || maxAgeInSimulation[sex] > 95)
						HLE[sex][1] += 0.5
								* (nInLifeTable[age - 1][sex] + nInLifeTable[age][sex])
								* diseasePerc;

				}
				/*
				 * if ageOfLE == 95 worden geen geen HLE data toegevoegd en is
				 * HLE 0 en 1 nog steeds 0 volgens stukje was gemaakt om daar
				 * een half van te maken, maar dat is niet terecht.
				 */

				/*
				 * 
				 * if (maxAgeInSimulation[sex] == ageOfLE) {
				 * 
				 * 
				 * HLE[sex][0] += 0.5 * nInLifeTable[scenario][ageOfLE][sex]; /*
				 * disease percentage is that of the starting year
				 *//*
					 * nPop = HLE[sex][2]; diseasePerc =
					 * applySuccesrate(diseased[0][year][ageOfLE][sex],
					 * diseased[scenario][year][ageOfLE][sex], scenario, year,
					 * ageOfLE, sex);
					 * 
					 * if (nPop != 0) diseasePerc = diseasePerc / nPop; else
					 * errorFlag = true;
					 * 
					 * HLE[sex][1] += 0.5 nInLifeTable[scenario][ageOfLE][sex]
					 * diseasePerc;
					 * 
					 * }
					 */

				/* volgende alleen voor MaxAgeInSimulation >=95 */
				/* hier nemen we disease perc van einde van periode */
				if (maxAgeInSimulation[sex] >= 95) {
					double rate = -Math.log(1 - mort_2 / nPop_2);
					HLE[sex][0] += nInLifeTable[maxAgeInSimulation[sex]][sex]
							/ rate;

					if (nPop_2 != 0)
						diseasePerc = applySuccesrate(disease0_2,
								diseaseScen_2, scenario, year, 95, sex)
								/ nPop_2;
					else
						errorFlag = true;

					HLE[sex][1] += (nInLifeTable[maxAgeInSimulation[sex]][sex] / rate)
							* diseasePerc;
				}
				HLE[sex][2] *= (HLE[sex][0] - HLE[sex][1]);
				/*
				 * the legend plots the labels of scenario 0, so here we use a
				 * general different label
				 */

			}
		} else
			HLE[0][0] = -1;
		if (errorFlag)
			HLE[0][0] = -1;
		return HLE;
	}

	private int[] getmaxAgeInSimulation(int year, int ageOfLE) {
		int[] maxAgeInSimulation = { -1, -1 };
		double[][][][] mortality = this.getMortality(false, -1);
		int maxAge = Math.min(96 + year, this.output.getNDim() - 1);
		for (int sex = 0; sex < 2; sex++) {
			for (int age = ageOfLE; age < maxAge; age++) {
				if (mortality[0][year][age][sex] < 0)
					break;
				maxAgeInSimulation[sex] = age;
			}
			if (maxAgeInSimulation[sex] < 0)
				maxAgeInSimulation[sex] = maxAge;
		}
		return maxAgeInSimulation;
	}

	private LegendItemCollection makeLegend(int disease) {

		LegendItemCollection legend = new LegendItemCollection();
		LegendItem item1 = new LegendItem("healthy");

		legend.add(item1);
		LegendItem item2;

		if (disease >= 0)
			item2 = new LegendItem("with "
					+ this.output.getDiseaseNames()[disease]);
		else
			item2 = new LegendItem("with disease");
		legend.add(item2);

		return legend;
	}

	/**
	 * the method produces a bargraph for life-expectancy in different
	 * scenario's for those who have age a at baseline. This is a longitudinal
	 * life-expectancy. It only gives proper results when the stepsize used in
	 * the simulation is high enough. In Dynamo this is set at age 105 (105-age
	 * when not life expectancy at birth); This means that everyone who survives
	 * until age 105 is expected to die at age 105.
	 * 
	 * @param age
	 *            : age at which the life expectancy is calculated
	 * @param disease
	 *            : diseasenumber. -1= all diseases, -2=disability
	 * @param blackAndWhite
	 *            : plot in black and white or color
	 * @param cumulative
	 *            : 0: daly values as calculated from a scenario with a one-year
	 *            change in exposure (returning after the first year to the
	 *            values of the reference scenario : 1: cumulative years gained
	 *            in the starting population : 2: years gained by the cohort
	 *            with the lowest value in the population : 3: none
	 * @return a jfreechart with a bargraph for life-expectancy in different
	 *         scenario's for those who have age a at baseline
	 * 
	 * 
	 */
	public JFreeChart makeCohortHealthyLifeExpectancyPlot(int age, int disease,
			boolean blackAndWhite, int cumulative) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String[] genderLabel = { "men", "women" };
		String itemLabel = null; // for text in bar
		String itemLabel0 = null; // for legend and as identifier for bar
		String itemLabel1 = null; // for legend
		String[] legend = new String[2];

		if (disease == -1) {

			itemLabel0 = "years without disease";
			itemLabel = "years without disease";
			itemLabel1 = "years with disease";
		} else if (disease == -2) {

			itemLabel = "years without disability";
			itemLabel0 = "years without disability";
			itemLabel1 = "years with disability";
		} else {

			itemLabel = "years without "
					+ this.output.getDiseaseNames()[disease];
			itemLabel0 = "years without "
					+ this.output.getDiseaseNames()[disease];
			itemLabel1 = "years with " + this.output.getDiseaseNames()[disease];
		}
		if (cumulative == 0)
			if (disease == -1) {

				itemLabel = "without-disease DALYs";
			} else if (disease == -2) {

				itemLabel = "DALYs";
			} else {
				itemLabel = "without-" + this.output.getDiseaseNames()[disease]
						+ " DALYs";
			}

		/* enable next statements for daly in bars */
		boolean noDaly[] = new boolean[this.output.getNScen()];
		if (cumulative > 0)
			for (int scenario = 0; scenario < this.output.getNScen(); scenario++)
				noDaly[scenario] = false;
		else
			noDaly = this.output.getScenTrans();
		LabelGenerator generator1 = new LabelGenerator(itemLabel, this.output
				.getNScen(), true, noDaly);
		CategoryItemLabelGenerator generator0 = new StandardCategoryItemLabelGenerator(
				"{2}", new DecimalFormat("0.00"));
		for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++) {
			/*
			 * the method calculateHealthExpectancy return a 2 by 2 array, first
			 * index is sex, second: 0 = life expectancy, 1= health expectancy
			 */

			double[][] HLE = calculateCohortHealthExpectancy(age, scenario,
					disease);
			double[] cumHLE = new double[2];

			/*
			 * the method calculateDALYHealthyYears returns the healthy years in
			 * each scenario, need to be substracted from the reference years
			 * for a daly result
			 */
			double[] daly = new double[2];
			if (cumulative == 0)
				if (scenario == 0 || !this.output.scenTrans[scenario - 1])
					daly = calculateCumulativeDALYHealthyYears(age, disease,
							scenario);

			if (cumulative == 1)
				cumHLE = calculateCumulativeHealthyYearsGained(age, disease,
						scenario, HLE);
			for (int s = 0; s < 2; s++) {

				if (cumulative == 2)
					generator1.setDaly(scenario, s, HLE[s][2]);
				else if (cumulative == 1)
					generator1.setDaly(scenario, s, cumHLE[s]);

				else if (cumulative == 0)
					generator1.setDaly(scenario, s, daly[s]);

				log.debug("setting DALY for scen " + scenario + " sex " + s
						+ " to :  " + HLE[s][2]);
				/*
				 * the legend plots the labels of scenario 0, so here we use a
				 * general different label
				 */
				if (scenario == 0) {
					dataset.addValue(HLE[s][0] - HLE[s][1], itemLabel0,
							genderLabel[s]);
					dataset.addValue(HLE[s][1], itemLabel1, genderLabel[s]);
				} else {
					dataset.addValue(HLE[s][0] - HLE[s][1], this.output
							.getScenarioNames()[scenario]
							+ "(" + itemLabel0 + ")", genderLabel[s]);
					dataset.addValue(HLE[s][1],
							this.output.getScenarioNames()[scenario] + "("
									+ itemLabel1 + ")", genderLabel[s]);

				}
			}
		}

		String chartTitle = ("Cohort life expectancy with and without ");

		String label;
		if (disease == -2)
			chartTitle = chartTitle + "disability";
		else if (disease == -1)
			chartTitle = chartTitle + "disease";
		else
			chartTitle = chartTitle + this.output.getDiseaseNames()[disease];

		if (age > 0)
			label = " at age " + age;
		else
			label = " at birth";

		JFreeChart chart = ChartFactory
				.createStackedBarChart(chartTitle, "", "years", dataset,
						PlotOrientation.HORIZONTAL, true, true, false);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		renderer.setBarPainter(new StandardBarPainter());

		KeyToGroupMap map = new KeyToGroupMap(this.output.getScenarioNames()[0]);
		map.mapKeyToGroup(itemLabel0, this.output.getScenarioNames()[0]);
		// map.mapKeyToGroup("with disease", this.output.getScenarioNames()[0]);
		// map.mapKeyToGroup("with disability",
		// this.output.getScenarioNames()[0]);

		for (int scenario = 1; scenario < this.output.getNScen() + 1; scenario++)

			for (int s = 0; s < 2; s++) {
				map.mapKeyToGroup(this.output.getScenarioNames()[scenario]
						+ "(" + itemLabel0 + ")", this.output
						.getScenarioNames()[scenario]);
				map.mapKeyToGroup(this.output.getScenarioNames()[scenario]
						+ "(" + itemLabel1 + ")", this.output
						.getScenarioNames()[scenario]);
				// map.mapKeyToGroup(this.output.getScenarioNames()[scenario]
				// + "(with disability)",
				// this.output.getScenarioNames()[scenario]);
			}
		renderer.setSeriesToGroupMap(map);

		SubCategoryAxis domainAxis = new SubCategoryAxis("");

		domainAxis.setCategoryMargin(0.2); // gap between men and women:
		if (this.output.getNScen() > 4)
			domainAxis.setCategoryMargin(0.1);
		/* put a little more space for the scenario name */
		domainAxis.setCategoryLabelPositionOffset(25);
		/* make more room for the scenario labels default=0.3 of axis length) */
		domainAxis.setMaximumCategoryLabelWidthRatio(0.4F);
		domainAxis.setSubLabelFont(new Font("SansSerif", Font.PLAIN, 11));
		domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 14));
		// add space after scenario name to make room between the axis and the
		// label
		for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)
			domainAxis.addSubCategory(this.output.getScenarioNames()[scenario]
					+ " ");
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		AxisSpace space = new AxisSpace();
		space.setTop(30);/* enough space is needed for the titles */
		space.setLeft(80);/* enough space in needed for the scenario names */
		space.setBottom(10);
		plot.setFixedRangeAxisSpace(space);
		ValueAxis valueAxis = plot.getRangeAxis();

		double lowerBound = valueAxis.getLowerMargin();
		valueAxis.setLowerMargin(lowerBound + 0.5);
		domainAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 14));
		plot.setDomainAxis(domainAxis);
		// plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
		renderer.setItemMargin(0.15);

		GrayPaintScale tint = null;
		if (blackAndWhite)
			tint = new GrayPaintScale(1, 4);

		// dikte van de lijn// between the scenarios;
		int currentSeries = 0;
		for (int scenario = 0; scenario < this.output.getNScen() + 1; scenario++)
			for (int s = 0; s < 2; s++) {
				renderer.setSeriesPaint(currentSeries, Color.pink);
				if (blackAndWhite)
					renderer.setSeriesPaint(currentSeries, tint.getPaint(2));
				renderer.setSeriesVisibleInLegend(currentSeries, false);
				currentSeries++;
				renderer.setSeriesPaint(currentSeries, Color.pink.darker());
				if (blackAndWhite)
					renderer.setSeriesPaint(currentSeries, tint.getPaint(3));
				renderer.setSeriesVisibleInLegend(currentSeries, false);
				currentSeries++;

			}
		renderer.setSeriesVisibleInLegend(0, true);
		renderer.setSeriesVisibleInLegend(1, true);
		renderer.setDrawBarOutline(true);
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijn

		plot.setRenderer(renderer);
		// plot.setFixedLegendItems(makeLegend(disease));

		/* next statement for no daly in bars */

		if (cumulative == 3)
			renderer.setBaseItemLabelGenerator(generator0);
		else
			renderer.setBaseItemLabelGenerator(generator1);

		renderer.setBaseItemLabelsVisible(true);
		if (cumulative < 3)
			renderer.setBaseToolTipGenerator(new TooltipGenerator(generator1));
		else
			renderer
					.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		return chart;
	}

	/**
	 * calculate the DALY's gained or lost for all ages above age
	 * 
	 * @param age
	 * @param disease
	 * @param scenario
	 * @return cumulative Dalys for the two sexes
	 */
	private double[] calculateCumulativeDALYHealthyYears(int age, int disease,
			int scenario) {

		double[] cumDALY = new double[2];
		cumDALY[0] = 0;
		cumDALY[1] = 0;
		double[] temDALY;
		for (int a = age; a <= this.output.getMaxAgeInSimulation(); a++) {
			temDALY = calculateDALYHealthyYears(a, disease, scenario);
			cumDALY[0] += temDALY[0];
			cumDALY[1] += temDALY[1];
		}
		return cumDALY;

	}

	/**
	 * returns the future healthy years lived in the entire population aged age
	 * and onlder
	 * 
	 * @param age
	 * @param disease
	 * @param scenario
	 * @param HLE
	 * @return array [2] containing the result for males (0) and females (1)
	 */
	private double[] calculateCumulativeHealthyYearsGained(int age,
			int disease, int scenario, double[][] HLE) {
		double[] cumHLE = new double[2];
		cumHLE[0] = HLE[0][2];
		cumHLE[1] = HLE[1][2];
		double[][] temHLE;
		for (int a = age + 1; a <= this.output.getMaxAgeInSimulation(); a++) {
			temHLE = calculateCohortHealthExpectancy(a, scenario, disease);
			cumHLE[0] += temHLE[0][2];
			cumHLE[1] += temHLE[1][2];
		}
		return cumHLE;
	}

	/**
	 * @param age
	 * @param disease
	 * @param scenario
	 * 
	 * @return
	 */
	private double[] calculateDALY(int age, int disease, int scenario) {
		double[] daly = new double[2];
		Arrays.fill(daly, 0);
		double[][] dataScen = new double[96][2];
		double[][] dataRef = new double[96][2];
		if (disease == -2) {
			dataScen = this.output.getDisabilityDALY()[scenario];
			dataRef = this.output.getDisabilityDALY()[0];
		} else if (disease == -1) {
			dataScen = this.output.getTotDiseaseDALY()[scenario];
			dataRef = this.output.getTotDiseaseDALY()[0];
		} else {
			dataScen = this.output.getDiseaseDALY(disease)[scenario];
			dataRef = this.output.getDiseaseDALY(disease)[0];
		}

		for (int a = age; a < 96; a++)
			for (int g = 0; g < 2; g++)
				daly[g] += applySuccesrate(dataScen[a][g], dataRef[a][g],
						scenario, 0, a, g)
						- dataRef[a][g];

		return daly;
	}/*
	 * this.popDALY[scen][a][s] += this.nPopDALYByOriRiskClassByOriAge[scen -
	 * 1][stepCount][r][a][s];
	 * 
	 * disabilityDALY[scen][a][s] += pDALYDisabilityByOriRiskClassByOriAge[scen
	 * - 1][stepCount][r][a][s]; totDiseaseDALY[scen][a][s] +=
	 * pDALYTotalDiseaseByOriRiskClassByOriAge[scen - 1][stepCount][r][a][s];
	 * for (int state = 0; state < this.nDiseaseStates; state++)
	 * diseaseStateDALY[
	 */

	/**
	 * returns the healthy years in the DALY scenarios (exposure changed during
	 * the first year only
	 * 
	 * @param age
	 * @param disease
	 *            (-2: disability, -1: total disease or number of the disease
	 * @param scenario
	 * 
	 * @return
	 */
	private double[] calculateDALYHealthyYears(int age, int disease,
			int scenario) {
		double[] healthyDalyYears = new double[2];
		Arrays.fill(healthyDalyYears, 0);
		double[] dataScen = new double[2];
		double[] dataRef = new double[2];
		double[] healthyYearsScen = new double[2];
		double[] healthyYearsRef = new double[2];
		double[] LEScen = this.output.getPopDALY()[scenario][age];
		double[] LERef = this.output.getPopDALY()[0][age];
		if (disease == -2) {
			dataScen = this.output.getDisabilityDALY()[scenario][age];
			dataRef = this.output.getDisabilityDALY()[0][age];
		} else if (disease == -1) {
			dataScen = this.output.getTotDiseaseDALY()[scenario][age];
			dataRef = this.output.getTotDiseaseDALY()[0][age];
		} else {
			dataScen = this.output.getDiseaseDALY(disease)[scenario][age];
			dataRef = this.output.getDiseaseDALY(disease)[0][age];
		}

		for (int g = 0; g < 2; g++) {
			/*
			 * data are the years lived with disease/disability, for the daly we
			 * need the difference in healthy years, that is without
			 * disease/disability
			 * 
			 * first calculate healthy years
			 */
			healthyYearsScen[g] = LEScen[g] - dataScen[g];
			healthyYearsRef[g] = LERef[g] - dataRef[g];
			/*
			 * take the difference between the health years in the scenarios
			 * (taking succesrate into account)
			 */
			healthyDalyYears[g] += applySuccesrate(healthyYearsRef[g],
					healthyYearsScen[g], scenario, 0, age, g);
		}

		return healthyDalyYears;
	}/*
	 * this.popDALY[scen][a][s] += this.nPopDALYByOriRiskClassByOriAge[scen -
	 * 1][stepCount][r][a][s];
	 * 
	 * disabilityDALY[scen][a][s] += pDALYDisabilityByOriRiskClassByOriAge[scen
	 * - 1][stepCount][r][a][s]; totDiseaseDALY[scen][a][s] +=
	 * pDALYTotalDiseaseByOriRiskClassByOriAge[scen - 1][stepCount][r][a][s];
	 * for (int state = 0; state < this.nDiseaseStates; state++)
	 * diseaseStateDALY[
	 */

	/**
	 * calculates the cohort healthy life expectancy
	 * 
	 * @param age
	 *            age at which the life expectancy should be calculated
	 * @param scenario
	 * @param disease
	 *            number of the disease, or -1 for all diseases and -2 for
	 *            disability weighted health expectancy
	 * @return double [sex][type], where type=0 give the total life expectancy,
	 *         and type=1 gives the healthy life expectancy and type=2 given the
	 *         difference times the baseline population
	 * 
	 */
	public double[][] calculateCohortHealthExpectancyOld(int age, int scenario,
			int disease) {

		double HLE[][] = new double[2][3];

		double[][][][] nPopByAge = getNPopByOriAge();

		double[][][] diseased;
		if (disease == -1)
			diseased = this.output.getNTotFutureDiseaseByOriAge(age);

		else if (disease == -2)
			diseased = this.output.getNFutureDisabledByOriAge(age);

		else
			diseased = this.output.getNDiseaseByOriAge(age, disease);

		int yearsLeft = this.output.getNDim() - age;

		/*
		 * age of the simulated population is the exact age , so everyone is
		 * assumed to have her/his birthday on the moment of registration in the
		 * simulation. For life-expectancy we have to integrate over all ages,
		 * also those in between. This is done using simple Euler integration,
		 * thus number in population between age 0 and 1 is taken as the average
		 * of the numbers in simulation at age 0 and 1. For age 1, this is the
		 * average between 1 and 2. So all ages are added twice with weight 0.5,
		 * thus alternatively added once with weight 1, except the first and the
		 * last, who are only added 0.5 times.
		 * 
		 * Here we neglect what happens at the highest age, as this is already
		 * assuming that almost everyone will have died at those ages. If not,
		 * we now implicitly assume that everyone at the last birthday in the
		 * simulation (age 105) has a life-expectancy left of 0.5 years
		 */

		/*
		 * most arrays below are for debugging purposes, and are not needed for
		 * the result
		 */
		double lifeExp[] = new double[2];
		double withDiseaseExp[] = new double[2];
		double lifeTableL[][] = new double[2][this.output.getNDim()];
		double percDisease[][] = new double[2][this.output.getNDim()];
		double[][] lifeTableDiseasesL = new double[2][this.output.getNDim()];
		double lifeTableN[][] = new double[2][this.output.getNDim()];
		double[][] lifeTableDiseasesN = new double[2][this.output.getNDim()];

		for (int s = 0; s < 2; s++) {
			double baselinePop = 0;
			double ageWeight = 0.5;
			for (int steps = 0; steps < yearsLeft; steps++) {

				lifeExp[s] += ageWeight
						* applySuccesrate(nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age, s);

				withDiseaseExp[s] += ageWeight
						* applySuccesrate(diseased[0][steps][s],
								diseased[scenario][steps][s], scenario, 0, age,
								s);
				lifeTableN[s][steps] = ageWeight
						* applySuccesrate(nPopByAge[0][steps][age][s],
								nPopByAge[scenario][steps][age][s], scenario,
								0, age, s);
				lifeTableDiseasesN[s][steps] = ageWeight
						* applySuccesrate(diseased[0][steps][s],
								diseased[scenario][steps][s], scenario, 0, age,
								s);
				percDisease[s][steps] = lifeTableDiseasesN[s][steps]
						/ lifeTableN[s][steps];
				if (steps == 0) {
					baselinePop += applySuccesrate(nPopByAge[0][steps][age][s],
							nPopByAge[scenario][steps][age][s], scenario, 0,
							age, s);
					ageWeight = 1;
				}

				lifeTableL[s][steps] = lifeTableN[s][steps] / baselinePop;

				lifeTableDiseasesL[s][steps] = lifeTableDiseasesN[s][steps]
						/ baselinePop;

			}

			if (baselinePop != 0) {
				lifeExp[s] = lifeExp[s] / baselinePop;
				withDiseaseExp[s] = withDiseaseExp[s] / baselinePop;
			} else {
				lifeExp[s] = 0;
				withDiseaseExp[s] = 0;
			}

			HLE[s][0] = lifeExp[s];
			HLE[s][1] = withDiseaseExp[s];
			HLE[s][2] = baselinePop * (lifeExp[s] - withDiseaseExp[s]);
		}
		return HLE;
	}

	/**
	 * calculates the cohort healthy life expectancy in a faster way than the
	 * old version
	 * 
	 * @param age
	 *            age at which the life expectancy should be calculated
	 * @param scenario
	 * @param disease
	 *            number of the disease, or -1 for all diseases and -2 for
	 *            disability weighted health expectancy
	 * @return double [sex][type], where type=0 give the total life expectancy,
	 *         and type=1 gives the healthy life expectancy and type=2 given the
	 *         difference times the baseline population
	 * 
	 */
	public double[][] calculateCohortHealthExpectancy(int age, int scenario,
			int disease) {

		double HLE[][] = new double[2][3];

		double[][][][][] nPopByAge = this.output
				.getNPopByOriRiskClassByOriAge();
		double[][][][][] pDisTotByAge = null;
		if (disease == -1)
			pDisTotByAge = this.output.getPTotalDiseaseByOriRiskClassByOriAge();
		if (disease == -2)
			pDisTotByAge = this.output.getPDisabilityByOriRiskClassByOriAge();

		int yearsLeft = this.output.getNDim() - age;

		/*
		 * age of the simulated population is the exact age , so everyone is
		 * assumed to have her/his birthday on the moment of registration in the
		 * simulation. For life-expectancy we have to integrate over all ages,
		 * also those in between. This is done using simple Euler integration,
		 * thus number in population between age 0 and 1 is taken as the average
		 * of the numbers in simulation at age 0 and 1. For age 1, this is the
		 * average between 1 and 2. So all ages are added twice with weight 0.5,
		 * thus alternatively added once with weight 1, except the first and the
		 * last, who are only added 0.5 times.
		 * 
		 * Here we neglect what happens at the highest age, as this is already
		 * assuming that almost everyone will have died at those ages. If not,
		 * we now implicitly assume that everyone at the last birthday in the
		 * simulation (age 105) has a life-expectancy left of 0.5 years
		 */

		/*
		 * most arrays below are for debugging purposes, and are not needed for
		 * the result
		 */
		double lifeExp[] = new double[2];
		double withDiseaseExp[] = new double[2];
		double lifeTableL[][] = new double[2][this.output.getNDim()];
		double percDisease[][] = new double[2][this.output.getNDim()];
		double[][] lifeTableDiseasesL = new double[2][this.output.getNDim()];
		double lifeTableN[][] = new double[2][this.output.getNDim()];
		double[][] lifeTableDiseasesN = new double[2][this.output.getNDim()];

		for (int s = 0; s < 2; s++) {
			double baselinePop = 0;
			double ageWeight = 0.5;
			for (int steps = 0; steps < yearsLeft; steps++) {
				double npop0 = 0;
				double ndis0 = 0;
				double npop1 = 0;
				double ndis1 = 0;

				for (int r = 0; r < this.output.nRiskFactorClasses; r++) {
					npop0 += nPopByAge[0][steps][r][age][s];
					npop1 += nPopByAge[scenario][steps][r][age][s];
					if (disease < 0) {
						ndis0 += pDisTotByAge[0][steps][r][age][s]
								* nPopByAge[0][steps][r][age][s];
						ndis1 += pDisTotByAge[scenario][steps][r][age][s]
								* nPopByAge[scenario][steps][r][age][s];
					} else {
						ndis0 += this.output
								.getNDiseasesByOriRiskClassByOriAge()[0][steps][disease][r][age][s];
						ndis1 += this.output
								.getNDiseasesByOriRiskClassByOriAge()[scenario][steps][disease][r][age][s];
					}

				}
				lifeTableN[s][steps] = ageWeight
						* applySuccesrate(npop0, npop1, scenario, 0, age, s);

				lifeTableDiseasesN[s][steps] = ageWeight
						* applySuccesrate(ndis0, ndis1, scenario, 0, age, s);
				lifeExp[s] += lifeTableN[s][steps];

				withDiseaseExp[s] += lifeTableDiseasesN[s][steps];

				percDisease[s][steps] = lifeTableDiseasesN[s][steps]
						/ lifeTableN[s][steps];
				if (steps == 0) {
					baselinePop = lifeTableN[s][steps] / ageWeight;
					ageWeight = 1;
				}

				lifeTableL[s][steps] = lifeTableN[s][steps] / baselinePop;

				lifeTableDiseasesL[s][steps] = lifeTableDiseasesN[s][steps]
						/ baselinePop;

			}

			if (baselinePop != 0) {
				lifeExp[s] = lifeExp[s] / baselinePop;
				withDiseaseExp[s] = withDiseaseExp[s] / baselinePop;
			} else {
				lifeExp[s] = 0;
				withDiseaseExp[s] = 0;
			}

			HLE[s][0] = lifeExp[s];
			HLE[s][1] = withDiseaseExp[s];
			HLE[s][2] = baselinePop * (lifeExp[s] - withDiseaseExp[s]);
		}
		return HLE;
	}

	/**
	 * method makePyramidChart makes a population pyramid chart for scenario
	 * "thisScen" compared to the reference scenario and year "timestep"
	 * 
	 * @param thisScen
	 *            : number of the scenario
	 * @param timestep
	 *            : year (0=start of simulation)
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white; here the chart is always black and white,
	 *            so no effect
	 * 
	 * @return a population pyramid chart for scenario "thisScen" compared to
	 *         the reference scenario and year "timestep"
	 */
	public JFreeChart makePyramidChart(int thisScen, int timestep,
			boolean differencePlot, boolean blackAndWhite) {

		double[][] pyramidDataMales = null;
		double[][] pyramidDataFemales = null;
		String[] typeKey = null;
		if (differencePlot) {
			pyramidDataMales = new double[2][105];
			pyramidDataFemales = new double[2][105];
			typeKey = new String[2];
		} else {
			pyramidDataMales = new double[1][105];
			pyramidDataFemales = new double[1][105];
			typeKey = new String[1];
		}

		double[][] nPopByAge = new double[105][2];
		double[][] nRefPopByAge = null;
		if (differencePlot)
			nRefPopByAge = new double[105][2];
		if (this.scaleRange == 0) {
			/* do only once */
			int maxPopulationSize = this.getMaxPop();
			this.scaleRange = 10000 * Math
					.ceil(maxPopulationSize * 1.1 / 10000);
		}

		String[] ageKey = new String[105];

		// if (Math.floor(a/5)==a)
		int maxAge = Math.min(96 + this.output.getStepsInRun(), 105);
		// else ageKey[104-a]="";
		for (int a = 0; a < 105; a++) {
			ageKey[104 - a] = ((Integer) a).toString();
			if (a < maxAge) {
				for (int r = 0; r < this.output.getNRiskFactorClasses(); r++) {
					nPopByAge[a][0] += applySuccesrate(
							this.output.getNPopByRiskClassByAge()[0][timestep][r][a][0],
							this.output.getNPopByRiskClassByAge()[thisScen][timestep][r][a][0],
							thisScen, timestep, a, 0);
					nPopByAge[a][1] += applySuccesrate(
							this.output.getNPopByRiskClassByAge()[0][timestep][r][a][1],
							this.output.getNPopByRiskClassByAge()[thisScen][timestep][r][a][1],
							thisScen, timestep, a, 1);
					if (differencePlot) {
						nRefPopByAge[a][0] += this.output
								.getNPopByRiskClassByAge()[0][timestep][r][a][0];
						nRefPopByAge[a][1] += this.output
								.getNPopByRiskClassByAge()[0][timestep][r][a][1];
					}

				}

				/*
				 * as it is printed upside down we change the order in the
				 * dataset by putting a in 99-a
				 */

				// TODO hoe aanpakken als effect van richting verschilt per
				// leeftijdsgroep
				/*
				 * round as plot gives the numbers and integer persons are
				 * strange to users
				 */

				if (!differencePlot) {
					pyramidDataMales[0][104 - a] = -Math.round(nPopByAge[a][0]);
					pyramidDataFemales[0][104 - a] = Math
							.round(nPopByAge[a][1]);
					typeKey[0] = this.output.getScenarioNames()[thisScen];
				}

				else if (nPopByAge[a][0] >= nRefPopByAge[a][0]) {

					pyramidDataMales[0][104 - a] = -Math
							.round(nRefPopByAge[a][0]);
					pyramidDataFemales[0][104 - a] = Math
							.round(nRefPopByAge[a][1]);
					typeKey[0] = "reference scenario";

					pyramidDataMales[1][104 - a] = -Math.round(nPopByAge[a][0]
							- nRefPopByAge[a][0]);
					pyramidDataFemales[1][104 - a] = Math.round(nPopByAge[a][1]
							- nRefPopByAge[a][1]);
					typeKey[1] = this.output.getScenarioNames()[thisScen]
							+ "-reference";

				} else {
					pyramidDataMales[0][104 - a] = -Math.round(nPopByAge[a][0]);
					pyramidDataFemales[0][104 - a] = Math
							.round(nPopByAge[a][1]);

					typeKey[0] = this.output.getScenarioNames()[thisScen];

					pyramidDataMales[1][104 - a] = -Math.round(-nPopByAge[a][0]
							+ nRefPopByAge[a][0]);
					pyramidDataFemales[1][104 - a] = Math

					.round(-nPopByAge[a][1] + nRefPopByAge[a][1]);
					typeKey[1] = "reference-"
							+ this.output.getScenarioNames()[thisScen];

				}
			} else { // no data
				pyramidDataMales[0][104 - a] = 0;
				pyramidDataFemales[0][104 - a] = 0;
				typeKey[0] = "reference scenario";
				if (differencePlot) {
					pyramidDataMales[1][104 - a] = 0;
					pyramidDataFemales[1][104 - a] = 0;
					typeKey[1] = this.output.getScenarioNames()[thisScen]
							+ "-reference";
				}
			}
		}

		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataMales);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataFemales);
		// BarRenderer.setDefaultShadowsVisible(false);
		/* the last three booleans are for: legend , tooltips ,url */
		JFreeChart chart = ChartFactory.createStackedBarChart(
				"Population pyramid for "
						+ this.output.getScenarioNames()[thisScen] + " versus"
						+ " ref scenario", "", "population size", dataset1,
				PlotOrientation.HORIZONTAL, false, true, true);
		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		String label = "" + (this.output.getStartYear() + timestep);
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		CategoryPlot plot = chart.getCategoryPlot();

		plot.setDataset(1, dataset2);
		/* the category anchor does not work yet */

		CategoryTextAnnotation annotation1 = new CategoryTextAnnotation(
				"women", "98", this.scaleRange * 0.78);
		CategoryTextAnnotation annotation2 = new CategoryTextAnnotation("men",
				"98", -this.scaleRange * 0.78);

		annotation1.setFont(new Font("SansSerif", Font.BOLD, 14));
		annotation2.setFont(new Font("SansSerif", Font.BOLD, 14));
		plot.addAnnotation(annotation1);
		plot.addAnnotation(annotation2);

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setItemMargin(0.0);
		renderer.setDrawBarOutline(true);
		renderer.setShadowVisible(false);

		renderer.setItemMargin(0.0);

		renderer.setItemLabelAnchorOffset(9.0);
		renderer.setSeriesPaint(0, Color.white);
		if (differencePlot)
			renderer.setSeriesPaint(1, Color.gray);
		renderer.setDrawBarOutline(true);
		renderer.setBaseOutlinePaint(Color.black);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijnen

		CategoryAxis categoryAxis = plot.getDomainAxis();
		categoryAxis.setCategoryMargin(0.0); // ruimte tussen de balken
		categoryAxis.setUpperMargin(0.02); // ruimte boven bovenste balk
		categoryAxis.setLowerMargin(0.02);// ruimte onder onderste balk
		categoryAxis.setAxisLineVisible(false);
		categoryAxis.setTickMarksVisible(false);
		plot.setRangeGridlinesVisible(true);
		plot.setBackgroundPaint(null);
		plot.setRangeGridlinePaint(Color.gray);

		/*
		 * only show ages every 5 years make color white for years in between
		 */

		Paint background = chart.getBackgroundPaint();
		for (int i = 0; i < 105; i++)
			if (5 * Math.floor(i / 5) != i) {
				categoryAxis.setTickLabelPaint(((Integer) i).toString(),
						background);
				categoryAxis.setTickLabelFont(((Integer) i).toString(),
						new Font("SansSerif", Font.PLAIN, 2));
			}

		categoryAxis.setTickLabelsVisible(true);
		boolean[] show = new boolean[105];
		for (int a = 0; a < 105; a++)
			if (Math.floor(a) == a)
				show[a] = true;
			else
				show[a] = false;

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(-this.scaleRange, this.scaleRange);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setUpperMargin(0.10);
		rangeAxis.setAxisLineVisible(false);
		rangeAxis.setTickMarksVisible(false);

		/* get rid of the minus sign on the horizontal axis */
		DecimalFormat format = new DecimalFormat();
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();

		symbols.setMinusSign(' ');
		rangeAxis.setNumberFormatOverride(new DecimalFormat("###,###,###.###",
				symbols));

		plot.setRenderer(1, renderer);
		plot.setRenderer(2, renderer);
		return chart;
	}

	/**
	 * method find the largest populationnumbers
	 * 
	 * @return
	 */

	double scaleRange = 0;

	/**
	 * method makePyramidChart makes a population pyramid chart for scenario
	 * "thisScen" compared to the reference scenario and year "timestep",
	 * indication also the persons in the population with any modelled disease
	 * 
	 * @param thisScen
	 *            : number of the scenario
	 * @param timestep
	 * @param d
	 *            : number of the disease: -2=disability; -1=all diseases; >=0:
	 *            diseaseNumber
	 * @param differencePlot
	 *            : plot difference with reference scenario
	 * @param blackAndWhite
	 *            : (boolean) indicates whether the plots are in color (false)
	 *            or black and white
	 * @return a population pyramid chart for scenario "thisScen" compared to
	 *         the reference scenario and year "timestep", indication also the
	 *         persons in the population with any modelled disease
	 */
	public JFreeChart makePyramidChartIncludingDisease(int thisScen,
			int timestep, int d, boolean differencePlot, boolean blackAndWhite) {

		/*
		 * pyramid data 1 are the data for men, pyramiddata2 those for women;
		 */

		double[][] pyramidDataMales = new double[2][105];
		double[][] pyramidDataFemales = new double[2][105];
		double[][] nHealthyByAge = new double[105][2];
		double[][] nRefHealthyByAge = new double[105][2];
		double[][] nDiseaseByAge = new double[105][2];
		double[][] nRefDiseaseByAge = new double[105][2];

		if (differencePlot) {
			pyramidDataMales = new double[6][105];
			pyramidDataFemales = new double[6][105];
		}

		String diseaseName = "disease";
		if (d == -2)
			diseaseName = "disability";
		if (d >= 0)
			diseaseName = this.output.getDiseaseNames()[d];
		double[][][][] withDisease;

		if (d < 0)
			if (d == -2)
				withDisease = this.output.getNDisabledByAge();
			else
				withDisease = this.output.getNDiseaseByAge(-1);
		else
			withDisease = this.output.getNDiseaseByAge(d);
		double[][][][] nPopByAge = this.output.getNPopByAge();

		/* the arrays with Key give the names of the columns in the plot */

		String[] typeKey = { "with " + diseaseName,
				"ref-scen (" + diseaseName + ")",
				"scen-ref (" + diseaseName + ")", "healthy",
				"ref-scen (total)", "scen-ref (total)" };

		if (!differencePlot) {
			String[] temp1 = { "with " + diseaseName, "healthy" };
			typeKey = temp1;

		}

		String[] ageKey = new String[105];
		int maxAge = Math.min(96 + this.output.getStepsInRun(), 105);
		for (int a = 0; a < 105; a++) {
			// if (Math.floor(a/5)==a)
			ageKey[104 - a] = ((Integer) a).toString();
			// else ageKey[104-a]="";
			if (a < maxAge) {
				nDiseaseByAge[a][0] += applySuccesrate(
						withDisease[0][timestep][a][0],
						withDisease[thisScen][timestep][a][0], thisScen,
						timestep, a, 0);
				nHealthyByAge[a][0] += applySuccesrate(
						nPopByAge[0][timestep][a][0]
								- withDisease[0][timestep][a][0],
						nPopByAge[thisScen][timestep][a][0]
								- withDisease[thisScen][timestep][a][0],
						thisScen, timestep, a, 0);
				nDiseaseByAge[a][1] += applySuccesrate(
						withDisease[0][timestep][a][1],
						withDisease[thisScen][timestep][a][1], thisScen,
						timestep, a, 1);
				nHealthyByAge[a][1] += applySuccesrate(
						nPopByAge[0][timestep][a][1]
								- withDisease[0][timestep][a][1],
						nPopByAge[thisScen][timestep][a][1]
								- withDisease[thisScen][timestep][a][1],
						thisScen, timestep, a, 1);
				/*
				 * the values for the reference population are only needed in
				 * case it is a difference plot
				 */
				if (differencePlot) {
					nRefDiseaseByAge[a][0] += withDisease[0][timestep][a][0];
					nRefDiseaseByAge[a][1] += withDisease[0][timestep][a][1];
					nRefHealthyByAge[a][0] += nPopByAge[0][timestep][a][0]
							- withDisease[0][timestep][a][0];
					nRefHealthyByAge[a][1] += nPopByAge[0][timestep][a][1]
							- withDisease[0][timestep][a][1];
				}
				/*
				 * as it is printed upside down we change the order in the
				 * dataset by putting a in 99-a
				 */

				/*
				 * round as plot gives the numbers and integer persons are
				 * strange to users
				 */

				/*
				 * pyramid data males are the data for men, females those for
				 * women; for the difference plot they contain 6 data parts:
				 * <br> 0: minimum of ( # withdisease reference scenario, #
				 * withdisease scenario) <br>1:maximum of (# withdisease
				 * reference scenario-# withdisease scenario, 0) <br>2:maximum
				 * of (# withdisease scenario-# withdisease reference scenario,
				 * 0)<br> 3:minimum of (# total reference scenario , # total
				 * scenario ) - total (0 - 2)<br> 4:maximum of (# total
				 * reference scenario-# total scenario, 0) <br>5:maximum of (#
				 * total scenario-# total reference scenario, 0)
				 */
				if (differencePlot) {
					pyramidDataMales[0][104 - a] = -Math.max(0, Math
							.round(nRefDiseaseByAge[a][0]));
					pyramidDataFemales[0][104 - a] = Math.max(0, Math
							.round(nRefDiseaseByAge[a][1]));
					pyramidDataMales[1][104 - a] = -Math.max(0,
							Math.round(nRefDiseaseByAge[a][0]
									- nDiseaseByAge[a][0]));
					pyramidDataFemales[1][104 - a] = Math.max(0,
							Math.round(nRefDiseaseByAge[a][1]
									- nDiseaseByAge[a][1]));

					pyramidDataMales[2][104 - a] = -Math.max(0, Math
							.round(nDiseaseByAge[a][0])
							- nRefDiseaseByAge[a][0]);
					pyramidDataFemales[2][104 - a] = Math.max(0, Math
							.round(nDiseaseByAge[a][1])
							- nRefDiseaseByAge[a][1]);

					pyramidDataMales[3][104 - a] = -Math.round(Math.min(
							(nRefHealthyByAge[a][0] + nRefDiseaseByAge[a][0]),
							(nHealthyByAge[a][0] + nDiseaseByAge[a][0])))
							- pyramidDataMales[0][104 - a]
							- pyramidDataMales[1][104 - a]
							- pyramidDataMales[2][104 - a];
					pyramidDataFemales[3][104 - a] = Math.round(Math.min(
							(nRefHealthyByAge[a][1] + nRefDiseaseByAge[a][1]),
							(nHealthyByAge[a][1] + nDiseaseByAge[a][1])))
							- pyramidDataFemales[0][104 - a]
							- pyramidDataFemales[1][104 - a]
							- pyramidDataFemales[2][104 - a];

					pyramidDataMales[4][104 - a] = -Math.max(0,
							Math
									.round(nRefHealthyByAge[a][0]
											+ nRefDiseaseByAge[a][0]
											- nHealthyByAge[a][0]
											- nDiseaseByAge[a][0]));
					pyramidDataFemales[4][104 - a] = Math.max(0,
							Math
									.round(nRefHealthyByAge[a][1]
											+ nRefDiseaseByAge[a][1]
											- nHealthyByAge[a][1]
											- nDiseaseByAge[a][1]));

					pyramidDataMales[5][104 - a] = -Math.max(0, Math
							.round(nHealthyByAge[a][0] + nDiseaseByAge[a][0]
									- nRefHealthyByAge[a][0]
									- nRefDiseaseByAge[a][0]));
					pyramidDataFemales[5][104 - a] = Math.max(0, Math
							.round(nHealthyByAge[a][1] + nDiseaseByAge[a][1]
									- nRefHealthyByAge[a][1]
									- nRefDiseaseByAge[a][1]));
				} else { /* if no difference plot */
					pyramidDataMales[0][104 - a] = -Math.max(0, Math
							.round(nDiseaseByAge[a][0]));
					pyramidDataFemales[0][104 - a] = Math.max(0, Math
							.round(nDiseaseByAge[a][1]));
					pyramidDataMales[1][104 - a] = -Math.max(0, Math
							.round(nHealthyByAge[a][0]));
					pyramidDataFemales[1][104 - a] = Math.max(0, Math
							.round(nHealthyByAge[a][1]));

				}

			} else {
				pyramidDataMales[0][104 - a] = 0;
				pyramidDataFemales[0][104 - a] = 0;
				pyramidDataMales[1][104 - a] = 0;
				pyramidDataFemales[1][104 - a] = 0;
				if (differencePlot) {

					pyramidDataMales[2][104 - a] = 0;
					pyramidDataFemales[2][104 - a] = 0;

					pyramidDataMales[3][104 - a] = 0;
					pyramidDataFemales[3][104 - a] = 0;

					pyramidDataMales[4][104 - a] = 0;
					pyramidDataFemales[4][104 - a] = 0;

					pyramidDataMales[5][104 - a] = 0;
					pyramidDataFemales[5][104 - a] = 0;
				}
			}
		}

		CategoryDataset dataset1 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataMales);
		CategoryDataset dataset2 = DatasetUtilities.createCategoryDataset(
				typeKey, ageKey, pyramidDataFemales);
		/* find the maximum value of the current population */
		/* assume that scenarios will not increase this by more than 50% */

		if (this.scaleRange == 0) {
			/* do only once */
			int maxPopulationSize = this.getMaxPop();
			this.scaleRange = 10000 * Math
					.ceil(maxPopulationSize * 1.1 / 10000);
		}
		String chartTitle = "";
		if (differencePlot)
			chartTitle = "Population pyramid for "
					+ this.output.getScenarioNames()[thisScen] + " versus"
					+ " ref scenario";
		else
			chartTitle = "Population pyramid for "
					+ this.output.getScenarioNames()[thisScen];

		/* the last three booleans are for: legend ,tooltips ,url */
		// BarRenderer.setDefaultShadowsVisible(false);
		JFreeChart chart = ChartFactory.createStackedBarChart(chartTitle,
				"age", "population size", dataset1, PlotOrientation.HORIZONTAL,
				true, true, true);

		TextTitle title = chart.getTitle();
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		chart.setBorderVisible(false);
		String label = "" + (this.output.getStartYear() + timestep);
		TextTitle subTitle = new TextTitle(label);
		subTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
		chart.addSubtitle(subTitle);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setDataset(dataset1);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.gray);
		// plot.setBackgroundPaint(Color.white);
		plot.setBackgroundPaint(null);
		CategoryAxis catAxis = (CategoryAxis) plot.getDomainAxis();

		// this does not work
		// catAxis.setLabel(null);
		catAxis.setTickLabelsVisible(true);
		boolean[] show = new boolean[105];
		for (int a = 0; a < 105; a++)
			if (Math.floor(a) == a)
				show[a] = true;
			else
				show[a] = false;
		// catAxis.setTickLabelPaint(show);

		plot.setDataset(1, dataset2);

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setShadowVisible(false);
		/*
		 * make a separate renderer for the female part of the plot, so that the
		 * legend can be made invisible here
		 */
		StackedBarRenderer renderer2 = new StackedBarRenderer();

		renderer2
				.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		plot.setRenderer(1, renderer2);
		renderer.setItemMargin(0.0);
		renderer2.setItemMargin(0.0);
		renderer.setDrawBarOutline(true);
		renderer2.setDrawBarOutline(true);

		renderer.setBarPainter(new StandardBarPainter());
		renderer2.setBarPainter(new StandardBarPainter());

		// ChartFrame frame = new ChartFrame("LifeExpectancy Chart", chart);

		renderer.setItemLabelAnchorOffset(9.0);
		renderer2.setItemLabelAnchorOffset(9.0);
		// renderer.setSeriesVisibleInLegend(1,false);
		if (!blackAndWhite) {
			renderer.setSeriesPaint(0, Color.pink);
			if (differencePlot)
				renderer.setSeriesPaint(1, Color.orange);

			else
				renderer.setSeriesPaint(1, Color.white);
			renderer.setSeriesPaint(2, Color.red);
			renderer.setSeriesPaint(3, Color.white);
			renderer.setSeriesPaint(4, Color.black);
			renderer.setSeriesPaint(5, Color.gray);
			renderer2.setSeriesPaint(0, Color.pink);
			if (differencePlot)
				renderer2.setSeriesPaint(1, Color.orange);
			else
				renderer2.setSeriesPaint(1, Color.white);
			renderer2.setSeriesPaint(2, Color.red);
			renderer2.setSeriesPaint(3, Color.white);
			renderer2.setSeriesPaint(4, Color.black);
			renderer2.setSeriesPaint(5, Color.gray);

			renderer.setBaseOutlinePaint(Color.black);
			renderer2.setBaseOutlinePaint(Color.black);
		} else {
			GrayPaintScale tint = new GrayPaintScale(1, 4);

			renderer.setSeriesPaint(0, tint.getPaint(1));
			if (differencePlot)
				renderer.setSeriesPaint(1, tint.getPaint(2));

			else
				renderer.setSeriesPaint(1, Color.white);
			renderer.setSeriesPaint(2, tint.getPaint(3));
			renderer.setSeriesPaint(3, Color.white);
			renderer.setSeriesPaint(4, Color.black);
			renderer.setSeriesPaint(5, tint.getPaint(4));
			renderer2.setSeriesPaint(0, tint.getPaint(1));
			if (differencePlot)
				renderer2.setSeriesPaint(1, tint.getPaint(2));
			else
				renderer2.setSeriesPaint(1, Color.white);
			renderer2.setSeriesPaint(2, tint.getPaint(4));
			renderer2.setSeriesPaint(3, Color.white);
			renderer2.setSeriesPaint(4, Color.black);
			renderer2.setSeriesPaint(5, tint.getPaint(4));

			renderer.setBaseOutlinePaint(Color.black);
			renderer2.setBaseOutlinePaint(Color.black);
		}

		renderer.setDrawBarOutline(true);
		renderer2.setDrawBarOutline(true);
		renderer.setBaseOutlineStroke(new BasicStroke(1.5f));
		renderer2.setBaseOutlineStroke(new BasicStroke(1.5f)); // dikte van de
		// lijnen
		CategoryAxis categoryAxis = plot.getDomainAxis();
		categoryAxis.setCategoryMargin(0.0); // ruimte tussen de balken
		categoryAxis.setUpperMargin(0.02); // ruimte boven bovenste balk
		categoryAxis.setLowerMargin(0.02);// ruimte onder onderste balk
		categoryAxis.setAxisLineVisible(false);
		categoryAxis.setTickMarksVisible(false);
		/*
		 * only show ages every 5 years make color white for years in between
		 */
		Paint background = chart.getBackgroundPaint();
		for (int i = 0; i < 105; i++)
			if (5 * Math.floor(i / 5) != i) {
				categoryAxis.setTickLabelPaint(((Integer) i).toString(),
						background);
				categoryAxis.setTickLabelFont(((Integer) i).toString(),
						new Font("SansSerif", Font.PLAIN, 2));
			}

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(-this.scaleRange, this.scaleRange);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setUpperMargin(0.10);
		rangeAxis.setAxisLineVisible(false);
		rangeAxis.setTickMarksVisible(false);
		/* get rid of the minus sign on the horizontal axis */
		DecimalFormat format = new DecimalFormat();
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();

		// Hair Space (U+200A)
		// space 0020
		// invisibile space = U+2063
		// volgende geeft een hokje op de uitvoer
		// symbols.setMinusSign('\u2063');
		symbols.setMinusSign(' ');
		rangeAxis.setNumberFormatOverride(new DecimalFormat("###,###,###.###",
				symbols));

		plot.setRenderer(0, renderer);
		renderer2.setBaseSeriesVisibleInLegend(false);
		plot.setRenderer(1, renderer2);

		/* the category anchor does not work yet */
		;
		CategoryTextAnnotation annotation1 = new CategoryTextAnnotation(
				"women", "98", this.scaleRange * 0.78);
		CategoryTextAnnotation annotation2 = new CategoryTextAnnotation("men",
				"98", -this.scaleRange * 0.78);
		annotation1.setFont(new Font("SansSerif", Font.BOLD, 14));
		annotation2.setFont(new Font("SansSerif", Font.BOLD, 14));
		plot.addAnnotation(annotation1);
		plot.addAnnotation(annotation2);
		return chart;
	}

	/**
	 * obsolete, no longer used, this writes a chart to a jpeg file kept in case
	 * this is needed in the future /**
	 * 
	 * @param fileName
	 * @param chart
	 * @throws DynamoOutputException
	 */
	public void writeCategoryChart(String fileName, JFreeChart chart)
			throws DynamoOutputException {
		File outFile = new File(fileName);
		boolean isDirectory = outFile.isDirectory();
		boolean canWrite = outFile.canWrite();
		try {
			boolean isNew = outFile.createNewFile();
			if (!isDirectory && (canWrite || isNew))

				ChartUtilities.saveChartAsJPEG(new File(fileName), chart, 300,
						500);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new DynamoOutputException(e.getMessage());
		}
	}

	/**
	 * @return
	 */
	public double[][][] getNPop() {

		double[][][] nPop = new double[this.output.getNScen() + 1][this.output
				.getStepsInRun() + 1][2];

		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++)

			for (int scen = 0; scen < this.output.getNScen() + 1; scen++)
				for (int a = 0; a < 96 + this.output.getStepsInRun(); a++)
					for (int g = 0; g < 2; g++)
						for (int d = 0; d < this.output.getNDiseases(); d++)
							for (int stepCount = 0; stepCount < this.output
									.getNDim(); stepCount++)
								nPop[scen][stepCount][g] += this.output
										.getNPopByRiskClassByAge()[scen][stepCount][r][a][g];
		return nPop;

	}

	/**
	 * @return
	 */
	public double[][][][] getNPopByOriAge() {

		double[][][][] nPopByAge = new double[this.output.getNScen() + 1][this.output
				.getNDim()][96][2];

		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++)

			for (int scen = 0; scen < this.output.getNScen() + 1; scen++)
				for (int a = 0; a < 96; a++)
					for (int g = 0; g < 2; g++)

						for (int stepCount = 0; stepCount < this.output
								.getNDim(); stepCount++)
							nPopByAge[scen][stepCount][a][g] += this.output
									.getNPopByOriRiskClassByOriAge()[scen][stepCount][r][a][g];
		return nPopByAge;

	}

	/**
	 * @return
	 */
	/**
	 * @param scen
	 * @param stepCount
	 * @param age
	 * @param sex
	 * @return
	 */
	public double getNPopByOriAge(int scen, int stepCount, int age, int sex) {

		double nPopByAge = 0;

		for (int r = 0; r < this.output.getNRiskFactorClasses(); r++)
			nPopByAge += this.output.getNPopByOriRiskClassByOriAge()[scen][stepCount][r][age][sex];
		return nPopByAge;

	}

	public double[][][][] getNPopByOriAge(int riskClass) {

		double[][][][] nPopByAge = new double[this.output.getNScen() + 1][this.output
				.getNDim()][96][2];

		for (int scen = 0; scen < this.output.getNScen() + 1; scen++)
			for (int a = 0; a < 96; a++)
				for (int g = 0; g < 2; g++)
					for (int stepCount = 0; stepCount < this.output.getNDim(); stepCount++)
						nPopByAge[scen][stepCount][a][g] = this.output
								.getNPopByOriRiskClassByOriAge()[scen][stepCount][riskClass][a][g];
		return nPopByAge;

	}

	/**
	 * method find the largest populationnumbers
	 * 
	 * @return largest population number in the simulation
	 */
	private int getMaxPop() {
		int maximum = 0;
		double[][][][] pop = this.output.getNPopByAge();
		for (int scen = 0; scen < this.output.getNScen() + 1; scen++)
			for (int year = 0; year < this.output.getStepsInRun() + 1; year++)
				for (int a = 0; a < 96; a++)
					for (int s = 0; s < 2; s++)
						if (pop[scen][year][a][s] > maximum)
							maximum = (int) Math.round(pop[scen][year][a][s]);

		return maximum;
	}

}
