/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public final class RiskFactorChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoOutputFactory output;
	public RiskFactorChartFactory(DynamoOutputFactory output){this.output=output;}
	/**
	 * @param output2
	 */
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	
			JFreeChart chart = null;

			int riskType=output.getRiskType();int nRiskClasses=output.getNRiskFactorClasses();
			
			if (riskType == 2 &&  info.riskClassChoice==nRiskClasses) {
				if (!info.axisIsAge)
					chart = output.makeYearMeanRiskFactorByScenarioPlot(
							info.genderChoice, 
							info.differencePlot);

				if (info.axisIsAge)
					chart = output.makeAgeMeanRiskFactorByScenarioPlot(info.currentYear,
							info.genderChoice, 
							info.differencePlot);
				
				
			} else {
				if (!info.axisIsAge)
					chart = output.makeYearRiskFactorByScenarioPlot(
							info.genderChoice, info.riskClassChoice,
							info.differencePlot, info.numbers);

				if (info.axisIsAge)
					chart = output.makeAgeRiskFactorByScenarioPlot(info.currentYear,
							info.genderChoice, info.riskClassChoice,
							info.differencePlot, info.numbers);
			}
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


