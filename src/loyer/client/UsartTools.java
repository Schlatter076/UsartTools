package loyer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import loyer.exception.NoSuchPort;
import loyer.exception.NotASerialPort;
import loyer.exception.PortInUse;
import loyer.exception.SerialPortParamFail;
import loyer.exception.TooManyListeners;
import loyer.serial.SerialPortTools;

public class UsartTools {

  private JFrame frame;
  private JTabbedPane tabbedPane;
  private JPanel leftPane;
  private JPanel rightPane;
  private final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
  private final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;  
  private JMenuBar menuBar;
  private JMenu helpMenu;
  private JMenuItem tipItem;
  private JMenuItem aboutItem;
  
  private JButton[] modButts = new JButton[6];
  /**接收缓冲区*/
  private JTextArea rxArea;
  /**发送缓冲区*/
  private JTextArea txArea;
  private JRadioButton rxStrButt;
  private JRadioButton rxHexButt;
  private JButton clearRxButt;
  private JButton saveRxButt;
  private JRadioButton txStrButt;
  private JRadioButton txHexButt;
  private JButton clearTxButt;
  private JButton saveTxButt;
  private JButton transFileButt;
  private JButton transDataButt;
  private JButton autoTransButt;
  private JTextField cycleField;
  private JComboBox<String> portListBox;
  private JComboBox<String> baudBox;
  private JComboBox<String> parityBox;
  private JComboBox<String> stopBitBox;
  private JComboBox<String> dataBitBox;
  private JButton openPort;
  /**串口对象*/
  private SerialPort COM1;
  private ArrayList<String> portList = SerialPortTools.findPort();
  /** 换行符 */
  public static final String SEPARATOR = System.getProperty("line.separator");
  private Timer timer1;
  private JTextField rxCountField;
  private JTextField txCountField;
  private JButton clearCountButt;

  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UsartTools window = new UsartTools();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public UsartTools() {
    initialize();
  }
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    
    try {
      //将界面风格设置成和系统一置
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    }//*/
    
