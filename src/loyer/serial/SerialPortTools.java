package loyer.serial;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

import javax.swing.JOptionPane;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import loyer.exception.NoSuchPort;
import loyer.exception.NotASerialPort;
import loyer.exception.PortInUse;
import loyer.exception.SerialPortParamFail;
import loyer.exception.TooManyListeners;

/**
 * 串口工具类
 * 
 * @author hw076
 *
 */
public class SerialPortTools {

  private static Map<String, String> map = new HashMap<>();
  static {
    for (int i = 0; i < 10; i++) {
      map.put(i + "", (30 + i) + "");
    }
    map.put("a", "41");
    map.put("b", "42");
    map.put("c", "43");
    map.put("d", "44");
    map.put("e", "45");
    map.put("f", "46");
  }

  private SerialPortTools() {
  } // 不允许其他类创建本类实例

  /**
   * 列出可用端口
   * 
   * @return
   */
  public static ArrayList<String> findPort() {
    ArrayList<String> list = new ArrayList<>();
    @SuppressWarnings("unchecked")
    Enumeration<CommPortIdentifier> comm = CommPortIdentifier.getPortIdentifiers();
    while (comm.hasMoreElements()) {
      list.add(comm.nextElement().getName());
    }
    return list;
  }

  /**
   * 重载串口获取方法
   * 
   * @param portName
   * @param baud
   * @param dataBits
   * @param stopBits
   * @param parity
   * @return
   * @throws SerialPortParamFail
   * @throws NotASerialPort
   * @throws NoSuchPort
   * @throws PortInUse
   */
  public static SerialPort getPort(String portName, int baud, int dataBits, int stopBits, int parity)
      throws SerialPortParamFail, NotASerialPort, NoSuchPort, PortInUse {
    try {
      CommPortIdentifier comm = CommPortIdentifier.getPortIdentifier(portName);
      CommPort commPort = comm.open(portName, 2000); // 打开端口，设置延时
      if (commPort instanceof SerialPort) {
        SerialPort port = (SerialPort) commPort;
        try {
          port.setSerialPortParams(baud, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
          throw new SerialPortParamFail();
        }
        return port;
      } else {
        throw new NotASerialPort();
      }
    } catch (NoSuchPortException e) {
      throw new NoSuchPort();
    } catch (PortInUseException e) {
      throw new PortInUse();
    }
  }

  /**
   * 发送字符串
   * 
   * @param port
   *          串口对象
   * @param charsetName
   *          字符集名称，如UTF-8
   * @param command
   *          待发送的指令
   * @return
   */
  public static boolean writeString(SerialPort port, String charsetName, String command) {
    try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(port.getOutputStream(), Charset.forName(charsetName)),
        true)) {
      pw.println(command);
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据发送失败::" + e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 回读字符串
   * 
   * @param port
   *          指定的串口对象
   * @param charsetName
   *          字符集名
   * @return
   */
  public static String readString(SerialPort port, String charsetName) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(port.getInputStream(), charsetName))) {
      char[] data = new char[1024];
      int len = br.read(data);
      return new String(data, 0, len);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return "";
    }
  }

  /**
   * 发送字节数组
   * 
   * @param port
   *          串口对象
   * @param datas
   *          待发送的数据
   * @return
   */
  public static boolean writeBytes(SerialPort port, byte[] datas) {
    try (BufferedOutputStream bos = new BufferedOutputStream(port.getOutputStream())) {
      bos.write(datas);
      bos.flush();
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据发送失败::" + e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 读取一个字节
   * 
   * @param port
   *          串口对象
   * @return
   */
  public static byte readByte(SerialPort port) {
    try (BufferedInputStream bis = new BufferedInputStream(port.getInputStream())) {
      return (byte) bis.read();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return -1;
    }
  }

  /**
   * 读取字节数组
   * 
   * @param port
   * @return
   */
  public static byte[] readBytes(SerialPort port) {
    try (BufferedInputStream bis = new BufferedInputStream(port.getInputStream())) {
      int len = bis.available();
      byte[] values = new byte[len];
      bis.read(values);
      return values;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return new byte[0];
    }
  }

  /**
   * 发送字符数组
   * 
   * @param port
   * @param datas
   * @param charsetName
   * @return
   */
  public static boolean writeChars(SerialPort port, char[] datas, String charsetName) {
    try (BufferedWriter bw = new BufferedWriter(
        new OutputStreamWriter(port.getOutputStream(), Charset.forName(charsetName)))) {
      bw.write(datas);
      bw.flush();
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据发送失败::" + e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 接收字符数组
   * 
   * @param port
   * @param charsetName
   * @return
   */
  public static char[] readChars(SerialPort port, String charsetName) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(port.getInputStream(), Charset.forName(charsetName)))) {
      char[] values = new char[1024];
      br.read(values);
      return values;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return new char[0];
    }
  }

  /**
   * 获取十六进制整数的ASCII码字符
   * 
   * @param ascii
   * @return
   */
  public static char byteAsciiToChar(int ascii) {
    char ch = (char) ascii;
    return ch;
  }

  /**
   * 将十六进制字符串转成字节数组
   * 
   * @param hexString
   * @return
   */
  public static byte[] toByteArray(String hexString) {
    hexString = hexString.replaceAll(" ", ""); // 去掉空格
    hexString = hexString.toLowerCase(); // 变成小写
    int len = 0;
    if (hexString.length() % 2 == 0) {
      len = hexString.length() / 2;
    } else {
      len = hexString.length() / 2 + 1;
    }
    byte[] array = new byte[len];
    int index = 0;
    for (int i = 0; i < array.length; i++) {
      byte high = (byte) (Character.digit(hexString.charAt(index), 16) & 0xff);
      byte low = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xff);
      array[i] = (byte) (high << 4 | low);
      index += 2;
    }
    return array;
  }

  /**
   * 将字节数组转换成16进制字符串
   * 
   * @param datas
   * @return
   */
  public static String bytesToHex(byte[] datas) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < datas.length; i++) {
      String hex = Integer.toHexString(datas[i] & 0xff);
      if (hex.length() < 2) {
        sb.append(0);
      }
      sb.append(hex);
      sb.append(' ');
    }
    return sb.toString();
  }

  /**
   * 回读三菱PLC数据寄存器指令生成
   * 
   * @param num
   *          数据寄存器编号
   * @return
   */
  public static String readPLCDataRegister(int num) {

    StringBuilder sb = new StringBuilder();

    int addr = num * 2 + 4096;
    int chk = 149;
    char[] cc = Integer.toHexString(addr).toLowerCase().toCharArray();
    sb.append("02");
    sb.append(" ");
    sb.append("30");
    sb.append(" ");
    for (int i = 0; i < 4; i++) {
      String val = map.get(String.valueOf(cc[i]));
      chk += Integer.parseInt(val, 16);
      sb.append(val);
      sb.append(" ");
    }
    sb.append("30");
    sb.append(" ");
    sb.append("32");
    sb.append(" ");
    sb.append("03");
    sb.append(" ");

    char[] ccc = Integer.toHexString(chk & 0xff).toCharArray();
    for (int i = 0; i < 2; i++) {
      sb.append(map.get(String.valueOf(ccc[i])));
      sb.append(" ");
    }
    return sb.toString().trim();
  }

  /**
   * 写入三菱PLC数据寄存器指令生成
   * 
   * @param num
   *          数据寄存器编号
   * @param val
   *          写入的值
   * @return
   */
  public static String writePLCDataRegister(int num, int val) {

    StringBuilder sb = new StringBuilder();
    int addr = num * 2 + 4096;
    int chk = 150;
    char[] cc = Integer.toHexString(addr).toLowerCase().toCharArray();
    sb.append("02");
    sb.append(" ");
    sb.append("31");
    sb.append(" ");
    for (int i = 0; i < 4; i++) {
      String value = map.get(String.valueOf(cc[i]));
      chk += Integer.parseInt(value, 16);
      sb.append(value);
      sb.append(" ");
    }
    sb.append("30");
    sb.append(" ");
    sb.append("32");
    sb.append(" ");
    int H = (val >> 8) & 0xff; // 取高位
    int L = val & 0xff; // 取低位
    char[] hc = String.format("%02x", H).toCharArray();
    char[] lc = String.format("%02x", L).toCharArray();

    for (int i = 0; i < 2; i++) {
      String v = map.get(String.valueOf(lc[i]));
      chk += Integer.parseInt(v, 16);
      sb.append(v);
      sb.append(" ");
    }
    for (int i = 0; i < 2; i++) {
      String v = map.get(String.valueOf(hc[i]));
      chk += Integer.parseInt(v, 16);
      sb.append(v);
      sb.append(" ");
    }
    sb.append("03");
    sb.append(" ");

    char[] ccc = Integer.toHexString(chk & 0xff).toCharArray();
    for (int i = 0; i < 2; i++) {
      sb.append(map.get(String.valueOf(ccc[i])));
      sb.append(" ");
    }
    return sb.toString().trim();
  }
  /**
   * 将从串口接收到PLC传回的数据转换成实际值
   * @param readData
   * @return
   */
  public static int checkPLCData(byte[] readData) {
    int val = -1;
    if(readData != null && readData.length > 0) {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < readData.length; i++) {
        if(String.format("%02x", readData[i]).equals("02")) {
          sb.append(byteAsciiToChar(readData[i + 3]));
          sb.append(byteAsciiToChar(readData[i + 4]));
          sb.append(byteAsciiToChar(readData[i + 1]));
          sb.append(byteAsciiToChar(readData[i + 2]));
          val = Integer.parseInt(sb.toString(), 16);
          break;
        }
      }
    }
    return val;
  }
  /**
   * 获取CRC校验码
   * @param buff
   * @return
   */
  public static String getCrc(byte[] buff) {
    int wCrc = 0xFFFF;
    for (int i = 0; i < buff.length; i++) {
      wCrc ^= (buff[i] & 0xFF); //保证buff[i]为无符号整数
      for (int j = 0; j < 8; j++) {
        if ((wCrc & 1) == 1) {
          wCrc >>= 1;
          wCrc ^= 0xA001;
        } else
          wCrc >>= 1;
      }
    }
    return String.format("%02x", wCrc & 0xff) + " " + String.format("%02x", (wCrc >> 8) & 0xff);
  }
  /**
   * 将源数组进行CRC校验后，把校验值添加到目标数组
   * @param source 原数组
   * @return 目标数组
   */
  public static byte[] addCrcCheck(byte[] source) {
    byte[] dest = new byte[source.length + 2];
    int wCrc = 0xFFFF;
    for (int i = 0; i < source.length; i++) {
      wCrc ^= (source[i] & 0xFF); //保证buff[i]为无符号整数
      for (int j = 0; j < 8; j++) {
        if ((wCrc & 1) == 1) {
          wCrc >>= 1;
          wCrc ^= 0xA001;
        } else
          wCrc >>= 1;
      }
      dest[i] = source[i]; //将原数据填充到目标数据
    }
    dest[dest.length - 2] = (byte) (wCrc & 0xff);
    dest[dest.length - 1] = (byte) ((wCrc >> 8) & 0xff);
    return dest;
  }

  /**
   * 注册监听器
   * 
   * @param port
   * @param listener
   * @throws TooManyListeners
   */
  public static void add(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {

    try {
      // 给串口添加监听器
      port.addEventListener(listener);
      // 设置当有数据到达时唤醒监听接收线程
      port.notifyOnDataAvailable(true);
      // 设置当通信中断时唤醒中断线程
      port.notifyOnBreakInterrupt(true);

    } catch (TooManyListenersException e) {
      throw new TooManyListeners();
    }
  }

  //////////////////////////////////////////////////////////////
  /**
   * serialorts表的实体
   * 
   * @author hw076
   *
   */
  public static class SerialPortData {

    private int number;
    private String PortName;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;

    public SerialPortData() {
      super();
    }

    public SerialPortData(int number, String portName, int baudRate, int dataBits, int stopBits, int parity) {
      super();
      this.number = number;
      PortName = portName;
      this.baudRate = baudRate;
      this.dataBits = dataBits;
      this.stopBits = stopBits;
      this.parity = parity;
    }

    public int getNumber() {
      return number;
    }

    public void setNumber(int number) {
      this.number = number;
    }

    public String getPortName() {
      return PortName;
    }

    public void setPortName(String portName) {
      PortName = portName;
    }

    public int getBaudRate() {
      return baudRate;
    }

    public void setBaudRate(int baudRate) {
      this.baudRate = baudRate;
    }

    public int getDataBits() {
      return dataBits;
    }

    public void setDataBits(int dataBits) {
      this.dataBits = dataBits;
    }

    public int getStopBits() {
      return stopBits;
    }

    public void setStopBits(int stopBits) {
      this.stopBits = stopBits;
    }

    public int getParity() {
      return parity;
    }

    public void setParity(int parity) {
      this.parity = parity;
    }

  }
}
