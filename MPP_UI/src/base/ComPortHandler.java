package base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import comm.I_HWI;
import comm.I_HWI_Listener;
import comm.PCDI_Parser;

/**
* ComPortHandler
* <p>
* this class is used as hardware interface for the PCDI, so it sends and receives bytes and notifies the PCDI-Interface
* 
*/
public class ComPortHandler implements I_HWI, SerialPortDataListener{
	
	private static ComPortHandler instance = null;
	private SerialPort port = null;
	
	//Listener pattern
	private Collection<I_HWI_Listener> listener = new ArrayList<I_HWI_Listener>();
	
	private static final int BAUDRATE = 9600;
	private static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
	private static final int PARITY_BIT = SerialPort.NO_PARITY;
	private static final int DATA_BITS = 8;
	
	private static final int WRITE_TIMEOUT = 1000;
	
	private ComPortHandler(){}
	
	/**
	* Constructor for class ComPortHandler
	* @return the instance of the comporthandler
	*/
	public static ComPortHandler getInstance() {
		if(ComPortHandler.instance == null) {
			ComPortHandler.instance = new ComPortHandler();
		}
		return ComPortHandler.instance;
	}

	/**
	 * @return a string-list of available ports
	 */
	public List<String> getAvailableComPorts(){
		List<String> res = new ArrayList<String>();
		SerialPort[] serialPorts = SerialPort.getCommPorts();
		
		for(SerialPort tmp : serialPorts) {
			res.add(tmp.getSystemPortName());
		}
		
		return res;
	}
	
	/**
	 * this method is used to get the actual state of connection
	 * @return true if the port is open, false if not
	 */
	public boolean portIsOpen() {
		if(this.port == null) return false;
		return this.port.isOpen();
	}
	
	/**
	 * this method configures the connection and sets the comport
	 * @param portDescriptor is the string representing the comport
	 */
	public void setComPortByDescriptor(String portDescriptor) {
		this.port = SerialPort.getCommPort(portDescriptor);
		
		SerialPort[] serialPorts = SerialPort.getCommPorts();
		for(SerialPort tmp : serialPorts) {
			if(tmp.getPortDescription().equals(portDescriptor)) {
				this.port = tmp;
				break;
			}
		}
		if(this.port == null) return;
		this.port.setBaudRate(ComPortHandler.BAUDRATE);
		this.port.setParity(ComPortHandler.PARITY_BIT);
		this.port.setNumStopBits(ComPortHandler.STOP_BITS);
		this.port.setNumDataBits(ComPortHandler.DATA_BITS);
		this.port.addDataListener(this);
	}
	
	/**
	 * this method is used to open the comPort
	 * @return true if opening was successful
	 */
	public boolean openComPort() {
		if(this.port == null || this.port.isOpen()) return false;
		if(this.port.openPort()) {
			while(this.port.bytesAvailable()>0) {
				byte[] unused=new byte[1];
				this.port.readBytes(unused, 1);
			}
			
			listener.forEach(listen->listen.notifyConnectionChanged(true));
			return true;
		}
		return false;
	}
	
	/**
	 * this method is used to close the comport
	 * @return true if the closing was successful
	 */
	public boolean closeComPort() {
		if(this.port == null || !this.port.isOpen()) return false;
		if(this.port.closePort()) {
			this.port = null;
			listener.forEach(listen->listen.notifyConnectionChanged(false));
			return true;
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		if(this.port == null) return "null";
		return this.port.getSystemPortName() + "@" + this.port.getBaudRate() + "Baud/s";
	}

	@Override
	public void registerListener(I_HWI_Listener listener) {
		if(this.listener.contains(listener)) return;
		this.listener.add(listener);
	}

	@Override
	public void unregisterListener(I_HWI_Listener listener) {
		if(!this.listener.contains(listener)) return;
		this.listener.remove(listener);
	}

	@Override
	public void sendBytes(List<Byte> data) {
		if(this.port == null || !this.port.isOpen()) return;
		byte[] tmp = PCDI_Parser.toByteArray(data);
		this.port.writeBytes(tmp,tmp.length);	
		try {
			this.port.getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
		//return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

	@Override
	public void serialEvent(SerialPortEvent ev) {
	      byte[] newData = ev.getReceivedData();
	      for (int i = 0; i < newData.length; ++i) {
	    	  byte data=newData[i];
	    	  listener.forEach(list->list.notifyByteReceived(data));
	      }	
	}
	/**
	 * this method is used to flush the outputstream
	 */
	public void flush() {
		try {
			this.port.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void initInterface() {
		this.openComPort();
	}

	@Override
	public void deInitInterface() {
		this.closeComPort();
	}


}
