/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public abstract class CommPort
/*     */ {
/*     */   protected String name;
/*     */   private static final boolean debug = false;
/*     */ 
/*     */   public abstract void enableReceiveFraming(int paramInt)
/*     */     throws UnsupportedCommOperationException;
/*     */ 
/*     */   public abstract void disableReceiveFraming();
/*     */ 
/*     */   public abstract boolean isReceiveFramingEnabled();
/*     */ 
/*     */   public abstract int getReceiveFramingByte();
/*     */ 
/*     */   public abstract void disableReceiveTimeout();
/*     */ 
/*     */   public abstract void enableReceiveTimeout(int timeout)
/*     */     throws UnsupportedCommOperationException;
/*     */ 
/*     */   public abstract boolean isReceiveTimeoutEnabled();
/*     */ 
/*     */   public abstract int getReceiveTimeout();
/*     */ 
/*     */   public abstract void enableReceiveThreshold(int paramInt)
/*     */     throws UnsupportedCommOperationException;
/*     */ 
/*     */   public abstract void disableReceiveThreshold();
/*     */ 
/*     */   public abstract int getReceiveThreshold();
/*     */ 
/*     */   public abstract boolean isReceiveThresholdEnabled();
/*     */ 
/*     */   public abstract void setInputBufferSize(int InputBufferSize);
/*     */ 
/*     */   public abstract int getInputBufferSize();
/*     */ 
/*     */   public abstract void setOutputBufferSize(int outputBufferSize);
/*     */ 
/*     */   public abstract int getOutputBufferSize();
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 103 */       CommPortIdentifier localCommPortIdentifier = CommPortIdentifier.getPortIdentifier(this);
/*     */ 
/* 105 */       if (localCommPortIdentifier != null)
/* 106 */         CommPortIdentifier.getPortIdentifier(this).internalClosePort();
/*     */     }
/*     */     catch (NoSuchPortException localNoSuchPortException)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract InputStream getInputStream() throws IOException;
/*     */ 
/*     */   public abstract OutputStream getOutputStream() throws IOException;
/*     */ 
/*     */   public String getName()
/*     */   {
/* 119 */     return this.name;
/*     */   }
/*     */ 
/*     */   @Override
public String toString()
/*     */   {
/* 124 */     return this.name;
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.CommPort
 * JD-Core Version:    0.6.2
 */