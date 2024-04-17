package nl.rivm.emi.dynamo.ui.panels.output;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
//ND: not experimental anymore
import org.jfree.chart.swt.ChartComposite;

public class AutoRunButton {
	

	Composite controlComposite;
	ChartComposite chartComposite;
	PlotDrawer plotDrawer;
	ButtonStates plotInfo;
	Button button;
	int nStepsInRun=0;
	public AutoRunButton(final Composite controlComposite,
			final ChartComposite chartComposite, final PlotDrawer plotDrawer,final ButtonStates plotInfo, int nStepsInRun) {
		this.chartComposite=chartComposite;
		this.controlComposite=chartComposite;
		this.plotDrawer=plotDrawer;
		this.plotInfo= plotInfo;
		this.nStepsInRun=nStepsInRun;
		
		this.button = new Button(controlComposite, SWT.VERTICAL);
		this.button.setText("autoRun");
		if (this.nStepsInRun==0) disable();
		
		/* NB
		 * the tooltiptext is used to recognize this group later
		 * Do not change it!!!!!
		 */
		this.button.setToolTipText("show changes in population pyramid over the years");
		// label.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		//this.button.setLayoutData(new RowLayout(SWT.VERTICAL));
		
		
		/*
		 * listeners for the combobox
		 */

		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Control[] otherControls = controlComposite.getChildren();
				for (int i = 0; i < otherControls.length; i++) {
					if (otherControls[i].getToolTipText() == "change year of population pyramid") {
						Scale scale=((Scale)otherControls[i]);
						int currentPlace =scale.getSelection();
					while (currentPlace>=0){
						currentPlace =scale.getSelection();
						scale.setSelection(--currentPlace);
						scale.notifyListeners(SWT.Selection, null);
						
						 try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}}

					}
					
						}
					}
				
				
				

			
		});
		
	
			
		}
	public void disable(){
		this.button.setEnabled(false);
	}
}
