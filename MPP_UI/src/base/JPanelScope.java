package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.knowm.xchart.CSVExporter;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.None;

import comm.I_PCDI;
import comm.I_PCDI_Listener;
import comm.PCDI_COMMANDS;
import comm.PCDI_ERROR_TYPE;
import comm.PCDI_OSCILLOSCOPE_COMMAND;
import comm.PCDI_OSCILLOSCOPE_STATE;
import comm.PCDI_OscilloscopeData;
import comm.PCDI_PERMISSION;
import comm.PCDI_Parameter;
import comm.PCDI_ParameterInfo;
import comm.PCDI_ParameterInt;
import comm.PCDI_TYPES;
import javax.swing.JCheckBox;
/**
* JPanelAdvanced
* <p>
* this is the class which handles the Sope Panel. 
* It contains a scope which is able to display up to 4 user specified parameters over time (in a scope)
*/
public class JPanelScope extends JPanel implements ActionListener, I_PCDI_Listener, I_Tab_Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6322559045161874867L;

	private PCDI_Parameter<Float> paramDrpdwn1=null;
	private PCDI_ParameterInfo paramInfoDrpdwn1=null;
	private PCDI_Parameter<Float> paramDrpdwn2=null;
	private PCDI_ParameterInfo paramInfoDrpdwn2=null;
	private PCDI_Parameter<Float> paramDrpdwn3=null;
	private PCDI_ParameterInfo paramInfoDrpdwn3=null;
	private PCDI_Parameter<Float> paramDrpdwn4=null;
	private PCDI_ParameterInfo paramInfoDrpdwn4=null;
	
	private GridBagConstraints c_8;
	private JButton buttonRefresh;
	private JButton buttonStartStopScope;
	private GridBagLayout layoutScope = new GridBagLayout();
	private JComboBox<String> box_drpValue1;
	private JComboBox<String> box_drpValue2;
	private JComboBox<String> box_drpValue3;
	private JComboBox<String> box_drpValue4;
	private JTextField txt_sampleRate;
	private JTextField txt_sampleDepth;
	private JTextField txtValue00;
	private JButton buttonSetValue00toZero;
	private JButton buttonReadValue00;
	private JButton buttonSetEnableDisableCC;
	private JButton buttonExportCSV;
	
	private String param00description="CC_Ref";
	private PCDI_Parameter<?> paramNr00=new PCDI_ParameterInt(20, PCDI_TYPES.INT,(short) 0);
	
	private PCDI_Parameter<?> parameterCCEnabled=new PCDI_ParameterInt(19, PCDI_TYPES.INT,(short) 0);
	private boolean isCControllerEnabled=false;
	private boolean isPControllerEnabled=false;
	private PCDI_Parameter<?> parameterPCEnabled=new PCDI_ParameterInt(44, PCDI_TYPES.INT,(short) 0);
	private boolean isSControllerEnabled=false;
	private PCDI_Parameter<?> parameterSCEnabled=new PCDI_ParameterInt(32, PCDI_TYPES.INT,(short) 0);
	private JButton buttonSetEnableDisablePC;
	private JButton buttonSetEnableDisableSC;
	
	private I_PCDI pcdi;
	
	private Timer scopeTimer;
	private boolean scoperunning=false;
	private int deviceId;
	private final XYChart chart;
	private JPanel chartPanel;
	private List<Double> scopeDatay1=new ArrayList<Double>();
	private List<Double> scopeDatax1=new ArrayList<Double>();
	private List<Double> scopeDatay2=new ArrayList<Double>();
	private List<Double> scopeDatax2=new ArrayList<Double>();
	private List<Double> scopeDatay3=new ArrayList<Double>();
	private List<Double> scopeDatax3=new ArrayList<Double>();
	private List<Double> scopeDatay4=new ArrayList<Double>();
	private List<Double> scopeDatax4=new ArrayList<Double>();
	private int sampleDepth=100;
	private double sampleRateMs=10.0;
	private XYSeries XYSeries1;
	private XYSeries XYSeries2;
	private XYSeries XYSeries3;
	private XYSeries XYSeries4;
	private double maxY1;
	private double minY1;
	private double maxY2;
	private double minY2;
	private double maxY3;
	private double minY3;
	private double maxY4;
	private double minY4;
	private Color color1=Color.black;
	private Color color2=Color.blue;
	private Color color3=Color.magenta;
	private Color color4=Color.cyan;
	
	private JTextArea infoConsole;

	private boolean isControllerEnabled=false;
	private boolean connected;
	private boolean isActive;
	
	private List<PCDI_Parameter<?>> params=new ArrayList<PCDI_Parameter<?>>();
	private List<PCDI_ParameterInfo> paramInfos=new ArrayList<PCDI_ParameterInfo>();
	private GridBagConstraints gbc_lbl_drpdwn_value_editable_1;
	private JButton buttonSetValue00toOne;
	
	//variable um zu erkennen welches Scope aktuell l�uft (ist die abtastzeit hoch (>10ms/Parameter), 
	//so werden die Parameter zyklisch ausgelesen, ist die abtastzeit geringer, so wird die Scope-Implementierung des Protokolls verwendet
	private SCOPE_TYPE scopeType=SCOPE_TYPE.CYCLE_READ;
	private int oversampling=1;
	private int valNr1=1;
	private int valNr2=1;
	private int valNr3=1;
	private int valNr4=1;
	private GridBagConstraints gbc_box_drpValue_1;
	private GridBagConstraints gbc_box_drpValue_2;
	private GridBagConstraints gbc_box_drpValue_3;
	private JCheckBox chckbxSingleShot;
	private JButton btnCCStepResponse;
	private JButton btnSCStepResponse;
	private JButton btnPCStepResponse;
	
	/**
	* this method is the constructor
	* @param pcdi is needed for communication
	* @param deviceId is needed for communication with the controller for ability to communicate with the right controller 
	* @param infoConsole is needed for giving user information
	*/
	public JPanelScope(I_PCDI pcdi, int deviceId, JTextArea infoConsole) {
		super();
		this.infoConsole=infoConsole;
		this.deviceId=deviceId;
		this.pcdi=pcdi;
		this.pcdi.registerListener(this);
		
		//set size
		this.setPreferredSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		this.setMinimumSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		
		layoutScope.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		layoutScope.columnWidths = new int[] {0, 50, 0, 30, 100, 0, 0};
		this.setLayout(layoutScope);
		
		//Labels
		JLabel labelValue03 = new JLabel("Scope Parameter1:");
		GridBagConstraints c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(0, 5, 5, 5);  //top padding
		c_5.weightx = 0.0;
		c_5.gridx = 0;
		c_5.gridy = 1;
		this.add(labelValue03, c_5);
		
		this.btnCCStepResponse = new JButton("CCStepResponse");
		this.btnCCStepResponse.addActionListener(this);
		GridBagConstraints gbc_btnCCStepResponse = new GridBagConstraints();
		gbc_btnCCStepResponse.insets = new Insets(0, 0, 5, 5);
		gbc_btnCCStepResponse.gridx = 5;
		gbc_btnCCStepResponse.gridy = 1;
		add(btnCCStepResponse, gbc_btnCCStepResponse);
		
		JLabel labelValue04 = new JLabel("Scope Parameter2:");
		c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(0, 5, 5, 5);  //top padding
		//c.fill = GridBagConstraints.HORIZONTAL;
		c_5.weightx = 0.0;
		c_5.gridx = 0;
		c_5.gridy = 2;
		this.add(labelValue04, c_5);
		
		labelValue04 = new JLabel("Scope Parameter3:");
		c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(0, 5, 5, 5);  //top padding
		//c.fill = GridBagConstraints.HORIZONTAL;
		c_5.weightx = 0.0;
		c_5.gridx = 3;
		c_5.gridy = 1;
		this.add(labelValue04, c_5);
		
		labelValue04 = new JLabel("Scope Parameter4:");
		c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(0, 5, 5, 5);  //top padding
		//c.fill = GridBagConstraints.HORIZONTAL;
		c_5.weightx = 0.0;
		c_5.gridx = 3;
		c_5.gridy = 2;
		this.add(labelValue04, c_5);
		
		 //Dropdown to select scope value:
	    box_drpValue1 = new JComboBox<String>();
		box_drpValue1.addActionListener(this);
		box_drpValue1.setMaximumSize(new Dimension(200, 30));
		box_drpValue1.setPreferredSize(new Dimension(200, 30));
		box_drpValue1.setMinimumSize(new Dimension(200, 30));
		GridBagConstraints gbc_box_drpValue = new GridBagConstraints();
		gbc_box_drpValue.anchor = GridBagConstraints.WEST;
		gbc_box_drpValue.insets = new Insets(0, 5, 5, 5);
		gbc_box_drpValue.gridx = 1;
		gbc_box_drpValue.gridy = 1;
		gbc_box_drpValue.weightx=0.3;
		gbc_box_drpValue.gridwidth=2;
		box_drpValue1.addItem(null);
		this.add(box_drpValue1, gbc_box_drpValue);
		
		box_drpValue2 = new JComboBox<String>();
		box_drpValue2.addActionListener(this);
		box_drpValue2.setMaximumSize(new Dimension(200, 30));
		box_drpValue2.setPreferredSize(new Dimension(200, 30));
		box_drpValue2.setMinimumSize(new Dimension(200, 30));
		gbc_box_drpValue_1 = new GridBagConstraints();
		gbc_box_drpValue_1.anchor = GridBagConstraints.WEST;
		gbc_box_drpValue_1.insets = new Insets(0, 5, 5, 5);
		gbc_box_drpValue_1.gridx = 1;
		gbc_box_drpValue_1.gridy = 2;
		gbc_box_drpValue_1.weightx=0.3;
		gbc_box_drpValue_1.gridwidth=2;
		box_drpValue2.addItem(null);
		this.add(box_drpValue2, gbc_box_drpValue_1);
		
		box_drpValue3 = new JComboBox<String>();
		box_drpValue3.addActionListener(this);
		box_drpValue3.setMaximumSize(new Dimension(200, 30));
		box_drpValue3.setPreferredSize(new Dimension(200, 30));
		box_drpValue3.setMinimumSize(new Dimension(200, 30));
		gbc_box_drpValue_2 = new GridBagConstraints();
		gbc_box_drpValue_2.anchor = GridBagConstraints.WEST;
		gbc_box_drpValue_2.insets = new Insets(0, 5, 5, 5);
		gbc_box_drpValue_2.gridx = 4;
		gbc_box_drpValue_2.gridy = 1;
		gbc_box_drpValue_2.weightx=0.3;
		box_drpValue3.addItem(null);
		this.add(box_drpValue3, gbc_box_drpValue_2);
		
		box_drpValue4 = new JComboBox<String>();
		box_drpValue4.addActionListener(this);
		box_drpValue4.setMaximumSize(new Dimension(200, 30));
		box_drpValue4.setPreferredSize(new Dimension(200, 30));
		box_drpValue4.setMinimumSize(new Dimension(200, 30));
		gbc_box_drpValue_3 = new GridBagConstraints();
		gbc_box_drpValue_3.anchor = GridBagConstraints.WEST;
		gbc_box_drpValue_3.insets = new Insets(0, 5, 5, 5);
		gbc_box_drpValue_3.gridx = 4;
		gbc_box_drpValue_3.gridy = 2;
		gbc_box_drpValue_3.weightx=0.3;
		box_drpValue4.addItem(null);
		this.add(box_drpValue4, gbc_box_drpValue_3);
		
		this.buttonStartStopScope = new JButton("StartScope");  
		this.buttonStartStopScope.addActionListener(this);
		c_8 = new GridBagConstraints();
		c_8.gridwidth = 1;
		c_8.insets = new Insets(0, 5, 5, 0);  //top padding
		c_8.fill = GridBagConstraints.HORIZONTAL;
		c_8.weightx = 0.1;
		c_8.gridx = 6;
		c_8.gridy = 1;
		this.add(this.buttonStartStopScope,c_8);
		
		this.btnSCStepResponse = new JButton("SCStepResponse");
		this.btnSCStepResponse.addActionListener(this);
		GridBagConstraints gbc_btnSCStepResponse = new GridBagConstraints();
		gbc_btnSCStepResponse.insets = new Insets(0, 0, 5, 5);
		gbc_btnSCStepResponse.gridx = 5;
		gbc_btnSCStepResponse.gridy = 2;
		add(btnSCStepResponse, gbc_btnSCStepResponse);
		
		this.chckbxSingleShot = new JCheckBox("SingleShot");
		GridBagConstraints gbc_chckbxSingleShot = new GridBagConstraints();
		gbc_chckbxSingleShot.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxSingleShot.gridx = 6;
		gbc_chckbxSingleShot.gridy = 2;
		add(chckbxSingleShot, gbc_chckbxSingleShot);
		this.chckbxSingleShot.addActionListener(this);
				
		//Sample Time:
		labelValue04 = new JLabel("Sample Rate/ms:");
		c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(10, 4, 0, 5);  //top padding
		c_5.weightx = 0.0;
		c_5.gridx = 0;
		c_5.gridy = 3;
		this.add(labelValue04, c_5);
		txt_sampleRate = new JTextField("80");
		txt_sampleRate.setMaximumSize(new Dimension(60, 20));
		txt_sampleRate.setPreferredSize(new Dimension(60, 20));
		txt_sampleRate.setMinimumSize(new Dimension(60, 20));
		GridBagConstraints gbc_lbl_drpdwn_value_editable = new GridBagConstraints();
		gbc_lbl_drpdwn_value_editable.insets = new Insets(10, 4, 0, 5);
		gbc_lbl_drpdwn_value_editable.gridx = 1;
		gbc_lbl_drpdwn_value_editable.gridy = 3;
		this.add(txt_sampleRate, gbc_lbl_drpdwn_value_editable);
		txt_sampleRate.setColumns(10);
		
		labelValue04 = new JLabel("Sample Depth (if sample-rate<10ms/Parameter then max 512):");
		c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(10, 4, 0, 5);  //top padding
		c_5.weightx = 0.0;
		c_5.gridx = 2;
		c_5.gridy = 3;
		this.add(labelValue04, c_5);
		txt_sampleDepth = new JTextField("512");
		txt_sampleDepth.setMaximumSize(new Dimension(60, 20));
		txt_sampleDepth.setPreferredSize(new Dimension(60, 20));
		txt_sampleDepth.setMinimumSize(new Dimension(60, 20));
		gbc_lbl_drpdwn_value_editable_1 = new GridBagConstraints();
		gbc_lbl_drpdwn_value_editable_1.anchor = GridBagConstraints.WEST;
		gbc_lbl_drpdwn_value_editable_1.gridwidth = 2;
		gbc_lbl_drpdwn_value_editable_1.insets = new Insets(10, 4, 0, 5);
		gbc_lbl_drpdwn_value_editable_1.gridx = 3;
		gbc_lbl_drpdwn_value_editable_1.gridy = 3;
		this.add(txt_sampleDepth, gbc_lbl_drpdwn_value_editable_1);
		txt_sampleDepth.setColumns(10);
		

		//ExportCSV
		this.buttonExportCSV = new JButton("Export data to CSV");  
		this.buttonExportCSV.addActionListener(this);
		
		this.btnPCStepResponse = new JButton("PCStepResponse");
		this.btnPCStepResponse.addActionListener(this);
		GridBagConstraints gbc_btnPCStepResponse = new GridBagConstraints();
		gbc_btnPCStepResponse.insets = new Insets(10, 0, 0, 5);
		gbc_btnPCStepResponse.gridx = 5;
		gbc_btnPCStepResponse.gridy = 3;
		add(btnPCStepResponse, gbc_btnPCStepResponse);
		c_8 = new GridBagConstraints();
		c_8.gridwidth = 1;
		c_8.insets = new Insets(10, 4, 0, 0);  //top padding
		c_8.fill = GridBagConstraints.HORIZONTAL;
		c_8.weightx = 0.1;
		c_8.gridx = 6;
		c_8.gridy = 3;
		this.add(this.buttonExportCSV,c_8);
		
		//CHART
		// Create Chart
		this.chart = new XYChartBuilder().width(MainWindow.width).height(500).theme(ChartTheme.Matlab).title("").xAxisTitle("t/ms").yAxisTitle("Y").build();

		// Customize Chart
		chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		
		
		// Series
		chart.getStyler().setYAxisGroupTitleColor(0,color1);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(0, color1);
		chart.getStyler().setYAxisGroupTitleColor(1, color2);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(1, color2);
		chart.getStyler().setYAxisGroupTitleColor(2, color3);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(2, color3);
		chart.getStyler().setYAxisGroupTitleColor(3, color4);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(3, color4);
		
		// chart
	    chartPanel = new XChartPanel<XYChart>(chart);
	    chartPanel.setPreferredSize(new Dimension(MainWindow.width, 500));
	    chartPanel.setMinimumSize(new Dimension(MainWindow.width, 350));
	    c_8 = new GridBagConstraints();
		c_8.gridwidth = 7;
		c_8.insets = new Insets(0, 4, 5, 0);  //top padding
		c_8.fill = GridBagConstraints.BOTH;
		c_8.weightx = 0.1;
		c_8.weighty = 1;
		c_8.gridx = 0;
		c_8.gridy = 0;
	    this.add(chartPanel, c_8);
	    

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.buttonExportCSV) {
			this.infoConsole.append("export data\n");
			 JFileChooser fileChooser = new JFileChooser();
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int option = fileChooser.showSaveDialog(this);
	            if(option == JFileChooser.APPROVE_OPTION){
	               File file = fileChooser.getSelectedFile();
	               String path=file.getAbsolutePath()+"\\";
	               this.infoConsole.append("Folder Selected: " + path +"\n");
	               CSVExporter.writeCSVColumns(this.chart, path);
	            }else{
	            	this.infoConsole.append("Open command cancelled\n");
	            }
		}
		else if(e.getSource() == this.buttonStartStopScope) {
			if(this.paramDrpdwn1==null) { 
				this.infoConsole.append("No Parameter selected!\n");
				return;
			}
			if(this.txt_sampleRate.getText().isEmpty() ) {
				this.infoConsole.append("No Samplerate selected!\n");
				return;
			}
			if(!this.scoperunning) {
				this.startScope(PCDI_OSCILLOSCOPE_COMMAND.START_ONLY);
			}
			else {
				this.stopScope();
			}
		}
		else if(e.getSource() == this.btnCCStepResponse) {
			if(this.paramDrpdwn1==null) { 
				this.infoConsole.append("No Parameter selected!\n");
				return;
			}
			if(this.txt_sampleRate.getText().isEmpty() ) {
				this.infoConsole.append("No Samplerate selected!\n");
				return;
			}
			if(!this.scoperunning) {
				this.startScope(PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_CC);
			}
			else {
				this.stopScope();
			}
		}
		else if(e.getSource() == this.btnSCStepResponse) {
			if(this.paramDrpdwn1==null) { 
				this.infoConsole.append("No Parameter selected!\n");
				return;
			}
			if(this.txt_sampleRate.getText().isEmpty() ) {
				this.infoConsole.append("No Samplerate selected!\n");
				return;
			}
			if(!this.scoperunning) {
				this.startScope(PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_SC);
			}
			else {
				this.stopScope();
			}
		}
		else if(e.getSource() == this.btnPCStepResponse) {
			if(this.paramDrpdwn1==null) { 
				this.infoConsole.append("No Parameter selected!\n");
				return;
			}
			if(this.txt_sampleRate.getText().isEmpty() ) {
				this.infoConsole.append("No Samplerate selected!\n");
				return;
			}
			if(!this.scoperunning) {
				this.startScope(PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_PC);
			}
			else {
				this.stopScope();
			}
		}
		else if(e.getSource() == this.box_drpValue1) {
			if(this.box_drpValue1.getSelectedItem()==null){
				if(this.paramInfoDrpdwn1!=null) {
					this.chart.removeSeries(this.paramInfoDrpdwn1.getName());
				}
				this.paramDrpdwn1=null;
				this.paramInfoDrpdwn1=null;
			}
			if(this.paramInfoDrpdwn1!=null && this.box_drpValue1.getSelectedItem()!=null) {
				if(this.box_drpValue1.getSelectedItem().equals(this.paramInfoDrpdwn1.getName()))
					return;
			}
			if(this.box_drpValue1.getSelectedItem()!=null && alreadyInsertedInChart(this.box_drpValue1.getSelectedItem().toString())) {
				this.box_drpValue1.setSelectedIndex(0);
				return;
			}
			if(this.box_drpValue1.getSelectedItem()!=null) {
				for(PCDI_ParameterInfo parI : this.paramInfos) {
					if(parI.getName().equals(this.box_drpValue1.getSelectedItem())) {
						for(PCDI_Parameter par : this.params) {
							if(par.getValueNumber()==parI.getValNr()) {
								this.paramDrpdwn1=par;
								if(this.paramInfoDrpdwn1!=null) {
									this.chart.removeSeries(this.paramInfoDrpdwn1.getName());
								}
								this.paramInfoDrpdwn1=parI;
								this.updateCharGUI(0);
							}
						}
						
						
					}
				}
			}
			
		}
		else if(e.getSource() == this.box_drpValue2) {
			if(this.box_drpValue2.getSelectedItem()==null){
				if(this.paramInfoDrpdwn2!=null) {
					this.chart.removeSeries(this.paramInfoDrpdwn2.getName());
				}
				this.paramDrpdwn2=null;
				this.paramInfoDrpdwn2=null;
			}
			if(this.paramInfoDrpdwn2!=null && this.box_drpValue2.getSelectedItem()!=null) {
				if(this.box_drpValue2.getSelectedItem().equals(this.paramInfoDrpdwn2.getName()))
					return;
			}
			if(this.box_drpValue2.getSelectedItem()!=null && alreadyInsertedInChart(this.box_drpValue2.getSelectedItem().toString())) {
				this.box_drpValue2.setSelectedIndex(0);
				return;
			}
			if(this.box_drpValue2.getSelectedItem()!=null) {
				for(PCDI_ParameterInfo parI : this.paramInfos) {
					if(parI.getName().equals(this.box_drpValue2.getSelectedItem())) {
						for(PCDI_Parameter par : this.params) {
							if(par.getValueNumber()==parI.getValNr()) {
								this.paramDrpdwn2=par;
								if(this.paramInfoDrpdwn2!=null) {
									this.chart.removeSeries(this.paramInfoDrpdwn2.getName());
								}
								this.paramInfoDrpdwn2=parI;
								this.updateCharGUI(1);
							}
						}
						
						
					}
				}
			}
		}
		else if(e.getSource() == this.box_drpValue3) {
			if(this.box_drpValue3.getSelectedItem()==null){
				if(this.paramInfoDrpdwn3!=null) {
					this.chart.removeSeries(this.paramInfoDrpdwn3.getName());
				}
				this.paramDrpdwn3=null;
				this.paramInfoDrpdwn3=null;
			}
			if(this.paramInfoDrpdwn3!=null && this.box_drpValue3.getSelectedItem()!=null) {
				if(this.box_drpValue3.getSelectedItem().equals(this.paramInfoDrpdwn3.getName()))
					return;
			}
			if(this.box_drpValue3.getSelectedItem()!=null && alreadyInsertedInChart(this.box_drpValue3.getSelectedItem().toString())) {
				this.box_drpValue3.setSelectedIndex(0);
				return;
			}
			if(this.box_drpValue3.getSelectedItem()!=null) {
				for(PCDI_ParameterInfo parI : this.paramInfos) {
					if(parI.getName().equals(this.box_drpValue3.getSelectedItem())) {
						for(PCDI_Parameter par : this.params) {
							if(par.getValueNumber()==parI.getValNr()) {
								this.paramDrpdwn3=par;
								if(this.paramInfoDrpdwn3!=null) {
									this.chart.removeSeries(this.paramInfoDrpdwn3.getName());
								}
								this.paramInfoDrpdwn3=parI;
								this.updateCharGUI(2);
							}
						}
						
						
					}
				}
			}
		}
		else if(e.getSource() == this.box_drpValue4) {
			if(this.box_drpValue4.getSelectedItem()==null){
				if(this.paramInfoDrpdwn4!=null) {
					this.chart.removeSeries(this.paramInfoDrpdwn4.getName());
				}
				this.paramDrpdwn4=null;
				this.paramInfoDrpdwn4=null;
			}
			if(this.paramInfoDrpdwn4!=null && this.box_drpValue4.getSelectedItem()!=null) {
				if(this.box_drpValue4.getSelectedItem().equals(this.paramInfoDrpdwn4.getName()))
					return;
			}
			if(this.box_drpValue4.getSelectedItem()!=null && alreadyInsertedInChart(this.box_drpValue4.getSelectedItem().toString())) {
				this.box_drpValue4.setSelectedIndex(0);
				return;
			}
			if(this.box_drpValue4.getSelectedItem()!=null) {
				for(PCDI_ParameterInfo parI : this.paramInfos) {
					if(parI.getName().equals(this.box_drpValue4.getSelectedItem())) {
						for(PCDI_Parameter par : this.params) {
							if(par.getValueNumber()==parI.getValNr()) {
								this.paramDrpdwn4=par;
								if(this.paramInfoDrpdwn4!=null) {
									this.chart.removeSeries(this.paramInfoDrpdwn4.getName());
								}
								this.paramInfoDrpdwn4=parI;
								this.updateCharGUI(3);
							}
						}
						
						
					}
				}
			}
			
		}
	}
	private void updateCharGUI(int yaxis) {
		if(yaxis==0) {
			if(paramInfoDrpdwn1!=null) {
				scopeDatax1.clear();
				scopeDatay1.clear();
				infoConsole.append("Parameter "+paramInfoDrpdwn1.getName()+" set!\n");
				XYSeries1=this.chart.addSeries(paramInfoDrpdwn1.getName(),new double[] {0.0}, new double[] {0.0},null);
				XYSeries1.setYAxisGroup(yaxis);
				XYSeries1.setMarker(new None());
				XYSeries1.setLineColor(color1);
				this.chart.setYAxisGroupTitle(yaxis, paramInfoDrpdwn1.getUnit().name());
				maxY1=Double.NaN;
				minY1=Double.NaN;
			}
		}
		else if(yaxis==1) {
			if(paramInfoDrpdwn2!=null) {
				scopeDatax2.clear();
				scopeDatay2.clear();
				infoConsole.append("Parameter "+paramInfoDrpdwn2.getName()+" set!\n");
				XYSeries2=this.chart.addSeries(paramInfoDrpdwn2.getName(),new double[] {0.0}, new double[] {0.0},null);
				XYSeries2.setYAxisGroup(yaxis);
				XYSeries2.setMarker(new None());
				XYSeries2.setLineColor(color2);
				this.chart.setYAxisGroupTitle(yaxis, paramInfoDrpdwn2.getUnit().name());
				maxY2=Double.NaN;
				minY2=Double.NaN;
			}
		}
		else if(yaxis==2) {
			if(paramInfoDrpdwn3!=null) {
				scopeDatax3.clear();
				scopeDatay3.clear();
				infoConsole.append("Parameter "+paramInfoDrpdwn3.getName()+" set!\n");
				XYSeries3=this.chart.addSeries(paramInfoDrpdwn3.getName(),new double[] {0.0}, new double[] {0.0},null);
				XYSeries3.setYAxisGroup(yaxis);
				XYSeries3.setMarker(new None());
				XYSeries3.setLineColor(color3);
				this.chart.setYAxisGroupTitle(yaxis, paramInfoDrpdwn3.getUnit().name());
				maxY3=Double.NaN;
				minY3=Double.NaN;
			}
		}
		else if(yaxis==3) {
			if(paramInfoDrpdwn4!=null) {
				scopeDatax4.clear();
				scopeDatay4.clear();
				infoConsole.append("Parameter "+paramInfoDrpdwn4.getName()+" set!\n");
				XYSeries4=this.chart.addSeries(paramInfoDrpdwn4.getName(),new double[] {0.0}, new double[] {0.0},null);
				XYSeries4.setYAxisGroup(yaxis);
				XYSeries4.setMarker(new None());
				XYSeries4.setLineColor(color4);
				this.chart.setYAxisGroupTitle(yaxis, paramInfoDrpdwn4.getUnit().name());
				maxY4=Double.NaN;
				minY4=Double.NaN;
			}
		}
		
	}
	
	private void startScope(PCDI_OSCILLOSCOPE_COMMAND osci_cmd) {	
		
	    this.sampleRateMs=Double.parseDouble(this.txt_sampleRate.getText());
	    this.sampleDepth=Integer.parseInt(this.txt_sampleDepth.getText());
	    this.box_drpValue1.setEnabled(false);
		this.box_drpValue2.setEnabled(false);
		this.box_drpValue3.setEnabled(false);
		this.box_drpValue4.setEnabled(false);
		this.txt_sampleRate.setEnabled(false);
		this.txt_sampleDepth.setEnabled(false);
		int numberOfScopeParams=getNumberOfScopeParams();
		if(numberOfScopeParams*10>this.sampleRateMs) {
			this.scopeType=SCOPE_TYPE.FAST_SCOPE;
			if(this.sampleDepth>512) {
				this.sampleDepth=512;
				this.txt_sampleDepth.setText("512");
				this.infoConsole.append("Sample Depth set to 512!\n");
			}
			this.oversampling=(int)(1.0/0.1*this.sampleRateMs);
	    	this.infoConsole.append("Used fast scope!\n");
	    	this.valNr1=paramDrpdwn1.getValueNumber();
	    	this.valNr2=1;
	    	this.valNr3=1;
	    	this.valNr4=1;
	    	if(paramDrpdwn2!=null) {
	    		this.valNr2=paramDrpdwn2.getValueNumber();
	    	}
        	if(paramDrpdwn3!=null) {
        		this.valNr3=paramDrpdwn3.getValueNumber();
        	}
        	if(paramDrpdwn4!=null) {
        		this.valNr4=paramDrpdwn4.getValueNumber();
        	}
	    	this.pcdi.startOscilloscope(osci_cmd, this.valNr1, this.valNr2, this.valNr3, this.valNr4, 1, 1, 1, this.oversampling, this.deviceId);
	    	
		}
		else {
			this.scopeType=SCOPE_TYPE.CYCLE_READ;
			this.infoConsole.append("Used cycle scope!\n");
			if(osci_cmd==PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_CC) {
				PCDI_Parameter<?> tmp =  new PCDI_ParameterInt(19, PCDI_TYPES.INT,(short) 1);
				pcdi.writeParameter(tmp, this.deviceId);
			}
			else if(osci_cmd==PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_SC) {
				PCDI_Parameter<?> tmp =  new PCDI_ParameterInt(32, PCDI_TYPES.INT,(short) 1);
				pcdi.writeParameter(tmp, this.deviceId);
			}
			else if(osci_cmd==PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_PC) {
				PCDI_Parameter<?> tmp =  new PCDI_ParameterInt(44, PCDI_TYPES.INT,(short) 1);
				pcdi.writeParameter(tmp, this.deviceId);
			}
				
			TimerTask task = new TimerTask() {
		        public void run() {
		        	pcdi.readParameter(paramDrpdwn1.getValueNumber(), deviceId);
		        	if(paramDrpdwn2!=null) {
		        		pcdi.readParameter(paramDrpdwn2.getValueNumber(), deviceId);
		        	}
		        	if(paramDrpdwn3!=null) {
		        		pcdi.readParameter(paramDrpdwn3.getValueNumber(), deviceId);
		        	}
		        	if(paramDrpdwn4!=null) {
		        		pcdi.readParameter(paramDrpdwn4.getValueNumber(), deviceId);
		        	}
		        }
			};
		    this.scopeTimer=new Timer();
	    	this.scopeTimer.scheduleAtFixedRate(task, 0, (int)this.sampleRateMs);
		}
	   
	    	
	    this.scoperunning=true;
	    this.infoConsole.append("[INFO] Scope started\n");
	    this.buttonStartStopScope.setText("StopScope");
	    this.scopeDatax1.clear();
	    this.scopeDatax2.clear();
	    this.scopeDatax3.clear();
	    this.scopeDatax4.clear();
	    this.scopeDatay1.clear();
	    this.scopeDatay2.clear();
	    this.scopeDatay3.clear();
	    this.scopeDatay4.clear();
	    this.maxY1=Double.NaN;
	    this.minY1=Double.NaN;
	    this.maxY2=Double.NaN;
	    this.minY2=Double.NaN;
	    this.maxY3=Double.NaN;
	    this.minY3=Double.NaN;
	    this.maxY4=Double.NaN;
	    this.minY4=Double.NaN;
	}
	private void stopScope() {
		if(this.scopeType==SCOPE_TYPE.CYCLE_READ) {
			if(this.scopeTimer!=null) {
				this.scopeTimer.cancel();
				this.scopeTimer=null;
				this.scoperunning=false;
				this.infoConsole.append("[INFO] Timer Scope stopped\n");
				this.buttonStartStopScope.setText("StartScope");
				this.buttonStartStopScope.setEnabled(true);
				this.box_drpValue1.setEnabled(true);
				this.box_drpValue2.setEnabled(true);
				this.box_drpValue3.setEnabled(true);
				this.box_drpValue4.setEnabled(true);
				this.txt_sampleRate.setEnabled(true);
				this.txt_sampleDepth.setEnabled(true);
			}
		}
		else {
			//TODO
			if(scoperunning) {
				this.buttonStartStopScope.setEnabled(false);
			}
			this.scoperunning=false;
			this.infoConsole.append("[INFO] Timer Scope stopped\n");
			//this.buttonStartStopScope.setText("StartScope");
			
			this.box_drpValue1.setEnabled(true);
			this.box_drpValue2.setEnabled(true);
			this.box_drpValue3.setEnabled(true);
			this.box_drpValue4.setEnabled(true);
			this.txt_sampleRate.setEnabled(true);
			this.txt_sampleDepth.setEnabled(true);
		}
		
		
	}
	
	
	
	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {		
	}
	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {
	}
	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		if(this.scopeType==SCOPE_TYPE.CYCLE_READ) {
			if(this.chckbxSingleShot.isSelected() && scopeDatay1.size()>=sampleDepth) {
				this.stopScope();
			}
			if(this.paramDrpdwn1!=null) {
				if(parameter.getValueNumber()==this.paramDrpdwn1.getValueNumber() && deviceId==this.deviceId && this.scopeTimer!=null) {
					double parameterVal=Double.parseDouble(parameter.getValue().toString());
					if (Double.isNaN(this.maxY1)) {
						this.maxY1=parameterVal;
						this.minY1=parameterVal;
						chart.getStyler().setYAxisMax(0,this.maxY1);
						chart.getStyler().setYAxisMin(0,this.minY1);
					}
					else if(parameterVal>this.maxY1) {
						this.maxY1=parameterVal;
						chart.getStyler().setYAxisMax(0,this.maxY1);
					}
					else if(parameterVal<this.minY1) {
						this.minY1=parameterVal;
						chart.getStyler().setYAxisMin(0,this.minY1);
					}
					scopeDatay1.add(parameterVal);
					if(scopeDatay1.size()>sampleDepth) {
						scopeDatay1.remove(0);
					}else {
						scopeDatax1.add((double)scopeDatay1.size()*this.sampleRateMs);
					}
					this.chart.updateXYSeries(this.paramInfoDrpdwn1.getName(),scopeDatax1, scopeDatay1,null);
					chartPanel.revalidate();
					chartPanel.repaint();
				}
			}
			if(this.paramDrpdwn2!=null) {
				if(parameter.getValueNumber()==this.paramDrpdwn2.getValueNumber() && deviceId==this.deviceId && this.scopeTimer!=null) {
					double parameterVal=Double.parseDouble(parameter.getValue().toString());
					if(Double.isNaN(this.maxY2)) {
						this.maxY2=parameterVal;
						this.minY2=parameterVal;
						chart.getStyler().setYAxisMax(1,this.maxY2);
						chart.getStyler().setYAxisMin(1,this.minY2);
					}
					else if(parameterVal>this.maxY2) {
						this.maxY2=parameterVal;
						chart.getStyler().setYAxisMax(1,this.maxY2);
					}
					else if(parameterVal<this.minY2) {
						this.minY2=parameterVal;
						chart.getStyler().setYAxisMin(1,this.minY2);
					}
					scopeDatay2.add(parameterVal);
					if(scopeDatay2.size()>sampleDepth) {
						scopeDatay2.remove(0);
					}else {
						scopeDatax2.add((double)scopeDatay2.size()*this.sampleRateMs);
					}
					this.chart.updateXYSeries(this.paramInfoDrpdwn2.getName(),scopeDatax2, scopeDatay2,null);
					chartPanel.revalidate();
					chartPanel.repaint();
				}
			}
			if(this.paramDrpdwn3!=null) {
				if(parameter.getValueNumber()==this.paramDrpdwn3.getValueNumber() && deviceId==this.deviceId && this.scopeTimer!=null) {
					double parameterVal=Double.parseDouble(parameter.getValue().toString());
					if(Double.isNaN(this.maxY3)) {
						this.maxY3=parameterVal;
						this.minY3=parameterVal;
						chart.getStyler().setYAxisMax(2,this.maxY3);
						chart.getStyler().setYAxisMin(2,this.minY3);
					}
					else if(parameterVal>this.maxY3) {
						this.maxY3=parameterVal;
						chart.getStyler().setYAxisMax(2,this.maxY3);
					}
					else if(parameterVal<this.minY3) {
						this.minY3=parameterVal;
						chart.getStyler().setYAxisMin(2,this.minY3);
					}
					scopeDatay3.add(parameterVal);
					if(scopeDatay3.size()>sampleDepth) {
						scopeDatay3.remove(0);
					}else {
						scopeDatax3.add((double)scopeDatay3.size()*this.sampleRateMs);
					}
					this.chart.updateXYSeries(this.paramInfoDrpdwn3.getName(),scopeDatax3, scopeDatay3,null);
					chartPanel.revalidate();
					chartPanel.repaint();
				}
			}
			if(this.paramDrpdwn4!=null) {
				if(parameter.getValueNumber()==this.paramDrpdwn4.getValueNumber() && deviceId==this.deviceId && this.scopeTimer!=null) {
					double parameterVal=Double.parseDouble(parameter.getValue().toString());
					if(Double.isNaN(this.maxY4)) {
						this.maxY4=parameterVal;
						this.minY4=parameterVal;
						chart.getStyler().setYAxisMax(3,this.maxY4);
						chart.getStyler().setYAxisMin(3,this.minY4);
					}
					else if(parameterVal>this.maxY4) {
						this.maxY4=parameterVal;
						chart.getStyler().setYAxisMax(3,this.maxY4);
					}
					else if(parameterVal<this.minY4) {
						this.minY4=parameterVal;
						chart.getStyler().setYAxisMin(3,this.minY4);
					}
					scopeDatay4.add(parameterVal);
					if(scopeDatay4.size()>sampleDepth) {
						scopeDatay4.remove(0);
					}else {
						scopeDatax4.add((double)scopeDatay4.size()*this.sampleRateMs);
					}
					this.chart.updateXYSeries(this.paramInfoDrpdwn4.getName(),scopeDatax4, scopeDatay4,null);
					chartPanel.revalidate();
					chartPanel.repaint();
				}
			}
		}
		
	}
	
	public void setActiveState(boolean state) {
		/*this.isActive = state;
		if(!state) {
			this.stopScope();
		}*/
		return;
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
	public void connectionChanged(boolean connected) {
		this.connected=connected;
		if(connected==false) {
			this.stopScope();
		}
	}
	private int getNumberOfScopeParams() {
		int i=0;
		if(this.paramDrpdwn1!=null) {
			i++;
		}
		if(this.paramDrpdwn2!=null) {
			i++;
		}
		if(this.paramDrpdwn3!=null) {
			i++;
		}
		if(this.paramDrpdwn4!=null) {
			i++;
		}
		return i;
	}
	private boolean alreadyInsertedInChart(String cmpString) {
		boolean inserted=false;
		Map seriesmap=this.chart.getSeriesMap();
		if(seriesmap.containsKey(cmpString)) {
			inserted=true;
		}
		return inserted;
	}
	@Override
	public void parameterUpdated(List<PCDI_Parameter<?>> params) {
		this.params=params;
		
	}
	@Override
	public void parameterInfoUpdated(List<PCDI_ParameterInfo> paramInfos) {
		this.paramInfos=paramInfos;
		this.box_drpValue1.removeAllItems();
		this.box_drpValue2.removeAllItems();
		this.box_drpValue3.removeAllItems();
		this.box_drpValue4.removeAllItems();
		box_drpValue1.addItem(null);
		box_drpValue2.addItem(null);
		box_drpValue3.addItem(null);
		box_drpValue4.addItem(null);
		paramInfos.forEach(parInfo->{
			if(!(parInfo.getPermission()==PCDI_PERMISSION.WRITE)) {
				this.box_drpValue1.addItem(parInfo.getName());
				this.box_drpValue2.addItem(parInfo.getName());
				this.box_drpValue3.addItem(parInfo.getName());
				this.box_drpValue4.addItem(parInfo.getName());
			}
				
			});
		
	}
	@Override
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId) {
		if (deviceId == this.deviceId && this.scoperunning && this.scopeType==SCOPE_TYPE.FAST_SCOPE) {
			// Parameter 1
			double parameterVal = data.getVal1();
			if (scopeDatay1.size() >= sampleDepth) {
				return;
			}
			if (Double.isNaN(this.maxY1)) {
				this.maxY1=parameterVal;
				this.minY1=parameterVal;
				chart.getStyler().setYAxisMax(0,this.maxY1);
				chart.getStyler().setYAxisMin(0,this.minY1);
			}
			else if(parameterVal>this.maxY1) {
				this.maxY1=parameterVal;
				chart.getStyler().setYAxisMax(0,this.maxY1);
			}
			else if(parameterVal<this.minY1) {
				this.minY1=parameterVal;
				chart.getStyler().setYAxisMin(0,this.minY1);
			}
			scopeDatay1.add(parameterVal);
			scopeDatax1.add((double) scopeDatay1.size() / 10 * this.oversampling);
			this.chart.updateXYSeries(this.paramInfoDrpdwn1.getName(), scopeDatax1, scopeDatay1, null);

			// Parameter 2
			if(this.paramDrpdwn2!=null) {
				parameterVal = data.getVal2();
				if (Double.isNaN(this.maxY2)) {
					this.maxY2=parameterVal;
					this.minY2=parameterVal;
					chart.getStyler().setYAxisMax(1,this.maxY2);
					chart.getStyler().setYAxisMin(1,this.minY2);
				}
				if (parameterVal > this.maxY2) {
					this.maxY2 = parameterVal;
					chart.getStyler().setYAxisMax(1, this.maxY2);
				} else if (parameterVal < this.minY2) {
					this.minY2 = parameterVal;
					chart.getStyler().setYAxisMin(1, this.minY2);
				}
				scopeDatay2.add(parameterVal);
				this.chart.updateXYSeries(this.paramInfoDrpdwn2.getName(), scopeDatax1, scopeDatay2, null);

			}
			
			// Parameter 3
			if(this.paramDrpdwn3!=null) {
				parameterVal = data.getVal3();
				if (Double.isNaN(this.maxY3)) {
					this.maxY3 = parameterVal;
					this.minY3 = parameterVal;
					chart.getStyler().setYAxisMax(2, this.maxY3);
					chart.getStyler().setYAxisMin(2, this.minY3);
				} else if (parameterVal > this.maxY3) {
					this.maxY3 = parameterVal;
					chart.getStyler().setYAxisMax(2, this.maxY3);
				} else if (parameterVal < this.minY3) {
					this.minY3 = parameterVal;
					chart.getStyler().setYAxisMin(2, this.minY3);
				}
				scopeDatay3.add(parameterVal);
				this.chart.updateXYSeries(this.paramInfoDrpdwn3.getName(), scopeDatax1, scopeDatay3, null);
			}
			
			// Parameter 4
			if(this.paramDrpdwn4!=null) {
				parameterVal = data.getVal4();
				if (Double.isNaN(this.maxY4)) {
					this.maxY4=parameterVal;
					this.minY4=parameterVal;
					chart.getStyler().setYAxisMax(3,this.maxY4);
					chart.getStyler().setYAxisMin(3,this.minY4);
				}
				if (parameterVal > this.maxY4) {
					this.maxY4 = parameterVal;
					chart.getStyler().setYAxisMax(3, this.maxY4);
				} else if (parameterVal < this.minY4) {
					this.minY4 = parameterVal;
					chart.getStyler().setYAxisMin(3, this.minY4);
				}
				scopeDatay4.add(parameterVal);
				this.chart.updateXYSeries(this.paramInfoDrpdwn4.getName(), scopeDatax1, scopeDatay4, null);
			}

			chartPanel.revalidate();
			chartPanel.repaint();
		}
	}
	@Override
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId) {
	}
	@Override
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId) {
		
		if(osci_state==PCDI_OSCILLOSCOPE_STATE.OSCI_READY) {
			infoConsole.append("Osci ready\n");
			if (this.deviceId != deviceId) {
				return;
			}
			if (this.scoperunning && !this.chckbxSingleShot.isSelected()) {
				this.scopeDatax1.clear();
				this.scopeDatay1.clear();
				this.scopeDatay2.clear();
				this.scopeDatay3.clear();
				this.scopeDatay4.clear();
				this.pcdi.startOscilloscope(PCDI_OSCILLOSCOPE_COMMAND.START_ONLY, this.valNr1, this.valNr2, this.valNr3,
						this.valNr4, 1, 1, 1, this.oversampling, this.deviceId);
				infoConsole.append("Osci started again\n");
			}
			else if (this.scoperunning && this.chckbxSingleShot.isSelected()) {
				this.stopScope();
				this.buttonStartStopScope.setText("StartScope");
				this.buttonStartStopScope.setEnabled(true);
			}
			else {
				this.buttonStartStopScope.setText("StartScope");
				this.buttonStartStopScope.setEnabled(true);
			}
		}
		
	}
	@Override
	public void notifyConnectionChanges(boolean isConnected) {
	}
	

}
