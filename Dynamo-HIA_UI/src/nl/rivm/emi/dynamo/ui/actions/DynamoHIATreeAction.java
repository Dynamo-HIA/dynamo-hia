package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.ui.main.DiseaseIncidenceModal;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DynamoHIATreeAction extends Action {
	Log log = LogFactory.getLog(this.getClass().getName());
	private String selectionPath;
	boolean isReferenceData = false;
	boolean isPopulation = false;
	private Shell shell;

	public DynamoHIATreeAction(Shell shell) {
		super();
		this.shell = shell;
	}

	public void setSelectionPath(String selectionPath) {
		this.selectionPath = selectionPath;
	}

	public String getSelectionPath() {
		return selectionPath;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	@Override
	public void run() {
		try {
			DiseaseIncidenceModal dialog = new DiseaseIncidenceModal(shell,
					selectionPath);
			Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
					dialog);
		} catch (ConfigurationException e){
			// Do nothing, message has been displayed.
		}
		// catch (Throwable t) {
//			t.printStackTrace();
//		}
	}

}
