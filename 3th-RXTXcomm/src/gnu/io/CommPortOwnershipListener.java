package gnu.io;

import java.util.EventListener;

public abstract interface CommPortOwnershipListener extends EventListener
{
  public static final int PORT_OWNED = 1;
  public static final int PORT_UNOWNED = 2;
  public static final int PORT_OWNERSHIP_REQUESTED = 3;

  public abstract void ownershipChange(int paramInt);
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPortOwnershipListener
 * JD-Core Version:    0.6.2
 */