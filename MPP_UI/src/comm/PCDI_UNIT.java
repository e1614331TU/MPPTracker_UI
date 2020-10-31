package comm;
/**
* PCDI_TYPES
* <p>
* this enum is used for parameter info and tells about the unit of the respective parameter
* 
*/
public enum PCDI_UNIT {
	NONE((byte) 0),
	AMPERE((byte) 1),
	VOLT((byte) 2),
	RAD((byte) 3),
	RADPS((byte) 4),
	OHM((byte) 5),
	HENRY((byte) 6),
	INVALID((byte) 4);
	
	private final Byte code;
	
	PCDI_UNIT(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	public static PCDI_UNIT fromByte(byte unit_type) {
		for(PCDI_UNIT type:PCDI_UNIT.values()) {
			if(type.getCode()==unit_type) {
				return type;
			}
		}
		return PCDI_UNIT.INVALID;
	}
}
