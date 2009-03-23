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
public final class DiseaseChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoOutputFactory output;
	public DiseaseChartFactory(DynamoOutputFactory output){this.output=output;}
	/**
	 * @param output2
	 */
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	
			JFreeChart chart = null;

			/*
			 * plotType= 0: by sex 1: by scenario 2: by risk class
			 * 
			 * TODO
			 */
			if ((info.plotType == 2) && !info.axisIsAge)
				chart = output.makeYearPrevalenceByGenderPlot(info.currentScen,
						info.currentDisease, info.differencePlot, info.numbers);
			if ((info.plotType == 1) && !info.axisIsAge)
				chart = output.makeYearPrevalenceByScenarioPlots(info.genderChoice,
						info.currentDisease, info.differencePlot, info.numbers);
			if ((info.plotType == 0) && !info.axisIsAge)
				chart = output.makeYearPrevalenceByRiskFactorPlots(info.genderChoice,
						info.currentScen, info.currentDisease, info.differencePlot, info.numbers);
			if ((info.plotType == 2) && info.axisIsAge)
				chart = output.makeAgePrevalenceByGenderPlot(info.currentScen,
						info.currentDisease, info.currentYear, info.differencePlot, info.numbers);
			if ((info.plotType == 1) && info.axisIsAge)
				chart = output.makeAgePrevalenceByScenarioPlot(info.genderChoice, info.currentDisease,
						info.currentYear, info.differencePlot, info.numbers);
			if ((info.plotType == 0) && info.axisIsAge)
				chart = output.makeAgePrevalenceByRiskFactorPlots(info.genderChoice,
						info.currentScen, info.currentDisease, info.currentYear, info.differencePlot,
						info.numbers);
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


