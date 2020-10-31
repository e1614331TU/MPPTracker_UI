package comm;
/**
* PCDI_COMMANDS
* <p>
* this enum is used for the pcdi commands which can be sent to the device
* 
*/
public enum PCDI_COMMANDS {
	RESET((byte) 0),
	EMERGENCY_STOP((byte) 1),
	INVALID((byte) 2),
	START_REF((byte) 3),
	RESET_PWRSTG((byte) 4),
	SET_HOME((byte) 5),
	SET_PWM((byte) 6);
	
	private final Byte code;
	
	PCDI_COMMANDS(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static PCDI_COMMANDS fromByte(byte pcdi_command) {
		for(PCDI_COMMANDS type:PCDI_COMMANDS.values()) {
			if(type.getCode()==pcdi_command) {
				return type;
			}
		}
		return PCDI_COMMANDS.INVALID;
	}	
	
}
