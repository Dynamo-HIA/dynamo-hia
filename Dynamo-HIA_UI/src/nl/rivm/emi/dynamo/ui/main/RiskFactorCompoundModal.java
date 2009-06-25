package nl.rivm.emi.dynamo.ui.main;

/**
 * 
 * Exception handling OK
 * 
 */

/**
 * Modal dialog to create and edit the population size XML files. 
 */
import java.io.File;
import java.util.LinkedHashMap;

import nl.rivm.emi.dynamo.data.factories.AgnosticGroupFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.NewbornsObject;
import nl.rivm.emi.dynamo.data.objects.RiskFactorCompoundObject;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.main.NewbornsModal.SavePreProcessor;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.NewbornsDialog;
import nl.rivm.emi.dynamo.ui.panels.RiskFactorCompoundGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.treecontrol.DirectoryNode;
import nl.rivm.emi.dynamo.ui.treecontrol.FileNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ParentNode;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardDirectoryStructureHandler;
import nl.rivm.emi.dynamo.ui.treecontrol.structure.StandardTreeNodeLabelsEnum;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb
 * 
 */
public class RiskFactorCompoundModal extends AbstractMultiRootChildDataModal {

	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass().getName());
	/**
	 * Must be "global"to be available to the save-listener.
	 */
	private RiskFactorCompoundObject modelObject;
	/**
	 * When a new configuration is being edited this integer controls how many
	 * classes the riskfactor will have.
	 */
	int numberOfClasses;
	TreeViewer theViewer;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param numberOfCompoundClasses
	 *            TODO
	 * @param theViewer
	 *            TODO
	 */
	public RiskFactorCompoundModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, int numberOfCompoundClasses,
			TreeViewer theViewer) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
		numberOfClasses = numberOfCompoundClasses;
		this.theViewer = theViewer;
	}

	@Override
	protected String createCaption(BaseNode selectedNode2) {
		return "Compound risk factor configuration";
	}

	public synchronized void open() {
		try {
			this.dataBindingContext = new DataBindingContext();
			this.modelObject = (RiskFactorCompoundObject) manufactureModelObject();
			Composite buttonPanel = new GenericButtonPanel(this.shell);
			((GenericButtonPanel) buttonPanel)
					.setModalParent((DataAndFileContainer) this);
			this.helpPanel = new HelpGroup(this, buttonPanel);
			RiskFactorCompoundGroup riskFactorCategoricalGroup = new RiskFactorCompoundGroup(
					this.shell, this.modelObject, this.dataBindingContext,
					this.selectedNode, this.helpPanel);
			riskFactorCategoricalGroup.setFormData(this.helpPanel.getGroup(),
					buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(400, 400);
			this.shell.open();
			Display display = this.shell.getDisplay();
			while (!this.shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (ConfigurationException e) {
			MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + this.configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		} catch (DynamoInconsistentDataException e) {
			MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
			box.setText("Processing " + this.configurationFilePath);
			box.setMessage(e.getMessage());
			box.open();
		}
	}

	/**
	 * This method constructs a model-object always containing Observables at
	 * the deepest level because these are needed for the databinding to work.
	 * 
	 * @return
	 * @throws ConfigurationException
	 * @throws DynamoInconsistentDataException
	 */
	protected LinkedHashMap<String, Object> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		LinkedHashMap<String, Object> producedData = null;
		AgnosticGroupFactory factory = (AgnosticGroupFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		File dataFile = new File(this.dataFilePath);
		if (dataFile.exists()) {
			// The configuration file with data already exists, fill the modal
			// with existing data
			if (dataFile.isFile() && dataFile.canRead()) {
				producedData = factory.manufactureObservable(dataFile,
						this.rootElementName);
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				// No file has been selected, continue without exceptions
				throw new ConfigurationException(this.dataFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			// The configuration file with data does not yet exist, create a new
			// screen object with default data
			factory.setIndexLimit(numberOfClasses);
			log.debug("Setting numberOfClasses: " + numberOfClasses);
			producedData = factory.manufactureObservableDefault();
		}
		return producedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.rivm.emi.dynamo.ui.main.AbstractDataModal#getData()
	 */
	@Override
	public Object getData() {
		return this.modelObject;
	}

	@Override
	/*
	 * Own implementation.
	 */
	public SideEffectProcessor getSavePostProcessor() {
		// return new SavePostProcessor();
		return null; // Caching makes the other solution impossible.
	}

	public class SavePostProcessor implements SideEffectProcessor {

		@SuppressWarnings("finally")
		synchronized public boolean doIt() {
			// New configuration-file about to be saved.
			boolean doSave = false;
			try {
				if (selectedNode instanceof DirectoryNode) {
					log.debug("SavePostProcessor is doing something");
					theViewer.refresh(); // Refresh the cache.
					StandardDirectoryStructureHandler
							.process((DirectoryNode) selectedNode);
					// File selectedNodeStorage = selectedNode
					// .getPhysicalStorage();
					// String absolutePath =
					// selectedNodeStorage.getAbsolutePath();
					// String durationDirectoryAbsolutePath = absolutePath
					// + File.separator
					// +
					// StandardTreeNodeLabelsEnum.DURATIONDISTRIBUTIONSDIRECTORY
					// .getNodeLabel();
					// File durationDistributionDirectory = new File(
					// durationDirectoryAbsolutePath);
					// if (!durationDistributionDirectory.exists()) {
					// durationDistributionDirectory.mkdir();
					// ((ParentNode) selectedNode)
					// .addChild((ChildNode) new DirectoryNode(
					// (ParentNode) selectedNode,
					// durationDistributionDirectory));
					// }
					doSave = true;
				} else {
					// No processing, but saving is allowed.
					doSave = true;
				}
			} catch (Exception e) {
				log.error("Caught Exception " + e.getClass().getName()
						+ " with message: \"" + e.getMessage() + "\".");
			} finally {
				return doSave;
			}
		}
	}
}
