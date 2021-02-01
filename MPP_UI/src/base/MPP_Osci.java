package base;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.None;
import org.knowm.xchart.style.markers.SeriesMarkers;

import comm.I_PCDI;
import comm.I_PCDI_Listener;
import comm.PCDI_COMMANDS;
import comm.PCDI_ERROR_TYPE;
import comm.PCDI_OSCILLOSCOPE_STATE;
import comm.PCDI_OscilloscopeData;
import comm.PCDI_Parameter;
import comm.PCDI_ParameterInfo;
import comm.PCDI_TableData;
import comm.PCDI_TableInfo;

public class MPP_Osci extends JPanel implements ActionListener, I_PCDI_Listener {

	private JTextArea infoConsole;
	private I_PCDI pcdi;
	private int deviceId;
	
	private Color color1=Color.black;
	private Color color2=Color.blue;
	private Color color3=Color.red;
	private int tableIdU = 1;
	private int tableIdI = 0; 
	private int tableIdP = 2;
	private int valNr_pointU = 14;
	private int valNr_pointI = 15;
	private int index_equalZeroU = 0;
	
	private Timer cyclicTimerSlow  = null;
	private Timer cyclicTimerFast  = null;
	
	private String nameScope1 = "I/U curve";
	private String nameScope2 = "P";
	private String nameScope3 = "act. Point";
	private String nameYAxisScope1 = "I/A";
	private String nameYAxisScope2 = "P/W";
	
	private final XYChart chart;
	private JPanel chartPanel;
	private List<Double> data_U = new ArrayList<Double>();
	private List<Double> data_I = new ArrayList<Double>();
	private List<Double> data_P = new ArrayList<Double>();
	private List<Double> data_U_point = new ArrayList<Double>();
	private List<Double> data_I_point = new ArrayList<Double>();
	private int sampleDepth = 100;
	private XYSeries XYSeries1;
	private XYSeries XYSeries2;
	private XYSeries XYSeries3;
	private double maxI = 10;
	private double minI = 0;
	private double maxP = 1000;
	private double minP = 0;
	
	public MPP_Osci(I_PCDI pcdi, int deviceId, JTextArea infoConsole) {
		super();
		
		
		this.infoConsole = infoConsole;
		this.deviceId = deviceId;
		this.pcdi = pcdi;
		this.pcdi.registerListener(this);
		
		// Create Chart
		this.chart = new XYChartBuilder().width(MainWindow.width).height(400).theme(ChartTheme.Matlab).xAxisTitle("U/V")
				.yAxisTitle("Y").build();

		// Customize Chart
		chart.getStyler().setLegendPosition(LegendPosition.InsideNE);
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);

