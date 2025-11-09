/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import nl.rivm.emi.dynamo.global.ScenarioParameters;
import nl.rivm.emi.dynamo.output.DynamoPlotFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.CategoryDataset;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;


/**
 * @author boshuizh
 *
 */
public final class PyramidChartFactory implements PlotDrawer {

	/* (non-Javadoc)
	 * @see nl.rivm.emi.dynamo.output.Action#performAction(nl.rivm.emi.dynamo.output.PlotFormatInfo)
	 */
	DynamoPlotFactory plotFactory;
	
	public void setParams(ScenarioParameters params) {
		
		this.plotFactory.setParams(params);
	};
	
	
	
	public PyramidChartFactory(DynamoPlotFactory factory){this.plotFactory=factory;}
	/**
	 * @param output2
	 */
	
	@Override
	public void drawChartAction(ButtonStates info, Composite composite) {
		
		Log log = LogFactory
		.getLog("nl.rivm.emi.dynamo.PyramidChartFactory");


			
		//	JFreeChart chart=((ChartComposite)composite).getChart();
		//	JFreeChart newChart=((ChartComposite)composite).getChart();
		JFreeChart newChart=null;
			if (info.currentDisease == 0)
				newChart = plotFactory.makePyramidChart(	info.currentScen ,
						info.currentYear,info.differencePlot,info.blackAndWhite );
			else
				newChart = plotFactory.makePyramidChartIncludingDisease(
						info.currentScen ,
						info.currentYear,info.currentDisease - 3,info.differencePlot,info.blackAndWhite);

			
         //   CategoryPlot newPlot=(CategoryPlot) newChart.getPlot();
         //   CategoryDataset newData = newPlot.getDataset();
         //   CategoryPlot plot=(CategoryPlot) chart.getPlot();
         //   plot.setDataset(newData);
		//	 chart.setTitle(newChart.getTitle());
			((ChartComposite) composite).setChart(newChart);
			//((ChartComposite) composite).redraw();
			 ((ChartComposite) composite).forceRedraw();

			

		}
		// TODO Auto-generated method stub
	
	

	}

	


