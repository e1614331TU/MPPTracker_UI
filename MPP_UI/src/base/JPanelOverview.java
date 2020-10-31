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
import comm.PCDI_ParameterFloat;
import comm.PCDI_ParameterInfo;
import comm.PCDI_ParameterInt;
import comm.PCDI_TYPES;


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

	private JTextField textField_pc_setPos;
	private JTextField textField_pc_actPos;
	private JTextField textField_pc_P;
	private JTextField textField_pc_D;
	private JTextField textField_sc_setOmega;
	private JTextField textField_sc_actOmega;
	private JTextField textField_sc_P;
	private JTextField textField_sc_I;
	private JTextField textField_cc_setIq;
	private JTextField textField_cc_setId;
	private JTextField textField_cc_actIq;
	private JTextField textField_cc_actId;
	private JTextField textField_cc_P;
	private JTextField textField_cc_I;
	private JTextField textField_mot_VSupply;
	private JTextField textField_V_U;
	private JTextField textField_V_V;
	private JTextField textField_V_W;
	private JTextField textField_I_U;
	private JTextField textField_I_V;
	private JTextField textField_I_W;
	private JTextField textField_I_maxCC;
	private JTextField textField_I_maxSC;
	private JTextField textField_I_ref;
	private JTextField textField_n_max;
	private JTextField textField_polepairs;
	private JTextField textField_offset;
	
	private boolean txt_pc_setPos_focused = false;
	private boolean txt_sc_setOmega_focused = false;
	private boolean txt_cc_setId_focused = false;
	private boolean txt_cc_setIq_focused = false;
	
	private boolean isActive = false;
	
	private List<Integer> cyclicVals = new ArrayList<Integer>();
	private Timer cyclicTimer  = null;
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
		
		this.cyclicVals.add(21); // gamma_mech
		this.cyclicVals.add(45); // pos set
		this.cyclicVals.add(30); // omega
		this.cyclicVals.add(33); // omega_set
		this.cyclicVals.add(28); // i d cur
		this.cyclicVals.add(29); // i q cur
		this.cyclicVals.add(26); // i d set
		this.cyclicVals.add(27); // i q set
		this.cyclicVals.add(16); // i_u
		this.cyclicVals.add(17); // i_v
		this.cyclicVals.add(18); // i_w
		this.cyclicVals.add(56); // v_u
		this.cyclicVals.add(57); // v_v
		this.cyclicVals.add(58); // v_w
		this.cyclicVals.add(55); // v_supply
		
		
		this.pcdi.registerListener(this);
		

		this.setPreferredSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		this.setMinimumSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{300, 300, 300, 300, 0};
		gridBagLayout.rowHeights = new int[] {446, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel panel_pc = new JPanel();
		panel_pc.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panel_pc = new GridBagConstraints();
		gbc_panel_pc.insets = new Insets(5, 0, 5, 5);
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
		
		JLabel lblNewLabel = new JLabel("Position Controller");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(5, 5, 5, 0);
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_pc.add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Position set (rad)");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		panel_pc.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		textField_pc_setPos = new JTextField();
		GridBagConstraints gbc_textField_pc_setPos = new GridBagConstraints();
		gbc_textField_pc_setPos.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_setPos.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_setPos.gridx = 1;
		gbc_textField_pc_setPos.gridy = 1;
		panel_pc.add(textField_pc_setPos, gbc_textField_pc_setPos);
		textField_pc_setPos.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Position actual (rad)");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel_pc.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		textField_pc_actPos = new JTextField();
		textField_pc_actPos.setEditable(false);
		GridBagConstraints gbc_textField_pc_actPos = new GridBagConstraints();
		gbc_textField_pc_actPos.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_actPos.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_actPos.gridx = 1;
		gbc_textField_pc_actPos.gridy = 2;
		panel_pc.add(textField_pc_actPos, gbc_textField_pc_actPos);
		textField_pc_actPos.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("P - factor (1)");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		panel_pc.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		textField_pc_P = new JTextField();
		textField_pc_P.setEditable(false);
		GridBagConstraints gbc_textField_pc_P = new GridBagConstraints();
		gbc_textField_pc_P.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_P.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_P.gridx = 1;
		gbc_textField_pc_P.gridy = 3;
		panel_pc.add(textField_pc_P, gbc_textField_pc_P);
		textField_pc_P.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("D - factor (1)");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 4;
		panel_pc.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		textField_pc_D = new JTextField();
		textField_pc_D.setEditable(false);
		GridBagConstraints gbc_textField_pc_D = new GridBagConstraints();
		gbc_textField_pc_D.insets = new Insets(0, 5, 5, 5);
		gbc_textField_pc_D.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_pc_D.gridx = 1;
		gbc_textField_pc_D.gridy = 4;
		panel_pc.add(textField_pc_D, gbc_textField_pc_D);
		textField_pc_D.setColumns(10);
		
		JLabel lbl_PC_img = new JLabel("");
		GridBagConstraints gbc_lbl_PC_img = new GridBagConstraints();
		gbc_lbl_PC_img.insets = new Insets(0, 0, 5, 0);
		gbc_lbl_PC_img.gridwidth = 2;
		gbc_lbl_PC_img.gridx = 0;
		gbc_lbl_PC_img.gridy = 7;
		panel_pc.add(lbl_PC_img, gbc_lbl_PC_img);
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\pc_block.jpg"));
			Image dimg = img.getScaledInstance(200, 300,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
			lbl_PC_img.setIcon(icon);
			
			Component verticalStrut = Box.createVerticalStrut(45);
			GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
			gbc_verticalStrut.gridheight = 2;
			gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
			gbc_verticalStrut.gridx = 0;
			gbc_verticalStrut.gridy = 5;
			panel_pc.add(verticalStrut, gbc_verticalStrut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JPanel panel_sc = new JPanel();
		panel_sc.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panel_sc = new GridBagConstraints();
		gbc_panel_sc.insets = new Insets(5, 0, 5, 5);
		gbc_panel_sc.fill = GridBagConstraints.BOTH;
		gbc_panel_sc.gridx = 1;
		gbc_panel_sc.gridy = 0;
		add(panel_sc, gbc_panel_sc);
		GridBagLayout gbl_panel_sc = new GridBagLayout();
		gbl_panel_sc.columnWidths = new int[] {200, 0};
		gbl_panel_sc.rowHeights = new int[]{13, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_sc.columnWeights = new double[]{0.0, 1.0};
		gbl_panel_sc.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_sc.setLayout(gbl_panel_sc);
		
		JLabel lblNewLabel_5 = new JLabel("Speed Controller");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.insets = new Insets(5, 0, 5, 0);
		gbc_lblNewLabel_5.gridwidth = 2;
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 0;
		panel_sc.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		JLabel lblNewLabel_8 = new JLabel("n (rpm)");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 0;
		gbc_lblNewLabel_8.gridy = 1;
		panel_sc.add(lblNewLabel_8, gbc_lblNewLabel_8);
		
		textField_sc_setOmega = new JTextField();
		GridBagConstraints gbc_textField_sc_setOmega = new GridBagConstraints();
		gbc_textField_sc_setOmega.insets = new Insets(0, 5, 5, 5);
		gbc_textField_sc_setOmega.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_sc_setOmega.gridx = 1;
		gbc_textField_sc_setOmega.gridy = 1;
		panel_sc.add(textField_sc_setOmega, gbc_textField_sc_setOmega);
		textField_sc_setOmega.setColumns(10);
		
		JLabel lblNewLabel_9 = new JLabel("n actual (rpm)");
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_9.gridx = 0;
		gbc_lblNewLabel_9.gridy = 2;
		panel_sc.add(lblNewLabel_9, gbc_lblNewLabel_9);
		
		textField_sc_actOmega = new JTextField();
		textField_sc_actOmega.setEditable(false);
		GridBagConstraints gbc_textField_sc_actOmega = new GridBagConstraints();
		gbc_textField_sc_actOmega.insets = new Insets(0, 5, 5, 5);
		gbc_textField_sc_actOmega.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_sc_actOmega.gridx = 1;
		gbc_textField_sc_actOmega.gridy = 2;
		panel_sc.add(textField_sc_actOmega, gbc_textField_sc_actOmega);
		textField_sc_actOmega.setColumns(10);
		
		JLabel lblNewLabel_10 = new JLabel("P - factor (1)");
		GridBagConstraints gbc_lblNewLabel_10 = new GridBagConstraints();
		gbc_lblNewLabel_10.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_10.gridx = 0;
		gbc_lblNewLabel_10.gridy = 3;
		panel_sc.add(lblNewLabel_10, gbc_lblNewLabel_10);
		
		textField_sc_P = new JTextField();
		textField_sc_P.setEditable(false);
		GridBagConstraints gbc_textField_sc_P = new GridBagConstraints();
		gbc_textField_sc_P.insets = new Insets(0, 5, 5, 5);
		gbc_textField_sc_P.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_sc_P.gridx = 1;
		gbc_textField_sc_P.gridy = 3;
		panel_sc.add(textField_sc_P, gbc_textField_sc_P);
		textField_sc_P.setColumns(10);
		
		JLabel lblNewLabel_11 = new JLabel("I - factor (1)");
		GridBagConstraints gbc_lblNewLabel_11 = new GridBagConstraints();
		gbc_lblNewLabel_11.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_11.gridx = 0;
		gbc_lblNewLabel_11.gridy = 4;
		panel_sc.add(lblNewLabel_11, gbc_lblNewLabel_11);
		
		textField_sc_I = new JTextField();
		textField_sc_I.setEditable(false);
		GridBagConstraints gbc_textField_sc_I = new GridBagConstraints();
		gbc_textField_sc_I.insets = new Insets(0, 5, 5, 5);
		gbc_textField_sc_I.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_sc_I.gridx = 1;
		gbc_textField_sc_I.gridy = 4;
		panel_sc.add(textField_sc_I, gbc_textField_sc_I);
		textField_sc_I.setColumns(10);
		
		Component verticalStrut = Box.createVerticalStrut(45);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.gridheight = 2;
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 5;
		panel_sc.add(verticalStrut, gbc_verticalStrut);
		
		JLabel lbl_SC_img = new JLabel("");
		GridBagConstraints gbc_lbl_SC_img = new GridBagConstraints();
		gbc_lbl_SC_img.gridwidth = 2;
		gbc_lbl_SC_img.gridx = 0;
		gbc_lbl_SC_img.gridy = 7;
		panel_sc.add(lbl_SC_img, gbc_lbl_SC_img);
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\sc_block.jpg"));
			Image dimg = img.getScaledInstance(200, 300,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
			lbl_SC_img.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JPanel panel_cc = new JPanel();
		panel_cc.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panel_cc = new GridBagConstraints();
		gbc_panel_cc.insets = new Insets(5, 0, 5, 5);
		gbc_panel_cc.fill = GridBagConstraints.BOTH;
		gbc_panel_cc.gridx = 2;
		gbc_panel_cc.gridy = 0;
		add(panel_cc, gbc_panel_cc);
		GridBagLayout gbl_panel_cc = new GridBagLayout();
		gbl_panel_cc.columnWidths = new int[] {200, 0};
		gbl_panel_cc.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_cc.columnWeights = new double[]{0.0, 1.0};
		gbl_panel_cc.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_cc.setLayout(gbl_panel_cc);
		
		JLabel lblNewLabel_6 = new JLabel("Current Controller");
		lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.gridwidth = 2;
		gbc_lblNewLabel_6.insets = new Insets(5, 0, 5, 0);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 0;
		panel_cc.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		JLabel lblNewLabel_12 = new JLabel("Iq set (A)");
		GridBagConstraints gbc_lblNewLabel_12 = new GridBagConstraints();
		gbc_lblNewLabel_12.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_12.gridx = 0;
		gbc_lblNewLabel_12.gridy = 1;
		panel_cc.add(lblNewLabel_12, gbc_lblNewLabel_12);
		
		textField_cc_setIq = new JTextField();
		GridBagConstraints gbc_textField_cc_setIq = new GridBagConstraints();
		gbc_textField_cc_setIq.insets = new Insets(0, 5, 5, 5);
		gbc_textField_cc_setIq.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_cc_setIq.gridx = 1;
		gbc_textField_cc_setIq.gridy = 1;
		panel_cc.add(textField_cc_setIq, gbc_textField_cc_setIq);
		textField_cc_setIq.setColumns(10);
		
		JLabel lblNewLabel_13 = new JLabel("Id set (A)");
		GridBagConstraints gbc_lblNewLabel_13 = new GridBagConstraints();
		gbc_lblNewLabel_13.insets = new Insets(0, 5, 5, 5);
		gbc_lblNewLabel_13.gridx = 0;
		gbc_lblNewLabel_13.gridy = 2;
		panel_cc.add(lblNewLabel_13, gbc_lblNewLabel_13);
		
		textField_cc_setId = new JTextField();
		GridBagConstraints gbc_textField_cc_setId = new GridBagConstraints();
		gbc_textField_cc_setId.insets = new Insets(0, 5, 5, 5);
		gbc_textField_cc_setId.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_cc_setId.gridx = 1;
		gbc_textField_cc_setId.gridy = 2;
		panel_cc.add(textField_cc_setId, gbc_textField_cc_setId);
		textField_cc_setId.setColumns(10);
		
		JLabel lblNewLabel_14 = new JLabel("Iq actual (A)");
		GridBagConstraints gbc_lblNewLabel_14 = new GridBagConstraints();
		gbc_lblNewLabel_14.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_14.gridx = 0;
		gbc_lblNewLabel_14.gridy = 3;
		panel_cc.add(lblNewLabel_14, gbc_lblNewLabel_14);
		
		textField_cc_actIq = new JTextField();
		textField_cc_actIq.setEditable(false);
		GridBagConstraints gbc_textField_cc_actIq = new GridBagConstraints();
		gbc_textField_cc_actIq.insets = new Insets(0, 5, 5, 5);
		gbc_textField_cc_actIq.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_cc_actIq.gridx = 1;
		gbc_textField_cc_actIq.gridy = 3;
		panel_cc.add(textField_cc_actIq, gbc_textField_cc_actIq);
		textField_cc_actIq.setColumns(10);
		
		JLabel lblNewLabel_15 = new JLabel("Id actual (A)");
		GridBagConstraints gbc_lblNewLabel_15 = new GridBagConstraints();
		gbc_lblNewLabel_15.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_15.gridx = 0;
		gbc_lblNewLabel_15.gridy = 4;
		panel_cc.add(lblNewLabel_15, gbc_lblNewLabel_15);
		
		textField_cc_actId = new JTextField();
		textField_cc_actId.setEditable(false);
		GridBagConstraints gbc_textField_cc_actId = new GridBagConstraints();
		gbc_textField_cc_actId.insets = new Insets(0, 5, 5, 5);
		gbc_textField_cc_actId.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_cc_actId.gridx = 1;
		gbc_textField_cc_actId.gridy = 4;
		panel_cc.add(textField_cc_actId, gbc_textField_cc_actId);
		textField_cc_actId.setColumns(10);
		
		JLabel lblNewLabel_16 = new JLabel("P - factor (1)");
		GridBagConstraints gbc_lblNewLabel_16 = new GridBagConstraints();
		gbc_lblNewLabel_16.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_16.gridx = 0;
		gbc_lblNewLabel_16.gridy = 5;
		panel_cc.add(lblNewLabel_16, gbc_lblNewLabel_16);
		
		textField_cc_P = new JTextField();
		textField_cc_P.setEditable(false);
		GridBagConstraints gbc_textField_cc_P = new GridBagConstraints();
		gbc_textField_cc_P.insets = new Insets(0, 5, 5, 5);
		gbc_textField_cc_P.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_cc_P.gridx = 1;
		gbc_textField_cc_P.gridy = 5;
		panel_cc.add(textField_cc_P, gbc_textField_cc_P);
		textField_cc_P.setColumns(10);
		
		JLabel lblNewLabel_17 = new JLabel("I - factor (1)");
		GridBagConstraints gbc_lblNewLabel_17 = new GridBagConstraints();
		gbc_lblNewLabel_17.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_17.gridx = 0;
		gbc_lblNewLabel_17.gridy = 6;
		panel_cc.add(lblNewLabel_17, gbc_lblNewLabel_17);
		
		textField_cc_I = new JTextField();
		textField_cc_I.setEditable(false);
		GridBagConstraints gbc_textField_cc_I = new GridBagConstraints();
		gbc_textField_cc_I.insets = new Insets(0, 5, 5, 5);
		gbc_textField_cc_I.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_cc_I.gridx = 1;
		gbc_textField_cc_I.gridy = 6;
		panel_cc.add(textField_cc_I, gbc_textField_cc_I);
		textField_cc_I.setColumns(10);
		
		JLabel lbl_CC_img = new JLabel("");
		GridBagConstraints gbc_lbl_CC_img = new GridBagConstraints();
		gbc_lbl_CC_img.gridwidth = 2;
		gbc_lbl_CC_img.insets = new Insets(0, 0, 0, 5);
		gbc_lbl_CC_img.gridx = 0;
		gbc_lbl_CC_img.gridy = 7;
		panel_cc.add(lbl_CC_img, gbc_lbl_CC_img);
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\cc_block.jpg"));
			Image dimg = img.getScaledInstance(200, 300,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
			lbl_CC_img.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JPanel panel_mot = new JPanel();
		panel_mot.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panel_mot = new GridBagConstraints();
		gbc_panel_mot.insets = new Insets(5, 0, 5, 0);
		gbc_panel_mot.fill = GridBagConstraints.BOTH;
		gbc_panel_mot.gridx = 3;
		gbc_panel_mot.gridy = 0;
		add(panel_mot, gbc_panel_mot);
		GridBagLayout gbl_panel_mot = new GridBagLayout();
		gbl_panel_mot.columnWidths = new int[] {200, 0};
		gbl_panel_mot.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_mot.columnWeights = new double[]{0.0, 1.0};
		gbl_panel_mot.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_mot.setLayout(gbl_panel_mot);
		
		JLabel lblNewLabel_7 = new JLabel("Motor");
		lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.gridwidth = 2;
		gbc_lblNewLabel_7.insets = new Insets(5, 0, 5, 0);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 0;
		panel_mot.add(lblNewLabel_7, gbc_lblNewLabel_7);
		
		JLabel lblNewLabel_18 = new JLabel("V Supply (V)");
		GridBagConstraints gbc_lblNewLabel_18 = new GridBagConstraints();
		gbc_lblNewLabel_18.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_18.gridx = 0;
		gbc_lblNewLabel_18.gridy = 1;
		panel_mot.add(lblNewLabel_18, gbc_lblNewLabel_18);
		
		textField_mot_VSupply = new JTextField();
		textField_mot_VSupply.setEditable(false);
		GridBagConstraints gbc_textField_mot_VSupply = new GridBagConstraints();
		gbc_textField_mot_VSupply.insets = new Insets(0, 5, 5, 5);
		gbc_textField_mot_VSupply.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_mot_VSupply.gridx = 1;
		gbc_textField_mot_VSupply.gridy = 1;
		panel_mot.add(textField_mot_VSupply, gbc_textField_mot_VSupply);
		textField_mot_VSupply.setColumns(10);
		
		JLabel lblNewLabel_19 = new JLabel("V Phase U (V)");
		GridBagConstraints gbc_lblNewLabel_19 = new GridBagConstraints();
		gbc_lblNewLabel_19.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_19.gridx = 0;
		gbc_lblNewLabel_19.gridy = 2;
		panel_mot.add(lblNewLabel_19, gbc_lblNewLabel_19);
		
		textField_V_U = new JTextField();
		textField_V_U.setEditable(false);
		GridBagConstraints gbc_textField_V_U = new GridBagConstraints();
		gbc_textField_V_U.insets = new Insets(0, 5, 5, 5);
		gbc_textField_V_U.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_V_U.gridx = 1;
		gbc_textField_V_U.gridy = 2;
		panel_mot.add(textField_V_U, gbc_textField_V_U);
		textField_V_U.setColumns(10);
		
		JLabel lblNewLabel_20 = new JLabel("V Phase V (V)");
		GridBagConstraints gbc_lblNewLabel_20 = new GridBagConstraints();
		gbc_lblNewLabel_20.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_20.gridx = 0;
		gbc_lblNewLabel_20.gridy = 3;
		panel_mot.add(lblNewLabel_20, gbc_lblNewLabel_20);
		
		textField_V_V = new JTextField();
		textField_V_V.setEditable(false);
		GridBagConstraints gbc_textField_V_V = new GridBagConstraints();
		gbc_textField_V_V.insets = new Insets(0, 5, 5, 5);
		gbc_textField_V_V.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_V_V.gridx = 1;
		gbc_textField_V_V.gridy = 3;
		panel_mot.add(textField_V_V, gbc_textField_V_V);
		textField_V_V.setColumns(10);
		
		JLabel lblNewLabel_21 = new JLabel("V Phase W (V)");
		GridBagConstraints gbc_lblNewLabel_21 = new GridBagConstraints();
		gbc_lblNewLabel_21.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_21.gridx = 0;
		gbc_lblNewLabel_21.gridy = 4;
		panel_mot.add(lblNewLabel_21, gbc_lblNewLabel_21);
		
		textField_V_W = new JTextField();
		textField_V_W.setEditable(false);
		GridBagConstraints gbc_textField_V_W = new GridBagConstraints();
		gbc_textField_V_W.insets = new Insets(0, 5, 5, 5);
		gbc_textField_V_W.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_V_W.gridx = 1;
		gbc_textField_V_W.gridy = 4;
		panel_mot.add(textField_V_W, gbc_textField_V_W);
		textField_V_W.setColumns(10);
		
		JLabel lblNewLabel_22 = new JLabel("I Phase U (A)");
		GridBagConstraints gbc_lblNewLabel_22 = new GridBagConstraints();
		gbc_lblNewLabel_22.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_22.gridx = 0;
		gbc_lblNewLabel_22.gridy = 5;
		panel_mot.add(lblNewLabel_22, gbc_lblNewLabel_22);
		
		textField_I_U = new JTextField();
		textField_I_U.setEditable(false);
		textField_I_U.setText("");
		GridBagConstraints gbc_textField_I_U = new GridBagConstraints();
		gbc_textField_I_U.insets = new Insets(0, 5, 5, 5);
		gbc_textField_I_U.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_I_U.gridx = 1;
		gbc_textField_I_U.gridy = 5;
		panel_mot.add(textField_I_U, gbc_textField_I_U);
		textField_I_U.setColumns(10);
		
		JLabel lblNewLabel_23 = new JLabel("I Phase V (A)");
		GridBagConstraints gbc_lblNewLabel_23 = new GridBagConstraints();
		gbc_lblNewLabel_23.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_23.gridx = 0;
		gbc_lblNewLabel_23.gridy = 6;
		panel_mot.add(lblNewLabel_23, gbc_lblNewLabel_23);
		
		textField_I_V = new JTextField();
		textField_I_V.setEditable(false);
		GridBagConstraints gbc_textField_I_V = new GridBagConstraints();
		gbc_textField_I_V.insets = new Insets(0, 5, 5, 5);
		gbc_textField_I_V.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_I_V.gridx = 1;
		gbc_textField_I_V.gridy = 6;
		panel_mot.add(textField_I_V, gbc_textField_I_V);
		textField_I_V.setColumns(10);
		
		JLabel lblNewLabel_24 = new JLabel("I Phase W (A)");
		GridBagConstraints gbc_lblNewLabel_24 = new GridBagConstraints();
		gbc_lblNewLabel_24.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_24.gridx = 0;
		gbc_lblNewLabel_24.gridy = 7;
		panel_mot.add(lblNewLabel_24, gbc_lblNewLabel_24);
		
		textField_I_W = new JTextField();
		textField_I_W.setEditable(false);
		GridBagConstraints gbc_textField_I_W = new GridBagConstraints();
		gbc_textField_I_W.insets = new Insets(0, 5, 5, 5);
		gbc_textField_I_W.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_I_W.gridx = 1;
		gbc_textField_I_W.gridy = 7;
		panel_mot.add(textField_I_W, gbc_textField_I_W);
		textField_I_W.setColumns(10);
		
		JLabel lblNewLabel_25 = new JLabel("I max cc (A)");
		GridBagConstraints gbc_lblNewLabel_25 = new GridBagConstraints();
		gbc_lblNewLabel_25.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_25.gridx = 0;
		gbc_lblNewLabel_25.gridy = 8;
		panel_mot.add(lblNewLabel_25, gbc_lblNewLabel_25);
		
		textField_I_maxCC = new JTextField();
		textField_I_maxCC.setEditable(false);
		GridBagConstraints gbc_textField_I_maxCC = new GridBagConstraints();
		gbc_textField_I_maxCC.insets = new Insets(0, 5, 5, 5);
		gbc_textField_I_maxCC.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_I_maxCC.gridx = 1;
		gbc_textField_I_maxCC.gridy = 8;
		panel_mot.add(textField_I_maxCC, gbc_textField_I_maxCC);
		textField_I_W.setColumns(10);
		
		JLabel lblNewLabel_26 = new JLabel("I max sc (A)");
		GridBagConstraints gbc_lblNewLabel_26 = new GridBagConstraints();
		gbc_lblNewLabel_26.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_26.gridx = 0;
		gbc_lblNewLabel_26.gridy = 9;
		panel_mot.add(lblNewLabel_26, gbc_lblNewLabel_26);
		
		textField_I_maxSC = new JTextField();
		textField_I_maxSC.setEditable(false);
		GridBagConstraints gbc_textField_I_maxSC = new GridBagConstraints();
		gbc_textField_I_maxSC.insets = new Insets(0, 5, 5, 5);
		gbc_textField_I_maxSC.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_I_maxSC.gridx = 1;
		gbc_textField_I_maxSC.gridy = 9;
		panel_mot.add(textField_I_maxSC, gbc_textField_I_maxSC);
		textField_I_W.setColumns(10);
		
		JLabel lblNewLabel_27 = new JLabel("n max (rpm)");
		GridBagConstraints gbc_lblNewLabel_27 = new GridBagConstraints();
		gbc_lblNewLabel_27.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_27.gridx = 0;
		gbc_lblNewLabel_27.gridy = 10;
		panel_mot.add(lblNewLabel_27, gbc_lblNewLabel_27);
		
		textField_n_max = new JTextField();
		textField_n_max.setEditable(false);
		GridBagConstraints gbc_textField_n_max = new GridBagConstraints();
		gbc_textField_n_max.insets = new Insets(0, 5, 5, 5);
		gbc_textField_n_max.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_n_max.gridx = 1;
		gbc_textField_n_max.gridy = 10;
		panel_mot.add(textField_n_max, gbc_textField_n_max);
		textField_I_W.setColumns(10);
		
		JLabel lblNewLabel_28 = new JLabel("polepairs (1)");
		GridBagConstraints gbc_lblNewLabel_28 = new GridBagConstraints();
		gbc_lblNewLabel_28.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_28.gridx = 0;
		gbc_lblNewLabel_28.gridy = 11;
		panel_mot.add(lblNewLabel_28, gbc_lblNewLabel_28);
		
		textField_polepairs = new JTextField();
		textField_polepairs.setEditable(false);
		GridBagConstraints gbc_textField_polepairs= new GridBagConstraints();
		gbc_textField_polepairs.insets = new Insets(0, 5, 5, 5);
		gbc_textField_polepairs.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_polepairs.gridx = 1;
		gbc_textField_polepairs.gridy = 11;
		panel_mot.add(textField_polepairs, gbc_textField_polepairs);
		textField_I_W.setColumns(10);
		
		JLabel lblNewLabel_29 = new JLabel("offset (rad)");
		GridBagConstraints gbc_lblNewLabel_29 = new GridBagConstraints();
		gbc_lblNewLabel_29.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_29.gridx = 0;
		gbc_lblNewLabel_29.gridy = 12;
		panel_mot.add(lblNewLabel_29, gbc_lblNewLabel_29);
		
		textField_offset = new JTextField();
		textField_offset.setEditable(false);
		GridBagConstraints gbc_textField_offset = new GridBagConstraints();
		gbc_textField_offset.insets = new Insets(0, 5, 5, 5);
		gbc_textField_offset.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_offset.gridx = 1;
		gbc_textField_offset.gridy = 12;
		panel_mot.add(textField_offset, gbc_textField_offset);
		textField_I_W.setColumns(10);
		
		JLabel lbl_MOT_img = new JLabel("");
		GridBagConstraints gbc_lbl_MOT_img = new GridBagConstraints();
		gbc_lbl_MOT_img.gridwidth = 2;
		gbc_lbl_MOT_img.insets = new Insets(0, 0, 0, 5);
		gbc_lbl_MOT_img.gridx = 0;
		gbc_lbl_MOT_img.gridy = 13;
		panel_mot.add(lbl_MOT_img, gbc_lbl_MOT_img);
		
		// load images
		try {
			BufferedImage img=ImageIO.read(new File("img\\motor.png"));
			Image dimg = img.getScaledInstance(100, 100,Image.SCALE_AREA_AVERAGING);
			ImageIcon icon = new ImageIcon(dimg);
			lbl_MOT_img.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		//register focus listener
		this.textField_cc_setId.addFocusListener(this);
		this.textField_cc_setIq.addFocusListener(this);
		this.textField_pc_setPos.addFocusListener(this);
		this.textField_sc_setOmega.addFocusListener(this);
		
		//register key listener
		textField_cc_setId.addKeyListener(this);
		this.textField_cc_setIq.addKeyListener(this);
		this.textField_pc_setPos.addKeyListener(this);
		this.textField_sc_setOmega.addKeyListener(this);
		
		
		this.disableGUI();
		
	}
	
	private void disableGUI() {
		this.textField_pc_setPos.setEnabled(false);
		this.textField_cc_setId.setEnabled(false);
		this.textField_cc_setIq.setEnabled(false);
		this.textField_sc_setOmega.setEnabled(false);
	}
	
	private void enableGUI() {
		this.textField_pc_setPos.setEnabled(true);
		this.textField_cc_setId.setEnabled(true);
		this.textField_cc_setIq.setEnabled(true);
		this.textField_sc_setOmega.setEnabled(true);
	}
	
	private void startTimer() {
		if(this.cyclicTimer==null) {
			TimerTask task = new TimerTask() {
		        public void run() {
		        	for(int tmp : cyclicVals) {
		        		pcdi.readParameter(tmp, deviceId);
		        	}
		        }
		    };
		    this.cyclicTimer=new Timer();
		    this.cyclicTimer.scheduleAtFixedRate(task, 0, 500);
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
		if(valueNr == 45 && this.txt_pc_setPos_focused == false) {
			this.textField_pc_setPos.setText(parameter.getValue().toString());
		}
		else if(valueNr == 21) {
			this.textField_pc_actPos.setText(parameter.getValue().toString());
		}
		else if(valueNr == 46) {
			this.textField_pc_P.setText(parameter.getValue().toString());
		}
		else if(valueNr == 47) {
			this.textField_pc_D.setText(parameter.getValue().toString());
		}
		else if(valueNr == 33 && this.txt_sc_setOmega_focused == false) {
			double tmp = ((float)parameter.getValue())/2.0/Math.PI*60.0;
			this.textField_sc_setOmega.setText(Double.toString(tmp));
		}
		else if(valueNr == 30) {
			double tmp = ((float)parameter.getValue())/2.0/Math.PI*60.0;
			this.textField_sc_actOmega.setText(Double.toString(tmp));
		}
		else if(valueNr == 43) {
			this.textField_sc_P.setText(parameter.getValue().toString());
		}
		else if(valueNr == 42) {
			this.textField_sc_I.setText(parameter.getValue().toString());
		}
		else if(valueNr == 27 && this.txt_cc_setIq_focused == false) {
			this.textField_cc_setIq.setText(parameter.getValue().toString());
		}
		else if(valueNr == 26 && this.txt_cc_setId_focused == false) {
			this.textField_cc_setId.setText(parameter.getValue().toString());
		}
		else if(valueNr == 29) {
			this.textField_cc_actIq.setText(parameter.getValue().toString());
		}
		else if(valueNr == 28) {
			this.textField_cc_actId.setText(parameter.getValue().toString());
		}
		else if(valueNr == 40) {
			this.textField_cc_P.setText(parameter.getValue().toString());
		}
		else if(valueNr == 39) {
			this.textField_cc_I.setText(parameter.getValue().toString());
		}
		else if(valueNr == 55) {
			this.textField_mot_VSupply.setText(parameter.getValue().toString());
		}
		else if(valueNr == 56) {
			this.textField_V_U.setText(parameter.getValue().toString());
		}
		else if(valueNr == 57) {
			this.textField_V_V.setText(parameter.getValue().toString());
		}
		else if(valueNr == 58) {
			this.textField_V_W.setText(parameter.getValue().toString());
		}
		else if(valueNr == 16) {
			this.textField_I_U.setText(parameter.getValue().toString());
		}
		else if(valueNr == 17) {
			this.textField_I_V.setText(parameter.getValue().toString());
		}
		else if(valueNr == 18) {
			this.textField_I_W.setText(parameter.getValue().toString());
		}
		else if(valueNr == 59) {
			this.textField_I_maxCC.setText(parameter.getValue().toString());
		}
		else if(valueNr == 49) {
			this.textField_I_maxSC.setText(parameter.getValue().toString());
		}
		else if(valueNr == 48) {
			double tmp = ((float)parameter.getValue())/2.0/Math.PI*60.0;
			this.textField_n_max.setText(Double.toString(tmp));
		}
		else if(valueNr == 38) {
			this.textField_polepairs.setText(parameter.getValue().toString());
		}
		else if(valueNr == 37) {
			this.textField_offset.setText(parameter.getValue().toString());
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
	    	 // set Iq
	    	 if(e.getSource() == this.textField_cc_setIq) {
	    		if(!this.textField_cc_setIq.getText().isEmpty()) {
		 			PCDI_Parameter<?> tmp;
					tmp = new PCDI_ParameterFloat(27, PCDI_TYPES.FLOAT, 0.0f);
					tmp.setValue(this.textField_cc_setIq.getText());
					this.pcdi.writeParameter(tmp, this.deviceId);
	    		}
	    	 }
	    	 // set Id
	    	 else if(e.getSource() == this.textField_cc_setId) {
	    		if(!this.textField_cc_setId.getText().isEmpty()) {
		 			PCDI_Parameter<?> tmp;
					tmp = new PCDI_ParameterFloat(26, PCDI_TYPES.FLOAT, 0.0f);
					tmp.setValue(this.textField_cc_setId.getText());
					this.pcdi.writeParameter(tmp, this.deviceId);
	    		}
	    	 }
	    	 // set Omega
	    	 else if(e.getSource() == this.textField_sc_setOmega) {
	    		if(!this.textField_sc_setOmega.getText().isEmpty()) {
		 			PCDI_Parameter<?> tmp;
					tmp = new PCDI_ParameterFloat(33, PCDI_TYPES.FLOAT, 0.0f);
					double res = Double.valueOf(this.textField_sc_setOmega.getText())*2.0*Math.PI/60.0;
					tmp.setValue(Double.toString(res));
					this.pcdi.writeParameter(tmp, this.deviceId);
	    		}
	    	 }
	    	 // set Omega
	    	 else if(e.getSource() == this.textField_pc_setPos) {
	    		if(!this.textField_pc_setPos.getText().isEmpty()) {
		 			PCDI_Parameter<?> tmp;
					tmp = new PCDI_ParameterFloat(45, PCDI_TYPES.FLOAT, 0.0f);
					tmp.setValue(this.textField_pc_setPos.getText());
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
		if(e.getSource() == this.textField_cc_setId) {
			this.txt_cc_setId_focused = true;
		}
		else if (e.getSource() == this.textField_cc_setIq) {
			this.txt_cc_setIq_focused = true;
		}
		else if (e.getSource() == this.textField_pc_setPos) {
			this.txt_pc_setPos_focused = true;
		}
		else if (e.getSource() == this.textField_sc_setOmega) {
			this.txt_sc_setOmega_focused = true;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == this.textField_cc_setId) {
			this.txt_cc_setId_focused = false;
		}
		else if (e.getSource() == this.textField_cc_setIq) {
			this.txt_cc_setIq_focused = false;
		}
		else if (e.getSource() == this.textField_pc_setPos) {
			this.txt_pc_setPos_focused = false;
		}
		else if (e.getSource() == this.textField_sc_setOmega) {
			this.txt_sc_setOmega_focused = false;
		}
	}

}
