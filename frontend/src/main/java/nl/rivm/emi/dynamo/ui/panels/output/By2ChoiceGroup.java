/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;


/**
 * @author boshuizh
 * 
 */
public class By2ChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;

	public By2ChoiceGroup(final Composite controlComposite,
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
