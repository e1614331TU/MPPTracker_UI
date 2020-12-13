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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.knowm.xchart.CSVExporter;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.None;

import comm.I_PCDI;
import comm.I_PCDI_Listener;
import comm.PCDI_COMMANDS;
import comm.PCDI_ERROR_TYPE;
import comm.PCDI_OSCILLOSCOPE_COMMAND;
import comm.PCDI_OSCILLOSCOPE_STATE;
import comm.PCDI_OscilloscopeData;
import comm.PCDI_Parameter;
import comm.PCDI_ParameterByte;
import comm.PCDI_ParameterFloat;
import comm.PCDI_ParameterInfo;
import comm.PCDI_ParameterInt;
import comm.PCDI_TYPES;
import javax.swing.JRadioButton;
/**
* JPanelAdvanced
* <p>
* this is the class which handles the Positioncontroller Panel.
* It is responsible for configuring the positioncontroller-parameters
*/
public class JPanelPowerController extends JPanel implements ActionListener, I_PCDI_Listener, I_Tab_Listener {
	// ----------CONFIG SOCPE -----------------//

	private Color color1 = Color.magenta;
	private Color color2 = Color.orange;
	private Color color3 = Color.red;
	private Color color4 = Color.blue;
	private String nameScope1 = "U in";
	private String nameScope2 = "I in";
	private String nameScope3 = "P in";
	private String nameScope4 = "delta set";
	private String nameScope5 = "delta act";
	private String nameScope6 = "U out";
	private String nameYAxisScope1 = "U/V";
	private String nameYAxisScope2= "I/A";
	private String nameYAxisScope3 = "P/W";
	private String nameYAxisScope4 = "1";
	// ---------CONFIG PARAMETER----------------//
	private PCDI_Parameter<?> paramNr00 = new PCDI_ParameterFloat(23, PCDI_TYPES.FLOAT, 0.0f);
	private PCDI_Parameter<?> paramNr01 = new PCDI_ParameterFloat(21, PCDI_TYPES.FLOAT, 0.0f);
	private PCDI_Parameter<?> paramNr02 = new PCDI_ParameterFloat(15, PCDI_TYPES.FLOAT, 0.0f);
	private PCDI_Parameter<?> paramNr03 = new PCDI_ParameterFloat(16, PCDI_TYPES.FLOAT, 0.0f);
	
	private String value00 = "Delta set";
	private String value01 = "Delta act";
	private String value02 = "-";
	private String value03 = "-";

	// Value Numbers for Scope:
	private int valNr1 = 14; //Uin
	private int valNr2 = 15; //Iin
	//TODO adapt Pin valNr
	private int valNr3 = 15; //Pin 
	private int valNr4 = 23; //delta set
	private int valNr5 = 21; //delta act
	private int valNr6 = 16; //Uout
	private int valNr7 = 25; //not used
	private int undersampling = 1;
	private int sampleTime = 70;

	// ---------------------------------------//
	private SCOPE_TYPE scopeType = SCOPE_TYPE.FAST_SCOPE;
	private List<Tuple<Integer,Double>> cyclicValues = new ArrayList<Tuple<Integer,Double>>();
	private Timer cyclicTimer  = null;
	
	private boolean isCControllerEnabled = false;
	private PCDI_Parameter<?> parameterCCEnabled = new PCDI_ParameterInt(19, PCDI_TYPES.INT, (short) 0);
	private boolean isVCEnabled = false;
	// TODO mppen oder vcen
	private PCDI_Parameter<?> parameterVCEnable = new PCDI_ParameterInt(17, PCDI_TYPES.INT, (short) 0);
	private boolean isSControllerEnabled = false;
	private PCDI_Parameter<?> parameterSCEnabled = new PCDI_ParameterInt(32, PCDI_TYPES.INT, (short) 0);
	private JButton buttonSetEnableDisableVC;
	private JButton buttonStartStepResponse;
	private boolean oneshot = false;
	private JButton buttonExportCSV;

	private boolean isPWMForced = false;
	private PCDI_Parameter<?> parameterForcePWM = new PCDI_ParameterInt(22, PCDI_TYPES.INT, (short) 0);
	
	
	private static final long serialVersionUID = 8776152363973472684L;

	private GridBagConstraints c_8;
	private JButton buttonStartStopScope;
	private GridBagLayout layoutScope = new GridBagLayout();
	private JTextField txt_sampleTime;
	private JTextField txt_sampleDepth;
	private JTextField txtValue00;
	private JTextField txtValueInfo00;
	private JButton buttonSetValue00;
	private JButton buttonReadValue00;
	private JTextField txtValue01;
	private JTextField txtValueInfo01;
	private JButton buttonSetValue01;
	private JButton buttonReadValue01;
	private JTextField txtValue02;
	private JTextField txtValueInfo02;
	private JButton buttonSetValue02;
	private JButton buttonReadValue02;
	private JTextField txtValue03;
	private JTextField txtValueInfo03;
	private JButton buttonSetValue03;
	private JButton buttonReadValue03;

	private JButton buttonSetDeltaZero;
	private JButton buttonReadAll;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_3;
	private JPanel panel_2;
	private JCheckBox boxSeries1enable;
	private JCheckBox boxSeries2enable;
	private JCheckBox boxSeries3enable;
	private JCheckBox boxSeries4enable;
	private JCheckBox boxSeries6enable;
	private JCheckBox boxSeries5enable;

	private JPanel panel_5;
	private GridBagConstraints c;
	private GridBagConstraints gbc_buttonReadValue03_1;
	private boolean isControllerEnabled = false;

	private I_PCDI pcdi;

	// private Timer scopeTimer;
	private boolean scoperunning = false;
	private int deviceId;
	private final XYChart chart;
	private JPanel chartPanel;
	private List<Double> scopeDatay1 = new ArrayList<Double>();
	private List<Double> scopeDatax1 = new ArrayList<Double>();
	private List<Double> scopeDatay2 = new ArrayList<Double>();
	private List<Double> scopeDatay3 = new ArrayList<Double>();
	private List<Double> scopeDatay4 = new ArrayList<Double>();
	private List<Double> scopeDatay5 = new ArrayList<Double>();
	private List<Double> scopeDatay6 = new ArrayList<Double>();
	private int sampleDepth = 100;
	private XYSeries XYSeries1;
	private XYSeries XYSeries2;
	private XYSeries XYSeries3;
	private XYSeries XYSeries4;
	private XYSeries XYSeries5;
	private XYSeries XYSeries6;
	private double maxY1 = 0;
	private double minY1 = 0;
	private double maxY2 = 0;
	private double minY2 = 0;
	private double maxY3 = 0;
	private double minY3 = 0;
	private double maxY4 = 1;
	private double minY4 = 0;

