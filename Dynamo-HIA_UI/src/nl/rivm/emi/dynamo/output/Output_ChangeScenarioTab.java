/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 */
public class Output_ChangeScenarioTab {

	private TabFolder tabFolder;
	private DynamoOutputFactory output;
	private ButtonStates plotInfo;
	private DiseaseChartFactory factory;

	public Output_ChangeScenarioTab(TabFolder tabfolder,
			DynamoOutputFactory output) {
		this.tabFolder = tabfolder;
		this.output = output;
		this.factory = new DiseaseChartFactory(output);

		makeIt();
	}

	public void makeIt() {

		Composite tabComposite = new Composite(tabFolder, SWT.NONE);

		// plotComposite.setBounds(10,10,720,600);
		GridLayout gridLayout = new GridLayout(SWT.VERTICAL, false);
		gridLayout.numColumns = 8;
		gridLayout.makeColumnsEqualWidth=false;
		tabComposite.setLayout(gridLayout);
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("change scenario settings");
		item.setControl(tabComposite);
		
		/* put headings in the first row */
		
		Label heading1 = new Label(tabComposite, SWT.NONE);
		heading1.setText("Scenario:");
		GridData data1 = new GridData();
		// data4.widthHint = 60;
		data1.horizontalSpan = 1;
		heading1.setLayoutData(data1);
		
		Label heading2 = new Label(tabComposite, SWT.NONE);
		heading2.setText("Succes rate:");
		GridData data2 = new GridData();
		// data2.widthHint = 60;
		data2.horizontalSpan = 2;
		heading2.setLayoutData(data2);
		
		Label heading3 = new Label(tabComposite, SWT.NONE);
		heading3.setText("Minimum Age:");
		GridData data3 = new GridData();
		// data3.widthHint = 60;
		data3.horizontalSpan = 2;
		heading3.setLayoutData(data3);
		
		Label heading4 = new Label(tabComposite, SWT.NONE);
		heading4.setText("Maximum Age:");
		GridData data4 = new GridData();
		// data4.widthHint = 60;
		data4.horizontalSpan = 2;
		heading4.setLayoutData(data4);

		Label heading5 = new Label(tabComposite, SWT.NONE);
		heading5.setText("Target gender:");
		GridData data5 = new GridData();
		// data4.widthHint = 60;
		data5.horizontalSpan = 1;
		heading5.setLayoutData(data5);

		/* put widgets for userinteractions in the next rows (one per scenario) */
		
		/* initialize the arrays with widgets */
		final Label[] label = new Label[output.getNScen()];
		final Slider[] slider1 = new Slider[output.getNScen()];
		final Text[] value1 = new Text[output.getNScen()];
		final Text[] value2 = new Text[output.getNScen()];
		final Text[] value3 = new Text[output.getNScen()];
		final Slider[] slider2 = new Slider[output.getNScen()];
		final Slider[] slider3 = new Slider[output.getNScen()];
		final Combo[] combo = new Combo[output.getNScen()];
		for (int i = 0; i < output.getNScen(); i++) {
			label[i] = new Label(tabComposite, SWT.HORIZONTAL);
			label[i].setText(output.getScenarioNames()[i + 1]);
            label[i].setLayoutData( new GridData());
            
			slider1[i] = new Slider(tabComposite, SWT.HORIZONTAL);
			slider1[i].setMaximum(1050);/* the slider needs 50 for the thump */
			slider1[i].setMinimum(0);
			slider1[i].setIncrement(1);
			slider1[i].setThumb(50);
			slider1[i].setSelection((int) ( 10*output.getSuccesrate()[i]));
			slider1[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			value1[i] = new Text(tabComposite, SWT.BORDER | SWT.SINGLE);
			value1[i].setEditable(false);
			

			value1[i].setText(((Float) output.getSuccesrate()[i]).toString());
			value1[i].setLayoutData(fixedSpace1());
			slider1[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					Slider slider = (Slider) ((SelectionEvent) event)
							.getSource();
					int value = slider.getSelection();
					int currenti = 0;
					for (int i = 0; i < output.getNScen(); i++) {
						if (slider1[i] == slider)
							currenti = i;
					}
					output.setSuccesrate((value/10F), currenti);
					value1[currenti]
							.setText(((Float) output.getSuccesrate()[currenti])
									.toString());
					

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}
			});
			slider2[i] = new Slider(tabComposite, SWT.HORIZONTAL);
			slider2[i].setMaximum(100);/* 10 is needed for thumb */
			slider2[i].setMinimum(0);
			slider2[i].setIncrement(1);
			slider2[i].setPageIncrement(1);
			slider2[i].setThumb(5);
			slider2[i].setSelection((int) output.getMinAge()[i]);
			slider2[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			value2[i] = new Text(tabComposite, SWT.BORDER | SWT.SINGLE);
			value2[i].setEditable(false);
		//	value2[i].setDigits(0);
			value2[i].setLayoutData(fixedSpace2());
			value2[i].setText(((Integer)  output.getMinAge()[i]).toString());

			slider2[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					Slider slider = (Slider) ((SelectionEvent) event)
							.getSource();
					int value = slider.getSelection();
					int currenti = 0;
					for (int i = 0; i < output.getNScen(); i++) {
						if (slider2[i] == slider)
							currenti = i;
					}
					output.setMinAge(value, currenti);
					value2[currenti]
							.setText(((Integer) output.getMinAge()[currenti])
									.toString());
					

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}
			});
			
