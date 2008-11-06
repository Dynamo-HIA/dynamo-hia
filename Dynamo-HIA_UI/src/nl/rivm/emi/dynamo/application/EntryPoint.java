package nl.rivm.emi.dynamo.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class EntryPoint {

	Log log =LogFactory.getLog(this.getClass().getName());
//	private static ResourceBundle resAddressBook = ResourceBundle
//			.getBundle("examples_addressbook");
	private Shell shell;

//	private Table table;
//
//	private File file;
//	private boolean isModified;
//
//	private String[] copyBuffer;
//
//	private int lastSortColumn = -1;
//
//	private static final String DELIMITER = "\t";
//	private static final String[] columnNames = {
//			resAddressBook.getString("Last_name"),
//			resAddressBook.getString("First_name"),
//			resAddressBook.getString("Business_phone"),
//			resAddressBook.getString("Home_phone"),
//			resAddressBook.getString("Email"), resAddressBook.getString("Fax") };

	public static void main(String[] args) {
		Display display = new Display();
		EntryPoint application = new EntryPoint();
		Shell shell = application.open(display);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public Shell open(Display display) {
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				e.doit = closeDynamo();
			}
		});
		createMenuBar();

		shell.setSize(600, 300);
		shell.open();
		return shell;
	}

	private boolean closeDynamo() {
		return true;
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

	/**
	 * Creates all the items located in the File submenu and associate all the
	 * menu items with their appropriate functions.
	 * 
	 * @param menuBar
	 *            Menu the <code>Menu</code> that file contain the File
	 *            submenu.
	 */
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
			}
		});

		// File -> New Contact
		MenuItem subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("New");
		subItem.setAccelerator(SWT.MOD1 + 'N');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openEntry();
			}
		});
		new MenuItem(menu, SWT.SEPARATOR);
		subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("Edit");
		subItem.setAccelerator(SWT.MOD1 + 'E');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createPopUpMenu("Twee");
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
				box.setText("About_1"
						+ shell.getText());
				box.setMessage(shell.getText()
						+ "About_2");
				box.open();
			}
		});
	}

	/**
	 * Creates all items located in the popup menu and associates all the menu
	 * items with their appropriate functions.
	 * 
	 * @return Menu The created popup menu.
	 */
	private Menu createPopUpMenu(String message) {
		Menu popUpMenu = new Menu(shell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu) e.widget;
			}
		});
		// New
		MenuItem item = new MenuItem(popUpMenu, SWT.PUSH);
		item.setText(message);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				;
			}
		});
		return popUpMenu;
	}

	private void openEntry() {
		ModalDummyDialog dialog = new ModalDummyDialog(shell);
		dialog.setLabels(new String[]{"Label1", "Label2"});
		String[] values = new String[]{"Values1", "Value2"};
//		for (int i = 0; i < values.length; i++) {
//			values[i] = item.getText(i);
//		}
		dialog.setValues(values);
		values = dialog.open();
//		if (values != null) {
//			item.setText(values);
//			isModified = true;
//		}
	}

}
