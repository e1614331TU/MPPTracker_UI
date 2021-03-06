package base;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import base.graphics.Circle;
import base.graphics.GraphicJPanel;
import comm.I_PCDI;
import comm.I_PCDI_Listener;
import comm.PCDI_COMMANDS;
import comm.PCDI_ERROR_TYPE;
import comm.PCDI_OSCILLOSCOPE_STATE;
import comm.PCDI_OscilloscopeData;
import comm.PCDI_Parameter;
import comm.PCDI_ParameterInfo;
import comm.PCDI_ParameterInt;
import comm.PCDI_TYPES;
import comm.PCDI_TableData;
import comm.PCDI_TableInfo;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.border.EtchedBorder;
/**
* JPanelConfig
* <p>
* this is the class which handles the Config Panel. It is responsible for enabling the controllers and give the user 
* information about the connection
* 
*/
public class JPanelConfig extends JPanel implements ActionListener, I_PCDI_Listener {
	

	private static final long serialVersionUID = 1L;
	private JTextArea infoConsole;
	private JList<String> listComPorts;
	private JButton buttonRefresh;
	private JButton buttonOpenClose;
	private JButton buttonResetComp;
	private JButton buttonEnableVC;
	private JButton buttonEnableMPP;
	
	private JLabel labelConnectionState;
	
	private int deviceId;
	private I_PCDI pcdi;
	private ComPortHandler comPortHandler = ComPortHandler.getInstance();
	private SoftwareVersionDAO swVersion = new SoftwareVersionDAO();
	
	// circles
	private GraphicJPanel circleConnected;
	private GraphicJPanel circleCOMP;
	private GraphicJPanel circleMPP;
	private GraphicJPanel circleVC;
	
	private boolean compIsEnabled = false;
	private boolean vcIsEnabled = false;
	private boolean mppIsEnabled = false;
		
	private JPanel panel_1;
	private JLabel labelSWVersion;
	
	
	private Timer blinkTimer  = null;
	private boolean toggleCon = false;
	private JPanel panel_2;
	private JButton buttonEmergencyStop;
	/**
	* this method is the constructor
	* @param pcdi is needed for communication
	* @param deviceId is needed for communication with the controller for ability to communicate with the right controller 
	* @param infoConsole is needed for giving user information
	*/
	public JPanelConfig(I_PCDI pcdi, int deviceId, JTextArea infoConsole) {
		super();
		setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		this.pcdi = pcdi;
		this.deviceId = deviceId;
		this.infoConsole = infoConsole;
		
		this.pcdi.registerListener(this);
		
		//List of COM-Ports and comport connection part
		DefaultListModel<String> model = new DefaultListModel<String>();
		model.addElement( "no port found" );
       
		this.setMinimumSize(new Dimension(MainWindow.width, 70));
		this.setPreferredSize(new Dimension(MainWindow.width, 70));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(MainWindow.width, 30));
		panel.setMinimumSize(new Dimension(MainWindow.width, 30));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(5, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("COM-Ports:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(5, 5, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		this.listComPorts = new JList<String>( model );
		this.listComPorts.setPreferredSize(new Dimension(150, 20));
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.insets = new Insets(5, 5, 0, 5);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 1;
		gbc_list.gridy = 0;
		gbc_list.fill = GridBagConstraints.BOTH;
		panel.add(this.listComPorts, gbc_list);
		
		this.buttonRefresh = new JButton("refresh"); 
		this.buttonRefresh.addActionListener(this);
		this.buttonRefresh.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(5, 5, 0, 5);
		gbc_btnNewButton_2.gridx = 2;
		gbc_btnNewButton_2.gridy = 0;
		gbc_btnNewButton_2.fill = GridBagConstraints.BOTH;
		panel.add(this.buttonRefresh, gbc_btnNewButton_2);
		
		this.buttonOpenClose = new JButton("open");
		this.buttonOpenClose.addActionListener(this);
		this.buttonOpenClose.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(5, 5, 0, 5);
		gbc_btnNewButton_1.gridx = 3;
		gbc_btnNewButton_1.gridy = 0;
		gbc_btnNewButton_1.fill = GridBagConstraints.VERTICAL;
		panel.add(this.buttonOpenClose, gbc_btnNewButton_1);
		
		this.labelConnectionState = new JLabel("not connected");
		labelConnectionState.setHorizontalAlignment(SwingConstants.CENTER);
		this.labelConnectionState.setPreferredSize(new Dimension(150, 20));
		GridBagConstraints gbc_labelConnectionState = new GridBagConstraints();
		gbc_labelConnectionState.insets = new Insets(5, 5, 0, 5);
		gbc_labelConnectionState.gridx = 4;
		gbc_labelConnectionState.gridy = 0;
		panel.add(this.labelConnectionState, gbc_labelConnectionState);

		this.circleConnected = new GraphicJPanel(new Circle(2,2,15));
		GridBagConstraints gbc_panel1 = new GridBagConstraints();
		gbc_panel1.insets = new Insets(5, 5, 0, 5);
		gbc_panel1.gridx = 5;
		gbc_panel1.gridy = 0;
		gbc_panel1.fill = GridBagConstraints.BOTH;
		panel.add(this.circleConnected, gbc_panel1);
		
		this.labelSWVersion = new JLabel("");
		labelSWVersion.setHorizontalAlignment(SwingConstants.CENTER);
		this.labelSWVersion.setPreferredSize(new Dimension(150, 20));
		GridBagConstraints gbc_labelSWVersion = new GridBagConstraints();
		gbc_labelSWVersion.fill = GridBagConstraints.BOTH;
		gbc_labelSWVersion.insets = new Insets(5, 10, 0, 0);
		gbc_labelSWVersion.gridx = 6;
		gbc_labelSWVersion.gridy = 0;
		panel.add(this.labelSWVersion, gbc_labelSWVersion);
		
		panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(MainWindow.width, 30));
		panel_1.setMinimumSize(new Dimension(MainWindow.width, 30));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{80, 0, 80, 80, 0, 80, 80, 0, 80};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblNewLabel_1 = new JLabel("Reset Comperator");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 10, 0, 5);
		gbc_lblNewLabel_1.gridx = 6;
		gbc_lblNewLabel_1.gridy = 0;
		panel_1.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		this.circleCOMP = new GraphicJPanel(new Circle(2,4,15));
		GridBagConstraints gbc_circleCC = new GridBagConstraints();
		gbc_circleCC.insets = new Insets(0, 5, 0, 10);
		gbc_circleCC.gridx = 7;
		gbc_circleCC.gridy = 0;
		gbc_circleCC.fill = GridBagConstraints.BOTH;
		panel_1.add(this.circleCOMP, gbc_circleCC);
		
