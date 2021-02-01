package comm;
/**
* I_PCDI
* <p>
* this is the interface for the pcdi class. It makes it possible to handle requests from the upper layer (user/gui usually)
* 
*/
public interface I_PCDI {
	/**
	 * this method makes it possible to request parameter info of a parameter by value number
	 * @param valueNr is the value number of the requested parameter
	 * @param deviceId the deviceId of the communication-device
	 */
	public void getParameterInfo(int valueNr, int deviceId);
	/**
	 * this method makes it possible to request parameter value of a parameter by value number
	 * @param valueNr is the value number of the requested parameter
	 * @param deviceId the deviceId of the communication-device
	 */
	public void readParameter(int valueNr, int deviceId);
	/**
	 * this method makes it possible to write a parameter value in the device
	 * @param param the parameter with the desired value
	 * @param deviceId the deviceId of the communication-device
	 */
	public void writeParameter(PCDI_Parameter<?> param, int deviceId);
	/**
	 * this method makes it possible to send commands. 
	 * @param command the desired command to execute by the device
	 * @param deviceId the deviceId of the communication-device
	 */
	public void sendCommand(PCDI_COMMANDS command, int deviceId);
	/**
	 * this method initiates a start of the oscilloscope-recording
	 * @param osci_cmd wether to only start the osci or additionally enable a controller
	 * @param valNr1 the value number of the desired recording parameter1
	 * @param valNr2 the value number of the desired recording parameter2
	 * @param valNr3 the value number of the desired recording parameter3
	 * @param valNr4 the value number of the desired recording parameter4
	 * @param valNr5 the value number of the desired recording parameter5
	 * @param valNr6 the value number of the desired recording parameter6
	 * @param valNr7 the value number of the desired recording parameter7
	 * @param oversampling factor to not use every timestep to sample
	 * @param deviceId the deviceId of the communication-device
	 */
	public void startOscilloscope(PCDI_OSCILLOSCOPE_COMMAND osci_cmd, int valNr1,int valNr2, int valNr3, int valNr4, int valNr5, int valNr6, int valNr7, int oversampling, int deviceId);
	/**
	 * this method is used to register the listener
	 * @param listener ist the listener to register
	 */
	public void registerListener(I_PCDI_Listener listener);
	/**
	 * this method clears the send and receivebuffers
	 */
	public void clearBuffers();
	/**
	 * this method is used to unregister the listener
	 * @param listener ist the listener to unregister
	 */
	public void unregisterListener(I_PCDI_Listener listener);
	/**
	 *  this method is used to open the connection to the device
	 */
	public void connect();
	/**
	 * this method is used to close the connection to the device
	 */
	public void disconnect();
	/**
	 * get information wether the communication is active
	 * @return state of connection
	 */
	public boolean isConnected();
	
	public void getTableInfo(int tableId, int deviceId);
	public void readTableIndex(int tableId, int indexNr, int deviceId);
	public void writeTableIndex(int tableId, float value, int indexNr, int deviceId);
}
