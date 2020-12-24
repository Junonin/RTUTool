package gnu.io;

import java.util.EventListener;

public abstract interface RawPortEventListener extends EventListener
{
  public abstract void RawEvent(RawPortEvent paramRawPortEvent);
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RawPortEventListener
 * JD-Core Version:    0.6.2
 */