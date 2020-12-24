package gnu.io;

public abstract interface CommDriver
{
  public abstract CommPort getCommPort(String paramString, int paramInt);

  public abstract void initialize();
}

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommDriver
 * JD-Core Version:    0.6.2
 */