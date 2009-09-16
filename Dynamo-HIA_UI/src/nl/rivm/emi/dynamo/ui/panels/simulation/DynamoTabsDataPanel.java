package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.TabPlatformListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
/**
 * For some hitherto mysterious reason this Class is not found in the listeners subpackage... 
 * @author mondeelr
 *
 */
public class DynamoTabsDataPanel {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private DynamoSimulationObject dynamoSimulationObject;
	private Composite myParent = null;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup theHelpGroup;
	private BaseNode selectedNode;
	private ScenariosTabPlatform scenariosTabPlatform;
	private RiskFactorTab riskFactorTab;
	private DiseasesTabPlatform diseasesTabPlatform;
	private RelativeRisksTabPlatform relativeRisksTabPlatform;

	public DynamoTabsDataPanel(Composite parent, BaseNode selectedNode,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, HelpGroup helpGroup)
			throws ConfigurationException {
		this.myParent = parent;
		this.dynamoSimulationObject = dynamoSimulationObject;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		this.selectedNode = selectedNode;
		makeDynamoTabsDisplay(parent);
	}

	/**
	 * Create the 4 tabfolders
	 * 
	 * @throws ConfigurationException
	 * 
	 */
	public void makeDynamoTabsDisplay(Composite parent)
			throws ConfigurationException {

		log.debug(dynamoSimulationObject + "dynamoSimulationObject");

		TabFolder upperTabFolder = new TabFolder(parent, SWT.FILL);

		upperTabFolder.setLayout(new FillLayout());
		// tabFolder.setBackground(new Color(null, 0x00, 0x00,0x00)); // white
		scenariosTabPlatform = new ScenariosTabPlatform(upperTabFolder, dynamoSimulationObject,
				dataBindingContext, selectedNode, theHelpGroup);

		riskFactorTab = new RiskFactorTab(upperTabFolder,
				dynamoSimulationObject, dataBindingContext, selectedNode,
				theHelpGroup);

		diseasesTabPlatform = new DiseasesTabPlatform(upperTabFolder, dynamoSimulationObject,
				selectedNode, theHelpGroup);

		relativeRisksTabPlatform = new RelativeRisksTabPlatform(upperTabFolder,
				dynamoSimulationObject, selectedNode, theHelpGroup);
		upperTabFolder.addListener(SWT.Selection, 
				(Listener) new TabPlatformListener(this));
	}

	public Composite getMyParent() {
		return myParent;
	}

	public DynamoSimulationObject getDynamoSimulationObject() {
		return dynamoSimulationObject;
	}

	public BaseNode getSelectedNode() {
		return selectedNode;
	}

	public ScenariosTabPlatform getScenariosTabPlatform() {
		return scenariosTabPlatform;
	}

	public RiskFactorTab getRiskFactorTab() {
		return riskFactorTab;
	}

	public DiseasesTabPlatform getDiseasesTabPlatform() {
		return diseasesTabPlatform;
	}

	public RelativeRisksTabPlatform getRelativeRisksTabPlatform() {
		return relativeRisksTabPlatform;
	}

}
