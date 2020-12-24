package gnu.io;

import java.util.EventListener;

public abstract interface ParallelPortEventListener extends EventListener
{
  public abstract void parallelEvent(ParallelPortEvent paramParallelPortEvent);
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.ParallelPortEventListener
 * JD-Core Version:    0.6.2
 */