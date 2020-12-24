package gnu.io;

import java.util.EventListener;

public abstract interface I2CPortEventListener extends EventListener
{
  public abstract void I2CEvent(I2CPortEvent paramI2CPortEvent);
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.I2CPortEventListener
 * JD-Core Version:    0.6.2
 */