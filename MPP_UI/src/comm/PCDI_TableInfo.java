package comm;

public class PCDI_TableInfo {
	private int tableId;
	private PCDI_PERMISSION permission;
	private String name;
	private PCDI_UNIT unit;
	private int size;
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * this is the constructor which needs all relevant information about the parameter
	 * @param valNr the value number of the parameter
	 * @param type the type of the value
	 * @param permission the permission of the parameter (read/write/..)
	 * @param name a 5 character string that represents a short name
	 * @param unit the unit of the value/parameter
	 */
	public PCDI_TableInfo(int tableId, PCDI_PERMISSION permission, String name, PCDI_UNIT unit, int size) {
		this.tableId = tableId;
		this.permission=permission;
		this.name=name;
		this.unit=unit;
		this.size=size;
	}
	/**
	 * @return the unit of the parameter
	 */
	public PCDI_UNIT getUnit() {
		return unit;
	}
	/**
	 * @return the value number of the parameter
	 */
	public int getTableId() {
		return tableId;
	}
	/**
	 * @return the permission of the parameter
	 */
	public PCDI_PERMISSION getPermission() {
		return permission;
	}
	/**
	 * @return the name of the parameter
	 */
	public String getName() {
		return name;
	}
	/**
	 * converts the tableinfo into a string
	 */
	public String toString() {
		return new String("Name: "+name+" TableNr: "+tableId+" Size: "+size+" Permission: "+permission.name()+" Unit: "+unit.name());
	}
}
