package comm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
/**
* PCD_Interface
* <p>
* 
* this is the class implements the interface of the PCDI and the Hardware Listener.
* It is responsible for handling the requests from the upper layer and for converting bytes from the hardware layer and notifying the upper layer 
*/
public class PCD_Interface implements I_PCDI, I_HWI_Listener{

	private boolean enableAutoCureCommunication = false;
	
	private Collection<I_PCDI_Listener> listener = new ArrayList<I_PCDI_Listener>();
	
	private I_HWI hwi;
	
	private BlockingQueue<PCDI_Message> txMessageBuffer = new ArrayBlockingQueue<PCDI_Message>(PCD_Interface.TX_MESSAGE_BUFFER_SIZE);
	private BlockingQueue<PCDI_Message> rxMessageBuffer = new ArrayBlockingQueue<PCDI_Message>(PCD_Interface.RX_MESSAGE_BUFFER_SIZE);
	
	private List<Byte> rxBuffer = new ArrayList<>();
	
	private int heartbeatTxCnt = 0;
	private int heartbeatRxCnt = 0;
	
	public static final int HEARTBEAT_TIMEOUT_CNT = 600;
	
	public static final int TX_MESSAGE_BUFFER_SIZE = 512;
	public static final int RX_MESSAGE_BUFFER_SIZE = 64;
	
	private boolean connected = false; 
	private Timer rxtxTimer;
	
	private BlockingQueue<PCDI_Message> requestedBuffer = new ArrayBlockingQueue<PCDI_Message>(100);
	
	/**
	* this method is the constructor
	* @param hwi is the hardware interface to establish a conneciton with the device
	*/
	public PCD_Interface(I_HWI hwi) {
		this.hwi = hwi;
		hwi.registerListener(this);
		
		
	}
	
	private void addToTxBuffer(PCDI_Message msg) {
		if(this.txMessageBuffer.size() < PCD_Interface.TX_MESSAGE_BUFFER_SIZE) {
			this.txMessageBuffer.add(msg);
		}
	}
	@Override
	public void clearBuffers() {
		this.txMessageBuffer.clear();
		this.rxMessageBuffer.clear();
		this.rxBuffer.clear();
		this.requestedBuffer.clear();
	}
	
