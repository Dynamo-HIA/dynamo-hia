package nl.rivm.emi.dynamo.ui.main.main;


import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import nl.rivm.emi.dynamo.estimation.FinishingListener;
import nl.rivm.emi.dynamo.estimation.ProgressIndicatorInterface;
import nl.rivm.emi.dynamo.ui.treecontrol.Util;

public class RCPProgressBar implements ProgressIndicatorInterface {

	Shell shell = null;
	Display display = null;

	ProgressBar bar = null;
	private class ExtendedBar extends ProgressBar {
		 ArrayList<FinishingListener> listeners = new ArrayList();


	
		public ExtendedBar(Composite parent, int style) {
			super(parent, style);
			// TODO Auto-generated constructor stub
		}

		public void addFinishingListener(FinishingListener e) {
			this.listeners.add(e);
		};

	}

	public RCPProgressBar(Shell parentShell, String message,
			Boolean indeterminate) {
		super();

		if (!indeterminate)
			makebar(parentShell, message);
		else
			makebar2(parentShell, message);

	}


	public RCPProgressBar(Shell parentShell, String message) {
		super();

		makebar(parentShell, message);

	}

	private void makebar(Shell parentShell, String message) {

		this.display = parentShell.getDisplay();

		shell = new Shell(parentShell, SWT.ON_TOP | SWT.TITLE); /*
																 * no close box
																 * added because
																 * user closing
																 * freeses
																 */

		shell.setText(message);
		RowLayout layout = new RowLayout();

		shell.setLayout(layout);
		shell.setSize(600, 56);
//		String imageDirectoryPath = System.getProperty("user.dir")
//				+ File.separator + "images";
//		
//		
//		ClassLoader loader = Util.class.getClassLoader();
//		
		
		Image image = Util.getImageRegistry().get(Util.imageRegistryLogoKey);		
		shell.setImage(image);
		
		
		// GC shellGC = new GC(shell);
		// Color shellBackground = shell.getBackground();

		// Inputstream stream=new
		// Inputstream(imageDirectoryPath+File.separator+"logo.bmp");

		Label label = new Label(shell, 0);
		label.setImage(image);

		bar = new ProgressBar(shell, SWT.SMOOTH);
		// bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);

		RowData rowdata = new RowData();
		bar.setLayoutData(rowdata);
		rowdata.width = 543;
		rowdata.height = 24;
		Device device = Display.getCurrent();
		// dit zijn de kleuren van de website
		Color zalm = new Color(device, 255, 229, 203);
		Color orange = new Color(device, 255, 197, 141);
		// Color blue = new Color (device, 0, 42, 118);
		Color blue = new Color(device, 46, 133, 255);
		Color gray = display.getSystemColor(SWT.COLOR_GRAY);
		// Color red = display.getSystemColor(SWT.COLOR_RED);
		shell.setBackground(zalm);
		label.setBackground(zalm);

		bar.setBackground(gray);
		bar.setForeground(blue);

		// bar.setEnabled(false);

		// bar.addTraverseListener(new FinishingListener(){

		// @Override
		// public void actionPerformed(ActionEvent actionEvent) {
		// if (actionEvent.getActionCommand().equalsIgnoreCase("update"))
		// RCPProgressBar.this.update();

		// }
		// });

		bar.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// The string to be drawn on the progress bar.
				String string = Math.floor(bar.getSelection() * 10.0
						/ (bar.getMaximum() - bar.getMinimum()) * 100)
						/ 10 + "%";

				Point point = bar.getSize();
				Font font = new Font(shell.getDisplay(), "Courier", 10,
						SWT.BOLD);
				e.gc.setFont(font);
				e.gc.setForeground(shell.getDisplay().getSystemColor(
						SWT.COLOR_WHITE));

				FontMetrics fontMetrics = e.gc.getFontMetrics();
				int stringWidth = fontMetrics.getAverageCharWidth()
						* string.length();
				int stringHeight = fontMetrics.getHeight();

				e.gc.drawString(string, (point.x - stringWidth) / 2,
						(point.y - stringHeight) / 2, true);

				font.dispose();
			}
		});
		shell.open();

		/*
		 * while (!shell.isDisposed()) { if (!display.readAndDispatch())
		 * display.sleep(); }
		 */

	}

	
	private void makebar2(Shell parentShell, String message) {

		this.display = parentShell.getDisplay();

		shell = new Shell(parentShell, SWT.ON_TOP | SWT.TITLE); /*
																 * no close box
																 * added because
																 * user closing
																 * freeses
																 */
		shell.setText(message);
		RowLayout layout = new RowLayout();

		shell.setLayout(layout);
		shell.setSize(600, 56);
//		String imageDirectoryPath = System.getProperty("user.dir")
//				+ File.separator + "images";

		Image image = Util.getImageRegistry().get(Util.imageRegistryLogoKey);
		shell.setImage(image);
		// GC shellGC = new GC(shell);
		// Color shellBackground = shell.getBackground();

		// Inputstream stream=new
		// Inputstream(imageDirectoryPath+File.separator+"logo.bmp");

		Label label = new Label(shell, 0);
		label.setImage(image);

		bar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH | SWT.INDETERMINATE );
		// bar.setBounds(10, 10, 200, 32);
		
		RowData rowdata = new RowData();
		bar.setLayoutData(rowdata);
		rowdata.width = 543;
		rowdata.height = 24;
		Device device = Display.getCurrent();
		// dit zijn de kleuren van de website
		Color zalm = new Color(device, 255, 229, 203);
		Color orange = new Color(device, 255, 197, 141);
		// Color blue = new Color (device, 0, 42, 118);
		Color blue = new Color(device, 46, 133, 255);
		Color gray = display.getSystemColor(SWT.COLOR_GRAY);
		// Color red = display.getSystemColor(SWT.COLOR_RED);
		shell.setBackground(zalm);
		label.setBackground(zalm);

		bar.setBackground(gray);
		bar.setForeground(blue);

		// bar.setEnabled(false);

		// bar.addTraverseListener(new FinishingListener(){

		// @Override
		// public void actionPerformed(ActionEvent actionEvent) {
		// if (actionEvent.getActionCommand().equalsIgnoreCase("update"))
		// RCPProgressBar.this.update();

		// }
		// });
		
		shell.open();
		/*
		 * while (!shell.isDisposed()) { if (!display.readAndDispatch())
		 * display.sleep(); }
		 */

	}

	
	
	
	
	@Override
	public void dispose() {
		// bar.dispose();
		// shell.setVisible(false);
		// shell.close();
		shell.dispose();
		//flush the event queu ;
		while(this.display.readAndDispatch()){};

	}

	@Override
	public void setMaximum(int percent) {
		bar.setMaximum(percent);
	}

	@Override
	public  void update(int percent) {
		bar.setSelection(percent);
		bar.redraw();

	}
	
	
	
	
	@Override
	public  void setIndeterminate(String text) {
		shell.setText(text);
		bar =new ProgressBar(shell,SWT.INDETERMINATE);
		bar.setBounds(10, 10, 200, 32);
		 shell.redraw();

	}
	
	
	

	@Override
	public void update() {
		int curvalue = bar.getSelection();
		curvalue++;
		if (curvalue <= bar.getMaximum())
			setSelection(curvalue);
		bar.redraw();

	}

	@Override
	public int getPosition() {
		return bar.getSelection();
	}


	@Override
	public boolean isDisposed() {

		return bar.isDisposed();
	}

	@Override
	public int getSelection() {

		return bar.getSelection();
	}

	@Override
	public void setSelection(int i) {

		bar.setSelection(i);
	}


}