			slider3[i] = new Slider(tabComposite, SWT.HORIZONTAL);
			slider3[i].setMaximum(100);
			slider3[i].setMinimum(0);
			slider3[i].setIncrement(1);
			slider3[i].setPageIncrement(1);
			slider3[i].setThumb(5);
			slider3[i].setSelection((int) this.output.getMaxAge()[i]);
			
			slider3[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			value3[i] = new Text(tabComposite, SWT.BORDER | SWT.SINGLE);
			value3[i].setEditable(false);
			value3[i].setLayoutData(fixedSpace2());
			value3[i].setText(((Integer) this.output.getMaxAge()[i]).toString());

			slider3[i].addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent event) {
					Slider slider = (Slider) ((SelectionEvent) event)
							.getSource();
					int value = slider.getSelection();
					int currenti = 0;
					for (int i = 0; i < output.getNScen(); i++) {
						if (slider3[i] == slider)
							currenti = i;
					}
					output.setMaxAge(value, currenti);
					value3[currenti]
							.setText(((Integer) output.getMaxAge()[currenti])
									.toString());
					

				}

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}

			});
			
			combo[i] = new Combo(tabComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
			String[] choices = { "men", "women", "both" };
			combo[i].setItems(choices);
			if (output.getInMen()[i] && output.getInWomen()[i])
				combo[i].select(2);
			else if (output.getInMen()[i])
				combo[i].select(0);
			else if (output.getInWomen()[i])
				combo[i].select(1);

			/*
			 * listeners for the lists
			 */

			combo[i].addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					// /Combo comboSel = (Combo) e.getSource();
					// Simpelste is om gewoon alle boxen langslopen en alles te
					// zetten

					int choice;
					for (int i = 0; i < combo.length; i++) {
						choice = combo[i].getSelectionIndex();
						switch (choice) {
						case 0:
							output.setInMen(i, true);
							output.setInWomen(i, false);
							break;

						case 1:
							output.setInMen(i, false);
							output.setInWomen(i, true);
							break;
						case 2:
							output.setInMen(i, true);
							output.setInWomen(i, true);

							break;
						}
					}

				}

			});
		}
	}
/* these methods set the width of the boxes containing the values
 * they are just wide enough to contain all largest value
 * this leaves the rest of the space for the other widgets */
	private GridData fixedSpace1() {
		GridData data = new GridData();
		data.widthHint = 28;
		return data;
	}
	private GridData fixedSpace2() {
		GridData data = new GridData();
		data.widthHint = 13;
		return data;
	}

	public void redraw() {
		Control[] plotcomp = tabFolder.getChildren();
		Control[] subcomp = ((Composite) plotcomp[0]).getChildren();
		factory.drawChartAction(plotInfo, (ChartComposite) subcomp[1]);
	}
}