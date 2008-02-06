package nl.rivm.emi.cdm.characteristic;

public class Characteristic {

	private int index;
	
	private String label = "simplicity";

	public Characteristic(int index, String label){
		this.index = index;
		this.label = label;
	}
	public int getIndex() {
		return index;
	}
	public String getLabel() {
		return label;
	}
}
