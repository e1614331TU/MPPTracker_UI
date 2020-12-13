package base;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import comm.I_PCDI;
import comm.I_PCDI_Listener;
import comm.PCDI_COMMANDS;
import comm.PCDI_ERROR_TYPE;
import comm.PCDI_OSCILLOSCOPE_STATE;
import comm.PCDI_OscilloscopeData;
import comm.PCDI_PERMISSION;
import comm.PCDI_Parameter;
import comm.PCDI_ParameterByte;
import comm.PCDI_ParameterFloat;
import comm.PCDI_ParameterInfo;
import comm.PCDI_ParameterInt;
import comm.PCDI_ParameterLong;
import comm.PCDI_TYPES;


/**
* JPanelSingleParam
* <p>
* this class is needed by the JPanelParams class. For each Parameter
* an instance of JPanelSingleParam is added to the JPanelParams.
*/
public class JPanelSingleParam extends JPanel implements ActionListener, I_PCDI_Listener, FocusListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField Value;
	private int deviceId;
	private JTextField actValue;
	
	JButton btnSetParam;
	JButton btnReadParam;
	
	private PCDI_Parameter<?> param;
	private PCDI_ParameterInfo parameterInfo;
	
	private I_PCDI pcdi;
	
	private JTextArea infoConsole;
	private JTextField textField;
	
	private boolean txt_focused = false;
	/**
	* this method is the constructor
	* @param parameter contains information about the parameterInfos (permissions, unit,..)
	* @param pcdi is needed for communication
	* @param deviceId is needed for communication with the controller for ability to communicate with the right controller 
	* @param infoConsole is needed for giving user information
	*/
	public JPanelSingleParam(PCDI_ParameterInfo parameter, int deviceId, I_PCDI pcdi, JTextArea infoConsole) {
		super();
		this.pcdi=pcdi;
		this.parameterInfo=parameter;
		this.infoConsole=infoConsole;
		
		
		generateGUI();
		
		if(parameter.getType() == PCDI_TYPES.BYTE) {
			this.param=new PCDI_ParameterByte(parameter.getValNr(), PCDI_TYPES.BYTE, (byte)0);
		}
		else if(parameter.getType() == PCDI_TYPES.FLOAT) {
			this.param=new PCDI_ParameterFloat(parameter.getValNr(), PCDI_TYPES.FLOAT, 0.0f);
		}
		else if(parameter.getType() == PCDI_TYPES.INT) {
			this.param=new PCDI_ParameterInt(parameter.getValNr(), PCDI_TYPES.INT, (short)0);
		}
		else if(parameter.getType() == PCDI_TYPES.LONG) {
			this.param=new PCDI_ParameterLong(parameter.getValNr(), PCDI_TYPES.LONG, 0);
		}
		
	}
	
	public boolean isTxt_focused() {
		return txt_focused;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.btnReadParam) {
			this.pcdi.readParameter(this.param.getValueNumber(), deviceId);
		}
		else if(e.getSource() == this.btnSetParam) {
			if(!this.Value.getText().isEmpty()) {
				this.param.setValue(this.Value.getText());			
				this.pcdi.writeParameter(this.param, deviceId);
			}
			else {
				infoConsole.append("No value inserted!!!\n");
			}
			this.pcdi.readParameter(this.param.getValueNumber(), deviceId);
		}
		
	}
	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		if(parameter.getValueNumber()==this.param.getValueNumber()) {
			this.actValue.setText(parameter.getValue().toString());
			this.Value.setText(parameter.getValue().toString());
		}
	}

	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {
	}

	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		if(parameter.getValueNumber()==this.param.getValueNumber()) {
			this.actValue.setText(parameter.getValue().toString());
			if(this.txt_focused == false)
				this.Value.setText(parameter.getValue().toString());
				
			//if(this.parameterInfo.getName().contentEquals("MPPen") )
			//	System.out.println("Value read "+ this.parameterInfo.getName() + " " + parameter.getValue().toString() + " focused " + this.txt_focused);
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
	private void generateGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{50, 40, 40,40, 40, 40};
		gridBagLayout.rowHeights = new int[]{15};
		gridBagLayout.columnWeights = new double[]{0.0, 0.4, 0.4, 0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0};
		setLayout(gridBagLayout);
		
		JLabel lblNameParam = new JLabel(parameterInfo.getName());
		GridBagConstraints gbc_lblNameParam = new GridBagConstraints();
		gbc_lblNameParam.insets = new Insets(2, 2, 2, 5);
		gbc_lblNameParam.anchor = GridBagConstraints.WEST;
		gbc_lblNameParam.gridx = 0;
		gbc_lblNameParam.gridy = 0;
		add(lblNameParam, gbc_lblNameParam);
		
		Value = new JTextField();
		GridBagConstraints gbc_Value = new GridBagConstraints();
		gbc_Value.fill = GridBagConstraints.HORIZONTAL;
		gbc_Value.insets = new Insets(2, 2, 2, 5);
		gbc_Value.gridx = 1;
		gbc_Value.gridy = 0;
		add(Value, gbc_Value);
		Value.setColumns(10);
		this.Value.addFocusListener(this);
		
		actValue = new JTextField();
		actValue.setEditable(false);
		GridBagConstraints gbc_actValue = new GridBagConstraints();
		gbc_actValue.fill = GridBagConstraints.HORIZONTAL;
		gbc_actValue.insets = new Insets(2, 2, 2, 5);
		gbc_actValue.gridx = 2;
		gbc_actValue.gridy = 0;
		add(actValue, gbc_actValue);
		actValue.setColumns(10);
		
		JLabel lblUnit = new JLabel(parameterInfo.getUnit().name());
		GridBagConstraints gbc_lblUnit = new GridBagConstraints();
		gbc_lblUnit.anchor = GridBagConstraints.EAST;
		gbc_lblUnit.insets = new Insets(2, 2, 2, 5);
		gbc_lblUnit.gridx = 3;
		gbc_lblUnit.gridy = 0;
		add(lblUnit, gbc_lblUnit);
		
		btnSetParam = new JButton("Set");
		GridBagConstraints gbc_btnSetParam = new GridBagConstraints();
		gbc_btnSetParam.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSetParam.insets = new Insets(2, 2, 2, 5);
		gbc_btnSetParam.gridx = 4;
		gbc_btnSetParam.gridy = 0;
		add(btnSetParam, gbc_btnSetParam);
		this.btnSetParam.addActionListener(this);
		
		btnReadParam = new JButton("Read");
		GridBagConstraints gbc_btnReadParam = new GridBagConstraints();
		gbc_btnReadParam.insets = new Insets(2, 2, 2, 5);
		gbc_btnReadParam.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnReadParam.gridx = 5;
		gbc_btnReadParam.gridy = 0;
		add(btnReadParam, gbc_btnReadParam);
		this.btnReadParam.addActionListener(this);
		
		this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.gray)); 
		if(this.parameterInfo.getPermission() == PCDI_PERMISSION.READ) {
			btnSetParam.setEnabled(false);
			this.Value.setEditable(false);
		}
	}
	@Override
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId) {
	}
	@Override
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId) {
	}
	@Override
	public void notifyConnectionChanges(boolean isConnected) {
	}
	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == this.Value) {
			this.txt_focused = true;
			//System.out.println("focused true" + parameterInfo.getName() );
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == this.Value) {
			this.txt_focused = false;
			//System.out.println("focused false" + parameterInfo.getName() );
		}
	}

	
	
}
