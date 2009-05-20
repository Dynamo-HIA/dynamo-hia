/**
 * 
 */
package nl.rivm.emi.dynamo.ui.panels;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.data.BiGender;
import nl.rivm.emi.dynamo.data.objects.DurationDistributionObject;
import nl.rivm.emi.dynamo.data.objects.DynamoSimulationObject;
import nl.rivm.emi.dynamo.data.objects.tabconfigs.TabRiskFactorConfigurationData;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 * Defines the nested risk factor tab
 * 
 * @author schutb
 * 
 */
public class DurationDistributionTab {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private int genderIndex;
	private DurationDistributionObject durationDistributionObject;
	private DataBindingContext dataBindingContext = null;
	private HelpGroup helpGroup;
	private BaseNode selectedNode;

	private TabFolder tabFolder;
	private TabItem tabItem;

	/**
	 * Default constructor for allocating array only.
	 */
	public DurationDistributionTab() {
	}

	/**
	 * @param tabfolder
	 * @param genderIndex
	 *            TODO
	 * @param output
	 * @throws ConfigurationException
	 */
	public DurationDistributionTab(TabFolder tabfolder, int genderIndex,
			DurationDistributionObject durationDistributionObject,
			DataBindingContext dataBindingContext, BaseNode selectedNode,
			HelpGroup helpGroup) throws ConfigurationException {
		this.tabFolder = tabfolder;
		this.genderIndex = genderIndex;
		this.dataBindingContext = dataBindingContext;
		this.durationDistributionObject = durationDistributionObject;
		this.helpGroup = helpGroup;
		this.selectedNode = selectedNode;
		makeIt();
	}

	/**
	 * makes the tabfolder
	 * 
	 * @throws ConfigurationException
	 */
	public void makeIt() throws ConfigurationException {
		tabItem = new TabItem(this.tabFolder, SWT.NONE);
		tabItem.setText(BiGender.labels[genderIndex]);


		
		DurationDistributionParameterDataPanel genderParameterDataPanel = new DurationDistributionParameterDataPanel(
				this.tabFolder, this.genderIndex, durationDistributionObject,
				dataBindingContext, helpGroup);
//		FormLayout formLayout = new FormLayout();
//		genderParameterDataPanel.setLayout(formLayout);
		genderParameterDataPanel
				.setBackground(new Color(null, 0xbb, 0xbb, 0xbb));
		tabItem.setControl(genderParameterDataPanel);
	}

	/**
	 * Redraws the tab component
	 */
	public void redraw() {
		log.debug("REDRAW THIS");
		tabItem.getControl().redraw();
	}

}