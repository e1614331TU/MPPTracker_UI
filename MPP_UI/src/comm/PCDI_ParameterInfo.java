package comm;
/**
* PCDI_ParameterInfo
* <p>
* this class represents the information of a parameter including 
* value number, type, permission, name and unit
* 
*/
public class PCDI_ParameterInfo {
	private int valNr;
	private PCDI_TYPES type;
	private PCDI_PERMISSION permission;
	private String name;
	private PCDI_UNIT unit;
	
	/**
	 * this is the constructor which needs all relevant information about the parameter
	 * @param valNr the value number of the parameter
	 * @param type the type of the value
	 * @param permission the permission of the parameter (read/write/..)
	 * @param name a 5 character string that represents a short name
	 * @param unit the unit of the value/parameter
	 */
	public PCDI_ParameterInfo(int valNr, PCDI_TYPES type, PCDI_PERMISSION permission, String name, PCDI_UNIT unit) {
		this.valNr = valNr;
		this.type = type;
		this.permission=permission;
		this.name=name;
		this.unit=unit;
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
	public int getValNr() {
		return valNr;
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
	 * @return the value type of the parameter
	 */
	public PCDI_TYPES getType() {
		return type;
	}
	/**
	 * converts the parameterinfo into a string
	 */
	public String toString() {
		return new String("Name: "+name+" ValNr: "+valNr+" Type: "+type.name()+" Permission: "+permission.name()+" Unit: "+unit.name());
	}
}
