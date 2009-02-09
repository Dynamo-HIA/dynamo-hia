package nl.rivm.emi.dynamo.ui.main;

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.DiseaseIncidencesGroup;
import nl.rivm.emi.dynamo.ui.panels.DiseasePrevalencesGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.OverallDALYWeightsGroup;
import nl.rivm.emi.dynamo.ui.panels.OverallMortalityGroup;
import nl.rivm.emi.dynamo.ui.panels.PopulationSizeGroup;
import nl.rivm.emi.dynamo.ui.panels.RelRisksForDeathContinuousGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RelRiskForDeathContinuousModal extends AgnosticModal implements
		Runnable, DataAndFileContainer {

	public RelRiskForDeathContinuousModal(Shell parentShell,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, configurationFilePath, rootElementName, selectedNode);
	}

	protected String createCaption(BaseNode selectedNode2) {
		return "Relative risk for death, continuous,";
	}

	@Override
	protected void specializedOpenPart(Composite buttonPanel) {
		RelRisksForDeathContinuousGroup relativeRiskForDeathGroup = new RelRisksForDeathContinuousGroup(
				shell, lotsOfData, dataBindingContext, selectedNode, helpPanel);
		relativeRiskForDeathGroup
				.setFormData(helpPanel.getGroup(), buttonPanel);
	}
}
