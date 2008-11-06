package nl.rivm.emi.dynamo.ui.poc;


import org.eclipse.swt.widgets.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.events.*;
import org.eclipse.jface.viewers.*;

public class IncidenceDataTable {

//	public static void main(String[] args) {
//		Display display = new Display();
//		Shell shell = new Shell(display);
//		shell.setSize(400, 400);
//		shell.setLayout(new FillLayout());
//
//		DummyElement[] datas = new DummyElement[] {
//				new DummyElement(new RGB(255, 12, 40), "row1col2", "row1col3"),
//				new DummyElement(new RGB(70, 255, 40), "row2col2", "row2col3") };
//
//		IncidenceDataTable theTable = new IncidenceDataTable();
//		theTable.put(shell, datas);
//
//		shell.open();
//		while (!shell.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}
//		display.dispose();
//	}

	public void putInFormLayout(Composite parent, DummyElement[] datas, FormData formData) {
		Table table = put(parent, datas);
		table.setLayoutData(formData);
	}

	public Table put(Composite parent, DummyElement[] datas) {
		Table table = new Table(parent, SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		tableLayout.addColumnData(new ColumnWeightData(1, 50, true));
		table.setLayout(tableLayout);

		new TableColumn(table, SWT.LEFT).setText("col1");
		new TableColumn(table, SWT.NONE).setText("col2");
		new TableColumn(table, SWT.RIGHT).setText("col3");

		final TableViewer tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new DummyContentProvider());
		tableViewer.setLabelProvider(new DummyLabelProvider());
		tableViewer.setCellEditors(new CellEditor[] {
				new ColorCellEditor(table), new TextCellEditor(table),
				new TextCellEditor(table) });

		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return true;
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
