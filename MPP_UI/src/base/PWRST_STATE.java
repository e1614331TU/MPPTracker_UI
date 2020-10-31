package base;
/**
* PWRST_STATE
* <p>
* this enum is used for the state of the power-stage
* 
*/
public enum PWRST_STATE {
	NONE((byte) 1),
	PWRST_ERR_VDS_OVERCURRENT((byte) 2),
	PWRST_ERR_GATE_DRIVER_FAULT((byte) 3),
	PWRST_ERR_UNDERVOLTAGE((byte) 4),
	PWRST_ERR_OVERTEMP((byte) 5),
	PWRST_ERR_OC_HIGH_A((byte) 6),
	PWRST_ERR_OC_LOW_A((byte) 7),
	PWRST_ERR_OC_HIGH_B((byte) 8),
	PWRST_ERR_OC_LOW_B((byte) 9),
	PWRST_ERR_OC_HIGH_C((byte) 10),
	PWRST_ERR_OC_LOW_C((byte) 11),
	PWRST_ERR_OC_A((byte) 12),
	PWRST_ERR_OC_B((byte) 13),
	PWRST_ERR_OC_C((byte) 14),
	PWRST_ERR_TEMP_WARN((byte) 15),
	PWRST_ERR_CHARGE_PUMP((byte) 16),

	INVALID((byte) 255);
	
	private final Byte code;
	
	PWRST_STATE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static PWRST_STATE fromByte(byte val) {
		for(PWRST_STATE type:PWRST_STATE.values()) {
			if(type.getCode()==val) {
				return type;
			}
		}
		return PWRST_STATE.INVALID;
	}	
	
}