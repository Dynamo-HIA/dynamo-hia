package nl.rivm.emi.dynamo.ui.panels.simulation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class Tab {

	private String tabName;
	protected Composite plotComposite;
	
	public Tab(TabFolder tabFolder, String tabName) {
		this.tabName = tabName;
		this.plotComposite = new Group(tabFolder, SWT.FILL);
		FormLayout formLayout = new FormLayout();
		this.plotComposite.setLayout(formLayout);
		this.plotComposite.setBackground(new Color(null, 0x00, 0x00,0x00)); //White		
		
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(tabName);
		item.setControl(this.plotComposite);
		
		makeIt();
	}
	
	protected abstract void makeIt();

	/**
	 * 
	 */
	public void redraw(){
		this.plotComposite.redraw();		
	}

	public String getName() {
		return tabName;
	}
	
}
