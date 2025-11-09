/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;


/**
 * @author boshuizh
 *
 */
public class CumulativeChoiceGroup {
	
	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	
	public CumulativeChoiceGroup(Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer, final ButtonStates plotInfo) {
		this.chartComposite=chartComposite;
		this.controlComposite=controlComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo=plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);
		radiogroup.setText("Life-years gained or lost:");
		/* do not change tooltiptext as this is also used to recognize the button for disabling */
		radiogroup.setToolTipText("Choose whether the years gained or lost are DALYs (effect of 1 year exposure changes), future years (permanent change of exposure) for entire population >= age"+
				  " or future years (permanent change of exposure) of single age cohort");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);
		Button dalyButton = new Button(radiogroup, SWT.RADIO);
		dalyButton.setText("DALY (1 year exposure change)");
	    dalyButton.setSelection(false);
		
		dalyButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.cumulative = 0;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
				}

			}
		}));
		Button cumulButton = new Button(radiogroup, SWT.RADIO);
		cumulButton.setText("for all higher ages");
		cumulButton.setSelection(false);

		cumulButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.cumulative = 1;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
				}

			}
		}));
		Button singleButton = new Button(radiogroup, SWT.RADIO);
		singleButton.setText("only for this age");
		// ageButton.setBounds(10,50,20,100);
		singleButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
		

					plotInfo.cumulative = 2;
					plotDrawer.drawChartAction(plotInfo,chartComposite);

				}

			}
		}));
		
		Button noneButton = new Button(radiogroup, SWT.RADIO);
		noneButton.setText("do not display");
		noneButton.setSelection(true);

		noneButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {

					plotInfo.cumulative = 3;
					plotDrawer.drawChartAction(plotInfo,chartComposite);
				}

			}
		}));
		}
		
	


			

	

}
