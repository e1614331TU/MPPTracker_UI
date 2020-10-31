package comm;
/**
* PCDI_ParameterInt
* <p>
* this class implements the PCDI_Parameter for the type Int (2 Byte value)
* 
*/
public class PCDI_ParameterInt extends PCDI_Parameter<Short> {

	/**
	 * the constructor needs all relevant information
	 * @param valNr is the value number of the parameter
	 * @param type is the value type number of the parameter
	 * @param value is the value of the parameter
	 */
	public PCDI_ParameterInt(int valNr, PCDI_TYPES type, Short value) {
		super(valNr, type, value);
	}

	@Override
	public void setValue(String value) {
		super.setValue(Short.parseShort(value));
		
	}

}
