package comm;
/**
* I_HWI_Listener
* <p>
* this is the class is the interface of the Hardware Listener.
* It has to handle received bytes and connection-changes
* 
*/
public interface I_HWI_Listener {
	/**
	 * This method is for notifying the listener about new data
	 * @param data is the received data-byte
	 * 
	*/
	public void notifyByteReceived(byte data);
	/**
	 * This method is for notifying the listener about a connection change
	 * @param connected is the new connection state (true = connected)
	 * 
	*/
	public void notifyConnectionChanged(boolean connected);
}
