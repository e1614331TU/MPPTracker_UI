package comm;
/**
* PCDI_Parameter
* <p>
* this abstract class represents a parameter, it consits of a value number, the value type and the value itself
* 
*/
public abstract class PCDI_Parameter<T> {
	private int valNr;
	private PCDI_TYPES type;
	private T value;
	
	/**
	 * the constructor needs all relevant information
	 * @param valNr is the value number of the parameter
	 * @param type is the value type number of the parameter
	 * @param value is the value of the parameter
	 */
	public PCDI_Parameter(int valNr, PCDI_TYPES type, T value) {
		this.valNr = valNr;
		this.type = type;
		this.value = value;
	}
	
	/**
	 * @param value is the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}
	/**
	 * @param value is the value to set as string
	 */
	public abstract void setValue(String value);

	/**
	 * @return the value number
	 */
	public int getValueNumber() {
		return this.valNr;
	}
	
	/**
	 * @return the enum type 
	 */
	public PCDI_TYPES getType() {
		return this.type;
	}
	
	/**
	 * @return the value
	 */
	public T getValue() {
		return this.value;
	}
	/**
	 * toString method
	 */
	public String toString() {
		return new String("ValNr: "+valNr+" Type: "+type.name()+" Value: "+value);
	}
}
