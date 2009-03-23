package nl.rivm.emi.dynamo.ui.main;

import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.widgets.Shell;

/**
 * Modal dialog to create and edit the population size XML files.
 * 
 */
public class TransitionMatrixModal extends AbstractDataModal {

	/**
	 * 
	 * Constructor
	 * 
	 * @param parentShell
	 * @param dataFilePath
	 * @param configurationFilePath
	 * @param rootElementName
	 * @param selectedNode
	 */
	public TransitionMatrixModal(Shell parentShell, String dataFilePath,
			String configurationFilePath, String rootElementName,
			BaseNode selectedNode) {
		super(parentShell, dataFilePath, configurationFilePath,
				rootElementName, selectedNode);
	}

	@Override
	protected String createCaption(BaseNode selectedNode) {
		return "Transition Matrix";
	}

	@Override
	protected void open() {
		// TODO Auto-generated method stub		
	}

}
