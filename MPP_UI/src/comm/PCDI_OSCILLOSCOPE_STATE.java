package comm;
/**
* PCDI_OSCILLOSCOPE_STATE
* <p>
* this enum is used for the oscilloscope answer of the device and informs about the state of the oscilloscope
* 
*/
public enum PCDI_OSCILLOSCOPE_STATE {
	OSCI_READY((byte) 0),
	OSCI_ERROR((byte) 1),
	OSCI_RUNNING((byte) 2),
	INVALID((byte) 100);
	
	private final Byte code;
	
	PCDI_OSCILLOSCOPE_STATE(Byte code){
		this.code = code;
	}
	
	public Byte getCode() {
		return this.code;
	}
	
	public static PCDI_OSCILLOSCOPE_STATE fromByte(byte osci_state) {
		for(PCDI_OSCILLOSCOPE_STATE type:PCDI_OSCILLOSCOPE_STATE.values()) {
			if(type.getCode()==osci_state) {
				return type;
			}
		}
		return PCDI_OSCILLOSCOPE_STATE.INVALID;
	}	
}
