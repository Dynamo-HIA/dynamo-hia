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
public class AxisChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	
	public AxisChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer, final ButtonStates plotInfo) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo=plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup.setText("X-axis:");
	/*
	 * NB the tooltiptext is used to recognized this item for disabling, so do not change the string!
	 * 
	 * 
	 */
		radiogroup.setToolTipText("choose age or year in simulation for x-axis");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button yearButton = new Button(radiogroup, SWT.RADIO);
		yearButton.setText("Year");
		yearButton.setSelection(true);

		yearButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.axisIsAge = false;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
					setEnabled(controlComposite, plotInfo );
				}

			}
		}));
		Button ageButton = new Button(radiogroup, SWT.RADIO);
		ageButton.setText("Age");
		// ageButton.setBounds(10,50,20,100);
		ageButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.axisIsAge = true;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
					setEnabled(controlComposite, plotInfo );
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

		}
	}
			

			

	

}
