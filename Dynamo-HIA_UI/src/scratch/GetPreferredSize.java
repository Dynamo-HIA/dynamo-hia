package scratch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class GetPreferredSize {

  public static void main(String[] args) {
    final Display display = new Display();
    Shell shell = new Shell(display);

    shell.setLayout(new GridLayout());

    Button button = new Button(shell, SWT.PUSH | SWT.LEFT);
    button.setText("Button");

    System.out.println("toControl: " + button.toControl(100, 200));

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }
}