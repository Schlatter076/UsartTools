package loyer.exception;

public class SendToPortFail extends Exception {
  
  private static final long serialVersionUID = 1L;
  
  public SendToPortFail() {}

  @Override
  public String toString() {
    // TODO 自动生成的方法存根
    return "往串口发送数据失败！";
  }
  
  
}