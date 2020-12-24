package gnu.io;

import java.util.EventListener;

public abstract interface RS485PortEventListener extends EventListener
{
  public abstract void RS485Event(RS485PortEvent paramRS485PortEvent);
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RS485PortEventListener
 * JD-Core Version:    0.6.2
 */