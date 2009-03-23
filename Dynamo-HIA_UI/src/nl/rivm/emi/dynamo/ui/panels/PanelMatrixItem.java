package nl.rivm.emi.dynamo.ui.panels;

import nl.rivm.emi.dynamo.data.types.atomic.base.AtomicTypeBase;

/**
 * @author schutb
 * 
 * Describes a matrix item in a panel matrix
 *
 * @param <T>
 */
public class PanelMatrixItem<T> {
	
	
	private String uniqueName;
	private String columnHeader;
	private int columnIndex;
	private AtomicTypeBase<T> myType;
	
	/**
	 * @param uniqueName
	 * @param columnHeader
	 * @param columnIndex
	 * @param atomicTypeBase
	 */
	public PanelMatrixItem(String uniqueName, 
			String columnHeader, int columnIndex,
			AtomicTypeBase<T> atomicTypeBase) {
		this.uniqueName = uniqueName;
		this.columnHeader = columnHeader;
		this.columnIndex = columnIndex;
		this.myType = atomicTypeBase;
	}
	/**
	 * @return String
	 */
	public String getUniqueName() {
		return this.uniqueName;
	}
	/**
	 * @param uniqueName
	 */
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	/**
	 * @return AtomicTypeBase<T>
	 */
	public AtomicTypeBase<T> getMyType() {
		return this.myType;
	}
	/**
	 * @param myType
	 */
	public void setMyType(AtomicTypeBase<T> myType) {
		this.myType = myType;
	}
	/**
	 * @return String
	 */
	public String getColumnHeader() {
		return this.columnHeader;
	}
	/**
	 * @param columnHeader
	 */
	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}
	/**
	 * @return int
	 */
	public int getColumnIndex() {
		return this.columnIndex;
	}
	/**
	 * @param columnIndex
	 */
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	
	
}
