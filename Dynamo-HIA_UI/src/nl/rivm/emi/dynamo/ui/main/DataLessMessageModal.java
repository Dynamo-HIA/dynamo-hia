package nl.rivm.emi.dynamo.ui.main;

import java.io.File;
import java.util.Set;

import nl.rivm.emi.dynamo.data.factories.TransitionDriftNettoFactoryImplementation;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.data.objects.TransitionDriftNettoObject;
import nl.rivm.emi.dynamo.data.writers.FileControlEnum;
import nl.rivm.emi.dynamo.data.xml.structure.RootElementNamesEnum;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.panels.HelpGroup;
import nl.rivm.emi.dynamo.ui.panels.TransitionDriftNettoGroup;
import nl.rivm.emi.dynamo.ui.panels.DataLessMessageGroup;
import nl.rivm.emi.dynamo.ui.panels.button.GenericButtonPanel;
import nl.rivm.emi.dynamo.ui.panels.button.NoImportButtonPanel;
import nl.rivm.emi.dynamo.ui.panels.button.TransitionDriftNettoButtonPanel;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class DataLessMessageModal extends AbstractHelplessModal {

	Log log = LogFactory.getLog(this.getClass().getName());

	Set<String> messageLineSet;

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 * @param caption
	 *            TODO
	 * @param messageLineSet
	 *            TODO
	 */
	public DataLessMessageModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode, String caption, Set<String> messageLineSet) {
		// Decoupled rootElementName because of Garbage in, Garbage out.
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode, caption);
		this.messageLineSet = messageLineSet;
	}

	@Override
	protected synchronized void open() {
		try {
			Composite buttonPanel = new NoImportButtonPanel(this.shell);
			((NoImportButtonPanel) buttonPanel).setModalParent(this);
			DataLessMessageGroup transitionDriftZeroGroup = new DataLessMessageGroup(
					this.shell, this.selectedNode, this.messageLineSet);
			transitionDriftZeroGroup.setFormData(buttonPanel);
			this.shell.pack();
			// This is the first place this works.
			this.shell.setSize(350, 225);
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
		}
	}

}
