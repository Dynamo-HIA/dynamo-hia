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
			 */if ((info.survival) && !info.axisIsAge)

					chart = plotFactory.makeSurvivalPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot, info.numbers,info.blackAndWhite);

				if ((info.survival) && info.axisIsAge)
					chart = plotFactory.makeSurvivalPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.numbers,info.blackAndWhite);
				if ((!info.survival) && info.axisIsAge)
					chart = plotFactory.makeAgeMortalityPlotByScenario(info.currentYear,
							info.genderChoice,info.riskClassChoice-1, info.differencePlot, info.numbers,info.blackAndWhite);
				if ((!info.survival) && !info.axisIsAge)
					chart = plotFactory.makeYearMortalityPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.numbers,info.blackAndWhite);

			
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


