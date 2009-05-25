package nl.rivm.emi.dynamo.ui.main;
/**
 * 
 * Exception handling OK
 * 
 */

import nl.rivm.emi.dynamo.ui.listeners.SideEffectProcessor;
import nl.rivm.emi.dynamo.ui.treecontrol.BaseNode;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;


public interface DataAndFileContainer {
	public void setConfigurationFilePath(String configurationFilePath);
	public void setDataFilePath(String dataFilePath);
	public String getConfigurationFilePath();
	public Object getData();
	public String getDataFilePath();
	public Object getRootElementName();
	public Shell getShell();	
	public Shell getParentShell();
	public BaseNode getBaseNode();
	/**
	 * Possible extra functionality at "save" time.
	 */
	public SideEffectProcessor getSavePreProcessor();
	public SideEffectProcessor getSavePostProcessor();
	
}