		this.buttonResetComp = new JButton("reset");
		this.buttonResetComp.addActionListener(this);
		this.buttonResetComp.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_buttonResetComp = new GridBagConstraints();
		gbc_buttonResetComp.fill = GridBagConstraints.BOTH;
		gbc_buttonResetComp.gridx = 8;
		gbc_buttonResetComp.gridy = 0;
		panel_1.add(this.buttonResetComp, gbc_buttonResetComp);
		
		JLabel lblNewLabel_2 = new JLabel("VoltageCotroller");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setPreferredSize(new Dimension(60, 20));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.insets = new Insets(0, 10, 0, 5);
		gbc_lblNewLabel_2.gridx = 3;
		gbc_lblNewLabel_2.gridy = 0;
		panel_1.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		this.circleVC = new GraphicJPanel(new Circle(2,4,15));
		GridBagConstraints gbc_circleSC = new GridBagConstraints();
		gbc_circleSC.insets = new Insets(0, 5, 0, 10);
		gbc_circleSC.gridx = 4;
		gbc_circleSC.gridy = 0;
		gbc_circleSC.fill = GridBagConstraints.BOTH;
		panel_1.add(this.circleVC, gbc_circleSC);
		
		this.buttonEnableVC = new JButton("enable");
		this.buttonEnableVC.addActionListener(this);
		this.buttonEnableVC.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_buttonEnableVC = new GridBagConstraints();
		gbc_buttonEnableVC.insets = new Insets(0, 0, 0, 5);
		gbc_buttonEnableVC.fill = GridBagConstraints.BOTH;
		gbc_buttonEnableVC.gridx = 5;
		gbc_buttonEnableVC.gridy = 0;
		panel_1.add(this.buttonEnableVC, gbc_buttonEnableVC);
		
