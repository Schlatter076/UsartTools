package loyer.exception;

public class ReadDataFromSerialFail extends Exception {
  
  private static final long serialVersionUID = 1L;
  
  public ReadDataFromSerialFail() {}

  @Override
  public String toString() {
    // TODO 自动生成的方法存根
    return "从串口读取数据时出错！";
  }
  
}