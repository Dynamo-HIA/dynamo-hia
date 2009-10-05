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
public final class SurvivalChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoOutputFactory output;
	public SurvivalChartFactory(DynamoOutputFactory output){this.output=output;}
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

					chart = output.makeSurvivalPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot, info.numbers);

				if ((info.survival) && info.axisIsAge)
					chart = output.makeSurvivalPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.numbers);
				if ((!info.survival) && info.axisIsAge)
					chart = output.makeAgeMortalityPlotByScenario(info.currentYear,
							info.genderChoice,info.riskClassChoice-1, info.differencePlot, info.numbers);
				if ((!info.survival) && !info.axisIsAge)
					chart = output.makeYearMortalityPlotByScenario(info.genderChoice,info.riskClassChoice-1,
							info.differencePlot,info.numbers);

			
			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


