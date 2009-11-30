/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 * 
 */
public class SullivanChoiceGroup {

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;

	public SullivanChoiceGroup(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,
			final ButtonStates plotInfo) {
		this.chartComposite = chartComposite;
		this.controlComposite = chartComposite;
		this.plotDrawer = plotDrawer;
		this.plotInfo = plotInfo;
		Group radiogroup = new Group(controlComposite, SWT.VERTICAL);
		// radiogroup.setBounds(10,10,200,150);

		radiogroup.setText("type of life expectancy:");
		/*
		 * NB the tooltiptext is used to recognized this item for disabling, so
		 * do not change the string!
		 */
		radiogroup.setToolTipText("choose type of life expectancy");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		radiogroup.setLayout(new RowLayout(SWT.VERTICAL));
		// yearButton.setBounds(10,10,20,100);

		Button cohortButton = new Button(radiogroup, SWT.RADIO);
		cohortButton.setText("cohort");
		/*
		 * NB tooltip tekst is used to recognized the widget for disabling so do not change
		 * 
		 */
		cohortButton.setToolTipText("choose cohort style");
		cohortButton.setSelection(true);
		cohortButton.addListener(SWT.Selection, (new Listener() {
			public void handleEvent(Event event) {
				Button button = (Button) event.widget;
				if (button.getSelection()) {
					plotInfo.Sullivan = false;
					setEnabled(controlComposite,plotInfo);
					plotDrawer.drawChartAction(plotInfo, chartComposite);
					
				}
			}
		}

		));
		Button sullivanButton = new Button(radiogroup, SWT.RADIO);
		sullivanButton.setText("period (Sullivan Method)");
		cohortButton.setToolTipText("choose period life table (with diseases: Sullivan Method)");
		sullivanButton.setSelection(false);
		// ageButton.setBounds(10,50,20,100);
		sullivanButton.addListener(SWT.Selection, (new Listener() {

			public void handleEvent(Event event) {
				if (((Button) event.widget).getSelection()) {
					plotInfo.Sullivan = true;
					setEnabled(controlComposite,plotInfo);
					plotDrawer.drawChartAction(plotInfo, chartComposite);
                   
				}
			}
		}));

	}


	private void setEnabled(final Composite controlComposite,
			ButtonStates plotInfo) {
		Control[] otherControls = controlComposite.getChildren();
		for (int i = 0; i < otherControls.length; i++) {
			
			
			
			if (otherControls[i].getToolTipText() == "choose year to plot") {
				if (plotInfo.Sullivan  ) otherControls[i].setEnabled(true);
				else otherControls[i].setEnabled(false);
				Control[] childControls = ((Composite) otherControls[i])
						.getChildren();
				for (int j = 0; j < childControls.length; j++) {
					if (plotInfo.Sullivan  )childControls[j].setEnabled(true);
					
					
					else childControls[j].setEnabled(false);
				}
			}
			/* this method is called from the sullivan button
			 * if this is called, the previously entered year is used.
			 * 
			 */
			if (otherControls[i].getToolTipText() == "choose age to plot") {
				if (plotInfo.Sullivan && ( !plotInfo.newborns|| plotInfo.minAge>0)) {
                   
                    
                    int maxAgeHere=Math.min(plotInfo.currentYear+plotInfo.maxAge,95);
    				boolean noValues=false;
    				/* if there is a minimum age, and there are newborns coming in the cohort,
    				
    				* there is an empty part in the population (no persons between the minimum age and the
    				* first cohort of newborns.
    				* This is handled as if there are no newborns
    				*/
    				
    					if (plotInfo.currentAge < plotInfo.currentYear+plotInfo.minAge &&
    							plotInfo.currentYear+plotInfo.minAge <= maxAgeHere)
    			         plotInfo.currentAge = plotInfo.currentYear+plotInfo.minAge;
    					
    					if (plotInfo.currentAge > maxAgeHere  &&
    							maxAgeHere >= plotInfo.currentYear+plotInfo.minAge)

    						plotInfo.currentAge = maxAgeHere;
    					if ( plotInfo.currentYear+plotInfo.minAge >	maxAgeHere) noValues=true;
    					
    					String[] names={"no valid ages"};
                    if (!noValues)
                    
					names = new String[maxAgeHere
							- plotInfo.currentYear-plotInfo.minAge+1];
					for (int age = plotInfo.currentYear+plotInfo.minAge; age <= maxAgeHere; age++) {
						names[age - plotInfo.currentYear-plotInfo.minAge] = ((Integer) age)
								.toString();
						if (age == 0)
							names[age - plotInfo.currentYear-plotInfo.minAge] = "at birth";
					}
                   
					Control[] childControls = ((Composite) otherControls[i])
							.getChildren();
					for (int j = 0; j < childControls.length; j++){
						((Combo) childControls[j]).setItems(names);
						if (names.length>1)((Combo) childControls[j]).select(plotInfo.currentAge	- plotInfo.currentYear-plotInfo.minAge);
					else 	((Combo) childControls[j]).select(0);
    				}}
				
				else /* no Sullivan, or (with newborns and minAge=0) or year0: in that case
				 every choice is possible for age minage up to maxage */
				{
					int maxAgeHere=Math.min(plotInfo.maxAge,95);
					String[] names = new String[maxAgeHere+1-plotInfo.minAge];
							
					for (int age = plotInfo.minAge; age <= maxAgeHere; age++) {
						names[age-plotInfo.minAge] = ((Integer) age)
								.toString();
						if (age == 0)
							names[age-plotInfo.minAge ] = "at birth";
					}
					 if (plotInfo.currentAge<plotInfo.minAge) plotInfo.currentAge=plotInfo.minAge;
					 if (plotInfo.currentAge>maxAgeHere) plotInfo.currentAge=maxAgeHere;
					Control[] childControls = ((Composite) otherControls[i])
							.getChildren();
					for (int j = 0; j < childControls.length; j++){
						((Combo) childControls[j]).setItems(names);
						if (names.length>1)((Combo) childControls[j]).select(plotInfo.currentAge	-plotInfo.minAge);
						else 	((Combo) childControls[j]).select(0);
						
				}
			}
				
			
			
			

		}
	}
				
	}}
