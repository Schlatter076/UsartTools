package loyer.exception;

public class NotASerialPort extends Exception {
  
  private static final long serialVersionUID = 1L;
  
  public NotASerialPort() {}

  @Override
  public String toString() {
    // TODO 自动生成的方法存根
    return "连接设备可能不是串口类型，打开串口操作失败！";
  }
  
  
}