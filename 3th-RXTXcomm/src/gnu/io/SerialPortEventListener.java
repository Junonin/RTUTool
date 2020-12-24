package gnu.io;

import java.util.EventListener;

public abstract interface SerialPortEventListener extends EventListener
{
  public abstract void serialEvent(SerialPortEvent paramSerialPortEvent);
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.SerialPortEventListener
 * JD-Core Version:    0.6.2
 */