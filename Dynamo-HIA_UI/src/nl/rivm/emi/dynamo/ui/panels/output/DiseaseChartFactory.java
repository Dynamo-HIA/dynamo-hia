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
public final class DiseaseChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoPlotFactory plotFactory;
	public DiseaseChartFactory(DynamoPlotFactory output){this.plotFactory=output;}
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
				chart = plotFactory.makeYearPrevalenceByGenderPlot(info.currentScen,
						info.currentDisease, info.differencePlot, info.numbers,info.blackAndWhite);
			if ((info.plotType == 1) && !info.axisIsAge)
				chart = plotFactory.makeYearPrevalenceByScenarioPlots(info.genderChoice,
						info.currentDisease, info.differencePlot, info.numbers,info.blackAndWhite);
			if ((info.plotType == 0) && !info.axisIsAge)
				chart = plotFactory.makeYearPrevalenceByRiskFactorPlots(info.genderChoice,
						info.currentScen, info.currentDisease, info.differencePlot, info.numbers,info.blackAndWhite);
			if ((info.plotType == 2) && info.axisIsAge)
				chart = plotFactory.makeAgePrevalenceByGenderPlot(info.currentScen,
						info.currentDisease, info.currentYear, info.differencePlot, info.numbers,info.blackAndWhite);
			if ((info.plotType == 1) && info.axisIsAge)
				chart = plotFactory.makeAgePrevalenceByScenarioPlot(info.genderChoice, info.currentDisease,
						info.currentYear, info.differencePlot, info.numbers,info.blackAndWhite);
			if ((info.plotType == 0) && info.axisIsAge)
				chart = plotFactory.makeAgePrevalenceByRiskFactorPlots(info.genderChoice,
						info.currentScen, info.currentDisease, info.currentYear, info.differencePlot,
						info.numbers,info.blackAndWhite);
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


