package nl.rivm.emi.dynamo.ui.panels.help;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PreconfiguredHelpGroup{
	Group theGroup = null;
	Text text = null;
	String validHelp[] = null;
	String errorHelp = "Help error: ";

	public PreconfiguredHelpGroup(Composite parent, String borderText, String[] helpStrings) {
		theGroup = new Group(parent, SWT.NONE);
			theGroup.setText(borderText);
			validHelp = helpStrings;
			FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
			fillLayout.marginHeight = 5;
			fillLayout.marginWidth = 5;
			theGroup.setLayout(fillLayout);
			text = new Text(theGroup, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
			text.setText((validHelp.length > 0)?validHelp[0]:errorHelp);
		}

	public Group getGroup(){
		return theGroup;
	}

	public void putHelpText(int id) {
		text.setText((validHelp.length > id )?validHelp[id]:errorHelp + "No help text configured for id " + id);
	}
}
