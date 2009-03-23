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
			chart = output.makeLifeExpectancyPlot(info.currentYear);
			break;
		default:
			chart = output.makeHealthyLifeExpectancyPlot(
					info.currentYear, info.currentDisease - 2);
			break;
		}
		
	((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


