package comm;

import java.util.List;
/**
* I_HWI
* <p>
* this is the class is the interface of the Hardware handler.
* It is able to notify the listener about new data and 
* can send the requested data.
*/
public interface I_HWI {
	/**
	 * This method is for registering a listener
	 * @param listener to register
	 * 
	*/
	public void registerListener(I_HWI_Listener listener);
	/**
	 * This method is for unregistering a listener
	 * @param listener to unregister
	 * 
	*/
	public void unregisterListener(I_HWI_Listener listener);
	
	/**
	 * This method is for telling the Hardware interface which data to send
	 * @param data data to send
	 * 
	*/
	public void sendBytes(List<Byte> data);
	/**
	 * This method is for telling the Hardware interface to start connection
	*/
	public void initInterface();
	/**
	 * This method is for telling the Hardware interface to stop connection
	*/
	public void deInitInterface();
}