	private void notifyListener(PCDI_Message msg) {
		if(msg.getType()==PCDI_MESSAGE_TYPE.PARAMETER_READ_ANSWER) {
			//notify all listeners:
			PCDI_Parameter<?> parameter=PCDI_Parser.parseRxReadWrite(msg);
			for(I_PCDI_Listener l:listener) {
				l.notifyParameterRead(parameter, msg.getDeviceId().intValue());
			}
			
			//remove from requestedbuffer:
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.PARAMETER_READ, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.PARAMETER_WRITE_ANSWER) {
			PCDI_Parameter<?> parameter=PCDI_Parser.parseRxReadWrite(msg);
			for(I_PCDI_Listener l:listener) {
				l.notifyParameterWrite(parameter, msg.getDeviceId().intValue());
			}
			
			//remove from requestedbuffer:
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.PARAMETER_WRITE, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.PARAMTER_GET_INFO_ANSWER) {
			PCDI_ParameterInfo parameterInfo=PCDI_Parser.parseRxInfo(msg);
			for(I_PCDI_Listener l:listener) {
				l.notifyParameterInfo(parameterInfo, msg.getDeviceId().intValue());
			}
			
			//remove from requestedbuffer:
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.PARAMTER_GET_INFO, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.PARAMETER_INVALID_ANSWER) {
			for(I_PCDI_Listener l:listener) {
				l.notifyParameterInvalid(PCDI_Parser.parseRxParameterInvalid(msg), msg.getDeviceId().intValue());
			}
			//remove from requestedbuffer:
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.PARAMETER_WRITE, msg);
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.PARAMETER_READ, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.COMMAND_ANSWER) {
			for(I_PCDI_Listener l:listener) {
				l.notifyCommandExcecuted(PCDI_Parser.parseRxCommand(msg), msg.getDeviceId().intValue());
			}
			//remove from requestedbuffer:
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.CMD, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.COMMAND_INVALID_ANSWER) {
			for(I_PCDI_Listener l:listener) {
				l.notifyCommandInvalid(PCDI_Parser.parseRxCommand(msg), msg.getDeviceId().intValue());
			}
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.CMD, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.OSCILLOSCOPE_DATA_ANSWER) {
			for(I_PCDI_Listener l:listener) {
				l.notifyOscilloscopeData(PCDI_Parser.parseRxOscilloscopeData(msg), msg.getDeviceId().intValue());
			}
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.OSCILLOSCOPE_STATE_ANSWER) {
			for(I_PCDI_Listener l:listener) {
				l.notifyOscilloscopeStateAnswer(PCDI_Parser.parseRxOscilloscopeState(msg), msg.getDeviceId().intValue());
			}
			this.removeFromRequestedBuffer(PCDI_MESSAGE_TYPE.OSCILLOSCOPE_START, msg);
		}
		else if(msg.getType()==PCDI_MESSAGE_TYPE.ERROR_MESSAGE) {
			for(I_PCDI_Listener l:listener) {
				l.notifyError(PCDI_Parser.parseRxError(msg), msg.getDeviceId().intValue());
			}
			if(this.enableAutoCureCommunication) {
				for(PCDI_Message reqBuf:requestedBuffer) {
					this.txMessageBuffer.add(reqBuf);
				}
			}
				this.requestedBuffer.clear();
		}	
		else if(msg.getType() == PCDI_MESSAGE_TYPE.HEARTBEAT_ANSWER) {
			
			heartbeatRxCnt = 0;
			
			List<PCDI_Parameter<Byte>> res = PCDI_Parser.parseHearbeat(msg);
			
			for(PCDI_Parameter<?> tmp : res) {
				for(I_PCDI_Listener l:listener) {
					l.notifyParameterRead(tmp, msg.getDeviceId().intValue());
				}
			}
		}
	}
	
	private void removeFromRequestedBuffer(PCDI_MESSAGE_TYPE msg_type,PCDI_Message msg) {
		int deviceId=msg.getDeviceId();
		int valueNr=msg.getData().get(0).intValue();
		for(PCDI_Message reqBuf:requestedBuffer) {
			if(reqBuf.getType()==msg_type && reqBuf.getData().get(0).intValue()==valueNr && reqBuf.getDeviceId()==deviceId) {
				this.requestedBuffer.remove(reqBuf);
			}
		}
	}
	
	@Override
	public void readParameter(int valueNr, int deviceId) {
		this.addToTxBuffer(PCDI_Parser.parseTxParameterRead(valueNr,deviceId));
	}

	@Override
	public void sendCommand(PCDI_COMMANDS command, int deviceId) {
		this.addToTxBuffer(PCDI_Parser.parseTxCommand(command, deviceId));
	}

	@Override
	public void getParameterInfo(int valueNr, int deviceId) {
		this.addToTxBuffer(PCDI_Parser.parseTxParameterInfo(valueNr,deviceId));
	}

	@Override
	public void writeParameter(PCDI_Parameter<?> param, int deviceId) {
		this.addToTxBuffer(PCDI_Parser.parseTxParameterWrite(param,deviceId));
	}
	
	@Override
	public void registerListener(I_PCDI_Listener listener) {
		if(this.listener.contains(listener)) return;
		this.listener.add(listener);
	}

	@Override
	public void unregisterListener(I_PCDI_Listener listener) {
		if(!this.listener.contains(listener)) return;
		this.listener.remove(listener);
	}

	@Override
	public void notifyByteReceived(byte data) {
		this.rxBuffer.add(data);
		while(this.rxBuffer.get(0)!=PCDI_Message.HEADER) {
			this.rxBuffer.remove(0);
			System.out.println("Byte received but not header\n");
		}
		if(this.rxBuffer.size()>=2) {
			int messageLength=this.rxBuffer.get(1).intValue();
			if(messageLength<=this.rxBuffer.size()) {
				PCDI_Message msg=new PCDI_Message(Byte.toUnsignedInt(this.rxBuffer.get(2)),PCDI_MESSAGE_TYPE.fromByte(this.rxBuffer.get(3)));
				for(int i=0; i<messageLength-PCDI_Message.BASE_LEN;i++) {
					msg.addData(this.rxBuffer.get(i+4));
				}		
				byte checksum=this.rxBuffer.get(messageLength-1);
				for(int i=0; i<messageLength;i++)
					this.rxBuffer.remove(0);
				if(checksum!=msg.calculateChecksum()) {
					System.out.println("ERROR CHECKSUM NOT OK. Checksum got: "+checksum+" Checksum should be: "+msg.calculateChecksum());
					return;
				}
				this.rxMessageBuffer.add(msg);
				
			}
		}
	}

	@Override
	public void notifyConnectionChanged(boolean connected) {
		this.connected=connected;
		if(connected==true && this.rxtxTimer==null) {
			TimerTask task = new TimerTask() {
		        public void run() {
		        	
		        	if(heartbeatTxCnt>50) {
		        		PCDI_Message sendMessage = new PCDI_Message(0,PCDI_MESSAGE_TYPE.HEARTBEAT);
		        		hwi.sendBytes(sendMessage.generateBytewiseMessage());
		        		heartbeatTxCnt = 0;
		        	}
		        	else if(!txMessageBuffer.isEmpty()) {
		            	try {
		            		PCDI_Message sendMessage = txMessageBuffer.take();
		            		
		            		if(requestedBuffer.size()<100) {
		            			requestedBuffer.add(sendMessage);
		            		}
		            		else {
		            			System.out.println("Too many msges in safetyBuffer!!");
		            			requestedBuffer.clear();
		            		}
							hwi.sendBytes(sendMessage.generateBytewiseMessage());
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
		            }
		        	
		            if(!rxMessageBuffer.isEmpty()) {
		            	try {
		            		
							notifyListener(rxMessageBuffer.take());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            }
		            
		            if(heartbeatRxCnt>PCD_Interface.HEARTBEAT_TIMEOUT_CNT) {
		            	disconnect();
		            }
		            
		            heartbeatTxCnt = heartbeatTxCnt+1;
		        	heartbeatRxCnt = heartbeatRxCnt+1;
		        }
		    };
		    this.rxtxTimer=new Timer();
		    this.rxtxTimer.scheduleAtFixedRate(task, 0, 10);
		}
		if(connected==false && this.rxtxTimer!=null) {
			this.rxtxTimer.cancel();
			this.rxtxTimer=null;
		}
		
		for(I_PCDI_Listener listener : this.listener) {
			listener.notifyConnectionChanges(connected);
		}
	}

	@Override
	public void startOscilloscope(PCDI_OSCILLOSCOPE_COMMAND osci_cmd, int valNr1, int valNr2, int valNr3, int valNr4,
			int valNr5, int valNr6, int valNr7, int oversampling, int deviceId) {
		this.addToTxBuffer(PCDI_Parser.parseTxStartOscilloscope(osci_cmd, valNr1, valNr2, valNr3, valNr4, valNr5, valNr6, valNr7, oversampling, deviceId));
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	@Override
	public void connect() {
		this.clearBuffers();
		this.heartbeatRxCnt = 0;
		this.heartbeatTxCnt = 0;
		this.hwi.initInterface();
	}

	@Override
	public void disconnect() {
		this.hwi.deInitInterface();
	}




}
