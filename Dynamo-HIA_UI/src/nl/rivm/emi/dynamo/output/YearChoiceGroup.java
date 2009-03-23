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
public class YearChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	Group listgroup ;
	
	public YearChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer, final ButtonStates plotInfo,String[] names) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo=plotInfo;
		
	
			this.listgroup = new Group(controlComposite, SWT.VERTICAL
					| SWT.V_SCROLL);
			this.listgroup.setText("year:");
			/* tooltip tekst is added to make it possible to select this widget for
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
					 plotDrawer.drawChartAction(plotInfo,chartComposite);
					 
				}
			});
		}
		
		
	public void disable(){
		this.listgroup.setEnabled(false);
	}
	
	public void enable(){this.listgroup.setEnabled(true);}

}		

			

	

