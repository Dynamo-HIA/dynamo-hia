/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class RiskFactorTab  {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private DynamoSimulationObject modelObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;
	
	private TabFolder tabFolder;
	//ChartComposite chartComposite;
	private Composite plotComposite;

	/**
	 * @param tabfolder
	 * @param output
	 */
	public RiskFactorTab(TabFolder tabfolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) {
		this.tabFolder = tabfolder;
		this.dataBindingContext = dataBindingContext; 
		this.modelObject = dynamoSimulationObject;
		this.helpGroup = helpGroup;
		this.selectedNode = selectedNode;
		makeIt();
	}
	
	/**
	 * makes the tabfolder
	 */
	public void makeIt(){
		this.plotComposite = new Group(this.tabFolder, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		this.plotComposite.setLayout(formLayout);
		this.plotComposite.setBackground(new Color(null, 0xff, 0xff,0xff)); //White
		
		RiskFactorSelectionGroup riskFactorSelectionGroup =
			new RiskFactorSelectionGroup(this.plotComposite,
					selectedNode, helpGroup);		
		
		RiskFactorResultGroup riskFactorResultGroup =
			new RiskFactorResultGroup(this.plotComposite,
					selectedNode, helpGroup,
					riskFactorSelectionGroup.group,
					riskFactorSelectionGroup.getDropDownModifyListener());

		TabItem item = new TabItem(this.tabFolder, SWT.NONE);
		item.setText("Risk Factor");
		item.setControl(this.plotComposite);
		
	}
	/**
	 * 
	 */
	public void redraw(){
		log.debug("REDRAW THIS");
		Control[] subcomp= this.plotComposite.getChildren();
		////this.factory.drawChartAction(this.plotInfo, (ChartComposite) subcomp[1]);
		this.plotComposite.redraw();
		
	}
		}