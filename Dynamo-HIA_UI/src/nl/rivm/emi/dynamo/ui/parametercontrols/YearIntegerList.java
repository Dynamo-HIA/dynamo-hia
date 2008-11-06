package nl.rivm.emi.dynamo.ui.parametercontrols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class YearIntegerList extends Composite {

//	Composite container = null;

	public YearIntegerList(Composite parent, int style) {
		super(parent, style);
//		container = new Composite(this, SWT.NONE);
//		FillLayout fillLayout = new FillLayout();
//		this.setLayout(fillLayout);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
//		setBackground(new Color(null, 0x00, 0x00, 0x00));
//		container.setLayout(gridLayout);
		setLayout(gridLayout);
		}

	public void putData(YearIntegerDataRow[] rows) {
		Label yearLabel = new Label(this, SWT.NONE);
		yearLabel.setText("Year");
//		label.setBackground(new Color(null, 0xee, 0x00, 0x00));
//		GridData layoutData = new GridData();
//		layoutData.horizontalAlignment = GridData.BEGINNING;
//		layoutData.heightHint = 10;
//		layoutData.widthHint = 20;
//		label.setLayoutData(layoutData);
		Label integerLabel = new Label(this, SWT.NONE);
		integerLabel.setText("Integer");

		for (YearIntegerDataRow row : rows) {
			Label label = new Label( this, SWT.NONE);
			label.setText(row.getYearAsString());
//			label.setBackground(new Color(null, 0xee, 0x00, 0x00));
//			GridData layoutData = new GridData();
//			layoutData.horizontalAlignment = GridData.BEGINNING;
//			layoutData.heightHint = 10;
//			layoutData.widthHint = 20;
//			label.setLayoutData(layoutData);
			Text femaleText = new Text(this, SWT.NONE);
			femaleText.setText(row.getIntegerAsString());
		}
	}
}
