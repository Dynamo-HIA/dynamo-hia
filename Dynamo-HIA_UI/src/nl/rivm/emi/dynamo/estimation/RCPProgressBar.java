package nl.rivm.emi.dynamo.estimation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class RCPProgressBar implements ProgressIndicatorInterface {

	Shell shell = null;
	ProgressBar bar = null;

	public RCPProgressBar(Shell parentShell, String message) {
		super();
		shell = new Shell(parentShell);
		shell.setText(message);
		shell.setLayout(new FillLayout());
		shell.setSize(600, 50);

		bar = new ProgressBar(shell, SWT.SMOOTH);
		bar.setBounds(10, 10, 200, 32);
		bar.setMinimum(0);
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
	public void update(int percent) {
		bar.setSelection(percent);
		 bar.redraw();

	}

	@Override
	public int getPosition() {
		return bar.getSelection();
	}

}
