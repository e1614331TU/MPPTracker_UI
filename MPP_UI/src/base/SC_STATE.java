package base;
/**
* SC_State
* <p>
* this enum is used for the state of the speedcontroller
* 
*/
public enum SC_STATE {
	DISABLED((byte) 0),
	ENABLED((byte) 1),

	INVALID((byte) 255);
	
	private final Byte code;
	
	SC_STATE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static SC_STATE fromByte(byte val) {
		for(SC_STATE type:SC_STATE.values()) {
			if(type.getCode()==val) {
				return type;
			}
		}
		return SC_STATE.INVALID;
	}	
	
}
