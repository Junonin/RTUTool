package tools.rtu.serialport.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

import org.junit.jupiter.api.Test;

import tools.rtu.serialport.awt.DataView;

public class RtuFileUtils {

	/**
	 * 解析RTU数据文件
	 *
	 * @return 1、第1次接收数信息：总共有多少帧数据，每帧数据的大小 2、判断是否接收完整，如果接收完整后，打包生成到本地文件中
	 */
	public boolean paraseRtuData() {
		if (checkReceiveDataPacketEnd()) {
			DataView.waitting();
			byte[] fullRtuData = this.mergeRtuData();
			this.writeRtuDataToTmpFile(fullRtuData);
		}
		return true;
	}

	/**
	 * 第1次接收数信息：总共有多少帧数据，每帧数据的大小
	 */
	private void paraseDataFirstPacket(byte[] data) {
		total_Package = getPacketIndexFromPacket(data);
	}

	/**
	 *
	 * @Description: 发生超时，清空已有的数据，数据归零
	 * @date: 2020年4月22日 下午8:52:41
	 * @author: JUNO void
	 */
	public void doGetDataTimeOut() {
		this.clear();
	}

	byte[] packetData = new byte[0];

	/**
	 * 检查数据包的头部内容是否正确
	 * 正确返回 true
	 * @date: 2020年4月26日 下午7:17:10
	 * @author: JUNO
	 * @param data
	 * @return  boolean
	 */
	private boolean checkValidPacketHeader(byte[] data)
	{
		if (data.length < 3 ||  data[0] != 0x68 || data[2] != 0x68)
			return false;
		return true;
	}

	/**
	 * 从已经接收的内容中，找出否合包文件开头的内容
	 * 比如从已接收的内容： 干扰数据xxxx0x68..0x68aaaa 找出 0x68..0x68aaaa 内容左右有效的包的一部分内容
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @date: 2020年4月26日 下午6:54:08
	 * @author: JUNO
	 * @return  byte[]
	 */
	private byte[] findValidPacket()
	{
		for(int i=0; i<packetData.length; i++)
		{
			byte[] validPacket = new byte[packetData.length-i];
			System.arraycopy(packetData, i, validPacket, 0, packetData.length-i);
			if (this.checkValidPacketHeader(validPacket))
				return validPacket;
		}
		return new byte[0];
	}

	/**
	 * @Description: 检测已经接收到数据是否是一个以 0x16结尾完整的数据包； 如果不是则继续接收数据
	 * @date: 2020年4月23日 下午8:32:25
	 * @author: JUNO
	 * @param data
	 * @return boolean
	 */
	public  boolean checkValidPacket()
	{
		boolean isValid = checkValidPacket0();
		if (!isValid)
		{
			tryCheckValidPacket();
			return false;
		}
		return true;
	}

	int tryCheckValidPacket =0;
	private void tryCheckValidPacket()
	{
		tryCheckValidPacket ++;
		if (tryCheckValidPacket > 2 )
		{
			/*
			 * 如果不是一个完整的数据包内容，需要清除下 packetData 缓存数据，重新从RTU获取数据包
			 * 累计超过3次判断，都没有得到一个完整的有效的数据包，则清理缓存数据，重新接收RTU数据
			 */
			String x = bytesTohexString(packetData);
			debugPrintln("tryCheckValidPacket 检查完整的数据包无效，tryCheckValidPacket=" +tryCheckValidPacket + ",len=" + packetData.length + ",    " +   x.toUpperCase());
			clearPacketData();
		}
	}

	private void clearPacketData()
	{
		this.packetData = new byte[0];
		tryCheckValidPacket=0;
	}

	private boolean checkValidPacket0() {
		byte[] data = this.packetData;
		if(data.length == 0)
			return false;

		//报文的结束符判断
		if (data[data.length - 1] != 0x16)
			return false;

		/*
		 * RTU 中的程序代码 Photo_data[0] = 0x68; Photo_data[1] = 0x01; Photo_data[2] = 0x68;
		 * Photo_data[3] = 0xF0;//ctrl
		 */
		if (byteToUnsignedByte(data[3]) != 0xF0 || data[10] != 0x61)
			return false;

		// rtuDataFileLen为RTU文件内容的大小（不包括报文中的地址等内容信息）
		// int rtuDataFileLen = this.getRtuDataSizeFromPacket(data);
		// 用户数据域内容的字节长度 = 一个完整的报文的长度 - 5个字节（头/尾部/cs校验符号共5个字节）
		byte cs = crc8(data, 3, data.length - 5);

		if (cs != data[data.length - 2])
			return false;

		return true;
	}

