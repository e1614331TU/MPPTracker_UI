package comm;
/**
* PCDI_MESSAGE_TYPE
* <p>
* this enum is used as part of the protocol to tell which messagetype is sent/received
* 
*/
public enum PCDI_MESSAGE_TYPE {
	CMD((byte) 0),
	PARAMTER_GET_INFO((byte) 1),
	PARAMETER_READ((byte) 2),
	PARAMETER_WRITE((byte) 3),
	OSCILLOSCOPE_START((byte) 4),
	HEARTBEAT((byte) 5),
	PARAMTER_GET_INFO_ANSWER((byte) 129),
	PARAMETER_READ_ANSWER((byte) 130),
	PARAMETER_WRITE_ANSWER((byte) 131),
	COMMAND_ANSWER((byte) 132),
	COMMAND_INVALID_ANSWER((byte) 133),
	PARAMETER_INVALID_ANSWER((byte) 134),
	OSCILLOSCOPE_DATA_ANSWER((byte) 135),
	ERROR_MESSAGE((byte) 136),
	OSCILLOSCOPE_STATE_ANSWER((byte) 137),
	HEARTBEAT_ANSWER((byte) 138),
	INVALID((byte) 7);
	
	private final Byte code;
	
	PCDI_MESSAGE_TYPE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	public static PCDI_MESSAGE_TYPE fromByte(byte msg_type) {
		for(PCDI_MESSAGE_TYPE type:PCDI_MESSAGE_TYPE.values()) {
			if(type.getCode()==msg_type) {
				return type;
			}
		}
		return PCDI_MESSAGE_TYPE.INVALID;
	}
}
