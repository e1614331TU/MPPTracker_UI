package base;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
import comm.PCDI_ParameterInt;
import comm.PCDI_TYPES;
import comm.PCDI_UNIT;
import java.awt.GridBagLayout;
import javax.swing.JScrollBar;
/**
* JPanelParams
* <p>
* this is the class which handles the Params Panel. 
* It contains a list of all Parameters of the controller. They are displayed and if
* possible the user can adapt them. 
*/
public class JPanelParams extends JPanel implements ActionListener, I_PCDI_Listener, I_Tab_Listener {

	private static final long serialVersionUID = 1L;
	private JTextArea infoConsole;
	private int deviceId;
	private I_PCDI pcdi;

	private PCDI_Parameter<?> paramNr00 = new PCDI_ParameterInt(19, PCDI_TYPES.INT, (short) 0);
	private JButton btnReadAll;
	private JScrollPane scrollPanel;
	private JPanel paramPanel;
	
	private int actualxvalue = 0;
	private List<JPanelSingleParam> paramList = new ArrayList<JPanelSingleParam>();
	private List<PCDI_ParameterInfo> paramInfos = new ArrayList<PCDI_ParameterInfo>();
	/**
	* this method is the constructor
	* @param pcdi is needed for communication
	* @param deviceId is needed for communication with the controller for ability to communicate with the right controller 
	* @param infoConsole is needed for giving user information
	*/
	public JPanelParams(I_PCDI pcdi, int deviceId, JTextArea infoConsole) {
		super();
		this.pcdi = pcdi;
		this.pcdi.registerListener(this);

		this.deviceId = deviceId;
		this.infoConsole = infoConsole;

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		setLayout(gridBagLayout);

		btnReadAll = new JButton("Read All");
		GridBagConstraints gbc_btnReadAll = new GridBagConstraints();
		gbc_btnReadAll.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnReadAll.insets = new Insets(0, 0, 0, 5);
		gbc_btnReadAll.gridx = 0;
		gbc_btnReadAll.gridy = 2;
		add(btnReadAll, gbc_btnReadAll);
		this.btnReadAll.addActionListener(this);

		// Paramlist
		// the panel inside the scrollpanel
	    paramPanel = new JPanel();
	    paramPanel.setLayout(new GridBagLayout());
	    paramPanel.setSize(new Dimension(MainWindow.width, MainWindow.height)); // whatever

		scrollPanel = new JScrollPane();
		scrollPanel.setPreferredSize(new Dimension(MainWindow.width, MainWindow.height));
		scrollPanel.setMinimumSize(new Dimension(MainWindow.width, 500));
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		GridBagConstraints gbc_scrollConsole = new GridBagConstraints();
		gbc_scrollConsole.insets = new Insets(0, 0, 5, 5);
		gbc_scrollConsole.fill = GridBagConstraints.BOTH;
		gbc_scrollConsole.gridx = 0;
		gbc_scrollConsole.gridy = 0;
		gbc_scrollConsole.weighty=1.0;
		gbc_scrollConsole.gridwidth=5;
		this.add(scrollPanel, gbc_scrollConsole);


	}

	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		this.paramList.forEach(param -> {
			param.notifyParameterWrite(parameter, deviceId);
		});
	}

	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {
	}

	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		this.paramList.forEach(param -> {
			param.notifyParameterRead(parameter, deviceId);
		});
	}

	@Override
	public void notifyParameterInfo(PCDI_ParameterInfo parameter, int deviceId) {
		if (this.deviceId != deviceId) {
			return;
		}
		this.paramInfos.add(parameter);
		JPanelSingleParam singleParam = new JPanelSingleParam(parameter, deviceId, pcdi, infoConsole);
		this.paramList.add(singleParam);
		singleParam.setPreferredSize(new Dimension(500,30));
		 GridBagConstraints constraint = new GridBagConstraints();
		    constraint.anchor = GridBagConstraints.CENTER;
		    constraint.fill = GridBagConstraints.NONE;
		    constraint.gridx = this.actualxvalue;
		    constraint.gridy = GridBagConstraints.RELATIVE;
		    constraint.weightx = 1.0f;
		    constraint.weighty = 1.0f;
		if (this.actualxvalue==1) {
			this.actualxvalue = 0;
		}
		else {
			this.actualxvalue = 1;
		}
		
		this.paramPanel.add(singleParam, constraint);
		
	    this.scrollPanel.setViewportView(paramPanel);
	    this.add(scrollPanel); // or other panel etc.
	    scrollPanel.updateUI();
		
	}

	@Override
	public void notifyCommandInvalid(PCDI_COMMANDS command, int deviceId) {
	}

	@Override
	public void notifyParameterInvalid(int valueNr, int deviceId) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.btnReadAll) {
			this.paramInfos.forEach(param -> {
				this.pcdi.readParameter(param.getValNr(), deviceId);
			});
		}

	}

	@Override
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId) {
	}

	@Override
	public void connectionChanged(boolean connected) {
		if (connected == false) {
			this.paramList.forEach(param -> {
				this.remove(param);
				this.paramPanel.remove(param);
			});
			this.actualxvalue = 0;
		}
	}

	@Override
	public void parameterUpdated(List<PCDI_Parameter<?>> params) {
	}

	@Override
	public void parameterInfoUpdated(List<PCDI_ParameterInfo> paramInfos) {
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

}
