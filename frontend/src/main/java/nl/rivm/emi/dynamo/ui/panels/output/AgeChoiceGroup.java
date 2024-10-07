/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *awt
 */
public class AgeChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	Group listgroup;

	public AgeChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,
			final ButtonStates plotInfo, String[] names) {
		this.chartComposite = chartComposite;
		this.controlComposite = chartComposite;
		this.plotDrawer = plotDrawer;
		this.plotInfo = plotInfo;

		this.listgroup = new Group(controlComposite, SWT.VERTICAL
				| SWT.V_SCROLL);
		this.listgroup.setText("at age:");
		/*
		 * tooltip tekst is added to make it possible to select this widget for
		 * disabling. Do not change this text
		 */
		this.listgroup.setToolTipText("choose age to plot");
		this.listgroup.setEnabled(true);
		GridLayout gridLayoutGroup = new GridLayout();
		gridLayoutGroup.numColumns = 3;
		this.listgroup.setLayout(gridLayoutGroup);

		final Combo combo = new Combo(this.listgroup, SWT.DROP_DOWN
				| SWT.READ_ONLY);

		combo.setItems(names);
		combo.select(0);
		combo.setEnabled(true);

		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.getSource();
				String[] names = combo.getItems();
				if (names[combo.getSelectionIndex()]
						.equalsIgnoreCase("at birth"))
					plotInfo.currentAge = 0;
				else
					plotInfo.currentAge = Integer.parseInt(names[combo
							.getSelectionIndex()]);

				// if (plotInfo.Sullivan && !plotInfo.newborns)
				// plotInfo.currentAge =
				// names[combo.getSelectionIndex()].parseInteger();
				// +plotInfo.currentYear;
				// else plotInfo.currentAge = combo.getSelectionIndex();

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

}
