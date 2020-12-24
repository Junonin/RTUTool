package gnu.io;

import java.util.TooManyListenersException;

abstract class RawPort extends CommPort
{
  public static final int DATABITS_5 = 5;
  public static final int DATABITS_6 = 6;
  public static final int DATABITS_7 = 7;
  public static final int DATABITS_8 = 8;
  public static final int PARITY_NONE = 0;
  public static final int PARITY_ODD = 1;
  public static final int PARITY_EVEN = 2;
  public static final int PARITY_MARK = 3;
  public static final int PARITY_SPACE = 4;
  public static final int STOPBITS_1 = 1;
  public static final int STOPBITS_1_5 = 0;
  public static final int STOPBITS_2 = 2;
  public static final int FLOWCONTROL_NONE = 0;
  public static final int FLOWCONTROL_RTSCTS_IN = 1;
  public static final int FLOWCONTROL_RTSCTS_OUT = 2;
  public static final int FLOWCONTROL_XONXOFF_IN = 4;
  public static final int FLOWCONTROL_XONXOFF_OUT = 8;
  public static final int WRITE_SIZE = 8;
  public static final int IO_PORT = 888;

  public abstract void setRawPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws UnsupportedCommOperationException;

  public abstract void addEventListener(RawPortEventListener paramRawPortEventListener)
    throws TooManyListenersException;

  public abstract void removeEventListener();
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RawPort
 * JD-Core Version:    0.6.2
 */