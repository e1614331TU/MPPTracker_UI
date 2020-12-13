package base;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import comm.I_PCDI;
import comm.I_PCDI_Listener;
import comm.PCDI_COMMANDS;
import comm.PCDI_ERROR_TYPE;
import comm.PCDI_OSCILLOSCOPE_STATE;
import comm.PCDI_OscilloscopeData;
import comm.PCDI_PERMISSION;
import comm.PCDI_Parameter;
import comm.PCDI_ParameterInfo;
import comm.PCD_Interface;
/**
* this MainWindow consists of the Config-Panel, the different tabs and the Console-Window.
* It also handles the tab-listeners.
*/
public class MainWindow extends JFrame implements ActionListener, I_PCDI_Listener, I_Tab, ChangeListener {

	private static final long serialVersionUID = 1L;
	
	// UI - Elements
	private GridBagLayout layout;
	private JTabbedPane tabbedPane;
	private JPanelOverview panelOverview;
	private JPanelPowerController panelPowerController;
	private JPanelScope panelScope;
	private JPanelParams panelParams;
	
	private JTextArea infoConsole;
	
	private int deviceId = 0;
	private List<PCDI_Parameter<?>> params=new ArrayList<PCDI_Parameter<?>>();
	private List<PCDI_ParameterInfo> paramInfos=new ArrayList<PCDI_ParameterInfo>();
	
	private Collection<I_Tab_Listener> tabListeners=new ArrayList<I_Tab_Listener>();
	
	private ComPortHandler comPortHandler = ComPortHandler.getInstance();
	private I_PCDI pcdi; 

	public static final int width = 1200;
	public static final int height = 800;
	public static final int contentHeight = 550;
	private JPanel panel;
	
	/**
	* the Constructor generates a window which contains all the sub-panels
	*/
	public MainWindow() {
		
		// Interface Init
		this.pcdi = new PCD_Interface(this.comPortHandler);
		this.pcdi.registerListener(this);
		// GUI Init
		this.setTitle("MPPTracker - UI");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(MainWindow.width,MainWindow.height));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
		gridBagLayout.columnWeights = new double[]{1.0};
		getContentPane().setLayout(gridBagLayout);
		
		
		//List of COM-Ports and comport connection part
		DefaultListModel<String> model = new DefaultListModel<String>();
		model.addElement( "not connected" );
		
		
		//Make GridBagLayout
		layout = new GridBagLayout();
		layout.columnWidths = new int[] {0};
		layout.rowWeights = new double[]{0.0, 0.0, 0.0};
		layout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0};
		
		//Make Tabs
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, MainWindow.width, MainWindow.contentHeight);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.anchor = GridBagConstraints.CENTER;
		gbc_tabbedPane.weightx=1.0;
		gbc_tabbedPane.weighty=1.0;
		gbc_tabbedPane.fill=GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		this.getContentPane().add(tabbedPane, gbc_tabbedPane);
		
		
		//Console
		JScrollPane scrollConsole = new JScrollPane();
		scrollConsole.setPreferredSize(new Dimension(MainWindow.width, 80));
		scrollConsole.setMinimumSize(new Dimension(MainWindow.width,80));
		scrollConsole.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		GridBagConstraints gbc_scrollConsole = new GridBagConstraints();
		gbc_scrollConsole.insets = new Insets(0, 0, 5, 0);  //top padding
		gbc_scrollConsole.anchor = GridBagConstraints.SOUTHWEST;
		gbc_scrollConsole.fill = GridBagConstraints.HORIZONTAL;
		gbc_scrollConsole.gridx = 0;
		gbc_scrollConsole.gridy = 2;
		gbc_tabbedPane.weighty=0.3;
		
		this.getContentPane().add(scrollConsole,gbc_scrollConsole);
		
		infoConsole = new JTextArea();
		infoConsole.setEditable(false);
		DefaultCaret caretArd = (DefaultCaret)infoConsole.getCaret();
		caretArd.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollConsole.setViewportView(infoConsole);
		infoConsole.setLineWrap(true);
		
		
		//Config Panel
		panel = new JPanelConfig(this.pcdi,this.deviceId,this.infoConsole);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 10, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		
		//Add Panels

		this.panelOverview = new JPanelOverview(this.pcdi,this.deviceId,this.infoConsole);
		tabbedPane.addTab("Overview", null, this.panelOverview, null);
		
		panelPowerController = new JPanelPowerController(this.pcdi,this.deviceId,this.infoConsole);
		tabbedPane.addTab("Controller", null, panelPowerController, null);
		
		panelScope = new JPanelScope(this.pcdi,this.deviceId,this.infoConsole);
		tabbedPane.addTab("Scope", null, panelScope, null);
		
		panelParams = new JPanelParams(this.pcdi,this.deviceId,this.infoConsole);
		tabbedPane.addTab("Parameters", null, panelParams, null);
		
		this.registerListener(panelPowerController);
		this.registerListener(panelScope);
		this.registerListener(panelParams);
		
		//TODO uncomment
