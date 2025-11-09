/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.global.ScenarioParameters;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public final class IncidenceChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoPlotFactory plotFactory;
	public IncidenceChartFactory(DynamoPlotFactory output){this.plotFactory=output;}
	
    public void setParams(ScenarioParameters params) {
		
		this.plotFactory.setParams(params);
	};
	
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
				chart = plotFactory.makeYearDiseaseByGenderPlot(info.currentScen,
						info.currentDisease, info.differencePlot, info.numbers,info.blackAndWhite, false);
			if ((info.plotType == 1) && !info.axisIsAge)
				chart = plotFactory.makeYearDiseaseByScenarioPlots(info.genderChoice,
						info.currentDisease, info.differencePlot, info.numbers,info.blackAndWhite, false);
			if ((info.plotType == 0) && !info.axisIsAge)
				chart = plotFactory.makeYearDiseaseByRiskFactorPlots(info.genderChoice,
						info.currentScen, info.currentDisease, info.differencePlot, info.numbers,info.blackAndWhite, false);
			if ((info.plotType == 2) && info.axisIsAge)
				chart = plotFactory.makeAgeDiseaseByGenderPlot(info.currentScen,
						info.currentDisease, info.currentYear, info.differencePlot, info.numbers,info.blackAndWhite, false);
			if ((info.plotType == 1) && info.axisIsAge)
				chart = plotFactory.makeAgeDiseaseByScenarioPlot(info.genderChoice, info.currentDisease,
						info.currentYear, info.differencePlot, info.numbers,info.blackAndWhite,false);
			if ((info.plotType == 0) && info.axisIsAge)
				chart = plotFactory.makeAgeDiseaseByRiskFactorPlots(info.genderChoice,
						info.currentScen, info.currentDisease, info.currentYear, info.differencePlot,
						info.numbers,info.blackAndWhite, false);
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