    frame = new JFrame("调试助手");
    //frame.setResizable(false);  //窗口大小不可更改
    frame.setBounds(WIDTH / 4, HEIGHT / 6, WIDTH / 2, HEIGHT * 2 / 3);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    // 窗口"X"关闭事件
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        close();
      }
    });
    
    menuBar = new JMenuBar();
    helpMenu = new JMenu("帮助(H)");
    tipItem = new JMenuItem("提示与技巧(T)...");
    aboutItem = new JMenuItem("关于(A)");
    helpMenu.add(tipItem);
    helpMenu.addSeparator();
    helpMenu.add(aboutItem);
    menuBar.add(helpMenu);
    frame.setJMenuBar(menuBar);
    tipItem.addActionListener(e -> tips());
    aboutItem.addActionListener(e -> about());
    
    
    tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    leftPane = new JPanel(new BorderLayout(5, 10));
    leftPane.setBorder(new TitledBorder(new EtchedBorder(), "串口调试助手", TitledBorder.CENTER, 
        TitledBorder.TOP, new Font("等线", Font.PLAIN, 13), Color.BLACK));
    
    rxArea = new JTextArea();
    rxArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    JScrollPane rxPane = new JScrollPane(rxArea);
    rxPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    rxStrButt = new JRadioButton("文本模式");
    rxStrButt.addActionListener(e -> {
      if(rxStrButt.isSelected()) {
        rxHexButt.setSelected(false);
      } else if(!rxHexButt.isSelected()) {
        rxStrButt.setSelected(true);
      }
    });
    rxHexButt = new JRadioButton("HEX模式", true);
    rxHexButt.addActionListener(e -> {
      if(rxHexButt.isSelected()) {
        rxStrButt.setSelected(false);
      } else if(!rxStrButt.isSelected()) {
        rxHexButt.setSelected(true);
      }
    });
    clearRxButt = new JButton("清空接收区");
    clearRxButt.addActionListener(e -> {
      rxArea.setText("");
    });
    saveRxButt = new JButton("保存接收数据");
    JPanel rxButtPanel = new JPanel(new GridLayout(4, 1, 5, 5));
    rxButtPanel.add(rxStrButt);
    rxButtPanel.add(rxHexButt);
    rxButtPanel.add(clearRxButt);
    rxButtPanel.add(saveRxButt);
    JPanel rxPanel = new JPanel(new BorderLayout(5, 5));
    rxPanel.setBorder(new TitledBorder(new EtchedBorder(), "接收缓冲区", TitledBorder.LEFT, TitledBorder.TOP));
    rxPanel.add(rxPane, BorderLayout.CENTER);
    rxPanel.add(rxButtPanel, BorderLayout.WEST);
    
    txArea = new JTextArea();
    txArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    JScrollPane txPane = new JScrollPane(txArea);
    txPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    txStrButt = new JRadioButton("文本模式");
    txStrButt.addActionListener(e -> {
      if(txStrButt.isSelected()) {
        txHexButt.setSelected(false);
      } else if(!txHexButt.isSelected()) {
        txStrButt.setSelected(true);
      }
    });
    txHexButt = new JRadioButton("HEX模式", true);
    txHexButt.addActionListener(e -> {
      if(txHexButt.isSelected()) {
        txStrButt.setSelected(false);
      } else if(!txStrButt.isSelected()) {
        txHexButt.setSelected(true);
      }
    });
    clearTxButt = new JButton("清空发送区");
    clearTxButt.addActionListener(e -> {
      txArea.setText("");
    });
    saveTxButt = new JButton("保存发送数据");
    JPanel txButtPanel = new JPanel(new GridLayout(4, 1, 5, 5));
    txButtPanel.add(txStrButt);
    txButtPanel.add(txHexButt);
    txButtPanel.add(clearTxButt);
    txButtPanel.add(saveTxButt);
    
    JPanel botPanel = new JPanel(new GridLayout(1, 4, 5, 5));
    transFileButt = new JButton("发送文件");
    transFileButt.setEnabled(false);
    transDataButt = new JButton("发送数据");
    transDataButt.setEnabled(false);
    transDataButt.addActionListener(e -> {
      transData();
    });
    autoTransButt = new JButton("自动发送");
    autoTransButt.setEnabled(false);
    autoTransButt.addActionListener(e -> {
      autoTrans();
    });
    cycleField = new JTextField("500");
    cycleField.setHorizontalAlignment(JTextField.CENTER);
    cycleField.setFont(new Font("宋体", Font.PLAIN, 12));
    JPanel cyclePanel = new JPanel(new GridLayout(1, 2));
    cyclePanel.add(new JLabel("周期(ms)") {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void setHorizontalAlignment(int alignment) {
        super.setHorizontalAlignment(JLabel.RIGHT);
      }
    });
    cyclePanel.add(cycleField);
    
    botPanel.add(transFileButt);
    botPanel.add(transDataButt);
    botPanel.add(autoTransButt);
    botPanel.add(cyclePanel);
    
    JPanel txPanel = new JPanel(new BorderLayout(5, 5));
    txPanel.setBorder(new TitledBorder(new EtchedBorder(), "发送缓冲区", TitledBorder.LEFT, TitledBorder.TOP));
    txPanel.add(txPane, BorderLayout.CENTER);
    txPanel.add(txButtPanel, BorderLayout.WEST);
    txPanel.add(botPanel, BorderLayout.SOUTH);
    
    JPanel jp = new JPanel(new GridLayout(2, 1, 5, 5));
    jp.add(rxPanel);
    jp.add(txPanel);
    
    portListBox = new JComboBox<>();
    baudBox = new JComboBox<>();
    parityBox = new JComboBox<>();
    stopBitBox = new JComboBox<>();
    dataBitBox = new JComboBox<>();
    SerialParam param1 = new SerialParam("串口", portListBox);
    SerialParam param2 = new SerialParam("波特率", baudBox);
    SerialParam param5 = new SerialParam("数据位", dataBitBox);
    SerialParam param3 = new SerialParam("校验位", parityBox);
    SerialParam param4 = new SerialParam("停止位", stopBitBox);
    JPanel paramPanel = new JPanel(new GridLayout(1, 5, 20, 5));
    for(String s : portList) {
      portListBox.addItem(s);
    }
    baudBox.addItem("9600");
    baudBox.addItem("2400");
    baudBox.addItem("19200");
    parityBox.addItem("0");
    parityBox.addItem("1");
    parityBox.addItem("2");
    stopBitBox.addItem("1");
    dataBitBox.addItem("8");
    dataBitBox.addItem("5");
    dataBitBox.addItem("6");
    dataBitBox.addItem("7");
    dataBitBox.addItem("9");
    
    paramPanel.add(param1);
    paramPanel.add(param2);
    paramPanel.add(param5);
    paramPanel.add(param3);
    paramPanel.add(param4);
    
    openPort = new JButton("打开串口");
    openPort.setFont(new Font("微软雅黑", Font.PLAIN, 20));
    openPort.addActionListener(e -> {
      openPortListener();
    });
    JPanel openPortPanel = new JPanel(new BorderLayout(10, 5));
    rxCountField = new JTextField("0");
    txCountField = new JTextField("0");
    SerialParam rxCount = new SerialParam("接收", rxCountField);
    SerialParam txCount = new SerialParam("发送", txCountField);
    JPanel countPanel = new JPanel(new GridLayout(2, 1));
    countPanel.add(rxCount);
    countPanel.add(txCount);
    
    openPortPanel.add(openPort, BorderLayout.WEST);
    openPortPanel.add(new JLabel("注：先查看参数，然后打开串口") {

      private static final long serialVersionUID = 1L;

      @Override
      public void setHorizontalAlignment(int alignment) {
        super.setHorizontalAlignment(JLabel.LEFT);
      }
      @Override
      public void setForeground(Color fg) {
        super.setForeground(Color.BLUE);
      }
    }, BorderLayout.CENTER);
    openPortPanel.add(countPanel, BorderLayout.EAST);
    
    JPanel portPanel = new JPanel(new BorderLayout(5, 10));
    portPanel.add(paramPanel, BorderLayout.NORTH);
    portPanel.add(openPortPanel, BorderLayout.CENTER);
    clearCountButt = new JButton("清零");
    clearCountButt.addActionListener(e -> {
      rxCountField.setText("0");
      txCountField.setText("0");
    });
    portPanel.add(clearCountButt, BorderLayout.EAST);
    
    leftPane.add(jp, BorderLayout.CENTER);
    leftPane.add(portPanel, BorderLayout.SOUTH);
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    rightPane = new JPanel(new BorderLayout(5, 5));
    rightPane.setBorder(new TitledBorder(new EtchedBorder(), "电机参数设置", TitledBorder.CENTER, 
        TitledBorder.TOP, new Font("等线", Font.PLAIN, 13), Color.BLACK));
    tabbedPane.addTab("串口工具", leftPane);
    
    tabbedPane.addTab("位置参数", rightPane);
    
    JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
    panel.setBorder(new TitledBorder("参数列表"));
    
    rightPane.add(panel, BorderLayout.CENTER);
    JPanel bp = new JPanel(new GridLayout(1, 6, 10, 5));
    modButts[0] = new JButton("X前进");
    modButts[1] = new JButton("X后退");
    modButts[2] = new JButton("Z前进");
    modButts[3] = new JButton("Z后退");
    modButts[4] = new JButton("电机停止");
    modButts[5] = new JButton("回原点");
    bp.setBorder(new TitledBorder("电机控制"));
    for(int i = 0; i < 6; i++) {
      modButts[i].addActionListener(e -> {
        buttProcess(e.getActionCommand());
      });
      bp.add(modButts[i]);
    }
    rightPane.add(bp, BorderLayout.SOUTH);
    
    frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    
    //自动发送定时任务
    timer1 = new Timer(Integer.parseInt(cycleField.getText()), e1 -> {
      
      String str = txArea.getText();
      if(txHexButt.isSelected()) {
        byte[] data = SerialPortTools.toByteArray(str);
        SerialPortTools.writeBytes(COM1, SerialPortTools.toByteArray(txArea.getText()));
        int count = Integer.parseInt(txCountField.getText()) + data.length;
        txCountField.setText(count + "");
      } else if(txStrButt.isSelected()) {
          SerialPortTools.writeString(COM1, "UTF-8", str);
          int count = Integer.parseInt(txCountField.getText()) + str.length();
          txCountField.setText(count + "");
      } //*/
    });//*/
    
  }
  /**
   * 打开串口事件
   */
  private void openPortListener() {
    if(COM1 == null && openPort.getText().equals("打开串口")) {
      if(portList.contains(portListBox.getSelectedItem().toString())) {
        try {
          COM1 = SerialPortTools.getPort(portListBox.getSelectedItem().toString(), Integer.parseInt(baudBox.getSelectedItem().toString()), 
              Integer.parseInt(dataBitBox.getSelectedItem().toString()), Integer.parseInt(stopBitBox.getSelectedItem().toString()), 
              Integer.parseInt(parityBox.getSelectedItem().toString()));
          openPort.setText("关闭串口");
          transFileButt.setEnabled(true);
          transDataButt.setEnabled(true);
          autoTransButt.setEnabled(true);
          SerialPortTools.add(COM1, arg0 -> {
            switch (arg0.getEventType()) {
            case SerialPortEvent.BI:  //10 通讯中断
            case SerialPortEvent.OE:  // 7 溢位（溢出）错误
            case SerialPortEvent.FE:  // 9 帧错误
            case SerialPortEvent.PE:  // 8 奇偶校验错误
            case SerialPortEvent.CD:  // 6 载波检测
            case SerialPortEvent.CTS:  // 3 清除待发送数据
            case SerialPortEvent.DSR:  // 4 待发送数据准备好了
            case SerialPortEvent.RI:  // 5 振铃指示
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  // 2 输出缓冲区已清空
              JOptionPane.showMessageDialog(null, COM1.getName() + "::" + arg0.toString());
              break;
            case SerialPortEvent.DATA_AVAILABLE: 
              //有数据到达
              try {
                Thread.sleep(20);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              if(rxHexButt.isSelected()) {
                byte[] data = SerialPortTools.readBytes(COM1);
                rxArea.append(SerialPortTools.bytesToHex(data) + SEPARATOR);
                /*int number = SerialPortTools.checkPLCData(data);
                if(number != -1) {
                  System.out.println(SerialPortTools.checkPLCData(data));
                }//*/
                int count = Integer.parseInt(rxCountField.getText()) + data.length;
                rxCountField.setText(count + "");
              } else if(rxStrButt.isSelected()) {
                String str = SerialPortTools.readString(COM1, "UTF-8");
                rxArea.append(str + SEPARATOR);
                int count = Integer.parseInt(rxCountField.getText()) + str.length();
                rxCountField.setText(count + "");
              }
              break;
            }
          });
        } catch (NumberFormatException | SerialPortParamFail | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e) {
          JOptionPane.showMessageDialog(null, e.toString());
          openPort.setText("打开串口");
          transFileButt.setEnabled(false);
          transDataButt.setEnabled(false);
          autoTransButt.setEnabled(false);
        }
      }
    } else if(openPort.getText().equals("关闭串口")) {
      COM1.close();
      transFileButt.setEnabled(false);
      transDataButt.setEnabled(false);
      autoTransButt.setEnabled(false);
      COM1 = null;
      openPort.setText("打开串口");
    }
  }
  /**
   * 发送数据事件
   */
  private void transData() {
    if(COM1 != null) {
      /*
      try {
        SerialPortTools.writeBytes(COM1, SerialPortTools.toByteArray(SerialPortTools.readPLCDataRegister(0)));
        Thread.sleep(100);
        SerialPortTools.writeBytes(COM1, SerialPortTools.toByteArray(SerialPortTools.writePLCDataRegister(10, 10)));
        Thread.sleep(100);
        SerialPortTools.writeBytes(COM1, SerialPortTools.toByteArray(SerialPortTools.writePLCDataRegister(20, 100)));
        Thread.sleep(100);
        SerialPortTools.writeBytes(COM1, SerialPortTools.toByteArray(SerialPortTools.readPLCDataRegister(30)));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }//*/
      
      String str = txArea.getText();
      if(str.length() > 0 && txHexButt.isSelected()) {
        byte[] data = SerialPortTools.toByteArray(str);
        SerialPortTools.writeBytes(COM1, data);
        int count = Integer.parseInt(txCountField.getText()) + data.length;
        txCountField.setText(count + "");
      } else if(str.length() > 0 && txStrButt.isSelected()) {
        SerialPortTools.writeString(COM1, "UTF-8", str);
        int count = Integer.parseInt(txCountField.getText()) + str.length();
        txCountField.setText(count + "");
      } else
        JOptionPane.showMessageDialog(null, COM1.getName() + "::发送数据不能为空！");//*/
    }
  }
  /**
   * 自动发送数据事件
   */
  private void autoTrans() {
    
    if(COM1 != null && autoTransButt.getText().equals("自动发送")) {
      if(txArea.getText().length() > 0) {
        timer1.setInitialDelay(Integer.parseInt(cycleField.getText()));
        timer1.start();
        autoTransButt.setText("停止发送");
      }
    } else if(autoTransButt.getText().equals("停止发送")){
      autoTransButt.setText("自动发送");
      timer1.stop();
    }
  }
  /**
   * 电机参数按键处理事件
   * @param command
   */
  private void buttProcess(String command) {
    switch (command) {
    
    case "去1号位置":
      JOptionPane.showMessageDialog(null, "1号");
      break;
    case "去2号位置":
      JOptionPane.showMessageDialog(null, "2号");
      break;
    case "去3号位置":
      JOptionPane.showMessageDialog(null, "3号");
      break;
    case "去4号位置":
      JOptionPane.showMessageDialog(null, "4号");
      break;
    case "更新参数1":
      JOptionPane.showMessageDialog(null, "更新参数1");
      break;
    case "更新参数2":
      JOptionPane.showMessageDialog(null, "更新参数2");
      break;
    case "更新参数3":
      JOptionPane.showMessageDialog(null, "更新参数3");
      break;
    case "更新参数4":
      JOptionPane.showMessageDialog(null, "更新参数4");
      break;
    case "回读行程1":
      JOptionPane.showMessageDialog(null, "回读行程1");
      break;
    case "回读行程2":
      JOptionPane.showMessageDialog(null, "回读行程2");
      break;
    case "回读行程3":
      JOptionPane.showMessageDialog(null, "回读行程3");
      break;
    case "回读行程4":
      JOptionPane.showMessageDialog(null, "回读行程4");
      break;
    case "X前进":
      JOptionPane.showMessageDialog(null, "X前进");
      break;
    case "X后退":
      JOptionPane.showMessageDialog(null, "X后退");
      break;
    case "Z前进":
      JOptionPane.showMessageDialog(null, "Z前进");
      break;
    case "Z后退":
      JOptionPane.showMessageDialog(null, "Z后退");
      break;
    case "电机停止":
      JOptionPane.showMessageDialog(null, "电机停止");
      break;
    case "回原点":
      JOptionPane.showMessageDialog(null, "回原点");
      break;
    
    default:
      break;
    }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * 窗口退出时调用
   */
  private void close() {
    System.exit(0);
  }
  /**
   * 提示与技巧菜单事件
   */
  private void tips() {
    int tem = JOptionPane.showConfirmDialog(null, "你确定要查看技巧？", "询问", JOptionPane.YES_NO_OPTION);
    if(tem == JOptionPane.YES_OPTION) {
      JOptionPane.showMessageDialog(null, "并没有什么技巧！哈哈^_^");
    }
  }
  /**
   * 关于菜单事件
   */
  private void about() {
    JOptionPane.showMessageDialog(null, "软件版本：V1.0-2018\r\n技术支持：Loyer");
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  class SerialParam extends JPanel {
    
    private static final long serialVersionUID = 1L;

    public SerialParam(String paramName, JComboBox<String> box) {
      setLayout(new BorderLayout());
      add(new JLabel(paramName), BorderLayout.WEST);
      add(box, BorderLayout.CENTER);
    }
    public SerialParam(String name, JTextField field) {
      setLayout(new BorderLayout(5, 5));
      add(new JLabel(name), BorderLayout.WEST);
      field.setColumns(10);
      add(field, BorderLayout.CENTER);
    }
  }
}
