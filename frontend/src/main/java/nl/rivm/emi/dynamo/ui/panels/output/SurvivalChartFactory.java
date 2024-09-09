/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public final class SurvivalChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoPlotFactory plotFactory;
	public SurvivalChartFactory(DynamoPlotFactory dfact){this.plotFactory=dfact;
	
	}
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
  // for survival age plot does not exists, so ignore this info
				if ((info.survival) &&  info.plotType==1)
					chart = plotFactory.makeSurvivalPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.numbers,info.blackAndWhite);
				
				if ((info.survival && !info.population)&& info.plotType==0)
					chart = plotFactory.makeSurvivalPlotByRiskClass(info.genderChoice,info.currentScen,
							info.differencePlot,info.numbers,info.blackAndWhite);
				
				
				if ((info.population) && !info.axisIsAge && info.plotType==1)

					chart = plotFactory.makeYearPopulationNumberPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot, info.blackAndWhite);

				if ((info.population) && info.axisIsAge && info.plotType==1)
					chart = plotFactory.makeAgePopulationNumberPlotByScenario(info.currentYear,info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.blackAndWhite);
				// geen survival en geen population betekent mortality
				if ((!info.survival && !info.population) && info.axisIsAge && info.plotType==1)
					chart = plotFactory.makeAgeMortalityPlotByScenario(info.currentYear,
							info.genderChoice,info.riskClassChoice-1, info.differencePlot, info.numbers,info.blackAndWhite);
				if ((!info.survival && !info.population) && !info.axisIsAge && info.plotType==1)
					chart = plotFactory.makeYearMortalityPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.numbers,info.blackAndWhite);
				
				if ((!info.survival && !info.population) && info.axisIsAge && info.plotType==0)
					chart = plotFactory.makeAgeMortalityByRiskFactorPlots(info.genderChoice,info.currentScen,info.currentYear,
					 info.differencePlot, info.numbers,info.blackAndWhite);
					
				if ((!info.survival && !info.population) && !info.axisIsAge && info.plotType==0)
					chart = plotFactory.makeYearMortalityByRiskFactorPlots(info.genderChoice,info.currentScen,
							info.differencePlot,info.numbers,info.blackAndWhite);
				
				
				if ((info.population) && !info.axisIsAge && info.plotType==0)

					chart = plotFactory.makeYearPopulationNumberPlotByRiskClass(info.genderChoice,info.currentScen,
							info.differencePlot, info.blackAndWhite);

				if ((info.population) && info.axisIsAge && info.plotType==0)
					chart = plotFactory.makeAgePopulationNumberPlotByRiskClass(info.currentYear,info.genderChoice,info.currentScen,
							info.differencePlot,info.blackAndWhite);
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


