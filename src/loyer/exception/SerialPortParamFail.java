package loyer.exception;

public class SerialPortParamFail extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public SerialPortParamFail() {}
  
  @Override
  public String toString() {
    // TODO 自动生成的方法存根
    return "设置串口参数失败！打开串口操作未完成！";
  }
  
}