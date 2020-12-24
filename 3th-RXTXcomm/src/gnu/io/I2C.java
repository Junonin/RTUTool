/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.TooManyListenersException;
/*     */ 
/*     */ final class I2C extends I2CPort
/*     */ {
/*     */   private int fd;
/* 100 */   static boolean dsrFlag = false;
/*     */ 
/* 103 */   private final I2COutputStream out = new I2COutputStream();
/*     */ 
/* 108 */   private final I2CInputStream in = new I2CInputStream();
/*     */ 
/* 130 */   private int speed = 9600;
/*     */ 
/* 134 */   private int dataBits = 8;
/*     */ 
/* 138 */   private int stopBits = 1;
/*     */ 
/* 142 */   private int parity = 0;
/*     */ 
/* 147 */   private int flowmode = 0;
/*     */ 
/* 177 */   private int timeout = 0;
/*     */ 
/* 203 */   private int threshold = 0;
/*     */ 
/* 233 */   private int InputBuffer = 0;
/* 234 */   private int OutputBuffer = 0;
/*     */   private I2CPortEventListener SPEventListener;
/*     */   private MonitorThread monThread;
/* 287 */   private int dataAvailable = 0;
/*     */ 
/*     */   private static native void Initialize();
/*     */ 
/*     */   public I2C(String paramString)
/*     */     throws PortInUseException
/*     */   {
/*  91 */     this.fd = open(paramString);
/*     */   }
/*     */ 
/*     */   private native int open(String paramString)
/*     */     throws PortInUseException;
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 104 */     return this.out;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 109 */     return this.in;
/*     */   }
/*     */ 
/*     */   public void setI2CPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 118 */     nativeSetI2CPortParams(paramInt1, paramInt2, paramInt3, paramInt4);
/* 119 */     this.speed = paramInt1;
/* 120 */     this.dataBits = paramInt2;
/* 121 */     this.stopBits = paramInt3;
/* 122 */     this.parity = paramInt4;
/*     */   }
/*     */ 
/*     */   private native void nativeSetI2CPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws UnsupportedCommOperationException;
/*     */ 
/*     */   public int getBaudRate()
/*     */   {
/* 131 */     return this.speed;
/*     */   }
/*     */ 
/*     */   public int getDataBits() {
/* 135 */     return this.dataBits;
/*     */   }
/*     */ 
/*     */   public int getStopBits() {
/* 139 */     return this.stopBits;
/*     */   }
/*     */ 
/*     */   public int getParity() {
/* 143 */     return this.parity;
/*     */   }
/*     */ 
/*     */   public void setFlowControlMode(int paramInt)
/*     */   {
/*     */     try {
/* 149 */       setflowcontrol(paramInt);
/*     */     } catch (IOException localIOException) {
/* 151 */       localIOException.printStackTrace();
/* 152 */       return;
/*     */     }
/* 154 */     this.flowmode = paramInt;
/*     */   }
/* 156 */   public int getFlowControlMode() { return this.flowmode; }
/*     */ 
/*     */ 
/*     */   native void setflowcontrol(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   public void enableReceiveFraming(int paramInt)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 169 */     throw new UnsupportedCommOperationException("Not supported");
/*     */   }
/*     */   public void disableReceiveFraming() {  } 
/* 172 */   public boolean isReceiveFramingEnabled() { return false; } 
/* 173 */   public int getReceiveFramingByte() { return 0; }
/*     */ 
/*     */   public native int NativegetReceiveTimeout();
/*     */ 
/*     */   public native boolean NativeisReceiveTimeoutEnabled();
/*     */ 
/*     */   public native void NativeEnableReceiveTimeoutThreshold(int paramInt1, int paramInt2, int paramInt3);
/*     */ 
/*     */   public void disableReceiveTimeout()
/*     */   {
/* 183 */     enableReceiveTimeout(0);
/*     */   }
/*     */   public void enableReceiveTimeout(int paramInt) {
/* 186 */     if (paramInt >= 0) {
/* 187 */       this.timeout = paramInt;
/* 188 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/*     */     }
/*     */     else {
/* 191 */       System.out.println("Invalid timeout");
/*     */     }
/*     */   }
/*     */ 
/* 195 */   public boolean isReceiveTimeoutEnabled() { return NativeisReceiveTimeoutEnabled(); }
/*     */ 
/*     */   public int getReceiveTimeout() {
/* 198 */     return NativegetReceiveTimeout();
/*     */   }
/*     */ 
/*     */   public void enableReceiveThreshold(int paramInt)
/*     */   {
/* 206 */     if (paramInt >= 0)
/*     */     {
/* 208 */       this.threshold = paramInt;
/* 209 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/*     */     }
/*     */     else
/*     */     {
/* 213 */       System.out.println("Invalid Threshold");
/*     */     }
/*     */   }
/*     */ 
/* 217 */   public void disableReceiveThreshold() { enableReceiveThreshold(0); }
/*     */ 
/*     */   public int getReceiveThreshold() {
/* 220 */     return this.threshold;
/*     */   }
/*     */   public boolean isReceiveThresholdEnabled() {
/* 223 */     return this.threshold > 0;
/*     */   }
/*     */ 
/*     */   public void setInputBufferSize(int paramInt)
/*     */   {
/* 237 */     this.InputBuffer = paramInt;
/*     */   }
/*     */ 
/*     */   public int getInputBufferSize() {
/* 241 */     return this.InputBuffer;
/*     */   }
/*     */ 
/*     */   public void setOutputBufferSize(int paramInt) {
/* 245 */     this.OutputBuffer = paramInt;
/*     */   }
/*     */ 
/*     */   public int getOutputBufferSize() {
/* 249 */     return this.OutputBuffer;
/*     */   }
/*     */ 
/*     */   public native boolean isDTR();
/*     */ 
/*     */   public native void setDTR(boolean paramBoolean);
/*     */ 
/*     */   public native void setRTS(boolean paramBoolean);
/*     */ 
/*     */   private native void setDSR(boolean paramBoolean);
/*     */ 
/*     */   public native boolean isCTS();
/*     */ 
/*     */   public native boolean isDSR();
/*     */ 
/*     */   public native boolean isCD();
/*     */ 
/*     */   public native boolean isRI();
/*     */ 
/*     */   public native boolean isRTS();
/*     */ 
/*     */   public native void sendBreak(int paramInt);
/*     */ 
/*     */   private native void writeByte(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   private native void writeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   private native void drain() throws IOException;
/*     */ 
/*     */   private native int nativeavailable() throws IOException;
/*     */ 
/*     */   private native int readByte() throws IOException;
/*     */ 
/*     */   private native int readArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException;
/*     */ 
/*     */   native void eventLoop();
/*     */ 
/*     */   public void sendEvent(int paramInt, boolean paramBoolean)
/*     */   {
/* 289 */     switch (paramInt) {
/*     */     case 1:
/* 291 */       this.dataAvailable = 1;
/* 292 */       if (!this.monThread.Data) return;
/*     */       break;
/*     */     case 2:
/* 295 */       if (!this.monThread.Output)
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 3:
/* 315 */       if (!this.monThread.CTS) return;
/*     */       break;
/*     */     case 4:
/* 318 */       if (!this.monThread.DSR) return;
/*     */       break;
/*     */     case 5:
/* 321 */       if (!this.monThread.RI) return;
/*     */       break;
/*     */     case 6:
/* 324 */       if (!this.monThread.CD) return;
/*     */       break;
/*     */     case 7:
/* 327 */       if (!this.monThread.OE) return;
/*     */       break;
/*     */     case 8:
/* 330 */       if (!this.monThread.PE) return;
/*     */       break;
/*     */     case 9:
/* 333 */       if (!this.monThread.FE) return;
/*     */       break;
/*     */     case 10:
/* 336 */       if (!this.monThread.BI) return;
/*     */       break;
/*     */     default:
/* 339 */       System.err.println("unknown event:" + paramInt);
/* 340 */       return;
/*     */     }
/* 342 */     I2CPortEvent localI2CPortEvent = new I2CPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 343 */     if (this.SPEventListener != null) this.SPEventListener.I2CEvent(localI2CPortEvent);
/*     */   }
/*     */ 
/*     */   public void addEventListener(I2CPortEventListener paramI2CPortEventListener)
/*     */     throws TooManyListenersException
/*     */   {
/* 350 */     if (this.SPEventListener != null) throw new TooManyListenersException();
/* 351 */     this.SPEventListener = paramI2CPortEventListener;
/* 352 */     this.monThread = new MonitorThread();
/* 353 */     this.monThread.start();
/*     */   }
/*     */ 
/*     */   public void removeEventListener() {
/* 357 */     this.SPEventListener = null;
/* 358 */     if (this.monThread != null) {
/* 359 */       this.monThread.interrupt();
/* 360 */       this.monThread = null;
/*     */     }
/*     */   }
/*     */ 
/* 364 */   public void notifyOnDataAvailable(boolean paramBoolean) { this.monThread.Data = paramBoolean; } 
/*     */   public void notifyOnOutputEmpty(boolean paramBoolean) {
/* 366 */     this.monThread.Output = paramBoolean;
/*     */   }
/* 368 */   public void notifyOnCTS(boolean paramBoolean) { this.monThread.CTS = paramBoolean; } 
/* 369 */   public void notifyOnDSR(boolean paramBoolean) { this.monThread.DSR = paramBoolean; } 
/* 370 */   public void notifyOnRingIndicator(boolean paramBoolean) { this.monThread.RI = paramBoolean; } 
/* 371 */   public void notifyOnCarrierDetect(boolean paramBoolean) { this.monThread.CD = paramBoolean; } 
/* 372 */   public void notifyOnOverrunError(boolean paramBoolean) { this.monThread.OE = paramBoolean; } 
/* 373 */   public void notifyOnParityError(boolean paramBoolean) { this.monThread.PE = paramBoolean; } 
/* 374 */   public void notifyOnFramingError(boolean paramBoolean) { this.monThread.FE = paramBoolean; } 
/* 375 */   public void notifyOnBreakInterrupt(boolean paramBoolean) { this.monThread.BI = paramBoolean; }
/*     */ 
/*     */   private native void nativeClose();
/*     */ 
/*     */   public void close()
/*     */   {
/* 381 */     setDTR(false);
/* 382 */     setDSR(false);
/* 383 */     nativeClose();
/* 384 */     super.close();
/* 385 */     this.fd = 0;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 391 */     if (this.fd > 0) close();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  77 */     System.loadLibrary("rxtxI2C");
/*  78 */     Initialize();
/*     */   }
/*     */ 
/*     */   class MonitorThread extends Thread
/*     */   {
/* 461 */     private boolean CTS = false;
/* 462 */     private boolean DSR = false;
/* 463 */     private boolean RI = false;
/* 464 */     private boolean CD = false;
/* 465 */     private boolean OE = false;
/* 466 */     private boolean PE = false;
/* 467 */     private boolean FE = false;
/* 468 */     private boolean BI = false;
/* 469 */     private boolean Data = false;
/* 470 */     private boolean Output = false;
/*     */ 
/*     */     MonitorThread() {  } 
/* 473 */     public void run() { I2C.this.eventLoop(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   class I2CInputStream extends InputStream
/*     */   {
/*     */     I2CInputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 414 */       I2C.this.dataAvailable = 0;
/* 415 */       return I2C.this.readByte();
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte) throws IOException {
/* 419 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 423 */       I2C.this.dataAvailable = 0;
/* 424 */       int i = 0; int j = 0;
/* 425 */       int[] arrayOfInt = { paramArrayOfByte.length, I2C.this.InputBuffer, paramInt2 };
/*     */ 
/* 437 */       while ((arrayOfInt[i] == 0) && (i < arrayOfInt.length)) i++;
/* 438 */       j = arrayOfInt[i];
/* 439 */       while (i < arrayOfInt.length)
/*     */       {
/* 441 */         if (arrayOfInt[i] > 0)
/*     */         {
/* 443 */           j = Math.min(j, arrayOfInt[i]);
/*     */         }
/* 445 */         i++;
/*     */       }
/* 447 */       j = Math.min(j, I2C.this.threshold);
/* 448 */       if (j == 0) j = 1;
/* 449 */       int k = available();
/* 450 */       int m = I2C.this.readArray(paramArrayOfByte, paramInt1, j);
/* 451 */       return m;
/*     */     }
/*     */     public int available() throws IOException {
/* 454 */       return I2C.this.nativeavailable();
/*     */     }
/*     */   }
/*     */ 
/*     */   class I2COutputStream extends OutputStream
/*     */   {
/*     */     I2COutputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void write(int paramInt)
/*     */       throws IOException
/*     */     {
/* 398 */       I2C.this.writeByte(paramInt);
/*     */     }
/*     */     public void write(byte[] paramArrayOfByte) throws IOException {
/* 401 */       I2C.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 404 */       I2C.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */     public void flush() throws IOException {
/* 407 */       I2C.this.drain();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.I2C
 * JD-Core Version:    0.6.2
 */