package loyer.exception;

public class OutputStreamCloseFail extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public OutputStreamCloseFail() {}

  @Override
  public String toString() {
    // TODO 自动生成的方法存根
    return "关闭串口对象的输出流（OutputStream）时出错！";
  }
  
  
  
}