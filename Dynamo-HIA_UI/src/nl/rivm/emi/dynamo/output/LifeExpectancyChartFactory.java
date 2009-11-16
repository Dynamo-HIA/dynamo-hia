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
public final class LifeExpectancyChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoOutputFactory output;
	public LifeExpectancyChartFactory(DynamoOutputFactory output){this.output=output;
	}
	/**
	 * @param output2
	 */
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	
		JFreeChart chart = null;
		switch (info.currentDisease) {
		case 0:
			if (info.Sullivan) chart = output.makeYearLifeExpectancyPlot(info.currentYear, info.currentAge, info.differencePlot);
			else chart = output.makeCohortLifeExpectancyPlot(info.currentAge, info.differencePlot);
			break;
		default:
			if (info.Sullivan) chart =
				output.makeYearHealthyLifeExpectancyPlot(info.currentYear, info.currentAge, info.currentDisease - 3, info.differencePlot);
			else chart = output.makeCohortHealthyLifeExpectancyPlot(
				 info.currentAge, info.currentDisease - 3);
			break;
		}
		
	((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


