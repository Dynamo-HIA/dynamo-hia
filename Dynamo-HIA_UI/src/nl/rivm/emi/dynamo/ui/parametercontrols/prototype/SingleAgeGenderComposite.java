package nl.rivm.emi.dynamo.ui.parametercontrols.prototype;

import nl.rivm.emi.dynamo.ui.parametercontrols.DatabindableAgeGenderRow;
import nl.rivm.emi.dynamo.ui.verifylisteners.AgeTextVerifyListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SingleAgeGenderComposite extends Composite {
	private Text ageText = new Text(this,SWT.NONE);
	private Text femaleText =  new Text(this,SWT.NONE);
	private Text maleText =  new Text(this,SWT.NONE);

	public SingleAgeGenderComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		setLayout(gridLayout);
		hookupVerificationListeners();
	}
	
	public void hookupVerificationListeners(){
		ageText.addVerifyListener(new AgeTextVerifyListener());
	}

	public void putData(DatabindableAgeGenderRow row) {
		setLabels();
		setTexts(row);
	}

	private void setLabels() {
		Label ageLabel = new Label(this, SWT.NONE);
		ageLabel.setText("Age");
		Label femaleLabel = new Label(this, SWT.NONE);
		femaleLabel.setText("Female");
		Label maleLabel = new Label(this, SWT.NONE);
		maleLabel.setText("Male");
	}

	private void setTexts(DatabindableAgeGenderRow row) {
			ageText = new Text(this, SWT.NONE);
			ageText.setText(row.getAgeValue().toString());
			femaleText = new Text(this, SWT.NONE);
			femaleText.setText(row.getFemaleValue().toString());
			maleText = new Text(this, SWT.NONE);
			maleText.setText(row.getMaleValue().toString());
	}

	public Text getAgeText() {
		return ageText;
	}

	public void setAgeText(Text age) {
		this.ageText = age;
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
