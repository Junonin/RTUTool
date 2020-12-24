package tools.rtu.serialport.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import serialException.ExceptionWriter;
import serialException.NoSuchPort;
import serialException.NotASerialPort;
import serialException.PortInUse;
import serialException.ReadDataFromSerialPortFailure;
import serialException.SendDataToSerialPortFailure;
import serialException.SerialPortInputStreamCloseFailure;
import serialException.SerialPortOutputStreamCloseFailure;
import serialException.SerialPortParameterFailure;
import serialException.TooManyListeners;
import tools.rtu.serialport.ApplicationClient;
import tools.rtu.serialport.utils.RtuFileUtils;
import tools.rtu.serialport.utils.SerialTool;

/**
 * 监测数据显示类
 *
 */
@SuppressWarnings("rawtypes")
public class DataView extends Frame {
	private static final long serialVersionUID = 1L;
	ApplicationClient applicationClient = null;

	private boolean isTimeOut = true; // 超时标记
	Object lock_lastGetDataTime = new Object();

	private long lastGetDataTime = -1;

	private List<String> commList = null; // 保存可用端口号
	private SerialPort serialPort = null; // 保存串口对象
	private Font font = new Font("微软雅黑", Font.BOLD, 20);
	private JComboBox commChoice = new JComboBox(); // 串口选择（下拉框）
	private JComboBox bpsChoice = new JComboBox(); // 波特率选择
	private JComboBox stopChoice = new JComboBox(); // 停止位选择
	private JComboBox dataChoice = new JComboBox(); // 数据位选择
	private JComboBox checkChoice = new JComboBox(); // 数据位选择
	private JComboBox datayearChoice = new JComboBox(); // 年份选择
	private JComboBox datamonthChoice = new JComboBox(); // 年份选择

	private void addInputer(int x, int y, int width_component, int width_label, int height, JComponent[] items,
			JLabel itemLabels[]) {
		for (int i = 0; i < items.length; i++) {
			itemLabels[i].setBounds(x, y + i * height, width_label, height - 3);
			itemLabels[i].setFont(font);
			items[i].setBounds(x + width_label + 1, y + i * height, width_component, height - 3);
		}
	}

	private void addButton(int x, int y, int width_component, int height, JButton[] items) {
		for (int i = 0; i < items.length; i++) {
			items[i].setFont(font);
			items[i].setBounds(x, y + i * height, width_component, height - 5);
		}

	}

	private JButton open = new JButton("打开串口");
	private JButton close = new JButton("关闭串口");
	private JButton read = new JButton("读取");
	private JLabel commLabel = new JLabel("串口号:");
	private JLabel bpsLabel = new JLabel("波特率:");
	private JLabel stopLabel = new JLabel("停止位:");
	private JLabel dataLabel = new JLabel("数据位:");
	private JLabel checkLabel = new JLabel("校验位:");
	private JLabel yearLabel = new JLabel("数据年份:");
	private JLabel monthLabel = new JLabel("数据月份:");

	private static JTextArea messagetext = new JTextArea();
	Image offScreen = null; // 重画时的画布
	// 创建一个进度条
	public JProgressBar progressBar = new JProgressBar();
	private static final int MIN_PROGRESS = 0;
	private static final int MAX_PROGRESS = 100;
	// 设置window的icon
	Toolkit toolKit = getToolkit();
	Image icon = toolKit.getImage(DataView.class.getResource("computer.png"));

	static public void waitting() {
		messagetext.setText("正在生成文件。。。请稍等。。。");
	}

	/**
	 * 类的构造方法
	 *
	 * @param applicationClient
	 */
	public DataView(ApplicationClient applicationClient) {
		this.applicationClient = applicationClient;
		commList = SerialTool.findPort(); // 程序初始化时就扫描一次有效串口
	}

