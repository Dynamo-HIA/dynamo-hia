package nl.rivm.emi.dynamo.ui.parametercontrols;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ScrollListener implements Listener{
	ScrolledComposite sc = null;
	

      public ScrollListener(ScrolledComposite sc) {
		super();
		this.sc = sc;
	}

	public void handleEvent(Event e) {
        Control child = (Control) e.widget;
        Rectangle bounds = child.getBounds();
        Rectangle area = sc.getClientArea();
        Point origin = sc.getOrigin();
        if (origin.x > bounds.x)
          origin.x = Math.max(0, bounds.x);
        if (origin.y > bounds.y)
          origin.y = Math.max(0, bounds.y);
        if (origin.x + area.width < bounds.x + bounds.width)
          origin.x = Math
              .max(0, bounds.x + bounds.width - area.width);
        if (origin.y + area.height < bounds.y + bounds.height)
          origin.y = Math.max(0, bounds.y + bounds.height
              - area.height);
        sc.setOrigin(origin);
      }
 }
