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
public final class PyramidChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoPlotFactory plotFactory;
	public PyramidChartFactory(DynamoPlotFactory factory){this.plotFactory=factory;}
	/**
	 * @param output2
	 */
	
	public void drawChartAction(ButtonStates info,  final Composite composite) {
		
	

			JFreeChart chart=null;
			if (info.currentDisease == 0)
				chart = plotFactory.makePyramidChart(	info.currentScen ,
						info.currentYear,info.differencePlot,info.blackAndWhite );
			else
				chart = plotFactory.makePyramidChartIncludingDisease(
						info.currentScen ,
						info.currentYear,info.currentDisease - 3,info.differencePlot,info.blackAndWhite);

			

			
			((ChartComposite) composite).setChart(chart);
			((ChartComposite) composite).redraw();
			((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub

	}

	


