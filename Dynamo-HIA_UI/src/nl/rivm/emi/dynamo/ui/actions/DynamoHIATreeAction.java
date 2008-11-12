package nl.rivm.emi.dynamo.ui.actions;

import java.io.File;

import nl.rivm.emi.dynamo.ui.main.DiseaseIncidenceModal;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
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
		} catch (Exception e){
			MessageBox box = new MessageBox(shell, SWT.ERROR_UNSPECIFIED);
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			StringBuffer theText = new StringBuffer();
			theText.append(e.getClass().getName() + "\n");
			theText.append(e.getMessage() + "\n\n");
			for (StackTraceElement stackTraceElement : stackTraceElements) {
				theText.append(stackTraceElement.getClassName() + "."
						+ stackTraceElement.getMethodName() + "("
						+ stackTraceElement.getLineNumber() + ")\n");
			}
			box.setText("Trouble");
			// box.setMessage(e.getMessage());
			box.setMessage(theText.toString());
			box.open();
		}
		// catch (Throwable t) {
//			t.printStackTrace();
//		}
	}

}
