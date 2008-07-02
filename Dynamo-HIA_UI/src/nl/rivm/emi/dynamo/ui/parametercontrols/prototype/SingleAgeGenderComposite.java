package nl.rivm.emi.dynamo.ui.parametercontrols.prototype;

import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.verification.AgeTextVerificationListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SingleAgeGenderComposite extends Composite {
	public Text age = new Text(this,SWT.NONE);
	public Text femaleText =  new Text(this,SWT.NONE);
	public Text maleText =  new Text(this,SWT.NONE);

	public SingleAgeGenderComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		setLayout(gridLayout);
		hookupListeners();
	}
	
	public void hookupListeners(){
		age.addVerifyListener(new AgeTextVerificationListener());
	}

	// public void putTestLabel(Composite parent) {
	// Text label = new Text(parent, SWT.BORDER);
	// label.setText("Test-tekst");
	// label.setBackground(new Color(null, 0xee, 0x00, 0x00));
	// GridData layoutData = new GridData();
	// layoutData.heightHint = 20;
	// layoutData.widthHint = 100;
	// label.setLayoutData(layoutData);
	// }
	//
	public void putData(DatabindableAgeGenderRow rows) {
		putLabels();
		bindData(rows);
	}

	private void putLabels() {
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
	}

	private void bindData(DatabindableAgeGenderRow row) {
//		for (AgeGenderRow row : rows) {
			age = new Text(this, SWT.NONE);
			age.setText(row.getAgeValue().toString());
			femaleText = new Text(this, SWT.NONE);
			femaleText.setText(row.getFemaleValue().toString());
			maleText = new Text(this, SWT.NONE);
			maleText.setText(row.getMaleValue().toString());
//		}
	}

	public Text getAge() {
		return age;
	}

	public void setAge(Text age) {
		this.age = age;
	}

	public Text getFemaleText() {
		return femaleText;
	}

	public void setFemaleText(Text femaleText) {
		this.femaleText = femaleText;
	}

	public Text getMaleText() {
		return maleText;
	}

	public void setMaleText(Text maleText) {
		this.maleText = maleText;
	}
}
