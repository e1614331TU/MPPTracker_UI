package comm;

/**
* PCDI_ParameterByte
* <p>
* this class implements the PCDI_Parameter for the type Byte (1 Byte value)
* 
*/
public class PCDI_ParameterByte extends PCDI_Parameter<Byte> {

	/**
	 * the constructor needs all relevant information
	 * @param valNr is the value number of the parameter
	 * @param type is the value type number of the parameter
	 * @param value is the value of the parameter
	 */
	public PCDI_ParameterByte(int valNr, PCDI_TYPES type, Byte value) {
		super(valNr, type, value);
	}

	@Override
	public void setValue(String value) {
		super.setValue(Byte.valueOf(value));
		
	}

}
