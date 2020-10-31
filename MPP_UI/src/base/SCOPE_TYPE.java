package base;
/**
* SCOPE_TYPE
* <p>
* this enum is used for the scope, wether it is using the fast-scope-mode or a cyclic read mode (slow)
* 
*/
public enum SCOPE_TYPE {
	FAST_SCOPE((byte) 0),
	CYCLE_READ((byte) 1),
	INVALID((byte) 255);
	
	private final Byte code;
	
	SCOPE_TYPE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static SCOPE_TYPE fromByte(byte val) {
		for(SCOPE_TYPE type:SCOPE_TYPE.values()) {
			if(type.getCode()==val) {
				return type;
			}
		}
		return SCOPE_TYPE.INVALID;
	}	
}
