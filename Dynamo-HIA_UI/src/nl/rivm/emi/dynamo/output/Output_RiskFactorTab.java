/**
 * 
 */
package nl.rivm.emi.dynamo.output;

import nl.rivm.emi.dynamo.exceptions.DynamoOutputException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author boshuizh
 */
public class Output_RiskFactorTab {

	private TabFolder tabFolder;
	private DynamoOutputFactory output;
	private ButtonStates plotInfo;
	private RiskFactorChartFactory factory;
	private Composite plotComposite;

	public Output_RiskFactorTab(TabFolder tabfolder, DynamoOutputFactory output) {
		this.tabFolder = tabfolder;
		this.output = output;
		this.factory = new RiskFactorChartFactory(output);

		makeIt();
	}

	public void makeIt() {
		/* put the default plot information in the object plotInfo */
		plotInfo = new ButtonStates();
		plotInfo.currentScen = 0;
		if (output.getNScen() > 0)
			plotInfo.currentScen = 1;
		plotInfo.currentDisease = 0;
		plotInfo.currentYear = 0;
		plotInfo.plotType = 1;
		plotInfo.differencePlot = false;
		plotInfo.axisIsAge = false;
		plotInfo.numbers = false;
		plotInfo.genderChoice = 2;
		/*
		 * plotComposite is the highest level composite in the folder it has to
		 * children: control composite containing the controls, and a
		 * chartcomposite containing the plot
		 */
		this.plotComposite = new Composite(tabFolder, SWT.FILL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		plotComposite.setLayout(gridLayout);

		// control composite contains the controls

		Composite controlComposite = new Composite(plotComposite, SWT.NONE);
		GridLayout gridLayoutControl = new GridLayout();
		gridLayoutControl.numColumns = 1;
		GridData controlData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

		controlComposite.setLayout(gridLayoutControl);
		controlComposite.setLayoutData(controlData);

		/* draw chart for the startup-situation */
		final ChartComposite chartComposite = new ChartComposite(plotComposite,
				SWT.NONE, null, true);
		factory.drawChartAction(plotInfo, chartComposite);

		GridData chartData = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		chartComposite.setLayoutData(chartData);
		/* draw the buttons */

		new AxisChoiceGroup(controlComposite, chartComposite, factory, plotInfo);
		new NumberChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo);
		new DifferenceChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo);

		String[] yearNames = new String[output.getStepsInRun() + 1];
		for (int i = 0; i < output.getStepsInRun() + 1; i++)
			yearNames[i] = ((Integer) (output.getStartYear() + i)).toString();
		new RiskClassChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo, output.getRiskClassnames());
		new YearChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo, yearNames);
		new GenderChoiceGroup(controlComposite, chartComposite, factory,
				plotInfo);

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("riskfactor plots");
		item.setControl(plotComposite);
		/*
		 * item2.addListener(SWT.Selection, new Listener() {
		 * 
		 * public void handleEvent(Event arg0) { JFreeChart chart =
		 * makeDiseaseChart(); chartComposite2.setChart(chart);
		 * chartComposite2.forceRedraw();
		 * 
		 * } });
		 */
	}

	public void redraw() {
		Control[] subcomp = plotComposite.getChildren();
		factory.drawChartAction(plotInfo, (ChartComposite) subcomp[1]);
		plotComposite.redraw();
	}
}