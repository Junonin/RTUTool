/*     */ package gnu.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.TooManyListenersException;
/*     */ 
/*     */ final class Raw extends RawPort
/*     */ {
/*     */   private int ciAddress;
/*  98 */   static boolean dsrFlag = false;
/*     */ 
/* 101 */   private final RawOutputStream out = new RawOutputStream();
/*     */ 
/* 106 */   private final RawInputStream in = new RawInputStream();
/*     */ 
/* 128 */   private int speed = 9600;
/*     */ 
/* 132 */   private int dataBits = 8;
/*     */ 
/* 136 */   private int stopBits = 1;
/*     */ 
/* 140 */   private int parity = 0;
/*     */ 
/* 145 */   private int flowmode = 0;
/*     */ 
/* 175 */   private int timeout = 0;
/*     */ 
/* 201 */   private int threshold = 0;
/*     */ 
/* 231 */   private int InputBuffer = 0;
/* 232 */   private int OutputBuffer = 0;
/*     */   private RawPortEventListener SPEventListener;
/*     */   private MonitorThread monThread;
/* 285 */   private int dataAvailable = 0;
/*     */ 
/*     */   private static native void Initialize();
/*     */ 
/*     */   public Raw(String paramString)
/*     */     throws PortInUseException
/*     */   {
/*  88 */     this.ciAddress = Integer.parseInt(paramString);
/*  89 */     open(this.ciAddress);
/*     */   }
/*     */ 
/*     */   private native int open(int paramInt)
/*     */     throws PortInUseException;
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 102 */     return this.out;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */   {
/* 107 */     return this.in;
/*     */   }
/*     */ 
/*     */   public void setRawPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 116 */     nativeSetRawPortParams(paramInt1, paramInt2, paramInt3, paramInt4);
/* 117 */     this.speed = paramInt1;
/* 118 */     this.dataBits = paramInt2;
/* 119 */     this.stopBits = paramInt3;
/* 120 */     this.parity = paramInt4;
/*     */   }
/*     */ 
/*     */   private native void nativeSetRawPortParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws UnsupportedCommOperationException;
/*     */ 
/*     */   public int getBaudRate()
/*     */   {
/* 129 */     return this.speed;
/*     */   }
/*     */ 
/*     */   public int getDataBits() {
/* 133 */     return this.dataBits;
/*     */   }
/*     */ 
/*     */   public int getStopBits() {
/* 137 */     return this.stopBits;
/*     */   }
/*     */ 
/*     */   public int getParity() {
/* 141 */     return this.parity;
/*     */   }
/*     */ 
/*     */   public void setFlowControlMode(int paramInt)
/*     */   {
/*     */     try {
/* 147 */       setflowcontrol(paramInt);
/*     */     } catch (IOException localIOException) {
/* 149 */       localIOException.printStackTrace();
/* 150 */       return;
/*     */     }
/* 152 */     this.flowmode = paramInt;
/*     */   }
/* 154 */   public int getFlowControlMode() { return this.flowmode; }
/*     */ 
/*     */ 
/*     */   native void setflowcontrol(int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   public void enableReceiveFraming(int paramInt)
/*     */     throws UnsupportedCommOperationException
/*     */   {
/* 167 */     throw new UnsupportedCommOperationException("Not supported");
/*     */   }
/*     */   public void disableReceiveFraming() {  } 
/* 170 */   public boolean isReceiveFramingEnabled() { return false; } 
/* 171 */   public int getReceiveFramingByte() { return 0; }
/*     */ 
/*     */   public native int NativegetReceiveTimeout();
/*     */ 
/*     */   public native boolean NativeisReceiveTimeoutEnabled();
/*     */ 
/*     */   public native void NativeEnableReceiveTimeoutThreshold(int paramInt1, int paramInt2, int paramInt3);
/*     */ 
/*     */   public void disableReceiveTimeout()
/*     */   {
/* 181 */     enableReceiveTimeout(0);
/*     */   }
/*     */   public void enableReceiveTimeout(int paramInt) {
/* 184 */     if (paramInt >= 0) {
/* 185 */       this.timeout = paramInt;
/* 186 */       NativeEnableReceiveTimeoutThreshold(paramInt, this.threshold, this.InputBuffer);
/*     */     }
/*     */     else {
/* 189 */       System.out.println("Invalid timeout");
/*     */     }
/*     */   }
/*     */ 
/* 193 */   public boolean isReceiveTimeoutEnabled() { return NativeisReceiveTimeoutEnabled(); }
/*     */ 
/*     */   public int getReceiveTimeout() {
/* 196 */     return NativegetReceiveTimeout();
/*     */   }
/*     */ 
/*     */   public void enableReceiveThreshold(int paramInt)
/*     */   {
/* 204 */     if (paramInt >= 0)
/*     */     {
/* 206 */       this.threshold = paramInt;
/* 207 */       NativeEnableReceiveTimeoutThreshold(this.timeout, this.threshold, this.InputBuffer);
/*     */     }
/*     */     else
/*     */     {
/* 211 */       System.out.println("Invalid Threshold");
/*     */     }
/*     */   }
/*     */ 
/* 215 */   public void disableReceiveThreshold() { enableReceiveThreshold(0); }
/*     */ 
/*     */   public int getReceiveThreshold() {
/* 218 */     return this.threshold;
/*     */   }
/*     */   public boolean isReceiveThresholdEnabled() {
/* 221 */     return this.threshold > 0;
/*     */   }
/*     */ 
/*     */   public void setInputBufferSize(int paramInt)
/*     */   {
/* 235 */     this.InputBuffer = paramInt;
/*     */   }
/*     */ 
/*     */   public int getInputBufferSize() {
/* 239 */     return this.InputBuffer;
/*     */   }
/*     */ 
/*     */   public void setOutputBufferSize(int paramInt) {
/* 243 */     this.OutputBuffer = paramInt;
/*     */   }
/*     */ 
/*     */   public int getOutputBufferSize() {
/* 247 */     return this.OutputBuffer;
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
/* 287 */     switch (paramInt) {
/*     */     case 1:
/* 289 */       this.dataAvailable = 1;
/* 290 */       if (!this.monThread.Data) return;
/*     */       break;
/*     */     case 2:
/* 293 */       if (!this.monThread.Output)
/*     */       {
/*     */         return;
/*     */       }
/*     */ 
/*     */       break;
/*     */     case 3:
/* 313 */       if (!this.monThread.CTS) return;
/*     */       break;
/*     */     case 4:
/* 316 */       if (!this.monThread.DSR) return;
/*     */       break;
/*     */     case 5:
/* 319 */       if (!this.monThread.RI) return;
/*     */       break;
/*     */     case 6:
/* 322 */       if (!this.monThread.CD) return;
/*     */       break;
/*     */     case 7:
/* 325 */       if (!this.monThread.OE) return;
/*     */       break;
/*     */     case 8:
/* 328 */       if (!this.monThread.PE) return;
/*     */       break;
/*     */     case 9:
/* 331 */       if (!this.monThread.FE) return;
/*     */       break;
/*     */     case 10:
/* 334 */       if (!this.monThread.BI) return;
/*     */       break;
/*     */     default:
/* 337 */       System.err.println("unknown event:" + paramInt);
/* 338 */       return;
/*     */     }
/* 340 */     RawPortEvent localRawPortEvent = new RawPortEvent(this, paramInt, !paramBoolean, paramBoolean);
/* 341 */     if (this.SPEventListener != null) this.SPEventListener.RawEvent(localRawPortEvent);
/*     */   }
/*     */ 
/*     */   public void addEventListener(RawPortEventListener paramRawPortEventListener)
/*     */     throws TooManyListenersException
/*     */   {
/* 348 */     if (this.SPEventListener != null) throw new TooManyListenersException();
/* 349 */     this.SPEventListener = paramRawPortEventListener;
/* 350 */     this.monThread = new MonitorThread();
/* 351 */     this.monThread.start();
/*     */   }
/*     */ 
/*     */   public void removeEventListener() {
/* 355 */     this.SPEventListener = null;
/* 356 */     if (this.monThread != null) {
/* 357 */       this.monThread.interrupt();
/* 358 */       this.monThread = null;
/*     */     }
/*     */   }
/*     */ 
/* 362 */   public void notifyOnDataAvailable(boolean paramBoolean) { this.monThread.Data = paramBoolean; } 
/*     */   public void notifyOnOutputEmpty(boolean paramBoolean) {
/* 364 */     this.monThread.Output = paramBoolean;
/*     */   }
/* 366 */   public void notifyOnCTS(boolean paramBoolean) { this.monThread.CTS = paramBoolean; } 
/* 367 */   public void notifyOnDSR(boolean paramBoolean) { this.monThread.DSR = paramBoolean; } 
/* 368 */   public void notifyOnRingIndicator(boolean paramBoolean) { this.monThread.RI = paramBoolean; } 
/* 369 */   public void notifyOnCarrierDetect(boolean paramBoolean) { this.monThread.CD = paramBoolean; } 
/* 370 */   public void notifyOnOverrunError(boolean paramBoolean) { this.monThread.OE = paramBoolean; } 
/* 371 */   public void notifyOnParityError(boolean paramBoolean) { this.monThread.PE = paramBoolean; } 
/* 372 */   public void notifyOnFramingError(boolean paramBoolean) { this.monThread.FE = paramBoolean; } 
/* 373 */   public void notifyOnBreakInterrupt(boolean paramBoolean) { this.monThread.BI = paramBoolean; }
/*     */ 
/*     */   private native int nativeClose();
/*     */ 
/*     */   public void close()
/*     */   {
/* 379 */     setDTR(false);
/* 380 */     setDSR(false);
/* 381 */     nativeClose();
/* 382 */     super.close();
/* 383 */     this.ciAddress = 0;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 389 */     close();
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 476 */     String str = "$Id: Raw.java,v 1.1.2.17 2008-09-14 22:29:30 jarvi Exp $";
/* 477 */     return str;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     System.loadLibrary("rxtxRaw");
/*  75 */     Initialize();
/*     */   }
/*     */ 
/*     */   class MonitorThread extends Thread
/*     */   {
/* 459 */     private boolean CTS = false;
/* 460 */     private boolean DSR = false;
/* 461 */     private boolean RI = false;
/* 462 */     private boolean CD = false;
/* 463 */     private boolean OE = false;
/* 464 */     private boolean PE = false;
/* 465 */     private boolean FE = false;
/* 466 */     private boolean BI = false;
/* 467 */     private boolean Data = false;
/* 468 */     private boolean Output = false;
/*     */ 
/*     */     MonitorThread() {  } 
/* 471 */     public void run() { Raw.this.eventLoop(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   class RawInputStream extends InputStream
/*     */   {
/*     */     RawInputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 412 */       Raw.this.dataAvailable = 0;
/* 413 */       return Raw.this.readByte();
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte) throws IOException {
/* 417 */       return read(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */ 
/*     */     public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 421 */       Raw.this.dataAvailable = 0;
/* 422 */       int i = 0; int j = 0;
/* 423 */       int[] arrayOfInt = { paramArrayOfByte.length, Raw.this.InputBuffer, paramInt2 };
/*     */ 
/* 435 */       while ((arrayOfInt[i] == 0) && (i < arrayOfInt.length)) i++;
/* 436 */       j = arrayOfInt[i];
/* 437 */       while (i < arrayOfInt.length)
/*     */       {
/* 439 */         if (arrayOfInt[i] > 0)
/*     */         {
/* 441 */           j = Math.min(j, arrayOfInt[i]);
/*     */         }
/* 443 */         i++;
/*     */       }
/* 445 */       j = Math.min(j, Raw.this.threshold);
/* 446 */       if (j == 0) j = 1;
/* 447 */       int k = available();
/* 448 */       int m = Raw.this.readArray(paramArrayOfByte, paramInt1, j);
/* 449 */       return m;
/*     */     }
/*     */     public int available() throws IOException {
/* 452 */       return Raw.this.nativeavailable();
/*     */     }
/*     */   }
/*     */ 
/*     */   class RawOutputStream extends OutputStream
/*     */   {
/*     */     RawOutputStream()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void write(int paramInt)
/*     */       throws IOException
/*     */     {
/* 396 */       Raw.this.writeByte(paramInt);
/*     */     }
/*     */     public void write(byte[] paramArrayOfByte) throws IOException {
/* 399 */       Raw.this.writeArray(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */     }
/*     */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 402 */       Raw.this.writeArray(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */     public void flush() throws IOException {
/* 405 */       Raw.this.drain();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\dev_ydsoft9b\SRC\trunk\yd_product\product-rtu\rtu-tools\java_lib\RXTX\RXTXcomm.jar
 * Qualified Name:     gnu.io.Raw
 * JD-Core Version:    0.6.2
 */