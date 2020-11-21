package comm;

import java.util.ArrayList;
import java.util.List;

/**
* PCDI_Message
* <p>
* this class is used for generating a bytewise message
* which can be sent to the device through the hardware-interface.
* It converts data to bytewise message, adds header and 
* calculates checksum. 
* 
*/
public class PCDI_Message {

	private Byte length;
	private Byte deviceId;
	private PCDI_MESSAGE_TYPE type;
	private List<Byte> data = new ArrayList<Byte>();
	private Byte cs;
	
	
	public static final Byte HEADER = (byte) 0xAA;
	public static final int BASE_LEN = 5;
	
	
	/**
	 * Constructor
	 * @param deviceId the deviceId of the communication-device
	 * @param type the message type
	 * @param data the data (command, value,..)
	 * @param cs the checksum byte to be compared
	 * @throws CS_Exception if the checksums dont match
	 */
	public PCDI_Message(int deviceId,PCDI_MESSAGE_TYPE type, List<Byte> data, byte cs) throws CS_Exception {
		this.deviceId=(byte) deviceId;
		this.type = type;
		for(byte tmp : data) {
			this.data.add(tmp);
		}
		this.length = (byte) (this.data.size() + PCDI_Message.BASE_LEN);
		if(!(this.calculateChecksum() == cs)) {throw new CS_Exception();}
		this.cs = cs;
	}
	/**
	 * Constructor
	 * @param deviceId the deviceId of the communication-device
	 * @param type the message type
	 * @param data the data (command, value,..)
	 */
	public PCDI_Message(int deviceId,PCDI_MESSAGE_TYPE type, List<Byte> data) {
		this.deviceId=(byte) deviceId;
		this.type = type;
		for(byte tmp : data) {
			this.data.add(tmp);
		}
		this.length = (byte) (this.data.size() + PCDI_Message.BASE_LEN);
	}
	
	/**
	 * Constructor
	 * the data is added afterwards
	 * @param deviceId the deviceId of the communication-device
	 * @param type the message type
	 */
	public PCDI_Message(int deviceId,PCDI_MESSAGE_TYPE type) {
		this.deviceId=(byte) deviceId;
		this.type = type;
		this.length = PCDI_Message.BASE_LEN;
	}
	
	/**
	 * this method is used to add a databyte to the message
	 * @param dataByte the byte to be added
	 */
	public void addData(byte dataByte) {
		this.data.add(dataByte);
		this.length = (byte) (this.data.size() + PCDI_Message.BASE_LEN);
	}
	
	/**
	 * this method is used to add databytes to the message
	 * @param dataBytes the bytes to be added
	 */
	public void addData(List<Byte> dataBytes) {
		for(byte tmp : dataBytes) {
			this.data.add(tmp);
		}
		this.length = (byte) (this.data.size() + PCDI_Message.BASE_LEN);
	}
	
	/**
	 * this method is used to calculate the checksum
	 * @return the checksum (sum of all bytes modulo 255)
	 */
	public byte calculateChecksum() {
		int res = 0;
		/*res = res + (((int)PCDI_Message.HEADER) & 0xFF);
		res = res + (((int)this.length) & 0xFF);
		res = res + (((int)this.deviceId) & 0xFF);
		res = res + (((int)this.type.getCode()) & 0xFF);
		for(Byte tmp : this.data) {
			res = res +(((int)tmp) & 0xFF);
		}
		return (byte)((res)&0xFF);*/
		res = res + (Byte.toUnsignedInt(PCDI_Message.HEADER) & 0xFF);
		res = res + (Byte.toUnsignedInt(this.length) & 0xFF);
		res = res + (Byte.toUnsignedInt(this.deviceId) & 0xFF);
		res = res + (Byte.toUnsignedInt(this.type.getCode()) & 0xFF);
		for(Byte tmp : this.data) {
			res = res +(Byte.toUnsignedInt(tmp) & 0xFF);
		}
		return (byte)((res)&0xFF);
	}
	
	/**
	 * @return the number of bytes as byte
	 */
	public Byte getLength() {
		return length;
	}

	/**
	 * @return the device id
	 */
	public Byte getDeviceId() {
		return deviceId;
	}

	/**
	 * @return the message type
	 */
	public PCDI_MESSAGE_TYPE getType() {
		return type;
	}

	/**
	 * @return the data as list of bytes
	 */
	public List<Byte> getData() {
		return data;
	}

	/** 
	 * @return the checksum
	 */
	public Byte getCs() {
		return cs;
	}

	/**
	 * @return a bytewise message which can be sent to a device
	 */
	public List<Byte> generateBytewiseMessage(){
		List<Byte> result = new ArrayList<Byte>();
		result.add(PCDI_Message.HEADER);
		result.add(this.length);
		result.add(this.deviceId);
		result.add(this.type.getCode());
		for(byte tmp : this.data) {
			result.add(tmp);
		}
		result.add(this.calculateChecksum());
		return result;
	}
	
	/**
	 * generates a string representing the object
	 */
	@Override
	public String toString() {
		String res = "PCDI_Message[id: "+ Byte.toUnsignedInt(this.type.getCode()) + ", length:" + this.length.toString() + ", type:" + this.type.toString() + ", cs:" + this.calculateChecksum() + "]";
		for(byte tmp : this.data) {
			res = res +  Byte.toUnsignedInt(tmp) + ":";
		}
		return res;
	}
	
}
