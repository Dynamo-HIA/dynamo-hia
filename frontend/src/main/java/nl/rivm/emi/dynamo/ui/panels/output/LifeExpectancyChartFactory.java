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
public final class LifeExpectancyChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoPlotFactory plotFactory;
	public LifeExpectancyChartFactory(DynamoPlotFactory plotFactory){this.plotFactory=plotFactory;
	}
	/**
	 * @param output2
	 */
    public void setParams(ScenarioParameters params) {
		
		this.plotFactory.setParams(params);
	};
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	
		JFreeChart chart = null;
		switch (info.currentDisease) {
		case 0:
			if (info.Sullivan ) chart = plotFactory.makeYearLifeExpectancyPlot(info.currentYear, info.currentAge, info.differencePlot,info.blackAndWhite);
			else chart = plotFactory.makeCohortLifeExpectancyPlot(info.currentAge, info.differencePlot,info.blackAndWhite, info.cumulative);
			break;
		default:
			if (info.Sullivan ) chart =
				plotFactory.makeYearHealthyLifeExpectancyPlot(info.currentYear, info.currentAge, info.currentDisease - 3, info.differencePlot,info.blackAndWhite,info.oldSullivan);
			
				else chart = plotFactory.makeCohortHealthyLifeExpectancyPlot(
				 info.currentAge, info.currentDisease - 3,info.blackAndWhite, info.cumulative);
			break;
		}
		
	((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


