/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jfree.chart.JFreeChart;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

/**
 * @author boshuizh
 *
 */
public class GenderChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	Group listgroup;
	
	public GenderChoiceGroup(Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,final ButtonStates plotInfo) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo=plotInfo;
		
		this.listgroup = new Group(controlComposite, SWT.VERTICAL);
		listgroup.setText("gender:");
		/*
		 * tooltiptext is used to recognise the widget. So do not change this text
		 */
		listgroup.setToolTipText("choose gender to plot");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		listgroup.setLayout(new RowLayout(SWT.VERTICAL));
		final Combo combo = new Combo(listgroup, SWT.DROP_DOWN
				| SWT.READ_ONLY);

		String[] choices = { "men", "women", "both" };
		combo.setItems(choices);
		combo.select(2);
		
        
		/*
		 * listeners for the lists
		 */

		combo.addSelectionListener(new SelectionAdapter() {
			

			public void widgetSelected(SelectionEvent e) {
				@SuppressWarnings("unused")
				Combo combo4 = (Combo) e.getSource();
				//combo4.getParent()
				 plotInfo.genderChoice = combo.getSelectionIndex();
                 plotDrawer.drawChartAction(plotInfo,chartComposite);
				
				
				
			}

			@SuppressWarnings("unused")
			private JFreeChart makeChart() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
	public void disable(){
		this.listgroup.setEnabled(false);
	}
	
	public void enable(){this.listgroup.setEnabled(true);}

}
