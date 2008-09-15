package nl.rivm.emi.dynamo.ui.main;

/* Imports */
import java.io.*;
import java.util.*;

import nl.rivm.emi.dynamo.data.AgeSteppedContainer;
import nl.rivm.emi.dynamo.data.BiGenderSteppedContainer;
import nl.rivm.emi.dynamo.ui.parametercontrols.AgeBiGenderModal;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class BaseScreen {
	private Shell shell;

	private Table table;

	private File file;
	private boolean isModified;

	private String[] copyBuffer;

	private int lastSortColumn = -1;

	private static final String DELIMITER = "\t";
	/** Testing only. */

	String configurationFilePath = "datatemplates" + File.separator
			+ "5agestep_2gender_popsize.xml";

	public Shell open(Display display) {
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
			}
		});

		createMenuBar();

		shell.open();
		return shell;
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

	private void editEntry() {
		AgeBiGenderModal dialog = new AgeBiGenderModal(shell,
				configurationFilePath);
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				dialog);
		dialog.open();
	}

	private void newEntry() {
		AgeBiGenderModal dialog = new AgeBiGenderModal(shell,
				configurationFilePath);
		Realm.runWithDefault(SWTObservables.getRealm(Display.getDefault()),
				dialog);
		dialog.open();
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
				newEntry();
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
	 *            Menu the <code>Menu</code> that file contain the Help
	 *            submenu.
	 */
	private void createHelpMenu(Menu menuBar) {

		// Help Menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("Help");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

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
