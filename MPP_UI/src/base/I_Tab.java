package base;
/**
* I_Tab
* <p>
* this is the interface for the tab notifier
* 
*/
public interface I_Tab {
	/**
	* this method is needed by the interface to register listeners
	* @param listener the listener to register
	*/
	public void registerListener(I_Tab_Listener listener);
	
	/**
	* this method is needed by the interface to unregister listeners
	* @param listener the listener to unregister
	*/
	public void unregisterListener(I_Tab_Listener listener);
	
}
