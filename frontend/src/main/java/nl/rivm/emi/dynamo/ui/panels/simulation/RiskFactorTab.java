/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;



import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.global.BaseNode;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 * Defines the nested risk factor tab
 * 
 * @author schutb
 *
 */
public class RiskFactorTab {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private DynamoSimulationObject dynamoSimulationObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;
	
	private TabFolder upperTabFolder;
	private Set<String> selections;
	private Composite plotComposite;

	
	private DynamoTabDataManager dynamoTabDataManager;
	
	/**
	 * @param upperTabfolder
	 * @param output
	 * @throws ConfigurationException 
	 */
	public RiskFactorTab(
			TabFolder upperTabfolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, 
			BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		
		this.upperTabFolder = upperTabfolder;
		this.dataBindingContext = dataBindingContext; 
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.helpGroup = helpGroup;
		this.selectedNode = selectedNode;
		
		this.selections = new LinkedHashSet<String>();
		
		Set<String> defaultTabKeyValues = this.getConfigurations();
		for (String defaultTabKeyValue : defaultTabKeyValues) {
			Set<String> keyValues = new LinkedHashSet<String>();
			this.selections.add(defaultTabKeyValue);
		}
		
		try {
			makeIt();
		} catch (NoMoreDataException e) {
			Shell messageShell=new Shell(upperTabFolder.getDisplay());
			MessageBox messageBox=new MessageBox(messageShell, SWT.OK);
			messageBox.setMessage(e.getMessage()+" \nSimulation is corrupted and not runnable");				
			
			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();
			}

			
			
		} catch (DynamoNoValidDataException e) {
			Shell messageShell=new Shell(upperTabFolder.getDisplay());
			MessageBox messageBox=new MessageBox(messageShell, SWT.OK);
			messageBox.setMessage(e.getMessage()+" \nSimulation is corrupted and not runnable");				
			
			if (messageBox.open() == SWT.OK) {
				messageShell.dispose();
			}
			e.printStackTrace();
		}
		

	}

	private Set<String> getConfigurations() {
		LinkedHashMap<String, TabRiskFactorConfigurationData> configurations = 
			(LinkedHashMap<String, TabRiskFactorConfigurationData>) this.dynamoSimulationObject.getRiskFactorConfigurations();
		return configurations.keySet();
	}

	/**
	 * makes the tabfolder
	 * @throws ConfigurationException 
	 * @throws NoMoreDataException 
	 * @throws DynamoNoValidDataException 
	 */
	public void makeIt() throws ConfigurationException, NoMoreDataException, DynamoNoValidDataException{
		this.setPlotComposite(new Group(this.upperTabFolder, SWT.NONE));
		FormLayout formLayout = new FormLayout();
		this.getPlotComposite().setLayout(formLayout);
		//this.plotComposite.setBackground(new Color(null, 0xff, 0xff,0xff)); //White
		
		this.dynamoTabDataManager =
			new RiskFactorTabDataManager(selectedNode, 
					dynamoSimulationObject,
					this.selections, this);
		
		RiskFactorSelectionGroup riskFactorSelectionGroup =
			new RiskFactorSelectionGroup( 
					this.selections, this.getPlotComposite(),
					selectedNode, helpGroup,
					dynamoTabDataManager
					);
		
		RiskFactorResultGroup riskFactorResultGroup =
			new RiskFactorResultGroup(this.selections, this.getPlotComposite(),
					selectedNode, helpGroup,
					riskFactorSelectionGroup.group,
					riskFactorSelectionGroup.getDropDownModifyListener(), 
					dynamoTabDataManager);
// changed from SWT.NONE to SWT.FILL
		TabItem item = new TabItem(this.upperTabFolder, SWT.NONE);
		item.setText("Risk Factor");
		item.setControl(this.getPlotComposite());		
	}
	
	/**
	 * Redraws the tab component 
	 */
	public void redraw(){
		log.debug("REDRAW THIS");
		this.getPlotComposite().redraw();
	}

	public void setPlotComposite(Composite plotComposite) {
		this.plotComposite = plotComposite;
	}

	public Composite getPlotComposite() {
		return plotComposite;
	}	
	
}