package comm;
/**
* PCDI_TYPES
* <p>
* this enum is used for parameter value data type
* 
*/
public enum PCDI_TYPES {
	FLOAT((byte) 0),
	LONG((byte) 1),
	INT((byte) 2),
	BYTE((byte) 3),
	INVALID((byte) 4);
	
	private final Byte code;
	
	PCDI_TYPES(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	public static PCDI_TYPES fromByte(byte pcdi_type) {
		for(PCDI_TYPES type:PCDI_TYPES.values()) {
			if(type.getCode()==pcdi_type) {
				return type;
			}
		}
		return PCDI_TYPES.INVALID;
	}
}
