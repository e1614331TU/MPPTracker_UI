package comm;
/**
* I_PCDI_Listener
* <p>
* this class is the interface of the PCDI Listener.
* It is usually implemented by the upper layer (for example a GUI) to communicate with the device.
* The upper layer gets notified if data is received or the connection changes
* 
*/
public interface I_PCDI_Listener {
	/**
	 * informs listener about connectionchanges
	 * @param isConnected new connection state
	 */
	public void notifyConnectionChanges(boolean isConnected);
	/**
	 * notifies listener if a parameter has been written
	 * @param parameter the written parameter (inclusive values)
	 * @param deviceId	the deviceId of the communication-device
	 */
	public void notifyParameterWrite(PCDI_Parameter<?> parameter,int deviceId);
	/**
	 * notifies listener if a command has been executed
	 * @param command is the command which has been executed
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyCommandExcecuted(PCDI_COMMANDS command,int deviceId);
	/**
	 * notifies the listener about a successfully read parameter
	 * @param parameter the parameter with its value
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId);
	/**
	 * notifies the listener about the requested parameter information
	 * @param parameter the requested parameter information
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyParameterInfo(PCDI_ParameterInfo parameter, int deviceId);
	/**
	 * notifies if the device could not handle a command
	 * @param command that could not be handled
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyCommandInvalid(PCDI_COMMANDS command, int deviceId);
	/**
	 * notifies the listener if the requested parameter value number was invalid
	 * @param valueNr the invalid parameter number
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyParameterInvalid(int valueNr, int deviceId);
	/**
	 * notifies the listener about incoming oscilloscope data
	 * @param data the data arrived 
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId);
	/**
	 * notifies the listener if an error occured
	 * @param error which occured
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId);
	/**
	 * notifies the listener about the oscilloscope state
	 * @param osci_state the actual oscilloscope-state of the device
	 * @param deviceId the deviceId of the communication-device
	 */
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId);
	
	public void notifyTableInfo(PCDI_TableInfo table, int deviceId);
	public void notifyTableRead(PCDI_TableData tableData, int deviceId);
	public void notifyTableWrite(PCDI_TableData tableData, int deviceId);
}