		JLabel lblNewLabel_3 = new JLabel("MPP Tracker");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_3.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_3.insets = new Insets(0, 10, 0, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 0;
		panel_1.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		this.circleMPP = new GraphicJPanel(new Circle(2,4,15));
		GridBagConstraints gbc_circlePC = new GridBagConstraints();
		gbc_circlePC.insets = new Insets(0, 5, 0, 10);
		gbc_circlePC.gridx = 1;
		gbc_circlePC.gridy = 0;
		gbc_circlePC.fill = GridBagConstraints.BOTH;
		panel_1.add(this.circleMPP, gbc_circlePC);
		
		this.buttonEnableMPP = new JButton("enable");
		this.buttonEnableMPP.addActionListener(this);
		this.buttonEnableMPP.setPreferredSize(new Dimension(100, 20));
		GridBagConstraints gbc_buttonEnableMPP = new GridBagConstraints();
		gbc_buttonEnableMPP.insets = new Insets(0, 0, 0, 5);
		gbc_buttonEnableMPP.fill = GridBagConstraints.BOTH;
		gbc_buttonEnableMPP.gridx = 2;
		gbc_buttonEnableMPP.gridy = 0;
		panel_1.add(this.buttonEnableMPP, gbc_buttonEnableMPP);
		
		panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(5, 5, 5, 5);
		gbc_panel_2.gridheight = 2;
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 0;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{107, 0};
		gbl_panel_2.rowHeights = new int[]{21, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		buttonEmergencyStop = new JButton("Emergency Stop");
		buttonEmergencyStop.addActionListener(this);
		buttonEmergencyStop.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnEmergencyStop = new GridBagConstraints();
		gbc_btnEmergencyStop.insets = new Insets(10, 0, 10, 0);
		gbc_btnEmergencyStop.fill = GridBagConstraints.BOTH;
		gbc_btnEmergencyStop.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnEmergencyStop.gridx = 0;
		gbc_btnEmergencyStop.gridy = 0;
		panel_2.add(buttonEmergencyStop, gbc_btnEmergencyStop);

		
		this.disableGUI();
		//at start update comportslist:
		this.updateComPortsList(this.comPortHandler.getAvailableComPorts());

	}
	    
    
	@Override
	public void actionPerformed(ActionEvent e) {
	
		// button refresh
		if(e.getSource() == this.buttonRefresh) {
			this.updateComPortsList(this.comPortHandler.getAvailableComPorts());
		}
		// button open/close com port
		else if (e.getSource() == this.buttonOpenClose) {		
			//disconnect
			if(this.pcdi.isConnected()) {
				this.pcdi.disconnect();
				this.labelConnectionState.setText(" not connected ");
				this.buttonOpenClose.setText("open");
				this.buttonOpenClose.setEnabled(true);
				this.listComPorts.setEnabled(true);
			}
			//connect
			else {
				if(this.listComPorts.getSelectedValue()==null) {
					this.infoConsole.append("[Error] no com port selected\n");
					return;
				}
				this.comPortHandler.setComPortByDescriptor(this.listComPorts.getSelectedValue());
				this.pcdi.connect();
				this.listComPorts.setEnabled(false);
				this.labelConnectionState.setText(this.comPortHandler.toString());
				this.buttonRefresh.setEnabled(false);
				this.buttonOpenClose.setText("close");
			}
		}else if(e.getSource() == this.buttonEmergencyStop) {
			infoConsole.append("[CMD] emergency stop\n");
			this.pcdi.sendCommand(PCDI_COMMANDS.EMERGENCY_STOP, deviceId);
		}
		else if(e.getSource() == this.buttonResetComp) {
			PCDI_Parameter<?> tmp;
			if(this.compIsEnabled) tmp = new PCDI_ParameterInt(18, PCDI_TYPES.INT,(short) 0);
			else tmp =  new PCDI_ParameterInt(18, PCDI_TYPES.INT,(short) 1);
			pcdi.writeParameter(tmp, this.deviceId);
		}
		else if(e.getSource() == this.buttonEnableVC) {
			PCDI_Parameter<?> tmp;
			if(this.vcIsEnabled) tmp = new PCDI_ParameterInt(17, PCDI_TYPES.INT,(short) 0);
			else tmp =  new PCDI_ParameterInt(17, PCDI_TYPES.INT,(short) 1);
			pcdi.writeParameter(tmp, this.deviceId);
		}
		else if(e.getSource() == this.buttonEnableMPP) {
			PCDI_Parameter<?> tmp;
			if(this.mppIsEnabled) tmp = new PCDI_ParameterInt(25, PCDI_TYPES.INT,(short) 0);
			else tmp =  new PCDI_ParameterInt(25, PCDI_TYPES.INT,(short) 1);
			pcdi.writeParameter(tmp, this.deviceId);
		}
		
	}
	
	
	private void updateComPortsList(Collection<String> ports) {
		DefaultListModel<String> model = new DefaultListModel<String>();
		for(String tmp : ports) {
			model.addElement( tmp );
		}
		if(model.isEmpty()) {
			model.addElement("No COM");
		}
		this.infoConsole.append("COM UPDATED\n");
		this.listComPorts.setModel( model );
	}
	
	
	// PCDI Listener inteface implementation
	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		this.notifyParameterRead(parameter, deviceId);
	}

	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {
	}

	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		int valueNr = parameter.getValueNumber();
		if(valueNr == 1) {
			this.swVersion.setMinorVersion((short) parameter.getValue());
			this.labelSWVersion.setText(this.swVersion.toString());
		}
		else if(valueNr == 2) {
			this.swVersion.setMajorVersion((short) parameter.getValue());
			this.labelSWVersion.setText(this.swVersion.toString());
		}
		else if(valueNr == 3) {
			this.swVersion.setDay((short) parameter.getValue());
			this.labelSWVersion.setText(this.swVersion.toString());
		}
		else if(valueNr == 4) {
			this.swVersion.setMonth((short) parameter.getValue());
			this.labelSWVersion.setText(this.swVersion.toString());
		}
		else if(valueNr == 5) {
			this.swVersion.setYear((short) parameter.getValue());
			this.labelSWVersion.setText(this.swVersion.toString());
		}
		else if (valueNr == 18) {
			if((byte)parameter.getValue()>0) {
				this.compIsEnabled = true;
				this.buttonResetComp.setText("reset");
				this.circleCOMP.setShapeColorActive();
			}
			else {
				this.compIsEnabled = false;
				this.buttonResetComp.setText("reset");
				this.circleCOMP.setShapeColorInactive();
			}
		}
		else if (valueNr == 17) {
			if((byte)parameter.getValue()>0) {
				this.vcIsEnabled = true;
				this.buttonEnableVC.setText("disable");
				this.circleVC.setShapeColorActive();
			}
			else {
				this.vcIsEnabled = false;
				this.buttonEnableVC.setText("enable");
				this.circleVC.setShapeColorInactive();
			}
		}
		else if (valueNr == 25) {
			if((byte)parameter.getValue()>0) {
				this.mppIsEnabled = true;
				this.buttonEnableMPP.setText("disable");
				this.circleMPP.setShapeColorActive();
			}
			else {
				this.mppIsEnabled = false;
				this.buttonEnableMPP.setText("enable");
				this.circleMPP.setShapeColorInactive();
			}
		}
	}

	@Override
	public void notifyParameterInfo(PCDI_ParameterInfo parameter, int deviceId) {
	}

	@Override
	public void notifyCommandInvalid(PCDI_COMMANDS command, int deviceId) {
	}

	@Override
	public void notifyParameterInvalid(int valueNr, int deviceId) {
	}

	@Override
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId) {
	}
	@Override
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId) {
	}
	@Override
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId) {
	}
	
	private void disableGUI() {
		this.buttonResetComp.setEnabled(false);
		this.buttonEnableVC.setEnabled(false);
		this.buttonEnableMPP.setEnabled(false);
		this.labelConnectionState.setText(" not connected ");
		this.buttonOpenClose.setText("open");
		this.buttonOpenClose.setEnabled(true);
		this.listComPorts.setEnabled(true);
		this.buttonRefresh.setEnabled(true);
	}				
	
	private void enableGUI() {
		this.buttonResetComp.setEnabled(true);
		this.buttonEnableVC.setEnabled(true);
		this.buttonEnableMPP.setEnabled(true);
	}

	@Override
	public void notifyConnectionChanges(boolean isConnected) {
		
		if(isConnected) {
			this.enableGUI();
			this.infoConsole.append("[INFO] connected\n");
			
			if(this.blinkTimer==null) {
				TimerTask task = new TimerTask() {
			        public void run() {
			        	if(toggleCon) {
			        		circleConnected.setShapeColorActive();
			        		toggleCon = false;
			        	}
			        	else {
			        		circleConnected.setShapeColorInactive();
			        		toggleCon = true;
			        	}
			        }
			    };
			    this.blinkTimer=new Timer();
			    this.blinkTimer.scheduleAtFixedRate(task, 0, 500);
			}
		}
		else {
			this.circleCOMP.setShapeColorInactive();
			this.circleVC.setShapeColorInactive();
			this.circleMPP.setShapeColorInactive();
			this.disableGUI();
			this.infoConsole.append("[INFO] disconnected\n");
			this.circleConnected.setShapeColorInactive();
			if(this.blinkTimer!=null) {
				this.blinkTimer.cancel();
				this.blinkTimer=null;
			}
		}
	}


	@Override
	public void notifyTableInfo(PCDI_TableInfo table, int deviceId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void notifyTableRead(PCDI_TableData tableData, int deviceId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void notifyTableWrite(PCDI_TableData tableData, int deviceId) {
		// TODO Auto-generated method stub
		
	}

}
