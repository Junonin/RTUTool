/*    */ package gnu.io;
/*    */ 
/*    */ public class RXTXVersion
/*    */ {
/* 79 */   private static String Version = "RXTX-2.2-20081207 Cloudhopper Build rxtx.cloudhopper.net";
/*    */ 
/*    */   public static String getVersion()
/*    */   {
/* 88 */     return Version;
/*    */   }
/*    */ 
/*    */   public static native String nativeGetVersion();
/*    */ 
/*    */   static
/*    */   {
/* 78 */     System.loadLibrary("rxtxSerial");
/*    */   }
/*    */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.RXTXVersion
 * JD-Core Version:    0.6.2
 */