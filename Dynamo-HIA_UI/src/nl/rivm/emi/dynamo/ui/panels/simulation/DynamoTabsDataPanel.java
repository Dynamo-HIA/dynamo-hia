package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.exceptions.DynamoScenarioException;
import nl.rivm.emi.dynamo.output.DynamoOutputFactory;
import nl.rivm.emi.dynamo.output.ErrorMessageWindow;
import nl.rivm.emi.dynamo.output.Output_ChangeScenarioTab;
import nl.rivm.emi.dynamo.output.Output_DiseaseTab;
import nl.rivm.emi.dynamo.output.Output_LifeExpTab;
import nl.rivm.emi.dynamo.output.Output_PyramidTab;
import nl.rivm.emi.dynamo.output.Output_RiskFactorTab;
import nl.rivm.emi.dynamo.output.Output_SurvivalTab;
import nl.rivm.emi.dynamo.output.Output_WriteOutputTab;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DynamoTabsDataPanel extends Composite {

	protected DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private BaseNode selectedNode;
	
	public DynamoTabsDataPanel(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext,			
			HelpGroup helpGroup) {
		// TODO Auto-generated constructor stub
		super(parent, SWT.NONE);
		this.myParent = parent;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.selectedNode = selectedNode;
		//output = new DynamoOutputFactory(scen,  pop);
		makeDynamoTabsDisplay(parent);
	}

	
	/**
	 * Create the 5 tabfolders
	 * 
	 */
	public void makeDynamoTabsDisplay(Composite parent)  {

		/* tab for pyramid plots */
		TabFolder tabFolder1 = new TabFolder(parent, SWT.FILL);

		tabFolder1.setLayout(new FillLayout());
		tabFolder1.setBounds(10, 10, 730, 580);

		/* tab for changing the parameters of the scenarios */
		/*
		final Output_PyramidTab tab0 = new Output_PyramidTab(tabFolder1, output);
		final Output_DiseaseTab tab1 = new Output_DiseaseTab(tabFolder1, output);
		final Output_RiskFactorTab tab2 = new Output_RiskFactorTab(tabFolder1,
				output);
		final Output_LifeExpTab tab3 = new Output_LifeExpTab(tabFolder1, output);
		final Output_SurvivalTab tab4 = new Output_SurvivalTab(tabFolder1, output);
		final Output_WriteOutputTab tab5 = new Output_WriteOutputTab(
				outputShell, baseDir, tabFolder1, output);
		final Output_ChangeScenarioTab tab6 = new Output_ChangeScenarioTab(
				tabFolder1, output);*/

		final RiskFactorTab tab0 = new RiskFactorTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		final RiskFactorTab tab1 = new RiskFactorTab(tabFolder1, dynamoSimulationObject, 
				dataBindingContext, selectedNode, theHelpGroup);
		
		//parent.open();
		tabFolder1.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
                TabItem item = (TabItem) event.item;
	            String tabId=item.getText();
			    if (tabId=="Risk Factor") tab0.redraw();
			    if (tabId=="Risk Factor Second") tab1.redraw();
			    /*
			    if (tabId=="disease plots") tab1.redraw();
			    if (tabId=="riskfactor plots") tab2.redraw();
			    if (tabId=="life expectancy plots") tab3.redraw();*/
        }

	    });

	}
	
	
}
