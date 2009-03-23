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
public class DiseaseChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	
	public DiseaseChoiceGroup(Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,final ButtonStates plotInfo, String[] diseaseNames) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo= plotInfo;
		
		
		Group listgroup = new Group(controlComposite, SWT.VERTICAL);

		listgroup.setText("disease:");
		//    
		listgroup.setLayout(new RowLayout(SWT.VERTICAL));

		final Combo combo = new Combo(listgroup, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		combo.setItems(diseaseNames);
		combo.select(plotInfo.currentDisease);
		
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.getSource();

				plotInfo.currentDisease = combo.getSelectionIndex();
				 plotDrawer.drawChartAction(plotInfo,chartComposite);
			}
		});
	
			
		}
		
		
			

			

	

}
