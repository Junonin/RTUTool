/*    */ package gnu.io;
/*    */ 
/*    */ public class PortInUseException extends Exception
/*    */ {
/*    */   public String currentOwner;
/*    */ 
/*    */   PortInUseException(String paramString)
/*    */   {
/* 82 */     super(paramString);
/* 83 */     this.currentOwner = paramString;
/*    */   }
/*    */ 
/*    */   public PortInUseException()
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.PortInUseException
 * JD-Core Version:    0.6.2
 */