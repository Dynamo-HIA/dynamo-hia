/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public class DifferenceChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	
	public DifferenceChoiceGroup(Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer, final ButtonStates plotInfo) {
		this.chartComposite=chartComposite;
		this.controlComposite=controlComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo=plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);
		radiogroup.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);
	
		Button rateButton = new Button(radiogroup, SWT.RADIO);
		rateButton.setText("absolute value for scenario");
		rateButton.setSelection(true);

		rateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.differencePlot = false;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
				}

			}
		}));
		Button differenceButton = new Button(radiogroup, SWT.RADIO);
		differenceButton.setText("difference with reference scenario");
		// ageButton.setBounds(10,50,20,100);
		differenceButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
		

					plotInfo.differencePlot = true;
					plotDrawer.drawChartAction(plotInfo,chartComposite);

				}

			}
		}));			
		}
		
	


			

	

}
