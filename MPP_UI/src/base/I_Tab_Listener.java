package base;

import java.util.List;

import comm.PCDI_Parameter;
import comm.PCDI_ParameterInfo;
/**
* I_Tab_Listener
* <p>
* this is the interface for the tabs, so it gets notified if the user changes the tab
* 
*/
public interface I_Tab_Listener {
	/**
	* this method is needed by the listeners to stop/start communication
	* @param connected tells the listener about the new connection state
	*/
	public void connectionChanged(boolean connected);
	/**
	* this method is needed by the listeners to get new information about parameters
	* @param params tells the listener about the new parameters
	*/
	public void parameterUpdated(List<PCDI_Parameter<?>> params);
	/**
	* this method is needed by the listeners to get new information about parameter information
	* @param paramInfos tells the listener about the new parameter Infos
	*/
	public void parameterInfoUpdated(List<PCDI_ParameterInfo> paramInfos);
	
}
