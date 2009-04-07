package nl.rivm.emi.dynamo.ui.panels.simulation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import nl.rivm.emi.dynamo.data.interfaces.ISimulationRiskFactorConfiguration;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.listeners.GenericComboModifyListener;
import nl.rivm.emi.dynamo.ui.panels.util.DropDownPropertiesSet;
import nl.rivm.emi.dynamo.ui.support.TreeAsDropdownLists;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class RiskFactorSelectionGroup {

	private Log log = LogFactory.getLog(this.getClass().getName());

	private static final String RISK_FACTOR = "Risk Factor";
	protected Group group;
	Composite plotComposite;
	GenericComboModifyListener dropDownModifyListener;
	ISimulationRiskFactorConfiguration configuration;
	BaseNode selectedNode;

	public RiskFactorSelectionGroup(Composite plotComposite,
			ISimulationRiskFactorConfiguration riskFactorConfiguration,
			BaseNode selectedNode, HelpGroup helpGroup) throws ConfigurationException {
		configuration = riskFactorConfiguration;
		this.plotComposite = plotComposite;
		this.selectedNode = selectedNode;
		log.debug("RiskFactorSelectionGroup::this.plotComposite: "
				+ plotComposite);
		group = new Group(plotComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = true;
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);
		// group.setBackground(new Color(null, 0xee, 0xee,0xee)); // ???

		log.debug("RiskFactorSelectionGroup" + group);

		layoutDropDownArea();
		createDropDownArea();
	}

	private void createDropDownArea() throws ConfigurationException {

		String curentRiskFactorName = configuration.getName();
		DropDownPropertiesSet theMap = new DropDownPropertiesSet();
			TreeAsDropdownLists tADL = TreeAsDropdownLists
					.getInstance(selectedNode);
			Set<String> riskFactorNames = tADL.getRiskFactors();
			theMap.addAll(riskFactorNames);
			RiskFactorDataAction updateRiskFactorDataAction = new RiskFactorDataAction();
			GenericDropDownPanel riskDropDownPanel = new GenericDropDownPanel(group, RISK_FACTOR, 2,
					theMap, updateRiskFactorDataAction);
			this.dropDownModifyListener = riskDropDownPanel
					.getGenericComboModifyListener();
	}

	private void layoutDropDownArea() {
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(27, -5);
		group.setLayoutData(formData);
	}

	public GenericComboModifyListener getDropDownModifyListener() {
		return this.dropDownModifyListener;
	}
}
