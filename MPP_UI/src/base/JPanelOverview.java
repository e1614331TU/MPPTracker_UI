package base;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
import comm.PCDI_ParameterByte;
import comm.PCDI_ParameterFloat;
import comm.PCDI_ParameterInfo;
import comm.PCDI_ParameterInt;
import comm.PCDI_TYPES;
import comm.PCDI_TableData;
import comm.PCDI_TableInfo;

import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Component;
import javax.swing.Box;
/**
* JPanelOverview
* <p>
* this is the class which handles the Overview Panel. 
* All relevant parameters are read cyclic and displayed in this panel.
* 
*/
public class JPanelOverview extends JPanel implements I_PCDI_Listener,  KeyListener, FocusListener  {
	
	private static final long serialVersionUID = 1L;
	private JTextArea infoConsole;
	private int deviceId;
	private I_PCDI pcdi;

	private JTextField textField_actU;
	private JTextField textField_actI;
	private JTextField textField_actP;
	private JTextField textField_actUout;
	private JTextField textField_I_ref;
	
	private boolean txt_pc_setPos_focused = false;
	private boolean txt_sc_setOmega_focused = false;
	private boolean txt_cc_setId_focused = false;
	private boolean txt_cc_setIq_focused = false;
	
	private JButton buttonDiagram;
	
	private boolean isActive = false;
	
	private List<Integer> cyclicVals = new ArrayList<Integer>();
	private Timer cyclicTimer  = null;
	
