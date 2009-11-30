/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.output.DynamoOutputFactory_old;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

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
	DynamoPlotFactory plotFactory;
	public RiskFactorChartFactory(DynamoPlotFactory plotfactory){this.plotFactory=plotfactory;}
	/**
	 * @param output2
	 */
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	
			JFreeChart chart = null;

			int riskType=plotFactory.output.getRiskType();int nRiskClasses=plotFactory.output.getNRiskFactorClasses();
			
			if (riskType == 2 &&  info.riskClassChoice==nRiskClasses) {
				if (!info.axisIsAge)
					chart = plotFactory.makeYearMeanRiskFactorByScenarioPlot(
							info.genderChoice, 
							info.differencePlot,info.blackAndWhite);

				if (info.axisIsAge)
					chart = plotFactory.makeAgeMeanRiskFactorByScenarioPlot(info.currentYear,
							info.genderChoice, 
							info.differencePlot,info.blackAndWhite);
				
				
			} else {
				if (!info.axisIsAge)
					chart = plotFactory.makeYearRiskFactorByScenarioPlot(
							info.genderChoice, info.riskClassChoice,
							info.differencePlot, info.numbers,info.blackAndWhite);

				if (info.axisIsAge)
					chart = plotFactory.makeAgeRiskFactorByScenarioPlot(info.currentYear,
							info.genderChoice, info.riskClassChoice,
							info.differencePlot, info.numbers,info.blackAndWhite);
			}
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


