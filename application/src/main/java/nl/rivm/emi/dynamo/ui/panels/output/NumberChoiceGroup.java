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
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public class NumberChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	
	public NumberChoiceGroup(Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer, final ButtonStates plotInfo) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo=plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);
		radiogroup.setText("Y-axis:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setToolTipText("choose rate or numbers");
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button rateButton = new Button(radiogroup, SWT.RADIO);
		rateButton.setText("rate");
		rateButton.setSelection(true);

		rateButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.numbers = false;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
				}

			}
		}));
		Button numberButton = new Button(radiogroup, SWT.RADIO);
		numberButton.setText("number of cases");
		// ageButton.setBounds(10,50,20,100);
		numberButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.numbers = true;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
				}

			}
		}));
	}
			
}
