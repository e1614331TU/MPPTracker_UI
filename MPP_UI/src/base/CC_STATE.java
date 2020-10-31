package base;


/**
* CC_State
* <p>
* this enum is used for the state of the currentcontroller
* 
*/
public enum CC_STATE {
	DISABLED((byte) 0),
	ENABLED((byte) 1),

	INVALID((byte) 255);
	
	private final Byte code;
	
	CC_STATE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static CC_STATE fromByte(byte val) {
		for(CC_STATE type:CC_STATE.values()) {
			if(type.getCode()==val) {
				return type;
			}
		}
		return CC_STATE.INVALID;
	}	
	
}