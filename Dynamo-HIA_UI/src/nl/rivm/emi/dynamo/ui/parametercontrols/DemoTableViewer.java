package nl.rivm.emi.dynamo.ui.parametercontrols;


import nl.rivm.emi.dynamo.ui.listeners.for_test.TestControlListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestDisposeListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestDragDetectlListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestFocusListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestHelpListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestKeyListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestMenuDetectListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestMouseListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestMouseMoveListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestMouseTrackListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestMouseWheelListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestPaintListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestSelectionListener;
import nl.rivm.emi.dynamo.ui.listeners.for_test.TestTraverseListener;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class DemoTableViewer {

	public void putInFormLayout(Composite parent, DummyElement[] datas,
			FormData formData) {
		Table table = put(parent, datas);
		table.setLayoutData(formData);
	}

	public Table put(Composite parent, DummyElement[] datas) {
		Table table = new Table(parent, SWT.SINGLE);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		table.setLayout(tableLayout);
		// table.setBackground(new Color(null, 0x00, 0x00, 0xee));
		new TableColumn(table, SWT.LEFT).setText("col1");
		new TableColumn(table, SWT.NONE).setText("col2");
		new TableColumn(table, SWT.RIGHT).setText("col3");
		attachListeners(table);
		final TableViewer tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new DummyContentProvider());
		tableViewer.setLabelProvider(new DummyLabelProvider());
		tableViewer.setCellEditors(new CellEditor[] {
				new ColorCellEditor(table), new TextCellEditor(table),
				new TextCellEditor(table) });

		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				boolean canModify = false;
				if (property == "col1")
					;
				else if (property == "col2")
					canModify = true;
				else if (property == "col3")
					canModify = true;
				else
					;
				return canModify;
			}

			public Object getValue(Object element, String property) {
				DummyElement e = (DummyElement) element;
				if (property == "col1")
					return e.col1;
				else if (property == "col2")
					return e.col2;
				else if (property == "col3")
					return e.col3;
				else
					return null;
			}

			public void modify(Object element, String property, Object value) {
				// workaround for bug 1938 where element is Item rather than
				// model element
				Item item = (Item) element;
				DummyElement e = (DummyElement) item.getData();
				if (property == "col1")
					e.col1 = (RGB) value;
				else if (property == "col2")
					e.col2 = (String) value;
				else if (property == "col3")
					e.col3 = (String) value;

				// This is a hack. Changing the model above should cause it to
				// notify
				// the content provider, which should update the viewer.
				// It should not be done directly here.
				tableViewer.update(e, null);
			}
		});

		tableViewer
				.setColumnProperties(new String[] { "col1", "col2", "col3" });
		tableViewer.setInput(datas);
		return table;
	}

	private void attachListeners(Table table) {
		table.addControlListener((ControlListener) new TestControlListener());
		table.addDisposeListener((DisposeListener) new TestDisposeListener());
		table
				.addDragDetectListener((DragDetectListener) new TestDragDetectlListener());
		table.addFocusListener((FocusListener) new TestFocusListener());
		table.addHelpListener((HelpListener) new TestHelpListener());
		table.addKeyListener((KeyListener) new TestKeyListener());
		table.addListener(1, new TestListener()); // TODO WTF is the integer?
		table
				.addMenuDetectListener((MenuDetectListener) new TestMenuDetectListener());
		table.addMouseListener((MouseListener) new TestMouseListener());
		table
				.addMouseMoveListener((MouseMoveListener) new TestMouseMoveListener());
		table
				.addMouseTrackListener((MouseTrackListener) new TestMouseTrackListener());
		table
				.addMouseWheelListener((MouseWheelListener) new TestMouseWheelListener());
		table.addPaintListener((PaintListener) new TestPaintListener());
		table
				.addSelectionListener((SelectionListener) new TestSelectionListener());
		table
				.addTraverseListener((TraverseListener) new TestTraverseListener());
	}

	public static class DummyElement {

		public RGB col1;
		public String col2;
		public String col3;

		public DummyElement(RGB col1, String col2, String col3) {
			this.col1 = col1;
			this.col2 = col2;
			this.col3 = col3;
		}
	}

	public static class DummyLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String columnText = null;
			DummyElement dummyElement = (DummyElement) element;
			switch (columnIndex) {
			case 0:
				columnText = "" + dummyElement.col1;
				break;
			case 1:
				columnText = "" + dummyElement.col2;
				break;
			case 2:
				columnText = "" + dummyElement.col3;
				break;
			}
			return columnText;
		}
	}

	public static class DummyContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object input) {
			return (DummyElement[]) input;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// should hook a listener on the model here (newInput)
		}
	}
}