	/**
	 * 主菜单窗口显示； 添加Label、按钮、下拉条及相关事件监听；
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void dataFrame() {

		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高

		this.setBounds((screenWidth + applicationClient.appwinWidth) / 2,
				(screenHeight - applicationClient.appwinHeight) / 2, applicationClient.appwinWidth,
				applicationClient.appwinHeight); // 设定程序在桌面出现的位置
		this.setTitle("远动RTU助手程序V1.0.1");
		this.setIconImage(icon);
		this.setBackground(Color.white);
		this.setLayout(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (serialPort != null) {
					// 程序退出时关闭串口释放资源
					SerialTool.closePort(serialPort);
				}
				System.exit(0);
			}
		});
		// 添加串口选择选项， 检查是否有可用串口，有则加入选项中
		if (commList == null || commList.size() < 1) {
			commChoice.addItem("None");
			JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
		} else {
			for (String s : commList) {
				commChoice.addItem(s);
			}
		}

		// addInputer(int x, int y, int width_component, int width_label, int height,
		// JComponent[] items, JLabel itemLabels[])
		int width_component = 180;
		int width_label = 120;
		addInputer(22, 50, width_component, width_label, 30,
				new JComponent[] { commChoice, bpsChoice, dataChoice, stopChoice, checkChoice, datayearChoice, datamonthChoice },
				new JLabel[] { commLabel, bpsLabel, dataLabel, stopLabel, checkLabel, yearLabel , monthLabel });
		addButton(22, datamonthChoice.getBounds().y + datamonthChoice.getHeight() + 10, width_component + width_label, 42,
				new JButton[] { open, close, read });
		// 添加波特率选项
		bpsChoice.addItem("115200");
//		bpsChoice.addItem("1200");
//		bpsChoice.addItem("2400");
//		bpsChoice.addItem("4800");
//		bpsChoice.addItem("9600");
//		bpsChoice.addItem("14400");
//		bpsChoice.addItem("19200");
//		bpsChoice.addItem("38400");
//		bpsChoice.addItem("57600");
		// 添加数据位选项
		dataChoice.addItem("8");
//		dataChoice.addItem("5");
//		dataChoice.addItem("6");
//		dataChoice.addItem("7");
		// 添加停止位选项
		stopChoice.addItem("1");
//		stopChoice.addItem("1.5");
//		stopChoice.addItem("2");
		// 添加校验位选项
		checkChoice.addItem("None");
//		checkChoice.addItem("Even");
//		checkChoice.addItem("Odd");
//		checkChoice.addItem("Mark");
//		checkChoice.addItem("Space");

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int anIndex = 0;
		for (int i = 2020; i < 2100; i++) {
			if (year == i) {
				anIndex = i - 2020;
			}
			datayearChoice.addItem(String.valueOf(i));
		}
		datayearChoice.setSelectedIndex(anIndex);

		datamonthChoice.addItem("全年");
		int month = cal.get(Calendar.MONTH);
		for (int i = 1; i < 13; i++) {
			datamonthChoice.addItem(String.valueOf(i));
		}
		datamonthChoice.setSelectedIndex(month);


		// 添加打开串口按钮
		// 添加打开串口按钮的事件监听
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		// 添加关闭串口按钮
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				close();
			}
		});
		// 读取文件按钮
		read.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				requestReadFromRtuData();
			}
		});
		progressBar.setFont(font);
		// 设置进度的 最小值 和 最大值
		progressBar.setMinimum(MIN_PROGRESS);
		progressBar.setMaximum(MAX_PROGRESS);
		progressBar.setBounds(22, this.read.getBounds().y + this.read.getBounds().height + 10, this.read.getWidth(),
				20);
		// 绘制百分比文本（进度条中间显示的百分数）
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);//是否显示

		messagetext.setEditable(false);
		messagetext.setRows(10);
		messagetext.setBounds(22, this.progressBar.getBounds().y + this.progressBar.getBounds().height + 10,
				this.progressBar.getWidth(), 100);
		messagetext.setLineWrap(true); // 激活自动换行功能
		messagetext.setWrapStyleWord(true); // 激活断行不断字功能
		messagetext.setFont(new Font("微软雅黑", Font.PLAIN, 14));

		add(yearLabel);
		add(monthLabel);
		add(commLabel);
		add(bpsLabel);
		add(stopLabel);
		add(dataLabel);
		add(checkLabel);
		add(commChoice);
		add(bpsChoice);
		add(stopChoice);
		add(dataChoice);
		add(checkChoice);
		add(datayearChoice);
		add(datamonthChoice);
		add(open);
		add(close);
		add(read);
		add(progressBar);
		add(messagetext);
		this.setResizable(false);
		new Thread(new RepaintThread()).start(); // 启动重画线程

	}

	/**
	 * 双缓冲方式重画界面各元素组件
	 */
	@Override
	public void update(Graphics g) {
		if (offScreen == null)
			offScreen = this.createImage(ApplicationClient.appwinWidth, ApplicationClient.appwinHeight);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, ApplicationClient.appwinWidth, ApplicationClient.appwinHeight); // 重画背景画布
		this.paint(gOffScreen); // 重画界面元素
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null); // 将新画好的画布“贴”在原画布上
	}

	SerialListener serialListener;

	public int stopType(String stopStr) {
		switch (stopStr) {
		case "1.5":
			return SerialPort.STOPBITS_1_5;
		case "1":
			return SerialPort.STOPBITS_1;
		case "2":
			return SerialPort.STOPBITS_2;
		default:
			return 0;
		}
	}

	public int checkType(String checkStr) {
		switch (checkStr) {
		case "None":
			return SerialPort.PARITY_NONE;
		case "Even":
			return SerialPort.PARITY_EVEN;
		case "Odd":
			return SerialPort.PARITY_ODD;
		case "Mark":
			return SerialPort.PARITY_MARK;
		case "Space":
			return SerialPort.PARITY_SPACE;
		default:
			return 0;
		}
	}

	public int dataType(String dataStr) {
		switch (dataStr) {
		case "5":
			return SerialPort.DATABITS_5;
		case "6":
			return SerialPort.DATABITS_6;
		case "7":
			return SerialPort.DATABITS_7;
		case "8":
			return SerialPort.DATABITS_8;
		default:
			return 0;
		}
	}

	public void open() {
		// 获取串口名称
		String commName = commChoice.getSelectedItem().toString();
		// 获取波特率
		String bpsStr = bpsChoice.getSelectedItem().toString();
		// 获取数据位
		String dataStr = dataChoice.getSelectedItem().toString();
		// 获取停止位
		String stopStr = stopChoice.getSelectedItem().toString();
		// 获取校验位
		String checkStr = checkChoice.getSelectedItem().toString();
		// 检查串口名称是否获取正确
		if (commName == null || commName.equals("")) {
			JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
		} else {
			// 检查波特率是否获取正确
			if (bpsStr == null || bpsStr.equals("")) {
				JOptionPane.showMessageDialog(null, "波特率获取错误！", "错误", JOptionPane.INFORMATION_MESSAGE);
			} else {
				// 串口名、波特率均获取正确时
				int bps = Integer.parseInt(bpsStr);
				try {
					// 获取指定端口名及波特率的串口对象
					serialPort = SerialTool.openPort(commName, bps, dataType(dataStr), stopType(stopStr),
							checkType(checkStr));
					// 监听成功进行提示
					JOptionPane.showMessageDialog(null, "打开串口成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
					messagetext.setText("打开串口 " + commName + " 成功！");
					open.setEnabled(false);
				} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse e1) {
					// 发生错误时使用一个Dialog提示具体的错误信息
					JOptionPane.showMessageDialog(null, e1, "错误", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	public void close() {
		if (serialPort != null) {
			closeAll();
			JOptionPane.showMessageDialog(null, "关闭串口成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "没有开启的串口！", "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	Thread timeOutThread = null;

	/**
	 *
	 * @Description: 发送命令给串口: 请求从RTU读取文件内容
	 * @date: 2020年4月22日 上午11:25:43
	 * @author: JUNO void
	 */
	public void requestReadFromRtuData() {
		// 获取年份
		String timeYear = datayearChoice.getSelectedItem().toString();
		//获取月份
		String timeMonth = datamonthChoice.getSelectedItem().toString();
		if(timeMonth == "全年") {
			timeMonth =  "13";
		}
		try {
			if (serialPort != null) {
				// 在该串口对象上添加监听器
				if(serialListener == null) {
				serialListener = new SerialListener();
				SerialTool.addListener(serialPort, serialListener);
				}
				messagetext.setText("发送读取文件命令到RTU设备，等待回复。。。" );
				SerialTool.sendToPort(serialPort, rtuFileUtils.getRtuCmd_GetRtuDataFile(timeYear,timeMonth));
				read.setEnabled(false);
				isTimeOut = true;
				if (timeOutThread == null){
						timeOutThread = new Thread(new ReadDataTimeOutThread());
						timeOutThread.start();
						lastGetDataTime = System.currentTimeMillis();
			}else {
					timeOutThread.start(); // 点击读取文件的时候启动线程检测是否超时
			}
				}
		} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure | TooManyListeners e) {
			JOptionPane.showMessageDialog(null, "发送读取文件命令到RTU设备失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}
	}

	/*
	 * 重画线程（每隔30毫秒重画一次）
	 */
	private class RepaintThread implements Runnable {
		@Override
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) {
				// 调用重画方法
				repaint();
				// 扫描可用串口
				commList = SerialTool.findPort();
				if (commList != null && commList.size() > 0) {
					// 添加新扫描到的可用串口
					for (String s : commList) {
						// 该串口名是否已存在，初始默认为不存在（在commList里存在但在commChoice里不存在，则新添加）
						boolean commExist = false;
						for (int i = 0; i < commChoice.getItemCount(); i++) {
							if (s.equals(commChoice.getItemAt(i))) {
								// 当前扫描到的串口名已经在初始扫描时存在
								commExist = true;
								break;
							}
						}
						if (commExist) {
							// 当前扫描到的串口名已经在初始扫描时存在，直接进入下一次循环
							continue;
						} else {
							// 若不存在则添加新串口名至可用串口下拉列表
							commChoice.addItem(s);
							RtuFileUtils.debugPrintln("add" + s);
						}
					}
					// 移除已经不可用的串口
					for (int i = 0; i < commChoice.getItemCount(); i++) {
						// 该串口是否已失效，初始默认为已经失效（在commChoice里存在但在commList里不存在，则已经失效）
						boolean commNotExist = true;
						for (String s : commList) {
							if (s.equals(commChoice.getItemAt(i))) {
								commNotExist = false;
								break;
							}
						}
						if (commNotExist) {
							RtuFileUtils.debugPrintln("remove" + commChoice.getItemAt(i));
							commChoice.removeItemAt(i);
						} else {
							continue;
						}
					}
				} else {
					// 如果扫描到的commList为空，则移除所有已有串口
					commChoice.removeAllItems();
				}

				if (isTimeOut) {
					progressBar.setMaximum(rtuFileUtils.total_Package);
					progressBar.setValue(rtuFileUtils.getProgressBarValue());
				}

				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
	}

	/**
	 *
	 * @Description: 超时判断
	 * @date: 2020年4月22日 下午8:55:38
	 * @author: JUNO
	 * @return boolean
	 */
	boolean isReadDataTimeOut() {
		synchronized (lock_lastGetDataTime) {
			if (lastGetDataTime > 0)
				return System.currentTimeMillis() - lastGetDataTime > 30000; // 30秒没有数据传回来超时
			return false;
		}
	}

	private class ReadDataTimeOutThread implements Runnable {
		// 每10秒检测一下是否超时
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (isTimeOut) {
				System.err.println("ReadDataTimeOutThread。。。" + System.currentTimeMillis() + "...now..." + lastGetDataTime );
				if (isReadDataTimeOut()) {
					isTimeOut = false;
					System.err.println("读取文件已经超时！！！");
					JOptionPane.showMessageDialog(null, "读取文件已经超时！！！", "错误", JOptionPane.INFORMATION_MESSAGE);
					progressBar.setVisible(false);
					close();
					rtuFileUtils.clear();
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}

	}

	private void closeAll() {
		rtuFileUtils.clear();
		SerialTool.closePort(serialPort);
		serialPort = null;
		isTimeOut = false;
		serialListener = null;
		timeOutThread = null;
		synchronized (lock_lastGetDataTime) {
			lastGetDataTime = -1;
		}
		progressBar.setVisible(false);
		open.setEnabled(true);
		read.setEnabled(true);
		messagetext.setText("");
		repaint();
	}

	private void isdone() {
		rtuFileUtils.clear();
		isTimeOut = false;
		timeOutThread = null;
		synchronized (lock_lastGetDataTime) {
			lastGetDataTime = -1;
		}
		read.setEnabled(true);
		progressBar.setVisible(false);
		repaint();
	}


	private RtuFileUtils rtuFileUtils = new RtuFileUtils();

	/**
	 * 以内部类形式创建一个串口监听类
	 *
	 */
	private class SerialListener implements SerialPortEventListener {

		/**
		 * 处理监控到的串口事件
		 */
		@Override
		public void serialEvent(SerialPortEvent serialPortEvent) {
//			 串口读取程序在监听到数据到来以后，等待50ms再读取数据，这时候就没有数据断行的存在了。
//			 try { Thread.sleep(50); } catch (InterruptedException e1) {
//			 e1.printStackTrace(); }
			switch (serialPortEvent.getEventType()) {
			case SerialPortEvent.BI: // 10 通讯中断
				JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
				break;
			case SerialPortEvent.OE: // 7 溢位（溢出）错误
			case SerialPortEvent.FE: // 9 帧错误
			case SerialPortEvent.PE: // 8 奇偶校验错误
			case SerialPortEvent.CD: // 6 载波检测
			case SerialPortEvent.CTS: // 3 清除待发送数据
			case SerialPortEvent.DSR: // 4 待发送数据准备好了
			case SerialPortEvent.RI: // 5 振铃指示
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				break;
			case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
				synchronized (lock_lastGetDataTime) {
					lastGetDataTime = System.currentTimeMillis();
				}
				byte[] data = null;
				try {
					if (serialPort == null) {
						JOptionPane.showMessageDialog(null, "串口对象为空！监听失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
					} else {
						// 有接收到数据
						data = SerialTool.readFromPort(serialPort); // 读取数据，存入字节数组

						// 自定义解析过程
						if (data == null || data.length < 1) { // 检查数据是否读取正确
							JOptionPane.showMessageDialog(null, "读取数据过程中未获取到有效数据！请检查设备或程序！", "错误",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						} else {
							// 数据开始处理
							boolean appendok = rtuFileUtils.appendPacketData(data);
//							messagetext.setText(RtuFileUtils
//									.debugPrintln("收数据长度= " + data.length + "," + rtuFileUtils.getPacketCountInfo()+ ", 数据有效=" + appendok));
							messagetext.setText(rtuFileUtils.getPacketCountInfo()+ ", 数据有效=" + appendok);
							progressBar.setVisible(true);
							if (rtuFileUtils.checkValidPacket()) {
//								messagetext.setText(RtuFileUtils
//										.debugPrintln("收数据长度= " + data.length + "," + rtuFileUtils.getPacketCountInfo()+ ", 数据包完整有效! "));
								rtuFileUtils.appendRtuData();
								// 串口读取程序在监听到完整数据到来以后，等待150ms再返回指令
								try {
									Thread.sleep(150);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}

								try {
									// 给RTU进行确认的条件：当收到一个完整有效的数据包才给RTU发送接收确认；
									SerialTool.sendToPort(serialPort, rtuFileUtils.getSendToRtuConfirmData());
								} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e) {
									e.printStackTrace();
								}

								// 解析数据
								if (rtuFileUtils.paraseRtuData()) {
									String fileName = rtuFileUtils.rtuDataTmpFilePath;
									if (fileName != null) {
										isdone();
										messagetext.setText("已生成RTU数据文件，文件路径为\n" + fileName);
										JOptionPane.showMessageDialog(null, "已生成RTU数据文件，文件路径为\n" + fileName, "信息",
												JOptionPane.INFORMATION_MESSAGE);
									}
								} else {
									JOptionPane.showMessageDialog(null, "数据解析过程出错，请检查设备或程序！", "错误",
											JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								}
							}

						}
					}
				} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
					JOptionPane.showMessageDialog(null, e, "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0); // 发生读取错误时显示错误信息后退出系统
				}
				break;
			}
		}
	}
}
