/**
 * 
 */
package nl.rivm.emi.dynamo.output;

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
					for (int j = 0; j < childControls.length; j++)
					{	((Combo) childControls[j]).setItems(names);
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

		// TODO also let the age-list run from select year in case there are no
		// newborns

	}
}