	/**
	 * 从数据包中获取 测站编码 测试地址，也就是 rtucode内容，从报文内容的第6个字节开始， 共占用5个字节，转换为字符后为 10个字符
	 *
	 * @param data
	 * @return 参考 @see {@link #rtu_sourceCode_61()} 中的rtu代码
	 */
	static private String getRtucodeFromPacket(byte[] data) {
		// StringBuffer rtucode = new StringBuffer();
		int beginIndex = 5;
		String rtucode = bytesTohexString(data, 2, beginIndex, beginIndex + 5);
		return rtucode;
	}

	public String getRtucode() {
		return getRtucodeFromPacket(this.dataPacketList.get(0));
	}

	/**
	 * 获取当前帧数据中的 帧的序号值 递减
	 *
	 * @param packetData
	 * @return
	 */
	static private int getPacketIndexFromPacket(byte[] data) {
		return byteToUnsignedByte(data[4]);
	}

	/**
	 * RTU数据文件总长度
	 *
	 * @param data
	 * @return
	 */
	private int getRtuDataSizeFromPacket(byte[] data) {
		return byteToUnsignedByte(data[12]) << 8 | byteToUnsignedByte(data[11]);
	}

	private byte[] getRtuDataFromPacket(byte[] data) {
		int size = getRtuDataSizeFromPacket(data);
		byte[] rtuData = new byte[size];
		System.arraycopy(data, 13, rtuData, 0, size);
		return rtuData;
	}

	/**
	 *
	 * @Description: 获取当前包数
	 * @date: 2020年4月23日 上午10:06:35
	 * @author: JUNO
	 * @return int
	 */
	public int getProgressBarValue() {
		return this.dataPacketList.size();
	}

	/**
	 * 检查数据是否接收结束和完整
	 *
	 * @return
	 */
	private boolean checkReceiveDataPacketEnd() {
		return this.total_Package == this.dataPacketList.size();
	}

	public String rtuDataTmpFilePath;

	/**
	 * 将已读取到的RTU数据文件内容写入到本地的临时文件，并返回文件的路径名
	 *
	 * @return
	 * @throws java.io.IOException
	 */

	private void writeRtuDataToTmpFile(byte[] rtuData) {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		String deskPath = com.getPath();
		String rtucode = getRtucode();
		String name = deskPath + "/雨情报表_" + rtucode + "_" + fileData + ".txt";
		rtuDataTmpFilePath = writeRtuDataToTmpFile(name, rtuData);
	}

