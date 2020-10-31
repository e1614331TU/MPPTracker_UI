package comm;
/**
* PCDI_ParameterFloat
* <p>
* this class implements the PCDI_Parameter for the type Float (4 Byte value)
* 
*/
public class PCDI_ParameterFloat extends PCDI_Parameter<Float> {

	/**
	 * the constructor needs all relevant information
	 * @param valNr is the value number of the parameter
	 * @param type is the value type number of the parameter
	 * @param value is the value of the parameter
	 */
	public PCDI_ParameterFloat(int valNr, PCDI_TYPES type, Float value) {
		super(valNr, type, value);
	}

	@Override
	public void setValue(String value) {
		super.setValue(Float.parseFloat(value));
		
	}

}