	private JTextArea infoConsole;

	private boolean connected;
	private GridBagConstraints gbc_lbl_drpdwn_value_editable_1;
	private JCheckBox chckbxForcePWM;

	/**
	* this method is the constructor
	* @param pcdi is needed for communication
	* @param deviceId is needed for communication with the controller for ability to communicate with the right controller 
	* @param infoConsole is needed for giving user information
	*/
	public JPanelPowerController(I_PCDI pcdi, int deviceId, JTextArea infoConsole) {
		super();
		this.infoConsole = infoConsole;
		this.deviceId = deviceId;
		this.pcdi = pcdi;
		this.pcdi.registerListener(this);

		// set size
		this.setPreferredSize(new Dimension(MainWindow.width, MainWindow.contentHeight));
		this.setMinimumSize(new Dimension(MainWindow.width, MainWindow.contentHeight));

		layoutScope.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		layoutScope.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		layoutScope.rowWeights = new double[] { 1.0, 0.0, 0.0 };
		layoutScope.rowHeights = new int[] { 200, 0, 0 };
		this.setLayout(layoutScope);

		// SET VALUE
		this.makeEditableValues();
		// Start Stepresponse
		this.buttonStartStepResponse = new JButton("StepResponse VC Fast");
		GridBagConstraints gbc_buttonStartStepResponse = new GridBagConstraints();
		gbc_buttonStartStepResponse.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonStartStepResponse.insets = new Insets(0, 0, 5, 5);
		gbc_buttonStartStepResponse.gridx = 0;
		gbc_buttonStartStepResponse.gridy = 1;
		this.buttonStartStepResponse.addActionListener(this);
		panel_2.add(buttonStartStepResponse, gbc_buttonStartStepResponse);

		// CHART
		// Create Chart
		this.chart = new XYChartBuilder().width(MainWindow.width).height(100).theme(ChartTheme.Matlab).xAxisTitle("t/ms")
				.yAxisTitle("Y").build();

		// Customize Chart
		chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

		// Series
		XYSeries1 = this.chart.addSeries(this.nameScope1, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries1.setYAxisGroup(0);
		XYSeries1.setMarker(new None());
		XYSeries1.setLineColor(color4);
		
		XYSeries2 = this.chart.addSeries(this.nameScope2, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries2.setYAxisGroup(1);
		XYSeries2.setMarker(new None());;
		//XYSeries2.setLineWidth(0.8f);
		XYSeries2.setLineColor(color3);
		XYSeries2.setLineStyle(SeriesLines.DOT_DOT);

		XYSeries3 = this.chart.addSeries(this.nameScope3, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries3.setYAxisGroup(2);
		XYSeries3.setMarker(new None());
		XYSeries3.setLineColor(color2);
		//XYSeries3.setLineWidth(0.8f);
		//XYSeries3.setLineStyle(SeriesLines.DOT_DOT);

		XYSeries4 = this.chart.addSeries(this.nameScope4, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries4.setYAxisGroup(3);
		XYSeries4.setMarker(new None());
		XYSeries4.setLineColor(color1);
		XYSeries4.setLineWidth(0.8f);
		XYSeries4.setLineStyle(SeriesLines.DOT_DOT);

		XYSeries5 = this.chart.addSeries(this.nameScope5, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries5.setYAxisGroup(3);
		XYSeries5.setMarker(new None());
		XYSeries5.setLineColor(color1);
		
		XYSeries6 = this.chart.addSeries(this.nameScope6, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries6.setYAxisGroup(0);
		XYSeries6.setMarker(new None());
		XYSeries6.setLineColor(color4);
		XYSeries6.setLineWidth(0.8f);
		XYSeries6.setLineStyle(SeriesLines.DOT_DOT);
		

		// axis 
		this.chart.setYAxisGroupTitle(0, this.nameYAxisScope1);
		this.chart.setYAxisGroupTitle(1, this.nameYAxisScope2);
		this.chart.setYAxisGroupTitle(2, this.nameYAxisScope3);
		this.chart.setYAxisGroupTitle(3, this.nameYAxisScope4);
		chart.getStyler().setYAxisMin(0, this.minY1);
		chart.getStyler().setYAxisMax(0, this.maxY1);
		chart.getStyler().setYAxisMin(1, this.minY2);
		chart.getStyler().setYAxisMax(1, this.maxY2);
		chart.getStyler().setYAxisMin(2, this.minY3);
		chart.getStyler().setYAxisMax(2, this.maxY3);
		chart.getStyler().setYAxisMin(3, this.minY4);
		chart.getStyler().setYAxisMax(3, this.maxY4);

		chart.getStyler().setYAxisGroupTitleColor(0, color4);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(0, color4);
		chart.getStyler().setYAxisGroupTitleColor(1, color3);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(1, color3);
		chart.getStyler().setYAxisGroupTitleColor(2, color2);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(2, color2);
		chart.getStyler().setYAxisGroupTitleColor(3, color1);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(3, color1);

		// chart
		chartPanel = new XChartPanel<XYChart>(chart);
		c_8 = new GridBagConstraints();
		c_8.gridwidth = 10;
		c_8.insets = new Insets(0, 0, 5, 0); // top padding
		c_8.fill = GridBagConstraints.BOTH;
		c_8.weightx = 0.1;
		c_8.weighty = 1.0;
		c_8.gridx = 0;
		c_8.gridy = 0;
		this.add(chartPanel, c_8);
		
		//this.XYSeries3.setEnabled(false);
		//this.XYSeries4.setEnabled(false);
		//this.XYSeries5.setEnabled(false);
		//this.XYSeries6.setEnabled(false);
		
		this.cyclicValues.add(new Tuple<Integer,Double>(this.valNr1, Double.NaN));
		this.cyclicValues.add(new Tuple<Integer,Double>(this.valNr2, Double.NaN));
		this.cyclicValues.add(new Tuple<Integer,Double>(this.valNr3, Double.NaN));
		this.cyclicValues.add(new Tuple<Integer,Double>(this.valNr4, Double.NaN));
		this.cyclicValues.add(new Tuple<Integer,Double>(this.valNr5, Double.NaN));
		this.cyclicValues.add(new Tuple<Integer,Double>(this.valNr6, Double.NaN));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.buttonExportCSV) {
			this.infoConsole.append("export data\n");
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = fileChooser.showSaveDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				String path = file.getAbsolutePath() + "\\";
				this.infoConsole.append("Folder Selected: " + path + "\n");
				CSVExporter.writeCSVColumns(this.chart, path);
			} else {
				this.infoConsole.append("Open command cancelled\n");
			}
		} else if (e.getSource() == this.boxSeries1enable) {
			this.XYSeries1.setEnabled(this.boxSeries1enable.isSelected());
			this.chartPanel.revalidate();
			this.chartPanel.repaint();
		} else if (e.getSource() == this.boxSeries2enable) {
			this.XYSeries2.setEnabled(this.boxSeries2enable.isSelected());
			this.chartPanel.revalidate();
			this.chartPanel.repaint();
		} else if (e.getSource() == this.boxSeries3enable) {
			this.XYSeries3.setEnabled(this.boxSeries3enable.isSelected());
			this.chartPanel.revalidate();
			this.chartPanel.repaint();
		} else if (e.getSource() == this.boxSeries4enable) {
			this.XYSeries4.setEnabled(this.boxSeries4enable.isSelected());
			this.chartPanel.revalidate();
			this.chartPanel.repaint();
		}else if (e.getSource() == this.boxSeries5enable) {
			this.XYSeries5.setEnabled(this.boxSeries5enable.isSelected());
			this.chartPanel.revalidate();
			this.chartPanel.repaint();
		} else if (e.getSource() == this.boxSeries6enable) {
			this.XYSeries6.setEnabled(this.boxSeries6enable.isSelected());
			this.chartPanel.revalidate();
			this.chartPanel.repaint();
		} else if(e.getSource() == this.chckbxForcePWM) {
			if(this.chckbxForcePWM.isSelected()) {
				this.parameterForcePWM.setValue("1");
				this.pcdi.writeParameter(this.parameterForcePWM, deviceId);
			} else {
				this.parameterForcePWM.setValue("0");
				this.pcdi.writeParameter(this.parameterForcePWM, deviceId);
			}
		} else if (e.getSource() == this.buttonReadValue00) {
			this.pcdi.readParameter(this.paramNr00.getValueNumber(), deviceId);
		} else if (e.getSource() == this.buttonSetValue00) {
			infoConsole.append("Set value\n");
			if (!this.txtValue00.getText().isEmpty()) {
				double value = Double.parseDouble(this.txtValue00.getText());
				this.paramNr00.setValue(Double.toString(value));
				this.pcdi.writeParameter(this.paramNr00, deviceId);
			} else {
				infoConsole.append("No value inserted!!!\n");
			}
		} else if (e.getSource() == this.buttonReadValue01) {
			this.pcdi.readParameter(this.paramNr01.getValueNumber(), deviceId);
		} else if (e.getSource() == this.buttonSetValue01) {
			infoConsole.append("Set value\n");
			if (!this.txtValue01.getText().isEmpty()) {
				this.paramNr01.setValue(this.txtValue01.getText());
				this.pcdi.writeParameter(this.paramNr01, deviceId);
			} else {
				infoConsole.append("No value inserted!!!\n");
			}
		} else if (e.getSource() == this.buttonReadValue02) {
			this.pcdi.readParameter(this.paramNr02.getValueNumber(), deviceId);
		} else if (e.getSource() == this.buttonSetValue02) {
			infoConsole.append("Set value\n");
			if (!this.txtValue02.getText().isEmpty()) {
				this.paramNr02.setValue(this.txtValue02.getText());
				this.pcdi.writeParameter(this.paramNr02, deviceId);
			} else {
				infoConsole.append("No value inserted!!!\n");
			}
		} else if (e.getSource() == this.buttonReadValue03) {
			this.pcdi.readParameter(this.paramNr03.getValueNumber(), deviceId);
		} else if (e.getSource() == this.buttonSetValue03) {
			infoConsole.append("Set value\n");
			if (!this.txtValue03.getText().isEmpty()) {
				this.paramNr03.setValue(this.txtValue03.getText());
				this.pcdi.writeParameter(this.paramNr03, deviceId);
			} else {
				infoConsole.append("No value inserted!!!\n");
			}
		} else if (e.getSource() == this.buttonReadAll) {
			this.pcdi.readParameter(this.paramNr00.getValueNumber(), deviceId);
			this.pcdi.readParameter(this.paramNr01.getValueNumber(), deviceId);
			this.pcdi.readParameter(this.paramNr02.getValueNumber(), deviceId);
			this.pcdi.readParameter(this.paramNr03.getValueNumber(), deviceId);
		} else if (e.getSource() == this.buttonSetEnableDisableVC) {

			if (this.isVCEnabled == true) {
				this.parameterVCEnable.setValue("0");
				this.pcdi.writeParameter(this.parameterVCEnable, deviceId);
			} else {
				this.parameterVCEnable.setValue("1");
				this.pcdi.writeParameter(this.parameterVCEnable, deviceId);
			} 
		} else if (e.getSource() == this.buttonSetDeltaZero) {
			this.paramNr00.setValue("0");
			this.pcdi.writeParameter(this.paramNr00, deviceId);
			
			/*this.parameterCCEnabled.setValue("0");
			this.pcdi.writeParameter(this.parameterCCEnabled, deviceId);
			this.parameterPCEnabled.setValue("0");
			this.pcdi.writeParameter(this.parameterPCEnabled, deviceId);
			this.parameterSCEnabled.setValue("0");
			this.pcdi.writeParameter(this.parameterSCEnabled, deviceId);*/
			this.pcdi.readParameter(this.paramNr00.getValueNumber(), deviceId);
			
		} else if (e.getSource() == this.buttonStartStopScope) {
			StartScope(PCDI_OSCILLOSCOPE_COMMAND.START_ONLY);
			this.oneshot = false;

		} else if (e.getSource() == this.buttonStartStepResponse) {
			if(this.isVCEnabled) {
				JFrame frameControllerEnabled=new JFrame();  
			    Object[] optionsControllerEnabled = {"Disable and start",
	                    "Go back"};
				int nController = JOptionPane.showOptionDialog(frameControllerEnabled,
				    "Controller already enabled!",
				    "Information",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.WARNING_MESSAGE,
				    null,
				    optionsControllerEnabled,
				    optionsControllerEnabled[1]);
				System.out.println("Option:"+nController);
				if(nController==0) {
					PCDI_Parameter<Byte> tmp=new PCDI_ParameterByte(this.parameterVCEnable.getValueNumber(),PCDI_TYPES.BYTE,(byte)0);
					this.pcdi.writeParameter(tmp, this.deviceId);
				}
				else if(nController==1) {
					return;
				}
			}
			if(!this.txtValueInfo00.getText().isEmpty() && !this.txtValueInfo01.getText().isEmpty()) {
				if(Double.parseDouble(this.txtValueInfo00.getText())==0.0) {
					JFrame frameValues=new JFrame();  
				    Object[] optionsValues = {"Start anyway",
		                    "Go back"};
					int nValues = JOptionPane.showOptionDialog(frameValues,
					    "Value entered is zero!",
					    "Information",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.WARNING_MESSAGE,
					    null,
					    optionsValues,
					    optionsValues[1]);
					System.out.println("Option:"+nValues);
					if(nValues==1) {
						return;
					}
				}
			}
			//TODO
			this.parameterVCEnable.setValue("1");
			this.pcdi.writeParameter(this.parameterVCEnable, deviceId);
			this.buttonSetEnableDisableVC.setText("Disable VC");
			this.isVCEnabled = true;
			//StartScope(PCDI_OSCILLOSCOPE_COMMAND.START_STEP_RESPONSE_PC);
			StartScope(PCDI_OSCILLOSCOPE_COMMAND.START_ONLY);
			this.oneshot = true;
			
			
		} else if (e.getSource() == this.buttonSetEnableDisableVC) {

			if (this.isVCEnabled == true) {
				this.parameterVCEnable.setValue("0");
				this.pcdi.writeParameter(this.parameterVCEnable, deviceId);
			} else {
				this.parameterVCEnable.setValue("1");
				this.pcdi.writeParameter(this.parameterVCEnable, deviceId);
			}
		}
	}

	private void StartScope(PCDI_OSCILLOSCOPE_COMMAND osci_cmd) {
		
		
		
		if (this.txt_sampleTime.getText().isEmpty()) {
			this.infoConsole.append("No Sampletime selected!\n");
			return;
		}
		if (!this.scoperunning) {

			//Calc wether to use fast scope or not
			double sampleTimeDouble = Double.parseDouble(this.txt_sampleTime.getText());
			if(Double.parseDouble(this.txt_sampleTime.getText())<25.6) {
				this.scopeType = SCOPE_TYPE.FAST_SCOPE;
				double temp = sampleTimeDouble/0.1;
				this.undersampling = (int)temp;
			}else {
				this.scopeType = SCOPE_TYPE.CYCLE_READ;
				if(sampleTimeDouble<60) {
					this.infoConsole.append("Sampletime set to 60ms!\n");
					sampleTimeDouble=60;
				}
				this.sampleTime = (int) sampleTimeDouble;
				this.txt_sampleTime.setText(Integer.toString(this.sampleTime));
			}
			
			this.sampleDepth = Integer.parseInt(this.txt_sampleDepth.getText());
			if(this.sampleDepth>512) {
				this.infoConsole.append("No sample Time greater then 512 possible!");
				this.sampleDepth=512;
			}
			
			this.txt_sampleTime.setEnabled(false);
			this.txt_sampleDepth.setEnabled(false);
			//System.out.println("undersampling:"+undersampling);
			
			// START SCOPE
			if(this.scopeType == SCOPE_TYPE.FAST_SCOPE) {
				this.pcdi.startOscilloscope(osci_cmd, this.valNr1, this.valNr2, this.valNr3, this.valNr4, this.valNr5,
						this.valNr6, this.valNr7, undersampling, deviceId);
			} else if(this.scopeType == SCOPE_TYPE.CYCLE_READ) {
				//TODO
				this.startTimer();
			}
			

			this.scoperunning = true;
			this.infoConsole.append("Scope started\n");
			this.buttonStartStopScope.setText("StopScope");
			this.scopeDatax1.clear();
			this.scopeDatay1.clear();
			this.scopeDatay2.clear();
			this.scopeDatay3.clear();
			this.scopeDatay4.clear();
			this.scopeDatay5.clear();
			this.scopeDatay6.clear();
			this.maxY1 = 0;
			this.minY1 = 0;
			this.maxY2 = 0;
			this.minY2 = 0;
			this.maxY3 = 0;
			this.minY3 = 0;
			this.maxY4 = 1;
			this.minY4 = 0;
		} else {
			StopScope();
		}
	}

	private void StopScope() {
		this.scoperunning = false;
		this.infoConsole.append("Timer Scope stopped\n");
		this.buttonStartStopScope.setText("StartScope");
		this.txt_sampleTime.setEnabled(true);
		this.txt_sampleDepth.setEnabled(true);
		if(this.scopeType == SCOPE_TYPE.CYCLE_READ) {
			this.stopTimer();
		}
	}
	private void startTimer() {
		if(this.cyclicTimer==null) {
			TimerTask task = new TimerTask() {
		        public void run() {
		        	for(Tuple<Integer,Double> tmp : cyclicValues) {
		        		pcdi.readParameter(tmp.getValA(), deviceId);
		        	}
		        }
		    };
		    this.cyclicTimer=new Timer();
		    this.cyclicTimer.scheduleAtFixedRate(task, 0, this.sampleTime);
		}
		//this.resetScope();
		//if(!this.txtNumberOfValues.getText().isEmpty()) this.NumberOfValues = Integer.parseInt(this.txtNumberOfValues.getText());
	}
	private void stopTimer() {
		if(this.cyclicTimer!=null) {
			this.cyclicTimer.cancel();
			this.cyclicTimer=null;
		}
	}

	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		if (parameter.getValueNumber() == this.paramNr00.getValueNumber() && deviceId == this.deviceId) {
			double value = Double.parseDouble(parameter.getValue().toString());
			this.txtValueInfo00.setText(Double.toString(value));
		} else if (parameter.getValueNumber() == this.paramNr01.getValueNumber() && deviceId == this.deviceId) {
			this.txtValueInfo01.setText(parameter.getValue().toString());
		} else if (parameter.getValueNumber() == this.paramNr02.getValueNumber() && deviceId == this.deviceId) {
			//this.txtValueInfo02.setText(parameter.getValue().toString());
		} else if (parameter.getValueNumber() == this.paramNr03.getValueNumber() && deviceId == this.deviceId) {
			//this.txtValueInfo03.setText(parameter.getValue().toString());
		} else if (parameter.getValueNumber() == this.parameterVCEnable.getValueNumber()
				&& deviceId == this.deviceId) {
			this.parameterVCEnable = parameter;
			if (Integer.parseInt(parameter.getValue().toString()) == 1) {
				this.buttonSetEnableDisableVC.setText("Disable VC");
				this.isVCEnabled = true;
			} else {
				this.buttonSetEnableDisableVC.setText("Enable VC");
				this.isVCEnabled = false;
			}
		} else if (parameter.getValueNumber() == this.parameterForcePWM.getValueNumber() && deviceId == this.deviceId) {
				if (Integer.parseInt(parameter.getValue().toString()) == 1) {
					this.chckbxForcePWM.setSelected(true);
					this.isPWMForced = true;
				} else {
					this.chckbxForcePWM.setSelected(false);
					this.isPWMForced = false;
				}
		}

	}

	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {

	}

	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		if (parameter.getValueNumber() == this.paramNr00.getValueNumber() && deviceId == this.deviceId) {
			double value = Double.parseDouble(parameter.getValue().toString());
			this.txtValue00.setText(Double.toString(value));
			this.txtValueInfo00.setText(Double.toString(value));
		} else if (parameter.getValueNumber() == this.paramNr01.getValueNumber() && deviceId == this.deviceId) {
			this.txtValue01.setText(parameter.getValue().toString());
			this.txtValueInfo01.setText(parameter.getValue().toString());
		} else if (parameter.getValueNumber() == this.paramNr02.getValueNumber() && deviceId == this.deviceId) {
			//this.txtValue02.setText(parameter.getValue().toString());
			//this.txtValueInfo02.setText(parameter.getValue().toString());
		} else if (parameter.getValueNumber() == this.paramNr03.getValueNumber() && deviceId == this.deviceId) {
			//this.txtValue03.setText(parameter.getValue().toString());
			//this.txtValueInfo03.setText(parameter.getValue().toString());
		} else if (parameter.getValueNumber() == this.parameterVCEnable.getValueNumber()
				&& deviceId == this.deviceId) {
			this.parameterVCEnable = parameter;
			if (Integer.parseInt(parameter.getValue().toString()) == 1) {
				this.buttonSetEnableDisableVC.setText("Disable VC");
				this.isVCEnabled = true;
			} else {
				this.buttonSetEnableDisableVC.setText("Enable VC");
				this.isVCEnabled = false;
			}
		} else if (parameter.getValueNumber() == this.parameterForcePWM.getValueNumber() && deviceId == this.deviceId) {
			if (Integer.parseInt(parameter.getValue().toString()) == 1) {
				this.chckbxForcePWM.setSelected(true);
				this.isPWMForced = true;
			} else {
				this.chckbxForcePWM.setSelected(false);
				this.isPWMForced = false;
			}
		}
		
		if(this.scoperunning == true && this.scopeType == SCOPE_TYPE.CYCLE_READ && deviceId == this.deviceId) {
			if (parameter.getValueNumber() == this.valNr1 || parameter.getValueNumber() == this.valNr2 || parameter.getValueNumber() == this.valNr3 || parameter.getValueNumber() == this.valNr4 || parameter.getValueNumber() == this.valNr5 || parameter.getValueNumber() == this.valNr6) {
				boolean doneCapturing = true;
				for(Tuple<Integer,Double> tmp : this.cyclicValues) {
					if(tmp.getValA() == parameter.getValueNumber()) {
						tmp.setValB(Double.parseDouble(parameter.getValue().toString()));
					}
					if(Double.isNaN(tmp.getValB())) doneCapturing = false;
				}
				if(doneCapturing) {
					List<Double> data_list = new ArrayList<Double>();
					
					data_list.add(this.cyclicValues.get(0).getValB());
					data_list.add(this.cyclicValues.get(1).getValB());
					data_list.add(this.cyclicValues.get(2).getValB());
					data_list.add(this.cyclicValues.get(3).getValB());
					data_list.add(this.cyclicValues.get(4).getValB());
					data_list.add(this.cyclicValues.get(5).getValB());

					
					for(Tuple<Integer,Double> tmp : this.cyclicValues) {
						tmp.setValB(Double.NaN);
					}
					
					PCDI_OscilloscopeData data = new PCDI_OscilloscopeData(data_list.get(0).floatValue(),data_list.get(1).floatValue(),data_list.get(3).floatValue(),data_list.get(4).floatValue(),data_list.get(4).floatValue(),data_list.get(5).floatValue(),0.0f);
					this.notifyOscilloscopeData(data, deviceId);
				}
			}
		}

	}
	private void updateScopeData(List<Double> data) {
		
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
		this.connected = connected;
		if (connected == false) {
			this.scoperunning = false;
			this.infoConsole.append("Timer Scope stopped\n");
			this.buttonStartStopScope.setText("StartScope");
			this.txt_sampleTime.setEnabled(true);
			this.txt_sampleDepth.setEnabled(true);
		}
	}

	@Override
	public void parameterUpdated(List<PCDI_Parameter<?>> params) {

	}

	@Override
	public void parameterInfoUpdated(List<PCDI_ParameterInfo> paramInfos) {

	}

	@Override
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId) {
		if (deviceId == this.deviceId && this.scoperunning) {
			
			if(this.scopeType == SCOPE_TYPE.FAST_SCOPE) {
				scopeDatax1.add((double) scopeDatay1.size() / 10 * this.undersampling);
			}else if(this.scopeType == SCOPE_TYPE.CYCLE_READ) {
				if(this.oneshot == true && scopeDatax1.size()>this.sampleDepth) {
					this.StopScope();
				} else if(scopeDatax1.size()>=this.sampleDepth) {
					this.scopeDatay1.remove(0);
					this.scopeDatay2.remove(0);
					this.scopeDatay3.remove(0);
					this.scopeDatay4.remove(0);
					this.scopeDatay5.remove(0);
					this.scopeDatay6.remove(0);
				} else if(scopeDatax1.size()<=this.sampleDepth) {
					this.scopeDatax1.add((double) scopeDatay1.size() * this.sampleTime);
				}
				
			}
			
			
			// Parameter 1
			double parameterVal = data.getVal1();
			if (scopeDatay1.size() >= sampleDepth) {
				return;
			}
			if (parameterVal > this.maxY1) {
				this.maxY1 = parameterVal;
				chart.getStyler().setYAxisMax(0, this.maxY1);
			} else if (parameterVal < this.minY1) {
				this.minY1 = parameterVal;
				chart.getStyler().setYAxisMin(0, this.minY1);
			}
			scopeDatay1.add(parameterVal);
			this.chart.updateXYSeries(this.nameScope1, scopeDatax1, scopeDatay1, null);

			// Parameter 2
			parameterVal = data.getVal2();
			if (parameterVal > this.maxY2) {
				this.maxY2 = parameterVal;
				chart.getStyler().setYAxisMax(1, this.maxY2);
			} else if (parameterVal < this.minY2) {
				this.minY2 = parameterVal;
				chart.getStyler().setYAxisMin(1, this.minY2);
			}
			scopeDatay2.add(parameterVal);
			this.chart.updateXYSeries(this.nameScope2, scopeDatax1, scopeDatay2, null);

			// Parameter 3
			parameterVal = data.getVal3();
			if (parameterVal > this.maxY3) {
				this.maxY3 = parameterVal;
				chart.getStyler().setYAxisMax(2, this.maxY3);
			} else if (parameterVal < this.minY3) {
				this.minY3 = parameterVal;
				chart.getStyler().setYAxisMin(2, this.minY3);
			}
			scopeDatay3.add(parameterVal);
			this.chart.updateXYSeries(this.nameScope3, scopeDatax1, scopeDatay3, null);

			// Parameter 4
			parameterVal = data.getVal4();
			if (parameterVal > this.maxY4) {
				this.maxY4 = parameterVal;
				chart.getStyler().setYAxisMax(3, this.maxY4);
			} else if (parameterVal < this.minY4) {
				this.minY4 = parameterVal;
				chart.getStyler().setYAxisMin(3, this.minY4);
			}
			scopeDatay4.add(parameterVal);
			this.chart.updateXYSeries(this.nameScope4, scopeDatax1, scopeDatay4, null);
			
			// Parameter 5
			parameterVal = data.getVal5();
			if (parameterVal > this.maxY4) {
				this.maxY4 = parameterVal;
				chart.getStyler().setYAxisMax(3, this.maxY4);
			} else if (parameterVal < this.minY4) {
				this.minY4 = parameterVal;
				chart.getStyler().setYAxisMin(3, this.minY4);
			}
			scopeDatay5.add(parameterVal);
			this.chart.updateXYSeries(this.nameScope5, scopeDatax1, scopeDatay5, null);
			
			// Parameter 5
			parameterVal = data.getVal6();
			if (parameterVal > this.maxY1) {
				this.maxY1 = parameterVal;
				chart.getStyler().setYAxisMax(0, this.maxY1);
			} else if (parameterVal < this.minY3) {
				this.minY3 = parameterVal;
				chart.getStyler().setYAxisMin(0, this.minY1);
			}
			scopeDatay6.add(parameterVal);
			this.chart.updateXYSeries(this.nameScope6, scopeDatax1, scopeDatay6, null);
			
			chartPanel.revalidate();
			chartPanel.repaint();
		}
	}

	public void makeEditableValues() {
		
		chckbxForcePWM = new JCheckBox("Force PWM");
		GridBagConstraints gbc_chckbxForcePWM = new GridBagConstraints();
		gbc_chckbxForcePWM.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxForcePWM.gridx = 0;
		gbc_chckbxForcePWM.gridy = 1;
		add(chckbxForcePWM, gbc_chckbxForcePWM);
		this.chckbxForcePWM.addActionListener(this);

		panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"2. Enable/Disable VC", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 3;
		add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 0, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		// EnableDisable PC
		this.buttonSetEnableDisableVC = new JButton("Enable VC");
		this.buttonSetEnableDisableVC.addActionListener(this);
		GridBagConstraints gbc_buttonSetEnableDisableVC = new GridBagConstraints();
		gbc_buttonSetEnableDisableVC.gridx = 0;
		gbc_buttonSetEnableDisableVC.gridy = 0;
		panel_3.add(buttonSetEnableDisableVC, gbc_buttonSetEnableDisableVC);
		this.buttonSetEnableDisableVC.addActionListener(this);

		// Sample Time:
		JLabel labelValue04 = new JLabel("Sample Time/ms (>0.1ms)");
		GridBagConstraints c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(0, 20, 5, 5); // top padding
		c_5.weightx = 0.0;
		c_5.gridx = 3;
		c_5.gridy = 1;
		this.add(labelValue04, c_5);

		txt_sampleTime = new JTextField("10");
		txt_sampleTime.setMaximumSize(new Dimension(60, 20));
		txt_sampleTime.setPreferredSize(new Dimension(60, 20));
		txt_sampleTime.setMinimumSize(new Dimension(60, 20));
		GridBagConstraints gbc_lbl_drpdwn_value_editable = new GridBagConstraints();
		gbc_lbl_drpdwn_value_editable.anchor = GridBagConstraints.WEST;
		gbc_lbl_drpdwn_value_editable.weightx = 0.3;
		gbc_lbl_drpdwn_value_editable.insets = new Insets(0, 4, 5, 5);
		gbc_lbl_drpdwn_value_editable.gridx = 4;
		gbc_lbl_drpdwn_value_editable.gridy = 1;
		this.add(txt_sampleTime, gbc_lbl_drpdwn_value_editable);
		txt_sampleTime.setColumns(10);

		labelValue04 = new JLabel("Samples on Scope:");
		c_5 = new GridBagConstraints();
		c_5.anchor = GridBagConstraints.EAST;
		c_5.insets = new Insets(0, 4, 5, 5); // top padding
		c_5.weightx = 0.0;
		c_5.gridx = 5;
		c_5.gridy = 1;
		this.add(labelValue04, c_5);
		txt_sampleDepth = new JTextField("512");
		txt_sampleDepth.setMaximumSize(new Dimension(60, 20));
		txt_sampleDepth.setPreferredSize(new Dimension(60, 20));
		txt_sampleDepth.setMinimumSize(new Dimension(60, 20));
		gbc_lbl_drpdwn_value_editable_1 = new GridBagConstraints();
		gbc_lbl_drpdwn_value_editable_1.anchor = GridBagConstraints.WEST;
		gbc_lbl_drpdwn_value_editable_1.weightx = 0.3;
		gbc_lbl_drpdwn_value_editable_1.insets = new Insets(0, 4, 5, 5);
		gbc_lbl_drpdwn_value_editable_1.gridx = 6;
		gbc_lbl_drpdwn_value_editable_1.gridy = 1;
		this.add(txt_sampleDepth, gbc_lbl_drpdwn_value_editable_1);
		txt_sampleDepth.setColumns(10);
		panel = new JPanel();
		panel.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"1. Set Delta to zero", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.rowWeights = new double[] { 0.0 };
		panel.setLayout(gbl_panel);

		this.buttonSetDeltaZero = new JButton("Set delta to 0");
		GridBagConstraints gbc_buttonSetSpeedZero = new GridBagConstraints();
		gbc_buttonSetSpeedZero.insets = new Insets(0, 0, 5, 0);
		gbc_buttonSetSpeedZero.fill = GridBagConstraints.BOTH;
		gbc_buttonSetSpeedZero.gridx = 0;
		gbc_buttonSetSpeedZero.gridy = 0;
		panel.add(buttonSetDeltaZero, gbc_buttonSetSpeedZero);
		this.buttonSetDeltaZero.addActionListener(this);

		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"3. Set delta desired Values", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridheight = 2;
		gbc_panel_1.gridwidth = 6;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 2;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 25, 29, 44, 52, 55, 29, 29, 44, 52, 49 };
		gbl_panel_1.rowHeights = new int[] { 23, 23 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0 };
		panel_1.setLayout(gbl_panel_1);

		JLabel labelValue00 = new JLabel(this.value00);
		GridBagConstraints gbc_labelValue00 = new GridBagConstraints();
		gbc_labelValue00.anchor = GridBagConstraints.EAST;
		gbc_labelValue00.insets = new Insets(0, 0, 5, 5);
		gbc_labelValue00.gridx = 0;
		gbc_labelValue00.gridy = 0;
		panel_1.add(labelValue00, gbc_labelValue00);

		// value 00
		this.txtValue00 = new JTextField("");
		GridBagConstraints gbc_txtValue00 = new GridBagConstraints();
		gbc_txtValue00.gridwidth = 2;
		gbc_txtValue00.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValue00.insets = new Insets(0, 0, 5, 5);
		gbc_txtValue00.gridx = 1;
		gbc_txtValue00.gridy = 0;
		panel_1.add(txtValue00, gbc_txtValue00);
		this.txtValueInfo00 = new JTextField("");
		GridBagConstraints gbc_txtValueInfo00 = new GridBagConstraints();
		gbc_txtValueInfo00.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValueInfo00.insets = new Insets(0, 0, 5, 5);
		gbc_txtValueInfo00.gridx = 3;
		gbc_txtValueInfo00.gridy = 0;
		panel_1.add(txtValueInfo00, gbc_txtValueInfo00);
		txtValueInfo00.setEditable(false);
		this.buttonSetValue00 = new JButton("Set");
		GridBagConstraints gbc_buttonSetValue00 = new GridBagConstraints();
		gbc_buttonSetValue00.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonSetValue00.insets = new Insets(0, 0, 5, 5);
		gbc_buttonSetValue00.gridx = 4;
		gbc_buttonSetValue00.gridy = 0;
		panel_1.add(buttonSetValue00, gbc_buttonSetValue00);
		this.buttonSetValue00.addActionListener(this);
		JLabel labelValue02 = new JLabel(this.value02);
		GridBagConstraints gbc_labelValue02 = new GridBagConstraints();
		gbc_labelValue02.anchor = GridBagConstraints.EAST;
		gbc_labelValue02.insets = new Insets(0, 0, 5, 5);
		gbc_labelValue02.gridx = 5;
		gbc_labelValue02.gridy = 0;
		panel_1.add(labelValue02, gbc_labelValue02);

		// value 02
		this.txtValue02 = new JTextField("");
		txtValue02.setEditable(false);
		GridBagConstraints gbc_txtValue02 = new GridBagConstraints();
		gbc_txtValue02.gridwidth = 2;
		gbc_txtValue02.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValue02.insets = new Insets(0, 0, 5, 5);
		gbc_txtValue02.gridx = 6;
		gbc_txtValue02.gridy = 0;
		panel_1.add(txtValue02, gbc_txtValue02);
		this.txtValueInfo02 = new JTextField("");
		GridBagConstraints gbc_txtValueInfo02 = new GridBagConstraints();
		gbc_txtValueInfo02.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValueInfo02.insets = new Insets(0, 0, 5, 5);
		gbc_txtValueInfo02.gridx = 8;
		gbc_txtValueInfo02.gridy = 0;
		panel_1.add(txtValueInfo02, gbc_txtValueInfo02);
		txtValueInfo02.setEditable(false);
		this.buttonSetValue02 = new JButton("Set");
		buttonSetValue02.setEnabled(false);
		GridBagConstraints gbc_buttonSetValue02 = new GridBagConstraints();
		gbc_buttonSetValue02.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonSetValue02.insets = new Insets(0, 0, 5, 5);
		gbc_buttonSetValue02.gridx = 9;
		gbc_buttonSetValue02.gridy = 0;
		panel_1.add(buttonSetValue02, gbc_buttonSetValue02);
		this.buttonSetValue02.addActionListener(this);
		this.buttonReadAll = new JButton("Read All");
		gbc_buttonReadValue03_1 = new GridBagConstraints();
		gbc_buttonReadValue03_1.gridheight = 2;
		gbc_buttonReadValue03_1.fill = GridBagConstraints.BOTH;
		gbc_buttonReadValue03_1.gridx = 10;
		gbc_buttonReadValue03_1.gridy = 0;
		panel_1.add(buttonReadAll, gbc_buttonReadValue03_1);
		this.buttonReadAll.addActionListener(this);
		JLabel labelValue01 = new JLabel(this.value01);
		GridBagConstraints gbc_labelValue01 = new GridBagConstraints();
		gbc_labelValue01.anchor = GridBagConstraints.EAST;
		gbc_labelValue01.insets = new Insets(0, 0, 0, 5);
		gbc_labelValue01.gridx = 0;
		gbc_labelValue01.gridy = 1;
		panel_1.add(labelValue01, gbc_labelValue01);
		
		// value 01
		this.txtValue01 = new JTextField("");
		txtValue01.setEditable(false);
		GridBagConstraints gbc_txtValue01 = new GridBagConstraints();
		gbc_txtValue01.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValue01.insets = new Insets(0, 0, 0, 5);
		gbc_txtValue01.gridwidth = 2;
		gbc_txtValue01.gridx = 1;
		gbc_txtValue01.gridy = 1;
		panel_1.add(txtValue01, gbc_txtValue01);
		this.txtValueInfo01 = new JTextField("");
		GridBagConstraints gbc_txtValueInfo01 = new GridBagConstraints();
		gbc_txtValueInfo01.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValueInfo01.insets = new Insets(0, 0, 0, 5);
		gbc_txtValueInfo01.gridx = 3;
		gbc_txtValueInfo01.gridy = 1;
		panel_1.add(txtValueInfo01, gbc_txtValueInfo01);
		txtValueInfo01.setEditable(false);
		this.buttonSetValue01 = new JButton("Set");
		buttonSetValue01.setEnabled(false);
		GridBagConstraints gbc_buttonSetValue01 = new GridBagConstraints();
		gbc_buttonSetValue01.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonSetValue01.insets = new Insets(0, 0, 0, 5);
		gbc_buttonSetValue01.gridx = 4;
		gbc_buttonSetValue01.gridy = 1;
		panel_1.add(buttonSetValue01, gbc_buttonSetValue01);
		this.buttonSetValue01.addActionListener(this);
		JLabel labelValue03 = new JLabel(this.value03);
		GridBagConstraints gbc_labelValue03 = new GridBagConstraints();
		gbc_labelValue03.anchor = GridBagConstraints.EAST;
		gbc_labelValue03.insets = new Insets(0, 0, 0, 5);
		gbc_labelValue03.gridx = 5;
		gbc_labelValue03.gridy = 1;
		panel_1.add(labelValue03, gbc_labelValue03);
		
		// value 03
		this.txtValue03 = new JTextField("");
		txtValue03.setEditable(false);
		GridBagConstraints gbc_txtValue03 = new GridBagConstraints();
		gbc_txtValue03.gridwidth = 2;
		gbc_txtValue03.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValue03.insets = new Insets(0, 0, 0, 5);
		gbc_txtValue03.gridx = 6;
		gbc_txtValue03.gridy = 1;
		panel_1.add(txtValue03, gbc_txtValue03);
		this.txtValueInfo03 = new JTextField("");
		GridBagConstraints gbc_txtValueInfo03 = new GridBagConstraints();
		gbc_txtValueInfo03.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtValueInfo03.insets = new Insets(0, 0, 0, 5);
		gbc_txtValueInfo03.gridx = 8;
		gbc_txtValueInfo03.gridy = 1;
		panel_1.add(txtValueInfo03, gbc_txtValueInfo03);
		txtValueInfo03.setEditable(false);
		this.buttonSetValue03 = new JButton("Set");
		buttonSetValue03.setEnabled(false);
		GridBagConstraints gbc_buttonSetValue03 = new GridBagConstraints();
		gbc_buttonSetValue03.insets = new Insets(0, 0, 0, 5);
		gbc_buttonSetValue03.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonSetValue03.gridx = 9;
		gbc_buttonSetValue03.gridy = 1;
		panel_1.add(buttonSetValue03, gbc_buttonSetValue03);
		this.buttonSetValue03.addActionListener(this);

		panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"4. Step response", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridheight = 2;
		gbc_panel_2.gridx = 7;
		gbc_panel_2.gridy = 2;
		add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 0.0 };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		panel_2.setLayout(gbl_panel_2);