	private MPP_Osci mpp_osci;
	/**
	* this method is the constructor
	* @param pcdi is needed for communication
	* @param deviceId is needed for communication with the controller for ability to communicate with the right controller 
	* @param infoConsole is needed for giving user information
	*/
	public JPanelOverview(I_PCDI pcdi, int deviceId, JTextArea infoConsole) {
		super();
		this.pcdi = pcdi;
		this.deviceId = deviceId;
		this.infoConsole = infoConsole;
		
		//this.cyclicVals.add(22); // gamma_mech

		Font font = new Font("Tahoma", Font.ROMAN_BASELINE, 14);
		
		this.pcdi.registerListener(this);
		

		this.mpp_osci = new MPP_Osci(this.pcdi, this.deviceId, this.infoConsole);
		
		
		
		this.setPreferredSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		this.setMinimumSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		
		
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{300, 0};
		gridBagLayout.rowHeights = new int[] {446, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel_pc = new JPanel();
		panel_pc.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panel_pc = new GridBagConstraints();
		gbc_panel_pc.insets = new Insets(5, 0, 5, 0);
		gbc_panel_pc.fill = GridBagConstraints.BOTH;
		gbc_panel_pc.gridx = 0;
		gbc_panel_pc.gridy = 0;
		add(panel_pc, gbc_panel_pc);
		GridBagLayout gbl_panel_pc = new GridBagLayout();
		gbl_panel_pc.columnWidths = new int[]{200, 0};
		gbl_panel_pc.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_pc.columnWeights = new double[]{0.0, 1.0};
		gbl_panel_pc.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_pc.setLayout(gbl_panel_pc);
		
		JLabel lblNewLabel = new JLabel("MPP Overview");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 0);
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_pc.add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Uin:");
		lblNewLabel_1.setFont(font);
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel_pc.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		textField_actU = new JTextField();
		textField_actU.setFont(new Font("Tahoma", Font.BOLD, 18));
		textField_actU.setEditable(false);
		GridBagConstraints gbc_textField_pc_setPos = new GridBagConstraints();
		gbc_textField_pc_setPos.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_setPos.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_setPos.gridx = 1;
		gbc_textField_pc_setPos.gridy = 1;
		panel_pc.add(textField_actU, gbc_textField_pc_setPos);
		textField_actU.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Iin:");
		lblNewLabel_2.setFont(font);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel_pc.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		textField_actI = new JTextField();
		textField_actI.setEditable(false);
		textField_actI.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_textField_pc_actPos = new GridBagConstraints();
		gbc_textField_pc_actPos.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_actPos.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_actPos.gridx = 1;
		gbc_textField_pc_actPos.gridy = 2;
		panel_pc.add(textField_actI, gbc_textField_pc_actPos);
		textField_actI.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("P:");
		lblNewLabel_3.setFont(font);
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		panel_pc.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		textField_actP = new JTextField();
		textField_actP.setEditable(false);
		textField_actP.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_textField_pc_P = new GridBagConstraints();
		gbc_textField_pc_P.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_P.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_P.gridx = 1;
		gbc_textField_pc_P.gridy = 3;
		panel_pc.add(textField_actP, gbc_textField_pc_P);
		textField_actP.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Uout:");
		lblNewLabel_4.setFont(font);
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 4;
		panel_pc.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		textField_actUout = new JTextField();
		textField_actUout.setFont(new Font("Tahoma", Font.BOLD, 18));
		textField_actUout.setEditable(false);
		GridBagConstraints gbc_textField_pc_D = new GridBagConstraints();
		gbc_textField_pc_D.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_D.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_D.gridx = 1;
		gbc_textField_pc_D.gridy = 4;
		panel_pc.add(textField_actUout, gbc_textField_pc_D);
		textField_actUout.setColumns(10);
		
		JLabel lbl_PC_img = new JLabel("");
		GridBagConstraints gbc_lbl_PC_img = new GridBagConstraints();
		gbc_lbl_PC_img.insets = new Insets(0, 0, 5, 0);
		gbc_lbl_PC_img.gridwidth = 2;
		gbc_lbl_PC_img.gridx = 0;
		gbc_lbl_PC_img.gridy = 7;
		panel_pc.add(lbl_PC_img, gbc_lbl_PC_img);
		
		GridBagConstraints c_8 = new GridBagConstraints();
		c_8.gridwidth = 2;
		c_8.insets = new Insets(0, 0, 5, 0); // top padding
		c_8.fill = GridBagConstraints.BOTH;
		c_8.weightx = 0.1;
		c_8.weighty = 1.0;
		c_8.gridheight = 2;
		c_8.gridx = 0;
		c_8.gridy = 6;
		panel_pc.add(mpp_osci, c_8);
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\pc_block.jpg"));
			Image dimg = img.getScaledInstance(200, 300,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
			//lbl_PC_img.setIcon(icon);
			
//			Component verticalStrut = Box.createVerticalStrut(45);
//			GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
//			gbc_verticalStrut.gridheight = 2;
//			gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
//			gbc_verticalStrut.gridx = 0;
//			gbc_verticalStrut.gridy = 5;
//			panel_pc.add(verticalStrut, gbc_verticalStrut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\sc_block.jpg"));
			Image dimg = img.getScaledInstance(200, 300,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\cc_block.jpg"));
			Image dimg = img.getScaledInstance(200, 300,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\motor.png"));
			Image dimg = img.getScaledInstance(100, 100,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.textField_actU.addFocusListener(this);
		this.textField_actU.addKeyListener(this);
		
		
		this.disableGUI();
		
	}
	
	private void disableGUI() {
		this.textField_actU.setEnabled(false);
	}
	
	private void enableGUI() {
		this.textField_actU.setEnabled(true);
	}
	
	private void startTimer() {
		if(this.cyclicTimer==null) {
			TimerTask task = new TimerTask() {
		        public void run() {
		        		pcdi.readParameter(16, deviceId);
		        		
		        }
		    };
		    this.cyclicTimer=new Timer();
		    this.cyclicTimer.scheduleAtFixedRate(task, 0, 1000);
		}
	}
	
	private void stopTimer() {
		if(this.cyclicTimer!=null) {
			this.cyclicTimer.cancel();
			this.cyclicTimer=null;
		}
	}
	/**
	* this method is the relevant for starting and stopping cyclic communication
	* @param state if 1 then it is connected and cyclic connection starts else it stops
	*/
	public void setActiveState(boolean state) {
		this.isActive = state;
		if(state && this.pcdi.isConnected()) {
			this.startTimer();
		}
		else {
			this.stopTimer();
		}
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
		if(valueNr == 14) {
			this.textField_actU.setText(Math.round(Double.parseDouble(parameter.getValue().toString())*10.0)/10.0+" V");
		}
		else if(valueNr == 15) {
			this.textField_actI.setText(Math.round(Double.parseDouble(parameter.getValue().toString())*10.0)/10.0+" A");
		}
		else if(valueNr == 32) {
			this.textField_actP.setText(Math.round(Double.parseDouble(parameter.getValue().toString())*10.0)/10.0+" W");
		}
		else if(valueNr == 16) {
			this.textField_actUout.setText(Math.round(Double.parseDouble(parameter.getValue().toString())*10.0)/10.0+" V");
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
	
	@Override
	public void notifyConnectionChanges(boolean isConnected) {
		if(isConnected) {
			this.enableGUI();
			if(this.isActive) this.startTimer();
		}
		else {
			this.disableGUI();
			this.stopTimer();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	    int key = e.getKeyCode();
	    if (key == KeyEvent.VK_ENTER) {
	    	 // set Omega
	    	 if(e.getSource() == this.textField_actU) {
	    		if(!this.textField_actU.getText().isEmpty()) {
		 			PCDI_Parameter<?> tmp;
					tmp = new PCDI_ParameterByte(22, PCDI_TYPES.BYTE, (byte)0);
					tmp.setValue(this.textField_actU.getText());
					this.pcdi.writeParameter(tmp, this.deviceId);
	    		}
	    	 }
	    	 
	     }
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == this.textField_actU) {
			this.txt_pc_setPos_focused = true;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == this.textField_actU) {
			this.txt_pc_setPos_focused = false;
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