//		tabbedPane.setEnabledAt(1, false);
//		tabbedPane.setEnabledAt(2, false);
//		tabbedPane.setEnabledAt(3, false);
//		tabbedPane.setEnabledAt(4, false);
//		tabbedPane.setEnabledAt(5, false);
//		tabbedPane.setEnabledAt(6, false);
//		tabbedPane.setEnabledAt(7, false);
//		tabbedPane.setEnabledAt(8, false);
		
		// https://kodejava.org/how-do-i-detect-tab-selection-changes-in-jtabbedpane/ 
		
		((JPanelOverview) this.panelOverview).setActiveState(true);
		
		this.tabbedPane.addChangeListener(this);
		
		//SET VISIBLE AND PACK
		this.pack();
		this.setVisible(true);
	}
	

	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		if(deviceId!=this.deviceId) {
			return;
		}
		if(parameter==null) {
			this.infoConsole.append("could not read value!\n");
			return;
		}
		if(parameter.getValueNumber()==0) {
			short numberParameters=(short) parameter.getValue();
			String tmp = "[INFO] will read " + Short.toString(numberParameters) + " parameters\n";
			this.infoConsole.append(tmp);
			for(int i=1;i<numberParameters;i++) {
				pcdi.getParameterInfo(i, deviceId);
				pcdi.readParameter(i, deviceId);//hopefully not only writeable.. think about it once again
			}
		}
		else {
			for(PCDI_Parameter param:params) {
				if(param.getValueNumber()==parameter.getValueNumber()) {
					param.setValue(parameter.getValue());
					this.tabListeners.forEach(lis -> {lis.parameterUpdated(this.params);});
					return;
				}
			}
			//if parameter didnt exist already in local variable params then add it
			params.add(parameter);
			this.tabListeners.forEach(lis -> {lis.parameterUpdated(this.params);});
		}
		
	}

	@Override
	public void notifyParameterInfo(PCDI_ParameterInfo parameter, int deviceId) {
		if(deviceId!=this.deviceId) {
			return;
		}
		this.infoConsole.append(parameter.toString()+"\n");
		for(PCDI_ParameterInfo paramI:paramInfos) {
			if(paramI.getValNr()==parameter.getValNr()) {
				this.paramInfos.remove(this.paramInfos.indexOf(paramI));
				this.paramInfos.add(parameter);
				updateInfoGUI(paramI);
				this.tabListeners.forEach(lis -> {lis.parameterInfoUpdated(this.paramInfos);});
				return;
			}
		}
		paramInfos.add(parameter);
		this.tabListeners.forEach(lis -> {lis.parameterInfoUpdated(this.paramInfos);});
	}

	private void updateInfoGUI(PCDI_ParameterInfo paramI) {
		
	}





	@Override
	public void notifyParameterInvalid(int valueNr, int deviceId) {
		if(this.deviceId==deviceId)
			this.infoConsole.append("[ERROR] Parameter with valueNr "+valueNr+" not executed\n");
		
	}
	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {
		this.infoConsole.append("[INFO] Command "+command.name()+" executed\n");
	}
	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		infoConsole.append("[INFO] Parameter "+parameter.getValueNumber()+" written. Value: "+parameter.getValue().toString()+"\n");
	}

	@Override
	public void notifyCommandInvalid(PCDI_COMMANDS command, int deviceId) {
		if(this.deviceId==deviceId)
			this.infoConsole.append("[ERROR] Command "+command.name()+" not executed\n");
	}

	@Override
	public void registerListener(I_Tab_Listener listener) {
		this.tabListeners.add(listener);
		
	}

	@Override
	public void unregisterListener(I_Tab_Listener listener) {
		this.tabListeners.remove(listener);
		
	}

	@Override
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId) {
	}

	@Override
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId) {
		this.infoConsole.append("[ERROR] "+error.name()+"\n");
		
	}

	@Override
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyConnectionChanges(boolean isConnected) {
		if(isConnected) {
			this.tabListeners.forEach(lis -> {lis.connectionChanged(true);});
			this.pcdi.readParameter(0, deviceId);
			this.infoConsole.append("Read parameter 0 - value Numbers\n");
			tabbedPane.setEnabledAt(1, true);
			tabbedPane.setEnabledAt(2, true);
		}
		else {
			this.paramInfos.clear();
			this.params.clear();
			this.tabListeners.forEach(lis -> {lis.connectionChanged(false);});
			tabbedPane.setEnabledAt(1, false);
			tabbedPane.setEnabledAt(2, false);
		}
	}



	@Override
	public void actionPerformed(ActionEvent e) {
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == this.tabbedPane) {
			int selectedIndex = this.tabbedPane.getSelectedIndex();
			if(selectedIndex == 0) {
				((JPanelOverview) this.panelOverview).setActiveState(true);
				this.panelScope.setActiveState(false);
			}
			else if(selectedIndex == 1) {
				((JPanelOverview) this.panelOverview).setActiveState(false);
				this.panelScope.setActiveState(false);
			}
			else if(selectedIndex == 6) {
				this.panelScope.setActiveState(true);
				((JPanelOverview) this.panelOverview).setActiveState(false);
			}
			else if(selectedIndex == 8) {
				((JPanelOverview) this.panelOverview).setActiveState(false);
				this.panelScope.setActiveState(false);
			}
			else {
				((JPanelOverview) this.panelOverview).setActiveState(false);
				this.panelScope.setActiveState(false);
			}
		}
	}



}
