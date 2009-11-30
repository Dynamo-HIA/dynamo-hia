/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 * 
 */
public class YearChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	Group listgroup;

	public YearChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,
			final ButtonStates plotInfo, String[] names) {
		this.chartComposite = chartComposite;
		this.controlComposite = chartComposite;
		this.plotDrawer = plotDrawer;
		this.plotInfo = plotInfo;

		this.listgroup = new Group(controlComposite, SWT.VERTICAL
				| SWT.V_SCROLL);
		this.listgroup.setText("year:");
		/*
		 * tooltip tekst is added to make it possible to select this widget for
		 * disabling. Do not change this text
		 */
		this.listgroup.setToolTipText("choose year to plot");
		this.listgroup.setEnabled(false);
		GridLayout gridLayoutGroup = new GridLayout();
		gridLayoutGroup.numColumns = 3;
		this.listgroup.setLayout(gridLayoutGroup);

		final Combo combo = new Combo(this.listgroup, SWT.DROP_DOWN
				| SWT.READ_ONLY);

		combo.setItems(names);
		combo.select(0);
		combo.setEnabled(false);

		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.getSource();
				plotInfo.currentYear = combo.getSelectionIndex();
				setEnabled(controlComposite, plotInfo);
				plotDrawer.drawChartAction(plotInfo, chartComposite);

			}
		});
	}

	public void disable() {
		this.listgroup.setEnabled(false);
	}

	public void enable() {
		this.listgroup.setEnabled(true);
	}

	private void setEnabled(final Composite controlComposite,
			ButtonStates plotInfo) {
		Control[] otherControls = controlComposite.getChildren();
		for (int i = 0; i < otherControls.length; i++) {

			if (otherControls[i].getToolTipText() == "choose age to plot") {
				
				/*  age must be larger than year (because there are
				 * no newborns, thus no persons below this age). Also the maximum is set at 95, as above 
				 * the calculations are not based on simulation and thus not very useful; 
				 * also the age must be in the range minage+year and min(maxage+year,95)
				 */
				int maxAgeHere=Math.min(plotInfo.currentYear+plotInfo.maxAge,95);
				boolean noValues=false;
				/* if there is a minimum age, and there are newborns coming in the cohort,
				
				* there is an empty part in the population (no persons between the minimum age and the
				* first cohort of newborns.
				* This is handled as if there are no newborns
				*/
				if (plotInfo.Sullivan && (!plotInfo.newborns|| plotInfo.minAge>0) ) {
					if (plotInfo.currentAge < plotInfo.currentYear+plotInfo.minAge &&
							plotInfo.currentYear+plotInfo.minAge <= maxAgeHere)
			         plotInfo.currentAge = plotInfo.currentYear+plotInfo.minAge;
					
					if (plotInfo.currentAge > maxAgeHere  &&
							maxAgeHere >= plotInfo.currentYear+plotInfo.minAge)

						plotInfo.currentAge = maxAgeHere;
					if ( plotInfo.currentYear+plotInfo.minAge >	maxAgeHere) noValues=true;
					
					
					String[] names = new String[1];
					names[0] = "no valid ages";
					if (!noValues) {
						names = new String[maxAgeHere
								- plotInfo.currentYear -plotInfo.minAge+ 1];

						for (int age = plotInfo.currentYear+plotInfo.minAge; age <= maxAgeHere; age++) {
							names[age - plotInfo.currentYear-plotInfo.minAge] = ((Integer) age)
									.toString();
							if (age == 0)
								names[age - plotInfo.currentYear-plotInfo.minAge] = "at birth";
						}
					}
					Control[] childControls = ((Composite) otherControls[i])
							.getChildren();
					for (int j = 0; j < childControls.length; j++) {
						((Combo) childControls[j]).setItems(names);
						if (names.length > 1)
							
							((Combo) childControls[j])
									.select(plotInfo.currentAge
											- plotInfo.currentYear-plotInfo.minAge);
							
						else
						{	((Combo) childControls[j]).select(0);
						plotInfo.currentAge=100;}
					}

				} else { /* newborns or no sullivan */

					String[] names = new String[plotInfo.maxAge];
                     
					int minimum=plotInfo.minAge;
					
					for (int age =minimum ; age < plotInfo.maxAge; age++) {
						names[age-minimum] = ((Integer) age).toString();
						if (age == 0)
							names[age-minimum] = "at birth";
					}

					Control[] childControls = ((Composite) otherControls[i])
							.getChildren();
					for (int j = 0; j < childControls.length; j++) {
						((Combo) childControls[j]).setItems(names);
						((Combo) childControls[j]).select(plotInfo.currentAge);
					}
				}
			}
		}

		// TODO also let the age-list run from select year in case there are no
		// newborns

	}
}
