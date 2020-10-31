package comm;
/**
* PCDI_OSCILLOSCOPE_COMMAND
* <p>
* this enum is used for the oscilloscope start command and tells wether to start only or to execute additional actions
* 
*/
public enum PCDI_OSCILLOSCOPE_COMMAND {
	START_ONLY((byte) 0),
	START_STEP_RESPONSE_CC((byte) 1),
	START_STEP_RESPONSE_SC((byte) 2),
	START_STEP_RESPONSE_PC((byte) 3),
	START_STEP_RESPONSE_INERTIA((byte) 4),
	INVALID((byte) 100);
	
	private final Byte code;
	
	PCDI_OSCILLOSCOPE_COMMAND(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static PCDI_OSCILLOSCOPE_COMMAND fromByte(byte osci_cmd) {
		for(PCDI_OSCILLOSCOPE_COMMAND type:PCDI_OSCILLOSCOPE_COMMAND.values()) {
			if(type.getCode()==osci_cmd) {
				return type;
			}
		}
		return PCDI_OSCILLOSCOPE_COMMAND.INVALID;
	}	
}
