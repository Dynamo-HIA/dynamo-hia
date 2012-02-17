package nl.rivm.emi.dynamo.estimation;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class RCPProgressBar implements ProgressIndicatorInterface {

	Shell shell = null;
	ProgressBar bar = null;
	private class ExtendedBar extends ProgressBar {
		 ArrayList<FinishingListener> listeners = new ArrayList();

		public ExtendedBar(Composite parent, int style) {
			super(parent, style);
			// TODO Auto-generated constructor stub
		}
		public void addFinishingListener (FinishingListener e){
			this.listeners.add(e);
		};
	
	}
	
	public RCPProgressBar(Shell parentShell, String message) {
		super();
		Display display=parentShell.getDisplay();
		shell = new Shell(parentShell,SWT.ON_TOP|SWT.TITLE); /* no close box */
		shell.setText(message);
		shell.setLayout(new FillLayout());
		shell.setSize(600, 50);

		bar = new ProgressBar(shell, SWT.SMOOTH);
		bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);
	//	bar.setEnabled(false);
		
		// bar.addTraverseListener(new FinishingListener(){
			
		//	@Override
	//		public void actionPerformed(ActionEvent actionEvent) {
	//			if (actionEvent.getActionCommand().equalsIgnoreCase("update")) RCPProgressBar.this.update();
				
	//		}
	//	});
		bar.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	            // The string to be drawn on the progress bar.
	            String string =   Math.floor(bar.getSelection() * 10.0 /
	          (bar.getMaximum()-bar.getMinimum()) * 100)/10 + "%";

	            Point point = bar.getSize();
	            Font font = new Font(shell.getDisplay(),"Courier",10,SWT.BOLD);
	            e.gc.setFont(font);
	            e.gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

	            FontMetrics fontMetrics = e.gc.getFontMetrics();
	            int stringWidth = fontMetrics.getAverageCharWidth() * string.length();
	            int stringHeight = fontMetrics.getHeight();

	            e.gc.drawString(string, (point.x-stringWidth)/2 ,
	                 (point.y-stringHeight)/2, true);

	            font.dispose();
	        }
	    });
		shell.open();
		/*while (!shell.isDisposed()) {
		      if (!display.readAndDispatch())
		        display.sleep();
		    }*/

	}

	@Override
	public void dispose() {
		shell.close();
		shell.dispose();
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
	public  void update() {
		int curvalue=bar.getSelection();
		curvalue++;
		if (curvalue<= bar.getMaximum() ) setSelection(curvalue);
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
