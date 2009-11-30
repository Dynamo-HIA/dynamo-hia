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
import org.eclipse.swt.widgets.Group;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public class RiskClassChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	
	/**makes combo box for selection of riskclass, and stores result in field RiskClassChoice of  ButtonStates object
	 * @param controlComposite
	 * @param chartComposite
	 * @param plotDrawer
	 * @param plotInfo
	 * @param riskClassNames
	 */
	public RiskClassChoiceGroup(Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,final ButtonStates plotInfo, String[] riskClassNames) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo= plotInfo;
		
		
		Group listgroup = new Group(controlComposite, SWT.VERTICAL);

		listgroup.setText("risk class:");
		//    
		listgroup.setLayout(new RowLayout(SWT.VERTICAL));

		final Combo combo = new Combo(listgroup, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		combo.setItems(riskClassNames);
		combo.select(plotInfo.riskClassChoice);
		
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.getSource();

				plotInfo.riskClassChoice = combo.getSelectionIndex();
				 plotDrawer.drawChartAction(plotInfo,chartComposite);
			}
		});
	
			
		}
		
		
			

			

	

}
