package tools.rtu.serialport.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import serialException.NoSuchPort;
import serialException.NotASerialPort;
import serialException.PortInUse;
import serialException.ReadDataFromSerialPortFailure;
import serialException.SendDataToSerialPortFailure;
import serialException.SerialPortInputStreamCloseFailure;
import serialException.SerialPortOutputStreamCloseFailure;
import serialException.SerialPortParameterFailure;
import serialException.TooManyListeners;

/**
 * 串口服务类，提供打开、关闭串口，读取、发送串口数据等服务（采用单例设计模式）
 *
 */
public class SerialTool {
	
	private static SerialTool serialTool = null;
	
	static {
		//在该类被ClassLoader加载时就初始化一个SerialTool对象
		if (serialTool == null) {
			serialTool = new SerialTool();
		}
	}
	
	//私有化SerialTool类的构造方法，不允许其他类生成SerialTool对象
	private SerialTool() {}	
	
	/**
	 * 获取提供服务的SerialTool对象
	 * @return serialTool
	 */
	public static SerialTool getSerialTool() {
		if (serialTool == null) {
			serialTool = new SerialTool();
		}
		return serialTool;
	}


	/**
	 * 查找所有可用端口
	 * @return 可用端口名称列表
	 */
	public static final ArrayList<String> findPort() {

		//获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();	
        
        ArrayList<String> portNameList = new ArrayList<>();

        //将可用串口名添加到List并返回该List
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }

        return portNameList;

    }
    
    /**
     * 打开串口
     * @param portName 端口名称
     * @param baudrate 波特率
     * @param data 数据位
     * @param stop 停止位
     * @param check 校验位
     * @return 串口对象
     * @throws SerialPortParameterFailure 设置串口参数失败
     * @throws NotASerialPort 端口指向设备不是串口类型
     * @throws NoSuchPort 没有该端口对应的串口设备
     * @throws PortInUse 端口已被占用
     */
    public static final SerialPort openPort(String portName, int baudrate, int data, int stop, int check) throws SerialPortParameterFailure, NotASerialPort, NoSuchPort, PortInUse {

        try {
            //通过端口名识别端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            //打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, 2000);

            //判断是不是串口
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                try {                    	
                    //设置一下串口的波特率等参数,数据位8，停止位1，校验位None
                    serialPort.setSerialPortParams(baudrate, data, stop, check);
                    serialPort.setInputBufferSize(1024*10);
                    serialPort.setOutputBufferSize(1024*10);
                } catch (UnsupportedCommOperationException e) {  
                	throw new SerialPortParameterFailure();
                }
                System.out.println("Open " + portName + " sucessfully !");
                return serialPort;
            } else {
            	//不是串口
            	throw new NotASerialPort();
            }
        } catch (NoSuchPortException e1) {
          throw new NoSuchPort();
        } catch (PortInUseException e2) {
        	throw new PortInUse();
        }
    }
    
    /**
     * 关闭串口
     * @param serialport 待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
    	if (serialPort != null) {
    		serialPort.close();
    		System.out.println("Close " + serialPort.getName() + " sucessfully !");
    	}
    }
    
    /**
     * 往串口发送数据
     * @param serialPort 串口对象
     * @param order	待发送数据
     * @throws SendDataToSerialPortFailure 向串口发送数据失败
     * @throws SerialPortOutputStreamCloseFailure 关闭串口对象的输出流出错
     */
    public static void sendToPort(SerialPort serialPort, byte[] order) throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure {

    	OutputStream out = null;
    	
        try {
        	if(serialPort!=null) {
            out = serialPort.getOutputStream();
            out.write(order);
            out.flush();
        	}
        } catch (IOException e) {
        	throw new SendDataToSerialPortFailure();
        } finally {
        	try {
        		if (out != null) {
        			out.close();
        			out = null;
        		}				
			} catch (IOException e) {
				throw new SerialPortOutputStreamCloseFailure();
			}
        }
        
    }
    
    
    /**
     * 从串口读取数据
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     * @throws ReadDataFromSerialPortFailure 从串口读取数据时出错
     * @throws SerialPortInputStreamCloseFailure 关闭串口对象输入流出错
     */
    public static byte[] readFromPort(SerialPort serialPort) throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {
    	//为了防止取不到完整的数据，先延时20毫秒再去读取数据
    	try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	InputStream in = null;
        byte[] bytes = new byte[0];
        try {
        	
        	in = serialPort.getInputStream();
        	
        	int bufferLenth = in.available();		//获取buffer里的数据长度
        	while (bufferLenth != 0) {
                byte[] buffer = new byte[bufferLenth];	//初始化byte数组为buffer中数据的长度
                in.read(buffer);
                byte[] newBytes = new byte[bytes.length + bufferLenth];
                System.arraycopy(buffer, 0, newBytes, bytes.length, bufferLenth);
                bytes = newBytes;
                bufferLenth = in.available();                
        	}
        	return bytes;
        } catch (IOException e) {
        	e.printStackTrace();
        	throw new ReadDataFromSerialPortFailure();
        }
        finally {
        	try {
            	if (in != null) {
            		in.close();
            		in = null;
            	}
        	} catch(IOException e) {
        		e.printStackTrace();
        		throw new SerialPortInputStreamCloseFailure();
        	}
        }
    }
    
    /**
     * 添加监听器
     * @param port     串口对象
     * @param listener 串口监听器
     * @throws TooManyListeners 监听类对象过多
     */
    public static void addListener(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {

        try {
        	
            //给串口添加监听器
            port.addEventListener(listener);
            //设置当有数据到达时唤醒监听接收线程
            port.notifyOnDataAvailable(true);
          //设置当通信中断时唤醒中断线程
            port.notifyOnBreakInterrupt(true);

        } catch (TooManyListenersException e) {
        	throw new TooManyListeners();
        }
    }
    
	/**
	 * 移除监听器
	 * 
	 * @param port     串口对象
	 */
	public static void removeListener(SerialPort port) {
		if (port != null) {
			port.removeEventListener();
			port.close();
			System.out.println("removeEventListener " + port.getName() + " sucessfully !");
		}

	}
    
    
}
