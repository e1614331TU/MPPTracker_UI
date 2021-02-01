package comm;

public class PCDI_TableData {

	private int tableId;
	private int indexNr;
	private float value;
	
	/**
	 * the constructor needs all relevant information
	 * @param valNr is the value number of the parameter
	 * @param type is the value type number of the parameter
	 * @param value is the value of the parameter
	 */
	public PCDI_TableData(int tableId, int indexNr, float value) {
		this.tableId = tableId;
		this.indexNr = indexNr;
		this.value = value;
	}
	
	public int getTableId() {
		return tableId;
	}

	public void setTableNr(int tableNr) {
		this.tableId = tableNr;
	}

	public int getIndexNr() {
		return indexNr;
	}

	public void setIndexNr(int indexNr) {
		this.indexNr = indexNr;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}
