/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 * 
 */
public class ByChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;

	public ByChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,
			final ButtonStates plotInfo) {
		this.chartComposite = chartComposite;
		this.controlComposite = controlComposite;
		this.plotInfo = plotInfo;
		this.plotDrawer = plotDrawer;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		radiogroup.setText("separate curves:");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		GridLayout gridLayoutGroup = new GridLayout();
		gridLayoutGroup.numColumns = 2;
		radiogroup.setLayout(gridLayoutGroup);
		// yearButton.setBounds(10,10,20,100);

		Button byRiskClassButton = new Button(radiogroup, SWT.RADIO);
		byRiskClassButton.setText("by riskclass");
		byRiskClassButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.plotType = 0;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
					setEnabled(controlComposite,plotInfo );
					}

				}

			}
		));

		Button byScenarioButton = new Button(radiogroup, SWT.RADIO);
		byScenarioButton.setText("by scenario");
		byScenarioButton.setSelection(true);
		// ageButton.setBounds(10,50,20,100);
		byScenarioButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.plotType = 1;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
					setEnabled(controlComposite,plotInfo);
				}
			}

			

		}));

		Button bySexButton = new Button(radiogroup, SWT.RADIO);
		bySexButton.setText("by gender");

		// ageButton.setBounds(10,50,20,100);
		bySexButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.plotType = 2;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
					setEnabled(controlComposite, plotInfo );
				}

			}
		}));

	}
/**
 * @param controlComposite
 */
private void setEnabled(final Composite controlComposite,
		ButtonStates plotInfo) {
	Control[] otherControls = controlComposite.getChildren();
	for (int i = 0; i < otherControls.length; i++) {
		if (otherControls[i].getToolTipText() == "choose the scenario to plot") {
			if (plotInfo.plotType==1) otherControls[i].setEnabled(false);
			else otherControls[i].setEnabled(true);
			Control[] childControls = ((Composite) otherControls[i])
					.getChildren();
			for (int j = 0; j < childControls.length; j++) {
				if (plotInfo.plotType==1)childControls[j].setEnabled(false);
				else childControls[j].setEnabled(true);
			}
		}
		if (otherControls[i].getToolTipText() == "choose gender to plot") {
			if (plotInfo.plotType==2) otherControls[i].setEnabled(false);
			else otherControls[i].setEnabled(true);
			Control[] childControls = ((Composite) otherControls[i])
					.getChildren();
			for (int j = 0; j < childControls.length; j++) {if (plotInfo.plotType==2) 
				childControls[j].setEnabled(false);else childControls[j].setEnabled(true);
			}
		}
		
		if (otherControls[i].getToolTipText() == "choose year to plot") {
			if (plotInfo.axisIsAge  ) otherControls[i].setEnabled(true);
			else otherControls[i].setEnabled(false);
			Control[] childControls = ((Composite) otherControls[i])
					.getChildren();
			for (int j = 0; j < childControls.length; j++) {
				if (plotInfo.axisIsAge  ) childControls[j].setEnabled(true);
				else childControls[j].setEnabled(false);
			}
		}

	}
}

}
