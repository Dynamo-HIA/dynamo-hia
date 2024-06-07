/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
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
public class SurvivalChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;

	public SurvivalChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,
			final ButtonStates plotInfo) {
		this.chartComposite = chartComposite;
		this.controlComposite = chartComposite;
		this.plotDrawer = plotDrawer;
		this.plotInfo = plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup.setText("outcome:");
		/*
		 * NB the tooltiptext is used to recognized this item for disabling, so
		 * do not change the string!
		 */
		radiogroup.setToolTipText("choose survival, population numbers or mortality");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button mortButton = new Button(radiogroup, SWT.RADIO);
		mortButton.setText("mortality");
		/*
		 * NB tooltip tekst is used to recognized the widget for disabling so do not change
		 * 
		 */
		mortButton.setToolTipText("choose to plot mortality, population numbers or survival");
		mortButton.setSelection(true);
		mortButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				if (button.getSelection()) {
					plotInfo.survival = false;
					plotInfo.population = false;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
					//String[] classNames=new String[output.riskClassnames.length];
					//classNames[0]="all";
					//for (int i = 0; i < output.riskClassnames.length; i++)
					//	classNames[i+1] = output.riskClassnames[i];
					//TODO veranderen in de year selection button ;
					setEnabled(controlComposite,plotInfo);
				}
			}
		}

		));
		Button populationButton = new Button(radiogroup, SWT.RADIO);
		populationButton.setText("population numbers");
		populationButton.setSelection(false);
		// ageButton.setBounds(10,50,20,100);
		populationButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					plotInfo.population = true;
					plotInfo.survival=false;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
                    setEnabled(controlComposite,plotInfo);
				}
			}
		}));
		Button survivalButton = new Button(radiogroup, SWT.RADIO);
		survivalButton.setText("survival");
		survivalButton.setSelection(false);
		// ageButton.setBounds(10,50,20,100);
		survivalButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					plotInfo.survival = true;
					plotInfo.population = false;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
                    setEnabled(controlComposite,plotInfo);
				}
			}
		}));

	}


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
			if (otherControls[i].getToolTipText() == "choose rate or numbers") {
				if (plotInfo.population  ) otherControls[i].setEnabled(false);
				else otherControls[i].setEnabled(true);
				Control[] childControls = ((Composite) otherControls[i])
						.getChildren();
				for (int j = 0; j < childControls.length; j++) {
					if (plotInfo.population  ) childControls[j].setEnabled(false);
					else childControls[j].setEnabled(true);
				}
			}
			
				
			if (otherControls[i].getToolTipText() == "choose age or year in simulation for x-axis") {
				if (plotInfo.survival  ) otherControls[i].setEnabled(false);
				else otherControls[i].setEnabled(true);
				Control[] childControls = ((Composite) otherControls[i])
						.getChildren();
				for (int j = 0; j < childControls.length; j++) {
					if (plotInfo.survival  ) childControls[j].setEnabled(false);
					else childControls[j].setEnabled(true);
				}
			}
			
			

		}
	}
			
}
