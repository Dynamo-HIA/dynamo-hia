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
import org.eclipse.swt.widgets.Group;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public class ScenarioChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	Group combigroup;
	public ScenarioChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,final ButtonStates plotInfo, String[] scenarioNames) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo= plotInfo;
		
		
		this.combigroup = new Group(controlComposite, SWT.VERTICAL);
		this.combigroup.setText("scenario:");
		if (this.plotInfo.plotType==1) this.combigroup.setEnabled(false);
		/* NB
		 * the tooltiptext is used to recognize this group later
		 * Do not change it!!!!!
		 */
		this.combigroup.setToolTipText("choose the scenario to plot");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		this.combigroup.setLayout(new RowLayout(SWT.VERTICAL));
		final Combo combo1 = new Combo(this.combigroup, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		if (this.plotInfo.plotType==1) combo1.setEnabled(false);

		String[] scenNames = scenarioNames;
		combo1.setItems(scenNames);
		if (scenNames.length > 1)
			combo1.select(1);
		else
			combo1.select(0);

		/*
		 * listeners for the combobox
		 */

		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo1 = (Combo) e.getSource();
				plotInfo.currentScen = combo1.getSelectionIndex();
				 plotDrawer.drawChartAction(plotInfo,chartComposite);

			}
		});
		
	
			
		}
		
		
			
	public void disable(){
		this.combigroup.setEnabled(false);
	}
	
	public void enable(){this.combigroup.setEnabled(true);}

}
			

	