		// Series
		XYSeries1 = this.chart.addSeries(this.nameScope1, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries1.setYAxisGroup(0);
		XYSeries1.setMarker(new None());
		XYSeries1.setLineColor(color1);

		
		XYSeries2 = this.chart.addSeries(this.nameScope2, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries2.setYAxisGroup(1);
		XYSeries2.setMarker(new None());;
		//XYSeries2.setLineWidth(0.8f);
		XYSeries2.setLineColor(color2);
		
		XYSeries3 = this.chart.addSeries(this.nameScope3, new double[] { 0.0 }, new double[] { 0.0 }, null);
		XYSeries3.setYAxisGroup(0);
		XYSeries3.setMarker(new None());;
		XYSeries3.setLineColor(color2);
		XYSeries3.setMarkerColor(color3);
		XYSeries3.setMarker(SeriesMarkers.CIRCLE);
		

		// axis 
		this.chart.setYAxisGroupTitle(0, this.nameYAxisScope1);
		this.chart.setYAxisGroupTitle(1, this.nameYAxisScope2);
		chart.getStyler().setYAxisMin(0, this.minI);
		chart.getStyler().setYAxisMax(0, this.maxI);
		chart.getStyler().setYAxisMin(1, this.minP);
		chart.getStyler().setYAxisMax(1, this.maxP);

		chart.getStyler().setYAxisGroupTitleColor(0, color1);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(0, color1);
		chart.getStyler().setYAxisGroupTitleColor(1, color2);
		chart.getStyler().setYAxisGroupTickLabelsColorMap(1, color2);


		// chart
		chartPanel = new XChartPanel<XYChart>(chart);
		GridBagConstraints c_8 = new GridBagConstraints();
		c_8.gridwidth = 10;
		c_8.insets = new Insets(0, 0, 5, 0); // top padding
		c_8.fill = GridBagConstraints.BOTH;
		c_8.weightx = 0.1;
		c_8.weighty = 1.0;
		c_8.gridx = 0;
		c_8.gridy = 0;
		this.add(chartPanel, c_8);
		
		this.data_U.clear();
		this.data_U.add(0.0);
		this.data_U.add(5.0);
		this.data_U.add(8.0);
		this.data_U_point.add(5.0);
		this.data_I.add(5.0);
		this.data_I.add(5.0);
		this.data_I_point.add(8.0);
		this.data_I.add(0.0);
		this.data_P.add(0.0);
		this.data_P.add(800.000);
		this.data_P.add(0.0);
		this.chart.updateXYSeries(this.nameScope1, data_U, data_I, null);
		this.chart.updateXYSeries(this.nameScope2, data_U, data_P, null);
		this.chart.updateXYSeries(this.nameScope3, data_U_point, data_I_point, null);
		
		
	}

	private void startTimerFast() {
		if(this.cyclicTimerFast==null) {
			TimerTask task = new TimerTask() {
		        public void run() {
		        	pcdi.readParameter(valNr_pointU, deviceId);
		        	pcdi.readParameter(valNr_pointI, deviceId);
		        }
		    };
		    this.cyclicTimerFast=new Timer();
		    this.cyclicTimerFast.scheduleAtFixedRate(task, 0, 5000);
		}
	}
	
	private void stopTimerFast() {
		if(this.cyclicTimerFast!=null) {
			this.cyclicTimerFast.cancel();
			this.cyclicTimerFast=null;
		}
	}
	private void startTimerSlow() {
		if(this.cyclicTimerSlow==null) {
			TimerTask task = new TimerTask() {
		        public void run() {
		        	pcdi.getTableInfo(1, deviceId);
		        	pcdi.getTableInfo(0, deviceId);
		        	pcdi.getTableInfo(2, deviceId);
		        }
		    };
		    this.cyclicTimerSlow=new Timer();
		    this.cyclicTimerSlow.scheduleAtFixedRate(task, 0, 30000);
		}
	}
	
	private void stopTimerSlow() {
		if(this.cyclicTimerSlow!=null) {
			this.cyclicTimerSlow.cancel();
			this.cyclicTimerSlow=null;
		}
	}

	@Override
	public void notifyConnectionChanges(boolean isConnected) {
		// TODO Auto-generated method stub
		if(isConnected) {
			startTimerFast();
			startTimerSlow();
		}
		else {
			stopTimerFast();
			stopTimerSlow();
		}

	}

	@Override
	public void notifyParameterWrite(PCDI_Parameter<?> parameter, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyCommandExcecuted(PCDI_COMMANDS command, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyParameterRead(PCDI_Parameter<?> parameter, int deviceId) {
		// TODO Auto-generated method stub
		if(parameter.getValueNumber()==this.valNr_pointI) {
			this.data_I_point.clear();
			this.data_I_point.add(Double.valueOf(parameter.getValue().toString()));
			this.chart.updateXYSeries(this.nameScope3, data_U_point, data_I_point, null);
		}else if(parameter.getValueNumber()==this.valNr_pointU) {
			this.data_U_point.clear();
			this.data_U_point.add(Double.valueOf(parameter.getValue().toString()));
			this.chart.updateXYSeries(this.nameScope3, data_U_point, data_I_point, null);
		}
		
	}

	@Override
	public void notifyParameterInfo(PCDI_ParameterInfo parameter, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyCommandInvalid(PCDI_COMMANDS command, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyParameterInvalid(int valueNr, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyOscilloscopeData(PCDI_OscilloscopeData data, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyError(PCDI_ERROR_TYPE error, int deviceId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void notifyOscilloscopeStateAnswer(PCDI_OSCILLOSCOPE_STATE osci_state, int deviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyTableInfo(PCDI_TableInfo table, int deviceId) {
		// TODO Auto-generated method stub
		this.infoConsole.append(table.toString());
		if(table.getTableId()==this.tableIdI)
			this.data_I.clear();
		if(table.getTableId()==this.tableIdU)
			this.data_U.clear();
		if(table.getTableId()==this.tableIdP)
			this.data_P.clear();
		if(table.getTableId()==this.tableIdI || table.getTableId()==this.tableIdU || table.getTableId()==this.tableIdP) {
			for(int i=0;i<=table.getSize();i++) {
				this.pcdi.readTableIndex(table.getTableId(), i, this.deviceId);
			}
		}
		
	}

	@Override
	public void notifyTableRead(PCDI_TableData tableData, int deviceId) {
		// TODO Auto-generated method stub
		int minDataPoints=tableData.getIndexNr();
		int minUequalzero =0;
		for(int i=0;i<minDataPoints;i++) {
			if(this.data_U.get(i)==0)
				minUequalzero=i;
		}
		if(tableData.getTableId()==this.tableIdI) {
			this.data_I.add(tableData.getIndexNr(),(double)tableData.getValue());
			if(this.data_U.size()<minDataPoints)
				minDataPoints=this.data_U.size();
			this.chart.updateXYSeries(this.nameScope1, this.data_U.subList(minUequalzero, minDataPoints), this.data_I.subList(minUequalzero, minDataPoints), null);
		
		}
		if(tableData.getTableId()==this.tableIdU) {
			this.data_U.add(tableData.getIndexNr(),(double)tableData.getValue());
			if(this.data_P.size()<minDataPoints)
				minDataPoints=this.data_P.size();
			this.chart.updateXYSeries(this.nameScope2, this.data_U.subList(minUequalzero, minDataPoints), this.data_P.subList(minUequalzero, minDataPoints), null);
			if(this.data_I.size()<minDataPoints)
				minDataPoints=this.data_I.size();
			this.chart.updateXYSeries(this.nameScope1, this.data_U.subList(minUequalzero, minDataPoints), this.data_I.subList(minUequalzero, minDataPoints), null);
		}
		if(tableData.getTableId()==this.tableIdP) {
			if(this.data_U.size()<minDataPoints)
				minDataPoints=this.data_U.size();
			if(tableData.getValue()==0)
				minUequalzero=tableData.getTableId();
			this.data_P.add(tableData.getIndexNr(),(double)tableData.getValue());
			this.chart.updateXYSeries(this.nameScope2, this.data_U.subList(minUequalzero, minDataPoints), this.data_P.subList(minUequalzero, minDataPoints), null);
		}

		
		
	}

	@Override
	public void notifyTableWrite(PCDI_TableData tableData, int deviceId) {
		// TODO Auto-generated method stub
		
	}

}
