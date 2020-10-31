package comm;
/**
* PCDI_OscilloscopeData
* <p>
* this is the class is used to put the data, sent from the device together to one object (all oscilloscope channels to 1 object)
*/
public class PCDI_OscilloscopeData {
	private Float val1;
	private Float val2;
	private Float val3;
	private Float val4;
	private Float val5;
	private Float val6;
	private Float val7;
	
	/**
	 * The constructor sets all the data values of the received data
	 * @param val1 value of scope channel 1
	 * @param val2 value of scope channel 2
	 * @param val3 value of scope channel 3
	 * @param val4 value of scope channel 4
	 * @param val5 value of scope channel 5
	 * @param val6 value of scope channel 6
	 * @param val7 value of scope channel 7
	 */
	public PCDI_OscilloscopeData(Float val1, Float val2, Float val3, Float val4, Float val5, Float val6, Float val7) {
		this.val1 = val1;
		this.val2 = val2;
		this.val3 = val3;
		this.val4 = val4;
		this.val5 = val5;
		this.val6 = val6;
		this.val7 = val7;
	}

	/**
	 * @return value1
	 */
	public Float getVal1() {
		return val1;
	}

	/**
	 * @return value2
	 */
	public Float getVal2() {
		return val2;
	}

	/**
	 * @return value3
	 */
	public Float getVal3() {
		return val3;
	}

	/**
	 * @return value4
	 */
	public Float getVal4() {
		return val4;
	}

	/**
	 * @return value5
	 */
	public Float getVal5() {
		return val5;
	}

	/**
	 * @return value6
	 */
	public Float getVal6() {
		return val6;
	}

	/**
	 * @return value7
	 */
	public Float getVal7() {
		return val7;
	}

	
}
