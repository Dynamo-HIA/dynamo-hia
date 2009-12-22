/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.simulation.listeners.RelativeRiskComboModifyListener;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * This Class has kept inheritance only because the tabPlatform implementation
 * would otherwise fall apart.
 * 
 * @author mondeelr
 * 
 */
public class RelativeRiskTab extends NestedTab {

	public static final String RELATIVE_RISK = "Relative Risk";

	// protected String tabName;
	// protected Composite plotComposite;
	private DynamoSimulationObject dynamoSimulationObject;
	// protected HelpGroup helpGroup;
	protected BaseNode selectedNode;

	// private Log log = LogFactory.getLog("RelativeRiskTab");

	private RelativeRiskSelectionGroup relativeRiskSelectionGroup;
	private RelativeRiskResultGroup relativeRiskResultGroup;
	RelativeRiskTabPlatformDataManager platformManager;
	private RelativeRiskTabDataManager relRiskTabDataManager;
	private RelativeRiskComboModifyListener relativeRiskComboModifyListener;

	Integer tabIndex = null;

	public RelativeRiskTabDataManager getDynamoTabDataManager() {
		return relRiskTabDataManager;
	}

	/**
	 * @param tabFolder
	 * @param helpGroup
	 * @param platformManager
	 *            TODO
	 * @param myParent
	 *            TODO
	 * @param tabName
	 * @throws ConfigurationException
	 */
	public RelativeRiskTab(TabFolder tabFolder, Integer tabIndex,
			HelpGroup helpGroup,
			RelativeRiskTabPlatformDataManager platformManager,
			TabPlatform myParent) throws ConfigurationException {
		super(null, tabFolder, RELATIVE_RISK + (tabIndex + 1), platformManager
				.getDynamoSimulationObject(), null /* selectedNode */,
				helpGroup, null, myParent);
		this.tabIndex = tabIndex;
		this.setDynamoSimulationObject(dynamoSimulationObject);
		// this.helpGroup = helpGroup;
		// this.selectedNode = selectedNode;
		// this.tabName = ;
		this.platformManager = platformManager;
		// this.plotComposite = new Composite(tabFolder, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		this.plotComposite.setLayout(formLayout);
		// this.plotComposite.setBackground(new Color(null, 0xbb, 0xbb,0xbb));
		log.debug("RelativeRiskTab, constructing with tabIndex: " + tabIndex);
		// Yes, make it here
		getRelRiskTabDataManager();
		try {
			makeIt();

			// The TabItem can only be created AFTER makeIt()
			tabFolder.removeSelectionListener(myTabPlatform.listener);
			TabItem item = new TabItem(tabFolder, SWT.NONE);
			item.setText(tabName);
			item.setControl(this.plotComposite);
			item.addListener(SWT.SELECTED, new MySelectionListener());
			tabFolder.addSelectionListener(myTabPlatform.listener);
			log.debug("Tab: " + tabName + "  selectionListener added.");
		} catch (NoMoreDataException e) {
			displayMessage(tabFolder.getParent().getDisplay(), e.getMessage()
					+ " \nNo new tab is made");

			e.printStackTrace();
		}
		// platformManager.getConfigurations();
	}

	class MySelectionListener implements Listener {
		public void handleEvent(Event event) {
			TabItem item = (TabItem) event.item;
			String tabId = item.getText();
			log.debug("THIS TAB IS SELECTED" + tabId);
		}
	}

	/**
	 * Create the active contents of this tab
	 * 
	 * @throws ConfigurationException
	 * @throws NoMoreDataException
	 * @throws DynamoNoValidDataException
	 */
	@Override
	public void makeIt() throws ConfigurationException, NoMoreDataException {
		log.debug("Entering makeIt().");
		try {
			relativeRiskSelectionGroup = new RelativeRiskSelectionGroup(
					tabName, selections, plotComposite, selectedNode,
					helpGroup, getRelRiskTabDataManager());

			log.debug("RelativeRiskSelectionGroup added.");

			relativeRiskResultGroup = new RelativeRiskResultGroup(
					plotComposite, selectedNode, helpGroup,
					relativeRiskSelectionGroup, relRiskTabDataManager);

			TabRelativeRiskConfigurationData myConfigurationData = platformManager
					.getConfiguration(tabIndex);
			if (myConfigurationData != null) {
				log
						.debug("RRResultGroup added, initializing data for tabIndex: "
								+ tabIndex
								+ " Configuration: "
								+ myConfigurationData.report());
			} else {
				log.debug("RRResultGroup added, new tab at tabIndex: "
						+ tabIndex);
			}
			( getRelativeRiskComboModifyListener())
					.initialize(myConfigurationData);

		} catch (DynamoNoValidDataException e) {

			log.debug("DynamoNoValidDataException: " + e.getMessage());

			// this.relRiskTabDataManager.removeFromDynamoSimulationObject();
			throw new NoMoreDataException(e.getMessage());
		} catch (NullPointerException e) {
			log.error("NullPointerException: " + e.getMessage());
			throw new NoMoreDataException(e.getMessage());
		} catch (Exception e) {
			log.debug("Exception: " + e.getClass().getSimpleName() + " "
					+ e.getMessage());
			e.printStackTrace(System.err);
			throw new ConfigurationException(e);
			// TODO This Exception goes all the way up.....
		}
	}

	public void refreshSelectionGroup() throws ConfigurationException,
			NoMoreDataException, DynamoNoValidDataException {
		// Don't do this if first time object construction is going on
		if (relativeRiskSelectionGroup != null) {
			relativeRiskSelectionGroup.refreshSelectionDropDown();
		}
	}

	public void removeTabDataObject() throws ConfigurationException {
		getRelRiskTabDataManager();
		relRiskTabDataManager.removeTabRelRiskConfiguration();
	}

	public void redraw() {
		this.plotComposite.redraw();
	}

	public String getName() {
		return tabName;
	}

	public void displayMessage(Display display, String message) {

		Shell messageShell = new Shell(display);
		MessageBox messageBox = new MessageBox(messageShell, SWT.OK);
		messageBox.setMessage(message);
		messageShell.open();

		if (messageBox.open() == SWT.OK) {
			messageShell.dispose();
		}

	}

	public void setDynamoSimulationObject(
			DynamoSimulationObject dynamoSimulationObject) {
		this.dynamoSimulationObject = dynamoSimulationObject;
	}

	public DynamoSimulationObject getDynamoSimulationObject() {
		return dynamoSimulationObject;
	}

	public RelativeRiskTabDataManager getRelRiskTabDataManager()
			throws ConfigurationException {
		if (relRiskTabDataManager == null) {
			relRiskTabDataManager = new RelativeRiskTabDataManager(
					this,
					((RelativeRisksTabPlatform) myTabPlatform).getDataManager(),
					tabIndex);
		}
		return relRiskTabDataManager;
	}

	public RelativeRiskComboModifyListener getRelativeRiskComboModifyListener() {
		if (relativeRiskComboModifyListener == null) {
			relativeRiskComboModifyListener = new RelativeRiskComboModifyListener(
					this, helpGroup);
		}
		return relativeRiskComboModifyListener;
	}

	public Integer getTabIndex() {
		return tabIndex;
	}

}