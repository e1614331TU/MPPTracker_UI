package comm;
/**
* PCDI_PERMISSION
* <p>
* this enum is used for parameter info and tells wether the parameter is readable/writeable
* 
*/
public enum PCDI_PERMISSION {
	READ((byte) 0),
	WRITE((byte) 1),
	READWRITE((byte) 2),
	INVALID((byte) 3);
	
	private final Byte code;
	
	PCDI_PERMISSION(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	public static PCDI_PERMISSION fromByte(byte permission_type) {
		for(PCDI_PERMISSION type:PCDI_PERMISSION.values()) {
			if(type.getCode()==permission_type) {
				return type;
			}
		}
		return PCDI_PERMISSION.INVALID;
	}
}