		// Scope start:
		this.buttonStartStopScope = new JButton("Start Fast Scope");
		GridBagConstraints gbc_buttonStartStopScope = new GridBagConstraints();
		gbc_buttonStartStopScope.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonStartStopScope.insets = new Insets(0, 0, 5, 5);
		gbc_buttonStartStopScope.gridx = 0;
		gbc_buttonStartStopScope.gridy = 0;
		panel_2.add(buttonStartStopScope, gbc_buttonStartStopScope);
		this.buttonStartStopScope.addActionListener(this);

		// Export CSV
		this.buttonExportCSV = new JButton("Export CSV");
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 0, 5);
		gbc_button.fill = GridBagConstraints.HORIZONTAL;
		gbc_button.gridx = 0;
		gbc_button.gridy = 2;
		this.buttonExportCSV.addActionListener(this);
		panel_2.add(buttonExportCSV, gbc_button);

		// SERIES ENABLE
		panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"Visible Series", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.gridheight = 2;
		gbc_panel_5.gridx = 9;
		gbc_panel_5.gridy = 2;
		add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_5.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_5.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_5.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_5.setLayout(gbl_panel_5);

		boxSeries1enable = new JCheckBox("U in");
		GridBagConstraints gbc_boxSeries1enable = new GridBagConstraints();
		gbc_boxSeries1enable.insets = new Insets(0, 0, 0, 5);
		gbc_boxSeries1enable.anchor = GridBagConstraints.WEST;
		gbc_boxSeries1enable.gridx = 0;
		gbc_boxSeries1enable.gridy = 0;
		panel_5.add(boxSeries1enable, gbc_boxSeries1enable);
		boxSeries1enable.setSelected(true);

		boxSeries2enable = new JCheckBox("I in");
		GridBagConstraints gbc_boxSeries2enable = new GridBagConstraints();
		gbc_boxSeries2enable.anchor = GridBagConstraints.WEST;
		gbc_boxSeries2enable.gridx = 1;
		gbc_boxSeries2enable.gridy = 0;
		panel_5.add(boxSeries2enable, gbc_boxSeries2enable);
		boxSeries2enable.setSelected(true);
		
		boxSeries5enable = new JCheckBox("delta act");
		GridBagConstraints gbc_boxSeries5enable = new GridBagConstraints();
		gbc_boxSeries5enable.anchor = GridBagConstraints.WEST;
		gbc_boxSeries5enable.insets = new Insets(0, 0, 0, 5);
		gbc_boxSeries5enable.gridx = 0;
		gbc_boxSeries5enable.gridy = 1;
		panel_5.add(boxSeries5enable, gbc_boxSeries5enable);
		boxSeries5enable.setSelected(true);
		

		boxSeries3enable = new JCheckBox("P in");
		GridBagConstraints gbc_boxSeries3enable = new GridBagConstraints();
		gbc_boxSeries3enable.anchor = GridBagConstraints.WEST;
		gbc_boxSeries3enable.gridx = 1;
		gbc_boxSeries3enable.gridy = 1;
		panel_5.add(boxSeries3enable, gbc_boxSeries3enable);
		boxSeries3enable.setSelected(true);
		
		boxSeries6enable = new JCheckBox("U out");
		GridBagConstraints gbc_boxSeries6enable = new GridBagConstraints();
		gbc_boxSeries6enable.anchor = GridBagConstraints.WEST;
		gbc_boxSeries6enable.insets = new Insets(0, 0, 0, 5);
		gbc_boxSeries6enable.gridx = 0;
		gbc_boxSeries6enable.gridy = 2;
		panel_5.add(boxSeries6enable, gbc_boxSeries6enable);
		boxSeries6enable.setSelected(true);

		boxSeries4enable = new JCheckBox("delta set");
		GridBagConstraints gbc_boxSeries4enable = new GridBagConstraints();
		gbc_boxSeries4enable.anchor = GridBagConstraints.WEST;
		gbc_boxSeries4enable.gridx = 1;
		gbc_boxSeries4enable.gridy = 2;
		panel_5.add(boxSeries4enable, gbc_boxSeries4enable);
		boxSeries4enable.setSelected(true);

		this.boxSeries6enable.addActionListener(this);
		this.boxSeries5enable.addActionListener(this);
		this.boxSeries4enable.addActionListener(this);
		this.boxSeries3enable.addActionListener(this);
		this.boxSeries2enable.addActionListener(this);
		this.boxSeries1enable.addActionListener(this);

	}

	@Override
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId) {

	}

	@Override
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId) {
		if (this.deviceId != deviceId) {
			return;
		}
		if (!this.oneshot && this.scoperunning) {
			this.scopeDatax1.clear();
			this.scopeDatay1.clear();
			this.scopeDatay2.clear();
			this.scopeDatay3.clear();
			this.scopeDatay4.clear();
			this.scopeDatay5.clear();
			this.scopeDatay6.clear();
			this.pcdi.startOscilloscope(PCDI_OSCILLOSCOPE_COMMAND.START_ONLY, this.valNr1, this.valNr2, this.valNr3,
					this.valNr4, this.valNr5, this.valNr6, this.valNr7, undersampling, deviceId);
			infoConsole.append("Osci started again\n");
		} else {
			this.scoperunning = false;
			this.infoConsole.append("Timer Scope stopped\n");
			this.buttonStartStopScope.setText("StartScope");
			this.txt_sampleTime.setEnabled(true);
			this.txt_sampleDepth.setEnabled(true);
		}

	}

	@Override
	public void notifyConnectionChanges(boolean isConnected) {
	}
}
