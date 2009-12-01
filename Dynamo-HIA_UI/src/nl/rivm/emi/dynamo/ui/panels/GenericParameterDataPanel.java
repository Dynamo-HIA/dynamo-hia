package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.TypedHashMap;
import nl.rivm.emi.dynamo.ui.panels.help.HelpGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

// TODO: First implementation for Transition Drift!
public abstract class GenericParameterDataPanel<T> 
	extends AbstractDataPanel /* implements Runnable */{
	Log log = LogFactory
			.getLog(this.getClass().getName());
	//private TypedHashMap<T> lotsOfData;
//	final private Composite myParent;
//	private boolean open = false;
	protected DataBindingContext dataBindingContext = null;
	protected HelpGroup theHelpGroup;
	//protected AtomicTypeBase<T>[] myTypes;

	public GenericParameterDataPanel(Composite parent, Text topNeighbour,
			TypedHashMap<?> lotsOfData,
			DataBindingContext dataBindingContext, HelpGroup helpGroup,
			PanelMatrix<T> panelMatrix
	) {
		super(parent, SWT.NONE, dataBindingContext);
		//this.lotsOfData = lotsOfData;
		this.dataBindingContext = dataBindingContext;
		this.theHelpGroup = helpGroup;
		//this.myTypes = atomicTypeBases;
		//this.myTypes = atomicTypeBases;
	}		
}
