/*
 * Created on Jun 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nl.rivm.emi.dynamo.ui.main;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TreeViewWindow {

	public void show() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Dynamo-HIA");
		shell.setBounds(100, 100, 600, 400);
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
		final Tree tree = new Tree(shell, SWT.SINGLE);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0,2);
		formData.right = new FormAttachment(0,202);
		formData.bottom = new FormAttachment(100,-2);
		formData.left = new FormAttachment(0,2);
		tree.setLayoutData(formData);
		TreeItem simulationsItem = new TreeItem(tree, 0);
		simulationsItem.setText("Simulations");
		TreeItem referenceDataItem = new TreeItem(tree, 0);
		referenceDataItem.setText("Reference data");
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
