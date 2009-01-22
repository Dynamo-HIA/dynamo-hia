package nl.rivm.emi.dynamo.ui.main;

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCategoricalObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCompoundObject;
import nl.rivm.emi.dynamo.data.objects.layers.CategoricalObjectImplementation;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.DiseaseIncidencesGroup;
import nl.rivm.emi.dynamo.ui.panels.DiseasePrevalencesGroup;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.OverallDALYWeightsGroup;
import nl.rivm.emi.dynamo.ui.panels.OverallMortalityGroup;
import nl.rivm.emi.dynamo.ui.panels.PopulationSizeGroup;
import nl.rivm.emi.dynamo.ui.panels.RelativeRiskForDeathGroup;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorCategoricalGroup;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorCompoundGroup;
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

public class RiskFactorCompoundModal implements Runnable, DataAndFileContainer {
	private Log log = LogFactory.getLog(this.getClass().getName());
	private Shell shell;
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private RiskFactorCompoundObject modelObject;
	private DataBindingContext dataBindingContext = null;
	private String configurationFilePath;
	private String rootElementName;
	private HelpGroup helpPanel;
	private BaseNode selectedNode;

	public RiskFactorCompoundModal(Shell parentShell, String configurationFilePath,
			String rootElementName, BaseNode selectedNode) {
		this.configurationFilePath = configurationFilePath;
		this.rootElementName = rootElementName;
		this.selectedNode = selectedNode;
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL
				| SWT.RESIZE);
		shell.setText(createCaption(selectedNode));
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
	}

	private String createCaption(BaseNode selectedNode2) {
		return "Compound risk factor configuration";
	}

	public synchronized void open() {
		try {
			dataBindingContext = new DataBindingContext();
			modelObject = new RiskFactorCompoundObject(true);
			modelObject = modelObject.manufacture(configurationFilePath);
			Composite buttonPanel = new GenericButtonPanel(shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			helpPanel = new HelpGroup(shell, buttonPanel);
			RiskFactorCompoundGroup riskFactorCategoricalGroup = new RiskFactorCompoundGroup(
					shell, modelObject, dataBindingContext, selectedNode, helpPanel);
			riskFactorCategoricalGroup.setFormData(helpPanel.getGroup(), buttonPanel);
			shell.pack();
			// This is the first place this works.
			shell.setSize(400, 400);
			shell.open();
			Display display = shell.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		} catch (DynamoInconsistentDataException e) {
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		}
	}

	public void run() {
		open();
	}

	static private void handlePlacementInContainer(Composite myComposite) {
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, -5);
		myComposite.setLayoutData(formData);
	}

	public Object getData() {
		return modelObject;
	}

	public String getFilePath() {
		return configurationFilePath;
	}

	public Object getRootElementName() {
		return rootElementName;
	}
}
