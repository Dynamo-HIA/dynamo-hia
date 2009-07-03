/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRelativeRiskConfigurationData;
import nl.rivm.emi.dynamo.exceptions.DynamoConfigurationException;
import nl.rivm.emi.dynamo.exceptions.DynamoNoValidDataException;
import nl.rivm.emi.dynamo.exceptions.NoMoreDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.support.RelRisksCollectionForDropdown;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author schutb, adapted by boshuizh
 * 
 */
public class RelativeRisksTab extends TabPlatform {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static final String RELATIVE_RISKS = "Relative Risks";
	private static final String RELATIVE_RISK = "Relative Risk";

	/**
	 * @param tabfolder
	 * @param output
	 * @throws ConfigurationException
	 */
	public RelativeRisksTab(TabFolder tabFolder,
			DynamoSimulationObject dynamoSimulationObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		super(tabFolder, RELATIVE_RISKS, selectedNode, dynamoSimulationObject,
				dataBindingContext, helpGroup);
		
	}

	@Override
	public NestedTab createNestedDefaultTab(Set<String> defaultSelections)
			throws ConfigurationException {
		int newTabNumber = this.getTabManager().getNumberOfTabs() + 1;
		String tabName = RELATIVE_RISK + newTabNumber;
		return new RelativeRiskTab(defaultSelections, this.getTabManager()
				.getTabFolder(), tabName, getDynamoSimulationObject(),
				dataBindingContext, selectedNode, helpGroup);
	}

	@Override
	public String getNestedTabPrefix() {
		return RELATIVE_RISK;
	}

	@Override
	public NestedTab createNestedTab() throws DynamoConfigurationException,
			ConfigurationException {
		return createNestedDefaultTab(null);
	}

	@Override
	public void deleteNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
		relativeRiskTab.removeTabDataObject();
	}

	public Set<String> getConfigurations() {
		Map<Integer, TabRelativeRiskConfigurationData> configurations = this.getDynamoSimulationObject()
				.getRelativeRiskConfigurations();
		// Conversion into a String keyset: but the contents are still integers
		Set<String> keySet = new LinkedHashSet<String>();
		for (Integer index : configurations.keySet()) {
			keySet.add(index.toString());
		}
		return keySet;
	}

	@Override
	public void refreshNestedTab(NestedTab nestedTab)
			throws ConfigurationException {
		if (nestedTab != null) {
			/*
			 * first find the index of the nestedTab, which can be found from
			 * the name (hopefully)
			 */
	        RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
			
				try {relativeRiskTab.refreshSelectionGroup();}
				catch (NoMoreDataException e) {
					
					Shell messageShell=new Shell(getTabFolder().getDisplay());
					MessageBox messageBox=new MessageBox(messageShell, SWT.OK);
					messageBox.setMessage(e.getMessage()+ "\nTab is not made");
						
					if (messageBox.open() == SWT.OK) {
						messageShell.dispose();
					}

					messageShell.open();
						
					} catch (DynamoNoValidDataException e) {
						Shell messageShell=new Shell(getTabFolder().getDisplay());
						MessageBox messageBox=new MessageBox(messageShell, SWT.OK);
						messageBox.setMessage(e.getMessage()+ "\nTab is deleted");
							this.tabManager.deleteNestedTab();
						
						if (messageBox.open() == SWT.OK) {
							messageShell.dispose();
					e.printStackTrace();
				}			}

			
		}
	}

	
	/*  organized when updating dynamo-object
	@Override
	public void renumberTabObject(NestedTab nestedTab, Integer index)
			 {
		if (nestedTab != null) {
			/*
			 * first find the index of the nestedTab, which can be found from
			 * the name (hopefully)
			 */
	  /*      RelativeRiskTab relativeRiskTab = (RelativeRiskTab) nestedTab;
	        RelativeRiskTabDataManager manager= (RelativeRiskTabDataManager)
	        relativeRiskTab.getDynamoTabDataManager();
	        TabRelativeRiskConfigurationData singleConfiguration = manager.getSingleConfiguration();
	       singleConfiguration.setIndex(index);
	       manager.setSingleConfiguration(singleConfiguration);
	        
		}
	} */

	public void refreshFirstTab() throws ConfigurationException, DynamoNoValidDataException {
		/* first update the list of availlable relative risks */
		
		RelativeRiskTabDataManager dataManager=(RelativeRiskTabDataManager)
		 ((RelativeRiskTab)this.getTabManager().nestedTabs.get(RELATIVE_RISK
				+ "1")).getDynamoTabDataManager();
		/* this is the list of all configured RR's minus the RR's with risk factors and diseases that
		 * have not been choosen in this configuration
		 */
		dataManager.refreshAvaillableRRlist();
		
		refreshNestedTab(this.getTabManager().nestedTabs.get(RELATIVE_RISK
				+ "1"));
	}
	private void handleErrorMessage(Exception e) {    	
		e.printStackTrace();
		MessageBox box = new MessageBox(this.getTabFolder().getShell(),
				SWT.ERROR_UNSPECIFIED);
		box.setText("Error while refreshing tab: /n" + e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}	
	/*
	 * added by hendriek but not used
	 */
	public void refreshAllTabs() throws ConfigurationException {
/* first update the list of availlable relative risks */
		
		
		int oldTabNumber = this.getTabManager().getNumberOfTabs() ;
		if (oldTabNumber>0) {RelativeRiskTabDataManager dataManager=(RelativeRiskTabDataManager)
		 ((RelativeRiskTab)this.getTabManager().nestedTabs.get(RELATIVE_RISK
				+ "1")).getDynamoTabDataManager();
		dataManager.refreshAvaillableRRlist();}
		int newTabNumber = this.getDynamoSimulationObject().getRelativeRiskConfigurations()
				.size();
		/* delete tabs if there are more then needed : do not use "deleteNestedTabs, because this removes
		 * the tab data from the DynamoSimulationObject and that has
		 * already been done by the listener for riskfactor choice or disease choice.*/
		for (int i = 0; i < oldTabNumber-newTabNumber; i++){
			/* note the the tabfolder in this object is the super tabfolder (of all groups, riskfactors, diseases etc)
			 * while we need
			 * the tabfolder for the relative risks which is found in the Tabmanager
			 */
			TabItem tabItem = this.tabManager.getTabFolder().getItem(oldTabNumber -i-1);
			tabItem.dispose();
		}
		
		for (int i = 0; i < newTabNumber; i++){
			String tabName = RELATIVE_RISK + Integer.toString(i+1);
			RelativeRiskTabDataManager dataManager=(RelativeRiskTabDataManager)
			 ((RelativeRiskTab)this.getTabManager().nestedTabs.get(tabName)).getDynamoTabDataManager();
			dataManager.setDynamoSimulationObject(getDynamoSimulationObject());
			dataManager.refreshConfigurations(i);
			refreshNestedTab(this.getTabManager().nestedTabs.get(tabName));
			
		}
		
		
		
		
			
			
			
		
	}

}