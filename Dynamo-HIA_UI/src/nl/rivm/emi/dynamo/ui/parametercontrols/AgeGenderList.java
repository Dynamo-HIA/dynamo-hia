package nl.rivm.emi.dynamo.ui.parametercontrols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AgeGenderList extends Composite {

//	Composite container = null;

	public AgeGenderList(Composite parent, int style) {
		super(parent, style);
//		container = new Composite(this, SWT.NONE);
//		FillLayout fillLayout = new FillLayout();
//		this.setLayout(fillLayout);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
//		setBackground(new Color(null, 0x00, 0x00, 0x00));
//		container.setLayout(gridLayout);
		setLayout(gridLayout);
		}

	public void putTestLabel(Composite parent) {
		Text label = new Text(parent, SWT.BORDER);
		label.setText("Test-tekst");
		label.setBackground(new Color(null, 0xee, 0x00, 0x00));
		GridData layoutData = new GridData();
//		layoutData.horizontalAlignment = GridData.BEGINNING;
		 layoutData.heightHint = 20;
		 layoutData.widthHint = 100;
		label.setLayoutData(layoutData);
	}

	public void putData(DatabindableAgeGenderRow[] rows) {
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
//		label.setBackground(new Color(null, 0xee, 0x00, 0x00));
//		GridData layoutData = new GridData();
//		layoutData.horizontalAlignment = GridData.BEGINNING;
//		layoutData.heightHint = 10;
//		layoutData.widthHint = 20;
//		label.setLayoutData(layoutData);
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");

		for (DatabindableAgeGenderRow row : rows) {
			Label label = new Label( this, SWT.NONE);
			label.setText(row.getAgeAsString());
//			label.setBackground(new Color(null, 0xee, 0x00, 0x00));
//			GridData layoutData = new GridData();
//			layoutData.horizontalAlignment = GridData.BEGINNING;
//			layoutData.heightHint = 10;
//			layoutData.widthHint = 20;
//			label.setLayoutData(layoutData);
			Text femaleText = new Text(this, SWT.NONE);
			femaleText.setText(row.getFemaleParamAsString());
			Text maleText = new Text(this, SWT.NONE);
			maleText.setText(row.getMaleParamAsString());
		}
	}
}
