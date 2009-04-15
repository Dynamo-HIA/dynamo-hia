/**
 * 
 */
package nl.rivm.emi.dynamo.output;

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
public class SullivanChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;

	public SullivanChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,
			final ButtonStates plotInfo) {
		this.chartComposite = chartComposite;
		this.controlComposite = chartComposite;
		this.plotDrawer = plotDrawer;
		this.plotInfo = plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup.setText("type of life expectancy:");
		/*
		 * NB the tooltiptext is used to recognized this item for disabling, so
		 * do not change the string!
		 */
		radiogroup.setToolTipText("choose type of life expectancy");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button cohortButton = new Button(radiogroup, SWT.RADIO);
		cohortButton.setText("cohort");
		/*
		 * NB tooltip tekst is used to recognized the widget for disabling so do not change
		 * 
		 */
		cohortButton.setToolTipText("choose cohort style");
		cohortButton.setSelection(true);
		cohortButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				if (button.getSelection()) {
					plotInfo.Sullivan = false;
					plotDrawer.drawChartAction(plotInfo, chartComposite);
					setEnabled(controlComposite,plotInfo);
				}
			}
		}

		));
		Button sullivanButton = new Button(radiogroup, SWT.RADIO);
		sullivanButton.setText("period (Sullivan Method)");
		cohortButton.setToolTipText("choose period life table (with diseases: Sullivan Method)");
		sullivanButton.setSelection(false);
		// ageButton.setBounds(10,50,20,100);
		sullivanButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					plotInfo.Sullivan = true;
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
			
			
			
			if (otherControls[i].getToolTipText() == "choose year to plot") {
				if (plotInfo.Sullivan  ) otherControls[i].setEnabled(true);
				else otherControls[i].setEnabled(false);
				Control[] childControls = ((Composite) otherControls[i])
						.getChildren();
				for (int j = 0; j < childControls.length; j++) {
					if (plotInfo.Sullivan  )childControls[j].setEnabled(true);
					
					
					else childControls[j].setEnabled(false);
				}
			}
			
			if (otherControls[i].getToolTipText() == "choose age to plot") {
				if (plotInfo.Sullivan && !plotInfo.newborns) {

					String[] names = new String[plotInfo.maxAge
							- plotInfo.currentYear];
					for (int age = plotInfo.currentYear; age < plotInfo.maxAge; age++) {
						names[age - plotInfo.currentYear] = ((Integer) age)
								.toString();
						if (age == 0)
							names[age - plotInfo.currentYear] = "at birth";
					}

					Control[] childControls = ((Composite) otherControls[i])
							.getChildren();
					for (int j = 0; j < childControls.length; j++){
						((Combo) childControls[j]).setItems(names);
						((Combo) childControls[j]).select(0);}
				}
				
				else 
				{

					String[] names = new String[plotInfo.maxAge];
							
					for (int age = 0; age < plotInfo.maxAge; age++) {
						names[age] = ((Integer) age)
								.toString();
						if (age == 0)
							names[age ] = "at birth";
					}

					Control[] childControls = ((Composite) otherControls[i])
							.getChildren();
					for (int j = 0; j < childControls.length; j++){
						((Combo) childControls[j]).setItems(names);
						((Combo) childControls[j]).select(0);}
				}
			}
				
			
			
			

		}
	}
			
}
