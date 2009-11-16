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
public final class PyramidChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoOutputFactory output;
	public PyramidChartFactory(DynamoOutputFactory output){this.output=output;}
	/**
	 * @param output2
	 */
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	

			JFreeChart chart=null;
			if (info.currentDisease == 0)
				chart = output.makePyramidChart(	info.currentScen ,
						info.currentYear,info.differencePlot );
			else
				chart = output.makePyramidChartIncludingDisease(
						info.currentScen ,
						info.currentYear,info.currentDisease - 3,info.differencePlot);

			

			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).redraw();
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


