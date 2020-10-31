package base;

/**
* PC_State
* <p>
* this enum is used for the state of the positioncontroller
* 
*/
public enum PC_STATE {
	DISABLED((byte) 0),
	ENABLED((byte) 1),

	INVALID((byte) 255);
	
	private final Byte code;
	
	PC_STATE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static PC_STATE fromByte(byte val) {
		for(PC_STATE type:PC_STATE.values()) {
			if(type.getCode()==val) {
				return type;
			}
		}
		return PC_STATE.INVALID;
	}	
	
}