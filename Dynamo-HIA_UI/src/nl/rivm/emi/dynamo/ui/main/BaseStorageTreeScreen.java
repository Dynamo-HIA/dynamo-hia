package nl.rivm.emi.dynamo.ui.main;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.rivm.emi.dynamo.ui.treecontrol.StorageTree;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeContentProvider;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeException;
import nl.rivm.emi.dynamo.ui.treecontrol.TreeViewerPlusCustomMenu;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class BaseStorageTreeScreen {
	Shell shell;

	Log log = LogFactory.getLog(getClass().getName());
	String baseDirectoryPath = null;

	// File baseDirectory = null;

	public BaseStorageTreeScreen(String baseDirectoryPath) throws Exception {
		this.baseDirectoryPath = baseDirectoryPath;
	}

	public Shell open(Display display) {
		try {
			shell = new Shell(display);
			shell.setLayout(new FillLayout());
			shell.addShellListener(new ShellAdapter() {
				public void shellClosed(ShellEvent e) {
				}
			});
			baseDirectoryPath = selectBaseDirectory(baseDirectoryPath);
			createMenuBar();
			StorageTree testTree = new StorageTree(baseDirectoryPath);
			StorageTreeContentProvider sTCP = new StorageTreeContentProvider(
					testTree.getRootNode());
			new TreeViewerPlusCustomMenu(shell, sTCP);
			shell.open();
			return shell;
		} catch (StorageTreeException e) {
			log.error("Caught " + e.getClass().getName() + " with message "
					+ e.getMessage());
			e.printStackTrace();
			return null;
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates the menu at the top of the shell where most of the programs
	 * functionality is accessed.
	 * 
	 * @return The <code>Menu</code> widget that was created
	 */
	private Menu createMenuBar() {
		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// create each header and subMenu for the menuBar
		createFileMenu(menuBar);
		createHelpMenu(menuBar);

		return menuBar;
	}

	private void displayError(String msg) {
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
		box.setMessage(msg);
		box.open();
	}

	private String selectBaseDirectory(String parBaseDirectoryPath)
			throws ConfigurationException {
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		if (baseDirectoryPath != null) {
			directoryDialog.setFilterPath(parBaseDirectoryPath);
		}
		directoryDialog.open();
		String newBaseDirectoryPath = directoryDialog.getFilterPath();
		return newBaseDirectoryPath;
	}

	private void editEntry() {
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.open();
		String selectedConfigurationFilePath = fileDialog.getFilterPath()
				+ File.separator + fileDialog.getFileName();
		DiseaseIncidencesModal dialog;
		dialog = new DiseaseIncidencesModal(shell,
				selectedConfigurationFilePath, selectedConfigurationFilePath, null);
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				dialog);
	}

	private void new2Entry() {
		InputDialog inputDialog = new InputDialog(shell, "dialogTitle",
				"dialogMessage", "initialValue", null);
		inputDialog.open();
		String input = inputDialog.getValue();
		MessageBox messageBox = new MessageBox(shell);
		messageBox.setMessage(input);
		messageBox.open();
	}

	private void createFileMenu(Menu menuBar) {
		// File menu.
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("File");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);
		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu) e.widget;
				MenuItem[] items = menu.getItems();
				items[1].setEnabled(true);
				items[2].setEnabled(true);
				items[3].setEnabled(false);
			}
		});
		// File -> New Contact
		MenuItem subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("New");
		subItem.setAccelerator(SWT.MOD1 + 'N');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
		// File -> New Contact
		subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("New2");
		subItem.setAccelerator(SWT.MOD1 + '2');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new2Entry();
			}
		});
		subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("Edit");
		subItem.setAccelerator(SWT.MOD1 + 'E');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				editEntry();
			}
		});
		new MenuItem(menu, SWT.SEPARATOR);
		// File -> Exit.
		subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("Exit");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	/**
	 * Creates all the items located in the Help submenu and associate all the
	 * menu items with their appropriate functions.
	 * 
	 * @param menuBar
	 *            Menu the <code>Menu</code> that file contain the Help submenu.
	 */
	private void createHelpMenu(Menu menuBar) {
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("Help");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

		addAboutItem(menu);
		// addDirectoryDialogItem(menu);
	}

	private void addAboutItem(Menu menu) {
		// Help -> About Text Editor
		MenuItem subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("About");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(shell, SWT.NONE);
				box.setText("About_1");
				box.setMessage("About_2");
				box.open();
			}
		});
	}

}
