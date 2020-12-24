package tools.rtu.serialport;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import serialException.ExceptionWriter;
import tools.rtu.serialport.awt.DataView;

/**
 * 主程序
 *
 * @author zhong
 *
 */
public class ApplicationClient extends Frame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 程序界面宽度
	 */
	public static final int appwinWidth = 340;

	/**
	 * 程序界面高度
	 */
	public static final int appwinHeight = 500;

	Color color = Color.WHITE;
	Image offScreen = null; // 用于双缓冲

	// 设置window的icon（这里我自定义了一下Windows窗口的icon图标，因为实在觉得哪个小咖啡图标不好看 = =）
	Toolkit toolKit = getToolkit();
	Image icon = toolKit.getImage(DataView.class.getResource("computer.png"));

	// 持有其他类
	DataView dataview = new DataView(this); // 主界面类（显示监控数据主面板）

	/**
	 * 主方法
	 *
	 */
	public static void main(String[] args) {
		new ApplicationClient().launchFrame();
	}

	/**
	 * 显示主界面
	 */
	public void launchFrame() {
		Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包
		Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸
		int screenWidth = screenSize.width; //获取屏幕的宽
		int screenHeight = screenSize.height; //获取屏幕的高

		this.setBounds((screenWidth+appwinWidth)/2, (screenHeight-appwinHeight)/2, appwinWidth, appwinHeight); // 设定程序在桌面出现的位置
		this.setTitle("远动RTU助手程序V1.0.1"); // 设置程序标题
		this.setIconImage(icon);
		this.setBackground(Color.white); // 设置背景色

		this.addWindowListener(new WindowAdapter() {
			// 添加对窗口状态的监听
			public void windowClosing(WindowEvent arg0) {
				// 当窗口关闭时
				System.exit(0); // 退出程序
			}

		});

		this.addKeyListener(new KeyMonitor()); // 添加键盘监听器
		this.setResizable(false); // 窗口大小不可更改
		this.setVisible(true); // 显示窗口

		new Thread(new RepaintThread()).start(); // 开启重画线程
	}

	/**
	 * 画出程序界面各组件元素
	 */
	public void paint(Graphics g) {
		Color c = g.getColor();

		g.setFont(new Font("微软雅黑", Font.BOLD, 30));
		g.setColor(Color.black);
		g.drawString("欢迎使用远动RTU助手", 20, 100);

		g.setFont(new Font("微软雅黑", Font.ITALIC, 18));
		g.setColor(Color.BLACK);
		g.drawString("Version：1.0.1", 20, 430);
		g.drawString("Powered By：www.gdremote.com", 20, 450);

		g.setFont(new Font("微软雅黑", Font.BOLD, 20));
		g.setColor(color);
		g.drawString("— —点击Enter键进入主界面— —", 20, 480);
		// 使文字 "————点击Enter键进入主界面————" 黑白闪烁
		if (color == Color.WHITE)
			color = Color.black;
		else if (color == Color.BLACK)
			color = Color.white;

	}

	/**
	 * 双缓冲方式重画界面各元素组件
	 */
	public void update(Graphics g) {
		if (offScreen == null)
			offScreen = this.createImage(appwinWidth, appwinHeight);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, appwinWidth, appwinHeight); // 重画背景画布
		this.paint(gOffScreen); // 重画界面元素
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null); // 将新画好的画布“贴”在原画布上
	}

	/*
	 * 内部类形式实现对键盘事件的监听
	 */
	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER) { // 当监听到用户敲击键盘enter键后执行下面的操作
				setVisible(false); // 隐去欢迎界面
				dataview.setVisible(true); // 显示监测界面
				dataview.dataFrame(); // 初始化监测界面
			}
		}

	}

	/*
	 * 重画线程（每隔250毫秒重画一次）
	 */
	private class RepaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// 重画线程出错抛出异常时创建一个Dialog并显示异常详细信息
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}

	}

}
