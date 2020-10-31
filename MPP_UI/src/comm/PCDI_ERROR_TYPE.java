package comm;
/**
* PCDI_ERROR_TYPE
* <p>
* this enum is used from the device to inform about about an error
* 
*/
public enum PCDI_ERROR_TYPE {
	CHECKSUM_ERROR((byte) 0),
	INVALID_LENGTH_ERROR((byte) 1),
	INVALID_MESSAGE_ID((byte) 2),
	INVALID((byte) 5);
	
	private final Byte code;
	
	PCDI_ERROR_TYPE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	public static PCDI_ERROR_TYPE fromByte(byte err_type) {
		for(PCDI_ERROR_TYPE type:PCDI_ERROR_TYPE.values()) {
			if(type.getCode()==err_type) {
				return type;
			}
		}
		return PCDI_ERROR_TYPE.INVALID;
	}
}
