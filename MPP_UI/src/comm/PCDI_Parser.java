package comm;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
/**
* PCDI_Parser
* <p>
* this class is needed by the PCD_Interface class to convert data into
* bytes or vice-versa.
* 
*/
public class PCDI_Parser {

	/**
	 * this method is used to parse a parameterinfo request into a message
	 * @param valueNr is the value number of the requested parameter
	 * @param deviceId the deviceId of the communication-device
	 * @return a message that contains the data in bytes including checksum, header and so on
	 */
	public static PCDI_Message parseTxParameterInfo(int valueNr, int deviceId){
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.PARAMTER_GET_INFO);
		msg.addData((byte) valueNr);
		return msg;
	}
	
	/**
	 * this method is used to parse a parameterread-request to a (bytewise) message
	 * @param valueNr the value number of the requested parameter
	 * @param deviceId the deviceId of the communication-device
	 * @return a message that contains the data in bytes including checksum, header and so on
	 */
	public static PCDI_Message parseTxParameterRead(int valueNr, int deviceId){
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.PARAMETER_READ);
		msg.addData((byte) valueNr);
		return msg;
	}
	
	/**
	 * this method is used to parse a parameter-Write request to a bytewise message
	 * @param parameter the parameter including the value to parse
	 * @param deviceId the deviceId of the communication-device
	 * @return a message that contains the data in bytes including checksum, header and so on
	 */
	public static PCDI_Message parseTxParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.PARAMETER_WRITE);
		msg.addData((byte)parameter.getValueNumber());
		if(parameter.getType()==PCDI_TYPES.BYTE) {
			msg.addData(PCDI_TYPES.BYTE.getCode());
			msg.addData((byte) parameter.getValue());	
		}
		else if(parameter.getType()==PCDI_TYPES.FLOAT) {
			msg.addData(PCDI_TYPES.FLOAT.getCode());
			byte[] data=ByteBuffer.allocate(4).putFloat((float) parameter.getValue()).array();
			msg.addData(toByteList(data));			
		}
		else if(parameter.getType()==PCDI_TYPES.INT) {
			msg.addData(PCDI_TYPES.INT.getCode());
			byte[] data=ByteBuffer.allocate(2).putShort((short) parameter.getValue()).array();
			msg.addData(toByteList(data));			
		}
		else if(parameter.getType()==PCDI_TYPES.LONG) {
			msg.addData(PCDI_TYPES.LONG.getCode());
			byte[] data=ByteBuffer.allocate(4).putInt((int) parameter.getValue()).array();
			msg.addData(toByteList(data));			
		}
		return msg;
	}
	/**
	 * this method is used to parse a StartOscilloscope-Request into a (bytewise) Message
	 * @param osci_cmd is the oscilloscope command to execute
	 * @param valNr1 is the requested value number of channel number 1
	 * @param valNr2 is the requested value number of channel number 2
	 * @param valNr3 is the requested value number of channel number 3
	 * @param valNr4 is the requested value number of channel number 4
	 * @param valNr5 is the requested value number of channel number 5
	 * @param valNr6 is the requested value number of channel number 6
	 * @param valNr7 is the requested value number of channel number 7
	 * @param oversampling is the oversampling factor the device should use for sampling
	 * @param deviceId the deviceId of the communication-device
	 * @return a message that contains the data in bytes including checksum, header and so on
	 */
	public static PCDI_Message parseTxStartOscilloscope(PCDI_OSCILLOSCOPE_COMMAND osci_cmd, int valNr1, int valNr2, int valNr3, int valNr4,
			int valNr5, int valNr6, int valNr7, int oversampling, int deviceId) {
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.OSCILLOSCOPE_START);
		msg.addData(osci_cmd.getCode());
		msg.addData((byte)valNr1);
		msg.addData((byte)valNr2);
		msg.addData((byte)valNr3);
		msg.addData((byte)valNr4);
		msg.addData((byte)valNr5);
		msg.addData((byte)valNr6);
		msg.addData((byte)valNr7);
		msg.addData((byte)oversampling);
		return msg;
		
	}
	
	/**
	 * this method is used to parse a Command-Request into a (bytewise) Message
	 * @param cmd the command to parse
	 * @param deviceId the deviceId of the communication-device
	 * @return a message that contains the data in bytes including checksum, header and so on
	 */
	public static PCDI_Message parseTxCommand(PCDI_COMMANDS cmd, int deviceId) {
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.CMD);
		msg.addData((byte) cmd.getCode());
		return msg;
	}
	/**
	 * this method is used to parse an incoming message into a parameter
	 * @param msg the incoming message
	 * @return the from the message converted parameter
	 */
	public static PCDI_Parameter<?> parseRxReadWrite(PCDI_Message msg){
		if(msg.getType()==PCDI_MESSAGE_TYPE.PARAMETER_READ_ANSWER || msg.getType()==PCDI_MESSAGE_TYPE.PARAMETER_WRITE_ANSWER) {
			List<Byte> data=msg.getData();
			int valueNr=data.get(0).intValue();
			PCDI_TYPES valueType=PCDI_TYPES.fromByte(data.get(1));
			
			if(valueType==PCDI_TYPES.BYTE) {
				byte value=data.get(2);
				PCDI_Parameter<Byte> param=new PCDI_ParameterByte(valueNr,valueType, value);
				return param;
			}
			else if(valueType==PCDI_TYPES.FLOAT) {
				byte[] floatbyte=toByteArray(data.subList(2, 6));
				float value = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
				PCDI_Parameter<Float> param=new PCDI_ParameterFloat(valueNr,valueType, value);
				return param;
			}
			else if(valueType==PCDI_TYPES.INT) { //int --> 32bits, short --> 16bits 
				short value = ByteBuffer.wrap(toByteArray(data.subList(2, 4))).order(ByteOrder.BIG_ENDIAN).getShort();
				PCDI_Parameter<Short> param=new PCDI_ParameterInt(valueNr,valueType, value);
				return param;
			}
			else if(valueType==PCDI_TYPES.LONG) {
				int value = ByteBuffer.wrap(toByteArray(data.subList(2, 6))).order(ByteOrder.BIG_ENDIAN).getInt();
				PCDI_Parameter<Integer> param=new PCDI_ParameterLong(valueNr,valueType, value);
				return param;
			}
			else if(valueType==PCDI_TYPES.INVALID) {
				
			}
		}
		return null;
	
	}
	/**
	 * this method is used to parse an incoming message into a parameter-Info
	 * @param msg is the incoming message
	 * @return the converted parameterinfo
	 */
	public static PCDI_ParameterInfo parseRxInfo(PCDI_Message msg){
		//value nr type u value
		if(msg.getType()==PCDI_MESSAGE_TYPE.PARAMTER_GET_INFO_ANSWER) {
			List<Byte> data=msg.getData();
			
			int valueNr=data.get(0).intValue();
			int length=msg.getLength();
			PCDI_TYPES valueType=PCDI_TYPES.fromByte(data.get(1));
			PCDI_PERMISSION permission=PCDI_PERMISSION.fromByte(data.get(2));
			PCDI_UNIT unit=PCDI_UNIT.fromByte(data.get(3));
			String name="err";
			try {
				name = new String(toByteArray(data.subList(4, length-5)), "ISO-8859-1");//"ASCII"
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
			PCDI_ParameterInfo param=new PCDI_ParameterInfo(valueNr, valueType, permission,name,unit);
			return param;
		}
		return null;
		
	}
	/**
	 * this method is used to parse an incoming message into OscilloscopeData
	 * @param msg is the incoming message
	 * @return the oscilloscope-data
	 */
	public static PCDI_OscilloscopeData parseRxOscilloscopeData(PCDI_Message msg) {
		List<Byte> data=msg.getData();
		byte[] floatbyte=toByteArray(data.subList(0, 4));
		float cur_U = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		floatbyte=toByteArray(data.subList(4, 8));
		float cur_V = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		floatbyte=toByteArray(data.subList(8, 12));
		float cur_W = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		//short encoderPos = ByteBuffer.wrap(toByteArray(data.subList(12, 14))).order(ByteOrder.BIG_ENDIAN).getShort();
		
		floatbyte=toByteArray(data.subList(12, 16));
		float gamma_el = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		floatbyte=toByteArray(data.subList(16, 20));
		float PWM_U = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		floatbyte=toByteArray(data.subList(20, 24));
		float PWM_V = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		floatbyte=toByteArray(data.subList(24, 28));
		float PWM_W = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		
		//return new PCDI_OscilloscopeData(cur_U,cur_V,cur_W,encoderPos);
		return new PCDI_OscilloscopeData(cur_U,cur_V,cur_W,gamma_el,PWM_U,PWM_V,PWM_W);
	}
	/**
	 * this method is used to parse an incoming message into a command
	 * @param msg the incoming message
	 * @return the from the message extracted command
	 */
	public static PCDI_COMMANDS parseRxCommand(PCDI_Message msg) {
		return PCDI_COMMANDS.fromByte(msg.getData().get(0));
		
	}
	/**
	 * parses an incoming message into OscilloscopeState
	 * @param msg the incoming message
	 * @return the enum oscilloscope-state
	 */
	public static PCDI_OSCILLOSCOPE_STATE parseRxOscilloscopeState(PCDI_Message msg) {
		return PCDI_OSCILLOSCOPE_STATE.fromByte(msg.getData().get(0));
	}
	/**
	 * parses an incoming message into an error
	 * @param PCDI_Message msg
	 * @return PCDI_ERROR_TYPE
	 */
	public static PCDI_ERROR_TYPE parseRxError(PCDI_Message msg) {
		return PCDI_ERROR_TYPE.fromByte(msg.getData().get(0));
		
	}
	/**
	 * this method is used to parse an incoming message into a value number
	 * @param msg the incoming message
	 * @return the value number of the invalid parameter
	 */
	public static int parseRxParameterInvalid(PCDI_Message msg) {
		return msg.getData().get(0);
	}
	
	/**
	 * parses an incoming heartbeat message int a list of controller-enable-parameters (type byte)
	 * @param msg the incoming message
	 * @return the contained controller-enable-parameters
	 */
	public static List<PCDI_Parameter<Byte>> parseHearbeat(PCDI_Message msg){
		List<PCDI_Parameter<Byte>> res = new ArrayList<PCDI_Parameter<Byte>>();
		List<Byte> data = msg.getData();
		
		// voltage controller is enabled
		byte value = data.get(0);
		PCDI_Parameter<Byte> param = new PCDI_ParameterByte(19,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		// comperator triggered
		value = data.get(1);
		param = new PCDI_ParameterByte(18,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		// forced pwm enabled
		value = data.get(2);
		param = new PCDI_ParameterByte(22,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		// voltage controller state
		value = data.get(3);
		param = new PCDI_ParameterByte(24,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		// mpp controller enabled
		value = data.get(4);
		param = new PCDI_ParameterByte(25,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		// forced output voltage enabled
		value = data.get(5);
		param = new PCDI_ParameterByte(26,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		// MPP controller state
		value = data.get(6);
		param = new PCDI_ParameterByte(28,PCDI_TYPES.BYTE, value);
		res.add(param);
		
		return res;
		
	}
	/**
	 * this method is used to convert data from list to byte array.
	 * it is needed if the hardware interface wants a byte array instead of a list
	 * @param data is the data to be converted
	 * @return byte array
	 */
	public static byte[] toByteArray(List<Byte> data) {
		byte[] tmp = new byte[data.size()];
		int j = 0;
		for(Byte b : data) {
			tmp[j] = b.byteValue();
			j++;
		}
		return tmp;
	}
	private static List<Byte> toByteList(byte[] bytes) {
	    final List<Byte> list = new ArrayList<>();
	    for (byte b : bytes) {
	        list.add(b);
	    }
	    return list;
	}

	public static PCDI_TableInfo parseRxTableInfo(PCDI_Message msg) {
		//value nr type u value
		if(msg.getType()==PCDI_MESSAGE_TYPE.TABLE_INFO_ANSWER) {
			List<Byte> data=msg.getData();
			
			int tableId=data.get(0).intValue();
			int size=data.get(1).intValue();
			int length=msg.getLength();
			PCDI_PERMISSION permission=PCDI_PERMISSION.fromByte(data.get(2));
			PCDI_UNIT unit=PCDI_UNIT.fromByte(data.get(3));
			String name="err";
			try {
				name = new String(toByteArray(data.subList(4, length-5)), "ISO-8859-1");//"ASCII"
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
			PCDI_TableInfo tableInfo = new PCDI_TableInfo(tableId,permission,name,unit,size);
			return tableInfo;
		}
		return null;
	}

	public static PCDI_TableData parseRxTableData(PCDI_Message msg) {
		if(msg.getType()==PCDI_MESSAGE_TYPE.TABLE_READ_ANSWER || msg.getType()==PCDI_MESSAGE_TYPE.TABLE_WRITE_ANSWER) {
			
			List<Byte> data=msg.getData();
			
			int tableId=data.get(0).intValue();
			int indexNr=data.get(1).intValue();
			
			byte[] floatbyte=toByteArray(data.subList(2, 6));
			float value = ByteBuffer.wrap(floatbyte).order(ByteOrder.BIG_ENDIAN).getFloat();
			PCDI_TableData tableData = new PCDI_TableData(tableId, indexNr, value);
			return tableData;

		}
		return null;
	}

	public static PCDI_Message parseTxTableInfo(int tableId, int deviceId) {
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.TABLE_INFO_READ);
		msg.addData((byte) tableId);
		return msg;
	}

	public static PCDI_Message parseTxReadTableIndex(int tableId, int indexNr, int deviceId) {
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.TABLE_READ);
		msg.addData((byte) tableId);
		msg.addData((byte) indexNr);
		return msg;
	}

	public static PCDI_Message parseTxWriteTableIndex(int tableId, float value, int indexNr, int deviceId) {
		PCDI_Message msg = new PCDI_Message(deviceId,PCDI_MESSAGE_TYPE.TABLE_WRITE);
		msg.addData((byte) tableId);
		msg.addData((byte) indexNr);
		byte[] data=ByteBuffer.allocate(4).putFloat(value).array();
		msg.addData(toByteList(data));			
		return msg;
	}
}
