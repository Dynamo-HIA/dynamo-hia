package nl.rivm.emi.dynamo.ui.startup;

import nl.rivm.emi.dynamo.global.StorageTree;
import nl.rivm.emi.dynamo.global.StorageTreeException;
import nl.rivm.emi.dynamo.ui.treecontrol.StorageTreeContentProvider;
import nl.rivm.emi.dynamo.ui.treecontrol.TreeViewerPlusCustomMenu;

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

public class BaseStorageTreeScreen {
	Shell shell;

	Log log = LogFactory.getLog(getClass().getName());
	public static final String DONOTRESTART = "DoNot";
	public static final String RESTART = "Restart";

	String baseDirectoryPath = null;
	private String restartMessage = DONOTRESTART;

	public BaseStorageTreeScreen(String baseDirectoryPath) throws Exception {
		this.baseDirectoryPath = baseDirectoryPath;
	}

	public String getRestartMessage() {
		return restartMessage;
	}
/*
 * 
 * configure and run simulation
 */
	/**
	 * @deprecated Choice of the directorypath has been moved outside.
	 * 
	 * @return
	 */
	public String getBaseDirectoryPath() {
		return baseDirectoryPath;
	}

	public Shell open(Display display) {
		try {
			this.shell = new Shell(display);
			this.shell.setLayout(new FillLayout());
			this.shell.addShellListener(new ShellAdapter() {
				public void shellClosed(ShellEvent e) {
				}
			});
			StorageTree testTree = new StorageTree(this.baseDirectoryPath);
			StorageTreeContentProvider sTCP = new StorageTreeContentProvider(
					testTree.getRootNode());
			new TreeViewerPlusCustomMenu(shell, sTCP);
			createMenuBar();
			this.shell.open();
			return this.shell;
		} catch (StorageTreeException ste) {
			this.log.error("Caught " + ste.getClass().getName()
					+ " with message " + ste.getMessage());
			showErrorMessage(ste);
			return null;
		}
	}

	private void showErrorMessage(Exception e) {
		this.log.fatal(e);
		e.printStackTrace();
		MessageBox box = new MessageBox(this.shell, SWT.ERROR_UNSPECIFIED);
		box.setText("Error occured during opening of base storage tree screen "
				+ e.getMessage());
		box.setMessage(e.getMessage());
		box.open();
	}

	/**
	 * Creates the menu at the top of the shell where most of the programs
	 * functionality is accessed.
	 * 
	 * @deprecated Not used anymore
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
			}
		});
		// File -> New Contact
		MenuItem subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("Change Workdirectory");
		subItem.setAccelerator(SWT.MOD1 + 'W');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				restartMessage = RESTART;
				shell.close();
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

		addTreeItem(menu);
		addAboutItem(menu);
	}

	private void addAboutItem(Menu menu) {
		// Help -> About Text Editor
		MenuItem subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("About");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(shell, SWT.NONE);
				box.setText("About Dynamo-HIA");
				box
						.setMessage("DYNAMO-HIA is a program projecting "
								+ "the health consequences of risk factor "
								+ "changes, intended for Health Impact Assessment.\n\n"
								+ "This software was developed by RIVM "
								+ "in cooperation with ErasmusMC "
								+ "with partial funding of DG-SANCO within the framework of grant agreement 2006116.\n\n"
								+ "See  'www.dynamo-hia.eu' for more information."
								+ "\n\n This product was developped using Eclipse SDK (http://www.eclipse.org/)," +
										" includes software developed by the"
								+ " Apache Software Foundation (http://www.apache.org/) and uses the libraries "
								+ "JFreeChart (http://www.jfree.org/jfreechart)"
								+ " and JAMA (http://math.nist.gov/javanumerics/jama)");
				box.open();
			}
		});
	}

	private void addTreeItem(Menu menu) {
		MenuItem subItem = new MenuItem(menu, SWT.NONE);
		subItem.setText("Tree navigation");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageBox box = new MessageBox(shell, SWT.NONE);
				box.setText("Tree navigation help");
				box.setMessage("Tree elements can be expanded "
						+ "by (left) clicking on the '+' sign.\n"
						+ "Clicking on the '-' sign collapses them.\n\n"
						+ "Right click on the text items for context menus.");
				box.open();
			}
		});
	}

}