	static private String writeRtuDataToTmpFile(String filename, byte[] rtuData) {
		File file;
		try {
			if (filename != null)
				file = new File(filename);
			else
				file = createTempFile(filename, true);
			try (final java.io.FileOutputStream os = new java.io.FileOutputStream(file);) {
				if (rtuData != null)
					os.write(rtuData);
			}
			String rtuDataTmpFilePath = file.getAbsolutePath();
			debugPrintln("文件存放路径：" + rtuDataTmpFilePath);
			return rtuDataTmpFilePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 追加内容到已有的数据包缓存中
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @date: 2020年4月26日 下午7:08:03
	 * @author: JUNO
	 * @param data
	 * @return  boolean
	 * 如果收到的数据作为有效的数据被采纳，返回 true，并且需要给RTU发送接收确认，
	 * 否则返回false，不需要给RTU发送接收确认，RTU需要重新发送数据上来
	 */
	public boolean appendPacketData(byte[] data) {
		if (!this.checkValidPacketHeader(data)  && packetData.length == 0)
		{
			System.err.println("无效报头的数据: data.len=" + data.length + "--- "  + bytesTohexString(data).toUpperCase());
			return false;
		}

		//如果数据不是 0x16 结尾、等非crc校验无效的场景下，但已有的packetData 缓存数据是有效的数据报头的情况下，先和缓存数据拼接在一起，再次判断下是否是一个完整的数据包
		byte[] newPacketData = new byte[packetData.length + data.length];
		System.arraycopy(packetData, 0, newPacketData, 0, packetData.length);
		System.arraycopy(data, 0, newPacketData, packetData.length, data.length);
		this.packetData = newPacketData;
		//this.packetData = findValidPacket();
		return true;
	}

	/**
	 * 追加内容到已有的RTU数据缓存中
	 *
	 * @param data
	 */
	public void appendRtuData() {
		byte[] data = this.packetData;
		//String x = bytesTohexString(data);
		//debugPrintln("debug appendRtuData=" + x.toUpperCase());

		if (this.total_Package <= 0) {
			paraseDataFirstPacket(data);
		}
		appendRtuDataPacket(data);
		debugPrintln(getPacketCountInfo());
		clearPacketData();
	}

	synchronized public String getPacketCountInfo() {
		String msg1 = "  正在接收包中已收到有效数据长度=" + this.packetData.length;
		if (dataPacketList.size() == 0)
			return "数据包数为0！" + msg1;
		//String rtucode = getRtucode();
		int index = getPacketIndexFromPacket(this.dataPacketList.get(dataPacketList.size() - 1));
		//return "RTU= " + rtucode + "总包数 " + total_Package + ",当前包序号=" + index + ",累计接收包数=" + dataPacketList.size()+ msg1;
		return  "总包数 " + total_Package + ",当前包序号=" + index + ", 第 " + dataPacketList.size()+ "包收到回复。";

	}

	void rtu_sourceCode_61() {
		/*
		 * case 0x61: Photo_data[0] = 0x68; Photo_data[1] = 0x01; Photo_data[2] = 0x68;
		 * Photo_data[3] = 0xF0;//ctrl for(i=0;i<5;i++) { Photo_data[5+i] =
		 * Data->msg_addr[i];//测站编码 } Photo_data[10] = 0x61;//AFN
		 * SendLen=(Photo_data[12]<<8)|Photo_data[11];//图片总长 SendLen+=13; //CRC
		 * Photo_data[SendLen] = CRC8(Photo_data+3, SendLen-3); SendLen++;
		 * Photo_data[SendLen] = 0x16; SendLen++;
		 *
		 * UART4_Data_Send(Photo_data,SendLen);
		 */
	}

	/**
	 * 有数据拆包的情况下：增加一个新收到的数据小包到缓存dataPacketList中
	 *
	 * @param data
	 */
	private void appendRtuDataPacket(byte[] data) {
		if (data != null && data.length > 0) {
			dataPacketList.add(data);
		}
	}

	/**
	 * 合并RTU数据到一个文件中
	 */
	protected byte[] mergeRtuData() {
		Object[] mergeDataPacketArray = new Object[total_Package];
		for (int i = 0; i < total_Package; i++) {
			byte[] rtuDataItem = dataPacketList.get(i);
			int index = getPacketIndexFromPacket(rtuDataItem);
			// 计数帧（数据包的序号）是递减方式传送的，序号最大的帧所在的数据包才是文件的开始内容
			mergeDataPacketArray[total_Package - index] = getRtuDataFromPacket(rtuDataItem);
		}
		/*
		 * 将所有读到的各个数据包中的RTU文件内容数据合并到一起; 需要按帧的顺序号，按顺序把多个包的数据合并成一个大的文件数据包
		 */
		byte[] fullRtuData = new byte[0];
		for (int i = 0; i < total_Package; i++) {
			byte[] rtuDataItem = (byte[]) mergeDataPacketArray[i];
			byte[] newRtuData = new byte[fullRtuData.length + rtuDataItem.length];
			System.arraycopy(fullRtuData, 0, newRtuData, 0, fullRtuData.length);
			System.arraycopy(rtuDataItem, 0, newRtuData, fullRtuData.length, rtuDataItem.length);
			fullRtuData = newRtuData;
		}
		return fullRtuData;
	}

	/**
	 * 返回给RTU的确认帧内容 按水资源协议标准，在收到RTU的信息时，需要给RTU设备返回的确认的标记
	 *
	 * 返回的示例内容说明： rtucode：4506030007 下行确认帧内容：68086800450603000761016C16
	 * 68-08-68-00-4506030007-61-01-6C-16
	 */
	public byte[] getSendToRtuConfirmData() {
		String cmdPacketStr = "6808680000000000016101CC16";
		byte[] data = hexStringToBytes(cmdPacketStr);
//		byte[] rtucode =  hexStringToBytes(this.getRtucode());

		// data[3] = 0x00; //接收到RTU传过来的第1个数据包后，此处使用0x00
		// data 4-8 共5个字节 为 rtucode 地址内容
//		for(int i=0; i<rtucode.length; i++)
//			data[4+i] = rtucode[i];
		// 总长度13 - 头部内容长度 3 - cs和最后0x16 两个字节长度
		byte cs = crc8(data, 3, 13 - 3 - 2);
		data[11] = cs;
		return data;
	}


	private String fileData;
	/**
	 * 召测命令：获取RTU数据文件
	 *
	 * @return
	 */
	public byte[] getRtuCmd_GetRtuDataFile(String timeYear , String timeMonth) {
		if(Integer.parseInt(timeMonth)<10) {
			fileData = timeYear + "0" +timeMonth;
		}else if(Integer.parseInt(timeMonth)>12) {
			fileData = timeYear;
		}else {
			fileData = timeYear + timeMonth;}
		System.err.println(fileData);
		String stryear = timeYear.substring(timeYear.length() - 2, timeYear.length());
		byte year = (byte) Integer.parseInt(stryear);
		byte month = (byte)Integer.parseInt(timeMonth);
		String cmdPacketStr = "680B6830000000000161010000" + stryear + "CC16";
		byte[] data = hexStringToBytes(cmdPacketStr);
		/*
		 * data 字节4-8 总共5个字节内容为 rtucode 地址内容 ， rtucode 可以不用传了，先给个 AAA1234AAA 0000000001
		 * 接收到RTU传过来的第1个数据包后，此处返回0x30, 收到后面的包时此处这个值为0x00 data[3] = 0x30; //0x30,
		 * 后面的帧这个值为0x00
		 */
		data[12] = month;
		data[13] = year;
		byte cs = crc8(data, 3, 16 - 3 - 2);
		data[14] = cs;
		return data;
	}

	static final int BufferSize = 1024;

	/**
	 * 由于RTU设备的内存比较小（最大也只有100K左右），不能一次性从RTU设备中读取到全部的文件内容，所以需要分包读取文件内容。
	 * 每次能够从RTU设备中读取的文件一般为1K 左右
	 *
	 * 示例： RTU文件：95k，RTU设备上的程序分10次传完 总帧数 = 10，每帧大小10K，
	 * 大部分情况RTU都是按顺序从1到10发每帧的数据，同时在每帧数据中有记录当前帧的序号是第几帧。最好应用程序中再根据帧的序号，检查组成要的完整数据；
	 */
	// private byte[] buffer = new byte[BufferSize];
	/**
	 * 接收数据包缓存
	 */
	private ArrayList<byte[]> dataPacketList = new ArrayList<byte[]>();

	/**
	 * RTU文件大小
	 */
	int rtuDataFileSize;

	/**
	 * 包计数
	 */
	int package_Cnt;

	/**
	 * 总包数 收到的最大拼接包序号 计数总包数
	 */
	public int total_Package = 0;

	public void clear() {
		this.dataPacketList.clear();
		this.rtuDataTmpFilePath = null;
		total_Package = 0;
		clearPacketData();
		rtuDataTmpFilePath = null;
	}

	final static public java.io.File createTempFile(String fileName, boolean deleteOnExit) throws IOException {
		if (fileName == null)
			return null;
		fileName = fileName.replace('\\', '/');
		int p = fileName.lastIndexOf('/');
		if (p >= 0)
			fileName = fileName.substring(p + 1);
		p = fileName.lastIndexOf('.');
		// System.err.println("fileName="+fileName);
		String suffix = p >= 0 ? fileName.substring(p) : "";
		String prefix = p >= 0 ? fileName.substring(0, p) : fileName;
		if (prefix.length() > 16)
			prefix = prefix.substring(0, 16);
		if (prefix.length() < 3)
			prefix = prefix + newString('_', 3 - prefix.length());
		File tmpFile = File.createTempFile(prefix, suffix);
		if (deleteOnExit)
			tmpFile.deleteOnExit();
		return tmpFile;
	}

	final static public void writeFile(String file, byte data[]) throws IOException {
		try (final java.io.FileOutputStream os = new java.io.FileOutputStream(file);) {
			if (data != null)
				os.write(data);
			os.flush();
			os.close();
		}
	}

	/**
	 * 指定一个字符及字符的个数，生成一个字符串 示例：StrUtil.newString('a',10); 结果：aaaaaaaaaa
	 *
	 * @param c     指定的字符
	 * @param count 字符出现的次数
	 * @return 新字符串
	 */
	public static final String newString(char c, int count) {
		final char a[] = new char[count > 0 ? count : 0];
		for (int i = 0; i < a.length; i++)
			a[i] = c;
		return new String(a);
	}

	/**
	 * CRC 校验算法
	 *
	 * @param data      传入的数据内容
	 * @param fromIndex 要校验数据内容的在传入数据中的起始位置
	 * @param len       要校验的数据内容的长度
	 * @return
	 */
	static public byte crc8(byte[] data, int fromIndex, int len) {
		int crc = 0;
		for (int i = 0; i < len; i++) {
			crc = (crc ^ byteToUnsignedByte(data[i + fromIndex])) & 0xFF;
			for (int j = 0; j < 8; j++) {
				if ((crc & 0x80) == 0x80)
					crc = ((crc << 1) ^ 0xE5) & 0xFF;
				else
					crc = (crc << 1) & 0xFF;
			}
		}
		return (byte) (crc & 0xFF);
	}

	/**
	 * 将字符串内容，转换为 byte 数组
	 *
	 * @param hexString
	 * @param fixedCharCount 每个16进制的数字占用的固定字符个数
	 * @return 1、用于水资源协议的报文中的内容解析； 2、RTU设备都是按无符号16进制值解释判断的 3、字符串中的内容 每2个字符构成一个16
	 *         进制的数字，并做为转换后的 1个字节 比如 680168F0 对应的 0x68 0x01 0x68 0xF0 4个字节内容 其中
	 *         数字的字符 0 转换为 byte 值 0
	 */
	static public byte[] hexStringToBytes(String hexString, int fixedCharCount) {
		byte[] byteValues = new byte[hexString.length() / fixedCharCount];
		for (int i = 0; i < hexString.length() / fixedCharCount; i++) {
			String s = hexString.substring(i * fixedCharCount, i * fixedCharCount + fixedCharCount);
			int intValue = Integer.parseInt(s, 16);
			byteValues[i] = (byte) intValue;
		}
		return byteValues;
	}

	/**
	 * 将 byte 数组 转换为 字符串内容
	 *
	 * @param hexBytes
	 * @param fixedCharCount 每个字节在转换后的字符串中占用的字符个数。默认2个
	 * @param beginIndex     从byte数组开始转换的的起始位置，默认为 0
	 * @param endIndex       需要转换的byte 从 beginIndex 位置开始，到 endIndex位置结束。< 0 时表示需要转换从
	 *                       beginIndex 位置开始的后面的所有的字节
	 * @return 字符串中的内容 每2个字符构成一个16 进制的数字，并做为转换前的 1个字节
	 * @see #hexStringToBytes(String)
	 */
	static public String bytesTohexString(byte[] hexBytes, int fixedCharCount, int beginIndex, int endIndex) {
		StringBuffer hexString = new StringBuffer();
		if (endIndex < 0 || endIndex > hexBytes.length)
			endIndex = hexBytes.length;

		String s0 = newString('0', fixedCharCount);
		for (int i = beginIndex; i < endIndex; i++) {
			// 如果 字节 == 0 时，字符串内容对应的补 0
			String x = (s0 + Integer.toHexString(byteToUnsignedByte(hexBytes[i])));
			x = x.substring(x.length() - fixedCharCount, x.length());
			hexString.append(x);
		}
		return hexString.toString();
	}

	static public String bytesTohexString(byte[] hexBytes) {
		return bytesTohexString(hexBytes, 2, 0, -1);
	}

	static public byte[] hexStringToBytes(String hexString) {
		return hexStringToBytes(hexString, 2);
	}

	@Test
	public void Test() {
		// crc8(data, 3, data.length-5);
		byte[] data = hexStringToBytes("680168F0070A00000094610004CD289BC19B2BD29C2A481370A49612A6A944AB8CA334D3C521352CA40C6ABCA2A7351B8CD48C816A4149B694F1400869A4D0C78A8C9A076158D30D19A69340EC349A6E695AA226985890B715131A42D494868514868A43486148C71480D3246A4302D48C6A1DDCD296A634389A6934CCD213C5432D215CD444D23B53334C2C39CD479A534D22A6E55809A4278A28352D90CF9B4CC4D2AC734A405526BB8D27C1B2CA4129D79AEC34DF0422852E9DBD2BD56D2391B3C861D22E65C7CA467DAAFC7E1D9CA8254D7B5C5E1BB685395518A64D67670AE3009F6A8755740B9E2173A14B19E47356B4CD09E5619535E8F7F6914D2ED8D00ABBA6E991C4830BCE2B2957B1BC299CF68FE17C95CAE2BB8D234286155CA0C81E9566C20000E3A56EC10F158B9B636AC2D95B471801540AD28D6A189715612A443D569E571421C535DA93006E2998A42D4D2D9A7615C314AA9482A54E94982055C529A09A314805538A47930298E7155DD8D24805965A8C7342AD4F145577B0B71624AB51253A18B15385C0A872B95623DB480629EDC542F262A79AC3B0E2454324B8A89E5AA571354399A4605BFB47350CF271546393E6A9A56E2927729C6C55958E6A355A730E69C82ADB010A802A0948153487154A67C9E2A6371B43B7548B55549A94356DCC66E23DA914D26695297393C849BE9D135302E6A78A2AB5225C49E2E94F65E29D147814E75AD1332656208AC8D4D0B2366B698E0567DE80CA78AB6C83CF358B3C392056215C1C576FAA41BB3815CE5CDA8DE48152A7635B5CA50DBEEA9FCB9601B8038AD0B0831DB8C55CB8894C4C3DAB58D7309C0A3A7EA04B619ABB4F0FDF82CA3757955E486DAE0ED2715ADA16B7E45C2966F9735B7B3B99B6D1F41E912070063B56D35B0715C3F85B538E78E3DADDBD6BBBB4995940CF6A9E4B18AAA66DC58904F15424B72B5D708C30A825B00C381DA938DCDA354E4C8C546D5BD73A79C1F96B2AE2D1D09E2B174CDD4EE52269A4D3A4422A33D2A1A2D08C6A334E34D352508698C69C6984D2B8C6B544D5231A89A98C6D3A900A750D8094D6A71A6B5218C271514878A7B531A81911EB484D2B7148280434D28A56A074A866888DC5443AD5861509A0621A691C53A8349811D21A53486A1B259A4BF64B51F2A826AADD6AD8044631DAB28C8CE78A72C58E4D755EC73F20CB8B89A73CB102ABECAB6454B14350E66B18D8A715B735AB676A768C8A7C100AD4821C62A0B6C8EDEDF15A312E05222D4CA28B90C541530A4518A5A7710A4D318D38D35A992C898D0B4F09522A531088B4EA785A9238EA1B191AAD3B6E2AC2A629B22E295C0A6F5108F3560AE4D4B1C755B0886286AD45153D140A947159B65A40060531DB8A4924AAB2CBC54DCAB0B3480551966CD366933C5442A5B34487139A8A619A940A6B2D4EE55CAC8B8A1CD4C5AE16");
		int x = byteToUnsignedByte(crc8(data, 3, data.length - 5));
		System.err.println(x);
		getRtuCmd_GetRtuDataFile("2020","13");
	}

	/**
	 * 将字节 value 转换为无符号的值 比如 (byte)234 有符号的值为 -22 转换为无符号后的 数值为 234
	 *
	 * @param value
	 * @return
	 */
	static public int byteToUnsignedByte(byte value) {
		return value & 0xFF;
	}

	static public String debugPrintln(String message) {
		Date currentTime = new Date();
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssS");
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ssS");
		String dateString = formatter.format(currentTime);
		String msg = dateString + " " + message;
		System.out.println(msg);
		return msg;
	}
}