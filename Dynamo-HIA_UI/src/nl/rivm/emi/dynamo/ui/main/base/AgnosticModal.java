package nl.rivm.emi.dynamo.ui.main.base;

/**
 * 
 * Exception handling OK
 * 
 */

/**
 * BaseClass for Modal dialogs that are used to create and edit configuration 
 * files that are handled by an derivative of the AgnosticFactory. 
 */
import java.io.File;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.data.factories.AgnosticCategoricalFactory;
import nl.rivm.emi.dynamo.data.factories.AgnosticFactory;
import nl.rivm.emi.dynamo.data.factories.dispatch.FactoryProvider;
import nl.rivm.emi.dynamo.exceptions.DynamoInconsistentDataException;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;
import nl.rivm.emi.dynamo.ui.treecontrol.ChildNode;
import nl.rivm.emi.dynamo.ui.util.RiskFactorUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author schutb<br/>
 *         Intermediate inheritance layer implementing some common behaviour for
 *         three windows.
 * 
 */
abstract public class AgnosticModal extends AbstractDataModal {
	protected Log log = LogFactory.getLog(this.getClass().getName());

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            Containing Shell
	 * @param dataFilePath
	 *            Path where the resulting configuration file is to be saved.
	 * @param configurationFilePath
	 *            Path to the imported configurationfile (if any).
	 * @param rootElementName
	 *            Name of the rootelement in the resulting configurationfile.
	 * @param selectedNode
	 *            Node on which the context menu was invoked to reach this
	 *            point.
	 */
	public AgnosticModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	/**
	 * Common open behaviour for all supported windows.
	 * 
	 * @throws DynamoInconsistentDataException
	 * @throws ConfigurationException
	 */
	public synchronized void openAgnostic() throws ConfigurationException,
			DynamoInconsistentDataException {
		this.modelObject = manufactureModelObject();
		specializedOpenPart(buttonPanel);
		this.shell.pack();
		// This is the first place this works.
//		this.shell.setSize(400, ModalStatics.defaultHeight);
		this.shell.setSize(475, ModalStatics.defaultHeight);
		this.shell.open();
		openModal();
	}

	/**
	 * The method name says it all, the Class that extends this baseclass must
	 * implement its own special behaviour.
	 * 
	 * @param buttonPanel
	 */
	abstract protected void specializedOpenPart(Composite buttonPanel)
			throws ConfigurationException;

	@Override
	protected TypedHashMap<?> manufactureModelObject()
			throws ConfigurationException, DynamoInconsistentDataException {
		TypedHashMap<?> producedData = null;
		AgnosticFactory factory = (AgnosticFactory) FactoryProvider
				.getRelevantFactoryByRootNodeName(this.rootElementName);
		if (factory == null) {
			throw new ConfigurationException(
					"No Factory found for rootElementName: "
							+ this.rootElementName);
		}
		File dataFile = new File(this.dataFilePath);
		if (dataFile.exists()) {
			if (dataFile.isFile() && dataFile.canRead()) {
				// 20090929 Added.
				if (factory instanceof AgnosticCategoricalFactory) {
					int numberOfClasses = RiskFactorUtil
							.getNumberOfRiskFactorClasses((BaseNode) ((ChildNode) this.selectedNode)
									.getParent());
					((AgnosticCategoricalFactory) factory)
							.setNumberOfCategories(numberOfClasses);
				}
				// ~ 20090929
				producedData = factory.manufactureObservable(dataFile,
						this.rootElementName);
				if (producedData == null) {
					throw new ConfigurationException(
							"DataModel could not be constructed.");
				}
			} else {
				throw new ConfigurationException(this.configurationFilePath
						+ " is no file or cannot be read.");
			}
		} else {
			producedData = bootstrapModelObject(factory);
		}
		return producedData;
	}

	/**
	 * Method that creates a modelobject containing default LeafValue-s for all
	 * ContainerValue-s(Age, Sex etc.) when no configuration file is supplied.
	 * 
	 * Contains behaviour that goes for the most simple ModelObjects.
	 * 
	 * For instance: Objects that contain category layers must override this
	 * methods to ensure the categories are initialized.
	 * 
	 * @param factory
	 * @return
	 * @throws ConfigurationException
	 */
	protected TypedHashMap<?> bootstrapModelObject(AgnosticFactory factory)
			throws ConfigurationException {
		TypedHashMap<?> producedData = factory.manufactureObservableDefault();
		return producedData;
	}

}
